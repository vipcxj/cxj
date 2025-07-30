package com.cxj.functional;

/**
 * Created by vipcxj on 2018/6/5.
 */
public class WrappedThrowable extends RuntimeException {

    public WrappedThrowable(Throwable cause) {
        super(cause);
    }
}
