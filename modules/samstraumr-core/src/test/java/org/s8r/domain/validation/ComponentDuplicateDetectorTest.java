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

package org.s8r.domain.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.exception.PropertyBasedDuplicateComponentException;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.validation.ComponentDuplicateDetector.DuplicateAnalysisResult;

/** Tests for the ComponentDuplicateDetector class. */
public class ComponentDuplicateDetectorTest {

  @Mock private ComponentRepository componentRepository;
  @Mock private LoggerPort logger;
  @Mock private ComponentPort component1;
  @Mock private ComponentPort component2;
  @Mock private ComponentPort component3;
  @Mock private ComponentId id1;
  @Mock private ComponentId id2;
  @Mock private ComponentId id3;

  private ComponentDuplicateDetector detector;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);

    // Setup component IDs
    when(id1.getIdString()).thenReturn("comp-1");
    when(id2.getIdString()).thenReturn("comp-2");
    when(id3.getIdString()).thenReturn("comp-3");

    // Setup components with their IDs
    when(component1.getId()).thenReturn(id1);
    when(component2.getId()).thenReturn(id2);
    when(component3.getId()).thenReturn(id3);

    // Setup lifecycle states
    when(component1.getLifecycleState()).thenReturn(LifecycleState.ACTIVE);
    when(component2.getLifecycleState()).thenReturn(LifecycleState.ACTIVE);
    when(component3.getLifecycleState()).thenReturn(LifecycleState.ACTIVE);

    // Initialize detector
    detector = new ComponentDuplicateDetector(componentRepository, logger);
  }

  @Test
  public void testNoDuplicatesWhenNoPropertiesMatch() {
    // Setup component properties
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Component A");
    props1.put("type", "Processor");
    props1.put("purpose", "Data processing");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Component B");
    props2.put("type", "Storage");
    props2.put("purpose", "Data storage");

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    List<ComponentPort> duplicates = detector.checkForDuplicates(component1);

    assertTrue(duplicates.isEmpty(), "Should not detect duplicates when properties don't match");
  }

  @Test
  public void testDetectsDuplicateWithExactPropertyMatches() {
    // Setup component properties with exact matches
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Processor");
    props1.put("type", "Processor");
    props1.put("purpose", "Processing data streams");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Data Processor"); // Exact match
    props2.put("type", "Processor"); // Exact match
    props2.put("purpose", "Different purpose");

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    List<ComponentPort> duplicates = detector.checkForDuplicates(component1);

    assertFalse(duplicates.isEmpty(), "Should detect duplicate with exact name and type match");
    assertEquals(component2, duplicates.get(0), "Should return component2 as a duplicate");
  }

  @Test
  public void testDetectsDuplicateWithSimilarPropertyValues() {
    // Setup component properties with similar values
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Processing Service");
    props1.put("description", "Processes incoming data streams for analysis");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Data Processor Service"); // Similar but not exact
    props2.put("description", "Service that processes data streams for analytics"); // Similar

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    List<ComponentPort> duplicates = detector.checkForDuplicates(component1);

    assertFalse(duplicates.isEmpty(), "Should detect duplicate with similar property values");
  }

  @Test
  public void testStrictModeThrowsException() {
    // Setup component properties with exact matches
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Processor");
    props1.put("type", "Processor");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Data Processor");
    props2.put("type", "Processor");

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    // Enable strict mode
    detector.setStrictMode(true);

    assertThrows(
        PropertyBasedDuplicateComponentException.class,
        () -> detector.checkForDuplicates(component1),
        "Should throw exception in strict mode when duplicate is found");
  }

  @Test
  public void testCustomThresholdChangesResults() {
    // Setup component properties with moderate similarity
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Service");
    props1.put("category", "Processing");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Database Service"); // Some similarity
    props2.put("category", "Storage"); // Different

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    // With high threshold, should not detect as duplicate
    List<ComponentPort> duplicatesHighThreshold = detector.checkForDuplicates(component1, 0.9);
    assertTrue(
        duplicatesHighThreshold.isEmpty(), "Should not detect as duplicate with high threshold");

    // With low threshold, should detect as duplicate
    List<ComponentPort> duplicatesLowThreshold = detector.checkForDuplicates(component1, 0.3);
    assertFalse(duplicatesLowThreshold.isEmpty(), "Should detect as duplicate with low threshold");
  }

  @Test
  public void testAnalyzePotentialDuplicate() {
    // Setup component properties
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Processor");
    props1.put("type", "Processor");
    props1.put("purpose", "Processing data streams");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Data Processor");
    props2.put("type", "Processor");
    props2.put("purpose", "Different purpose");

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    DuplicateAnalysisResult result = detector.analyzePotentialDuplicate(component1, component2);

    // Should have high similarity score
    assertTrue(result.getSimilarityScore() > 0.6, "Similarity score should be high");

    // Should have detected matching properties
    assertTrue(
        result.getMatchingProperties().contains("name"),
        "Should detect 'name' as matching property");
    assertTrue(
        result.getMatchingProperties().contains("type"),
        "Should detect 'type' as matching property");

    // Should have individual property scores
    assertNotNull(result.getPropertyScores().get("name"), "Should have score for 'name' property");
    assertNotNull(result.getPropertyScores().get("type"), "Should have score for 'type' property");
    assertNotNull(
        result.getPropertyScores().get("purpose"), "Should have score for 'purpose' property");

    // Should have a non-empty reason string
    assertNotNull(result.getDuplicateReason());
    assertFalse(result.getDuplicateReason().isEmpty());
  }

  @Test
  public void testIgnoredPropertiesAreSkipped() {
    // Setup component properties
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Processor");
    props1.put("type", "Processor");
    props1.put("id", "A12345"); // Should be ignored

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Data Processor");
    props2.put("type", "Processor");
    props2.put("id", "B67890"); // Different, but should be ignored

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    // Ignore the 'id' property
    detector.ignoreProperty("id");

    List<ComponentPort> duplicates = detector.checkForDuplicates(component1);

    assertFalse(
        duplicates.isEmpty(), "Should detect duplicate despite ignored property difference");
  }

  @Test
  public void testCustomPropertyMatcher() {
    // Setup component properties
    Map<String, Object> props1 = new HashMap<>();
    props1.put("version", "1.2.0");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("version", "1.2.5"); // Similar version

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2));

    // Add custom version matcher that considers versions in the same minor version as similar
    detector.addPropertyMatcher(
        "version",
        (v1, v2) -> {
          String[] parts1 = v1.toString().split("\\.");
          String[] parts2 = v2.toString().split("\\.");

          // If major and minor versions match, consider them similar
          if (parts1.length >= 2
              && parts2.length >= 2
              && parts1[0].equals(parts2[0])
              && parts1[1].equals(parts2[1])) {
            return 0.9;
          }
          return 0.0;
        });

    List<ComponentPort> duplicates = detector.checkForDuplicates(component1);

    assertFalse(duplicates.isEmpty(), "Should detect duplicate based on custom version matcher");
  }

  @Test
  public void testMultipleDuplicates() {
    // Setup component properties for multiple potential duplicates
    Map<String, Object> props1 = new HashMap<>();
    props1.put("name", "Data Processor");
    props1.put("type", "Processor");

    Map<String, Object> props2 = new HashMap<>();
    props2.put("name", "Data Processor"); // Same name
    props2.put("type", "Different Type");

    Map<String, Object> props3 = new HashMap<>();
    props3.put("name", "Different Name");
    props3.put("type", "Processor"); // Same type

    when(component1.getProperties()).thenReturn(props1);
    when(component2.getProperties()).thenReturn(props2);
    when(component3.getProperties()).thenReturn(props3);

    when(componentRepository.findAll()).thenReturn(Arrays.asList(component2, component3));

    // With medium threshold, both should be detected
    List<ComponentPort> duplicates = detector.checkForDuplicates(component1, 0.5);

    assertEquals(2, duplicates.size(), "Should detect both components as duplicates");
    assertTrue(duplicates.contains(component2), "Should include component2 as a duplicate");
    assertTrue(duplicates.contains(component3), "Should include component3 as a duplicate");
  }
}
