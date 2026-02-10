/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.test.tube;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.s8r.core.env.Environment;
import org.s8r.core.tube.identity.Identity;
import org.s8r.core.tube.identity.IdentityInspector;
import org.s8r.core.tube.impl.Component;
import org.slf4j.LoggerFactory;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for Adam Tube concept tests. The Adam Tube concept represents the first tube
 * created in a system, which by definition cannot have a parent.
 */
public class AdamTubeSteps {
  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AdamTubeSteps.class);

  private TestContext context = new TestContext();
  private Environment environment;
  private Component originTube;
  private Identity originIdentity;
  private List<Component> childTubes = new ArrayList<>();
  private Exception exceptionThrown;

  @Given("the system environment is properly configured for Adam tests")
  public void the_system_environment_is_properly_configured_for_adam_tests() {
    LOGGER.info("Configuring system environment for Adam Tube tests");
    environment = new Environment();
    environment.setParameter("adam_test", "true");
    environment.setParameter("host", "localhost");
    environment.setParameter("mode", "test");

    assertNotNull(environment, "Environment should be initialized successfully");
    context.store("environment", environment);
  }

  @When("an origin tube is created without a parent")
  public void an_origin_tube_is_created_without_a_parent() {
    LOGGER.info("Creating an origin tube (Adam tube) without a parent");
    try {
      // If environment wasn't set in this scenario, use the one from AtomicTubeSteps or create a
      // new one
      if (environment == null) {
        environment =
            context.contains("environment") ? context.get("environment") : new Environment();
      }

      originTube = Component.createAdam("Origin Tube Test", environment);
      originIdentity = originTube.getIdentity();

      assertNotNull(originTube, "Origin tube should be created successfully");
      assertNotNull(originIdentity, "Origin tube identity should be created successfully");

      context.store("originTube", originTube);
      context.store("originIdentity", originIdentity);
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception creating origin tube: {}", e.getMessage());
    }
  }

  @Then("the tube should be identified as an Adam tube")
  public void the_tube_should_be_identified_as_an_adam_tube() {
    LOGGER.info("Verifying tube is identified as an Adam tube");
    originTube = context.contains("originTube") ? context.get("originTube") : originTube;
    originIdentity =
        context.contains("originIdentity") ? context.get("originIdentity") : originIdentity;

    assertNotNull(originTube, "Origin tube should exist");
    assertNotNull(originIdentity, "Origin identity should exist");

    IdentityInspector inspector = new IdentityInspector();
    assertTrue(
        inspector.isAdamIdentity(originIdentity), "The tube should be identified as an Adam tube");
    assertNull(
        inspector.getParentIdentity(originIdentity), "An Adam tube should not have a parent");
  }

  @Then("the tube should capture the environmental context")
  public void the_tube_should_capture_the_environmental_context() {
    LOGGER.info("Verifying tube captures environmental context");
    originIdentity =
        context.contains("originIdentity") ? context.get("originIdentity") : originIdentity;

    assertNotNull(originIdentity, "Origin identity should exist");
    IdentityInspector inspector = new IdentityInspector();

    assertNotNull(
        inspector.getEnvironmentalContext(originIdentity),
        "Environmental context should not be null");
    assertFalse(
        inspector.getEnvironmentalContext(originIdentity).isEmpty(),
        "Environmental context should not be empty");
  }

  @Then("the tube should have a root-level hierarchical address")
  public void the_tube_should_have_a_root_level_hierarchical_address() {
    LOGGER.info("Verifying tube has a root-level hierarchical address");
    originIdentity =
        context.contains("originIdentity") ? context.get("originIdentity") : originIdentity;

    assertNotNull(originIdentity, "Origin identity should exist");
    IdentityInspector inspector = new IdentityInspector();

    String address = inspector.getHierarchicalAddress(originIdentity);
    assertNotNull(address, "Hierarchical address should not be null");
    assertTrue(address.startsWith("C<"), "Root-level address should start with 'C<'");
    assertFalse(
        address.contains("."), "Root-level address should not contain a dot (child indicator)");
  }

  @Given("an origin tube exists in the system")
  public void an_origin_tube_exists_in_the_system() {
    LOGGER.info("Setting up an origin tube in the system");

    if (originTube == null) {
      // If environment wasn't set in this scenario, use the one from context or create a new one
      if (environment == null) {
        environment =
            context.contains("environment") ? context.get("environment") : new Environment();
      }

      try {
        originTube = Component.createAdam("Origin Tube In System", environment);
        originIdentity = originTube.getIdentity();

        context.store("originTube", originTube);
        context.store("originIdentity", originIdentity);
      } catch (Exception e) {
        exceptionThrown = e;
        LOGGER.error("Exception creating origin tube: {}", e.getMessage());
        fail("Failed to create origin tube: " + e.getMessage());
      }
    }

    assertNotNull(originTube, "Origin tube should be created successfully");
    IdentityInspector inspector = new IdentityInspector();
    assertTrue(inspector.isAdamIdentity(originIdentity), "The tube should be an Adam tube");
  }

  @When("the origin tube creates a child tube")
  public void the_origin_tube_creates_a_child_tube() {
    LOGGER.info("Creating a child tube from the origin tube");

    originTube = context.contains("originTube") ? context.get("originTube") : originTube;
    originIdentity =
        context.contains("originIdentity") ? context.get("originIdentity") : originIdentity;

    assertNotNull(originTube, "Origin tube should exist");
    assertNotNull(originIdentity, "Origin identity should exist");

    try {
      Component childTube = Component.createChild("Child of Origin", environment, originTube);
      Identity childIdentity = childTube.getIdentity();
      childTubes.add(childTube);

      context.store("childTube", childTube);
      context.store("childIdentity", childIdentity);

      assertNotNull(childTube, "Child tube should be created successfully");
      assertNotNull(childIdentity, "Child identity should be created successfully");

      IdentityInspector inspector = new IdentityInspector();
      assertFalse(inspector.isAdamIdentity(childIdentity), "Child tube should not be an Adam tube");
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception creating child tube: {}", e.getMessage());
      fail("Failed to create child tube: " + e.getMessage());
    }
  }

  @Then("the child tube should have the origin tube as parent")
  public void the_child_tube_should_have_the_origin_tube_as_parent() {
    LOGGER.info("Verifying child tube has origin tube as parent");

    Component childTube = context.get("childTube");
    Identity childIdentity = context.get("childIdentity");

    assertNotNull(childTube, "Child tube should exist");
    assertNotNull(childIdentity, "Child identity should exist");

    IdentityInspector inspector = new IdentityInspector();
    assertEquals(
        originIdentity,
        inspector.getParentIdentity(childIdentity),
        "Child tube should have origin tube as parent");
  }

  @Then("the child tube should not be an Adam tube")
  public void the_child_tube_should_not_be_an_adam_tube() {
    LOGGER.info("Verifying child tube is not an Adam tube");

    Identity childIdentity = context.get("childIdentity");
    assertNotNull(childIdentity, "Child identity should exist");

    IdentityInspector inspector = new IdentityInspector();
    assertFalse(inspector.isAdamIdentity(childIdentity), "Child tube should not be an Adam tube");
  }

  @Then("the child tube should have a hierarchical address derived from its parent")
  public void the_child_tube_should_have_a_hierarchical_address_derived_from_its_parent() {
    LOGGER.info("Verifying child tube's hierarchical address is derived from parent");

    Identity childIdentity = context.get("childIdentity");
    assertNotNull(childIdentity, "Child identity should exist");

    IdentityInspector inspector = new IdentityInspector();
    String parentAddress = inspector.getHierarchicalAddress(originIdentity);
    String childAddress = inspector.getHierarchicalAddress(childIdentity);

    assertNotNull(childAddress, "Child hierarchical address should not be null");
    assertTrue(
        childAddress.startsWith(parentAddress + "."),
        "Child address should start with parent address followed by dot");
  }

  @When("attempting to create an Adam tube with a parent reference")
  public void attempting_to_create_an_adam_tube_with_a_parent_reference() {
    LOGGER.info("Attempting to create an Adam tube with a parent reference");

    originTube = context.contains("originTube") ? context.get("originTube") : originTube;
    assertNotNull(originTube, "Origin tube should exist for this test");

    try {
      // This should fail because Adam tubes cannot have parents
      Component invalidAdamTube =
          Component.createAdam("Invalid Adam Tube", environment, originTube);

      // If we get here, the test should fail because an exception should have been thrown
      fail("Should not be able to create an Adam tube with a parent");
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.info("Exception thrown as expected: {}", e.getMessage());
    }
  }

  @When("multiple child tubes are created from the origin tube")
  public void multiple_child_tubes_are_created_from_the_origin_tube() {
    LOGGER.info("Creating multiple child tubes from the origin tube");

    originTube = context.contains("originTube") ? context.get("originTube") : originTube;
    assertNotNull(originTube, "Origin tube should exist");

    childTubes.clear();
    List<Identity> childIdentities = new ArrayList<>();

    try {
      for (int i = 0; i < 3; i++) {
        String reason = "Child " + i + " of Origin";
        Component childTube = Component.createChild(reason, environment, originTube);
        Identity childIdentity = childTube.getIdentity();

        childTubes.add(childTube);
        childIdentities.add(childIdentity);

        assertNotNull(childTube, "Child tube " + i + " should be created successfully");
        assertNotNull(childIdentity, "Child identity " + i + " should be created successfully");
      }

      context.store("childTubes", childTubes);
      context.store("childIdentities", childIdentities);

      assertEquals(3, childTubes.size(), "Should have created 3 child tubes");
    } catch (Exception e) {
      exceptionThrown = e;
      LOGGER.error("Exception creating child tubes: {}", e.getMessage());
      fail("Failed to create child tubes: " + e.getMessage());
    }
  }

  @Then("the origin tube should properly track all its descendants")
  public void the_origin_tube_should_properly_track_all_its_descendants() {
    LOGGER.info("Verifying origin tube tracks all its descendants");

    originTube = context.contains("originTube") ? context.get("originTube") : originTube;
    List<Component> children =
        context.contains("childTubes") ? context.get("childTubes") : childTubes;

    assertNotNull(originTube, "Origin tube should exist");
    assertFalse(children.isEmpty(), "There should be child tubes");

    IdentityInspector inspector = new IdentityInspector();
    List<Identity> descendants = inspector.getDescendants(originIdentity);

    assertNotNull(descendants, "Descendants list should not be null");
    assertEquals(
        children.size(),
        descendants.size(),
        "Origin tube should track the correct number of descendants");
  }

  @Then("each child should have the origin tube as its parent")
  public void each_child_should_have_the_origin_tube_as_its_parent() {
    LOGGER.info("Verifying each child has the origin tube as its parent");

    List<Component> children =
        context.contains("childTubes") ? context.get("childTubes") : childTubes;
    assertFalse(children.isEmpty(), "There should be child tubes");

    IdentityInspector inspector = new IdentityInspector();

    for (Component child : children) {
      Identity childIdentity = child.getIdentity();
      assertEquals(
          originIdentity,
          inspector.getParentIdentity(childIdentity),
          "Child should have origin tube as parent");
    }
  }

  @Then("the operation should be rejected")
  public void the_operation_should_be_rejected() {
    LOGGER.info("Verifying the operation was rejected");
    assertNotNull(exceptionThrown, "An exception should have been thrown");
    assertTrue(
        exceptionThrown instanceof IllegalArgumentException,
        "The exception should be of type IllegalArgumentException");
  }

  @Then("an appropriate error message should be logged")
  public void an_appropriate_error_message_should_be_logged() {
    LOGGER.info("Verifying appropriate error message was logged");
    assertNotNull(exceptionThrown, "An exception should have been thrown");
    assertNotNull(exceptionThrown.getMessage(), "Exception should have a message");
    assertTrue(
        exceptionThrown.getMessage().toLowerCase().contains("parent")
            || exceptionThrown.getMessage().toLowerCase().contains("adam"),
        "Error message should mention parent relationship or Adam tube constraints");
  }
}
