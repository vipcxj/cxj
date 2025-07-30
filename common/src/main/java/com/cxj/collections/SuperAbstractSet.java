/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author Administrator
 * @param <E>
 */
public abstract class SuperAbstractSet<E> extends SuperAbstractCollection<E> implements SuperSet<E> {

    private static final long serialVersionUID = -7039015480160350014L;

    public class SuperSetView<T> extends SuperAbstractCollectionView<T> implements Set<T> {

        protected class SuperSetIterator extends SuperAbstractCollectionIterator {
        }

        public SuperSetView(Class<T> clazz) {
            super(clazz);
        }

        @Override
        public Iterator<T> iterator() {
            return new SuperSetIterator();
        }

    }

    private final Set<E> inner;

    public SuperAbstractSet(Set inner) {
        this.inner = new HashSet<>();
        inner.stream().forEach((o) -> {
            this.inner.add(translateTo(o));
        });
    }

    public SuperAbstractSet(Set inner, Function<Object, E> translater) {
        this.inner = new HashSet<>();
        inner.stream().forEach((o) -> {
            this.inner.add(translater.apply(o));
        });
    }

    public SuperAbstractSet(E... inner) {
        this.inner = new HashSet<>(Arrays.asList(inner));
    }

    @Override
    public Collection<E> innerCollection() {
        return innerSet();
    }

    @Override
    public <T> Collection<T> collectionView(Class<T> clazz) {
        return setView(clazz);
    }

    @Override
    public Set<E> innerSet() {
        return inner;
    }

    @Override
    public <T> Set<T> setView(Class<T> clazz) {
        return new SuperSetView<>(clazz);
    }

}
