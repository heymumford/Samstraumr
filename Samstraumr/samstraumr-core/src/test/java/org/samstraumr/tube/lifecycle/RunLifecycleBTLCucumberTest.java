package org.samstraumr.tube.lifecycle;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludePackages;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

/**
 * Cucumber test runner for Below The Line (BTL) biological lifecycle tests.
 * This runner focuses on robustness tests for biological lifecycle phases that provide 
 * comprehensive coverage but are not critical for every build.
 *
 * <p>Tags included in this runner:
 * <ul>
 *   <li>@BTL - Below The Line (robustness) tests
 *   <li>And one of the biological phases: 
 *       @Conception, @Embryonic, @Infancy, @Childhood
 * </ul>
 */
@Suite
@IncludePackages("org.samstraumr.tube.lifecycle")
@SelectClasspathResource("tube/features/L0_Tube/lifecycle")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, json:target/cucumber-reports/lifecycle-BTL.json, html:target/cucumber-reports/lifecycle-BTL.html")
@IncludeTags("BTL")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@BTL and (@Conception or @Embryonic or @Infancy or @Childhood)")
public class RunLifecycleBTLCucumberTest {
    // This class serves as a cucumber test runner
}
