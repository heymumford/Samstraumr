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

package org.s8r.tube;

import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Above-The-Line (ATL) test runner for executing critical tests that must pass for every build.
 *
 * <p>This runner discovers and executes all tests annotated with @ATL across the codebase. These
 * tests are considered critical to the functioning of the system and must pass for the build to be
 * considered valid.
 *
 * <p>ATL tests have the following characteristics:
 *
 * <ul>
 *   <li>Fast - They execute quickly to provide immediate feedback
 *   <li>Reliable - They produce consistent results without flakiness
 *   <li>Critical - They verify core functionality essential to the system
 *   <li>High Priority - They block the build if failing
 * </ul>
 */
@Suite
@SuiteDisplayName("S8r Above-The-Line Tests")
@SelectPackages({
  "org.s8r.tube",
  "org.s8r.component",
  "org.s8r.core.tube",
  "org.s8r.domain",
  "org.s8r.test"
})
@IncludeTags("ATL")
public class RunATLTests {
  // This class is intentionally empty. It is used only as a holder for the annotations above.
}
