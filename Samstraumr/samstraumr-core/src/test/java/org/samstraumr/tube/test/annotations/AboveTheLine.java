/*
Filename: AboveTheLine.java
Purpose: Annotation for marking tests that are critical to the codebase and must pass with every build
Goals:
  - Provide a way to categorize tests as critical (must pass)
  - Enable filtering of tests by criticalness
  - Support the ATL/BTL test categorization strategy
Dependencies:
  - JUnit 5 for test framework
  - Maven for test execution
Assumptions:
  - Tests marked with @AboveTheLine will be run in the main build pipeline
  - Tests without @AboveTheLine or with @BelowTheLine will be run separately
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
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("ATL")
public @interface AboveTheLine {}
