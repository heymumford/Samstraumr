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
import org.s8r.test.steps.alz001.mock.component.ProteinExpressionComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mock composite for protein expression analysis in Alzheimer's disease modeling.
 * 
 * <p>This composite coordinates multiple protein expression components to provide
 * a comprehensive view of protein dynamics in Alzheimer's disease. It supports
 * multi-protein interaction analysis, cross-compartment protein transport, aggregate
 * formation simulation, and integration of proteomics datasets.
 */
public class ProteinExpressionComposite extends ALZ001MockComposite {
    
    /**
     * Represents a protein interaction network.
     */
    public static class ProteinInteractionNetwork {
        private final String name;
        private final List<String> proteinTypes;
        private final Map<String, Map<String, Double>> interactionStrengths;
        private final Map<String, Object> properties;
        
        /**
         * Creates a new protein interaction network.
         *
         * @param name The network name
         */
        public ProteinInteractionNetwork(String name) {
            this.name = name;
            this.proteinTypes = new ArrayList<>();
            this.interactionStrengths = new HashMap<>();
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
         * Adds a protein type to the network.
         *
         * @param proteinType The protein type to add
         */
        public void addProteinType(String proteinType) {
            if (!proteinTypes.contains(proteinType)) {
                proteinTypes.add(proteinType);
                interactionStrengths.put(proteinType, new HashMap<>());
            }
        }
        
        /**
         * Gets all protein types in the network.
         *
         * @return A list of all protein types
         */
        public List<String> getProteinTypes() {
            return new ArrayList<>(proteinTypes);
        }
        
        /**
         * Sets the interaction strength between two protein types.
         *
         * @param type1 The first protein type
         * @param type2 The second protein type
         * @param strength The interaction strength
         */
        public void setInteractionStrength(String type1, String type2, double strength) {
            addProteinType(type1);
            addProteinType(type2);
            
            interactionStrengths.get(type1).put(type2, strength);
            interactionStrengths.get(type2).put(type1, strength);
        }
        
        /**
         * Gets the interaction strength between two protein types.
         *
         * @param type1 The first protein type
         * @param type2 The second protein type
         * @return The interaction strength, or 0.0 if not set
         */
        public double getInteractionStrength(String type1, String type2) {
            if (!interactionStrengths.containsKey(type1) || 
                !interactionStrengths.get(type1).containsKey(type2)) {
                return 0.0;
            }
            
            return interactionStrengths.get(type1).get(type2);
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
     * Represents a cellular compartment for protein localization.
     */
    public static class CellularCompartment {
        private final String name;
        private final Map<String, Double> proteinLevels;
        private final Map<String, Object> properties;
        
        /**
         * Creates a new cellular compartment.
         *
         * @param name The compartment name
         */
        public CellularCompartment(String name) {
            this.name = name;
            this.proteinLevels = new HashMap<>();
            this.properties = new HashMap<>();
        }
        
        /**
         * Gets the compartment name.
         *
         * @return The compartment name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Sets the level of a protein in this compartment.
         *
         * @param proteinType The protein type
         * @param level The protein level
         */
        public void setProteinLevel(String proteinType, double level) {
            proteinLevels.put(proteinType, level);
        }
        
        /**
         * Gets the level of a protein in this compartment.
         *
         * @param proteinType The protein type
         * @return The protein level, or 0.0 if not set
         */
        public double getProteinLevel(String proteinType) {
            return proteinLevels.getOrDefault(proteinType, 0.0);
        }
        
        /**
         * Gets all protein types in this compartment.
         *
         * @return A list of all protein types
         */
        public List<String> getProteinTypes() {
            return new ArrayList<>(proteinLevels.keySet());
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
     * Represents a protein transport process between compartments.
     */
    public static class ProteinTransport {
        private final String proteinType;
        private final CellularCompartment source;
        private final CellularCompartment target;
        private final double rate;
        private final Map<String, Object> properties;
        
        /**
         * Creates a new protein transport process.
         *
         * @param proteinType The protein type
         * @param source The source compartment
         * @param target The target compartment
         * @param rate The transport rate
         */
        public ProteinTransport(String proteinType, CellularCompartment source, 
                                CellularCompartment target, double rate) {
            this.proteinType = proteinType;
            this.source = source;
            this.target = target;
            this.rate = rate;
            this.properties = new HashMap<>();
        }
        
        /**
         * Gets the protein type.
         *
         * @return The protein type
         */
        public String getProteinType() {
            return proteinType;
        }
        
        /**
         * Gets the source compartment.
         *
         * @return The source compartment
         */
        public CellularCompartment getSource() {
            return source;
        }
        
        /**
         * Gets the target compartment.
         *
         * @return The target compartment
         */
        public CellularCompartment getTarget() {
            return target;
        }
        
        /**
         * Gets the transport rate.
         *
         * @return The transport rate
         */
        public double getRate() {
            return rate;
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
         * Simulates the transport process for one time step.
         *
         * @param timeStep The time step
         */
        public void simulateTransport(double timeStep) {
            double sourceLevel = source.getProteinLevel(proteinType);
            double amountTransported = sourceLevel * rate * timeStep;
            
            if (amountTransported > sourceLevel) {
                amountTransported = sourceLevel;
            }
            
            source.setProteinLevel(proteinType, sourceLevel - amountTransported);
            target.setProteinLevel(proteinType, target.getProteinLevel(proteinType) + amountTransported);
        }
    }
    
    private final List<ProteinInteractionNetwork> interactionNetworks;
    private final List<CellularCompartment> compartments;
    private final List<ProteinTransport> transportProcesses;
    private final Map<String, List<ProteinExpressionComponent.ProteinSample>> proteomicsDatasets;
    
    /**
     * Creates a new protein expression composite with the given name.
     *
     * @param name The composite name
     */
    public ProteinExpressionComposite(String name) {
        super(name, "PROTEIN_EXPRESSION");
        this.interactionNetworks = new ArrayList<>();
        this.compartments = new ArrayList<>();
        this.transportProcesses = new ArrayList<>();
        this.proteomicsDatasets = new HashMap<>();
    }
    
    /**
     * Adds a protein expression component to this composite.
     *
     * @param component The component to add
     */
    public void addProteinComponent(ProteinExpressionComponent component) {
        addChild(component);
    }
    
    /**
     * Gets all protein expression components in this composite.
     *
     * @return A list of all protein expression components
     */
    public List<ProteinExpressionComponent> getProteinComponents() {
        return children.stream()
                .filter(c -> c instanceof ProteinExpressionComponent)
                .map(c -> (ProteinExpressionComponent) c)
                .collect(Collectors.toList());
    }
    
    /**
     * Creates a new protein interaction network.
     *
     * @param name The network name
     * @return The created network
     */
    public ProteinInteractionNetwork createInteractionNetwork(String name) {
        ProteinInteractionNetwork network = new ProteinInteractionNetwork(name);
        interactionNetworks.add(network);
        return network;
    }
    
    /**
     * Gets a protein interaction network by name.
     *
     * @param name The network name
     * @return The network, or null if not found
     */
    public ProteinInteractionNetwork getInteractionNetwork(String name) {
        for (ProteinInteractionNetwork network : interactionNetworks) {
            if (network.getName().equals(name)) {
                return network;
            }
        }
        return null;
    }
    
    /**
     * Gets all protein interaction networks.
     *
     * @return A list of all protein interaction networks
     */
    public List<ProteinInteractionNetwork> getInteractionNetworks() {
        return new ArrayList<>(interactionNetworks);
    }
    
    /**
     * Creates a new cellular compartment.
     *
     * @param name The compartment name
     * @return The created compartment
     */
    public CellularCompartment createCompartment(String name) {
        CellularCompartment compartment = new CellularCompartment(name);
        compartments.add(compartment);
        return compartment;
    }
    
    /**
     * Gets a cellular compartment by name.
     *
     * @param name The compartment name
     * @return The compartment, or null if not found
     */
    public CellularCompartment getCompartment(String name) {
        for (CellularCompartment compartment : compartments) {
            if (compartment.getName().equals(name)) {
                return compartment;
            }
        }
        return null;
    }
    
    /**
     * Gets all cellular compartments.
     *
     * @return A list of all cellular compartments
     */
    public List<CellularCompartment> getCompartments() {
        return new ArrayList<>(compartments);
    }
    
    /**
     * Creates a new protein transport process.
     *
     * @param proteinType The protein type
     * @param sourceName The source compartment name
     * @param targetName The target compartment name
     * @param rate The transport rate
     * @return The created transport process, or null if either compartment was not found
     */
    public ProteinTransport createTransport(String proteinType, String sourceName, 
                                            String targetName, double rate) {
        CellularCompartment source = getCompartment(sourceName);
        CellularCompartment target = getCompartment(targetName);
        
        if (source == null || target == null) {
            return null;
        }
        
        ProteinTransport transport = new ProteinTransport(proteinType, source, target, rate);
        transportProcesses.add(transport);
        return transport;
    }
    
    /**
     * Gets all protein transport processes.
     *
     * @return A list of all protein transport processes
     */
    public List<ProteinTransport> getTransportProcesses() {
        return new ArrayList<>(transportProcesses);
    }
    
    /**
     * Gets transport processes for a specific protein type.
     *
     * @param proteinType The protein type
     * @return A list of transport processes for the specified protein type
     */
    public List<ProteinTransport> getTransportsByProteinType(String proteinType) {
        List<ProteinTransport> result = new ArrayList<>();
        for (ProteinTransport transport : transportProcesses) {
            if (transport.getProteinType().equals(proteinType)) {
                result.add(transport);
            }
        }
        return result;
    }
    
    /**
     * Simulates protein transport processes for one time step.
     *
     * @param timeStep The time step
     */
    public void simulateTransport(double timeStep) {
        for (ProteinTransport transport : transportProcesses) {
            transport.simulateTransport(timeStep);
        }
    }
    
    /**
     * Adds a proteomics dataset.
     *
     * @param datasetName The dataset name
     * @param samples The protein samples
     */
    public void addProteomicsDataset(String datasetName, List<ProteinExpressionComponent.ProteinSample> samples) {
        proteomicsDatasets.put(datasetName, new ArrayList<>(samples));
    }
    
    /**
     * Gets a proteomics dataset by name.
     *
     * @param datasetName The dataset name
     * @return The dataset, or null if not found
     */
    public List<ProteinExpressionComponent.ProteinSample> getProteomicsDataset(String datasetName) {
        return proteomicsDatasets.getOrDefault(datasetName, null);
    }
    
    /**
     * Gets all proteomics datasets.
     *
     * @return A map of all proteomics datasets
     */
    public Map<String, List<ProteinExpressionComponent.ProteinSample>> getProteomicsDatasets() {
        Map<String, List<ProteinExpressionComponent.ProteinSample>> result = new HashMap<>();
        for (Map.Entry<String, List<ProteinExpressionComponent.ProteinSample>> entry : proteomicsDatasets.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }
    
    /**
     * Simulates multi-protein interactions within the specified network.
     *
     * @param networkName The interaction network name
     * @param timePoints The number of time points to simulate
     * @return A map of protein levels over time for each protein type
     */
    public Map<String, List<Double>> simulateMultiProteinInteraction(String networkName, int timePoints) {
        ProteinInteractionNetwork network = getInteractionNetwork(networkName);
        if (network == null) {
            return new HashMap<>();
        }
        
        Map<String, List<Double>> result = new HashMap<>();
        Map<String, Double> currentLevels = new HashMap<>();
        
        // Initialize levels and result lists
        for (String proteinType : network.getProteinTypes()) {
            double initialLevel = 0.0;
            for (ProteinExpressionComponent component : getProteinComponents()) {
                initialLevel += component.getAverageLevel(proteinType);
            }
            currentLevels.put(proteinType, initialLevel > 0.0 ? initialLevel : 1.0);
            result.put(proteinType, new ArrayList<>());
        }
        
        // Simulation loop
        for (int i = 0; i < timePoints; i++) {
            // Record current levels
            for (String proteinType : network.getProteinTypes()) {
                result.get(proteinType).add(currentLevels.get(proteinType));
            }
            
            // Calculate deltas based on interactions
            Map<String, Double> deltas = new HashMap<>();
            for (String type1 : network.getProteinTypes()) {
                double totalDelta = 0.0;
                for (String type2 : network.getProteinTypes()) {
                    if (!type1.equals(type2)) {
                        double strength = network.getInteractionStrength(type1, type2);
                        double level2 = currentLevels.get(type2);
                        totalDelta += strength * level2 * 0.1; // Simple linear interaction model
                    }
                }
                deltas.put(type1, totalDelta);
            }
            
            // Apply deltas
            for (String proteinType : network.getProteinTypes()) {
                double newLevel = currentLevels.get(proteinType) + deltas.get(proteinType);
                if (newLevel < 0.0) newLevel = 0.0;
                currentLevels.put(proteinType, newLevel);
            }
        }
        
        return result;
    }
    
    /**
     * Simulates protein aggregation with cross-seeding effects.
     *
     * @param primaryProtein The primary protein type
     * @param secondaryProteins The secondary protein types
     * @param timePoints The number of time points to simulate
     * @param crossSeedingFactors The cross-seeding factors for each secondary protein
     * @return A map of aggregation levels over time for each protein type
     */
    public Map<String, List<Double>> simulateAggregationWithCrossSeeding(
            String primaryProtein, List<String> secondaryProteins, 
            int timePoints, Map<String, Double> crossSeedingFactors) {
        
        Map<String, List<Double>> result = new HashMap<>();
        result.put(primaryProtein, new ArrayList<>());
        for (String protein : secondaryProteins) {
            result.put(protein, new ArrayList<>());
        }
        
        // Get initial levels
        Map<String, Double> currentLevels = new HashMap<>();
        currentLevels.put(primaryProtein, getAverageProteinLevel(primaryProtein));
        for (String protein : secondaryProteins) {
            currentLevels.put(protein, getAverageProteinLevel(protein));
        }
        
        // Ensure positive levels
        for (Map.Entry<String, Double> entry : currentLevels.entrySet()) {
            if (entry.getValue() <= 0.0) {
                currentLevels.put(entry.getKey(), 1.0);
            }
        }
        
        // Simulation loop
        for (int i = 0; i < timePoints; i++) {
            // Record current levels
            for (String protein : currentLevels.keySet()) {
                result.get(protein).add(currentLevels.get(protein));
            }
            
            // Calculate aggregation rates with cross-seeding
            Map<String, Double> aggregationRates = new HashMap<>();
            
            // Primary protein aggregation with cross-seeding effects
            double primaryRate = 0.1 * currentLevels.get(primaryProtein); // Base rate
            for (String secondary : secondaryProteins) {
                primaryRate += currentLevels.get(secondary) * crossSeedingFactors.getOrDefault(secondary, 0.01);
            }
            aggregationRates.put(primaryProtein, primaryRate);
            
            // Secondary proteins affected by primary
            for (String secondary : secondaryProteins) {
                double secondaryRate = 0.05 * currentLevels.get(secondary); // Base rate
                secondaryRate += currentLevels.get(primaryProtein) * 0.02; // Effect of primary
                aggregationRates.put(secondary, secondaryRate);
            }
            
            // Apply aggregation
            for (String protein : currentLevels.keySet()) {
                double newLevel = currentLevels.get(protein) * (1.0 + aggregationRates.get(protein));
                currentLevels.put(protein, newLevel);
            }
        }
        
        return result;
    }
    
    /**
     * Gets the average level of a protein across all components.
     *
     * @param proteinType The protein type
     * @return The average protein level
     */
    public double getAverageProteinLevel(String proteinType) {
        List<ProteinExpressionComponent> components = getProteinComponents();
        if (components.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (ProteinExpressionComponent component : components) {
            sum += component.getAverageLevel(proteinType);
        }
        
        return sum / components.size();
    }
    
    /**
     * Creates a composite protein profile by integrating data from all components.
     *
     * @return A map of protein types to average levels and properties
     */
    public Map<String, Map<String, Object>> createCompositeProteinProfile() {
        Map<String, Map<String, Object>> profile = new HashMap<>();
        Set<String> proteinTypes = new HashSet<>();
        
        // Collect all protein types
        for (ProteinExpressionComponent component : getProteinComponents()) {
            for (ProteinExpressionComponent.ProteinSample sample : component.getAllSamples()) {
                proteinTypes.add(sample.getProteinType());
            }
        }
        
        // Create profile entries
        for (String proteinType : proteinTypes) {
            Map<String, Object> entry = new HashMap<>();
            
            // Calculate average level
            entry.put("average_level", getAverageProteinLevel(proteinType));
            
            // Calculate variability
            List<Double> allLevels = new ArrayList<>();
            for (ProteinExpressionComponent component : getProteinComponents()) {
                for (ProteinExpressionComponent.ProteinSample sample : component.getSamplesByType(proteinType)) {
                    allLevels.add(sample.getLevel());
                }
            }
            
            if (!allLevels.isEmpty()) {
                double mean = allLevels.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                double variance = allLevels.stream()
                    .mapToDouble(level -> Math.pow(level - mean, 2))
                    .average()
                    .orElse(0.0);
                entry.put("standard_deviation", Math.sqrt(variance));
                entry.put("sample_count", allLevels.size());
            }
            
            // Add to profile
            profile.put(proteinType, entry);
        }
        
        return profile;
    }
    
    /**
     * Validates the composite's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = super.validateConfiguration();
        
        // Check that we have at least one protein expression component
        if (getProteinComponents().isEmpty()) {
            errors.put("protein_components", "At least one protein expression component is required");
        }
        
        return errors;
    }
    
    /**
     * Initializes the composite.
     */
    @Override
    public void initialize() {
        super.initialize();
        
        if ("READY".equals(getState())) {
            // Create default interaction network if none exists
            if (interactionNetworks.isEmpty()) {
                ProteinInteractionNetwork network = createInteractionNetwork("default");
                network.addProteinType("amyloid");
                network.addProteinType("tau");
                network.setInteractionStrength("amyloid", "tau", 0.5);
            }
            
            // Create default compartments if none exist
            if (compartments.isEmpty()) {
                createCompartment("extracellular");
                createCompartment("cytoplasm");
                createCompartment("nucleus");
            }
        }
    }
}