package com.johnmalcolmnorwood.hashbash.rainbow.service;

import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.stream.IntStream;

/**
 *
 */
public class RainbowTableSearchService {
    private final RainbowChainGeneratorService rainbowChainGeneratorService;
    private final RainbowChainRepository rainbowChainRepository;
    private final short rainbowTableId;
    private final int chainLength;

    public RainbowTableSearchService(
            RainbowChainGeneratorService rainbowChainGeneratorService,
            RainbowChainRepository rainbowChainRepository,
            short rainbowTableId,
            int chainLength
    ) {
        this.rainbowChainGeneratorService = rainbowChainGeneratorService;
        this.rainbowChainRepository = rainbowChainRepository;
        this.rainbowTableId = rainbowTableId;
        this.chainLength = chainLength;
    }

    @Builder
    @Getter
    @Setter
    private static class RainbowChainIndexPair {
        private int chainIndex;
        private String endpoint;
    }

    /**
     * @param hash       The hash to begin generating from
     * @param chainIndex The index of the chain to begin generating from
     * @return the pair with the link index of the chain that we started generating from, and the hash that
     * resulted at the end of the chain
     */
    private RainbowChainIndexPair getChainIndexPairForHash(HashCode hash, int chainIndex) {
        RainbowChainLink rainbowChainEndingLink = rainbowChainGeneratorService.generateRainbowChainLinkFromHash(
                hash,
                chainIndex,
                chainLength - chainIndex - 1
        );

        return RainbowChainIndexPair.builder()
                .chainIndex(chainIndex)
                .endpoint(rainbowChainEndingLink.getHashedPlaintext().toString())
                .build();
    }

    /**
     * This method retrieves the chain from the database given the endpoint contained in the input index pair
     *
     * @param rainbowChainIndexPair The pair of index of the chain that we started generating from and the endpoint that resulted
     * @return The ending link of the chain, given the index started generating from and the endpoint of the chain
     */
    private RainbowChainLink getEndingLinkForEndpointIndexPair(RainbowChainIndexPair rainbowChainIndexPair) {
        RainbowChain rainbowChain = rainbowChainRepository.findByEndHashAndRainbowTableId(
                rainbowChainIndexPair.getEndpoint(),
                rainbowTableId
        );

        if (rainbowChain == null) {
            return null;
        }

        return rainbowChainGeneratorService.generateRainbowChainLinkFromPlaintext(
                rainbowChain.getStartPlaintext(),
                0,
                rainbowChainIndexPair.getChainIndex() + 1
        );
    }

    public String reverseHash(HashCode hash) {
        return IntStream.range(0, chainLength + 1).parallel()
                .boxed()
                .map(chainIndex -> getChainIndexPairForHash(hash, chainIndex))
                .map(this::getEndingLinkForEndpointIndexPair)
                .filter(Objects::nonNull)
                .filter(rainbowChainEndingLink -> rainbowChainEndingLink.getHashedPlaintext().equals(hash))
                .map(RainbowChainLink::getPlaintext)
                .findFirst()
                .orElse(null);
    }
}
