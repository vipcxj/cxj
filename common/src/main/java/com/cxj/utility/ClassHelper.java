/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

/**
 *
 * @author Administrator
 */
public class ClassHelper {
    
    /**
     * 若cls是基本类型，则返回其包装类，否则返回本身
     * @param cls
     * @return 
     */
    public static Class standardClass(Class cls) {
        if (cls.isPrimitive()) {
            if (boolean.class.equals(cls)) {
                return Boolean.class;
            } else if (char.class.equals(cls)) {
                return Character.class;
            } else if (byte.class.equals(cls)) {
                return Byte.class;
            } else if (short.class.equals(cls)) {
                return Short.class;
            } else if (int.class.equals(cls)) {
                return Integer.class;
            } else if (long.class.equals(cls)) {
                return Long.class;
            } else if (float.class.equals(cls)) {
                return Float.class;
            } else {
                return Double.class;
            }
        } else {
            return cls;
        }
    }
}
