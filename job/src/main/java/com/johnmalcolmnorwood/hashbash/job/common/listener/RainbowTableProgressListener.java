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
        String jobExecutionStatus = stepExecution.getJobExecution()
                .getStatus()
                .toString();

        rainbowTableRepository.setStatusAndSearchStartedById(rainbowTable.getId(), jobExecutionStatus);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long chainsGenerated = rainbowChainRepository.countByRainbowTableId(rainbowTable.getId());
        rainbowTableRepository.setStatusAndFinalChainCountById(rainbowTable.getId(), chainsGenerated, "COMPLETED");
        return stepExecution.getExitStatus();
    }
}
