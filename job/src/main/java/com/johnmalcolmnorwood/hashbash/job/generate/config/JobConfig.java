package com.johnmalcolmnorwood.hashbash.job.generate.config;

import com.johnmalcolmnorwood.hashbash.model.RainbowChain;
import org.springframework.batch.core.*;
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


@Configuration("com.johnmalcolmnorwood.hashbash.job.generate.config.JobConfig")
public class JobConfig {

    @Value("${job.generate.batchSize}")
    private int chunkSize;

    @Value("${job.generate.numThreads}")
    private int numThreads;


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Resource(name = "org.springframework.core.task.TaskExecutor-batchJobExecution")
    private TaskExecutor batchTaskExecutor;

    @Resource(name = "org.springframework.batch.core.StepExecutionListener-rainbowTable")
    private StepExecutionListener rainbowChainGenerateProgressListener;

    @Resource(name = "org.springframework.batch.core.ChunkListener-rainbowTable")
    private ChunkListener rainbowChainWriteListener;

    @Resource(name = "org.springframework.batch.item.ItemReader-rainbowTable")
    private ItemReader<String> rainbowChainGenerateReader;

    @Resource(name = "org.springframework.batch.item.ItemWriter-rainbowChainGenerate")
    private ItemWriter<RainbowChain> rainbowChainGenerateItemWriter;


    @Bean(name = "org.springframework.batch.core.Step-generate")
    public Step rainbowTableGenerateStep() {
        return stepBuilderFactory.get("rainbowTableGenerateStep")
                .listener(rainbowChainGenerateProgressListener)
                .<String, RainbowChain>chunk(chunkSize)
                .reader(rainbowChainGenerateReader)
                .writer(rainbowChainGenerateItemWriter)
                .listener(rainbowChainWriteListener)
                .taskExecutor(batchTaskExecutor)
                .throttleLimit(numThreads)
                .build();
    }

    @Bean(name = "org.springframework.batch.core.Job-generate")
    public Job rainbowTableGenerateJob() {
        return jobBuilderFactory.get("rainbowTableGenerate")
                .preventRestart()
                .start(rainbowTableGenerateStep())
                .build();
    }
}
