/*
Filename: BelowTheLine.java
Purpose: Annotation for marking tests that are important but non-blocking and can run separately
Goals:
  - Provide a way to categorize tests as non-critical (should pass but don't block)
  - Enable filtering of tests by criticalness
  - Support the ATL/BTL test categorization strategy
Dependencies:
  - JUnit 5 for test framework
  - Maven for test execution
Assumptions:
  - Tests marked with @BelowTheLine will be run in separate processes
  - BTL tests won't block the main build pipeline if they fail
*/

package org.samstraumr.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as "Below The Line" (BTL), indicating it is an important but non-blocking test that
 * can run separately from the main build pipeline.
 *
 * <p>Below The Line tests have the following characteristics:
 *
 * <ul>
 *   <li>May be slower - They may take longer to execute
 *   <li>More complex - They often test edge cases or rare scenarios
 *   <li>Lower priority - They don't block the build if failing
 *   <li>Comprehensive - They provide broader coverage of the system
 * </ul>
 *
 * <p>BTL tests are executed in separate processes (nightly builds, separate CI jobs) to provide
 * comprehensive coverage without blocking development.
 *
 * <p>Examples of BTL tests include:
 *
 * <ul>
 *   <li>Edge cases and boundary tests
 *   <li>Performance and stress tests
 *   <li>Rare user scenarios
 *   <li>Resource-intensive tests
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("BTL")
public @interface BelowTheLine {}
