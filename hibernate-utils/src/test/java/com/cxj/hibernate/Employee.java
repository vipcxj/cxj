package com.cxj.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by vipcxj on 2018/8/15.
 */
@Entity
public class Employee {
    @Id
    @Column(name = "EMP_ID")
    private long id;
    @OneToMany(mappedBy = "owner")
    private List<Phone> phones;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }
}
