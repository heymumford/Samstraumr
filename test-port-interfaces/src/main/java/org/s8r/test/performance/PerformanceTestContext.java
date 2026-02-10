/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.test.performance;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Context for port interface performance testing.
 * This class provides utilities for measuring and recording performance metrics
 * of port interface operations.
 */
public class PerformanceTestContext {

    private static final Logger LOGGER = Logger.getLogger(PerformanceTestContext.class.getName());
    
    private final Map<String, List<OperationMetrics>> operationMetrics;
    private final Map<String, Object> properties;
    private final Map<String, List<ExecutionResult>> executionResults;
    private final List<String> errors;
    
    /**
     * Creates a new PerformanceTestContext.
     */
    public PerformanceTestContext() {
        this.operationMetrics = new ConcurrentHashMap<>();
        this.properties = new ConcurrentHashMap<>();
        this.executionResults = new ConcurrentHashMap<>();
        this.errors = new CopyOnWriteArrayList<>();
    }
    
    /**
     * Initializes the performance test metrics.
     */
    public void initialize() {
        operationMetrics.clear();
        properties.clear();
        executionResults.clear();
        errors.clear();
    }
    
    /**
     * Measures the execution time of a runnable operation.
     *
     * @param operationName The name of the operation
     * @param operation The operation to measure
     */
    public void measureOperation(String operationName, Runnable operation) {
        Instant start = Instant.now();
        try {
            operation.run();
            Instant end = Instant.now();
            recordMetrics(operationName, start, end, true);
        } catch (Exception e) {
            Instant end = Instant.now();
            recordMetrics(operationName, start, end, false);
            LOGGER.log(Level.WARNING, "Operation failed: " + operationName, e);
            errors.add("Operation failed: " + operationName + " - " + e.getMessage());
        }
    }
    
    /**
     * Measures the execution time of multiple runnable operations in sequence.
     *
     * @param operationName The name of the operation
     * @param operations The operations to measure
     * @param count The number of times to execute the operations
     */
    public void measureOperations(String operationName, Runnable operation, int count) {
        for (int i = 0; i < count; i++) {
            measureOperation(operationName, operation);
        }
    }
    
    /**
     * Measures the execution time of multiple runnable operations concurrently.
     *
     * @param operationName The name of the operation
     * @param operation The operation to measure
     * @param threadCount The number of threads to use
     * @param operationsPerThread The number of operations per thread
     */
    public void measureConcurrentOperations(String operationName, Runnable operation, 
            int threadCount, int operationsPerThread) {
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger completedOperations = new AtomicInteger(0);
        Instant startTime = Instant.now();
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    measureOperation(operationName, operation);
                    completedOperations.incrementAndGet();
                }
            });
        }
        
        try {
            executor.shutdown();
            boolean completed = executor.awaitTermination(5, TimeUnit.MINUTES);
            Instant endTime = Instant.now();
            
            if (!completed) {
                LOGGER.warning("Concurrent operations did not complete within the timeout");
                errors.add("Concurrent operations did not complete within the timeout");
            }
            
            // Record overall execution result
            int totalOperations = threadCount * operationsPerThread;
            int actualCompleted = completedOperations.get();
            Duration totalDuration = Duration.between(startTime, endTime);
            double operationsPerSecond = actualCompleted / (totalDuration.toMillis() / 1000.0);
            
            ExecutionResult result = new ExecutionResult(
                    operationName + "_concurrent", 
                    threadCount, 
                    operationsPerThread, 
                    actualCompleted, 
                    totalDuration, 
                    operationsPerSecond);
            
            executionResults.computeIfAbsent(operationName, k -> new ArrayList<>()).add(result);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.SEVERE, "Concurrent operations interrupted", e);
            errors.add("Concurrent operations interrupted: " + e.getMessage());
        }
    }
    
    /**
     * Records operation metrics.
     *
     * @param operationName The name of the operation
     * @param start The start time
     * @param end The end time
     * @param success Whether the operation succeeded
     */
    private void recordMetrics(String operationName, Instant start, Instant end, boolean success) {
        long durationMs = Duration.between(start, end).toMillis();
        OperationMetrics metrics = new OperationMetrics(operationName, durationMs, success);
        operationMetrics.computeIfAbsent(operationName, k -> new CopyOnWriteArrayList<>()).add(metrics);
    }
    
    /**
     * Gets the average operation time.
     *
     * @param operationName The name of the operation
     * @return The average operation time in milliseconds
     */
    public double getAverageOperationTime(String operationName) {
        List<OperationMetrics> metrics = operationMetrics.getOrDefault(operationName, Collections.emptyList());
        if (metrics.isEmpty()) {
            return 0;
        }
        
        return metrics.stream()
                .mapToLong(OperationMetrics::getDurationMs)
                .average()
                .orElse(0);
    }
    
    /**
     * Gets the percentile operation time.
     *
     * @param operationName The name of the operation
     * @param percentile The percentile (0-100)
     * @return The percentile operation time in milliseconds
     */
    public long getPercentileOperationTime(String operationName, int percentile) {
        List<OperationMetrics> metrics = operationMetrics.getOrDefault(operationName, Collections.emptyList());
        if (metrics.isEmpty()) {
            return 0;
        }
        
        List<Long> times = new ArrayList<>();
        for (OperationMetrics metric : metrics) {
            times.add(metric.getDurationMs());
        }
        
        Collections.sort(times);
        int index = (int) Math.ceil(percentile / 100.0 * times.size()) - 1;
        if (index < 0) {
            index = 0;
        }
        
        return times.get(index);
    }
    
    /**
     * Gets the throughput.
     *
     * @param operationName The name of the operation
     * @return The throughput in operations per second
     */
    public double getThroughput(String operationName) {
        List<OperationMetrics> metrics = operationMetrics.getOrDefault(operationName, Collections.emptyList());
        if (metrics.isEmpty()) {
            return 0;
        }
        
        // Calculate the total time spent
        long totalTimeMs = metrics.stream()
                .mapToLong(OperationMetrics::getDurationMs)
                .sum();
        
        if (totalTimeMs == 0) {
            return 0;
        }
        
        // Return operations per second
        return metrics.size() / (totalTimeMs / 1000.0);
    }
    
    /**
     * Gets the success rate.
     *
     * @param operationName The name of the operation
     * @return The success rate (0-1)
     */
    public double getSuccessRate(String operationName) {
        List<OperationMetrics> metrics = operationMetrics.getOrDefault(operationName, Collections.emptyList());
        if (metrics.isEmpty()) {
            return 0;
        }
        
        long successCount = metrics.stream()
                .filter(OperationMetrics::isSuccess)
                .count();
        
        return (double) successCount / metrics.size();
    }
    
    /**
     * Gets the execution results.
     *
     * @param operationName The name of the operation
     * @return The execution results
     */
    public List<ExecutionResult> getExecutionResults(String operationName) {
        return executionResults.getOrDefault(operationName, Collections.emptyList());
    }
    
    /**
     * Gets all errors.
     *
     * @return The list of errors
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    /**
     * Sets a property.
     *
     * @param key The property key
     * @param value The property value
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * Gets a property.
     *
     * @param key The property key
     * @return The property value
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * Gets the operation metrics.
     *
     * @return The operation metrics
     */
    public Map<String, List<OperationMetrics>> getOperationMetrics() {
        return new HashMap<>(operationMetrics);
    }
    
    /**
     * Gets the properties.
     *
     * @return The properties
     */
    public Map<String, Object> getProperties() {
        return new HashMap<>(properties);
    }
    
    /**
     * Generates a performance report.
     *
     * @return The performance report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("# Performance Test Report\n\n");
        report.append("Generated: ").append(Instant.now()).append("\n\n");
        
        // Operation Metrics
        report.append("## Operation Metrics\n\n");
        report.append("| Operation | Count | Avg Time (ms) | 95th Percentile (ms) | Throughput (ops/sec) | Success Rate |\n");
        report.append("|-----------|-------|--------------|----------------------|----------------------|-------------|\n");
        
        for (String operationName : operationMetrics.keySet()) {
            List<OperationMetrics> metrics = operationMetrics.get(operationName);
            double avgTime = getAverageOperationTime(operationName);
            long p95Time = getPercentileOperationTime(operationName, 95);
            double throughput = getThroughput(operationName);
            double successRate = getSuccessRate(operationName);
            
            report.append("| ").append(operationName).append(" | ")
                  .append(metrics.size()).append(" | ")
                  .append(String.format("%.2f", avgTime)).append(" | ")
                  .append(p95Time).append(" | ")
                  .append(String.format("%.2f", throughput)).append(" | ")
                  .append(String.format("%.2f%%", successRate * 100)).append(" |\n");
        }
        
        report.append("\n");
        
        // Execution Results
        report.append("## Execution Results\n\n");
        report.append("| Operation | Threads | Ops/Thread | Completed | Duration (ms) | Throughput (ops/sec) |\n");
        report.append("|-----------|---------|------------|-----------|---------------|----------------------|\n");
        
        for (String operationName : executionResults.keySet()) {
            for (ExecutionResult result : executionResults.get(operationName)) {
                report.append("| ").append(result.getOperationName()).append(" | ")
                      .append(result.getThreadCount()).append(" | ")
                      .append(result.getOperationsPerThread()).append(" | ")
                      .append(result.getCompletedOperations()).append(" | ")
                      .append(result.getTotalDuration().toMillis()).append(" | ")
                      .append(String.format("%.2f", result.getOperationsPerSecond())).append(" |\n");
            }
        }
        
        report.append("\n");
        
        // Errors
        if (!errors.isEmpty()) {
            report.append("## Errors\n\n");
            for (String error : errors) {
                report.append("- ").append(error).append("\n");
            }
            report.append("\n");
        }
        
        return report.toString();
    }
    
    /**
     * Class representing operation metrics.
     */
    public static class OperationMetrics {
        private final String operationName;
        private final long durationMs;
        private final boolean success;
        
        /**
         * Creates a new OperationMetrics.
         *
         * @param operationName The name of the operation
         * @param durationMs The duration in milliseconds
         * @param success Whether the operation succeeded
         */
        public OperationMetrics(String operationName, long durationMs, boolean success) {
            this.operationName = operationName;
            this.durationMs = durationMs;
            this.success = success;
        }
        
        /**
         * Gets the operation name.
         *
         * @return The operation name
         */
        public String getOperationName() {
            return operationName;
        }
        
        /**
         * Gets the duration in milliseconds.
         *
         * @return The duration in milliseconds
         */
        public long getDurationMs() {
            return durationMs;
        }
        
        /**
         * Gets whether the operation succeeded.
         *
         * @return Whether the operation succeeded
         */
        public boolean isSuccess() {
            return success;
        }
    }
    
    /**
     * Class representing execution results.
     */
    public static class ExecutionResult {
        private final String operationName;
        private final int threadCount;
        private final int operationsPerThread;
        private final int completedOperations;
        private final Duration totalDuration;
        private final double operationsPerSecond;
        
        /**
         * Creates a new ExecutionResult.
         *
         * @param operationName The name of the operation
         * @param threadCount The number of threads
         * @param operationsPerThread The number of operations per thread
         * @param completedOperations The number of completed operations
         * @param totalDuration The total duration
         * @param operationsPerSecond The operations per second
         */
        public ExecutionResult(String operationName, int threadCount, int operationsPerThread, 
                int completedOperations, Duration totalDuration, double operationsPerSecond) {
            this.operationName = operationName;
            this.threadCount = threadCount;
            this.operationsPerThread = operationsPerThread;
            this.completedOperations = completedOperations;
            this.totalDuration = totalDuration;
            this.operationsPerSecond = operationsPerSecond;
        }
        
        /**
         * Gets the operation name.
         *
         * @return The operation name
         */
        public String getOperationName() {
            return operationName;
        }
        
        /**
         * Gets the thread count.
         *
         * @return The thread count
         */
        public int getThreadCount() {
            return threadCount;
        }
        
        /**
         * Gets the operations per thread.
         *
         * @return The operations per thread
         */
        public int getOperationsPerThread() {
            return operationsPerThread;
        }
        
        /**
         * Gets the completed operations.
         *
         * @return The completed operations
         */
        public int getCompletedOperations() {
            return completedOperations;
        }
        
        /**
         * Gets the total duration.
         *
         * @return The total duration
         */
        public Duration getTotalDuration() {
            return totalDuration;
        }
        
        /**
         * Gets the operations per second.
         *
         * @return The operations per second
         */
        public double getOperationsPerSecond() {
            return operationsPerSecond;
        }
    }
}