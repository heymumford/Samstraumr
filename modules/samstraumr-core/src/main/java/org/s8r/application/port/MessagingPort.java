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
package org.s8r.application.port;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * MessagingPort defines the interface for messaging operations in the S8r framework.
 *
 * <p>This port provides messaging capabilities between components and potential external systems,
 * extending the event-driven architecture with more general messaging patterns. It follows Clean
 * Architecture principles by defining a boundary between the application and infrastructure layers.
 */
public interface MessagingPort {

  /** Represents a message that can be sent through the messaging system. */
  interface Message {
    /**
     * Gets the unique identifier of the message.
     *
     * @return The message identifier
     */
    String getId();

    /**
     * Gets the topic or destination of the message.
     *
     * @return The topic or destination
     */
    String getTopic();

    /**
     * Gets the payload of the message.
     *
     * @return The message payload
     */
    Object getPayload();

    /**
     * Gets the type of the payload.
     *
     * @return The payload type
     */
    Class<?> getPayloadType();

    /**
     * Gets the headers of the message.
     *
     * @return The message headers
     */
    Map<String, Object> getHeaders();

    /**
     * Gets the timestamp when the message was created.
     *
     * @return The creation timestamp in milliseconds
     */
    long getTimestamp();
  }

  /** Delivery options for sending messages. */
  interface DeliveryOptions {
    /**
     * Gets the time-to-live for the message.
     *
     * @return The time-to-live duration
     */
    Optional<Duration> getTimeToLive();

    /**
     * Gets the priority of the message.
     *
     * @return The message priority (0-9, higher is more important)
     */
    int getPriority();

    /**
     * Checks if the message should be persistent.
     *
     * @return True if the message should be persisted, false otherwise
     */
    boolean isPersistent();

    /**
     * Gets the delivery mode.
     *
     * @return The delivery mode
     */
    DeliveryMode getDeliveryMode();
  }

  /** Supported delivery modes for messages. */
  enum DeliveryMode {
    /** At most once delivery, may be lost */
    AT_MOST_ONCE,

    /** At least once delivery, may be duplicated */
    AT_LEAST_ONCE,

    /** Exactly once delivery, guaranteed no duplication */
    EXACTLY_ONCE
  }

  /** Represents a message handler that can process messages from a topic. */
  interface MessageHandler {
    /**
     * Processes a message.
     *
     * @param message The message to process
     * @return True if the message was processed successfully, false otherwise
     */
    boolean onMessage(Message message);
  }

  /** Represents a messaging channel used for communication. */
  interface Channel {
    /**
     * Gets the name of the channel.
     *
     * @return The channel name
     */
    String getName();

    /**
     * Gets the type of the channel.
     *
     * @return The channel type
     */
    ChannelType getType();

    /**
     * Sends a message to this channel.
     *
     * @param message The message to send
     * @return A MessageResult indicating success or failure
     */
    MessageResult send(Message message);

    /**
     * Sends a message to this channel with delivery options.
     *
     * @param message The message to send
     * @param options The delivery options
     * @return A MessageResult indicating success or failure
     */
    MessageResult send(Message message, DeliveryOptions options);

    /**
     * Subscribes to messages on this channel.
     *
     * @param handler The message handler to register
     * @return A subscription ID that can be used to unsubscribe
     */
    String subscribe(MessageHandler handler);

    /**
     * Unsubscribes from messages on this channel.
     *
     * @param subscriptionId The subscription ID to unsubscribe
     * @return True if unsubscribed successfully, false otherwise
     */
    boolean unsubscribe(String subscriptionId);

    /**
     * Gets the number of subscribers to this channel.
     *
     * @return The number of subscribers
     */
    int getSubscriberCount();

    /**
     * Gets the properties of the channel.
     *
     * @return The channel properties
     */
    ChannelProperties getProperties();
  }

  /** Types of messaging channels. */
  enum ChannelType {
    /** Point-to-point channel (one publisher, one consumer) */
    QUEUE,

    /** Publish-subscribe channel (one publisher, many consumers) */
    TOPIC,

    /** Request-reply channel (bidirectional communication) */
    REQUEST_REPLY
  }

  /** Properties for configuring a messaging channel. */
  interface ChannelProperties {
    /**
     * Gets the maximum number of subscribers allowed.
     *
     * @return The maximum subscriber count, or empty for unlimited
     */
    Optional<Integer> getMaxSubscribers();

    /**
     * Gets the maximum message size in bytes.
     *
     * @return The maximum message size, or empty for unlimited
     */
    Optional<Long> getMaxMessageSize();

    /**
     * Gets the message time-to-live.
     *
     * @return The default message TTL, or empty for unlimited
     */
    Optional<Duration> getMessageTtl();

    /**
     * Checks if the channel is durable (survives system restarts).
     *
     * @return True if the channel is durable, false otherwise
     */
    boolean isDurable();

    /**
     * Checks if the channel should auto-delete when no subscribers.
     *
     * @return True if the channel auto-deletes, false otherwise
     */
    boolean isAutoDelete();
  }

  /** Information about a messaging channel. */
  interface ChannelInfo {
    /**
     * Gets the name of the channel.
     *
     * @return The channel name
     */
    String getName();

    /**
     * Gets the type of the channel.
     *
     * @return The channel type
     */
    ChannelType getType();

    /**
     * Gets the number of subscribers to the channel.
     *
     * @return The number of subscribers
     */
    int getSubscriberCount();

    /**
     * Gets the number of pending messages in the channel.
     *
     * @return The number of pending messages
     */
    long getPendingMessageCount();

    /**
     * Gets the properties of the channel.
     *
     * @return The channel properties
     */
    ChannelProperties getProperties();
  }

  /** Represents the outcome of a messaging operation with detailed information. */
  final class MessageResult {
    private final boolean successful;
    private final String messageId;
    private final String message;
    private final String reason;
    private final Map<String, Object> attributes;

    private MessageResult(
        boolean successful,
        String messageId,
        String message,
        String reason,
        Map<String, Object> attributes) {
      this.successful = successful;
      this.messageId = messageId;
      this.message = message;
      this.reason = reason;
      this.attributes = attributes;
    }

    /**
     * Creates a successful result.
     *
     * @param messageId The ID of the message
     * @param message A message describing the successful operation
     * @return A new MessageResult instance indicating success
     */
    public static MessageResult success(String messageId, String message) {
      return new MessageResult(true, messageId, message, null, Map.of());
    }

    /**
     * Creates a successful result with additional attributes.
     *
     * @param messageId The ID of the message
     * @param message A message describing the successful operation
     * @param attributes Additional information about the operation
     * @return A new MessageResult instance indicating success
     */
    public static MessageResult success(
        String messageId, String message, Map<String, Object> attributes) {
      return new MessageResult(true, messageId, message, null, attributes);
    }

    /**
     * Creates a failed result.
     *
     * @param messageId The ID of the message, if available
     * @param message A message describing the failed operation
     * @param reason The reason for the failure
     * @return A new MessageResult instance indicating failure
     */
    public static MessageResult failure(String messageId, String message, String reason) {
      return new MessageResult(false, messageId, message, reason, Map.of());
    }

    /**
     * Creates a failed result with additional attributes.
     *
     * @param messageId The ID of the message, if available
     * @param message A message describing the failed operation
     * @param reason The reason for the failure
     * @param attributes Additional information about the operation
     * @return A new MessageResult instance indicating failure
     */
    public static MessageResult failure(
        String messageId, String message, String reason, Map<String, Object> attributes) {
      return new MessageResult(false, messageId, message, reason, attributes);
    }

    /**
     * Checks if the operation was successful.
     *
     * @return True if the operation was successful, false otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Gets the ID of the message associated with the operation.
     *
     * @return The message ID, or null if not available
     */
    public Optional<String> getMessageId() {
      return Optional.ofNullable(messageId);
    }

    /**
     * Gets the message associated with the operation result.
     *
     * @return The message describing the operation outcome
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the reason for a failed operation.
     *
     * @return The reason for the failure, or empty if the operation was successful
     */
    public Optional<String> getReason() {
      return Optional.ofNullable(reason);
    }

    /**
     * Gets the additional attributes associated with the operation result.
     *
     * @return A map of attributes providing additional information
     */
    public Map<String, Object> getAttributes() {
      return attributes;
    }
  }

  /**
   * Builder for creating message instances.
   *
   * @param <T> The type of the message payload
   */
  interface MessageBuilder<T> {
    /**
     * Sets the topic or destination of the message.
     *
     * @param topic The topic or destination
     * @return This builder for method chaining
     */
    MessageBuilder<T> topic(String topic);

    /**
     * Sets the payload of the message.
     *
     * @param payload The message payload
     * @return This builder for method chaining
     */
    MessageBuilder<T> payload(T payload);

    /**
     * Adds a header to the message.
     *
     * @param key The header key
     * @param value The header value
     * @return This builder for method chaining
     */
    MessageBuilder<T> header(String key, Object value);

    /**
     * Adds all headers to the message.
     *
     * @param headers The headers to add
     * @return This builder for method chaining
     */
    MessageBuilder<T> headers(Map<String, Object> headers);

    /**
     * Builds the message.
     *
     * @return The built message
     */
    Message build();
  }

  /** Builder for creating delivery options. */
  interface DeliveryOptionsBuilder {
    /**
     * Sets the time-to-live for the message.
     *
     * @param ttl The time-to-live duration
     * @return This builder for method chaining
     */
    DeliveryOptionsBuilder timeToLive(Duration ttl);

    /**
     * Sets the priority of the message.
     *
     * @param priority The message priority (0-9, higher is more important)
     * @return This builder for method chaining
     */
    DeliveryOptionsBuilder priority(int priority);

    /**
     * Sets whether the message should be persistent.
     *
     * @param persistent True if the message should be persisted, false otherwise
     * @return This builder for method chaining
     */
    DeliveryOptionsBuilder persistent(boolean persistent);

    /**
     * Sets the delivery mode.
     *
     * @param mode The delivery mode
     * @return This builder for method chaining
     */
    DeliveryOptionsBuilder deliveryMode(DeliveryMode mode);

    /**
     * Builds the delivery options.
     *
     * @return The built delivery options
     */
    DeliveryOptions build();
  }

  /** Builder for creating channel properties. */
  interface ChannelPropertiesBuilder {
    /**
     * Sets the maximum number of subscribers allowed.
     *
     * @param maxSubscribers The maximum subscriber count
     * @return This builder for method chaining
     */
    ChannelPropertiesBuilder maxSubscribers(int maxSubscribers);

    /**
     * Sets the maximum message size in bytes.
     *
     * @param maxMessageSize The maximum message size
     * @return This builder for method chaining
     */
    ChannelPropertiesBuilder maxMessageSize(long maxMessageSize);

    /**
     * Sets the message time-to-live.
     *
     * @param ttl The default message TTL
     * @return This builder for method chaining
     */
    ChannelPropertiesBuilder messageTtl(Duration ttl);

    /**
     * Sets whether the channel is durable.
     *
     * @param durable True if the channel should be durable, false otherwise
     * @return This builder for method chaining
     */
    ChannelPropertiesBuilder durable(boolean durable);

    /**
     * Sets whether the channel should auto-delete.
     *
     * @param autoDelete True if the channel should auto-delete, false otherwise
     * @return This builder for method chaining
     */
    ChannelPropertiesBuilder autoDelete(boolean autoDelete);

    /**
     * Builds the channel properties.
     *
     * @return The built channel properties
     */
    ChannelProperties build();
  }

  /**
   * Creates a message builder for the specified payload type.
   *
   * @param <T> The type of the message payload
   * @param payloadType The class of the payload type
   * @return A new message builder
   */
  <T> MessageBuilder<T> createMessageBuilder(Class<T> payloadType);

  /**
   * Creates a delivery options builder.
   *
   * @return A new delivery options builder
   */
  DeliveryOptionsBuilder createDeliveryOptionsBuilder();

  /**
   * Creates a channel properties builder.
   *
   * @return A new channel properties builder
   */
  ChannelPropertiesBuilder createChannelPropertiesBuilder();

  /**
   * Initializes the messaging system.
   *
   * @return A MessageResult indicating success or failure
   */
  MessageResult initialize();

  /**
   * Creates a channel with the specified name and type.
   *
   * @param name The name of the channel
   * @param type The type of the channel
   * @return A MessageResult containing the created channel
   */
  MessageResult createChannel(String name, ChannelType type);

  /**
   * Creates a channel with the specified name, type, and properties.
   *
   * @param name The name of the channel
   * @param type The type of the channel
   * @param properties The properties of the channel
   * @return A MessageResult containing the created channel
   */
  MessageResult createChannel(String name, ChannelType type, ChannelProperties properties);

  /**
   * Gets a channel by name.
   *
   * @param name The name of the channel
   * @return A MessageResult containing the channel, or a failure if not found
   */
  MessageResult getChannel(String name);

  /**
   * Deletes a channel by name.
   *
   * @param name The name of the channel
   * @return A MessageResult indicating success or failure
   */
  MessageResult deleteChannel(String name);

  /**
   * Gets all channels.
   *
   * @return A MessageResult containing a list of channel information
   */
  MessageResult getChannels();

  /**
   * Sends a message to the specified topic.
   *
   * @param topic The topic to send the message to
   * @param message The message to send
   * @return A MessageResult indicating success or failure
   */
  MessageResult send(String topic, Message message);

  /**
   * Sends a message to the specified topic with delivery options.
   *
   * @param topic The topic to send the message to
   * @param message The message to send
   * @param options The delivery options
   * @return A MessageResult indicating success or failure
   */
  MessageResult send(String topic, Message message, DeliveryOptions options);

  /**
   * Subscribes to messages on the specified topic.
   *
   * @param topic The topic to subscribe to
   * @param handler The message handler
   * @return A MessageResult containing the subscription ID
   */
  MessageResult subscribe(String topic, MessageHandler handler);

  /**
   * Unsubscribes from messages on the specified topic.
   *
   * @param topic The topic to unsubscribe from
   * @param subscriptionId The subscription ID to unsubscribe
   * @return A MessageResult indicating success or failure
   */
  MessageResult unsubscribe(String topic, String subscriptionId);

  /**
   * Sends a request to the specified topic and waits for a reply.
   *
   * @param topic The topic to send the request to
   * @param message The request message
   * @return A MessageResult containing the reply message
   */
  MessageResult request(String topic, Message message);

  /**
   * Sends a request to the specified topic and waits for a reply with a timeout.
   *
   * @param topic The topic to send the request to
   * @param message The request message
   * @param timeout The timeout duration
   * @return A MessageResult containing the reply message
   */
  MessageResult request(String topic, Message message, Duration timeout);

  /**
   * Registers a reply handler for request-reply messaging.
   *
   * @param topic The topic to handle requests for
   * @param handler The function that processes requests and returns replies
   * @return A MessageResult containing the handler ID
   */
  MessageResult registerReplyHandler(String topic, Function<Message, Message> handler);

  /**
   * Unregisters a reply handler.
   *
   * @param topic The topic the handler is registered for
   * @param handlerId The handler ID to unregister
   * @return A MessageResult indicating success or failure
   */
  MessageResult unregisterReplyHandler(String topic, String handlerId);

  /**
   * Shuts down the messaging system.
   *
   * @return A MessageResult indicating success or failure
   */
  MessageResult shutdown();
}
