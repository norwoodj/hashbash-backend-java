package com.johnmalcolmnorwood.hashbash.api.controller;

import com.google.common.collect.Lists;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/rainbow-chain")
public class RainbowChainController {

    @RequestMapping("/")
    public ResponseEntity<List<RainbowChainLink>> generateRainbowChain(
            @RequestParam String plaintext,
            @RequestParam(defaultValue = "10000") int chainLength,
            @RequestParam(defaultValue = "MD5") HashFunctionName hashFunctionName,
            @RequestParam(defaultValue = "abcdefghijklmnopqrstuvwxyz") String charset,
            @RequestParam(defaultValue = "8") int passwordLength
    ) {
        ReductionFunctionFamily reductionFunctionFamily = ReductionFunctionFamilies.defaultReductionFunctionFamily(
                passwordLength,
                charset
        );

        RainbowChainGeneratorService rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(hashFunctionName),
                reductionFunctionFamily
        );

        List<RainbowChainLink> chain = Lists.newArrayList();
        Consumer<RainbowChainLink> rainbowChainLinkConsumer = link -> chain.add(RainbowChainLink.builder()
                .plaintext(link.getPlaintext())
                .hashedPlaintext(link.getHashedPlaintext())
                .build());

        rainbowChainGeneratorService.generateRainbowChainLinkFromPlaintext(
                plaintext,
                0,
                chainLength,
                rainbowChainLinkConsumer
        );

        return ResponseEntity.ok(chain);
    }
}
