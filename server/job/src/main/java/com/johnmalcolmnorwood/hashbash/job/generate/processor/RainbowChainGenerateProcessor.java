package com.johnmalcolmnorwood.hashbash.job.generate.processor;

import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@StepScope
public class RainbowChainGenerateProcessor implements ItemProcessor<String, RainbowChain> {

    private final RainbowTable rainbowTable;
    private final RainbowChainGeneratorService rainbowChainGeneratorService;

    @Autowired
    public RainbowChainGenerateProcessor(RainbowTableWrapper rainbowTableWrapper) {
        rainbowTable = rainbowTableWrapper.getRainbowTable();
        rainbowChainGeneratorService = new RainbowChainGeneratorService(
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
