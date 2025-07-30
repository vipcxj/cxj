package com.cxj.hibernate;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by vipcxj on 2018/8/20.
 */
@Embeddable
public class TestEmbeddable2 implements Serializable {

    private String value1;
    private String value2;

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestEmbeddable2)) return false;

        TestEmbeddable2 that = (TestEmbeddable2) o;

        if (getValue1() != null ? !getValue1().equals(that.getValue1()) : that.getValue1() != null) return false;
        return getValue2() != null ? getValue2().equals(that.getValue2()) : that.getValue2() == null;
    }

    @Override
    public int hashCode() {
        int result = getValue1() != null ? getValue1().hashCode() : 0;
        result = 31 * result + (getValue2() != null ? getValue2().hashCode() : 0);
        return result;
    }
}
