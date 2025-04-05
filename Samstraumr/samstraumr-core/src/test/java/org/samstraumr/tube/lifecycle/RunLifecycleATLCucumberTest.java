/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.lifecycle;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludePackages;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Cucumber test runner for Above The Line (ATL) biological lifecycle tests. This runner focuses on
 * critical tests for biological lifecycle phases that must pass with every build.
 *
 * <p>Tags included in this runner:
 *
 * <ul>
 *   <li>@ATL - Above The Line (critical) tests
 *   <li>And one of the biological phases: @Conception, @Embryonic, @Infancy, @Childhood
 * </ul>
 */
@Suite
@IncludePackages("org.samstraumr.tube.lifecycle")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, json:target/cucumber-reports/lifecycle-ATL.json, html:target/cucumber-reports/lifecycle-ATL.html")
@IncludeTags("ATL")
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@ATL and (@Conception or @Embryonic or @Infancy or @Childhood)")
public class RunLifecycleATLCucumberTest {
  // This class serves as a cucumber test runner
}
