/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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