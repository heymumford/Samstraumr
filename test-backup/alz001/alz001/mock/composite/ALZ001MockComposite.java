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

package org.s8r.test.steps.alz001.mock.composite;

import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;

/**
 * Base class for all mock composites in the ALZ001 test suite.
 * 
 * <p>Provides common functionality for managing child components,
 * connections between components, and composite lifecycle operations.
 * This class extends ALZ001MockComponent and adds composite-specific
 * capabilities while maintaining compatibility with the component API.
 */
public abstract class ALZ001MockComposite extends ALZ001MockComponent {
    
    /**
     * Represents a connection between two components.
     */
    public static class ComponentConnection {
        private final ALZ001MockComponent source;
        private final ALZ001MockComponent target;
        private final String connectionType;
        private final Map<String, Object> properties;
        private boolean active;
        
        /**
         * Creates a new component connection.
         *
         * @param source The source component
         * @param target The target component
         * @param connectionType The type of connection
         */
        public ComponentConnection(ALZ001MockComponent source, ALZ001MockComponent target, String connectionType) {
            this.source = source;
            this.target = target;
            this.connectionType = connectionType;
            this.properties = new HashMap<>();
            this.active = true;
        }
        
        /**
         * Gets the source component.
         *
         * @return The source component
         */
        public ALZ001MockComponent getSource() {
            return source;
        }
        
        /**
         * Gets the target component.
         *
         * @return The target component
         */
        public ALZ001MockComponent getTarget() {
            return target;
        }
        
        /**
         * Gets the connection type.
         *
         * @return The connection type
         */
        public String getConnectionType() {
            return connectionType;
        }
        
        /**
         * Sets a property value.
         *
         * @param key The property key
         * @param value The property value
         */
        public void setProperty(String key, Object value) {
            properties.put(key, value);
        }
        
        /**
         * Gets a property value.
         *
         * @param <T> The type of the property value
         * @param key The property key
         * @return The property value, cast to the expected type
         */
        @SuppressWarnings("unchecked")
        public <T> T getProperty(String key) {
            return (T) properties.get(key);
        }
        
        /**
         * Checks if the connection is active.
         *
         * @return true if the connection is active, false otherwise
         */
        public boolean isActive() {
            return active;
        }
        
        /**
         * Sets the active state of the connection.
         *
         * @param active The new active state
         */
        public void setActive(boolean active) {
            this.active = active;
        }
    }
    
    /**
     * The child components in this composite.
     */
    protected final List<ALZ001MockComponent> children;
    
    /**
     * The connections between components.
     */
    protected final List<ComponentConnection> connections;
    
    /**
     * The composite type (e.g., STANDARD, PIPELINE, OBSERVER).
     */
    protected final String compositeType;
    
    /**
     * Constructs a new mock composite with the specified name and type.
     *
     * @param name The unique name of the composite
     * @param compositeType The type of composite
     */
    public ALZ001MockComposite(String name, String compositeType) {
        super(name);
        this.children = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.compositeType = compositeType;
        
        // Set composite-specific metadata
        this.metadata.put("composite_type", compositeType);
        this.metadata.put("child_count", "0");
    }
    
    /**
     * Gets the composite type.
     *
     * @return The composite type
     */
    public String getCompositeType() {
        return compositeType;
    }
    
    /**
     * Adds a child component to this composite.
     *
     * @param component The component to add
     */
    public void addChild(ALZ001MockComponent component) {
        children.add(component);
        updateChildCount();
    }
    
    /**
     * Removes a child component from this composite.
     *
     * @param component The component to remove
     * @return true if the component was removed, false otherwise
     */
    public boolean removeChild(ALZ001MockComponent component) {
        boolean removed = children.remove(component);
        if (removed) {
            // Remove any connections involving this component
            connections.removeIf(conn -> 
                conn.getSource() == component || conn.getTarget() == component);
            updateChildCount();
        }
        return removed;
    }
    
    /**
     * Gets a child component by name.
     *
     * @param name The component name
     * @return The component, or null if not found
     */
    public ALZ001MockComponent getChild(String name) {
        for (ALZ001MockComponent child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }
    
    /**
     * Gets all child components.
     *
     * @return A list of all child components
     */
    public List<ALZ001MockComponent> getChildren() {
        return new ArrayList<>(children);
    }
    
    /**
     * Updates the child count metadata.
     */
    private void updateChildCount() {
        this.metadata.put("child_count", String.valueOf(children.size()));
    }
    
    /**
     * Connects two components with the specified connection type.
     *
     * @param source The source component
     * @param target The target component
     * @param connectionType The type of connection
     * @return The created connection
     */
    public ComponentConnection connect(ALZ001MockComponent source, ALZ001MockComponent target, String connectionType) {
        ComponentConnection connection = new ComponentConnection(source, target, connectionType);
        connections.add(connection);
        return connection;
    }
    
    /**
     * Connects two components by name with the specified connection type.
     *
     * @param sourceName The name of the source component
     * @param targetName The name of the target component
     * @param connectionType The type of connection
     * @return The created connection, or null if either component was not found
     */
    public ComponentConnection connectByName(String sourceName, String targetName, String connectionType) {
        ALZ001MockComponent source = getChild(sourceName);
        ALZ001MockComponent target = getChild(targetName);
        
        if (source == null || target == null) {
            return null;
        }
        
        return connect(source, target, connectionType);
    }
    
    /**
     * Finds a connection between two components.
     *
     * @param source The source component
     * @param target The target component
     * @return The connection, or null if not found
     */
    public ComponentConnection findConnection(ALZ001MockComponent source, ALZ001MockComponent target) {
        for (ComponentConnection connection : connections) {
            if (connection.getSource() == source && connection.getTarget() == target) {
                return connection;
            }
        }
        return null;
    }
    
    /**
     * Gets all connections.
     *
     * @return A list of all connections
     */
    public List<ComponentConnection> getConnections() {
        return new ArrayList<>(connections);
    }
    
    /**
     * Gets all active connections.
     *
     * @return A list of all active connections
     */
    public List<ComponentConnection> getActiveConnections() {
        List<ComponentConnection> active = new ArrayList<>();
        for (ComponentConnection connection : connections) {
            if (connection.isActive()) {
                active.add(connection);
            }
        }
        return active;
    }
    
    /**
     * Gets all connections of the specified type.
     *
     * @param connectionType The connection type
     * @return A list of connections of the specified type
     */
    public List<ComponentConnection> getConnectionsByType(String connectionType) {
        List<ComponentConnection> byType = new ArrayList<>();
        for (ComponentConnection connection : connections) {
            if (connection.getConnectionType().equals(connectionType)) {
                byType.add(connection);
            }
        }
        return byType;
    }
    
    /**
     * Initializes the composite and all its children.
     */
    @Override
    public void initialize() {
        if (validateConfiguration().isEmpty()) {
            // Initialize all children first
            for (ALZ001MockComponent child : children) {
                child.initialize();
            }
            
            // Then initialize this composite
            setState("READY");
        } else {
            setState("ERROR");
        }
    }
    
    /**
     * Destroys the composite and all its children.
     */
    @Override
    public void destroy() {
        // Destroy all children first
        for (ALZ001MockComponent child : children) {
            child.destroy();
        }
        
        // Clear connections
        connections.clear();
        
        // Then destroy this composite
        setState("DESTROYED");
    }
    
    /**
     * Validates the composite's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = super.validateConfiguration();
        
        // Check composite-specific configuration
        if (!configuration.containsKey("max_children")) {
            errors.put("max_children", "Missing required configuration");
        } else {
            int maxChildren = getConfig("max_children");
            if (children.size() > maxChildren) {
                errors.put("children_count", "Too many children (maximum: " + maxChildren + ")");
            }
        }
        
        return errors;
    }
}