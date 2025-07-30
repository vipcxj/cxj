/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.error;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author 陈晓靖
 */
public class Assert {

    private Assert() {
    }

    public static void isNull(Object object) {
        if (object != null) {
            throw new IllegalArgumentException();
        }
    }

    public static void isNull(Object object, String message, Object... args) {
        if (object != null) {
            throw new IllegalArgumentException(String.format(message, args));
        }
    }

    public static void isEmpty(Collection object) {
        if (!object.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public static void isEmpty(Map object) {
        if (!object.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public static void isEmpty(String object) {
        if (!object.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public static void isTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void isTrue(boolean expression, String errorMessage, Object... args) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessage, args));
        }
    }

    public static <T> T notNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T notNull(T reference, String errorMessage, Object... args) {
        if (reference == null) {
            throw new NullPointerException(String.format(errorMessage, args));
        }
        return reference;
    }

    public static <T> void notEmpty(T[] object) {
        if (object.length == 0) {
            throw new IllegalArgumentException("The array mustn't be empty!");
        }
    }

    public static void notEmpty(Collection object) {
        if (object.isEmpty()) {
            throw new IllegalArgumentException("The collection mustn't be empty!");
        }
    }

    public static void notEmpty(Map object) {
        if (object.isEmpty()) {
            throw new IllegalArgumentException("The map mustn't be empty!");
        }
    }

    public static void notEmpty(String object) {
        if (object.isEmpty()) {
            throw new IllegalArgumentException("The string mustn't be empty!");
        }
    }

    public static <T> void notEmpty(T[] object, String errorMessage, Object... args) {
        if (object.length == 0) {
            throw new IllegalArgumentException(String.format(errorMessage, args));
        }
    }

    public static void notEmpty(Collection object, String errorMessage, Object... args) {
        if (object.isEmpty()) {
            throw new IllegalArgumentException(String.format(errorMessage, args));
        }
    }

    public static void notEmpty(Map object, String errorMessage, Object... args) {
        if (object.isEmpty()) {
            throw new IllegalArgumentException(String.format(errorMessage, args));
        }
    }

    public static void notEmpty(String object, String errorMessage, Object... args) {
        if (object.isEmpty()) {
            throw new IllegalArgumentException(String.format(errorMessage, args));
        }
    }

    public static void state(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void state(boolean expression, String errorMessage, Object... args) {
        if (!expression) {
            throw new IllegalStateException(String.format(errorMessage, args));
        }
    }
}
