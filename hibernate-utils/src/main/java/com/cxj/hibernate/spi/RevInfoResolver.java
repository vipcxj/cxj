package com.cxj.hibernate.spi;

import org.hibernate.MappingException;
import org.hibernate.annotations.common.reflection.ClassLoadingException;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.envers.*;
import org.hibernate.envers.configuration.internal.GlobalConfiguration;
import org.hibernate.envers.enhanced.SequenceIdTrackingModifiedEntitiesRevisionEntity;
import org.hibernate.envers.internal.entities.PropertyData;
import org.hibernate.envers.internal.revisioninfo.DefaultRevisionInfoGenerator;
import org.hibernate.envers.internal.revisioninfo.DefaultTrackingModifiedEntitiesRevisionInfoGenerator;
import org.hibernate.envers.internal.revisioninfo.RevisionInfoGenerator;
import org.hibernate.envers.internal.tools.MutableBoolean;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import javax.persistence.Column;
import java.util.Date;
import java.util.Set;

/**
 * Created by vipcxj on 2018/8/16.
 */
public class RevInfoResolver {
    private String revisionInfoEntityName;
    private PropertyData revisionInfoIdData;
    private PropertyData revisionInfoTimestampData;
    private PropertyData modifiedEntityNamesData;
    private Type revisionInfoTimestampType;
    private GlobalConfiguration globalCfg;

    private String revisionPropType;
    private String revisionPropSqlType;

    public String getRevisionInfoEntityName() {
        return revisionInfoEntityName;
    }

    public PropertyData getRevisionInfoIdData() {
        return revisionInfoIdData;
    }

    public PropertyData getRevisionInfoTimestampData() {
        return revisionInfoTimestampData;
    }

    public PropertyData getModifiedEntityNamesData() {
        return modifiedEntityNamesData;
    }

    public Type getRevisionInfoTimestampType() {
        return revisionInfoTimestampType;
    }

    public String getRevisionPropType() {
        return revisionPropType;
    }

    public String getRevisionPropSqlType() {
        return revisionPropSqlType;
    }

    public RevInfoResolver(GlobalConfiguration globalCfg) {
        this.globalCfg = globalCfg;
        if ( globalCfg.isUseRevisionEntityWithNativeId() ) {
            revisionInfoEntityName = "org.hibernate.envers.DefaultRevisionEntity";
        }
        else {
            revisionInfoEntityName = "org.hibernate.envers.enhanced.SequenceIdRevisionEntity";
        }
        revisionInfoIdData = new PropertyData( "id", "id", "field", null );
        revisionInfoTimestampData = new PropertyData( "timestamp", "timestamp", "field", null );
        modifiedEntityNamesData = new PropertyData( "modifiedEntityNames", "modifiedEntityNames", "field", null );
        revisionInfoTimestampType = new LongType();

        revisionPropType = "integer";
    }

    private void searchForRevisionInfoCfgInProperties(
            XClass clazz,
            ReflectionManager reflectionManager,
            MutableBoolean revisionNumberFound,
            MutableBoolean revisionTimestampFound,
            MutableBoolean modifiedEntityNamesFound,
            String accessType) {
        for ( XProperty property : clazz.getDeclaredProperties( accessType ) ) {
            final RevisionNumber revisionNumber = property.getAnnotation( RevisionNumber.class );
            final RevisionTimestamp revisionTimestamp = property.getAnnotation( RevisionTimestamp.class );
            final ModifiedEntityNames modifiedEntityNames = property.getAnnotation( ModifiedEntityNames.class );

            if ( revisionNumber != null ) {
                if ( revisionNumberFound.isSet() ) {
                    throw new MappingException( "Only one property may be annotated with @RevisionNumber!" );
                }

                final XClass revisionNumberClass = property.getType();
                if ( reflectionManager.equals( revisionNumberClass, Integer.class ) ||
                        reflectionManager.equals( revisionNumberClass, Integer.TYPE ) ) {
                    revisionInfoIdData = new PropertyData( property.getName(), property.getName(), accessType, null );
                    revisionNumberFound.set();
                }
                else if ( reflectionManager.equals( revisionNumberClass, Long.class ) ||
                        reflectionManager.equals( revisionNumberClass, Long.TYPE ) ) {
                    revisionInfoIdData = new PropertyData( property.getName(), property.getName(), accessType, null );
                    revisionNumberFound.set();

                    // The default is integer
                    revisionPropType = "long";
                }
                else {
                    throw new MappingException(
                            "The field annotated with @RevisionNumber must be of type " +
                                    "int, Integer, long or Long"
                    );
                }

                // Getting the @Column definition of the revision number property, to later use that info to
                // generate the same mapping for the relation from an audit table's revision number to the
                // revision entity revision number.
                final Column revisionPropColumn = property.getAnnotation( Column.class );
                if ( revisionPropColumn != null ) {
                    revisionPropSqlType = revisionPropColumn.columnDefinition();
                }
            }

            if ( revisionTimestamp != null ) {
                if ( revisionTimestampFound.isSet() ) {
                    throw new MappingException( "Only one property may be annotated with @RevisionTimestamp!" );
                }

                final XClass revisionTimestampClass = property.getType();
                if ( reflectionManager.equals( revisionTimestampClass, Long.class ) ||
                        reflectionManager.equals( revisionTimestampClass, Long.TYPE ) ||
                        reflectionManager.equals( revisionTimestampClass, Date.class ) ||
                        reflectionManager.equals( revisionTimestampClass, java.sql.Date.class ) ) {
                    revisionInfoTimestampData = new PropertyData(
                            property.getName(),
                            property.getName(),
                            accessType,
                            null
                    );
                    revisionTimestampFound.set();
                }
                else {
                    throw new MappingException(
                            "The field annotated with @RevisionTimestamp must be of type " +
                                    "long, Long, java.util.Date or java.sql.Date"
                    );
                }
            }

            if ( modifiedEntityNames != null ) {
                if ( modifiedEntityNamesFound.isSet() ) {
                    throw new MappingException( "Only one property may be annotated with @ModifiedEntityNames!" );
                }
                final XClass modifiedEntityNamesClass = property.getType();
                if ( reflectionManager.equals( modifiedEntityNamesClass, Set.class ) &&
                        reflectionManager.equals( property.getElementClass(), String.class ) ) {
                    modifiedEntityNamesData = new PropertyData(
                            property.getName(),
                            property.getName(),
                            accessType,
                            null
                    );
                    modifiedEntityNamesFound.set();
                }
                else {
                    throw new MappingException(
                            "The field annotated with @ModifiedEntityNames must be of Set<String> type."
                    );
                }
            }
        }
    }

    private void searchForRevisionInfoCfg(
            XClass clazz, ReflectionManager reflectionManager,
            MutableBoolean revisionNumberFound, MutableBoolean revisionTimestampFound,
            MutableBoolean modifiedEntityNamesFound) {
        final XClass superclazz = clazz.getSuperclass();
        if ( !"java.lang.Object".equals( superclazz.getName() ) ) {
            searchForRevisionInfoCfg(
                    superclazz,
                    reflectionManager,
                    revisionNumberFound,
                    revisionTimestampFound,
                    modifiedEntityNamesFound
            );
        }

        searchForRevisionInfoCfgInProperties(
                clazz, reflectionManager, revisionNumberFound, revisionTimestampFound,
                modifiedEntityNamesFound, "field"
        );
        searchForRevisionInfoCfgInProperties(
                clazz, reflectionManager, revisionNumberFound, revisionTimestampFound,
                modifiedEntityNamesFound, "property"
        );
    }

    public void configure(MetadataImplementor metadata, ReflectionManager reflectionManager) {
        boolean revisionEntityFound = false;
        RevisionInfoGenerator revisionInfoGenerator = null;
        Class<?> revisionInfoClass;

        for ( PersistentClass persistentClass : metadata.getEntityBindings() ) {
            // Ensure we're in POJO, not dynamic model, mapping.
            if (persistentClass.getClassName() != null) {
                XClass clazz;
                try {
                    clazz = reflectionManager.classForName( persistentClass.getClassName() );
                }
                catch (ClassLoadingException e) {
                    throw new MappingException( e );
                }

                final RevisionEntity revisionEntity = clazz.getAnnotation( RevisionEntity.class );
                if ( revisionEntity != null ) {
                    if (revisionEntityFound) {
                        throw new MappingException("Only one entity may be annotated with @RevisionEntity!");
                    }

                    // Checking if custom revision entity isn't audited
                    if ( clazz.getAnnotation( Audited.class ) != null ) {
                        throw new MappingException("An entity annotated with @RevisionEntity cannot be audited!");
                    }

                    revisionEntityFound = true;

                    final MutableBoolean revisionNumberFound = new MutableBoolean();
                    final MutableBoolean revisionTimestampFound = new MutableBoolean();
                    final MutableBoolean modifiedEntityNamesFound = new MutableBoolean();

                    searchForRevisionInfoCfg(
                            clazz,
                            reflectionManager,
                            revisionNumberFound,
                            revisionTimestampFound,
                            modifiedEntityNamesFound
                    );

                    if (!revisionNumberFound.isSet()) {
                        throw new MappingException(
                                "An entity annotated with @RevisionEntity must have a field annotated " +
                                        "with @RevisionNumber!"
                        );
                    }

                    if (!revisionTimestampFound.isSet()) {
                        throw new MappingException(
                                "An entity annotated with @RevisionEntity must have a field annotated " +
                                        "with @RevisionTimestamp!"
                        );
                    }

                    revisionInfoEntityName = persistentClass.getEntityName();
                    revisionInfoClass = persistentClass.getMappedClass();
                    final Class<? extends RevisionListener> revisionListenerClass = getRevisionListenerClass( revisionEntity.value() );
                    revisionInfoTimestampType = persistentClass.getProperty( revisionInfoTimestampData.getName() ).getType();
                    if ( globalCfg.isTrackEntitiesChangedInRevision()
                            || ( globalCfg.isUseRevisionEntityWithNativeId() && DefaultTrackingModifiedEntitiesRevisionEntity.class
                            .isAssignableFrom( revisionInfoClass ) )
                            || ( !globalCfg.isUseRevisionEntityWithNativeId() && SequenceIdTrackingModifiedEntitiesRevisionEntity.class
                            .isAssignableFrom( revisionInfoClass ) )
                            || modifiedEntityNamesFound.isSet() ) {
                        // If tracking modified entities parameter is enabled, custom revision info entity is a subtype
                        // of DefaultTrackingModifiedEntitiesRevisionEntity class, or @ModifiedEntityNames annotation is used.
                        revisionInfoGenerator = new DefaultTrackingModifiedEntitiesRevisionInfoGenerator(
                                revisionInfoEntityName,
                                revisionInfoClass,
                                revisionListenerClass,
                                revisionInfoTimestampData,
                                isTimestampAsDate(),
                                modifiedEntityNamesData,
                                metadata.getMetadataBuildingOptions().getServiceRegistry()
                        );
                        globalCfg.setTrackEntitiesChangedInRevision( true );
                    }
                    else {
                        revisionInfoGenerator = new DefaultRevisionInfoGenerator(
                                revisionInfoEntityName,
                                revisionInfoClass,
                                revisionListenerClass,
                                revisionInfoTimestampData,
                                isTimestampAsDate(),
                                metadata.getMetadataBuildingOptions().getServiceRegistry()
                        );
                    }
                }
            }
        }

        if ( revisionInfoGenerator == null ) {
            if ( globalCfg.isTrackEntitiesChangedInRevision() ) {
                revisionInfoClass = globalCfg.isUseRevisionEntityWithNativeId()
                        ? DefaultTrackingModifiedEntitiesRevisionEntity.class
                        : SequenceIdTrackingModifiedEntitiesRevisionEntity.class;
                revisionInfoEntityName = revisionInfoClass.getName();
            }
        }
    }

    private boolean isTimestampAsDate() {
        final String typename = revisionInfoTimestampType.getName();
        return "date".equals( typename ) || "time".equals( typename ) || "timestamp".equals( typename );
    }

    /**
     * @param defaultListener Revision listener that shall be applied if {@code org.hibernate.envers.revision_listener}
     * parameter has not been set.
     *
     * @return Revision listener.
     */
    private Class<? extends RevisionListener> getRevisionListenerClass(Class<? extends RevisionListener> defaultListener) {
        if ( globalCfg.getRevisionListenerClass() != null ) {
            return globalCfg.getRevisionListenerClass();
        }
        return defaultListener;
    }
}
