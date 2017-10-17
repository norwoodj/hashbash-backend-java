package com.johnmalcolmnorwood.hashbash.rainbow.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunction;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;

import java.util.HashMap;
import java.util.Map;


public class TestUtils {

    private static Map<HashCode, Map<Integer, String>> REDUCTION_MAP = new HashMap<HashCode, Map<Integer, String>>() {{
        put(getTestHashFunction().apply("0"), ImmutableMap.of(0, "1"));
        put(getTestHashFunction().apply("1"), ImmutableMap.of(1, "2"));
        put(getTestHashFunction().apply("2"), ImmutableMap.of(2, "3"));
        put(getTestHashFunction().apply("3"), ImmutableMap.of(3, "4"));
        put(getTestHashFunction().apply("4"), ImmutableMap.of(4, "5"));
    }};

    public static HashFunction getTestHashFunction() {
        return HashFunctions.getHashFunctionByName(HashFunctionName.MD5);
    }

    public static ReductionFunctionFamily getTestReductionFunction() {
        return (hash, i) -> REDUCTION_MAP.getOrDefault(hash, Maps.newHashMap()).getOrDefault(i, "7");
    }
}
