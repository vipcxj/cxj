/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.utils;

import java.util.List;

/**
 *
 * @author Administrator
 */
public class CollectionUtils {
    
    public static <T> T removeAt(List<T> list, int idx) {
        return list.remove(idx);
    }
}
