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
 * Marks a test as a Level 3 System test in the hierarchical testing model.
 *
 * <p>L3_System tests focus on machine integration within complete systems:
 *
 * <ul>
 *   <li>Multiple machine integration
 *   <li>System-wide configuration and coordination
 *   <li>End-to-end workflow validation
 *   <li>System-level performance and resilience
 *   <li>Full data processing pipelines
 * </ul>
 *
 * <p>L3_System is the fourth level in the hierarchical testing model, focusing on
 * the integration of multiple machines into complete functional systems.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("L3_System")
public @interface L3_System {}