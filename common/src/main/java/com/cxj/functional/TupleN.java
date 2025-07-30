package com.cxj.functional;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vipcxj on 2018/5/29.
 */
public class TupleN implements Tuple {

    private Object[] values;

    public TupleN(Object... values) {
        this.values = values;
    }

    public TupleN(@Nonnull List values) {
        this.values = values.toArray();
    }

    @Override
    public int size() {
        return values.length;
    }

    @Override
    public Object getAt(int i) {
        return values[i];
    }

    @Override
    public void setAt(int i, Object v) {
        values[i] = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TupleN)) return false;

        TupleN tupleN = (TupleN) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(values, tupleN.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }
}
