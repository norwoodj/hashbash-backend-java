package com.johnmalcolmnorwood.hashbash.job.common.listener;


import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;


public class RainbowTableProgressListener implements StepExecutionListener {

    private final RainbowTable rainbowTable;
    private final RainbowTableRepository rainbowTableRepository;
    private final RainbowChainRepository rainbowChainRepository;

    public RainbowTableProgressListener(
            RainbowTable rainbowTable,
            RainbowTableRepository rainbowTableRepository,
            RainbowChainRepository rainbowChainRepository
    ) {
        this.rainbowTable = rainbowTable;
        this.rainbowTableRepository = rainbowTableRepository;
        this.rainbowChainRepository = rainbowChainRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        rainbowTable.setBatchExecutionId(stepExecution.getId());
        rainbowTableRepository.save(rainbowTable);
    }

    public ExitStatus afterStep(StepExecution stepExecution) {
        rainbowTable.setBatchExecutionId(stepExecution.getId());
        rainbowTableRepository.save(rainbowTable);
        long chainsGenerated = rainbowChainRepository.countByRainbowTableId(rainbowTable.getId());
        rainbowTable.setFinalChainCount(chainsGenerated);
        rainbowTableRepository.save(rainbowTable);

        return stepExecution.getExitStatus();
    }
}
