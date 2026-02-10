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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineType;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the MachineFactoryAdapter class.
 *
 * <p>These tests verify that the adapter correctly implements the MachineFactoryPort interface and
 * creates properly configured MachinePort instances.
 */
@UnitTest
public class MachineFactoryAdapterTest {

  private LoggerPort mockLogger;
  private MachineFactoryAdapter adapter;

  @BeforeEach
  void setUp() {
    mockLogger = mock(LoggerPort.class);
    adapter = new MachineFactoryAdapter(mockLogger);
  }

  @Test
  @DisplayName("createMachine should create a machine with type and reason")
  void createMachine_shouldCreateWithTypeAndReason() {
    // Arrange
    MachineType type = MachineType.DATA_PROCESSOR;
    String reason = "Test machine creation";

    // Act
    MachinePort machine = adapter.createMachine(type, reason);

    // Assert
    assertNotNull(machine);
    assertEquals(type, machine.getMachineType());
    verify(mockLogger).debug(anyString(), eq(type), eq(reason));
  }

  @Test
  @DisplayName("createMachine should create a machine with ID and type")
  void createMachine_shouldCreateWithIdAndType() {
    // Arrange
    ComponentId id = mock(ComponentId.class);
    when(id.getShortId()).thenReturn("test-id");
    MachineType type = MachineType.INTEGRATION;

    // Act
    MachinePort machine = adapter.createMachine(id, type);

    // Assert
    assertNotNull(machine);
    assertEquals(type, machine.getMachineType());
    verify(mockLogger).debug(anyString(), eq("test-id"), eq(type));
  }

  @Test
  @DisplayName("createMachine should create a machine with configuration")
  void createMachine_shouldCreateWithConfiguration() {
    // Arrange
    MachineType type = MachineType.WORKFLOW;
    String reason = "Configured machine";
    Map<String, Object> config = new HashMap<>();
    config.put("maxComponents", 10);
    config.put("description", "Test machine");

    // Act
    MachinePort machine = adapter.createMachine(type, reason, config);

    // Assert
    assertNotNull(machine);
    assertEquals(type, machine.getMachineType());

    // Verify logger called for both configuration properties
    verify(mockLogger, times(2)).debug(anyString(), anyString(), anyString());
  }

  @Test
  @DisplayName("cloneMachine should create a new machine based on source")
  void cloneMachine_shouldCreateClone() {
    // Arrange
    MachinePort sourceMachine = mock(MachinePort.class);
    ComponentId sourceId = mock(ComponentId.class);
    when(sourceId.getShortId()).thenReturn("source-id");
    when(sourceMachine.getId()).thenReturn(sourceId);
    when(sourceMachine.getMachineType()).thenReturn(MachineType.MONITORING);

    ComponentId newId = mock(ComponentId.class);
    when(newId.getShortId()).thenReturn("cloned-id");

    // Act
    MachinePort clonedMachine = adapter.cloneMachine(sourceMachine, newId);

    // Assert
    assertNotNull(clonedMachine);
    verify(mockLogger).debug(anyString(), eq("source-id"), eq("cloned-id"));
    verify(mockLogger).debug(eq("Machine cloned successfully"));
  }
}
