package com.johnmalcolmnorwood.hashbash.api.model;

import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchResponse {
    private String hash;
    private long searchId;
    private RainbowTableSearchStatus status;
}
