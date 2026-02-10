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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.MachineType;
import org.s8r.test.annotation.UnitTest;

/** Unit tests for the NotificationService class. */
@UnitTest
public class NotificationServiceTest {

  private NotificationPort mockNotificationPort;
  private LoggerPort mockLogger;
  private NotificationService service;
  private ComponentPort mockComponent;

  @BeforeEach
  void setUp() {
    mockNotificationPort = mock(NotificationPort.class);
    mockLogger = mock(LoggerPort.class);
    mockComponent = mock(ComponentPort.class);

    service = new NotificationService(mockNotificationPort, mockLogger);

    // Setup mock component
    when(mockComponent.getId()).thenReturn(ComponentId.create("test-component-123"));
    when(mockComponent.getName()).thenReturn("Test Component");
    when(mockComponent.getType()).thenReturn("TestComponent");
    when(mockComponent.getLifecycleState()).thenReturn(LifecycleState.ACTIVE);
  }

  @Test
  void testSendComponentStatusNotification() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-123", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.sendSystemNotification(
            anyString(), anyString(), eq(NotificationSeverity.INFO)))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result =
        service.sendComponentStatusNotification(mockComponent, "Status update");

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendSystemNotification(
            contains("Component Status:"),
            contains("Status update"),
            eq(NotificationSeverity.INFO));
    verify(mockLogger).debug(anyString(), anyString());
  }

  @Test
  void testSendComponentErrorNotification() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-456", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.sendSystemNotification(
            anyString(), anyString(), eq(NotificationSeverity.ERROR)))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result =
        service.sendComponentErrorNotification(mockComponent, "Test error message");

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendSystemNotification(
            contains("Component Error:"),
            contains("Test error message"),
            eq(NotificationSeverity.ERROR));
    verify(mockLogger).debug(anyString(), anyString());
  }

  @Test
  void testSendMachineCreationNotification() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-789", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.sendSystemNotification(
            anyString(), anyString(), eq(NotificationSeverity.INFO)))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result =
        service.sendMachineCreationNotification(
            "machine-123", MachineType.DATA_PROCESSOR, "Test Machine");

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendSystemNotification(
            contains("Machine Created:"), contains("Test Machine"), eq(NotificationSeverity.INFO));
    verify(mockLogger).debug(anyString(), anyString());
  }

  @Test
  void testSendSystemWarning() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-abc", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.sendSystemNotification(
            anyString(), anyString(), eq(NotificationSeverity.WARNING)))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result = service.sendSystemWarning("Warning Subject", "Warning message");

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendSystemNotification(
            eq("Warning Subject"), eq("Warning message"), eq(NotificationSeverity.WARNING));
    verify(mockLogger).debug(anyString());
  }

  @Test
  void testSendSystemError() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-def", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.sendSystemNotification(
            anyString(), anyString(), eq(NotificationSeverity.ERROR)))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result = service.sendSystemError("Error Subject", "Error message");

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendSystemNotification(
            eq("Error Subject"), eq("Error message"), eq(NotificationSeverity.ERROR));
    verify(mockLogger).debug(anyString());
  }

  @Test
  void testSendUserNotification_RegisteredUser() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-ghi", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.isRecipientRegistered("user123")).thenReturn(true);
    when(mockNotificationPort.sendNotificationToRecipient(
            eq("user123"), anyString(), anyString(), any(NotificationSeverity.class), anyMap()))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result =
        service.sendUserNotification(
            "user123", "User Subject", "User message", NotificationSeverity.INFO);

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("user123");
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("user123"),
            eq("User Subject"),
            eq("User message"),
            eq(NotificationSeverity.INFO),
            argThat(map -> map.containsKey("userId") && map.get("userId").equals("user123")));
    verify(mockLogger).debug(anyString(), anyString());
    verify(mockNotificationPort, never()).registerRecipient(anyString(), anyMap());
  }

  @Test
  void testSendUserNotification_UnregisteredUser() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-jkl", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.isRecipientRegistered("user456")).thenReturn(false);
    when(mockNotificationPort.registerRecipient(eq("user456"), anyMap())).thenReturn(true);
    when(mockNotificationPort.sendNotificationToRecipient(
            eq("user456"), anyString(), anyString(), any(NotificationSeverity.class), anyMap()))
        .thenReturn(expectedResult);

    // Call the service method
    NotificationResult result =
        service.sendUserNotification(
            "user456", "User Subject", "User message", NotificationSeverity.INFO);

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("user456");
    verify(mockNotificationPort)
        .registerRecipient(
            eq("user456"),
            argThat(map -> map.containsKey("type") && map.get("type").equals("email")));
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("user456"),
            eq("User Subject"),
            eq("User message"),
            eq(NotificationSeverity.INFO),
            argThat(map -> map.containsKey("userId") && map.get("userId").equals("user456")));
    verify(mockLogger).debug(anyString(), anyString());
    verify(mockLogger).warn(anyString(), anyString());
  }

  @Test
  void testRegisterUserRecipient() {
    // Setup mock
    when(mockNotificationPort.registerRecipient(
            eq("user789"),
            argThat(map -> map.containsKey("type") && map.get("type").equals("email"))))
        .thenReturn(true);

    // Call the service method
    boolean result = service.registerUserRecipient("user789", "email", "user789@example.com");

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort)
        .registerRecipient(
            eq("user789"),
            argThat(
                map ->
                    map.containsKey("type")
                        && map.get("type").equals("email")
                        && map.containsKey("address")
                        && map.get("address").equals("user789@example.com")));
    verify(mockLogger).debug(anyString(), anyString());
  }

  @Test
  void testUnregisterUserRecipient() {
    // Setup mock
    when(mockNotificationPort.unregisterRecipient("user999")).thenReturn(true);

    // Call the service method
    boolean result = service.unregisterUserRecipient("user999");

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort).unregisterRecipient("user999");
    verify(mockLogger).debug(anyString(), anyString());
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
  void testSendPushNotification_RegisteredUser() {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-push", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.isRecipientRegistered("push-user")).thenReturn(true);
    when(mockNotificationPort.sendNotificationToRecipient(
            eq("push-user"),
            anyString(),
            anyString(),
            any(NotificationSeverity.class),
            argThat(map -> map.containsKey("channel") && map.get("channel").equals("push"))))
        .thenReturn(expectedResult);

    // Prepare test data
    Map<String, String> data = new HashMap<>();
    data.put("action", "open_profile");
    data.put("profileId", "12345");

    // Call the service method
    NotificationResult result =
        service.sendPushNotification(
            "push-user", "Push Title", "Push content", NotificationSeverity.INFO, data);

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("push-user");
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("push-user"),
            eq("Push Title"),
            eq("Push content"),
            eq(NotificationSeverity.INFO),
            argThat(
                map ->
                    map.containsKey("channel")
                        && map.get("channel").equals("push")
                        && map.containsKey("action")
                        && map.get("action").equals("open_profile")
                        && map.containsKey("profileId")
                        && map.get("profileId").equals("12345")));
    verify(mockLogger).debug(anyString(), anyString());
  }

  @Test
  void testSendPushNotification_UnregisteredUser() {
    // Setup mock
    when(mockNotificationPort.isRecipientRegistered("unregistered-user")).thenReturn(false);

    // Call the service method
    NotificationResult result =
        service.sendPushNotification(
            "unregistered-user", "Push Title", "Push content", NotificationSeverity.INFO, null);

    // Verify the result
    assertNotNull(result);
    assertFalse(result.isSent());
    assertEquals(DeliveryStatus.FAILED, result.getStatus());
    assertEquals("N/A", result.getNotificationId());

    // Verify the mock interactions
    verify(mockNotificationPort).isRecipientRegistered("unregistered-user");
    verify(mockNotificationPort, never())
        .sendNotificationToRecipient(
            anyString(), anyString(), anyString(), any(NotificationSeverity.class), anyMap());
    verify(mockLogger).debug(anyString(), anyString());
    verify(mockLogger).warn(anyString(), anyString());
  }

  @Test
  void testRegisterPushDevice() {
    // Setup mock
    when(mockNotificationPort.registerRecipient(
            eq("device-user"),
            argThat(
                map ->
                    map.containsKey("type")
                        && map.get("type").equals("push")
                        && map.containsKey("device")
                        && map.get("device").equals("device-token-123"))))
        .thenReturn(true);

    // Call the service method
    boolean result =
        service.registerPushDevice("device-user", "device-token-123", "android", "1.0.0");

    // Verify the result
    assertTrue(result);

    // Verify the mock interactions
    verify(mockNotificationPort)
        .registerRecipient(
            eq("device-user"),
            argThat(
                map ->
                    map.containsKey("type")
                        && map.get("type").equals("push")
                        && map.containsKey("device")
                        && map.get("device").equals("device-token-123")
                        && map.containsKey("platform")
                        && map.get("platform").equals("android")
                        && map.containsKey("appVersion")
                        && map.get("appVersion").equals("1.0.0")
                        && map.containsKey("registeredAt")));
    verify(mockLogger).debug(anyString(), anyString(), anyString());
  }

  @Test
  void testSendNotificationAsync() throws Exception {
    // Setup mock notification result
    NotificationResult expectedResult =
        new NotificationResult(
            "test-notif-async", DeliveryStatus.SENT, "Notification sent successfully");
    when(mockNotificationPort.sendNotification(
            anyString(), anyString(), any(NotificationSeverity.class), anyMap()))
        .thenReturn(expectedResult);

    // Call the service method
    CompletableFuture<NotificationResult> future =
        service.sendNotificationAsync(
            "Async Subject", "Async content", NotificationSeverity.INFO, new HashMap<>());

    // Get the result
    NotificationResult result = future.get(); // This blocks until the future completes

    // Verify the result
    assertNotNull(result);
    assertEquals(expectedResult.getNotificationId(), result.getNotificationId());
    assertEquals(expectedResult.getStatus(), result.getStatus());

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendNotification(
            eq("Async Subject"), eq("Async content"), eq(NotificationSeverity.INFO), anyMap());
  }

  @Test
  void testSendBatchNotification() {
    // Setup mock notification results
    NotificationResult result1 =
        new NotificationResult("notif-1", DeliveryStatus.SENT, "Notification sent successfully");
    NotificationResult result2 =
        new NotificationResult("notif-2", DeliveryStatus.SENT, "Notification sent successfully");
    NotificationResult result3 =
        new NotificationResult("notif-3", DeliveryStatus.FAILED, "Recipient not found");

    when(mockNotificationPort.sendNotificationToRecipient(
            eq("user1"), anyString(), anyString(), any(NotificationSeverity.class), anyMap()))
        .thenReturn(result1);
    when(mockNotificationPort.sendNotificationToRecipient(
            eq("user2"), anyString(), anyString(), any(NotificationSeverity.class), anyMap()))
        .thenReturn(result2);
    when(mockNotificationPort.sendNotificationToRecipient(
            eq("user3"), anyString(), anyString(), any(NotificationSeverity.class), anyMap()))
        .thenReturn(result3);

    // Prepare test data
    List<String> recipients = Arrays.asList("user1", "user2", "user3");
    Map<String, String> metadata = new HashMap<>();
    metadata.put("batchId", "batch-123");

    // Call the service method
    Map<String, NotificationResult> results =
        service.sendBatchNotification(
            recipients, "Batch Subject", "Batch content", NotificationSeverity.INFO, metadata);

    // Verify the result
    assertNotNull(results);
    assertEquals(3, results.size());
    assertTrue(results.containsKey("user1"));
    assertTrue(results.containsKey("user2"));
    assertTrue(results.containsKey("user3"));
    assertEquals(result1, results.get("user1"));
    assertEquals(result2, results.get("user2"));
    assertEquals(result3, results.get("user3"));

    // Verify the mock interactions
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("user1"),
            eq("Batch Subject"),
            eq("Batch content"),
            eq(NotificationSeverity.INFO),
            eq(metadata));
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("user2"),
            eq("Batch Subject"),
            eq("Batch content"),
            eq(NotificationSeverity.INFO),
            eq(metadata));
    verify(mockNotificationPort)
        .sendNotificationToRecipient(
            eq("user3"),
            eq("Batch Subject"),
            eq("Batch content"),
            eq(NotificationSeverity.INFO),
            eq(metadata));
    verify(mockLogger).debug(anyString());
  }
}
