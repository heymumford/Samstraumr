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

package org.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "Component Test", indicating it focuses on testing connected components working
 * together.
 *
 * <p>Component Tests have the following characteristics:
 *
 * <ul>
 *   <li>Multi-unit - They test multiple units working together
 *   <li>Isolated boundaries - They verify interaction across internal boundaries
 *   <li>No external dependencies - They don't interact with external systems
 * </ul>
 *
 * <p>In traditional testing terminology, component tests verify that units work correctly together.
 * In Samstraumr, they test composites (groups of tubes working together).
 *
 * <p>Examples of component tests include:
 *
 * <ul>
 *   <li>Data flow between connected tubes
 *   <li>State propagation across tubes
 *   <li>Error handling in tube chains
 *   <li>Composite behavior validation
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Component")
@Tag("Composite")
public @interface ComponentTest {}
