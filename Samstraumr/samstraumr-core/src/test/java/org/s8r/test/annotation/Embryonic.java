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
 * Marks a test as an Embryonic phase test in the biological lifecycle model.
 *
 * <p>Embryonic tests verify the formation and specialization of component structures:
 *
 * <ul>
 *   <li>Basic internal structure development
 *   <li>Component specialization
 *   <li>Simple interaction capabilities
 *   <li>Formation of basic functionality
 * </ul>
 *
 * <p>Embryonic is the second phase in the biological lifecycle model used in the testing
 * strategy, representing the development of component structure and specialization.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Embryonic")
public @interface Embryonic {}