/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as an "Orchestration Test", indicating it focuses on testing the assembly and
 * interconnectivity of the entire Samstraumr system.
 *
 * <p>Orchestration Tests are the highest level of Above The Line (ATL) tests and have these
 * characteristics:
 *
 * <ul>
 *   <li>Critical - These tests MUST pass for the build to be considered valid
 *   <li>System-level - They test the entire application assembly and connectivity
 *   <li>Lightweight - They run quickly and provide immediate feedback on build health
 *   <li>First to run - They run before any other tests in the CI pipeline
 * </ul>
 *
 * <p>Examples of Orchestration Tests include:
 *
 * <ul>
 *   <li>System bootstrap tests (can the application start up correctly?)
 *   <li>Component wiring tests (are all required components properly connected?)
 *   <li>Configuration tests (is the application properly configured?)
 *   <li>Smoke tests (do the most basic operations work?)
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Orchestration")
@Tag("ATL")
public @interface OrchestrationTest {}
