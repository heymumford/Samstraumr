/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.tube.lifecycle;

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
