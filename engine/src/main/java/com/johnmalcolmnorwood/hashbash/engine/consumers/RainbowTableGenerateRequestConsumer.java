package com.johnmalcolmnorwood.hashbash.engine.consumers;

import com.google.common.collect.ImmutableMap;
import com.johnmalcolmnorwood.hashbash.engine.exchanges.TaskExchange;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.mq.Queues;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableActionRequestMessage;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import javax.annotation.Resource;
import javax.batch.operations.JobRestartException;


@EnableBinding(TaskExchange.class)
public class RainbowTableGenerateRequestConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RainbowTableGenerateRequestConsumer.class);

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @Resource(name = "org.springframework.core.launch.JobLauncher-sync")
    private JobLauncher jobLauncher;

    @Resource(name = "org.springframework.batch.core.Job-generate")
    private Job rainbowTableGenerateJob;


    private void runGenerateTableJob(RainbowTable rainbowTable) {
        try {
            JobParameters jobParameters = new JobParameters(ImmutableMap.of("rainbowTableId", new JobParameter(Long.valueOf(rainbowTable.getId()))));
            jobLauncher.run(rainbowTableGenerateJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobParametersInvalidException | JobRestartException e) {
            throw new RuntimeException(e);
        } catch (org.springframework.batch.core.repository.JobRestartException e) {
            e.printStackTrace();
        }
    }

    private RainbowTable retrieveRainbowTable(short rainbowTableId) {
        var rainbowTable = rainbowTableRepository.findById(rainbowTableId);

        if (rainbowTable.isEmpty()) {
            throw new RuntimeException(String.format("No Rainbow Table with id %s", rainbowTableId));
        }

        return rainbowTable.get();
    }

    private RainbowTable handleGenerateRainbowTable(RainbowTableActionRequestMessage generateRainbowTableRequestMessage) {
        RainbowTable rainbowTable = retrieveRainbowTable(generateRainbowTableRequestMessage.getRainbowTableId());
        runGenerateTableJob(rainbowTable);
        return rainbowTable;
    }

    @StreamListener(value = Queues.RAINBOW_TABLE_GENERATE_REQUESTS)
    public void rainbowTableGenerateRequest(RainbowTableActionRequestMessage rainbowTableActionRequestMessage) {
        LOGGER.info(
                "Received Rainbow Table Generate Request: for rainbow table {}",
                rainbowTableActionRequestMessage.getRainbowTableId()
        );

        handleGenerateRainbowTable(rainbowTableActionRequestMessage);
    }
}
