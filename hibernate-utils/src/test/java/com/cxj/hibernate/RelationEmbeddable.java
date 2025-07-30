package com.cxj.hibernate;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

/**
 * Created by vipcxj on 2018/8/17.
 */
@Embeddable
public class RelationEmbeddable implements Serializable {

    @OneToMany
    private List<OtherAuditedEntity> otherAuditedEntities;
    @ManyToOne
    private AnotherAuditedEntity anotherAuditedEntity;

    public List<OtherAuditedEntity> getOtherAuditedEntities() {
        return otherAuditedEntities;
    }

    public void setOtherAuditedEntities(List<OtherAuditedEntity> otherAuditedEntities) {
        this.otherAuditedEntities = otherAuditedEntities;
    }

    public AnotherAuditedEntity getAnotherAuditedEntity() {
        return anotherAuditedEntity;
    }

    public void setAnotherAuditedEntity(AnotherAuditedEntity anotherAuditedEntity) {
        this.anotherAuditedEntity = anotherAuditedEntity;
    }
}
