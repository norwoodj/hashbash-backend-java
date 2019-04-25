package com.johnmalcolmnorwood.hashbash.repository.model;

import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RainbowTableSearchResults {
    private RainbowTableSearchStatus searchStatus;
    private long count;
}

