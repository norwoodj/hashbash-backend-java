package com.johnmalcolmnorwood.hashbash.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import lombok.Value;

@Value
public class GenerateRainbowTableRequest {
    @JsonProperty(required = true)
    private String name;

    private Integer chainLength;
    private String charset;
    private HashFunctionName hashFunction;
    private Integer numChains;
    private Integer passwordLength;
}
