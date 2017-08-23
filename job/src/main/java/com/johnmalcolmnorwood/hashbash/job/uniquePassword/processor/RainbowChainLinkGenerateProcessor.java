package com.johnmalcolmnorwood.hashbash.job.uniquePassword.processor;

import com.google.common.collect.Lists;
import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
public class RainbowChainLinkGenerateProcessor implements ItemProcessor<String, List<RainbowChainLink>> {

    private final RainbowTable rainbowTable;
    private final RainbowChainGeneratorService rainbowChainGeneratorService;

    @Autowired
    public RainbowChainLinkGenerateProcessor(RainbowTableWrapper rainbowTableWrapper) {
        rainbowTable = rainbowTableWrapper.getRainbowTable();
        rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(rainbowTable.getHashFunction()),
                ReductionFunctionFamilies.defaultReductionFunctionFamily(rainbowTable.getPasswordLength(), rainbowTable.getCharacterSet())
        );
    }

    @Override
    public List<RainbowChainLink> process(String startPlaintext) throws Exception {
        List<RainbowChainLink> rainbowChainLinks = Lists.newArrayList();
        rainbowChainGeneratorService.generateRainbowChain(
                startPlaintext,
                rainbowTable.getChainLength(),
                rainbowChainLink -> rainbowChainLinks.add(RainbowChainLink.builder()
                        .plaintext(rainbowChainLink.getPlaintext())
                        .hashedPlaintext(rainbowChainLink.getHashedPlaintext())
                        .build()
                )
        );

        return rainbowChainLinks;
    }
}
