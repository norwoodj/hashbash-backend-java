package com.johnmalcolmnorwood.hashbash.rainbow.service;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public String reverseHash(HashCode hash) {
        Map<String, RainbowChainIndexPair> endpointRainbowChainIndexMap = IntStream.range(0, chainLength).parallel()
                .boxed()
                .map(chainIndex -> getChainIndexPairForHash(hash, chainIndex))
                .collect(Collectors.toMap(RainbowChainIndexPair::getEndpoint, Function.identity(), (k, v) -> k));


        List<List<String>> endpointBatches = Lists.partition(
                new ArrayList<>(endpointRainbowChainIndexMap.keySet()),
                1000
        );

        return endpointBatches.stream().parallel()
                .map(endpointBatch -> rainbowChainRepository.findByRainbowTableIdAndEndHashIn(rainbowTableId, endpointBatch))
                .flatMap(Collection::stream)
                .map(rainbowChain -> rainbowChainGeneratorService.generateRainbowChainLinkFromPlaintext(
                        rainbowChain.getStartPlaintext(),
                        0,
                        endpointRainbowChainIndexMap.get(rainbowChain.getEndHash()).getChainIndex() + 1
                ))
                .filter(rainbowChainEndingLink -> rainbowChainEndingLink.getHashedPlaintext().equals(hash))
                .map(RainbowChainLink::getPlaintext)
                .findFirst()
                .orElse(null);
    }
}
