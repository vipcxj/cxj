package com.cxj.functional;

/**
 * Created by vipcxj on 2018/5/29.
 */
public interface Tuple {
    int size();
    Object getAt(int i);
    void setAt(int i, Object v);
}
