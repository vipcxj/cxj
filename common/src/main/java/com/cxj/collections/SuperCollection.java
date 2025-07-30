/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.Collection;

/**
 *
 * @author 陈晓靖
 * @param <E>
 */
public interface SuperCollection<E> extends Collection<E> {

    public Collection<E> innerCollection();

    public <T> Collection<T> collectionView(Class<T> clazz);

    public <T> E translateTo(T value);

    public <T> T translateFrom(E value, Class<T> clazz);

    public <T> Object translate(Object value, Class<T> clazz);
}
