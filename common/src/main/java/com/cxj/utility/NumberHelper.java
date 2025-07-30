/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import com.cxj.error.Assert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class NumberHelper {

    public static NumberType getType(Class<? extends Number> type) {
        if (Double.class.isAssignableFrom(type)) {
            return NumberType.DOUBLE;
        } else if (Long.class.isAssignableFrom(type)) {
            return NumberType.LONG;
        } else if (BigDecimal.class.isAssignableFrom(type)) {
            return NumberType.BDEC;
        } else if (Float.class.isAssignableFrom(type)) {
            return NumberType.FLOAT;
        } else if (BigInteger.class.isAssignableFrom(type)) {
            return NumberType.BINT;
        } else if (Integer.class.isAssignableFrom(type)) {
            return NumberType.INTEGER;
        } else if (Short.class.isAssignableFrom(type)) {
            return NumberType.SHORT;
        } else if (Byte.class.isAssignableFrom(type)) {
            return NumberType.BYPE;
        } else if (Number.class.isAssignableFrom(type)) {
            return NumberType.BDEC;
        } else {
            throw new IllegalArgumentException("Unsupport number type: " + type + "!");
        }
    }

    public static NumberType getType(Number value) {
        Assert.notNull(value, "We can not decide the number type of a null number!");
        if (value instanceof Double) {
            return NumberType.DOUBLE;
        } else if (value instanceof Long) {
            return NumberType.LONG;
        } else if (value instanceof BigDecimal) {
            return NumberType.BDEC;
        } else if (value instanceof Float) {
            return NumberType.FLOAT;
        } else if (value instanceof BigInteger) {
            return NumberType.BINT;
        } else if (value instanceof Integer) {
            return NumberType.INTEGER;
        } else if (value instanceof Short) {
            return NumberType.SHORT;
        } else if (value instanceof Byte) {
            return NumberType.BYPE;
        } else {
            throw new IllegalArgumentException("Unsupport number type: " + value.getClass() + "!");
        }
    }

    public static NumberType getType(List<? extends Number> list) {
        Assert.notEmpty(list, "We can not decide the number type of a empty number list!");
        return NumberType.ofLevel(list.stream().map(e -> NumberHelper.getType(e).level()).reduce(Integer.MIN_VALUE, Integer::max));
    }

    public static Long toInteger(Number value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Integer) {
            return (long) (int) value;
        } else if (value instanceof Short) {
            return (long) (short) value;
        } else if (value instanceof Byte) {
            return (long) (byte) value;
        } else {
            throw new IllegalArgumentException("Unsupport number type: " + value.getClass() + "!");
        }
    }

    public static BigInteger toBigInteger(Number value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        } else {
            return BigInteger.valueOf(toInteger(value));
        }
    }

    public static Double toDecimal(Number value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Float) {
            return (double) (float) value;
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            return toBigInteger(value).doubleValue();
        }
    }

    public static BigDecimal toBigDecimal(Number value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else {
            return BigDecimal.valueOf((toDecimal(value)));
        }
    }

    public static List<Long> toIntegerList(List<Number> numbers) {
        List<Long> list = new ArrayList<>();
        numbers.stream().forEach((number) -> {
            list.add(toInteger(number));
        });
        return list;
    }

    public static List<BigInteger> toBigIntegerList(List<Number> numbers) {
        List<BigInteger> list = new ArrayList<>();
        numbers.stream().forEach((number) -> {
            list.add(toBigInteger(number));
        });
        return list;
    }

    public static List<Double> toDecimalList(List<Number> numbers) {
        List<Double> list = new ArrayList<>();
        numbers.stream().forEach((number) -> {
            list.add(toDecimal(number));
        });
        return list;
    }

    public static List<BigDecimal> toBigDecimalList(List<Number> numbers) {
        List<BigDecimal> list = new ArrayList<>();
        numbers.stream().forEach((number) -> {
            list.add(toBigDecimal(number));
        });
        return list;
    }

    public static Number toType(Number value, NumberType type) {
        if (type.level() <= NumberType.LONG.level()) {
            return toInteger(value);
        } else if (type.level() <= NumberType.BINT.level()) {
            return toBigInteger(value);
        } else if (type.level() <= NumberType.DOUBLE.level()) {
            return toDecimal(value);
        } else if (type.level() <= NumberType.BDEC.level()) {
            return toBigDecimal(value);
        } else {
            throw new IllegalArgumentException("Unsupport number type: " + type + "!");
        }
    }

    public static Number add(Number left, Number right) {
        NumberType tl = NumberHelper.getType(left);
        NumberType tr = NumberHelper.getType(right);
        NumberType targetType = tl.level() > tr.level() ? tl : tr;
        if (tl != targetType) {
            left = toType(left, targetType);
        }
        if (tr != targetType) {
            right = toType(right, targetType);
        }
        switch (targetType) {
            case DOUBLE:
                return (double) left + (double) right;
            case LONG:
                return (long) left + (long) right;
            case FLOAT:
                return (float) left + (float) right;
            case INTEGER:
                return (int) left + (int) right;
            case SHORT:
                return (short) left + (short) right;
            case BYPE:
                return (byte) left + (byte) right;
            case BDEC:
                return ((BigDecimal) left).add((BigDecimal) right);
            case BINT:
                return ((BigInteger) left).add((BigInteger) right);
            default:
                throw new IllegalArgumentException("Unsupport number type: " + targetType + "!");
        }
    }

    public static Number subtract(Number left, Number right) {
        NumberType tl = NumberHelper.getType(left);
        NumberType tr = NumberHelper.getType(right);
        NumberType targetType = tl.level() > tr.level() ? tl : tr;
        if (tl != targetType) {
            left = toType(left, targetType);
        }
        if (tr != targetType) {
            right = toType(right, targetType);
        }
        switch (targetType) {
            case DOUBLE:
                return (double) left - (double) right;
            case LONG:
                return (long) left - (long) right;
            case FLOAT:
                return (float) left - (float) right;
            case INTEGER:
                return (int) left - (int) right;
            case SHORT:
                return (short) left - (short) right;
            case BYPE:
                return (byte) left - (byte) right;
            case BDEC:
                return ((BigDecimal) left).subtract((BigDecimal) right);
            case BINT:
                return ((BigInteger) left).subtract((BigInteger) right);
            default:
                throw new IllegalArgumentException("Unsupport number type: " + targetType + "!");
        }
    }

    public static Number multiply(Number left, Number right) {
        NumberType tl = NumberHelper.getType(left);
        NumberType tr = NumberHelper.getType(right);
        NumberType targetType = tl.level() > tr.level() ? tl : tr;
        if (tl != targetType) {
            left = toType(left, targetType);
        }
        if (tr != targetType) {
            right = toType(right, targetType);
        }
        switch (targetType) {
            case DOUBLE:
                return (double) left * (double) right;
            case LONG:
                return (long) left * (long) right;
            case FLOAT:
                return (float) left * (float) right;
            case INTEGER:
                return (int) left * (int) right;
            case SHORT:
                return (short) left * (short) right;
            case BYPE:
                return (byte) left * (byte) right;
            case BDEC:
                return ((BigDecimal) left).multiply((BigDecimal) right);
            case BINT:
                return ((BigInteger) left).multiply((BigInteger) right);
            default:
                throw new IllegalArgumentException("Unsupport number type: " + targetType + "!");
        }
    }

    public static Number divide(Number left, Number right) {
        NumberType tl = NumberHelper.getType(left);
        NumberType tr = NumberHelper.getType(right);
        NumberType targetType = tl.level() > tr.level() ? tl : tr;
        if (tl != targetType) {
            left = toType(left, targetType);
        }
        if (tr != targetType) {
            right = toType(right, targetType);
        }
        switch (targetType) {
            case DOUBLE:
                return (double) left / (double) right;
            case LONG:
                return (long) left / (long) right;
            case FLOAT:
                return (float) left / (float) right;
            case INTEGER:
                return (int) left / (int) right;
            case SHORT:
                return (short) left / (short) right;
            case BYPE:
                return (byte) left / (byte) right;
            case BDEC:
                return ((BigDecimal) left).divide((BigDecimal) right);
            case BINT:
                return ((BigInteger) left).divide((BigInteger) right);
            default:
                throw new IllegalArgumentException("Unsupport number type: " + targetType + "!");
        }
    }

    public static Number negative(Number num) {
        NumberType type = NumberHelper.getType(num);
        switch (type) {
            case DOUBLE:
                return -(double) num;
            case LONG:
                return -(long) num;
            case FLOAT:
                return -(float) num;
            case INTEGER:
                return -(int) num;
            case SHORT:
                return -(short) num;
            case BYPE:
                return -(byte) num;
            case BDEC:
                return ((BigDecimal) num).negate();
            case BINT:
                return ((BigInteger) num).negate();
            default:
                throw new IllegalArgumentException("Unsupport number type: " + type + "!");
        }
    }
}
