package com.archive.paperlessworld.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Async Configuration for Threading and Concurrency
 * Demonstrates ExecutorService and ThreadPoolTaskExecutor configuration
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Fixed Thread Pool ExecutorService
     * Used for background tasks with a fixed number of threads
     */
    @Bean(name = "fixedThreadPool")
    public ExecutorService fixedThreadPoolExecutor() {
        return Executors.newFixedThreadPool(5);
    }

    /**
     * Cached Thread Pool ExecutorService
     * Creates new threads as needed, reuses previously constructed threads
     */
    @Bean(name = "cachedThreadPool")
    public ExecutorService cachedThreadPoolExecutor() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Single Thread Executor
     * Ensures tasks are executed sequentially in a single background thread
     */
    @Bean(name = "singleThreadExecutor")
    public ExecutorService singleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    /**
     * Spring's ThreadPoolTaskExecutor for @Async methods
     * Demonstrates Spring's async capabilities with configurable thread pool
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);           // Minimum number of threads
        executor.setMaxPoolSize(10);           // Maximum number of threads
        executor.setQueueCapacity(100);        // Queue size for pending tasks
        executor.setThreadNamePrefix("Async-"); // Thread name prefix
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * Email Notification Executor
     * Dedicated executor for email/notification tasks
     */
    @Bean(name = "notificationExecutor")
    public Executor notificationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("Notification-");
        executor.initialize();
        return executor;
    }

    /**
     * Document Processing Executor
     * Dedicated executor for heavy document processing tasks
     */
    @Bean(name = "documentProcessingExecutor")
    public Executor documentProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(200);
        executor.setThreadNamePrefix("DocProcess-");
        executor.initialize();
        return executor;
    }
}
