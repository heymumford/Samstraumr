/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.s8r.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as focusing on connected tubes within a composite. These tests verify that
 * composites are correctly assembled and components interact properly.
 *
 * <p>Composite Tests have the following characteristics:
 *
 * <ul>
 *   <li>Focus on multiple connected tubes
 *   <li>Verify integration between components within a composite
 *   <li>Test composite formation and tube interaction patterns
 *   <li>May use partial mocking for external dependencies
 * </ul>
 *
 * <p>This annotation corresponds to the industry-standard concept of "Component Tests" but uses
 * domain-specific terminology.
 *
 * <p>NOTE: This annotation replaces the legacy "BundleTest" annotation, as the terminology has been
 * standardized to "Composite" throughout the codebase.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("CompositeTest")
public @interface CompositeTest {}
