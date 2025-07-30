package com.cxj.functional;

/**
 * Created by vipcxj on 2018/5/29.
 */
public class Tuple1<T> implements Tuple {

    private T value0;

    public Tuple1() {
    }

    public Tuple1(T value0) {
        this.value0 = value0;
    }

    public T getValue0() {
        return value0;
    }

    public void setValue0(T value0) {
        this.value0 = value0;
    }

    @Override
    public Object getAt(int i) {
        if (i != 0) {
            throw new IndexOutOfBoundsException("Excepted: 0, but: " + i + ".");
        }
        return getValue0();
    }

    @Override
    public void setAt(int i, Object v) {
        if (i != 0) {
            throw new IndexOutOfBoundsException("Excepted: 0, but: " + i + ".");
        }
        //noinspection unchecked
        setValue0((T) v);
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public String toString() {
        return "Tuple1{" +
                "value0=" + value0 +
                '}';
    }
}
