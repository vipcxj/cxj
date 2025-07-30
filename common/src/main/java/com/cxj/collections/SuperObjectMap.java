/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.Map;

/**
 *
 * @author Administrator
 * @param <K>
 * @param <V>
 */
public class SuperObjectMap<K, V> extends SuperAbstractMap<K, V> {

    private static final long serialVersionUID = -2629737546727753848L;

    private final Class<V> clazz;

    public SuperObjectMap(Class<V> clazz, Map<K, ?> inner) {
        super(inner, o -> {
            if (clazz.isAssignableFrom(o.getClass())) {
                return (V) o;
            }
            throw new ClassCastException();
        });
        this.clazz = clazz;
    }

    @Override
    public <T> V translateTo(T value) {
        if (clazz.isAssignableFrom(value.getClass())) {
            return (V) value;
        }
        throw new ClassCastException();
    }

    @Override
    public <T> T translateFrom(V value, Class<T> clazz) {
        if (clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        throw new ClassCastException();
    }

    @Override
    public <T> Object translate(Object value, Class<T> clazz) {
        return value;
    }

    @Override
    public <T> Map<K, T> mapView(Class<T> clazz) {
        if (this.clazz.isAssignableFrom(clazz)) {
            return super.mapView(clazz);
        }
        throw new ClassCastException();
    }

}
