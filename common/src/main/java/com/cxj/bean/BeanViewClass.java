/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaClass;

/**
 *
 * 支持扩展属性。扩展属性的名称同样受属性名映射的影响。
 *
 *
 * @author Administrator
 */
public class BeanViewClass implements DynaClass {

    private final BeanViewInfo info;
    private final Reference<Class<?>> beanClassRef;
    private final WrapDynaClass wrappedClass;
    private @Nonnull
    Map<String, BeanViewProperty> extendProperties;
    private Map<String, BeanViewProperty> properties;
    private boolean dirty;

    public BeanViewClass(@Nonnull Class<?> beanClass, @Nonnull BeanViewInfo info) {
        this(beanClass, null, info);
    }

    public BeanViewClass(@Nonnull Class<?> beanClass, @Nullable List<BeanViewProperty> extendProperties, @Nonnull BeanViewInfo info) {
        this.info = info;
        this.beanClassRef = new WeakReference<>(beanClass);
        this.wrappedClass = WrapDynaClass.createDynaClass(beanClass);
        this.dirty = false;
        assert !BeanView.class.isAssignableFrom(beanClass);
        if (extendProperties != null) {
            this.extendProperties = new HashMap<>();
            for (BeanViewProperty extendProperty : extendProperties) {
                this.extendProperties.put(extendProperty.getName(), extendProperty);
            }
        } else {
            this.extendProperties = Collections.EMPTY_MAP;
        }
    }

    public BeanViewInfo getInfo() {
        return info;
    }

    public Class<?> getBeanClass() {
        return beanClassRef.get();
    }

    public boolean isExtend() {
        return !extendProperties.isEmpty();
    }

    /**
     * 要对extendProperties进行修改时再使用此函数，其会new一个HashMap来代替可能的Collections.EMPTY_MAP
     *
     * @return
     */
    private Map<String, BeanViewProperty> getExtendProperties() {
        if (extendProperties == Collections.EMPTY_MAP) {
            extendProperties = new HashMap<>();
        }
        return extendProperties;
    }

    public BeanViewClass addExtendProperty(BeanViewProperty property) {
        getExtendProperties().put(property.getName(), property);
        dirty = true;
        return this;
    }

    public BeanViewClass removeExtendProperty(BeanViewProperty property) {
        if (getExtendProperties().remove(property.getName()) != null) {
            dirty = true;
        }
        return this;
    }

    public boolean isExtendProperty(String property) {
        return extendProperties.containsKey(info.restoreFrom(property));
    }

    @Override
    public String getName() {
        return wrappedClass.getName();
    }

    /**
     * @inherit
     *
     * @param name 属性名，不支持索引名和map的键名
     * @return
     */
    @Override
    public DynaProperty getDynaProperty(String name) {
        String nName = info.restoreFrom(name);
        BeanViewProperty pt = getProperties().get(nName);
        if (pt == null) {
            throw new IllegalArgumentException(new NoSuchFieldException(name));
        }
        return pt.mapTo(info);
    }

    protected Map<String, BeanViewProperty> getProperties() {
        if (properties == null || dirty) {
            properties = new HashMap<>();
            for (BeanViewProperty pt : getExtendProperties().values()) {
                properties.put(pt.getName(), pt);
            }
            if (!getBeanClass().isArray() && !List.class.isAssignableFrom(getBeanClass()) && !Map.class.isAssignableFrom(getBeanClass())) {
                DynaProperty[] baseProperties = wrappedClass.getDynaProperties();
                for (DynaProperty baseProperty : baseProperties) {
                    if (!"class".equals(baseProperty.getName())) {
                        if (!extendProperties.containsKey(baseProperty.getName())) {
                            PropertyDescriptor descriptor = wrappedClass.getPropertyDescriptor(baseProperty.getName());
                            if (descriptor == null) {
                                throw new IllegalArgumentException();
                            }
                            boolean readable = descriptor.getReadMethod() != null;
                            boolean writeable = descriptor.getWriteMethod() != null;
                            properties.put(baseProperty.getName(), BeanViewProperty.fromDynaProperty(baseProperty, readable, writeable));
                        }
                    }
                }
            }
            dirty = false;
        }
        return properties;
    }

    /**
     *
     * @return @see #getDynaProperty(java.lang.String)
     */
    @Override
    public DynaProperty[] getDynaProperties() {
        return getProperties().values().stream().filter(pt -> {
            return info.isValidNativeProperty(pt.getName());
        }).map(pt -> {
            return pt.mapTo(info);
        }).toArray(DynaProperty[]::new);
    }

    @Override
    public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @CheckReturnValue
    public BeanViewClass combine(BeanViewInfo _info) {
        return new BeanViewClass(getBeanClass(), info.combine(_info));
    }

    @CheckReturnValue
    public BeanViewClass combine(BeanViewClass clazz) {
        assert Objects.equals(getBeanClass(), clazz.getBeanClass());
        BeanViewClass combined = new BeanViewClass(getBeanClass(), info.combine(clazz.getInfo()));
        if (isExtend() || clazz.isExtend()) {
            combined.extendProperties = new HashMap<>(extendProperties);
            combined.extendProperties.putAll(clazz.getExtendProperties());
        }
        return combined;
    }

}
