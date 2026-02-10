/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import org.s8r.application.port.EventPublisherPort.EventSubscriber;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for the EventPublisherAdapter.
 */
class EventPublisherAdapterTest {

    private EventPublisherAdapter adapter;
    
    @BeforeEach
    void setUp() {
        adapter = new EventPublisherAdapter();
    }
    
    @Test
    @DisplayName("Should publish an event with no subscribers")
    void testPublishEventWithNoSubscribers() {
        boolean result = adapter.publishEvent("test-topic", "test-event", "test-payload");
        assertTrue(result, "Publishing an event to a topic with no subscribers should succeed");
    }
    
    @Test
    @DisplayName("Should publish an event with subscribers")
    void testPublishEventWithSubscribers() {
        AtomicBoolean eventReceived = new AtomicBoolean(false);
        AtomicReference<String> receivedTopic = new AtomicReference<>();
        AtomicReference<String> receivedEventType = new AtomicReference<>();
        AtomicReference<String> receivedPayload = new AtomicReference<>();
        
        EventSubscriber subscriber = (topic, eventType, payload, properties) -> {
            eventReceived.set(true);
            receivedTopic.set(topic);
            receivedEventType.set(eventType);
            receivedPayload.set(payload);
        };
        
        // Subscribe to the topic
        String subscriptionId = adapter.subscribe("test-topic", subscriber);
        assertNotNull(subscriptionId, "Subscription ID should not be null");
        
        // Publish an event
        boolean result = adapter.publishEvent("test-topic", "test-event", "test-payload");
        assertTrue(result, "Publishing an event should succeed");
        
        // Verify the subscriber received the event
        assertTrue(eventReceived.get(), "The subscriber should have received the event");
        assertEquals("test-topic", receivedTopic.get(), "The received topic should match");
        assertEquals("test-event", receivedEventType.get(), "The received event type should match");
        assertEquals("test-payload", receivedPayload.get(), "The received payload should match");
    }
    
    @Test
    @DisplayName("Should publish an event with properties")
    void testPublishEventWithProperties() {
        AtomicReference<Map<String, String>> receivedProperties = new AtomicReference<>();
        
        EventSubscriber subscriber = (topic, eventType, payload, properties) -> {
            receivedProperties.set(properties);
        };
        
        // Subscribe to the topic
        adapter.subscribe("test-topic", subscriber);
        
        // Create properties
        Map<String, String> properties = Map.of(
            "property1", "value1",
            "property2", "value2"
        );
        
        // Publish an event with properties
        boolean result = adapter.publishEvent("test-topic", "test-event", "test-payload", properties);
        assertTrue(result, "Publishing an event with properties should succeed");
        
        // Verify the subscriber received the properties
        assertNotNull(receivedProperties.get(), "The received properties should not be null");
        assertEquals(2, receivedProperties.get().size(), "The received properties map should have 2 entries");
        assertEquals("value1", receivedProperties.get().get("property1"), "Property1 value should match");
        assertEquals("value2", receivedProperties.get().get("property2"), "Property2 value should match");
    }
    
    @Test
    @DisplayName("Should support multiple subscribers for a topic")
    void testMultipleSubscribers() {
        AtomicInteger subscriber1Count = new AtomicInteger(0);
        AtomicInteger subscriber2Count = new AtomicInteger(0);
        
        EventSubscriber subscriber1 = (topic, eventType, payload, properties) -> {
            subscriber1Count.incrementAndGet();
        };
        
        EventSubscriber subscriber2 = (topic, eventType, payload, properties) -> {
            subscriber2Count.incrementAndGet();
        };
        
        // Subscribe to the topic
        adapter.subscribe("test-topic", subscriber1);
        adapter.subscribe("test-topic", subscriber2);
        
        // Publish an event
        adapter.publishEvent("test-topic", "test-event", "test-payload");
        
        // Verify both subscribers received the event
        assertEquals(1, subscriber1Count.get(), "Subscriber 1 should have received the event");
        assertEquals(1, subscriber2Count.get(), "Subscriber 2 should have received the event");
    }
    
    @Test
    @DisplayName("Should handle subscribers for different topics")
    void testDifferentTopicSubscribers() {
        AtomicInteger topic1Count = new AtomicInteger(0);
        AtomicInteger topic2Count = new AtomicInteger(0);
        
        EventSubscriber subscriber1 = (topic, eventType, payload, properties) -> {
            topic1Count.incrementAndGet();
        };
        
        EventSubscriber subscriber2 = (topic, eventType, payload, properties) -> {
            topic2Count.incrementAndGet();
        };
        
        // Subscribe to different topics
        adapter.subscribe("topic1", subscriber1);
        adapter.subscribe("topic2", subscriber2);
        
        // Publish events to each topic
        adapter.publishEvent("topic1", "test-event", "test-payload");
        adapter.publishEvent("topic2", "test-event", "test-payload");
        
        // Verify subscribers received only their own topic events
        assertEquals(1, topic1Count.get(), "Topic 1 subscriber should have received 1 event");
        assertEquals(1, topic2Count.get(), "Topic 2 subscriber should have received 1 event");
    }
    
    @Test
    @DisplayName("Should unsubscribe a subscriber")
    void testUnsubscribe() {
        AtomicInteger eventCount = new AtomicInteger(0);
        
        EventSubscriber subscriber = (topic, eventType, payload, properties) -> {
            eventCount.incrementAndGet();
        };
        
        // Subscribe to the topic
        String subscriptionId = adapter.subscribe("test-topic", subscriber);
        
        // Publish an event
        adapter.publishEvent("test-topic", "test-event", "test-payload");
        assertEquals(1, eventCount.get(), "Subscriber should have received the event");
        
        // Unsubscribe
        boolean unsubscribed = adapter.unsubscribe(subscriptionId);
        assertTrue(unsubscribed, "Unsubscribe should succeed");
        
        // Publish another event
        adapter.publishEvent("test-topic", "test-event", "test-payload");
        assertEquals(1, eventCount.get(), "Subscriber should not receive events after unsubscribing");
    }
    
    @Test
    @DisplayName("Should handle unsubscribe with invalid ID")
    void testUnsubscribeInvalidId() {
        boolean result = adapter.unsubscribe("non-existent-id");
        assertFalse(result, "Unsubscribing with an invalid ID should return false");
    }
}