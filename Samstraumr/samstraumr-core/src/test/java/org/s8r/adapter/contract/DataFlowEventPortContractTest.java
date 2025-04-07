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

package org.s8r.adapter.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.application.port.DataFlowEventPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.infrastructure.event.DataFlowEventHandler;

/**
 * Contract tests for the DataFlowEventPort interface.
 * 
 * <p>This test class verifies that any implementation of the DataFlowEventPort
 * interface adheres to the contract defined by the interface. It tests the
 * behavior expected by the application core regardless of the specific adapter
 * implementation.</p>
 * 
 * <p>The tests cover core functionality such as subscribing, unsubscribing,
 * and publishing data to channels.</p>
 */
public class DataFlowEventPortContractTest extends PortContractTest<DataFlowEventPort> {

    private ComponentId sourceId;
    private ComponentId targetId;
    
    @Override
    protected DataFlowEventPort createPortImplementation() {
        // Create component IDs for testing
        sourceId = new ComponentId(UUID.randomUUID().toString());
        targetId = new ComponentId(UUID.randomUUID().toString());
        
        return new DataFlowEventHandler(logger);
    }
    
    @Override
    protected void verifyNullInputHandling() {
        // This is tested in nullInputHandlingTests()
    }
    
    @Override
    protected void verifyRequiredMethods() {
        // This is tested across multiple method-specific tests
    }
    
    /**
     * Verifies that the DataFlowEventPort implementation handles null inputs correctly.
     */
    @Test
    @DisplayName("Should handle null inputs gracefully")
    public void nullInputHandlingTests() {
        // Test null component ID in subscribe
        portUnderTest.subscribe(null, "channel", event -> {});
        // No exception should be thrown
        
        // Test null channel in subscribe
        portUnderTest.subscribe(sourceId, null, event -> {});
        // No exception should be thrown
        
        // Test null handler in subscribe
        portUnderTest.subscribe(sourceId, "channel", null);
        // No exception should be thrown
        
        // Test null component ID in unsubscribe
        portUnderTest.unsubscribe(null, "channel");
        // No exception should be thrown
        
        // Test null channel in unsubscribe
        portUnderTest.unsubscribe(sourceId, null);
        // No exception should be thrown
        
        // Test null component ID in unsubscribeAll
        portUnderTest.unsubscribeAll(null);
        // No exception should be thrown
        
        // Test null component ID in getComponentSubscriptions
        Set<String> nullSubscriptions = portUnderTest.getComponentSubscriptions(null);
        assertTrue(nullSubscriptions.isEmpty(), "Subscriptions for null component should be empty");
        
        // Test null inputs in publishData
        portUnderTest.publishData(null, "channel", new HashMap<>());
        portUnderTest.publishData(sourceId, null, new HashMap<>());
        portUnderTest.publishData(sourceId, "channel", null);
        // No exceptions should be thrown
    }
    
    /**
     * Tests the subscription management functionality of DataFlowEventPort implementations.
     */
    @Test
    @DisplayName("Should manage subscriptions correctly")
    public void subscriptionManagementTests() {
        // Initially the component should have no subscriptions
        assertTrue(portUnderTest.getComponentSubscriptions(sourceId).isEmpty(),
                "Initial component subscriptions should be empty");
        
        // Subscribe to a channel
        AtomicBoolean handlerCalled = new AtomicBoolean(false);
        Consumer<ComponentDataEvent> handler = event -> handlerCalled.set(true);
        
        portUnderTest.subscribe(sourceId, "test-channel", handler);
        
        // Verify subscription
        Set<String> subscriptions = portUnderTest.getComponentSubscriptions(sourceId);
        assertFalse(subscriptions.isEmpty(), "Component subscriptions should not be empty after subscribe");
        assertTrue(subscriptions.contains("test-channel"), "Component should be subscribed to test-channel");
        
        // Verify channel is available
        Set<String> availableChannels = portUnderTest.getAvailableChannels();
        assertTrue(availableChannels.contains("test-channel"), "test-channel should be available");
        
        // Subscribe to another channel
        portUnderTest.subscribe(sourceId, "test-channel-2", event -> {});
        
        // Verify multiple subscriptions
        subscriptions = portUnderTest.getComponentSubscriptions(sourceId);
        assertEquals(2, subscriptions.size(), "Component should have 2 subscriptions");
        
        // Unsubscribe from one channel
        portUnderTest.unsubscribe(sourceId, "test-channel");
        
        // Verify remaining subscription
        subscriptions = portUnderTest.getComponentSubscriptions(sourceId);
        assertEquals(1, subscriptions.size(), "Component should have 1 subscription after unsubscribe");
        assertTrue(subscriptions.contains("test-channel-2"), "Component should still be subscribed to test-channel-2");
        
        // Unsubscribe from all channels
        portUnderTest.unsubscribeAll(sourceId);
        
        // Verify no subscriptions
        subscriptions = portUnderTest.getComponentSubscriptions(sourceId);
        assertTrue(subscriptions.isEmpty(), "Component should have no subscriptions after unsubscribeAll");
    }
    
    /**
     * Tests the data publishing functionality of DataFlowEventPort implementations.
     */
    @Test
    @DisplayName("Should publish data to subscribers correctly")
    public void dataPublishingTests() {
        // Create a mock handler to verify delivery
        @SuppressWarnings("unchecked")
        Consumer<ComponentDataEvent> mockHandler = mock(Consumer.class);
        
        // Subscribe target to the channel
        portUnderTest.subscribe(targetId, "data-channel", mockHandler);
        
        // Prepare test data
        Map<String, Object> testData = new HashMap<>();
        testData.put("key1", "value1");
        testData.put("key2", 123);
        
        // Publish data from source to the channel
        portUnderTest.publishData(sourceId, "data-channel", testData);
        
        // Verify the handler was called
        verify(mockHandler, times(1)).accept(any(ComponentDataEvent.class));
        
        // Capture the event for verification
        @SuppressWarnings("unchecked")
        Consumer<ComponentDataEvent> capturingHandler = mock(Consumer.class);
        doAnswer(invocation -> {
            ComponentDataEvent event = invocation.getArgument(0);
            // Verify event properties
            assertEquals(sourceId, event.getSourceId(), "Event source ID should match publisher");
            assertEquals("data-channel", event.getDataChannel(), "Event channel should match published channel");
            assertEquals(testData, event.getData(), "Event data should match published data");
            return null;
        }).when(capturingHandler).accept(any(ComponentDataEvent.class));
        
        // Reset and use capturing handler
        reset(mockHandler);
        portUnderTest.unsubscribe(targetId, "data-channel");
        portUnderTest.subscribe(targetId, "data-channel", capturingHandler);
        
        // Publish again
        portUnderTest.publishData(sourceId, "data-channel", testData);
        
        // Verify the handler was called with the correct event
        verify(capturingHandler, times(1)).accept(any(ComponentDataEvent.class));
    }
    
    /**
     * Tests that a component doesn't receive its own published data.
     */
    @Test
    @DisplayName("Should not deliver published data back to the publisher")
    public void noSelfDeliveryTests() {
        // Create a mock handler
        @SuppressWarnings("unchecked")
        Consumer<ComponentDataEvent> mockHandler = mock(Consumer.class);
        
        // Subscribe source to the channel (same component that will publish)
        portUnderTest.subscribe(sourceId, "self-test", mockHandler);
        
        // Publish data from the same component
        Map<String, Object> testData = new HashMap<>();
        testData.put("key", "value");
        portUnderTest.publishData(sourceId, "self-test", testData);
        
        // Verify the handler was NOT called (no self-delivery)
        verify(mockHandler, never()).accept(any(ComponentDataEvent.class));
    }
    
    /**
     * Tests handling of multiple subscribers to the same channel.
     */
    @Test
    @DisplayName("Should deliver data to multiple subscribers")
    public void multipleSubscribersTests() {
        // Create additional component ID
        ComponentId thirdId = new ComponentId(UUID.randomUUID().toString());
        
        // Create mock handlers
        @SuppressWarnings("unchecked")
        Consumer<ComponentDataEvent> mockHandler1 = mock(Consumer.class);
        @SuppressWarnings("unchecked")
        Consumer<ComponentDataEvent> mockHandler2 = mock(Consumer.class);
        
        // Subscribe both components to the same channel
        portUnderTest.subscribe(targetId, "multi-channel", mockHandler1);
        portUnderTest.subscribe(thirdId, "multi-channel", mockHandler2);
        
        // Publish data
        Map<String, Object> testData = new HashMap<>();
        testData.put("key", "value");
        portUnderTest.publishData(sourceId, "multi-channel", testData);
        
        // Verify both handlers were called
        verify(mockHandler1, times(1)).accept(any(ComponentDataEvent.class));
        verify(mockHandler2, times(1)).accept(any(ComponentDataEvent.class));
    }
}