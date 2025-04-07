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
 * Marks a test as a composite test, focusing on verifying the behavior of a composite component
 * in the new S8r framework.
 *
 * <p>Composite Tests have the following characteristics:
 *
 * <ul>
 *   <li>Focused on a composite with its contained components
 *   <li>Verify interaction between components within a composite
 *   <li>Test composite capabilities and behaviors
 *   <li>May test state propagation within the composite
 * </ul>
 *
 * <p>This annotation is specifically for the new S8r framework implementation of composites,
 * distinguishing it from legacy composite tests. It aligns with the component testing level
 * in the standard testing pyramid.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("CompositeS8rTest")
public @interface CompositeS8rTest {}