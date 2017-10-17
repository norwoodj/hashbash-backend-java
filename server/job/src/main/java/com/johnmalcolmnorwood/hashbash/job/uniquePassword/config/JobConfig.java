package com.johnmalcolmnorwood.hashbash.job.uniquePassword.config;

import com.johnmalcolmnorwood.hashbash.job.uniquePassword.processor.RainbowChainLinkGenerateProcessor;
import com.johnmalcolmnorwood.hashbash.rainbow.model.RainbowChainLink;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.Resource;
import java.util.List;


@Configuration("com.johnmalcolmnorwood.hashbash.job.uniquePassword.config.JobConfig")
public class JobConfig {

    @Value("${job.uniquePassword.batchSize}")
    private int chunkSize;

    @Value("${job.uniquePassword.numThreads}")
    private int numThreads;


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Resource(name = "org.springframework.core.task.TaskExecutor-batchJobExecution")
    private TaskExecutor batchTaskExecutor;

    @Resource(name = "org.springframework.batch.core.StepExecutionListener-rainbowTable")
    private StepExecutionListener rainbowChainUniquePasswordProgressListener;

    @Resource(name = "org.springframework.batch.item.ItemReader-rainbowTable")
    private ItemReader<String> rainbowChainGenerateReader;

    @Autowired
    private RainbowChainLinkGenerateProcessor rainbowChainLinkGenerateProcessor;

    @Resource(name = "org.springframework.batch.item.ItemWriter-uniquePassword")
    public ItemWriter<List<RainbowChainLink>> rainbowUniquePasswordItemWriter;


    @Bean(name = "org.springframework.batch.core.Step-uniquePassword")
    public Step rainbowTableUniquePasswordStep() {
        return stepBuilderFactory.get("rainbowTableUniquePassword")
                .listener(rainbowChainUniquePasswordProgressListener)
                .<String, List<RainbowChainLink>>chunk(1)
                .reader(rainbowChainGenerateReader)
                .processor(rainbowChainLinkGenerateProcessor)
                .writer(rainbowUniquePasswordItemWriter)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(numThreads)
                .build();
    }

    @Bean(name = "org.springframework.batch.core.Job-uniquePassword")
    public Job rainbowTableUniquePasswordJob() {
        return jobBuilderFactory.get("rainbowTableUniquePassword")
                .preventRestart()
                .start(rainbowTableUniquePasswordStep())
                .build();
    }
}
