package com.johnmalcolmnorwood.hashbash.job.generate.processor;

import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import org.springframework.batch.item.ItemProcessor;


public class RainbowChainGenerateProcessor implements ItemProcessor<String, RainbowChain> {

    private RainbowTable rainbowTable;
    private RainbowChainGeneratorService rainbowChainGeneratorService;

    public RainbowChainGenerateProcessor(RainbowTable rainbowTable) {
        this.rainbowTable = rainbowTable;
        this.rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(rainbowTable.getHashFunction()),
                ReductionFunctionFamilies.defaultReductionFunctionFamily(rainbowTable.getPasswordLength(), rainbowTable.getCharacterSet())
        );
    }

    @Override
    public RainbowChain process(String chainStart) throws Exception {
        RainbowChain rainbowChain = rainbowChainGeneratorService.generateRainbowChain(
                chainStart,
                rainbowTable.getChainLength()
        );

        rainbowChain.setRainbowTable(rainbowTable);
        return rainbowChain;
    }
}
