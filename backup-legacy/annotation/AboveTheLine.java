/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as "Above The Line" (ATL), indicating it is a critical test that MUST pass with
 * every build.
 * 
 * <p>⚠️ DEPRECATED: This annotation has been replaced by {@link ATL}.
 * Please use the {@link ATL} annotation instead for all new code.
 * This annotation will be removed in a future release.
 *
 * <p>Above The Line tests have the following characteristics:
 *
 * <ul>
 *   <li>Fast - They execute quickly to provide immediate feedback
 *   <li>Reliable - They produce consistent results without flakiness
 *   <li>Critical - They verify core functionality essential to the system
 *   <li>High Priority - They block the build if failing
 * </ul>
 *
 * <p>ATL tests are executed as part of the main build pipeline and provide immediate feedback on
 * critical issues.
 *
 * <p>Examples of ATL tests include:
 *
 * <ul>
 *   <li>Core tube functionality tests
 *   <li>Critical business flows
 *   <li>Key user journeys
 *   <li>Identity and initialization tests
 * </ul>
 * 
 * @deprecated Use {@link ATL} instead. This annotation will be removed in a future release.
 */
@Deprecated
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ATL")
public @interface AboveTheLine {}
