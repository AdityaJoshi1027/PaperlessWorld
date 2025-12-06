package com.archive.paperlessworld.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Async Service demonstrating threading and concurrency
 * Shows @Async annotations, CompletableFuture, thread-safe collections,
 * and concurrent utilities
 */
@Service
public class AsyncTaskService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskService.class);

    // Thread-safe counter using AtomicInteger
    private final AtomicInteger taskCounter = new AtomicInteger(0);

    // Thread-safe map for storing task results
    private final ConcurrentHashMap<String, String> taskResults = new ConcurrentHashMap<>();

    // Thread-safe list for notifications
    private final CopyOnWriteArrayList<String> notifications = new CopyOnWriteArrayList<>();

    // ReadWriteLock for document cache
    private final ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    private final ConcurrentHashMap<String, Object> documentCache = new ConcurrentHashMap<>();

    /**
     * Async method to send email notification
     * Uses specialized notificationExecutor
     */
    @Async("notificationExecutor")
    public CompletableFuture<String> sendEmailAsync(String recipient, String subject, String body) {
        try {
            logger.info("Sending email to {} - Thread: {}", recipient, Thread.currentThread().getName());
            
            // Simulate email sending delay
            Thread.sleep(2000);
            
            String result = String.format("Email sent to %s with subject: %s", recipient, subject);
            taskResults.put("email_" + taskCounter.incrementAndGet(), result);
            
            logger.info("Email sent successfully to {}", recipient);
            return CompletableFuture.completedFuture(result);
            
        } catch (InterruptedException e) {
            logger.error("Email sending interrupted", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Email sending failed");
        }
    }

    /**
     * Async method for document processing
     * Uses dedicated documentProcessingExecutor
     */
    @Async("documentProcessingExecutor")
    public CompletableFuture<String> processDocumentAsync(String documentId, String operation) {
        try {
            logger.info("Processing document {} - Operation: {} - Thread: {}", 
                       documentId, operation, Thread.currentThread().getName());
            
            // Simulate heavy processing
            Thread.sleep(3000);
            
            // Store result in thread-safe map
            String result = String.format("Document %s processed with operation: %s", documentId, operation);
            taskResults.put("doc_" + documentId, result);
            
            // Update cache with write lock
            cacheLock.writeLock().lock();
            try {
                documentCache.put(documentId, result);
            } finally {
                cacheLock.writeLock().unlock();
            }
            
            logger.info("Document processing completed for {}", documentId);
            return CompletableFuture.completedFuture(result);
            
        } catch (InterruptedException e) {
            logger.error("Document processing interrupted", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Processing failed");
        }
    }

    /**
     * Async method for sending notifications
     * Uses dedicated notificationExecutor
     */
    @Async("notificationExecutor")
    public CompletableFuture<Void> sendNotificationAsync(String userId, String message) {
        try {
            logger.info("Sending notification to user {} - Thread: {}", userId, Thread.currentThread().getName());
            
            // Add to thread-safe list
            notifications.add(String.format("User: %s, Message: %s, Time: %s", 
                                           userId, message, System.currentTimeMillis()));
            
            // Simulate notification delay
            Thread.sleep(1000);
            
            logger.info("Notification sent to user {}", userId);
            return CompletableFuture.completedFuture(null);
            
        } catch (InterruptedException e) {
            logger.error("Notification sending interrupted", e);
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Async batch processing of multiple documents
     * Demonstrates CompletableFuture.allOf for parallel execution
     */
    @Async("documentProcessingExecutor")
    public CompletableFuture<String> batchProcessDocuments(String... documentIds) {
        logger.info("Starting batch processing of {} documents - Thread: {}", 
                   documentIds.length, Thread.currentThread().getName());
        
        // Create array of CompletableFutures
        CompletableFuture<String>[] futures = new CompletableFuture[documentIds.length];
        
        for (int i = 0; i < documentIds.length; i++) {
            final String docId = documentIds[i];
            futures[i] = processDocumentAsync(docId, "batch");
        }
        
        // Wait for all to complete
        return CompletableFuture.allOf(futures)
            .thenApply(v -> {
                logger.info("Batch processing completed for all documents");
                return "Batch processing completed for " + documentIds.length + " documents";
            });
    }

    /**
     * Get document from cache with read lock
     * Demonstrates ReadWriteLock usage
     */
    public Object getDocumentFromCache(String documentId) {
        cacheLock.readLock().lock();
        try {
            logger.debug("Reading document {} from cache - Thread: {}", documentId, Thread.currentThread().getName());
            return documentCache.get(documentId);
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    /**
     * Clear cache with write lock
     */
    public void clearCache() {
        cacheLock.writeLock().lock();
        try {
            logger.info("Clearing document cache - Thread: {}", Thread.currentThread().getName());
            documentCache.clear();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    /**
     * Get task count (thread-safe)
     */
    public int getTaskCount() {
        return taskCounter.get();
    }

    /**
     * Get all task results (returns copy of map)
     */
    public ConcurrentHashMap<String, String> getTaskResults() {
        return new ConcurrentHashMap<>(taskResults);
    }

    /**
     * Get all notifications (thread-safe list)
     */
    public CopyOnWriteArrayList<String> getNotifications() {
        return new CopyOnWriteArrayList<>(notifications);
    }

    /**
     * Get cache size (thread-safe)
     */
    public int getCacheSize() {
        cacheLock.readLock().lock();
        try {
            return documentCache.size();
        } finally {
            cacheLock.readLock().unlock();
        }
    }
}
