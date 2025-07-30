package com.cxj.hibernate;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vipcxj on 2018/8/9.
 */
public class Utils {

    public static final String HIBERNATE_VERSION = org.hibernate.Version.getVersionString();
    public static int HIBERNATE_MAJOR_VERSION = Integer.parseInt(HIBERNATE_VERSION.substring(0, HIBERNATE_VERSION.indexOf('.')));
    private static String HIBERNATE_MAJOR_VERSION_LEFT = HIBERNATE_VERSION.substring(HIBERNATE_VERSION.indexOf('.') + 1);
    public static int HIBERNATE_MINOR_VERSION = Integer.parseInt(HIBERNATE_MAJOR_VERSION_LEFT.substring(0, HIBERNATE_MAJOR_VERSION_LEFT.indexOf('.')));
    private static String HIBERNATE_MINOR_VERSION_LEFT = HIBERNATE_MAJOR_VERSION_LEFT.substring(HIBERNATE_MAJOR_VERSION_LEFT.indexOf('.') + 1);
    public static int HIBERNATE_INCREMENTAL_VERSION = Integer.parseInt(HIBERNATE_MINOR_VERSION_LEFT.substring(0, HIBERNATE_MINOR_VERSION_LEFT.indexOf('.')));
    public static String HIBERNATE_VERSION_QUALIFIER = HIBERNATE_VERSION.substring(HIBERNATE_VERSION.lastIndexOf('.') + 1);

    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String uncapitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toLowerCase() + value.substring(1);
    }

    public static String toCamelCase(String value) {
        return uncapitalize(Arrays.stream(value.split("[\\s_\\-]+")).map(Utils::capitalize).collect(Collectors.joining()));
    }

    public static String addWithMakeUnconflict(List<String> list, String toAdd) {
        String newToAdd = toAdd;
        int i = 1;
        while (list.contains(newToAdd)) {
            newToAdd = toAdd + i++;
        }
        list.add(newToAdd);
        return newToAdd;
    }

    public static List<String> makeUnconflict(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String el : list) {
            addWithMakeUnconflict(newList, el);
        }
        return newList;
    }

    public static String getRootProperty(String property) {
        if (property == null || property.isEmpty()) {
            return property;
        }
        int index = property.indexOf('.');
        if (index == -1) {
            return property;
        } else {
            return property.substring(0, index);
        }
    }

    public static String getLeftProperty(@Nonnull String property, @Nonnull String rightProperty) {
        if (property.equals(rightProperty)) {
            return "";
        }
        return property.substring(rightProperty.length() + 1);
    }

    public static int getPropertyLevel(@Nonnull String property) {
        return (int) property.codePoints().filter(ch -> ch == '.').count();
    }

    public static <T> T getFieldValue(@Nonnull Object value, String property, Class<T> type) {
        Class<?> valueType = value.getClass();
        try {
            Field field = valueType.getField(property);
            field.setAccessible(true);
            //noinspection unchecked
            return (T) field.get(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

}
