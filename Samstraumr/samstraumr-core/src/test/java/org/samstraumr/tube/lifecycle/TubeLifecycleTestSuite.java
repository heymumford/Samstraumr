package org.samstraumr.tube.lifecycle;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test suite that groups all Tube Lifecycle tests.
 *
 * <p>This suite collects all the test classes related to the biological lifecycle model of Tube
 * development, organized into initialization, growth, operational phases, and termination.
 */
@Suite
@SuiteDisplayName("Tube Lifecycle Test Suite")
@SelectClasses({
  // Add test classes as they are implemented
  RunTubeLifecycleCucumberTest.class,
})
public class TubeLifecycleTestSuite {}
