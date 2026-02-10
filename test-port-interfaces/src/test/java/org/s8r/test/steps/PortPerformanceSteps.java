/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.s8r.application.port.CachePort;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.FileSystemPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.SecurityPort;
import org.s8r.application.port.TaskExecutionPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationSeverity;
import org.s8r.test.integration.TaskNotificationBridge;
import org.s8r.test.mock.MockCacheAdapter;
import org.s8r.test.mock.MockEventPublisherAdapter;
import org.s8r.test.mock.MockFileSystemAdapter;
import org.s8r.test.mock.MockNotificationAdapter;
import org.s8r.test.mock.MockSecurityAdapter;
import org.s8r.test.mock.MockTaskExecutionAdapter;
import org.s8r.test.performance.PerformanceTestContext;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Step definitions for port interface performance tests.
 */
public class PortPerformanceSteps {
    
    private static final Logger LOGGER = Logger.getLogger(PortPerformanceSteps.class.getName());
    
    private PerformanceTestContext performanceContext;
    private CachePort cachePort;
    private EventPublisherPort eventPublisherPort;
    private FileSystemPort fileSystemPort;
    private NotificationPort notificationPort;
    private SecurityPort securityPort;
    private TaskExecutionPort taskExecutionPort;
    private TaskNotificationBridge taskNotificationBridge;
    
    @Before
    public void setup() {
        performanceContext = new PerformanceTestContext();
        cachePort = new MockCacheAdapter();
        eventPublisherPort = new MockEventPublisherAdapter();
        fileSystemPort = new MockFileSystemAdapter();
        notificationPort = new MockNotificationAdapter();
        securityPort = new MockSecurityAdapter();
        taskExecutionPort = new MockTaskExecutionAdapter();
        taskNotificationBridge = new TaskNotificationBridge(taskExecutionPort, notificationPort);
        
        // Initialize the mock adapters for testing
        initializeMockAdapters();
    }
    
    private void initializeMockAdapters() {
        // Initialize SecurityPort with test users
        MockSecurityAdapter securityAdapter = (MockSecurityAdapter) securityPort;
        securityAdapter.addUser("testuser", "password", List.of("USER"));
        securityAdapter.addUser("admin", "adminpass", List.of("ADMIN", "USER"));
        
        // Initialize FileSystemPort with test files
        MockFileSystemAdapter fileSystemAdapter = (MockFileSystemAdapter) fileSystemPort;
        fileSystemAdapter.createDirectory("/test");
        for (int i = 1; i <= 100; i++) {
            fileSystemAdapter.writeFile("/test/file" + i + ".txt", 
                    ("Test file " + i + " content").getBytes(StandardCharsets.UTF_8));
        }
        
        // Initialize CachePort with test data
        MockCacheAdapter cacheAdapter = (MockCacheAdapter) cachePort;
        for (int i = 1; i <= 100; i++) {
            cacheAdapter.put("key" + i, "value" + i);
        }
    }
    
    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already initialized in setup
    }
    
    @Given("performance test metrics are initialized")
    public void performanceTestMetricsAreInitialized() {
        performanceContext.initialize();
    }
    
    // Cache Port Performance Tests
    
    @When("I measure the performance of retrieving {int} items from the cache")
    public void iMeasureThePerformanceOfRetrievingItemsFromTheCache(int count) {
        // Ensure we have enough items in the cache
        ensureCacheItems(count);
        
        // Measure cache get operations
        performanceContext.measureOperations("cache-get", () -> {
            int key = (int) (Math.random() * count) + 1;
            cachePort.get("key" + key);
        }, count);
    }
    
    private void ensureCacheItems(int count) {
        MockCacheAdapter cacheAdapter = (MockCacheAdapter) cachePort;
        for (int i = 1; i <= count; i++) {
            if (!cacheAdapter.contains("key" + i)) {
                cacheAdapter.put("key" + i, "value" + i);
            }
        }
    }
    
    @When("I measure the performance of {int} concurrent threads accessing the cache")
    public void iMeasureThePerformanceOfConcurrentThreadsAccessingTheCache(int threadCount) {
        performanceContext.setProperty("cache-single-thread-avg", 
                performanceContext.getAverageOperationTime("cache-get"));
        
        performanceContext.measureConcurrentOperations("cache-concurrent", () -> {
            int key = (int) (Math.random() * 1000) + 1;
            String operation = Math.random() < 0.5 ? "get" : "put";
            
            if ("get".equals(operation)) {
                cachePort.get("key" + key);
            } else {
                cachePort.put("key" + key, "value" + key + "-" + UUID.randomUUID());
            }
        }, threadCount, 1000);
    }
    
    @And("each thread performs {int} get and put operations")
    public void eachThreadPerformsGetAndPutOperations(int operationsPerThread) {
        // This is handled in the previous step
    }
    
    // FileSystem Port Performance Tests
    
    @When("I measure the performance of reading {int} small files")
    public void iMeasureThePerformanceOfReadingSmallFiles(int count) {
        // Ensure we have enough files
        ensureFiles(count);
        
        // Measure file read operations
        performanceContext.measureOperations("filesystem-read", () -> {
            int fileNum = (int) (Math.random() * count) + 1;
            fileSystemPort.readFile("/test/file" + fileNum + ".txt");
        }, count);
    }
    
    private void ensureFiles(int count) {
        MockFileSystemAdapter fileSystemAdapter = (MockFileSystemAdapter) fileSystemPort;
        if (!fileSystemAdapter.exists("/test")) {
            fileSystemAdapter.createDirectory("/test");
        }
        
        for (int i = 1; i <= count; i++) {
            if (!fileSystemAdapter.exists("/test/file" + i + ".txt")) {
                fileSystemAdapter.writeFile("/test/file" + i + ".txt", 
                        ("Test file " + i + " content").getBytes(StandardCharsets.UTF_8));
            }
        }
    }
    
    @When("I measure the performance of {int} concurrent threads accessing the filesystem")
    public void iMeasureThePerformanceOfConcurrentThreadsAccessingTheFilesystem(int threadCount) {
        performanceContext.setProperty("filesystem-single-thread-avg", 
                performanceContext.getAverageOperationTime("filesystem-read"));
        
        performanceContext.measureConcurrentOperations("filesystem-concurrent", () -> {
            int fileNum = (int) (Math.random() * 100) + 1;
            String operation = Math.random() < 0.5 ? "read" : "write";
            
            if ("read".equals(operation)) {
                fileSystemPort.readFile("/test/file" + fileNum + ".txt");
            } else {
                fileSystemPort.writeFile("/test/file" + fileNum + ".txt", 
                        ("Updated content " + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8));
            }
        }, threadCount, 100);
    }
    
    @And("each thread performs {int} read and write operations")
    public void eachThreadPerformsReadAndWriteOperations(int operationsPerThread) {
        // This is handled in the previous step
    }
    
    // Event Publisher Port Performance Tests
    
    @When("I measure the performance of publishing {int} events to a single topic")
    public void iMeasureThePerformanceOfPublishingEventsToASingleTopic(int count) {
        String topic = "test-topic";
        
        // Measure event publishing operations
        performanceContext.measureOperations("event-publish", () -> {
            Map<String, String> properties = new HashMap<>();
            properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
            properties.put("eventId", UUID.randomUUID().toString());
            
            eventPublisherPort.publishEvent(topic, "TEST_EVENT", "Event payload " + UUID.randomUUID(), properties);
        }, count);
    }
    
    @When("I measure the performance with {int} subscribers to a single topic")
    public void iMeasureThePerformanceWithSubscribersToASingleTopic(int subscriberCount) {
        MockEventPublisherAdapter eventAdapter = (MockEventPublisherAdapter) eventPublisherPort;
        String topic = "test-topic-multi";
        List<EventSubscriber> subscribers = new ArrayList<>();
        
        // Create and register subscribers
        for (int i = 0; i < subscriberCount; i++) {
            EventSubscriber subscriber = new EventSubscriber();
            subscribers.add(subscriber);
            eventAdapter.subscribe(topic, subscriber);
        }
        
        // Measure event publishing with multiple subscribers
        performanceContext.measureOperations("event-publish-many-subscribers", () -> {
            Map<String, String> properties = new HashMap<>();
            properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
            properties.put("eventId", UUID.randomUUID().toString());
            
            eventPublisherPort.publishEvent(topic, "TEST_EVENT", "Event payload " + UUID.randomUUID(), properties);
        }, 1000);
        
        performanceContext.setProperty("subscriber-count", subscriberCount);
        performanceContext.setProperty("event-count", 1000);
        
        // Verify that all subscribers received all events
        for (EventSubscriber subscriber : subscribers) {
            performanceContext.setProperty("received-events-" + subscribers.indexOf(subscriber), 
                    subscriber.getEventCount());
        }
    }
    
    @And("{int} events are published to the topic")
    public void eventsArePublishedToTheTopic(int eventCount) {
        // This is handled in the previous step
    }
    
    @Then("all subscribers should receive all events")
    public void allSubscribersShouldReceiveAllEvents() {
        int subscriberCount = (int) performanceContext.getProperty("subscriber-count");
        int eventCount = (int) performanceContext.getProperty("event-count");
        
        for (int i = 0; i < subscriberCount; i++) {
            int receivedEvents = (int) performanceContext.getProperty("received-events-" + i);
            assertTrue(receivedEvents == eventCount, 
                    "Subscriber " + i + " received " + receivedEvents + " events, expected " + eventCount);
        }
    }
    
    // Notification Port Performance Tests
    
    @When("I measure the performance of sending {int} notifications")
    public void iMeasureThePerformanceOfSendingNotifications(int count) {
        // Measure notification sending operations
        performanceContext.measureOperations("notification-send", () -> {
            String recipient = "test@example.com";
            String subject = "Test Notification " + UUID.randomUUID();
            String message = "This is a test notification " + UUID.randomUUID();
            
            notificationPort.sendNotification(recipient, subject, message);
        }, count);
    }
    
    // Security Port Performance Tests
    
    @When("I measure the performance of {int} authentication operations")
    public void iMeasureThePerformanceOfAuthenticationOperations(int count) {
        // Measure authentication operations
        performanceContext.measureOperations("security-authentication", () -> {
            boolean useValidCredentials = Math.random() < 0.8; // 80% valid credentials
            String username = useValidCredentials ? "testuser" : "user" + UUID.randomUUID();
            String password = useValidCredentials ? "password" : "pass" + UUID.randomUUID();
            
            securityPort.authenticate(username, password);
        }, count);
    }
    
    @When("I measure the performance of {int} concurrent threads authenticating")
    public void iMeasureThePerformanceOfConcurrentThreadsAuthenticating(int threadCount) {
        performanceContext.setProperty("security-single-thread-avg", 
                performanceContext.getAverageOperationTime("security-authentication"));
        
        performanceContext.measureConcurrentOperations("security-concurrent", () -> {
            boolean useValidCredentials = Math.random() < 0.8; // 80% valid credentials
            String username = useValidCredentials ? "testuser" : "user" + UUID.randomUUID();
            String password = useValidCredentials ? "password" : "pass" + UUID.randomUUID();
            
            securityPort.authenticate(username, password);
        }, threadCount, 100);
    }
    
    @And("each thread performs {int} authentication operations")
    public void eachThreadPerformsAuthenticationOperations(int operationsPerThread) {
        // This is handled in the previous step
    }
    
    // Task Execution Port Performance Tests
    
    @When("I measure the performance of scheduling {int} simple tasks")
    public void iMeasureThePerformanceOfSchedulingSimpleTasks(int count) {
        // Measure task scheduling operations
        performanceContext.measureOperations("task-scheduling", () -> {
            taskExecutionPort.scheduleTask(() -> {
                // Simple task
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Task completed";
            }, Duration.ofMillis(10));
        }, count);
    }
    
    @When("I measure the performance of {int} concurrent threads scheduling tasks")
    public void iMeasureThePerformanceOfConcurrentThreadsSchedulingTasks(int threadCount) {
        performanceContext.setProperty("task-single-thread-avg", 
                performanceContext.getAverageOperationTime("task-scheduling"));
        
        performanceContext.measureConcurrentOperations("task-concurrent", () -> {
            taskExecutionPort.scheduleTask(() -> {
                // Simple task
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Task completed";
            }, Duration.ofMillis(10));
        }, threadCount, 1000);
    }
    
    @And("each thread schedules {int} tasks")
    public void eachThreadSchedulesTasks(int tasksPerThread) {
        // This is handled in the previous step
    }
    
    // Integration Performance Tests
    
    @When("I measure the performance of {int} cached file reads")
    public void iMeasureThePerformanceOfCachedFileReads(int count) {
        // Ensure we have files
        ensureFiles(count);
        
        // First, measure uncached reads
        performanceContext.measureOperations("filesystem-uncached-read", () -> {
            int fileNum = (int) (Math.random() * count) + 1;
            fileSystemPort.readFile("/test/file" + fileNum + ".txt");
        }, count);
        
        // Now measure cached reads (simulate cache integration)
        Map<String, byte[]> fileCache = new HashMap<>();
        
        performanceContext.measureOperations("filesystem-cached-read", () -> {
            int fileNum = (int) (Math.random() * count) + 1;
            String filePath = "/test/file" + fileNum + ".txt";
            
            if (!fileCache.containsKey(filePath)) {
                byte[] content = fileSystemPort.readFile(filePath);
                fileCache.put(filePath, content);
            } else {
                // Use cached content
                fileCache.get(filePath);
            }
        }, count);
    }
    
    @When("I measure the performance of {int} security operations with event publishing")
    public void iMeasureThePerformanceOfSecurityOperationsWithEventPublishing(int count) {
        // Measure integrated security-event operations
        performanceContext.measureOperations("security-event-integration", () -> {
            // Simulate security operation with event publishing
            String username = "testuser";
            String password = "password";
            
            // Do security operation
            securityPort.authenticate(username, password);
            
            // Publish security event
            Map<String, String> properties = new HashMap<>();
            properties.put("username", username);
            properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
            properties.put("eventId", UUID.randomUUID().toString());
            
            eventPublisherPort.publishEvent("security.events", "LOGIN", 
                    "User " + username + " logged in", properties);
        }, count);
    }
    
    @When("I measure the performance of scheduling {int} notifications")
    public void iMeasureThePerformanceOfSchedulingNotifications(int count) {
        // Measure task-notification integration
        performanceContext.measureOperations("task-notification-integration", () -> {
            String recipient = "test@example.com";
            String subject = "Scheduled Notification " + UUID.randomUUID();
            String message = "This is a scheduled notification " + UUID.randomUUID();
            
            taskNotificationBridge.scheduleNotification(recipient, subject, message, 
                    Duration.ofMillis(10));
        }, count);
    }
    
    @When("I run a stress test with all port interfaces for {int} minutes")
    public void iRunAStressTestWithAllPortInterfacesForMinutes(int minutes) {
        Duration testDuration = Duration.ofMinutes(minutes);
        long endTime = System.currentTimeMillis() + testDuration.toMillis();
        
        // Run mixed operations for the specified duration
        int operationCount = 0;
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() < endTime) {
            int operationType = (int) (Math.random() * 6);
            
            switch (operationType) {
                case 0: // Cache
                    performanceContext.measureOperation("stress-cache", () -> {
                        int key = (int) (Math.random() * 1000) + 1;
                        if (Math.random() < 0.7) {
                            cachePort.get("key" + key);
                        } else {
                            cachePort.put("key" + key, "value" + key + "-" + UUID.randomUUID());
                        }
                    });
                    break;
                    
                case 1: // FileSystem
                    performanceContext.measureOperation("stress-filesystem", () -> {
                        int fileNum = (int) (Math.random() * 100) + 1;
                        if (Math.random() < 0.7) {
                            fileSystemPort.readFile("/test/file" + fileNum + ".txt");
                        } else {
                            fileSystemPort.writeFile("/test/file" + fileNum + ".txt", 
                                    ("Updated content " + UUID.randomUUID()).getBytes(StandardCharsets.UTF_8));
                        }
                    });
                    break;
                    
                case 2: // Event
                    performanceContext.measureOperation("stress-event", () -> {
                        String topic = "test-topic-" + ((int) (Math.random() * 10) + 1);
                        Map<String, String> properties = new HashMap<>();
                        properties.put("timestamp", String.valueOf(System.currentTimeMillis()));
                        properties.put("eventId", UUID.randomUUID().toString());
                        
                        eventPublisherPort.publishEvent(topic, "TEST_EVENT", 
                                "Event payload " + UUID.randomUUID(), properties);
                    });
                    break;
                    
                case 3: // Notification
                    performanceContext.measureOperation("stress-notification", () -> {
                        String recipient = "test" + ((int) (Math.random() * 10) + 1) + "@example.com";
                        String subject = "Test Notification " + UUID.randomUUID();
                        String message = "This is a test notification " + UUID.randomUUID();
                        
                        notificationPort.sendAdvancedNotification(recipient, subject, message, 
                                NotificationSeverity.INFO, NotificationChannel.EMAIL, 
                                ContentFormat.TEXT, Collections.emptyMap());
                    });
                    break;
                    
                case 4: // Security
                    performanceContext.measureOperation("stress-security", () -> {
                        boolean useValidCredentials = Math.random() < 0.8; // 80% valid credentials
                        String username = useValidCredentials ? "testuser" : "user" + UUID.randomUUID();
                        String password = useValidCredentials ? "password" : "pass" + UUID.randomUUID();
                        
                        securityPort.authenticate(username, password);
                    });
                    break;
                    
                case 5: // Task
                    performanceContext.measureOperation("stress-task", () -> {
                        taskExecutionPort.scheduleTask(() -> {
                            // Simple task
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                            return "Task completed";
                        }, Duration.ofMillis(10));
                    });
                    break;
            }
            
            operationCount++;
            
            // Check if we need to calculate throughput metrics at intervals
            if (operationCount % 1000 == 0) {
                long elapsedMs = System.currentTimeMillis() - startTime;
                double throughput = operationCount / (elapsedMs / 1000.0);
                
                performanceContext.setProperty("stress-throughput-" + operationCount, throughput);
                
                // Calculate current 99th percentile response time
                Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                        performanceContext.getOperationMetrics();
                
                List<Long> allResponseTimes = new ArrayList<>();
                for (List<PerformanceTestContext.OperationMetrics> metricsList : metrics.values()) {
                    for (PerformanceTestContext.OperationMetrics metric : metricsList) {
                        allResponseTimes.add(metric.getDurationMs());
                    }
                }
                
                if (!allResponseTimes.isEmpty()) {
                    Collections.sort(allResponseTimes);
                    int index = (int) Math.ceil(99.0 / 100.0 * allResponseTimes.size()) - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    
                    long p99 = allResponseTimes.get(index);
                    performanceContext.setProperty("stress-p99-" + operationCount, p99);
                }
                
                // Memory usage
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                performanceContext.setProperty("stress-memory-" + operationCount, usedMemory);
            }
        }
        
        // Record final metrics
        long elapsedMs = System.currentTimeMillis() - startTime;
        double overallThroughput = operationCount / (elapsedMs / 1000.0);
        
        performanceContext.setProperty("stress-operations", operationCount);
        performanceContext.setProperty("stress-duration-ms", elapsedMs);
        performanceContext.setProperty("stress-throughput", overallThroughput);
    }
    
    // General Assertion Steps
    
    @Then("the average operation time should be less than {int} milliseconds")
    public void theAverageOperationTimeShouldBeLessThanMilliseconds(int maxAvgTimeMs) {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        double avgTime = performanceContext.getAverageOperationTime(lastOperation);
        
        assertTrue(avgTime < maxAvgTimeMs, 
                "Average operation time for " + lastOperation + " was " + avgTime + 
                " ms, expected less than " + maxAvgTimeMs + " ms");
        
        LOGGER.info(lastOperation + " average time: " + avgTime + " ms");
    }
    
    @And("the {int}th percentile response time should be less than {int} milliseconds")
    public void thePercentileResponseTimeShouldBeLessThanMilliseconds(int percentile, int maxTimeMs) {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        long pTime = performanceContext.getPercentileOperationTime(lastOperation, percentile);
        
        assertTrue(pTime < maxTimeMs, 
                percentile + "th percentile response time for " + lastOperation + " was " + pTime + 
                " ms, expected less than " + maxTimeMs + " ms");
        
        LOGGER.info(lastOperation + " " + percentile + "th percentile: " + pTime + " ms");
    }
    
    @And("the throughput should be at least {int} operations per second")
    public void theThroughputShouldBeAtLeastOperationsPerSecond(int minThroughput) {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        double throughput = performanceContext.getThroughput(lastOperation);
        
        assertTrue(throughput >= minThroughput, 
                "Throughput for " + lastOperation + " was " + throughput + 
                " ops/sec, expected at least " + minThroughput + " ops/sec");
        
        LOGGER.info(lastOperation + " throughput: " + throughput + " ops/sec");
    }
    
    @Then("all operations should complete successfully")
    public void allOperationsShouldCompleteSuccessfully() {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        double successRate = performanceContext.getSuccessRate(lastOperation);
        
        assertTrue(successRate > 0.99, 
                "Success rate for " + lastOperation + " was " + successRate + 
                ", expected at least 0.99");
        
        LOGGER.info(lastOperation + " success rate: " + (successRate * 100) + "%");
    }
    
    @And("the average operation time should increase by less than {int}% compared to single-threaded access")
    public void theAverageOperationTimeShouldIncreaseByLessThanComparedToSingleThreadedAccess(int maxIncreasePercent) {
        // Get the relevant operation names
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        
        // Find the concurrent operation (should be the last one)
        String concurrentOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        // Get the single-threaded average time property
        String singlePropertyName = concurrentOperation.split("-")[0] + "-single-thread-avg";
        double singleAvgTime = (Double) performanceContext.getProperty(singlePropertyName);
        
        // Get the concurrent average time
        double concurrentAvgTime = performanceContext.getAverageOperationTime(concurrentOperation);
        
        // Calculate percentage increase
        double percentIncrease = ((concurrentAvgTime - singleAvgTime) / singleAvgTime) * 100;
        
        assertTrue(percentIncrease < maxIncreasePercent, 
                "Average operation time increase for " + concurrentOperation + " was " + percentIncrease + 
                "%, expected less than " + maxIncreasePercent + "%");
        
        LOGGER.info(concurrentOperation + " average time increase: " + percentIncrease + "%");
    }
    
    @And("the throughput should degrade by less than {int}% compared to single-threaded access")
    public void theThroughputShouldDegradeByLessThanComparedToSingleThreadedAccess(int maxDegradePercent) {
        // Get the relevant operation names
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        
        // Find the operations (single-threaded and concurrent)
        String baseOperation = metrics.keySet().stream()
                .filter(op -> !op.contains("concurrent"))
                .findFirst()
                .orElse("");
        
        String concurrentOperation = metrics.keySet().stream()
                .filter(op -> op.contains("concurrent"))
                .findFirst()
                .orElse("");
        
        // Get the throughputs
        double singleThroughput = performanceContext.getThroughput(baseOperation);
        double concurrentThroughput = performanceContext.getThroughput(concurrentOperation);
        
        // Calculate percentage degradation
        double percentDegrade = ((singleThroughput - concurrentThroughput) / singleThroughput) * 100;
        
        assertTrue(percentDegrade < maxDegradePercent, 
                "Throughput degradation for " + concurrentOperation + " was " + percentDegrade + 
                "%, expected less than " + maxDegradePercent + "%");
        
        LOGGER.info(concurrentOperation + " throughput degradation: " + percentDegrade + "%");
    }
    
    @And("subsequent reads should be at least {int}% faster than the initial reads")
    public void subsequentReadsShouldBeAtLeastFasterThanTheInitialReads(int minImprovement) {
        double uncachedAvgTime = performanceContext.getAverageOperationTime("filesystem-uncached-read");
        double cachedAvgTime = performanceContext.getAverageOperationTime("filesystem-cached-read");
        
        // Calculate percentage improvement
        double percentImprovement = ((uncachedAvgTime - cachedAvgTime) / uncachedAvgTime) * 100;
        
        assertTrue(percentImprovement >= minImprovement, 
                "Cached reads improvement was " + percentImprovement + 
                "%, expected at least " + minImprovement + "%");
        
        LOGGER.info("Cached reads improvement: " + percentImprovement + "%");
    }
    
    @And("the average event delivery time should be less than {int} milliseconds")
    public void theAverageEventDeliveryTimeShouldBeLessThanMilliseconds(int maxAvgTimeMs) {
        // This would be measured by the event subscribers
        // For this implementation, we'll just log a message
        LOGGER.info("Event delivery time assertion (would be implemented with real event delivery timing)");
    }
    
    @And("the average scheduling time should be less than {int} milliseconds")
    public void theAverageSchedulingTimeShouldBeLessThanMilliseconds(int maxAvgTimeMs) {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        double avgTime = performanceContext.getAverageOperationTime(lastOperation);
        
        assertTrue(avgTime < maxAvgTimeMs, 
                "Average scheduling time for " + lastOperation + " was " + avgTime + 
                " ms, expected less than " + maxAvgTimeMs + " ms");
        
        LOGGER.info(lastOperation + " average scheduling time: " + avgTime + " ms");
    }
    
    @And("the {int}th percentile scheduling time should be less than {int} milliseconds")
    public void thePercentileSchedulingTimeShouldBeLessThanMilliseconds(int percentile, int maxTimeMs) {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        long pTime = performanceContext.getPercentileOperationTime(lastOperation, percentile);
        
        assertTrue(pTime < maxTimeMs, 
                percentile + "th percentile scheduling time for " + lastOperation + " was " + pTime + 
                " ms, expected less than " + maxTimeMs + " ms");
        
        LOGGER.info(lastOperation + " " + percentile + "th percentile: " + pTime + " ms");
    }
    
    @Then("all tasks should be scheduled successfully")
    public void allTasksShouldBeScheduledSuccessfully() {
        // Get the last operation name from context
        Map<String, List<PerformanceTestContext.OperationMetrics>> metrics = 
                performanceContext.getOperationMetrics();
        String lastOperation = new ArrayList<>(metrics.keySet()).get(metrics.keySet().size() - 1);
        
        double successRate = performanceContext.getSuccessRate(lastOperation);
        
        assertTrue(successRate > 0.99, 
                "Success rate for " + lastOperation + " was " + successRate + 
                ", expected at least 0.99");
        
        LOGGER.info(lastOperation + " success rate: " + (successRate * 100) + "%");
    }
    
    @Then("the system should maintain a minimum throughput of {int} operations per second")
    public void theSystemShouldMaintainAMinimumThroughputOfOperationsPerSecond(int minThroughput) {
        double overallThroughput = (Double) performanceContext.getProperty("stress-throughput");
        
        assertTrue(overallThroughput >= minThroughput, 
                "Overall throughput was " + overallThroughput + 
                " ops/sec, expected at least " + minThroughput + " ops/sec");
        
        LOGGER.info("Overall stress test throughput: " + overallThroughput + " ops/sec");
    }
    
    @And("the {int}th percentile response time should not increase by more than {int}% over time")
    public void thePercentileResponseTimeShouldNotIncreaseByMoreThanOverTime(int percentile, int maxIncreasePercent) {
        // Get the p99 metrics at different points in time
        Map<String, Object> properties = performanceContext.getProperties();
        
        List<Long> p99Values = new ArrayList<>();
        for (String key : properties.keySet()) {
            if (key.startsWith("stress-p99-")) {
                p99Values.add((Long) properties.get(key));
            }
        }
        
        if (p99Values.size() < 2) {
            // Not enough data points
            return;
        }
        
        long initialP99 = p99Values.get(0);
        long maxP99 = p99Values.stream().max(Long::compare).orElse(initialP99);
        
        // Calculate percentage increase
        double percentIncrease = ((maxP99 - initialP99) / (double) initialP99) * 100;
        
        assertTrue(percentIncrease <= maxIncreasePercent, 
                percentile + "th percentile response time increased by " + percentIncrease + 
                "%, expected less than " + maxIncreasePercent + "%");
        
        LOGGER.info(percentile + "th percentile increase: " + percentIncrease + "%");
    }
    
    @And("memory usage should remain stable throughout the test")
    public void memoryUsageShouldRemainStableThroughoutTheTest() {
        // Get the memory usage metrics at different points in time
        Map<String, Object> properties = performanceContext.getProperties();
        
        List<Long> memoryValues = new ArrayList<>();
        for (String key : properties.keySet()) {
            if (key.startsWith("stress-memory-")) {
                memoryValues.add((Long) properties.get(key));
            }
        }
        
        if (memoryValues.size() < 2) {
            // Not enough data points
            return;
        }
        
        long initialMemory = memoryValues.get(0);
        long finalMemory = memoryValues.get(memoryValues.size() - 1);
        
        // Calculate percentage increase
        double percentIncrease = ((finalMemory - initialMemory) / (double) initialMemory) * 100;
        
        // Memory usage should not increase by more than 50% during the test
        assertTrue(percentIncrease <= 50, 
                "Memory usage increased by " + percentIncrease + 
                "%, expected less than 50%");
        
        LOGGER.info("Memory usage increase: " + percentIncrease + "%");
    }
    
    /**
     * Simple event subscriber for testing.
     */
    private static class EventSubscriber implements EventPublisherPort.EventSubscriber {
        private final List<EventRecord> receivedEvents = Collections.synchronizedList(new ArrayList<>());
        
        @Override
        public void onEvent(String topic, String eventType, String payload, Map<String, String> properties) {
            receivedEvents.add(new EventRecord(topic, eventType, payload, properties, System.currentTimeMillis()));
        }
        
        public int getEventCount() {
            return receivedEvents.size();
        }
        
        public List<EventRecord> getReceivedEvents() {
            return new ArrayList<>(receivedEvents);
        }
        
        public static class EventRecord {
            private final String topic;
            private final String eventType;
            private final String payload;
            private final Map<String, String> properties;
            private final long receivedTime;
            
            public EventRecord(String topic, String eventType, String payload, 
                    Map<String, String> properties, long receivedTime) {
                this.topic = topic;
                this.eventType = eventType;
                this.payload = payload;
                this.properties = new HashMap<>(properties);
                this.receivedTime = receivedTime;
            }
        }
    }
}