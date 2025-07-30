package com.cxj.hibernate;

import org.hibernate.envers.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by vipcxj on 2018/8/9.
 */
@Entity
@Audited
@AuditTable("AuditTableOfSomeAuditedEntity")
public class SomeAuditedEntity extends SuperClass {

    private String value;
    @NotAudited
    private String notAuditedValue;
    @ManyToOne
    private OtherAuditedEntity otherAuditedEntity;
    @OrderColumn
    @ElementCollection
    @AuditJoinTable(name = "CustomAuditJoinTable")
    private List<String> stringList = new ArrayList<>();
    @ElementCollection
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private List<String> notAuditedStringList = new ArrayList<>();
    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private SomeEntity someEntity;
    @ManyToMany
    private List<AnotherAuditedEntity> anotherAuditedEntities = new ArrayList<>();
    @ElementCollection
    private Set<TestEmbeddable> embeddables;
    private TestEmbeddable embeddable;
    @AttributeOverrides({
            @AttributeOverride(name = "value1", column = @Column(name = "value21")),
            @AttributeOverride(name = "value2", column = @Column(name = "value22"))
    })
    private TestEmbeddable2 embeddable2;
    private RelationEmbeddable relationEmbeddable = new RelationEmbeddable();

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

    public OtherAuditedEntity getOtherAuditedEntity() {
        return otherAuditedEntity;
    }

    public void setOtherAuditedEntity(OtherAuditedEntity otherAuditedEntity) {
        this.otherAuditedEntity = otherAuditedEntity;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<String> getNotAuditedStringList() {
        return notAuditedStringList;
    }

    public void setNotAuditedStringList(List<String> notAuditedStringList) {
        this.notAuditedStringList = notAuditedStringList;
    }

    public SomeEntity getSomeEntity() {
        return someEntity;
    }

    public void setSomeEntity(SomeEntity someEntity) {
        this.someEntity = someEntity;
    }

    public List<AnotherAuditedEntity> getAnotherAuditedEntities() {
        return anotherAuditedEntities;
    }

    public void setAnotherAuditedEntities(List<AnotherAuditedEntity> anotherAuditedEntities) {
        this.anotherAuditedEntities = anotherAuditedEntities;
    }

    public Set<TestEmbeddable> getEmbeddables() {
        return embeddables;
    }

    public void setEmbeddables(Set<TestEmbeddable> embeddables) {
        this.embeddables = embeddables;
    }

    public TestEmbeddable getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(TestEmbeddable embeddable) {
        this.embeddable = embeddable;
    }

    public TestEmbeddable2 getEmbeddable2() {
        return embeddable2;
    }

    public void setEmbeddable2(TestEmbeddable2 embeddable2) {
        this.embeddable2 = embeddable2;
    }

    public RelationEmbeddable getRelationEmbeddable() {
        return relationEmbeddable;
    }

    public void setRelationEmbeddable(RelationEmbeddable relationEmbeddable) {
        this.relationEmbeddable = relationEmbeddable;
    }
}
