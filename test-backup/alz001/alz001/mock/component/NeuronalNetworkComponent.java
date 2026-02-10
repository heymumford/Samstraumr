/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.test.steps.alz001.mock.component;

import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;

/**
 * Mock implementation of a neuronal network component for Alzheimer's disease modeling.
 * 
 * <p>This component simulates neural network structures and their degradation patterns 
 * in Alzheimer's disease, including network topology, connectivity, and neuronal function.
 */
public class NeuronalNetworkComponent extends ALZ001MockComponent {
    
    /**
     * Represents a neuron in the network.
     */
    public static class Neuron {
        private final int id;
        private final String type; // "excitatory" or "inhibitory"
        private double activationThreshold;
        private double currentActivation;
        private boolean isActive;
        private final Map<String, Object> properties;
        
        public Neuron(int id, String type) {
            this.id = id;
            this.type = type;
            this.activationThreshold = "excitatory".equals(type) ? 0.5 : 0.7;
            this.currentActivation = 0.0;
            this.isActive = false;
            this.properties = new HashMap<>();
        }
        
        public int getId() {
            return id;
        }
        
        public String getType() {
            return type;
        }
        
        public double getActivationThreshold() {
            return activationThreshold;
        }
        
        public void setActivationThreshold(double threshold) {
            this.activationThreshold = threshold;
        }
        
        public double getCurrentActivation() {
            return currentActivation;
        }
        
        public void setCurrentActivation(double activation) {
            this.currentActivation = activation;
            this.isActive = activation >= activationThreshold;
        }
        
        public boolean isActive() {
            return isActive;
        }
        
        public void setProperty(String key, Object value) {
            properties.put(key, value);
        }
        
        public Object getProperty(String key) {
            return properties.get(key);
        }
    }
    
    /**
     * Represents a connection between two neurons.
     */
    public static class Synapse {
        private final Neuron source;
        private final Neuron target;
        private double weight;
        private double integrityFactor; // 1.0 = full integrity, 0.0 = degraded
        
        public Synapse(Neuron source, Neuron target, double weight) {
            this.source = source;
            this.target = target;
            this.weight = weight;
            this.integrityFactor = 1.0;
        }
        
        public Neuron getSource() {
            return source;
        }
        
        public Neuron getTarget() {
            return target;
        }
        
        public double getWeight() {
            return weight;
        }
        
        public void setWeight(double weight) {
            this.weight = weight;
        }
        
        public double getIntegrityFactor() {
            return integrityFactor;
        }
        
        public void setIntegrityFactor(double factor) {
            this.integrityFactor = Math.max(0.0, Math.min(1.0, factor));
        }
        
        public double getEffectiveWeight() {
            return weight * integrityFactor;
        }
    }
    
    private final List<Neuron> neurons = new ArrayList<>();
    private final List<Synapse> synapses = new ArrayList<>();
    private final Map<Integer, Set<Synapse>> outgoingSynapses = new HashMap<>();
    private final Map<Integer, Set<Synapse>> incomingSynapses = new HashMap<>();
    private final Random random = new Random();
    
    private String topologyType = "random";
    private int neuronsCount = 0;
    private int excitatory = 0;
    private int inhibitory = 0;
    
    /**
     * Creates a new neuronal network component with the given name.
     *
     * @param name The component name
     */
    public NeuronalNetworkComponent(String name) {
        super(name);
    }
    
    /**
     * Creates a new neuron and adds it to the network.
     *
     * @param type The neuron type ("excitatory" or "inhibitory")
     * @return The created neuron
     */
    public Neuron createNeuron(String type) {
        int id = neurons.size();
        Neuron neuron = new Neuron(id, type);
        neurons.add(neuron);
        outgoingSynapses.put(id, new HashSet<>());
        incomingSynapses.put(id, new HashSet<>());
        
        if ("excitatory".equals(type)) {
            excitatory++;
        } else if ("inhibitory".equals(type)) {
            inhibitory++;
        }
        
        neuronsCount++;
        setState("BUILDING");
        
        return neuron;
    }
    
    /**
     * Creates a synapse between two neurons.
     *
     * @param source The source neuron
     * @param target The target neuron
     * @param weight The synapse weight
     * @return The created synapse
     */
    public Synapse createSynapse(Neuron source, Neuron target, double weight) {
        Synapse synapse = new Synapse(source, target, weight);
        synapses.add(synapse);
        outgoingSynapses.get(source.getId()).add(synapse);
        incomingSynapses.get(target.getId()).add(synapse);
        
        return synapse;
    }
    
    /**
     * Gets all neurons in the network.
     *
     * @return A list of all neurons
     */
    public List<Neuron> getNeurons() {
        return new ArrayList<>(neurons);
    }
    
    /**
     * Gets all synapses in the network.
     *
     * @return A list of all synapses
     */
    public List<Synapse> getSynapses() {
        return new ArrayList<>(synapses);
    }
    
    /**
     * Gets all outgoing synapses for a neuron.
     *
     * @param neuron The source neuron
     * @return A set of outgoing synapses
     */
    public Set<Synapse> getOutgoingSynapses(Neuron neuron) {
        return new HashSet<>(outgoingSynapses.getOrDefault(neuron.getId(), new HashSet<>()));
    }
    
    /**
     * Gets all incoming synapses for a neuron.
     *
     * @param neuron The target neuron
     * @return A set of incoming synapses
     */
    public Set<Synapse> getIncomingSynapses(Neuron neuron) {
        return new HashSet<>(incomingSynapses.getOrDefault(neuron.getId(), new HashSet<>()));
    }
    
    /**
     * Gets neurons of a specific type.
     *
     * @param type The neuron type
     * @return A list of neurons of the specified type
     */
    public List<Neuron> getNeuronsByType(String type) {
        List<Neuron> result = new ArrayList<>();
        for (Neuron neuron : neurons) {
            if (neuron.getType().equals(type)) {
                result.add(neuron);
            }
        }
        return result;
    }
    
    /**
     * Configures a random network topology.
     *
     * @param neuronCount The number of neurons
     * @param synapseCount The number of synapses
     * @param inhibitoryRatio The ratio of inhibitory neurons (0.0 to 1.0)
     */
    public void configureRandomTopology(int neuronCount, int synapseCount, double inhibitoryRatio) {
        clear();
        topologyType = "random";
        
        // Create neurons
        int inhibitoryCount = (int) (neuronCount * inhibitoryRatio);
        int excitatoryCount = neuronCount - inhibitoryCount;
        
        for (int i = 0; i < excitatoryCount; i++) {
            createNeuron("excitatory");
        }
        
        for (int i = 0; i < inhibitoryCount; i++) {
            createNeuron("inhibitory");
        }
        
        // Create random synapses
        for (int i = 0; i < synapseCount; i++) {
            int sourceIdx = random.nextInt(neuronCount);
            int targetIdx = random.nextInt(neuronCount);
            
            // Avoid self-connections
            if (sourceIdx != targetIdx) {
                Neuron source = neurons.get(sourceIdx);
                Neuron target = neurons.get(targetIdx);
                
                // Weight depends on neuron type
                double weight = "excitatory".equals(source.getType()) ? 
                    random.nextDouble() : -random.nextDouble();
                
                createSynapse(source, target, weight);
            }
        }
        
        setState("CONFIGURED");
        setMetadata("topology", "random");
    }
    
    /**
     * Configures a small-world network topology.
     *
     * @param neuronCount The number of neurons
     * @param meanDegree The mean degree (number of connections per neuron)
     * @param rewireProb The probability of rewiring a connection
     */
    public void configureSmallWorldTopology(int neuronCount, int meanDegree, double rewireProb) {
        clear();
        topologyType = "small-world";
        
        // Create neurons (80% excitatory, 20% inhibitory is biologically plausible)
        int inhibitoryCount = (int) (neuronCount * 0.2);
        int excitatoryCount = neuronCount - inhibitoryCount;
        
        for (int i = 0; i < excitatoryCount; i++) {
            createNeuron("excitatory");
        }
        
        for (int i = 0; i < inhibitoryCount; i++) {
            createNeuron("inhibitory");
        }
        
        // Create lattice connections
        int k = meanDegree / 2; // Connect to k neighbors on each side
        for (int i = 0; i < neuronCount; i++) {
            for (int j = 1; j <= k; j++) {
                int target = (i + j) % neuronCount;
                Neuron source = neurons.get(i);
                Neuron targetNeuron = neurons.get(target);
                
                double weight = "excitatory".equals(source.getType()) ? 
                    0.5 + random.nextDouble() * 0.5 : -(0.5 + random.nextDouble() * 0.5);
                
                createSynapse(source, targetNeuron, weight);
            }
        }
        
        // Rewire connections with probability rewireProb
        for (int i = 0; i < neuronCount; i++) {
            for (int j = 1; j <= k; j++) {
                if (random.nextDouble() < rewireProb) {
                    // Find the synapse to rewire
                    int originalTarget = (i + j) % neuronCount;
                    Neuron source = neurons.get(i);
                    
                    // Remove old synapse
                    Synapse toRemove = null;
                    for (Synapse s : getOutgoingSynapses(source)) {
                        if (s.getTarget().getId() == originalTarget) {
                            toRemove = s;
                            break;
                        }
                    }
                    
                    if (toRemove != null) {
                        synapses.remove(toRemove);
                        outgoingSynapses.get(i).remove(toRemove);
                        incomingSynapses.get(originalTarget).remove(toRemove);
                        
                        // Create new random synapse
                        int newTarget;
                        do {
                            newTarget = random.nextInt(neuronCount);
                        } while (newTarget == i || newTarget == originalTarget);
                        
                        double weight = "excitatory".equals(source.getType()) ? 
                            0.5 + random.nextDouble() * 0.5 : -(0.5 + random.nextDouble() * 0.5);
                        
                        createSynapse(source, neurons.get(newTarget), weight);
                    }
                }
            }
        }
        
        setState("CONFIGURED");
        setMetadata("topology", "small-world");
    }
    
    /**
     * Configures a scale-free network topology using the Barabási-Albert model.
     *
     * @param neuronCount The number of neurons
     * @param initialConnections The number of connections for each new neuron
     */
    public void configureScaleFreeTopology(int neuronCount, int initialConnections) {
        clear();
        topologyType = "scale-free";
        
        // Create initial fully connected network
        int initialSize = Math.min(5, neuronCount);
        for (int i = 0; i < initialSize; i++) {
            createNeuron(random.nextDouble() < 0.8 ? "excitatory" : "inhibitory");
        }
        
        // Connect all initial neurons
        for (int i = 0; i < initialSize; i++) {
            for (int j = i + 1; j < initialSize; j++) {
                double weight = "excitatory".equals(neurons.get(i).getType()) ? 
                    0.5 + random.nextDouble() * 0.5 : -(0.5 + random.nextDouble() * 0.5);
                
                createSynapse(neurons.get(i), neurons.get(j), weight);
                
                weight = "excitatory".equals(neurons.get(j).getType()) ? 
                    0.5 + random.nextDouble() * 0.5 : -(0.5 + random.nextDouble() * 0.5);
                
                createSynapse(neurons.get(j), neurons.get(i), weight);
            }
        }
        
        // Add remaining neurons with preferential attachment
        for (int i = initialSize; i < neuronCount; i++) {
            Neuron newNeuron = createNeuron(random.nextDouble() < 0.8 ? "excitatory" : "inhibitory");
            
            // Select nodes to connect based on degree
            List<Integer> selected = new ArrayList<>();
            for (int attempt = 0; attempt < initialConnections * 3 && selected.size() < initialConnections; attempt++) {
                // Find node with probability proportional to its degree
                int totalDegree = synapses.size() * 2; // Each synapse contributes to degree of both nodes
                int randomDegree = random.nextInt(totalDegree);
                int currentSum = 0;
                int selectedId = -1;
                
                for (int nodeId = 0; nodeId < neurons.size() - 1; nodeId++) {
                    int degree = outgoingSynapses.get(nodeId).size() + incomingSynapses.get(nodeId).size();
                    currentSum += degree;
                    if (currentSum > randomDegree) {
                        selectedId = nodeId;
                        break;
                    }
                }
                
                if (selectedId >= 0 && !selected.contains(selectedId)) {
                    selected.add(selectedId);
                }
            }
            
            // Create connections to selected nodes
            for (int nodeId : selected) {
                Neuron target = neurons.get(nodeId);
                
                double weight = "excitatory".equals(newNeuron.getType()) ? 
                    0.5 + random.nextDouble() * 0.5 : -(0.5 + random.nextDouble() * 0.5);
                
                createSynapse(newNeuron, target, weight);
                
                weight = "excitatory".equals(target.getType()) ? 
                    0.5 + random.nextDouble() * 0.5 : -(0.5 + random.nextDouble() * 0.5);
                
                createSynapse(target, newNeuron, weight);
            }
        }
        
        setState("CONFIGURED");
        setMetadata("topology", "scale-free");
    }
    
    /**
     * Activates a neuron and propagates the signal through the network.
     *
     * @param neuronId The ID of the neuron to activate
     * @param activationStrength The activation strength (0.0 to 1.0)
     * @return Map of neuron IDs to activation levels
     */
    public Map<Integer, Double> activateNeuron(int neuronId, double activationStrength) {
        if (neuronId < 0 || neuronId >= neurons.size()) {
            return new HashMap<>();
        }
        
        Map<Integer, Double> activations = new HashMap<>();
        Set<Integer> processed = new HashSet<>();
        List<Integer> queue = new ArrayList<>();
        
        // Start with initial neuron
        Neuron startNeuron = neurons.get(neuronId);
        startNeuron.setCurrentActivation(activationStrength);
        activations.put(neuronId, activationStrength);
        queue.add(neuronId);
        processed.add(neuronId);
        
        // Breadth-first propagation
        while (!queue.isEmpty()) {
            int currentId = queue.remove(0);
            Neuron current = neurons.get(currentId);
            double currentActivation = current.getCurrentActivation();
            
            // Propagate to connected neurons
            for (Synapse synapse : outgoingSynapses.get(currentId)) {
                int targetId = synapse.getTarget().getId();
                Neuron target = neurons.get(targetId);
                
                // Calculate incoming activation
                double incomingActivation = currentActivation * synapse.getEffectiveWeight();
                
                // Update target activation
                double newActivation = target.getCurrentActivation() + incomingActivation;
                target.setCurrentActivation(newActivation);
                activations.put(targetId, newActivation);
                
                // Add to queue if not processed and activation is significant
                if (!processed.contains(targetId) && Math.abs(incomingActivation) > 0.01) {
                    queue.add(targetId);
                    processed.add(targetId);
                }
            }
        }
        
        return activations;
    }
    
    /**
     * Resets all neuron activations to zero.
     */
    public void resetActivations() {
        for (Neuron neuron : neurons) {
            neuron.setCurrentActivation(0.0);
        }
    }
    
    /**
     * Simulates neuronal degeneration over time.
     *
     * @param degradationRate The degradation rate (0.0 to 1.0)
     * @param timePoints The number of time points to simulate
     * @return A map of time point to network connectivity measure
     */
    public Map<Integer, Double> simulateDegeneration(double degradationRate, int timePoints) {
        Map<Integer, Double> connectivityOverTime = new HashMap<>();
        double initialConnectivity = calculateConnectivity();
        connectivityOverTime.put(0, initialConnectivity);
        
        // Store current integrity factors to restore later
        Map<Integer, Double> originalIntegrityFactors = new HashMap<>();
        for (int i = 0; i < synapses.size(); i++) {
            originalIntegrityFactors.put(i, synapses.get(i).getIntegrityFactor());
        }
        
        // Simulate degeneration
        for (int t = 1; t < timePoints; t++) {
            // Apply degradation to synapses
            for (Synapse synapse : synapses) {
                double currentIntegrity = synapse.getIntegrityFactor();
                double randomFactor = 0.8 + 0.4 * random.nextDouble(); // Some randomness in degradation
                double newIntegrity = currentIntegrity * (1.0 - degradationRate * randomFactor * t / timePoints);
                synapse.setIntegrityFactor(newIntegrity);
            }
            
            // Calculate and store connectivity
            double connectivity = calculateConnectivity();
            connectivityOverTime.put(t, connectivity);
        }
        
        // Restore original integrity factors
        for (int i = 0; i < synapses.size(); i++) {
            synapses.get(i).setIntegrityFactor(originalIntegrityFactors.get(i));
        }
        
        return connectivityOverTime;
    }
    
    /**
     * Calculates a connectivity measure for the network.
     *
     * @return The network connectivity (0.0 to 1.0)
     */
    public double calculateConnectivity() {
        if (synapses.isEmpty()) {
            return 0.0;
        }
        
        double totalEffectiveWeight = 0.0;
        double totalPossibleWeight = 0.0;
        
        for (Synapse synapse : synapses) {
            totalEffectiveWeight += Math.abs(synapse.getEffectiveWeight());
            totalPossibleWeight += Math.abs(synapse.getWeight());
        }
        
        return totalPossibleWeight > 0 ? totalEffectiveWeight / totalPossibleWeight : 0.0;
    }
    
    /**
     * Calculates the clustering coefficient of the network.
     *
     * @return The clustering coefficient (0.0 to 1.0)
     */
    public double calculateClusteringCoefficient() {
        if (neurons.size() < 3) {
            return 0.0;
        }
        
        double sumCoefficients = 0.0;
        int countNonZeroDegree = 0;
        
        for (Neuron neuron : neurons) {
            Set<Synapse> outgoing = outgoingSynapses.get(neuron.getId());
            
            if (outgoing.size() < 2) {
                continue;
            }
            
            countNonZeroDegree++;
            
            // Get neighbors
            Set<Integer> neighbors = new HashSet<>();
            for (Synapse synapse : outgoing) {
                neighbors.add(synapse.getTarget().getId());
            }
            
            // Count connections between neighbors
            int possibleConnections = neighbors.size() * (neighbors.size() - 1) / 2;
            int actualConnections = 0;
            
            for (int neighbor1 : neighbors) {
                for (int neighbor2 : neighbors) {
                    if (neighbor1 < neighbor2) {
                        // Check if these neighbors are connected
                        boolean connected = false;
                        for (Synapse synapse : outgoingSynapses.get(neighbor1)) {
                            if (synapse.getTarget().getId() == neighbor2) {
                                connected = true;
                                break;
                            }
                        }
                        
                        if (connected) {
                            actualConnections++;
                        }
                    }
                }
            }
            
            // Calculate local clustering coefficient
            double localCoefficient = possibleConnections > 0 ? 
                actualConnections / (double) possibleConnections : 0.0;
            sumCoefficients += localCoefficient;
        }
        
        return countNonZeroDegree > 0 ? sumCoefficients / countNonZeroDegree : 0.0;
    }
    
    /**
     * Calculates the average path length in the network.
     *
     * @return The average path length
     */
    public double calculateAveragePathLength() {
        if (neurons.size() < 2) {
            return 0.0;
        }
        
        int totalPaths = 0;
        double sumPathLengths = 0.0;
        
        // For each pair of neurons, find shortest path
        for (int start = 0; start < neurons.size(); start++) {
            // Breadth-first search
            Map<Integer, Integer> distances = new HashMap<>();
            List<Integer> queue = new ArrayList<>();
            
            distances.put(start, 0);
            queue.add(start);
            
            while (!queue.isEmpty()) {
                int current = queue.remove(0);
                int distance = distances.get(current);
                
                for (Synapse synapse : outgoingSynapses.get(current)) {
                    int target = synapse.getTarget().getId();
                    
                    if (!distances.containsKey(target)) {
                        distances.put(target, distance + 1);
                        queue.add(target);
                    }
                }
            }
            
            // Add up all found paths
            for (int target = 0; target < neurons.size(); target++) {
                if (target != start && distances.containsKey(target)) {
                    sumPathLengths += distances.get(target);
                    totalPaths++;
                }
            }
        }
        
        return totalPaths > 0 ? sumPathLengths / totalPaths : 0.0;
    }
    
    /**
     * Analyzes the network resilience to random failures.
     *
     * @param failureProbability The probability of neuron failure
     * @param iterations The number of iterations to average over
     * @return A map of statistics about network resilience
     */
    public Map<String, Double> analyzeResilience(double failureProbability, int iterations) {
        Map<String, Double> results = new HashMap<>();
        double sumConnectivity = 0.0;
        double sumComponentSize = 0.0;
        double sumPathLength = 0.0;
        
        // Run multiple simulations and average results
        for (int i = 0; i < iterations; i++) {
            // Create copy of network
            List<Boolean> neuronFailed = new ArrayList<>();
            for (int n = 0; n < neurons.size(); n++) {
                neuronFailed.add(random.nextDouble() < failureProbability);
            }
            
            // Count active synapses
            int totalSynapses = synapses.size();
            int activeSynapses = 0;
            
            for (Synapse synapse : synapses) {
                int source = synapse.getSource().getId();
                int target = synapse.getTarget().getId();
                
                if (!neuronFailed.get(source) && !neuronFailed.get(target)) {
                    activeSynapses++;
                }
            }
            
            // Calculate metrics
            double connectivity = totalSynapses > 0 ? (double) activeSynapses / totalSynapses : 0.0;
            sumConnectivity += connectivity;
            
            // Find largest connected component
            List<Set<Integer>> components = findConnectedComponents(neuronFailed);
            int maxSize = 0;
            for (Set<Integer> component : components) {
                maxSize = Math.max(maxSize, component.size());
            }
            
            double componentRatio = neurons.size() > 0 ? 
                (double) maxSize / (neurons.size() - countFailed(neuronFailed)) : 0.0;
            sumComponentSize += componentRatio;
            
            // Calculate adjusted path length (if possible)
            if (!components.isEmpty() && maxSize > 1) {
                double pathLength = estimatePathLength(components, neuronFailed);
                sumPathLength += pathLength;
            }
        }
        
        // Average results
        results.put("connectivity", sumConnectivity / iterations);
        results.put("largest_component", sumComponentSize / iterations);
        results.put("path_length", iterations > 0 ? sumPathLength / iterations : 0.0);
        
        return results;
    }
    
    /**
     * Helper method to count failed neurons.
     */
    private int countFailed(List<Boolean> neuronFailed) {
        int count = 0;
        for (Boolean failed : neuronFailed) {
            if (failed) count++;
        }
        return count;
    }
    
    /**
     * Helper method to find connected components in the damaged network.
     */
    private List<Set<Integer>> findConnectedComponents(List<Boolean> neuronFailed) {
        List<Set<Integer>> components = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        
        for (int start = 0; start < neurons.size(); start++) {
            if (neuronFailed.get(start) || visited.contains(start)) {
                continue;
            }
            
            // BFS to find connected component
            Set<Integer> component = new HashSet<>();
            List<Integer> queue = new ArrayList<>();
            
            component.add(start);
            visited.add(start);
            queue.add(start);
            
            while (!queue.isEmpty()) {
                int current = queue.remove(0);
                
                for (Synapse synapse : outgoingSynapses.get(current)) {
                    int target = synapse.getTarget().getId();
                    
                    if (!neuronFailed.get(target) && !visited.contains(target)) {
                        component.add(target);
                        visited.add(target);
                        queue.add(target);
                    }
                }
            }
            
            components.add(component);
        }
        
        return components;
    }
    
    /**
     * Helper method to estimate average path length in damaged network.
     */
    private double estimatePathLength(List<Set<Integer>> components, List<Boolean> neuronFailed) {
        // Find largest component
        Set<Integer> largestComponent = components.get(0);
        for (Set<Integer> component : components) {
            if (component.size() > largestComponent.size()) {
                largestComponent = component;
            }
        }
        
        // Sample path lengths in largest component
        int sampleSize = Math.min(50, largestComponent.size());
        List<Integer> sampleNodes = new ArrayList<>(largestComponent);
        double sumPathLengths = 0.0;
        int pathCount = 0;
        
        for (int i = 0; i < sampleSize; i++) {
            int start = sampleNodes.get(random.nextInt(sampleNodes.size()));
            
            // BFS to find distances
            Map<Integer, Integer> distances = new HashMap<>();
            List<Integer> queue = new ArrayList<>();
            
            distances.put(start, 0);
            queue.add(start);
            
            while (!queue.isEmpty()) {
                int current = queue.remove(0);
                int distance = distances.get(current);
                
                for (Synapse synapse : outgoingSynapses.get(current)) {
                    int target = synapse.getTarget().getId();
                    
                    if (!neuronFailed.get(target) && !distances.containsKey(target)) {
                        distances.put(target, distance + 1);
                        queue.add(target);
                    }
                }
            }
            
            // Sum distances
            for (int distance : distances.values()) {
                sumPathLengths += distance;
                pathCount++;
            }
        }
        
        return pathCount > 0 ? sumPathLengths / pathCount : 0.0;
    }
    
    /**
     * Simulates the impact of protein expression on neuronal connectivity.
     *
     * @param proteinLevels Map of protein types to levels
     * @param impactFactors Map of protein types to impact factors
     * @param timePoints Number of time points to simulate
     * @return A list of connectivity values over time
     */
    public List<Double> simulateProteinImpact(Map<String, Double> proteinLevels, 
                                            Map<String, Double> impactFactors, 
                                            int timePoints) {
        List<Double> connectivityOverTime = new ArrayList<>();
        double initialConnectivity = calculateConnectivity();
        connectivityOverTime.add(initialConnectivity);
        
        // Store current integrity factors to restore later
        Map<Integer, Double> originalIntegrityFactors = new HashMap<>();
        for (int i = 0; i < synapses.size(); i++) {
            originalIntegrityFactors.put(i, synapses.get(i).getIntegrityFactor());
        }
        
        // Calculate total impact factor
        double totalImpact = 0.0;
        for (Map.Entry<String, Double> entry : proteinLevels.entrySet()) {
            String proteinType = entry.getKey();
            Double level = entry.getValue();
            Double factor = impactFactors.getOrDefault(proteinType, 0.0);
            
            totalImpact += level * factor;
        }
        
        // Normalize to reasonable range
        double normalizedImpact = Math.min(1.0, totalImpact / 100.0);
        
        // Simulate degeneration based on protein impact
        for (int t = 1; t < timePoints; t++) {
            // Apply degradation to synapses
            for (Synapse synapse : synapses) {
                double currentIntegrity = synapse.getIntegrityFactor();
                double timeProgress = t / (double) timePoints;
                double degradation = normalizedImpact * timeProgress;
                double newIntegrity = currentIntegrity * (1.0 - degradation);
                synapse.setIntegrityFactor(newIntegrity);
            }
            
            // Calculate and store connectivity
            double connectivity = calculateConnectivity();
            connectivityOverTime.add(connectivity);
        }
        
        // Restore original integrity factors
        for (int i = 0; i < synapses.size(); i++) {
            synapses.get(i).setIntegrityFactor(originalIntegrityFactors.get(i));
        }
        
        return connectivityOverTime;
    }
    
    /**
     * Gets network statistics.
     *
     * @return A map of network statistics
     */
    public Map<String, Object> getNetworkStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("neuron_count", neurons.size());
        stats.put("synapse_count", synapses.size());
        stats.put("excitatory_count", excitatory);
        stats.put("inhibitory_count", inhibitory);
        stats.put("topology_type", topologyType);
        stats.put("connectivity", calculateConnectivity());
        stats.put("clustering_coefficient", calculateClusteringCoefficient());
        
        // Calculate degree statistics
        List<Integer> degrees = new ArrayList<>();
        for (int i = 0; i < neurons.size(); i++) {
            int degree = outgoingSynapses.get(i).size() + incomingSynapses.get(i).size();
            degrees.add(degree);
        }
        
        if (!degrees.isEmpty()) {
            // Calculate average degree
            double avgDegree = degrees.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            stats.put("average_degree", avgDegree);
            
            // Calculate max degree
            int maxDegree = degrees.stream().mapToInt(Integer::intValue).max().orElse(0);
            stats.put("max_degree", maxDegree);
        }
        
        return stats;
    }
    
    /**
     * Clears the network, removing all neurons and synapses.
     */
    public void clear() {
        neurons.clear();
        synapses.clear();
        outgoingSynapses.clear();
        incomingSynapses.clear();
        neuronsCount = 0;
        excitatory = 0;
        inhibitory = 0;
        setState("INITIALIZED");
    }
    
    /**
     * Validates the component's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = new HashMap<>();
        
        // Check required configuration
        if (!configuration.containsKey("default_connectivity_threshold")) {
            errors.put("default_connectivity_threshold", "Missing required configuration");
        }
        
        if (!configuration.containsKey("neuronal_update_interval_ms")) {
            errors.put("neuronal_update_interval_ms", "Missing required configuration");
        }
        
        return errors;
    }
    
    /**
     * Initializes the component.
     */
    @Override
    public void initialize() {
        Map<String, String> validationErrors = validateConfiguration();
        if (!validationErrors.isEmpty()) {
            setMetadata("initialization_errors", String.join(", ", validationErrors.values()));
            setState("ERROR");
            return;
        }
        
        // Apply configuration
        Integer networkSize = getConfig("network_size");
        if (networkSize != null && networkSize > 0) {
            Integer synapseCount = getConfig("synapse_count");
            if (synapseCount == null) {
                synapseCount = networkSize * 10; // Default 10 synapses per neuron
            }
            
            configureRandomTopology(networkSize, synapseCount, 0.2);
        }
        
        setState("READY");
    }
    
    /**
     * Destroys the component, releasing any resources.
     */
    @Override
    public void destroy() {
        clear();
        setState("DESTROYED");
    }
}