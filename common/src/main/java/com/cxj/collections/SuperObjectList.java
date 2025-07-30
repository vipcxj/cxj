/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.List;

/**
 * 元素以本类或其基类形式存放于容器中，支持转化为本类或本类子类的容器视图 例如：<p>
 * @author 陈晓靖
 *
 * @param <E> 本类或其基类
 */
public class SuperObjectList<E> extends SuperAbstractList<E> {

    private static final long serialVersionUID = 3081268923899759296L;

    private final Class<E> clazz;

    public SuperObjectList(Class<E> clazz, E... inner) {
        super(inner);
        this.clazz = clazz;
    }

    public SuperObjectList(Class<E> clazz, List inner) {
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
    public <T> List<T> listView(Class<T> clazz) {
        if (this.clazz.isAssignableFrom(clazz)) {
            return super.listView(clazz);
        }
        throw new ClassCastException();
    }

}
