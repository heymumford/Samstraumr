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
 * Marks a test as focusing on neuronal network modeling and analysis.
 *
 * <p>NeuronalNetwork tests verify capabilities related to:
 *
 * <ul>
 *   <li>Modeling neuronal network structure
 *   <li>Simulating neuronal connectivity
 *   <li>Modeling neuronal degradation
 *   <li>Analyzing network plasticity and compensation
 *   <li>Simulating region-specific network properties
 * </ul>
 *
 * <p>This annotation is commonly used with ALZ001 tests focusing on
 * neuronal network aspects of Alzheimer's disease modeling.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("NeuronalNetwork")
public @interface NeuronalNetwork {}