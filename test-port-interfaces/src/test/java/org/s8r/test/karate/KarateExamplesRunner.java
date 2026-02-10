package org.s8r.test.karate;

import com.intuit.karate.junit5.Karate;

/**
 * JUnit 5 test runner for Karate example tests.
 * 
 * This runner executes Karate tests demonstrating the reusable patterns
 * for port interface testing.
 */
public class KarateExamplesRunner {

    @Karate.Test
    Karate runReusablePatternsExample() {
        return Karate.run("classpath:karate/examples/reusable-patterns-example.feature");
    }
    
    // Additional example test runners can be added here
}