/**
 * Performance testing utilities for Karate tests
 * 
 * This file contains utility functions for measuring and validating
 * performance metrics in Karate tests.
 */

/**
 * Measures the average execution time of a function over multiple iterations
 * @param {number} iterations - Number of times to execute the function
 * @param {Function} func - The function to execute
 * @returns {number} Average execution time in milliseconds
 */
function measureAverageTime(iterations, func) {
  var System = Java.type('java.lang.System');
  var start = System.nanoTime();
  
  for (var i = 0; i < iterations; i++) {
    func();
  }
  
  var totalNanos = System.nanoTime() - start;
  // Return average time in milliseconds
  return totalNanos / (iterations * 1000000);
}

/**
 * Measures execution time for a single function call
 * @param {Function} func - The function to execute
 * @returns {number} Execution time in milliseconds
 */
function measureSingleExecution(func) {
  var System = Java.type('java.lang.System');
  var start = System.nanoTime();
  
  func();
  
  var totalNanos = System.nanoTime() - start;
  // Return time in milliseconds
  return totalNanos / 1000000;
}

/**
 * Runs a throughput test to measure operations per second
 * @param {Function} func - The function to execute
 * @param {number} durationSeconds - Duration to run the test in seconds
 * @returns {Object} Test results including operations per second
 */
function runThroughputTest(func, durationSeconds) {
  var System = Java.type('java.lang.System');
  var count = 0;
  var start = System.currentTimeMillis();
  var endTime = start + (durationSeconds * 1000);
  var errors = 0;
  
  while (System.currentTimeMillis() < endTime) {
    try {
      func();
      count++;
    } catch (e) {
      errors++;
    }
  }
  
  var actualDuration = (System.currentTimeMillis() - start) / 1000;
  var opsPerSecond = count / actualDuration;
  
  return {
    totalOperations: count,
    duration: actualDuration,
    operationsPerSecond: opsPerSecond,
    errors: errors
  };
}

/**
 * Creates a performance report based on test results
 * @param {Object} testResults - The results from a performance test
 * @param {string} operationName - Name of the operation being tested
 * @returns {string} A formatted performance report
 */
function createPerformanceReport(testResults, operationName) {
  var report = "Performance Test Report for " + operationName + ":\n";
  report += "-------------------------------------------\n";
  
  if (testResults.averageTimeMs !== undefined) {
    report += "Average execution time: " + testResults.averageTimeMs.toFixed(3) + " ms\n";
  }
  
  if (testResults.operationsPerSecond !== undefined) {
    report += "Operations per second: " + testResults.operationsPerSecond.toFixed(2) + " ops/sec\n";
    report += "Total operations: " + testResults.totalOperations + "\n";
    report += "Test duration: " + testResults.duration.toFixed(2) + " seconds\n";
  }
  
  if (testResults.errors !== undefined) {
    report += "Errors encountered: " + testResults.errors + "\n";
  }
  
  report += "-------------------------------------------";
  
  return report;
}

/**
 * Runs a load test with multiple concurrent operations
 * @param {Function} func - The function to execute
 * @param {number} concurrency - Number of concurrent operations
 * @param {number} iterations - Number of iterations per concurrent operation
 * @returns {Object} Test results including averages and timing stats
 */
function runConcurrentTest(func, concurrency, iterations) {
  // We'll simulate concurrency by running operations in a tight loop
  var System = Java.type('java.lang.System');
  var results = [];
  var start = System.currentTimeMillis();
  
  // Create batches of operations
  for (var i = 0; i < iterations; i++) {
    var batchStart = System.nanoTime();
    
    // Run a batch of concurrent operations
    for (var j = 0; j < concurrency; j++) {
      try {
        func();
      } catch (e) {
        // Log error but continue
      }
    }
    
    var batchTime = (System.nanoTime() - batchStart) / 1000000;
    results.push(batchTime);
  }
  
  var totalDuration = System.currentTimeMillis() - start;
  
  // Calculate statistics
  var sum = 0;
  var min = results[0];
  var max = results[0];
  
  for (var k = 0; k < results.length; k++) {
    sum += results[k];
    if (results[k] < min) min = results[k];
    if (results[k] > max) max = results[k];
  }
  
  var avgBatchTime = sum / results.length;
  var opsPerSecond = (concurrency * iterations) / (totalDuration / 1000);
  
  return {
    totalOperations: concurrency * iterations,
    concurrency: concurrency,
    iterations: iterations,
    totalDurationMs: totalDuration,
    avgBatchTimeMs: avgBatchTime,
    minBatchTimeMs: min,
    maxBatchTimeMs: max,
    operationsPerSecond: opsPerSecond
  };
}