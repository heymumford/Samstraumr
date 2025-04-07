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

package org.s8r.infrastructure.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the DataFlowComponentAdapter class.
 * 
 * <p>These tests verify that the adapter correctly implements the DataFlowComponentPort interface
 * and properly delegates to the DataFlowEventPort implementation.
 */
@UnitTest
public class DataFlowComponentAdapterTest {

  private DataFlowEventPort mockDataFlowEventPort;
  private LoggerPort mockLogger;
  private ComponentId componentId;
  private DataFlowComponentAdapter adapter;
  private Consumer<ComponentDataEvent> mockHandler;

  @BeforeEach
  void setUp() {
    mockDataFlowEventPort = mock(DataFlowEventPort.class);
    mockLogger = mock(LoggerPort.class);
    mockHandler = mock(Consumer.class);
    
    componentId = mock(ComponentId.class);
    when(componentId.getIdString()).thenReturn("test-component-id");
    
    adapter = new DataFlowComponentAdapter(componentId, mockDataFlowEventPort, mockLogger);
  }

  @Test
  @DisplayName("subscribe should delegate to the event port and track subscriptions")
  void subscribe_shouldDelegateAndTrackSubscriptions() {
    // Act
    boolean result = adapter.subscribe("test-channel", mockHandler);
    
    // Assert
    assertTrue(result);
    verify(mockDataFlowEventPort).subscribe(eq(componentId), eq("test-channel"), eq(mockHandler));
    assertEquals(1, adapter.getSubscriptions().size());
    assertTrue(adapter.getSubscriptions().contains("test-channel"));
  }

  @Test
  @DisplayName("subscribe should handle null or empty channel")
  void subscribe_shouldHandleNullOrEmptyChannel() {
    // Act
    boolean result1 = adapter.subscribe(null, mockHandler);
    boolean result2 = adapter.subscribe("", mockHandler);
    
    // Assert
    assertFalse(result1);
    assertFalse(result2);
    verify(mockDataFlowEventPort, never()).subscribe(any(), any(), any());
    assertTrue(adapter.getSubscriptions().isEmpty());
  }

  @Test
  @DisplayName("subscribe should handle null handler")
  void subscribe_shouldHandleNullHandler() {
    // Act
    boolean result = adapter.subscribe("test-channel", null);
    
    // Assert
    assertFalse(result);
    verify(mockDataFlowEventPort, never()).subscribe(any(), any(), any());
    assertTrue(adapter.getSubscriptions().isEmpty());
  }

  @Test
  @DisplayName("subscribe should handle exceptions")
  void subscribe_shouldHandleExceptions() {
    // Arrange
    doThrow(new RuntimeException("Test error")).when(mockDataFlowEventPort)
        .subscribe(any(), any(), any());
    
    // Act
    boolean result = adapter.subscribe("test-channel", mockHandler);
    
    // Assert
    assertFalse(result);
    verify(mockLogger).error(anyString(), anyString(), anyString(), anyString(), any());
    assertTrue(adapter.getSubscriptions().isEmpty());
  }

  @Test
  @DisplayName("unsubscribe should delegate to the event port and update subscriptions")
  void unsubscribe_shouldDelegateAndUpdateSubscriptions() {
    // Arrange
    adapter.subscribe("test-channel", mockHandler);
    
    // Act
    boolean result = adapter.unsubscribe("test-channel");
    
    // Assert
    assertTrue(result);
    verify(mockDataFlowEventPort).unsubscribe(eq(componentId), eq("test-channel"));
    assertTrue(adapter.getSubscriptions().isEmpty());
  }

  @Test
  @DisplayName("unsubscribe should handle null or empty channel")
  void unsubscribe_shouldHandleNullOrEmptyChannel() {
    // Act
    boolean result1 = adapter.unsubscribe(null);
    boolean result2 = adapter.unsubscribe("");
    
    // Assert
    assertFalse(result1);
    assertFalse(result2);
    verify(mockDataFlowEventPort, never()).unsubscribe(any(), any());
  }

  @Test
  @DisplayName("unsubscribe should handle exceptions")
  void unsubscribe_shouldHandleExceptions() {
    // Arrange
    adapter.subscribe("test-channel", mockHandler);
    doThrow(new RuntimeException("Test error")).when(mockDataFlowEventPort)
        .unsubscribe(any(), any());
    
    // Act
    boolean result = adapter.unsubscribe("test-channel");
    
    // Assert
    assertFalse(result);
    verify(mockLogger).error(anyString(), anyString(), anyString(), anyString(), any());
    assertEquals(1, adapter.getSubscriptions().size());
  }

  @Test
  @DisplayName("unsubscribeAll should delegate to the event port and clear all subscriptions")
  void unsubscribeAll_shouldDelegateAndClearSubscriptions() {
    // Arrange
    adapter.subscribe("channel1", mockHandler);
    adapter.subscribe("channel2", mockHandler);
    
    // Act
    boolean result = adapter.unsubscribeAll();
    
    // Assert
    assertTrue(result);
    verify(mockDataFlowEventPort).unsubscribeAll(eq(componentId));
    assertTrue(adapter.getSubscriptions().isEmpty());
  }

  @Test
  @DisplayName("unsubscribeAll should handle exceptions")
  void unsubscribeAll_shouldHandleExceptions() {
    // Arrange
    adapter.subscribe("channel1", mockHandler);
    doThrow(new RuntimeException("Test error")).when(mockDataFlowEventPort)
        .unsubscribeAll(any());
    
    // Act
    boolean result = adapter.unsubscribeAll();
    
    // Assert
    assertFalse(result);
    verify(mockLogger).error(anyString(), anyString(), anyString(), any());
    assertEquals(1, adapter.getSubscriptions().size());
  }

  @Test
  @DisplayName("publish should delegate to the event port")
  void publish_shouldDelegate() {
    // Arrange
    Map<String, Object> data = Collections.singletonMap("key", "value");
    
    // Act
    boolean result = adapter.publish("test-channel", data);
    
    // Assert
    assertTrue(result);
    verify(mockDataFlowEventPort).publishData(eq(componentId), eq("test-channel"), eq(data));
  }

  @Test
  @DisplayName("publish should handle null or empty channel")
  void publish_shouldHandleNullOrEmptyChannel() {
    // Arrange
    Map<String, Object> data = Collections.singletonMap("key", "value");
    
    // Act
    boolean result1 = adapter.publish(null, data);
    boolean result2 = adapter.publish("", data);
    
    // Assert
    assertFalse(result1);
    assertFalse(result2);
    verify(mockDataFlowEventPort, never()).publishData(any(), any(), anyMap());
  }

  @Test
  @DisplayName("publish should handle null data")
  void publish_shouldHandleNullData() {
    // Act
    boolean result = adapter.publish("test-channel", null);
    
    // Assert
    assertFalse(result);
    verify(mockDataFlowEventPort, never()).publishData(any(), any(), anyMap());
  }

  @Test
  @DisplayName("publish should handle exceptions")
  void publish_shouldHandleExceptions() {
    // Arrange
    Map<String, Object> data = Collections.singletonMap("key", "value");
    doThrow(new RuntimeException("Test error")).when(mockDataFlowEventPort)
        .publishData(any(), any(), anyMap());
    
    // Act
    boolean result = adapter.publish("test-channel", data);
    
    // Assert
    assertFalse(result);
    verify(mockLogger).error(anyString(), anyString(), anyString(), anyString(), any());
  }

  @Test
  @DisplayName("getSubscriptions should return an unmodifiable set of subscriptions")
  void getSubscriptions_shouldReturnUnmodifiableSet() {
    // Arrange
    adapter.subscribe("channel1", mockHandler);
    adapter.subscribe("channel2", mockHandler);
    
    // Act
    Set<String> subscriptions = adapter.getSubscriptions();
    
    // Assert
    assertEquals(2, subscriptions.size());
    assertTrue(subscriptions.contains("channel1"));
    assertTrue(subscriptions.contains("channel2"));
    
    // Verify it's unmodifiable
    try {
      subscriptions.add("channel3");
      // If no exception is thrown, fail the test
      assertTrue(false, "Should have thrown UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // Expected
    }
  }

  @Test
  @DisplayName("getSubscriptionCount should return the number of subscriptions")
  void getSubscriptionCount_shouldReturnCount() {
    // Arrange
    adapter.subscribe("channel1", mockHandler);
    adapter.subscribe("channel2", mockHandler);
    
    // Act
    int count = adapter.getSubscriptionCount();
    
    // Assert
    assertEquals(2, count);
  }

  @Test
  @DisplayName("isSubscribedTo should check if component is subscribed to a channel")
  void isSubscribedTo_shouldCheckSubscription() {
    // Arrange
    adapter.subscribe("channel1", mockHandler);
    
    // Act
    boolean subscribed1 = adapter.isSubscribedTo("channel1");
    boolean subscribed2 = adapter.isSubscribedTo("channel2");
    
    // Assert
    assertTrue(subscribed1);
    assertFalse(subscribed2);
  }
}