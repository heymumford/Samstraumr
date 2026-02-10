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

package org.s8r.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;
import org.s8r.test.annotation.UnitTest;

/** Unit tests for the NotificationService class. */
@UnitTest
public class NotificationServiceTest {

  private NotificationPort mockNotificationPort;
  private LoggerPort mockLogger;
  private NotificationService service;

  @BeforeEach
  void setUp() {
    mockNotificationPort = mock(NotificationPort.class);
    mockLogger = mock(LoggerPort.class);
    service = new NotificationService(mockNotificationPort, mockLogger);
  }

  @Test
  void testSendNotification() {
    // Setup mock
    when(mockNotificationPort.sendNotification(anyString(), anyString(), anyString()))
        .thenReturn(true);

    // Call the service method
    boolean result = service.sendNotification("test@example.com", "Test Subject", "Test message");

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendNotification("test@example.com", "Test Subject", "Test message");
    verify(mockLogger).debug(anyString(), anyString(), anyString());
  }

  @Test
  void testSendNotificationWithProperties() {
    // Setup mock
    Map<String, String> properties = new HashMap<>();
    properties.put("priority", "high");
    when(mockNotificationPort.sendNotification(anyString(), anyString(), anyString(), anyMap()))
        .thenReturn(true);

    // Call the service method
    boolean result =
        service.sendNotification("test@example.com", "Test Subject", "Test message", properties);

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendNotification("test@example.com", "Test Subject", "Test message", properties);
    verify(mockLogger).debug(anyString(), anyString(), anyString());
  }

  @Test
  void testSendAdvancedNotification() {
    // Setup mock notification result
    NotificationResult expectedResult =
        NotificationResult.success("Notification sent successfully");
    Map<String, String> properties = new HashMap<>();
    properties.put("priority", "high");

    when(mockNotificationPort.sendAdvancedNotification(
            anyString(),
            anyString(),
            anyString(),
            any(NotificationSeverity.class),
            any(NotificationChannel.class),
            any(ContentFormat.class),
            anyMap()))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result =
        service.sendAdvancedNotification(
            "test@example.com",
            "Advanced Subject",
            "Advanced message",
            NotificationSeverity.INFO,
            NotificationChannel.EMAIL,
            ContentFormat.TEXT,
            properties);

    // Verify the result
    assertNotNull(result);
    assertTrue(result.isSuccess());
    assertEquals(DeliveryStatus.DELIVERED, result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendAdvancedNotification(
            eq("test@example.com"),
            eq("Advanced Subject"),
            eq("Advanced message"),
            eq(NotificationSeverity.INFO),
            eq(NotificationChannel.EMAIL),
            eq(ContentFormat.TEXT),
            anyMap());
    verify(mockLogger).debug(anyString(), anyString(), any());
  }

  @Test
  void testGetNotificationStatus() {
    // Setup mock
    when(mockNotificationPort.getNotificationStatus("notif-xyz"))
        .thenReturn(DeliveryStatus.DELIVERED);

    // Call the service method
    DeliveryStatus status = service.getNotificationStatus("notif-xyz");

    // Verify the result
    assertEquals(DeliveryStatus.DELIVERED, status);

    // Verify the mock interactions
    verify(mockNotificationPort).getNotificationStatus("notif-xyz");
  }

  @Test
  void testSendUserNotification_RegisteredUser() {
    // Setup mock
    when(mockNotificationPort.isRecipientRegistered("user123")).thenReturn(true);
    when(mockNotificationPort.sendNotificationToRecipient(
            eq("user123"), anyString(), anyString(), any(NotificationSeverity.class), any()))
        .thenReturn(true);

    // Call the service method
    boolean result =
        service.sendUserNotification(
            "user123", "User Subject", "User message", NotificationSeverity.INFO);

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("user123");
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("user123"),
            eq("User Subject"),
            eq("User message"),
            eq(NotificationSeverity.INFO),
            any());
    verify(mockLogger).debug(anyString(), anyString(), any());
  }

  @Test
  void testSendUserNotification_UnregisteredUser() {
    // Setup mock
    when(mockNotificationPort.isRecipientRegistered("user456")).thenReturn(false);

    // Call the service method
    boolean result =
        service.sendUserNotification(
            "user456", "User Subject", "User message", NotificationSeverity.INFO);

    // Verify the result
    assertFalse(result);

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("user456");
    verify(mockNotificationPort, never())
        .sendNotificationToRecipient(
            anyString(), anyString(), anyString(), any(NotificationSeverity.class), any());
    verify(mockLogger).debug(anyString(), anyString(), any());
    verify(mockLogger).warn(anyString(), anyString());
  }

  @Test
  void testRegisterUserRecipient() {
    // Setup mock
    when(mockNotificationPort.registerRecipient(eq("user789"), any())).thenReturn(true);

    // Call the service method
    boolean result = service.registerUserRecipient("user789", "user789@example.com", "email");

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort).registerRecipient(eq("user789"), any());
    verify(mockLogger).debug(anyString(), anyString(), anyString());
  }

  @Test
  void testUnregisterUserRecipient_RegisteredUser() {
    // Setup mock
    when(mockNotificationPort.isRecipientRegistered("user999")).thenReturn(true);
    when(mockNotificationPort.unregisterRecipient("user999")).thenReturn(true);

    // Call the service method
    boolean result = service.unregisterUserRecipient("user999");

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("user999");
    verify(mockNotificationPort).unregisterRecipient("user999");
    verify(mockLogger).debug(anyString(), anyString());
  }

  @Test
  void testUnregisterUserRecipient_UnregisteredUser() {
    // Setup mock
    when(mockNotificationPort.isRecipientRegistered("user888")).thenReturn(false);

    // Call the service method
    boolean result = service.unregisterUserRecipient("user888");

    // Verify the result
    assertFalse(result);

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("user888");
    verify(mockNotificationPort, never()).unregisterRecipient(anyString());
    verify(mockLogger).debug(anyString(), anyString());
    verify(mockLogger).warn(anyString(), anyString());
  }
}
