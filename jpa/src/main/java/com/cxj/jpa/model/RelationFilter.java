/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.model;

import com.alibaba.fastjson.annotation.JSONType;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Administrator
 */
@JSONType(typeName = "RelationFilter")
public class RelationFilter implements Filter {

    public static final RelationFilter UNDEFINE = new RelationFilter(true);

    private boolean undefine;
    private Filters filters;
    private Set<String> relations;
    private Map<String, Object> relationReferences;
    private Map<String, Filters> relationFilters;

    public RelationFilter() {
    }

    public RelationFilter(boolean undefine, String... relations) {
        this.undefine = undefine;
        if (!undefine) {
            this.filters = new Filters();
            this.relations = new HashSet<>(Arrays.asList(relations));
            this.relationReferences = null;
            this.relationFilters = null;
        } else {
            this.filters = null;
            this.relations = null;
            this.relationReferences = null;
            this.relationFilters = null;
        }
    }

    public RelationFilter(String... relations) {
        this(false, relations);
    }

    public RelationFilter(RelationFilter other) {
        init(other);
    }

    private void init(RelationFilter other) {
        this.undefine = other.undefine;
        if (other.filters != null) {
            this.filters = new Filters(other.filters);
        }
        if (other.relations != null) {
            this.relations = new HashSet<>(other.relations);
        }
        if (other.relationReferences != null) {
            this.relationReferences = new HashMap<>(other.relationReferences);
        }
        if (other.relationFilters != null) {
            this.relationFilters = new HashMap<>();
            for (Map.Entry<String, Filters> entry : other.relationFilters.entrySet()) {
                this.relationFilters.put(entry.getKey(), new Filters(entry.getValue()));
            }
        }
    }

    public static RelationFilter create(String... relations) {
        return new RelationFilter(false, relations);
    }

    public Filters getFilters() {
        if (filters == null) {
            filters = new Filters();
        }
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public Map<String, Object> getRelationReferences() {
        return relationReferences;
    }

    public void setRelationReferences(Map<String, Object> relationReferences) {
        this.relationReferences = relationReferences;
    }

    public Map<String, Filters> getRelationFilters() {
        return relationFilters;
    }

    public void setRelationFilters(Map<String, Filters> relationFilters) {
        this.relationFilters = relationFilters;
    }

    public Set<String> getRelations() {
        return relations != null ? relations : Collections.emptySet();
    }

    public void setRelations(Set<String> relations) {
        this.relations = relations;
    }

    public int relationNum() {
        return relations != null ? relations.size() : 0;
    }

    public Object getReference(String relation) {
        return relationReferences != null ? relationReferences.get(relation) : null;
    }

    public boolean hasReference(String relation) {
        return relationReferences != null ? relationReferences.containsKey(relation) : false;
    }

    public Filters getFilters(String relation) {
        return relationFilters != null ? relationFilters.get(relation) : null;
    }

    public boolean hasFilters(String relation) {
        return relationFilters != null ? relationFilters.containsKey(relation) : false;
    }

    public void addReference(String relation, Object reference) {
        if (relation == null || !relations.contains(relation)) {
            throw new IllegalArgumentException("Add relation first!");
        }
        if (relationReferences == null) {
            relationReferences = new HashMap<>();
        }
        relationReferences.put(relation, reference);
    }

    public void removeReference(String relation) {
        if (relationReferences != null) {
            relationReferences.remove(relation);
        }
    }

    public void addFilters(String relation, Filters filters) {
        if (relation == null || !relations.contains(relation)) {
            throw new IllegalArgumentException("Add relation first!");
        }
        if (relationFilters == null) {
            relationFilters = new HashMap<>();
        }
        relationFilters.put(relation, filters);
    }

    public void removeFilters(String relation) {
        if (relationFilters != null) {
            relationFilters.remove(relation);
        }
    }

    @Override
    public boolean isUndefine() {
        return undefine;
    }

    public void setUndefine(boolean undefine) {
        this.undefine = undefine;
    }

    @Override
    public Filter cloneMe() {
        return new RelationFilter(this);
    }

    private static <X extends Filter> X cloneFilter(X filter) {
        return filter != null ? (X) filter.cloneMe() : null;
    }
    
    public boolean sameRelations(RelationFilter other) {
        if (other == null) {
            return true;
        }
        return Objects.equals(relations, other.relations);
    }

    public void and(RelationFilter other) {
        if (!sameRelations(other)) {
            throw new IllegalArgumentException("Only same relations can be added!");
        }
        if (other == null || other.isUndefine()) {
            return;
        }
        if (isUndefine()) {
            init(other);
        } else {
            this.filters = this.filters != null ? this.filters.and(other.filters) : new Filters(other.filters);
            if (other.relationReferences != null) {
                for (Map.Entry<String, Object> entry : other.relationReferences.entrySet()) {
                    addReference(entry.getKey(), entry.getValue());
                }
            }
            if (other.relationFilters != null) {
                for (Map.Entry<String, Filters> entry : other.relationFilters.entrySet()) {
                    addFilters(entry.getKey(), new Filters(entry.getValue()));
                }
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.undefine ? 1 : 0);
        if (!undefine) {
            hash = 97 * hash + Objects.hashCode(this.filters);
            hash = 97 * hash + Objects.hashCode(this.relations);
            hash = 97 * hash + Objects.hashCode(this.relationReferences);
            hash = 97 * hash + Objects.hashCode(this.relationFilters);
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
        final RelationFilter other = (RelationFilter) obj;
        if (this.undefine != other.undefine) {
            return false;
        }
        if (undefine) {
            return true;
        }
        if (!Objects.equals(this.filters, other.filters)) {
            return false;
        }
        if (!Objects.equals(this.relations, other.relations)) {
            return false;
        }
        if (!Objects.equals(this.relationReferences, other.relationReferences)) {
            return false;
        }
        return Objects.equals(this.relationFilters, other.relationFilters);
    }

}
