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

package org.s8r.adapter.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.adapter.MachineFactoryAdapter;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

/**
 * Contract tests for the MachinePort interface.
 *
 * <p>This test class verifies that any implementation of the MachinePort interface adheres to the
 * contract defined by the interface. It tests the behavior expected by the application core
 * regardless of the specific adapter implementation.
 *
 * <p>The tests cover core functionality such as state transitions, component management, and
 * composite connections.
 */
public class MachinePortContractTest extends PortContractTest<MachinePort> {

  private MachineFactoryAdapter machineFactoryAdapter;
  private CompositeComponentPort mockComposite1;
  private CompositeComponentPort mockComposite2;

  @Override
  protected MachinePort createPortImplementation() {
    machineFactoryAdapter = new MachineFactoryAdapter(logger);
    return machineFactoryAdapter.createMachine(MachineType.DATA_PROCESSOR, "test-machine");
  }

  @Override
  protected void verifyNullInputHandling() {
    // This is tested in nullInputHandlingTests()
  }

  @Override
  protected void verifyRequiredMethods() {
    // This is tested across multiple method-specific tests
  }

  /** Verifies that the MachinePort implementation handles null inputs correctly. */
  @Test
  @DisplayName("Should handle null inputs gracefully")
  public void nullInputHandlingTests() {
    // Test null component name in addComponent
    assertFalse(
        portUnderTest.addComposite(null, mock(CompositeComponentPort.class)),
        "Adding composite with null name should return false");

    // Test null component in addComponent
    assertFalse(
        portUnderTest.addComposite("test-component", null),
        "Adding null composite should return false");

    // Test null name in removeComponent
    assertTrue(
        portUnderTest.removeComposite(null).isEmpty(),
        "Removing composite with null name should return empty Optional");

    // Test null name in getComponent
    assertNull(
        portUnderTest.getComposite(null), "Getting composite with null name should return null");

    // Test null source/target names in connect
    assertFalse(
        portUnderTest.connectComposites(null, "target"),
        "Connecting with null source should return false");
    assertFalse(
        portUnderTest.connectComposites("source", null),
        "Connecting with null target should return false");
  }

  /** Tests the core functionality of MachinePort implementations. */
  @Test
  @DisplayName("Should provide basic machine identity information")
  public void basicMachineIdentityTests() {
    assertNotNull(portUnderTest.getMachineId(), "Machine ID should not be null");
    assertEquals("test-machine", portUnderTest.getName(), "Machine name should match");
    assertEquals(
        MachineType.DATA_PROCESSOR, portUnderTest.getMachineType(), "Machine type should match");
    assertEquals(
        MachineState.STOPPED, portUnderTest.getMachineState(), "Initial state should be STOPPED");
  }

  /** Tests the state transition functionality of MachinePort implementations. */
  @Test
  @DisplayName("Should handle state transitions correctly")
  public void stateTransitionTests() {
    // Initial state should be STOPPED
    assertEquals(
        MachineState.STOPPED, portUnderTest.getMachineState(), "Initial state should be STOPPED");

    // Start the machine
    assertTrue(portUnderTest.start(), "Machine should start successfully");
    assertEquals(
        MachineState.RUNNING,
        portUnderTest.getMachineState(),
        "State should be RUNNING after start");
    assertTrue(portUnderTest.isActive(), "Machine should be active after start");

    // Stop the machine
    assertTrue(portUnderTest.stop(), "Machine should stop successfully");
    assertEquals(
        MachineState.STOPPED,
        portUnderTest.getMachineState(),
        "State should be STOPPED after stop");
    assertFalse(portUnderTest.isActive(), "Machine should be inactive after stop");

    // Explicit state setting
    portUnderTest.setMachineState(MachineState.ERROR);
    assertEquals(
        MachineState.ERROR,
        portUnderTest.getMachineState(),
        "State should match after explicit set");
  }

  /** Tests the composite management functionality of MachinePort implementations. */
  @Test
  @DisplayName("Should manage composites correctly")
  public void compositeManagementTests() {
    // Create mock composites
    mockComposite1 = mock(CompositeComponentPort.class);
    mockComposite2 = mock(CompositeComponentPort.class);

    // Add composites
    assertTrue(
        portUnderTest.addComposite("comp1", mockComposite1), "Adding composite should succeed");
    assertTrue(
        portUnderTest.addComposite("comp2", mockComposite2),
        "Adding second composite should succeed");

    // Get composite by name
    assertEquals(
        mockComposite1,
        portUnderTest.getComposite("comp1"),
        "Retrieved composite should match added composite");

    // Get all composites
    Map<String, CompositeComponentPort> composites = portUnderTest.getComposites();
    assertEquals(2, composites.size(), "Should have 2 composites");
    assertTrue(composites.containsKey("comp1"), "Should contain comp1");
    assertTrue(composites.containsKey("comp2"), "Should contain comp2");

    // Remove composite
    Optional<CompositeComponentPort> removed = portUnderTest.removeComposite("comp1");
    assertTrue(removed.isPresent(), "Removed composite should be present");
    assertEquals(mockComposite1, removed.get(), "Removed composite should match original");

    // Verify removal
    composites = portUnderTest.getComposites();
    assertEquals(1, composites.size(), "Should have 1 composite after removal");
    assertFalse(composites.containsKey("comp1"), "Should not contain removed composite");
  }

  /** Tests the composite connection functionality of MachinePort implementations. */
  @Test
  @DisplayName("Should manage composite connections correctly")
  public void compositeConnectionTests() {
    // Create mock composites
    mockComposite1 = mock(CompositeComponentPort.class);
    mockComposite2 = mock(CompositeComponentPort.class);

    // Add composites
    portUnderTest.addComposite("comp1", mockComposite1);
    portUnderTest.addComposite("comp2", mockComposite2);

    // Connect composites
    assertTrue(
        portUnderTest.connectComposites("comp1", "comp2"), "Connecting composites should succeed");

    // Verify connections
    Map<String, List<String>> connections = portUnderTest.getCompositeConnections();
    assertTrue(connections.containsKey("comp1"), "Connections should contain source");
    assertTrue(
        connections.get("comp1").contains("comp2"), "Connection should link source to target");

    // Connection with non-existent composite should fail
    assertFalse(
        portUnderTest.connectComposites("comp1", "nonexistent"),
        "Connecting to non-existent composite should fail");
    assertFalse(
        portUnderTest.connectComposites("nonexistent", "comp2"),
        "Connecting from non-existent composite should fail");
  }

  /**
   * Creates a mock implementation of the specified interface.
   *
   * @param <T> the interface type
   * @param interfaceClass the class object of the interface
   * @return a mock implementation
   */
  @SuppressWarnings("unchecked")
  private <T> T mock(Class<T> interfaceClass) {
    // This is a simple mock implementation
    if (interfaceClass == CompositeComponentPort.class) {
      return (T) new MockCompositeComponentPort();
    } else if (interfaceClass == ComponentPort.class) {
      return (T) new MockComponentPort();
    }
    return null;
  }

  /** Simple mock implementation of CompositeComponentPort for testing. */
  private class MockCompositeComponentPort implements CompositeComponentPort {
    // Implement required methods with minimal functionality
    // Add more implementation as needed for specific tests

    @Override
    public String getCompositeId() {
      return "mock-composite-id";
    }

    // Implement other methods...

    // These methods are just stubs for the mock - not important for the test
    @Override
    public ComponentId getId() {
      return null;
    }

    @Override
    public List<String> getLineage() {
      return null;
    }

    @Override
    public List<String> getActivityLog() {
      return null;
    }

    @Override
    public java.time.Instant getCreationTime() {
      return null;
    }

    @Override
    public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
      return null;
    }

    @Override
    public void addToLineage(String entry) {}

    @Override
    public void clearEvents() {}

    @Override
    public void publishData(String channel, Map<String, Object> data) {}

    @Override
    public void publishData(String channel, String key, Object value) {}

    @Override
    public void transitionTo(org.s8r.domain.lifecycle.LifecycleState newState) {}

    @Override
    public void activate() {}

    @Override
    public void deactivate() {}

    @Override
    public void terminate() {}

    @Override
    public org.s8r.domain.lifecycle.LifecycleState getLifecycleState() {
      return null;
    }

    @Override
    public boolean addComponent(String name, ComponentPort component) {
      return false;
    }

    @Override
    public Optional<ComponentPort> removeComponent(String name) {
      return Optional.empty();
    }

    @Override
    public ComponentPort getComponent(String name) {
      return null;
    }

    @Override
    public boolean hasComponent(String name) {
      return false;
    }

    @Override
    public Map<String, ComponentPort> getComponents() {
      return new HashMap<>();
    }

    @Override
    public boolean connect(String sourceName, String targetName) {
      return false;
    }

    @Override
    public boolean disconnect(String sourceName, String targetName) {
      return false;
    }

    @Override
    public Map<String, List<String>> getConnections() {
      return new HashMap<>();
    }

    @Override
    public List<String> getConnectionsFrom(String sourceName) {
      return null;
    }
  }

  /** Simple mock implementation of ComponentPort for testing. */
  private class MockComponentPort implements ComponentPort {
    // Implement required methods with minimal functionality
    // Add more implementation as needed for specific tests

    // These methods are just stubs for the mock - not important for the test
    @Override
    public ComponentId getId() {
      return null;
    }

    @Override
    public List<String> getLineage() {
      return null;
    }

    @Override
    public List<String> getActivityLog() {
      return null;
    }

    @Override
    public java.time.Instant getCreationTime() {
      return null;
    }

    @Override
    public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
      return null;
    }

    @Override
    public void addToLineage(String entry) {}

    @Override
    public void clearEvents() {}

    @Override
    public void publishData(String channel, Map<String, Object> data) {}

    @Override
    public void publishData(String channel, String key, Object value) {}

    @Override
    public void transitionTo(org.s8r.domain.lifecycle.LifecycleState newState) {}

    @Override
    public void activate() {}

    @Override
    public void deactivate() {}

    @Override
    public void terminate() {}

    @Override
    public org.s8r.domain.lifecycle.LifecycleState getLifecycleState() {
      return null;
    }
  }
}
