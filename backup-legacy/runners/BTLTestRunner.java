package org.samstraumr.tube.test.runners;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * JUnit test runner that only executes Below The Line (BTL) tests.
 *
 * <p>BTL tests are important but non-blocking tests that can run separately. They have the
 * following characteristics:
 *
 * <ul>
 *   <li>May be slower - They may take longer to execute
 *   <li>More complex - They often test edge cases or rare scenarios
 *   <li>Lower priority - They don't block the build if failing
 *   <li>Comprehensive - They provide broader coverage of the system
 * </ul>
 *
 * <p>This runner is designed to be used in separate processes (nightly builds, separate CI jobs) to
 * provide comprehensive coverage without blocking development.
 *
 * <p>Tests can be included by either:
 *
 * <ul>
 *   <li>Using the {@code @BTL} annotation (preferred)
 *   <li>Using the {@code @BelowTheLine} annotation (deprecated)
 *   <li>Using the {@code @Tag("BTL")} annotation
 * </ul>
 */
@Suite
@SuiteDisplayName("Below The Line (BTL) Tests - Robustness Path")
@SelectPackages({"org.samstraumr.tube", "org.samstraumr.tube.lifecycle"})
@IncludeTags("BTL")
public class BTLTestRunner {
  // This class is just a test runner configuration with no implementation
}
