/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author Administrator
 * @param <E>
 */
public abstract class SuperObjectCollection<E> extends SuperAbstractCollection<E> {

    private static final long serialVersionUID = -1958060379043118228L;

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

    public abstract Class<E> getElementClass();

    @Override
    public <T> Collection<T> collectionView(Class<T> clazz) {
        if (getElementClass().isAssignableFrom(clazz)) {
            return new SuperCollectionView<>(clazz);
        }
        throw new ClassCastException();
    }

    @Override
    public <T> E translateTo(T value) {
        if (getElementClass().isAssignableFrom(value.getClass())) {
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

}
