/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as part of the ALZ001 initiative for Alzheimer's disease modeling framework tests.
 *
 * <p>ALZ001 tests focus on validating complex combinations of tubes, composites, and machines 
 * for representing biological structures, processes, and measurements relevant to Alzheimer's 
 * disease research.
 *
 * <p>Tests with this annotation have the following characteristics:
 *
 * <ul>
 *   <li>Model complex biological structures and processes
 *   <li>Test time-series data processing (both correlated and non-correlated)
 *   <li>Validate internal and external measurement capabilities
 *   <li>Simulate environmental changes and verify system adaptations
 *   <li>Follow hierarchical testing structure (L0_Component through L3_System)
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ALZ001")
public @interface ALZ001 {}