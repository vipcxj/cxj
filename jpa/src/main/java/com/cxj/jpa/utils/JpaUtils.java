/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jpa.utils;

import com.cxj.jpa.model.EmbeddableMeta;
import com.cxj.jpa.model.EntityMeta;
import com.cxj.jpa.model.ManagedMeta;
import com.cxj.utility.CollectionHelper;
import com.cxj.utility.QualifiedNameHelper;
import com.cxj.utility.TypeUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.persistence.criteria.Path;
import javax.persistence.metamodel.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

/**
 *
 * @author Administrator
 */
@SuppressWarnings({"WeakerAccess", "unused", "SameParameterValue"})
public class JpaUtils {

    @Nonnull
    public static String getTableName(@Nonnull EntityType type) {
        Class javaType = type.getJavaType();
        Table table = (Table) javaType.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            return table.name();
        }
        return type.getName();
    }

    public static String getName(@Nonnull ManagedType type) {
        if (type instanceof EntityType) {
            return ((EntityType) type).getName();
        } else {
            return type.getJavaType().getName();
        }
    }

    public static String getMappedBy(@Nonnull Attribute attribute) {
        Attribute.PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
        if (attributeType == Attribute.PersistentAttributeType.MANY_TO_MANY) {
            ManyToMany manyToMany = getAnnotation(attribute, ManyToMany.class);
            if (manyToMany == null) {
                throw new IllegalStateException("Unable to find manyToMany annotation of attribute " + attribute.getName() + " in " + getName(attribute.getDeclaringType()) + "! Perhaps orm.xml used instead of annotation.");
            }
            if (!manyToMany.mappedBy().isEmpty()) {
                return manyToMany.mappedBy();
            } else {
                return null;
            }
        }
        if (attributeType == Attribute.PersistentAttributeType.ONE_TO_MANY) {
            OneToMany oneToMany = getAnnotation(attribute, OneToMany.class);
            if (oneToMany == null) {
                throw new IllegalStateException("Unable to find oneToMany annotation of attribute" + attribute.getName() + " in " + getName(attribute.getDeclaringType()) + "! Perhaps orm.xml used instead of annotation.");
            }
            if (!oneToMany.mappedBy().isEmpty()) {
                return oneToMany.mappedBy();
            } else {
                return null;
            }
        }
        if (attributeType == Attribute.PersistentAttributeType.ONE_TO_ONE) {
            OneToOne oneToOne = getAnnotation(attribute, OneToOne.class);
            if (oneToOne == null) {
                throw new IllegalStateException("Unable to find oneToOne annotation of attribute" + attribute.getName() + " in " + getName(attribute.getDeclaringType()) + "! Perhaps orm.xml used instead of annotation.");
            }
            if (!oneToOne.mappedBy().isEmpty()) {
                return oneToOne.mappedBy();
            } else {
                return null;
            }
        }
        return null;
    }

    private static String findAssociationPropertyByMappedBy(ManagedType type, @Nonnull String property, @Nullable Attribute.PersistentAttributeType targetAttributeType) {
        //noinspection unchecked
        Set<Attribute> attributes = type.getAttributes();
        for (Attribute a : attributes) {
            if (targetAttributeType != null && !Objects.equals(a.getPersistentAttributeType(), targetAttributeType)) {
                continue;
            }
            if (a.isAssociation() && Objects.equals(getMappedBy(a), property)) {
                return a.getName();
            } else if (a.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
                String find = findAssociationPropertyByMappedBy((ManagedType) toType(a), property, targetAttributeType);
                if (find != null) {
                    return a.getName() + "." + find;
                }
            }
        }
        return null;
    }

    private static Attribute findAssociationAttributeByMappedBy(ManagedType type, @Nonnull String property, @Nullable Attribute.PersistentAttributeType targetAttributeType) {
        //noinspection unchecked
        Set<Attribute> attributes = type.getAttributes();
        for (Attribute a : attributes) {
            if (targetAttributeType != null && !Objects.equals(a.getPersistentAttributeType(), targetAttributeType)) {
                continue;
            }
            if (a.isAssociation() && Objects.equals(getMappedBy(a), property)) {
                return a;
            } else if (a.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED) {
                Attribute find = findAssociationAttributeByMappedBy((ManagedType) toType(a), property, targetAttributeType);
                if (find != null) {
                    return find;
                }
            }
        }
        return null;
    }

    public static boolean isAssociationPropertyBelongsToOwner(@Nonnull Attribute attribute) {
        if (!attribute.isAssociation()) {
            throw new IllegalArgumentException("The property " + attribute + " of the entity " + getName(attribute.getDeclaringType()) + " is not a association.");
        }
        String mappedBy = getMappedBy(attribute);
        return mappedBy == null;
    }

    @Nonnull
    private static Attribute checkAttribute(@Nonnull EntityType type,  @Nullable Attribute attribute, @Nonnull String property) {
        if (attribute == null) {
            attribute = getAttribute(type, property, true, true);
        }
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid path: " + property + " of entity " + type.getName() + ".");
        }
        return attribute;
    }

    private static Attribute checkAssociationAttribute(@Nonnull EntityType type,  @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAttribute(type, attribute, property);
        if (!attribute.isAssociation()) {
            throw new IllegalArgumentException("The property " + property + " of the entity " + getName(type) + " is not a association.");
        }
        return attribute;
    }

    private static Attribute checkAssociationOrElementCollectionAttribute(@Nonnull EntityType type,  @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAttribute(type, attribute, property);
        if (!attribute.isAssociation() && !attribute.isCollection()) {
            throw new IllegalArgumentException("The property " + property + " of the entity " + getName(type) + " is not a association or element collection.");
        }
        return attribute;
    }

    @Nonnull
    public static String getAssociationPropertyOwnerSide(@Nonnull EntityType type, @Nonnull String property) {
        return getAssociationPropertyOwnerSide(type, null, property);
    }

    @Nonnull
    private static String getAssociationPropertyOwnerSide(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAssociationOrElementCollectionAttribute(type, attribute, property);
        String mappedBy = getMappedBy(attribute);
        if (mappedBy != null) {
            return mappedBy;
        } else {
            return property;
        }
    }

    @Nonnull
    public static Attribute getAssociationAttributeOwnerSide(@Nonnull EntityType type, @Nonnull String property) {
        return getAssociationAttributeOwnerSide(type, null, property);
    }

    @Nonnull
    private static Attribute getAssociationAttributeOwnerSide(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAssociationOrElementCollectionAttribute(type, attribute, property);
        String mappedBy = getMappedBy(attribute);
        if (mappedBy != null) {
            return getAttribute(getAssociationPropertyEntityType(type, attribute, property), mappedBy, true, true);
        } else {
            return attribute;
        }
    }

    @Nonnull
    public static Attribute.PersistentAttributeType getAnotherSidePersistentAttributeType(@Nonnull Attribute attribute) {
        if (isManyToMany(attribute)) {
            return Attribute.PersistentAttributeType.MANY_TO_MANY;
        } else if (isOneToMany(attribute)) {
            return Attribute.PersistentAttributeType.MANY_TO_ONE;
        } else if (isOneToOne(attribute)) {
            return Attribute.PersistentAttributeType.ONE_TO_ONE;
        } else if (isManyToOne(attribute)) {
            return Attribute.PersistentAttributeType.ONE_TO_MANY;
        } else {
            throw new IllegalStateException("This is impossible! Invalid association attribute type: " + attribute.getPersistentAttributeType());
        }
    }

    @Nullable
    public static String getAssociationPropertyAnotherSide(@Nonnull EntityType type, @Nonnull String property) {
        return getAssociationPropertyAnotherSide(type, null, property);
    }

    @Nullable
    private static String getAssociationPropertyAnotherSide(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAssociationOrElementCollectionAttribute(type, attribute, property);
        if (isElementCollection(attribute)) {
            return null;
        }
        Attribute.PersistentAttributeType targetAttributeType = getAnotherSidePersistentAttributeType(attribute);
        if (isManyToOne(attribute)) {
            return null;
        }
        return findAssociationPropertyByMappedBy((ManagedType) toType(attribute), property, targetAttributeType);
    }

    @Nullable
    public static Attribute getAssociationAttributeAnotherSide(@Nonnull EntityType type, @Nonnull String property) {
        return getAssociationAttributeAnotherSide(type, null, property);
    }

    @Nullable
    private static Attribute getAssociationAttributeAnotherSide(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAssociationOrElementCollectionAttribute(type, attribute, property);
        if (isElementCollection(attribute)) {
            return null;
        }
        Attribute.PersistentAttributeType targetAttributeType = getAnotherSidePersistentAttributeType(attribute);
        if (isManyToOne(attribute)) {
            return null;
        }
        return findAssociationAttributeByMappedBy((ManagedType) toType(attribute), property, targetAttributeType);
    }

    @Nonnull
    public static EntityType getAssociationPropertyEntityType(@Nonnull EntityType type, @Nonnull String property) {
        return getAssociationPropertyEntityType(type, null, property);
    }

    @Nonnull
    public static EntityType getAssociationPropertyEntityType(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        attribute = checkAssociationAttribute(type, attribute, property);
        return (EntityType) toType(attribute);
    }

    public static boolean isManyToMany(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_MANY;
    }

    public static boolean isManyToOne(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.MANY_TO_ONE;
    }

    public static boolean isOneToMany(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_MANY;
    }

    public static boolean isOneToOne(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ONE_TO_ONE;
    }

    public static boolean isElementCollection(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.ELEMENT_COLLECTION;
    }

    public static boolean isEmbeddable(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.EMBEDDED;
    }

    public static boolean isBasic(@Nonnull Attribute attribute) {
        return attribute.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC;
    }

    public static boolean hasAssociationTable(@Nonnull EntityType type, @Nonnull String property) {
        return hasAssociationTable(type, null, property);
    }

    private static boolean hasAssociationTable(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        if (attribute == null) {
            attribute = getAttribute(type, property, true, true);
        }
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid path: " + property + " of entity " + type.getName() + ".");
        }
        JoinTable joinTable = getJoinTableAnnotation(type, attribute, property);
        return joinTable != null || isManyToMany(attribute) || isOneToMany(attribute) || isElementCollection(attribute);
    }

    @Nullable
    public static JoinTable getJoinTableAnnotation(@Nonnull EntityType type, @Nonnull String property) {
        return getJoinTableAnnotation(type, null, property);
    }

    @Nullable
    private static JoinTable getJoinTableAnnotation(@Nonnull EntityType type, @Nullable Attribute attribute, @Nonnull String property) {
        if (attribute == null) {
            attribute = getAttribute(type, property, true, true);
        }
        if (attribute == null) {
            throw new IllegalArgumentException("Invalid path: " + property + " of entity " + type.getName() + ".");
        }
        if (!attribute.isAssociation()) {
            throw new IllegalArgumentException("The property " + property + " of the entity " + getName(type) + " is not a association.");
        }
        AssociationOverride override = getAssociationOverride(type, property);
        if (override != null && !override.joinTable().name().isEmpty()) {
            return override.joinTable();
        }
        JoinTable joinTable = getAnnotation(attribute, JoinTable.class);
        if (joinTable != null && !joinTable.name().isEmpty()) {
            return joinTable;
        }
        return null;
    }

    @Nonnull
    public static String getAssociationTableName(@Nonnull EntityType type, @Nonnull String property) {
        Attribute attribute = checkAttribute(type, null, property);
        if (!hasAssociationTable(type, attribute, property)) {
            throw new IllegalArgumentException("The property " + property + " of the entity " + getName(type) + " has not a association table.");
        }
        if (attribute.isAssociation()) {
            JoinTable joinTable = getJoinTableAnnotation(type, attribute, property);
            if (joinTable != null) {
                return joinTable.name();
            }
            EntityType associationPropertyEntityType = getAssociationPropertyEntityType(type, property);
            if (isAssociationPropertyBelongsToOwner(attribute)) {
                return type.getName() + "_" + associationPropertyEntityType.getName();
            } else {
                return associationPropertyEntityType.getName() + "_" + type.getName();
            }
        } else if (attribute.isCollection()) {
            CollectionTable collectionTable = getAnnotation(attribute, CollectionTable.class);
            if (collectionTable != null && !collectionTable.name().isEmpty()) {
                return collectionTable.name();
            }
            JoinTable joinTable = getAnnotation(attribute, JoinTable.class);
            if (joinTable != null && !joinTable.name().isEmpty()) {
                return joinTable.name();
            }
            return type.getName() + "_" + property;
        } else {
            throw new IllegalArgumentException("The property " + property + " of the entity " + getName(type) + " is not a association or element collection.");
        }
    }

    @Nonnull
    public static String[] getAssociationOwnerColumnNames(@Nonnull EntityType type, @Nonnull String property) {
        Attribute attribute = checkAttribute(type, null, property);
        Attribute attributeAnotherSide = getAssociationAttributeAnotherSide(type, attribute, property);
        // 单向
        if (attributeAnotherSide == null) {
            return Arrays.stream(getIdColumns(type)).map(c -> type.getName() + "_" + c).toArray(String[]::new);
        } else {
            return Arrays.stream(getIdColumns(type)).map(c -> attribute.getName() + "_" + c).toArray(String[]::new);
        }
    }

    @Nonnull
    public static String[] getColumnNames(@Nonnull EntityType type, @Nonnull String property) {
        Attribute attribute = getAttribute(type, property, true, true);
        Attribute.PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
        if (attribute.isAssociation() || attribute.isCollection()) {
            AssociationOverride override = getAssociationOverride(type, property);
            if (override != null && override.joinColumns().length > 0) {
                return Arrays.stream(override.joinColumns()).map(JoinColumn::name).toArray(String[]::new);
            }
            JoinColumn joinColumn = getAnnotation(attribute, JoinColumn.class);
            if (joinColumn != null && !joinColumn.name().isEmpty()) {
                return new String[] {joinColumn.name()};
            }
            JoinColumns joinColumns = getAnnotation(attribute, JoinColumns.class);
            if (joinColumns != null && joinColumns.value().length > 0) {
                return Arrays.stream(joinColumns.value()).map(JoinColumn::name).toArray(String[]::new);
            }
            if (isElementCollection(attribute)) {
                Type theType = toType(attribute);
                if (theType.getPersistenceType() == Type.PersistenceType.EMBEDDABLE) {
                    return Arrays.stream(getFlattenSingularAttribute(type, false))
                            .map(Attribute::getName).toArray(String[]::new);
                } else {
                    return new String[] {attribute.getName()};
                }
            } else {
                EntityType targetType = (EntityType) toType(attribute);
                return Arrays.stream(getIdColumns(targetType)).map(c -> attribute.getName() + "_" + c).toArray(String[]::new);
            }
        } else if (attributeType == Attribute.PersistentAttributeType.BASIC || attributeType == Attribute.PersistentAttributeType.EMBEDDED) {
            AttributeOverride override = getAttributeOverride(type, property);
            if (override != null && !override.column().name().isEmpty()) {
                return new String[] {override.column().name()};
            }
            Column column = getAnnotation(attribute, Column.class);
            if (column != null && !column.name().isEmpty()) {
                return new String[] {column.name()};
            }
            return new String[] {attribute.getName()};
        } else {
            return new String[] {};
        }
    }

    @SuppressWarnings("unchecked")
    public static SingularAttribute[] getIdAttributes(@Nonnull EntityType type) {
        if (type.hasSingleIdAttribute()) {
            return new SingularAttribute[] {type.getId(type.getIdType().getJavaType())};
        } else {
            try {
                Set<SingularAttribute> idClassAttributes = type.getIdClassAttributes();
                return idClassAttributes.toArray(new SingularAttribute[0]);
            } catch (IllegalArgumentException e) {
                Set<SingularAttribute> singularAttributes = type.getSingularAttributes();
                return singularAttributes.stream().filter(SingularAttribute::isId).toArray(SingularAttribute[]::new);
            }
        }
    }

    public static String[] getIdColumns(@Nonnull EntityType type) {
        String[] properties = getFlattenIdProperties(type);
        return Arrays.stream(properties).flatMap(p -> Arrays.stream(getColumnNames(type, p))).toArray(String[]::new);
    }

    @SuppressWarnings("unchecked")
    public static String[] getIdProperties(@Nonnull EntityType type) {
        if (type.hasSingleIdAttribute()) {
            return new String[] {type.getId(type.getIdType().getJavaType()).getName()};
        } else {
            try {
                Set<SingularAttribute> idClassAttributes = type.getIdClassAttributes();
                return idClassAttributes.stream().map(SingularAttribute::getName).toArray(String[]::new);
            } catch (IllegalArgumentException e) {
                Set<SingularAttribute> singularAttributes = type.getSingularAttributes();
                return singularAttributes.stream().filter(SingularAttribute::isId).map(SingularAttribute::getName).toArray(String[]::new);
            }
        }
    }

    public static Class getIdType(@Nonnull IdentifiableType type) {
        Type idType = type.getIdType();
        if (idType == null) {
            IdClass idClass = getAnnotation(type, IdClass.class);
            if (idClass != null) {
                return idClass.value();
            }
            IdentifiableType supertype = type.getSupertype();
            if (supertype != null) {
                return getIdType(supertype);
            } else {
                return null;
            }
        } else {
            return idType.getJavaType();
        }
    }

    @Nullable
    public static Object createIdFromFlattenIds(@Nonnull EntityType type, Object... ids) {
        String[] idProperties = getFlattenIdProperties(type);
        if (ids.length != idProperties.length) {
            throw new IllegalArgumentException("The number of ids is wrong. except " + idProperties.length + ", get " + ids.length);
        }
        Type idType = type.getIdType();
        if (type.hasSingleIdAttribute()) {
            if (idType == null) {
                throw new IllegalStateException("This is impossible! The single id attribute should have a id type.");
            }
            if (idType.getPersistenceType() == Type.PersistenceType.BASIC) {
                if (ids.length != 1) {
                    throw new IllegalArgumentException("The number of ids is wrong. except " + 1 + ", get " + ids.length);
                }
                //noinspection unchecked
                return TypeUtils.toType(ids[0], idType.getJavaType());
            } else if (idType.getPersistenceType() == Type.PersistenceType.EMBEDDABLE){
                Map<String, Object> map = new HashMap<>();
                for (int i = 0; i < ids.length; i++) {
                    map.put(QualifiedNameHelper.getLeftPath(idProperties[i], 1), ids[i]);
                }
                //noinspection unchecked
                return TypeUtils.toObjectOrNull(map, idType.getJavaType());
            } else {
                throw new IllegalArgumentException("Unsupported id type: " + idType.getPersistenceType() + " for entity " + type.getName() + ".");
            }
        }
        Class idClass = getIdType(type);
        if (idClass != null) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < ids.length; i++) {
                map.put(idProperties[i], ids[i]);
            }
            //noinspection unchecked
            return TypeUtils.toObjectOrNull(map, idClass);
        } else {
            throw new IllegalStateException("Unable to create composite id of entity " + type.getName() + " without id class.");
        }
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static String[] getFlattenIdProperties(@Nonnull ManagedType type) {
        Set<SingularAttribute> singularAttributes = type.getSingularAttributes();
        return singularAttributes.stream().filter(SingularAttribute::isId).flatMap(a -> {
            Attribute.PersistentAttributeType attributeType = a.getPersistentAttributeType();
            if (attributeType == Attribute.PersistentAttributeType.BASIC) {
                return Stream.of(a.getName());
            } else if (attributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                return Arrays.stream(getFlattenSingularProperties((EmbeddableType) a.getType(), false))
                        .map(p -> a.getName() + "." + p);
            } else {
                throw new IllegalStateException("This is impossible! Impossible persistent attribute type + " + attributeType + " here. Manage type: " + type.getJavaType().getName() + ", attribute: " + a.getName() + ".");
            }
        }).toArray(String[]::new);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static Attribute[] getFlattenSingularAttribute(@Nonnull ManagedType type, boolean includeRelation) {
        Set<SingularAttribute> singularAttributes = type.getSingularAttributes();
        return singularAttributes.stream().flatMap(a -> {
            if (isBasic(a)) {
                return Stream.of(a);
            } else if (isEmbeddable(a)) {
                return Arrays.stream(getFlattenSingularAttribute((EmbeddableType) a.getType(), includeRelation));
            } else if (includeRelation && (isManyToOne(a) || isOneToOne(a))) {
                return Arrays.stream(getFlattenSingularAttribute((EntityType) a.getType(), true));
            } else {
                throw new IllegalStateException("This is impossible! Impossible persistent attribute type + " + a.getPersistentAttributeType() + " here. Manage type: " + type.getJavaType().getName() + ", attribute: " + a.getName() + ".");
            }
        }).toArray(Attribute[]::new);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public static String[] getFlattenSingularProperties(@Nonnull ManagedType type, boolean includeRelation) {
        Set<SingularAttribute> singularAttributes = type.getSingularAttributes();
        return singularAttributes.stream().flatMap(a -> {
            Attribute.PersistentAttributeType attributeType = a.getPersistentAttributeType();
            if (attributeType == Attribute.PersistentAttributeType.BASIC) {
                return Stream.of(a.getName());
            } else if (attributeType == Attribute.PersistentAttributeType.EMBEDDED) {
                return Arrays.stream(getFlattenSingularProperties((EmbeddableType) a.getType(), includeRelation))
                        .map(p -> a.getName() + "." + p);
            } else if (includeRelation && (attributeType == Attribute.PersistentAttributeType.MANY_TO_ONE || attributeType == Attribute.PersistentAttributeType.ONE_TO_ONE)) {
                return Arrays.stream(getFlattenSingularProperties((EntityType) a.getType(), true))
                        .map(p -> a.getName() + "." + p);
            } else {
                throw new IllegalStateException("This is impossible! Impossible persistent attribute type + " + attributeType + " here. Manage type: " + type.getJavaType().getName() + ", attribute: " + a.getName() + ".");
            }
        }).toArray(String[]::new);
    }

    public static String[] getOwnerCollectionProperties(@Nonnull ManagedType type) {
        //noinspection unchecked
        Set<Attribute> attributes = type.getAttributes();
        return attributes.stream().flatMap(a -> {
            if (a instanceof PluralAttribute) {
                if (isElementCollection(a) || isAssociationPropertyBelongsToOwner(a)) {
                    return Stream.of(a.getName());
                } else {
                    return null;
                }
            } else if (isEmbeddable(a)) {
                return Arrays.stream(getOwnerCollectionProperties((EmbeddableType) toType(a))).map(p -> a.getName() + "." + p);
            } else {
                return null;
            }
        }).toArray(String[]::new);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(@Nonnull ManagedType type, @Nonnull Class<A> annotationType) {
        return (A) type.getJavaType().getAnnotation(annotationType);
    }

    @Nullable
    public static <A extends Annotation> A getAnnotation(@Nonnull Attribute attribute, @Nonnull Class<A> annotationType) {
        Member member = attribute.getJavaMember();
        if (member instanceof Field) {
            return ((Field) member).getAnnotation(annotationType);
        } else if (member instanceof Method) {
            return ((Method) member).getAnnotation(annotationType);
        } else {
            throw new RuntimeException("This is impossible! The member's type is " + member.getClass().getName() + ".");
        }
    }
    
    public static <T> T getReference(EntityManager em, Class<T> type, Object id) {
        try {
            return em.getReference(type, id);
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    public static <T> String getSimpleName(Bindable<T> bindable) {
        switch (bindable.getBindableType()) {
            case ENTITY_TYPE:
                return ((EntityType<T>) bindable).getName();
            case PLURAL_ATTRIBUTE:
            case SINGULAR_ATTRIBUTE:
                //noinspection unchecked
                return ((Attribute<?, T>) bindable).getName();
            default:
                return null;
        }
    }

    /**
     * 将bindable转为Type<br>
     * 若bindable为实体或单值属性，则返回表示其本身的Type，若为多值属性，则返回表示其元素的Type
     *
     * @param <T> 实体或属性的类型
     * @param bindable 需要转换的对象
     * @return 转换结果
     */
    public static <T> Type<T> toType(Bindable<T> bindable) {
        switch (bindable.getBindableType()) {
            case ENTITY_TYPE:
                return (EntityType<T>) bindable;
            case PLURAL_ATTRIBUTE:
                return ((PluralAttribute<?, ?, T>) bindable).getElementType();
            case SINGULAR_ATTRIBUTE:
                return ((SingularAttribute<?, T>) bindable).getType();
            default:
                return null;
        }
    }

    public static Type toType(Attribute attribute) {
        return toType(toBindable(attribute));
    }

    public static boolean isEntity(Bindable bindable) {
        if (bindable.getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
            return true;
        } else if (bindable.getBindableType() == Bindable.BindableType.SINGULAR_ATTRIBUTE) {
            SingularAttribute attribute = (SingularAttribute) bindable;
            return attribute.getType() instanceof EntityType;
        } else if (bindable.getBindableType() == Bindable.BindableType.PLURAL_ATTRIBUTE) {
            PluralAttribute attribute = (PluralAttribute) bindable;
            return attribute.getElementType() instanceof EntityType;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static boolean isEntity(Metamodel model, String name) {
        return !StringUtils.isEmpty(name) && model.getEntities().stream().anyMatch(e -> name.equals(e.getName()));
    }

    public static boolean isEntity(Metamodel model, Class eType) {
        return eType != null && model.getEntities().stream().anyMatch(e -> Objects.equals(eType, e.getJavaType()));
    }

    public static boolean isEmbeddable(Metamodel model, Class eType) {
        return eType != null && model.getEmbeddables().stream().anyMatch(e -> Objects.equals(eType, e.getJavaType()));
    }

    public static boolean isManaged(Metamodel model, Class type) {
        return type != null && model.getManagedTypes().stream().anyMatch(e -> Objects.equals(type, e.getJavaType()));
    }

    public static String entityName(Metamodel model, Class eType) {
        if (eType == null) {
            return null;
        }
        return model.getEntities().stream().filter(e -> Objects.equals(eType, e.getJavaType())).map(EntityType::getName).findFirst().orElse(null);
    }

    public static Class entityClass(Metamodel model, Object entity) {
        if (entity == null) {
            return null;
        }
        Class clazz = entity.getClass();
        if (isEntity(model, clazz)) {
            return clazz;
        }
        if (clazz.getSuperclass() != null && !Objects.equals(clazz, Object.class)) {
            if (isEntity(model, clazz.getSuperclass())) {
                return clazz.getSuperclass();
            }
        }
        return null;
    }

    public static Class embeddableClass(Metamodel model, Object embeddable) {
        if (embeddable == null) {
            return null;
        }
        Class clazz = embeddable.getClass();
        if (isEmbeddable(model, clazz)) {
            return clazz;
        }
        if (clazz.getSuperclass() != null && !Objects.equals(clazz, Object.class)) {
            if (isEmbeddable(model, clazz.getSuperclass())) {
                return clazz.getSuperclass();
            }
        }
        return null;
    }

    public static Class managedClass(Metamodel model, Object managed) {
        if (managed == null) {
            return null;
        }
        Class clazz = managed.getClass();
        if (isManaged(model, clazz)) {
            return clazz;
        }
        if (clazz.getSuperclass() != null && !Objects.equals(clazz, Object.class)) {
            if (isManaged(model, clazz.getSuperclass())) {
                return clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 返回代表当前路径的父实体路径。比如Employee.Address.lane。
     * 其中Employee是实体，Address是内嵌类，lane是Address的属性。
     * 那么Employee.Address.lane的父实体应为Employee，而非Employee.Address。
     * 特别的对于1级的路径，例如Employee，则返回null。
     * 对于2级的路径，比如Employee.Address，直接返回其父路径Employee。
     *
     * @param model 元模型
     * @param path 路径
     * @return 父实体路径
     */
    public static String parentEntity(Metamodel model, String path) {
        if (QualifiedNameHelper.isOneLayerPath(path)) {
            return null;
        }
        String parent = QualifiedNameHelper.getParentPath(path);
        if (QualifiedNameHelper.isOneLayerPath(parent)) {
            return parent;
        } else {
            Bindable bindable = toBindable(model, parent);
            if (isEntity(bindable)) {
                return parent;
            } else {
                return parentEntity(model, parent);
            }
        }
    }

    public static EntityType parentEntityType(Metamodel model, String path) {
        if (QualifiedNameHelper.isOneLayerPath(path)) {
            return null;
        }
        String parent = QualifiedNameHelper.getParentPath(path);
        if (QualifiedNameHelper.isOneLayerPath(parent)) {
            return entity(model, parent);
        } else {
            Type type = toType(toBindable(model, parent));
            if (type instanceof EntityType) {
                return (EntityType) type;
            } else {
                return parentEntityType(model, parent);
            }
        }
    }

    /**
     * 判断attribute是否只能是路径的叶子，比如当attribute对应的java类型是基本类型时。
     *
     * @param attribute 需要判断的属性
     * @return 判断结果
     */
    public static boolean isLeafAttribute(Attribute attribute) {
        return toType(attribute).getPersistenceType() == Type.PersistenceType.BASIC;
    }

    /**
     * 解析Path返回对应元模型。
     *
     * @param model 元模型
     * @param managed ManagedType的类型
     * @param property 属性路径 不为空字符串
     * @return 解析结果
     */
    private static Bindable<?> parsePath(Metamodel model, Class<?> managed, String property) {
        ManagedType<?> type = model.managedType(managed);
        String root = QualifiedNameHelper.getRootPath(property);
        String subProperty = QualifiedNameHelper.getLeftPath(property, root);
        Attribute<?, ?> attribute = type.getAttribute(root);
        if (!subProperty.isEmpty()) {
            return parsePath(model, toBindable(attribute).getBindableJavaType(), subProperty);
        } else {
            return toBindable(attribute);
        }
    }

    @Nonnull
    private static IllegalArgumentException parseException(ManagedType type, String property, @Nonnull IllegalArgumentException e) {
        String msg = e.getMessage();
        if (msg != null && msg.length() > 3) {
            String err = msg.substring(0, 3);
            String p = msg.substring(3);
            String typeName = type.getJavaType().getName();
            if ("nc:".equals(err)) {
                return new IllegalArgumentException("The path " + p + " of " + typeName + " is not a singular attribute. Input path: " + property + ".");
            } else if ("oe:".equals(err)) {
                return new IllegalArgumentException("The path " + p + " of  " + typeName + " must not contain attribute except embeddable type. Input path: " + property + ".");
            } else if ("bd:".equals(err)) {
                return new IllegalArgumentException("Invalid path " + p + " of  " + typeName + ". Input path: " + property + ".");
            } else if ("mp:".equals(err)) {
                return new IllegalArgumentException("The path " + p + " of  " + typeName + " must not contain any map attribute. Input path: " + property + ".");
            }
        }
        return e;
    }

    public static Attribute getAttribute(ManagedType type, String property, boolean onlyEmbeddable, boolean noCross) {
        try {
            return _getAttribute(type, property, onlyEmbeddable, noCross);
        } catch (IllegalArgumentException e) {
            throw parseException(type, property, e);
        }
    }

    private static Attribute _getAttribute(ManagedType type, String property, boolean onlyEmbeddable, boolean noCross) {
        String root = QualifiedNameHelper.getRootPath(property);
        String subProperty = QualifiedNameHelper.getLeftPath(property, root);
        Attribute<?, ?> attribute = type.getAttribute(root);
        if (!subProperty.isEmpty()) {
            if (noCross && !(attribute instanceof SingularAttribute)) {
                throw new IllegalArgumentException("nc:" + root);
            }
            try {
                if (attribute instanceof  SingularAttribute) {
                    SingularAttribute singularAttribute = (SingularAttribute) attribute;
                    Type attributeType = singularAttribute.getType();
                    if (attributeType instanceof ManagedType) {
                        if (onlyEmbeddable && !(attributeType instanceof EmbeddableType)) {
                            throw new IllegalArgumentException("oe:" + root);
                        }
                        return _getAttribute((ManagedType) attributeType, subProperty, onlyEmbeddable, noCross);
                    } else {
                        throw new IllegalArgumentException("bd:" + root);
                    }
                } else {
                    PluralAttribute pluralAttribute = (PluralAttribute) attribute;
                    if (pluralAttribute instanceof MapAttribute) {
                        throw new IllegalArgumentException("mp:" + root);
                    }
                    Type attributeType = pluralAttribute.getElementType();
                    if (attributeType instanceof ManagedType) {
                        if (onlyEmbeddable && !(attributeType instanceof EmbeddableType)) {
                            throw new IllegalArgumentException("oe:" + root);
                        }
                        return _getAttribute((ManagedType) attributeType, subProperty, onlyEmbeddable, false);
                    } else {
                        throw new IllegalArgumentException("bd:" + root);
                    }
                }
            } catch (IllegalArgumentException e) {
                String msg = e.getMessage();
                if (msg != null && msg.length() > 3) {
                    if (msg.startsWith("nc:") || msg.startsWith("oe:") || msg.startsWith("bd:") || msg.startsWith("mp:")) {
                        throw new IllegalArgumentException(msg.substring(0, 3) + root + msg.substring(3));
                    }
                }
                throw e;
            }
        } else {
            return attribute;
        }
    }

    public static AttributeOverride getAttributeOverride(ManagedType type, String property) {
        try {
            return _getAttributeOverride(type, property);
        } catch (IllegalArgumentException e) {
            throw parseException(type, property, e);
        }
    }

    private static AttributeOverride _getAttributeOverride(ManagedType type, String property) {
        AttributeOverride override = getAnnotation(type, AttributeOverride.class);
        if (override != null && Objects.equals(override.name(), property)) {
            return override;
        }
        AttributeOverrides overrides = getAnnotation(type, AttributeOverrides.class);
        if (overrides != null) {
            override = Arrays.stream(overrides.value())
                    .filter(o -> o != null && Objects.equals(o.name(), property))
                    .findFirst()
                    .orElse(null);
            if (override != null) {
                return override;
            }
        }
        String root = QualifiedNameHelper.getRootPath(property);
        String subProperty = QualifiedNameHelper.getLeftPath(property, root);
        Attribute<?, ?> attribute = type.getAttribute(root);
        if (!subProperty.isEmpty() && !(attribute instanceof SingularAttribute)) {
            throw new IllegalArgumentException("nc:" + root);
        }
        override = getAnnotation(attribute, AttributeOverride.class);
        if (override != null && Objects.equals(override.name(), subProperty)) {
            return override;
        }
        if (!subProperty.isEmpty()) {
            try {
                SingularAttribute singularAttribute = (SingularAttribute) attribute;
                Type attributeType = singularAttribute.getType();
                if (attributeType instanceof ManagedType) {
                    if (!(attributeType instanceof EmbeddableType)) {
                        throw new IllegalArgumentException("oe:" + root);
                    }
                    return _getAttributeOverride((ManagedType) attributeType, subProperty);
                } else {
                    throw new IllegalArgumentException("bd:" + root);
                }
            } catch (IllegalArgumentException e) {
                String msg = e.getMessage();
                if (msg != null && msg.length() > 3) {
                    if (msg.startsWith("nc:") || msg.startsWith("oe:") || msg.startsWith("bd:")) {
                        throw new IllegalArgumentException(msg.substring(0, 3) + root + msg.substring(3));
                    }
                }
                throw e;
            }
        } else {
            return null;
        }
    }

    public static AssociationOverride getAssociationOverride(ManagedType type, String property) {
        try {
            return _getAssociationOverride(type, property);
        } catch (IllegalArgumentException e) {
            throw parseException(type, property, e);
        }
    }

    private static AssociationOverride _getAssociationOverride(ManagedType type, String property) {
        AssociationOverride override = getAnnotation(type, AssociationOverride.class);
        if (override != null && Objects.equals(override.name(), property)) {
            return override;
        }
        AssociationOverrides overrides = getAnnotation(type, AssociationOverrides.class);
        if (overrides != null) {
            override = Arrays.stream(overrides.value())
                    .filter(o -> o != null && Objects.equals(o.name(), property))
                    .findFirst()
                    .orElse(null);
            if (override != null) {
                return override;
            }
        }
        String root = QualifiedNameHelper.getRootPath(property);
        String subProperty = QualifiedNameHelper.getLeftPath(property, root);
        Attribute<?, ?> attribute = type.getAttribute(root);
        if (!subProperty.isEmpty() && !(attribute instanceof SingularAttribute)) {
            throw new IllegalArgumentException("nc:" + root);
        }
        override = getAnnotation(attribute, AssociationOverride.class);
        if (override != null && Objects.equals(override.name(), subProperty)) {
            return override;
        }
        if (!subProperty.isEmpty()) {
            try {
                SingularAttribute singularAttribute = (SingularAttribute) attribute;
                Type attributeType = singularAttribute.getType();
                if (attributeType instanceof ManagedType) {
                    if (!(attributeType instanceof EmbeddableType)) {
                        throw new IllegalArgumentException("oe:" + root);
                    }
                    return _getAssociationOverride((ManagedType) attributeType, subProperty);
                } else {
                    throw new IllegalArgumentException("bd:" + root);
                }
            } catch (IllegalArgumentException e) {
                String msg = e.getMessage();
                if (msg != null && msg.length() > 3) {
                    if (msg.startsWith("nc:") || msg.startsWith("oe:") || msg.startsWith("bd:")) {
                        throw new IllegalArgumentException(msg.substring(0, 3) + root + msg.substring(3));
                    }
                }
                throw e;
            }
        } else {
            return null;
        }
    }

    public static Bindable<?> toBindable(Attribute<?, ?> attribute) {
        if (attribute instanceof SingularAttribute || attribute instanceof PluralAttribute) {
            return (Bindable<?>) attribute;
        }
        throw new ClassCastException();
    }

    public static Bindable<?> toBindable(Metamodel model, String path) {
        String root = QualifiedNameHelper.getRootPath(path);
        String property = QualifiedNameHelper.getLeftPath(path, root);
        if (property.isEmpty()) {
            return JpaUtils.entity(model, root);
        } else {
            return parsePath(model, JpaUtils.entity(model, root).getJavaType(), property);
        }
    }

    /**
     * 提取bindable的实体名称。如果bindable是属性，则返回空字符串，若bindable是实体，则返回实体名
     *
     * @param bindable 待提取目标
     * @return 实体名称
     */
    public static String extractEntity(Bindable bindable) {
        if (bindable.getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
            return ((EntityType) bindable).getName();
        } else {
            return "";
        }
    }

    /**
     * 提取bindable的属性名称。如果bindable是实体，则返回空字符串，若bindable是属性，则返回属性名
     *
     * @param bindable 待提取目标
     * @return 属性名称
     */
    public static String extractProperty(Bindable bindable) {
        if (bindable.getBindableType() != Bindable.BindableType.ENTITY_TYPE) {
            return ((Attribute) bindable).getName();
        } else {
            return "";
        }
    }

    public static boolean isPluralAttribute(Bindable bindable) {
        return bindable.getBindableType() == Bindable.BindableType.PLURAL_ATTRIBUTE;
    }

    /**
     * 计算路径的实际类型
     *
     * @param model 元信息
     * @param path 路径
     * @return 类型
     */
    public static Class<?> toClass(Metamodel model, String path) {
        return toBindable(model, path).getBindableJavaType();
    }

    /**
     * 根据实体名获取实体元模型， 当实体名不存在时抛IllegalArgumentException异常
     *
     * @param model 元模型
     * @param name 实体名
     * @return 实体元模型
     */
    public static EntityType<?> entity(Metamodel model, String name) {
        Set<EntityType<?>> types = model.getEntities();
        for (EntityType<?> type : types) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unrecognized entity name: " + name);
    }

    @SuppressWarnings("unchecked")
    private static List<String> getAttributeNames(ManagedType type, String prefix) {
        List<String> list = new ArrayList<>();
        Set<Attribute> attributes = type.getAttributes();
        attributes.forEach((attribute) -> {
            Type aType = toType(attribute);
            String aName;
            if (prefix.isEmpty()) {
                aName = attribute.getName();
            } else {
                aName = prefix + "." + attribute.getName();
            }
            list.add(aName);
            if (aType.getPersistenceType() == Type.PersistenceType.EMBEDDABLE) {
                list.addAll(getAttributeNames((ManagedType) aType, aName));
            }
        });
        return list;
    }

    /**
     * 获取某个实体类或内嵌类的所有属性名，若属性是内嵌类，则会继续递归下去
     *
     * @param type 实体对应元信息
     * @return 所有属性名
     */
    public static List<String> getAttributeNames(ManagedType type) {
        return getAttributeNames(type, "");
    }

    /**
     * 根据输入的实体类型集，得到在集合中不会冲突的属性名（即其在输入实体集中名称是唯一的）及其对应的实体类型
     *
     * @param model 元模型
     * @param classes 实体类型集（可重复）
     * @return 结果
     */
    public static Map<String, Class<?>> uniqueAttribute(Metamodel model, Collection<Class<?>> classes) {
        Map<String, Class<?>> attributeMap = new HashMap<>();
        classes.forEach((cls) -> {
            EntityType entity = model.entity(cls);
            //noinspection unchecked
            Set<Attribute<?, ?>> attributes = entity.getAttributes();
            attributes.forEach((attribute) -> {
                if (attributeMap.containsKey(attribute.getName())) {
                    attributeMap.replace(attribute.getName(), null);
                } else {
                    attributeMap.put(attribute.getName(), cls);
                }
            });
        });
        CollectionHelper.filterCollection(attributeMap.values(), Objects::nonNull);
        return attributeMap;
    }

    public static Path resolvePathFromStringPath(Path root, String path) {
        QualifiedNameHelper.QNameIterator iterator = QualifiedNameHelper.qNameIterator(path);
        Path _path = root;
        while (iterator.hasNext()) {
            String subPath = iterator.next();
            _path = _path.get(subPath);
        }
        return _path;
    }

    @SuppressWarnings("unchecked")
    public static <X> EntityMeta<X> resolveEntityMeta(EntityType<X> et) {
        return new EntityMeta(et);
    }

    @SuppressWarnings("unchecked")
    public static <X> EmbeddableMeta<X> resolveEmbeddableMeta(EmbeddableType<X> et) {
        return new EmbeddableMeta(et);
    }

    public static <X> ManagedMeta<X> resolveManagedMeta(ManagedType<X> mt) {
        if (mt instanceof EntityType) {
            return resolveEntityMeta((EntityType<X>) mt);
        }
        if (mt instanceof EmbeddableType) {
            return resolveManagedMeta(mt);
        }
        return null;
    }

    public static Object getPropertyValue(@Nonnull Object entity, @Nonnull Attribute attribute) {
        Member member = attribute.getJavaMember();
        if (member instanceof Field) {
            Field field = (Field) member;
            try {
                field.setAccessible(true);
                return field.get(entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (member instanceof Method) {
            Method method = (Method) member;
            try {
                method.setAccessible(true);
                return method.invoke(entity);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Unsupported member type: " + member.getClass().getName() + ".");
    }

    public static void setPropertyValue(@Nonnull Object entity, @Nonnull Attribute attribute, @Nullable Object value) {
        Member member = attribute.getJavaMember();
        if (member instanceof Field) {
            Field field = (Field) member;
            try {
                field.setAccessible(true);
                field.set(entity, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (member instanceof Method) {
            Method method = (Method) member;
            try {
                method.setAccessible(true);
                method.invoke(entity, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Unsupported member type: " + member.getClass().getName() + ".");
    }

    public static class QueryIterable<T> implements Iterable<T> {

        private EntityManager em;
        private Class<T> resultType;
        private QueryBox<T> dataQuery;
        private int batchSize;

        public QueryIterable(EntityManager em, Class<T> resultType) {
            this.em = em;
            this.resultType = resultType;
            this.batchSize = 3000;
        }

        public void reset() {
            dataQuery = null;
        }

        public QueryBox<T> withQuery(String query) {
            dataQuery = new QueryBox<>(null, query, resultType);
            return dataQuery;
        }

        public QueryBox<T> withNamedQuery(String name) {
            dataQuery = new QueryBox<>(name, null, resultType);
            return dataQuery;
        }

        @Override
        @Nonnull
        public Iterator<T> iterator() {
            return new QueryIterator<>(dataQuery);
        }

        @SuppressWarnings("unused")
        public class QueryBox<E> {

            private final String queryString;
            private final String queryName;
            private final Class<E> resultType;
            private List<QueryArgument> indexedArguments;
            private Map<String, QueryArgument> mappedArguments;

            QueryBox(String queryName, String queryString, Class<E> resultType) {
                this.queryName = queryName;
                this.queryString = queryString;
                this.resultType = resultType;
                this.indexedArguments = new ArrayList<>();
                this.mappedArguments = new HashMap<>();
            }

            public <A> QueryBox<E> withArgument(A arg) {
                indexedArguments.add(new QueryArgument(arg));
                return this;
            }

            public <A> QueryBox<E> withArgument(A date, TemporalType type) {
                indexedArguments.add(new QueryArgument(date, type));
                return this;
            }

            public <A> QueryBox<E> withArgument(String name, A arg) {
                mappedArguments.put(name, new QueryArgument(arg));
                return this;
            }

            public <A> QueryBox<E> withArgument(String name, A date, TemporalType type) {
                mappedArguments.put(name, new QueryArgument(date, type));
                return this;
            }

            public QueryIterable<T> buildQuery() {
                return QueryIterable.this;
            }

            private TypedQuery<E> generateQuery(EntityManager em) {
                TypedQuery<E> query;
                if (queryName != null) {
                    query = em.createNamedQuery(queryName, resultType);
                } else {
                    query = em.createQuery(queryString, resultType);
                }
                int argNum = indexedArguments.size();
                for (int i = 0; i < argNum; i++) {
                    QueryArgument argument = indexedArguments.get(i);
                    if (argument.getTemporalType() == null) {
                        query.setParameter(i, argument.getArgument());
                    } else {
                        if (argument.getArgument() instanceof Calendar) {
                            Calendar c = (Calendar) argument.getArgument();
                            query.setParameter(i, c, argument.getTemporalType());
                        } else if (argument.getArgument() instanceof Date) {
                            Date d = (Date) argument.getArgument();
                            query.setParameter(i, d, argument.getTemporalType());
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
                for (Map.Entry<String, QueryArgument> entry : mappedArguments.entrySet()) {
                    QueryArgument argument = entry.getValue();
                    if (argument.getTemporalType() == null) {
                        query.setParameter(entry.getKey(), argument.getArgument());
                    } else {
                        if (argument.getArgument() instanceof Calendar) {
                            Calendar c = (Calendar) argument.getArgument();
                            query.setParameter(entry.getKey(), c, argument.getTemporalType());
                        } else if (argument.getArgument() instanceof Date) {
                            Date d = (Date) argument.getArgument();
                            query.setParameter(entry.getKey(), d, argument.getTemporalType());
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
                return query;
            }
        }

        public static class QueryArgument {

            private Object argument;
            private TemporalType temporalType;

            public QueryArgument(Object argument) {
                this(argument, null);
            }

            public QueryArgument(Object argument, TemporalType temporalType) {
                this.argument = argument;
                this.temporalType = temporalType;
            }

            public Object getArgument() {
                return argument;
            }

            public TemporalType getTemporalType() {
                return temporalType;
            }

        }

        public class QueryIterator<E> implements Iterator<E> {

            private final QueryBox<E> queryBox;
            private int idx;
            private List<E> currentCache;
            private List<E> nextCache;

            QueryIterator(QueryBox<E> queryBox) {
                this.queryBox = queryBox;
                this.idx = -1;
            }

            private List<E> cachedQuery(List<E> cache, int idxBatch) {
                if (cache == null) {
                    TypedQuery<E> query = queryBox.generateQuery(em);
                    query.setFirstResult(idxBatch * batchSize);
                    query.setMaxResults(batchSize);
                    cache = query.getResultList();
                }
                return cache;
            }

            private void readyCache() {
                if (currentCache == null) {
                    if (idx >= 0) {
                        int idxBatch = idx / batchSize;
                        if (idxBatch == (idx + 1) / batchSize) {
                            if (nextCache != null) {
                                currentCache = nextCache;
                            }
                        }
                        currentCache = cachedQuery(currentCache, idxBatch);
                    }
                }
                if (nextCache == null) {
                    if (idx + 1 >= 0) {
                        int idxBatch = (idx + 1) / batchSize;
                        if (idxBatch == idx / batchSize) {
                            if (currentCache != null) {
                                nextCache = currentCache;
                            }
                        }
                        nextCache = cachedQuery(nextCache, idxBatch);
                    }
                }
            }

            private E currentData() {
                readyCache();
                if (idx < 0) {
                    return null;
                }
                if (currentCache != null) {
                    int localIdx = idx % batchSize;
                    if (currentCache.size() > localIdx) {
                        return currentCache.get(localIdx);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }

            private boolean hasNextData() {
                readyCache();
                int localIdx = (idx + 1) % batchSize;
                return idx + 1 >= 0 && nextCache != null && nextCache.size() > localIdx;
            }

            private void detachCache(List<E> target, List<E> toCompare) {
                if (target != null) {
                    if (toCompare != target) {
                        em.flush();
                        for (E data : target) {
                            em.detach(data);
                        }
                    }
                }
            }

            private void step() {
                if ((idx + 1) / batchSize != idx / batchSize) {
                    detachCache(currentCache, nextCache);
                    currentCache = null;
                }
                if ((idx + 2) / batchSize != (idx + 1) / batchSize) {
                    detachCache(nextCache, currentCache);
                    nextCache = null;
                }
                ++idx;
            }

            @Override
            public boolean hasNext() {
                return hasNextData();
            }

            @Override
            public E next() {
                step();
                return currentData();
            }

        }
    }

}
