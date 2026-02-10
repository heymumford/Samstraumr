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

package org.s8r.domain.component.composite;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the ComponentConnection domain value object.
 *
 * <p>These tests validate the behavior of ComponentConnection as a value object, including
 * construction, property access, activation/deactivation, and equality/hashCode.
 */
@UnitTest
@DisplayName("ComponentConnection Domain Value Object Tests")
class ComponentConnectionTest {

  private final ComponentId sourceId = ComponentId.create("Source component for test");
  private final ComponentId targetId = ComponentId.create("Target component for test");

  @Nested
  @DisplayName("Constructor and Property Tests")
  class ConstructorTests {

    @Test
    @DisplayName("constructor should create a valid ComponentConnection with all properties set")
    void constructorShouldCreateValidComponentConnection() {
      // When
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, ConnectionType.DATA_FLOW, "Test connection");

      // Then
      assertNotNull(connection.getConnectionId(), "Connection ID should be generated");
      assertEquals(sourceId, connection.getSourceId(), "Source ID should match");
      assertEquals(targetId, connection.getTargetId(), "Target ID should match");
      assertEquals(ConnectionType.DATA_FLOW, connection.getType(), "Connection type should match");
      assertEquals("Test connection", connection.getDescription(), "Description should match");
      assertTrue(connection.isActive(), "Connection should be active by default");
      assertNotNull(connection.getCreationTime(), "Creation time should be set");

      // Creation time should be recent (within last 5 seconds)
      Instant now = Instant.now();
      assertTrue(
          connection.getCreationTime().isAfter(now.minus(5, ChronoUnit.SECONDS)),
          "Creation time should be recent");
    }

    @Test
    @DisplayName("constructor should handle null description by using empty string")
    void constructorShouldHandleNullDescription() {
      // When
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, ConnectionType.DATA_FLOW, null);

      // Then
      assertEquals("", connection.getDescription(), "Description should default to empty string");
    }

    @Test
    @DisplayName("constructor should throw NullPointerException for null sourceId")
    void constructorShouldThrowExceptionForNullSourceId() {
      // Then
      assertThrows(
          NullPointerException.class,
          () -> new ComponentConnection(null, targetId, ConnectionType.DATA_FLOW, "Test"),
          "Should throw NullPointerException for null sourceId");
    }

    @Test
    @DisplayName("constructor should throw NullPointerException for null targetId")
    void constructorShouldThrowExceptionForNullTargetId() {
      // Then
      assertThrows(
          NullPointerException.class,
          () -> new ComponentConnection(sourceId, null, ConnectionType.DATA_FLOW, "Test"),
          "Should throw NullPointerException for null targetId");
    }

    @Test
    @DisplayName("constructor should throw NullPointerException for null connectionType")
    void constructorShouldThrowExceptionForNullConnectionType() {
      // Then
      assertThrows(
          NullPointerException.class,
          () -> new ComponentConnection(sourceId, targetId, null, "Test"),
          "Should throw NullPointerException for null connectionType");
    }
  }

  @Nested
  @DisplayName("Connection Type Support Tests")
  class ConnectionTypeTests {

    @ParameterizedTest
    @EnumSource(ConnectionType.class)
    @DisplayName("constructor should accept all ConnectionType values")
    void shouldAcceptAllConnectionTypes(ConnectionType type) {
      // When
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, type, "Test with " + type);

      // Then
      assertEquals(type, connection.getType(), "Connection type should match");
    }
  }

  @Nested
  @DisplayName("Activation State Tests")
  class ActivationTests {

    @Test
    @DisplayName("connection should be active by default")
    void connectionShouldBeActiveByDefault() {
      // When
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, ConnectionType.CONTROL, "Test connection");

      // Then
      assertTrue(connection.isActive(), "Connection should be active by default");
    }

    @Test
    @DisplayName("deactivate() should set active state to false")
    void deactivateShouldSetActiveToFalse() {
      // Given
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, ConnectionType.EVENT, "Test connection");

      // When
      connection.deactivate();

      // Then
      assertFalse(connection.isActive(), "Connection should be inactive after deactivate()");
    }

    @Test
    @DisplayName("activate() should set active state to true")
    void activateShouldSetActiveToTrue() {
      // Given
      ComponentConnection connection =
          new ComponentConnection(
              sourceId, targetId, ConnectionType.TRANSFORMATION, "Test connection");
      connection.deactivate();

      // When
      connection.activate();

      // Then
      assertTrue(connection.isActive(), "Connection should be active after activate()");
    }

    @Test
    @DisplayName("multiple activation/deactivation should work correctly")
    void multipleActivationDeactivationShouldWorkCorrectly() {
      // Given
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, ConnectionType.MONITORING, "Test connection");

      // When/Then - sequence of activations and deactivations
      assertTrue(connection.isActive(), "Initially active");

      connection.deactivate();
      assertFalse(connection.isActive(), "Inactive after first deactivate");

      connection.activate();
      assertTrue(connection.isActive(), "Active after activate");

      connection.deactivate();
      assertFalse(connection.isActive(), "Inactive after second deactivate");
    }
  }

  @Nested
  @DisplayName("Value Object Behavior Tests")
  class ValueObjectTests {

    @Test
    @DisplayName("equals() should compare based on connectionId only")
    void equalsShouldCompareByConnectionId() throws Exception {
      // Given
      ComponentConnection connection1 =
          new ComponentConnection(sourceId, targetId, ConnectionType.DATA_FLOW, "First connection");

      // Create a copy with same connectionId using reflection (testing purposes only)
      ComponentConnection connection2 =
          new ComponentConnection(
              ComponentId.create("Different source"),
              ComponentId.create("Different target"),
              ConnectionType.CONTROL,
              "Different description");

      // Get private field and set it
      java.lang.reflect.Field idField = ComponentConnection.class.getDeclaredField("connectionId");
      idField.setAccessible(true);
      idField.set(connection2, connection1.getConnectionId());

      // Different connection with different ID
      ComponentConnection connection3 =
          new ComponentConnection(sourceId, targetId, ConnectionType.DATA_FLOW, "First connection");

      // Then
      assertEquals(connection1, connection1, "Connection should equal itself");
      assertEquals(connection1, connection2, "Connections with same ID should be equal");
      assertNotEquals(
          connection1, connection3, "Connections with different IDs should not be equal");
      assertNotEquals(connection1, null, "Connection should not equal null");
      assertNotEquals(connection1, "not a connection", "Connection should not equal other types");
    }

    @Test
    @DisplayName("hashCode() should be based on connectionId only")
    void hashCodeShouldBeBasedOnConnectionId() throws Exception {
      // Given
      ComponentConnection connection1 =
          new ComponentConnection(sourceId, targetId, ConnectionType.DATA_FLOW, "First connection");

      // Create a copy with same connectionId using reflection (testing purposes only)
      ComponentConnection connection2 =
          new ComponentConnection(
              ComponentId.create("Different source"),
              ComponentId.create("Different target"),
              ConnectionType.CONTROL,
              "Different description");

      // Get private field and set it
      java.lang.reflect.Field idField = ComponentConnection.class.getDeclaredField("connectionId");
      idField.setAccessible(true);
      idField.set(connection2, connection1.getConnectionId());

      // Different connection with different ID
      ComponentConnection connection3 =
          new ComponentConnection(sourceId, targetId, ConnectionType.DATA_FLOW, "First connection");

      // Then
      assertEquals(
          connection1.hashCode(), connection2.hashCode(), "Hash codes should be equal for same ID");
      assertNotEquals(
          connection1.hashCode(),
          connection3.hashCode(),
          "Hash codes should differ for different IDs");
    }

    @Test
    @DisplayName("toString() should include key fields")
    void toStringShouldIncludeKeyFields() {
      // Given
      ComponentConnection connection =
          new ComponentConnection(sourceId, targetId, ConnectionType.VALIDATION, "Test connection");

      // When
      String toString = connection.toString();

      // Then
      assertTrue(
          toString.contains(connection.getConnectionId()), "toString should include connectionId");
      assertTrue(toString.contains(sourceId.toString()), "toString should include sourceId");
      assertTrue(toString.contains(targetId.toString()), "toString should include targetId");
      assertTrue(
          toString.contains(ConnectionType.VALIDATION.toString()), "toString should include type");
      assertTrue(
          toString.contains(String.valueOf(connection.isActive())),
          "toString should include active state");
    }
  }
}
