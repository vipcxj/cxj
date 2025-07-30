package com.cxj.hibernate;

import java.io.Serializable;

/**
 * Created by vipcxj on 2018/8/15.
 */
public class PhonePK implements Serializable {
    private String type;
    private long owner;

    public PhonePK() {
    }

    public PhonePK(String type, long owner) {
        this.type = type;
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhonePK)) return false;

        PhonePK phonePK = (PhonePK) o;

        if (getOwner() != phonePK.getOwner()) return false;
        return getType() != null ? getType().equals(phonePK.getType()) : phonePK.getType() == null;
    }

    @Override
    public int hashCode() {
        int result = getType() != null ? getType().hashCode() : 0;
        result = 31 * result + (int) (getOwner() ^ (getOwner() >>> 32));
        return result;
    }
}
