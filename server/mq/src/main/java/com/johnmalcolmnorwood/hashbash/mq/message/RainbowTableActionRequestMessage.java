package com.johnmalcolmnorwood.hashbash.mq.message;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RainbowTableActionRequestMessage {
    private short rainbowTableId;
}
