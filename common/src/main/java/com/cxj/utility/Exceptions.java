/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *
 * @author Administrator
 */
public class Exceptions {

    private static Throwable throwable;

    private Exceptions() throws Throwable {
        throw throwable;
    }

    public static synchronized <T> T forceThrow(Throwable throwable) {
        Exceptions.throwable = throwable;
        try {
            Exceptions.class.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
        } finally {
            Exceptions.throwable = null;
        }
        return null;
    }

    public static void prependCurrentStackTrace(Throwable t, Object context) {
        final StackTraceElement[] innerFrames = t.getStackTrace();
        final StackTraceElement[] outerFrames = new Throwable().getStackTrace();
        final StackTraceElement[] frames = new StackTraceElement[innerFrames.length + outerFrames.length];
        System.arraycopy(innerFrames, 0, frames, 0, innerFrames.length);
        frames[innerFrames.length] = new StackTraceElement(context.getClass().getName(),
                "<placeholder>", "Changed Threads", -1);
        System.arraycopy(outerFrames, 1, frames, innerFrames.length + 1, outerFrames.length - 1);
        t.setStackTrace(frames);
    }

// Checked exception masker
    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T maskException(Throwable t) throws T {
        throw (T) t;
    }

    public static <T extends Throwable> T createExceptionWithContext(Throwable t, Object context) throws T {
        prependCurrentStackTrace(t, context);
        throw (T) t;
    }

    public static <T extends Throwable> T extractCause(Throwable e, Class<T> type) {
        if (e == null) {
            return null;
        }
        if (type.isAssignableFrom(e.getClass())) {
            return (T) e;
        }
        return extractCause(e.getCause(), type);
    }

    public static String toString(Throwable e) {
        if (e == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            e.printStackTrace(pw);
        }
        return sw.toString();
    }
}
