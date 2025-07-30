/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import com.cxj.collections.SuperObjectList;
import com.cxj.collections.SuperObjectSet;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author 陈晓靖
 */
public class CollectionHelper {

    public static <K, E> Map<K, E> mapFrom(K key, E value) {
        return Collections.singletonMap(key, value);
    }

    public static <K, E> Map<K, E> mapFrom(K key0, E value0, K key1, E value1) {
        Map<K, E> map = new HashMap<>();
        map.put(key0, value0);
        map.put(key1, value1);
        return Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, E> mapFrom(K key0, E value0, K key1, E value1, K key2, E value2) {
        Map<K, E> map = new HashMap<>();
        map.put(key0, value0);
        map.put(key1, value1);
        map.put(key2, value2);
        return Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, E> mapFrom(K key0, E value0, K key1, E value1, K key2, E value2, K key3, E value3) {
        Map<K, E> map = new HashMap<>();
        map.put(key0, value0);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        return Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, E> mapFrom(
            K key0, E value0,
            K key1, E value1,
            K key2, E value2,
            K key3, E value3,
            K key4, E value4
    ) {
        Map<K, E> map = new HashMap<>();
        map.put(key0, value0);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        return Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, E> mapFrom(
            K key0, E value0,
            K key1, E value1,
            K key2, E value2,
            K key3, E value3,
            K key4, E value4,
            K key5, E value5
    ) {
        Map<K, E> map = new HashMap<>();
        map.put(key0, value0);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        return Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, E> mapFrom(
            K key0, E value0,
            K key1, E value1,
            K key2, E value2,
            K key3, E value3,
            K key4, E value4,
            K key5, E value5,
            K key6, E value6
    ) {
        Map<K, E> map = new HashMap<>();
        map.put(key0, value0);
        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.put(key4, value4);
        map.put(key5, value5);
        map.put(key6, value6);
        return Collections.unmodifiableMap(map);
    }

    public static <K, E> Map<K, E> mapFrom(@Nonnull List<? extends K> keys, @Nonnull List<? extends E> elements) {
        assert keys.size() == elements.size();
        Map<K, E> m = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            m.put(keys.get(i), elements.get(i));
        }
        return m;
    }

    public static <E> Set<E> setFrom(E value) {
        return Collections.singleton(value);
    }

    public static <E> Set<E> setFrom(E value0, E value1) {
        Set<E> set = new HashSet<>();
        set.add(value0);
        set.add(value1);
        return Collections.unmodifiableSet(set);
    }

    public static <E> Set<E> setFrom(E value0, E value1, E value2) {
        Set<E> set = new HashSet<>();
        set.add(value0);
        set.add(value1);
        set.add(value2);
        return Collections.unmodifiableSet(set);
    }

    public static <E> Set<E> setFrom(E value0, E value1, E value2, E value3) {
        Set<E> set = new HashSet<>();
        set.add(value0);
        set.add(value1);
        set.add(value2);
        set.add(value3);
        return Collections.unmodifiableSet(set);
    }

    public static <E> Set<E> setFrom(E value0, E value1, E value2, E value3, E value4) {
        Set<E> set = new HashSet<>();
        set.add(value0);
        set.add(value1);
        set.add(value2);
        set.add(value3);
        set.add(value4);
        return Collections.unmodifiableSet(set);
    }

    public static <E> Set<E> setFrom(E value0, E value1, E value2, E value3, E value4, E value5) {
        Set<E> set = new HashSet<>();
        set.add(value0);
        set.add(value1);
        set.add(value2);
        set.add(value3);
        set.add(value4);
        set.add(value5);
        return Collections.unmodifiableSet(set);
    }

    @SafeVarargs
    public static <E> Set<E> setFrom(
            E value0, E value1, E value2,
            E value3, E value4, E value5,
            E value6, E... others) {
        Set<E> set = new HashSet<>();
        set.add(value0);
        set.add(value1);
        set.add(value2);
        set.add(value3);
        set.add(value4);
        set.add(value5);
        set.add(value6);
        set.addAll(Arrays.asList(others));
        return Collections.unmodifiableSet(set);
    }

    public static <T> T[] appendArray(T[] strArray, T other) {
        T[] newArray = Arrays.copyOf(strArray, strArray.length + 1);
        newArray[newArray.length - 1] = other;
        return newArray;
    }

    /**
     * 为list排序，返回排序后的原下标，即：<br>
     * List oldList = new ArrayList(list);<br>
     * int indexes = sortList(list, comparator);<br>
     * oldList.get(indexes[i]) == list.get(i);
     *
     * @param <T>
     * @param list
     * @param comparator
     * @return
     */
    public static <T> int[] sortList(List<T> list, Comparator<T> comparator) {
        int lSize = list.size();
        List<Pair<T, Integer>> listIndexMap = new ArrayList<>(lSize);
        for (int i = 0; i < lSize; i++) {
            listIndexMap.add(i, Pair.of(list.get(i), i));
        }
        if (comparator != null) {
            listIndexMap.sort((a1, a2) -> Objects.compare(a1.getKey(), a2.getKey(), comparator));
        } else {
            listIndexMap.sort((a1, a2) -> Objects.compare(a1.getKey(), a2.getKey(), (b1, b2) -> ((Comparable) b1).compareTo(b2)));
        }
        for (int i = 0; i < lSize; i++) {
            list.set(i, listIndexMap.get(i).getKey());
        }
        int[] indexes = new int[lSize];
        for (int i = 0; i < lSize; i++) {
            indexes[i] = listIndexMap.get(i).getValue();
        }
        return indexes;
    }

    /**
     * 使用indexes重排列list, 例如：<br>
     * <code>
     * List oldList = new ArrayList(list);<br>
     * rearrangeList(list, indexes);<br>
     * 满足:<br>当indexes[i] != -1时，<br>
     * list.get(i) == oldList.get(indexes[i]);<br>
     * 当indexes[i] == -1时，<br>
     * list.get(i) == oldList.get(i);<br>
     * </code> note: <br>list必须事先申请好足够空间
     *
     * @param <T>
     * @param list
     * @param indexes
     * @see #sortList(java.util.List, java.util.Comparator)
     * sortList(List&lt;T&gt; list, Comparator&lt;T&gt; comparator)
     */
    public static <T> void rearrangeList(List<T> list, int[] indexes) {
        List<T> tmp = new ArrayList<>(list);
        for (int i = 0; i < indexes.length; i++) {
            if (indexes[i] != -1) {
                list.set(i, tmp.get(indexes[i]));
            }
        }
    }

    /**
     * 去重，不会新建容器，直接在原容器去重
     *
     * @param <T> 容器元素类型
     * @param list 容器
     * @return 去重后的原容器
     */
    public static <T> List<T> uniqueList(List<T> list) {
        return uniqueList(list, null);
    }

    /**
     * 去重，不会新建容器，直接在原容器去重
     *
     * @param <T> 容器元素类型
     * @param list 容器
     * @param comparator 用来比较2个元素是否相同的方法
     * @return 去重后的原容器
     */
    public static <T> List<T> uniqueList(List<T> list, Comparator<T> comparator) {
        Set<T> tmp = comparator != null ? new TreeSet<>(comparator) : new HashSet<>();
        for (ListIterator<T> iter1 = list.listIterator(), iter2 = list.listIterator(); iter1.hasNext();) {
            T var = iter1.next();
            if (!tmp.contains(var)) {
                tmp.add(var);
                iter2.next();
                iter2.set(var);
            }
        }
        list.subList(tmp.size(), list.size()).clear();
        return list;
    }

    /**
     * 去重并从小到大排序，不会新建容器，直接在原容器去重
     *
     * @param <T> 容器元素类型
     * @param list 容器
     * @return 去重后的原容器
     */
    public static <T> List<T> uniqueListAndSort(List<T> list) {
        TreeSet<T> tmp = new TreeSet<>(list);
        list.clear();
        list.addAll(tmp);
        return list;
    }

    /**
     * 去重并从小到大排序，不会新建容器，直接在原容器去重
     *
     * @param <T> 容器元素类型
     * @param list 容器
     * @param comparator 用来比较2个元素是否相同的方法
     * @return 去重后的原容器
     */
    public static <T> List<T> uniqueListAndSort(List<T> list, Comparator<T> comparator) {
        TreeSet<T> tmp = new TreeSet<>(comparator);
        tmp.addAll(list);
        list.clear();
        list.addAll(tmp);
        return list;
    }

    /**
     * 按predicate筛选容器，不符合条件的元素将被删除，直接再原容器筛选，不新建容器
     *
     * @param <T> 容器元素类型
     * @param collection 容器
     * @param predicate 筛选方法
     * @return 筛选后的原容器
     */
    public static <T> Collection<T> filterCollection(Collection<T> collection, Predicate<T> predicate) {
        for (Iterator<T> iterator = collection.iterator(); iterator.hasNext();) {
            T next = iterator.next();
            if (!predicate.test(next)) {
                iterator.remove();
            }
        }
        return collection;
    }

    /**
     * 按predicate筛选容器，不符合条件的元素将被删除，直接再原容器筛选，不新建容器
     *
     * @param <T> 容器元素类型
     * @param list 容器
     * @param predicate 筛选方法
     * @return 筛选后的原容器
     */
    public static <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
        ListIterator<T> iter1 = list.listIterator(), iter2 = list.listIterator();
        for (; iter1.hasNext();) {
            T next = iter1.next();
            if (predicate.test(next)) {
                iter2.next();
                iter2.set(next);
            }
        }
        list.subList(iter2.nextIndex(), list.size()).clear();
        return list;
    }

    /**
     * 转换容器元素
     *
     * @param <T> 转换前的元素类型
     * @param <R> 转换后的元素类型
     * @param collection 待转换的容器
     * @param out 存放转换结果的容器，应为空，或者里面数据无关紧要，因为会先被置空
     * @param translater 转换器
     * @return out
     */
    public static <T, R> Collection<R> transCollection(Collection<T> collection, Collection<R> out, Function<T, R> translater) {
        out.clear();
        collection.stream().forEach((v) -> {
            out.add(translater.apply(v));
        });
        return out;
    }

    /**
     * 提升元素类型，返回的容器仅仅是个视图，即修改新容器，同时会影响原容器
     *
     * @param <T> 原始类型
     * @param <R> 提升后的类型
     * @param list 待提升的容器
     * @param clazz 需要提升的目标类型，必须是原始类型的子类
     * @return 提升类型容器视图
     */
    public static <T, R extends T> List<R> typeList(List<T> list, Class<R> clazz) {
        filterList(list, v -> {
            return v != null && clazz.isAssignableFrom(v.getClass());
        });
        return new SuperObjectList<>(clazz, list).listView(clazz);
    }

    /**
     * 提升元素类型，返回的容器仅仅是个视图，即修改新容器，同时会影响原容器
     *
     * @param <T> 原始类型
     * @param <R> 提升后的类型
     * @param set 待提升的容器
     * @param clazz 需要提升的目标类型，必须是原始类型的子类
     * @return 提升类型容器视图
     */
    public static <T, R extends T> Set<R> typeSet(Set<T> set, Class<R> clazz) {
        filterCollection(set, v -> v != null && clazz.isAssignableFrom(v.getClass()));
        return new SuperObjectSet<>(clazz, set).setView(clazz);
    }

    /**
     * 提升元素类型，返回新的容器，即修改新容器，不会影响原容器
     *
     * @param <T> 原始类型
     * @param <R> 提升后的类型
     * @param list 待提升的容器
     * @param clazz 需要提升的目标类型，必须是原始类型的子类
     * @return 提升类型后的容器
     */
    public static <T, R extends T> List<R> separateTypeList(List<T> list, Class<R> clazz) {
        List<R> typeList = new ArrayList<>();
        list.stream().filter(v -> v != null && clazz.isAssignableFrom(v.getClass())).forEach(v -> {
            //noinspection unchecked
            typeList.add((R) v);
        });
        return typeList;
    }

    /**
     * 提升元素类型，返回新的容器，即修改新容器，不会影响原容器
     *
     * @param <T> 原始类型
     * @param <R> 提升后的类型
     * @param set 待提升的容器
     * @param clazz 需要提升的目标类型，必须是原始类型的子类
     * @return 提升类型后的容器
     */
    public static <T, R extends T> Set<R> separateTypeSet(Set<T> set, Class<R> clazz) {
        Set<R> typeSet = new HashSet<>();
        set.stream().filter(v -> v != null && clazz.isAssignableFrom(v.getClass())).forEach(v -> {
            //noinspection unchecked
            typeSet.add((R) v);
        });
        return typeSet;
    }

    /**
     * 提升元素类型，返回新的容器，即修改新容器，不会影响原容器
     *
     * @param <K> 键值类型
     * @param <T> 原始类型
     * @param <R> 提升后的类型
     * @param map 待提升的容器
     * @param clazz 需要提升的目标类型，必须是原始类型的子类
     * @return 提升类型后的容器
     */
    public static <K, T, R extends T> Map<K, R> separateTypeMap(Map<K, T> map, Class<R> clazz) {
        Map<K, R> typeMap = new HashMap<>();
        map.entrySet().stream().filter(v -> v.getValue() != null && clazz.isAssignableFrom(v.getValue().getClass())).forEach(v -> {
            //noinspection unchecked
            typeMap.put(v.getKey(), (R) v.getValue());
        });
        return typeMap;
    }

    /**
     * 根据指定方法从容器元素获取key，并生成一个map
     *
     * @param <K> 键类型
     * @param <V> 值类型
     * @param collection 容器
     * @param keyGetter 从容器元素得到key的方法
     * @return 新建的map
     */
    public static <K, V> Map<K, V> mapFromCollection(Collection<V> collection, Function<V, K> keyGetter) {
        Map<K, V> result = new HashMap<>();
        collection.forEach((v) -> result.put(keyGetter.apply(v), v));
        return result;
    }

    public static <T, S> List<T> addAllToList(List<T> target, Collection<S> source, Function<S, T> valueGetter) {
        addAllToCollection(target, source, valueGetter);
        return target;
    }

    public static <T, S> Set<T> addAllToSet(Set<T> target, Collection<S> source, Function<S, T> valueGetter) {
        addAllToCollection(target, source, valueGetter);
        return target;
    }

    public static <T, S> Collection<T> addAllToCollection(Collection<T> target, Collection<S> source, Function<S, T> valueGetter) {
        source.forEach((s) -> {
            target.add(valueGetter.apply(s));
        });
        return target;
    }

    public static <K, T, S> Map<K, T> addAllToMap(Map<K, T> target, Collection<S> source, Function<S, K> keyGetter, Function<S, T> valueGetter) {
        source.forEach((s) -> {
            target.put(keyGetter.apply(s), valueGetter.apply(s));
        });
        return target;
    }

    /**
     * 根据值返回第一个匹配的键，一般用在值也是唯一的map中
     *
     * @param <K> 键类型
     * @param <T> 值类型
     * @param target 待查找的map
     * @param value 匹配对象
     * @return
     */
    public static <K, T> K findFirstKeyByValue(Map<K, T> target, T value) {
        for (Map.Entry<K, T> entrySet : target.entrySet()) {
            if (Objects.equals(entrySet.getValue(), value)) {
                return entrySet.getKey();
            }
        }
        return null;
    }

    public static <T> ArrayList<T> toArrayList(Collection<T> collection) {
        if (collection instanceof ArrayList) {
            return (ArrayList<T>) collection;
        } else {
            return new ArrayList<>(collection);
        }
    }

    public static <T> LinkedList<T> toLinkedList(Collection<T> collection) {
        if (collection instanceof LinkedList) {
            return (LinkedList<T>) collection;
        } else {
            return new LinkedList<>(collection);
        }
    }

}
