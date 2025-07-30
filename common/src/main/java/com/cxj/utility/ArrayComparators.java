/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author Administrator
 */
public class ArrayComparators {

    public static final Comparator<int[]> INT_ARRAY_COMPARATOR = new IntComparator();
    public static final Comparator<long[]> LONG_ARRAY_COMPARATOR = new LongComparator();

    public static class GeneralComparator<T extends Comparable<T>> implements Comparator<T[]>, Serializable {

        private static final long serialVersionUID = 871234393723756206L;

        @Override
        public int compare(T[] o1, T[] o2) {
            if (o1.length == 0 && o2.length == 0) {
                return 0;
            } else if (o1.length == 0) {
                return -1;
            } else if (o2.length == 0) {
                return 1;
            } else {
                int cmpSz = Math.min(o1.length, o2.length);
                for (int i = 0; i < cmpSz; i++) {
                    if (!Objects.equals(o1[i], o2[i])) {
                        if (o1[i] == null) {
                            return -1;
                        } else if (o2[i] == null) {
                            return 1;
                        } else {
                            return o1[i].compareTo(o2[i]);
                        }
                    }
                }
                if (o1.length > o2.length) {
                    return -1;
                } else if (o1.length < o2.length) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    public static class IntComparator implements Comparator<int[]>, Serializable {

        private static final long serialVersionUID = 3463039800367470916L;

        @Override
        public int compare(int[] o1, int[] o2) {
            if (o1.length == 0 && o2.length == 0) {
                return 0;
            } else if (o1.length == 0) {
                return -1;
            } else if (o2.length == 0) {
                return 1;
            } else {
                int cmpSz = Math.min(o1.length, o2.length);
                for (int i = 0; i < cmpSz; i++) {
                    if (o1[i] != o2[i]) {
                        return o1[i] < o2[i] ? -1 : 1;
                    }
                }
                if (o1.length > o2.length) {
                    return -1;
                } else if (o1.length < o2.length) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }

    public static class LongComparator implements Comparator<long[]>, Serializable {

        private static final long serialVersionUID = 5329800916678678109L;

        @Override
        public int compare(long[] o1, long[] o2) {
            if (o1.length == 0 && o2.length == 0) {
                return 0;
            } else if (o1.length == 0) {
                return -1;
            } else if (o2.length == 0) {
                return 1;
            } else {
                int cmpSz = Math.min(o1.length, o2.length);
                for (int i = 0; i < cmpSz; i++) {
                    if (o1[i] != o2[i]) {
                        return o1[i] < o2[i] ? -1 : 1;
                    }
                }
                if (o1.length > o2.length) {
                    return -1;
                } else if (o1.length < o2.length) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }
}
