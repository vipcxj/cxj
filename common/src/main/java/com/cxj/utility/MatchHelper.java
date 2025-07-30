/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class MatchHelper {
    
    public static final int COMPARE_RESULT_GREATER = 1;
    public static final int COMPARE_RESULT_EQUAL = 0;
    public static final int COMPARE_RESULT_LESS = -1;
    public static final int COMPARE_RESULT_UNDEFINE = Integer.MAX_VALUE;
    
    public static int compareNull(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return COMPARE_RESULT_EQUAL;
        }
        if (o1 == null) {
            return COMPARE_RESULT_LESS;
        }
        if (o2 == null) {
            return COMPARE_RESULT_GREATER;
        }
        return COMPARE_RESULT_UNDEFINE;
    }
    
    public static int compareObject(Comparable o1, Comparable o2) {
        int result = compareNull(o1, o2);
        if (result != COMPARE_RESULT_UNDEFINE) {
            return result;
        } else {
            return o1.compareTo(o2);
        }
    }
    
    public static int compareObjectPairs(Comparable... pairs) {
        if (pairs.length % 2 != 0) {
            throw new IllegalArgumentException("The arguments count must be even!");
        }
        if (pairs.length <= 0) {
            throw new IllegalArgumentException("The arguments count must be at least 2!");
        }
        int result = COMPARE_RESULT_UNDEFINE;
        for (int i = 0; i < pairs.length / 2; i++) {
            result = compareObject(pairs[i * 2], pairs[i * 2 + 1]);
            if (result != COMPARE_RESULT_EQUAL) {
                return result;
            }
        }
        return result;
    }

    public static <T> boolean matchTo(Iterable<T> a, Iterable<T> b, MatchTo<T> matcher) {
        return matchAllTo(a, b, matcher);
    }

    public static <T> boolean matchAllTo(Iterable<T> a, Iterable<T> b, MatchTo<T> matcher) {
        Iterator<T> iterA = a.iterator(), iterB = b.iterator();
        for (; iterA.hasNext() && iterB.hasNext();) {
            T objA = iterA.next();
            T objB = iterB.next();
            if (!matcher.matchTo(objA, objB)) {
                return false;
            }
        }
        return !iterA.hasNext() && !iterB.hasNext();
    }

    public static <T> boolean matchAnyTo(Iterable<T> a, Iterable<T> b, MatchTo<T> matcher) {
        Iterator<T> iterA = a.iterator(), iterB = b.iterator();
        for (; iterA.hasNext() && iterB.hasNext();) {
            T objA = iterA.next();
            T objB = iterB.next();
            if (matcher.matchTo(objA, objB)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean matchToNoOrder(Iterable<T> a, Iterable<T> b, MatchTo<T> matcher) {
        return matchAllToNoOrder(a, b, matcher);
    }

    public static <T> boolean matchAllToNoOrder(Iterable<T> a, Iterable<T> b, MatchTo<T> matcher) {
        List<T> listB = new LinkedList<>();
        for (Iterator<T> iterator = b.iterator(); iterator.hasNext();) {
            listB.add(iterator.next());
        }
        for (T objA : a) {
            boolean match = false;
            for (Iterator<T> iterB = listB.iterator(); iterB.hasNext();) {
                T objB = iterB.next();
                if (matcher.matchTo(objA, objB)) {
                    match = true;
                    iterB.remove();
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean matchAnyToNoOrder(Iterable<T> a, Iterable<T> b, MatchTo<T> matcher) {
        for (T objA : a) {
            for (T objB : b) {
                if (matcher.matchTo(objA, objB)) {
                    return true;
                }
            }
        }
        return false;
    }

    public interface MatchTo<T> {

        boolean matchTo(T a, T b);
    }

    public static <T> ReverseComparator<T> reverse(Comparator<T> comparator) {
        return new ReverseComparator<>(comparator);
    }

    public static class ReverseComparator<T> implements Comparator<T> {
        private final Comparator<T> wrapped;

        public ReverseComparator(Comparator<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public int compare(T o1, T o2) {
            return - wrapped.compare(o1, o2);
        }
    }
}
