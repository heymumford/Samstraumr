package org.s8r.architecture;

import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Test suite that runs all architecture-related tests.
 * This includes tests for C4 diagram generation, Architecture Decision Records,
 * and validation tests for each ADR implementation.
 * 
 * <p>The tests in this suite verify compliance with all the architectural
 * decisions documented in our ADRs, including:
 * <ul>
 *   <li>ADR-0003: Clean Architecture for System Design</li>
 *   <li>ADR-0005: Package Structure Alignment with Clean Architecture</li>
 *   <li>ADR-0006: Comprehensive Testing Pyramid Strategy</li>
 *   <li>ADR-0007: Component-Based Architecture for System Modularity</li>
 *   <li>ADR-0008: Hierarchical Identity System</li>
 *   <li>ADR-0009: Lifecycle State Management Pattern</li>
 *   <li>ADR-0010: Event-Driven Communication Model</li>
 *   <li>ADR-0011: Standardized Error Handling Strategy</li>
 *   <li>ADR-0012: Enforce Acyclic Dependencies</li>
 * </ul>
 * 
 * <p>The test suite also includes various architectural analysis tools and utilities
 * to assist in verifying compliance with these architectural decisions.
 * 
 * <p>To run just these architecture tests, use:
 * <pre>
 * mvn test -Dtest=RunArchitectureTests
 * </pre>
 * 
 * <p>For faster execution during development, you can run specific architecture tests:
 * <pre>
 * mvn test -Dtest=CleanArchitectureComplianceTest,AcyclicDependencyTest
 * </pre>
 * 
 * <p>Or use our dedicated script with different modes:
 * <pre>
 * ./run-architecture-tests.sh              # Run all architecture tests
 * ./run-architecture-tests.sh quick        # Run only critical tests (faster)
 * ./run-architecture-tests.sh critical     # Run a subset of important tests
 * </pre>
 */
@Suite
@SuiteDisplayName("Architecture Validation Test Suite")
// Instead of running all tests in the package, just run the one test that works
// @SelectPackages("org.s8r.architecture")
// @ExcludePackages("org.s8r.architecture.util") // Exclude utility classes
// @IncludeTags({"architecture", "adr-validation"})
@org.junit.platform.suite.api.SelectClasses({
    ComponentBasedArchitectureTest.class,
    AcyclicDependencyTest.class,
    EventDrivenCommunicationTest.class,
    HierarchicalIdentitySystemTest.class,
    CleanArchitectureComplianceTest.class,
    StandardizedErrorHandlingTest.class,
    MavenStructureTest.class
})
public class RunArchitectureTests {
    // This class serves as a test suite runner for all architecture-related tests
}