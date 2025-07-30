package com.cxj.functional;

/**
 * Created by vipcxj on 2018/5/29.
 */
public class Tuple4<T0, T1, T2, T3> implements Tuple {

    private T0 value0;
    private T1 value1;
    private T2 value2;
    private T3 value3;

    public Tuple4() {
    }

    public Tuple4(T0 value0, T1 value1, T2 value2, T3 value3) {
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    public T0 getValue0() {
        return value0;
    }

    public void setValue0(T0 value0) {
        this.value0 = value0;
    }

    public T1 getValue1() {
        return value1;
    }

    public void setValue1(T1 value1) {
        this.value1 = value1;
    }

    public T2 getValue2() {
        return value2;
    }

    public void setValue2(T2 value2) {
        this.value2 = value2;
    }

    public T3 getValue3() {
        return value3;
    }

    public void setValue3(T3 value3) {
        this.value3 = value3;
    }

    @Override
    public int size() {
        return 4;
    }

    @Override
    public Object getAt(int i) {
        if (i == 0) {
            return getValue0();
        } else if (i == 1) {
            return getValue1();
        } else if (i == 2) {
            return getValue2();
        } else if (i == 3) {
            return getValue3();
        } else {
            throw new IndexOutOfBoundsException("Expected: [0, " + size() + "), but: " + i + ".");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setAt(int i, Object v) {
        if (i == 0) {
            setValue0((T0) v);
        } else if (i == 1) {
            setValue1((T1) v);
        } else if (i == 2) {
            setValue2((T2) v);
        } else if (i == 3) {
            setValue3((T3) v);
        } else {
            throw new IndexOutOfBoundsException("Expected: [0, " + size() + "), but: " + i + ".");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple4)) return false;

        Tuple4<?, ?, ?, ?> tuple4 = (Tuple4<?, ?, ?, ?>) o;

        if (getValue0() != null ? !getValue0().equals(tuple4.getValue0()) : tuple4.getValue0() != null) return false;
        if (getValue1() != null ? !getValue1().equals(tuple4.getValue1()) : tuple4.getValue1() != null) return false;
        if (getValue2() != null ? !getValue2().equals(tuple4.getValue2()) : tuple4.getValue2() != null) return false;
        return getValue3() != null ? getValue3().equals(tuple4.getValue3()) : tuple4.getValue3() == null;
    }

    @Override
    public int hashCode() {
        int result = getValue0() != null ? getValue0().hashCode() : 0;
        result = 31 * result + (getValue1() != null ? getValue1().hashCode() : 0);
        result = 31 * result + (getValue2() != null ? getValue2().hashCode() : 0);
        result = 31 * result + (getValue3() != null ? getValue3().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple4{" +
                "value0=" + value0 +
                ", value1=" + value1 +
                ", value2=" + value2 +
                ", value3=" + value3 +
                '}';
    }
}
