/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import com.alibaba.fastjson.annotation.JSONType;
import java.util.Objects;

/**
 *
 * @author Administrator
 */
@JSONType(typeName = "NotValueFilter")
public class NotValueFilter<T> implements Filter {

    public static final NotValueFilter UNDEFINE = new NotValueFilter();
    private ValueFilter<T> filter;

    public NotValueFilter() {
    }

    public NotValueFilter(ValueFilter<T> filter) {
        this.filter = filter;
    }

    public ValueFilter<T> getFilter() {
        return filter;
    }

    public void setFilter(ValueFilter<T> filter) {
        this.filter = filter;
    }

    @Override
    public boolean isUndefine() {
        return filter != null ? filter.isUndefine() : true;
    }

    @Override
    public NotValueFilter<T> cloneMe() {
        return new NotValueFilter(this.filter != null ? this.filter.cloneMe() : null);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(this.filter);
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
        final NotValueFilter<?> other = (NotValueFilter<?>) obj;
        return Objects.equals(this.filter, other.filter);
    }
}
