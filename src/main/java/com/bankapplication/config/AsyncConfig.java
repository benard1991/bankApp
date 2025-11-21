package com.bankapplication.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);      // number of threads
        executor.setMaxPoolSize(5);       // max threads
        executor.setQueueCapacity(50);    // max queued tasks
        executor.setThreadNamePrefix("Email-");
        executor.initialize();
        return executor;
    }
}