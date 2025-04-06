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

package org.s8r.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class or method as an Orchestration-level test.
 *
 * <p>Orchestration tests validate the coordination and interaction of multiple machines working
 * together in a larger system. These tests focus on:
 *
 * <ul>
 *   <li>Inter-machine communication patterns
 *   <li>System-level workflows spanning multiple machines
 *   <li>Coordination of state across machine boundaries
 *   <li>Integration with external systems or resources
 * </ul>
 *
 * <p>Orchestration tests are high-level tests that ensure the entire system functions correctly as
 * a cohesive whole. They often validate end-to-end capabilities from an external user's
 * perspective.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface OrchestrationTest {}
