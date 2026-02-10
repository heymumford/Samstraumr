/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford.
 * Licensed under the Mozilla Public License 2.0.
 */
package org.s8r.application.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MessagingPort;

/**
 * Service class that provides messaging functionality to the application layer.
 *
 * <p>This service uses the MessagingPort to interact with the messaging infrastructure. It provides
 * a simplified interface for common messaging operations and adds additional business logic as
 * needed.
 */
public class MessagingService {

  private final MessagingPort messagingPort;
  private final LoggerPort logger;

  /**
   * Creates a new MessagingService with the specified dependencies.
   *
   * @param messagingPort The messaging port implementation to use
   * @param logger The logger to use for logging
   */
  public MessagingService(MessagingPort messagingPort, LoggerPort logger) {
    this.messagingPort = messagingPort;
    this.logger = logger;
  }

  /**
   * Initializes the messaging service.
   *
   * @return True if initialized successfully, false otherwise
   */
  public boolean initialize() {
    logger.info("Initializing messaging service");
    MessagingPort.MessageResult result = messagingPort.initialize();
    if (!result.isSuccessful()) {
      logger.error(
          "Failed to initialize messaging service: {}",
          result.getReason().orElse("Unknown reason"));
      return false;
    }
    return true;
  }

  /**
   * Creates a message builder for the specified payload type.
   *
   * @param <T> The type of the message payload
   * @param payloadType The class of the payload type
   * @return A new message builder
   */
  public <T> MessagingPort.MessageBuilder<T> createMessageBuilder(Class<T> payloadType) {
    return messagingPort.createMessageBuilder(payloadType);
  }

  /**
   * Creates a delivery options builder.
   *
   * @return A new delivery options builder
   */
  public MessagingPort.DeliveryOptionsBuilder createDeliveryOptionsBuilder() {
    return messagingPort.createDeliveryOptionsBuilder();
  }

  /**
   * Creates a channel properties builder.
   *
   * @return A new channel properties builder
   */
  public MessagingPort.ChannelPropertiesBuilder createChannelPropertiesBuilder() {
    return messagingPort.createChannelPropertiesBuilder();
  }

  /**
   * Creates a channel with the specified name and type.
   *
   * @param name The name of the channel
   * @param type The type of the channel
   * @return An Optional containing the created channel, or empty if creation failed
   */
  public Optional<MessagingPort.Channel> createChannel(
      String name, MessagingPort.ChannelType type) {
    MessagingPort.MessageResult result = messagingPort.createChannel(name, type);
    if (result.isSuccessful()) {
      @SuppressWarnings("unchecked")
      MessagingPort.Channel channel = (MessagingPort.Channel) result.getAttributes().get("channel");
      return Optional.ofNullable(channel);
    } else {
      logger.warn(
          "Failed to create channel {}: {}", name, result.getReason().orElse("Unknown reason"));
      return Optional.empty();
    }
  }

  /**
   * Creates a channel with the specified name, type, and properties.
   *
   * @param name The name of the channel
   * @param type The type of the channel
   * @param properties The properties of the channel
   * @return An Optional containing the created channel, or empty if creation failed
   */
  public Optional<MessagingPort.Channel> createChannel(
      String name, MessagingPort.ChannelType type, MessagingPort.ChannelProperties properties) {
    MessagingPort.MessageResult result = messagingPort.createChannel(name, type, properties);
    if (result.isSuccessful()) {
      @SuppressWarnings("unchecked")
      MessagingPort.Channel channel = (MessagingPort.Channel) result.getAttributes().get("channel");
      return Optional.ofNullable(channel);
    } else {
      logger.warn(
          "Failed to create channel {}: {}", name, result.getReason().orElse("Unknown reason"));
      return Optional.empty();
    }
  }

  /**
   * Gets a channel by name.
   *
   * @param name The name of the channel
   * @return An Optional containing the channel, or empty if not found
   */
  public Optional<MessagingPort.Channel> getChannel(String name) {
    MessagingPort.MessageResult result = messagingPort.getChannel(name);
    if (result.isSuccessful()) {
      @SuppressWarnings("unchecked")
      MessagingPort.Channel channel = (MessagingPort.Channel) result.getAttributes().get("channel");
      return Optional.ofNullable(channel);
    } else {
      if (result.getReason().isPresent() && !result.getReason().get().contains("not found")) {
        logger.warn(
            "Failed to get channel {}: {}", name, result.getReason().orElse("Unknown reason"));
      }
      return Optional.empty();
    }
  }

  /**
   * Deletes a channel by name.
   *
   * @param name The name of the channel
   * @return True if the channel was deleted successfully, false otherwise
   */
  public boolean deleteChannel(String name) {
    MessagingPort.MessageResult result = messagingPort.deleteChannel(name);
    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to delete channel {}: {}", name, result.getReason().orElse("Unknown reason"));
    }
    return result.isSuccessful();
  }

  /**
   * Gets all channels.
   *
   * @return A list of channel information, or an empty list if none found
   */
  public List<MessagingPort.ChannelInfo> getChannels() {
    MessagingPort.MessageResult result = messagingPort.getChannels();
    if (result.isSuccessful()) {
      @SuppressWarnings("unchecked")
      List<MessagingPort.ChannelInfo> channels =
          (List<MessagingPort.ChannelInfo>) result.getAttributes().get("channels");
      return channels != null ? channels : Collections.emptyList();
    } else {
      logger.warn("Failed to get channels: {}", result.getReason().orElse("Unknown reason"));
      return Collections.emptyList();
    }
  }

  /**
   * Sends a message to the specified topic.
   *
   * @param topic The topic to send the message to
   * @param message The message to send
   * @return True if the message was sent successfully, false otherwise
   */
  public boolean send(String topic, MessagingPort.Message message) {
    MessagingPort.MessageResult result = messagingPort.send(topic, message);
    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to send message to topic {}: {}",
          topic,
          result.getReason().orElse("Unknown reason"));
    }
    return result.isSuccessful();
  }

  /**
   * Sends a message to the specified topic with delivery options.
   *
   * @param topic The topic to send the message to
   * @param message The message to send
   * @param options The delivery options
   * @return True if the message was sent successfully, false otherwise
   */
  public boolean send(
      String topic, MessagingPort.Message message, MessagingPort.DeliveryOptions options) {
    MessagingPort.MessageResult result = messagingPort.send(topic, message, options);
    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to send message to topic {}: {}",
          topic,
          result.getReason().orElse("Unknown reason"));
    }
    return result.isSuccessful();
  }

  /**
   * Subscribes to messages on the specified topic.
   *
   * @param topic The topic to subscribe to
   * @param handler The message handler
   * @return An Optional containing the subscription ID, or empty if subscription failed
   */
  public Optional<String> subscribe(String topic, MessagingPort.MessageHandler handler) {
    MessagingPort.MessageResult result = messagingPort.subscribe(topic, handler);
    if (result.isSuccessful()) {
      String subscriptionId = (String) result.getAttributes().get("subscriptionId");
      return Optional.ofNullable(subscriptionId);
    } else {
      logger.warn(
          "Failed to subscribe to topic {}: {}",
          topic,
          result.getReason().orElse("Unknown reason"));
      return Optional.empty();
    }
  }

  /**
   * Unsubscribes from messages on the specified topic.
   *
   * @param topic The topic to unsubscribe from
   * @param subscriptionId The subscription ID to unsubscribe
   * @return True if unsubscribed successfully, false otherwise
   */
  public boolean unsubscribe(String topic, String subscriptionId) {
    MessagingPort.MessageResult result = messagingPort.unsubscribe(topic, subscriptionId);
    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to unsubscribe from topic {}: {}",
          topic,
          result.getReason().orElse("Unknown reason"));
    }
    return result.isSuccessful();
  }

  /**
   * Sends a request to the specified topic and waits for a reply.
   *
   * @param topic The topic to send the request to
   * @param message The request message
   * @return An Optional containing the reply message, or empty if the request failed
   */
  public Optional<MessagingPort.Message> request(String topic, MessagingPort.Message message) {
    return request(topic, message, Duration.ofSeconds(30));
  }

  /**
   * Sends a request to the specified topic and waits for a reply with a timeout.
   *
   * @param topic The topic to send the request to
   * @param message The request message
   * @param timeout The timeout duration
   * @return An Optional containing the reply message, or empty if the request failed or timed out
   */
  public Optional<MessagingPort.Message> request(
      String topic, MessagingPort.Message message, Duration timeout) {
    MessagingPort.MessageResult result = messagingPort.request(topic, message, timeout);
    if (result.isSuccessful()) {
      @SuppressWarnings("unchecked")
      MessagingPort.Message reply = (MessagingPort.Message) result.getAttributes().get("reply");
      return Optional.ofNullable(reply);
    } else {
      if (result.getReason().isPresent() && !result.getReason().get().contains("timed out")) {
        logger.warn(
            "Failed to send request to topic {}: {}",
            topic,
            result.getReason().orElse("Unknown reason"));
      }
      return Optional.empty();
    }
  }

  /**
   * Sends a request asynchronously and returns a CompletableFuture for the reply.
   *
   * @param topic The topic to send the request to
   * @param message The request message
   * @param timeout The timeout duration
   * @return A CompletableFuture that will complete with the reply message, or complete
   *     exceptionally if the request fails
   */
  public CompletableFuture<MessagingPort.Message> requestAsync(
      String topic, MessagingPort.Message message, Duration timeout) {
    CompletableFuture<MessagingPort.Message> future = new CompletableFuture<>();

    // Execute the request in a separate thread to not block the caller
    CompletableFuture.runAsync(
        () -> {
          try {
            Optional<MessagingPort.Message> reply = request(topic, message, timeout);
            if (reply.isPresent()) {
              future.complete(reply.get());
            } else {
              future.completeExceptionally(new RuntimeException("Request failed"));
            }
          } catch (Exception e) {
            future.completeExceptionally(e);
          }
        });

    return future;
  }

  /**
   * Registers a reply handler for request-reply messaging.
   *
   * @param topic The topic to handle requests for
   * @param handler The function that processes requests and returns replies
   * @return An Optional containing the handler ID, or empty if registration failed
   */
  public Optional<String> registerReplyHandler(
      String topic, Function<MessagingPort.Message, MessagingPort.Message> handler) {
    MessagingPort.MessageResult result = messagingPort.registerReplyHandler(topic, handler);
    if (result.isSuccessful()) {
      String handlerId = (String) result.getAttributes().get("handlerId");
      return Optional.ofNullable(handlerId);
    } else {
      logger.warn(
          "Failed to register reply handler for topic {}: {}",
          topic,
          result.getReason().orElse("Unknown reason"));
      return Optional.empty();
    }
  }

  /**
   * Unregisters a reply handler.
   *
   * @param topic The topic the handler is registered for
   * @param handlerId The handler ID to unregister
   * @return True if unregistered successfully, false otherwise
   */
  public boolean unregisterReplyHandler(String topic, String handlerId) {
    MessagingPort.MessageResult result = messagingPort.unregisterReplyHandler(topic, handlerId);
    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to unregister reply handler for topic {}: {}",
          topic,
          result.getReason().orElse("Unknown reason"));
    }
    return result.isSuccessful();
  }

  /**
   * Creates a simple message with a string payload.
   *
   * @param topic The topic for the message
   * @param payload The string payload
   * @return The created message
   */
  public MessagingPort.Message createTextMessage(String topic, String payload) {
    return messagingPort.createMessageBuilder(String.class).topic(topic).payload(payload).build();
  }

  /**
   * Creates a simple message with an object payload.
   *
   * @param <T> The type of the payload
   * @param topic The topic for the message
   * @param payload The object payload
   * @return The created message
   */
  public <T> MessagingPort.Message createObjectMessage(String topic, T payload) {
    @SuppressWarnings("unchecked")
    Class<T> payloadType = (Class<T>) payload.getClass();
    return messagingPort.createMessageBuilder(payloadType).topic(topic).payload(payload).build();
  }

  /**
   * Publishes an event to all subscribers of a topic.
   *
   * @param <T> The type of the event
   * @param topic The topic to publish to
   * @param event The event to publish
   * @return True if the event was published successfully, false otherwise
   */
  public <T> boolean publishEvent(String topic, T event) {
    MessagingPort.Message message = createObjectMessage(topic, event);
    return send(topic, message);
  }

  /**
   * Broadcasts a message to multiple topics.
   *
   * @param topics The topics to broadcast to
   * @param message The message to broadcast
   * @return The number of topics the message was successfully sent to
   */
  public int broadcast(Set<String> topics, MessagingPort.Message message) {
    int successCount = 0;
    for (String topic : topics) {
      if (send(topic, message)) {
        successCount++;
      }
    }
    return successCount;
  }

  /**
   * Gets statistics about the messaging system.
   *
   * @return A map of statistics, or an empty map if not available
   */
  public Map<String, Object> getStatistics() {
    try {
      // Use reflection to access adapter implementation statistics if available
      if (messagingPort instanceof org.s8r.infrastructure.messaging.InMemoryMessagingAdapter) {
        java.lang.reflect.Method method = messagingPort.getClass().getMethod("getStatistics");
        @SuppressWarnings("unchecked")
        Map<String, Object> statistics = (Map<String, Object>) method.invoke(messagingPort);
        return statistics;
      }
    } catch (Exception e) {
      logger.debug("Could not access messaging statistics: {}", e.getMessage());
    }
    return Collections.emptyMap();
  }

  /**
   * Shuts down the messaging service.
   *
   * @return True if the service was shut down successfully, false otherwise
   */
  public boolean shutdown() {
    logger.info("Shutting down messaging service");
    MessagingPort.MessageResult result = messagingPort.shutdown();
    if (!result.isSuccessful()) {
      logger.error(
          "Failed to shut down messaging service: {}", result.getReason().orElse("Unknown reason"));
      return false;
    }
    return true;
  }
}
