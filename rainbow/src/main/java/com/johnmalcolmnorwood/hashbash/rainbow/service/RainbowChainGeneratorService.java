package com.johnmalcolmnorwood.hashbash.rainbow.service;

import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunction;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;

import java.util.function.Consumer;


public class RainbowChainGeneratorService {
    private final HashFunction hashFunction;
    private final ReductionFunctionFamily reductionFunctionFamily;

    public RainbowChainGeneratorService(
            HashFunction hashFunction,
            ReductionFunctionFamily reductionFunctionFamily
    ) {
        this.hashFunction = hashFunction;
        this.reductionFunctionFamily = reductionFunctionFamily;
    }

    public RainbowChainLink generateRainbowChainLinkFromHash(
            HashCode hash,
            int nextChainIndex,
            int numLinks
    ) {
        if (numLinks > 0) {
            String plaintext = reductionFunctionFamily.apply(hash, nextChainIndex);
            return generateRainbowChainLinkFromPlaintext(plaintext, nextChainIndex + 1, numLinks, null);
        }

        return RainbowChainLink.builder()
                .hashedPlaintext(hash)
                .build();
    }

    public RainbowChainLink generateRainbowChainLinkFromPlaintext(
            String plaintext,
            int nextChainIndex,
            int numLinks
    ) {
        return generateRainbowChainLinkFromPlaintext(
                plaintext,
                nextChainIndex,
                numLinks,
                null
        );
    }

    public RainbowChainLink generateRainbowChainLinkFromPlaintext(
            String plaintext,
            int nextChainIndex,
            int numLinks,
            Consumer<RainbowChainLink> rainbowChainLinkConsumer
    ) {
        // Hash the plaintext, generating the first link
        RainbowChainLink rainbowChainLink = RainbowChainLink.builder()
                .plaintext(plaintext)
                .hashedPlaintext(hashFunction.apply(plaintext))
                .build();

        if (rainbowChainLinkConsumer != null) {
            rainbowChainLinkConsumer.accept(rainbowChainLink);
        }

        // From this link to the end of the chain
        for (int i = 0; i < numLinks - 1; ++i) {
            // Hash the current key, then reduce it to the next key
            String reducedPlaintext = reductionFunctionFamily.apply(rainbowChainLink.getHashedPlaintext(), nextChainIndex + i);
            HashCode hash = hashFunction.apply(reducedPlaintext);

            rainbowChainLink.setPlaintext(reducedPlaintext);
            rainbowChainLink.setHashedPlaintext(hash);

            if (rainbowChainLinkConsumer != null) {
                rainbowChainLinkConsumer.accept(rainbowChainLink);
            }
        }

        return rainbowChainLink;
    }

    public RainbowChain generateRainbowChain(String startPoint, int chainLength) {
        return generateRainbowChain(startPoint, chainLength, null);
    }

    public RainbowChain generateRainbowChain(String startPoint, int chainLength, Consumer<RainbowChainLink> hashPasswordConsumer) {
        if (chainLength < 1) {
            throw new IllegalArgumentException("Rainbow chain length must be greater than 0");
        }

        RainbowChainLink endingLink = generateRainbowChainLinkFromPlaintext(startPoint, 0, chainLength, hashPasswordConsumer);
        return RainbowChain.builder()
                .startPlaintext(startPoint)
                .endHash(endingLink.getHashedPlaintext().toString())
                .build();
    }
}
