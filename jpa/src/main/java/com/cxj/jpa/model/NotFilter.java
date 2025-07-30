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
@JSONType(typeName = "NotFilter")
public class NotFilter implements Filter {

    public static final NotFilter UNDEFINE = new NotFilter();

    private Filter filter;

    public NotFilter() {
    }

    public NotFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public Filter calcuate() {
        if (isUndefine()) {
            return NotFilter.UNDEFINE;
        }
        if (filter instanceof ValueFilter) {
            return new NotValueFilter<>((ValueFilter) filter);
        } else if (filter instanceof RangeFilter) {
            RangeFilter rangeFilter = (RangeFilter) filter;
            ValueFilter upper = rangeFilter.getUpperBound();
            ValueFilter lower = rangeFilter.getLowerBound();
            ValueFilter newLower = (upper != null && !upper.isUndefine()) ? upper.isAccurate() ? ValueFilter.likeFilter(upper.getValue()) : ValueFilter.accurateFilter(upper.getValue()) : ValueFilter.UNDEFINE;
            ValueFilter newUpper = (lower != null && !lower.isUndefine()) ? lower.isAccurate() ? ValueFilter.likeFilter(lower.getValue()) : ValueFilter.accurateFilter(lower.getValue()) : ValueFilter.UNDEFINE;
            return new RangeFilter(newLower, newUpper);
        } else if (filter instanceof NotValueFilter) {
            NotValueFilter notValueFilter = (NotValueFilter) filter;
            return notValueFilter.isUndefine() ? ValueFilter.UNDEFINE : notValueFilter.getFilter();
        } else if (filter instanceof NotFilter) {
            NotFilter notFilter = (NotFilter) filter;
            return notFilter.isUndefine() ? ValueFilter.UNDEFINE : notFilter.getFilter();
        } else if (filter instanceof RelationFilter) {
            throw new IllegalArgumentException("Not RelationFilter is illegal！");
        } else {
            throw new IllegalArgumentException("Unknow filter type: " + filter.getClass());
        }
    }

    @Override
    public boolean isUndefine() {
        return filter != null ? filter.isUndefine() : true;
    }

    @Override
    public NotFilter cloneMe() {
        return new NotFilter(this.filter != null ? this.filter.cloneMe() : null);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (isUndefine() ? 0 : Objects.hashCode(this.filter));
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
        final NotFilter other = (NotFilter) obj;
        if (isUndefine() && other.isUndefine()) {
            return true;
        }
        return Objects.equals(this.filter, other.filter);
    }

}
