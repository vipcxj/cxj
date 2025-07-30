/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * @author Administrator
 * @param <K>
 * @param <V>
 */
public abstract class SuperAbstractMap<K, V> implements SuperMap<K, V>, Serializable {

    private static final long serialVersionUID = -8092607351515675041L;

    class MapTypeTranslager implements TypeTranslater<V> {

        @Override
        public <T> Object translate(Object value, Class<T> clazz) {
            return SuperAbstractMap.this.translate(value, clazz);
        }

        @Override
        public <T> T translateFrom(V value, Class<T> clazz) {
            return SuperAbstractMap.this.translateFrom(value, clazz);
        }

        @Override
        public <T> V translateTo(T value) {
            return SuperAbstractMap.this.translateTo(value);
        }

    }

    class SuperMapView<T> implements Map<K, T> {

        class SuperEntry implements Entry<K, T> {

            private final Entry<K, V> entry;

            public SuperEntry(Entry<K, V> entry) {
                this.entry = entry;
            }

            @Override
            public K getKey() {
                return entry.getKey();
            }

            @Override
            public T getValue() {
                return translateFrom(entry.getValue(), clazz);
            }

            @Override
            public T setValue(T value) {
                return translateFrom(entry.setValue(translateTo(value)), clazz);
            }

        }

        private final Class<T> clazz;

        public SuperMapView(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public void clear() {
            SuperAbstractMap.this.clear();
        }

        @Override
        public boolean containsKey(Object key) {
            return SuperAbstractMap.this.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return SuperAbstractMap.this.containsValue(translate(value, clazz));
        }

        @Override
        public Set<Entry<K, T>> entrySet() {
            Set<Entry<K, T>> entries = new HashSet<>();
            SuperAbstractMap.this.entrySet().stream().forEach((entry) -> {
                entries.add(new SuperEntry(entry));
            });
            return entries;
        }

        @Override
        public T get(Object key) {
            return translateFrom(SuperAbstractMap.this.get(key), clazz);
        }

        @Override
        public boolean isEmpty() {
            return SuperAbstractMap.this.isEmpty();
        }

        @Override
        public Set<K> keySet() {
            return SuperAbstractMap.this.keySet();
        }

        @Override
        public T put(K key, T value) {
            return translateFrom(SuperAbstractMap.this.put(key, translateTo(value)), clazz);
        }

        @Override
        public void putAll(Map<? extends K, ? extends T> m) {
            m.entrySet().stream().forEach((entry) -> {
                put(entry.getKey(), entry.getValue());
            });
        }

        @Override
        public T remove(Object key) {
            return translateFrom(SuperAbstractMap.this.remove(key), clazz);
        }

        @Override
        public int size() {
            return SuperAbstractMap.this.size();
        }

        @Override
        public Collection<T> values() {
            return new BaseSuperCollection<>(SuperAbstractMap.this.values(), new MapTypeTranslager()).collectionView(clazz);
        }

        @Override
        public String toString() {
            return innerMap().toString();
        }

    }

    private final Map<K, V> inner;

    public SuperAbstractMap(Map<K, ?> inner) {
        this.inner = new HashMap<>();
        inner.entrySet().stream().forEach(entry -> {
            this.inner.put(entry.getKey(), translateTo(entry.getValue()));
        });
    }

    public SuperAbstractMap(Map<K, ?> inner, Function<Object, V> translater) {
        this.inner = new HashMap<>();
        inner.entrySet().stream().forEach(entry -> {
            this.inner.put(entry.getKey(), translater.apply(entry.getValue()));
        });
    }

    @Override
    public void clear() {
        inner.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return inner.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return inner.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return inner.entrySet();
    }

    @Override
    public V get(Object key) {
        return inner.get(key);
    }

    @Override
    public Map<K, V> innerMap() {
        return inner;
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return inner.keySet();
    }

    @Override
    public <T> Map<K, T> mapView(Class<T> clazz) {
        return new SuperMapView<>(clazz);
    }

    @Override
    public V put(K key, V value) {
        return inner.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        inner.putAll(m);
    }

    @Override
    public V remove(Object key) {
        return inner.remove(key);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public Collection<V> values() {
        return inner.values();
    }

    @Override
    public String toString() {
        return innerMap().toString();
    }

}
