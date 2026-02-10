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

package org.s8r.test.mock;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MessagingPort;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mock implementation of the MessagingPort interface.
 *
 * <p>This adapter provides an in-memory messaging system for testing the
 * MessagingPort interface functionality.
 */
public class MockMessagingAdapter implements MessagingPort {
    
    private final LoggerPort logger;
    private final Map<String, InMemoryChannel> channels;
    private final Map<String, List<MessageHandler>> topicSubscribers;
    private final Map<String, Map<String, Function<Message, Message>>> replyHandlers;
    private boolean initialized;
    
    /**
     * Implementation of the Message interface.
     */
    private static class InMemoryMessage implements Message {
        private final String id;
        private final String topic;
        private final Object payload;
        private final Class<?> payloadType;
        private final Map<String, Object> headers;
        private final long timestamp;
        
        /**
         * Constructs a new InMemoryMessage.
         *
         * @param id The message ID
         * @param topic The message topic
         * @param payload The message payload
         * @param payloadType The payload type
         * @param headers The message headers
         */
        public InMemoryMessage(String id, String topic, Object payload, Class<?> payloadType, Map<String, Object> headers) {
            this.id = id;
            this.topic = topic;
            this.payload = payload;
            this.payloadType = payloadType;
            this.headers = new HashMap<>(headers);
            this.timestamp = System.currentTimeMillis();
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
            return Collections.unmodifiableMap(headers);
        }
        
        @Override
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * Implementation of the DeliveryOptions interface.
     */
    private static class InMemoryDeliveryOptions implements DeliveryOptions {
        private final Optional<Duration> timeToLive;
        private final int priority;
        private final boolean persistent;
        private final DeliveryMode deliveryMode;
        
        /**
         * Constructs a new InMemoryDeliveryOptions.
         *
         * @param timeToLive The time-to-live
         * @param priority The priority
         * @param persistent Whether the message is persistent
         * @param deliveryMode The delivery mode
         */
        public InMemoryDeliveryOptions(Optional<Duration> timeToLive, int priority, boolean persistent, DeliveryMode deliveryMode) {
            this.timeToLive = timeToLive;
            this.priority = priority;
            this.persistent = persistent;
            this.deliveryMode = deliveryMode;
        }
        
        @Override
        public Optional<Duration> getTimeToLive() {
            return timeToLive;
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
    
    /**
     * Implementation of the ChannelProperties interface.
     */
    private static class InMemoryChannelProperties implements ChannelProperties {
        private final Optional<Integer> maxSubscribers;
        private final Optional<Long> maxMessageSize;
        private final Optional<Duration> messageTtl;
        private final boolean durable;
        private final boolean autoDelete;
        
        /**
         * Constructs a new InMemoryChannelProperties.
         *
         * @param maxSubscribers The maximum number of subscribers
         * @param maxMessageSize The maximum message size
         * @param messageTtl The message time-to-live
         * @param durable Whether the channel is durable
         * @param autoDelete Whether the channel auto-deletes
         */
        public InMemoryChannelProperties(
                Optional<Integer> maxSubscribers,
                Optional<Long> maxMessageSize,
                Optional<Duration> messageTtl,
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
            return maxSubscribers;
        }
        
        @Override
        public Optional<Long> getMaxMessageSize() {
            return maxMessageSize;
        }
        
        @Override
        public Optional<Duration> getMessageTtl() {
            return messageTtl;
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
    
    /**
     * Implementation of the Channel interface.
     */
    private class InMemoryChannel implements Channel, ChannelInfo {
        private final String name;
        private final ChannelType type;
        private final InMemoryChannelProperties properties;
        private final List<Message> pendingMessages;
        private final Map<String, MessageHandler> subscribers;
        
        /**
         * Constructs a new InMemoryChannel.
         *
         * @param name The channel name
         * @param type The channel type
         * @param properties The channel properties
         */
        public InMemoryChannel(String name, ChannelType type, ChannelProperties properties) {
            this.name = name;
            this.type = type;
            
            // Convert properties to InMemoryChannelProperties if needed
            if (properties instanceof InMemoryChannelProperties) {
                this.properties = (InMemoryChannelProperties) properties;
            } else {
                this.properties = new InMemoryChannelProperties(
                        properties.getMaxSubscribers(),
                        properties.getMaxMessageSize(),
                        properties.getMessageTtl(),
                        properties.isDurable(),
                        properties.isAutoDelete());
            }
            
            this.pendingMessages = new CopyOnWriteArrayList<>();
            this.subscribers = new ConcurrentHashMap<>();
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
            return send(message, createDefaultDeliveryOptions());
        }
        
        @Override
        public MessageResult send(Message message, DeliveryOptions options) {
            try {
                // Validate message size if max size is set
                if (properties.getMaxMessageSize().isPresent()) {
                    // This is a simplistic size check - in a real implementation
                    // we would need to properly serialize the message to check its size
                    String serialized = message.getPayload().toString();
                    if (serialized.length() > properties.getMaxMessageSize().get()) {
                        return MessageResult.failure(
                                message.getId(),
                                "Message size exceeds channel limit",
                                "The message size of " + serialized.length() + " bytes exceeds the channel limit of " + properties.getMaxMessageSize().get() + " bytes");
                    }
                }
                
                // Check if TTL has expired (for testing purposes, we'll consider the message
                // already expired if the TTL is zero)
                if (options.getTimeToLive().isPresent() && options.getTimeToLive().get().isZero()) {
                    logger.warn("Message TTL already expired for message ID {}", message.getId());
                    return MessageResult.failure(
                            message.getId(),
                            "Message expired",
                            "Message time-to-live (TTL) has already expired");
                }
                
                // For QUEUE channel type, only deliver to one subscriber
                if (type == ChannelType.QUEUE && !subscribers.isEmpty()) {
                    // Select the subscriber with the lowest subscription ID (for deterministic testing)
                    String subscriberId = Collections.min(subscribers.keySet());
                    MessageHandler handler = subscribers.get(subscriberId);
                    
                    boolean delivered = handler.onMessage(message);
                    logger.debug("Message {} delivered to subscriber {}: {}", message.getId(), subscriberId, delivered);
                    
                    if (delivered) {
                        return MessageResult.success(message.getId(), "Message delivered to queue subscriber");
                    } else {
                        // Store the message for later retrieval
                        pendingMessages.add(message);
                        return MessageResult.success(message.getId(), "Message saved to queue for later delivery");
                    }
                } 
                // For TOPIC channel type, deliver to all subscribers
                else if (type == ChannelType.TOPIC) {
                    boolean anyDelivered = false;
                    
                    // Also check topic subscribers
                    List<MessageHandler> topicHandlers = topicSubscribers.getOrDefault(name, new ArrayList<>());
                    
                    // Combine channel-specific and topic subscribers
                    Map<String, MessageHandler> allSubscribers = new HashMap<>(subscribers);
                    for (int i = 0; i < topicHandlers.size(); i++) {
                        allSubscribers.put("topic-" + i, topicHandlers.get(i));
                    }
                    
                    // Deliver to all subscribers
                    for (Map.Entry<String, MessageHandler> entry : allSubscribers.entrySet()) {
                        try {
                            boolean delivered = entry.getValue().onMessage(message);
                            if (delivered) {
                                anyDelivered = true;
                            }
                            logger.debug("Message {} delivered to subscriber {}: {}", message.getId(), entry.getKey(), delivered);
                        } catch (Exception e) {
                            logger.error("Error delivering message to subscriber {}: {}", entry.getKey(), e.getMessage(), e);
                        }
                    }
                    
                    if (anyDelivered) {
                        return MessageResult.success(message.getId(), "Message delivered to topic subscribers");
                    } else {
                        // Store the message even if no subscribers (someone might subscribe later)
                        pendingMessages.add(message);
                        return MessageResult.success(message.getId(), "Message saved to topic for later delivery");
                    }
                } 
                // For REQUEST_REPLY channel type, delegate to a reply handler
                else if (type == ChannelType.REQUEST_REPLY) {
                    Map<String, Function<Message, Message>> handlers = replyHandlers.getOrDefault(name, new HashMap<>());
                    
                    if (handlers.isEmpty()) {
                        return MessageResult.failure(
                                message.getId(),
                                "No reply handlers registered",
                                "No reply handlers are registered for the channel " + name);
                    }
                    
                    // We'll just use the first handler for simplicity
                    String handlerId = handlers.keySet().iterator().next();
                    Function<Message, Message> handler = handlers.get(handlerId);
                    
                    try {
                        Message reply = handler.apply(message);
                        if (reply != null) {
                            // Store reply in reply results attribute
                            Map<String, Object> attributes = new HashMap<>();
                            attributes.put("reply", reply);
                            
                            return MessageResult.success(message.getId(), "Reply received", attributes);
                        } else {
                            return MessageResult.failure(
                                    message.getId(),
                                    "No reply received",
                                    "Handler didn't generate a reply");
                        }
                    } catch (Exception e) {
                        logger.error("Error processing request by handler {}: {}", handlerId, e.getMessage(), e);
                        return MessageResult.failure(
                                message.getId(),
                                "Error processing request",
                                "Handler threw an exception: " + e.getMessage());
                    }
                }
                
                // Default case (no subscribers or unknown channel type)
                pendingMessages.add(message);
                return MessageResult.success(message.getId(), "Message stored for later delivery");
            } catch (Exception e) {
                logger.error("Error processing message: {}", e.getMessage(), e);
                return MessageResult.failure(
                        message.getId(),
                        "Internal error",
                        "An error occurred while processing the message: " + e.getMessage());
            }
        }
        
        @Override
        public String subscribe(MessageHandler handler) {
            // Validate against max subscribers limit
            if (properties.getMaxSubscribers().isPresent() && subscribers.size() >= properties.getMaxSubscribers().get()) {
                logger.warn("Maximum subscriber limit reached for channel {}", name);
                return null;
            }
            
            // Generate a subscription ID
            String subscriptionId = UUID.randomUUID().toString();
            subscribers.put(subscriptionId, handler);
            
            logger.info("Subscriber {} registered for channel {}", subscriptionId, name);
            
            // Deliver any pending messages to the new subscriber
            for (Message message : pendingMessages) {
                try {
                    boolean delivered = handler.onMessage(message);
                    logger.debug("Pending message {} delivered to new subscriber {}: {}", message.getId(), subscriptionId, delivered);
                } catch (Exception e) {
                    logger.error("Error delivering pending message to new subscriber: {}", e.getMessage(), e);
                }
            }
            
            return subscriptionId;
        }
        
        @Override
        public boolean unsubscribe(String subscriptionId) {
            if (subscribers.containsKey(subscriptionId)) {
                subscribers.remove(subscriptionId);
                logger.info("Subscriber {} unregistered from channel {}", subscriptionId, name);
                
                // Check if channel should be auto-deleted
                if (properties.isAutoDelete() && subscribers.isEmpty() && topicSubscribers.getOrDefault(name, List.of()).isEmpty()) {
                    channels.remove(name);
                    logger.info("Channel {} auto-deleted as it has no subscribers", name);
                }
                
                return true;
            } else {
                logger.warn("No subscriber with ID {} found for channel {}", subscriptionId, name);
                return false;
            }
        }
        
        @Override
        public int getSubscriberCount() {
            return subscribers.size() + topicSubscribers.getOrDefault(name, List.of()).size();
        }
        
        @Override
        public ChannelProperties getProperties() {
            return properties;
        }
        
        @Override
        public long getPendingMessageCount() {
            return pendingMessages.size();
        }
    }
    
    /**
     * Implementation of the MessageBuilder interface.
     *
     * @param <T> The payload type
     */
    private class InMemoryMessageBuilder<T> implements MessageBuilder<T> {
        private String topic;
        private T payload;
        private final Class<T> payloadType;
        private final Map<String, Object> headers;
        
        /**
         * Constructs a new InMemoryMessageBuilder.
         *
         * @param payloadType The payload type
         */
        public InMemoryMessageBuilder(Class<T> payloadType) {
            this.payloadType = payloadType;
            this.headers = new HashMap<>();
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
            this.headers.put(key, value);
            return this;
        }
        
        @Override
        public MessageBuilder<T> headers(Map<String, Object> headers) {
            this.headers.putAll(headers);
            return this;
        }
        
        @Override
        public Message build() {
            String id = UUID.randomUUID().toString();
            return new InMemoryMessage(id, topic, payload, payloadType, headers);
        }
    }
    
    /**
     * Implementation of the DeliveryOptionsBuilder interface.
     */
    private class InMemoryDeliveryOptionsBuilder implements DeliveryOptionsBuilder {
        private Optional<Duration> timeToLive = Optional.empty();
        private int priority = 4; // Default medium priority
        private boolean persistent = false;
        private DeliveryMode deliveryMode = DeliveryMode.AT_MOST_ONCE;
        
        @Override
        public DeliveryOptionsBuilder timeToLive(Duration ttl) {
            this.timeToLive = Optional.ofNullable(ttl);
            return this;
        }
        
        @Override
        public DeliveryOptionsBuilder priority(int priority) {
            this.priority = Math.max(0, Math.min(9, priority)); // Clamp to 0-9
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
    
    /**
     * Implementation of the ChannelPropertiesBuilder interface.
     */
    private class InMemoryChannelPropertiesBuilder implements ChannelPropertiesBuilder {
        private Optional<Integer> maxSubscribers = Optional.empty();
        private Optional<Long> maxMessageSize = Optional.empty();
        private Optional<Duration> messageTtl = Optional.empty();
        private boolean durable = false;
        private boolean autoDelete = false;
        
        @Override
        public ChannelPropertiesBuilder maxSubscribers(int maxSubscribers) {
            this.maxSubscribers = Optional.of(maxSubscribers);
            return this;
        }
        
        @Override
        public ChannelPropertiesBuilder maxMessageSize(long maxMessageSize) {
            this.maxMessageSize = Optional.of(maxMessageSize);
            return this;
        }
        
        @Override
        public ChannelPropertiesBuilder messageTtl(Duration ttl) {
            this.messageTtl = Optional.ofNullable(ttl);
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
            return new InMemoryChannelProperties(maxSubscribers, maxMessageSize, messageTtl, durable, autoDelete);
        }
    }
    
    /**
     * Constructs a new MockMessagingAdapter.
     *
     * @param logger The logger
     */
    public MockMessagingAdapter(LoggerPort logger) {
        this.logger = logger;
        this.channels = new ConcurrentHashMap<>();
        this.topicSubscribers = new ConcurrentHashMap<>();
        this.replyHandlers = new ConcurrentHashMap<>();
        this.initialized = false;
        
        // Initialize the adapter
        initialize();
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
            logger.warn("Messaging system already initialized");
            return MessageResult.success(null, "Messaging system already initialized");
        }
        
        try {
            // Clear any existing channels and subscribers
            channels.clear();
            topicSubscribers.clear();
            replyHandlers.clear();
            
            initialized = true;
            logger.info("Messaging system initialized");
            
            return MessageResult.success(null, "Messaging system initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing messaging system: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error initializing messaging system", e.getMessage());
        }
    }
    
    @Override
    public MessageResult createChannel(String name, ChannelType type) {
        return createChannel(name, type, createDefaultChannelProperties());
    }
    
    @Override
    public MessageResult createChannel(String name, ChannelType type, ChannelProperties properties) {
        if (!initialized) {
            return MessageResult.failure(null, "Messaging system not initialized", "Initialize the messaging system before creating channels");
        }
        
        // Validate parameters
        if (name == null || name.trim().isEmpty()) {
            return MessageResult.failure(null, "Invalid channel name", "Channel name cannot be null or empty");
        }
        
        if (type == null) {
            return MessageResult.failure(null, "Invalid channel type", "Channel type cannot be null");
        }
        
        if (channels.containsKey(name)) {
            return MessageResult.failure(null, "Channel already exists", "A channel with the name '" + name + "' already exists");
        }
        
        try {
            // Create a new channel
            InMemoryChannel channel = new InMemoryChannel(name, type, properties);
            channels.put(name, channel);
            
            logger.info("Channel created: {} (type: {})", name, type);
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("channel", channel);
            
            return MessageResult.success(null, "Channel created successfully", attributes);
        } catch (Exception e) {
            logger.error("Error creating channel: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error creating channel", e.getMessage());
        }
    }
    
    @Override
    public MessageResult getChannel(String name) {
        if (!initialized) {
            return MessageResult.failure(null, "Messaging system not initialized", "Initialize the messaging system before getting channels");
        }
        
        // Validate parameters
        if (name == null || name.trim().isEmpty()) {
            return MessageResult.failure(null, "Invalid channel name", "Channel name cannot be null or empty");
        }
        
        if (!channels.containsKey(name)) {
            return MessageResult.failure(null, "Channel not found", "No channel found with the name '" + name + "'");
        }
        
        try {
            InMemoryChannel channel = channels.get(name);
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("channel", channel);
            
            return MessageResult.success(null, "Channel retrieved successfully", attributes);
        } catch (Exception e) {
            logger.error("Error retrieving channel: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error retrieving channel", e.getMessage());
        }
    }
    
    @Override
    public MessageResult deleteChannel(String name) {
        if (!initialized) {
            return MessageResult.failure(null, "Messaging system not initialized", "Initialize the messaging system before deleting channels");
        }
        
        // Validate parameters
        if (name == null || name.trim().isEmpty()) {
            return MessageResult.failure(null, "Invalid channel name", "Channel name cannot be null or empty");
        }
        
        if (!channels.containsKey(name)) {
            return MessageResult.failure(null, "Channel not found", "No channel found with the name '" + name + "'");
        }
        
        try {
            channels.remove(name);
            topicSubscribers.remove(name);
            replyHandlers.remove(name);
            
            logger.info("Channel deleted: {}", name);
            
            return MessageResult.success(null, "Channel deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting channel: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error deleting channel", e.getMessage());
        }
    }
    
    @Override
    public MessageResult getChannels() {
        if (!initialized) {
            return MessageResult.failure(null, "Messaging system not initialized", "Initialize the messaging system before getting channels");
        }
        
        try {
            List<ChannelInfo> channelInfos = channels.values().stream()
                    .collect(Collectors.toList());
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("channels", channelInfos);
            
            return MessageResult.success(null, "Channels retrieved successfully", attributes);
        } catch (Exception e) {
            logger.error("Error retrieving channels: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error retrieving channels", e.getMessage());
        }
    }
    
    @Override
    public MessageResult send(String topic, Message message) {
        return send(topic, message, createDefaultDeliveryOptions());
    }
    
    @Override
    public MessageResult send(String topic, Message message, DeliveryOptions options) {
        if (!initialized) {
            return MessageResult.failure(message.getId(), "Messaging system not initialized", "Initialize the messaging system before sending messages");
        }
        
        // Validate parameters
        if (topic == null || topic.trim().isEmpty()) {
            return MessageResult.failure(message.getId(), "Invalid topic", "Topic cannot be null or empty");
        }
        
        if (message == null) {
            return MessageResult.failure(null, "Invalid message", "Message cannot be null");
        }
        
        // If the channel doesn't exist, create it as a TOPIC type
        if (!channels.containsKey(topic)) {
            MessageResult result = createChannel(topic, ChannelType.TOPIC);
            if (!result.isSuccessful()) {
                return MessageResult.failure(message.getId(), "Error creating channel", "Could not create channel for topic: " + result.getMessage());
            }
        }
        
        try {
            InMemoryChannel channel = channels.get(topic);
            return channel.send(message, options);
        } catch (Exception e) {
            logger.error("Error sending message: {}", e.getMessage(), e);
            return MessageResult.failure(message.getId(), "Error sending message", e.getMessage());
        }
    }
    
    @Override
    public MessageResult subscribe(String topic, MessageHandler handler) {
        if (!initialized) {
            return MessageResult.failure(null, "Messaging system not initialized", "Initialize the messaging system before subscribing");
        }
        
        // Validate parameters
        if (topic == null || topic.trim().isEmpty()) {
            return MessageResult.failure(null, "Invalid topic", "Topic cannot be null or empty");
        }
        
        if (handler == null) {
            return MessageResult.failure(null, "Invalid handler", "Handler cannot be null");
        }
        
        try {
            String subscriptionId;
            
            // If the channel exists, subscribe directly to it
            if (channels.containsKey(topic)) {
                InMemoryChannel channel = channels.get(topic);
                subscriptionId = channel.subscribe(handler);
                
                if (subscriptionId == null) {
                    return MessageResult.failure(
                            null,
                            "Subscription failed",
                            "Could not subscribe to the channel. Check if the maximum number of subscribers has been reached.");
                }
            } else {
                // Otherwise, add to topic subscribers
                subscriptionId = UUID.randomUUID().toString();
                
                topicSubscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(handler);
                logger.info("Subscriber {} registered for topic {}", subscriptionId, topic);
            }
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("subscriptionId", subscriptionId);
            
            return MessageResult.success(subscriptionId, "Subscription successful", attributes);
        } catch (Exception e) {
            logger.error("Error subscribing to topic: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error subscribing to topic", e.getMessage());
        }
    }
    
    @Override
    public MessageResult unsubscribe(String topic, String subscriptionId) {
        if (!initialized) {
            return MessageResult.failure(subscriptionId, "Messaging system not initialized", "Initialize the messaging system before unsubscribing");
        }
        
        // Validate parameters
        if (topic == null || topic.trim().isEmpty()) {
            return MessageResult.failure(subscriptionId, "Invalid topic", "Topic cannot be null or empty");
        }
        
        if (subscriptionId == null || subscriptionId.trim().isEmpty()) {
            return MessageResult.failure(subscriptionId, "Invalid subscription ID", "Subscription ID cannot be null or empty");
        }
        
        try {
            // If the channel exists, unsubscribe directly from it
            if (channels.containsKey(topic)) {
                InMemoryChannel channel = channels.get(topic);
                boolean result = channel.unsubscribe(subscriptionId);
                
                if (result) {
                    return MessageResult.success(subscriptionId, "Unsubscription successful");
                } else {
                    return MessageResult.failure(
                            subscriptionId,
                            "Unsubscription failed",
                            "No subscription found with the given ID");
                }
            } else {
                // Otherwise, check topic subscribers
                // In a real implementation, we'd need to match the subscription ID to the handler
                // For now, we'll just remove a handler if any exist (simplistic)
                List<MessageHandler> handlers = topicSubscribers.get(topic);
                
                if (handlers != null && !handlers.isEmpty()) {
                    handlers.remove(0);
                    logger.info("Subscriber {} unregistered from topic {}", subscriptionId, topic);
                    
                    if (handlers.isEmpty()) {
                        topicSubscribers.remove(topic);
                    }
                    
                    return MessageResult.success(subscriptionId, "Unsubscription successful");
                } else {
                    return MessageResult.failure(
                            subscriptionId,
                            "Unsubscription failed",
                            "No subscription found for the topic");
                }
            }
        } catch (Exception e) {
            logger.error("Error unsubscribing from topic: {}", e.getMessage(), e);
            return MessageResult.failure(subscriptionId, "Error unsubscribing from topic", e.getMessage());
        }
    }
    
    @Override
    public MessageResult request(String topic, Message message) {
        return request(topic, message, Duration.ofSeconds(10)); // Default timeout
    }
    
    @Override
    public MessageResult request(String topic, Message message, Duration timeout) {
        if (!initialized) {
            return MessageResult.failure(message.getId(), "Messaging system not initialized", "Initialize the messaging system before sending requests");
        }
        
        // Validate parameters
        if (topic == null || topic.trim().isEmpty()) {
            return MessageResult.failure(message.getId(), "Invalid topic", "Topic cannot be null or empty");
        }
        
        if (message == null) {
            return MessageResult.failure(null, "Invalid message", "Message cannot be null");
        }
        
        // If the channel doesn't exist, create it as a REQUEST_REPLY type
        if (!channels.containsKey(topic)) {
            MessageResult result = createChannel(topic, ChannelType.REQUEST_REPLY);
            if (!result.isSuccessful()) {
                return MessageResult.failure(message.getId(), "Error creating channel", "Could not create channel for topic: " + result.getMessage());
            }
        }
        
        try {
            InMemoryChannel channel = channels.get(topic);
            if (channel.getType() != ChannelType.REQUEST_REPLY) {
                return MessageResult.failure(
                        message.getId(),
                        "Invalid channel type",
                        "Channel is not of REQUEST_REPLY type");
            }
            
            // Send the request message
            MessageResult sendResult = channel.send(message);
            
            if (!sendResult.isSuccessful()) {
                return sendResult;
            }
            
            // Check if a reply was received
            if (!sendResult.getAttributes().containsKey("reply")) {
                return MessageResult.failure(
                        message.getId(),
                        "No reply received",
                        "Request was sent but no reply was received");
            }
            
            Message reply = (Message) sendResult.getAttributes().get("reply");
            
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("reply", reply);
            
            return MessageResult.success(message.getId(), "Reply received", attributes);
        } catch (Exception e) {
            logger.error("Error sending request: {}", e.getMessage(), e);
            return MessageResult.failure(message.getId(), "Error sending request", e.getMessage());
        }
    }
    
    @Override
    public MessageResult registerReplyHandler(String topic, Function<Message, Message> handler) {
        if (!initialized) {
            return MessageResult.failure(null, "Messaging system not initialized", "Initialize the messaging system before registering handlers");
        }
        
        // Validate parameters
        if (topic == null || topic.trim().isEmpty()) {
            return MessageResult.failure(null, "Invalid topic", "Topic cannot be null or empty");
        }
        
        if (handler == null) {
            return MessageResult.failure(null, "Invalid handler", "Handler cannot be null");
        }
        
        try {
            // If the channel doesn't exist, create it as a REQUEST_REPLY type
            if (!channels.containsKey(topic)) {
                MessageResult result = createChannel(topic, ChannelType.REQUEST_REPLY);
                if (!result.isSuccessful()) {
                    return MessageResult.failure(null, "Error creating channel", "Could not create channel for topic: " + result.getMessage());
                }
            }
            
            InMemoryChannel channel = channels.get(topic);
            if (channel.getType() != ChannelType.REQUEST_REPLY) {
                return MessageResult.failure(
                        null,
                        "Invalid channel type",
                        "Channel is not of REQUEST_REPLY type");
            }
            
            // Register the handler
            String handlerId = UUID.randomUUID().toString();
            replyHandlers.computeIfAbsent(topic, k -> new ConcurrentHashMap<>()).put(handlerId, handler);
            
            logger.info("Reply handler {} registered for topic {}", handlerId, topic);
            
            return MessageResult.success(handlerId, "Reply handler registered successfully");
        } catch (Exception e) {
            logger.error("Error registering reply handler: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error registering reply handler", e.getMessage());
        }
    }
    
    @Override
    public MessageResult unregisterReplyHandler(String topic, String handlerId) {
        if (!initialized) {
            return MessageResult.failure(handlerId, "Messaging system not initialized", "Initialize the messaging system before unregistering handlers");
        }
        
        // Validate parameters
        if (topic == null || topic.trim().isEmpty()) {
            return MessageResult.failure(handlerId, "Invalid topic", "Topic cannot be null or empty");
        }
        
        if (handlerId == null || handlerId.trim().isEmpty()) {
            return MessageResult.failure(handlerId, "Invalid handler ID", "Handler ID cannot be null or empty");
        }
        
        try {
            // Check if the topic exists
            if (!replyHandlers.containsKey(topic)) {
                return MessageResult.failure(
                        handlerId,
                        "Topic not found",
                        "No handlers registered for the topic");
            }
            
            // Check if the handler exists
            Map<String, Function<Message, Message>> handlers = replyHandlers.get(topic);
            if (!handlers.containsKey(handlerId)) {
                return MessageResult.failure(
                        handlerId,
                        "Handler not found",
                        "No handler found with the given ID");
            }
            
            // Remove the handler
            handlers.remove(handlerId);
            logger.info("Reply handler {} unregistered from topic {}", handlerId, topic);
            
            if (handlers.isEmpty()) {
                replyHandlers.remove(topic);
            }
            
            return MessageResult.success(handlerId, "Reply handler unregistered successfully");
        } catch (Exception e) {
            logger.error("Error unregistering reply handler: {}", e.getMessage(), e);
            return MessageResult.failure(handlerId, "Error unregistering reply handler", e.getMessage());
        }
    }
    
    @Override
    public MessageResult shutdown() {
        if (!initialized) {
            logger.warn("Messaging system not initialized");
            return MessageResult.success(null, "Messaging system already shut down");
        }
        
        try {
            // Clear all channels and subscribers
            channels.clear();
            topicSubscribers.clear();
            replyHandlers.clear();
            
            initialized = false;
            logger.info("Messaging system shut down");
            
            return MessageResult.success(null, "Messaging system shut down successfully");
        } catch (Exception e) {
            logger.error("Error shutting down messaging system: {}", e.getMessage(), e);
            return MessageResult.failure(null, "Error shutting down messaging system", e.getMessage());
        }
    }
    
    /**
     * Creates default delivery options.
     *
     * @return The default delivery options
     */
    private DeliveryOptions createDefaultDeliveryOptions() {
        return new InMemoryDeliveryOptions(
                Optional.empty(),
                4, // Medium priority
                false,
                DeliveryMode.AT_MOST_ONCE);
    }
    
    /**
     * Creates default channel properties.
     *
     * @return The default channel properties
     */
    private ChannelProperties createDefaultChannelProperties() {
        return new InMemoryChannelProperties(
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                false,
                false);
    }
}