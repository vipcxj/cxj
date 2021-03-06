/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.functional;

import java.util.Objects;

/**
 * Represents an operation that accepts three input arguments and returns no
 * result. This is the three-arity specialization of {@link Consumer}. Unlike most
 * other functional interfaces, {@code TriConsumer} is expected to operate via
 * side-effects.
 *
 * <p>
 * This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object, Object, Object)}.
 *
 * @author 陈晓靖
 * @param <A1> the type of the first argument to the operation
 * @param <A2> the type of the second argument to the operation
 * @param <A3> the type of the third argument to the operation
 *
 * @see Consumer
 */
@FunctionalInterface
public interface TriConsumer<A1, A2, A3> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param a1 the first input argument
     * @param a2 the second input argument
     * @param a3 the third input argument
     */
    void accept(A1 a1, A2 a2, A3 a3);

    /**
     * Returns a composed {@code TriConsumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation. If performing this operation throws an exception, the
     * {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code TriConsumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default TriConsumer<A1, A2, A3> andThen(TriConsumer<? super A1, ? super A2, ? super A3> after) {
        Objects.requireNonNull(after);

        return (a1, a2, a3) -> {
            accept(a1, a2, a3);
            after.accept(a1, a2, a3);
        };
    }
}
