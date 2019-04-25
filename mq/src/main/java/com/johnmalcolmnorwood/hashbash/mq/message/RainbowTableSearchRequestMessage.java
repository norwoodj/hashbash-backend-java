package com.johnmalcolmnorwood.hashbash.mq.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RainbowTableSearchRequestMessage {
    private String hash;
    private short rainbowTableId;
    private long searchId;
}
