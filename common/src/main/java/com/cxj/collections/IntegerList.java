/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import com.cxj.collections.SuperAbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 陈晓靖
 */
public class IntegerList extends SuperAbstractList<Long> {

    private static final long serialVersionUID = 2275279832161861611L;

    public IntegerList(List<Long> inner) {
        super(inner);
    }

    public IntegerList(Long... inner) {
        super(inner);
    }

    public IntegerList(Integer... inner) {
        super(new ArrayList<>(inner.length));
        for (Integer v : inner) {
            innerList().add((long) v);
        }
    }

    public IntegerList(Short... inner) {
        super(new ArrayList<>(inner.length));
        for (Short v : inner) {
            innerList().add((long) v);
        }
    }

    public List<Integer> intView() {
        return listView(Integer.class);
    }

    public List<Short> shortView() {
        return listView(Short.class);
    }

    public List<Long> longView() {
        return innerList();
    }

    @Override
    public <T> Long translateTo(T value) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return (long) (int) (Integer) value;
        } else if (value instanceof Short) {
            return (long) (short) (Short) value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public <T> T translateFrom(Long value, Class<T> clazz) {
        if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return (T) value;
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            checkNumberTypeRange(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
            return (T) (Integer) (int) (long) value;
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            checkNumberTypeRange(value, Short.MIN_VALUE, Short.MAX_VALUE);
            return (T) (Short) (short) (long) value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public <T> Object translate(Object value, Class<T> clazz) {
        if (Long.class.equals(clazz) || long.class.equals(clazz)) {
            return value;
        } else if (Integer.class.equals(clazz) || int.class.equals(clazz)) {
            return (int) (long) value;
        } else if (Short.class.equals(clazz) || short.class.equals(clazz)) {
            return (short) (long) value;
        } else {
            return value;
        }
    }

    private void checkNumberTypeRange(long value, long min, long max) {
        if (value > max || value < min) {
            throw new IllegalArgumentException("Out of number range!");
        }
    }
}
