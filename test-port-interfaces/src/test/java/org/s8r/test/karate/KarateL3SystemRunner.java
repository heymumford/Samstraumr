package org.s8r.test.karate;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;

/**
 * JUnit 5 test runner for Karate L3_System tests.
 * 
 * This runner executes Karate tests for system-level integration and end-to-end scenarios.
 * These tests represent the migrated versions of the original Cucumber L3_System tests.
 */
@Tag("L3_System")
@Tag("API")
@Tag("Karate")
public class KarateL3SystemRunner {

    @Karate.Test
    Karate allSystemTests() {
        return Karate.run("classpath:karate/L3_System").relativeTo(getClass());
    }

    @Karate.Test
    Karate systemIntegrationTests() {
        return Karate.run("classpath:karate/L3_System/system-integration-test.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate systemEndToEndTests() {
        return Karate.run("classpath:karate/L3_System/system-end-to-end-test.feature").relativeTo(getClass());
    }
    
    // Additional test methods for other L3_System tests can be added here
    // as they are migrated from Cucumber to Karate
}