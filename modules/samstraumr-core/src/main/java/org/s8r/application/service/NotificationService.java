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
package org.s8r.application.service;

import java.util.HashMap;
import java.util.Map;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.notification.ContentFormat;
import org.s8r.application.port.notification.DeliveryStatus;
import org.s8r.application.port.notification.NotificationChannel;
import org.s8r.application.port.notification.NotificationResult;
import org.s8r.application.port.notification.NotificationSeverity;

/** Service for sending notifications. */
public class NotificationService {

  private final NotificationPort notificationPort;
  private final LoggerPort logger;

  /**
   * Creates a new NotificationService.
   *
   * @param notificationPort The notification port to use
   * @param logger The logger to use
   */
  public NotificationService(NotificationPort notificationPort, LoggerPort logger) {
    this.notificationPort = notificationPort;
    this.logger = logger;
  }

  /**
   * Sends a notification with the specified subject and message.
   *
   * @param recipient The recipient of the notification
   * @param subject The subject of the notification
   * @param message The message content
   * @return true if the notification was sent successfully, false otherwise
   */
  public boolean sendNotification(String recipient, String subject, String message) {
    logger.debug("Sending notification to {} with subject: {}", recipient, subject);
    return notificationPort.sendNotification(recipient, subject, message);
  }

  /**
   * Sends a notification with the specified subject, message, and additional properties.
   *
   * @param recipient The recipient of the notification
   * @param subject The subject of the notification
   * @param message The message content
   * @param properties Additional properties for the notification
   * @return true if the notification was sent successfully, false otherwise
   */
  public boolean sendNotification(
      String recipient, String subject, String message, Map<String, String> properties) {
    logger.debug("Sending notification to {} with subject: {} and properties", recipient, subject);
    return notificationPort.sendNotification(recipient, subject, message, properties);
  }

  /**
   * Sends a notification with advanced options.
   *
   * @param recipient The recipient of the notification
   * @param subject The subject of the notification
   * @param message The message content
   * @param severity The severity of the notification
   * @param channel The channel to use for the notification
   * @param format The format of the content
   * @param properties Additional properties for the notification
   * @return A result object containing information about the notification
   */
  public NotificationResult sendAdvancedNotification(
      String recipient,
      String subject,
      String message,
      NotificationSeverity severity,
      NotificationChannel channel,
      ContentFormat format,
      Map<String, String> properties) {
    logger.debug("Sending advanced notification to {} via {}", recipient, channel);

    Map<String, String> allProperties = new HashMap<>();
    if (properties != null) {
      allProperties.putAll(properties);
    }

    return notificationPort.sendAdvancedNotification(
        recipient, subject, message, severity, channel, format, allProperties);
  }

  /**
   * Sends a notification to a registered user.
   *
   * @param userId The ID of the user
   * @param subject The subject of the notification
   * @param message The message content
   * @param severity The severity of the notification
   * @return true if the notification was sent successfully, false otherwise
   */
  public boolean sendUserNotification(
      String userId, String subject, String message, NotificationSeverity severity) {
    logger.debug("Sending user notification to {} with severity {}", userId, severity);

    if (!notificationPort.isRecipientRegistered(userId)) {
      logger.warn("User {} is not registered for notifications", userId);
      return false;
    }

    // Get user data from the registration
    // In a real implementation, this would retrieve the appropriate recipient data
    Object userData = new HashMap<String, String>();

    return notificationPort.sendNotificationToRecipient(
        userId, subject, message, severity, userData);
  }

  /**
   * Registers a user as a notification recipient.
   *
   * @param userId The ID of the user
   * @param email The email of the user
   * @param preferredChannel The preferred notification channel
   * @return true if registration was successful, false otherwise
   */
  public boolean registerUserRecipient(String userId, String email, String preferredChannel) {
    logger.debug("Registering user {} with email {} for notifications", userId, email);

    Map<String, String> userData = new HashMap<>();
    userData.put("email", email);
    userData.put("preferredChannel", preferredChannel);

    return notificationPort.registerRecipient(userId, userData);
  }

  /**
   * Unregisters a user as a notification recipient.
   *
   * @param userId The ID of the user to unregister
   * @return true if unregistration was successful, false otherwise
   */
  public boolean unregisterUserRecipient(String userId) {
    logger.debug("Unregistering user {} from notifications", userId);

    if (!notificationPort.isRecipientRegistered(userId)) {
      logger.warn("User {} is not registered for notifications", userId);
      return false;
    }

    return notificationPort.unregisterRecipient(userId);
  }

  /**
   * Gets the status of a notification.
   *
   * @param notificationId The ID of the notification
   * @return The delivery status of the notification
   */
  public DeliveryStatus getNotificationStatus(String notificationId) {
    logger.debug("Getting status for notification {}", notificationId);
    return notificationPort.getNotificationStatus(notificationId);
  }
}
