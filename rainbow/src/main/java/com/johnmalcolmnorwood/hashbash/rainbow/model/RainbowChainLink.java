package com.johnmalcolmnorwood.hashbash.rainbow.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.util.JsonToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A RainbowChainLink is one link in a rainbow chain. It contains a hash and a plaintext, whose relationship is that
 * hash(plaintext) = hashedPlaintext
 * A RainbowChain is then a series of links: (plaintext, hash) -> (plaintext, hash) -> ...
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RainbowChainLink {
    private String plaintext;

    @JsonSerialize(using = JsonToStringSerializer.class)
    private HashCode hashedPlaintext;
}
