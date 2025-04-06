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
 * Marks a test that verifies the basic assembly, connectivity, and initialization of the system.
 *
 * <p>Orchestration Tests have the following characteristics:
 *
 * <ul>
 *   <li>Verify that the system can be assembled correctly
 *   <li>Check basic connectivity between components
 *   <li>Ensure initialization sequences complete properly
 *   <li>Fast and reliable, focusing on basic system readiness
 * </ul>
 *
 * <p>This annotation corresponds to the industry-standard concept of "Smoke Tests" but uses
 * domain-specific terminology. These tests serve as the foundational verification that the system
 * is ready for more detailed testing.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("OrchestrationTest")
public @interface OrchestrationTest {}
