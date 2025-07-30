package com.cxj.functional;

/**
 * Created by vipcxj on 2018/6/5.
 */
@FunctionalInterface
public interface ThrowableRunnable<E extends Throwable> {

    void run() throws E;
}
