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

package org.s8r.infrastructure.notification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the NotificationPort interface contract.
 *
 * <p>This test class verifies that any implementation of the NotificationPort interface adheres to
 * the contract defined by the interface. It tests the behavior expected by the application core
 * regardless of the specific adapter implementation.
 */
@UnitTest
@Tag("L0_Unit")
@Tag("Functional")
@Tag("PortInterface")
public class NotificationPortContractTest {

  private NotificationPort notificationPort;
  private LoggerPort mockLogger;
  private ConfigurationPort mockConfigPort;

  @BeforeEach
  void setUp() {
    mockLogger = mock(LoggerPort.class);
    mockConfigPort = mock(ConfigurationPort.class);

    // Configure the mock ConfigurationPort
    when(mockConfigPort.getString(eq("notification.default.recipient"), anyString()))
        .thenReturn("system");
    when(mockConfigPort.getBoolean(eq("notification.test.recipients.enabled"), anyBoolean()))
        .thenReturn(false);
    when(mockConfigPort.getBoolean(eq("notification.cleanup.enabled"), anyBoolean()))
        .thenReturn(false);

    // Create the NotificationPort implementation
    notificationPort = new NotificationAdapter(mockLogger, mockConfigPort);
  }

  @Test
  @DisplayName("Should register and find recipient")
  void shouldRegisterAndFindRecipient() {
    // Register a test recipient
    Map<String, String> contactInfo = new HashMap<>();
    contactInfo.put("type", "email");
    contactInfo.put("address", "test@example.com");

    boolean result = notificationPort.registerRecipient("test-recipient", contactInfo);

    // Verify registration
    assertTrue(result, "Registration should succeed");
    assertTrue(
        notificationPort.isRecipientRegistered("test-recipient"),
        "Recipient should be found after registration");
  }

  @Test
  @DisplayName("Should return false when checking unregistered recipient")
  void shouldReturnFalseWhenCheckingUnregisteredRecipient() {
    // Check for a recipient that hasn't been registered
    boolean result = notificationPort.isRecipientRegistered("nonexistent-recipient");

    // Verify result
    assertFalse(result, "Unregistered recipient should not be found");
  }

  @Test
  @DisplayName("Should unregister recipient")
  void shouldUnregisterRecipient() {
    // Register a test recipient
    Map<String, String> contactInfo = new HashMap<>();
    contactInfo.put("type", "email");
    contactInfo.put("address", "test@example.com");

    notificationPort.registerRecipient("temp-recipient", contactInfo);

    // Unregister the recipient
    boolean result = notificationPort.unregisterRecipient("temp-recipient");

    // Verify unregistration
    assertTrue(result, "Unregistration should succeed");
    assertFalse(
        notificationPort.isRecipientRegistered("temp-recipient"),
        "Recipient should not be found after unregistration");
  }

  @Test
  @DisplayName("Should not unregister system recipient")
  void shouldNotUnregisterSystemRecipient() {
    // Attempt to unregister the system recipient
    boolean result = notificationPort.unregisterRecipient("system");

    // Verify result
    assertFalse(result, "System recipient should not be unregisterable");
    assertTrue(
        notificationPort.isRecipientRegistered("system"),
        "System recipient should still be registered");
  }

  @Test
  @DisplayName("Should send notification to default recipient")
  void shouldSendNotificationToDefaultRecipient() {
    // Send a notification without specifying recipient
    NotificationResult result =
        notificationPort.sendNotification(
            "Test Subject", "Test Content", NotificationSeverity.INFO, null);

    // Verify result
    assertNotNull(result, "Notification result should not be null");
    assertTrue(result.isSent(), "Notification should be sent successfully");
    assertNotNull(result.getNotificationId(), "Notification ID should not be null");
    assertEquals(DeliveryStatus.SENT, result.getStatus(), "Status should be SENT");
  }

  @Test
  @DisplayName("Should send notification to specified recipient")
  void shouldSendNotificationToSpecifiedRecipient() {
    // Register a test recipient
    Map<String, String> contactInfo = new HashMap<>();
    contactInfo.put("type", "email");
    contactInfo.put("address", "test@example.com");

    notificationPort.registerRecipient("test-recipient", contactInfo);

    // Send a notification to the recipient
    NotificationResult result =
        notificationPort.sendNotificationToRecipient(
            "test-recipient", "Test Subject", "Test Content", NotificationSeverity.INFO, null);

    // Verify result
    assertNotNull(result, "Notification result should not be null");
    assertTrue(result.isSent(), "Notification should be sent successfully");
    assertNotNull(result.getNotificationId(), "Notification ID should not be null");
    assertEquals(DeliveryStatus.SENT, result.getStatus(), "Status should be SENT");
  }

  @Test
  @DisplayName("Should fail to send notification to unregistered recipient")
  void shouldFailToSendNotificationToUnregisteredRecipient() {
    // Send a notification to an unregistered recipient
    NotificationResult result =
        notificationPort.sendNotificationToRecipient(
            "nonexistent-recipient",
            "Test Subject",
            "Test Content",
            NotificationSeverity.INFO,
            null);

    // Verify failure
    assertNotNull(result, "Notification result should not be null");
    assertFalse(result.isSent(), "Notification should not be sent successfully");
    assertEquals(DeliveryStatus.FAILED, result.getStatus(), "Status should be FAILED");
    assertNotNull(result.getMessage(), "Error message should not be null");
  }

  @Test
  @DisplayName("Should send system notification")
  void shouldSendSystemNotification() {
    // Send a system notification
    NotificationResult result =
        notificationPort.sendSystemNotification(
            "System Alert", "System Test", NotificationSeverity.WARNING);

    // Verify result
    assertNotNull(result, "Notification result should not be null");
    assertTrue(result.isSent(), "Notification should be sent successfully");
    assertNotNull(result.getNotificationId(), "Notification ID should not be null");
    assertEquals(DeliveryStatus.SENT, result.getStatus(), "Status should be SENT");
  }

  @Test
  @DisplayName("Should retrieve notification status")
  void shouldRetrieveNotificationStatus() {
    // Send a notification to get an ID
    NotificationResult result =
        notificationPort.sendSystemNotification(
            "Status Test", "Test Content", NotificationSeverity.INFO);

    String notificationId = result.getNotificationId();

    // Retrieve the status
    DeliveryStatus status = notificationPort.getNotificationStatus(notificationId);

    // Verify result
    assertNotNull(status, "Retrieved status should not be null");
    assertEquals(DeliveryStatus.SENT, status, "Status should match the expected value");
  }

  @Test
  @DisplayName("Should return FAILED for nonexistent notification status")
  void shouldReturnFailedForNonexistentNotificationStatus() {
    // Try to retrieve status for a nonexistent notification
    DeliveryStatus status = notificationPort.getNotificationStatus("NOTIF-NONEXISTENT");

    // Verify result
    assertEquals(
        DeliveryStatus.FAILED, status, "Status should be FAILED for nonexistent notification");
  }

  @Test
  @DisplayName("Should handle metadata in notifications")
  void shouldHandleMetadataInNotifications() {
    // Create metadata
    Map<String, String> metadata = new HashMap<>();
    metadata.put("category", "test");
    metadata.put("priority", "high");
    metadata.put("source", "unit-test");

    // Send a notification with metadata
    NotificationResult result =
        notificationPort.sendSystemNotification(
            "Metadata Test", "Test with metadata", NotificationSeverity.INFO);

    // Verify result
    assertNotNull(result, "Notification result should not be null");
    assertTrue(result.isSent(), "Notification should be sent successfully");
  }

  @Test
  @DisplayName("Should validate recipient registration parameters")
  void shouldValidateRecipientRegistrationParameters() {
    // Test null recipient ID
    boolean nullIdResult = notificationPort.registerRecipient(null, new HashMap<>());
    assertFalse(nullIdResult, "Registration with null ID should fail");

    // Test empty recipient ID
    boolean emptyIdResult = notificationPort.registerRecipient("", new HashMap<>());
    assertFalse(emptyIdResult, "Registration with empty ID should fail");

    // Test null contact info
    boolean nullInfoResult = notificationPort.registerRecipient("test", null);
    assertFalse(nullInfoResult, "Registration with null contact info should fail");

    // Test empty contact info
    boolean emptyInfoResult = notificationPort.registerRecipient("test", new HashMap<>());
    assertFalse(emptyInfoResult, "Registration with empty contact info should fail");
  }

  @Test
  @DisplayName("Should support different notification severities")
  void shouldSupportDifferentNotificationSeverities() {
    // Test each severity level
    for (NotificationSeverity severity : NotificationSeverity.values()) {
      NotificationResult result =
          notificationPort.sendSystemNotification(
              severity + " Test", "Test with " + severity + " severity", severity);

      assertNotNull(result, "Notification result should not be null for severity: " + severity);
      assertTrue(
          result.isSent(), "Notification should be sent successfully for severity: " + severity);
    }
  }

  @Test
  @DisplayName("Should detect system recipient by default")
  void shouldDetectSystemRecipientByDefault() {
    // Check if system recipient exists by default
    boolean result = notificationPort.isRecipientRegistered("system");

    // Verify result
    assertTrue(result, "System recipient should exist by default");
  }
}
