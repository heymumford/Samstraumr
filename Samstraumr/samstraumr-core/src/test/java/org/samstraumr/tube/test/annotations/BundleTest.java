/*
Filename: BundleTest.java
Purpose: Annotation for marking tests that focus on connected tubes (component tests)
Goals:
  - Provide a way to categorize tests by test type
  - Enable filtering of tests by type
  - Support the unified testing strategy
Dependencies:
  - JUnit 5 for test framework
  - Maven for test execution
Assumptions:
  - Tests marked with @BundleTest focus on testing connected tubes as components
*/

package org.samstraumr.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "Bundle Test", indicating it focuses on testing connected tubes working
 * together as components.
 *
 * <p>Bundle Tests have the following characteristics:
 *
 * <ul>
 *   <li>Component level - They test multiple tubes working together
 *   <li>Integration - They verify interaction between tubes
 *   <li>Isolated - They don't interact with external systems
 * </ul>
 *
 * <p>In TBD terminology, Bundle Tests align with Composite Tube Interaction Testing (CTIT).
 *
 * <p>Examples of Bundle Tests include:
 *
 * <ul>
 *   <li>Data flow between connected tubes
 *   <li>State propagation across tubes
 *   <li>Error handling in tube chains
 *   <li>Bundle lifecycle tests
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("BundleTest")
public @interface BundleTest {}
