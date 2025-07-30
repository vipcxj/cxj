package com.cxj.hibernate;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by vipcxj on 2018/8/17.
 */
@Embeddable
public class TestEmbeddable implements Serializable {

    private int value1;
    private String value2;
    private String value3;

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestEmbeddable)) return false;

        TestEmbeddable that = (TestEmbeddable) o;

        if (getValue1() != that.getValue1()) return false;
        if (getValue2() != null ? !getValue2().equals(that.getValue2()) : that.getValue2() != null) return false;
        return getValue3() != null ? getValue3().equals(that.getValue3()) : that.getValue3() == null;
    }

    @Override
    public int hashCode() {
        int result = getValue1();
        result = 31 * result + (getValue2() != null ? getValue2().hashCode() : 0);
        result = 31 * result + (getValue3() != null ? getValue3().hashCode() : 0);
        return result;
    }

    public void setValue2(String value2) {

        this.value2 = value2;

    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

}
