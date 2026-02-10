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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.component.Composite;
import org.s8r.component.Machine;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.UnitTest;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;

/**
 * Tests for the MachineAdapter class.
 *
 * <p>These tests verify that the MachineAdapter correctly bridges between the legacy Tube-based
 * machines and the new Component-based machines.
 */
@UnitTest
public class MachineAdapterTest {

  private S8rMigrationFactory factory;
  private MachineAdapter adapter;

  @BeforeEach
  public void setUp() {
    factory = new S8rMigrationFactory(new ConsoleLogger("Test"));
    adapter = factory.getMachineAdapter();
  }

  @Test
  public void testTubeMachineToComponentMachine() {
    // Create a tube machine with composites
    Environment env = new Environment();
    env.setParameter("name", "TestMachine");

    org.s8r.tube.machine.Machine tubeMachine =
        new org.s8r.tube.machine.Machine("test-machine", env);

    // Add composites to the machine
    org.s8r.tube.composite.Composite composite1 =
        new org.s8r.tube.composite.Composite("input-composite", env);
    org.s8r.tube.composite.Composite composite2 =
        new org.s8r.tube.composite.Composite("processor-composite", env);
    org.s8r.tube.composite.Composite composite3 =
        new org.s8r.tube.composite.Composite("output-composite", env);

    // Add tubes to composites
    Tube tube1 = Tube.create("Input tube", env);
    Tube tube2 = Tube.create("Processor tube", env);
    Tube tube3 = Tube.create("Output tube", env);

    composite1.addTube("input", tube1);
    composite2.addTube("processor", tube2);
    composite3.addTube("output", tube3);

    // Add composites to the machine
    tubeMachine.addComposite("input", composite1);
    tubeMachine.addComposite("processor", composite2);
    tubeMachine.addComposite("output", composite3);

    // Connect composites
    tubeMachine.connect("input", "processor");
    tubeMachine.connect("processor", "output");

    // Add machine state
    tubeMachine.updateState("testKey", "testValue");

    // Convert to component machine
    Machine componentMachine = adapter.tubeMachineToComponentMachine(tubeMachine);

    // Verify conversion
    assertNotNull(componentMachine);
    assertEquals("test-machine", componentMachine.getMachineId());

    // Verify composites were converted
    Map<String, Composite> composites = componentMachine.getComposites();
    assertEquals(3, composites.size());
    assertTrue(composites.containsKey("input"));
    assertTrue(composites.containsKey("processor"));
    assertTrue(composites.containsKey("output"));

    // Verify connections were preserved
    Map<String, List<String>> connections = componentMachine.getConnections();
    assertEquals(2, connections.size());
    assertTrue(connections.containsKey("input"));
    assertTrue(connections.get("input").contains("processor"));
    assertTrue(connections.containsKey("processor"));
    assertTrue(connections.get("processor").contains("output"));

    // Verify state was copied
    assertTrue(componentMachine.getState().containsKey("testKey"));
    assertEquals("testValue", componentMachine.getState().get("testKey"));

    // Verify activation status
    assertTrue(componentMachine.isActive());

    // Test deactivation
    tubeMachine.deactivate();
    componentMachine = adapter.tubeMachineToComponentMachine(tubeMachine);
    assertFalse(componentMachine.isActive());
  }

  @Test
  public void testWrapTubeMachine() {
    // Create a tube machine with composites
    Environment env = new Environment();
    org.s8r.tube.machine.Machine tubeMachine =
        new org.s8r.tube.machine.Machine("wrapped-machine", env);

    // Add composites to the machine
    org.s8r.tube.composite.Composite sourceComposite =
        new org.s8r.tube.composite.Composite("source", env);
    org.s8r.tube.composite.Composite targetComposite =
        new org.s8r.tube.composite.Composite("target", env);

    // Add tubes to composites
    sourceComposite.addTube("source-tube", Tube.create("Source", env));
    targetComposite.addTube("target-tube", Tube.create("Target", env));

    tubeMachine.addComposite("source", sourceComposite);
    tubeMachine.addComposite("target", targetComposite);

    // Connect composites
    tubeMachine.connect("source", "target");

    // Create wrapper
    Machine wrapper = adapter.wrapTubeMachine(tubeMachine);

    // Verify wrapper
    assertNotNull(wrapper);
    assertEquals("wrapped-machine", wrapper.getMachineId());

    // Get composites from wrapper
    Composite source = wrapper.getComposite("source");
    Composite target = wrapper.getComposite("target");

    assertNotNull(source);
    assertNotNull(target);

    // Verify connections
    Map<String, List<String>> connections = wrapper.getConnections();
    assertTrue(connections.containsKey("source"));
    assertTrue(connections.get("source").contains("target"));

    // Test that state changes propagate
    tubeMachine.updateState("wrapperTest", "wrapperValue");
    assertTrue(wrapper.getState().containsKey("wrapperTest"));
    assertEquals("wrapperValue", wrapper.getState().get("wrapperTest"));

    // Test that activation status propagates
    assertTrue(wrapper.isActive());
    tubeMachine.deactivate();
    assertFalse(wrapper.isActive());

    // Test that activation from wrapper propagates to tube machine
    wrapper.activate();
    assertTrue(tubeMachine.isActive());

    // Test adding a composite to the wrapper
    Environment tubeEnv = new Environment();
    org.s8r.tube.composite.Composite newTubeComposite =
        new org.s8r.tube.composite.Composite("new-composite", tubeEnv);
    Composite wrappedComposite = factory.wrapTubeComposite(newTubeComposite);

    wrapper.addComposite("new", wrappedComposite);
    assertTrue(wrapper.getComposites().containsKey("new"));
    assertTrue(tubeMachine.getComposites().containsKey("new"));

    // Test connecting the new composite
    wrapper.connect("source", "new");
    assertTrue(wrapper.getConnections().get("source").contains("new"));
    assertTrue(tubeMachine.getConnections().get("source").contains("new"));
  }

  @Test
  public void testGetComponentsFromWrapper() {
    // Create a tube machine with composites
    Environment env = new Environment();
    org.s8r.tube.machine.Machine tubeMachine =
        new org.s8r.tube.machine.Machine("wrapped-machine", env);

    // Create a wrapper
    Machine wrapper = adapter.wrapTubeMachine(tubeMachine);

    // Add composites to the tube machine after wrapping
    org.s8r.tube.composite.Composite composite =
        new org.s8r.tube.composite.Composite("dynamic", env);
    composite.addTube("tube", Tube.create("Dynamic tube", env));
    tubeMachine.addComposite("dynamic", composite);

    // Get the composite through the wrapper
    Composite retrievedComposite = wrapper.getComposite("dynamic");
    assertNotNull(retrievedComposite);
    assertEquals("dynamic", retrievedComposite.getCompositeId());

    // Verify all composites are retrieved
    Map<String, Composite> allComposites = wrapper.getComposites();
    assertEquals(1, allComposites.size());
    assertTrue(allComposites.containsKey("dynamic"));
  }

  @Test
  public void testShutdownPropagation() {
    // Create tube machine
    Environment env = new Environment();
    org.s8r.tube.machine.Machine tubeMachine =
        new org.s8r.tube.machine.Machine("test-shutdown", env);

    // Create wrapper
    Machine wrapper = adapter.wrapTubeMachine(tubeMachine);

    // Add a state value to track
    tubeMachine.updateState("status", "ACTIVE");

    // Shutdown through wrapper
    wrapper.shutdown();

    // Verify shutdown propagated
    assertFalse(tubeMachine.isActive());
    assertEquals("TERMINATED", tubeMachine.getState().get("status"));
  }
}
