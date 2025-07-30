/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import com.cxj.utility.Exceptions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author Administrator
 */
public class AttributeMeta<X, Y> {

    private final Attribute<X, Y> ab;
    private final Annotation[] annotations;

    public AttributeMeta(Attribute<X, Y> ab) {
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

    public Attribute<X, Y> unwrap() {
        return ab;
    }

    public boolean isRelation() {
        return isOneToOne() || isOneToMany() || isManyToMany() || isManyToOne();
    }

    public boolean isOneToOne() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE;
    }

    public boolean isOneToMany() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY;
    }

    public boolean isManyToMany() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY;
    }

    public boolean isManyToOne() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE;
    }

    public boolean isEmbedded() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED;
    }

    public boolean isBasic() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC;
    }

    public boolean isElementCollection() {
        return unwrap().getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
    }

    public Class getRelationTargetType() {
        Attribute<X, Y> attribute = unwrap();
        if (attribute instanceof SingularAttribute) {
            return ((SingularAttribute) unwrap()).getBindableJavaType();
        }
        if (attribute instanceof PluralAttribute) {
            return ((PluralAttribute) unwrap()).getBindableJavaType();
        }
        return null;
    }

    public PluralAttribute.CollectionType getCollectionType() {
        Attribute attribute = unwrap();
        if (attribute instanceof PluralAttribute) {
            return ((PluralAttribute) attribute).getCollectionType();
        }
        return null;
    }

    public void set(Object entity, Object value) {
        Member member = this.ab.getJavaMember();
        if (member instanceof Field) {
            if (PropertyUtils.isWriteable(entity, ab.getName())) {
                try {
                    PropertyUtils.setProperty(entity, ab.getName(), value);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Exceptions.forceThrow(ex);
                }
            } else {
                Field field = (Field) member;
                java.security.AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        return null;
                    }
                });
                try {
                    field.set(entity, value);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Exceptions.forceThrow(ex);
                }
            }
        } else if (member instanceof Method) {
            Method method = (Method) member;
            try {
                method.invoke(entity, value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            throw new RuntimeException("This is impossible!");
        }
    }

    public Y get(Object entity) {
        Member member = this.ab.getJavaMember();
        if (member instanceof Field) {
            if (PropertyUtils.isReadable(entity, ab.getName())) {
                try {
                    return (Y) PropertyUtils.getProperty(entity, ab.getName());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    Exceptions.forceThrow(ex);
                    return null;
                }
            } else {
                Field field = (Field) member;
                java.security.AccessController.doPrivileged(new PrivilegedAction<Void>() {

                    @Override
                    public Void run() {
                        field.setAccessible(true);
                        return null;
                    }
                });
                try {
                    return (Y) field.get(entity);
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    Exceptions.forceThrow(ex);
                    return null;
                }
            }
        } else if (member instanceof Method) {
            Method method = (Method) member;
            try {
                return (Y) method.invoke(entity);
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
