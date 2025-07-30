/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.commons.beanutils.DynaProperty;

/**
 *
 * @author Administrator
 */
public class BeanViewProperty implements Serializable {

    private static final long serialVersionUID = -5978684534099516578L;

    private final DynaProperty property;
    private final boolean readable;
    private final boolean writeable;

    public BeanViewProperty(@Nonnull DynaProperty property, boolean readable, boolean writeable) {
        this.property = property;
        this.readable = readable;
        this.writeable = writeable;
    }

    public boolean isReadable() {
        return readable;
    }

    public boolean isWriteable() {
        return writeable;
    }

    public String getName() {
        return property.getName();
    }

    public Class<?> getType() {
        return property.getType();
    }

    public Class<?> getContentType() {
        return property.getContentType();
    }

    public DynaProperty unwrap() {
        return property;
    }

    public DynaProperty mapTo(BeanViewInfo info) {
        String mappedName = info.mapTo(getName());
        if (!Objects.equals(mappedName, getName())) {
            return new DynaProperty(mappedName, getType(), getContentType());
        } else {
            return unwrap();
        }
    }

    public static <T, E> BeanViewProperty createProperty(String name, Class<T> type, Class<E> contentType, boolean readable, boolean writeable) {
        return new BeanViewProperty(new DynaProperty(name, type, contentType), readable, writeable);
    }

    public static <T> BeanViewProperty createProperty(String name, Class<T> type, boolean readable, boolean writeable) {
        return new BeanViewProperty(new DynaProperty(name, type), readable, writeable);
    }

    public static <T> BeanViewProperty createProperty(String name, Class<T> type) {
        return createProperty(name, type, true, true);
    }

    public static <T> BeanViewProperty createReadOnlyProperty(String name, Class<T> type) {
        return createProperty(name, type, true, false);
    }

    public static <T, E> BeanViewProperty createReadOnlyProperty(String name, Class<T> type, Class<E> contentType) {
        return createProperty(name, type, contentType, true, false);
    }

    public static <T> BeanViewProperty createWriteOnlyProperty(String name, Class<T> type) {
        return createProperty(name, type, false, true);
    }

    public static <T, E> BeanViewProperty createWriteOnlyProperty(String name, Class<T> type, Class<E> contentType) {
        return createProperty(name, type, contentType, false, true);
    }

    public static <E> BeanViewProperty createCollectionProperty(String name, Class<? extends Collection<E>> type, Class<E> contentType, boolean readable, boolean writeable) {
        return new BeanViewProperty(new DynaProperty(name, type, contentType), readable, writeable);
    }

    public static <E> BeanViewProperty createCollectionProperty(String name, Class<? extends Collection<E>> type, Class<E> contentType) {
        return createCollectionProperty(name, type, contentType, true, true);
    }

    public static <T> BeanViewProperty fromDynaProperty(DynaProperty property, boolean readable, boolean writeable) {
        return new BeanViewProperty(property, readable, writeable);
    }

    public static <T> BeanViewProperty fromDynaProperty(DynaProperty property) {
        return fromDynaProperty(property, true, true);
    }

}
