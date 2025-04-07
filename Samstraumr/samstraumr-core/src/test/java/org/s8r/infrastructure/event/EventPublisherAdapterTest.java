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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for the EventPublisherAdapter class.
 * 
 * <p>These tests verify that the EventPublisherAdapter correctly implements the
 * EventPublisherPort interface and properly delegates to the EventDispatcher.
 */
@UnitTest
public class EventPublisherAdapterTest {

  private EventDispatcher mockEventDispatcher;
  private ComponentRepository mockComponentRepository;
  private LoggerPort mockLogger;
  private EventPublisherAdapter eventPublisherAdapter;
  private ComponentPort mockComponent;

  @BeforeEach
  void setUp() {
    mockEventDispatcher = mock(EventDispatcher.class);
    mockComponentRepository = mock(ComponentRepository.class);
    mockLogger = mock(LoggerPort.class);
    mockComponent = mock(ComponentPort.class);
    
    eventPublisherAdapter = new EventPublisherAdapter(
        mockEventDispatcher, mockComponentRepository, mockLogger);
  }

  @Test
  @DisplayName("publishEvent should dispatch a single event")
  void publishEvent_shouldDispatchSingleEvent() {
    // Arrange
    DomainEvent testEvent = new ComponentCreatedEvent("test-component-id");
    
    // Act
    eventPublisherAdapter.publishEvent(testEvent);
    
    // Assert
    verify(mockEventDispatcher, times(1)).dispatch(testEvent);
  }

  @Test
  @DisplayName("publishEvent should handle null events gracefully")
  void publishEvent_shouldHandleNullEvents() {
    // Act
    eventPublisherAdapter.publishEvent(null);
    
    // Assert
    verify(mockEventDispatcher, never()).dispatch(any());
    verify(mockLogger, times(1)).warn(any(String.class));
  }

  @Test
  @DisplayName("publishEvents should dispatch multiple events")
  void publishEvents_shouldDispatchMultipleEvents() {
    // Arrange
    DomainEvent event1 = new ComponentCreatedEvent("test-component-1");
    DomainEvent event2 = new ComponentStateChangedEvent("test-component-2", "CREATED", "READY");
    List<DomainEvent> events = Arrays.asList(event1, event2);
    
    // Act
    eventPublisherAdapter.publishEvents(events);
    
    // Assert
    verify(mockEventDispatcher, times(1)).dispatch(event1);
    verify(mockEventDispatcher, times(1)).dispatch(event2);
  }

  @Test
  @DisplayName("publishEvents should handle empty event lists gracefully")
  void publishEvents_shouldHandleEmptyEventLists() {
    // Act
    eventPublisherAdapter.publishEvents(new ArrayList<>());
    
    // Assert
    verify(mockEventDispatcher, never()).dispatch(any());
  }

  @Test
  @DisplayName("publishEvents should handle null event lists gracefully")
  void publishEvents_shouldHandleNullEventLists() {
    // Act
    eventPublisherAdapter.publishEvents(null);
    
    // Assert
    verify(mockEventDispatcher, never()).dispatch(any());
  }

  @Test
  @DisplayName("publishPendingEvents should publish and clear component events")
  void publishPendingEvents_shouldPublishAndClearEvents() {
    // Arrange
    ComponentId componentId = mock(ComponentId.class);
    when(componentId.getIdString()).thenReturn("test-component-id");
    
    List<DomainEvent> componentEvents = new ArrayList<>();
    componentEvents.add(new ComponentCreatedEvent("test-component-id"));
    componentEvents.add(new ComponentStateChangedEvent("test-component-id", "CREATED", "READY"));
    
    when(mockComponent.getDomainEvents()).thenReturn(componentEvents);
    when(mockComponent.getId()).thenReturn(componentId);
    when(mockComponentRepository.findById(componentId)).thenReturn(Optional.of(mockComponent));
    
    // Act
    int published = eventPublisherAdapter.publishPendingEvents(componentId);
    
    // Assert
    assertEquals(2, published);
    verify(mockComponent, times(1)).getDomainEvents();
    verify(mockComponent, times(1)).clearEvents();
    verify(mockEventDispatcher, times(2)).dispatch(any(DomainEvent.class));
  }

  @Test
  @DisplayName("publishPendingEvents should handle component not found")
  void publishPendingEvents_shouldHandleComponentNotFound() {
    // Arrange
    ComponentId componentId = mock(ComponentId.class);
    when(componentId.getIdString()).thenReturn("nonexistent-component-id");
    when(mockComponentRepository.findById(componentId)).thenReturn(Optional.empty());
    
    // Act
    int published = eventPublisherAdapter.publishPendingEvents(componentId);
    
    // Assert
    assertEquals(0, published);
    verify(mockLogger, times(1)).warn(any(String.class), any(String.class));
    verify(mockEventDispatcher, never()).dispatch(any());
  }

  @Test
  @DisplayName("publishPendingEvents should handle null component ID")
  void publishPendingEvents_shouldHandleNullComponentId() {
    // Act
    int published = eventPublisherAdapter.publishPendingEvents(null);
    
    // Assert
    assertEquals(0, published);
    verify(mockLogger, times(1)).warn(any(String.class));
    verify(mockEventDispatcher, never()).dispatch(any());
  }
}