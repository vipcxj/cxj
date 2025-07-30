package com.cxj.functional;

/**
 * Created by vipcxj on 2018/5/29.
 */
public class Tuple9<T0, T1, T2, T3, T4, T5, T6, T7, T8> implements Tuple {

    private T0 value0;
    private T1 value1;
    private T2 value2;
    private T3 value3;
    private T4 value4;
    private T5 value5;
    private T6 value6;
    private T7 value7;
    private T8 value8;

    public Tuple9(T0 value0) {
        this.value0 = value0;
    }

    public Tuple9(T0 value0, T1 value1, T2 value2, T3 value3, T4 value4, T5 value5, T6 value6, T7 value7, T8 value8) {
        this.value0 = value0;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
        this.value4 = value4;
        this.value5 = value5;
        this.value6 = value6;
        this.value7 = value7;
        this.value8 = value8;
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

    public T6 getValue6() {
        return value6;
    }

    public void setValue6(T6 value6) {
        this.value6 = value6;
    }

    public T7 getValue7() {
        return value7;
    }

    public void setValue7(T7 value7) {
        this.value7 = value7;
    }

    public T8 getValue8() {
        return value8;
    }

    public void setValue8(T8 value8) {
        this.value8 = value8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple9)) return false;

        Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?> tuple9 = (Tuple9<?, ?, ?, ?, ?, ?, ?, ?, ?>) o;

        if (getValue0() != null ? !getValue0().equals(tuple9.getValue0()) : tuple9.getValue0() != null) return false;
        if (getValue1() != null ? !getValue1().equals(tuple9.getValue1()) : tuple9.getValue1() != null) return false;
        if (getValue2() != null ? !getValue2().equals(tuple9.getValue2()) : tuple9.getValue2() != null) return false;
        if (getValue3() != null ? !getValue3().equals(tuple9.getValue3()) : tuple9.getValue3() != null) return false;
        if (getValue4() != null ? !getValue4().equals(tuple9.getValue4()) : tuple9.getValue4() != null) return false;
        if (getValue5() != null ? !getValue5().equals(tuple9.getValue5()) : tuple9.getValue5() != null) return false;
        if (getValue6() != null ? !getValue6().equals(tuple9.getValue6()) : tuple9.getValue6() != null) return false;
        if (getValue7() != null ? !getValue7().equals(tuple9.getValue7()) : tuple9.getValue7() != null) return false;
        return getValue8() != null ? getValue8().equals(tuple9.getValue8()) : tuple9.getValue8() == null;
    }

    @Override
    public int hashCode() {
        int result = getValue0() != null ? getValue0().hashCode() : 0;
        result = 31 * result + (getValue1() != null ? getValue1().hashCode() : 0);
        result = 31 * result + (getValue2() != null ? getValue2().hashCode() : 0);
        result = 31 * result + (getValue3() != null ? getValue3().hashCode() : 0);
        result = 31 * result + (getValue4() != null ? getValue4().hashCode() : 0);
        result = 31 * result + (getValue5() != null ? getValue5().hashCode() : 0);
        result = 31 * result + (getValue6() != null ? getValue6().hashCode() : 0);
        result = 31 * result + (getValue7() != null ? getValue7().hashCode() : 0);
        result = 31 * result + (getValue8() != null ? getValue8().hashCode() : 0);
        return result;
    }

    @Override
    public int size() {
        return 9;
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
        } else if (i == 6) {
            return getValue6();
        } else if (i == 7) {
            return getValue7();
        } else if (i == 8) {
            return getValue8();
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
        } else if (i == 6) {
            setValue6((T6) v);
        } else if (i == 7) {
            setValue7((T7) v);
        } else if (i == 8) {
            setValue8((T8) v);
        } else {
            throw new IndexOutOfBoundsException("Expected: [0, " + size() + "), but: " + i + ".");
        }
    }
}
