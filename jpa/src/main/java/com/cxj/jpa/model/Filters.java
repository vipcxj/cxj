/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import com.alibaba.fastjson.annotation.JSONType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Administrator
 */
@JSONType(typeName = "Filters")
public class Filters {

    private Map<String, Filter> filterMap;
    private List<RelationFilter> relationFilters;

    public Filters() {
    }
    
    public Filters(Filters other) {
        if (other.filterMap != null) {
            this.filterMap = new HashMap<>(other.filterMap);
        }
        if (other.relationFilters != null) {
            this.relationFilters = new ArrayList<>();
            for (RelationFilter relationFilter : other.relationFilters) {
                this.relationFilters.add(new RelationFilter(relationFilter));
            }
        }
    }

    public Map<String, Filter> getFilterMap() {
        if (filterMap == null) {
            filterMap = new HashMap<>();
        }
        return filterMap;
    }

    public void setFilterMap(Map<String, Filter> filterMap) {
        this.filterMap = filterMap;
    }
    
    public void addFilter(String propertyName, Filter filter) {
        if (filter != null) {
            getFilterMap().put(propertyName, filter);
        }
    }
    
    public void addFilters(Map<String, Filter> filters) {
        if (filters != null) {
            getFilterMap().putAll(filters);
        }
    }

    public List<RelationFilter> getRelationFilters() {
        if (relationFilters == null) {
            relationFilters = new ArrayList<>();
        }
        return relationFilters;
    }

    public void setRelationFilters(List<RelationFilter> relationFilters) {
        this.relationFilters = relationFilters;
    }
    
    public void addRelationFilter(RelationFilter filter) {
        if (filter == null) {
            return;
        }
        Set<String> relations = filter.getRelations();
        for (RelationFilter relationFilter : getRelationFilters()) {
            if (Objects.equals(relations, relationFilter.getRelations())) {
                relationFilter.and(filter);
                return;
            }
        }
        getRelationFilters().add(filter);
    }
    
    public void addRelationFilters(Collection<RelationFilter> filters) {
        if (filters == null) {
            return;
        }
        for (RelationFilter filter : filters) {
            addRelationFilter(filter);
        }
    }
    
    private static <X extends Filter> X cloneFilter(X filter) {
        return filter != null ? (X)filter.cloneMe() : null;
    }
    
    public Filters and(Filters other) {
        Filters filters = new Filters(this);
        if (other.filterMap != null) {
            for (Map.Entry<String, Filter> entry : other.filterMap.entrySet()) {
                filters.filterMap.put(entry.getKey(), cloneFilter(entry.getValue()));
            }
        }
        if (other.relationFilters != null) {
            for (RelationFilter relationFilter : other.relationFilters) {
                addRelationFilter(cloneFilter(relationFilter));
            }
        }
        return filters;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.filterMap);
        hash = 43 * hash + Objects.hashCode(this.relationFilters);
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
        final Filters other = (Filters) obj;
        if (!Objects.deepEquals(this.filterMap, other.filterMap)) {
            return false;
        }
        return Objects.deepEquals(this.relationFilters, other.relationFilters);
    }

}
