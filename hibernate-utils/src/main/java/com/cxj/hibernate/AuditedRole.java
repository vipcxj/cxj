package com.cxj.hibernate;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * Created by vipcxj on 2018/8/21.
 */
public class AuditedRole extends Role {

    private String auditCollectionTable;
    private String[] auditKeyColumns;
    private String[] auditIndexColumns;
    private String[] auditElementColumns;

    public AuditedRole(
            @Nonnull String entity,
            @Nonnull String property,
            @Nonnull String collectionTable,
            String[] keyColumns, String[] indexColumns, String[] elementColumns,
            boolean oneToMany, boolean inverse,
            @Nonnull String auditCollectionTable,
            String[] auditKeyColumns, String[] auditIndexColumns, String[] auditElementColumns
    ) {
        super(entity, property, collectionTable, keyColumns, indexColumns, elementColumns, oneToMany, inverse);
        this.auditCollectionTable = auditCollectionTable;
        this.auditKeyColumns = auditKeyColumns;
        this.auditIndexColumns = auditIndexColumns;
        this.auditElementColumns = auditElementColumns;
    }

    public AuditedRole(
            @Nonnull Role role,
            @Nonnull String auditCollectionTable,
            String[] auditKeyColumns, String[] auditIndexColumns, String[] auditElementColumns
    ) {
        this(
                role.getEntity(),
                role.getProperty(),
                role.getCollectionTable(),
                role.getKeyColumns(),
                role.getIndexColumns(),
                role.getElementColumns(),
                role.isOneToMany(),
                role.isInverse(),
                auditCollectionTable,
                auditKeyColumns,
                auditIndexColumns,
                auditElementColumns
        );
    }

    public String getAuditCollectionTable() {
        return auditCollectionTable;
    }

    public void setAuditCollectionTable(String auditCollectionTable) {
        this.auditCollectionTable = auditCollectionTable;
    }

    public String[] getAuditKeyColumns() {
        return auditKeyColumns;
    }

    public void setAuditKeyColumns(String[] auditKeyColumns) {
        this.auditKeyColumns = auditKeyColumns;
    }

    public String[] getAuditIndexColumns() {
        return auditIndexColumns;
    }

    public void setAuditIndexColumns(String[] auditIndexColumns) {
        this.auditIndexColumns = auditIndexColumns;
    }

    public String[] getAuditElementColumns() {
        return auditElementColumns;
    }

    public void setAuditElementColumns(String[] auditElementColumns) {
        this.auditElementColumns = auditElementColumns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuditedRole)) return false;
        if (!super.equals(o)) return false;

        AuditedRole role = (AuditedRole) o;

        if (!getAuditCollectionTable().equals(role.getAuditCollectionTable())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getAuditKeyColumns(), role.getAuditKeyColumns())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getAuditIndexColumns(), role.getAuditIndexColumns())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getAuditElementColumns(), role.getAuditElementColumns());
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + getAuditCollectionTable().hashCode();
        result = 31 * result + Arrays.hashCode(getAuditKeyColumns());
        result = 31 * result + Arrays.hashCode(getAuditIndexColumns());
        result = 31 * result + Arrays.hashCode(getAuditElementColumns());
        return result;
    }
}
