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

    /**
     * This function wraps consumers that throw an exception and returns a Consumer that will perform the same action as
     * the input consumer, but propagate any exceptions produced as RuntimeExceptions
     *
     * @param throwingConsumer The input consumer that may throw an exception
     * @param <T>              The type of item that the consumer consumes
     * @return a Consumer that will perform the same action as the input consumer, but propagate any exceptions produced as RuntimeExceptions
     */
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
