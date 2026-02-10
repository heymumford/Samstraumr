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

package org.s8r.test.steps.alz001.mock.machine;

import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;
import org.s8r.test.steps.alz001.mock.composite.ALZ001MockComposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Base class for all mock machines in the ALZ001 test suite.
 * 
 * <p>A machine represents the highest level of abstraction in the ALZ001 architecture,
 * coordinating multiple composites to perform complex simulations and analyses. 
 * Machines are responsible for data flow between composites, executing 
 * simulation workflows, and ensuring proper synchronization.
 * 
 * <p>This class extends ALZ001MockComponent and adds machine-specific capabilities
 * while maintaining compatibility with the component API.
 */
public abstract class ALZ001MockMachine extends ALZ001MockComponent {
    
    /**
     * Represents a connection between two composites within a machine.
     */
    public static class CompositeConnection {
        private final ALZ001MockComposite source;
        private final ALZ001MockComposite target;
        private final String connectionType;
        private final Map<String, Object> properties;
        private boolean active;
        
        /**
         * Creates a new composite connection.
         *
         * @param source The source composite
         * @param target The target composite
         * @param connectionType The type of connection
         */
        public CompositeConnection(ALZ001MockComposite source, ALZ001MockComposite target, String connectionType) {
            this.source = source;
            this.target = target;
            this.connectionType = connectionType;
            this.properties = new HashMap<>();
            this.active = true;
        }
        
        /**
         * Gets the source composite.
         *
         * @return The source composite
         */
        public ALZ001MockComposite getSource() {
            return source;
        }
        
        /**
         * Gets the target composite.
         *
         * @return The target composite
         */
        public ALZ001MockComposite getTarget() {
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
     * Represents a data flow between composites in the machine.
     */
    public static class DataFlow {
        private final String name;
        private final CompositeConnection connection;
        private final String dataType;
        private final Map<String, Object> metrics;
        private final List<DataTransfer> history;
        
        /**
         * Creates a new data flow.
         *
         * @param name The name of the data flow
         * @param connection The composite connection
         * @param dataType The type of data being transferred
         */
        public DataFlow(String name, CompositeConnection connection, String dataType) {
            this.name = name;
            this.connection = connection;
            this.dataType = dataType;
            this.metrics = new ConcurrentHashMap<>();
            this.history = new CopyOnWriteArrayList<>();
        }
        
        /**
         * Gets the data flow name.
         *
         * @return The data flow name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the connection associated with this data flow.
         *
         * @return The connection
         */
        public CompositeConnection getConnection() {
            return connection;
        }
        
        /**
         * Gets the data type.
         *
         * @return The data type
         */
        public String getDataType() {
            return dataType;
        }
        
        /**
         * Records a data transfer in this flow.
         *
         * @param data The data being transferred
         * @param timestamp The timestamp of the transfer
         * @return The created data transfer record
         */
        public DataTransfer recordTransfer(Object data, long timestamp) {
            DataTransfer transfer = new DataTransfer(data, timestamp);
            history.add(transfer);
            updateMetrics(transfer);
            return transfer;
        }
        
        /**
         * Updates metrics based on a data transfer.
         *
         * @param transfer The data transfer
         */
        private void updateMetrics(DataTransfer transfer) {
            metrics.put("last_transfer_time", transfer.getTimestamp());
            metrics.put("transfer_count", history.size());
            
            // Calculate transfer rate (transfers per second)
            if (history.size() >= 2) {
                DataTransfer first = history.get(0);
                DataTransfer last = history.get(history.size() - 1);
                long timeSpan = last.getTimestamp() - first.getTimestamp();
                if (timeSpan > 0) {
                    double transfersPerSecond = history.size() / (timeSpan / 1000.0);
                    metrics.put("transfers_per_second", transfersPerSecond);
                }
            }
        }
        
        /**
         * Gets a metric value.
         *
         * @param <T> The type of the metric value
         * @param key The metric key
         * @return The metric value, cast to the expected type
         */
        @SuppressWarnings("unchecked")
        public <T> T getMetric(String key) {
            return (T) metrics.get(key);
        }
        
        /**
         * Gets the transfer history.
         *
         * @return The transfer history
         */
        public List<DataTransfer> getHistory() {
            return new ArrayList<>(history);
        }
    }
    
    /**
     * Represents a single data transfer between composites.
     */
    public static class DataTransfer {
        private final Object data;
        private final long timestamp;
        
        /**
         * Creates a new data transfer.
         *
         * @param data The data being transferred
         * @param timestamp The timestamp of the transfer
         */
        public DataTransfer(Object data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
        
        /**
         * Gets the transferred data.
         *
         * @param <T> The type of the data
         * @return The data, cast to the expected type
         */
        @SuppressWarnings("unchecked")
        public <T> T getData() {
            return (T) data;
        }
        
        /**
         * Gets the timestamp of the transfer.
         *
         * @return The timestamp
         */
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    /**
     * The composites in this machine.
     */
    protected final List<ALZ001MockComposite> composites;
    
    /**
     * The connections between composites.
     */
    protected final List<CompositeConnection> connections;
    
    /**
     * The data flows between composites.
     */
    protected final List<DataFlow> dataFlows;
    
    /**
     * The machine type.
     */
    protected final String machineType;
    
    /**
     * Thread pool for parallel execution of composite operations.
     */
    protected ExecutorService executorService;
    
    /**
     * Constructs a new mock machine with the specified name and type.
     *
     * @param name The unique name of the machine
     * @param machineType The type of machine
     * @param threadPoolSize The size of the thread pool for parallel execution
     */
    public ALZ001MockMachine(String name, String machineType, int threadPoolSize) {
        super(name);
        this.composites = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.dataFlows = new ArrayList<>();
        this.machineType = machineType;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        
        // Set machine-specific metadata
        this.metadata.put("machine_type", machineType);
        this.metadata.put("composite_count", "0");
        this.metadata.put("thread_pool_size", String.valueOf(threadPoolSize));
    }
    
    /**
     * Alternative constructor with default thread pool size of 4.
     *
     * @param name The unique name of the machine
     * @param machineType The type of machine
     */
    public ALZ001MockMachine(String name, String machineType) {
        this(name, machineType, 4);
    }
    
    /**
     * Gets the machine type.
     *
     * @return The machine type
     */
    public String getMachineType() {
        return machineType;
    }
    
    /**
     * Adds a composite to this machine.
     *
     * @param composite The composite to add
     */
    public void addComposite(ALZ001MockComposite composite) {
        composites.add(composite);
        updateCompositeCount();
    }
    
    /**
     * Removes a composite from this machine.
     *
     * @param composite The composite to remove
     * @return true if the composite was removed, false otherwise
     */
    public boolean removeComposite(ALZ001MockComposite composite) {
        boolean removed = composites.remove(composite);
        if (removed) {
            // Remove any connections involving this composite
            connections.removeIf(conn -> 
                conn.getSource() == composite || conn.getTarget() == composite);
            
            // Remove any data flows involving those connections
            dataFlows.removeIf(flow -> 
                flow.getConnection().getSource() == composite || 
                flow.getConnection().getTarget() == composite);
            
            updateCompositeCount();
        }
        return removed;
    }
    
    /**
     * Gets a composite by name.
     *
     * @param name The composite name
     * @return The composite, or null if not found
     */
    public ALZ001MockComposite getComposite(String name) {
        for (ALZ001MockComposite composite : composites) {
            if (composite.getName().equals(name)) {
                return composite;
            }
        }
        return null;
    }
    
    /**
     * Gets all composites.
     *
     * @return A list of all composites
     */
    public List<ALZ001MockComposite> getComposites() {
        return new ArrayList<>(composites);
    }
    
    /**
     * Updates the composite count metadata.
     */
    private void updateCompositeCount() {
        this.metadata.put("composite_count", String.valueOf(composites.size()));
    }
    
    /**
     * Connects two composites with the specified connection type.
     *
     * @param source The source composite
     * @param target The target composite
     * @param connectionType The type of connection
     * @return The created connection
     */
    public CompositeConnection connect(ALZ001MockComposite source, ALZ001MockComposite target, String connectionType) {
        CompositeConnection connection = new CompositeConnection(source, target, connectionType);
        connections.add(connection);
        return connection;
    }
    
    /**
     * Connects two composites by name with the specified connection type.
     *
     * @param sourceName The name of the source composite
     * @param targetName The name of the target composite
     * @param connectionType The type of connection
     * @return The created connection, or null if either composite was not found
     */
    public CompositeConnection connectByName(String sourceName, String targetName, String connectionType) {
        ALZ001MockComposite source = getComposite(sourceName);
        ALZ001MockComposite target = getComposite(targetName);
        
        if (source == null || target == null) {
            return null;
        }
        
        return connect(source, target, connectionType);
    }
    
    /**
     * Creates a data flow between two composites.
     *
     * @param name The name of the data flow
     * @param source The source composite
     * @param target The target composite
     * @param connectionType The type of connection
     * @param dataType The type of data being transferred
     * @return The created data flow
     */
    public DataFlow createDataFlow(String name, ALZ001MockComposite source, ALZ001MockComposite target, 
                                   String connectionType, String dataType) {
        CompositeConnection connection = connect(source, target, connectionType);
        DataFlow dataFlow = new DataFlow(name, connection, dataType);
        dataFlows.add(dataFlow);
        return dataFlow;
    }
    
    /**
     * Creates a data flow between two composites by name.
     *
     * @param name The name of the data flow
     * @param sourceName The name of the source composite
     * @param targetName The name of the target composite
     * @param connectionType The type of connection
     * @param dataType The type of data being transferred
     * @return The created data flow, or null if either composite was not found
     */
    public DataFlow createDataFlowByName(String name, String sourceName, String targetName, 
                                        String connectionType, String dataType) {
        CompositeConnection connection = connectByName(sourceName, targetName, connectionType);
        
        if (connection == null) {
            return null;
        }
        
        DataFlow dataFlow = new DataFlow(name, connection, dataType);
        dataFlows.add(dataFlow);
        return dataFlow;
    }
    
    /**
     * Gets a data flow by name.
     *
     * @param name The data flow name
     * @return The data flow, or null if not found
     */
    public DataFlow getDataFlow(String name) {
        for (DataFlow dataFlow : dataFlows) {
            if (dataFlow.getName().equals(name)) {
                return dataFlow;
            }
        }
        return null;
    }
    
    /**
     * Gets all data flows.
     *
     * @return A list of all data flows
     */
    public List<DataFlow> getDataFlows() {
        return new ArrayList<>(dataFlows);
    }
    
    /**
     * Initializes the machine and all its composites.
     */
    @Override
    public void initialize() {
        if (validateConfiguration().isEmpty()) {
            // Initialize all composites in parallel
            List<Future<?>> futures = new ArrayList<>();
            
            for (ALZ001MockComposite composite : composites) {
                futures.add(executorService.submit(composite::initialize));
            }
            
            // Wait for all initializations to complete
            try {
                for (Future<?> future : futures) {
                    future.get();
                }
                setState("READY");
            } catch (Exception e) {
                setState("ERROR");
                setMetadata("error_message", e.getMessage());
            }
        } else {
            setState("ERROR");
        }
    }
    
    /**
     * Destroys the machine and all its composites.
     */
    @Override
    public void destroy() {
        try {
            // Destroy all composites in parallel
            List<Future<?>> futures = new ArrayList<>();
            
            for (ALZ001MockComposite composite : composites) {
                futures.add(executorService.submit(composite::destroy));
            }
            
            // Wait for all destructions to complete
            for (Future<?> future : futures) {
                future.get();
            }
            
            // Clear connections and data flows
            connections.clear();
            dataFlows.clear();
            
            // Shutdown executor service
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
            
            setState("DESTROYED");
        } catch (Exception e) {
            setState("ERROR");
            setMetadata("error_message", e.getMessage());
        }
    }
    
    /**
     * Validates the machine's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = super.validateConfiguration();
        
        // Check machine-specific configuration
        if (!configuration.containsKey("max_composites")) {
            errors.put("max_composites", "Missing required configuration");
        } else {
            int maxComposites = getConfig("max_composites");
            if (composites.size() > maxComposites) {
                errors.put("composite_count", "Too many composites (maximum: " + maxComposites + ")");
            }
        }
        
        return errors;
    }
}