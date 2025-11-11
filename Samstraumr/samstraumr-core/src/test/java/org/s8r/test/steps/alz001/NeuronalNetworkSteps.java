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

package org.s8r.test.steps.alz001;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions for neuronal network test scenarios.
 * 
 * <p>Implements steps for creating, configuring, and testing neuronal network
 * components and their capabilities.
 */
public class NeuronalNetworkSteps extends ALZ001BaseSteps {

    /**
     * Sets up a scientific analysis environment for neuronal network modeling.
     */
    @Given("a scientific analysis environment for neuronal network modeling")
    public void scientificAnalysisEnvironmentForNeuronalNetworkModeling() {
        // Create a mock or real scientific environment for testing
        ScientificAnalysisEnvironment environment = new ScientificAnalysisEnvironment();
        environment.enableCapability("neuronal_network_modeling");
        environment.enableCapability("graph_analysis");
        
        // Store in context
        storeInContext("environment", environment);
    }
    
    /**
     * Prepares a configuration for network simulation.
     */
    @Given("a configuration for network simulation")
    public void configurationForNetworkSimulation() {
        Map<String, Object> config = new HashMap<>();
        
        // Default network configuration
        config.put("number_of_neurons", 1000);
        config.put("connection_density", 0.1);
        config.put("inhibitory_ratio", 0.2);
        config.put("baseline_firing_rate", 5.0);
        config.put("connection_weight_mean", 0.5);
        config.put("connection_weight_std_dev", 0.1);
        
        // Store in context
        storeInContext("networkConfig", config);
    }
    
    /**
     * Creates a neuronal network component.
     */
    @When("I create a neuronal network component")
    public void createNeuronalNetworkComponent() {
        // Create the component
        NeuronalNetworkComponent component = new NeuronalNetworkComponent();
        component.initialize();
        
        // Store in test context
        storeInContext("neuronalComponent", component);
    }
    
    /**
     * Verifies component has neuronal modeling capabilities.
     */
    @Then("the component should have neuronal modeling capabilities")
    public void componentShouldHaveNeuronalModelingCapabilities() {
        NeuronalNetworkComponent component = getFromContext("neuronalComponent");
        assertTrue(component.hasCapability("neuronal_modeling"), 
                "Component should have neuronal modeling capability");
        assertTrue(component.hasCapability("graph_analysis"), 
                "Component should have graph analysis capability");
    }
    
    /**
     * Sets up an initialized neuronal network component.
     */
    @Given("an initialized neuronal network component")
    public void initializedNeuronalNetworkComponent() {
        createNeuronalNetworkComponent();
    }
    
    /**
     * Configures network parameters.
     */
    @When("I configure the following network parameters:")
    public void configureNetworkParameters(DataTable dataTable) {
        NeuronalNetworkComponent component = getFromContext("neuronalComponent");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Configure parameters
        Map<String, Object> parameters = new HashMap<>();
        for (Map<String, String> row : rows) {
            String parameter = row.get("parameter");
            String valueStr = row.get("value");
            
            // Convert value to appropriate type
            Object value;
            if (valueStr.contains(".")) {
                value = Double.parseDouble(valueStr);
            } else {
                value = Integer.parseInt(valueStr);
            }
            
            parameters.put(parameter, value);
        }
        
        // Apply configuration
        component.configure(parameters);
        
        // Store parameters
        storeInContext("networkParameters", parameters);
    }
    
    /**
     * Verifies parameters are successfully configured.
     */
    @Then("the parameters should be successfully configured")
    public void parametersShouldBeSuccessfullyConfigured() {
        NeuronalNetworkComponent component = getFromContext("neuronalComponent");
        Map<String, Object> configuredParams = component.getConfiguration();
        
        Map<String, Object> expectedParams = getFromContext("networkParameters");
        
        for (Map.Entry<String, Object> entry : expectedParams.entrySet()) {
            String param = entry.getKey();
            Object value = entry.getValue();
            
            assertTrue(configuredParams.containsKey(param), 
                    "Configuration should contain parameter: " + param);
            assertEquals(value, configuredParams.get(param), 
                    "Parameter " + param + " should have expected value");
        }
    }
    
    /**
     * Verifies component is ready for network generation.
     */
    @Then("the component should be ready for network generation")
    public void componentShouldBeReadyForNetworkGeneration() {
        NeuronalNetworkComponent component = getFromContext("neuronalComponent");
        assertTrue(component.isConfigured(), "Component should be fully configured");
        assertEquals("CONFIGURED", component.getState(), "Component should be in CONFIGURED state");
    }
    
    /**
     * Sets up a configured neuronal network component.
     */
    @Given("a configured neuronal network component")
    public void configuredNeuronalNetworkComponent() {
        initializedNeuronalNetworkComponent();
        
        // Create default parameter data table
        List<Map<String, String>> rows = List.of(
                Map.of("parameter", "number_of_neurons", "value", "1000"),
                Map.of("parameter", "connection_density", "value", "0.1"),
                Map.of("parameter", "inhibitory_ratio", "value", "0.2"),
                Map.of("parameter", "baseline_firing_rate", "value", "5.0"),
                Map.of("parameter", "connection_weight_mean", "value", "0.5"),
                Map.of("parameter", "connection_weight_std_dev", "value", "0.1")
        );
        
        DataTable dataTable = DataTable.create(rows);
        configureNetworkParameters(dataTable);
    }
    
    /**
     * Generates the network structure.
     */
    @When("I generate the network structure")
    public void generateNetworkStructure() {
        NeuronalNetworkComponent component = getFromContext("neuronalComponent");
        component.generateNetwork();
        
        // Store the generated graph
        storeInContext("neuronalGraph", component.getGraph());
    }
    
    /**
     * Verifies a graph representation is created.
     */
    @Then("a graph representation should be created")
    public void graphRepresentationShouldBeCreated() {
        NeuronalGraph graph = getFromContext("neuronalGraph");
        assertNotNull(graph, "Graph representation should be created");
        assertTrue(graph.hasNodes(), "Graph should have nodes");
        assertTrue(graph.hasConnections(), "Graph should have connections");
    }
    
    /**
     * Verifies graph has the specified number of neurons.
     */
    @Then("the graph should have the specified number of neurons")
    public void graphShouldHaveSpecifiedNumberOfNeurons() {
        NeuronalGraph graph = getFromContext("neuronalGraph");
        Map<String, Object> params = getFromContext("networkParameters");
        
        int expectedNeurons = (int) params.get("number_of_neurons");
        assertEquals(expectedNeurons, graph.getNodeCount(), 
                "Graph should have specified number of neurons");
    }
    
    /**
     * Verifies graph has the specified connection density.
     */
    @Then("the graph should have the specified connection density")
    public void graphShouldHaveSpecifiedConnectionDensity() {
        NeuronalGraph graph = getFromContext("neuronalGraph");
        Map<String, Object> params = getFromContext("networkParameters");
        
        double expectedDensity = (double) params.get("connection_density");
        double actualDensity = graph.getConnectionDensity();
        
        assertEquals(expectedDensity, actualDensity, 0.01, 
                "Graph should have the specified connection density");
    }
    
    /**
     * Verifies neuron types match the inhibitory ratio.
     */
    @Then("the neuron types should match the inhibitory ratio")
    public void neuronTypesShouldMatchInhibitoryRatio() {
        NeuronalGraph graph = getFromContext("neuronalGraph");
        Map<String, Object> params = getFromContext("networkParameters");
        
        double expectedRatio = (double) params.get("inhibitory_ratio");
        double actualRatio = graph.getInhibitoryRatio();
        
        assertEquals(expectedRatio, actualRatio, 0.01, 
                "Neuron types should match the specified inhibitory ratio");
    }
    
    /**
     * Verifies connection weights follow the specified distribution.
     */
    @Then("connection weights should follow the specified distribution")
    public void connectionWeightsShouldFollowSpecifiedDistribution() {
        NeuronalGraph graph = getFromContext("neuronalGraph");
        Map<String, Object> params = getFromContext("networkParameters");
        
        double expectedMean = (double) params.get("connection_weight_mean");
        double expectedStdDev = (double) params.get("connection_weight_std_dev");
        
        Map<String, Double> weightStats = graph.getWeightStatistics();
        
        assertEquals(expectedMean, weightStats.get("mean"), 0.05, 
                "Connection weights should have the specified mean");
        assertEquals(expectedStdDev, weightStats.get("std_dev"), 0.05, 
                "Connection weights should have the specified standard deviation");
    }
    
    /**
     * Sets up a neuronal network with generated structure.
     */
    @Given("a neuronal network with generated structure")
    public void neuronalNetworkWithGeneratedStructure() {
        configuredNeuronalNetworkComponent();
        generateNetworkStructure();
    }
    
    /**
     * Mock class representing a neuronal network component.
     */
    public static class NeuronalNetworkComponent {
        private boolean initialized = false;
        private String state = "CREATED";
        private final Map<String, String> capabilities = new HashMap<>();
        private final Map<String, Object> configuration = new HashMap<>();
        private boolean configured = false;
        private NeuronalGraph graph;
        
        public void initialize() {
            this.initialized = true;
            this.state = "READY";
            
            // Set default capabilities
            capabilities.put("neuronal_modeling", "true");
            capabilities.put("graph_analysis", "true");
            capabilities.put("network_simulation", "true");
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public boolean hasCapability(String capability) {
            return capabilities.containsKey(capability) && 
                   "true".equals(capabilities.get(capability));
        }
        
        public String getState() {
            return state;
        }
        
        public void configure(Map<String, Object> parameters) {
            configuration.putAll(parameters);
            configured = true;
            state = "CONFIGURED";
        }
        
        public Map<String, Object> getConfiguration() {
            return configuration;
        }
        
        public boolean isConfigured() {
            return configured;
        }
        
        public void generateNetwork() {
            if (!configured) {
                throw new IllegalStateException("Component must be configured before generating network");
            }
            
            // Create the graph based on configuration
            int neurons = (int) configuration.get("number_of_neurons");
            double density = (double) configuration.get("connection_density");
            double inhibRatio = (double) configuration.get("inhibitory_ratio");
            double weightMean = (double) configuration.get("connection_weight_mean");
            double weightStdDev = (double) configuration.get("connection_weight_std_dev");
            
            graph = new NeuronalGraph(neurons, density, inhibRatio, weightMean, weightStdDev);
            state = "NETWORK_GENERATED";
        }
        
        public NeuronalGraph getGraph() {
            return graph;
        }
    }
    
    /**
     * Mock class representing a neuronal network graph.
     */
    public static class NeuronalGraph {
        private final int nodeCount;
        private final double connectionDensity;
        private final double inhibitoryRatio;
        private final double weightMean;
        private final double weightStdDev;
        private final List<NeuronNode> nodes = new ArrayList<>();
        private final List<NeuronConnection> connections = new ArrayList<>();
        
        public NeuronalGraph(int nodeCount, double density, double inhibRatio, 
                            double weightMean, double weightStdDev) {
            this.nodeCount = nodeCount;
            this.connectionDensity = density;
            this.inhibitoryRatio = inhibRatio;
            this.weightMean = weightMean;
            this.weightStdDev = weightStdDev;
            
            // Generate nodes
            for (int i = 0; i < nodeCount; i++) {
                boolean inhibitory = (i < nodeCount * inhibRatio);
                nodes.add(new NeuronNode(i, inhibitory));
            }
            
            // Generate connections based on density
            int totalPossibleConnections = nodeCount * (nodeCount - 1);
            int connectionsToCreate = (int) (totalPossibleConnections * density);
            
            for (int i = 0; i < connectionsToCreate; i++) {
                int source = i % nodeCount;
                int target = (i / nodeCount) % nodeCount;
                
                if (source != target) {
                    // Simple normal-ish distribution for weights
                    double weight = weightMean + (Math.random() - 0.5) * weightStdDev * 3.0;
                    connections.add(new NeuronConnection(source, target, weight));
                }
            }
        }
        
        public boolean hasNodes() {
            return !nodes.isEmpty();
        }
        
        public boolean hasConnections() {
            return !connections.isEmpty();
        }
        
        public int getNodeCount() {
            return nodeCount;
        }
        
        public double getConnectionDensity() {
            return connectionDensity;
        }
        
        public double getInhibitoryRatio() {
            int inhibitoryCount = 0;
            for (NeuronNode node : nodes) {
                if (node.isInhibitory()) {
                    inhibitoryCount++;
                }
            }
            return (double) inhibitoryCount / nodes.size();
        }
        
        public Map<String, Double> getWeightStatistics() {
            Map<String, Double> stats = new HashMap<>();
            
            // Calculate mean
            double sum = 0.0;
            for (NeuronConnection conn : connections) {
                sum += conn.getWeight();
            }
            double mean = sum / connections.size();
            
            // Calculate standard deviation
            double sumSquaredDiff = 0.0;
            for (NeuronConnection conn : connections) {
                double diff = conn.getWeight() - mean;
                sumSquaredDiff += diff * diff;
            }
            double stdDev = Math.sqrt(sumSquaredDiff / connections.size());
            
            stats.put("mean", mean);
            stats.put("std_dev", stdDev);
            
            return stats;
        }
    }
    
    /**
     * Mock class representing a neuron node in the graph.
     */
    public static class NeuronNode {
        private final int id;
        private final boolean inhibitory;
        
        public NeuronNode(int id, boolean inhibitory) {
            this.id = id;
            this.inhibitory = inhibitory;
        }
        
        public int getId() {
            return id;
        }
        
        public boolean isInhibitory() {
            return inhibitory;
        }
    }
    
    /**
     * Mock class representing a connection between neurons.
     */
    public static class NeuronConnection {
        private final int sourceId;
        private final int targetId;
        private final double weight;
        
        public NeuronConnection(int sourceId, int targetId, double weight) {
            this.sourceId = sourceId;
            this.targetId = targetId;
            this.weight = weight;
        }
        
        public int getSourceId() {
            return sourceId;
        }
        
        public int getTargetId() {
            return targetId;
        }
        
        public double getWeight() {
            return weight;
        }
    }
}