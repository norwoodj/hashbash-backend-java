#!/usr/bin/env python
from hashlib import md5, sha1, sha256, sha384, sha512


class HashFunction:
    MD5 = "MD5"
    SHA1 = "SHA1"
    SHA256 = "SHA256"
    SHA384 = "SHA384"
    SHA512 = "SHA512"


class SearchStatus:
    QUEUED = "QUEUED"
    STARTED = "STARTED"
    FOUND = "FOUND"
    NOT_FOUND = "NOT_FOUND"


_HASH_FN_FOR_NAME = {
    HashFunction.MD5: md5,
    HashFunction.SHA1: sha1,
    HashFunction.SHA256: sha256,
    HashFunction.SHA384: sha384,
    HashFunction.SHA512: sha512,
}


def get_hash_function_for_name(hash_fn):
    if hash_fn not in _HASH_FN_FOR_NAME:
        raise ValueError(f"No Registered Hash Function with name {hash_fn}")

    return _HASH_FN_FOR_NAME[hash_fn]
