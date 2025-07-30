/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.nio.charset.Charset;

/**
 *
 * @author Administrator
 */
public enum Charsets {

    UTF_8("UTF-8");

    private final Charset value;

    Charsets(String name) {
        value = Charset.forName(name);
    }

    public Charset getValue() {
        return value;
    }

    public Charset get() {
        return getValue();
    }
}
