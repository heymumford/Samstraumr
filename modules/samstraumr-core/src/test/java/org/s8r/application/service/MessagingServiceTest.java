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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MessagingPort;
import org.s8r.test.annotation.UnitTest;

@UnitTest
public class MessagingServiceTest {

  private MessagingPort mockMessagingPort;
  private LoggerPort mockLogger;
  private MessagingService service;

  @BeforeEach
  void setUp() {
    mockMessagingPort = mock(MessagingPort.class);
    mockLogger = mock(LoggerPort.class);
    service = new MessagingService(mockMessagingPort, mockLogger);
  }

  @Test
  void initialize_shouldCallPortInitialize() {
    // Arrange
    when(mockMessagingPort.initialize())
        .thenReturn(MessagingPort.MessageResult.success(null, "Success"));

    // Act
    boolean result = service.initialize();

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).initialize();
    verify(mockLogger).info(anyString());
    verify(mockLogger, never()).error(anyString(), anyString());
  }

  @Test
  void initialize_shouldLogErrorOnFailure() {
    // Arrange
    when(mockMessagingPort.initialize())
        .thenReturn(
            MessagingPort.MessageResult.failure(null, "Initialization failed", "Test error"));

    // Act
    boolean result = service.initialize();

    // Assert
    assertFalse(result);
    verify(mockMessagingPort).initialize();
    verify(mockLogger).error(contains("Failed to initialize"), eq("Test error"));
  }

  @Test
  void createChannel_shouldReturnChannelOnSuccess() {
    // Arrange
    String name = "testChannel";
    MessagingPort.ChannelType type = MessagingPort.ChannelType.TOPIC;
    MessagingPort.Channel mockChannel = mock(MessagingPort.Channel.class);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("channel", mockChannel);

    when(mockMessagingPort.createChannel(name, type))
        .thenReturn(
            MessagingPort.MessageResult.success(null, "Channel created successfully", attributes));

    // Act
    Optional<MessagingPort.Channel> result = service.createChannel(name, type);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(mockChannel, result.get());
    verify(mockMessagingPort).createChannel(name, type);
  }

  @Test
  void createChannel_shouldReturnEmptyAndLogOnFailure() {
    // Arrange
    String name = "testChannel";
    MessagingPort.ChannelType type = MessagingPort.ChannelType.TOPIC;
    when(mockMessagingPort.createChannel(name, type))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Failed to create channel", "Channel already exists"));

    // Act
    Optional<MessagingPort.Channel> result = service.createChannel(name, type);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).createChannel(name, type);
    verify(mockLogger).warn(anyString(), eq(name), anyString());
  }

  @Test
  void createChannel_withPropertiesShouldReturnChannelOnSuccess() {
    // Arrange
    String name = "testChannel";
    MessagingPort.ChannelType type = MessagingPort.ChannelType.TOPIC;
    MessagingPort.ChannelProperties properties = mock(MessagingPort.ChannelProperties.class);
    MessagingPort.Channel mockChannel = mock(MessagingPort.Channel.class);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("channel", mockChannel);

    when(mockMessagingPort.createChannel(name, type, properties))
        .thenReturn(
            MessagingPort.MessageResult.success(null, "Channel created successfully", attributes));

    // Act
    Optional<MessagingPort.Channel> result = service.createChannel(name, type, properties);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(mockChannel, result.get());
    verify(mockMessagingPort).createChannel(name, type, properties);
  }

  @Test
  void getChannel_shouldReturnChannelOnSuccess() {
    // Arrange
    String name = "testChannel";
    MessagingPort.Channel mockChannel = mock(MessagingPort.Channel.class);
    Map<String, Object> attributes = new HashMap<>();
    attributes.put("channel", mockChannel);

    when(mockMessagingPort.getChannel(name))
        .thenReturn(MessagingPort.MessageResult.success(null, "Channel found", attributes));

    // Act
    Optional<MessagingPort.Channel> result = service.getChannel(name);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(mockChannel, result.get());
    verify(mockMessagingPort).getChannel(name);
  }

  @Test
  void getChannel_shouldReturnEmptyOnNotFound() {
    // Arrange
    String name = "nonExistentChannel";
    when(mockMessagingPort.getChannel(name))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Channel not found", "No channel exists with name 'nonExistentChannel'"));

    // Act
    Optional<MessagingPort.Channel> result = service.getChannel(name);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).getChannel(name);
    verify(mockLogger, never()).warn(anyString(), anyString(), anyString());
  }

  @Test
  void getChannel_shouldLogWarningOnOtherErrors() {
    // Arrange
    String name = "errorChannel";
    when(mockMessagingPort.getChannel(name))
        .thenReturn(MessagingPort.MessageResult.failure(null, "Error", "System error"));

    // Act
    Optional<MessagingPort.Channel> result = service.getChannel(name);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).getChannel(name);
    verify(mockLogger).warn(anyString(), eq(name), anyString());
  }

  @Test
  void deleteChannel_shouldReturnTrueOnSuccess() {
    // Arrange
    String name = "testChannel";
    when(mockMessagingPort.deleteChannel(name))
        .thenReturn(MessagingPort.MessageResult.success(null, "Channel deleted successfully"));

    // Act
    boolean result = service.deleteChannel(name);

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).deleteChannel(name);
  }

  @Test
  void deleteChannel_shouldReturnFalseAndLogOnFailure() {
    // Arrange
    String name = "nonExistentChannel";
    when(mockMessagingPort.deleteChannel(name))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Failed to delete channel", "Channel not found"));

    // Act
    boolean result = service.deleteChannel(name);

    // Assert
    assertFalse(result);
    verify(mockMessagingPort).deleteChannel(name);
    verify(mockLogger).warn(anyString(), eq(name), anyString());
  }

  @Test
  void getChannels_shouldReturnChannelsOnSuccess() {
    // Arrange
    MessagingPort.ChannelInfo mockChannel1 = mock(MessagingPort.ChannelInfo.class);
    MessagingPort.ChannelInfo mockChannel2 = mock(MessagingPort.ChannelInfo.class);
    List<MessagingPort.ChannelInfo> channels = new ArrayList<>();
    channels.add(mockChannel1);
    channels.add(mockChannel2);

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("channels", channels);

    when(mockMessagingPort.getChannels())
        .thenReturn(
            MessagingPort.MessageResult.success(
                null, "Channels retrieved successfully", attributes));

    // Act
    List<MessagingPort.ChannelInfo> result = service.getChannels();

    // Assert
    assertEquals(2, result.size());
    verify(mockMessagingPort).getChannels();
  }

  @Test
  void getChannels_shouldReturnEmptyListAndLogOnFailure() {
    // Arrange
    when(mockMessagingPort.getChannels())
        .thenReturn(
            MessagingPort.MessageResult.failure(null, "Error getting channels", "System error"));

    // Act
    List<MessagingPort.ChannelInfo> result = service.getChannels();

    // Assert
    assertTrue(result.isEmpty());
    verify(mockMessagingPort).getChannels();
    verify(mockLogger).warn(anyString(), anyString());
  }

  @Test
  void send_shouldReturnTrueOnSuccess() {
    // Arrange
    String topic = "testTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    when(mockMessage.getId()).thenReturn("msg123");

    when(mockMessagingPort.send(topic, mockMessage))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Message sent successfully"));

    // Act
    boolean result = service.send(topic, mockMessage);

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).send(topic, mockMessage);
  }

  @Test
  void send_shouldReturnFalseAndLogOnFailure() {
    // Arrange
    String topic = "testTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    when(mockMessage.getId()).thenReturn("msg123");

    when(mockMessagingPort.send(topic, mockMessage))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                "msg123", "Failed to send message", "No subscribers"));

    // Act
    boolean result = service.send(topic, mockMessage);

    // Assert
    assertFalse(result);
    verify(mockMessagingPort).send(topic, mockMessage);
    verify(mockLogger).warn(anyString(), eq(topic), anyString());
  }

  @Test
  void send_withOptionsShouldDelegateToPort() {
    // Arrange
    String topic = "testTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    MessagingPort.DeliveryOptions mockOptions = mock(MessagingPort.DeliveryOptions.class);
    when(mockMessage.getId()).thenReturn("msg123");

    when(mockMessagingPort.send(topic, mockMessage, mockOptions))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Message sent successfully"));

    // Act
    boolean result = service.send(topic, mockMessage, mockOptions);

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).send(topic, mockMessage, mockOptions);
  }

  @Test
  void subscribe_shouldReturnSubscriptionIdOnSuccess() {
    // Arrange
    String topic = "testTopic";
    MessagingPort.MessageHandler mockHandler = mock(MessagingPort.MessageHandler.class);
    String subscriptionId = "sub123";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("subscriptionId", subscriptionId);

    when(mockMessagingPort.subscribe(topic, mockHandler))
        .thenReturn(
            MessagingPort.MessageResult.success(null, "Subscribed successfully", attributes));

    // Act
    Optional<String> result = service.subscribe(topic, mockHandler);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(subscriptionId, result.get());
    verify(mockMessagingPort).subscribe(topic, mockHandler);
  }

  @Test
  void subscribe_shouldReturnEmptyAndLogOnFailure() {
    // Arrange
    String topic = "testTopic";
    MessagingPort.MessageHandler mockHandler = mock(MessagingPort.MessageHandler.class);

    when(mockMessagingPort.subscribe(topic, mockHandler))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Failed to subscribe", "Maximum subscribers reached"));

    // Act
    Optional<String> result = service.subscribe(topic, mockHandler);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).subscribe(topic, mockHandler);
    verify(mockLogger).warn(anyString(), eq(topic), anyString());
  }

  @Test
  void unsubscribe_shouldReturnTrueOnSuccess() {
    // Arrange
    String topic = "testTopic";
    String subscriptionId = "sub123";

    when(mockMessagingPort.unsubscribe(topic, subscriptionId))
        .thenReturn(MessagingPort.MessageResult.success(null, "Unsubscribed successfully"));

    // Act
    boolean result = service.unsubscribe(topic, subscriptionId);

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).unsubscribe(topic, subscriptionId);
  }

  @Test
  void unsubscribe_shouldReturnFalseAndLogOnFailure() {
    // Arrange
    String topic = "testTopic";
    String subscriptionId = "invalidSub";

    when(mockMessagingPort.unsubscribe(topic, subscriptionId))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Failed to unsubscribe", "Subscription not found"));

    // Act
    boolean result = service.unsubscribe(topic, subscriptionId);

    // Assert
    assertFalse(result);
    verify(mockMessagingPort).unsubscribe(topic, subscriptionId);
    verify(mockLogger).warn(anyString(), eq(topic), anyString());
  }

  @Test
  void request_shouldReturnReplyOnSuccess() {
    // Arrange
    String topic = "requestTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    MessagingPort.Message mockReply = mock(MessagingPort.Message.class);
    when(mockMessage.getId()).thenReturn("msg123");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("reply", mockReply);

    when(mockMessagingPort.request(eq(topic), eq(mockMessage), any(Duration.class)))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Reply received", attributes));

    // Act
    Optional<MessagingPort.Message> result = service.request(topic, mockMessage);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(mockReply, result.get());
    verify(mockMessagingPort).request(eq(topic), eq(mockMessage), any(Duration.class));
  }

  @Test
  void request_shouldReturnEmptyOnTimeout() {
    // Arrange
    String topic = "requestTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    when(mockMessage.getId()).thenReturn("msg123");

    when(mockMessagingPort.request(eq(topic), eq(mockMessage), any(Duration.class)))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                "msg123", "Request timed out", "No reply received within timeout period"));

    // Act
    Optional<MessagingPort.Message> result = service.request(topic, mockMessage);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).request(eq(topic), eq(mockMessage), any(Duration.class));
    verify(mockLogger, never()).warn(anyString(), anyString(), anyString());
  }

  @Test
  void request_shouldLogWarningOnOtherErrors() {
    // Arrange
    String topic = "requestTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    when(mockMessage.getId()).thenReturn("msg123");

    when(mockMessagingPort.request(eq(topic), eq(mockMessage), any(Duration.class)))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                "msg123", "Request failed", "No handler registered"));

    // Act
    Optional<MessagingPort.Message> result = service.request(topic, mockMessage);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).request(eq(topic), eq(mockMessage), any(Duration.class));
    verify(mockLogger).warn(anyString(), eq(topic), anyString());
  }

  @Test
  void requestAsync_shouldReturnCompletableFuture() throws Exception {
    // Arrange
    String topic = "requestTopic";
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);
    MessagingPort.Message mockReply = mock(MessagingPort.Message.class);
    when(mockMessage.getId()).thenReturn("msg123");

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("reply", mockReply);

    when(mockMessagingPort.request(eq(topic), eq(mockMessage), any(Duration.class)))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Reply received", attributes));

    // Act
    CompletableFuture<MessagingPort.Message> future =
        service.requestAsync(topic, mockMessage, Duration.ofSeconds(5));
    MessagingPort.Message result = future.get(1, TimeUnit.SECONDS); // Short timeout for test

    // Assert
    assertEquals(mockReply, result);
  }

  @Test
  void registerReplyHandler_shouldReturnHandlerIdOnSuccess() {
    // Arrange
    String topic = "replyTopic";
    Function<MessagingPort.Message, MessagingPort.Message> handler =
        msg -> mock(MessagingPort.Message.class);
    String handlerId = "handler123";

    Map<String, Object> attributes = new HashMap<>();
    attributes.put("handlerId", handlerId);

    when(mockMessagingPort.registerReplyHandler(topic, handler))
        .thenReturn(
            MessagingPort.MessageResult.success(
                null, "Handler registered successfully", attributes));

    // Act
    Optional<String> result = service.registerReplyHandler(topic, handler);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(handlerId, result.get());
    verify(mockMessagingPort).registerReplyHandler(topic, handler);
  }

  @Test
  void registerReplyHandler_shouldReturnEmptyAndLogOnFailure() {
    // Arrange
    String topic = "replyTopic";
    Function<MessagingPort.Message, MessagingPort.Message> handler =
        msg -> mock(MessagingPort.Message.class);

    when(mockMessagingPort.registerReplyHandler(topic, handler))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Failed to register handler", "System error"));

    // Act
    Optional<String> result = service.registerReplyHandler(topic, handler);

    // Assert
    assertFalse(result.isPresent());
    verify(mockMessagingPort).registerReplyHandler(topic, handler);
    verify(mockLogger).warn(anyString(), eq(topic), anyString());
  }

  @Test
  void unregisterReplyHandler_shouldReturnTrueOnSuccess() {
    // Arrange
    String topic = "replyTopic";
    String handlerId = "handler123";

    when(mockMessagingPort.unregisterReplyHandler(topic, handlerId))
        .thenReturn(MessagingPort.MessageResult.success(null, "Handler unregistered successfully"));

    // Act
    boolean result = service.unregisterReplyHandler(topic, handlerId);

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).unregisterReplyHandler(topic, handlerId);
  }

  @Test
  void unregisterReplyHandler_shouldReturnFalseAndLogOnFailure() {
    // Arrange
    String topic = "replyTopic";
    String handlerId = "invalidHandler";

    when(mockMessagingPort.unregisterReplyHandler(topic, handlerId))
        .thenReturn(
            MessagingPort.MessageResult.failure(
                null, "Failed to unregister handler", "Handler not found"));

    // Act
    boolean result = service.unregisterReplyHandler(topic, handlerId);

    // Assert
    assertFalse(result);
    verify(mockMessagingPort).unregisterReplyHandler(topic, handlerId);
    verify(mockLogger).warn(anyString(), eq(topic), anyString());
  }

  @Test
  void createTextMessage_shouldCreateMessageWithStringPayload() {
    // Arrange
    String topic = "testTopic";
    String payload = "Test Message";
    MessagingPort.MessageBuilder<String> mockBuilder = mock(MessagingPort.MessageBuilder.class);
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);

    when(mockMessagingPort.createMessageBuilder(String.class)).thenReturn(mockBuilder);
    when(mockBuilder.topic(topic)).thenReturn(mockBuilder);
    when(mockBuilder.payload(payload)).thenReturn(mockBuilder);
    when(mockBuilder.build()).thenReturn(mockMessage);

    // Act
    MessagingPort.Message result = service.createTextMessage(topic, payload);

    // Assert
    assertEquals(mockMessage, result);
    verify(mockMessagingPort).createMessageBuilder(String.class);
    verify(mockBuilder).topic(topic);
    verify(mockBuilder).payload(payload);
    verify(mockBuilder).build();
  }

  @Test
  void createObjectMessage_shouldCreateMessageWithObjectPayload() {
    // Arrange
    String topic = "testTopic";
    TestObject payload = new TestObject("test");
    MessagingPort.MessageBuilder<TestObject> mockBuilder = mock(MessagingPort.MessageBuilder.class);
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);

    when(mockMessagingPort.createMessageBuilder(TestObject.class)).thenReturn(mockBuilder);
    when(mockBuilder.topic(topic)).thenReturn(mockBuilder);
    when(mockBuilder.payload(payload)).thenReturn(mockBuilder);
    when(mockBuilder.build()).thenReturn(mockMessage);

    // Act
    MessagingPort.Message result = service.createObjectMessage(topic, payload);

    // Assert
    assertEquals(mockMessage, result);
    verify(mockMessagingPort).createMessageBuilder(TestObject.class);
    verify(mockBuilder).topic(topic);
    verify(mockBuilder).payload(payload);
    verify(mockBuilder).build();
  }

  @Test
  void publishEvent_shouldSendEventAsMessage() {
    // Arrange
    String topic = "eventTopic";
    TestObject event = new TestObject("event");
    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);

    // Mock the createObjectMessage method using spy
    MessagingService spy = spy(service);
    doReturn(mockMessage).when(spy).createObjectMessage(topic, event);

    when(mockMessagingPort.send(topic, mockMessage))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Message sent successfully"));

    // Act
    boolean result = spy.publishEvent(topic, event);

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).send(topic, mockMessage);
  }

  @Test
  void broadcast_shouldSendToMultipleTopics() {
    // Arrange
    Set<String> topics = new HashSet<>();
    topics.add("topic1");
    topics.add("topic2");
    topics.add("topic3");

    MessagingPort.Message mockMessage = mock(MessagingPort.Message.class);

    when(mockMessagingPort.send("topic1", mockMessage))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Message sent successfully"));
    when(mockMessagingPort.send("topic2", mockMessage))
        .thenReturn(MessagingPort.MessageResult.success("msg123", "Message sent successfully"));
    when(mockMessagingPort.send("topic3", mockMessage))
        .thenReturn(
            MessagingPort.MessageResult.failure("msg123", "Failed to send", "No subscribers"));

    // Act
    int result = service.broadcast(topics, mockMessage);

    // Assert
    assertEquals(2, result);
    verify(mockMessagingPort, times(3)).send(anyString(), eq(mockMessage));
  }

  @Test
  void shutdown_shouldCallPortShutdown() {
    // Arrange
    when(mockMessagingPort.shutdown())
        .thenReturn(
            MessagingPort.MessageResult.success(null, "Messaging system shutdown successfully"));

    // Act
    boolean result = service.shutdown();

    // Assert
    assertTrue(result);
    verify(mockMessagingPort).shutdown();
    verify(mockLogger).info(anyString());
    verify(mockLogger, never()).error(anyString(), anyString());
  }

  @Test
  void shutdown_shouldLogErrorOnFailure() {
    // Arrange
    when(mockMessagingPort.shutdown())
        .thenReturn(MessagingPort.MessageResult.failure(null, "Shutdown failed", "Test error"));

    // Act
    boolean result = service.shutdown();

    // Assert
    assertFalse(result);
    verify(mockMessagingPort).shutdown();
    verify(mockLogger).error(contains("Failed to shut down"), eq("Test error"));
  }

  // Helper class for testing object messages
  private static class TestObject {
    private final String value;

    public TestObject(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }
}
