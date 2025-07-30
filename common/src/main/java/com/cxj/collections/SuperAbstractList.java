/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Function;

/**
 *
 * @author 陈晓靖
 * @param <E>
 */
public abstract class SuperAbstractList<E> extends SuperAbstractCollection<E> implements SuperList<E> {

    private static final long serialVersionUID = -6011514561177125472L;

    public class SuperListView<T> extends SuperAbstractCollectionView<T> implements List<T> {

        protected class SuperListIterator extends SuperAbstractCollectionIterator implements ListIterator<T> {

            private final ListIterator<E> iterator;

            public SuperListIterator(ListIterator<E> iterator) {
                this.iterator = iterator;
            }

            @Override
            protected Iterator<E> getIterator() {
                return iterator;
            }

            @Override
            public boolean hasPrevious() {
                return iterator.hasPrevious();
            }

            @Override
            public T previous() {
                return translateFrom(iterator.previous(), clazz);
            }

            @Override
            public int nextIndex() {
                return iterator.nextIndex();
            }

            @Override
            public int previousIndex() {
                return iterator.previousIndex();
            }

            @Override
            public void remove() {
                iterator.remove();
            }

            @Override
            public void set(T e) {
                iterator.set(translateTo(e));
            }

            @Override
            public void add(T e) {
                iterator.add(translateTo(e));
            }

        }

        class SubList<E> extends SuperListView<E> {

            private final SuperListView<E> l;
            private final int offset;
            private int size;

            SubList(SuperListView<E> list, int fromIndex, int toIndex) {
                super(list.clazz);
                if (fromIndex < 0) {
                    throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
                }
                if (toIndex > list.size()) {
                    throw new IndexOutOfBoundsException("toIndex = " + toIndex);
                }
                if (fromIndex > toIndex) {
                    throw new IllegalArgumentException("fromIndex(" + fromIndex
                            + ") > toIndex(" + toIndex + ")");
                }
                l = list;
                offset = fromIndex;
                size = toIndex - fromIndex;
            }

            @Override
            public E set(int index, E element) {
                rangeCheck(index);
                return l.set(index + offset, element);
            }

            @Override
            public E get(int index) {
                rangeCheck(index);
                return l.get(index + offset);
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public void add(int index, E element) {
                rangeCheckForAdd(index);
                l.add(index + offset, element);
                size++;
            }

            @Override
            public E remove(int index) {
                rangeCheck(index);
                E result = l.remove(index + offset);
                size--;
                return result;
            }

            @Override
            protected void removeRange(int fromIndex, int toIndex) {
                l.removeRange(fromIndex + offset, toIndex + offset);
                size -= (toIndex - fromIndex);
            }

            @Override
            public boolean addAll(Collection<? extends E> c) {
                return addAll(size, c);
            }

            @Override
            public boolean addAll(int index, Collection<? extends E> c) {
                rangeCheckForAdd(index);
                int cSize = c.size();
                if (cSize == 0) {
                    return false;
                }

                l.addAll(offset + index, c);
                size += cSize;
                return true;
            }

            @Override
            public Iterator<E> iterator() {
                return listIterator();
            }

            @Override
            public ListIterator<E> listIterator(final int index) {
                rangeCheckForAdd(index);

                return new ListIterator<E>() {
                    private final ListIterator<E> i = l.listIterator(index + offset);

                    @Override
                    public boolean hasNext() {
                        return nextIndex() < size;
                    }

                    @Override
                    public E next() {
                        if (hasNext()) {
                            return i.next();
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public boolean hasPrevious() {
                        return previousIndex() >= 0;
                    }

                    @Override
                    public E previous() {
                        if (hasPrevious()) {
                            return i.previous();
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public int nextIndex() {
                        return i.nextIndex() - offset;
                    }

                    @Override
                    public int previousIndex() {
                        return i.previousIndex() - offset;
                    }

                    @Override
                    public void remove() {
                        i.remove();
                        size--;
                    }

                    @Override
                    public void set(E e) {
                        i.set(e);
                    }

                    @Override
                    public void add(E e) {
                        i.add(e);
                        size++;
                    }
                };
            }

            @Override
            public List<E> subList(int fromIndex, int toIndex) {
                return new SubList<>(this, fromIndex, toIndex);
            }

            private void rangeCheck(int index) {
                if (index < 0 || index >= size) {
                    throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
                }
            }

            private void rangeCheckForAdd(int index) {
                if (index < 0 || index > size) {
                    throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
                }
            }

            private String outOfBoundsMsg(int index) {
                return "Index: " + index + ", Size: " + size;
            }
        }

        class RandomAccessSubList<E> extends SubList<E> implements RandomAccess {

            RandomAccessSubList(SuperListView<E> list, int fromIndex, int toIndex) {
                super(list, fromIndex, toIndex);
            }

            @Override
            public List<E> subList(int fromIndex, int toIndex) {
                return new RandomAccessSubList<>(this, fromIndex, toIndex);
            }
        }

        public SuperListView(Class<T> clazz) {
            super(clazz);
        }

        private String outOfBoundsMsg(int index) {
            return "Index: " + index + ", Size: " + size();
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > size()) {
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
            }
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        protected void removeRange(int fromIndex, int toIndex) {
            ListIterator<T> it = listIterator(fromIndex);
            for (int i = 0, n = toIndex - fromIndex; i < n; i++) {
                it.next();
                it.remove();
            }
        }

        @Override
        public boolean addAll(int index, Collection<? extends T> c) {
            rangeCheckForAdd(index);
            boolean modified = false;
            for (T e : c) {
                add(index++, e);
                modified = true;
            }
            return modified;
        }

        @Override
        public T get(int index) {
            return translateFrom(innerList().get(index), clazz);
        }

        @Override
        public T set(int index, T element) {
            return translateFrom(innerList().set(index, translateTo(element)), clazz);
        }

        @Override
        public void add(int index, T element) {
            innerList().add(index, translateTo(element));
        }

        @Override
        public T remove(int index) {
            return translateFrom(innerList().remove(index), clazz);
        }

        @Override
        public int indexOf(Object o) {
            return innerList().indexOf(translate(o, clazz));
        }

        @Override
        public int lastIndexOf(Object o) {
            return innerList().lastIndexOf(translate(o, clazz));
        }

        @Override
        public ListIterator<T> listIterator() {
            return listIterator(0);
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            return new SuperListIterator(SuperAbstractList.this.listIterator(index));
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            return new SubList<>(this, fromIndex, toIndex);
        }

    }

    private final List<E> inner;

    public SuperAbstractList(List inner) {
        this.inner = new ArrayList<>();
        inner.stream().forEach((o) -> {
            this.inner.add(translateTo(o));
        });
    }

    public SuperAbstractList(List inner, Function<Object, E> translater) {
        this.inner = new ArrayList<>();
        inner.stream().forEach((o) -> {
            this.inner.add(translater.apply(o));
        });
    }

    public SuperAbstractList(E... inner) {
        this.inner = new ArrayList<>(Arrays.asList(inner));
    }

    @Override
    public Collection<E> innerCollection() {
        return innerList();
    }

    @Override
    public List<E> innerList() {
        return inner;
    }

    @Override
    public E get(int index) {
        return innerList().get(index);
    }

    @Override
    public E set(int index, E element) {
        return innerList().set(index, element);
    }

    @Override
    public void add(int index, E element) {
        innerList().add(index, element);
    }

    @Override
    public E remove(int index) {
        return innerList().remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return innerList().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return innerList().lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return innerList().listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return innerList().listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return innerList().subList(fromIndex, toIndex);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return innerList().addAll(index, c);
    }

    @Override
    public <T> List<T> listView(Class<T> clazz) {
        return new SuperListView<>(clazz);
    }

    @Override
    public <T> Collection<T> collectionView(Class<T> clazz) {
        return listView(clazz);
    }
}
