package com.cxj.hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by vipcxj on 2018/8/9.
 */
@MappedSuperclass
@Table(name = "SuperTable")
public class SuperClass implements Serializable {
    @Id
    private long id;
    @Version
    private Timestamp version;
    private String superValue;
    @ElementCollection
    private List<String> superStringList;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public String getSuperValue() {
        return superValue;
    }

    public void setSuperValue(String superValue) {
        this.superValue = superValue;
    }

    public List<String> getSuperStringList() {
        return superStringList;
    }

    public void setSuperStringList(List<String> superStringList) {
        this.superStringList = superStringList;
    }
}
