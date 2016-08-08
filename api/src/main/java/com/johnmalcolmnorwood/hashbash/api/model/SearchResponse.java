package com.johnmalcolmnorwood.hashbash.api.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchResponse {
    private String hash;
    private String password;
}
