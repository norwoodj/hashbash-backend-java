package com.johnmalcolmnorwood.hashbash.job.common.listener;

import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;


public class RainbowChainChunkListener implements ChunkListener {

    private final RainbowTable rainbowTable;
    private final RainbowTableRepository rainbowTableRepository;

    public RainbowChainChunkListener(RainbowTable rainbowTable, RainbowTableRepository rainbowTableRepository) {
        this.rainbowTable = rainbowTable;
        this.rainbowTableRepository = rainbowTableRepository;
    }

    @Override
    public void beforeChunk(ChunkContext context) {}

    @Override
    public void afterChunk(ChunkContext context) {
        var writeCount = context.getStepContext()
                .getStepExecution()
                .getWriteCount();

        rainbowTableRepository.setChainsGeneratedById(rainbowTable.getId(), writeCount);
    }

    @Override
    public void afterChunkError(ChunkContext context) {}
}
