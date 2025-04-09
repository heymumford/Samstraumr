/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.runners;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.Tag;

/**
 * JUnit 5 runner for Karate system and integration tests of port interfaces.
 * This runner executes all Karate tests in the specified classpath locations.
 */
@Tag("L3_System")
@Tag("API")
@Tag("Karate")
public class KaratePortTests {

    /**
     * Runs all Karate tests in the karate directory.
     * This method will execute all .feature files found in any subdirectory of karate.
     *
     * @return Karate test runner
     */
    @Karate.Test
    Karate allTests() {
        return Karate.run("classpath:karate").relativeTo(getClass());
    }

    /**
     * Runs only security-event integration tests.
     *
     * @return Karate test runner for security-event tests
     */
    @Karate.Test
    Karate securityEventTests() {
        return Karate.run("classpath:karate/port_interfaces/security-event-integration.feature").relativeTo(getClass());
    }

    /**
     * Runs only task-notification integration tests.
     *
     * @return Karate test runner for task-notification tests
     */
    @Karate.Test
    Karate taskNotificationTests() {
        return Karate.run("classpath:karate/port_interfaces/task-notification-integration.feature").relativeTo(getClass());
    }
    
    /**
     * Runs only cache port tests.
     *
     * @return Karate test runner for cache port tests
     */
    @Karate.Test
    Karate cachePortTests() {
        return Karate.run("classpath:karate/port_interfaces/cache-port-test.feature").relativeTo(getClass());
    }
    
    /**
     * Runs only filesystem port tests.
     *
     * @return Karate test runner for filesystem port tests
     */
    @Karate.Test
    Karate filesystemPortTests() {
        return Karate.run("classpath:karate/port_interfaces/filesystem-port-test.feature").relativeTo(getClass());
    }
    
    /**
     * Runs only configuration port tests.
     *
     * @return Karate test runner for configuration port tests
     */
    @Karate.Test
    Karate configurationPortTests() {
        return Karate.run("classpath:karate/port_interfaces/configuration-port-test.feature").relativeTo(getClass());
    }
    
    /**
     * Runs only validation port tests.
     *
     * @return Karate test runner for validation port tests
     */
    @Karate.Test
    Karate validationPortTests() {
        return Karate.run("classpath:karate/port_interfaces/validation-port-test.feature").relativeTo(getClass());
    }

    /**
     * Runs all port interface tests.
     *
     * @return Karate test runner for all port interface tests
     */
    @Karate.Test
    Karate portInterfaceTests() {
        return Karate.run("classpath:karate/port_interfaces").relativeTo(getClass());
    }
}