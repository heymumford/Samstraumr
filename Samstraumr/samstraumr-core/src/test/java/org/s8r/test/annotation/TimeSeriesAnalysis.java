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
 * Marks a test as focusing on time series data analysis.
 *
 * <p>TimeSeriesAnalysis tests verify capabilities related to:
 *
 * <ul>
 *   <li>Processing temporal data sequences
 *   <li>Identifying trends, seasonality, and cycles
 *   <li>Detecting change points in time series
 *   <li>Correlating multiple time series
 *   <li>Forecasting future values in time series
 * </ul>
 *
 * <p>This annotation is commonly used with ALZ001 tests focusing on
 * temporal aspects of disease progression modeling.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("TimeSeriesAnalysis")
public @interface TimeSeriesAnalysis {}