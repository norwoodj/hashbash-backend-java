package com.johnmalcolmnorwood.hashbash.rainbow.service;

import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunction;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RainbowChainGeneratorServiceTest {

    private HashFunction testHashFunction;
    private RainbowChainGeneratorService generatorService;

    @Before
    public void setUp() {
        testHashFunction = TestUtils.getTestHashFunction();
        generatorService = new RainbowChainGeneratorService(
                testHashFunction,
                TestUtils.getTestReductionFunction()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void generateZeroLengthChain() {
        generatorService.generateRainbowChain("blah", 0);
    }

    @Test
    public void generateOneLengthChain() {
        RainbowChain chain = generatorService.generateRainbowChain("0", 1);
        assertThat(chain.getStartPlaintext(), is("0"));
        assertThat(chain.getEndHash(), is(testHashFunction.apply("0").toString()));
    }

    @Test
    public void generateFiveLengthChain() {
        RainbowChain chain = generatorService.generateRainbowChain("0", 5);
        assertThat(chain.getStartPlaintext(), is("0"));
        assertThat(chain.getEndHash(), is(testHashFunction.apply("4").toString()));
    }
}
