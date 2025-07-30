/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import com.cxj.collections.SuperAbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Administrator
 * @param <E>
 */
public class BaseSuperCollection<E> extends SuperAbstractCollection<E> {

    class SuperCollectionView<T> extends SuperAbstractCollectionView<T> implements Collection<T> {

        class SuperCollectionIterator extends SuperAbstractCollectionIterator {

        }

        public SuperCollectionView(Class<T> clazz) {
            super(clazz);
        }

        @Override
        public Iterator<T> iterator() {
            return new SuperCollectionIterator();
        }

    }

    private final Collection<E> inner;
    private final TypeTranslater<E> translater;

    public BaseSuperCollection(Collection<E> inner, TypeTranslater<E> translater) {
        this.inner = inner;
        this.translater = translater;
    }
    
    

    @Override
    public <T> Collection<T> collectionView(Class<T> clazz) {
        return new SuperCollectionView<>(clazz);
    }

    @Override
    public Collection<E> innerCollection() {
        return inner;
    }

    @Override
    public <T> Object translate(Object value, Class<T> clazz) {
        return translater.translate(value, clazz);
    }

    @Override
    public <T> T translateFrom(E value, Class<T> clazz) {
        return translater.translateFrom(value, clazz);
    }

    @Override
    public <T> E translateTo(T value) {
        return translater.translateTo(value);
    }

}
