/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import com.cxj.error.Assert;
import com.cxj.utility.ClassHelper;
import com.cxj.utility.CollectionHelper;
import com.cxj.utility.NumberHelper;
import com.cxj.utility.NumberType;
import com.cxj.utility.QualifiedNameHelper;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author 陈晓靖
 */
public class ReflactHelper {

    static final Logger LOGGER = LogManager.getLogger(ReflactHelper.class);
    public static final Class WILDCARD_TYPE = null;
    
    public static boolean equalType(Class a, Class b) {
        return canAssignTo(a, b) && canAssignTo(b, a);
    }

    /**
     * 测试类a是否能赋值给类b，这里假设只要两边都是数字类型就一定能赋值
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean canAssignTo(Class a, Class b) {
        if (a == WILDCARD_TYPE || b == WILDCARD_TYPE) {
            return true;
        }
        if (Objects.equals(a, b)) {
            return true;
        }
        a = ClassHelper.standardClass(a);
        b = ClassHelper.standardClass(b);
        if (Objects.equals(a, b)) {
            return true;
        }
        if (Number.class.isAssignableFrom(a) && Number.class.isAssignableFrom(b)) {
            return true;
        } else {
            return b.isAssignableFrom(a);
        }
    }

    public static <T> T asType(Object obj, Class<T> type) {
        if (obj == null) {
            return (T) null;
        }
        Class a = ClassHelper.standardClass(obj.getClass());
        Class b = ClassHelper.standardClass(type);
        if (b.isAssignableFrom(a)) {
            return (T) obj;
        } else if (Number.class.isAssignableFrom(a) && Number.class.isAssignableFrom(b)) {
            NumberType nType = NumberHelper.getType(b);
            return (T) NumberHelper.toType((Number) obj, nType);
        } else {
            throw new IllegalArgumentException(obj + " can not be cast to type " + type);
        }
    }

    public static Class commonParentClass(Class a, Class b) {
        if (a.isAssignableFrom(b)) {
            return a;
        } else if (b.isAssignableFrom(a)) {
            return b;
        } else {
            Class superclass = a.getSuperclass();
            if (superclass == null) {
                return null;
            } else {
                return commonParentClass(superclass, b);
            }
        }
    }

    /**
     * 返回所有属性，包括继承的
     *
     * @param clazz
     * @return
     */
    public static List<Field> getFields(Class clazz) {
        return getFields(clazz, Object.class);
    }

    /**
     * 返回到root为止所有继承属性。
     *
     * @param clazz
     * @param root 祖先类
     * @return
     */
    public static List<Field> getFields(Class clazz, Class root) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class parent = clazz.getSuperclass();
        if (!clazz.equals(root) && !clazz.equals(Object.class) && parent != null) {
            fields.addAll(getFields(parent, root));
        }
        return fields;
    }

    /**
     * 返回所有属性名，包括继承的
     *
     * @param clazz
     * @return
     */
    public static List<String> getFieldNames(Class clazz) {
        return getFieldNames(clazz, Object.class);
    }

    /**
     * 返回到root为止所有继承属性名。
     *
     * @param clazz
     * @param root 祖先类
     * @return
     */
    public static List<String> getFieldNames(Class clazz, Class root) {
        return (List<String>) CollectionHelper.transCollection(getFields(clazz, root), new ArrayList<>(), v -> v.getName());
    }

    public static Class getFieldType(Class clazz, String property) throws NoSuchFieldException {
        return getFieldType(clazz, Object.class, property);
    }

    public static Class _getFieldType(Class clazz, Class root, String attribute) throws NoSuchFieldException {
        Field field;
        try {
            return clazz.getDeclaredField(attribute).getType();
        } catch (NoSuchFieldException e) {
            Class parent = clazz.getSuperclass();
            if (!clazz.equals(root) && !clazz.equals(Object.class) && parent != null) {
                return _getFieldType(parent, root, attribute);
            } else {
                throw e;
            }
        }
    }

    public static Class getFieldType(Class clazz, Class root, String property) throws NoSuchFieldException {
        String attribute = QualifiedNameHelper.getRootPath(property);
        String left = QualifiedNameHelper.getLeftPath(property, 1);
        if (left.isEmpty()) {
            return _getFieldType(clazz, root, attribute);
        } else {
            Class cls = _getFieldType(clazz, root, attribute);
            return _getFieldType(cls, Object.class, left);
        }
    }

    /**
     * 返回指定属性，若声明属性中没找到，会尝试找继承属性。若找不到，则返回空
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getFieldNT(Class clazz, String name) {
        return getFieldNT(clazz, Object.class, name);
    }

    /**
     * 返回指定属性，若声明属性中没找到，会尝试寻找最高到root类的继承属性。若找不到，则返回空
     *
     * @param clazz
     * @param root 祖先类
     * @param name
     * @return
     */
    public static Field getFieldNT(Class clazz, Class root, String name) {
        Field field;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            Class parent = clazz.getSuperclass();
            if (!clazz.equals(root) && !clazz.equals(Object.class) && parent != null) {
                return getFieldNT(parent, root, name);
            } else {
                return null;
            }
        }
        return field;
    }

    /**
     * 得到特定名称的属性值，若该属性不存在，则抛NoSuchFieldException异常
     *
     * @param <T> 对象类型
     * @param obj 对象
     * @param name 属性名
     * @return
     * @throws java.lang.NoSuchFieldException
     */
    public static <T> Object getField(T obj, String name) throws NoSuchFieldException {
        Field field = ReflactHelper.getFieldNT(obj.getClass(), name);
        if (field == null) {
            throw new NoSuchFieldException();
        }
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * 得到特定名称和类型的属性值，若该属性不存在，则抛NoSuchFieldException异常。
     * 若指定属性的类型与传入类型不符，抛IllegalArgumentException异常。
     *
     * @param <T> 对象类型
     * @param <R> 属性类型
     * @param obj 对象
     * @param name 属性名
     * @param cls 属性类型
     * @return
     * @throws java.lang.NoSuchFieldException
     */
    public static <T, R> R getField(T obj, String name, Class<R> cls) throws NoSuchFieldException {
        Field field = ReflactHelper.getFieldNT(obj.getClass(), name);
        if (field == null) {
            throw new NoSuchFieldException();
        } else if (!cls.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException();
        }
        try {
            field.setAccessible(true);
            return (R) field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            //这里理论上是不可能触发的
            throw new IllegalArgumentException();
        }
    }

    /**
     * 得到特定名称的属性值，若该属性不存在，则返回null。
     * 注意若此属性值本身也是null，则同样返回null，所以不能根据此来得出该属性不存在的结论。
     *
     * @param <T> 对象类型
     * @param obj 对象
     * @param name 属性名
     * @return
     */
    public static <T> Object getFieldNT(T obj, String name) {
        Field field = ReflactHelper.getFieldNT(obj.getClass(), name);
        if (field == null) {
            return null;
        }
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    /**
     * 得到特定名称和类型的属性值，若该属性不存在，则返回null。
     * 注意若此属性值本身也是null，则同样返回null，所以不能根据此来得出该属性不存在的结论。
     * 另外若指定属性的类型与传入类型不符，抛IllegalArgumentException异常
     *
     * @param <T> 对象类型
     * @param <R> 属性类型
     * @param obj 对象
     * @param name 属性名
     * @param cls 属性类型
     * @return
     */
    public static <T, R> R getFieldNT(T obj, String name, Class<R> cls) {
        Field field = ReflactHelper.getFieldNT(obj.getClass(), name);
        if (field == null) {
            return null;
        } else if (!cls.isAssignableFrom(field.getType())) {
            throw new IllegalArgumentException();
        }
        try {
            field.setAccessible(true);
            return (R) field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            //这里理论上是不可能触发的
            throw new IllegalArgumentException();
        }
    }

    public static Field getFieldRNT(Class clazz, Class root, String path) {
        String attribute = QualifiedNameHelper.getRootPath(path);
        String left = QualifiedNameHelper.getLeftPath(path, 1);
        if (left.isEmpty()) {
            return getFieldNT(clazz, root, attribute);
        } else {
            Field f = getFieldNT(clazz, root, attribute);
            return f == null ? null : ReflactHelper.getFieldRNT(f.getType(), root, left);
        }
    }

    public static Field getFieldRNT(Class clazz, String path) {
        String attribute = QualifiedNameHelper.getRootPath(path);
        String left = QualifiedNameHelper.getLeftPath(path, 1);
        if (left.isEmpty()) {
            return ReflactHelper.getFieldNT(clazz, attribute);
        } else {
            Field f = ReflactHelper.getFieldNT(clazz, attribute);
            return f == null ? null : ReflactHelper.getFieldRNT(f.getType(), left);
        }
    }

    /**
     * 根据递归属性路径得到属性值，递归属性路径不存在则抛NoSuchFieldException异常。
     * 若递归属性路径上某个非叶结点的属性为空，则抛NullPointerException异常。<br>
     * 递归属性路径指属性的属性。以"."分开。比如"manger.name.firstName"指对象的manager属性的name属性的firstName属性。
     * 若对象的manger或manger.name属性为空，则抛NullPointerException异常。
     *
     * @param <T>
     * @param obj
     * @param path
     * @return
     * @throws NoSuchFieldException
     */
    public static <T> Object getFieldR(T obj, String path) throws NoSuchFieldException {
        Assert.notNull(obj);
        String attribute = QualifiedNameHelper.getRootPath(path);
        String left = QualifiedNameHelper.getLeftPath(path, 1);
        if (left.isEmpty()) {
            return getField(obj, attribute);
        } else {
            Object subObj = getField(obj, attribute);
            if (subObj == null) {
                throw new NullPointerException("Some attribute value on the path is null -> " + attribute + ".");
            } else {
                return ReflactHelper.getFieldR(subObj, left);
            }
        }
    }

    /**
     * 根据递归属性路径得到特定类型的属性值，递归属性路径不存在则抛NoSuchFieldException异常。
     * 若属性类型和给定类型不符，则抛ClassCastException异常。
     * 若递归属性路径上某个非叶结点的属性为空，则抛NullPointerException异常。<br>
     * 递归属性路径指属性的属性。以"."分开。比如"manger.name.firstName"指对象的manager属性的name属性的firstName属性。
     * 若对象的manger或manger.name属性为空，则抛NullPointerException异常。
     *
     * @param <T>
     * @param <R>
     * @param obj
     * @param path
     * @param cls
     * @return
     * @throws NoSuchFieldException
     */
    public static <T, R> R getFieldR(T obj, String path, Class<R> cls) throws NoSuchFieldException {
        return (R) ReflactHelper.getFieldR(obj, path);
    }

    /**
     * {@link #getFieldR(java.lang.Object, java.lang.String) getField(T, String)}的无异常版。
     * 根据递归属性路径得到属性值，递归属性路径不存在则返回null。 若递归属性路径上某个非叶结点的属性为空，则返回null。<br>
     * 递归属性路径指属性的属性。以"."分开。比如"manger.name.firstName"指对象的manager属性的name属性的firstName属性。
     * 若对象的manger或manger.name或manger.name.firstName属性为空，则返回null。
     *
     * @param <T>
     * @param obj
     * @param path
     * @return
     */
    public static <T> Object getFieldRNT(T obj, String path) {
        Assert.notNull(obj);
        String attribute = QualifiedNameHelper.getRootPath(path);
        String left = QualifiedNameHelper.getLeftPath(path, 1);
        if (left.isEmpty()) {
            return getFieldNT(obj, attribute);
        } else {
            Object subObj = getFieldNT(obj, attribute);
            return subObj == null ? null : ReflactHelper.getFieldRNT(subObj, left);
        }
    }

    /**
     * {@link #getFieldR(java.lang.Object, java.lang.String, java.lang.Class) getFieldR(T, String, Class)}的无异常版。
     * 根据递归属性路径得到特定类型的属性值，递归属性路径不存在则则返回null。
     * 若属性类型和给定类型不符，则抛ClassCastException异常。 若递归属性路径上某个非叶结点的属性为空，则返回null。<br>
     * 递归属性路径指属性的属性。以"."分开。比如"manger.name.firstName"指对象的manager属性的name属性的firstName属性。
     * 若对象的manger或manger.name或manger.name.firstName属性为空，则返回null。
     *
     * @param <T>
     * @param <R>
     * @param obj
     * @param path
     * @param cls
     * @return
     */
    public static <T, R> R getFieldRNT(T obj, String path, Class<R> cls) {
        return (R) ReflactHelper.getFieldRNT(obj, path);
    }

    /**
     * 得到多个属性值，若某个属性不存在，则抛NoSuchFieldException异常
     *
     * @param <T> 对象类型
     * @param obj 对象
     * @param names 属性名列表
     * @return
     * @throws NoSuchFieldException
     */
    public static <T> List<Object> getFields(T obj, List<String> names) throws NoSuchFieldException {
        List<Object> result = new ArrayList<>();
        for (String name : names) {
            result.add(getField(obj, name));
        }
        return result;
    }

    /**
     * 得到多个属性值，若某个属性不存在，则抛NoSuchFieldException异常
     *
     * @param <T> 对象类型
     * @param obj 对象
     * @param names 属性名列表
     * @return
     * @throws NoSuchFieldException
     */
    public static <T> List<Object> getFields(T obj, String... names) throws NoSuchFieldException {
        return ReflactHelper.getFields(obj, Arrays.asList(names));
    }

    /**
     * 根据属性名设置属性值，属性不存在则抛NoSuchFieldException异常。
     * 设置值类型与属性类型不匹配则抛IllegalArgumentException异常。
     *
     * @param obj 设置对象
     * @param source 设置值
     * @param name 属性名
     * @throws NoSuchFieldException 属性不存在
     * @throws IllegalArgumentException 设置值类型与属性类型不匹配
     */
    public static void setField(Object obj, Object source, String name) throws NoSuchFieldException, IllegalArgumentException {
        Assert.notNull(obj);
        Field field = ReflactHelper.getFieldNT(obj.getClass(), name);
        if (field == null) {
            throw new NoSuchFieldException(name);
        }
        field.setAccessible(true);
        try {
            field.set(obj, source);
        } catch (IllegalAccessException ex) {
        }
    }

    /**
     * 根据递归属性路径设置属性值，递归属性路径不存在则抛NoSuchFieldException异常。
     * 设置值类型与属性类型不匹配则抛IllegalArgumentException异常。
     * 若递归属性路径上某个非叶结点的属性为空，则抛NullPointerException异常。<br>
     * 递归属性路径指属性的属性。以"."分开。比如"manger.name.firstName"指对象的manager属性的name属性的firstName属性。
     * 若对象的manger或manger.name属性为空，会抛NullPointerException异常。
     *
     * @param obj 设置对象
     * @param source 设置值
     * @param path 递归属性路径
     * @throws NoSuchFieldException 属性不存在
     * @throws IllegalArgumentException 设置值类型与属性类型不匹配
     * @see #setFieldRNT(java.lang.Object, java.lang.Object, java.lang.String)
     * setFieldRNT(...)
     */
    public static void setFieldR(Object obj, Object source, String path) throws NoSuchFieldException, IllegalArgumentException {
        Assert.notNull(obj);
        String attribute = QualifiedNameHelper.getRootPath(path);
        String left = QualifiedNameHelper.getLeftPath(path, 1);
        if (left.isEmpty()) {
            setField(obj, source, attribute);
        } else {
            setFieldR(getField(obj, attribute), source, left);
        }
    }

    /**
     * 根据递归属性路径设置属性值，递归属性路径不存在则抛NoSuchFieldException异常。
     * 设置值类型与属性类型不匹配则抛IllegalArgumentException异常。
     * 若递归属性路径上某个非叶结点的属性为空，则设置属性操作失败，返回<code>false</code>，否则返回<code>true</code>。<br>
     * 递归属性路径指属性的属性。以"."分开。比如"manger.name.firstName"指对象的manager属性的name属性的firstName属性。
     * 若对象obj的manger或manger.name属性为空，则操作失败，返回<code>false</code>，否则将obj.manger.name.firstName的值修改为source，并且返回<code>true</code>。
     *
     * @param obj 设置对象
     * @param source 设置值
     * @param path 递归属性路径
     * @return 是否设置属性成功
     * @throws NoSuchFieldException 属性不存在
     * @throws IllegalArgumentException 设置值类型与属性类型不匹配
     * @see #setFieldR(java.lang.Object, java.lang.Object, java.lang.String)
     * setFieldR(...)
     */
    public static boolean setFieldRNT(Object obj, Object source, String path) throws NoSuchFieldException, IllegalArgumentException {
        Assert.notNull(obj);
        String attribute = QualifiedNameHelper.getRootPath(path);
        String left = QualifiedNameHelper.getLeftPath(path, 1);
        if (left.isEmpty()) {
            setField(obj, source, attribute);
            return true;
        } else {
            Object subObj = getField(obj, attribute);
            return subObj == null ? false : setFieldRNT(subObj, source, left);
        }
    }

    public static boolean addFieldR(Object obj, Object source, String path) throws NoSuchFieldException {
        return funionOnFieldR(obj, path, (Collection c) -> c.add(source));
    }

    public static Object putFieldR(Object obj, Object key, Object source, String path) throws NoSuchFieldException {
        return funionOnFieldR(obj, path, (Map c) -> c.put(key, source));
    }

    public static boolean addAllFieldR(Object obj, Collection source, String path) throws NoSuchFieldException {
        return funionOnFieldR(obj, path, (Collection c) -> c.addAll(source));
    }

    public static void putAllFieldR(Object obj, Map source, String path) throws NoSuchFieldException {
        consumerOnFieldR(obj, path, (Map m) -> m.putAll(source));
    }

    public static boolean removeFieldR(Object obj, Object source, String path) throws NoSuchFieldException {
        return funionOnFieldR(obj, path, (Collection c) -> c.remove(source));
    }

    public static Object removeMapFieldR(Object obj, Object key, String path) throws NoSuchFieldException {
        return funionOnFieldR(obj, path, (Map c) -> c.remove(key));
    }

    public static boolean removeAllFieldR(Object obj, Collection source, String path) throws NoSuchFieldException {
        return funionOnFieldR(obj, path, (Collection c) -> c.removeAll(source));
    }

    public static void removeMapAllFieldR(Object obj, Set keys, String path) throws NoSuchFieldException {
        consumerOnFieldR(obj, path, (Map m) -> {
            keys.stream().forEach(key -> m.remove(key));
        });
    }

    public static <T, R> R funionOnFieldR(Object obj, String path, Function<T, R> function) throws NoSuchFieldException {
        Object target = ReflactHelper.getFieldR(obj, path);
        return function.apply((T) target);
    }

    public static <T, U, R> R funionOnFieldR(Object obj, String path, U arg, BiFunction<T, U, R> function) throws NoSuchFieldException {
        Object target = ReflactHelper.getFieldR(obj, path);
        return function.apply((T) target, arg);
    }

    public static <T> void consumerOnFieldR(Object obj, String path, Consumer<T> function) throws NoSuchFieldException {
        Object target = ReflactHelper.getFieldR(obj, path);
        function.accept((T) target);
    }

    public static <T, U> void consumerOnFieldR(Object obj, String path, U arg, BiConsumer<T, U> function) throws NoSuchFieldException {
        Object target = ReflactHelper.getFieldR(obj, path);
        function.accept((T) target, arg);
    }

    public static <T, R> R funionOnFieldRNT(Object obj, String path, Function<T, R> function) {
        Object target = ReflactHelper.getFieldRNT(obj, path);
        return function.apply((T) target);
    }

    public static <T, U, R> R funionOnFieldRNT(Object obj, String path, U arg, BiFunction<T, U, R> function) {
        Object target = ReflactHelper.getFieldRNT(obj, path);
        return function.apply((T) target, arg);
    }

    public static <T> void consumerOnFieldRNT(Object obj, String path, Consumer<T> function) {
        Object target = ReflactHelper.getFieldRNT(obj, path);
        function.accept((T) target);
    }

    public static <T, U> void consumerOnFieldRNT(Object obj, String path, U arg, BiConsumer<T, U> function) {
        Object target = ReflactHelper.getFieldRNT(obj, path);
        function.accept((T) target, arg);
    }
}
