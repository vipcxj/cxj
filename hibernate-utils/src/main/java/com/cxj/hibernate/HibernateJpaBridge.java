package com.cxj.hibernate;

import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by vipcxj on 2018/1/26.
 */
public class HibernateJpaBridge {

    private static final List<Method> METHODS_GET_TYPE_NAME = new CopyOnWriteArrayList<>();
    public static String getHibernateEntityName(EntityType entityType) {
        Method getter = null;
        for (Method method : METHODS_GET_TYPE_NAME) {
            if (method.getDeclaringClass().isAssignableFrom(entityType.getClass())) {
                getter = method;
                break;
            }
        }
        if (getter == null) {
            try {
                getter = entityType.getClass().getMethod("getTypeName");
                METHODS_GET_TYPE_NAME.add(getter);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Unable to find method getTypeName in class " + entityType.getClass().getName() + "." );
            }
        }
        //noinspection EjbProhibitedPackageUsageInspection
        try {
            getter.setAccessible(true);
            return (String) getter.invoke(entityType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static EntityType getEntityTypeByHibernateName(Metamodel metamodel, String hibernateEntityName) {
        Set<EntityType<?>> entities = metamodel.getEntities();
        for (EntityType<?> entity : entities) {
            if (Objects.equals(getHibernateEntityName(entity), hibernateEntityName)) {
                return entity;
            }
        }
        throw new IllegalArgumentException("Invalid hibernate entity name: " + hibernateEntityName + ".");
    }
}
