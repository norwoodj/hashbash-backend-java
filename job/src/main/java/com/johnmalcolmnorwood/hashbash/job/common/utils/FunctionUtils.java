package com.johnmalcolmnorwood.hashbash.job.common.utils;

import com.google.common.base.Throwables;

import java.util.function.Consumer;

/**
 * Utilities for using Java's new functional interfaces
 */
public class FunctionUtils {

    @FunctionalInterface
    public interface ThrowingConsumer<T> {
        void accept(T input) throws Exception;
    }

    public static <T> Consumer<T> propagateExceptions(ThrowingConsumer<T> throwingConsumer) {
        return input -> {
            try {
                throwingConsumer.accept(input);
            } catch (Exception e) {
                Throwables.propagate(e);
            }
        };
    }
}
