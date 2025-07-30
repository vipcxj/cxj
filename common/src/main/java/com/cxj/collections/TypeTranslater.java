/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

/**
 *
 * @author 陈晓靖
 * @param <E>
 */
public interface TypeTranslater<E> {

    public <T> E translateTo(T value);

    public <T> T translateFrom(E value, Class<T> clazz);

    public <T> Object translate(Object value, Class<T> clazz);
}
