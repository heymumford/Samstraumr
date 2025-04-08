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
 * Marks a test as focusing on protein expression modeling and analysis.
 *
 * <p>ProteinExpression tests verify capabilities related to:
 *
 * <ul>
 *   <li>Modeling protein expression patterns
 *   <li>Detecting abnormal protein accumulation
 *   <li>Analyzing protein expression time series
 *   <li>Correlating protein expression with other factors
 *   <li>Predicting future protein expression patterns
 * </ul>
 *
 * <p>This annotation is commonly used with ALZ001 tests focusing on
 * protein-related aspects of Alzheimer's disease modeling.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ProteinExpression")
public @interface ProteinExpression {}