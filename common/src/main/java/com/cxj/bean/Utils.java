/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.bean;

import com.cxj.utility.ClassHelper;
import com.cxj.utility.NumberHelper;
import groovy.lang.GString;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 *
 * @author Administrator
 */
public class Utils {

    public static Set<String> getPropertyNames(Object target) {
        if (target == null) {
            return Collections.EMPTY_SET;
        } else if (target instanceof BeanView) {
            return new HashSet<>(((BeanView) target).getReadablePropertyNames());
        } else if (target.getClass().isArray()) {
            Object[] array = (Object[]) target;
            Set<String> names = new HashSet<>();
            for (int i = 0; i < array.length; i++) {
                String name = "[" + i + "]";
                names.add(name);
            }
            return names;
        } else if (target instanceof Collection) {
            Collection col = (Collection) target;
            Set<String> names = new HashSet<>();
            for (int i = 0; i < col.size(); i++) {
                String name = "[" + i + "]";
                names.add(name);
            }
            return names;
        } else if (target instanceof Map) {
            Map map = (Map) target;
            Set<String> names = new HashSet<>();
            for (Object key : map.keySet()) {
                String name = (String) key;
                names.add(name);
            }
            return names;
        } else {
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(target);
            Set<String> names = new HashSet<>();
            for (PropertyDescriptor descriptor : descriptors) {
                names.add(descriptor.getName());
            }
            return names;
        }
    }

    public static Set<String> getReadablePropertyNames(Object target) {
        if (target == null) {
            return Collections.EMPTY_SET;
        } else if (target instanceof BeanView) {
            return new HashSet<>(((BeanView) target).getReadablePropertyNames());
        } else if (target.getClass().isArray()) {
            Object[] array = (Object[]) target;
            Set<String> names = new HashSet<>();
            for (int i = 0; i < array.length; i++) {
                String name = "[" + i + "]";
                names.add(name);
            }
            return names;
        } else if (target instanceof Collection) {
            Collection col = (Collection) target;
            Set<String> names = new HashSet<>();
            for (int i = 0; i < col.size(); i++) {
                String name = "[" + i + "]";
                names.add(name);
            }
            return names;
        } else if (target instanceof Map) {
            Map map = (Map) target;
            Set<String> names = new HashSet<>();
            for (Object key : map.keySet()) {
                String name = (String) key;
                names.add(name);
            }
            return names;
        } else {
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(target);
            Set<String> names = new HashSet<>();
            for (PropertyDescriptor descriptor : descriptors) {
                if (descriptor.getReadMethod() != null) {
                    names.add(descriptor.getName());
                }
            }
            return names;
        }
    }

    public static Set<String> getWriteablePropertyNames(Object target) {
        if (target == null) {
            return Collections.EMPTY_SET;
        } else if (target instanceof BeanView) {
            return new HashSet<>(((BeanView) target).getReadablePropertyNames());
        } else if (target.getClass().isArray()) {
            Object[] array = (Object[]) target;
            Set<String> names = new HashSet<>();
            for (int i = 0; i < array.length; i++) {
                String name = "[" + i + "]";
                names.add(name);
            }
            return names;
        } else if (target instanceof Collection) {
            Collection col = (Collection) target;
            Set<String> names = new HashSet<>();
            for (int i = 0; i < col.size(); i++) {
                String name = "[" + i + "]";
                names.add(name);
            }
            return names;
        } else if (target instanceof Map) {
            Map map = (Map) target;
            Set<String> names = new HashSet<>();
            for (Object key : map.keySet()) {
                String name = (String) key;
                names.add(name);
            }
            return names;
        } else {
            PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(target);
            Set<String> names = new HashSet<>();
            for (PropertyDescriptor descriptor : descriptors) {
                if (descriptor.getWriteMethod() != null) {
                    names.add(descriptor.getName());
                }
            }
            return names;
        }
    }

    public static Class<?> getPropertyType(Object target, String name) {
        if (target == null) {
            throw new NullPointerException();
        } else if (target instanceof BeanView) {
            return ((BeanView) target).getPropertyType(name);
        } else {
            try {
                PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(target, name);
                Class<?> ret = descriptor.getPropertyType();
                return ret != null ? ret : Object.class;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void copyProperties(Object target, Object from) {
        Set<String> targetNames = getWriteablePropertyNames(target);
        Set<String> fromNames = getReadablePropertyNames(from);
        for (String fromName : fromNames) {
            if (targetNames.contains(fromName)) {
                try {
                    Object property = PropertyUtils.getProperty(from, fromName);
                    if (property != null) {
                        Class targetType = getPropertyType(target, fromName);
                        targetType = ClassHelper.standardClass(targetType);
                        Class propertyType = ClassHelper.standardClass(property.getClass());
                        if (targetType.isAssignableFrom(propertyType)) {
                            PropertyUtils.setProperty(target, fromName, property);
                        } else {
                            if (Number.class.isAssignableFrom(targetType) && Number.class.isAssignableFrom(propertyType)) {
                                PropertyUtils.setProperty(target, fromName, NumberHelper.toType((Number) property, NumberHelper.getType(targetType)));
                            }else if (String.class.isAssignableFrom(targetType) && GString.class.isAssignableFrom(propertyType)) {
                                PropertyUtils.setProperty(target, fromName, property.toString());
                            }else if (Date.class.isAssignableFrom(targetType)) {
                                if (Number.class.isAssignableFrom(propertyType)) {
                                    PropertyUtils.setProperty(target, fromName, Date.from(Instant.ofEpochMilli(((Number)property).longValue())));
                                } else if (String.class.isAssignableFrom(propertyType)) {
                                    try {
                                        PropertyUtils.setProperty(target, fromName, FastDateFormat.getInstance("yyyy/MM/dd").parse((String) property));
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    throw new IllegalArgumentException("Mismatched type! Need type [" + targetType.getName() + "], but achieve type [" + propertyType.getName() + "]");
                                }
                            }else {
                                throw new IllegalArgumentException("Mismatched type! Need type [" + targetType.getName() + "], but achieve type [" + propertyType.getName() + "]");
                            }
                        }
                    } else {
                        PropertyUtils.setProperty(target, fromName, null);
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
