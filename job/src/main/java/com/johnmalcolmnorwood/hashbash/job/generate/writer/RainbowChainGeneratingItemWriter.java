package com.johnmalcolmnorwood.hashbash.job.generate.writer;

import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.batch.item.ItemWriter;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class RainbowChainGeneratingItemWriter implements ItemWriter<String> {

    private final Timer chainGenerationTimer;
    private final Timer chainWriteTimer;
    private final Counter chainsCreatedCounter;
    private final RainbowTable rainbowTable;
    private final RainbowChainGeneratorService rainbowChainGeneratorService;
    private final ItemWriter<RainbowChain> batchItemWriter;

    public RainbowChainGeneratingItemWriter(
            ItemWriter<RainbowChain> batchItemWriter,
            RainbowTableWrapper rainbowTableWrapper,
            MeterRegistry meterRegistry,
            int batchSize
    ) {
        this.batchItemWriter = batchItemWriter;
        rainbowTable = rainbowTableWrapper.getRainbowTable();
        chainGenerationTimer = Timer.builder("rainbow_chain_generate")
                .tag("batch_size", String.valueOf(batchSize))
                .tag("chain_length", String.valueOf(rainbowTable.getChainLength()))
                .tag("hash_function", String.valueOf(rainbowTable.getHashFunction()))
                .tag("rainbow_table_id", String.valueOf(rainbowTable.getId()))
                .register(meterRegistry);

        chainWriteTimer = Timer.builder("rainbow_chain_write")
                .tag("batch_size", String.valueOf(batchSize))
                .tag("chain_length", String.valueOf(rainbowTableWrapper.getRainbowTable().getChainLength()))
                .tag("hash_function", String.valueOf(rainbowTableWrapper.getRainbowTable().getHashFunction()))
                .tag("rainbow_table_id", String.valueOf(rainbowTableWrapper.getRainbowTable().getId()))
                .register(meterRegistry);

        chainsCreatedCounter = Counter.builder("rainbow_chain_created")
                .tag("batch_size", String.valueOf(batchSize))
                .tag("chain_length", String.valueOf(rainbowTableWrapper.getRainbowTable().getChainLength()))
                .tag("hash_function", String.valueOf(rainbowTableWrapper.getRainbowTable().getHashFunction()))
                .tag("rainbow_table_id", String.valueOf(rainbowTableWrapper.getRainbowTable().getId()))
                .register(meterRegistry);

        rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(rainbowTable.getHashFunction()),
                ReductionFunctionFamilies.defaultReductionFunctionFamily(rainbowTable.getPasswordLength(), rainbowTable.getCharacterSet())
        );
    }

    @Override
    public void write(List<? extends String> startPasswords) throws Exception {
        var generateSample = Timer.start();
        var rainbowChains = startPasswords.stream()
                .map(chainStart -> rainbowChainGeneratorService.generateRainbowChain(chainStart, rainbowTable.getChainLength()))
                .sorted(Comparator.comparing(RainbowChain::getEndHash))
                .collect(Collectors.toList());

        rainbowChains.forEach(rainbowChain -> rainbowChain.setRainbowTableId(rainbowTable.getId()));
        generateSample.stop(chainGenerationTimer);

        var writeSample = Timer.start();
        batchItemWriter.write(rainbowChains);
        writeSample.stop(chainWriteTimer);

        chainsCreatedCounter.increment(startPasswords.size());
    }
}
