package com.johnmalcolmnorwood.hashbash.job.common.listener;


import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;


public class RainbowTableProgressListener implements StepExecutionListener {

    private final RainbowTable rainbowTable;
    private final RainbowTableRepository rainbowTableRepository;

    public RainbowTableProgressListener(
            RainbowTable rainbowTable,
            RainbowTableRepository rainbowTableRepository
    ) {
        this.rainbowTable = rainbowTable;
        this.rainbowTableRepository = rainbowTableRepository;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        rainbowTable.setBatchExecutionId(stepExecution.getId());
        rainbowTableRepository.save(rainbowTable);
    }

    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}
