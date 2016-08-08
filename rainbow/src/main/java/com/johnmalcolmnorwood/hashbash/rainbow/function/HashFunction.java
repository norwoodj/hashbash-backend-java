package com.johnmalcolmnorwood.hashbash.rainbow.function;

import com.google.common.hash.HashCode;

import java.util.function.Function;


public interface HashFunction extends Function<String, HashCode> {
}
