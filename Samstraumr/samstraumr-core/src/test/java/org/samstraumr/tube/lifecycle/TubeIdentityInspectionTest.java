/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

package org.samstraumr.tube.lifecycle;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.TubeIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This test class provides detailed inspection of TubeIdentity instances during creation and
 * lifecycle operations, showing what's being instantiated in memory.
 */
public class TubeIdentityInspectionTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(TubeIdentityInspectionTest.class);

  private Environment environment;

  @BeforeEach
  public void setUp() {
    environment = new Environment();
  }

  @Test
  public void inspectStandaloneIdentityCreation() {
    LOGGER.info("===== Testing Standalone Identity Creation =====");

    // Create a simple TubeIdentity
    String reason = "Identity Inspection Test";
    LOGGER.info("Creating TubeIdentity with reason: {}", reason);

    TubeIdentity identity = TubeIdentity.createWithRandomId(reason);

    // Log identity details
    LOGGER.info("Identity Created:");
    LOGGER.info("  - Unique ID: {}", identity.getUniqueId());
    LOGGER.info("  - Reason: {}", identity.getReason());
    LOGGER.info("  - Conception Time: {}", identity.getConceptionTime());
    LOGGER.info("  - Is Adam Tube: {}", identity.isAdamTube());
    LOGGER.info("  - Hierarchical Address: {}", identity.getHierarchicalAddress());
    LOGGER.info("  - Parent Identity: {}", identity.getParentIdentity());
    LOGGER.info("  - Descendants Count: {}", identity.getDescendants().size());
    LOGGER.info("  - Environment Context: {}", identity.getEnvironmentContext());
    LOGGER.info("  - Lineage: {}", identity.getLineage());

    // Verify basic properties
    assertNotNull(identity.getUniqueId(), "ID should be created");
    assertEquals(reason, identity.getReason(), "Reason should match");
    assertNotNull(identity.getConceptionTime(), "Conception time should be set");
  }

  @Test
  public void inspectAdamTubeIdentityCreation() {
    LOGGER.info("===== Testing Adam Tube Identity Creation =====");

    String reason = "Adam Tube Test";
    LOGGER.info("Creating Adam TubeIdentity with reason: {}", reason);

    TubeIdentity adamIdentity = TubeIdentity.createAdamIdentity(reason, environment);

    // Log identity details
    LOGGER.info("Adam Identity Created:");
    LOGGER.info("  - Unique ID: {}", adamIdentity.getUniqueId());
    LOGGER.info("  - Reason: {}", adamIdentity.getReason());
    LOGGER.info("  - Conception Time: {}", adamIdentity.getConceptionTime());
    LOGGER.info("  - Is Adam Tube: {}", adamIdentity.isAdamTube());
    LOGGER.info("  - Hierarchical Address: {}", adamIdentity.getHierarchicalAddress());
    LOGGER.info("  - Parent Identity: {}", adamIdentity.getParentIdentity());
    LOGGER.info("  - Descendants Count: {}", adamIdentity.getDescendants().size());
    LOGGER.info("  - Environment Context Keys: {}", adamIdentity.getEnvironmentContext().keySet());
    LOGGER.info("  - Lineage: {}", adamIdentity.getLineage());

    // Verify Adam-specific properties
    assertTrue(adamIdentity.isAdamTube(), "Should be an Adam tube");
    assertNull(adamIdentity.getParentIdentity(), "Adam tube should have no parent");
    assertTrue(
        adamIdentity.getHierarchicalAddress().startsWith("T<"),
        "Adam tube should have root-level address");
  }

  @Test
  public void inspectParentChildRelationship() {
    LOGGER.info("===== Testing Parent-Child Relationship =====");

    // Create parent (Adam) tube identity
    String parentReason = "Parent Tube";
    LOGGER.info("Creating parent identity with reason: {}", parentReason);
    TubeIdentity parentIdentity = TubeIdentity.createAdamIdentity(parentReason, environment);

    // Create child tube identity
    String childReason = "Child Tube";
    LOGGER.info("Creating child identity with reason: {}", childReason);
    TubeIdentity childIdentity =
        TubeIdentity.createChildIdentity(childReason, environment, parentIdentity);

    // Add child to parent's descendants
    parentIdentity.addChild(childIdentity);

    // Log parent identity after adding child
    LOGGER.info("Parent Identity After Creating Child:");
    LOGGER.info("  - Unique ID: {}", parentIdentity.getUniqueId());
    LOGGER.info("  - Is Adam Tube: {}", parentIdentity.isAdamTube());
    LOGGER.info("  - Hierarchical Address: {}", parentIdentity.getHierarchicalAddress());
    LOGGER.info("  - Descendants Count: {}", parentIdentity.getDescendants().size());
    if (!parentIdentity.getDescendants().isEmpty()) {
      LOGGER.info(
          "  - First Descendant ID: {}", parentIdentity.getDescendants().get(0).getUniqueId());
    }

    // Log child identity details
    LOGGER.info("Child Identity:");
    LOGGER.info("  - Unique ID: {}", childIdentity.getUniqueId());
    LOGGER.info("  - Reason: {}", childIdentity.getReason());
    LOGGER.info("  - Is Adam Tube: {}", childIdentity.isAdamTube());
    LOGGER.info("  - Hierarchical Address: {}", childIdentity.getHierarchicalAddress());
    LOGGER.info(
        "  - Parent Identity ID: {}",
        childIdentity.getParentIdentity() != null
            ? childIdentity.getParentIdentity().getUniqueId()
            : "null");
    LOGGER.info("  - Lineage: {}", childIdentity.getLineage());

    // Verify parent-child relationship
    assertFalse(childIdentity.isAdamTube(), "Child should not be an Adam tube");
    assertEquals(
        parentIdentity, childIdentity.getParentIdentity(), "Child should reference parent");
    assertTrue(
        parentIdentity.getDescendants().contains(childIdentity), "Parent should track child");
    assertTrue(
        childIdentity
            .getHierarchicalAddress()
            .startsWith(parentIdentity.getHierarchicalAddress() + "."),
        "Child address should be derived from parent");
  }

  @Test
  public void inspectEnvironmentalContextCapture() {
    LOGGER.info("===== Testing Environmental Context Capture =====");

    // Create special environment with custom parameters
    Environment customEnv = new Environment();
    customEnv.setParameter("testMode", "true");
    customEnv.setParameter("region", "test-region");
    customEnv.setParameter("securityLevel", "high");

    // Create identity with this environment
    TubeIdentity identity = TubeIdentity.createAdamIdentity("Environmental Test", customEnv);

    // Log environment context
    LOGGER.info("Identity Environmental Context:");
    for (Map.Entry<String, String> entry : identity.getEnvironmentContext().entrySet()) {
      LOGGER.info("  - {}: {}", entry.getKey(), entry.getValue());
    }

    // Add custom environmental context
    identity.addEnvironmentContext("customKey", "customValue");
    LOGGER.info("After adding custom context:");
    LOGGER.info("  - Custom key value: {}", identity.getEnvironmentContext().get("customKey"));

    // Verify environmental context
    assertFalse(
        identity.getEnvironmentContext().isEmpty(), "Environmental context should not be empty");
  }

  @Test
  public void inspectComplexHierarchy() {
    LOGGER.info("===== Testing Complex Hierarchy =====");

    // Create a multi-generational hierarchy
    TubeIdentity adam = TubeIdentity.createAdamIdentity("Adam", environment);

    // First generation - 3 children
    TubeIdentity[] firstGen = new TubeIdentity[3];
    for (int i = 0; i < 3; i++) {
      firstGen[i] = TubeIdentity.createChildIdentity("First Gen " + (i + 1), environment, adam);
      adam.addChild(firstGen[i]);
    }

    // Second generation - 2 children from each first gen
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 2; j++) {
        TubeIdentity secondGen =
            TubeIdentity.createChildIdentity(
                "Second Gen " + (i + 1) + "-" + (j + 1), environment, firstGen[i]);
        firstGen[i].addChild(secondGen);
      }
    }

    // Log hierarchy details
    LOGGER.info("Hierarchy Created:");
    LOGGER.info("Adam Tube: {}", adam.getUniqueId());
    LOGGER.info("Adam Address: {}", adam.getHierarchicalAddress());
    LOGGER.info("Adam has {} direct descendants", adam.getDescendants().size());

    for (int i = 0; i < 3; i++) {
      LOGGER.info(
          "First Gen {} ({}): {}", i + 1, firstGen[i].getReason(), firstGen[i].getUniqueId());
      LOGGER.info("  Address: {}", firstGen[i].getHierarchicalAddress());
      LOGGER.info("  Has {} children", firstGen[i].getDescendants().size());

      for (TubeIdentity grandchild : firstGen[i].getDescendants()) {
        LOGGER.info("  - Grandchild ({}): {}", grandchild.getReason(), grandchild.getUniqueId());
        LOGGER.info("    Address: {}", grandchild.getHierarchicalAddress());
      }
    }

    // Verify hierarchy properties
    assertEquals(3, adam.getDescendants().size(), "Adam should have 3 children");
    for (int i = 0; i < 3; i++) {
      assertEquals(2, firstGen[i].getDescendants().size(), "First gen should have 2 children each");
    }
  }

  @Test
  public void inspectTubeWithIdentity() {
    LOGGER.info("===== Testing Tube with Identity Integration =====");

    // Create a tube through the normal API
    Tube tube = Tube.create("Identity Integration Test", environment);

    // Log tube identity-related properties
    LOGGER.info("Tube Created:");
    LOGGER.info("  - Unique ID: {}", tube.getUniqueId());
    LOGGER.info("  - Reason: {}", tube.getReason());
    LOGGER.info("  - Lineage entries: {}", tube.getLineage().size());
    LOGGER.info("  - Lineage content: {}", tube.getLineage());
    LOGGER.info("  - Mimir Log size: {}", tube.getMimirLogSize());

    // Sample a few entries from the Mimir Log
    LOGGER.info("Mimir Log Sample:");
    int count = 0;
    for (String entry : tube.queryMimirLog()) {
      if (count++ < 5) { // just show first 5 entries
        LOGGER.info("  - {}", entry);
      } else {
        break;
      }
    }

    // Add to lineage and see how it's reflected
    tube.addToLineage("Test lineage entry");
    LOGGER.info("After adding to lineage:");
    LOGGER.info("  - Lineage size: {}", tube.getLineage().size());
    LOGGER.info("  - Last lineage entry: {}", tube.getLineage().get(tube.getLineage().size() - 1));

    // Verify basic properties
    assertNotNull(tube.getUniqueId(), "Tube should have a unique ID");
    assertTrue(tube.getMimirLogSize() > 0, "Tube should have log entries");
    assertEquals("Identity Integration Test", tube.getReason(), "Reason should match");
  }
}
