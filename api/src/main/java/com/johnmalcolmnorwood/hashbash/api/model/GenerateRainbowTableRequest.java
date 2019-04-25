package com.johnmalcolmnorwood.hashbash.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateRainbowTableRequest {
    @JsonProperty(required = true)
    private String name;

    private Integer chainLength;
    private String charset;
    private HashFunctionName hashFunction;
    private Integer numChains;
    private Integer passwordLength;
}
