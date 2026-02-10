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

package org.s8r.test.steps;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.s8r.application.port.MessagingPort;
import org.s8r.application.port.MessagingPort.Channel;
import org.s8r.application.port.MessagingPort.ChannelType;
import org.s8r.application.port.MessagingPort.Message;
import org.s8r.application.port.MessagingPort.MessageResult;
import org.s8r.test.context.MessagingPortTestContext;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for testing the MessagingPort interface.
 */
public class MessagingPortSteps {

    private final MessagingPortTestContext context;
    private final List<Message> receivedMessages = new ArrayList<>();

    /**
     * Constructs a new MessagingPortSteps.
     *
     * @param context The test context
     */
    public MessagingPortSteps(MessagingPortTestContext context) {
        this.context = context;
    }

    @Given("a clean messaging environment")
    public void aCleanMessagingEnvironment() {
        context.clear();
    }

    @Given("the MessagingPort interface is initialized")
    public void theMessagingPortInterfaceIsInitialized() {
        MessageResult result = context.getMessagingAdapter().initialize();
        assertTrue(result.isSuccessful(), "Messaging system should initialize successfully: " + result.getMessage());
    }

    @Given("a {string} channel of type {string} exists")
    public void aChannelOfTypeExists(String channelName, String channelType) {
        context.setCurrentChannel(channelName);
        context.setCurrentChannelType(ChannelType.valueOf(channelType));

        // Create the channel
        MessageResult result = context.getMessagingAdapter().createChannel(
                channelName,
                ChannelType.valueOf(channelType));

        assertTrue(result.isSuccessful(), "Channel should be created successfully: " + result.getMessage());
    }

    @Given("I am subscribed to the {string} channel")
    public void iAmSubscribedToTheChannel(String channelName) {
        context.setCurrentChannel(channelName);

        // Create a message handler that stores received messages
        MessagingPort.MessageHandler handler = message -> {
            receivedMessages.add(message);
            context.addReceivedMessage(message);
            return true;
        };

        // Subscribe to the channel
        MessageResult result = context.getMessagingAdapter().subscribe(channelName, handler);
        assertTrue(result.isSuccessful(), "Subscription should be successful: " + result.getMessage());

        if (result.getAttributes() != null && result.getAttributes().containsKey("subscriptionId")) {
            String subscriptionId = (String) result.getAttributes().get("subscriptionId");
            context.setCurrentSubscriptionId(subscriptionId);
        } else if (result.getMessageId().isPresent()) {
            context.setCurrentSubscriptionId(result.getMessageId().get());
        }
    }

    @Given("a reply handler is registered for the {string} channel")
    public void aReplyHandlerIsRegisteredForTheChannel(String channelName) {
        context.setCurrentChannel(channelName);

        // Create a reply handler that returns a static response
        context.registerReplyHandler(request -> {
            // Create a response message
            MessagingPort.MessageBuilder<String> builder = context.getMessagingAdapter().createMessageBuilder(String.class);
            Message response = builder
                    .topic(request.getTopic())
                    .payload("Data Response for request: " + request.getPayload())
                    .header("requestId", request.getId())
                    .build();

            context.setReplyMessage(response);
            return response;
        });

        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Reply handler registration should be successful: " + result.getMessage());
    }

    @When("I create a {string} channel of type {string}")
    public void iCreateAChannelOfType(String channelName, String channelType) {
        context.setCurrentChannel(channelName);
        context.setCurrentChannelType(ChannelType.valueOf(channelType));

        // Create the channel
        MessageResult result = context.getMessagingAdapter().createChannel(
                channelName,
                ChannelType.valueOf(channelType));

        context.setLastResult(result);
    }

    @When("I create a message with payload {string}")
    public void iCreateAMessageWithPayload(String payload) {
        Message message = context.createTestMessage(payload);
        context.setCurrentMessage(message);
    }

    @When("I send the message to the {string} channel")
    public void iSendTheMessageToTheChannel(String channelName) {
        context.setCurrentChannel(channelName);
        Message message = context.getCurrentMessage();

        MessageResult result = context.getMessagingAdapter().send(channelName, message);
        context.setLastResult(result);
    }

    @When("I subscribe to the {string} channel")
    public void iSubscribeToTheChannel(String channelName) {
        context.setCurrentChannel(channelName);

        // Create a message handler that stores received messages
        AtomicReference<Message> receivedMessage = new AtomicReference<>();
        MessagingPort.MessageHandler handler = message -> {
            receivedMessage.set(message);
            context.addReceivedMessage(message);
            return true;
        };

        // Subscribe to the channel
        MessageResult result = context.getMessagingAdapter().subscribe(channelName, handler);
        context.setLastResult(result);

        if (result.getAttributes() != null && result.getAttributes().containsKey("subscriptionId")) {
            String subscriptionId = (String) result.getAttributes().get("subscriptionId");
            context.setCurrentSubscriptionId(subscriptionId);
        } else if (result.getMessageId().isPresent()) {
            context.setCurrentSubscriptionId(result.getMessageId().get());
        }
    }

    @When("a message with payload {string} is sent to the {string} channel")
    public void aMessageWithPayloadIsSentToTheChannel(String payload, String channelName) {
        Message message = context.createTestMessage(payload);
        MessageResult result = context.getMessagingAdapter().send(channelName, message);
        
        assertTrue(result.isSuccessful(), "Message should be sent successfully: " + result.getMessage());
        context.setLastResult(result);
    }

    @When("I unsubscribe from the {string} channel")
    public void iUnsubscribeFromTheChannel(String channelName) {
        String subscriptionId = context.getCurrentSubscriptionId();
        assertNotNull(subscriptionId, "Subscription ID should not be null");

        MessageResult result = context.getMessagingAdapter().unsubscribe(channelName, subscriptionId);
        context.setLastResult(result);
    }

    @When("I send a request with payload {string} to the {string} channel")
    public void iSendARequestWithPayloadToTheChannel(String payload, String channelName) {
        context.setCurrentChannel(channelName);
        Message message = context.createTestMessage(payload);
        context.setCurrentMessage(message);

        MessageResult result = context.getMessagingAdapter().request(channelName, message);
        context.setLastResult(result);
    }

    @When("I set the following delivery options:")
    public void iSetTheFollowingDeliveryOptions(DataTable dataTable) {
        Map<String, String> options = dataTable.asMap(String.class, String.class);

        // Parse options
        long ttl = Long.parseLong(options.getOrDefault("timeToLive", "0"));
        int priority = Integer.parseInt(options.getOrDefault("priority", "4"));
        boolean persistent = Boolean.parseBoolean(options.getOrDefault("persistent", "false"));
        MessagingPort.DeliveryMode mode = MessagingPort.DeliveryMode.valueOf(
                options.getOrDefault("mode", "AT_MOST_ONCE"));

        // Create delivery options
        MessagingPort.DeliveryOptions deliveryOptions = context.createDeliveryOptions(ttl, priority, persistent, mode);
        context.setCurrentDeliveryOptions(deliveryOptions);
    }

    @When("I send the message with delivery options to the {string} channel")
    public void iSendTheMessageWithDeliveryOptionsToTheChannel(String channelName) {
        context.setCurrentChannel(channelName);
        Message message = context.getCurrentMessage();
        MessagingPort.DeliveryOptions options = context.getCurrentDeliveryOptions();

        MessageResult result = context.getMessagingAdapter().send(channelName, message, options);
        context.setLastResult(result);
    }

    @When("I create a channel with the following properties:")
    public void iCreateAChannelWithTheFollowingProperties(DataTable dataTable) {
        Map<String, String> properties = dataTable.asMap(String.class, String.class);

        // Extract channel name and type
        String channelName = properties.get("name");
        ChannelType channelType = ChannelType.valueOf(properties.get("type"));

        context.setCurrentChannel(channelName);
        context.setCurrentChannelType(channelType);

        // Create channel properties builder
        MessagingPort.ChannelPropertiesBuilder builder = context.getMessagingAdapter().createChannelPropertiesBuilder();

        // Set properties
        if (properties.containsKey("maxSubscribers")) {
            builder.maxSubscribers(Integer.parseInt(properties.get("maxSubscribers")));
        }

        if (properties.containsKey("maxMessageSize")) {
            builder.maxMessageSize(Long.parseLong(properties.get("maxMessageSize")));
        }

        if (properties.containsKey("messageTtl")) {
            builder.messageTtl(Duration.ofMillis(Long.parseLong(properties.get("messageTtl"))));
        }

        if (properties.containsKey("durable")) {
            builder.durable(Boolean.parseBoolean(properties.get("durable")));
        }

        if (properties.containsKey("autoDelete")) {
            builder.autoDelete(Boolean.parseBoolean(properties.get("autoDelete")));
        }

        // Store properties in the context
        Map<String, Object> propertiesMap = new HashMap<>();
        propertiesMap.putAll(properties);
        context.setCurrentChannelProperties(propertiesMap);

        // Create the channel
        MessageResult result = context.getMessagingAdapter().createChannel(
                channelName,
                channelType,
                builder.build());

        context.setLastResult(result);
    }

    @When("I delete the {string} channel")
    public void iDeleteTheChannel(String channelName) {
        context.setCurrentChannel(channelName);

        MessageResult result = context.getMessagingAdapter().deleteChannel(channelName);
        context.setLastResult(result);
    }

    @Then("the channel should be created successfully")
    public void theChannelShouldBeCreatedSuccessfully() {
        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Channel creation should be successful: " + result.getMessage());
    }

    @Then("the channel should be retrievable")
    public void theChannelShouldBeRetrievable() {
        String channelName = context.getCurrentChannel();
        MessageResult result = context.getMessagingAdapter().getChannel(channelName);

        assertTrue(result.isSuccessful(), "Channel should be retrievable: " + result.getMessage());
        assertNotNull(result.getAttributes(), "Channel attributes should not be null");
        assertNotNull(result.getAttributes().get("channel"), "Channel should not be null");

        Channel channel = (Channel) result.getAttributes().get("channel");
        assertEquals(channelName, channel.getName(), "Channel name should match");
        assertEquals(context.getCurrentChannelType(), channel.getType(), "Channel type should match");
    }

    @Then("the channel should have {int} subscribers")
    public void theChannelShouldHaveSubscribers(int expectedCount) {
        String channelName = context.getCurrentChannel();
        MessageResult result = context.getMessagingAdapter().getChannel(channelName);

        assertTrue(result.isSuccessful(), "Channel should be retrievable: " + result.getMessage());
        assertNotNull(result.getAttributes(), "Channel attributes should not be null");
        assertNotNull(result.getAttributes().get("channel"), "Channel should not be null");

        Channel channel = (Channel) result.getAttributes().get("channel");
        assertEquals(expectedCount, channel.getSubscriberCount(), "Subscriber count should match");
    }

    @Then("the send operation should be successful")
    public void theSendOperationShouldBeSuccessful() {
        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Send operation should be successful: " + result.getMessage());
    }

    @Then("the subscription should be successful")
    public void theSubscriptionShouldBeSuccessful() {
        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Subscription should be successful: " + result.getMessage());
        assertNotNull(context.getCurrentSubscriptionId(), "Subscription ID should not be null");
    }

    @Then("I should receive the message with payload {string}")
    public void iShouldReceiveTheMessageWithPayload(String expectedPayload) {
        // Wait a bit for the message to be delivered (in a real system, this might use a latch or condition)
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        List<Message> messages = context.getReceivedMessages();
        assertFalse(messages.isEmpty(), "Should have received at least one message");

        boolean found = false;
        for (Message message : messages) {
            if (expectedPayload.equals(message.getPayload())) {
                found = true;
                break;
            }
        }

        assertTrue(found, "Should have received a message with payload: " + expectedPayload);
    }

    @Then("the unsubscription should be successful")
    public void theUnsubscriptionShouldBeSuccessful() {
        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Unsubscription should be successful: " + result.getMessage());
    }

    @Then("I should receive a reply")
    public void iShouldReceiveAReply() {
        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Request should be successful: " + result.getMessage());
        assertNotNull(result.getAttributes(), "Reply attributes should not be null");
        assertNotNull(result.getAttributes().get("reply"), "Reply should not be null");
    }

    @Then("the reply payload should contain {string}")
    public void theReplyPayloadShouldContain(String expectedContent) {
        MessageResult result = context.getLastResult();
        assertNotNull(result.getAttributes(), "Reply attributes should not be null");

        Message reply = (Message) result.getAttributes().get("reply");
        assertNotNull(reply, "Reply should not be null");
        assertNotNull(reply.getPayload(), "Reply payload should not be null");
        assertTrue(reply.getPayload().toString().contains(expectedContent),
                "Reply payload should contain: " + expectedContent);
    }

    @Then("the channel properties should match the specified values")
    public void theChannelPropertiesShouldMatchTheSpecifiedValues() {
        String channelName = context.getCurrentChannel();
        MessageResult result = context.getMessagingAdapter().getChannel(channelName);

        assertTrue(result.isSuccessful(), "Channel should be retrievable: " + result.getMessage());
        assertNotNull(result.getAttributes(), "Channel attributes should not be null");
        assertNotNull(result.getAttributes().get("channel"), "Channel should not be null");

        Channel channel = (Channel) result.getAttributes().get("channel");
        MessagingPort.ChannelProperties properties = channel.getProperties();

        Map<String, Object> expectedProperties = context.getCurrentChannelProperties();

        if (expectedProperties.containsKey("maxSubscribers")) {
            int expected = Integer.parseInt(expectedProperties.get("maxSubscribers").toString());
            assertEquals(Optional.of(expected), properties.getMaxSubscribers(), "maxSubscribers should match");
        }

        if (expectedProperties.containsKey("maxMessageSize")) {
            long expected = Long.parseLong(expectedProperties.get("maxMessageSize").toString());
            assertEquals(Optional.of(expected), properties.getMaxMessageSize(), "maxMessageSize should match");
        }

        if (expectedProperties.containsKey("messageTtl")) {
            long expected = Long.parseLong(expectedProperties.get("messageTtl").toString());
            assertEquals(Duration.ofMillis(expected), properties.getMessageTtl().orElse(null),
                    "messageTtl should match");
        }

        if (expectedProperties.containsKey("durable")) {
            boolean expected = Boolean.parseBoolean(expectedProperties.get("durable").toString());
            assertEquals(expected, properties.isDurable(), "durable should match");
        }

        if (expectedProperties.containsKey("autoDelete")) {
            boolean expected = Boolean.parseBoolean(expectedProperties.get("autoDelete").toString());
            assertEquals(expected, properties.isAutoDelete(), "autoDelete should match");
        }
    }

    @Then("the delete operation should be successful")
    public void theDeleteOperationShouldBeSuccessful() {
        MessageResult result = context.getLastResult();
        assertTrue(result.isSuccessful(), "Delete operation should be successful: " + result.getMessage());
    }

    @Then("the channel should no longer exist")
    public void theChannelShouldNoLongerExist() {
        String channelName = context.getCurrentChannel();
        MessageResult result = context.getMessagingAdapter().getChannel(channelName);

        assertFalse(result.isSuccessful(), "Channel should not exist: " + result.getMessage());
    }
}