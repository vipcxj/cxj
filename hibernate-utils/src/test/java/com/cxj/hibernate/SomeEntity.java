package com.cxj.hibernate;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipcxj on 2018/8/9.
 */
@Entity
public class SomeEntity extends SuperClass {

    private String value;
    @OneToMany(mappedBy = "someEntity")
    private List<SomeAuditedEntity> someAuditedEntities = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<SomeAuditedEntity> getSomeAuditedEntities() {
        return someAuditedEntities;
    }

    public void setSomeAuditedEntities(List<SomeAuditedEntity> someAuditedEntities) {
        this.someAuditedEntities = someAuditedEntities;
    }
}
