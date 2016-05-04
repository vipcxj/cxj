/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.functional;

import java.util.function.Predicate;

/**
 *
 * @author Administrator
 */
public class FunctionalUtils {

    public static <T> Predicate<T> alwaysTrue() {
        return arg -> true;
    }
    
    public static <T> Predicate<T> alwaysFalse() {
        return arg -> false;
    }
}
