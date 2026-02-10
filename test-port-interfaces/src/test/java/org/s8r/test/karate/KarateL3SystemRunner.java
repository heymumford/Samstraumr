/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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
    
    @Karate.Test
    Karate systemResilienceTests() {
        return Karate.run("classpath:karate/L3_System/system-resilience-test.feature").relativeTo(getClass());
    }
    
    @Karate.Test
    Karate systemSecurityTests() {
        return Karate.run("classpath:karate/L3_System/system-security-test.feature").relativeTo(getClass());
    }
    
    @Karate.Test
    Karate systemReliabilityTests() {
        return Karate.run("classpath:karate/L3_System/system-reliability-test.feature").relativeTo(getClass());
    }
    
    @Karate.Test
    Karate systemScalabilityTests() {
        return Karate.run("classpath:karate/L3_System/system-scalability-test.feature").relativeTo(getClass());
    }
}