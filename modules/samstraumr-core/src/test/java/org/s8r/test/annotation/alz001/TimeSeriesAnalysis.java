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
 * Marks a test as part of the ALZ001 Time Series Analysis capability.
 *
 * <p>Tests with this annotation focus on validating components, composites, and machines that
 * process and analyze time series data relevant to Alzheimer's disease, including:
 *
 * <ul>
 *   <li>Time series decomposition (trend, seasonal, residual)
 *   <li>Cross-correlation analysis
 *   <li>Change point detection
 *   <li>Anomaly detection
 *   <li>Forecasting and prediction
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("TimeSeriesAnalysis")
public @interface TimeSeriesAnalysis {}