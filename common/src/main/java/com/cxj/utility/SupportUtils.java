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
public class SupportUtils {

    private static boolean GROOVY_SUPPORT;

    static {
        try {
            Class.forName("groovy.lang.GString");
            GROOVY_SUPPORT = true;
        } catch (Throwable e) {
            GROOVY_SUPPORT = false;
        }
    }
    
    public static boolean isSupportGroovy() {
        return GROOVY_SUPPORT;
    }
}
