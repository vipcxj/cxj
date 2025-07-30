package com.cxj.functional;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts four arguments and produces a result.
 * This is the four-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}.
 *
 * @author Administrator
 * @param <A1> the type of the first argument to the function
 * @param <A2> the type of the second argument to the function
 * @param <A3> the type of the third argument to the function
 * @param <A4> the type of the third argument to the function
 * @param <R> the type of the result of the function
 *
 * @see Function
 */
@FunctionalInterface
public interface QuadrFunction<A1, A2, A3, A4, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param a1 the first function argument
     * @param a2 the second function argument
     * @param a3 the third function argument
     * @param a4 the forth function argument
     * @return the function result
     */
    R apply(A1 a1, A2 a2, A3 a3, A4 a4);

    /**
     * Returns a composed function that first applies this function to its
     * input, and then applies the {@code after} function to the result. If
     * evaluation of either function throws an exception, it is relayed to the
     * caller of the composed function.
     *
     * @param <V> the type of output of the {@code after} function, and of the
     * composed function
     * @param after the function to apply after this function is applied
     * @return a composed function that first applies this function and then
     * applies the {@code after} function
     * @throws NullPointerException if after is null
     */
    default <V> QuadrFunction<A1, A2, A3, A4, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A1 a1, A2 a2, A3 a3, A4 a4) -> after.apply(apply(a1, a2, a3, a4));
    }
}
