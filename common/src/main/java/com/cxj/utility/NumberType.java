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
public enum NumberType {

    BDEC(8), DOUBLE(7), FLOAT(6), BINT(5), LONG(4), INTEGER(3), SHORT(2), BYPE(1);

    private final int level;

    private NumberType(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }
    
    public static NumberType ofLevel(int level) {
        for (NumberType type : NumberType.values()) {
            if (type.level == level) {
                return type;
            }
        }
        throw new IllegalArgumentException("None of number type match this level : " + level);
    }

}
