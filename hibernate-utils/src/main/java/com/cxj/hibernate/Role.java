package com.cxj.hibernate;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by vipcxj on 2018/8/17.
 */
public class Role implements Serializable {
    private String entity;
    private String property;
    private String collectionTable;
    private String[] keyColumns;
    private String[] indexColumns;
    private String[] elementColumns;
    private boolean oneToMany;
    private boolean inverse;

    public Role(@Nonnull String entity, @Nonnull String property, @Nonnull String collectionTable, String[] keyColumns, String[] indexColumns, String[] elementColumns, boolean oneToMany, boolean inverse) {
        this.entity = entity;
        this.property = property;
        this.collectionTable = collectionTable;
        this.keyColumns = keyColumns;
        this.indexColumns = indexColumns;
        this.elementColumns = elementColumns;
        this.oneToMany = oneToMany;
        this.inverse = inverse;
    }

    @Nonnull
    public String getEntity() {
        return entity;
    }

    public void setEntity(@Nonnull String entity) {
        this.entity = entity;
    }

    @Nonnull
    public String getProperty() {
        return property;
    }

    public void setProperty(@Nonnull String property) {
        this.property = property;
    }

    @Nonnull
    public String getCollectionTable() {
        return collectionTable;
    }

    public void setCollectionTable(@Nonnull String collectionTable) {
        this.collectionTable = collectionTable;
    }

    public String[] getElementColumns() {
        return elementColumns;
    }

    public void setElementColumns(String[] elementColumns) {
        this.elementColumns = elementColumns;
    }

    public String[] getKeyColumns() {
        return keyColumns;
    }

    public void setKeyColumns(String[] keyColumns) {
        this.keyColumns = keyColumns;
    }

    public String[] getIndexColumns() {
        return indexColumns;
    }

    public void setIndexColumns(String[] indexColumns) {
        this.indexColumns = indexColumns;
    }

    public boolean isOneToMany() {
        return oneToMany;
    }

    public void setOneToMany(boolean oneToMany) {
        this.oneToMany = oneToMany;
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;

        Role role = (Role) o;

        if (isOneToMany() != role.isOneToMany()) return false;
        if (isInverse() != role.isInverse()) return false;
        if (!getEntity().equals(role.getEntity())) return false;
        if (!getProperty().equals(role.getProperty())) return false;
        if (!getCollectionTable().equals(role.getCollectionTable())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getKeyColumns(), role.getKeyColumns())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getIndexColumns(), role.getIndexColumns())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getElementColumns(), role.getElementColumns());
    }

    @Override
    public int hashCode() {
        int result = getEntity().hashCode();
        result = 31 * result + getProperty().hashCode();
        result = 31 * result + getCollectionTable().hashCode();
        result = 31 * result + Arrays.hashCode(getKeyColumns());
        result = 31 * result + Arrays.hashCode(getIndexColumns());
        result = 31 * result + Arrays.hashCode(getElementColumns());
        result = 31 * result + (isOneToMany() ? 1 : 0);
        result = 31 * result + (isInverse() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return entity + "." + property;
    }
}
