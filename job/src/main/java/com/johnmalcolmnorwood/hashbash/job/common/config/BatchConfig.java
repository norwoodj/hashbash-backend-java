package com.johnmalcolmnorwood.hashbash.job.common.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@EnableBatchProcessing
@Configuration
public class BatchConfig {

    @Value("${job.threadPoolSize}")
    private int threadPoolSize;

    @Autowired
    private JobRepository jobRepository;

    @Bean(name = "org.springframework.core.task.TaskExecutor-batchJobExecution")
    public TaskExecutor batchJobExecutionTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadPoolSize);
        taskExecutor.setMaxPoolSize(threadPoolSize);
        return taskExecutor;
    }

    @Bean(name = "org.springframework.core.launch.JobLauncher-sync")
    public JobLauncher syncBatchJobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setTaskExecutor(new SyncTaskExecutor());
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }

    @Bean(name = "org.springframework.core.launch.JobLauncher-async")
    public JobLauncher asyncBatchJobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }
}
