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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;

/**
 * Adapter implementation of the NotificationPort interface.
 *
 * <p>This adapter provides notification operations for the application using a simple in-memory
 * notification system. In a production environment, this would be connected to external
 * notification systems (email, SMS, push notifications, etc.).
 *
 * <p>Features:
 *
 * <ul>
 *   <li>Supports multiple notification channels (email, SMS, push)
 *   <li>Tracks notification delivery status
 *   <li>Supports recipient registration and management
 *   <li>Provides system-wide notifications
 *   <li>Configurable default recipient
 * </ul>
 */
public class NotificationAdapter implements NotificationPort {

  private final LoggerPort logger;
  private final ConfigurationPort configurationPort;
  private final Map<String, Map<String, String>> recipients;
  private final Map<String, NotificationRecord> notifications;
  private final String defaultRecipient;
  private final Map<String, List<NotificationListener>> listeners = new HashMap<>();

  // Map of internal channel names to NotificationChannel enum values
  private static final Map<String, NotificationChannel> CHANNEL_MAP =
      Map.of(
          "email", NotificationChannel.EMAIL,
          "sms", NotificationChannel.SMS,
          "push", NotificationChannel.PUSH,
          "system", NotificationChannel.SYSTEM,
          "webhook", NotificationChannel.WEBHOOK,
          "discord", NotificationChannel.DISCORD,
          "slack", NotificationChannel.SLACK,
          "teams", NotificationChannel.TEAMS);

  // Channel types supported by this adapter
  private static final String CHANNEL_EMAIL = "email";
  private static final String CHANNEL_SMS = "sms";
  private static final String CHANNEL_PUSH = "push";
  private static final String CHANNEL_SYSTEM = "system";
  private static final String CHANNEL_WEBHOOK = "webhook";
  private static final String CHANNEL_DISCORD = "discord";
  private static final String CHANNEL_SLACK = "slack";
  private static final String CHANNEL_TEAMS = "teams";

  /** Record class to store notification information. */
  private static class NotificationRecord {
    private final String id;
    private final String subject;
    private final String content;
    private final String recipient;
    private final NotificationSeverity severity;
    private final Map<String, String> metadata;
    private final LocalDateTime timestamp;
    private DeliveryStatus status;
    private String statusMessage;
    private LocalDateTime deliveryTimestamp;
    private String channel;
    private int retryCount;
    private int maxRetries;

    /** Constructor for the notification record. */
    public NotificationRecord(
        String id,
        String subject,
        String content,
        String recipient,
        NotificationSeverity severity,
        Map<String, String> metadata) {
      this.id = id;
      this.subject = subject;
      this.content = content;
      this.recipient = recipient;
      this.severity = severity;
      this.metadata = new HashMap<>(metadata);
      this.timestamp = LocalDateTime.now();
      this.status = DeliveryStatus.PENDING;
      this.statusMessage = "Notification pending delivery";
      this.retryCount = 0;
      this.maxRetries = 3; // Default max retries
      this.channel = determineChannel(recipient, metadata);
    }

    /**
     * Sets the status of the notification.
     *
     * @param status The new status
     * @param message The status message
     */
    public void setStatus(DeliveryStatus status, String message) {
      this.status = status;
      this.statusMessage = message;

      if (status == DeliveryStatus.DELIVERED || status == DeliveryStatus.SENT) {
        this.deliveryTimestamp = LocalDateTime.now();
      }
    }

    /**
     * Increments the retry count.
     *
     * @return true if retry limit not reached, false otherwise
     */
    public boolean incrementRetry() {
      this.retryCount++;
      return this.retryCount <= this.maxRetries;
    }

    /**
     * Gets the time elapsed since the notification was created.
     *
     * @return The elapsed time in seconds
     */
    public long getElapsedTimeSeconds() {
      return java.time.Duration.between(timestamp, LocalDateTime.now()).getSeconds();
    }

    /**
     * Determines the delivery channel for this notification.
     *
     * @param recipient The recipient
     * @param metadata The notification metadata
     * @return The determined channel
     */
    private String determineChannel(String recipient, Map<String, String> metadata) {
      // Use channel from metadata if specified
      if (metadata.containsKey("channel")) {
        return metadata.get("channel");
      }

      // For system recipient, use system channel
      if ("system".equals(recipient)) {
        return CHANNEL_SYSTEM;
      }

      // Default to email
      return CHANNEL_EMAIL;
    }
  }

  /**
   * Constructs a new NotificationAdapter.
   *
   * @param logger The logger to use
   * @param configurationPort The configuration port to use
   */
  public NotificationAdapter(LoggerPort logger, ConfigurationPort configurationPort) {
    this.logger = logger;
    this.configurationPort = configurationPort;
    this.recipients = new ConcurrentHashMap<>();
    this.notifications = new ConcurrentHashMap<>();

    // Get default recipient from configuration
    this.defaultRecipient = configurationPort.getString("notification.default.recipient", "system");

    // Register system recipient
    Map<String, String> systemContact = new HashMap<>();
    systemContact.put("type", CHANNEL_SYSTEM);
    systemContact.put("address", "system");
    this.recipients.put("system", systemContact);

    // Add test recipients if enabled
    boolean addTestRecipients =
        configurationPort.getBoolean("notification.test.recipients.enabled", false);
    if (addTestRecipients) {
      initializeTestRecipients();
    }

    // Start notification cleaner if enabled
    boolean cleanupEnabled = configurationPort.getBoolean("notification.cleanup.enabled", true);
    if (cleanupEnabled) {
      scheduleNotificationCleanup();
    }

    logger.info("NotificationAdapter initialized with default recipient: {}", defaultRecipient);
  }

  /** Initializes test recipients for development and testing purposes. */
  private void initializeTestRecipients() {
    // Add email recipient
    Map<String, String> emailContact = new HashMap<>();
    emailContact.put("type", CHANNEL_EMAIL);
    emailContact.put("address", "test@example.com");
    this.recipients.put("email-user", emailContact);

    // Add SMS recipient
    Map<String, String> smsContact = new HashMap<>();
    smsContact.put("type", CHANNEL_SMS);
    smsContact.put("phone", "555-123-4567");
    this.recipients.put("sms-user", smsContact);

    // Add push notification recipient
    Map<String, String> pushContact = new HashMap<>();
    pushContact.put("type", CHANNEL_PUSH);
    pushContact.put("device", "device-token-123");
    pushContact.put("platform", "android");
    this.recipients.put("push-user", pushContact);

    logger.debug("Test recipients initialized");
  }

  /** Schedules periodic cleanup of old notifications. */
  private void scheduleNotificationCleanup() {
    // Simple cleanup thread that runs every minute
    Thread cleanupThread =
        new Thread(
            () -> {
              try {
                while (!Thread.currentThread().isInterrupted()) {
                  // Sleep for a minute
                  Thread.sleep(60000);

                  // Perform cleanup
                  cleanupOldNotifications();
                }
              } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Notification cleanup thread interrupted");
              }
            });
    cleanupThread.setDaemon(true);
    cleanupThread.setName("notification-cleanup");
    cleanupThread.start();

    logger.debug("Notification cleanup scheduled");
  }

  /** Cleans up old notifications to prevent memory leaks. */
  private void cleanupOldNotifications() {
    int retentionDays = configurationPort.getInt("notification.retention.days", 30);
    LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
    int cleanupCount = 0;

    for (Map.Entry<String, NotificationRecord> entry : new HashMap<>(notifications).entrySet()) {
      NotificationRecord record = entry.getValue();
      if (record.timestamp.isBefore(cutoffTime)) {
        notifications.remove(entry.getKey());
        cleanupCount++;
      }
    }

    if (cleanupCount > 0) {
      logger.debug("Cleaned up {} old notifications", cleanupCount);
    }
  }

  @Override
  public NotificationResult sendNotification(
      String subject, String content, NotificationSeverity severity, Map<String, String> metadata) {
    return sendNotificationToRecipient(defaultRecipient, subject, content, severity, metadata);
  }

  @Override
  public NotificationResult sendNotificationToRecipient(
      String recipient,
      String subject,
      String content,
      NotificationSeverity severity,
      Map<String, String> metadata) {
    logger.debug("Sending notification to recipient: {}", recipient);

    // Validate recipient
    if (!isRecipientRegistered(recipient)) {
      logger.warn("Recipient not registered: {}", recipient);
      return NotificationResult.failure("N/A", "Recipient not registered: " + recipient);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata =
        metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add standard metadata
    notificationMetadata.put("timestamp", LocalDateTime.now().toString());
    notificationMetadata.put("severity", severity.toString());

    // Create and store notification record
    NotificationRecord record =
        new NotificationRecord(
            notificationId, subject, content, recipient, severity, notificationMetadata);
    notifications.put(notificationId, record);

    try {
      // Use the appropriate delivery method based on the recipient's channel
      deliverNotification(record);

      logger.info("Notification sent successfully: {}", notificationId);
      record.setStatus(DeliveryStatus.SENT, "Notification sent successfully");
      return NotificationResult.success(notificationId);
    } catch (Exception e) {
      logger.error("Failed to send notification: {}", e.getMessage(), e);
      record.setStatus(DeliveryStatus.FAILED, "Failed to send notification: " + e.getMessage());
      return NotificationResult.failure(
          notificationId, "Failed to send notification: " + e.getMessage());
    }
  }

  @Override
  public NotificationResult sendSystemNotification(
      String subject, String content, NotificationSeverity severity) {
    logger.debug("Sending system notification");

    // Use empty metadata for system notifications
    return sendNotificationToRecipient("system", subject, content, severity, new HashMap<>());
  }

  @Override
  public boolean isRecipientRegistered(String recipient) {
    return recipients.containsKey(recipient);
  }

  @Override
  public DeliveryStatus getNotificationStatus(String notificationId) {
    logger.debug("Getting notification status: {}", notificationId);

    if (!notifications.containsKey(notificationId)) {
      logger.warn("Notification not found: {}", notificationId);
      return DeliveryStatus.FAILED;
    }

    return notifications.get(notificationId).status;
  }

  @Override
  public boolean registerRecipient(String recipient, Map<String, String> contactInfo) {
    logger.debug("Registering recipient: {}", recipient);

    if (recipient == null || recipient.trim().isEmpty()) {
      logger.warn("Invalid recipient ID");
      return false;
    }

    if (contactInfo == null || contactInfo.isEmpty()) {
      logger.warn("Invalid contact information for recipient: {}", recipient);
      return false;
    }

    // Store recipient information
    recipients.put(recipient, new HashMap<>(contactInfo));
    logger.info("Recipient registered: {}", recipient);
    return true;
  }

  @Override
  public boolean unregisterRecipient(String recipient) {
    logger.debug("Unregistering recipient: {}", recipient);

    if (recipient == null || recipient.trim().isEmpty()) {
      logger.warn("Invalid recipient ID");
      return false;
    }

    // Don't allow unregistering the system recipient
    if ("system".equals(recipient)) {
      logger.warn("Cannot unregister system recipient");
      return false;
    }

    if (!recipients.containsKey(recipient)) {
      logger.warn("Recipient not found: {}", recipient);
      return false;
    }

    recipients.remove(recipient);
    logger.info("Recipient unregistered: {}", recipient);
    return true;
  }

  @Override
  public NotificationResult sendNotificationViaChannel(
      String recipient,
      String subject,
      String content,
      NotificationSeverity severity,
      NotificationChannel channel,
      Map<String, String> metadata) {
    logger.debug("Sending notification via channel {} to recipient: {}", channel, recipient);

    // Validate recipient
    if (!isRecipientRegistered(recipient)) {
      logger.warn("Recipient not registered: {}", recipient);
      return NotificationResult.failure("N/A", "Recipient not registered: " + recipient, channel);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata =
        metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add standard metadata
    notificationMetadata.put("timestamp", LocalDateTime.now().toString());
    notificationMetadata.put("severity", severity.toString());
    notificationMetadata.put("channel", channel.toString());

    // Add channel-specific metadata
    switch (channel) {
      case SMS:
        notificationMetadata.put(
            "smsType", notificationMetadata.getOrDefault("smsType", SmsType.STANDARD.toString()));
        break;
      case PUSH:
        notificationMetadata.put(
            "pushType", notificationMetadata.getOrDefault("pushType", "default"));
        break;
      default:
        // No additional metadata needed
        break;
    }

    // Create and store notification record
    NotificationRecord record =
        new NotificationRecord(
            notificationId, subject, content, recipient, severity, notificationMetadata);

    // Set channel-specific attributes
    record.channel = getChannelString(channel);
    notifications.put(notificationId, record);

    try {
      // Deliver notification via the specified channel
      deliverNotificationViaChannel(record, channel);

      logger.info("Notification sent successfully via {}: {}", channel, notificationId);
      record.setStatus(DeliveryStatus.SENT, "Notification sent successfully via " + channel);
      return NotificationResult.success(notificationId, channel, notificationMetadata);
    } catch (Exception e) {
      logger.error("Failed to send notification via {}: {}", channel, e.getMessage(), e);
      record.setStatus(DeliveryStatus.FAILED, "Failed to send notification: " + e.getMessage());
      return NotificationResult.failure(
          notificationId, "Failed to send notification: " + e.getMessage(), channel);
    }
  }

  @Override
  public NotificationResult sendSmsNotification(
      String phoneNumber, String message, SmsType smsType, Map<String, String> metadata) {
    logger.debug("Sending SMS notification to: {}", phoneNumber);

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> smsMetadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add SMS-specific metadata
    smsMetadata.put("phoneNumber", phoneNumber);
    smsMetadata.put("smsType", smsType.toString());
    smsMetadata.put("timestamp", LocalDateTime.now().toString());

    try {
      // Check for a registered SMS recipient
      String recipientId = null;
      for (Map.Entry<String, Map<String, String>> entry : recipients.entrySet()) {
        Map<String, String> contactInfo = entry.getValue();
        if (CHANNEL_SMS.equals(contactInfo.get("type"))
            && phoneNumber.equals(contactInfo.get("phone"))) {
          recipientId = entry.getKey();
          break;
        }
      }

      // If no registered recipient, register the phone number
      if (recipientId == null) {
        recipientId = "sms-" + phoneNumber.replaceAll("[^0-9]", "");
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("type", CHANNEL_SMS);
        contactInfo.put("phone", phoneNumber);
        contactInfo.put("optIn", "true");
        registerRecipient(recipientId, contactInfo);
      }

      // Create and store notification record
      NotificationRecord record =
          new NotificationRecord(
              notificationId,
              "SMS Notification",
              message,
              recipientId,
              NotificationSeverity.INFO,
              smsMetadata);
      record.channel = CHANNEL_SMS;
      notifications.put(notificationId, record);

      // Simulate SMS delivery
      boolean delivered = simulateSmsDelivery(record, recipients.get(recipientId), smsType);

      if (delivered) {
        logger.info("SMS sent successfully to {}: {}", phoneNumber, notificationId);
        record.setStatus(DeliveryStatus.SENT, "SMS sent successfully");
        return NotificationResult.success(notificationId, NotificationChannel.SMS, smsMetadata);
      } else {
        logger.warn("Failed to deliver SMS to {}", phoneNumber);
        record.setStatus(DeliveryStatus.FAILED, "Failed to deliver SMS");
        return NotificationResult.failure(
            notificationId, "Failed to deliver SMS", NotificationChannel.SMS);
      }
    } catch (Exception e) {
      logger.error("Error sending SMS to {}: {}", phoneNumber, e.getMessage(), e);
      return NotificationResult.failure(
          notificationId, "Error sending SMS: " + e.getMessage(), NotificationChannel.SMS);
    }
  }

  @Override
  public NotificationResult scheduleNotification(
      String recipient,
      String subject,
      String content,
      NotificationSeverity severity,
      String scheduledTime,
      NotificationChannel channel) {
    logger.debug(
        "Scheduling notification for recipient {} via {} at {}", recipient, channel, scheduledTime);

    // Validate recipient
    if (!isRecipientRegistered(recipient)) {
      logger.warn("Recipient not registered: {}", recipient);
      return NotificationResult.failure("N/A", "Recipient not registered: " + recipient, channel);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata = new HashMap<>();
    notificationMetadata.put("scheduledTime", scheduledTime);
    notificationMetadata.put("channel", channel.toString());
    notificationMetadata.put("severity", severity.toString());

    // Create and store notification record
    NotificationRecord record =
        new NotificationRecord(
            notificationId, subject, content, recipient, severity, notificationMetadata);
    record.channel = getChannelString(channel);
    record.status = DeliveryStatus.SCHEDULED;
    record.statusMessage = "Notification scheduled for delivery at " + scheduledTime;
    notifications.put(notificationId, record);

    logger.info("Notification scheduled successfully: {}", notificationId);

    // Add the notification to the scheduling system
    Thread schedulerThread = new Thread(() -> {
        try {
            // Parse the scheduled time
            LocalDateTime scheduleTime = LocalDateTime.parse(scheduledTime);
            
            // Calculate delay until scheduled time
            long delayMillis = java.time.Duration.between(
                LocalDateTime.now(), scheduleTime).toMillis();
            
            if (delayMillis > 0) {
                Thread.sleep(delayMillis);
                
                // Retrieve the notification record (it might have been updated)
                NotificationRecord scheduledRecord = notifications.get(notificationId);
                
                if (scheduledRecord != null && scheduledRecord.status == DeliveryStatus.SCHEDULED) {
                    // Deliver the notification via the specified channel
                    deliverNotificationViaChannel(scheduledRecord, channel);
                    logger.info("Scheduled notification {} delivered at {}", 
                        notificationId, LocalDateTime.now());
                }
            } else {
                // If scheduled time is in the past, deliver immediately
                NotificationRecord scheduledRecord = notifications.get(notificationId);
                if (scheduledRecord != null) {
                    deliverNotificationViaChannel(scheduledRecord, channel);
                    logger.info("Past-due scheduled notification {} delivered immediately", notificationId);
                }
            }
        } catch (Exception e) {
            logger.error("Error delivering scheduled notification {}: {}", 
                notificationId, e.getMessage(), e);
            
            // Update notification status
            NotificationRecord failedRecord = notifications.get(notificationId);
            if (failedRecord != null) {
                failedRecord.setStatus(DeliveryStatus.FAILED, 
                    "Failed to deliver scheduled notification: " + e.getMessage());
            }
        }
    });
    
    schedulerThread.setDaemon(true);
    schedulerThread.setName("notification-scheduler-" + notificationId);
    schedulerThread.start();
    
    logger.info("SCHEDULED NOTIFICATION [{}]: {} for {} at {}", 
        channel, subject, recipient, scheduledTime);

    // Return the scheduled result
    return NotificationResult.scheduled(notificationId, scheduledTime);
  }

  /**
   * Generates a unique notification ID.
   *
   * @return A unique notification ID
   */
  private String generateNotificationId() {
    return "NOTIF-" + UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * Delivers a notification to the recipient.
   *
   * <p>In a real implementation, this would connect to external notification systems based on the
   * recipient's contact information.
   *
   * @param record The notification record
   */
  private void deliverNotification(NotificationRecord record) {
    Map<String, String> recipientInfo = recipients.get(record.recipient);
    String recipientType = recipientInfo.getOrDefault("type", "unknown");

    // Log the notification details
    logger.info(
        "Delivering notification: {} to {} ({})", record.id, record.recipient, recipientType);
    logger.debug("Subject: {}", record.subject);
    logger.debug("Content: {}", record.content);
    logger.debug("Severity: {}", record.severity);
    logger.debug("Channel: {}", record.channel);

    // Convert recipient type to NotificationChannel enum
    NotificationChannel channel =
        CHANNEL_MAP.getOrDefault(recipientType, NotificationChannel.EMAIL);

    // Deliver using the channel-specific method
    deliverNotificationViaChannel(record, channel);
  }

  /**
   * Delivers a notification via a specific channel.
   *
   * @param record The notification record
   * @param channel The notification channel to use
   */
  private void deliverNotificationViaChannel(
      NotificationRecord record, NotificationChannel channel) {
    Map<String, String> recipientInfo = recipients.get(record.recipient);

    if (recipientInfo == null) {
      logger.error("Recipient information not found for: {}", record.recipient);
      record.setStatus(DeliveryStatus.FAILED, "Recipient information not found");
      return;
    }

    // In a real implementation, we would use different delivery methods
    // based on the notification channel
    boolean deliverySuccess = false;

    try {
      switch (channel) {
        case EMAIL:
          // Send email notification
          deliverySuccess = simulateEmailDelivery(record, recipientInfo);
          break;

        case SMS:
          // Extract SMS type from metadata if available
          SmsType smsType = SmsType.STANDARD;
          if (record.metadata.containsKey("smsType")) {
            try {
              smsType = SmsType.valueOf(record.metadata.get("smsType"));
            } catch (IllegalArgumentException e) {
              logger.warn("Invalid SMS type: {}, using STANDARD", record.metadata.get("smsType"));
            }
          }

          // Send SMS notification
          deliverySuccess = simulateSmsDelivery(record, recipientInfo, smsType);
          break;

        case PUSH:
          // Send push notification
          deliverySuccess = simulatePushDelivery(record, recipientInfo);
          break;

        case SYSTEM:
          // Log system notification
          logger.info("SYSTEM NOTIFICATION: {} - {}", record.subject, record.content);
          deliverySuccess = true;
          break;

        case WEBHOOK:
          // Send webhook notification
          String webhookUrl = recipientInfo.getOrDefault("webhookUrl", "unknown");
          logger.info("WEBHOOK to {}: {} - {}", webhookUrl, record.subject, record.content);
          deliverySuccess = true;
          break;

        default:
          // Default to logging
          logger.info("NOTIFICATION [{}]: {} - {}", channel, record.subject, record.content);
          deliverySuccess = true;
          break;
      }

      // Update status based on delivery result
      if (deliverySuccess) {
        record.setStatus(
            DeliveryStatus.DELIVERED, "Notification delivered successfully via " + channel);
      } else if (record.incrementRetry()) {
        record.setStatus(
            DeliveryStatus.PENDING,
            "Delivery failed, will retry (attempt " + record.retryCount + ")");
        // In a real implementation, we would schedule a retry
        logger.warn("Notification delivery failed, will retry: {}", record.id);
      } else {
        record.setStatus(
            DeliveryStatus.FAILED, "Delivery failed after " + record.retryCount + " attempts");
        logger.error("Notification delivery failed permanently: {}", record.id);
      }

    } catch (Exception e) {
      logger.error("Error delivering notification via {}: {}", channel, e.getMessage(), e);
      record.setStatus(DeliveryStatus.FAILED, "Delivery error: " + e.getMessage());
    }
  }

  /**
   * Converts a NotificationChannel enum to the internal channel string.
   *
   * @param channel The NotificationChannel enum
   * @return The internal channel string
   */
  private String getChannelString(NotificationChannel channel) {
    return switch (channel) {
      case EMAIL -> CHANNEL_EMAIL;
      case SMS -> CHANNEL_SMS;
      case PUSH -> CHANNEL_PUSH;
      case SYSTEM -> CHANNEL_SYSTEM;
      case WEBHOOK -> CHANNEL_WEBHOOK;
      case DISCORD -> CHANNEL_DISCORD;
      case SLACK -> CHANNEL_SLACK;
      case TEAMS -> CHANNEL_TEAMS;
      default -> CHANNEL_EMAIL;
    };
  }

  /**
   * Simulates email delivery (for demo purposes).
   *
   * @param record The notification record
   * @param recipientInfo The recipient information
   * @return true if delivery was successful, false otherwise
   */
  private boolean simulateEmailDelivery(
      NotificationRecord record, Map<String, String> recipientInfo) {
    String emailAddress = recipientInfo.getOrDefault("address", "unknown");
    logger.info("EMAIL to {}: {} - {}", emailAddress, record.subject, record.content);

    // Simulate delivery delay
    try {
      Thread.sleep(100);

      // Simulate occasional delivery failures (10% chance)
      if (Math.random() < 0.1) {
        logger.warn("Simulated email delivery failure to: {}", emailAddress);
        return false;
      }

      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Simulates SMS delivery (for demo purposes).
   *
   * @param record The notification record
   * @param recipientInfo The recipient information
   * @return true if delivery was successful, false otherwise
   */
  private boolean simulateSmsDelivery(
      NotificationRecord record, Map<String, String> recipientInfo, SmsType smsType) {
    String phoneNumber = recipientInfo.getOrDefault("phone", "unknown");
    logger.info("SMS to {} (type: {}): {}", phoneNumber, smsType, record.content);

    // Process SMS based on type
    String smsContent = record.content;
    int maxLength = 160;
    boolean needsSpecialHandling = false;

    switch (smsType) {
      case STANDARD:
        // Standard SMS messages are limited to 160 characters
        if (smsContent.length() > maxLength) {
          smsContent = smsContent.substring(0, maxLength - 3) + "...";
          logger.debug("SMS content truncated to fit standard SMS length");
        }
        break;

      case EXTENDED:
        // Extended SMS can be longer but gets split into multiple messages
        int numParts = (int) Math.ceil(smsContent.length() / (double) maxLength);
        logger.debug("Extended SMS will be sent in {} parts", numParts);
        break;

      case BINARY:
        // Binary SMS would contain non-text data
        logger.debug("Binary SMS payload size: {} bytes", smsContent.getBytes().length);
        needsSpecialHandling = true;
        break;

      case FLASH:
        // Flash SMS appears directly on screen
        logger.debug("Flash SMS will display immediately on recipient device");
        if (smsContent.length() > maxLength) {
          smsContent = smsContent.substring(0, maxLength - 3) + "...";
        }
        break;

      default:
        logger.warn("Unknown SMS type: {}, treating as STANDARD", smsType);
        if (smsContent.length() > maxLength) {
          smsContent = smsContent.substring(0, maxLength - 3) + "...";
        }
        break;
    }

    // Simulate delivery delay
    try {
      // Different SMS types might have different delivery times
      long delayMs =
          switch (smsType) {
            case FLASH -> 50; // Flash messages are faster
            case BINARY -> 150; // Binary messages take longer
            case EXTENDED -> 200; // Extended messages take longest
            default -> 100; // Standard delay
          };

      Thread.sleep(delayMs);

      // Simulate different failure rates for different SMS types
      double failureRate =
          switch (smsType) {
            case STANDARD -> 0.10; // 10% failure rate
            case EXTENDED -> 0.20; // 20% failure rate (more parts = more chance of failure)
            case BINARY -> 0.25; // 25% failure rate (binary is less reliable)
            case FLASH -> 0.15; // 15% failure rate
            default -> 0.15; // Default failure rate
          };

      // Simulate failure
      if (Math.random() < failureRate) {
        logger.warn("Simulated SMS delivery failure to: {} (type: {})", phoneNumber, smsType);
        return false;
      }

      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  private boolean simulateSmsDelivery(
      NotificationRecord record, Map<String, String> recipientInfo) {
    // For backwards compatibility
    return simulateSmsDelivery(record, recipientInfo, SmsType.STANDARD);
  }

  /**
   * Simulates push notification delivery (for demo purposes).
   *
   * @param record The notification record
   * @param recipientInfo The recipient information
   * @return true if delivery was successful, false otherwise
   */
  private boolean simulatePushDelivery(
      NotificationRecord record, Map<String, String> recipientInfo) {
    String deviceToken = recipientInfo.getOrDefault("device", "unknown");
    String platform = recipientInfo.getOrDefault("platform", "unknown");

    logger.info("PUSH to {} device {}: {}", platform, deviceToken, record.subject);

    // Construct push payload
    Map<String, Object> pushPayload = new HashMap<>();
    pushPayload.put("title", record.subject);
    pushPayload.put("body", record.content);
    pushPayload.put("severity", record.severity.toString());

    // Add notification ID to payload for tracking
    pushPayload.put("notificationId", record.id);

    // Add custom data from metadata
    Map<String, String> customData = new HashMap<>();
    for (Map.Entry<String, String> entry : record.metadata.entrySet()) {
      if (!entry.getKey().startsWith("_")) { // Skip internal metadata
        customData.put(entry.getKey(), entry.getValue());
      }
    }
    pushPayload.put("data", customData);

    logger.debug("Push payload: {}", pushPayload);

    // Simulate delivery delay
    try {
      Thread.sleep(150);

      // Simulate occasional delivery failures (20% chance)
      if (Math.random() < 0.2) {
        logger.warn("Simulated push delivery failure to: {} ({})", deviceToken, platform);
        return false;
      }

      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  /**
   * Gets all notifications (for testing and monitoring).
   *
   * @return A map of notification records
   */
  public Map<String, NotificationRecord> getAllNotifications() {
    return new HashMap<>(notifications);
  }

  /**
   * Gets all registered recipients (for testing and monitoring).
   *
   * @return A map of recipient information
   */
  public Map<String, Map<String, String>> getAllRecipients() {
    return new HashMap<>(recipients);
  }

  /**
   * Gets notifications for a specific recipient (for testing purposes).
   *
   * @param recipient The recipient identifier
   * @return A list of notifications for the recipient
   */
  public List<Map<String, Object>> getNotificationsFor(String recipient) {
    logger.debug("Getting notifications for recipient: {}", recipient);

    List<Map<String, Object>> recipientNotifications = new ArrayList<>();

    for (NotificationRecord record : notifications.values()) {
      if (record.recipient.equals(recipient)) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("id", record.id);
        notification.put("subject", record.subject);
        notification.put("content", record.content);
        notification.put("severity", record.severity);
        notification.put("timestamp", record.timestamp);
        notification.put("status", record.status);
        notification.put("channel", record.channel);
        notification.put("metadata", record.metadata);

        recipientNotifications.add(notification);
      }
    }

    return recipientNotifications;
  }

  @Override
  public void registerListener(String topic, NotificationListener listener) {
    if (topic == null || listener == null) {
      logger.warn("Cannot register null topic or listener");
      return;
    }

    listeners.computeIfAbsent(topic, k -> new ArrayList<>()).add(listener);
    logger.debug("Registered notification listener for topic: {}", topic);
  }

  @Override
  public void unregisterListener(String topic, NotificationListener listener) {
    if (topic == null || listener == null) {
      logger.warn("Cannot unregister null topic or listener");
      return;
    }

    if (listeners.containsKey(topic)) {
      listeners.get(topic).remove(listener);
      logger.debug("Unregistered notification listener from topic: {}", topic);

      // Remove the topic if there are no more listeners
      if (listeners.get(topic).isEmpty()) {
        listeners.remove(topic);
      }
    }
  }

  @Override
  public boolean registerChannelRecipient(
      String recipient, NotificationChannel channel, Map<String, String> channelSpecificInfo) {
    logger.debug("Registering recipient {} for channel {}", recipient, channel);

    if (recipient == null || recipient.trim().isEmpty()) {
      logger.warn("Invalid recipient ID");
      return false;
    }

    if (channelSpecificInfo == null) {
      logger.warn("Channel specific information cannot be null");
      return false;
    }

    // Create contact info with channel-specific data
    Map<String, String> contactInfo = new HashMap<>(channelSpecificInfo);

    // Add channel type
    contactInfo.put("type", getChannelString(channel));

    // Add registration timestamp
    contactInfo.put("registeredAt", String.valueOf(System.currentTimeMillis()));

    // Store recipient information
    recipients.put(recipient, contactInfo);
    logger.info("Recipient registered for channel {}: {}", channel, recipient);
    return true;
  }

  @Override
  public NotificationResult sendDiscordNotification(
      String serverId,
      String channel,
      String message,
      ContentFormat contentFormat,
      Map<String, String> metadata) {
    logger.debug("Sending Discord notification to server {} channel {}", serverId, channel);

    if (serverId == null || serverId.trim().isEmpty()) {
      logger.warn("Invalid Discord server ID");
      return NotificationResult.failure(
          "N/A", "Invalid Discord server ID", NotificationChannel.DISCORD);
    }

    if (channel == null || channel.trim().isEmpty()) {
      logger.warn("Invalid Discord channel");
      return NotificationResult.failure(
          "N/A", "Invalid Discord channel", NotificationChannel.DISCORD);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata =
        metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add Discord-specific metadata
    notificationMetadata.put("serverId", serverId);
    notificationMetadata.put("channel", channel);
    notificationMetadata.put("contentFormat", contentFormat.toString());
    notificationMetadata.put("timestamp", LocalDateTime.now().toString());

    // Format message if needed
    String formattedMessage = message;
    if (contentFormat == ContentFormat.MARKDOWN) {
      // In a real implementation, this would convert Markdown to Discord's format
      // For this demo, we'll just note that it's Markdown
      logger.debug("Converting Markdown content for Discord");
    }

    try {
      // Create and store notification record
      NotificationRecord record =
          new NotificationRecord(
              notificationId,
              "Discord Notification",
              formattedMessage,
              serverId,
              NotificationSeverity.INFO,
              notificationMetadata);
      record.channel = CHANNEL_DISCORD;
      notifications.put(notificationId, record);

      // Log the Discord notification
      logger.info(
          "DISCORD to server {} channel {}: {}",
          serverId,
          channel,
          contentFormat == ContentFormat.MARKDOWN ? "[Markdown content]" : message);

      record.setStatus(DeliveryStatus.SENT, "Discord notification sent successfully");
      return NotificationResult.success(
          notificationId, NotificationChannel.DISCORD, notificationMetadata);
    } catch (Exception e) {
      logger.error("Failed to send Discord notification: {}", e.getMessage(), e);
      return NotificationResult.failure(
          notificationId,
          "Failed to send Discord notification: " + e.getMessage(),
          NotificationChannel.DISCORD);
    }
  }

  @Override
  public NotificationResult sendDiscordNotification(
      String serverId, String channel, String message) {
    // Use the more detailed method with default values
    return sendDiscordNotification(
        serverId, channel, message, ContentFormat.PLAIN_TEXT, new HashMap<>());
  }

  @Override
  public NotificationResult sendTeamsNotification(
      String teamId,
      String channel,
      String message,
      ContentFormat contentFormat,
      Map<String, String> metadata) {
    logger.debug("Sending Teams notification to team {} channel {}", teamId, channel);

    if (teamId == null || teamId.trim().isEmpty()) {
      logger.warn("Invalid Teams ID");
      return NotificationResult.failure("N/A", "Invalid Teams ID", NotificationChannel.TEAMS);
    }

    if (channel == null || channel.trim().isEmpty()) {
      logger.warn("Invalid Teams channel");
      return NotificationResult.failure("N/A", "Invalid Teams channel", NotificationChannel.TEAMS);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata =
        metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add Teams-specific metadata
    notificationMetadata.put("teamId", teamId);
    notificationMetadata.put("channel", channel);
    notificationMetadata.put("contentFormat", contentFormat.toString());
    notificationMetadata.put("timestamp", LocalDateTime.now().toString());

    // Format message if needed
    String formattedMessage = message;
    if (contentFormat == ContentFormat.MARKDOWN) {
      // In a real implementation, this would convert Markdown to Teams' format
      // For this demo, we'll just note that it's Markdown
      logger.debug("Converting Markdown content for Teams");
    }

    try {
      // Create and store notification record
      NotificationRecord record =
          new NotificationRecord(
              notificationId,
              "Teams Notification",
              formattedMessage,
              teamId,
              NotificationSeverity.INFO,
              notificationMetadata);
      record.channel = CHANNEL_TEAMS;
      notifications.put(notificationId, record);

      // Log the Teams notification
      logger.info(
          "TEAMS to team {} channel {}: {}",
          teamId,
          channel,
          contentFormat == ContentFormat.MARKDOWN ? "[Markdown content]" : message);

      record.setStatus(DeliveryStatus.SENT, "Teams notification sent successfully");
      return NotificationResult.success(
          notificationId, NotificationChannel.TEAMS, notificationMetadata);
    } catch (Exception e) {
      logger.error("Failed to send Teams notification: {}", e.getMessage(), e);
      return NotificationResult.failure(
          notificationId,
          "Failed to send Teams notification: " + e.getMessage(),
          NotificationChannel.TEAMS);
    }
  }

  @Override
  public NotificationResult sendTeamsNotification(String teamId, String channel, String message) {
    // Use the more detailed method with default values
    return sendTeamsNotification(
        teamId, channel, message, ContentFormat.PLAIN_TEXT, new HashMap<>());
  }

  @Override
  public NotificationResult sendSlackNotification(
      String workspace,
      String channel,
      String message,
      ContentFormat contentFormat,
      Map<String, String> metadata) {
    logger.debug("Sending Slack notification to workspace {} channel {}", workspace, channel);

    if (workspace == null || workspace.trim().isEmpty()) {
      logger.warn("Invalid Slack workspace");
      return NotificationResult.failure(
          "N/A", "Invalid Slack workspace", NotificationChannel.SLACK);
    }

    if (channel == null || channel.trim().isEmpty()) {
      logger.warn("Invalid Slack channel");
      return NotificationResult.failure("N/A", "Invalid Slack channel", NotificationChannel.SLACK);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata =
        metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add Slack-specific metadata
    notificationMetadata.put("workspace", workspace);
    notificationMetadata.put("channel", channel);
    notificationMetadata.put("contentFormat", contentFormat.toString());
    notificationMetadata.put("timestamp", LocalDateTime.now().toString());

    // Format message if needed
    String formattedMessage = message;
    if (contentFormat == ContentFormat.MARKDOWN) {
      // In a real implementation, this would convert Markdown to Slack's format
      // For this demo, we'll just note that it's Markdown
      logger.debug("Converting Markdown content for Slack");
    }

    try {
      // Check if there's a webhook URL in the metadata
      String webhookUrl = notificationMetadata.getOrDefault("webhookUrl", null);
      if (webhookUrl != null) {
        logger.debug("Slack notification will use webhook URL: {}", webhookUrl);
      }

      // Create and store notification record
      NotificationRecord record =
          new NotificationRecord(
              notificationId,
              "Slack Notification",
              formattedMessage,
              workspace,
              NotificationSeverity.INFO,
              notificationMetadata);
      record.channel = CHANNEL_SLACK;
      notifications.put(notificationId, record);

      // Log the Slack notification
      logger.info(
          "SLACK to workspace {} channel {}: {}",
          workspace,
          channel,
          contentFormat == ContentFormat.MARKDOWN ? "[Markdown content]" : message);

      record.setStatus(DeliveryStatus.SENT, "Slack notification sent successfully");
      return NotificationResult.success(
          notificationId, NotificationChannel.SLACK, notificationMetadata);
    } catch (Exception e) {
      logger.error("Failed to send Slack notification: {}", e.getMessage(), e);
      return NotificationResult.failure(
          notificationId,
          "Failed to send Slack notification: " + e.getMessage(),
          NotificationChannel.SLACK);
    }
  }

  @Override
  public NotificationResult sendSlackNotification(
      String workspace, String channel, String message) {
    // Use the more detailed method with default values
    return sendSlackNotification(
        workspace, channel, message, ContentFormat.PLAIN_TEXT, new HashMap<>());
  }

  @Override
  public NotificationResult sendFormattedNotification(
      String recipient,
      String subject,
      String content,
      ContentFormat contentFormat,
      NotificationSeverity severity,
      NotificationChannel channel,
      Map<String, String> metadata) {
    logger.debug("Sending formatted notification to {} via {}", recipient, channel);

    if (recipient == null || recipient.trim().isEmpty()) {
      logger.warn("Invalid recipient ID");
      return NotificationResult.failure("N/A", "Invalid recipient ID", channel);
    }

    if (!isRecipientRegistered(recipient)) {
      logger.warn("Recipient not registered: {}", recipient);
      return NotificationResult.failure("N/A", "Recipient not registered: " + recipient, channel);
    }

    // Generate notification ID
    String notificationId = generateNotificationId();

    // Prepare metadata
    Map<String, String> notificationMetadata =
        metadata != null ? new HashMap<>(metadata) : new HashMap<>();

    // Add common metadata
    notificationMetadata.put("contentFormat", contentFormat.toString());
    notificationMetadata.put("timestamp", LocalDateTime.now().toString());
    notificationMetadata.put("channel", channel.toString());

    // Format content if needed
    String formattedContent = content;
    switch (contentFormat) {
      case MARKDOWN:
        logger.debug("Processing Markdown content for {}", channel);
        // In a real implementation, this would convert Markdown to the appropriate format
        break;
      case HTML:
        logger.debug("Processing HTML content for {}", channel);
        // In a real implementation, this would convert HTML to the appropriate format
        break;
      case RICH_TEXT:
        logger.debug("Processing Rich Text content for {}", channel);
        // In a real implementation, this would convert Rich Text to the appropriate format
        break;
      case PLAIN_TEXT:
      default:
        // No formatting needed for plain text
        break;
    }

    try {
      // Create and store notification record
      NotificationRecord record =
          new NotificationRecord(
              notificationId, subject, formattedContent, recipient, severity, notificationMetadata);
      record.channel = getChannelString(channel);
      notifications.put(notificationId, record);

      // Log the notification
      logger.info(
          "FORMATTED NOTIFICATION via {} to {}: {} - {}",
          channel,
          recipient,
          subject,
          (contentFormat != ContentFormat.PLAIN_TEXT)
              ? "[" + contentFormat + " content]"
              : content);

      record.setStatus(DeliveryStatus.SENT, "Notification sent successfully via " + channel);
      return NotificationResult.success(notificationId, channel, notificationMetadata);
    } catch (Exception e) {
      logger.error("Failed to send notification: {}", e.getMessage(), e);
      return NotificationResult.failure(
          notificationId, "Failed to send notification: " + e.getMessage(), channel);
    }
  }

  @Override
  public NotificationResult sendTemplatedNotification(
      String recipient,
      String templateName,
      Map<String, String> variables,
      NotificationSeverity severity,
      NotificationChannel channel) {
    logger.debug(
        "Sending templated notification '{}' to recipient {} via {}",
        templateName,
        recipient,
        channel);

    if (recipient == null || recipient.trim().isEmpty()) {
      logger.warn("Invalid recipient ID");
      return NotificationResult.failure("N/A", "Invalid recipient ID", channel);
    }

    if (templateName == null || templateName.trim().isEmpty()) {
      logger.warn("Invalid template name");
      return NotificationResult.failure("N/A", "Invalid template name", channel);
    }

    if (!isRecipientRegistered(recipient)) {
      logger.warn("Recipient not registered: {}", recipient);
      return NotificationResult.failure("N/A", "Recipient not registered: " + recipient, channel);
    }

    // Get template from configuration
    String template = configurationPort.getString("notification.template." + templateName, null);

    if (template == null) {
      logger.warn("Template not found: {}", templateName);
      return NotificationResult.failure("N/A", "Template not found: " + templateName, channel);
    }

    // Apply variable substitution
    String content = template;
    String subject = templateName;

    if (variables != null) {
      // Extract subject from variables if provided
      if (variables.containsKey("subject")) {
        subject = variables.get("subject");
      }

      // Replace variables in template
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        String placeholder = "{{" + entry.getKey() + "}}";
        content = content.replace(placeholder, entry.getValue());
      }
    }

    // Send notification using the processed template content
    return sendNotificationViaChannel(recipient, subject, content, severity, channel, variables);
  }
}
