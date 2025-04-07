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

package org.s8r.domain.component.port;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.test.annotation.UnitTest;

/**
 * Simple tests for the port interface pattern implementation.
 * 
 * <p>This test focuses on demonstrating the port interface pattern without
 * relying on the full implementation, which is still being developed.
 */
@UnitTest
public class SimplePortInterfaceTest {
    
    // A simple implementation of ComponentPort for testing
    private static class TestComponentPort implements ComponentPort {
        private final ComponentId id;
        private LifecycleState state;
        private final List<String> lineage = new ArrayList<>();
        private final List<String> activityLog = new ArrayList<>();
        private final java.time.Instant creationTime = java.time.Instant.now();
        
        public TestComponentPort(ComponentId id) {
            this.id = id;
            this.state = LifecycleState.INITIALIZED;
            this.lineage.add(id.getIdString());
        }
        
        @Override
        public ComponentId getId() {
            return id;
        }
        
        @Override
        public LifecycleState getLifecycleState() {
            return state;
        }
        
        @Override
        public List<String> getLineage() {
            return Collections.unmodifiableList(lineage);
        }
        
        @Override
        public List<String> getActivityLog() {
            return Collections.unmodifiableList(activityLog);
        }
        
        @Override
        public java.time.Instant getCreationTime() {
            return creationTime;
        }
        
        @Override
        public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
            return Collections.emptyList();
        }
        
        @Override
        public void addToLineage(String entry) {
            lineage.add(entry);
        }
        
        @Override
        public void clearEvents() {
            // No-op for this simple implementation
        }
        
        @Override
        public void publishData(String channel, Map<String, Object> data) {
            activityLog.add("Published data to channel: " + channel);
        }
        
        @Override
        public void publishData(String channel, String key, Object value) {
            activityLog.add("Published " + key + "=" + value + " to channel: " + channel);
        }
        
        @Override
        public void transitionTo(LifecycleState newState) {
            activityLog.add("Transitioned from " + state + " to " + newState);
            state = newState;
        }
        
        @Override
        public void activate() {
            transitionTo(LifecycleState.ACTIVE);
        }
        
        @Override
        public void deactivate() {
            transitionTo(LifecycleState.READY);
        }
        
        @Override
        public void terminate() {
            transitionTo(LifecycleState.TERMINATED);
        }
    }
    
    // A simple repository implementation for testing
    private static class TestComponentRepository {
        private final Map<String, ComponentPort> components = new HashMap<>();
        
        public void save(ComponentPort component) {
            components.put(component.getId().getIdString(), component);
        }
        
        public Optional<ComponentPort> findById(ComponentId id) {
            return Optional.ofNullable(components.get(id.getIdString()));
        }
        
        public List<ComponentPort> findAll() {
            return new ArrayList<>(components.values());
        }
    }
    
    // A simple service that works with port interfaces
    private static class TestComponentService {
        private final TestComponentRepository repository;
        
        public TestComponentService(TestComponentRepository repository) {
            this.repository = repository;
        }
        
        public ComponentPort createComponent(String name) {
            ComponentId id = ComponentId.create(UUID.randomUUID().toString(), name);
            ComponentPort component = new TestComponentPort(id);
            repository.save(component);
            return component;
        }
        
        public void activateComponent(ComponentId id) {
            repository.findById(id).ifPresent(ComponentPort::activate);
        }
        
        public void publishData(ComponentId id, String channel, String key, Object value) {
            repository.findById(id).ifPresent(comp -> comp.publishData(channel, key, value));
        }
    }
    
    private TestComponentRepository repository;
    private TestComponentService service;
    
    @BeforeEach
    void setUp() {
        repository = new TestComponentRepository();
        service = new TestComponentService(repository);
    }
    
    @Test
    @DisplayName("Test creating component through port interface")
    void testCreateComponentThroughPortInterface() {
        // Create a component through the service
        ComponentPort component = service.createComponent("Test Component");
        
        // Verify component was created
        assertNotNull(component);
        assertEquals(LifecycleState.INITIALIZED, component.getLifecycleState());
        
        // Verify component was saved to repository
        Optional<ComponentPort> retrieved = repository.findById(component.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(component.getId().getIdString(), retrieved.get().getId().getIdString());
    }
    
    @Test
    @DisplayName("Test activating component through port interface")
    void testActivateComponentThroughPortInterface() {
        // Create a component
        ComponentPort component = service.createComponent("Test Component");
        
        // Activate the component
        service.activateComponent(component.getId());
        
        // Verify component was activated
        Optional<ComponentPort> activated = repository.findById(component.getId());
        assertTrue(activated.isPresent());
        assertEquals(LifecycleState.ACTIVE, activated.get().getLifecycleState());
    }
    
    @Test
    @DisplayName("Test publishing data through port interface")
    void testPublishDataThroughPortInterface() {
        // Create a component
        ComponentPort component = service.createComponent("Test Component");
        
        // Publish data through the component
        service.publishData(component.getId(), "test-channel", "key1", "value1");
        
        // Verify data was published
        Optional<ComponentPort> retrieved = repository.findById(component.getId());
        assertTrue(retrieved.isPresent());
        
        List<String> activityLog = retrieved.get().getActivityLog();
        assertFalse(activityLog.isEmpty());
        assertTrue(activityLog.stream().anyMatch(entry -> 
            entry.contains("Published key1=value1 to channel: test-channel")));
    }
    
    @Test
    @DisplayName("Test lifecycle transitions through port interface")
    void testLifecycleTransitions() {
        // Create a component 
        ComponentPort component = service.createComponent("Lifecycle Test Component");
        
        // Initial state should be INITIALIZED
        assertEquals(LifecycleState.INITIALIZED, component.getLifecycleState());
        
        // Transition through states
        component.transitionTo(LifecycleState.READY);
        assertEquals(LifecycleState.READY, component.getLifecycleState());
        
        component.activate();
        assertEquals(LifecycleState.ACTIVE, component.getLifecycleState());
        
        component.deactivate();
        assertEquals(LifecycleState.READY, component.getLifecycleState());
        
        component.terminate();
        assertEquals(LifecycleState.TERMINATED, component.getLifecycleState());
    }
    
    @Test
    @DisplayName("Test domain objects working with port interfaces")
    void testDomainObjectsWithPortInterfaces() {
        // Create a domain component
        Component domainComponent = Component.create("Domain Component");
        
        // Create a port interface adapter for it
        ComponentPort componentPort = new TestComponentPort(domainComponent.getId());
        
        // Save it to the repository
        repository.save(componentPort);
        
        // Retrieve from repository
        Optional<ComponentPort> retrieved = repository.findById(domainComponent.getId());
        assertTrue(retrieved.isPresent());
        
        // Verify retrieved component
        ComponentPort retrievedPort = retrieved.get();
        assertEquals(domainComponent.getId().getIdString(), retrievedPort.getId().getIdString());
        
        // Interact with the port interface
        retrievedPort.activate();
        assertEquals(LifecycleState.ACTIVE, retrievedPort.getLifecycleState());
    }
}