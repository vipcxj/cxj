package com.cxj.hibernate;

import com.cxj.hibernate.spi.MetaIntegrator;
import org.hibernate.InstantiationException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.*;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.service.ServiceRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by vipcxj on 2018/1/26.
 */
public class HibernateUtils {

    private static SessionImplementor unwrap(Session session) {
        if (session instanceof SessionImplementor) {
            return (SessionImplementor) session;
        }
        throw new RuntimeException("The session is not an instance of SessionImplementor.");
    }

    private static SessionFactoryImplementor unwrap(SessionFactory factory) {
        if (factory instanceof SessionFactoryImplementor) {
            return (SessionFactoryImplementor) factory;
        }
        throw new RuntimeException("The session factory is not an instance of SessionFactoryImplementor.");
    }

    public static ServiceRegistry getServiceRegistry() {
        return MetaIntegrator.getServiceRegistry();
    }

    static CollectionPersister getCollectionPersister(Session session, String role) {
        return getCollectionPersister(session.getSessionFactory(), role);
    }

    static CollectionPersister getCollectionPersister(SessionFactory factory, String role) {
        SessionFactoryImplementor sfi = unwrap(factory);
        return sfi.getCollectionPersister(role);
    }

    static PersistentCollection getPersistentCollection(Session session, String role, Serializable id) {
        CollectionPersister collectionPersister = getCollectionPersister(session, role);
        return getPersistenceContext(session).getCollection(new CollectionKey(collectionPersister, id));
    }

    static PersistenceContext getPersistenceContext(Session session) {
        return unwrap(session).getPersistenceContext();
    }

/*    public static EntityPersister getEntityPersister(Session session, String hibEntityName) {
        return getEntityPersister(session.getSessionFactory(), hibEntityName);
    }

    public static EntityPersister getEntityPersister(SessionFactory factory, String hibEntityName) {
        return unwrap(factory).getEntityPersister(hibEntityName);
    }*/

    public static Dialect getDialect() {
        JdbcServices jdbcService = MetaIntegrator.getService(JdbcServices.class);
        return jdbcService.getDialect();
    }

    private static Method METHOD_GET_PROPERTY_VALUES_TO_INSERT = null;
    //work around for compatible with both 5.1.x and 5.2.x
    static Object[] getPropertyValuesToInsert(EntityPersister persister, Object entity, Session session) {
        if (METHOD_GET_PROPERTY_VALUES_TO_INSERT == null) {
            METHOD_GET_PROPERTY_VALUES_TO_INSERT = Arrays.stream(EntityPersister.class.getMethods())
                    .filter(m -> {
                        if (m.getName().equals("getPropertyValuesToInsert")) {
                            Class<?>[] parameterTypes = m.getParameterTypes();
                            if (parameterTypes.length == 3) {
                                return parameterTypes[0].equals(Object.class) && parameterTypes[1].isAssignableFrom(Map.class) && parameterTypes[2].isAssignableFrom(SessionImplementor.class);
                            }
                        }
                        return false;
                    }).findFirst().orElse(null);
        }
        if (METHOD_GET_PROPERTY_VALUES_TO_INSERT != null) {
            try {
                METHOD_GET_PROPERTY_VALUES_TO_INSERT.setAccessible(true);
                return (Object[]) METHOD_GET_PROPERTY_VALUES_TO_INSERT.invoke(persister, entity, null, unwrap(session));
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            throw new IllegalStateException("No such method: org.hibernate.persister.entity.EntityPersister.getPropertyValuesToInsert");
        }
    }

    public static Set<String> getAllEntityNames() {
        return getMetadata().getEntityBindings().stream()
                .map(PersistentClass::getEntityName)
                .collect(Collectors.toSet());
    }

    @Nonnull
    private static Metadata getMetadata() {
        Metadata metadata = MetaIntegrator.getMetadata();
        if (metadata == null) {
            throw new IllegalStateException("Unable to get the metadata.");
        }
        return metadata;
    }

    @Nonnull
    private static PersistentClass getPersistentClass(String hibEntityName) {
        Metadata metadata = getMetadata();
        PersistentClass entityBinding = metadata.getEntityBinding(hibEntityName);
        if (entityBinding == null) {
            throw new IllegalArgumentException("Invalid hibernate entity name: " + hibEntityName + ".");
        }
        return entityBinding;
    }

    @Nullable
    public static Property getChildProperty(@Nonnull Property property, @Nonnull String name) {
        if (!(property.getValue() instanceof Component)) {
            return null;
        }
        Component component = (Component) property.getValue();
        Iterator iterator = component.getPropertyIterator();
        while (iterator.hasNext()) {
            Property p = (Property) iterator.next();
            if (name.equals(p.getName())) {
                return p;
            }
        }
        return null;
    }

    public static Property[] getChildrenProperty(Property property) {
        if (!(property.getValue() instanceof Component)) {
            return new Property[0];
        }
        Component component = (Component) property.getValue();
        Property[] properties = new Property[component.getPropertySpan()];
        Iterator iterator = component.getPropertyIterator();
        for (int i = 0; i < properties.length; ++i) {
            properties[i] = (Property) iterator.next();
        }
        return properties;
    }

    public static Map<String, Property> getFlatChildrenProperty(Property property) {
        Map<String, Property> flatProperties = new HashMap<>();
        Property[] childrenProperties = getChildrenProperty(property);
        for (Property childProperty : childrenProperties) {
            Map<String, Property> flatChildrenProperties = getFlatChildrenProperty(childProperty);
            if (flatChildrenProperties.isEmpty()) {
                flatProperties.put(property.getName() + "." + childProperty.getName(), childProperty);
            } else {
                for (Map.Entry<String, Property> entry : flatChildrenProperties.entrySet()) {
                    flatProperties.put(property.getName() + "." + entry.getKey(), entry.getValue());
                }
            }
        }
        return flatProperties;
    }

    public static Map<String, Property> getFlatProperty(Property property) {
        Map<String, Property> flatChildrenProperty = getFlatChildrenProperty(property);
        if (flatChildrenProperty.isEmpty()) {
            Map<String, Property> propertyMap = new HashMap<>();
            propertyMap.put(property.getName(), property);
            return propertyMap;
        } else {
            return flatChildrenProperty;
        }
    }

    public static Object getPropertyValue(String hibEntityName, String propertyName, Object entity) {
        if (entity == null) {
            return null;
        }
        String[] parts = propertyName.split("\\.");
        Class entityType = getPersistentClass(hibEntityName).getMappedClass();
        Class containerType;
        Property property = null;
        Object current = entity;
        for (String part : parts) {
            if (current == null) {
                return null;
            }
            if (property == null) {
                property = getProperty(hibEntityName, part);
                containerType = entityType;
            } else {
                property = Arrays.stream(getChildrenProperty(property)).filter(p -> Objects.equals(p.getName(), part)).findAny().orElse(null);
                if (property == null) {
                    throw new IllegalArgumentException("Invalid property path: " + propertyName + " of entity: " + hibEntityName + ".");
                }
                containerType = current.getClass();
            }
            current = property.getGetter(containerType).get(current);
        }
        return current;
    }

    private static Object instantiate(Constructor constructor) {
        try {
            return constructor.newInstance();
        }
        catch (Exception e) {
            throw new InstantiationException( "could not instantiate test object", constructor.getDeclaringClass(), e );
        }
    }

    private static boolean isAbstractClass(Class clazz) {
        int modifier = clazz.getModifiers();
        return Modifier.isAbstract(modifier) || Modifier.isInterface(modifier);
    }

    private static final Class[] NO_PARAM_SIGNATURE = new Class[0];

    private static Constructor getConstructor(PersistentClass persistentClass) {
        if ( persistentClass == null || !persistentClass.hasPojoRepresentation() ) {
            return null;
        }

        try {
            Class clazz = persistentClass.getMappedClass();
            if ( isAbstractClass( clazz ) ) {
                return null;
            }
            try {
                //noinspection unchecked
                Constructor constructor = clazz.getDeclaredConstructor( NO_PARAM_SIGNATURE );
                constructor.setAccessible( true );
                return constructor;
            }
            catch ( NoSuchMethodException nme ) {
                throw new PropertyNotFoundException(
                        "Object class [" + clazz.getName() + "] must declare a default (no-argument) constructor"
                );
            }
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static boolean hasCollectionProperty(String hibEntityName, String propertyName) {
        Property property = getProperty(hibEntityName, propertyName);
        Map<String, Property> flatProperties = getFlatProperty(property);
        return flatProperties.values().stream().anyMatch(p -> p.getType().isCollectionType());
    }

    public static boolean isCollectionPropertyInitialNotNull(String hibEntityName, String propertyName) {
        PersistentClass persistentClass = getPersistentClass(hibEntityName);
        if (!persistentClass.hasPojoRepresentation()) {
            return false;
        }
        Property property = getProperty(hibEntityName, propertyName);
        Map<String, Property> flatProperties = getFlatProperty(property);
        Object test = instantiate(getConstructor(persistentClass));
        for (Map.Entry<String, Property> entry : flatProperties.entrySet()) {
            Property p = entry.getValue();
            if (p.getType().isCollectionType()) {
                Object value = getPropertyValue(hibEntityName, entry.getKey(), test);
                if (value != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Property[] getPropertyChain(String hibEntityName, String propertyName) {
        if (propertyName == null || propertyName.isEmpty()) {
            throw new IllegalArgumentException("Invalid property: " + propertyName + " of the entity: " + hibEntityName + ".");
        }
        int level = Utils.getPropertyLevel(propertyName);
        Property[] chain = new Property[level + 1];
        String rootProperty = Utils.getRootProperty(propertyName);
        chain[0] = getDirectProperty(hibEntityName, rootProperty);
        String leftProperty = Utils.getLeftProperty(propertyName, rootProperty);
        if (leftProperty.isEmpty()) {
            return chain;
        }
        String[] parts = leftProperty.split("\\.");
        int partNum = parts.length;
        Property currentProperty = chain[0];
        StringBuilder currentPropertyName = new StringBuilder(rootProperty);
        for (int i = 0; i < partNum; ++i) {
            String part = parts[i];
            currentPropertyName.append(".").append(part);
            chain[i + 1] = getChildProperty(currentProperty, part);
            if (chain[i + 1] == null) {
                throw new IllegalArgumentException("Invalid property: " + currentPropertyName + " of the entity: " + hibEntityName + ".");
            }
        }
        return chain;
    }

    public static Property getProperty(String hibEntityName, String propertyName) {
        String rootProperty = Utils.getRootProperty(propertyName);
        Property directProperty = getDirectProperty(hibEntityName, rootProperty);
        String leftProperty = Utils.getLeftProperty(propertyName, rootProperty);
        if (leftProperty.isEmpty()) {
            return directProperty;
        }
        String[] parts = leftProperty.split("\\.");
        Property currentProperty = directProperty;
        StringBuilder currentPropertyName = new StringBuilder(rootProperty);
        for (String part : parts) {
            currentPropertyName.append(".").append(part);
            currentProperty = getChildProperty(currentProperty, part);
            if (currentProperty == null) {
                throw new IllegalArgumentException("Invalid property: " + currentPropertyName + " of the entity: " + hibEntityName + ".");
            }
        }
        return currentProperty;
    }

    public static Property getDirectProperty(String hibEntityName, String property) {
        PersistentClass pc = getPersistentClass(hibEntityName);
        Property identifierProperty = pc.getIdentifierProperty();
        if (Objects.equals(property, identifierProperty.getName())) {
            return identifierProperty;
        }
        KeyValue identifier = pc.getIdentifier();
        if (identifier instanceof Component) {
            Iterator propertyIterator = ((Component) identifier).getPropertyIterator();
            while (propertyIterator.hasNext()) {
                Property pt = (Property) propertyIterator.next();
                if (Objects.equals(property, pt.getName())) {
                    return pt;
                }
            }
        }
        return pc.getProperty(property);
    }

    public static List<Property> getDirectProperties(String hibEntityName, boolean withKey) {
        PersistentClass pc = getPersistentClass(hibEntityName);
        List<Property> properties = new ArrayList<>();
        if (withKey) {
            KeyValue identifier = pc.getIdentifier();
            if (identifier instanceof Component) {
                Iterator propertyIterator = ((Component) identifier).getPropertyIterator();
                while (propertyIterator.hasNext()) {
                    properties.add((Property) propertyIterator.next());
                }
            } else {
                Property identifierProperty = pc.getIdentifierProperty();
                if (identifierProperty != null) {
                    properties.add(identifierProperty);
                }
            }
        }
        Iterator propertyIterator = pc.getPropertyIterator();
        while (propertyIterator.hasNext()) {
            properties.add((Property) propertyIterator.next());
        }
        return properties;
    }

    public static List<String> getDirectPropertyNames(String hibEntityName, boolean withKey) {
        return getDirectProperties(hibEntityName, withKey).stream().map(Property::getName).collect(Collectors.toList());
    }

    public static Property[] getKeyProperties(String hibEntityName) {
        PersistentClass pc = getPersistentClass(hibEntityName);
        KeyValue identifier = pc.getIdentifier();
        if (identifier instanceof Component) {
            Property[] keyProperties = new Property[((Component) identifier).getPropertySpan()];
            Iterator propertyIterator = ((Component) identifier).getPropertyIterator();
            int i = 0;
            while (propertyIterator.hasNext()) {
                keyProperties[i++] = (Property) propertyIterator.next();
            }
            return keyProperties;
        }
        Property keyProperty = pc.getIdentifierProperty();
        if (keyProperty != null) {
            return new Property[] { pc.getIdentifierProperty() };
        } else {
            return new Property[0];
        }
    }

    public static String[] getKeyPropertyNames(String hibEntityName) {
        return Arrays.stream(getKeyProperties(hibEntityName)).map(Property::getName).toArray(String[]::new);
    }

    static Stream<Column> getKeyColumnsStream(String hibEntityName) {
        return Arrays.stream(getKeyProperties(hibEntityName))
                .flatMap(pt -> StreamSupport.stream(Spliterators.spliteratorUnknownSize((Iterator<?>) pt.getColumnIterator(), Spliterator.ORDERED), false))
                .map(oc -> (Column) oc);
    }

    public static String[] getKeyColumns(String hibEntityName) {
        return getKeyColumnsStream(hibEntityName)
                .map(Column::getName)
                .toArray(String[]::new);
    }

    public static String[] getKeyColumnTypes(String hibEntityName) {
        return getKeyColumnsStream(hibEntityName)
                .map(c -> c.getSqlType(HibernateUtils.getDialect(), MetaIntegrator.getMetadata()))
                .toArray(String[]::new);
    }

    public static String getTableName(String hibEntityName) {
        return getPersistentClass(hibEntityName).getTable().getName();
    }

    public static String[] getColumnName(String hibEntityName, String propertyName) {
        return getColumnName(hibEntityName, propertyName, false);
    }

    public static String[] getColumnName(String hibEntityName, String propertyName, boolean quoted) {
        Property property = getProperty(hibEntityName, propertyName);
        if (property == null) {
            throw new IllegalArgumentException("Invalid property name: " + propertyName + " of entity: " + hibEntityName + ".");
        }
        String[] columns = new String[property.getColumnSpan()];
        Iterator propertyColumnIterator = property.getColumnIterator();
        Dialect dialect = HibernateUtils.getDialect();
        int i = 0;
        while (propertyColumnIterator.hasNext()) {
            Column column = (Column) propertyColumnIterator.next();
            columns[i++] = quoted ? dialect.openQuote() + column.getText() + dialect.closeQuote() : column.getText();
        }
        return columns;
    }

    public static String[] getColumnType(String hibEntityName, String propertyName) {
        Property property = getProperty(hibEntityName, propertyName);
        if (property == null) {
            throw new IllegalArgumentException("Invalid property name: " + propertyName + " of entity: " + hibEntityName + ".");
        }
        String[] columnTypes = new String[property.getColumnSpan()];
        Iterator propertyColumnIterator = property.getColumnIterator();
        int i = 0;
        while (propertyColumnIterator.hasNext()) {
            Column column = (Column) propertyColumnIterator.next();
            columnTypes[i++] = column.getSqlType(getDialect(), MetaIntegrator.getMetadata());
        }
        return columnTypes;
    }

    public static List<String> getColumnNames(String hibEntityName, boolean quoted, boolean withKey) {
        return getDirectPropertyNames(hibEntityName, withKey).stream().flatMap(pn -> Arrays.stream(getColumnName(hibEntityName, pn, quoted))).collect(Collectors.toList());
    }

    public static List<String> getColumnTypes(String hibEntityName, boolean withKey) {
        return getDirectPropertyNames(hibEntityName, withKey).stream().flatMap(pn -> Arrays.stream(getColumnType(hibEntityName, pn))).collect(Collectors.toList());
    }

    public static Table getTable(String tableName) {
        Metadata metadata = getMetadata();
        Table table = metadata.getEntityBindings().stream()
                .filter(eb -> Objects.equals(eb.getTable().getName(), tableName))
                .map(PersistentClass::getTable)
                .findAny()
                .orElse(null);
        if (table == null) {
            table = metadata.getCollectionBindings().stream()
                    .filter(cb -> Objects.equals(cb.getTable().getName(), tableName))
                    .map(Collection::getTable)
                    .findAny()
                    .orElse(null);
        }
        if (table == null) {
            throw new IllegalArgumentException("Invalid table: " + tableName + ".");
        }
        return table;
    }

    public static List<Column> getColumnsFromTable(String tableName) {
        Table table = getTable(tableName);
        Iterator columnIterator = table.getColumnIterator();
        List<Column> columns = new ArrayList<>();
        while (columnIterator.hasNext()) {
            columns.add((Column) columnIterator.next());
        }
        return columns;
    }

    public static Map<String, String> getCollectionTables(String hibEntityName) {
        return getCollectionTables(hibEntityName, true);
    }

    public static Map<String, String> getCollectionTables(String hibEntityName, boolean standalone) {
        Metadata metadata = getMetadata();
        return metadata.getCollectionBindings().stream()
                .filter(c -> {
                    boolean res = c.getOwner().getEntityName().equals(hibEntityName);
                    if (!res) {
                        return false;
                    }
                    if (standalone) {
                        return !c.isOneToMany();
                    }
                    return true;
                })
                .collect(Collectors.toMap(c -> c.getRole().substring(c.getOwnerEntityName().length() + 1), c -> c.getCollectionTable().getName()));
    }

    public static List<Role> getRoles() {
        Metadata metadata = getMetadata();
        return metadata.getCollectionBindings().stream()
                .map(c -> {
                    String property = c.getRole().substring(c.getOwnerEntityName().length() + 1);
                    String[] keyColumns = null;
                    String[] indexColumns = null;
                    String[] elementColumns = null;
                    KeyValue key = c.getKey();
                    if (key != null) {
                        keyColumns = getColumns(key);
                    }
                    if (c instanceof org.hibernate.mapping.List) {
                        Value index = ((org.hibernate.mapping.List) c).getIndex();
                        if (index != null) {
                            indexColumns = getColumns(index);
                        }
                    }
                    Value element = c.getElement();
                    if (element != null) {
                        elementColumns = getColumns(element);
                    }
                    return new Role(c.getOwnerEntityName(), property, c.getCollectionTable().getName(), keyColumns, indexColumns, elementColumns, c.isOneToMany(), c.isInverse());
                })
                .collect(Collectors.toList());
    }

    public static List<Role> getRoles(String hibEntityName) {
        Metadata metadata = getMetadata();
        return metadata.getCollectionBindings().stream()
                .filter(c -> Objects.equals(hibEntityName, c.getOwnerEntityName()))
                .map(c -> {
                    String property = c.getRole().substring(c.getOwnerEntityName().length() + 1);
                    String[] keyColumns = null;
                    String[] indexColumns = null;
                    String[] elementColumns = null;
                    KeyValue key = c.getKey();
                    if (key != null) {
                        keyColumns = getColumns(key);
                    }
                    if (c instanceof org.hibernate.mapping.List) {
                        Value index = ((org.hibernate.mapping.List) c).getIndex();
                        if (index != null) {
                            indexColumns = getColumns(index);
                        }
                    }
                    Value element = c.getElement();
                    if (element != null) {
                        elementColumns = getColumns(element);
                    }
                    return new Role(hibEntityName, property, c.getCollectionTable().getName(), keyColumns, indexColumns, elementColumns, c.isOneToMany(), c.isInverse());
                })
                .collect(Collectors.toList());
    }

    public static Role getRole(String hibEntityName, String propertyName) {
        Metadata metadata = getMetadata();
        Collection cb = metadata.getCollectionBinding(hibEntityName + "." + propertyName);
        String property = cb.getRole().substring(cb.getOwnerEntityName().length() + 1);
        String[] keyColumns = null;
        String[] indexColumns = null;
        String[] elementColumns = null;
        KeyValue key = cb.getKey();
        if (key != null) {
            keyColumns = getColumns(key);
        }
        if (cb instanceof org.hibernate.mapping.List) {
            Value index = ((org.hibernate.mapping.List) cb).getIndex();
            if (index != null) {
                indexColumns = getColumns(index);
            }
        }
        Value element = cb.getElement();
        if (element != null) {
            elementColumns = getColumns(element);
        }
        return new Role(hibEntityName, property, cb.getCollectionTable().getName(), keyColumns, indexColumns, elementColumns, cb.isOneToMany(), cb.isInverse());
    }

    public static List<Role> getRolesFromMiddleTable(String middleTable) {
        return getRoles().stream().filter(role -> Objects.equals(role.getCollectionTable(), middleTable)).collect(Collectors.toList());
    }

    public static Set<String> getStandaloneCollectionTables() {
        Metadata metadata = getMetadata();
        return metadata.getCollectionBindings().stream()
                .filter(c -> !c.isOneToMany())
                .map(cb -> cb.getCollectionTable().getName())
                .collect(Collectors.toSet());
    }

    public static String tableType(String tableName) {
        Metadata metadata = getMetadata();
        if (metadata.getEntityBindings().stream().anyMatch(eb -> Objects.equals(tableName, eb.getTable().getName()))) {
            return "entity";
        }
        if (metadata.getCollectionBindings().stream().anyMatch(cb -> Objects.equals(tableName, cb.getCollectionTable().getName()))) {
            return "collection";
        }
        return "unknown";
    }

    public static String resolveEntityName(String entityTableName) {
        Metadata metadata = getMetadata();
        return metadata.getEntityBindings().stream()
                .filter(eb -> Objects.equals(entityTableName, eb.getTable().getName()))
                .map(PersistentClass::getEntityName)
                .findAny().orElse(null);
    }

    @Nonnull
    private static String[] getColumns(@Nonnull Value value) {
        String[] columns = new String[value.getColumnSpan()];
        Iterator<Selectable> columnIterator = value.getColumnIterator();
        int i = 0;
        while (columnIterator.hasNext()) {
            columns[i++] = columnIterator.next().getText();
        }
        return columns;
    }

    public static List<Role> resolveRoles(String collectionTableName) {
        Metadata metadata = getMetadata();
        return metadata.getCollectionBindings().stream()
                .filter(cb -> Objects.equals(collectionTableName, cb.getCollectionTable().getName()))
                .map(cb -> {
                    String owner = cb.getOwnerEntityName();
                    String property = cb.getRole().substring(cb.getOwnerEntityName().length() + 1);
                    String[] keyColumns = null;
                    String[] indexColumns = null;
                    String[] elementColumns = null;
                    KeyValue key = cb.getKey();
                    if (key != null) {
                        keyColumns = getColumns(key);
                    }
                    if (cb instanceof org.hibernate.mapping.List) {
                        Value index = ((org.hibernate.mapping.List) cb).getIndex();
                        if (index != null) {
                            indexColumns = getColumns(index);
                        }
                    }
                    Value element = cb.getElement();
                    if (element != null) {
                        elementColumns = getColumns(element);
                    }
                    return new Role(owner, property, collectionTableName, keyColumns, indexColumns, elementColumns, cb.isOneToMany(), cb.isInverse());
                })
                .collect(Collectors.toList());
    }

    public static String quote(String identity) {
        Dialect dialect = HibernateUtils.getDialect();
        return dialect.openQuote() + identity + dialect.closeQuote();
    }

    public static String getDefaultSchemaName() {
        Metadata metadata = getMetadata();
        Identifier schema = metadata.getDatabase().getDefaultNamespace().getName().getSchema();
        return schema != null ? schema.render() : null;
    }

    public static String getDefaultCatalogName() {
        Metadata metadata = getMetadata();
        Identifier catalog = metadata.getDatabase().getDefaultNamespace().getName().getCatalog();
        return catalog != null ? catalog.render() : null;
    }

/*    private static String getIdentityGenerator(@Nonnull PersistentClass pc, String idVar, boolean declare, String indent) {
        IdentifierGeneratorFactory identifierGeneratorFactory = getServiceRegistry().getService(MutableIdentifierGeneratorFactory.class);
        IdentifierGenerator generator = pc.getIdentifier().createIdentifierGenerator(identifierGeneratorFactory, getDialect(), getDefaultCatalogName(), getDefaultSchemaName(), (RootClass) pc);
        if (generator == null) {
            throw new NullPointerException("Unable to find the identifier generator of " + pc.getEntityName() + ".");
        }
        if (generator instanceof IdentityGenerator) {
            return "";
        }
        if (generator.getClass().getName().equals("org.hibernate.id.enhanced.SequenceStyleGenerator")) {
            Object structure = ReflectHelper.invokeMethod(generator, "getDatabaseStructure", DatabaseStructure.class);
            Boolean isPhysicalSequence = ReflectHelper.invokeMethod(structure, "isPhysicalSequence", boolean.class);
            if (isPhysicalSequence) {
                String seqName = ((PersistentIdentifierGenerator) generator).generatorKey().toString();
                return getDialect().getSelectSequenceNextValString(seqName);
            } else {

            }
        }
        if (generator.getClass().getName().equals("org.hibernate.id.SequenceGenerator")) {
            String seqName = ((PersistentIdentifierGenerator) generator).generatorKey().toString();
            return getDialect().getSelectSequenceNextValString(seqName);
        }
        if (generator instanceof TableGenerator) {
            TableGenerator tableGenerator = (TableGenerator) generator;
            String selectQuery = ReflectHelper.invokeMethod(tableGenerator, "buildSelectQuery", String.class, getDialect());
            String updateQuery = ReflectHelper.invokeMethod(tableGenerator, "buildUpdateQuery", String.class);
            String insertQuery = ReflectHelper.invokeMethod(tableGenerator, "buildInsertQuery", String.class);
            String segmentValue = tableGenerator.getSegmentValue();
            selectQuery = selectQuery.replace("?", "'" + segmentValue + "'");
            StringBuilder sb = new StringBuilder();
            if (declare) {
                sb.append(SqlUtils.declare(idVar, ))
            }


            int incrementSize = tableGenerator.getIncrementSize();
            int initialValue = tableGenerator.getInitialValue();
            String tableName = tableGenerator.getTableName();
            String valueColumnName = tableGenerator.getValueColumnName();
        }

    }*/
}
