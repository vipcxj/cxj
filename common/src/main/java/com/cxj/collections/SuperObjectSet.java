/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.Set;

/**
 *
 * @author Administrator
 * @param <E>
 */
public class SuperObjectSet<E> extends SuperAbstractSet<E> {

    private static final long serialVersionUID = -3638152286885376119L;

    private final Class<E> clazz;

    public SuperObjectSet(Class<E> clazz, E... inner) {
        super(inner);
        this.clazz = clazz;
    }

    public SuperObjectSet(Class<E> clazz, Set inner) {
        super(inner, o -> {
            if (clazz.isAssignableFrom(o.getClass())) {
                return (E) o;
            }
            throw new ClassCastException();
        });
        this.clazz = clazz;
    }

    @Override
    public <T> E translateTo(T value) {
        if (clazz.isAssignableFrom(value.getClass())) {
            return (E) value;
        }
        throw new ClassCastException();
    }

    @Override
    public <T> T translateFrom(E value, Class<T> clazz) {
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
    public <T> Set<T> setView(Class<T> clazz) {
        if (this.clazz.isAssignableFrom(clazz)) {
            return super.setView(clazz);
        }
        throw new ClassCastException();
    }

}
