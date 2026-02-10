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
 * Marks a test as part of the ALZ001 test suite, focusing on Alzheimer's disease modeling
 * using complex combinations of tubes, composites, and machines.
 *
 * <p>ALZ001 tests have the following characteristics:
 *
 * <ul>
 *   <li>Models neuronal degradation, protein expression, and environmental factors
 *   <li>Uses time series data analysis for correlation and prediction
 *   <li>Employs complex composite structures to model disease progression
 *   <li>Tests both correlated and non-correlated variable impacts
 *   <li>Contains positive and negative test scenarios
 * </ul>
 *
 * <p>This annotation should be used in conjunction with other test categorizations
 * such as {@code @ATL}/{@code @BTL} for criticality, lifecycle phase annotations 
 * ({@code @Conception}, {@code @Embryonic}, etc.), and hierarchical level annotations
 * ({@code @L0_Component}, {@code @L1_Composite}, etc.).
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ALZ001")
public @interface ALZ001 {}