/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class BeanView extends AbstractMap<String, Object> implements DynaBean, JsonSerializable {

    private static final Logger LOGGER = LogManager.getLogger(BeanView.class);
    private static final Pattern PT_INDEX = Pattern.compile("\\[\\d+\\]");

    @Nullable
    private final Object wrapped;
    //private final BeanViewInfo info;
    @Nonnull
    private final BeanViewClass beanViewClass;
    @Nonnull
    private Map<String, Object> extendProperties;

    public BeanView(@Nonnull Object wrapped) {
        this(wrapped, new BeanViewClass(wrapped.getClass(), BeanViewInfo.createDefaultInfo()));
    }

    public BeanView(@Nonnull Object wrapped, @Nonnull BeanViewInfo info) {
        this(wrapped, new BeanViewClass(wrapped.getClass(), info));
    }

    public BeanView(@Nonnull BeanView wrapped, @Nonnull BeanViewInfo info) {
        this(wrapped.unWrapped(), wrapped.beanViewClass.combine(info));
    }

    public BeanView(@Nullable Object wrapped, @Nonnull BeanViewClass clazz) {
        this.wrapped = wrapped;
        this.beanViewClass = clazz;
        this.extendProperties = Collections.EMPTY_MAP;
        if (wrapped != null) {
            assert Objects.equals(wrapped.getClass(), clazz.getBeanClass());
        }
    }

    public BeanView(@Nonnull BeanView wrapped, @Nonnull BeanViewClass clazz) {
        this(wrapped.unWrapped(), wrapped.beanViewClass.combine(clazz));
    }

    public Object unWrapped() {
        return wrapped;
    }

    protected Map<String, Object> getExtendProperties() {
        if (extendProperties == Collections.EMPTY_MAP) {
            extendProperties = new HashMap<>();
        }
        return extendProperties;
    }

    public Object getProperty(String pName) {
        String nName = beanViewClass.getInfo().restoreFrom(pName);
        if (wrapped == null) {
            throw new NullPointerException();
        } else if (beanViewClass.isExtendProperty(pName)) {
            return getExtendProperties().get(nName);
        } else {
            try {
                return PropertyUtils.getProperty(wrapped, nName);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new IllegalArgumentException(ex);
            } catch (NestedNullException ex) {
                return null;
            }
        }
    }

    public void setProperty(String pName, Object newValue) {
        String nName = beanViewClass.getInfo().restoreFrom(pName);
        if (wrapped == null) {
            throw new NullPointerException();
        } else if (beanViewClass.isExtendProperty(pName)) {
            if (newValue != null) {
                BeanViewProperty p = beanViewClass.getProperties().get(nName);
                if (!p.getType().isAssignableFrom(newValue.getClass())) {
                    throw new ClassCastException(pName);
                }
            }
            getExtendProperties().put(nName, newValue);
        } else {
            try {
                PropertyUtils.setProperty(wrapped, nName, newValue);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new IllegalArgumentException(ex);
            } catch (NestedNullException ex) {
            }
        }
    }

    public boolean isIndexed() {
        return wrapped != null && (wrapped instanceof List || wrapped.getClass().isArray());
    }

    public boolean isMapBased() {
        return wrapped != null && wrapped instanceof Map;
    }

    public int contentSize() {
        if (!isIndexed()) {
            return -1;
        }
        if (wrapped instanceof List) {
            return ((List) wrapped).size();
        }
        if (wrapped.getClass().isArray()) {
            return ArrayUtils.getLength(wrapped);
        }
        throw new IllegalStateException();
    }

    public Class<?> getComponentType() {
        if (!isIndexed()) {
            return null;
        }
        if (wrapped instanceof List) {
            return Object.class;
        }
        if (wrapped.getClass().isArray()) {
            return wrapped.getClass().getComponentType();
        }
        throw new IllegalStateException();
    }

    /**
     * 判断是否符合索引名的格式：[xxx]，其中xxx为数字，并且不超出数组范围
     *
     * @param name
     * @return
     */
    private boolean isValidIndexName(String name) {
        if (!isIndexed()) {
            return false;
        }
        if (!PT_INDEX.matcher(name).matches()) {
            return false;
        }
        int index = Integer.parseInt(name.substring(1, name.length() - 1));
        return index >= 0 && index < contentSize();
    }

    public Class<?> getPropertyType(String name) {
        String nName = beanViewClass.getInfo().restoreFrom(name);
        if (wrapped == null) {
            throw new NullPointerException();
        } else if (beanViewClass.isExtendProperty(name)) {
            return beanViewClass.getProperties().get(nName).getType();
        } else if (isIndexed()) {
            if (isValidIndexName(nName)) {
                return getComponentType();
            } else {
                throw new IllegalArgumentException(new NoSuchFieldException("No such field: " + name));
            }
        } else if (isMapBased()) {
            Map map = (Map) wrapped;
            if (map.containsKey(nName)) {
                Object v = map.get(nName);
                return v == null ? Object.class : v.getClass();
            } else {
                throw new IllegalArgumentException(new NoSuchFieldException("No such field: " + name));
            }
        } else {
            return beanViewClass.getProperties().get(nName).getType();
        }
    }

    public List<String> getReadablePropertyNames() {
        final BeanViewInfo info = beanViewClass.getInfo();
        if (wrapped == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> names = new ArrayList<>();
        for (BeanViewProperty p : beanViewClass.getProperties().values()) {
            if (p.isReadable() && info.isValidNativeProperty(p.getName())) {
                names.add(info.mapTo(p.getName()));
            }
        }

        if (wrapped.getClass().isArray()) {
            int arSize = ArrayUtils.getLength(wrapped);
            for (int i = 0; i < arSize; i++) {
                String name = "[" + i + "]";
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        } else if (wrapped instanceof List) {
            List list = (List) wrapped;
            for (int i = 0; i < list.size(); i++) {
                String name = "[" + i + "]";
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        } else if (isMapBased()) {
            Map map = (Map) wrapped;
            for (Object key : map.keySet()) {
                String name = (String) key;
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        }
        return names;
    }

    public List<String> getWriteablePropertyNames() {
        final BeanViewInfo info = beanViewClass.getInfo();
        if (wrapped == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> names = new ArrayList<>();
        for (BeanViewProperty p : beanViewClass.getProperties().values()) {
            if (p.isWriteable() && info.isValidNativeProperty(p.getName())) {
                names.add(info.mapTo(p.getName()));
            }
        }

        if (wrapped.getClass().isArray()) {
            int arSize = ArrayUtils.getLength(wrapped);
            for (int i = 0; i < arSize; i++) {
                String name = "[" + i + "]";
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        } else if (wrapped instanceof List) {
            List list = (List) wrapped;
            for (int i = 0; i < list.size(); i++) {
                String name = "[" + i + "]";
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        } else if (isMapBased()) {
            Map map = (Map) wrapped;
            for (Object key : map.keySet()) {
                String name = (String) key;
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        }
        return names;
    }

    public int getPropertyNum() {
        final BeanViewInfo info = beanViewClass.getInfo();
        if (wrapped == null) {
            return 0;
        }
        long baseNum = beanViewClass.getProperties().keySet().stream().filter(name -> info.isValidNativeProperty(name)).count();
        if (wrapped.getClass().isArray()) {
            baseNum += IntStream.range(0, ArrayUtils.getLength(wrapped))
                    .filter(i -> !beanViewClass.getProperties().containsKey("[" + i + "]") && info.isValidNativeProperty("[" + i + "]"))
                    .count();
        } else if (wrapped instanceof List) {
            baseNum += IntStream.range(0, ((List) wrapped).size())
                    .filter(i -> !beanViewClass.getProperties().containsKey("[" + i + "]") && info.isValidNativeProperty("[" + i + "]"))
                    .count();
        } else if (isMapBased()) {
            baseNum += ((Map<String, Object>) wrapped).keySet().stream()
                    .filter(name -> !beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name))
                    .count();
        }
        return (int) baseNum;
    }

    public List<String> getPropertyNames() {
        final BeanViewInfo info = beanViewClass.getInfo();
        if (wrapped == null) {
            return Collections.EMPTY_LIST;
        }
        List<String> names = new ArrayList<>();
        for (BeanViewProperty p : beanViewClass.getProperties().values()) {
            if (info.isValidNativeProperty(p.getName())) {
                names.add(info.mapTo(p.getName()));
            }
        }

        if (wrapped.getClass().isArray()) {
            int arSize = ArrayUtils.getLength(wrapped);
            for (int i = 0; i < arSize; i++) {
                String name = "[" + i + "]";
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        } else if (wrapped instanceof List) {
            List list = (List) wrapped;
            for (int i = 0; i < list.size(); i++) {
                String name = "[" + i + "]";
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        } else if (isMapBased()) {
            Map map = (Map) wrapped;
            for (Object key : map.keySet()) {
                String name = (String) key;
                if (!beanViewClass.getProperties().containsKey(name) && info.isValidNativeProperty(name)) {
                    names.add(info.mapTo(name));
                }
            }
        }
        return names;
    }

    /**
     * 扩展bean，增加扩展属性
     *
     * @param <T>
     * @param <E>
     * @param name 原生名，即映射前的名称
     * @param type 属性类型
     * @param contentType 若属性为数组或集合，其元素类型，否则为{@code null}
     * @param defaultValue 属性默认值
     * @param readable 属性是否可读
     * @param writeable 属性是否可写
     * @return
     */
    public <T, E> BeanView extendProperty(String name, Class<T> type, Class<E> contentType, T defaultValue, boolean readable, boolean writeable) {
        beanViewClass.addExtendProperty(BeanViewProperty.createProperty(name, type, contentType, readable, writeable));
        getExtendProperties().put(name, defaultValue);
        return this;
    }

    public <T, E> BeanView extendProperty(String name, Class<T> type, Class<E> contentType, T defaultValue) {
        extendProperty(name, type, contentType, defaultValue, true, true);
        return this;
    }

    public <T, E> BeanView extendProperty(String name, Class<T> type, T defaultValue) {
        extendProperty(name, type, null, defaultValue);
        return this;
    }

    public <T, E> BeanView extendProperty(String name, Class<T> type, T defaultValue, boolean readable, boolean writeable) {
        extendProperty(name, type, null, defaultValue, readable, writeable);
        return this;
    }

    public <T, E> BeanView extendProperty(String name, Class<T> type) {
        extendProperty(name, type, null);
        return this;
    }

    public <T, E> BeanView extendProperty(String name, Class<T> type, boolean readable, boolean writeable) {
        extendProperty(name, type, null, readable, writeable);
        return this;
    }

    public <T, E> BeanView extendReadOnlyProperty(String name, Class<T> type, Class<E> contentType, T defaultValue) {
        extendProperty(name, type, contentType, defaultValue, true, false);
        return this;
    }

    public <T, E> BeanView extendReadOnlyProperty(String name, Class<T> type, T defaultValue) {
        extendReadOnlyProperty(name, type, null, defaultValue);
        return this;
    }

    public <T, E> BeanView extendReadOnlyProperty(String name, Class<T> type) {
        extendReadOnlyProperty(name, type, null);
        return this;
    }

    public <T, E> BeanView extendWriteOnlyProperty(String name, Class<T> type, Class<E> contentType, T defaultValue) {
        extendProperty(name, type, contentType, defaultValue, false, true);
        return this;
    }

    public <T, E> BeanView extendWriteOnlyProperty(String name, Class<T> type, T defaultValue) {
        extendWriteOnlyProperty(name, type, null, defaultValue);
        return this;
    }

    public <T, E> BeanView extendWriteOnlyProperty(String name, Class<T> type) {
        extendWriteOnlyProperty(name, type, null);
        return this;
    }

    public BeanViewInfo getBeanInfo() {
        return beanViewClass.getInfo();
    }

    @Override
    public Object get(String name) {
        return getProperty(name);
    }

    @Override
    public Object get(String name, String key) {
        return getProperty(name + "(" + key + ")");
    }

    @Override
    public Object get(String name, int index) {
        return getProperty(name + "[" + index + "]");
    }

    @Override
    public void set(String name, Object value) {
        setProperty(name, value);
    }

    @Override
    public void set(String name, String key, Object value) {
        setProperty(name + "(" + key + ")", value);
    }

    @Override
    public void set(String name, int index, Object value) {
        setProperty(name + "[" + index + "]", value);
    }

    @Override
    public void remove(String name, String key) {
        ((Map<String, ?>) getProperty(name)).remove(key);
    }

    @Override
    public boolean contains(String name, String key) {
        return ((Map<String, ?>) getProperty(name)).containsKey(key);
    }

    public BeanViewClass getBeanViewClass() {
        return beanViewClass;
    }

    @Override
    public DynaClass getDynaClass() {
        return beanViewClass;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        BeanViewInfo info = beanViewClass.getInfo();
        gen.writeStartObject();
        List<String> names = getReadablePropertyNames();
        for (String name : names) {
            if (!info.isValidMappedPropertyForSerialize(name)) {
                continue;
            }
            Object pValue = getProperty(name);
            JsonSerializer serializer = info.getJsonSerializer(name);
            if (serializer != null) {
                serializer.serialize(pValue, gen, serializers);
            } else {
                if (wrapped instanceof BeanViewJsonPropertySerializer) {
                    ((BeanViewJsonPropertySerializer) wrapped).serialize(gen, serializers, info.restoreFrom(name), name);
                } else {
                    gen.writeObjectField(name, pValue);
                }
            }
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        BeanViewInfo info = beanViewClass.getInfo();
        gen.writeStartObject();
        typeSer.writeTypePrefixForObject(wrapped, gen);
        List<String> names = getReadablePropertyNames();
        for (String name : names) {
            if (!info.isValidMappedPropertyForSerialize(name)) {
                continue;
            }
            Object pValue = getProperty(name);
            JsonSerializer serializer = info.getJsonSerializer(name);
            if (serializer != null) {
                serializer.serialize(pValue, gen, serializers);
            } else {
                if (wrapped instanceof BeanViewJsonPropertySerializer) {
                    ((BeanViewJsonPropertySerializer) wrapped).serialize(gen, serializers, info.restoreFrom(name), name);
                } else {
                    gen.writeObjectField(name, pValue);
                }
            }
        }
        typeSer.writeTypeSuffixForObject(wrapped, gen);
        gen.writeEndObject();
    }

    @Override
    public String toString() {
        return wrapped.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return Objects.equals(wrapped, obj);
        }
        final BeanView other = (BeanView) obj;
        return Objects.equals(this.wrapped, other.wrapped);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        List<String> names = getReadablePropertyNames();
        Set<Entry<String, Object>> entrySet = new HashSet<>();
        for (String name : names) {
            entrySet.add(new SimpleEntry<>(name, getProperty(name)));
        }
        return entrySet;
    }

}
