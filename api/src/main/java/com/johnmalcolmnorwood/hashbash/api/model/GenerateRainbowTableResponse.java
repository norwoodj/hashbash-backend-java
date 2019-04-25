package com.johnmalcolmnorwood.hashbash.api.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenerateRainbowTableResponse {
    private short rainbowTableId;
    private String message;
}
