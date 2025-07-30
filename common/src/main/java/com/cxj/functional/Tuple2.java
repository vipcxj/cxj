package com.cxj.functional;

/**
 * Created by vipcxj on 2018/5/29.
 */
public class Tuple2<T0, T1> implements Tuple {

    private T0 value0;
    private T1 value1;

    public Tuple2() {
    }

    public Tuple2(T0 value0, T1 value1) {
        this.value0 = value0;
        this.value1 = value1;
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

    @Override
    public Object getAt(int i) {
        if (i == 0) {
            return getValue0();
        } else if (i == 1) {
            return getValue1();
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
        } else {
            throw new IndexOutOfBoundsException("Expected: [0, " + size() + "), but: " + i + ".");
        }
    }

    @Override
    public int size() {
        return 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple2)) return false;

        Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

        if (getValue0() != null ? !getValue0().equals(tuple2.getValue0()) : tuple2.getValue0() != null) return false;
        return getValue1() != null ? getValue1().equals(tuple2.getValue1()) : tuple2.getValue1() == null;
    }

    @Override
    public int hashCode() {
        int result = getValue0() != null ? getValue0().hashCode() : 0;
        result = 31 * result + (getValue1() != null ? getValue1().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Tuple2{" +
                "value0=" + value0 +
                ", value1=" + value1 +
                '}';
    }
}
