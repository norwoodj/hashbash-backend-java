package com.johnmalcolmnorwood.hashbash.rainbow.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;

import java.util.Map;


public class HashFunctions {
    private static final Map<HashFunctionName, HashFunction> HASH_FUNCTION_NAME_MAP = ImmutableMap.of(
            HashFunctionName.MD5, HashFunctions::md5,
            HashFunctionName.SHA1, HashFunctions::sha1,
            HashFunctionName.SHA256, HashFunctions::sha256,
            HashFunctionName.SHA384, HashFunctions::sha384,
            HashFunctionName.SHA512, HashFunctions::sha512
    );

    public static HashFunction getHashFunctionByName(HashFunctionName hashFunctionName) {
        return HASH_FUNCTION_NAME_MAP.get(hashFunctionName);
    }

    private static HashCode md5(String plainText) {
        return Hashing.md5().hashBytes(plainText.getBytes());
    }

    private static HashCode sha1(String plainText) {
        return Hashing.sha1().hashBytes(plainText.getBytes());
    }

    private static HashCode sha256(String plainText) {
        return Hashing.sha256().hashBytes(plainText.getBytes());
    }

    private static HashCode sha384(String plainText) {
        return Hashing.sha384().hashBytes(plainText.getBytes());
    }

    private static HashCode sha512(String plainText) {
        return Hashing.sha512().hashBytes(plainText.getBytes());
    }
}
