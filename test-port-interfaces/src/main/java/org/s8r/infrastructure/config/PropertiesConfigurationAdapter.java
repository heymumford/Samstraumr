/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

package org.s8r.infrastructure.config;

import org.s8r.application.port.ConfigurationPort;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of the ConfigurationPort interface using Java Properties.
 * This adapter manages configuration properties in memory with the ability
 * to load from and save to properties files.
 */
public class PropertiesConfigurationAdapter implements ConfigurationPort {

    private final Map<String, String> properties;
    private final Map<String, ConfigurationChangeListener> listeners;
    private final AtomicLong listenerIdGenerator;
    
    /**
     * Factory method to create a new instance with empty properties.
     * This is primarily used by Karate tests to create instances.
     *
     * @return A new PropertiesConfigurationAdapter instance
     */
    public static PropertiesConfigurationAdapter createInstance() {
        return new PropertiesConfigurationAdapter();
    }
    
    /**
     * Factory method to create a new instance with the given properties.
     * This is primarily used by Karate tests to create instances with predefined properties.
     *
     * @param properties Initial properties map
     * @return A new PropertiesConfigurationAdapter instance
     */
    public static PropertiesConfigurationAdapter createWithProperties(Map<String, String> properties) {
        return new PropertiesConfigurationAdapter(properties);
    }

    /**
     * Creates a new PropertiesConfigurationAdapter with an empty properties map.
     */
    public PropertiesConfigurationAdapter() {
        this.properties = new ConcurrentHashMap<>();
        this.listeners = new ConcurrentHashMap<>();
        this.listenerIdGenerator = new AtomicLong(0);
    }

    /**
     * Creates a new PropertiesConfigurationAdapter with the given properties.
     *
     * @param properties Initial properties
     */
    public PropertiesConfigurationAdapter(Map<String, String> properties) {
        this.properties = new ConcurrentHashMap<>(properties);
        this.listeners = new ConcurrentHashMap<>();
        this.listenerIdGenerator = new AtomicLong(0);
    }

    /**
     * Creates a new PropertiesConfigurationAdapter and loads properties from the given source.
     *
     * @param source The source file path to load properties from
     * @throws IOException If an error occurs while loading properties
     */
    public PropertiesConfigurationAdapter(String source) throws IOException {
        this.properties = new ConcurrentHashMap<>();
        this.listeners = new ConcurrentHashMap<>();
        this.listenerIdGenerator = new AtomicLong(0);
        
        // Load the properties directly rather than calling loadConfiguration
        // since we want to throw IOException rather than returning false
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(source)) {
            props.load(fis);
            
            // Update properties map
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
        }
    }

    @Override
    public Optional<String> getString(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public String getString(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    @Override
    public Optional<Integer> getInteger(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public int getInteger(String key, int defaultValue) {
        return getInteger(key).orElse(defaultValue);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        String value = properties.get(key);
        if (value == null) {
            return Optional.empty();
        }
        
        // Handle common boolean string values
        value = value.toLowerCase();
        if (value.equals("true") || value.equals("yes") || value.equals("1")) {
            return Optional.of(true);
        } else if (value.equals("false") || value.equals("no") || value.equals("0")) {
            return Optional.of(false);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    @Override
    public List<String> getStringList(String key) {
        String value = properties.get(key);
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptyList();
        }
        
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Override
    public void setProperty(String key, String value) {
        properties.put(key, value);
        notifyListeners(key, value, ChangeType.SET);
    }

    @Override
    public boolean removeProperty(String key) {
        boolean removed = properties.remove(key) != null;
        if (removed) {
            notifyListeners(key, null, ChangeType.REMOVE);
        }
        return removed;
    }

    @Override
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public Map<String, String> getAllProperties() {
        return new HashMap<>(properties);
    }

    @Override
    public boolean loadConfiguration(String source) {
        return loadConfigurationWithDetails(source).isSuccess();
    }

    @Override
    public ConfigurationResult loadConfigurationWithDetails(String source) {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(source)) {
            props.load(fis);
            
            // Update properties map
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
                // We don't notify listeners for individual properties here,
                // just send a single LOAD notification
            }
            
            // Notify listeners of the load operation
            notifyListeners(source, null, ChangeType.LOAD);
            
            return ConfigurationResult.success();
        } catch (IOException e) {
            return ConfigurationResult.failure("Failed to load configuration from " + source, e);
        }
    }

    @Override
    public boolean saveConfiguration(String destination) {
        return saveConfigurationWithDetails(destination).isSuccess();
    }

    @Override
    public ConfigurationResult saveConfigurationWithDetails(String destination) {
        Properties props = toProperties();
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            props.store(fos, "Configuration saved by PropertiesConfigurationAdapter");
            return ConfigurationResult.success();
        } catch (IOException e) {
            return ConfigurationResult.failure("Failed to save configuration to " + destination, e);
        }
    }

    @Override
    public Map<String, String> getPropertiesWithPrefix(String prefix) {
        return properties.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Properties toProperties() {
        Properties props = new Properties();
        properties.forEach(props::setProperty);
        return props;
    }

    @Override
    public void clear() {
        properties.clear();
        notifyListeners(null, null, ChangeType.CLEAR);
    }

    @Override
    public String registerChangeListener(ConfigurationChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener cannot be null");
        }
        
        String listenerId = "listener-" + listenerIdGenerator.incrementAndGet();
        listeners.put(listenerId, listener);
        return listenerId;
    }

    @Override
    public boolean unregisterChangeListener(String listenerId) {
        return listeners.remove(listenerId) != null;
    }

    /**
     * Notifies all registered listeners of a configuration change.
     *
     * @param key The key of the property that changed (can be null for clear operation)
     * @param value The new value of the property (can be null if removed)
     * @param changeType The type of change that occurred
     */
    private void notifyListeners(String key, String value, ChangeType changeType) {
        // Make a copy of the listeners to avoid concurrency issues
        // if a listener is unregistered during notification
        List<ConfigurationChangeListener> listenersCopy = new ArrayList<>(listeners.values());
        
        // Notify all listeners
        for (ConfigurationChangeListener listener : listenersCopy) {
            try {
                listener.onConfigurationChange(key, value, changeType);
            } catch (Exception e) {
                // Catch and log exceptions to prevent a listener from breaking the notification chain
                System.err.println("Error notifying configuration change listener: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}