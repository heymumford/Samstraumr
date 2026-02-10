/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.notification;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;

/**
 * In-memory implementation of the NotificationPort interface.
 *
 * <p>This adapter provides a simple in-memory notification system. It is primarily intended for
 * testing and development environments.
 */
public class NotificationAdapter implements NotificationPort {

  private final Map<String, List<NotificationListener>> listeners = new ConcurrentHashMap<>();
  private final Map<String, List<Map<String, Object>>> notifications = new HashMap<>();
  private final Map<String, DeliveryStatus> notificationStatuses = new ConcurrentHashMap<>();
  private final Map<String, Object> registeredRecipients = new ConcurrentHashMap<>();

  @Override
  public boolean sendNotification(String recipient, String subject, String message) {
    return sendNotification(recipient, subject, message, Map.of());
  }

  @Override
  public boolean sendNotification(
      String recipient, String subject, String message, Map<String, String> properties) {
    // Store the notification
    notifications
        .computeIfAbsent(recipient, k -> new CopyOnWriteArrayList<>())
        .add(
            Map.of(
                "subject", subject,
                "message", message,
                "properties", properties));

    // Notify listeners
    if (listeners.containsKey(subject)) {
      for (NotificationListener listener : listeners.get(subject)) {
        listener.onNotification("NotificationAdapter", subject, message, properties);
      }
    }

    return true;
  }

  /**
   * Sends a notification with the specified subject and message. This is an extension of the
   * standard method that returns a more detailed result.
   *
   * @param subject The subject of the notification
   * @param message The message content
   * @param severity The severity of the notification
   * @param metadata Additional metadata for the notification
   * @return A result object containing information about the notification
   */
  public NotificationResult sendNotification(
      String subject, String message, NotificationSeverity severity, Map<String, String> metadata) {

    // Use system as default recipient
    String recipient = "system";

    // Generate a notification ID
    String notificationId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    // Store the notification
    Map<String, Object> notificationData = new HashMap<>();
    notificationData.put("subject", subject);
    notificationData.put("message", message);
    notificationData.put("severity", severity);
    notificationData.put("id", notificationId);
    notificationData.put("timestamp", Instant.now());

    // Add metadata if provided
    if (metadata != null) {
      notificationData.put("metadata", metadata);
    }

    notifications
        .computeIfAbsent(recipient, k -> new CopyOnWriteArrayList<>())
        .add(notificationData);

    // Set status
    notificationStatuses.put(notificationId, DeliveryStatus.SENT);

    // Build result metadata
    Map<String, String> resultMetadata = new HashMap<>();
    resultMetadata.put("recipientId", recipient);
    resultMetadata.put("notificationId", notificationId);
    resultMetadata.put("severity", severity.toString());

    // Add additional metadata if provided
    if (metadata != null) {
      resultMetadata.putAll(metadata);
    }

    return NotificationResult.success(
        "Notification sent successfully", DeliveryStatus.SENT, resultMetadata);
  }

  @Override
  public NotificationResult sendAdvancedNotification(
      String recipient,
      String subject,
      String message,
      NotificationSeverity severity,
      NotificationChannel channel,
      ContentFormat format,
      Map<String, String> properties) {

    // Store the notification with additional metadata
    Map<String, Object> notificationData = new HashMap<>();
    notificationData.put("subject", subject);
    notificationData.put("message", message);
    notificationData.put("severity", severity);
    notificationData.put("channel", channel);
    notificationData.put("format", format);
    notificationData.put("properties", properties);

    notifications
        .computeIfAbsent(recipient, k -> new CopyOnWriteArrayList<>())
        .add(notificationData);

    // Generate a notification ID
    String notificationId = UUID.randomUUID().toString();

    // Set initial status
    notificationStatuses.put(notificationId, DeliveryStatus.DELIVERED);

    // Notify listeners
    if (listeners.containsKey(subject)) {
      for (NotificationListener listener : listeners.get(subject)) {
        listener.onNotification("NotificationAdapter", subject, message, properties);
      }
    }

    // Return a successful result
    return NotificationResult.success(
        "Notification sent successfully",
        DeliveryStatus.DELIVERED,
        Map.of(
            "channel", channel.toString(),
            "severity", severity.toString(),
            "format", format.toString(),
            "recipient", recipient));
  }

  @Override
  public void registerListener(String topic, NotificationListener listener) {
    listeners.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(listener);
  }

  @Override
  public void unregisterListener(String topic, NotificationListener listener) {
    if (listeners.containsKey(topic)) {
      listeners.get(topic).remove(listener);
      if (listeners.get(topic).isEmpty()) {
        listeners.remove(topic);
      }
    }
  }

  @Override
  public DeliveryStatus getNotificationStatus(String notificationId) {
    return notificationStatuses.getOrDefault(notificationId, DeliveryStatus.UNKNOWN);
  }

  /**
   * Sends a notification to the system recipient. This is used for system-level notifications that
   * aren't targeted at a specific user.
   *
   * @param subject The subject of the notification
   * @param message The message content
   * @param severity The severity of the notification
   * @return A result object containing information about the notification
   */
  public NotificationResult sendSystemNotification(
      String subject, String message, NotificationSeverity severity) {
    // Generate a notification ID
    String notificationId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    // Store the notification
    Map<String, Object> notificationData = new HashMap<>();
    notificationData.put("subject", subject);
    notificationData.put("message", message);
    notificationData.put("severity", severity);
    notificationData.put("id", notificationId);
    notificationData.put("timestamp", Instant.now());

    notifications
        .computeIfAbsent("system", k -> new CopyOnWriteArrayList<>())
        .add(notificationData);

    // Set status
    notificationStatuses.put(notificationId, DeliveryStatus.SENT);

    // Return a result
    return NotificationResult.success(
        "System notification sent successfully",
        DeliveryStatus.SENT,
        Map.of(
            "recipient",
            "system",
            "notificationId",
            notificationId,
            "severity",
            severity.toString()));
  }

  /**
   * Gets all notifications sent to a recipient.
   *
   * @param recipient The recipient to check
   * @return A list of notification data maps
   */
  public List<Map<String, Object>> getNotificationsFor(String recipient) {
    return notifications.getOrDefault(recipient, List.of());
  }

  /** Clears all stored notifications. */
  public void clearNotifications() {
    notifications.clear();
  }

  @Override
  public boolean isRecipientRegistered(String recipientId) {
    return registeredRecipients.containsKey(recipientId);
  }

  @Override
  public boolean registerRecipient(String recipientId, Object recipientData) {
    if (recipientId == null || recipientId.isEmpty()) {
      return false;
    }

    registeredRecipients.put(recipientId, recipientData);
    return true;
  }

  @Override
  public boolean unregisterRecipient(String recipientId) {
    if (recipientId == null
        || recipientId.isEmpty()
        || !registeredRecipients.containsKey(recipientId)) {
      return false;
    }

    registeredRecipients.remove(recipientId);
    return true;
  }

  @Override
  public boolean sendNotificationToRecipient(
      String recipientId,
      String subject,
      String message,
      NotificationSeverity severity,
      Object recipientData) {
    // Check if recipient is registered
    if (!isRecipientRegistered(recipientId)) {
      return false;
    }

    // In a real implementation, we would format and send according to recipient preferences
    // For this mock implementation, we just store the notification
    Map<String, String> properties = new HashMap<>();
    properties.put("severity", severity.toString());

    return sendNotification(recipientId, subject, message, properties);
  }

  /**
   * Enhanced version of sendNotificationToRecipient that returns a NotificationResult. This is an
   * overload used by test code.
   *
   * @param recipientId The ID of the recipient
   * @param subject The subject of the notification
   * @param message The message content
   * @param severity The severity of the notification
   * @param metadata Additional metadata for the notification
   * @return A result object containing information about the notification
   */
  public NotificationResult sendNotificationToRecipient(
      String recipientId,
      String subject,
      String message,
      NotificationSeverity severity,
      Map<String, String> metadata) {

    // Check if recipient is registered
    if (!isRecipientRegistered(recipientId)) {
      return NotificationResult.failure(
          "Recipient not registered",
          "The recipient '" + recipientId + "' is not registered",
          DeliveryStatus.FAILED,
          Map.of("recipientId", recipientId, "notificationId", "N/A"));
    }

    // Generate a notification ID
    String notificationId = "NOTIF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    // Store the notification
    Map<String, Object> notificationData = new HashMap<>();
    notificationData.put("subject", subject);
    notificationData.put("message", message);
    notificationData.put("severity", severity);
    notificationData.put("id", notificationId);
    notificationData.put("timestamp", Instant.now());

    // Add metadata if provided
    if (metadata != null) {
      notificationData.put("metadata", metadata);
    }

    notifications
        .computeIfAbsent(recipientId, k -> new CopyOnWriteArrayList<>())
        .add(notificationData);

    // Set status
    notificationStatuses.put(notificationId, DeliveryStatus.SENT);

    // Build result metadata
    Map<String, String> resultMetadata = new HashMap<>();
    resultMetadata.put("recipientId", recipientId);
    resultMetadata.put("notificationId", notificationId);
    resultMetadata.put("severity", severity.toString());

    // Add additional metadata if provided
    if (metadata != null) {
      resultMetadata.putAll(metadata);
    }

    return NotificationResult.success(
        "Notification sent successfully to recipient '" + recipientId + "'",
        DeliveryStatus.SENT,
        resultMetadata);
  }
}
