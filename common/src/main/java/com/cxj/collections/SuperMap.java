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
 * @param <E>
 */
public interface SuperMap<K, E> extends Map<K, E> {

    public Map<K, E> innerMap();

    public <T> Map<K, T> mapView(Class<T> clazz);

    public <T> E translateTo(T value);

    public <T> T translateFrom(E value, Class<T> clazz);

    public <T> Object translate(Object value, Class<T> clazz);
}
