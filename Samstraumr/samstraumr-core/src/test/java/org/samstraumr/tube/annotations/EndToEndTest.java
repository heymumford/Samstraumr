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
 * Marks a test as an "End-to-End Test", indicating it focuses on testing the entire system from the
 * user's perspective.
 *
 * <p>End-to-End Tests have the following characteristics:
 *
 * <ul>
 *   <li>User-centric - They test the system from the user's perspective
 *   <li>Acceptance criteria - They verify the system meets user requirements
 *   <li>Business process - They validate complete business processes
 * </ul>
 *
 * <p>In traditional testing terminology, end-to-end tests verify that the system works correctly
 * from a user's perspective. In Samstraumr, they align with Acceptance Tests that verify the system
 * meets user requirements.
 *
 * <p>Examples of end-to-end tests include:
 *
 * <ul>
 *   <li>User journey validation
 *   <li>Business process verification
 *   <li>Customer acceptance criteria validation
 *   <li>System behavior from user perspective
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("EndToEnd")
@Tag("Acceptance")
public @interface EndToEndTest {}
