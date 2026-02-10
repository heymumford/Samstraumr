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

package org.s8r.adapter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the Clean Architecture MachineAdapter and MachineFactoryAdapter classes.
 *
 * <p>These tests verify that the adapters properly implement the port interfaces and perform the
 * necessary type conversions between domain and component layers.
 */
@UnitTest
public class CleanArchitectureMachineAdapterTest {

  private ConsoleLogger logger;
  private org.s8r.domain.machine.Machine domainMachine;
  private MachineFactoryAdapter machineFactoryAdapter;

  @BeforeEach
  public void setup() {
    logger = new ConsoleLogger(this.getClass());

    // Create a domain machine using correct constructor via factory
    domainMachine =
        org.s8r.domain.machine.MachineFactory.createMachine(
            MachineType.DATA_PROCESSOR,
            "test-domain-machine",
            "Test domain machine description",
            "1.0.0");

    // Create the factory adapter
    machineFactoryAdapter = new MachineFactoryAdapter(logger);
  }

  @Test
  @DisplayName("Domain machine should be properly adapted to MachinePort")
  public void testDomainMachineAdapter() {
    // Create adapter from domain machine
    MachinePort machinePort = MachineAdapter.createMachinePortFromDomain(domainMachine);

    // Verify adapter properly represents domain machine
    assertNotNull(machinePort, "Adapter should not be null");
    assertEquals(
        domainMachine.getId().getIdString(), machinePort.getId().getIdString(), "IDs should match");
    assertEquals(
        domainMachine.getState(), machinePort.getMachineState(), "Machine states should match");
    assertEquals(
        domainMachine.getType().toString(),
        machinePort.getMachineType().toString(),
        "Machine types should match");
  }

  @Test
  @DisplayName("MachineFactoryAdapter should create valid machine ports")
  public void testMachineFactoryAdapter() {
    // Create a machine using the factory adapter - use the interface method first
    MachinePort machinePort =
        machineFactoryAdapter.createMachine(MachineType.DATA_PROCESSOR, "factory-test");

    // Verify the machine was created properly
    assertNotNull(machinePort, "Created machine should not be null");
    assertNotNull(machinePort.getId(), "Machine ID should not be null");
    assertEquals("factory-test", machinePort.getName(), "Machine name should match");
    assertEquals(
        MachineState.STOPPED,
        machinePort.getMachineState(),
        "Initial machine state should be STOPPED");

    // Test the convenience method as well
    MachinePort machinePort2 =
        machineFactoryAdapter.createMachine("factory-test-2", "DATA_PROCESSOR");

    // Verify the machine was created properly
    assertNotNull(machinePort2, "Created machine should not be null");
    assertNotNull(machinePort2.getId(), "Machine ID should not be null");
    assertEquals("factory-test-2", machinePort2.getName(), "Machine name should match");
    assertEquals(
        MachineState.STOPPED,
        machinePort2.getMachineState(),
        "Initial machine state should be STOPPED");
  }

  @Test
  @DisplayName("MachinePort state changes should work properly")
  public void testMachinePortStateChanges() {
    // Create a machine using the factory adapter
    MachinePort machinePort =
        machineFactoryAdapter.createMachine(MachineType.DATA_PROCESSOR, "state-test");

    // Initial state
    assertEquals(
        MachineState.STOPPED, machinePort.getMachineState(), "Initial state should be STOPPED");

    // Start the machine
    assertTrue(machinePort.start(), "Machine should start successfully");
    assertEquals(
        MachineState.RUNNING, machinePort.getMachineState(), "State should be RUNNING after start");

    // Stop the machine
    assertTrue(machinePort.stop(), "Machine should stop successfully");
    assertEquals(
        MachineState.STOPPED, machinePort.getMachineState(), "State should be STOPPED after stop");
  }

  @Test
  @DisplayName("MachinePort should handle null inputs gracefully")
  public void testNullHandling() {
    // Test null domain machine
    MachinePort nullDomainPort = MachineAdapter.createMachinePortFromDomain(null);
    assertNull(nullDomainPort, "Adapter should return null for null domain machine");
  }

  @Test
  @DisplayName("MachinePort should implement ComponentPort interface")
  public void testComponentPortImplementation() {
    // Create adapter from domain machine
    MachinePort machinePort = MachineAdapter.createMachinePortFromDomain(domainMachine);

    // Verify it properly implements ComponentPort
    ComponentPort componentPort =
        machinePort; // Should compile since MachinePort extends ComponentPort

    assertNotNull(componentPort, "Component port should not be null");
    assertEquals(
        domainMachine.getId().getIdString(),
        componentPort.getId().getIdString(),
        "Component IDs should match");
    assertNotNull(componentPort.getCreationTime(), "Creation time should not be null");
    assertNotNull(componentPort.getLifecycleState(), "Lifecycle state should not be null");
  }

  @Test
  @DisplayName("MachinePort should provide default implementations for interface methods")
  public void testDefaultImplementations() {
    // Create adapter from domain machine
    MachinePort machinePort =
        machineFactoryAdapter.createMachine(MachineType.DATA_PROCESSOR, "defaults-test");

    // Test default implementations
    assertFalse(machinePort.isActive(), "New machine should not be active by default");
    assertEquals("defaults-test", machinePort.getName(), "Name default implementation should work");
    assertEquals("Machine", machinePort.getType(), "Type default implementation should work");
    assertEquals(
        LifecycleState.INITIALIZED.name(),
        machinePort.getState(),
        "State default implementation should work");
  }
}
