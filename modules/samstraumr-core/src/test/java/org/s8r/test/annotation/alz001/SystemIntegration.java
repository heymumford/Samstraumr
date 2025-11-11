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

package org.s8r.test.annotation.alz001;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as part of the ALZ001 System Integration capability.
 *
 * <p>Tests with this annotation focus on validating complete system integration of all ALZ001
 * components, composites, and machines, including:
 *
 * <ul>
 *   <li>End-to-end Alzheimer's disease modeling workflows
 *   <li>Multi-scale integration (molecular to systems)
 *   <li>Virtual patient cohort simulations
 *   <li>Intervention mechanism testing
 *   <li>System-level validation
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("SystemIntegration")
public @interface SystemIntegration {}