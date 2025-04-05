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
 * Marks a test as focusing on the core functionality of individual tubes. These tests follow the
 * unit testing approach, verifying atomic behaviors in isolation from other components.
 *
 * <p>Tube Tests have the following characteristics:
 *
 * <ul>
 *   <li>Focused on a single tube in isolation
 *   <li>Verify core functionality and component properties
 *   <li>May use mocks for dependencies
 *   <li>Fast and reliable
 * </ul>
 *
 * <p>This annotation corresponds to the industry-standard concept of "Unit Tests" but uses
 * domain-specific terminology.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("TubeTest")
public @interface TubeTest {}
