/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.Objects;

/**
 *
 * @author Administrator
 */
@JSONType(typeName = "ValueFilter")
public class ValueFilter<T> implements Filter {

    public static final ValueFilter UNDEFINE = new ValueFilter(true, true, null);

    private boolean undefine;
    private boolean accurate;
    private T value;

    public ValueFilter() {
    }

    public ValueFilter(T value) {
        this(false, true, value);
    }

    public ValueFilter(boolean undefine, boolean accurate, T value) {
        this.undefine = undefine;
        this.accurate = accurate;
        this.value = value;
    }

    public static <T> ValueFilter<T> accurateFilter(T value) {
        return new ValueFilter(false, true, value);
    }

    public static <T> ValueFilter<T> likeFilter(T value) {
        return new ValueFilter(false, false, value);
    }

    public static <T> ValueFilter<T> undefine() {
        return new ValueFilter<>(false, true, null);
    }

    @Override
    public boolean isUndefine() {
        return undefine;
    }

    public void setUndefine(boolean undefine) {
        this.undefine = undefine;
    }

    public boolean isAccurate() {
        return accurate;
    }

    public void setAccurate(boolean accurate) {
        this.accurate = accurate;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @JSONField(serialize = false, deserialize = false)
    public T getAccurateValue() {
        return (!undefine && accurate) ? value : null;
    }

    public void setAccurateValue(T value) {
        if (value != null) {
            accurate = true;
            setValue(value);
        } else {
            undefine = true;
            setValue(null);
        }
    }

    @JSONField(serialize = false, deserialize = false)
    public T getFuzzyValue() {
        return (!undefine || accurate) ? null : value;
    }

    public void setFuzzyValue(T value) {
        if (value != null) {
            accurate = false;
            setValue(value);
        } else {
            undefine = true;
            setValue(null);
        }
    }

    @Override
    public ValueFilter<T> cloneMe() {
        return new ValueFilter<>(undefine, accurate, value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.undefine ? 1 : 0);
        if (!undefine) {
            hash = 17 * hash + (this.accurate ? 1 : 0);
            hash = 17 * hash + Objects.hashCode(this.value);
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValueFilter other = (ValueFilter) obj;
        if (this.undefine != other.undefine) {
            return false;
        }
        if (this.undefine) {
            return true;
        }
        if (this.accurate != other.accurate) {
            return false;
        }
        return Objects.deepEquals(this.value, other.value);
    }
}
