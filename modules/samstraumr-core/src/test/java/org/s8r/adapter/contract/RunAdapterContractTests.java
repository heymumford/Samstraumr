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

package org.s8r.adapter.contract;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * JUnit 5 test suite for running all adapter contract tests.
 *
 * <p>This suite automatically includes all classes in the contract package with names ending in
 * "ContractTest", making it easy to run all contract tests together.
 */
@Suite
@SuiteDisplayName("Adapter Contract Tests")
@SelectPackages("org.s8r.adapter.contract")
@IncludeClassNamePatterns(".*ContractTest")
public class RunAdapterContractTests {
  // This class is just a marker for the test suite
}
