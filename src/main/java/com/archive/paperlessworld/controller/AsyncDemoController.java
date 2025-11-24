package com.archive.paperlessworld.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.archive.paperlessworld.service.AsyncTaskService;

/**
 * REST Controller demonstrating async operations and threading
 * Shows real-world use cases of concurrency in web applications
 */
@RestController
@RequestMapping("/api/async")
public class AsyncDemoController {

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private ExecutorService fixedThreadPool;

    @Autowired
    private ExecutorService cachedThreadPool;

    /**
     * Demo: Send async email notification
     * GET /api/async/send-email?recipient=user@example.com&subject=Test
     */
    @GetMapping("/send-email")
    public ResponseEntity<Map<String, Object>> sendEmailDemo(
            @RequestParam String recipient,
            @RequestParam String subject) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Trigger async email sending
        CompletableFuture<String> emailFuture = asyncTaskService.sendEmailAsync(
            recipient, subject, "This is a demo email from Paperless World"
        );
        
        response.put("success", true);
        response.put("message", "Email is being sent asynchronously");
        response.put("recipient", recipient);
        response.put("threadInfo", "Processing in background thread");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demo: Process document asynchronously
     * POST /api/async/process-document
     */
    @PostMapping("/process-document")
    public ResponseEntity<Map<String, Object>> processDocumentDemo(@RequestBody Map<String, String> request) {
        String documentId = request.get("documentId");
        String operation = request.getOrDefault("operation", "index");
        
        Map<String, Object> response = new HashMap<>();
        
        // Trigger async document processing
        CompletableFuture<String> processFuture = asyncTaskService.processDocumentAsync(documentId, operation);
        
        response.put("success", true);
        response.put("message", "Document processing started");
        response.put("documentId", documentId);
        response.put("operation", operation);
        response.put("status", "processing");
        
        return ResponseEntity.accepted().body(response);
    }

    /**
     * Demo: Batch process multiple documents
     * POST /api/async/batch-process
     */
    @PostMapping("/batch-process")
    public ResponseEntity<Map<String, Object>> batchProcessDemo(@RequestBody Map<String, String[]> request) {
        String[] documentIds = request.get("documentIds");
        
        Map<String, Object> response = new HashMap<>();
        
        if (documentIds == null || documentIds.length == 0) {
            response.put("success", false);
            response.put("error", "No document IDs provided");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Trigger batch processing
        CompletableFuture<String> batchFuture = asyncTaskService.batchProcessDocuments(documentIds);
        
        response.put("success", true);
        response.put("message", "Batch processing started");
        response.put("documentCount", documentIds.length);
        response.put("status", "processing");
        
        return ResponseEntity.accepted().body(response);
    }

    /**
     * Demo: Send notification to user
     * POST /api/async/notify
     */
    @PostMapping("/notify")
    public ResponseEntity<Map<String, Object>> sendNotificationDemo(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String message = request.get("message");
        
        Map<String, Object> response = new HashMap<>();
        
        // Trigger async notification
        CompletableFuture<Void> notifyFuture = asyncTaskService.sendNotificationAsync(userId, message);
        
        response.put("success", true);
        response.put("message", "Notification sent asynchronously");
        response.put("userId", userId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demo: Get task statistics
     * GET /api/async/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAsyncStats() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("totalTasks", asyncTaskService.getTaskCount());
        response.put("taskResults", asyncTaskService.getTaskResults());
        response.put("notifications", asyncTaskService.getNotifications());
        response.put("cacheSize", asyncTaskService.getCacheSize());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demo: ExecutorService usage
     * GET /api/async/executor-demo
     */
    @GetMapping("/executor-demo")
    public ResponseEntity<Map<String, Object>> executorServiceDemo() {
        Map<String, Object> response = new HashMap<>();
        
        // Submit tasks to fixed thread pool
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            fixedThreadPool.submit(() -> {
                System.out.println("Fixed Thread Pool - Task " + taskId + " executed by: " + 
                                  Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Submit tasks to cached thread pool
        for (int i = 1; i <= 3; i++) {
            final int taskId = i;
            cachedThreadPool.submit(() -> {
                System.out.println("Cached Thread Pool - Task " + taskId + " executed by: " + 
                                  Thread.currentThread().getName());
            });
        }
        
        response.put("success", true);
        response.put("message", "ExecutorService demo tasks submitted");
        response.put("fixedPoolTasks", 5);
        response.put("cachedPoolTasks", 3);
        response.put("info", "Check server logs to see thread execution");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Demo: Clear cache
     * POST /api/async/clear-cache
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, Object>> clearCache() {
        asyncTaskService.clearCache();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cache cleared successfully");
        
        return ResponseEntity.ok(response);
    }
}
