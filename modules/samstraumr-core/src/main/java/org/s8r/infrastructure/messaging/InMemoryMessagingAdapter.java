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
package org.s8r.infrastructure.messaging;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MessagingPort;

/**
 * In-memory implementation of the MessagingPort interface.
 *
 * <p>This adapter provides a thread-safe in-memory messaging system with support for different
 * channel types, message expiration, and request-reply messaging. It's suitable for development,
 * testing, and lightweight production use cases.
 */
public class InMemoryMessagingAdapter implements MessagingPort {

  // Implementation classes

  /**
   * Implementation of the Message interface.
   *
   * @param <T> The type of the message payload
   */
  private static class InMemoryMessage<T> implements Message {
    private final String id;
    private final String topic;
    private final T payload;
    private final Class<T> payloadType;
    private final Map<String, Object> headers;
    private final long timestamp;

    @SuppressWarnings("unchecked")
    public InMemoryMessage(
        String id,
        String topic,
        T payload,
        Class<T> payloadType,
        Map<String, Object> headers,
        long timestamp) {
      this.id = id;
      this.topic = topic;
      this.payload = payload;
      this.payloadType =
          payloadType != null
              ? payloadType
              : (Class<T>) (payload != null ? payload.getClass() : Object.class);
      this.headers = Collections.unmodifiableMap(headers);
      this.timestamp = timestamp;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public String getTopic() {
      return topic;
    }

    @Override
    public Object getPayload() {
      return payload;
    }

    @Override
    public Class<?> getPayloadType() {
      return payloadType;
    }

    @Override
    public Map<String, Object> getHeaders() {
      return headers;
    }

    @Override
    public long getTimestamp() {
      return timestamp;
    }

    @Override
    public String toString() {
      return "Message{"
          + "id='"
          + id
          + '\''
          + ", topic='"
          + topic
          + '\''
          + ", payloadType="
          + payloadType.getName()
          + ", timestamp="
          + Instant.ofEpochMilli(timestamp)
          + '}';
    }
  }

  /**
   * Implementation of the MessageBuilder interface.
   *
   * @param <T> The type of the message payload
   */
  private class InMemoryMessageBuilder<T> implements MessageBuilder<T> {
    private String topic;
    private T payload;
    private final Class<T> payloadType;
    private final Map<String, Object> headers = new HashMap<>();

    public InMemoryMessageBuilder(Class<T> payloadType) {
      this.payloadType = payloadType;
    }

    @Override
    public MessageBuilder<T> topic(String topic) {
      this.topic = topic;
      return this;
    }

    @Override
    public MessageBuilder<T> payload(T payload) {
      this.payload = payload;
      return this;
    }

    @Override
    public MessageBuilder<T> header(String key, Object value) {
      headers.put(key, value);
      return this;
    }

    @Override
    public MessageBuilder<T> headers(Map<String, Object> headers) {
      this.headers.putAll(headers);
      return this;
    }

    @Override
    public Message build() {
      if (topic == null) {
        throw new IllegalStateException("Topic must be specified");
      }
      return new InMemoryMessage<>(
          UUID.randomUUID().toString(),
          topic,
          payload,
          payloadType,
          new HashMap<>(headers),
          System.currentTimeMillis());
    }
  }

  /** Implementation of the DeliveryOptions interface. */
  private static class InMemoryDeliveryOptions implements DeliveryOptions {
    private final Duration timeToLive;
    private final int priority;
    private final boolean persistent;
    private final DeliveryMode deliveryMode;

    public InMemoryDeliveryOptions(
        Duration timeToLive, int priority, boolean persistent, DeliveryMode deliveryMode) {
      this.timeToLive = timeToLive;
      this.priority = Math.min(9, Math.max(0, priority)); // Ensure 0-9 range
      this.persistent = persistent;
      this.deliveryMode = deliveryMode != null ? deliveryMode : DeliveryMode.AT_LEAST_ONCE;
    }

    @Override
    public Optional<Duration> getTimeToLive() {
      return Optional.ofNullable(timeToLive);
    }

    @Override
    public int getPriority() {
      return priority;
    }

    @Override
    public boolean isPersistent() {
      return persistent;
    }

    @Override
    public DeliveryMode getDeliveryMode() {
      return deliveryMode;
    }
  }

  /** Implementation of the DeliveryOptionsBuilder interface. */
  private class InMemoryDeliveryOptionsBuilder implements DeliveryOptionsBuilder {
    private Duration timeToLive;
    private int priority = 4; // Default priority
    private boolean persistent = false;
    private DeliveryMode deliveryMode = DeliveryMode.AT_LEAST_ONCE;

    @Override
    public DeliveryOptionsBuilder timeToLive(Duration ttl) {
      this.timeToLive = ttl;
      return this;
    }

    @Override
    public DeliveryOptionsBuilder priority(int priority) {
      this.priority = priority;
      return this;
    }

    @Override
    public DeliveryOptionsBuilder persistent(boolean persistent) {
      this.persistent = persistent;
      return this;
    }

    @Override
    public DeliveryOptionsBuilder deliveryMode(DeliveryMode mode) {
      this.deliveryMode = mode;
      return this;
    }

    @Override
    public DeliveryOptions build() {
      return new InMemoryDeliveryOptions(timeToLive, priority, persistent, deliveryMode);
    }
  }

  /** Implementation of the ChannelProperties interface. */
  private static class InMemoryChannelProperties implements ChannelProperties {
    private final Integer maxSubscribers;
    private final Long maxMessageSize;
    private final Duration messageTtl;
    private final boolean durable;
    private final boolean autoDelete;

    public InMemoryChannelProperties(
        Integer maxSubscribers,
        Long maxMessageSize,
        Duration messageTtl,
        boolean durable,
        boolean autoDelete) {
      this.maxSubscribers = maxSubscribers;
      this.maxMessageSize = maxMessageSize;
      this.messageTtl = messageTtl;
      this.durable = durable;
      this.autoDelete = autoDelete;
    }

    @Override
    public Optional<Integer> getMaxSubscribers() {
      return Optional.ofNullable(maxSubscribers);
    }

    @Override
    public Optional<Long> getMaxMessageSize() {
      return Optional.ofNullable(maxMessageSize);
    }

    @Override
    public Optional<Duration> getMessageTtl() {
      return Optional.ofNullable(messageTtl);
    }

    @Override
    public boolean isDurable() {
      return durable;
    }

    @Override
    public boolean isAutoDelete() {
      return autoDelete;
    }
  }

  /** Implementation of the ChannelPropertiesBuilder interface. */
  private class InMemoryChannelPropertiesBuilder implements ChannelPropertiesBuilder {
    private Integer maxSubscribers;
    private Long maxMessageSize;
    private Duration messageTtl;
    private boolean durable = false;
    private boolean autoDelete = true;

    @Override
    public ChannelPropertiesBuilder maxSubscribers(int maxSubscribers) {
      this.maxSubscribers = maxSubscribers;
      return this;
    }

    @Override
    public ChannelPropertiesBuilder maxMessageSize(long maxMessageSize) {
      this.maxMessageSize = maxMessageSize;
      return this;
    }

    @Override
    public ChannelPropertiesBuilder messageTtl(Duration ttl) {
      this.messageTtl = ttl;
      return this;
    }

    @Override
    public ChannelPropertiesBuilder durable(boolean durable) {
      this.durable = durable;
      return this;
    }

    @Override
    public ChannelPropertiesBuilder autoDelete(boolean autoDelete) {
      this.autoDelete = autoDelete;
      return this;
    }

    @Override
    public ChannelProperties build() {
      return new InMemoryChannelProperties(
          maxSubscribers, maxMessageSize, messageTtl, durable, autoDelete);
    }
  }

  /** Internal class representing a message with expiration. */
  private static class PendingMessage {
    private final Message message;
    private final Instant expiryTime;
    private final int priority;

    public PendingMessage(Message message, DeliveryOptions options) {
      this.message = message;
      this.expiryTime = options.getTimeToLive().map(ttl -> Instant.now().plus(ttl)).orElse(null);
      this.priority = options.getPriority();
    }

    public Message getMessage() {
      return message;
    }

    public boolean isExpired() {
      return expiryTime != null && Instant.now().isAfter(expiryTime);
    }

    public int getPriority() {
      return priority;
    }
  }

  /** Implementation of the Channel interface. */
  private class InMemoryChannel implements Channel {
    private final String name;
    private final ChannelType type;
    private final ChannelProperties properties;
    private final ConcurrentMap<String, MessageHandler> subscribers = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<PendingMessage> pendingMessages;

    public InMemoryChannel(String name, ChannelType type, ChannelProperties properties) {
      this.name = name;
      this.type = type;
      this.properties = properties;
      this.pendingMessages =
          new PriorityBlockingQueue<>(
              11, Comparator.comparingInt(PendingMessage::getPriority).reversed());

      // For QUEUE type channels, start a consumer thread
      if (type == ChannelType.QUEUE) {
        String threadName = "queue-" + name + "-consumer";
        Thread consumerThread = new Thread(this::processQueueMessages, threadName);
        consumerThread.setDaemon(true);
        consumerThread.start();
      }
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public ChannelType getType() {
      return type;
    }

    @Override
    public MessageResult send(Message message) {
      return send(message, defaultDeliveryOptions);
    }

    @Override
    public MessageResult send(Message message, DeliveryOptions options) {
      try {
        if (message == null) {
          return MessageResult.failure(null, "Failed to send message", "Message cannot be null");
        }

        // Check if we're over the size limit
        if (properties.getMaxMessageSize().isPresent()) {
          // This is a rough size estimation
          long estimatedSize = message.toString().length() * Character.BYTES;
          if (message.getPayload() != null) {
            estimatedSize += message.getPayload().toString().length() * Character.BYTES;
          }
          if (estimatedSize > properties.getMaxMessageSize().get()) {
            return MessageResult.failure(
                message.getId(), "Failed to send message", "Message exceeds maximum size limit");
          }
        }

        // Check channel type and process accordingly
        switch (type) {
          case QUEUE:
            // In queue mode, we store the message for later processing
            pendingMessages.add(new PendingMessage(message, options));
            messageCounter.incrementAndGet();
            return MessageResult.success(message.getId(), "Message queued successfully");

          case TOPIC:
            // In topic mode, we broadcast to all subscribers immediately
            if (subscribers.isEmpty()) {
              if (properties.isAutoDelete()) {
                deleteChannel(name);
                return MessageResult.failure(
                    message.getId(),
                    "No subscribers available",
                    "Topic deleted due to no subscribers");
              }
              return MessageResult.failure(
                  message.getId(),
                  "No subscribers available",
                  "Message was not delivered to any subscribers");
            }

            // Broadcast to all subscribers
            boolean success = broadcastToSubscribers(message);
            if (success) {
              deliveredCounter.incrementAndGet();
              return MessageResult.success(message.getId(), "Message delivered to subscribers");
            } else {
              failedDeliveryCounter.incrementAndGet();
              return MessageResult.failure(
                  message.getId(),
                  "Failed to deliver message",
                  "No subscribers processed the message successfully");
            }

          case REQUEST_REPLY:
            // This is handled by the request/reply methods, not directly
            return MessageResult.failure(
                message.getId(),
                "Invalid operation",
                "Direct sending to REQUEST_REPLY channels is not supported");

          default:
            return MessageResult.failure(
                message.getId(),
                "Unknown channel type",
                "Channel type '" + type + "' is not supported");
        }
      } catch (Exception e) {
        logger.error("Error sending message to channel {}: {}", name, e.getMessage(), e);
        return MessageResult.failure(
            message != null ? message.getId() : null, "Failed to send message", e.getMessage());
      }
    }

    private boolean broadcastToSubscribers(Message message) {
      if (subscribers.isEmpty()) {
        return false;
      }

      boolean anySuccess = false;
      for (MessageHandler handler : subscribers.values()) {
        try {
          boolean success = handler.onMessage(message);
          anySuccess = anySuccess || success;
        } catch (Exception e) {
          logger.error("Error delivering message to subscriber: {}", e.getMessage(), e);
        }
      }
      return anySuccess;
    }

    private void processQueueMessages() {
      while (!Thread.currentThread().isInterrupted() && initialized) {
        try {
          PendingMessage pendingMessage = pendingMessages.poll(100, TimeUnit.MILLISECONDS);
          if (pendingMessage == null) {
            continue; // No message available
          }

          // Check if the message is expired
          if (pendingMessage.isExpired()) {
            expiredCounter.incrementAndGet();
            continue;
          }

          // Find a subscriber to process the message
          if (subscribers.isEmpty()) {
            // If no subscribers and the message is persistent, put it back
            pendingMessages.add(pendingMessage);
            Thread.sleep(100); // Wait a bit before trying again
          } else {
            // Pick a subscriber (round-robin)
            List<MessageHandler> handlers = new ArrayList<>(subscribers.values());
            int index = (int) (Math.abs(messageCounter.get()) % handlers.size());
            MessageHandler handler = handlers.get(index);

            try {
              boolean success = handler.onMessage(pendingMessage.getMessage());
              if (success) {
                deliveredCounter.incrementAndGet();
              } else {
                failedDeliveryCounter.incrementAndGet();
                // Consider putting back in the queue for retry
              }
            } catch (Exception e) {
              logger.error("Error delivering message to subscriber: {}", e.getMessage(), e);
              failedDeliveryCounter.incrementAndGet();
            }
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        } catch (Exception e) {
          logger.error("Error processing queue messages: {}", e.getMessage(), e);
        }
      }
    }

    @Override
    public String subscribe(MessageHandler handler) {
      if (handler == null) {
        return null;
      }

      // Check if we're over the subscriber limit
      if (properties.getMaxSubscribers().isPresent()
          && subscribers.size() >= properties.getMaxSubscribers().get()) {
        return null;
      }

      String subscriptionId = UUID.randomUUID().toString();
      subscribers.put(subscriptionId, handler);
      return subscriptionId;
    }

    @Override
    public boolean unsubscribe(String subscriptionId) {
      if (subscriptionId == null) {
        return false;
      }

      MessageHandler removed = subscribers.remove(subscriptionId);
      if (removed != null && subscribers.isEmpty() && properties.isAutoDelete()) {
        deleteChannel(name);
      }
      return removed != null;
    }

    @Override
    public int getSubscriberCount() {
      return subscribers.size();
    }

    @Override
    public ChannelProperties getProperties() {
      return properties;
    }

    /** Gets the number of pending messages in the channel. */
    public int getPendingMessageCount() {
      return pendingMessages.size();
    }
  }

  /** Implementation of the ChannelInfo interface. */
  private static class InMemoryChannelInfo implements ChannelInfo {
    private final String name;
    private final ChannelType type;
    private final int subscriberCount;
    private final long pendingMessageCount;
    private final ChannelProperties properties;

    public InMemoryChannelInfo(InMemoryChannel channel) {
      this.name = channel.getName();
      this.type = channel.getType();
      this.subscriberCount = channel.getSubscriberCount();
      this.pendingMessageCount = channel.getPendingMessageCount();
      this.properties = channel.getProperties();
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public ChannelType getType() {
      return type;
    }

    @Override
    public int getSubscriberCount() {
      return subscriberCount;
    }

    @Override
    public long getPendingMessageCount() {
      return pendingMessageCount;
    }

    @Override
    public ChannelProperties getProperties() {
      return properties;
    }
  }

  // Main class fields and methods

  private final LoggerPort logger;
  private final ConcurrentMap<String, InMemoryChannel> channels = new ConcurrentHashMap<>();
  private final ScheduledExecutorService executor;
  private final ConcurrentMap<String, CompletableFuture<Message>> pendingRequests =
      new ConcurrentHashMap<>();
  private final ConcurrentMap<String, Function<Message, Message>> replyHandlers =
      new ConcurrentHashMap<>();
  private final DeliveryOptions defaultDeliveryOptions;
  private final AtomicLong messageCounter = new AtomicLong(0);
  private final AtomicLong deliveredCounter = new AtomicLong(0);
  private final AtomicLong failedDeliveryCounter = new AtomicLong(0);
  private final AtomicLong expiredCounter = new AtomicLong(0);
  private boolean initialized = false;

  /**
   * Creates a new InMemoryMessagingAdapter with the specified logger.
   *
   * @param logger The logger to use for logging
   */
  public InMemoryMessagingAdapter(LoggerPort logger) {
    this.logger = logger;
    this.executor =
        Executors.newScheduledThreadPool(
            2,
            r -> {
              Thread t = new Thread(r, "messaging-executor");
              t.setDaemon(true);
              return t;
            });
    this.defaultDeliveryOptions =
        new InMemoryDeliveryOptions(null, 4, false, DeliveryMode.AT_LEAST_ONCE);
  }

  @Override
  public <T> MessageBuilder<T> createMessageBuilder(Class<T> payloadType) {
    return new InMemoryMessageBuilder<>(payloadType);
  }

  @Override
  public DeliveryOptionsBuilder createDeliveryOptionsBuilder() {
    return new InMemoryDeliveryOptionsBuilder();
  }

  @Override
  public ChannelPropertiesBuilder createChannelPropertiesBuilder() {
    return new InMemoryChannelPropertiesBuilder();
  }

  @Override
  public MessageResult initialize() {
    if (initialized) {
      return MessageResult.success(null, "Messaging system already initialized");
    }

    try {
      // Schedule expired request cleanup
      executor.scheduleAtFixedRate(this::cleanupExpiredRequests, 30, 30, TimeUnit.SECONDS);

      initialized = true;
      logger.info("In-memory messaging system initialized successfully");
      return MessageResult.success(null, "Messaging system initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize messaging system: {}", e.getMessage(), e);
      return MessageResult.failure(null, "Failed to initialize messaging system", e.getMessage());
    }
  }

  @Override
  public MessageResult createChannel(String name, ChannelType type) {
    ChannelProperties properties = new InMemoryChannelProperties(null, null, null, false, true);
    return createChannel(name, type, properties);
  }

  @Override
  public MessageResult createChannel(String name, ChannelType type, ChannelProperties properties) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before creating channels");
    }

    if (name == null || type == null || properties == null) {
      return MessageResult.failure(
          null, "Invalid parameters", "Name, type, and properties cannot be null");
    }

    try {
      if (channels.containsKey(name)) {
        return MessageResult.failure(
            null, "Channel already exists", "A channel with name '" + name + "' already exists");
      }

      InMemoryChannel channel = new InMemoryChannel(name, type, properties);
      channels.put(name, channel);

      logger.info("Created channel '{}' of type '{}'", name, type);
      return MessageResult.success(
          null, "Channel created successfully", Map.of("channel", channel));
    } catch (Exception e) {
      logger.error("Error creating channel {}: {}", name, e.getMessage(), e);
      return MessageResult.failure(null, "Failed to create channel", e.getMessage());
    }
  }

  @Override
  public MessageResult getChannel(String name) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before getting channels");
    }

    if (name == null) {
      return MessageResult.failure(null, "Invalid parameters", "Channel name cannot be null");
    }

    InMemoryChannel channel = channels.get(name);
    if (channel == null) {
      return MessageResult.failure(
          null, "Channel not found", "No channel exists with name '" + name + "'");
    }

    return MessageResult.success(null, "Channel found", Map.of("channel", channel));
  }

  @Override
  public MessageResult deleteChannel(String name) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before deleting channels");
    }

    if (name == null) {
      return MessageResult.failure(null, "Invalid parameters", "Channel name cannot be null");
    }

    InMemoryChannel removed = channels.remove(name);
    if (removed == null) {
      return MessageResult.failure(
          null, "Channel not found", "No channel exists with name '" + name + "'");
    }

    logger.info("Deleted channel '{}'", name);
    return MessageResult.success(null, "Channel deleted successfully");
  }

  @Override
  public MessageResult getChannels() {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before getting channels");
    }

    List<ChannelInfo> channelInfos = new ArrayList<>();
    for (InMemoryChannel channel : channels.values()) {
      channelInfos.add(new InMemoryChannelInfo(channel));
    }

    return MessageResult.success(
        null, "Channels retrieved successfully", Map.of("channels", channelInfos));
  }

  @Override
  public MessageResult send(String topic, Message message) {
    return send(topic, message, defaultDeliveryOptions);
  }

  @Override
  public MessageResult send(String topic, Message message, DeliveryOptions options) {
    if (!initialized) {
      return MessageResult.failure(
          message != null ? message.getId() : null,
          "Messaging system not initialized",
          "Initialize the messaging system before sending messages");
    }

    if (topic == null || message == null) {
      return MessageResult.failure(
          message != null ? message.getId() : null,
          "Invalid parameters",
          "Topic and message cannot be null");
    }

    try {
      // Get or create topic channel
      InMemoryChannel channel = channels.get(topic);
      if (channel == null) {
        // Auto-create topic channel
        MessageResult createResult = createChannel(topic, ChannelType.TOPIC);
        if (!createResult.isSuccessful()) {
          return MessageResult.failure(
              message.getId(),
              "Failed to send message",
              "Could not create topic channel: "
                  + createResult.getReason().orElse("Unknown reason"));
        }
        channel = channels.get(topic);
      }

      // Send to channel
      return channel.send(message, options != null ? options : defaultDeliveryOptions);
    } catch (Exception e) {
      logger.error("Error sending message to topic {}: {}", topic, e.getMessage(), e);
      return MessageResult.failure(message.getId(), "Failed to send message", e.getMessage());
    }
  }

  @Override
  public MessageResult subscribe(String topic, MessageHandler handler) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before subscribing to topics");
    }

    if (topic == null || handler == null) {
      return MessageResult.failure(null, "Invalid parameters", "Topic and handler cannot be null");
    }

    try {
      // Get or create topic channel
      InMemoryChannel channel = channels.get(topic);
      if (channel == null) {
        // Auto-create topic channel
        MessageResult createResult = createChannel(topic, ChannelType.TOPIC);
        if (!createResult.isSuccessful()) {
          return MessageResult.failure(
              null,
              "Failed to subscribe",
              "Could not create topic channel: "
                  + createResult.getReason().orElse("Unknown reason"));
        }
        channel = channels.get(topic);
      }

      // Subscribe to channel
      String subscriptionId = channel.subscribe(handler);
      if (subscriptionId == null) {
        return MessageResult.failure(
            null,
            "Failed to subscribe",
            "Could not subscribe to channel, possibly at subscriber limit");
      }

      logger.info("Subscribed to topic '{}' with subscription ID '{}'", topic, subscriptionId);
      return MessageResult.success(
          null, "Subscribed to topic successfully", Map.of("subscriptionId", subscriptionId));
    } catch (Exception e) {
      logger.error("Error subscribing to topic {}: {}", topic, e.getMessage(), e);
      return MessageResult.failure(null, "Failed to subscribe", e.getMessage());
    }
  }

  @Override
  public MessageResult unsubscribe(String topic, String subscriptionId) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before unsubscribing from topics");
    }

    if (topic == null || subscriptionId == null) {
      return MessageResult.failure(
          null, "Invalid parameters", "Topic and subscription ID cannot be null");
    }

    try {
      InMemoryChannel channel = channels.get(topic);
      if (channel == null) {
        return MessageResult.failure(
            null, "Topic not found", "No channel exists for topic '" + topic + "'");
      }

      boolean unsubscribed = channel.unsubscribe(subscriptionId);
      if (!unsubscribed) {
        return MessageResult.failure(
            null, "Failed to unsubscribe", "Subscription ID '" + subscriptionId + "' not found");
      }

      logger.info("Unsubscribed from topic '{}' with subscription ID '{}'", topic, subscriptionId);
      return MessageResult.success(null, "Unsubscribed from topic successfully");
    } catch (Exception e) {
      logger.error("Error unsubscribing from topic {}: {}", topic, e.getMessage(), e);
      return MessageResult.failure(null, "Failed to unsubscribe", e.getMessage());
    }
  }

  @Override
  public MessageResult request(String topic, Message message) {
    return request(topic, message, Duration.ofSeconds(30)); // Default timeout of 30 seconds
  }

  @Override
  public MessageResult request(String topic, Message message, Duration timeout) {
    if (!initialized) {
      return MessageResult.failure(
          message != null ? message.getId() : null,
          "Messaging system not initialized",
          "Initialize the messaging system before sending requests");
    }

    if (topic == null || message == null || timeout == null) {
      return MessageResult.failure(
          message != null ? message.getId() : null,
          "Invalid parameters",
          "Topic, message, and timeout cannot be null");
    }

    try {
      // Check if there's a reply handler for this topic
      if (!replyHandlers.containsKey(topic)) {
        return MessageResult.failure(
            message.getId(),
            "No reply handler",
            "No reply handler registered for topic '" + topic + "'");
      }

      // Create a CompletableFuture for the reply
      CompletableFuture<Message> future = new CompletableFuture<>();
      pendingRequests.put(message.getId(), future);

      // Create a special message with the reply-to information
      MessageBuilder<Object> builder =
          createMessageBuilder(Object.class)
              .topic(topic)
              .payload(message.getPayload())
              .headers(message.getHeaders())
              .header("replyToMessageId", message.getId());

      Message requestMessage = builder.build();

      // Process the request directly
      Function<Message, Message> handler = replyHandlers.get(topic);
      try {
        Message reply = handler.apply(requestMessage);
        if (reply != null) {
          future.complete(reply);
        } else {
          future.completeExceptionally(new RuntimeException("Reply handler returned null"));
        }
      } catch (Exception e) {
        future.completeExceptionally(e);
      }

      // Wait for the reply with a timeout
      try {
        Message reply = future.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        pendingRequests.remove(message.getId());
        return MessageResult.success(message.getId(), "Reply received", Map.of("reply", reply));
      } catch (TimeoutException e) {
        pendingRequests.remove(message.getId());
        return MessageResult.failure(
            message.getId(), "Request timed out", "No reply received within timeout period");
      } catch (Exception e) {
        pendingRequests.remove(message.getId());
        return MessageResult.failure(message.getId(), "Request failed", e.getMessage());
      }
    } catch (Exception e) {
      logger.error("Error sending request to topic {}: {}", topic, e.getMessage(), e);
      return MessageResult.failure(message.getId(), "Failed to send request", e.getMessage());
    }
  }

  @Override
  public MessageResult registerReplyHandler(String topic, Function<Message, Message> handler) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before registering reply handlers");
    }

    if (topic == null || handler == null) {
      return MessageResult.failure(null, "Invalid parameters", "Topic and handler cannot be null");
    }

    try {
      // Register the reply handler
      String handlerId = UUID.randomUUID().toString();
      replyHandlers.put(topic, handler);

      logger.info("Registered reply handler for topic '{}'", topic);
      return MessageResult.success(
          null, "Reply handler registered successfully", Map.of("handlerId", handlerId));
    } catch (Exception e) {
      logger.error("Error registering reply handler for topic {}: {}", topic, e.getMessage(), e);
      return MessageResult.failure(null, "Failed to register reply handler", e.getMessage());
    }
  }

  @Override
  public MessageResult unregisterReplyHandler(String topic, String handlerId) {
    if (!initialized) {
      return MessageResult.failure(
          null,
          "Messaging system not initialized",
          "Initialize the messaging system before unregistering reply handlers");
    }

    if (topic == null) {
      return MessageResult.failure(null, "Invalid parameters", "Topic cannot be null");
    }

    try {
      Function<Message, Message> removed = replyHandlers.remove(topic);
      if (removed == null) {
        return MessageResult.failure(
            null,
            "Reply handler not found",
            "No reply handler registered for topic '" + topic + "'");
      }

      logger.info("Unregistered reply handler for topic '{}'", topic);
      return MessageResult.success(null, "Reply handler unregistered successfully");
    } catch (Exception e) {
      logger.error("Error unregistering reply handler for topic {}: {}", topic, e.getMessage(), e);
      return MessageResult.failure(null, "Failed to unregister reply handler", e.getMessage());
    }
  }

  @Override
  public MessageResult shutdown() {
    if (!initialized) {
      return MessageResult.success(null, "Messaging system not initialized");
    }

    try {
      // Shutdown the executor
      executor.shutdown();
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }

      // Clear all channels and pending requests
      channels.clear();
      pendingRequests.clear();
      replyHandlers.clear();

      initialized = false;
      logger.info("In-memory messaging system shutdown successfully");
      return MessageResult.success(null, "Messaging system shutdown successfully");
    } catch (Exception e) {
      logger.error("Error during messaging system shutdown: {}", e.getMessage(), e);
      return MessageResult.failure(null, "Error during messaging system shutdown", e.getMessage());
    }
  }

  /** Cleans up expired request futures. */
  private void cleanupExpiredRequests() {
    // CompletableFutures don't expire on their own, so we don't need to do anything
    // This is just a placeholder for future enhancements
  }

  /**
   * Gets messaging statistics.
   *
   * @return A map of statistics
   */
  public Map<String, Object> getStatistics() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalMessages", messageCounter.get());
    stats.put("deliveredMessages", deliveredCounter.get());
    stats.put("failedDeliveries", failedDeliveryCounter.get());
    stats.put("expiredMessages", expiredCounter.get());
    stats.put("channelCount", channels.size());
    stats.put("pendingRequestsCount", pendingRequests.size());
    stats.put("replyHandlersCount", replyHandlers.size());
    return stats;
  }
}
