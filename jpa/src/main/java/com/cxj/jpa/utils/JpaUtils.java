/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.utils;

import com.cxj.utility.CollectionHelper;
import com.cxj.utility.QualifiedNameHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

/**
 *
 * @author Administrator
 */
public class JpaUtils {

    public static <T> String getSimpleName(Bindable<T> bindable) {
        switch (bindable.getBindableType()) {
            case ENTITY_TYPE:
                return ((EntityType<T>) bindable).getName();
            case PLURAL_ATTRIBUTE:
            case SINGULAR_ATTRIBUTE:
                return ((Attribute<?, T>) bindable).getName();
            default:
                return null;
        }
    }

    /**
     * 将bindable转为Type<br>
     * 若bindable为实体或单值属性，则返回表示其本身的Type，若为多值属性，则返回表示其元素的Type
     *
     * @param <T>
     * @param bindable
     * @return
     */
    public static <T> Type<T> toType(Bindable<T> bindable) {
        switch (bindable.getBindableType()) {
            case ENTITY_TYPE:
                return (EntityType<T>) bindable;
            case PLURAL_ATTRIBUTE:
                return ((PluralAttribute<?, ?, T>) bindable).getElementType();
            case SINGULAR_ATTRIBUTE:
                return ((SingularAttribute<?, T>) bindable).getType();
            default:
                return null;
        }
    }

    public static Type toType(Attribute attribute) {
        return toType(toBindable(attribute));
    }

    public static boolean isEntity(Bindable bindable) {
        if (bindable.getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
            return true;
        } else if (bindable.getBindableType() == Bindable.BindableType.SINGULAR_ATTRIBUTE) {
            SingularAttribute attribute = (SingularAttribute) bindable;
            return attribute.getType() instanceof EntityType;
        } else if (bindable.getBindableType() == Bindable.BindableType.PLURAL_ATTRIBUTE) {
            PluralAttribute attribute = (PluralAttribute) bindable;
            return attribute.getElementType() instanceof EntityType;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * 返回代表当前路径的父实体路径。比如Employee.Address.lane。
     * 其中Employee是实体，Address是内嵌类，lane是Address的属性。
     * 那么Employee.Address.lane的父实体应为Employee，而非Employee.Address。
     * 特别的对于1级的路径，例如Employee，则返回null。
     * 对于2级的路径，比如Employee.Address，直接返回其父路径Employee。
     *
     * @param model 元模型
     * @param path 路径
     * @return 父实体路径
     */
    public static String parentEntity(Metamodel model, String path) {
        if (QualifiedNameHelper.isOneLayerPath(path)) {
            return null;
        }
        String parent = QualifiedNameHelper.getParentPath(path);
        if (QualifiedNameHelper.isOneLayerPath(parent)) {
            return parent;
        } else {
            Bindable bindable = toBindable(model, parent);
            if (isEntity(bindable)) {
                return parent;
            } else {
                return parentEntity(model, parent);
            }
        }
    }

    public static EntityType parentEntityType(Metamodel model, String path) {
        if (QualifiedNameHelper.isOneLayerPath(path)) {
            return null;
        }
        String parent = QualifiedNameHelper.getParentPath(path);
        if (QualifiedNameHelper.isOneLayerPath(parent)) {
            return entity(model, parent);
        } else {
            Type type = toType(toBindable(model, parent));
            if (type instanceof EntityType) {
                return (EntityType) type;
            } else {
                return parentEntityType(model, parent);
            }
        }
    }

    /**
     * 判断attribute是否只能是路径的叶子，比如当attribute对应的java类型是基本类型时。
     *
     * @param attribute
     * @return
     */
    public static boolean isLeafAttribute(Attribute attribute) {
        return toType(attribute).getPersistenceType() == Type.PersistenceType.BASIC;
    }

    /**
     * 解析Path返回对应元模型。
     *
     * @param model 元模型
     * @param managed ManagedType的类型
     * @param property 属性路径 不为空字符串
     * @return
     */
    private static Bindable<?> parsePath(Metamodel model, Class<?> managed, String property) {
        ManagedType<?> type = model.managedType(managed);
        String root = QualifiedNameHelper.getRootPath(property);
        String subProperty = QualifiedNameHelper.getLeftPath(property, root);
        Attribute<?, ?> attribute = type.getAttribute(root);
        if (!subProperty.isEmpty()) {
            return parsePath(model, toBindable(attribute).getBindableJavaType(), subProperty);
        } else {
            return toBindable(attribute);
        }

    }

    public static Bindable<?> toBindable(Attribute<?, ?> attribute) {
        if (attribute instanceof SingularAttribute || attribute instanceof PluralAttribute) {
            return (Bindable<?>) attribute;
        }
        throw new ClassCastException();
    }

    public static Bindable<?> toBindable(Metamodel model, String path) {
        String root = QualifiedNameHelper.getRootPath(path);
        String property = QualifiedNameHelper.getLeftPath(path, root);
        if (property.isEmpty()) {
            return JpaUtils.entity(model, root);
        } else {
            return parsePath(model, JpaUtils.entity(model, root).getJavaType(), property);
        }
    }

    /**
     * 提取bindable的实体名称。如果bindable是属性，则返回空字符串，若bindable是实体，则返回实体名
     *
     * @param bindable
     * @return
     */
    public static String extractEntity(Bindable bindable) {
        if (bindable.getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
            return ((EntityType) bindable).getName();
        } else {
            return "";
        }
    }

    /**
     * 提取bindable的属性名称。如果bindable是实体，则返回空字符串，若bindable是属性，则返回属性名
     *
     * @param bindable
     * @return
     */
    public static String extractProperty(Bindable bindable) {
        if (bindable.getBindableType() != Bindable.BindableType.ENTITY_TYPE) {
            return ((Attribute) bindable).getName();
        } else {
            return "";
        }
    }

    public static boolean isPluralAttribute(Bindable bindable) {
        return bindable.getBindableType() == Bindable.BindableType.PLURAL_ATTRIBUTE;
    }

    /**
     * 计算路径的实际类型
     *
     * @param model
     * @param path
     * @return
     */
    public static Class<?> toClass(Metamodel model, String path) {
        return toBindable(model, path).getBindableJavaType();
    }

    /**
     * 根据实体名获取实体元模型， 当实体名不存在时抛IllegalArgumentException异常
     *
     * @param model 元模型
     * @param name 实体名
     * @return 实体元模型
     */
    public static EntityType<?> entity(Metamodel model, String name) {
        Set<EntityType<?>> types = model.getEntities();
        for (EntityType<?> type : types) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unrecognized entity name: " + name);
    }

    private static List<String> getAttributeNames(ManagedType type, String prefix) {
        List<String> list = new ArrayList<>();
        Set<Attribute> attributes = type.getAttributes();
        attributes.stream().forEach((attribute) -> {
            Type aType = toType(attribute);
            String aName;
            if (prefix.isEmpty()) {
                aName = attribute.getName();
            } else {
                aName = prefix + "." + attribute.getName();
            }
            list.add(aName);
            if (aType.getPersistenceType() == Type.PersistenceType.EMBEDDABLE) {
                list.addAll(getAttributeNames((ManagedType) aType, aName));
            }
        });
        return list;
    }

    /**
     * 获取某个实体类或内嵌类的所有属性名，若属性是内嵌类，则会继续递归下去
     *
     * @param type
     * @return
     */
    public static List<String> getAttributeNames(ManagedType type) {
        return getAttributeNames(type, "");
    }

    /**
     * 根据输入的实体类型集，得到在集合中不会冲突的属性名（即其在输入实体集中名称是唯一的）及其对应的实体类型
     *
     * @param model 元模型
     * @param classes 实体类型集（可重复）
     * @return
     */
    public static Map<String, Class<?>> uniqueAttribute(Metamodel model, Collection<Class<?>> classes) {
        Map<String, Class<?>> attributeMap = new HashMap<>();
        classes.stream().forEach((cls) -> {
            EntityType entity = model.entity(cls);
            Set<Attribute<?, ?>> attributes = entity.getAttributes();
            attributes.stream().forEach((attribute) -> {
                if (attributeMap.containsKey(attribute.getName())) {
                    attributeMap.replace(attribute.getName(), null);
                } else {
                    attributeMap.put(attribute.getName(), cls);
                }
            });
        });
        CollectionHelper.filterCollection(attributeMap.values(), e -> e != null);
        return attributeMap;
    }

    public static EntityMeta resolveEntityMeta(EntityType et) {
        return new EntityMeta(et);
    }

    public static AttributeMeta resolveAttributeMeta(Attribute at) {
        return new AttributeMeta(at);
    }

    public static class EntityMeta {

        private final EntityType et;
        private final Map<String, AttributeMeta> attributes;

        public EntityMeta(EntityType et) {
            this.et = et;
            this.attributes = new HashMap<>();
            for (Object o : et.getAttributes()) {
                Attribute a = (Attribute) o;
                this.attributes.put(a.getName(), resolveAttributeMeta(a));
            }
        }

        public String getName() {
            return et.getName();
        }

        public EntityType unwrap() {
            return et;
        }

        public Map<String, AttributeMeta> getAttributes() {
            return attributes;
        }

    }

    public static class AttributeMeta {

        private final Attribute ab;
        private final Annotation[] annotations;

        public AttributeMeta(Attribute ab) {
            this.ab = ab;
            Member member = this.ab.getJavaMember();
            if (member instanceof Field) {
                Field field = (Field) member;
                this.annotations = field.getAnnotations();
            } else if (member instanceof Method) {
                Method method = (Method) member;
                this.annotations = method.getAnnotations();
            } else {
                throw new RuntimeException("This is impossible!");
            }
        }

        public String getName() {
            return ab.getName();
        }

        public Attribute unwrap() {
            return ab;
        }

        public Object get(Object entity) {
            Member member = this.ab.getJavaMember();
            if (member instanceof Field) {
                Field field = (Field) member;
                java.security.AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        return null;
                    }
                });
                try {
                    return field.get(entity);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (member instanceof Method) {
                Method method = (Method) member;
                try {
                    return method.invoke(entity);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                throw new RuntimeException("This is impossible!");
            }
        }

        public Annotation[] getAnnotations() {
            return annotations;
        }

    }
}
