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

package org.s8r.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.component.Component;
import org.s8r.component.Composite;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.UnitTest;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;

/**
 * Tests for the CompositeAdapter class.
 *
 * <p>These tests verify that the CompositeAdapter correctly bridges between the legacy Tube-based
 * composites and the new Component-based composites.
 */
@UnitTest
public class CompositeAdapterTest {

  private S8rMigrationFactory factory;
  private CompositeAdapter adapter;

  @BeforeEach
  public void setUp() {
    factory = new S8rMigrationFactory(new ConsoleLogger("Test"));
    adapter = factory.getCompositeAdapter();
  }

  @Test
  public void testTubeCompositeToComponentComposite() {
    // Create a tube composite with tubes
    Environment env = new Environment();
    env.setParameter("name", "TestComposite");

    org.s8r.tube.composite.Composite tubeComposite =
        new org.s8r.tube.composite.Composite("test-composite", env);

    // Add tubes to the composite
    Tube tube1 = Tube.create("Input tube", env);
    Tube tube2 = Tube.create("Processor tube", env);
    Tube tube3 = Tube.create("Output tube", env);

    tubeComposite.addTube("input", tube1);
    tubeComposite.addTube("processor", tube2);
    tubeComposite.addTube("output", tube3);

    // Connect tubes
    tubeComposite.connect("input", "processor");
    tubeComposite.connect("processor", "output");

    // Convert to component composite
    Composite componentComposite = adapter.tubeCompositeToComponentComposite(tubeComposite);

    // Verify conversion
    assertNotNull(componentComposite);
    assertEquals("test-composite", componentComposite.getCompositeId());

    // Verify components were converted
    Map<String, Component> components = componentComposite.getComponents();
    assertEquals(3, components.size());
    assertTrue(components.containsKey("input"));
    assertTrue(components.containsKey("processor"));
    assertTrue(components.containsKey("output"));

    // Verify connections were preserved
    Map<String, java.util.List<String>> connections = componentComposite.getConnections();
    assertEquals(2, connections.size());
    assertTrue(connections.containsKey("input"));
    assertTrue(connections.get("input").contains("processor"));
    assertTrue(connections.containsKey("processor"));
    assertTrue(connections.get("processor").contains("output"));
  }

  @Test
  public void testWrapTubeComposite() {
    // Create a tube composite with tubes
    Environment env = new Environment();
    org.s8r.tube.composite.Composite tubeComposite =
        new org.s8r.tube.composite.Composite("wrapped-composite", env);

    // Add tubes to the composite
    Tube tube1 = Tube.create("Source", env);
    Tube tube2 = Tube.create("Target", env);

    tubeComposite.addTube("source", tube1);
    tubeComposite.addTube("target", tube2);

    // Connect tubes
    tubeComposite.connect("source", "target");

    // Create wrapper
    Composite wrapper = adapter.wrapTubeComposite(tubeComposite);

    // Verify wrapper
    assertNotNull(wrapper);
    assertEquals("wrapped-composite", wrapper.getCompositeId());

    // Get components from wrapper
    Component source = wrapper.getComponent("source");
    Component target = wrapper.getComponent("target");

    assertNotNull(source);
    assertNotNull(target);

    // Verify connections
    Map<String, java.util.List<String>> connections = wrapper.getConnections();
    assertTrue(connections.containsKey("source"));
    assertTrue(connections.get("source").contains("target"));

    // Test data processing through the wrapper
    // This requires setting up transformer and process methods which is beyond scope of this test
  }

  @Test
  public void testHybridComposite() {
    // Create a hybrid composite
    Environment env = new Environment();
    Composite hybrid = adapter.createHybridComposite("hybrid-composite", env);

    // Add a wrapped tube
    Tube tube = Tube.create("Legacy tube", env);
    org.s8r.domain.component.port.ComponentPort domainWrappedTube = factory.tubeToComponent(tube);
    org.s8r.component.Component wrappedTube =
        ComponentTypeAdapter.fromDomainComponent(domainWrappedTube);
    hybrid.addComponent("legacy", wrappedTube);

    // Add a native component
    org.s8r.component.Environment componentEnv = factory.tubeEnvironmentToS8rEnvironment(env);
    // Create a domain component and convert it using our test adapter
    org.s8r.domain.component.Component domainComponent =
        org.s8r.domain.component.Component.create(
            org.s8r.domain.identity.ComponentId.create("Native component"));
    Component nativeComponent = ComponentTypeAdapter.fromDomainComponent(domainComponent);
    hybrid.addComponent("native", nativeComponent);

    // Verify components
    assertNotNull(hybrid.getComponent("legacy"));
    assertNotNull(hybrid.getComponent("native"));

    // Connect them
    hybrid.connect("legacy", "native");

    // Verify connection
    Map<String, java.util.List<String>> connections = hybrid.getConnections();
    assertTrue(connections.containsKey("legacy"));
    assertTrue(connections.get("legacy").contains("native"));
  }

  @Test
  public void testAddTubeToComponentComposite() {
    // Create source tube composite
    Environment env = new Environment();
    org.s8r.tube.composite.Composite tubeComposite =
        new org.s8r.tube.composite.Composite("source-composite", env);

    // Add a tube to it
    Tube tube = Tube.create("Transferable tube", env);
    tubeComposite.addTube("source-tube", tube);

    // Create target component composite
    org.s8r.component.Environment componentEnv = factory.tubeEnvironmentToS8rEnvironment(env);
    Composite componentComposite = new Composite("target-composite", componentEnv);

    // Transfer the tube
    adapter.addTubeToComponentComposite(
        tubeComposite, "source-tube", componentComposite, "target-component");

    // Verify the transfer
    Component transferredComponent = componentComposite.getComponent("target-component");
    assertNotNull(transferredComponent);

    // Verify the component is present
    assertNotNull(transferredComponent);
    // Check that the component has information from the original tube
    assertEquals("target-component", transferredComponent.getEnvironment().getParameter("name"));
  }
}
