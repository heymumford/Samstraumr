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

package org.s8r.test.context;

import org.s8r.application.port.MessagingPort;
import org.s8r.application.port.MessagingPort.DeliveryOptions;
import org.s8r.application.port.MessagingPort.Message;
import org.s8r.application.port.MessagingPort.MessageResult;
import org.s8r.test.mock.MockLoggerAdapter;
import org.s8r.test.mock.MockMessagingAdapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Test context for messaging port tests.
 *
 * <p>This class manages the state of messaging tests, including the adapter instance,
 * test data, and operation results.
 */
public class MessagingPortTestContext {

    private final MockLoggerAdapter loggerAdapter;
    private final MockMessagingAdapter messagingAdapter;
    
    private String currentChannel;
    private MessagingPort.ChannelType currentChannelType;
    private Map<String, Object> currentChannelProperties;
    private String currentSubscriptionId;
    private String currentHandlerId;
    private Message currentMessage;
    private DeliveryOptions currentDeliveryOptions;
    private MessageResult lastResult;
    private List<Message> receivedMessages;
    private Message replyMessage;
    private Map<String, List<String>> subscriptionIds;
    private Map<String, List<String>> handlerIds;
    
    /**
     * Constructs a new MessagingPortTestContext.
     */
    public MessagingPortTestContext() {
        this.loggerAdapter = new MockLoggerAdapter();
        this.messagingAdapter = new MockMessagingAdapter(loggerAdapter);
        this.currentChannelProperties = new HashMap<>();
        this.receivedMessages = new ArrayList<>();
        this.subscriptionIds = new ConcurrentHashMap<>();
        this.handlerIds = new ConcurrentHashMap<>();
    }

    /**
     * Gets the messaging adapter.
     *
     * @return The messaging adapter
     */
    public MessagingPort getMessagingAdapter() {
        return messagingAdapter;
    }

    /**
     * Gets the logger adapter.
     *
     * @return The logger adapter
     */
    public MockLoggerAdapter getLoggerAdapter() {
        return loggerAdapter;
    }

    /**
     * Sets the current channel name.
     *
     * @param channelName The channel name
     */
    public void setCurrentChannel(String channelName) {
        this.currentChannel = channelName;
    }

    /**
     * Gets the current channel name.
     *
     * @return The current channel name
     */
    public String getCurrentChannel() {
        return currentChannel;
    }

    /**
     * Sets the current channel type.
     *
     * @param channelType The channel type
     */
    public void setCurrentChannelType(MessagingPort.ChannelType channelType) {
        this.currentChannelType = channelType;
    }

    /**
     * Gets the current channel type.
     *
     * @return The current channel type
     */
    public MessagingPort.ChannelType getCurrentChannelType() {
        return currentChannelType;
    }

    /**
     * Sets the current channel properties.
     *
     * @param properties The channel properties
     */
    public void setCurrentChannelProperties(Map<String, Object> properties) {
        this.currentChannelProperties = new HashMap<>(properties);
    }

    /**
     * Gets the current channel properties.
     *
     * @return The current channel properties
     */
    public Map<String, Object> getCurrentChannelProperties() {
        return currentChannelProperties;
    }

    /**
     * Sets the current subscription ID.
     *
     * @param subscriptionId The subscription ID
     */
    public void setCurrentSubscriptionId(String subscriptionId) {
        this.currentSubscriptionId = subscriptionId;
        
        // Track subscription for the current channel
        if (currentChannel != null && subscriptionId != null) {
            subscriptionIds.computeIfAbsent(currentChannel, k -> new ArrayList<>()).add(subscriptionId);
        }
    }

    /**
     * Gets the current subscription ID.
     *
     * @return The current subscription ID
     */
    public String getCurrentSubscriptionId() {
        return currentSubscriptionId;
    }

    /**
     * Sets the current handler ID.
     *
     * @param handlerId The handler ID
     */
    public void setCurrentHandlerId(String handlerId) {
        this.currentHandlerId = handlerId;
        
        // Track handler for the current channel
        if (currentChannel != null && handlerId != null) {
            handlerIds.computeIfAbsent(currentChannel, k -> new ArrayList<>()).add(handlerId);
        }
    }

    /**
     * Gets the current handler ID.
     *
     * @return The current handler ID
     */
    public String getCurrentHandlerId() {
        return currentHandlerId;
    }

    /**
     * Sets the current message.
     *
     * @param message The message
     */
    public void setCurrentMessage(Message message) {
        this.currentMessage = message;
    }

    /**
     * Gets the current message.
     *
     * @return The current message
     */
    public Message getCurrentMessage() {
        return currentMessage;
    }

    /**
     * Sets the current delivery options.
     *
     * @param options The delivery options
     */
    public void setCurrentDeliveryOptions(DeliveryOptions options) {
        this.currentDeliveryOptions = options;
    }

    /**
     * Gets the current delivery options.
     *
     * @return The current delivery options
     */
    public DeliveryOptions getCurrentDeliveryOptions() {
        return currentDeliveryOptions;
    }

    /**
     * Sets the last operation result.
     *
     * @param result The operation result
     */
    public void setLastResult(MessageResult result) {
        this.lastResult = result;
    }

    /**
     * Gets the last operation result.
     *
     * @return The last operation result
     */
    public MessageResult getLastResult() {
        return lastResult;
    }

    /**
     * Adds a received message.
     *
     * @param message The received message
     */
    public void addReceivedMessage(Message message) {
        this.receivedMessages.add(message);
    }

    /**
     * Gets the received messages.
     *
     * @return The received messages
     */
    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    /**
     * Sets the reply message.
     *
     * @param message The reply message
     */
    public void setReplyMessage(Message message) {
        this.replyMessage = message;
    }

    /**
     * Gets the reply message.
     *
     * @return The reply message
     */
    public Message getReplyMessage() {
        return replyMessage;
    }

    /**
     * Gets the subscription IDs for a channel.
     *
     * @param channelName The channel name
     * @return The subscription IDs
     */
    public List<String> getSubscriptionIds(String channelName) {
        return subscriptionIds.getOrDefault(channelName, new ArrayList<>());
    }

    /**
     * Gets the handler IDs for a channel.
     *
     * @param channelName The channel name
     * @return The handler IDs
     */
    public List<String> getHandlerIds(String channelName) {
        return handlerIds.getOrDefault(channelName, new ArrayList<>());
    }

    /**
     * Registers a reply handler for the current channel.
     *
     * @param handler The reply handler
     */
    public void registerReplyHandler(Function<Message, Message> handler) {
        if (currentChannel == null) {
            return;
        }
        
        MessageResult result = messagingAdapter.registerReplyHandler(currentChannel, handler);
        setLastResult(result);
        
        if (result.isSuccessful() && result.getMessageId().isPresent()) {
            setCurrentHandlerId(result.getMessageId().get());
        }
    }

    /**
     * Creates a standard test message.
     *
     * @param payload The message payload
     * @return The created message
     */
    public Message createTestMessage(String payload) {
        MessagingPort.MessageBuilder<String> builder = messagingAdapter.createMessageBuilder(String.class);
        
        if (currentChannel != null) {
            builder.topic(currentChannel);
        }
        
        Message message = builder
            .payload(payload)
            .header("test", true)
            .header("timestamp", System.currentTimeMillis())
            .build();
        
        setCurrentMessage(message);
        return message;
    }

    /**
     * Creates standard delivery options.
     *
     * @param ttlMillis The time-to-live in milliseconds
     * @param priority The message priority
     * @param persistent Whether the message is persistent
     * @param mode The delivery mode
     * @return The created delivery options
     */
    public DeliveryOptions createDeliveryOptions(long ttlMillis, int priority, boolean persistent, MessagingPort.DeliveryMode mode) {
        MessagingPort.DeliveryOptionsBuilder builder = messagingAdapter.createDeliveryOptionsBuilder();
        
        DeliveryOptions options = builder
            .timeToLive(Duration.ofMillis(ttlMillis))
            .priority(priority)
            .persistent(persistent)
            .deliveryMode(mode)
            .build();
        
        setCurrentDeliveryOptions(options);
        return options;
    }

    /**
     * Clears all test data.
     */
    public void clear() {
        currentChannel = null;
        currentChannelType = null;
        currentChannelProperties.clear();
        currentSubscriptionId = null;
        currentHandlerId = null;
        currentMessage = null;
        currentDeliveryOptions = null;
        lastResult = null;
        receivedMessages.clear();
        replyMessage = null;
        subscriptionIds.clear();
        handlerIds.clear();
        
        // Reset the messaging adapter
        messagingAdapter.shutdown();
        messagingAdapter.initialize();
    }
}