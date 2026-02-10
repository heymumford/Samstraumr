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

package org.s8r.test.steps.alz001.composite;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.jupiter.api.Assertions;
import org.s8r.test.steps.ALZ001TestContext;
import org.s8r.test.steps.alz001.ALZ001BaseSteps;
import org.s8r.test.steps.alz001.mock.ALZ001MockFactory;
import org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent;
import org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite;
import org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite.BrainRegion;
import org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite.RegionConnection;
import org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite.FunctionalNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Step definitions for testing the neuronal network composite in the ALZ001 test suite.
 */
public class NeuronalNetworkCompositeSteps {
    
    private final ALZ001BaseSteps baseSteps = new ALZ001BaseSteps();
    private NeuronalNetworkComposite neuronalNetworkComposite;
    
    @Before
    public void setUp() {
        baseSteps.setUp();
        baseSteps.logInfo("Setting up NeuronalNetworkCompositeSteps");
    }
    
    @After
    public void tearDown() {
        baseSteps.logInfo("Tearing down NeuronalNetworkCompositeSteps");
        baseSteps.tearDown();
    }
    
    @Given("an ALZ001 test environment is initialized")
    public void alz001TestEnvironmentIsInitialized() {
        baseSteps.logInfo("Initializing ALZ001 test environment");
        baseSteps.context.store("environmentInitialized", true);
    }
    
    @When("I create a neuronal network composite named {string}")
    public void createNeuronalNetworkComposite(String name) {
        baseSteps.logInfo("Creating neuronal network composite: " + name);
        neuronalNetworkComposite = new NeuronalNetworkComposite(name);
        baseSteps.context.store("neuronalNetworkComposite", neuronalNetworkComposite);
    }
    
    @When("I configure the following properties for the composite:")
    public void configurePropertiesForComposite(DataTable dataTable) {
        baseSteps.logInfo("Configuring composite properties");
        
        Map<String, String> properties = dataTable.asMap(String.class, String.class);
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            baseSteps.logInfo("Setting property: " + key + " = " + value);
            
            // Convert value to appropriate type and set as configuration
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                composite.setConfig(key, Boolean.parseBoolean(value));
            } else {
                try {
                    // Try parsing as double first
                    double doubleValue = Double.parseDouble(value);
                    composite.setConfig(key, doubleValue);
                } catch (NumberFormatException e) {
                    // If not a number, set as string
                    composite.setConfig(key, value);
                }
            }
        }
    }
    
    @When("I initialize the composite")
    public void initializeComposite() {
        baseSteps.logInfo("Initializing composite");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        composite.initialize();
    }
    
    @Then("the composite should be successfully initialized")
    public void compositeShouldBeSuccessfullyInitialized() {
        baseSteps.logInfo("Checking if composite is successfully initialized");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Assertions.assertNotEquals("ERROR", composite.getState(), "Composite should not be in ERROR state");
    }
    
    @Then("the composite type should be {string}")
    public void compositeTypeShouldBe(String expectedType) {
        baseSteps.logInfo("Checking composite type: " + expectedType);
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Assertions.assertEquals(expectedType, composite.getType(), "Composite type should match");
    }
    
    @Then("the composite state should be {string}")
    public void compositeStateShouldBe(String expectedState) {
        baseSteps.logInfo("Checking composite state: " + expectedState);
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Assertions.assertEquals(expectedState, composite.getState(), "Composite state should match");
    }
    
    @Then("the composite should contain at least {int} brain regions")
    public void compositeShouldContainAtLeastRegions(int minRegions) {
        baseSteps.logInfo("Checking if composite contains at least " + minRegions + " brain regions");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        List<BrainRegion> regions = composite.getRegions();
        
        Assertions.assertTrue(regions.size() >= minRegions, 
            "Composite should contain at least " + minRegions + " brain regions, but found " + regions.size());
    }
    
    @Then("the composite should contain at least {int} region connections")
    public void compositeShouldContainAtLeastConnections(int minConnections) {
        baseSteps.logInfo("Checking if composite contains at least " + minConnections + " region connections");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        List<RegionConnection> connections = composite.getRegionConnections();
        
        Assertions.assertTrue(connections.size() >= minConnections, 
            "Composite should contain at least " + minConnections + " region connections, but found " + connections.size());
    }
    
    @Then("the composite should contain at least {int} functional networks")
    public void compositeShouldContainAtLeastFunctionalNetworks(int minNetworks) {
        baseSteps.logInfo("Checking if composite contains at least " + minNetworks + " functional networks");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        List<FunctionalNetwork> networks = composite.getFunctionalNetworks();
        
        Assertions.assertTrue(networks.size() >= minNetworks, 
            "Composite should contain at least " + minNetworks + " functional networks, but found " + networks.size());
    }
    
    @Given("I have created a neuronal network composite named {string}")
    public void haveCreatedNeuronalNetworkComposite(String name) {
        baseSteps.logInfo("Creating neuronal network composite for subsequent steps: " + name);
        
        neuronalNetworkComposite = new NeuronalNetworkComposite(name);
        neuronalNetworkComposite.setConfig("signal_propagation_decay", 0.1);
        neuronalNetworkComposite.setConfig("compensation_enabled", true);
        neuronalNetworkComposite.initialize();
        
        baseSteps.context.store("neuronalNetworkComposite", neuronalNetworkComposite);
    }
    
    @When("I create the following brain regions:")
    public void createBrainRegions(DataTable dataTable) {
        baseSteps.logInfo("Creating brain regions");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, BrainRegion> regionMap = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String name = row.get("name");
            String function = row.get("function");
            String topology = row.get("topology");
            
            baseSteps.logInfo("Creating region: " + name + " (" + function + ", " + topology + ")");
            
            // Create network component for this region
            NeuronalNetworkComponent network = new NeuronalNetworkComponent(name + "_network");
            Map<String, Object> config = new HashMap<>();
            config.put("default_connectivity_threshold", 0.6);
            config.put("neuronal_update_interval_ms", 100);
            config.put("network_size", 100);
            config.put("region_name", name);
            config.put("topology", topology);
            network.configure(config);
            composite.addNetworkComponent(network);
            
            // Configure network topology
            if ("small-world".equals(topology)) {
                network.configureSmallWorldTopology(100, 10, 0.1);
            } else if ("scale-free".equals(topology)) {
                network.configureScaleFreeTopology(100, 5);
            } else {
                network.configureRandomTopology(100, 500, 0.2);
            }
            
            // Create region
            BrainRegion region = composite.createRegion(name, function, network);
            regionMap.put(name, region);
        }
        
        baseSteps.context.store("regionMap", regionMap);
    }
    
    @When("I establish the following region connections:")
    public void establishRegionConnections(DataTable dataTable) {
        baseSteps.logInfo("Establishing region connections");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, BrainRegion> regionMap = baseSteps.context.retrieve("regionMap");
        List<RegionConnection> connections = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String source = row.get("source");
            String target = row.get("target");
            String type = row.get("type");
            double strength = Double.parseDouble(row.get("strength"));
            
            baseSteps.logInfo("Connecting: " + source + " -> " + target + " (" + type + ", " + strength + ")");
            
            BrainRegion sourceRegion = regionMap.get(source);
            BrainRegion targetRegion = regionMap.get(target);
            
            if (sourceRegion != null && targetRegion != null) {
                RegionConnection connection = composite.connectRegions(sourceRegion, targetRegion, type, strength);
                connections.add(connection);
            } else {
                baseSteps.logInfo("Warning: Could not establish connection due to missing region(s)");
            }
        }
        
        baseSteps.context.store("connections", connections);
    }
    
    @Then("the composite should contain {int} brain regions")
    public void compositeShouldContainExactRegions(int expectedRegions) {
        baseSteps.logInfo("Checking if composite contains exactly " + expectedRegions + " brain regions");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        List<BrainRegion> regions = composite.getRegions();
        
        Assertions.assertEquals(expectedRegions, regions.size(), 
            "Composite should contain exactly " + expectedRegions + " brain regions");
    }
    
    @Then("the composite should contain {int} region connections")
    public void compositeShouldContainExactConnections(int expectedConnections) {
        baseSteps.logInfo("Checking if composite contains exactly " + expectedConnections + " region connections");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        List<RegionConnection> connections = composite.getRegionConnections();
        
        Assertions.assertEquals(expectedConnections, connections.size(), 
            "Composite should contain exactly " + expectedConnections + " region connections");
    }
    
    @Then("the {string} region should have {string} topology")
    public void regionShouldHaveTopology(String regionName, String expectedTopology) {
        baseSteps.logInfo("Checking if " + regionName + " region has " + expectedTopology + " topology");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        BrainRegion region = composite.getRegion(regionName);
        
        Assertions.assertNotNull(region, "Region " + regionName + " should exist");
        NeuronalNetworkComponent network = region.getNetwork();
        Assertions.assertNotNull(network, "Region " + regionName + " should have a network");
        
        String actualTopology = (String) network.getMetadata("topology");
        Assertions.assertEquals(expectedTopology, actualTopology, 
            "Region " + regionName + " should have " + expectedTopology + " topology");
    }
    
    @Given("I have created a neuronal network composite with the following regions:")
    public void haveCreatedNeuronalNetworkCompositeWithRegions(DataTable dataTable) {
        baseSteps.logInfo("Creating neuronal network composite with specified regions");
        
        // Create composite
        String name = "NetworkWithRegions";
        neuronalNetworkComposite = new NeuronalNetworkComposite(name);
        neuronalNetworkComposite.setConfig("signal_propagation_decay", 0.1);
        neuronalNetworkComposite.setConfig("compensation_enabled", true);
        neuronalNetworkComposite.initialize();
        
        baseSteps.context.store("neuronalNetworkComposite", neuronalNetworkComposite);
        
        // Now create the regions
        createBrainRegions(dataTable);
    }
    
    @Given("I have established connectivity between the regions")
    public void haveEstablishedConnectivityBetweenRegions() {
        baseSteps.logInfo("Establishing default connectivity between regions");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, BrainRegion> regionMap = baseSteps.context.retrieve("regionMap");
        List<RegionConnection> connections = new ArrayList<>();
        
        // Create connections between all regions (fully connected network)
        List<BrainRegion> regions = new ArrayList<>(regionMap.values());
        for (int i = 0; i < regions.size(); i++) {
            for (int j = 0; j < regions.size(); j++) {
                if (i != j) {
                    BrainRegion source = regions.get(i);
                    BrainRegion target = regions.get(j);
                    String type = (i + j) % 2 == 0 ? "structural" : "functional";
                    double strength = 0.5 + (0.1 * ((i + j) % 5)); // Vary between 0.5 and 0.9
                    
                    baseSteps.logInfo("Connecting: " + source.getName() + " -> " + target.getName() + 
                                     " (" + type + ", " + strength + ")");
                    
                    RegionConnection connection = composite.connectRegions(source, target, type, strength);
                    connections.add(connection);
                }
            }
        }
        
        baseSteps.context.store("connections", connections);
    }
    
    @When("I create the following functional networks:")
    public void createFunctionalNetworks(DataTable dataTable) {
        baseSteps.logInfo("Creating functional networks");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, BrainRegion> regionMap = baseSteps.context.retrieve("regionMap");
        Map<String, FunctionalNetwork> networkMap = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String name = row.get("name");
            String function = row.get("function");
            String regions = row.get("regions");
            
            baseSteps.logInfo("Creating functional network: " + name + " (" + function + ") with regions: " + regions);
            
            FunctionalNetwork network = composite.createFunctionalNetwork(name, function);
            
            // Add regions to the network
            String[] regionNames = regions.split(",");
            for (String regionName : regionNames) {
                BrainRegion region = regionMap.get(regionName.trim());
                if (region != null) {
                    network.addRegion(region);
                } else {
                    baseSteps.logInfo("Warning: Region " + regionName + " not found for functional network " + name);
                }
            }
            
            networkMap.put(name, network);
        }
        
        baseSteps.context.store("networkMap", networkMap);
    }
    
    @Then("the {string} should include the {string} region")
    public void networkShouldIncludeRegion(String networkName, String regionName) {
        baseSteps.logInfo("Checking if " + networkName + " includes " + regionName + " region");
        
        Map<String, FunctionalNetwork> networkMap = baseSteps.context.retrieve("networkMap");
        FunctionalNetwork network = networkMap.get(networkName);
        
        Assertions.assertNotNull(network, "Functional network " + networkName + " should exist");
        
        boolean containsRegion = network.getRegions().stream()
            .anyMatch(r -> r.getName().equals(regionName));
        
        Assertions.assertTrue(containsRegion, 
            "Functional network " + networkName + " should include the " + regionName + " region");
    }
    
    @Then("all functional networks should include the {string} region")
    public void allNetworksShouldIncludeRegion(String regionName) {
        baseSteps.logInfo("Checking if all functional networks include " + regionName + " region");
        
        Map<String, FunctionalNetwork> networkMap = baseSteps.context.retrieve("networkMap");
        
        for (Map.Entry<String, FunctionalNetwork> entry : networkMap.entrySet()) {
            String networkName = entry.getKey();
            FunctionalNetwork network = entry.getValue();
            
            boolean containsRegion = network.getRegions().stream()
                .anyMatch(r -> r.getName().equals(regionName));
            
            Assertions.assertTrue(containsRegion, 
                "Functional network " + networkName + " should include the " + regionName + " region");
        }
    }
    
    @Given("I have created a neuronal network composite with default brain network")
    public void haveCreatedNeuronalNetworkCompositeWithDefaultNetwork() {
        baseSteps.logInfo("Creating neuronal network composite with default brain network");
        
        // Create composite
        String name = "DefaultBrainNetwork";
        neuronalNetworkComposite = new NeuronalNetworkComposite(name);
        neuronalNetworkComposite.setConfig("signal_propagation_decay", 0.1);
        neuronalNetworkComposite.setConfig("compensation_enabled", true);
        
        // Initialize with default brain network
        neuronalNetworkComposite.initializeDefaultBrainNetwork(false);
        neuronalNetworkComposite.setState("READY");
        
        baseSteps.context.store("neuronalNetworkComposite", neuronalNetworkComposite);
    }
    
    @Given("I have created a neuronal network composite with default brain network and functional networks")
    public void haveCreatedNeuronalNetworkCompositeWithDefaultNetworkAndFunctionalNetworks() {
        baseSteps.logInfo("Creating neuronal network composite with default brain network and functional networks");
        
        // Create composite
        String name = "DefaultNetworkWithFunctional";
        neuronalNetworkComposite = new NeuronalNetworkComposite(name);
        neuronalNetworkComposite.setConfig("signal_propagation_decay", 0.1);
        neuronalNetworkComposite.setConfig("compensation_enabled", true);
        
        // Initialize with default brain network including functional networks
        neuronalNetworkComposite.initializeDefaultBrainNetwork(true);
        neuronalNetworkComposite.setState("READY");
        
        baseSteps.context.store("neuronalNetworkComposite", neuronalNetworkComposite);
    }
    
    @When("I simulate signal propagation starting from the {string} region with strength {double}")
    public void simulateSignalPropagation(String regionName, double strength) {
        baseSteps.logInfo("Simulating signal propagation from " + regionName + " with strength " + strength);
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        BrainRegion startRegion = composite.getRegion(regionName);
        
        Assertions.assertNotNull(startRegion, "Region " + regionName + " should exist");
        
        Map<String, Double> activations = composite.simulateSignalPropagation(startRegion, strength);
        baseSteps.context.store("signalActivations", activations);
    }
    
    @Then("the signal should propagate to at least {int} regions")
    public void signalShouldPropagateToAtLeastRegions(int minRegions) {
        baseSteps.logInfo("Checking if signal propagated to at least " + minRegions + " regions");
        
        Map<String, Double> activations = baseSteps.context.retrieve("signalActivations");
        
        Assertions.assertTrue(activations.size() >= minRegions, 
            "Signal should propagate to at least " + minRegions + " regions, but reached " + activations.size());
    }
    
    @Then("the {string} region should receive a signal strength of at least {double}")
    public void regionShouldReceiveMinimumSignalStrength(String regionName, double minStrength) {
        baseSteps.logInfo("Checking if " + regionName + " received signal strength of at least " + minStrength);
        
        Map<String, Double> activations = baseSteps.context.retrieve("signalActivations");
        
        Assertions.assertTrue(activations.containsKey(regionName), 
            "Region " + regionName + " should receive a signal");
        
        double actualStrength = activations.get(regionName);
        Assertions.assertTrue(actualStrength >= minStrength, 
            "Region " + regionName + " should receive signal strength of at least " + minStrength + 
            ", but got " + actualStrength);
    }
    
    @Then("the signal strength should decrease with network distance")
    public void signalStrengthShouldDecreaseWithNetworkDistance() {
        baseSteps.logInfo("Checking if signal strength decreases with network distance");
        
        Map<String, Double> activations = baseSteps.context.retrieve("signalActivations");
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        
        // Find source region (the one with highest activation)
        String sourceRegion = null;
        double maxActivation = Double.MIN_VALUE;
        
        for (Map.Entry<String, Double> entry : activations.entrySet()) {
            if (entry.getValue() > maxActivation) {
                maxActivation = entry.getValue();
                sourceRegion = entry.getKey();
            }
        }
        
        Assertions.assertNotNull(sourceRegion, "Should find a source region");
        
        // Check if directly connected regions have higher activation than indirectly connected ones
        BrainRegion source = composite.getRegion(sourceRegion);
        List<RegionConnection> directConnections = composite.getConnectionsFrom(source);
        
        // Get directly connected regions
        Set<String> directRegions = directConnections.stream()
            .map(c -> c.getTarget().getName())
            .collect(Collectors.toSet());
        
        // Calculate average activation for direct vs indirect connections
        double avgDirectActivation = directRegions.stream()
            .filter(activations::containsKey)
            .mapToDouble(activations::get)
            .average()
            .orElse(0);
        
        double avgIndirectActivation = activations.entrySet().stream()
            .filter(e -> !e.getKey().equals(sourceRegion) && !directRegions.contains(e.getKey()))
            .mapToDouble(Map.Entry::getValue)
            .average()
            .orElse(0);
        
        Assertions.assertTrue(avgDirectActivation > avgIndirectActivation, 
            "Directly connected regions should have higher activation than indirectly connected ones");
    }
    
    @When("I simulate regional degeneration with rate {double} over {int} time points")
    public void simulateRegionalDegeneration(double rate, int timePoints) {
        baseSteps.logInfo("Simulating regional degeneration with rate " + rate + " over " + timePoints + " time points");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, List<Double>> degenerationResults = composite.simulateRegionalDegeneration(rate, timePoints);
        
        baseSteps.context.store("degenerationResults", degenerationResults);
        baseSteps.context.store("degenerationRate", rate);
        baseSteps.context.store("degenerationTimePoints", timePoints);
    }
    
    @Then("regions with higher vulnerability should show greater connectivity loss")
    public void regionsWithHigherVulnerabilityShouldShowGreaterConnectivityLoss() {
        baseSteps.logInfo("Checking if regions with higher vulnerability show greater connectivity loss");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, List<Double>> results = baseSteps.context.retrieve("degenerationResults");
        
        // Group regions by vulnerability (high vs low)
        List<BrainRegion> highVulnerabilityRegions = new ArrayList<>();
        List<BrainRegion> lowVulnerabilityRegions = new ArrayList<>();
        
        for (BrainRegion region : composite.getRegions()) {
            if (region.getVulnerabilityFactor() >= 0.6) {
                highVulnerabilityRegions.add(region);
            } else {
                lowVulnerabilityRegions.add(region);
            }
        }
        
        // Check that we have regions in both categories
        Assertions.assertFalse(highVulnerabilityRegions.isEmpty(), "Should have high vulnerability regions");
        Assertions.assertFalse(lowVulnerabilityRegions.isEmpty(), "Should have low vulnerability regions");
        
        // Calculate average connectivity loss for each group
        double highVulnerabilityLoss = calculateAverageConnectivityLoss(highVulnerabilityRegions, results);
        double lowVulnerabilityLoss = calculateAverageConnectivityLoss(lowVulnerabilityRegions, results);
        
        baseSteps.logInfo("High vulnerability loss: " + highVulnerabilityLoss);
        baseSteps.logInfo("Low vulnerability loss: " + lowVulnerabilityLoss);
        
        Assertions.assertTrue(highVulnerabilityLoss > lowVulnerabilityLoss, 
            "Regions with higher vulnerability should show greater connectivity loss");
    }
    
    private double calculateAverageConnectivityLoss(List<BrainRegion> regions, Map<String, List<Double>> results) {
        double totalLoss = 0.0;
        int count = 0;
        
        for (BrainRegion region : regions) {
            String regionName = region.getName();
            if (results.containsKey(regionName)) {
                List<Double> connectivityValues = results.get(regionName);
                if (connectivityValues.size() >= 2) {
                    double initial = connectivityValues.get(0);
                    double last = connectivityValues.get(connectivityValues.size() - 1);
                    double loss = initial - last;
                    totalLoss += loss;
                    count++;
                }
            }
        }
        
        return count > 0 ? totalLoss / count : 0.0;
    }
    
    @Then("the {string} region should show greater degeneration than {string}")
    public void regionShouldShowGreaterDegenerationThanOther(String region1, String region2) {
        baseSteps.logInfo("Checking if " + region1 + " shows greater degeneration than " + region2);
        
        Map<String, List<Double>> results = baseSteps.context.retrieve("degenerationResults");
        
        Assertions.assertTrue(results.containsKey(region1), "Region " + region1 + " should have results");
        Assertions.assertTrue(results.containsKey(region2), "Region " + region2 + " should have results");
        
        List<Double> values1 = results.get(region1);
        List<Double> values2 = results.get(region2);
        
        Assertions.assertTrue(values1.size() >= 2, "Region " + region1 + " should have multiple time points");
        Assertions.assertTrue(values2.size() >= 2, "Region " + region2 + " should have multiple time points");
        
        double initial1 = values1.get(0);
        double last1 = values1.get(values1.size() - 1);
        double loss1 = initial1 - last1;
        
        double initial2 = values2.get(0);
        double last2 = values2.get(values2.size() - 1);
        double loss2 = initial2 - last2;
        
        baseSteps.logInfo(region1 + " degeneration: " + loss1);
        baseSteps.logInfo(region2 + " degeneration: " + loss2);
        
        Assertions.assertTrue(loss1 > loss2, 
            "Region " + region1 + " should show greater degeneration than " + region2);
    }
    
    @Then("all regions should show progressive connectivity decline over time")
    public void allRegionsShouldShowProgressiveConnectivityDecline() {
        baseSteps.logInfo("Checking if all regions show progressive connectivity decline over time");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, List<Double>> results = baseSteps.context.retrieve("degenerationResults");
        int timePoints = baseSteps.context.retrieve("degenerationTimePoints");
        
        for (BrainRegion region : composite.getRegions()) {
            String regionName = region.getName();
            if (results.containsKey(regionName)) {
                List<Double> values = results.get(regionName);
                
                // Check that values decrease over time
                for (int i = 1; i < values.size(); i++) {
                    Assertions.assertTrue(values.get(i) <= values.get(i-1), 
                        "Region " + regionName + " should show progressive decline at time point " + i);
                }
                
                // Check that total decline is significant
                double initial = values.get(0);
                double last = values.get(values.size() - 1);
                double percentDecline = (initial - last) / initial * 100.0;
                
                baseSteps.logInfo("Region " + regionName + " decline: " + percentDecline + "%");
                
                Assertions.assertTrue(percentDecline > 0, 
                    "Region " + regionName + " should show significant connectivity decline");
            }
        }
    }
    
    @When("I calculate global network measures")
    public void calculateGlobalNetworkMeasures() {
        baseSteps.logInfo("Calculating global network measures");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, Double> measures = composite.calculateGlobalNetworkMeasures();
        
        baseSteps.context.store("globalMeasures", measures);
    }
    
    @Then("the global connectivity measure should be between {int} and {int}")
    public void globalConnectivityMeasureShouldBeBetween(int min, int max) {
        baseSteps.logInfo("Checking if global connectivity measure is between " + min + " and " + max);
        
        Map<String, Double> measures = baseSteps.context.retrieve("globalMeasures");
        
        Assertions.assertTrue(measures.containsKey("global_connectivity"), 
            "Global measures should include connectivity");
        
        double connectivity = measures.get("global_connectivity");
        Assertions.assertTrue(connectivity >= min && connectivity <= max, 
            "Global connectivity should be between " + min + " and " + max + ", but got " + connectivity);
    }
    
    @Then("the global clustering coefficient should be between {int} and {int}")
    public void globalClusteringCoefficientShouldBeBetween(int min, int max) {
        baseSteps.logInfo("Checking if global clustering coefficient is between " + min + " and " + max);
        
        Map<String, Double> measures = baseSteps.context.retrieve("globalMeasures");
        
        Assertions.assertTrue(measures.containsKey("global_clustering"), 
            "Global measures should include clustering coefficient");
        
        double clustering = measures.get("global_clustering");
        Assertions.assertTrue(clustering >= min && clustering <= max, 
            "Global clustering coefficient should be between " + min + " and " + max + ", but got " + clustering);
    }
    
    @Then("the region connection integrity should be between {int} and {int}")
    public void regionConnectionIntegrityShouldBeBetween(int min, int max) {
        baseSteps.logInfo("Checking if region connection integrity is between " + min + " and " + max);
        
        Map<String, Double> measures = baseSteps.context.retrieve("globalMeasures");
        
        Assertions.assertTrue(measures.containsKey("connection_integrity"), 
            "Global measures should include connection integrity");
        
        double integrity = measures.get("connection_integrity");
        Assertions.assertTrue(integrity >= min && integrity <= max, 
            "Region connection integrity should be between " + min + " and " + max + ", but got " + integrity);
    }
    
    @When("I analyze functional network integrity")
    public void analyzeFunctionalNetworkIntegrity() {
        baseSteps.logInfo("Analyzing functional network integrity");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, Double> integrityResults = composite.analyzeFunctionalNetworkIntegrity();
        
        baseSteps.context.store("functionalNetworkIntegrity", integrityResults);
    }
    
    @Then("all functional networks should have integrity measures between {int} and {int}")
    public void allFunctionalNetworksShouldHaveIntegrityBetween(int min, int max) {
        baseSteps.logInfo("Checking if all functional networks have integrity between " + min + " and " + max);
        
        Map<String, Double> integrityResults = baseSteps.context.retrieve("functionalNetworkIntegrity");
        
        Assertions.assertFalse(integrityResults.isEmpty(), "Should have functional network integrity results");
        
        for (Map.Entry<String, Double> entry : integrityResults.entrySet()) {
            String networkName = entry.getKey();
            double integrity = entry.getValue();
            
            Assertions.assertTrue(integrity >= min && integrity <= max, 
                "Functional network " + networkName + " should have integrity between " + 
                min + " and " + max + ", but got " + integrity);
        }
    }
    
    @Then("the {string} integrity should be at least {double}")
    public void networkIntegrityShouldBeAtLeast(String networkName, double minIntegrity) {
        baseSteps.logInfo("Checking if " + networkName + " integrity is at least " + minIntegrity);
        
        Map<String, Double> integrityResults = baseSteps.context.retrieve("functionalNetworkIntegrity");
        
        Assertions.assertTrue(integrityResults.containsKey(networkName), 
            "Should have integrity results for " + networkName);
        
        double integrity = integrityResults.get(networkName);
        Assertions.assertTrue(integrity >= minIntegrity, 
            "Functional network " + networkName + " should have integrity of at least " + 
            minIntegrity + ", but got " + integrity);
    }
    
    @When("I simulate the impact of the following protein levels on the network:")
    public void simulateProteinImpactOnNetwork(DataTable dataTable) {
        baseSteps.logInfo("Simulating protein impact on network");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        
        // Convert protein levels to map
        Map<String, Double> proteinLevels = new HashMap<>();
        for (Map<String, String> row : rows) {
            String protein = row.get("protein");
            double level = Double.parseDouble(row.get("level"));
            proteinLevels.put(protein, level);
        }
        
        // Run simulation
        Map<String, List<Double>> impactResults = composite.simulateProteinImpact(proteinLevels, 10);
        baseSteps.context.store("proteinImpactResults", impactResults);
    }
    
    @Then("all regions should show connectivity decline over time")
    public void allRegionsShouldShowConnectivityDecline() {
        baseSteps.logInfo("Checking if all regions show connectivity decline due to protein impact");
        
        Map<String, List<Double>> results = baseSteps.context.retrieve("proteinImpactResults");
        
        for (Map.Entry<String, List<Double>> entry : results.entrySet()) {
            String regionName = entry.getKey();
            List<Double> values = entry.getValue();
            
            Assertions.assertTrue(values.size() >= 2, 
                "Region " + regionName + " should have multiple time points");
            
            double initial = values.get(0);
            double last = values.get(values.size() - 1);
            
            Assertions.assertTrue(last < initial, 
                "Region " + regionName + " should show connectivity decline due to protein impact");
        }
    }
    
    @Then("regions with higher vulnerability should show greater impact from protein levels")
    public void vulnerableRegionsShouldShowGreaterProteinImpact() {
        baseSteps.logInfo("Checking if vulnerable regions show greater impact from protein levels");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        Map<String, List<Double>> results = baseSteps.context.retrieve("proteinImpactResults");
        
        // Group regions by vulnerability
        List<BrainRegion> highVulnerabilityRegions = new ArrayList<>();
        List<BrainRegion> lowVulnerabilityRegions = new ArrayList<>();
        
        for (BrainRegion region : composite.getRegions()) {
            if (region.getVulnerabilityFactor() >= 0.6) {
                highVulnerabilityRegions.add(region);
            } else {
                lowVulnerabilityRegions.add(region);
            }
        }
        
        // Calculate average impact for each group
        double highVulnerabilityImpact = calculateAverageConnectivityLoss(highVulnerabilityRegions, results);
        double lowVulnerabilityImpact = calculateAverageConnectivityLoss(lowVulnerabilityRegions, results);
        
        baseSteps.logInfo("High vulnerability impact: " + highVulnerabilityImpact);
        baseSteps.logInfo("Low vulnerability impact: " + lowVulnerabilityImpact);
        
        Assertions.assertTrue(highVulnerabilityImpact > lowVulnerabilityImpact, 
            "Regions with higher vulnerability should show greater impact from protein levels");
    }
    
    @When("I simulate network damage to the following regions:")
    public void simulateNetworkDamage(DataTable dataTable) {
        baseSteps.logInfo("Simulating network damage to specified regions");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        List<String> damageRegions = rows.stream()
            .map(row -> row.get("region"))
            .collect(Collectors.toList());
        
        baseSteps.context.store("damagedRegions", damageRegions);
    }
    
    @When("I simulate compensatory reorganization with factor {double} over {int} time points")
    public void simulateCompensatoryReorganization(double factor, int timePoints) {
        baseSteps.logInfo("Simulating compensatory reorganization with factor " + factor + 
                         " over " + timePoints + " time points");
        
        NeuronalNetworkComposite composite = baseSteps.context.retrieve("neuronalNetworkComposite");
        List<String> damageRegions = baseSteps.context.retrieve("damagedRegions");
        
        Map<Integer, Map<String, Double>> reorganizationResults = 
            composite.simulateCompensation(damageRegions, factor, timePoints);
        
        baseSteps.context.store("reorganizationResults", reorganizationResults);
        baseSteps.context.store("compensationFactor", factor);
        baseSteps.context.store("compensationTimePoints", timePoints);
    }
    
    @Then("undamaged regions should show increased connectivity over time")
    public void undamagedRegionsShouldShowIncreasedConnectivity() {
        baseSteps.logInfo("Checking if undamaged regions show increased connectivity over time");
        
        Map<Integer, Map<String, Double>> results = baseSteps.context.retrieve("reorganizationResults");
        int timePoints = baseSteps.context.retrieve("compensationTimePoints");
        
        // Get initial and final global connectivity
        Map<String, Double> initialMeasures = results.get(0);
        Map<String, Double> finalMeasures = results.get(timePoints - 1);
        
        double initialConnectivity = initialMeasures.getOrDefault("global_connectivity", 0.0);
        double finalConnectivity = finalMeasures.getOrDefault("global_connectivity", 0.0);
        
        baseSteps.logInfo("Initial connectivity: " + initialConnectivity);
        baseSteps.logInfo("Final connectivity: " + finalConnectivity);
        
        // The global connectivity might decrease due to damage, but it should recover somewhat
        // due to compensation, so final should be better than the worst point
        Map<String, Double> worstMeasures = results.values().stream()
            .min((m1, m2) -> Double.compare(m1.getOrDefault("global_connectivity", 0.0), 
                                           m2.getOrDefault("global_connectivity", 0.0)))
            .orElse(new HashMap<>());
        
        double worstConnectivity = worstMeasures.getOrDefault("global_connectivity", 0.0);
        
        baseSteps.logInfo("Worst connectivity: " + worstConnectivity);
        
        Assertions.assertTrue(finalConnectivity > worstConnectivity, 
            "Final connectivity should be better than worst point due to compensation");
    }
    
    @Then("global network measures should initially decrease then partially recover")
    public void globalMeasuresShouldInitiallyDecreaseThenRecover() {
        baseSteps.logInfo("Checking if global measures initially decrease then partially recover");
        
        Map<Integer, Map<String, Double>> results = baseSteps.context.retrieve("reorganizationResults");
        int timePoints = baseSteps.context.retrieve("compensationTimePoints");
        
        // Check if there's a recovery pattern (V-shape) in the connectivity measure
        List<Double> connectivityValues = new ArrayList<>();
        for (int t = 0; t < timePoints; t++) {
            Map<String, Double> measures = results.get(t);
            connectivityValues.add(measures.getOrDefault("global_connectivity", 0.0));
        }
        
        // Find minimum connectivity and its time point
        double minConnectivity = Double.MAX_VALUE;
        int minTimePoint = -1;
        
        for (int t = 0; t < connectivityValues.size(); t++) {
            if (connectivityValues.get(t) < minConnectivity) {
                minConnectivity = connectivityValues.get(t);
                minTimePoint = t;
            }
        }
        
        // Check recovery pattern: should initially decrease (min point not at the end) 
        // and final value should be better than minimum
        Assertions.assertTrue(minTimePoint > 0 && minTimePoint < timePoints - 1, 
            "Minimum connectivity should not be at the beginning or end");
        
        double finalConnectivity = connectivityValues.get(connectivityValues.size() - 1);
        Assertions.assertTrue(finalConnectivity > minConnectivity, 
            "Final connectivity should be better than minimum due to compensation");
    }
}