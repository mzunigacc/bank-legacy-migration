package com.example.banklegacymigration.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BatchTaskExecutorConfig {

    @Value("${batch.executor.core-pool-size}")
    private int corePoolSize;

    @Value("${batch.executor.max-pool-size}")
    private int maxPoolSize;

    @Value("${batch.executor.queue-capacity}")
    private int queueCapacity;

    @Bean
    public TaskExecutor batchTaskExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("batch-thread-");

        executor.setDaemon(true);

        executor.initialize();

        return executor;
    }
}