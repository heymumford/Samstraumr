package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Cucumber test runner for Tube Lifecycle tests based on the biological lifecycle model.
 *
 * <p>This runner focuses specifically on the lifecycle aspects of Tubes based on the biological
 * development model, from conception to legacy. It executes the tests defined in the lifecycle
 * feature files that match specific biological phases, initiatives, and other tags.
 *
 * <p>Test execution can be filtered using the cucumber.filter.tags system property:
 *
 * <ul>
 *   <li>Run all lifecycle tests: -Dcucumber.filter.tags="@L0_Tube"
 *   <li>Run by initiative:
 *       <ul>
 *         <li>-Dcucumber.filter.tags="@SubstrateIdentity" - Creation and physical continuity
 *         <li>-Dcucumber.filter.tags="@StructuralIdentity" - Structural formation
 *         <li>-Dcucumber.filter.tags="@MemoryIdentity" - State and experience recording
 *         <li>-Dcucumber.filter.tags="@FunctionalIdentity" - Operational capabilities
 *       </ul>
 *   <li>Run by biological phase:
 *       <ul>
 *         <li>-Dcucumber.filter.tags="@Conception" - Initial creation phase
 *         <li>-Dcucumber.filter.tags="@Embryonic" - Basic structure formation phase
 *         <li>-Dcucumber.filter.tags="@Infancy" - Initial capability development phase
 *         <li>-Dcucumber.filter.tags="@Childhood" - Active functional development phase
 *       </ul>
 *   <li>Run positive or negative tests:
 *       <ul>
 *         <li>-Dcucumber.filter.tags="@Positive" - Expected behavior tests
 *         <li>-Dcucumber.filter.tags="@Negative" - Error handling tests
 *       </ul>
 *   <li>Run by capability:
 *       <ul>
 *         <li>-Dcucumber.filter.tags="@Identity" - Identity-related capability
 *         <li>-Dcucumber.filter.tags="@Structure" - Structural capability
 *         <li>-Dcucumber.filter.tags="@State" - State management capability
 *         <li>-Dcucumber.filter.tags="@Function" - Functional capability
 *         <li>-Dcucumber.filter.tags="@Flow" - Data flow capability
 *       </ul>
 *   <li>Run by epic:
 *       <ul>
 *         <li>-Dcucumber.filter.tags="@UniqueIdentification" - UUID and uniqueness
 *         <li>-Dcucumber.filter.tags="@ConnectionPoints" - Connection framework
 *         <li>-Dcucumber.filter.tags="@StatePersistence" - State management
 *         <li>-Dcucumber.filter.tags="@DataProcessing" - Data processing operations
 *       </ul>
 *   <li>Combined filter examples:
 *       <ul>
 *         <li>-Dcucumber.filter.tags="@Conception and @Positive" - Positive conception tests
 *         <li>-Dcucumber.filter.tags="@SubstrateIdentity and @ATL" - Critical substrate identity
 *             tests
 *         <li>-Dcucumber.filter.tags="@StructuralIdentity and not @Negative" - All positive
 *             structural tests
 *         <li>-Dcucumber.filter.tags="@Childhood and @FunctionalIdentity" - Childhood functional
 *             tests
 *       </ul>
 * </ul>
 *
 * <p>This runner can be used with Maven profiles defined in the pom.xml:
 *
 * <ul>
 *   <li>mvn test -P conception-tests
 *   <li>mvn test -P embryonic-tests
 *   <li>mvn test -P infancy-tests
 *   <li>mvn test -P childhood-tests
 *   <li>mvn test -P substrate-identity-tests
 *   <li>mvn test -P structural-identity-tests
 *   <li>mvn test -P memory-identity-tests
 *   <li>mvn test -P functional-identity-tests
 *   <li>mvn test -P positive-tests
 *   <li>mvn test -P negative-tests
 * </ul>
 */
@Suite
@SuiteDisplayName("Tube Lifecycle Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.samstraumr.tube.lifecycle.steps")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/lifecycle/cucumber.html, "
            + "json:target/cucumber-reports/lifecycle/cucumber.json")
public class RunTubeLifecycleCucumberTest {}
