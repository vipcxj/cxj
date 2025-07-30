package com.cxj.hibernate;

import javax.persistence.*;

/**
 * Created by vipcxj on 2018/8/15.
 */
@Entity
@IdClass(PhonePK.class)
public class Phone {
    @Id
    private String type;
    @ManyToOne
    @Id
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "EMP_ID")
    private Employee owner;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Employee getOwner() {
        return owner;
    }

    public void setOwner(Employee owner) {
        this.owner = owner;
    }
}
