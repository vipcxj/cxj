/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.functional;

import com.cxj.utility.Exceptions;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;

/**
 *
 * @author Administrator
 */
public class FunctionalUtils {

    public static <T> Predicate<T> alwaysTrue() {
        return arg -> true;
    }
    
    public static <T> Predicate<T> alwaysFalse() {
        return arg -> false;
    }

    public static <R, T> Function<T, R> cachedFunction(@Nonnull Function<T, R> function) {
        return new CachedFunction<>(function);
    }

    public static <R, T, U> BiFunction<T, U, R> cachedFunction(@Nonnull BiFunction<T, U, R> function) {
        return new CachedBiFunction<>(function);
    }

    public static <R, T0, T1, T2> TriFunction<T0, T1, T2, R> cachedFunction(@Nonnull TriFunction<T0, T1, T2, R> function) {
        return new CachedTriFunction<>(function);
    }

    public static <R, T0, T1, T2, T3> QuadrFunction<T0, T1, T2, T3, R> cachedFunction(@Nonnull QuadrFunction<T0, T1, T2, T3, R> function) {
        return new CachedQuadrFunction<>(function);
    }

    public static class CachedFunction<T, U> implements Function<T, U> {

        private Function<T, U> function;
        private Map<T, U> cache;

        public CachedFunction(@Nonnull Function<T, U> function) {
            this.function = function;
            this.cache = new ConcurrentHashMap<>();
        }

        @Override
        public U apply(T t) {
            return cache.computeIfAbsent(t, function);
        }
    }

    public static class CachedBiFunction<T, U, R> implements BiFunction<T, U, R> {

        private BiFunction<T, U, R> function;
        private Map<Tuple2<T, U>, R> cache;

        public CachedBiFunction(@Nonnull BiFunction<T, U, R> function) {
            this.function = function;
            this.cache = new ConcurrentHashMap<>();
        }

        @Override
        public R apply(T t, U u) {
            return cache.computeIfAbsent(new Tuple2<>(t, u), (args) -> function.apply(args.getValue0(), args.getValue1()));
        }
    }

    public static class CachedTriFunction<T0, T1, T2, R> implements TriFunction<T0, T1, T2, R> {

        private TriFunction<T0, T1, T2, R> function;
        private Map<Tuple3<T0, T1, T2>, R> cache;

        public CachedTriFunction(@Nonnull TriFunction<T0, T1, T2, R> function) {
            this.function = function;
            this.cache = new ConcurrentHashMap<>();
        }

        @Override
        public R apply(T0 t0, T1 t1, T2 t2) {
            return cache.computeIfAbsent(new Tuple3<>(t0, t1, t2), (args) -> function.apply(args.getValue0(), args.getValue1(), args.getValue2()));
        }
    }

    public static class CachedQuadrFunction<T0, T1, T2, T3, R> implements QuadrFunction<T0, T1, T2, T3, R> {

        private QuadrFunction<T0, T1, T2, T3, R> function;
        private Map<Tuple4<T0, T1, T2, T3>, R> cache;

        public CachedQuadrFunction(@Nonnull QuadrFunction<T0, T1, T2, T3, R> function) {
            this.function = function;
            this.cache = new ConcurrentHashMap<>();
        }

        @Override
        public R apply(T0 t0, T1 t1, T2 t2, T3 t3) {
            return cache.computeIfAbsent(new Tuple4<>(t0, t1, t2, t3), (args) -> function.apply(args.getValue0(), args.getValue1(), args.getValue2(), args.getValue3()));
        }
    }

    public static Runnable wrapThrowable(ThrowableRunnable<? extends Throwable> runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable throwable) {
                throw new WrappedThrowable(throwable);
            }
        };
    }

    public static <T> Consumer<T> wrapThrowable(ThrowableConsumer<T, ? extends Throwable> consumer) {
        return (arg) -> {
            try {
                consumer.accept(arg);
            } catch (Throwable throwable) {
                throw new WrappedThrowable(throwable);
            }
        };
    }

    public static <T> Supplier<T> wrapThrowable(ThrowableSupplier<T, ? extends Throwable> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable throwable) {
                throw new WrappedThrowable(throwable);
            }
        };
    }

    public static <T, R> Function<T, R> wrapThrowable(ThrowableFunction<T, R, ? extends Throwable> function) {
        return (arg) -> {
            try {
                return function.apply(arg);
            } catch (Throwable throwable) {
                throw new WrappedThrowable(throwable);
            }
        };
    }

    public static <E extends Throwable> ThrowableRunnable<E> unwrapThrowable(Runnable runnable, Class<E> throwableType) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                WrappedThrowable wrappedThrowable = Exceptions.extractCause(e, WrappedThrowable.class);
                if (wrappedThrowable != null && wrappedThrowable.getCause() != null && throwableType.isAssignableFrom(wrappedThrowable.getCause().getClass())) {
                    //noinspection unchecked
                    throw (E) wrappedThrowable.getCause();
                } else {
                    Exceptions.forceThrow(e);
                }
            }
        };
    }

    public static <T, E extends Throwable> ThrowableConsumer<T, E> unwrapThrowable(Consumer<T> consumer, Class<E> throwableType) {
        return (arg) -> {
            try {
                consumer.accept(arg);
            } catch (Throwable e) {
                WrappedThrowable wrappedThrowable = Exceptions.extractCause(e, WrappedThrowable.class);
                if (wrappedThrowable != null && wrappedThrowable.getCause() != null && throwableType.isAssignableFrom(wrappedThrowable.getCause().getClass())) {
                    //noinspection unchecked
                    throw (E) wrappedThrowable.getCause();
                } else {
                    Exceptions.forceThrow(e);
                }
            }
        };
    }

    public static <T, E extends Throwable> ThrowableSupplier<T, E> unwrapThrowable(Supplier<T> supplier, Class<E> throwableType) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable e) {
                WrappedThrowable wrappedThrowable = Exceptions.extractCause(e, WrappedThrowable.class);
                if (wrappedThrowable != null && wrappedThrowable.getCause() != null && throwableType.isAssignableFrom(wrappedThrowable.getCause().getClass())) {
                    //noinspection unchecked
                    throw (E) wrappedThrowable.getCause();
                } else {
                    return Exceptions.forceThrow(e);
                }
            }
        };
    }

    public static <T, R, E extends Throwable> ThrowableFunction<T, R, E> unwrapThrowable(Function<T, R> function, Class<E> throwableType) {
        return (arg) -> {
            try {
                return function.apply(arg);
            } catch (Throwable e) {
                WrappedThrowable wrappedThrowable = Exceptions.extractCause(e, WrappedThrowable.class);
                if (wrappedThrowable != null && wrappedThrowable.getCause() != null && throwableType.isAssignableFrom(wrappedThrowable.getCause().getClass())) {
                    //noinspection unchecked
                    throw (E) wrappedThrowable.getCause();
                } else {
                    return Exceptions.forceThrow(e);
                }
            }
        };
    }
}
