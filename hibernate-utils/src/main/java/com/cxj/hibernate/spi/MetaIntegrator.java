package com.cxj.hibernate.spi;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.envers.configuration.internal.ClassesAuditingData;
import org.hibernate.envers.configuration.internal.PersistentClassGraphDefiner;
import org.hibernate.envers.configuration.internal.metadata.reader.AnnotationsMetadataReader;
import org.hibernate.envers.configuration.internal.metadata.reader.ClassAuditingData;
import org.hibernate.envers.internal.tools.graph.GraphTopologicalSort;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.service.Service;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.util.Iterator;

/**
 * Created by vipcxj on 2018/8/10.
 */
public class MetaIntegrator implements Integrator {

    private static volatile Metadata metadata;
    private static volatile ClassesAuditingData classesAuditingData;
    private static volatile SessionFactoryServiceRegistry serviceRegistry;
    private static volatile RevInfoResolver revInfoResolver;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        MetaIntegrator.metadata = metadata;
        MetaIntegrator.serviceRegistry = serviceRegistry;
        EnversService service = serviceRegistry.getService(EnversService.class);
        if (service == null || !service.isEnabled() || !service.isInitialized()) {
            return;
        }
        MetadataImplementor metadataImplementor = (MetadataImplementor) metadata;
        final ReflectionManager reflectionManager = metadataImplementor.getMetadataBuildingOptions()
                .getReflectionManager();

        // Sorting the persistent class topologically - superclass always before subclass
        final Iterator<PersistentClass> classes = GraphTopologicalSort.sort( new PersistentClassGraphDefiner( metadataImplementor ) )
                .iterator();

        final ClassesAuditingData classesAuditingData = new ClassesAuditingData();

        // Reading metadata from annotations
        while ( classes.hasNext() ) {
            final PersistentClass pc = classes.next();

            // Ensure we're in POJO, not dynamic model, mapping.
            if (pc.getClassName() != null) {
                // Collecting information from annotations on the persistent class pc
                final AnnotationsMetadataReader annotationsMetadataReader =
                        new AnnotationsMetadataReader( service.getGlobalConfiguration(), reflectionManager, pc );
                final ClassAuditingData auditData = annotationsMetadataReader.getAuditData();

                classesAuditingData.addClassAuditingData( pc, auditData );
            }
        }

        // Now that all information is read we can update the calculated fields.
        classesAuditingData.updateCalculatedFields();
        MetaIntegrator.classesAuditingData = classesAuditingData;
        final RevInfoResolver resolver = new RevInfoResolver(service.getGlobalConfiguration());
        resolver.configure(metadataImplementor, reflectionManager);
        MetaIntegrator.revInfoResolver = resolver;
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        MetaIntegrator.metadata = null;
        MetaIntegrator.classesAuditingData = null;
        MetaIntegrator.serviceRegistry = null;
        MetaIntegrator.revInfoResolver = null;
    }

    public static Metadata getMetadata() {
        return metadata;
    }

    public static ClassesAuditingData getClassesAuditingData() {
        return classesAuditingData;
    }

    public static SessionFactoryServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static RevInfoResolver getRevInfoResolver() {
        return revInfoResolver;
    }

    public static <R extends Service> R getService(Class<R> type) {
        if (serviceRegistry == null) {
            throw new IllegalStateException("The serviceRegistry is not initialized yet!");
        }
        return serviceRegistry.getService(type);
    }
}
