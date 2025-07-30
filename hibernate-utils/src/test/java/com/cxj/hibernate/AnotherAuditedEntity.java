package com.cxj.hibernate;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by vipcxj on 2018/8/13.
 */
@Entity
@Audited
public class AnotherAuditedEntity extends SuperClass {

    @ManyToMany(mappedBy = "anotherAuditedEntities")
    private List<SomeAuditedEntity> someAuditedEntities;
    @OneToMany(mappedBy = "relationEmbeddable.anotherAuditedEntity")
    private List<SomeAuditedEntity> someAuditedEntities2;

    public List<SomeAuditedEntity> getSomeAuditedEntities() {
        return someAuditedEntities;
    }

    public void setSomeAuditedEntities(List<SomeAuditedEntity> someAuditedEntities) {
        this.someAuditedEntities = someAuditedEntities;
    }

    public List<SomeAuditedEntity> getSomeAuditedEntities2() {
        return someAuditedEntities2;
    }

    public void setSomeAuditedEntities2(List<SomeAuditedEntity> someAuditedEntities2) {
        this.someAuditedEntities2 = someAuditedEntities2;
    }
}
