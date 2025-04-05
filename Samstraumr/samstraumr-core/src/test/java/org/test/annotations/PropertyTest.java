/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "Property Test", indicating it focuses on testing system properties across a
 * range of inputs.
 *
 * <p>Property Tests have the following characteristics:
 *
 * <ul>
 *   <li>Property verification - They verify system properties hold for many inputs
 *   <li>Generative - They often use generated test data
 *   <li>Invariant checking - They verify that certain properties always hold
 * </ul>
 *
 * <p>In traditional testing terminology, property tests verify that system properties hold across a
 * wide range of inputs. In Samstraumr, they align with Adaptation Tests that verify the system
 * adapts correctly to different conditions.
 *
 * <p>Examples of property tests include:
 *
 * <ul>
 *   <li>System behavior verification across many inputs
 *   <li>System resilience testing under varying conditions
 *   <li>Invariant verification across state transitions
 *   <li>Fuzz testing of interfaces
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Property")
@Tag("Adaptation")
public @interface PropertyTest {}
