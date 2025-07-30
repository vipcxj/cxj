package com.cxj.hibernate;

import com.cxj.hibernate.spi.MetaIntegrator;
import org.hibernate.Session;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.configuration.internal.ClassesAuditingData;
import org.hibernate.envers.configuration.internal.metadata.reader.ClassAuditingData;
import org.hibernate.envers.configuration.internal.metadata.reader.PropertyAuditingData;
import org.hibernate.envers.internal.synchronization.AuditProcess;
import org.hibernate.envers.internal.synchronization.work.AddWorkUnit;
import org.hibernate.envers.internal.synchronization.work.AuditWorkUnit;
import org.hibernate.envers.internal.synchronization.work.PersistentCollectionChangeWorkUnit;
import org.hibernate.event.spi.EventSource;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Property;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.JoinColumn;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cxj.hibernate.SqlUtils.*;

/**
 * Created by vipcxj on 2018/1/26.
 */
public class EnversUtils {

    @Nonnull
    private static ClassesAuditingData getClassesAuditingData() {
        ClassesAuditingData classesAuditingData = MetaIntegrator.getClassesAuditingData();
        if (classesAuditingData == null) {
            throw new IllegalStateException("Unable to get the classes auditing data.");
        }
        return classesAuditingData;
    }

    public static boolean isAuditedEntity(String hibernateEntityName) {
        ClassesAuditingData classesAuditingData = getClassesAuditingData();
        ClassAuditingData classAuditingData = classesAuditingData.getClassAuditingData(hibernateEntityName);
        if (classAuditingData == null) {
            return false;
        }
        return classAuditingData.isAudited();
    }

    public static Set<String> getAuditedEntityNames() {
        ClassesAuditingData classesAuditingData = getClassesAuditingData();
        return classesAuditingData.getAllClassAuditedData().stream()
                .filter(el -> el.getValue().isAudited())
                .map(el -> el.getKey().getEntityName())
                .collect(Collectors.toSet());
    }

    public static Set<String> getAuditedPropertyNames(String hibEntityName) {
        return getAuditedPropertyNames(hibEntityName, true);
    }

    public static Set<String> getAuditedPropertyNames(String hibEntityName, boolean withKey) {
        ClassesAuditingData classesAuditingData = getClassesAuditingData();
        ClassAuditingData classAuditingData = classesAuditingData.getClassAuditingData(hibEntityName);
        if (classAuditingData == null || !classAuditingData.isAudited()) {
            return Collections.emptySet();
        }
        Set<String> names = new HashSet<>();
        if (withKey) {
            names.addAll(Arrays.asList(HibernateUtils.getKeyPropertyNames(hibEntityName)));
        }
        for (String name : classAuditingData.getPropertyNames()) {
            names.add(name);
        }
        return names;
    }

    @Nonnull
    private static ClassAuditingData getClassAuditingData(@Nonnull String hibEntityName) {
        ClassesAuditingData classesAuditingData = getClassesAuditingData();
        ClassAuditingData classAuditingData = classesAuditingData.getClassAuditingData(hibEntityName);
        if (classAuditingData == null || !classAuditingData.isAudited()) {
            throw new IllegalStateException("Unable to get the class auditing data of entity: " + hibEntityName + ".");
        }
        return classAuditingData;
    }

    @Nullable
    private static ClassAuditingData tryToGetClassAuditingData(@Nonnull String hibEntityName) {
        ClassesAuditingData classesAuditingData = getClassesAuditingData();
        ClassAuditingData classAuditingData = classesAuditingData.getClassAuditingData(hibEntityName);
        if (classAuditingData == null || !classAuditingData.isAudited()) {
            return null;
        }
        return classAuditingData;
    }

    public static String getAuditTableName(String hibEntityName) {
        getClassAuditingData(hibEntityName);
        String tableName = HibernateUtils.getTableName(hibEntityName);
        EnversService service = MetaIntegrator.getService(EnversService.class);
        return service.getAuditEntitiesConfiguration().getAuditTableName(hibEntityName, tableName);
    }

    public static String getAuditTableNameFromTableName(String tableName) {
        if (isAuditedTable(tableName)) {
            String tableType = HibernateUtils.tableType(tableName);
            switch (tableType) {
                case "entity":
                    String entityName = HibernateUtils.resolveEntityName(tableName);
                    return getAuditTableName(entityName);
                case "collection":
                    List<Role> roles = HibernateUtils.getRolesFromMiddleTable(tableName);
                    AuditedRole role = roles.stream()
                            .map(r -> getRole(r.getEntity(), r.getProperty()))
                            .filter(Objects::nonNull)
                            .findAny()
                            .orElse(null);
                    if (role == null) {
                        throw new IllegalArgumentException("Invalid table: " + tableName + ".");
                    }
                    return role.getAuditCollectionTable();
                case "unknown":
                default:
                    throw new IllegalArgumentException("Invalid table: " + tableName + ".");
            }
        } else {
            throw new IllegalArgumentException("The table \"" + tableName + "\" is not audited.");
        }
    }

    public static String[] getAuditedColumnName(String hibEntityName, String propertyName) {
        return getAuditedColumnName(hibEntityName, propertyName, false);
    }

    public static String[] getAuditedColumnName(String hibEntityName, String propertyName, boolean quoted) {
        ClassAuditingData classAuditingData = getClassAuditingData(hibEntityName);
        PropertyAuditingData propertyAuditingData = classAuditingData.getPropertyAuditingData(propertyName);
        if (propertyAuditingData == null) {
            throw new IllegalArgumentException("Invalid property name: " + propertyName + " of entity: " + hibEntityName + ".");
        }
        return HibernateUtils.getColumnName(hibEntityName, propertyName, quoted);
    }

    public static List<String> getAuditedColumnNames(String hibEntityName) {
        return getAuditedColumnNames(hibEntityName, false, true);
    }

    public static List<String> getAuditedColumnNames(String hibEntityName, boolean quoted) {
        return getAuditedColumnNames(hibEntityName, quoted, true);
    }

    public static List<String> getAuditedColumnNames(String hibEntityName, boolean quoted, boolean withKey) {
        return getAuditedPropertyNames(hibEntityName, withKey).stream()
                .flatMap(p -> Arrays.stream(HibernateUtils.getColumnName(hibEntityName, p, quoted)))
                .collect(Collectors.toList());
    }

    public static Map<String, String> getAuditModifiedColumnNames(String hibEntityName, boolean quoted) {
        return getAuditedPropertyNames(hibEntityName, false).stream()
                .collect(Collectors.toMap(n -> n, n -> getAuditModifiedColumnName(hibEntityName, n, quoted)));
    }

    public static String getAuditModifiedColumnName(String hibEntityName, String propertyName, boolean quoted) {
        ClassAuditingData classAuditingData = getClassAuditingData(hibEntityName);
        propertyName = Utils.getRootProperty(propertyName);
        PropertyAuditingData propertyAuditingData = classAuditingData.getPropertyAuditingData(propertyName);
        if (propertyAuditingData == null) {
            throw new IllegalArgumentException("Invalid property name: " + propertyName + " of entity: " + hibEntityName + ".");
        }
        String modifiedFlagName = propertyAuditingData.getModifiedFlagName();
        Dialect dialect = HibernateUtils.getDialect();
        return quoted ? dialect.openQuote() + modifiedFlagName + dialect.closeQuote() : modifiedFlagName;
    }

    public static String getPropertyNameFromModifiedColumnName(String hibEntityName, String modifiedColumnName) {
        ClassAuditingData classAuditingData = getClassAuditingData(hibEntityName);
        for (String propertyName : classAuditingData.getPropertyNames()) {
            PropertyAuditingData propertyAuditingData = classAuditingData.getPropertyAuditingData(propertyName);
            String modifiedFlagName = propertyAuditingData.getModifiedFlagName();
            if (Objects.equals(modifiedColumnName, modifiedFlagName)) {
                return propertyName;
            }
        }
        throw new IllegalArgumentException("Invalid modified column name: " + modifiedColumnName + ".");
    }

    private static String normalizeQuotedName(String name) {
        if (Dialect.QUOTE.indexOf(name.charAt( 0 )) > -1) {
            return name.substring(1, name.length() - 1);
        } else {
            return name;
        }
    }

    @Nullable
    public static AuditedRole getRole(String hibEntityName, String propertyName) {
        Role role = HibernateUtils.getRole(hibEntityName, propertyName);
        ClassAuditingData classAuditingData = tryToGetClassAuditingData(hibEntityName);
        if (classAuditingData == null) {
            return null;
        }
        String rootPropertyName = Utils.getRootProperty(propertyName);
        PropertyAuditingData auditingData = classAuditingData.getPropertyAuditingData(rootPropertyName);
        if (auditingData == null) {
            return null;
        }
        EnversService service = MetaIntegrator.getService(EnversService.class);
        String childPropertyName = Utils.getLeftProperty(propertyName, rootPropertyName);
        Property[] propertyChain = HibernateUtils.getPropertyChain(hibEntityName, propertyName);
        Property leaf = propertyChain[propertyChain.length - 1];
        if (leaf.getValue() instanceof Collection) {
            String auditMiddleTableName;
            AuditOverride auditOverride = auditingData.getAuditingOverrides().stream().filter(ao -> ao.name().equals(childPropertyName)).findAny().orElse(null);
            AuditJoinTable auditJoinTable = auditOverride != null ? auditOverride.auditJoinTable() : null;
            if (auditJoinTable == null) {
                auditJoinTable = auditingData.getJoinTable();
            }
            if (!auditJoinTable.name().isEmpty()) {
                auditMiddleTableName = normalizeQuotedName(auditJoinTable.name());
            } else {
                auditMiddleTableName = service.getAuditEntitiesConfiguration().getAuditTableName(null, role.getCollectionTable());
            }
            JoinColumn[] joinColumns = auditJoinTable.inverseJoinColumns();
            String[] elementColumns;
            if (joinColumns.length == 0) {
                elementColumns = role.getElementColumns();
            } else {
                elementColumns = Arrays.stream(joinColumns).map(JoinColumn::name).map(EnversUtils::normalizeQuotedName).toArray(String[]::new);
            }
            return new AuditedRole(role, auditMiddleTableName, role.getKeyColumns(), role.getIndexColumns(), elementColumns);
        }
        return null;
    }

    public static List<AuditedRole> getRoles(String hibEntityName) {
        List<AuditedRole> roles = new ArrayList<>();
        ClassAuditingData classAuditingData = getClassAuditingData(hibEntityName);
        for (String propertyName : classAuditingData.getPropertyNames()) {
            Property propertyData = HibernateUtils.getProperty(hibEntityName, propertyName);
            Map<String, Property> flatProperties = HibernateUtils.getFlatProperty(propertyData);
            for (Map.Entry<String, Property> entry : flatProperties.entrySet()) {
                AuditedRole role = getRole(hibEntityName, entry.getKey());
                if (role != null) {
                    roles.add(role);
                }
            }
        }
        return roles;
    }

    public static Set<String> getStandaloneCollectionAuditTableNames() {
        Set<String> tables = new HashSet<>();
        for (String entityName : getAuditedEntityNames()) {
            tables.addAll(getRoles(entityName).stream().filter(r -> !r.isOneToMany()).map(AuditedRole::getAuditCollectionTable).collect(Collectors.toSet()));
        }
        return tables;
    }

    public static Set<String> getStandaloneCollectionAuditEntityNames() {
        return getStandaloneCollectionAuditTableNames().stream().map(HibernateUtils::resolveEntityName).collect(Collectors.toSet());
    }

    public static void simAuditInsert(Session session, String entityName, Serializable id) {
        EnversService service = getEnversService();
        EventSource source = (EventSource) session;
        Object entity = session.get(entityName, id);
        EntityPersister persister = source.getEntityPersister(entityName, entity);
        // session should be cast to SharedSessionContractImplementor in hibernate 5.2.x
        Object[] state = HibernateUtils.getPropertyValuesToInsert(persister, entity, session);
        final AuditProcess auditProcess = getEnversService().getAuditProcessManager().get(source);
        final AuditWorkUnit workUnit = new AddWorkUnit(
                (SessionImplementor) session,
                entityName,
                service,
                id,
                persister,
                state
        );
        auditProcess.addWorkUnit( workUnit );
    }

    public static void simCollectionAuditRecreate(Session session, String entityName, Serializable id, String property) {
        String role = entityName + "." + property;
        CollectionPersister collectionPersister = HibernateUtils.getCollectionPersister(session, role);
        if (!collectionPersister.isInverse()) {
            PersistentCollection collection = HibernateUtils.getPersistentCollection(session, role, id);
            CollectionEntry collectionEntry = HibernateUtils.getPersistenceContext(session).getCollectionEntry(collection);
            final AuditProcess auditProcess = getEnversService().getAuditProcessManager().get((EventSource) session);
            final String ownerEntityName = ((AbstractCollectionPersister) collectionEntry.getLoadedPersister()).getOwnerEntityName();
            final String referencingPropertyName = collectionEntry.getRole().substring( ownerEntityName.length() + 1 );
            final PersistentCollectionChangeWorkUnit workUnit = new PersistentCollectionChangeWorkUnit(
                    (SessionImplementor) session,
                    entityName,
                    getEnversService(),
                    collection,
                    collectionEntry,
                    null,
                    id,
                    referencingPropertyName
            );
            auditProcess.addWorkUnit( workUnit );
        }
    }

    public static EnversService getEnversService() {
        return MetaIntegrator.getService(EnversService.class);
    }

    public static String getRevInfoEntityName() {
        return getEnversService().getAuditEntitiesConfiguration().getRevisionInfoEntityName();
    }

    public static String getRevInfoTableName() {
        return HibernateUtils.getTableName(getRevInfoEntityName());
    }

    public static String getRevPropertyName() {
        return getEnversService().getAuditEntitiesConfiguration().getRevisionFieldName();
    }

    public static String getRevEndPropertyName() {
        return getEnversService().getAuditEntitiesConfiguration().getRevisionEndFieldName();
    }

    public static String getRevEndTimestampPropertyName() {
        return getEnversService().getAuditEntitiesConfiguration().getRevisionEndTimestampFieldName();
    }

    public static String getRevTypePropertyName() {
        return getEnversService().getAuditEntitiesConfiguration().getRevisionTypePropName();
    }

    public static String getRevTypePropertyType() {
        return getEnversService().getAuditEntitiesConfiguration().getRevisionTypePropType();
    }

    public static String getRevInfoIdPropertyName() {
        return MetaIntegrator.getRevInfoResolver().getRevisionInfoIdData().getName();
    }

    public static String getRevInfoIdColumnName() {
        return HibernateUtils.getColumnName(getRevInfoEntityName(), getRevInfoIdPropertyName())[0];
    }

    public static String getRevInfoIdColumnType() {
        return HibernateUtils.getColumnType(getRevInfoEntityName(), getRevInfoIdPropertyName())[0];
    }

    public static String getRevInfoTimestampPropertyName() {
        return MetaIntegrator.getRevInfoResolver().getRevisionInfoTimestampData().getName();
    }

    public static String getRevInfoTimestampColumnName() {
        return HibernateUtils.getColumnName(getRevInfoEntityName(), getRevInfoTimestampPropertyName())[0];
    }

    public static String getRevInfoTimestampColumnType() {
        return HibernateUtils.getColumnType(getRevInfoEntityName(), getRevInfoTimestampPropertyName())[0];
    }

    public static String getRevInfoModifiedEntitiesPropertyName() {
        return MetaIntegrator.getRevInfoResolver().getModifiedEntityNamesData().getName();
    }

    public static String getRevInfoModifiedEntitiesTableName() {
        String property = getRevInfoModifiedEntitiesPropertyName();
        if (property == null) {
            return null;
        }
        return HibernateUtils.getRole(getRevInfoEntityName(), property).getCollectionTable();
    }

    public static String getRevInfoModifiedEntitiesKeyColumnName() {
        String property = getRevInfoModifiedEntitiesPropertyName();
        if (property == null) {
            return null;
        }
        return HibernateUtils.getRole(getRevInfoEntityName(), property).getKeyColumns()[0];
    }

    public static String getRevInfoModifiedEntitiesElementColumnName() {
        String property = getRevInfoModifiedEntitiesPropertyName();
        if (property == null) {
            return null;
        }
        return HibernateUtils.getRole(getRevInfoEntityName(), property).getElementColumns()[0];
    }

    public static boolean isAuditedTable(String tableName) {
        String type = HibernateUtils.tableType(tableName);
        switch (type) {
            case "entity":
                String entityName = HibernateUtils.resolveEntityName(tableName);
                return isAuditedEntity(entityName);
            case "collection":
                List<Role> roles = HibernateUtils.getRolesFromMiddleTable(tableName);
                return roles.stream().allMatch(role -> getRole(role.getEntity(), role.getProperty()) != null);
            case "unknown":
            default:
                throw new IllegalArgumentException("Invalid table: " + tableName + ".");
        }
    }

    public static String TABLE_NAME_AUDIT_TEMP_INFO = "AUD__TEMP_INFO";

    @SuppressWarnings("SameParameterValue")
    private static String beginTrigger(@Nonnull String tableName, @Nonnull String method, String prefix, String postfix) {
        if (prefix == null) {
            prefix = "tg_";
        }
        if (postfix == null) {
            postfix = "_audit";
        }
        String tgName= prefix + tableName + postfix + "_" + method.toLowerCase();
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TRIGGER ").append(HibernateUtils.quote(tgName)).append("\n");
        sb.append("ON ").append(HibernateUtils.quote(tableName)).append(" AFTER ").append(method.toUpperCase()).append(" AS\n");
        sb.append("BEGIN\n");
        sb.append("  DECLARE @tid INT;\n");
        sb.append("  SELECT @tid = transaction_id FROM sys.dm_tran_current_transaction;\n");
        sb.append("  DECLARE @rev INT = NULL;\n");
        String columnTimestamp = getRevInfoTimestampColumnName();
        String columnId = getRevInfoIdColumnName();
        List<String> revInfoColumnNames = HibernateUtils.getColumnNames(EnversUtils.getRevInfoEntityName(), false, true);
        List<String> revInfoVarNames = Utils.makeUnconflict(revInfoColumnNames);
        List<String> revInfoColumnTypes = HibernateUtils.getColumnTypes(EnversUtils.getRevInfoEntityName(), true);
        int idxToRemove = revInfoColumnNames.indexOf(columnId);
        revInfoColumnNames.remove(idxToRemove);
        revInfoVarNames.remove(idxToRemove);
        revInfoColumnTypes.remove(idxToRemove);
        idxToRemove = revInfoColumnNames.indexOf(columnTimestamp);
        revInfoColumnNames.remove(idxToRemove);
        revInfoVarNames.remove(idxToRemove);
        revInfoColumnTypes.remove(idxToRemove);
        IntStream.range(0, revInfoVarNames.size())
                .forEach(i -> sb.append("  DECLARE @").append(revInfoVarNames.get(i)).append(" ").append(revInfoColumnTypes.get(i)).append(" = NULL;\n"));
        sb.append("  SELECT\n");
        revInfoVarNames.forEach(var -> sb.append("    @").append(var).append(" = ").append(var).append(",\n"));
        sb.append("    @rev = rev,\n");
        sb.append("  FROM ").append(HibernateUtils.quote(TABLE_NAME_AUDIT_TEMP_INFO)).append("\n");
        sb.append("  WHERE tid = @tid\n");
        sb.append("  IF @rev IS NOT NULL\n");
        sb.append("    BEGIN\n");
        sb.append("      DECLARE @sometime DATETIME2 = SYSUTCDATETIME();\n");
        sb.append("      DECLARE @someday DATETIME = DATEADD(DAY, DATEDIFF(DAY, 0, GETUTCDATE()), 0);\n");
        sb.append("      DECLARE @tstmp BIGINT = datediff(ms, @someday, @sometime) + CAST(DATEDIFF(DAY, '1970-01-01T00:00:00', @someday) AS BIGINT) * 24 * 3600 * 1000;\n");
        sb.append("      MERGE REVINFO AS r\n");
        sb.append("      USING (\n");
        sb.append("        SELECT\n");
        revInfoVarNames.forEach(var -> sb.append("          @").append(var).append(" as ").append(var).append(",\n"));
        sb.append("          @rev as rev,\n");
        sb.append("          @tstmp as tstmp,\n");
        sb.append("      ) AS d\n");
        sb.append("      ON r.").append(HibernateUtils.quote(columnId)).append(" = d.rev\n");
        sb.append("      WHEN NOT MATCHED BY TARGET THEN\n");
        sb.append("      INSERT(").append(HibernateUtils.quote(columnId)).append(", ").append(HibernateUtils.quote(columnTimestamp));
        revInfoColumnNames.forEach(c -> sb.append(", ").append(HibernateUtils.quote(c)));
        sb.append(") VALUES (d.rev, d.tstmp");
        revInfoVarNames.forEach(var -> sb.append(", d.").append(var));
        sb.append(");");
        return sb.toString();
    }

    @SuppressWarnings("SameParameterValue")
    private static String generateInsertValueForEntityTable(
            @Nonnull List<String> auditColumnNames,
            List<String> auditedColumnNames,
            Set<String> auditModifiedColumnNames,
            String entityName,
            String auditTableName,
            String identity,
            String indent) {
        return auditColumnNames.stream().map(c -> {
            if (getRevPropertyName() != null && Objects.equals(c.toUpperCase(), getRevPropertyName().toUpperCase())) {
                return indent + "  @rev";
            }
            if (getRevTypePropertyName() != null && Objects.equals(c.toUpperCase(), getRevTypePropertyName().toUpperCase())) {
                return indent + "  0";
            }
            if (getRevEndPropertyName() != null && Objects.equals(c.toUpperCase(), getRevEndPropertyName().toUpperCase())) {
                return indent + "  NULL";
            }
            if (getRevEndTimestampPropertyName() != null && Objects.equals(c.toUpperCase(), getRevEndTimestampPropertyName().toUpperCase())) {
                return indent + "  NULL";
            }
            if (auditedColumnNames.contains(c)) {
                return indent + "  " + identity + "." + HibernateUtils.quote(c);
            }
            if (auditModifiedColumnNames.contains(c)) {
                String propertyName = getPropertyNameFromModifiedColumnName(entityName, c);
                Property property = HibernateUtils.getProperty(entityName, propertyName);
                String[] columns = getAuditedColumnName(entityName, propertyName, false);
                if (!property.isOptional()) {
                    return indent + "  1";
                } else if (HibernateUtils.hasCollectionProperty(entityName, propertyName)) {
                    if (HibernateUtils.isCollectionPropertyInitialNotNull(entityName, propertyName)) {
                        return indent + "  1";
                    } else {
                        return indent
                                + "  ( CASE WHEN "
                                + Arrays.stream(columns)
                                .map(col -> identity + "." + HibernateUtils.quote(col) + " IS NULL")
                                .collect(Collectors.joining(" AND "))
                                + " THEN 0 ELSE 1 END )";
                    }
                } else {
                    return indent
                            + "  ( CASE WHEN "
                            + Arrays.stream(columns)
                            .map(col -> identity + "." + HibernateUtils.quote(col) + " IS NULL")
                            .collect(Collectors.joining(" AND "))
                            + " THEN 0 ELSE 1 END )";
                }
            }
            throw new IllegalArgumentException("Unable to deal with column: " + c + " of table: " + auditTableName + ".");
        }).collect(Collectors.joining(",\n"));
    }

    @SuppressWarnings("SameParameterValue")
    private static String generateInserModifiedFlagForEntityTable(
            @Nonnull List<String> auditColumnNames,
            @Nonnull List<String> auditedColumnNames,
            @Nonnull Set<String> auditModifiedColumnNames,
            @Nonnull String modifiedColumn,
            @Nonnull String auditTableName,
            @Nonnull String identity,
            @Nonnull String indent) {
        String revPropertyName = getRevPropertyName();
        final String rev = revPropertyName != null ? revPropertyName.toUpperCase() : null;
        String revTypePropertyName = getRevTypePropertyName();
        final String revType = revTypePropertyName != null ? revTypePropertyName.toUpperCase() : null;
        String revEndPropertyName = getRevEndPropertyName();
        final String revEnd = revEndPropertyName != null ? revEndPropertyName.toUpperCase() : null;
        String revEndTimestampPropertyName = getRevEndTimestampPropertyName();
        final String revEndTimestamp = revEndTimestampPropertyName != null ? revEndTimestampPropertyName.toUpperCase() : null;
        return auditColumnNames.stream().map(c -> {
            if (rev != null && rev.equalsIgnoreCase(c)) {
                return indent + "  @rev";
            }
            if (revType != null && revType.equalsIgnoreCase(c)) {
                return indent + "  1";
            }
            if (revEnd != null && revEnd.equalsIgnoreCase(c)) {
                return indent + "  source." + HibernateUtils.quote(rev);
            }
            if (revEndTimestamp != null && revEndTimestamp.equalsIgnoreCase(c)) {
                return indent + "  source." + HibernateUtils.quote("__last_time__");
            }
            if (modifiedColumn.equalsIgnoreCase(c)) {
                return indent + "  1";
            }
            if (auditedColumnNames.contains(c)) {
                return indent + "  " + identity + "." + HibernateUtils.quote(c);
            }
            if (auditModifiedColumnNames.contains(c)) {
                return indent + "  0";
            }
            throw new IllegalArgumentException("Unable to deal with column: " + c + " of table: " + auditTableName + ".");
        }).collect(Collectors.joining(",\n"));
    }

    private static String triggerInsertForEntityTable(String tableName) {
        String entityName = HibernateUtils.resolveEntityName(tableName);
        String auditTableName = getAuditTableName(entityName);
        String auditEntityName = HibernateUtils.resolveEntityName(auditTableName);
        List<String> auditColumnNames = HibernateUtils.getColumnNames(auditEntityName, false, true);
        List<String> auditedColumnNames = getAuditedColumnNames(entityName, false);
        Set<String> auditModifiedColumnNames = new HashSet<>(getAuditModifiedColumnNames(entityName, false).values());
        String indent = "      ";
        return indent + "INSERT INTO " + HibernateUtils.quote(auditTableName) + " (\n" +
                String.join(",\n", auditColumnNames.stream().map(c -> indent + "  " + HibernateUtils.quote(c)).collect(Collectors.toList())) +
                "\n" +
                indent + ")\n" +
                indent + "SELECT\n" +
                generateInsertValueForEntityTable(auditColumnNames, auditedColumnNames, auditModifiedColumnNames, entityName, auditTableName, "i", indent) +
                "\n" +
                indent + "FROM inserted i;";
    }

    private static String triggerInsertForCollectionTable(String tableName) {
        List<AuditedRole> roles = HibernateUtils.getRolesFromMiddleTable(tableName).stream()
                .map(role -> getRole(role.getEntity(), role.getProperty()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Invalid collection table: " + tableName + ".");
        }
        AuditedRole role = roles.iterator().next();
        String auditTableName = role.getAuditCollectionTable();
        String auditEntityName = HibernateUtils.resolveEntityName(auditTableName);
        List<String> auditColumnNames = HibernateUtils.getColumnNames(auditEntityName, false, true);
        String indent = "      ";
        StringBuilder sb = new StringBuilder();
        EnversService service = getEnversService();
        String setOrdinalPropertyName = service.getAuditEntitiesConfiguration().getEmbeddableSetOrdinalPropertyName();
        final String setOrdinal = setOrdinalPropertyName != null ? setOrdinalPropertyName.toUpperCase() : null;
        String revPropertyName = getRevPropertyName();
        final String rev = revPropertyName != null ? revPropertyName.toUpperCase() : null;
        String revTypePropertyName = getRevTypePropertyName();
        final String revType = revTypePropertyName != null ? revTypePropertyName.toUpperCase() : null;
        boolean withSetOrder = roles.size() == 1 && setOrdinal != null && auditColumnNames.contains(setOrdinal);
        if (withSetOrder) {
            String keyList = Arrays.stream(role.getKeyColumns()).map(c -> "i." + HibernateUtils.quote(c)).collect(Collectors.joining(", "));
            int keyNum = role.getAuditKeyColumns().length;
            String keyJoin = IntStream.range(0, keyNum).mapToObj(i -> {
                String column = role.getKeyColumns()[i];
                String auditColumn = role.getAuditKeyColumns()[i];
                return "i." + HibernateUtils.quote(column) + " = a." + HibernateUtils.quote(auditColumn);
            }).collect(Collectors.joining(" AND "));
            sb.append(indent).append("WITH LastSetOrders AS (\n");
            sb.append(indent).append("  SELECT a.").append(HibernateUtils.quote(rev))
                    .append(", a.").append(HibernateUtils.quote(revType))
                    .append(", a.").append(HibernateUtils.quote(setOrdinal))
                    .append(", ").append(keyList)
                    .append(", ROW_NUMBER() OVER (PARTITION BY ").append(keyList).append(" ORDER BY a.").append(rev).append(" DESC) AS rn")
                    .append("\n");
            sb.append(indent).append("  FROM inserted i\n");
            sb.append(indent).append("  LEFT JOIN ").append(HibernateUtils.quote(auditTableName)).append(" a ON ")
                    .append(keyJoin).append("\n");
            sb.append(indent).append("  WHERE a.").append(HibernateUtils.quote(rev)).append(" = @rev AND a.").append(HibernateUtils.quote(revType)).append(" = 0 AND rn = 1\n");
            sb.append(indent).append(")\n");
        }
        sb.append(indent).append("INSERT INTO ").append(HibernateUtils.quote(auditTableName)).append(" (\n");
        sb.append(String.join(",\n", auditColumnNames.stream().map(c -> indent + "  " + HibernateUtils.quote(c)).collect(Collectors.toList())));
        sb.append("\n");
        sb.append(indent).append(")\n");
        sb.append(indent).append("SELECT\n");
        sb.append(String.join(",\n", auditColumnNames.stream().map(c -> {
            if (rev != null && Objects.equals(c.toUpperCase(), rev)) {
                return indent + "  @rev";
            }
            if (revType != null && Objects.equals(c.toUpperCase(), revType)) {
                return indent + "  0";
            }
            if (getRevEndPropertyName() != null && Objects.equals(c.toUpperCase(), getRevEndPropertyName().toUpperCase())) {
                return indent + "  NULL";
            }
            if (getRevEndTimestampPropertyName() != null && Objects.equals(c.toUpperCase(), getRevEndTimestampPropertyName().toUpperCase())) {
                return indent + "  NULL";
            }
            if (role.getAuditKeyColumns() != null) {
                List<String> auditKeyColumns = Arrays.asList(role.getAuditKeyColumns());
                int keyIndex = auditKeyColumns.indexOf(c);
                if (keyIndex != -1) {
                    return indent + "  i." + HibernateUtils.quote(role.getKeyColumns()[keyIndex]);
                }
            }
            if (role.getAuditIndexColumns() != null) {
                List<String> auditIndexColumns = Arrays.asList(role.getAuditIndexColumns());
                int indexIndex = auditIndexColumns.indexOf(c);
                if (indexIndex != -1) {
                    return indent + "  i." + HibernateUtils.quote(role.getIndexColumns()[indexIndex]);
                }
            }
            if (role.getAuditElementColumns() != null) {
                List<String> auditElementColumns = Arrays.asList(role.getAuditElementColumns());
                int elementIndex = auditElementColumns.indexOf(c);
                if (elementIndex != -1) {
                    return indent + "  i." + HibernateUtils.quote(role.getElementColumns()[elementIndex]);
                }
            }
            if (setOrdinal != null && Objects.equals(c.toUpperCase(), setOrdinal)) {
                return indent + "  ( CASE WHEN lso." + HibernateUtils.quote(setOrdinal) + " IS NULL THEN 0 ELSE lso." + HibernateUtils.quote(setOrdinal) + " + 1 END )";
            }
            throw new IllegalArgumentException("Unable to deal with column: " + c + " of table: " + auditTableName + ".");
        }).collect(Collectors.toList())));
        sb.append("\n");
        if (withSetOrder) {
            String keyJoin = Arrays.stream(role.getKeyColumns())
                    .map(c -> "i." + HibernateUtils.quote(c) + " = lso." + HibernateUtils.quote(c))
                    .collect(Collectors.joining(" AND "));
            sb.append(indent).append("FROM inserted i\n");
            sb.append(indent).append("JOIN LastSetOrders lso ON ").append(keyJoin).append(";");
        } else {
            sb.append(indent).append("FROM inserted i;");
        }
        roles.forEach(r -> {
            String atn = getAuditTableName(r.getEntity());
            String mc = getAuditModifiedColumnName(r.getEntity(), r.getProperty(), false);
            String[] refKeys = HibernateUtils.getKeyColumns(r.getEntity());
            String ate = HibernateUtils.resolveEntityName(atn);
            String[] auditKeys = HibernateUtils.getKeyColumns(ate);
            List<String> acs = HibernateUtils.getColumnNames(ate, false, true);
            String keyList1 = Arrays.stream(refKeys).map(c -> "a." + HibernateUtils.quote(c)).collect(Collectors.joining(", "));
            String keyList2 = acs.stream().map(HibernateUtils::quote).collect(Collectors.joining(", "));
            String valueList = generateInserModifiedFlagForEntityTable(
                    acs,
                    getAuditedColumnNames(r.getEntity(), false),
                    new HashSet<>(getAuditModifiedColumnNames(r.getEntity(), false).values()),
                    mc,
                    atn,
                    "source",
                    indent + "    "
            );
            String keyJoins1 = IntStream.range(0, r.getKeyColumns().length).mapToObj(i -> {
                String key = r.getKeyColumns()[i];
                String refKey = refKeys[i];
                return "a." + HibernateUtils.quote(refKey) + " = i." + HibernateUtils.quote(key);
            }).collect(Collectors.joining(" AND "));
            String keyJoins4 = Arrays.stream(auditKeys)
                    .map(c -> "target." + HibernateUtils.quote(c) + " = source." + HibernateUtils.quote(c))
                    .collect(Collectors.joining(" AND "));
            String quotedRev = HibernateUtils.quote(rev);
            sb.append("\n");
            sb.append(indent).append("MERGE ").append(HibernateUtils.quote(atn)).append(" AS target\n");
            sb.append(indent).append("USING (\n");
            sb.append(indent).append("  SELECT\n");
            sb.append(indent).append("    a.*,\n");
            sb.append(indent).append("    ROW_NUMBER() OVER (PARTITION BY ").append(keyList1).append(" ORDER BY a.").append(quotedRev).append(" DESC) AS ").append(HibernateUtils.quote("__rn__")).append(",\n");
            sb.append(indent).append("    r.").append(HibernateUtils.quote(getRevInfoTimestampColumnName())).append(" AS ").append(HibernateUtils.quote("__last_time__")).append("\n");
            sb.append(indent).append("  FROM ").append(HibernateUtils.quote(atn)).append(" a\n");
            sb.append(indent).append("  JOIN inserted i ON ").append(keyJoins1).append(" AND a.").append(quotedRev).append(" <= @rev\n");
            sb.append(indent).append("  JOIN ").append(HibernateUtils.quote(getRevInfoTableName())).append(" r ON a.").append(quotedRev).append(" = r.").append(quotedRev).append("\n");
            sb.append(indent).append("  WHERE ").append(HibernateUtils.quote("__rn__")).append(" = 1\n");
            sb.append(indent).append(") as source\n");
            sb.append(indent).append("ON ").append(keyJoins4).append(" AND source.").append(quotedRev).append(" = @rev").append("\n");
            sb.append(indent).append("WHEN MATCHED THEN\n");
            sb.append(indent).append("  UPDATE SET target.").append(HibernateUtils.quote(mc)).append(" = 1\n");
            sb.append(indent).append("WHEN NOT MATCHED BY TARGET THEN\n");
            sb.append(indent).append("  INSERT\n");
            sb.append(indent).append("    (").append(keyList2).append(")\n");
            sb.append(indent).append("  VALUES (\n");
            sb.append(valueList).append("\n");
            sb.append(indent).append("  );");
        });
        if(service.getGlobalConfiguration().isTrackEntitiesChangedInRevision()) {
            String modifiedEntitiesTableName = getRevInfoModifiedEntitiesTableName();
            if (modifiedEntitiesTableName != null) {
                Set<String> changedEntities = roles.stream().map(AuditedRole::getEntity).collect(Collectors.toSet());
                for (String entity : changedEntities) {
                    sb.append("\n");
                    sb.append(indent).append("MERGE ").append(HibernateUtils.quote(modifiedEntitiesTableName)).append(" AS target\n");
                    sb.append(indent).append("USING (\n");
                    sb.append(indent).append("  SELECT @rev, '").append(entity).append("'\n");
                    sb.append(indent).append(") AS source ( rev, entity )\n");
                    sb.append(indent).append("ON target.")
                            .append(HibernateUtils.quote(getRevInfoModifiedEntitiesKeyColumnName()))
                            .append(" = source.rev AND target.")
                            .append(HibernateUtils.quote(getRevInfoModifiedEntitiesElementColumnName()))
                            .append(" = source.entity\n");
                    sb.append(indent).append("WHEN NOT MATCHED BY TARGET THEN\n");
                    sb.append(indent).append("  INSERT\n");
                    sb.append(indent).append("    ( ")
                            .append(HibernateUtils.quote(getRevInfoModifiedEntitiesKeyColumnName()))
                            .append(", ")
                            .append(HibernateUtils.quote(getRevInfoModifiedEntitiesElementColumnName()))
                            .append(" )\n");
                    sb.append(indent).append("  VALUES\n");
                    sb.append(indent).append("    ( source.rev, source.entity );");
                }
            }
        }
        return sb.toString();
    }

    public static String triggerInsert(String tableName, String prefix, String postfix) {
        if (isAuditedTable(tableName)) {
            String tableType = HibernateUtils.tableType(tableName);
            StringBuilder sb = new StringBuilder();
            sb.append(beginTrigger(tableName, "insert", prefix, postfix));
            sb.append("\n");
            switch (tableType) {
                case "entity":
                    sb.append(triggerInsertForEntityTable(tableName));
                    break;
                case "collection":
                    sb.append(triggerInsertForCollectionTable(tableName));
                    break;
                case "unknown":
                default:
                    throw new IllegalArgumentException("Invalid table: " + tableName + ".");
            }
            sb.append("\n");
            sb.append(endTrigger());
            return sb.toString();
        } else {
            return "";
        }
    }

    private static String endTrigger() {
        return "    END\n" + "END;";
    }

    public static String PROCEDURE_AUDIT_EXEC_SQL = "auditExecSql";

    public static String auditExecSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(ifBlock("",
                exists(checkProcedureExists(PROCEDURE_AUDIT_EXEC_SQL)),
                "DROP PROCEDURE " + HibernateUtils.quote(PROCEDURE_AUDIT_EXEC_SQL) + ";"
        ));
        sb.append("\n");
        StringBuilder code = new StringBuilder();
        //todo: 提高泛用性
        code.append(SqlUtils.declare("rev", "INT", "NEXT VALUE FOR hibernate_sequence"));
        code.append("\n");
        code.append(SqlUtils.declare("tid", "INT", "(SELECT transaction_id FROM sys.dm_tran_current_transaction)"));
        code.append("\n");
        Map<String, String> paramsDefines = new HashMap<>();
        paramsDefines.put("sql", "NVARCHAR(MAX)");
        paramsDefines.put("entity", "VARCHAR(256) = NULL");
        sb.append(defineProcedure(
                "CREATE",
                PROCEDURE_AUDIT_EXEC_SQL,
                SqlUtils.transactionBlock(code.toString(), "audit_exec_sql_trans", null),
                paramsDefines,
                null
        ));
        return sb.toString();
    }
}
