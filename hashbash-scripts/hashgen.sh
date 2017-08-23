#!/bin/bash

openssl dgst -md5 <(printf ${1})
