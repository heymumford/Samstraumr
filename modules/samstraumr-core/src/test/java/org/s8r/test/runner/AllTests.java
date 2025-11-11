/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.runner;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Master test suite that runs all JUnit tests in the project.
 * 
 * <p>This suite automatically discovers and runs all tests in the org.s8r package hierarchy.
 * It uses JUnit 5's test discovery mechanism to find and execute unit tests, integration tests,
 * and other test types that are based on JUnit.
 * 
 * <p>BDD tests using Cucumber should be run using the specific Cucumber test runners:
 * - {@link ComponentTests}
 * - {@link MachineTests}
 * - {@link PortIntegrationTests}
 * 
 * <p>To run this suite, use one of these methods:
 * <ul>
 *   <li>Maven: {@code mvn test -Dtest=AllTests}</li>
 *   <li>s8r-test script: {@code ./s8r-test all}</li>
 * </ul>
 */
@Suite
@SelectPackages("org.s8r")
@SuiteDisplayName("All S8r Tests")
public class AllTests {
    // This class is intentionally empty. It's used only as a holder for the annotations.
}