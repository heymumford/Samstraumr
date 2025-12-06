/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import io.cucumber.junit.platform.engine.Constants;

/**
 * Test runner for Maven structure BDD tests.
 *
 * <p>This test runner executes the Cucumber feature tests for verifying the Maven project structure
 * in the Samstraumr project. It uses the maven-structure tag to filter for the relevant feature
 * files.
 *
 * <p>The tests verify:
 *
 * <ul>
 *   <li>Basic Maven structure (presence of required POM files)
 *   <li>Source and test directories existence
 *   <li>Clean architecture layer compliance
 *   <li>Package structure for domain, application and infrastructure
 *   <li>Port interface packages existence
 * </ul>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("tube/features/L0_Orchestration/maven-structure-test.feature")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "org.s8r.test.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.FILTER_TAGS_PROPERTY_NAME, value = "@maven-structure")
public class MavenStructureTests {
  // This class is intentionally empty. It's used only as a holder for the annotations.
}
