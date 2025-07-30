package com.cxj.hibernate;

import com.cxj.utility.CollectionHelper;
import org.hibernate.Transaction;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by vipcxj on 2018/8/9.
 */
public class EnversUtilsTest extends BaseCoreFunctionalTestCase {

    // Add your entities here.
    @Override
    protected Class[] getAnnotatedClasses() {
        return new Class[] {
                SomeEntity.class,
                SomeAuditedEntity.class,
                OtherAuditedEntity.class,
                AnotherAuditedEntity.class,
                Phone.class,
                Employee.class
        };
    }

    @Override
    protected void configure(Configuration configuration) {
        super.configure( configuration );

        configuration.setProperty( AvailableSettings.SHOW_SQL, Boolean.TRUE.toString() );
        configuration.setProperty( AvailableSettings.FORMAT_SQL, Boolean.TRUE.toString() );
        //configuration.setProperty( AvailableSettings.GENERATE_STATISTICS, "true" );
    }

    // Add your tests, using standard JUnit.
    @Test
    public void testGetAllAuditedEntityNames() {
        openSession();
        Assert.assertEquals(CollectionHelper.setFrom(SomeAuditedEntity.class.getName(), OtherAuditedEntity.class.getName()), EnversUtils.getAuditedEntityNames());
    }

    @Test
    public void testGetAllAuditedAttributeNames() {
        openSession();
        Assert.assertEquals(CollectionHelper.setFrom("value"), EnversUtils.getAuditedPropertyNames(SomeAuditedEntity.class.getName()));
        Assert.assertEquals(CollectionHelper.setFrom("superValue", "value"), EnversUtils.getAuditedPropertyNames(OtherAuditedEntity.class.getName()));
    }

    @Test
    public void testGetAuditTableName() {
        openSession();
        Assert.assertEquals("AuditTableOfSomeAuditedEntity", EnversUtils.getAuditTableName(SomeAuditedEntity.class.getName()));
        Assert.assertEquals("Test_OtherAuditedTable_Audit", EnversUtils.getAuditTableName(OtherAuditedEntity.class.getName()));
    }

    @Test
    public void testtest() {
        openSession();
        SomeAuditedEntity test = new SomeAuditedEntity();
        test.setId(1);
        test.setEmbeddable(new TestEmbeddable());
        Transaction transaction = session.beginTransaction();
        try {
            session.persist(test);
            session.flush();
            transaction.commit();
        } catch (Throwable e) {
            transaction.rollback();
        }
        Set<String> auditedEntityNames = EnversUtils.getAuditedEntityNames();
        for (String auditedEntityName : auditedEntityNames) {
            String tableName = HibernateUtils.getTableName(auditedEntityName);
            System.out.println(EnversUtils.triggerInsert(tableName, null, null));
        }
        Set<String> collectionTables = HibernateUtils.getStandaloneCollectionTables();
        for (String tableName : collectionTables) {
            System.out.println(EnversUtils.triggerInsert(tableName, null, null));
        }
        System.out.println(EnversUtils.auditExecSql());
    }
}
