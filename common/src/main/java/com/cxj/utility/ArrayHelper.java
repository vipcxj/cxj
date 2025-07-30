/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import com.cxj.error.Assert;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Administrator
 */
public class ArrayHelper {

    public static Object add(@Nonnull Object array, Object item) {
        Assert.isTrue(array.getClass().isArray());
        Class cType = array.getClass().getComponentType();
        if (Object.class.isAssignableFrom(cType)) {
            return ArrayUtils.add((Object[]) array, item);
        } else if (boolean.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Boolean.class.equals(item.getClass()));
            return ArrayUtils.add((boolean[]) array, (boolean) item);
        } else if (char.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Character.class.equals(item.getClass()));
            return ArrayUtils.add((char[]) array, (char) item);
        } else if (short.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Short.class.equals(item.getClass()));
            return ArrayUtils.add((short[]) array, (short) item);
        } else if (int.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Integer.class.equals(item.getClass()));
            return ArrayUtils.add((int[]) array, (int) item);
        } else if (long.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Long.class.equals(item.getClass()));
            return ArrayUtils.add((long[]) array, (long) item);
        } else if (float.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Float.class.equals(item.getClass()));
            return ArrayUtils.add((float[]) array, (float) item);
        } else if (double.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Double.class.equals(item.getClass()));
            return ArrayUtils.add((double[]) array, (double) item);
        } else if (byte.class.equals(cType)) {
            Assert.notNull(item);
            Assert.isTrue(Byte.class.equals(item.getClass()));
            return ArrayUtils.add((byte[]) array, (byte) item);
        } else {
            throw new IllegalArgumentException("This is impossible!");
        }
    }
}
