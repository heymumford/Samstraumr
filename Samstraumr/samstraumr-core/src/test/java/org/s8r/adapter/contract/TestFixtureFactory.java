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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.adapter.ComponentAdapter;
import org.s8r.adapter.MachineAdapter;
import org.s8r.adapter.MachineFactoryAdapter;
import org.s8r.application.port.ConfigurationPort;
import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.MachinePort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineType;
import org.s8r.infrastructure.config.ConfigurationAdapter;
import org.s8r.infrastructure.event.DataFlowEventHandler;
import org.s8r.infrastructure.event.EventPublisherAdapter;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.notification.NotificationAdapter;

/**
 * Factory for creating test fixtures for adapter contract tests.
 * 
 * <p>This factory provides standard implementations and test instances
 * for use in adapter contract tests, ensuring consistency across all
 * test classes.</p>
 */
public class TestFixtureFactory {

    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    
    /**
     * Gets a ConsoleLogger instance for testing.
     * 
     * @return A ConsoleLogger instance
     */
    public static LoggerPort getLogger() {
        return getCachedInstance("logger", ConsoleLogger::new);
    }
    
    /**
     * Gets a ConfigurationAdapter instance for testing.
     * 
     * @return A ConfigurationAdapter instance
     */
    public static ConfigurationPort getConfigurationPort() {
        return getCachedInstance("configPort", 
                () -> new ConfigurationAdapter(getLogger()));
    }
    
    /**
     * Gets a DataFlowEventPort instance for testing.
     * 
     * @return A DataFlowEventPort instance
     */
    public static DataFlowEventPort getDataFlowEventPort() {
        return getCachedInstance("dataFlowPort", 
                () -> new DataFlowEventHandler(getLogger()));
    }
    
    /**
     * Gets an EventPublisherPort instance for testing.
     * 
     * @return An EventPublisherPort instance
     */
    public static EventPublisherPort getEventPublisherPort() {
        return getCachedInstance("eventPublisherPort", 
                () -> new EventPublisherAdapter(getLogger()));
    }
    
    /**
     * Gets a NotificationPort instance for testing.
     * 
     * @return A NotificationPort instance
     */
    public static NotificationPort getNotificationPort() {
        return getCachedInstance("notificationPort", 
                () -> new NotificationAdapter(getLogger(), getConfigurationPort()));
    }
    
    /**
     * Gets a MachineFactoryAdapter instance for testing.
     * 
     * @return A MachineFactoryAdapter instance
     */
    public static MachineFactoryAdapter getMachineFactoryAdapter() {
        return getCachedInstance("machineFactoryAdapter", 
                () -> new MachineFactoryAdapter(getLogger()));
    }
    
    /**
     * Creates a new ComponentPort instance for testing.
     * 
     * @param name The name for the component
     * @return A new ComponentPort instance
     */
    public static ComponentPort createComponentPort(String name) {
        Component component = Component.create(name);
        return ComponentAdapter.createComponentPort(component);
    }
    
    /**
     * Creates a new MachinePort instance for testing.
     * 
     * @param name The name for the machine
     * @param type The machine type
     * @return A new MachinePort instance
     */
    public static MachinePort createMachinePort(String name, MachineType type) {
        return getMachineFactoryAdapter().createMachine(type, name);
    }
    
    /**
     * Creates a new MachinePort instance for testing with the STANDARD type.
     * 
     * @param name The name for the machine
     * @return A new MachinePort instance
     */
    public static MachinePort createStandardMachinePort(String name) {
        return createMachinePort(name, MachineType.STANDARD);
    }
    
    /**
     * Creates a new domain Machine instance for testing.
     * 
     * @param name The name for the machine
     * @param type The machine type
     * @return A new Machine instance
     */
    public static Machine createDomainMachine(String name, MachineType type) {
        return Machine.create(name, type);
    }
    
    /**
     * Creates a new ComponentId instance with a random UUID.
     * 
     * @return A new ComponentId instance
     */
    public static ComponentId createRandomComponentId() {
        return new ComponentId(UUID.randomUUID().toString());
    }
    
    /**
     * Gets or creates a cached instance using the provided factory function.
     * 
     * @param <T> The type of the instance
     * @param key The cache key
     * @param factory The factory function
     * @return The cached or new instance
     */
    @SuppressWarnings("unchecked")
    private static <T> T getCachedInstance(String key, Supplier<T> factory) {
        return (T) instanceCache.computeIfAbsent(key, k -> factory.get());
    }
    
    /**
     * Supplier functional interface for instance creation.
     *
     * @param <T> The type of the instance
     */
    @FunctionalInterface
    private interface Supplier<T> {
        T get();
    }
}