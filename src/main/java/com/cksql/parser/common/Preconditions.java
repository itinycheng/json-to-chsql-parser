package com.cksql.parser.common;

import javax.annotation.Nullable;

import java.util.function.Supplier;

/** check whether throw exception. */
public class Preconditions {

    public static <T> T checkNotNull(@Nullable T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }

    public static <T extends Throwable> void checkThrow(boolean condition, Supplier<T> supplier)
            throws T {
        if (condition) {
            throw supplier.get();
        }
    }
}
