/*
Filename: ATLTestRunner.java
Purpose: JUnit test runner for executing Above The Line (ATL) tests
Goals:
  - Provide a way to run only ATL tests
  - Filter tests based on @AboveTheLine annotation or ATL tag
  - Support the ATL/BTL test categorization strategy
Dependencies:
  - JUnit 5 for test framework
  - Maven for test execution
Assumptions:
  - Tests marked with @AboveTheLine will be included in this runner
  - This runner will be used in the main build pipeline
*/

package org.samstraumr.tube.test.runners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * JUnit test runner that only executes Above The Line (ATL) tests.
 *
 * <p>ATL tests are critical tests that MUST pass with every build. They have the following
 * characteristics:
 *
 * <ul>
 *   <li>Fast - They execute quickly to provide immediate feedback
 *   <li>Reliable - They produce consistent results without flakiness
 *   <li>Critical - They verify core functionality essential to the system
 *   <li>High Priority - They block the build if failing
 * </ul>
 *
 * <p>This runner is designed to be used in the main build pipeline to provide immediate feedback on
 * critical issues.
 *
 * <p>Tests can be included by either:
 *
 * <ul>
 *   <li>Using the {@code @AboveTheLine} annotation
 *   <li>Using the {@code @Tag("ATL")} annotation
 * </ul>
 */
@Suite
@SuiteDisplayName("Above The Line (ATL) Tests - Critical Path")
@SelectPackages({"org.samstraumr.tube"})
@IncludeTags("ATL")
public class ATLTestRunner {
  // This class is just a test runner configuration with no implementation
}
