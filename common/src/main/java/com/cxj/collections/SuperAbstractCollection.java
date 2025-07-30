/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 *
 * @author 陈晓靖
 * @param <E>
 */
public abstract class SuperAbstractCollection<E> implements SuperCollection<E>, Serializable {

    private static final long serialVersionUID = 8717008152388596388L;

    public abstract class SuperAbstractCollectionView<T> implements Collection<T> {

        protected final Class<T> clazz;

        public SuperAbstractCollectionView(Class<T> clazz) {
            this.clazz = clazz;
        }

        protected abstract class SuperAbstractCollectionIterator implements Iterator<T> {

            protected Iterator<E> getIterator() {
                return innerCollection().iterator();
            }

            @Override
            public boolean hasNext() {
                return getIterator().hasNext();
            }

            @Override
            public T next() {
                return translateFrom(getIterator().next(), clazz);
            }

            @Override
            public void remove() {
                getIterator().remove();
            }
        }

        @Override
        public int size() {
            return SuperAbstractCollection.this.innerCollection().size();
        }

        @Override
        public boolean isEmpty() {
            return SuperAbstractCollection.this.innerCollection().isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return SuperAbstractCollection.this.innerCollection().contains(translate(o, clazz));
        }

        @Override
        public abstract Iterator<T> iterator();

        @Override
        public Object[] toArray() {
            // Estimate size of array; be prepared to see more or fewer elements
            Object[] r = new Object[size()];
            Iterator<T> it = iterator();
            for (int i = 0; i < r.length; i++) {
                if (!it.hasNext()) // fewer elements than expected
                {
                    return Arrays.copyOf(r, i);
                }
                r[i] = it.next();
            }
            return it.hasNext() ? finishToArray(r, it) : r;
        }

        @Override
        public <E> E[] toArray(E[] a) {
            // Estimate size of array; be prepared to see more or fewer elements
            int size = size();
            E[] r = a.length >= size ? a
                    : (E[]) java.lang.reflect.Array
                    .newInstance(a.getClass().getComponentType(), size);
            Iterator<T> it = iterator();

            for (int i = 0; i < r.length; i++) {
                if (!it.hasNext()) { // fewer elements than expected
                    if (a == r) {
                        r[i] = null; // null-terminate
                    } else if (a.length < i) {
                        return Arrays.copyOf(r, i);
                    } else {
                        System.arraycopy(r, 0, a, 0, i);
                        if (a.length > i) {
                            a[i] = null;
                        }
                    }
                    return a;
                }
                r[i] = (E) it.next();
            }
            // more elements than expected
            return it.hasNext() ? finishToArray(r, it) : r;
        }

        @Override
        public boolean add(T e) {
            return SuperAbstractCollection.this.innerCollection().add(translateTo(e));
        }

        @Override
        public boolean remove(Object o) {
            return SuperAbstractCollection.this.innerCollection().remove(translate(o, clazz));
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            for (Object e : c) {
                if (!contains(e)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            boolean modified = false;
            for (T e : c) {
                if (add(e)) {
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            Objects.requireNonNull(c);
            boolean modified = false;
            Iterator<T> it = iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            Objects.requireNonNull(c);
            boolean modified = false;
            Iterator<T> it = iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }

        @Override
        public void clear() {
            SuperAbstractCollection.this.innerCollection().clear();
        }

        @Override
        public String toString() {
            return innerCollection().toString();
        }

    }

    @Override
    public int size() {
        return innerCollection().size();
    }

    @Override
    public boolean isEmpty() {
        return innerCollection().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return innerCollection().contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return innerCollection().iterator();
    }

    @Override
    public Object[] toArray() {
        return innerCollection().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return innerCollection().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return innerCollection().add(e);
    }

    @Override
    public boolean remove(Object o) {
        return innerCollection().remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return innerCollection().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return innerCollection().addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return innerCollection().removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return innerCollection().retainAll(c);
    }

    @Override
    public void clear() {
        innerCollection().clear();
    }

    /**
     * The maximum size of array to allocate. Some VMs reserve some header words
     * in an array. Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Reallocates the array being used within toArray when the iterator
     * returned more elements than expected, and finishes filling it from the
     * iterator.
     *
     * @param r the array, replete with previously stored elements
     * @param it the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any
     * further elements returned by the iterator, trimmed to size
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] finishToArray(T[] r, Iterator<?> it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0) {
                    newCap = hugeCapacity(cap + 1);
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = (T) it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
        {
            throw new OutOfMemoryError("Required array size too large");
        }
        return (minCapacity > MAX_ARRAY_SIZE)
                ? Integer.MAX_VALUE
                : MAX_ARRAY_SIZE;
    }

    @Override
    public String toString() {
        return innerCollection().toString();
    }

}
