package com.johnmalcolmnorwood.hashbash.rainbow.model;

import com.google.common.hash.HashCode;
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
    private HashCode hashedPlaintext;
}
