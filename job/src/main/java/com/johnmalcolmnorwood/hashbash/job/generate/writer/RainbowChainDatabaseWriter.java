package com.johnmalcolmnorwood.hashbash.job.generate.writer;

import com.johnmalcolmnorwood.hashbash.job.common.utils.RainbowTableWrapper;
import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import java.util.List;


public class RainbowChainDatabaseWriter extends JdbcBatchItemWriter<RainbowChain> {

    private final Timer chainWriteTimer;

    public RainbowChainDatabaseWriter(
            RainbowTableWrapper rainbowTableWrapper,
            MeterRegistry meterRegistry
    ) {
        chainWriteTimer = Timer.builder("rainbow.chain.write")
                .tag("rainbow-table-id", String.valueOf(rainbowTableWrapper.getRainbowTable().getId()))
                .tag("chain-length", String.valueOf(rainbowTableWrapper.getRainbowTable().getChainLength()))
                .tag("hash-function", String.valueOf(rainbowTableWrapper.getRainbowTable().getHashFunction()))
                .register(meterRegistry);
    }

    @Override
    public void write(List<? extends RainbowChain> items) throws Exception {
        var sample = Timer.start();
        super.write(items);
        sample.stop(chainWriteTimer);
    }
}
