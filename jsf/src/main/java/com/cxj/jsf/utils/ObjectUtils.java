/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class ObjectUtils {
    
    private final static Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    public static boolean isMap(Object toTest) {
        return toTest instanceof Map;
    }

    public static boolean isCollection(Object toTest) {
        return toTest instanceof Collection;
    }

    public static boolean isList(Object toTest) {
        return toTest instanceof List;
    }

    public static boolean isSet(Object toTest) {
        return toTest instanceof Set;
    }

    public static boolean isArray(Object toTest) {
        return toTest != null && toTest.getClass().isArray();
    }
    
    public static Object[] emptyObjectArray() {
        return EMPTY_OBJECT_ARRAY;
    }
}
