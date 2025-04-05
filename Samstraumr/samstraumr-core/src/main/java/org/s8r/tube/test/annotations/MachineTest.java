/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class or method as a Machine-level test.
 *
 * <p>Machine tests validate the behavior of interconnected composite tubes working together
 * as a state machine. These tests focus on:
 *
 * <ul>
 *   <li>State transitions within the machine
 *   <li>Data flow between composites
 *   <li>Overall machine behavior according to its specification
 *   <li>Proper composite connection and interaction
 * </ul>
 *
 * <p>Machine tests are higher-level integration tests that ensure multiple components work together
 * correctly to achieve the intended functionality.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MachineTest {
}