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

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.s8r.architecture.*;

/**
 * Test runner for Architecture compliance tests.
 * 
 * <p>This suite runs all tests that verify architectural rules and constraints are followed.
 * It includes tests for clean architecture, acyclic dependencies, class structure, etc.
 * 
 * <p>To run this suite, use one of these methods:
 * <ul>
 *   <li>Maven: {@code mvn test -Dtest=ArchitectureTests}</li>
 *   <li>s8r-test script: {@code ./s8r-test architecture}</li>
 * </ul>
 */
@Suite
@SuiteDisplayName("Architecture Compliance Tests")
@SelectClasses({
    AcyclicDependencyTest.class,
    ArchitectureDecisionRecordTest.class, 
    ArchitectureToolsIntegrationTest.class,
    CleanArchitectureComplianceTest.class,
    ComponentBasedArchitectureTest.class,
    DiagramGenerationTest.class,
    EventDrivenCommunicationTest.class,
    HierarchicalIdentitySystemTest.class,
    LifecycleStateManagementTest.class,
    MavenStructureTest.class,
    PackageStructureTest.class,
    StandardizedErrorHandlingTest.class,
    TestingPyramidComplianceTest.class
})
public class ArchitectureTests {
    // This class is intentionally empty. It's used only as a holder for the annotations.
}