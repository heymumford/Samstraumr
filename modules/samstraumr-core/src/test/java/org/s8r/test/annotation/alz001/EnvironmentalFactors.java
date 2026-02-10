/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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
 * Marks a test as part of the ALZ001 Environmental Factors modeling capability.
 *
 * <p>Tests with this annotation focus on validating components, composites, and machines that model
 * environmental influences on Alzheimer's disease progression, including:
 *
 * <ul>
 *   <li>Oxidative stress modeling
 *   <li>Inflammatory response simulation
 *   <li>Cognitive reserve quantification
 *   <li>Environmental exposure modeling
 *   <li>Factor interaction analysis
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("EnvironmentalFactors")
public @interface EnvironmentalFactors {}
