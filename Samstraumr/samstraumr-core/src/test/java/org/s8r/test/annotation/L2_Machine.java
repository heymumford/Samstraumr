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
 * Marks a test as a Level 2 Machine test in the hierarchical testing model.
 *
 * <p>L2_Machine tests focus on composite integration within machines:
 *
 * <ul>
 *   <li>Multiple composite integration
 *   <li>Machine creation and configuration
 *   <li>Composite orchestration and coordination
 *   <li>Machine behavior and functionality
 *   <li>Advanced data processing workflows
 * </ul>
 *
 * <p>L2_Machine is the third level in the hierarchical testing model, focusing on
 * the integration of multiple composites into functional machines.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("L2_Machine")
public @interface L2_Machine {}