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
import org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent;
import org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent.Neuron;
import org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent.Synapse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Mock composite for neuronal network analysis in Alzheimer's disease modeling.
 * 
 * <p>This composite coordinates multiple neuronal network components to model
 * different brain regions and their interconnections. It supports multi-regional
 * analysis, cross-region signaling, and hierarchical network structures.
 */
public class NeuronalNetworkComposite extends ALZ001MockComposite {
    
    /**
     * Represents a brain region with its own neuronal network.
     */
    public static class BrainRegion {
        private final String name;
        private final String function;
        private final NeuronalNetworkComponent network;
        private final Map<String, Object> properties;
        private double vulnerabilityFactor; // 0-1, higher means more vulnerable to degeneration
        
        /**
         * Creates a new brain region.
         *
         * @param name The region name
         * @param function The region function
         * @param network The neuronal network for this region
         */
        public BrainRegion(String name, String function, NeuronalNetworkComponent network) {
            this.name = name;
            this.function = function;
            this.network = network;
            this.properties = new HashMap<>();
            this.vulnerabilityFactor = 0.5; // Default medium vulnerability
        }
        
        /**
         * Gets the region name.
         *
         * @return The region name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the region function.
         *
         * @return The region function
         */
        public String getFunction() {
            return function;
        }
        
        /**
         * Gets the neuronal network.
         *
         * @return The neuronal network
         */
        public NeuronalNetworkComponent getNetwork() {
            return network;
        }
        
        /**
         * Gets the vulnerability factor.
         *
         * @return The vulnerability factor
         */
        public double getVulnerabilityFactor() {
            return vulnerabilityFactor;
        }
        
        /**
         * Sets the vulnerability factor.
         *
         * @param factor The vulnerability factor (0-1)
         */
        public void setVulnerabilityFactor(double factor) {
            this.vulnerabilityFactor = Math.max(0.0, Math.min(1.0, factor));
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
    }
    
    /**
     * Represents a connection between brain regions.
     */
    public static class RegionConnection {
        private final BrainRegion source;
        private final BrainRegion target;
        private final String connectionType; // e.g., "structural", "functional"
        private double strength;
        private double integrityFactor; // 1.0 = full integrity, 0.0 = degraded
        private final Map<String, Object> properties;
        
        /**
         * Creates a new region connection.
         *
         * @param source The source region
         * @param target The target region
         * @param connectionType The connection type
         * @param strength The connection strength
         */
        public RegionConnection(BrainRegion source, BrainRegion target, String connectionType, double strength) {
            this.source = source;
            this.target = target;
            this.connectionType = connectionType;
            this.strength = strength;
            this.integrityFactor = 1.0;
            this.properties = new HashMap<>();
        }
        
        /**
         * Gets the source region.
         *
         * @return The source region
         */
        public BrainRegion getSource() {
            return source;
        }
        
        /**
         * Gets the target region.
         *
         * @return The target region
         */
        public BrainRegion getTarget() {
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
         * Gets the connection strength.
         *
         * @return The connection strength
         */
        public double getStrength() {
            return strength;
        }
        
        /**
         * Sets the connection strength.
         *
         * @param strength The connection strength
         */
        public void setStrength(double strength) {
            this.strength = strength;
        }
        
        /**
         * Gets the integrity factor.
         *
         * @return The integrity factor
         */
        public double getIntegrityFactor() {
            return integrityFactor;
        }
        
        /**
         * Sets the integrity factor.
         *
         * @param factor The integrity factor (0-1)
         */
        public void setIntegrityFactor(double factor) {
            this.integrityFactor = Math.max(0.0, Math.min(1.0, factor));
        }
        
        /**
         * Gets the effective strength (strength * integrity).
         *
         * @return The effective strength
         */
        public double getEffectiveStrength() {
            return strength * integrityFactor;
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
    }
    
    /**
     * Represents a functional network spanning multiple regions.
     */
    public static class FunctionalNetwork {
        private final String name;
        private final String function;
        private final List<BrainRegion> regions;
        private final Map<String, Object> properties;
        
        /**
         * Creates a new functional network.
         *
         * @param name The network name
         * @param function The network function
         */
        public FunctionalNetwork(String name, String function) {
            this.name = name;
            this.function = function;
            this.regions = new ArrayList<>();
            this.properties = new HashMap<>();
        }
        
        /**
         * Gets the network name.
         *
         * @return The network name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the network function.
         *
         * @return The network function
         */
        public String getFunction() {
            return function;
        }
        
        /**
         * Adds a region to this functional network.
         *
         * @param region The region to add
         */
        public void addRegion(BrainRegion region) {
            if (!regions.contains(region)) {
                regions.add(region);
            }
        }
        
        /**
         * Removes a region from this functional network.
         *
         * @param region The region to remove
         * @return true if the region was removed, false otherwise
         */
        public boolean removeRegion(BrainRegion region) {
            return regions.remove(region);
        }
        
        /**
         * Gets all regions in this functional network.
         *
         * @return A list of regions
         */
        public List<BrainRegion> getRegions() {
            return new ArrayList<>(regions);
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
    }
    
    private final List<BrainRegion> regions = new ArrayList<>();
    private final List<RegionConnection> regionConnections = new ArrayList<>();
    private final List<FunctionalNetwork> functionalNetworks = new ArrayList<>();
    private final Random random = new Random();
    
    /**
     * Creates a new neuronal network composite with the given name.
     *
     * @param name The composite name
     */
    public NeuronalNetworkComposite(String name) {
        super(name, "NEURONAL_NETWORK");
    }
    
    /**
     * Adds a neuronal network component to this composite.
     *
     * @param component The component to add
     */
    public void addNetworkComponent(NeuronalNetworkComponent component) {
        addChild(component);
    }
    
    /**
     * Gets all neuronal network components in this composite.
     *
     * @return A list of neuronal network components
     */
    public List<NeuronalNetworkComponent> getNetworkComponents() {
        return children.stream()
                .filter(c -> c instanceof NeuronalNetworkComponent)
                .map(c -> (NeuronalNetworkComponent) c)
                .collect(Collectors.toList());
    }
    
    /**
     * Creates a new brain region.
     *
     * @param name The region name
     * @param function The region function
     * @param networkComponent The neuronal network component for this region
     * @return The created brain region
     */
    public BrainRegion createRegion(String name, String function, NeuronalNetworkComponent networkComponent) {
        BrainRegion region = new BrainRegion(name, function, networkComponent);
        regions.add(region);
        return region;
    }
    
    /**
     * Gets a brain region by name.
     *
     * @param name The region name
     * @return The region, or null if not found
     */
    public BrainRegion getRegion(String name) {
        for (BrainRegion region : regions) {
            if (region.getName().equals(name)) {
                return region;
            }
        }
        return null;
    }
    
    /**
     * Gets all brain regions.
     *
     * @return A list of all brain regions
     */
    public List<BrainRegion> getRegions() {
        return new ArrayList<>(regions);
    }
    
    /**
     * Creates a connection between two brain regions.
     *
     * @param source The source region
     * @param target The target region
     * @param connectionType The connection type
     * @param strength The connection strength
     * @return The created region connection
     */
    public RegionConnection connectRegions(BrainRegion source, BrainRegion target, String connectionType, double strength) {
        RegionConnection connection = new RegionConnection(source, target, connectionType, strength);
        regionConnections.add(connection);
        return connection;
    }
    
    /**
     * Gets all region connections.
     *
     * @return A list of all region connections
     */
    public List<RegionConnection> getRegionConnections() {
        return new ArrayList<>(regionConnections);
    }
    
    /**
     * Gets connections from a specific region.
     *
     * @param region The source region
     * @return A list of connections from the specified region
     */
    public List<RegionConnection> getConnectionsFrom(BrainRegion region) {
        List<RegionConnection> result = new ArrayList<>();
        for (RegionConnection connection : regionConnections) {
            if (connection.getSource() == region) {
                result.add(connection);
            }
        }
        return result;
    }
    
    /**
     * Gets connections to a specific region.
     *
     * @param region The target region
     * @return A list of connections to the specified region
     */
    public List<RegionConnection> getConnectionsTo(BrainRegion region) {
        List<RegionConnection> result = new ArrayList<>();
        for (RegionConnection connection : regionConnections) {
            if (connection.getTarget() == region) {
                result.add(connection);
            }
        }
        return result;
    }
    
    /**
     * Creates a new functional network.
     *
     * @param name The network name
     * @param function The network function
     * @return The created functional network
     */
    public FunctionalNetwork createFunctionalNetwork(String name, String function) {
        FunctionalNetwork network = new FunctionalNetwork(name, function);
        functionalNetworks.add(network);
        return network;
    }
    
    /**
     * Gets a functional network by name.
     *
     * @param name The network name
     * @return The network, or null if not found
     */
    public FunctionalNetwork getFunctionalNetwork(String name) {
        for (FunctionalNetwork network : functionalNetworks) {
            if (network.getName().equals(name)) {
                return network;
            }
        }
        return null;
    }
    
    /**
     * Gets all functional networks.
     *
     * @return A list of all functional networks
     */
    public List<FunctionalNetwork> getFunctionalNetworks() {
        return new ArrayList<>(functionalNetworks);
    }
    
    /**
     * Initializes a default brain network with common regions.
     * 
     * @param includeFunctionalNetworks Whether to include functional networks
     */
    public void initializeDefaultBrainNetwork(boolean includeFunctionalNetworks) {
        // Clear existing data
        regions.clear();
        regionConnections.clear();
        functionalNetworks.clear();
        
        // Create regions with different network topologies
        List<String[]> regionData = new ArrayList<>();
        regionData.add(new String[]{"prefrontal_cortex", "executive_function", "small-world"});
        regionData.add(new String[]{"hippocampus", "memory_formation", "small-world"});
        regionData.add(new String[]{"entorhinal_cortex", "memory_processing", "scale-free"});
        regionData.add(new String[]{"temporal_lobe", "language_processing", "small-world"});
        regionData.add(new String[]{"parietal_lobe", "sensory_integration", "scale-free"});
        
        // Create the regions with appropriate networks
        Map<String, BrainRegion> regionMap = new HashMap<>();
        for (String[] data : regionData) {
            String regionName = data[0];
            String function = data[1];
            String topology = data[2];
            
            // Create network component for this region
            NeuronalNetworkComponent network = new NeuronalNetworkComponent(regionName + "_network");
            network.configure(createRegionNetworkConfig(regionName, topology));
            addNetworkComponent(network);
            
            // Configure network topology
            if ("small-world".equals(topology)) {
                network.configureSmallWorldTopology(200, 12, 0.1);
            } else if ("scale-free".equals(topology)) {
                network.configureScaleFreeTopology(200, 5);
            } else {
                network.configureRandomTopology(200, 1000, 0.2);
            }
            
            // Create region and store in map
            BrainRegion region = createRegion(regionName, function, network);
            regionMap.put(regionName, region);
            
            // Set vulnerability factors - hippocampus and entorhinal cortex are more vulnerable in AD
            if ("hippocampus".equals(regionName) || "entorhinal_cortex".equals(regionName)) {
                region.setVulnerabilityFactor(0.8);
            } else if ("prefrontal_cortex".equals(regionName)) {
                region.setVulnerabilityFactor(0.6);
            } else {
                region.setVulnerabilityFactor(0.4);
            }
        }
        
        // Create connections between regions
        List<String[]> connectionData = new ArrayList<>();
        connectionData.add(new String[]{"prefrontal_cortex", "hippocampus", "structural", "0.7"});
        connectionData.add(new String[]{"hippocampus", "entorhinal_cortex", "structural", "0.9"});
        connectionData.add(new String[]{"entorhinal_cortex", "temporal_lobe", "structural", "0.8"});
        connectionData.add(new String[]{"temporal_lobe", "parietal_lobe", "structural", "0.6"});
        connectionData.add(new String[]{"prefrontal_cortex", "parietal_lobe", "structural", "0.5"});
        connectionData.add(new String[]{"prefrontal_cortex", "temporal_lobe", "functional", "0.4"});
        connectionData.add(new String[]{"hippocampus", "temporal_lobe", "functional", "0.7"});
        
        for (String[] data : connectionData) {
            String sourceName = data[0];
            String targetName = data[1];
            String type = data[2];
            double strength = Double.parseDouble(data[3]);
            
            BrainRegion source = regionMap.get(sourceName);
            BrainRegion target = regionMap.get(targetName);
            
            if (source != null && target != null) {
                connectRegions(source, target, type, strength);
            }
        }
        
        // Create functional networks if requested
        if (includeFunctionalNetworks) {
            // Memory network
            FunctionalNetwork memoryNetwork = createFunctionalNetwork("memory_network", "memory_processing");
            memoryNetwork.addRegion(regionMap.get("hippocampus"));
            memoryNetwork.addRegion(regionMap.get("entorhinal_cortex"));
            memoryNetwork.addRegion(regionMap.get("prefrontal_cortex"));
            
            // Language network
            FunctionalNetwork languageNetwork = createFunctionalNetwork("language_network", "language_processing");
            languageNetwork.addRegion(regionMap.get("temporal_lobe"));
            languageNetwork.addRegion(regionMap.get("prefrontal_cortex"));
            
            // Attention network
            FunctionalNetwork attentionNetwork = createFunctionalNetwork("attention_network", "attention_control");
            attentionNetwork.addRegion(regionMap.get("prefrontal_cortex"));
            attentionNetwork.addRegion(regionMap.get("parietal_lobe"));
        }
    }
    
    /**
     * Creates a configuration map for a region's neuronal network.
     *
     * @param regionName The region name
     * @param topology The topology type
     * @return A configuration map
     */
    private Map<String, Object> createRegionNetworkConfig(String regionName, String topology) {
        Map<String, Object> config = new HashMap<>();
        config.put("default_connectivity_threshold", 0.6);
        config.put("neuronal_update_interval_ms", 100);
        config.put("network_size", 200);
        config.put("synaptic_plasticity_enabled", true);
        config.put("region_name", regionName);
        config.put("topology", topology);
        return config;
    }
    
    /**
     * Simulates signal propagation across brain regions.
     *
     * @param startRegion The region where the signal starts
     * @param signalStrength The initial signal strength
     * @return A map of region names to activation levels
     */
    public Map<String, Double> simulateSignalPropagation(BrainRegion startRegion, double signalStrength) {
        Map<String, Double> regionActivations = new HashMap<>();
        Set<String> processedRegions = new HashSet<>();
        List<BrainRegion> queue = new ArrayList<>();
        
        // Start with initial region
        regionActivations.put(startRegion.getName(), signalStrength);
        queue.add(startRegion);
        processedRegions.add(startRegion.getName());
        
        // First, propagate signal within the start region's network
        NeuronalNetworkComponent startNetwork = startRegion.getNetwork();
        if (startNetwork != null && !startNetwork.getNeurons().isEmpty()) {
            int startNeuronId = 0; // Start with first neuron
            startNetwork.activateNeuron(startNeuronId, signalStrength);
        }
        
        // Breadth-first propagation across regions
        while (!queue.isEmpty()) {
            BrainRegion currentRegion = queue.remove(0);
            double currentActivation = regionActivations.get(currentRegion.getName());
            
            // Propagate to connected regions
            List<RegionConnection> outgoing = getConnectionsFrom(currentRegion);
            for (RegionConnection connection : outgoing) {
                BrainRegion targetRegion = connection.getTarget();
                
                // Calculate signal decay based on connection strength and integrity
                double signalDecay = 1.0 - connection.getEffectiveStrength();
                double propagatedSignal = currentActivation * (1.0 - signalDecay);
                
                // Update target region activation
                String targetName = targetRegion.getName();
                double currentTargetActivation = regionActivations.getOrDefault(targetName, 0.0);
                double newActivation = Math.max(currentTargetActivation, propagatedSignal);
                regionActivations.put(targetName, newActivation);
                
                // Also propagate signal within the target region's network
                NeuronalNetworkComponent targetNetwork = targetRegion.getNetwork();
                if (targetNetwork != null && !targetNetwork.getNeurons().isEmpty()) {
                    int targetNeuronId = 0; // Start with first neuron
                    targetNetwork.activateNeuron(targetNeuronId, propagatedSignal);
                }
                
                // Add to queue if not processed and signal is significant
                if (!processedRegions.contains(targetName) && propagatedSignal > 0.05) {
                    queue.add(targetRegion);
                    processedRegions.add(targetName);
                }
            }
        }
        
        return regionActivations;
    }
    
    /**
     * Simulates network degeneration with region-specific vulnerability factors.
     *
     * @param degradationRate The base degradation rate
     * @param timePoints The number of time points to simulate
     * @return A map of region names to connectivity values over time
     */
    public Map<String, List<Double>> simulateRegionalDegeneration(double degradationRate, int timePoints) {
        Map<String, List<Double>> results = new HashMap<>();
        
        // For each region, simulate degeneration with its vulnerability factor
        for (BrainRegion region : regions) {
            String regionName = region.getName();
            double vulnerability = region.getVulnerabilityFactor();
            NeuronalNetworkComponent network = region.getNetwork();
            
            // Skip if network is not available
            if (network == null) {
                continue;
            }
            
            // Apply vulnerability factor to degradation rate
            double adjustedRate = degradationRate * vulnerability;
            
            // Simulate degeneration
            Map<Integer, Double> degradationResult = network.simulateDegeneration(adjustedRate, timePoints);
            
            // Convert to list for consistent results format
            List<Double> connectivityValues = new ArrayList<>();
            for (int i = 0; i < timePoints; i++) {
                connectivityValues.add(degradationResult.getOrDefault(i, 1.0));
            }
            
            results.put(regionName, connectivityValues);
        }
        
        // Also simulate degradation of region connections
        for (RegionConnection connection : regionConnections) {
            // Store original integrity factors to restore later
            double originalIntegrity = connection.getIntegrityFactor();
            
            // Calculate vulnerability as average of source and target
            double sourceVulnerability = connection.getSource().getVulnerabilityFactor();
            double targetVulnerability = connection.getTarget().getVulnerabilityFactor();
            double connectionVulnerability = (sourceVulnerability + targetVulnerability) / 2.0;
            
            // Apply degradation over time
            for (int t = 1; t < timePoints; t++) {
                double adjustedRate = degradationRate * connectionVulnerability;
                double timeProgress = t / (double) timePoints;
                double newIntegrity = originalIntegrity * (1.0 - adjustedRate * timeProgress);
                connection.setIntegrityFactor(newIntegrity);
            }
            
            // Restore original integrity
            connection.setIntegrityFactor(originalIntegrity);
        }
        
        return results;
    }
    
    /**
     * Calculates global network measures across all regions.
     *
     * @return A map of global network measures
     */
    public Map<String, Double> calculateGlobalNetworkMeasures() {
        Map<String, Double> measures = new HashMap<>();
        
        // Global connectivity measure (average of all regions)
        double totalConnectivity = 0.0;
        int regionCount = 0;
        for (BrainRegion region : regions) {
            NeuronalNetworkComponent network = region.getNetwork();
            if (network != null) {
                totalConnectivity += network.calculateConnectivity();
                regionCount++;
            }
        }
        measures.put("global_connectivity", regionCount > 0 ? totalConnectivity / regionCount : 0.0);
        
        // Region connection integrity (average across all region connections)
        double totalIntegrity = 0.0;
        for (RegionConnection connection : regionConnections) {
            totalIntegrity += connection.getIntegrityFactor();
        }
        measures.put("connection_integrity", regionConnections.isEmpty() ? 1.0 : totalIntegrity / regionConnections.size());
        
        // Calculate average clustering coefficient across regions
        double totalClustering = 0.0;
        int clusteringCount = 0;
        for (BrainRegion region : regions) {
            NeuronalNetworkComponent network = region.getNetwork();
            if (network != null) {
                totalClustering += network.calculateClusteringCoefficient();
                clusteringCount++;
            }
        }
        measures.put("global_clustering", clusteringCount > 0 ? totalClustering / clusteringCount : 0.0);
        
        // Graph-theoretic measures for the region connection network
        
        // Region connection density
        int possibleConnections = regions.size() * (regions.size() - 1);
        measures.put("region_connection_density", possibleConnections > 0 ? 
                regionConnections.size() / (double) possibleConnections : 0.0);
        
        return measures;
    }
    
    /**
     * Simulates the impact of protein levels on network degeneration.
     *
     * @param proteinLevels Map of protein types to levels
     * @param timePoints Number of time points to simulate
     * @return A map of region names to connectivity values over time
     */
    public Map<String, List<Double>> simulateProteinImpact(Map<String, Double> proteinLevels, int timePoints) {
        Map<String, List<Double>> results = new HashMap<>();
        
        // Create impact factors based on protein types
        Map<String, Double> impactFactors = new HashMap<>();
        impactFactors.put("amyloid", 0.05);
        impactFactors.put("tau", 0.08);
        impactFactors.put("phosphorylated_tau", 0.12);
        impactFactors.put("neurofilament", 0.03);
        
        // For each region, simulate protein impact
        for (BrainRegion region : regions) {
            String regionName = region.getName();
            NeuronalNetworkComponent network = region.getNetwork();
            
            if (network == null) {
                continue;
            }
            
            // Apply vulnerability factor to protein impact
            double vulnerability = region.getVulnerabilityFactor();
            Map<String, Double> adjustedImpactFactors = new HashMap<>();
            
            for (Map.Entry<String, Double> entry : impactFactors.entrySet()) {
                adjustedImpactFactors.put(entry.getKey(), entry.getValue() * vulnerability);
            }
            
            // Simulate protein impact
            List<Double> connectivityValues = network.simulateProteinImpact(proteinLevels, adjustedImpactFactors, timePoints);
            results.put(regionName, connectivityValues);
        }
        
        return results;
    }
    
    /**
     * Analyzes functional network integrity.
     *
     * @return A map of functional network names to integrity measures
     */
    public Map<String, Double> analyzeFunctionalNetworkIntegrity() {
        Map<String, Double> results = new HashMap<>();
        
        for (FunctionalNetwork network : functionalNetworks) {
            double integrity = calculateFunctionalNetworkIntegrity(network);
            results.put(network.getName(), integrity);
        }
        
        return results;
    }
    
    /**
     * Calculates integrity measure for a functional network.
     *
     * @param network The functional network
     * @return The integrity measure (0-1)
     */
    private double calculateFunctionalNetworkIntegrity(FunctionalNetwork network) {
        List<BrainRegion> netRegions = network.getRegions();
        
        if (netRegions.size() < 2) {
            return 1.0; // Single region networks are intact by definition
        }
        
        // Count connections between regions in this functional network
        double totalPossibleConnections = netRegions.size() * (netRegions.size() - 1);
        double weightedConnectionSum = 0.0;
        
        for (BrainRegion source : netRegions) {
            for (BrainRegion target : netRegions) {
                if (source != target) {
                    // Find if there's a connection between these regions
                    boolean found = false;
                    for (RegionConnection connection : regionConnections) {
                        if (connection.getSource() == source && connection.getTarget() == target) {
                            weightedConnectionSum += connection.getEffectiveStrength();
                            found = true;
                            break;
                        }
                    }
                    
                    // If no connection found, effective strength is 0
                    if (!found) {
                        weightedConnectionSum += 0.0;
                    }
                }
            }
        }
        
        // Calculate integrity as ratio of actual connection strength to potential
        return totalPossibleConnections > 0 ? weightedConnectionSum / totalPossibleConnections : 1.0;
    }
    
    /**
     * Simulates compensatory network reorganization in response to damage.
     *
     * @param damageRegions List of regions to damage
     * @param compensationFactor Factor determining compensation strength (0-1)
     * @param timePoints Number of time points to simulate
     * @return A map of time points to global network measures
     */
    public Map<Integer, Map<String, Double>> simulateCompensation(List<String> damageRegions, double compensationFactor, int timePoints) {
        Map<Integer, Map<String, Double>> results = new HashMap<>();
        
        // Find the regions to damage
        List<BrainRegion> targetRegions = new ArrayList<>();
        for (String regionName : damageRegions) {
            BrainRegion region = getRegion(regionName);
            if (region != null) {
                targetRegions.add(region);
            }
        }
        
        // Store original integrity factors to restore later
        Map<BrainRegion, Double> originalVulnerability = new HashMap<>();
        for (BrainRegion region : regions) {
            originalVulnerability.put(region, region.getVulnerabilityFactor());
        }
        
        Map<RegionConnection, Double> originalIntegrity = new HashMap<>();
        for (RegionConnection connection : regionConnections) {
            originalIntegrity.put(connection, connection.getIntegrityFactor());
        }
        
        // Apply initial damage to target regions
        for (BrainRegion region : targetRegions) {
            // Increase vulnerability to simulate damage
            region.setVulnerabilityFactor(1.0);
            
            // Damage connections involving this region
            for (RegionConnection connection : regionConnections) {
                if (connection.getSource() == region || connection.getTarget() == region) {
                    connection.setIntegrityFactor(0.3); // Significant initial damage
                }
            }
        }
        
        // Calculate initial measures
        results.put(0, calculateGlobalNetworkMeasures());
        
        // For each time point, simulate compensation
        for (int t = 1; t < timePoints; t++) {
            // Identify undamaged regions that can compensate
            List<BrainRegion> compensatingRegions = new ArrayList<>();
            for (BrainRegion region : regions) {
                if (!targetRegions.contains(region)) {
                    compensatingRegions.add(region);
                }
            }
            
            // Strengthen connections between compensating regions
            for (RegionConnection connection : regionConnections) {
                BrainRegion source = connection.getSource();
                BrainRegion target = connection.getTarget();
                
                if (compensatingRegions.contains(source) && compensatingRegions.contains(target)) {
                    // Progressive strengthening based on time and compensation factor
                    double timeProgress = t / (double) timePoints;
                    double strengthIncrease = timeProgress * compensationFactor;
                    double originalStrength = connection.getStrength();
                    double newStrength = Math.min(1.0, originalStrength * (1.0 + strengthIncrease));
                    connection.setStrength(newStrength);
                }
            }
            
            // Calculate and store measures for this time point
            results.put(t, calculateGlobalNetworkMeasures());
        }
        
        // Restore original values
        for (BrainRegion region : regions) {
            region.setVulnerabilityFactor(originalVulnerability.get(region));
        }
        
        for (RegionConnection connection : regionConnections) {
            connection.setIntegrityFactor(originalIntegrity.get(connection));
        }
        
        return results;
    }
    
    /**
     * Gets summary statistics for the composite neuronal network.
     *
     * @return A map of network statistics
     */
    public Map<String, Object> getNetworkStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("region_count", regions.size());
        stats.put("region_connection_count", regionConnections.size());
        stats.put("functional_network_count", functionalNetworks.size());
        
        // Calculate totals across all regions
        int totalNeurons = 0;
        int totalSynapses = 0;
        
        for (BrainRegion region : regions) {
            NeuronalNetworkComponent network = region.getNetwork();
            if (network != null) {
                Map<String, Object> networkStats = network.getNetworkStats();
                totalNeurons += (int) networkStats.getOrDefault("neuron_count", 0);
                totalSynapses += (int) networkStats.getOrDefault("synapse_count", 0);
            }
        }
        
        stats.put("total_neurons", totalNeurons);
        stats.put("total_synapses", totalSynapses);
        
        // Add global measures
        Map<String, Double> globalMeasures = calculateGlobalNetworkMeasures();
        stats.putAll(globalMeasures);
        
        return stats;
    }
    
    /**
     * Validates the component's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = super.validateConfiguration();
        
        // Check required configuration for neuronal network composite
        if (!configuration.containsKey("signal_propagation_decay")) {
            errors.put("signal_propagation_decay", "Missing required configuration");
        }
        
        if (!configuration.containsKey("compensation_enabled")) {
            errors.put("compensation_enabled", "Missing required configuration");
        }
        
        return errors;
    }
    
    /**
     * Initializes the composite.
     */
    @Override
    public void initialize() {
        Map<String, String> validationErrors = validateConfiguration();
        if (!validationErrors.isEmpty()) {
            setMetadata("initialization_errors", String.join(", ", validationErrors.values()));
            setState("ERROR");
            return;
        }
        
        // Initialize default brain network if requested
        Boolean initializeDefaultNetwork = getConfig("initialize_default_network");
        Boolean includeFunctionalNetworks = getConfig("include_functional_networks");
        
        if (Boolean.TRUE.equals(initializeDefaultNetwork)) {
            initializeDefaultBrainNetwork(Boolean.TRUE.equals(includeFunctionalNetworks));
        }
        
        super.initialize();
    }
    
    /**
     * Destroys the composite.
     */
    @Override
    public void destroy() {
        // Clean up all region networks
        for (BrainRegion region : regions) {
            NeuronalNetworkComponent network = region.getNetwork();
            if (network != null) {
                network.destroy();
            }
        }
        
        // Clear all data structures
        regions.clear();
        regionConnections.clear();
        functionalNetworks.clear();
        
        super.destroy();
    }
}