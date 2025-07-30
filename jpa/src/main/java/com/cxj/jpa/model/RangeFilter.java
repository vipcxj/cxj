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
@JSONType(typeName = "RangeFilter")
public class RangeFilter<T> implements Filter {

    public static final RangeFilter UNDEFINE = new RangeFilter(ValueFilter.UNDEFINE, ValueFilter.UNDEFINE);

    private ValueFilter<T> lowerBound;
    private ValueFilter<T> upperBound;

    public RangeFilter() {
    }

    RangeFilter(ValueFilter<T> lowerBound, ValueFilter<T> upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public static <T> RangeFilter<T> greaterThan(T value) {
        return new RangeFilter(ValueFilter.likeFilter(value), ValueFilter.UNDEFINE);
    }

    public static <T> RangeFilter<T> greaterOrEqual(T value) {
        return new RangeFilter(ValueFilter.accurateFilter(value), ValueFilter.UNDEFINE);
    }

    public static <T> RangeFilter<T> lessThan(T value) {
        return new RangeFilter(ValueFilter.UNDEFINE, ValueFilter.likeFilter(value));
    }

    public static <T> RangeFilter<T> lessOrEqual(T value) {
        return new RangeFilter(ValueFilter.UNDEFINE, ValueFilter.accurateFilter(value));
    }

    public static <T> RangeFilter<T> between(T lower, T upper) {
        return new RangeFilter(ValueFilter.accurateFilter(lower), ValueFilter.likeFilter(upper));
    }

    public ValueFilter<T> getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(ValueFilter<T> lowerBound) {
        this.lowerBound = lowerBound;
    }

    public ValueFilter<T> getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(ValueFilter<T> upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    @JSONField(serialize = false, deserialize = false)
    public boolean isUndefine() {
        return lowerBound.isUndefine() && upperBound.isUndefine();
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isGreaterThan() {
        return !lowerBound.isUndefine() && !lowerBound.isAccurate();
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isGreaterOrEqual() {
        return !lowerBound.isUndefine() && lowerBound.isAccurate();
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isLessThan() {
        return !upperBound.isUndefine() && !upperBound.isAccurate();
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isLessOrEqual() {
        return !upperBound.isUndefine() && upperBound.isAccurate();
    }

    @Override
    public RangeFilter<T> cloneMe() {
        return new RangeFilter<>(lowerBound != null ? lowerBound.cloneMe() : null, upperBound != null ? upperBound.cloneMe() : null);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if (isUndefine()) {
            return 29 * hash;
        }
        hash = 29 * hash + Objects.hashCode(this.lowerBound);
        hash = 29 * hash + Objects.hashCode(this.upperBound);
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
        final RangeFilter<?> other = (RangeFilter<?>) obj;
        if (isUndefine() && other.isUndefine()) {
            return true;
        }
        if (!Objects.equals(this.lowerBound, other.lowerBound)) {
            return false;
        }
        return Objects.equals(this.upperBound, other.upperBound);
    }

}
