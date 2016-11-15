package com.johnmalcolmnorwood.hashbash.rainbow.service;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearch;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunction;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.omg.PortableInterceptor.NON_EXISTENT;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

/**
 * These tests simulate having a chain, 5 long:
 * 0 -> hash(0) -> 1 -> hash(1) -> 2 -> hash(2) -> 3 -> hash(3) -> 4 -> hash(4)
 */
@RunWith(MockitoJUnitRunner.class)
public class RainbowTableSearchServiceTest {

    @Mock
    private RainbowChainRepository rainbowChainRepository;

    @Captor
    private ArgumentCaptor<List<String>> inputSearchList;

    private HashFunction testHashFunction;
    private RainbowChainGeneratorService generatorService;
    private RainbowTableSearchService rainbowTableSearchService;

    @Before
    public void setUp() {
        testHashFunction = TestUtils.getTestHashFunction();
        generatorService = new RainbowChainGeneratorService(
                testHashFunction,
                TestUtils.getTestReductionFunction()
        );
    }

    public void setupSearchMock(RainbowChain rainbowChain, short rainbowTableId, int tableSize) {
        when(rainbowChainRepository.findByRainbowTableIdAndEndHashIn(
                Matchers.eq(rainbowTableId),
                inputSearchList.capture()
        )).thenAnswer(invocationOnMock -> {
            Set<String> endpoints = Sets.newHashSet(inputSearchList.getValue());
            String tableEndpoint = testHashFunction.apply(String.valueOf(tableSize - 1)).toString();

            if (!endpoints.contains(tableEndpoint)) {
                return Collections.emptyList();
            }

            return Collections.singletonList(rainbowChain);
        });
    }

    private void setUpFiveLengthChain() {
        short rainbowTableId = (short) 23;
        rainbowTableSearchService = new RainbowTableSearchService(
                generatorService,
                rainbowChainRepository,
                rainbowTableId,
                5
        );

        HashCode endingHash = testHashFunction.apply("4");
        RainbowChain rainbowChain = RainbowChain.builder()
                .startPlaintext("0")
                .endHash(endingHash.toString())
                .build();

        setupSearchMock(rainbowChain, rainbowTableId, 5);
    }

    private void setUpSixLengthChain() {
        short rainbowTableId = (short) 23;
        rainbowTableSearchService = new RainbowTableSearchService(
                generatorService,
                rainbowChainRepository,
                rainbowTableId,
                6
        );

        HashCode endingHash = testHashFunction.apply("5");
        RainbowChain rainbowChain = RainbowChain.builder()
                .startPlaintext("0")
                .endHash(endingHash.toString())
                .build();

        setupSearchMock(rainbowChain, rainbowTableId, 6);
    }

    private void testForPlaintext(String plaintext) {
        HashCode searchHash = testHashFunction.apply(plaintext);
        String result = rainbowTableSearchService.reverseHash(searchHash);
        assertThat(result, is(plaintext));
    }

    @Test
    public void testReverseToEndpoint() {
        setUpFiveLengthChain();
        testForPlaintext("4");
    }

    @Test
    public void testReverseOthers() {
        setUpFiveLengthChain();
        Lists.newArrayList("0", "1", "2", "3").stream()
                .forEach(this::testForPlaintext);
    }

    /**
     * In this test, we simulate a collision, where the reduction function merges two chains, collision is in brackets
     * The test is that we search for the hash plaintext "6", but reduction(hash(6)) results in "3" and we don't save
     * that chain, and so we won't end up seeing hash(6) in the chain. This should result in nothing being returned
     * 0 -> hash(0) -> 1 -> hash(1) -> 2 -> hash(2) -> { 3 -> hash(3) -> 4 -> hash(4) }
     * x -> hash(x) -> x -> hash(x) -> 6 -> hash(6) --/
     */
    @Test
    public void testFailToReverseCollision() {
        setUpFiveLengthChain();
        HashCode searchHash = testHashFunction.apply("6");
        String result = rainbowTableSearchService.reverseHash(searchHash);
        assertThat(result, nullValue());
    }

    /**
     * In this test, we simulate a collision, but unlike in the last case, the plaintext is still in one of the stored
     * chains. In this case, we'll add a link to our chain that we've been using, so now the chain ends in hash(7). Since
     * hash(7) is the endpoint, it is never reduced, and so it stays in the table, while another chain, which collides with
     * this chain is removed
     * 0 -> hash(0) -> 1 -> hash(1) -> 2 -> hash(2) -> { 3 -> hash(3) -> 4 -> hash(4) -> hash(7) }
     * x -> hash(x) -> x -> hash(x) -> 5 -> hash(5) --/
     */
    @Test
    public void testSuccessfulReverseWithCollision() {
        setUpSixLengthChain();
        HashCode searchHash = testHashFunction.apply("5");
        String result = rainbowTableSearchService.reverseHash(searchHash);
        assertThat(result, is("5"));
    }
}