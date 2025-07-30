package com.cxj.functional;

/**
 * Created by vipcxj on 2018/5/29.
 */
public class Tuple6<T0, T1, T2, T3, T4, T5> implements Tuple {

    private T0 value0;
    private T1 value1;
    private T2 value2;
    private T3 value3;
    private T4 value4;
    private T5 value5;

    public Tuple6() {
    }

    public Tuple6(T0 value0, T1 value1, T2 value2, T3 value3, T4 value4, T5 value5) {
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.value5 = value5;
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

    public T4 getValue4() {
        return value4;
    }

    public void setValue4(T4 value4) {
        this.value4 = value4;
    }

    public T5 getValue5() {
        return value5;
    }

    public void setValue5(T5 value5) {
        this.value5 = value5;
    }

    @Override
    public int size() {
        return 6;
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
        } else if (i == 4) {
            return getValue4();
        } else if (i == 5) {
            return getValue5();
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
        } else if (i == 4) {
            setValue4((T4) v);
        } else if (i == 5) {
            setValue5((T5) v);
        } else {
            throw new IndexOutOfBoundsException("Expected: [0, " + size() + "), but: " + i + ".");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple6)) return false;

        Tuple6<?, ?, ?, ?, ?, ?> tuple6 = (Tuple6<?, ?, ?, ?, ?, ?>) o;

        if (getValue0() != null ? !getValue0().equals(tuple6.getValue0()) : tuple6.getValue0() != null) return false;
        if (getValue1() != null ? !getValue1().equals(tuple6.getValue1()) : tuple6.getValue1() != null) return false;
        if (getValue2() != null ? !getValue2().equals(tuple6.getValue2()) : tuple6.getValue2() != null) return false;
        if (getValue3() != null ? !getValue3().equals(tuple6.getValue3()) : tuple6.getValue3() != null) return false;
        if (getValue4() != null ? !getValue4().equals(tuple6.getValue4()) : tuple6.getValue4() != null) return false;
        return getValue5() != null ? getValue5().equals(tuple6.getValue5()) : tuple6.getValue5() == null;
    }

    @Override
    public int hashCode() {
        int result = getValue0() != null ? getValue0().hashCode() : 0;
        result = 31 * result + (getValue1() != null ? getValue1().hashCode() : 0);
        result = 31 * result + (getValue2() != null ? getValue2().hashCode() : 0);
        result = 31 * result + (getValue3() != null ? getValue3().hashCode() : 0);
        result = 31 * result + (getValue4() != null ? getValue4().hashCode() : 0);
        result = 31 * result + (getValue5() != null ? getValue5().hashCode() : 0);
        return result;
    }
}
