package com.johnmalcolmnorwood.hashbash.rainbow.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * This class will supply random strings from a provided character set and of a certain length.
 */
public class RandomStringSupplier implements Supplier<String> {

    private final String charset;
    private final int stringLength;
    private final int numToSupply;
    private AtomicInteger numSupplied;

    /**
     * @param charset      The character set from which strings will be generated
     * @param stringLength The length of string that will be returned
     * @param numToSupply  The number of strings to supply before returning null
     */
    public RandomStringSupplier(String charset, int stringLength, int numToSupply) {
        this.charset = charset;
        this.stringLength = stringLength;
        this.numToSupply = numToSupply;
        numSupplied = new AtomicInteger();
    }

    @Override
    public String get() {
        if (numToSupply < 0 || numSupplied.incrementAndGet() < numToSupply) {
            return RandomStringUtils.random(stringLength, charset);
        }

        return null;
    }
}
