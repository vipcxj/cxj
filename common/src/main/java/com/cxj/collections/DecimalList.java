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
public class DecimalList extends SuperAbstractList<Double> {

    private static final long serialVersionUID = -2116668313996153079L;

    public DecimalList(List<Double> inner) {
        super(inner);
    }

    public DecimalList(Double... inner) {
        super(inner);
    }

    public DecimalList(Float... inner) {
        super(new ArrayList<>(inner.length));
        for (Float v : inner) {
            innerList().add((double) v);
        }
    }

    public List<Float> floatView() {
        return listView(Float.class);
    }

    public List<Double> doubleView() {
        return innerList();
    }

    @Override
    public <T> Double translateTo(T value) {
        if (value instanceof Double) {
            return (Double) value;
        } else if (value instanceof Float) {
            return (double) (float) (Float) value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public <T> T translateFrom(Double value, Class<T> clazz) {
        if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return (T) value;
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            checkNumberTypeRange(value, Float.MIN_VALUE, Float.MAX_VALUE);
            return (T) (Float) (float) (double) value;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public <T> Object translate(Object value, Class<T> clazz) {
        if (Double.class.equals(clazz) || double.class.equals(clazz)) {
            return value;
        } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
            return (float) (double) value;
        } else {
            return value;
        }
    }

    private void checkNumberTypeRange(double value, double min, double max) {
        if (value > max || value < min) {
            throw new IllegalArgumentException("Out of number range!");
        }
    }
}
