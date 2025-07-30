/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.annotation.CheckReturnValue;
import org.apache.commons.collections.ListUtils;

/**
 *
 * @author Administrator
 */
public class BeanViewInfo implements Serializable {

    private static final BeanViewInfo GLOBAL_INFO = createGlobalInfo();
    private static final char NESTED = '.';
    private static final char MAPPED_START = '(';
    private static final char INDEXED_START = '[';
    private static final long serialVersionUID = -8723261467004208616L;

    private boolean global = false;
    private Map<String, String> pMap;
    private Set<String> nativePropertyWriteList;
    private Set<String> mappedPropertyWriteList;
    private Set<String> nativePropertyBlackList;
    private Set<String> mappedPropertyBlackList;
    private Set<String> serializerNativePropertyWriteList;
    private Set<String> serializerMappedPropertyWriteList;
    private Set<String> serializerNativePropertyBlackList;
    private Set<String> serializerMappedPropertyBlackList;

    private List<BiFunction<BeanViewInfo, String, SerializableJsonSerializer<?>>> serializerFactories;

    enum FilterMode {

        WRITE_LIST, BLACK_LIST
    }

    enum SerializerFilterMode {

        WRITE_LIST, BLACK_LIST
    }

    private FilterMode filterMode = FilterMode.BLACK_LIST;
    private SerializerFilterMode serializerFilterMode = SerializerFilterMode.BLACK_LIST;

    public BeanViewInfo() {
        this.pMap = null;
    }

    public BeanViewInfo(Map<String, String> pMap) {
        this.pMap = new HashMap<>(pMap);
        checkMap();
    }

    private boolean isGlobal() {
        return global;
    }

    private boolean hasValidMap() {
        return pMap != null && !pMap.isEmpty();
    }

    private void checkMap() {
        if (pMap != null) {
            if (ImmutableSet.copyOf(pMap.values()).size() != pMap.size()) {
                throw new IllegalStateException();
            }
        }
    }

    private void clearFilterData() {
        if (mappedPropertyBlackList != null) {
            mappedPropertyBlackList.clear();
        }
        if (nativePropertyBlackList != null) {
            nativePropertyBlackList.clear();
        }
        if (mappedPropertyWriteList != null) {
            mappedPropertyWriteList.clear();
        }
        if (nativePropertyWriteList != null) {
            nativePropertyWriteList.clear();
        }
    }

    private void clearSerializerFilterData() {
        if (serializerMappedPropertyBlackList != null) {
            serializerMappedPropertyBlackList.clear();
        }
        if (serializerNativePropertyBlackList != null) {
            serializerNativePropertyBlackList.clear();
        }
        if (serializerMappedPropertyWriteList != null) {
            serializerMappedPropertyWriteList.clear();
        }
        if (serializerNativePropertyWriteList != null) {
            serializerNativePropertyWriteList.clear();
        }
    }
    
    public BeanViewInfo addMap(String nName, String mName) {
        if (!getPropertyMap().containsValue(mName)) {
            getPropertyMap().put(nName, mName);
        } else {
            throw new IllegalArgumentException("Duplicated mapped name: " + mName);
        }
        return this;
    }
    
    public BeanViewInfo addMaps(Map<String, String> maps) {
        getPropertyMap().putAll(maps);
        checkMap();
        return this;
    }
    
    public BeanViewInfo removeMap(String nName, String mName) {
        if (!getPropertyMap().remove(nName, mName)) {
            throw new IllegalArgumentException("Map[" + nName + " -> " + mName + "] not exist!");
        }
        return this;
    }

    public BeanViewInfo switchFilterModeToWriteList() {
        filterMode = FilterMode.WRITE_LIST;
        clearFilterData();
        return this;
    }

    public BeanViewInfo switchFilterModeToBlackList() {
        filterMode = FilterMode.BLACK_LIST;
        clearFilterData();
        return this;
    }

    public BeanViewInfo switchSerializerFilterModeToWriteList() {
        serializerFilterMode = SerializerFilterMode.WRITE_LIST;
        clearSerializerFilterData();
        return this;
    }

    public BeanViewInfo switchSerializerFilterModeToBlackList() {
        serializerFilterMode = SerializerFilterMode.BLACK_LIST;
        clearSerializerFilterData();
        return this;
    }

    public boolean isValidNativeProperty(String name) {
        switch (filterMode) {
            case BLACK_LIST:
                return !getNativePropertyBlackList().contains(name) && !getGlobalInfo().getNativePropertyBlackList().contains(name);
            case WRITE_LIST:
                return getNativePropertyWriteList().contains(name) || getGlobalInfo().getNativePropertyWriteList().contains(name);
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isValidMappedProperty(String name) {
        switch (filterMode) {
            case BLACK_LIST:
                return !getMappedPropertyBlackList().contains(name) && !getGlobalInfo().getMappedPropertyBlackList().contains(name);
            case WRITE_LIST:
                return getMappedPropertyWriteList().contains(name) || getGlobalInfo().getMappedPropertyWriteList().contains(name);
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isValidNativePropertyForSerialize(String name) {
        switch (serializerFilterMode) {
            case BLACK_LIST:
                return !getSerializerNativePropertyBlackList().contains(name) && !getGlobalInfo().getSerializerNativePropertyBlackList().contains(name);
            case WRITE_LIST:
                return getSerializerNativePropertyWriteList().contains(name) || getGlobalInfo().getSerializerNativePropertyWriteList().contains(name);
            default:
                throw new IllegalStateException();
        }
    }

    public boolean isValidMappedPropertyForSerialize(String name) {
        switch (serializerFilterMode) {
            case BLACK_LIST:
                return !getSerializerMappedPropertyBlackList().contains(name) && !getGlobalInfo().getSerializerMappedPropertyBlackList().contains(name);
            case WRITE_LIST:
                return getSerializerMappedPropertyWriteList().contains(name) || getGlobalInfo().getSerializerMappedPropertyWriteList().contains(name);
            default:
                throw new IllegalStateException();
        }
    }

    public JsonSerializer<?> getJsonSerializer(String propertyName) {
        if (serializerFactories != null) {
            int size = serializerFactories.size();
            for (int i = 0; i < size; i++) {
                JsonSerializer<?> serializer = serializerFactories.get(size - i - 1).apply(this, propertyName);
                if (serializer != null) {
                    return serializer;
                }
            }
        }
        return null;
    }

    public BeanViewInfo addMappedPropertyToWhiteList(String name) {
        String nativeName = restoreFrom(name);
        getNativePropertyWriteList().add(nativeName);
        getMappedPropertyWriteList().add(name);
        return this;
    }

    public BeanViewInfo addMappedPropertyToWhiteList(Collection<String> names) {
        for (String name : names) {
            addMappedPropertyToWhiteList(name);
        }
        return this;
    }

    public BeanViewInfo addNativePropertyToWhiteList(String name) {
        String mappedName = mapTo(name);
        getNativePropertyWriteList().add(name);
        getMappedPropertyWriteList().add(mappedName);
        return this;
    }

    public BeanViewInfo addNativePropertyToWhiteList(Collection<String> names) {
        for (String name : names) {
            addNativePropertyToWhiteList(name);
        }
        return this;
    }

    public BeanViewInfo addSerializerFactory(BiFunction<BeanViewInfo, String, SerializableJsonSerializer<?>> callBack) {
        getSerializerFactories().add(callBack);
        return this;
    }

    public BeanViewInfo removeSerializerFactory(BiFunction<BeanViewInfo, String, SerializableJsonSerializer<?>> callBack) {
        if (serializerFactories != null) {
            serializerFactories.remove(callBack);
        }
        return this;
    }

    public BeanViewInfo addMappedPropertyToBlackList(String name) {
        String nativeName = restoreFrom(name);
        getNativePropertyBlackList().add(nativeName);
        getMappedPropertyBlackList().add(name);
        return this;
    }

    public BeanViewInfo addMappedPropertyToBlackList(Collection<String> names) {
        for (String name : names) {
            addMappedPropertyToBlackList(name);
        }
        return this;
    }

    public BeanViewInfo addNativePropertyToBlackList(String name) {
        String mappedName = mapTo(name);
        getNativePropertyBlackList().add(name);
        getMappedPropertyBlackList().add(mappedName);
        return this;
    }

    public BeanViewInfo addNativePropertyToBlackList(Collection<String> names) {
        for (String name : names) {
            addNativePropertyToBlackList(name);
        }
        return this;
    }

    public BeanViewInfo addMappedPropertyToSerializerWhiteList(String name) {
        String nativeName = restoreFrom(name);
        getSerializerNativePropertyWriteList().add(nativeName);
        getSerializerMappedPropertyWriteList().add(name);
        return this;
    }

    public BeanViewInfo addMappedPropertyToSerializerWhiteList(Collection<String> names) {
        for (String name : names) {
            addMappedPropertyToSerializerWhiteList(name);
        }
        return this;
    }

    public BeanViewInfo addNativePropertyToSerializerWhiteList(String name) {
        String mappedName = mapTo(name);
        getSerializerNativePropertyWriteList().add(name);
        getSerializerMappedPropertyWriteList().add(mappedName);
        return this;
    }

    public BeanViewInfo addNativePropertyToSerializerWhiteList(Collection<String> names) {
        for (String name : names) {
            addNativePropertyToSerializerWhiteList(name);
        }
        return this;
    }

    public BeanViewInfo addMappedPropertyToSerializerBlackList(String name) {
        String nativeName = restoreFrom(name);
        getSerializerNativePropertyBlackList().add(nativeName);
        getSerializerMappedPropertyBlackList().add(name);
        return this;
    }

    public BeanViewInfo addMappedPropertyToSerializerBlackList(Collection<String> names) {
        for (String name : names) {
            addMappedPropertyToSerializerBlackList(name);
        }
        return this;
    }

    public BeanViewInfo addNativePropertyToSerializerBlackList(String name) {
        String mappedName = mapTo(name);
        getSerializerNativePropertyBlackList().add(name);
        getSerializerMappedPropertyBlackList().add(mappedName);
        return this;
    }

    public BeanViewInfo addNativePropertyToSerializerBlackList(Collection<String> names) {
        for (String name : names) {
            addNativePropertyToSerializerBlackList(name);
        }
        return this;
    }

    public String restoreFrom(String expression) {
        if (!hasValidMap()) {
            return isGlobal() ? expression : getGlobalInfo().restoreFrom(expression);
        }
        for (Map.Entry<String, String> entry : pMap.entrySet()) {
            if (expression.startsWith(entry.getValue())) {
                String to = entry.getValue();
                if (expression.length() == entry.getValue().length()) {
                    return entry.getKey();
                } else {
                    int toTest = expression.codePointAt(to.length());
                    if (toTest == NESTED || toTest == MAPPED_START || toTest == INDEXED_START) {
                        return entry.getKey() + expression.substring(to.length(), expression.length());
                    }
                }
            }
        }
        return isGlobal() ? expression : getGlobalInfo().restoreFrom(expression);
    }

    public String mapTo(String expression) {
        if (!hasValidMap()) {
            return isGlobal() ? expression : getGlobalInfo().mapTo(expression);
        }
        for (Map.Entry<String, String> entry : pMap.entrySet()) {
            if (expression.startsWith(entry.getKey())) {
                String to = entry.getKey();
                if (expression.length() == entry.getKey().length()) {
                    return entry.getValue();
                } else {
                    int toTest = expression.codePointAt(to.length());
                    if (toTest == NESTED || toTest == MAPPED_START || toTest == INDEXED_START) {
                        return entry.getValue() + expression.substring(to.length(), expression.length());
                    } else {
                        return expression;
                    }
                }
            }
        }
        return isGlobal() ? expression : getGlobalInfo().mapTo(expression);
    }

    public Map<String, String> getPropertyMap() {
        if (pMap == null) {
            pMap = new HashMap<>();
        }
        return pMap;
    }
    
    public @CheckReturnValue BeanViewInfo combine(BeanViewInfo info) {
        return combineInfo(this, info);
    }

    protected Set<String> getMappedPropertyWriteList() {
        if (mappedPropertyWriteList == null) {
            mappedPropertyWriteList = new HashSet<>();
        }
        return mappedPropertyWriteList;
    }

    protected Set<String> getNativePropertyWriteList() {
        if (nativePropertyWriteList == null) {
            nativePropertyWriteList = new HashSet<>();
        }
        return nativePropertyWriteList;
    }

    protected Set<String> getMappedPropertyBlackList() {
        if (mappedPropertyBlackList == null) {
            mappedPropertyBlackList = new HashSet<>();
        }
        return mappedPropertyBlackList;
    }

    protected Set<String> getNativePropertyBlackList() {
        if (nativePropertyBlackList == null) {
            nativePropertyBlackList = new HashSet<>();
        }
        return nativePropertyBlackList;
    }

    protected Set<String> getSerializerMappedPropertyWriteList() {
        if (serializerMappedPropertyWriteList == null) {
            serializerMappedPropertyWriteList = new HashSet<>();
        }
        return serializerMappedPropertyWriteList;
    }

    protected Set<String> getSerializerNativePropertyWriteList() {
        if (serializerNativePropertyWriteList == null) {
            serializerNativePropertyWriteList = new HashSet<>();
        }
        return serializerNativePropertyWriteList;
    }

    protected Set<String> getSerializerMappedPropertyBlackList() {
        if (serializerMappedPropertyBlackList == null) {
            serializerMappedPropertyBlackList = new HashSet<>();
        }
        return serializerMappedPropertyBlackList;
    }

    protected Set<String> getSerializerNativePropertyBlackList() {
        if (serializerNativePropertyBlackList == null) {
            serializerNativePropertyBlackList = new HashSet<>();
        }
        return serializerNativePropertyBlackList;
    }

    protected List<BiFunction<BeanViewInfo, String, SerializableJsonSerializer<?>>> getSerializerFactories() {
        if (serializerFactories == null) {
            serializerFactories = new ArrayList<>();
        }
        return serializerFactories;
    }

    public static BeanViewInfo createDefaultInfo() {
        return new BeanViewInfo();
    }

    public static BeanViewInfo getGlobalInfo() {
        return GLOBAL_INFO;
    }

    public static BeanViewInfo combineInfo(BeanViewInfo info1, BeanViewInfo info2) {
        if (info1.filterMode != info2.filterMode || info1.serializerFilterMode != info2.serializerFilterMode) {
            throw new IllegalArgumentException();
        }
        BeanViewInfo info = new BeanViewInfo();
        //filter mode
        info.filterMode = info1.filterMode;
        info.serializerFilterMode = info2.serializerFilterMode;
        //combine map;
        info.pMap = new HashMap<>();
        for (Map.Entry<String, String> map : info1.pMap.entrySet()) {
            String m2 = info2.pMap.get(map.getValue());
            if (m2 != null) {
                info.pMap.put(map.getKey(), m2);
            } else {
                info.pMap.put(map.getKey(), map.getValue());
            }
        }
        for (Map.Entry<String, String> map : info2.pMap.entrySet()) {
            if (!info1.pMap.containsValue(map.getKey()) && !info1.pMap.containsKey(map.getKey())) {
                info.pMap.put(map.getKey(), map.getValue());
            }
        }
        info.checkMap();
        //write and black list;
        for (String w : info1.nativePropertyWriteList) {
            info.addNativePropertyToWhiteList(w);
        }
        for (String w : info1.nativePropertyBlackList) {
            info.addNativePropertyToBlackList(w);
        }
        for (String w : info1.serializerNativePropertyWriteList) {
            info.addNativePropertyToSerializerWhiteList(w);
        }
        for (String w : info1.serializerNativePropertyBlackList) {
            info.addNativePropertyToSerializerBlackList(w);
        }
        for (String w : info2.mappedPropertyWriteList) {
            info.addMappedPropertyToWhiteList(w);
        }
        for (String w : info2.mappedPropertyBlackList) {
            info.addMappedPropertyToBlackList(w);
        }
        for (String w : info2.serializerMappedPropertyWriteList) {
            info.addMappedPropertyToSerializerWhiteList(w);
        }
        for (String w : info2.serializerMappedPropertyBlackList) {
            info.addMappedPropertyToSerializerBlackList(w);
        }
        //combine serializer factories;
        List<BiFunction<BeanViewInfo, String, SerializableJsonSerializer>> factories1 = info1.serializerFactories.stream().map(factory -> {
            return (BiFunction<BeanViewInfo, String, SerializableJsonSerializer>)(_info, _pName) -> {
                return factory.apply(info1, info2.restoreFrom(_pName));
            };
        }).collect(Collectors.toList());
        List<BiFunction<BeanViewInfo, String, SerializableJsonSerializer>> factories2 = info1.serializerFactories.stream().map(factory -> {
            return (BiFunction<BeanViewInfo, String, SerializableJsonSerializer>)(_info, _pName) -> {
                return factory.apply(info2, _pName);
            };
        }).collect(Collectors.toList());
        info.serializerFactories = ListUtils.union(factories1, factories2);
        return info;
    }

    private static BeanViewInfo createGlobalInfo() {
        BeanViewInfo info = new BeanViewInfo();
        info.global = true;
        info.switchSerializerFilterModeToBlackList().addNativePropertyToSerializerBlackList("class");
        return info;
    }
}
