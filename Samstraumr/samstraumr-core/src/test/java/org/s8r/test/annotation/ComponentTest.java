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
 * Marks a test as a component test, focusing on verifying the behavior of a single component in the S8r framework.
 *
 * <p>Component Tests have the following characteristics:
 *
 * <ul>
 *   <li>Focused on a single component in isolation
 *   <li>Verify core functionality and component properties
 *   <li>May use mocks for dependencies
 *   <li>Fast and reliable
 * </ul>
 *
 * <p>This annotation aligns with the unit testing level in the standard testing pyramid but
 * uses S8r's domain-specific terminology to clearly indicate the test is focused on the
 * Component implementation in the new S8r framework.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ComponentTest")
public @interface ComponentTest {}