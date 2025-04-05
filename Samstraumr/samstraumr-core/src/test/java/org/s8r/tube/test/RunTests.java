/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Test execution utility for Samstraumr with unified terminology support.
 *
 * This class provides a flexible test runner that bridges the gap between industry-standard
 * and Samstraumr-specific terminology. It allows tests to be run using either set of terms,
 * facilitating a more intuitive testing experience for developers familiar with standard
 * testing terminology while maintaining Samstraumr's domain-specific naming conventions.
 *
 * Key features:
 * - Support for both industry-standard and Samstraumr-specific test tags
 * - Bidirectional terminology mapping between equivalent concepts
 * - Ability to include tests from both terminologies in a single run
 * - Comprehensive test execution summary generation
 *
 * Usage example:
 * ```
 * // Run unit tests (industry-standard) and tube tests (Samstraumr equivalent)
 * RunTests.runAndPrintResults("unit", true);
 *
 * // Run only Samstraumr-specific acceptance tests
 * RunTests.runAndPrintResults("acceptance", false);
 * ```
 */

package org.s8r.tube.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test runner utility that allows running tests with either industry-standard or
 * Samstraumr-specific terminology.
 */
public class RunTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(RunTests.class);
  private static final Map<String, String> TERMINOLOGY_MAP = createTerminologyMap();

  private static Map<String, String> createTerminologyMap() {
    Map<String, String> map = new HashMap<>();
    // Industry standard to Samstraumr mapping
    map.put("smoke", "orchestration");
    map.put("unit", "tube");
    map.put("component", "composite");
    map.put("integration", "flow");
    map.put("api", "machine");
    map.put("system", "stream");
    map.put("endtoend", "acceptance");
    map.put("property", "adaptation");
    map.put("adam", "adamtube");

    // Also add the reverse mappings
    map.put("orchestration", "smoke");
    map.put("tube", "unit");
    map.put("composite", "component");
    map.put("flow", "integration");
    map.put("machine", "api");
    map.put("stream", "system");
    map.put("acceptance", "endtoend");
    map.put("adaptation", "property");
    map.put("adamtube", "adam");
    return map;
  }

  /**
   * Run tests with the specified tag.
   *
   * @param tag The tag to filter tests by
   * @param includeEquivalent Whether to include tests with equivalent tags in the other terminology
   */
  public static TestExecutionSummary runTestsWithTag(String tag, boolean includeEquivalent) {
    LauncherDiscoveryRequestBuilder requestBuilder =
        LauncherDiscoveryRequestBuilder.request()
            .selectors(DiscoverySelectors.selectPackage("org.samstraumr"));

    if (includeEquivalent && TERMINOLOGY_MAP.containsKey(tag.toLowerCase())) {
      String equivalentTag = TERMINOLOGY_MAP.get(tag.toLowerCase());
      requestBuilder.filters(TagFilter.includeTags(tag, equivalentTag));
    } else {
      requestBuilder.filters(TagFilter.includeTags(tag));
    }

    LauncherDiscoveryRequest request = requestBuilder.build();
    Launcher launcher = LauncherFactory.create();
    SummaryGeneratingListener listener = new SummaryGeneratingListener();

    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);

    return listener.getSummary();
  }

  /**
   * Run tests with the specified tag and print results to the console.
   *
   * @param tag The tag to filter tests by
   * @param includeEquivalent Whether to include tests with equivalent tags in the other terminology
   */
  public static void runAndPrintResults(String tag, boolean includeEquivalent) {
    TestExecutionSummary summary = runTestsWithTag(tag, includeEquivalent);

    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    summary.printTo(printWriter);
    printWriter.flush();

    LOGGER.info(
        "Test execution summary for tag '{}' (including equivalent: {}):\n{}",
        tag,
        includeEquivalent,
        stringWriter.toString());
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      LOGGER.error("Usage: RunTests <tag> [includeEquivalent]");
      System.exit(1);
    }

    String tag = args[0];
    boolean includeEquivalent = args.length > 1 && Boolean.parseBoolean(args[1]);

    LOGGER.info("Running tests with tag: '{}', include equivalent: {}", tag, includeEquivalent);
    runAndPrintResults(tag, includeEquivalent);
  }
}
