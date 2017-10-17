package com.johnmalcolmnorwood.hashbash.rainbow.function;

import com.google.common.hash.HashCode;

import java.util.function.BiFunction;


public interface ReductionFunctionFamily extends BiFunction<HashCode, Integer, String> {
}
