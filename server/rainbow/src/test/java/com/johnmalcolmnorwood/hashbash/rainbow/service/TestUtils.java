package com.johnmalcolmnorwood.hashbash.rainbow.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunction;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TestUtils {

    private static final HashFunction TEST_HASH_FUNCTION = HashFunctions.getHashFunctionByName(HashFunctionName.MD5);
    private static Map<HashCode, Map<Integer, String>> REDUCTION_MAP = IntStream.range(0, 5)
            .boxed()
            .collect(Collectors.toMap(
                    i -> getTestHashFunction().apply(String.valueOf(i)),
                    i -> ImmutableMap.of(i, String.valueOf(i + 1))
            ));

    public static HashFunction getTestHashFunction() {
        return TEST_HASH_FUNCTION;
    }

    public static ReductionFunctionFamily getTestReductionFunction() {
        return (hash, i) -> REDUCTION_MAP.getOrDefault(hash, Maps.newHashMap()).getOrDefault(i, "7");
    }
}
