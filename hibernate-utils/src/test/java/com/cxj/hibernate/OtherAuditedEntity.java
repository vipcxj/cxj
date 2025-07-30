package com.cxj.hibernate;

import org.hibernate.envers.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipcxj on 2018/8/13.
 */
@Entity
@Audited(auditParents = {SuperClass.class})
@Table(name = "OtherAuditedTable")
@AuditOverrides({
        @AuditOverride(name = "superStringList", auditJoinTable = @AuditJoinTable(name = "CustomOverrideAuditJoinTable"))
})
@AssociationOverrides({
        @AssociationOverride(name = "superStringList", joinTable = @JoinTable(name = "SuperStringListOfOtherAuditedEntity"))
})
public class OtherAuditedEntity extends SuperClass {

    private String value;
    @NotAudited
    private String notAuditedValue;
    @OneToMany(mappedBy = "otherAuditedEntity")
    private List<SomeAuditedEntity> someAuditedEntities = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNotAuditedValue() {
        return notAuditedValue;
    }

    public void setNotAuditedValue(String notAuditedValue) {
        this.notAuditedValue = notAuditedValue;
    }

    public List<SomeAuditedEntity> getSomeAuditedEntities() {
        return someAuditedEntities;
    }
}
