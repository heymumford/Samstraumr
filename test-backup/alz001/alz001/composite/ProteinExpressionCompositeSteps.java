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
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import org.s8r.test.steps.alz001.ALZ001BaseSteps;
import org.s8r.test.steps.alz001.mock.ALZ001MockFactory;
import org.s8r.test.steps.alz001.mock.component.ProteinExpressionComponent;
import org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for protein expression composite scenarios.
 * 
 * <p>Handles the integration of multiple protein expression components
 * and coordinates higher-level protein dynamics analysis for Alzheimer's disease modeling.
 */
public class ProteinExpressionCompositeSteps extends ALZ001BaseSteps {
    
    private ProteinExpressionComposite getProteinComposite(String name) {
        return getTestContext().getData("protein_composite_" + name);
    }
    
    private void setProteinComposite(String name, ProteinExpressionComposite composite) {
        getTestContext().setData("protein_composite_" + name, composite);
    }
    
    @Given("a protein expression composite named {string}")
    public void aProteinExpressionCompositeNamed(String name) {
        ProteinExpressionComposite composite = ALZ001MockFactory.createProteinExpressionComposite(name);
        setProteinComposite(name, composite);
    }
    
    @Given("a protein expression composite named {string} with default components")
    public void aProteinExpressionCompositeWithDefaultComponents(String name) {
        ProteinExpressionComposite composite = ALZ001MockFactory.createProteinExpressionCompositeWithComponents(name);
        setProteinComposite(name, composite);
    }
    
    @Given("a fully configured protein expression composite named {string}")
    public void aFullyConfiguredProteinExpressionComposite(String name) {
        ProteinExpressionComposite composite = ALZ001MockFactory.createFullProteinExpressionComposite(name);
        setProteinComposite(name, composite);
    }
    
    @When("I add the following protein expression components to composite {string}:")
    public void iAddTheFollowingProteinExpressionComponentsToComposite(String compositeName, DataTable dataTable) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String componentName = row.get("component_name");
            
            ProteinExpressionComponent component = ALZ001MockFactory.createProteinComponent(componentName);
            
            // Configure the component if protein type and level are provided
            if (row.containsKey("protein_type") && row.containsKey("initial_level")) {
                String proteinType = row.get("protein_type");
                double level = Double.parseDouble(row.get("initial_level"));
                component.createSample(proteinType, level);
            }
            
            composite.addProteinComponent(component);
        }
    }
    
    @When("I create an interaction network named {string} in composite {string}")
    public void iCreateAnInteractionNetworkInComposite(String networkName, String compositeName) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        composite.createInteractionNetwork(networkName);
    }
    
    @When("I add the following protein types to network {string} in composite {string}:")
    public void iAddTheFollowingProteinTypesToNetwork(String networkName, String compositeName, DataTable dataTable) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        ProteinExpressionComposite.ProteinInteractionNetwork network = composite.getInteractionNetwork(networkName);
        
        List<String> proteinTypes = dataTable.asList();
        for (String proteinType : proteinTypes) {
            network.addProteinType(proteinType);
        }
    }
    
    @When("I set the following interaction strengths in network {string} of composite {string}:")
    public void iSetTheFollowingInteractionStrengths(String networkName, String compositeName, DataTable dataTable) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        ProteinExpressionComposite.ProteinInteractionNetwork network = composite.getInteractionNetwork(networkName);
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String type1 = row.get("protein_type_1");
            String type2 = row.get("protein_type_2");
            double strength = Double.parseDouble(row.get("strength"));
            
            network.setInteractionStrength(type1, type2, strength);
        }
    }
    
    @When("I create the following cellular compartments in composite {string}:")
    public void iCreateTheFollowingCellularCompartmentsInComposite(String compositeName, DataTable dataTable) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        List<String> compartmentNames = dataTable.asList();
        for (String name : compartmentNames) {
            composite.createCompartment(name);
        }
    }
    
    @When("I set the following protein levels in compartments of composite {string}:")
    public void iSetTheFollowingProteinLevelsInCompartments(String compositeName, DataTable dataTable) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String compartmentName = row.get("compartment");
            String proteinType = row.get("protein_type");
            double level = Double.parseDouble(row.get("level"));
            
            ProteinExpressionComposite.CellularCompartment compartment = composite.getCompartment(compartmentName);
            compartment.setProteinLevel(proteinType, level);
        }
    }
    
    @When("I create the following transport processes in composite {string}:")
    public void iCreateTheFollowingTransportProcesses(String compositeName, DataTable dataTable) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        List<Map<String, String>> rows = dataTable.asMaps();
        for (Map<String, String> row : rows) {
            String proteinType = row.get("protein_type");
            String source = row.get("source");
            String target = row.get("target");
            double rate = Double.parseDouble(row.get("rate"));
            
            composite.createTransport(proteinType, source, target, rate);
        }
    }
    
    @When("I simulate multi-protein interaction in network {string} of composite {string} for {int} timepoints")
    public void iSimulateMultiProteinInteraction(String networkName, String compositeName, int timePoints) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        Map<String, List<Double>> results = composite.simulateMultiProteinInteraction(networkName, timePoints);
        
        // Store the results in the test context
        getTestContext().setData("interaction_results_" + compositeName + "_" + networkName, results);
    }
    
    @When("I simulate protein transport in composite {string} for {int} timesteps with step size {double}")
    public void iSimulateProteinTransport(String compositeName, int timesteps, double stepSize) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        // Store initial levels for comparison
        Map<String, Map<String, Double>> initialLevels = new HashMap<>();
        for (ProteinExpressionComposite.CellularCompartment compartment : composite.getCompartments()) {
            Map<String, Double> levels = new HashMap<>();
            for (String proteinType : compartment.getProteinTypes()) {
                levels.put(proteinType, compartment.getProteinLevel(proteinType));
            }
            initialLevels.put(compartment.getName(), levels);
        }
        
        // Simulate transport
        for (int i = 0; i < timesteps; i++) {
            composite.simulateTransport(stepSize);
        }
        
        // Store final levels for comparison
        Map<String, Map<String, Double>> finalLevels = new HashMap<>();
        for (ProteinExpressionComposite.CellularCompartment compartment : composite.getCompartments()) {
            Map<String, Double> levels = new HashMap<>();
            for (String proteinType : compartment.getProteinTypes()) {
                levels.put(proteinType, compartment.getProteinLevel(proteinType));
            }
            finalLevels.put(compartment.getName(), levels);
        }
        
        // Store the results in the test context
        getTestContext().setData("transport_initial_levels_" + compositeName, initialLevels);
        getTestContext().setData("transport_final_levels_" + compositeName, finalLevels);
    }
    
    @When("I simulate aggregation with cross-seeding in composite {string} for {int} timepoints")
    public void iSimulateAggregationWithCrossSeeding(String compositeName, int timePoints) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        // Configure cross-seeding simulation
        String primaryProtein = "amyloid";
        List<String> secondaryProteins = new ArrayList<>();
        secondaryProteins.add("tau");
        secondaryProteins.add("phosphorylated_tau");
        
        Map<String, Double> crossSeedingFactors = new HashMap<>();
        crossSeedingFactors.put("tau", 0.05);
        crossSeedingFactors.put("phosphorylated_tau", 0.08);
        
        // Run simulation
        Map<String, List<Double>> results = composite.simulateAggregationWithCrossSeeding(
            primaryProtein, secondaryProteins, timePoints, crossSeedingFactors);
        
        // Store the results in the test context
        getTestContext().setData("aggregation_results_" + compositeName, results);
    }
    
    @When("I create a composite protein profile for composite {string}")
    public void iCreateACompositeProteinProfile(String compositeName) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        Map<String, Map<String, Object>> profile = composite.createCompositeProteinProfile();
        
        // Store the profile in the test context
        getTestContext().setData("protein_profile_" + compositeName, profile);
    }
    
    @Then("the composite {string} should contain {int} protein expression components")
    public void theCompositeShouldContainComponents(String compositeName, int expectedCount) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        assertEquals(expectedCount, composite.getProteinComponents().size(), 
                   "Composite should contain " + expectedCount + " protein expression components");
    }
    
    @Then("the interaction network {string} in composite {string} should contain {int} protein types")
    public void theInteractionNetworkShouldContainProteinTypes(String networkName, String compositeName, int expectedCount) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        ProteinExpressionComposite.ProteinInteractionNetwork network = composite.getInteractionNetwork(networkName);
        
        assertEquals(expectedCount, network.getProteinTypes().size(), 
                   "Network should contain " + expectedCount + " protein types");
    }
    
    @Then("the interaction strength between {string} and {string} in network {string} of composite {string} should be {double}")
    public void theInteractionStrengthShouldBe(String type1, String type2, String networkName, 
                                               String compositeName, double expectedStrength) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        ProteinExpressionComposite.ProteinInteractionNetwork network = composite.getInteractionNetwork(networkName);
        
        double actualStrength = network.getInteractionStrength(type1, type2);
        assertEquals(expectedStrength, actualStrength, 0.001, 
                   "Interaction strength between " + type1 + " and " + type2 + " should be " + expectedStrength);
    }
    
    @Then("the composite {string} should have {int} cellular compartments")
    public void theCompositeShouldHaveCellularCompartments(String compositeName, int expectedCount) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        assertEquals(expectedCount, composite.getCompartments().size(), 
                   "Composite should have " + expectedCount + " cellular compartments");
    }
    
    @Then("the level of protein {string} in compartment {string} of composite {string} should be {double}")
    public void theLevelOfProteinInCompartmentShouldBe(String proteinType, String compartmentName, 
                                                      String compositeName, double expectedLevel) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        ProteinExpressionComposite.CellularCompartment compartment = composite.getCompartment(compartmentName);
        
        double actualLevel = compartment.getProteinLevel(proteinType);
        assertEquals(expectedLevel, actualLevel, 0.001, 
                   "Level of " + proteinType + " in " + compartmentName + " should be " + expectedLevel);
    }
    
    @Then("the composite {string} should have {int} transport processes")
    public void theCompositeShouldHaveTransportProcesses(String compositeName, int expectedCount) {
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        assertEquals(expectedCount, composite.getTransportProcesses().size(), 
                   "Composite should have " + expectedCount + " transport processes");
    }
    
    @Then("the multi-protein interaction results for network {string} in composite {string} should show increasing levels for {string}")
    public void theMultiProteinInteractionResultsShouldShowIncreasingLevels(String networkName, 
                                                                          String compositeName, 
                                                                          String proteinType) {
        Map<String, List<Double>> results = getTestContext().getData("interaction_results_" + compositeName + "_" + networkName);
        List<Double> levels = results.get(proteinType);
        
        assertNotNull(levels, "No results found for protein type: " + proteinType);
        assertTrue(levels.size() > 1, "Not enough data points for analysis");
        
        double firstValue = levels.get(0);
        double lastValue = levels.get(levels.size() - 1);
        
        assertTrue(lastValue > firstValue, 
                 "Protein level for " + proteinType + " should increase over time");
    }
    
    @Then("the protein transport simulation for composite {string} should show decreased levels in source compartments")
    public void theProteinTransportSimulationShouldShowDecreasedLevelsInSourceCompartments(String compositeName) {
        Map<String, Map<String, Double>> initialLevels = getTestContext().getData("transport_initial_levels_" + compositeName);
        Map<String, Map<String, Double>> finalLevels = getTestContext().getData("transport_final_levels_" + compositeName);
        
        ProteinExpressionComposite composite = getProteinComposite(compositeName);
        
        // Check each transport process
        for (ProteinExpressionComposite.ProteinTransport transport : composite.getTransportProcesses()) {
            String proteinType = transport.getProteinType();
            String sourceName = transport.getSource().getName();
            
            // If the source compartment had this protein initially, its level should have decreased
            if (initialLevels.containsKey(sourceName) && 
                initialLevels.get(sourceName).containsKey(proteinType) && 
                initialLevels.get(sourceName).get(proteinType) > 0) {
                
                double initialLevel = initialLevels.get(sourceName).get(proteinType);
                double finalLevel = finalLevels.get(sourceName).get(proteinType);
                
                assertTrue(finalLevel < initialLevel, 
                         "Protein " + proteinType + " level in source compartment " + sourceName + 
                         " should decrease from " + initialLevel + " to less than " + initialLevel);
            }
        }
    }
    
    @Then("the cross-seeding aggregation results for composite {string} should show all protein levels increasing")
    public void theCrossSeedingAggregationResultsShouldShowAllProteinLevelsIncreasing(String compositeName) {
        Map<String, List<Double>> results = getTestContext().getData("aggregation_results_" + compositeName);
        
        for (Map.Entry<String, List<Double>> entry : results.entrySet()) {
            String proteinType = entry.getKey();
            List<Double> levels = entry.getValue();
            
            assertNotNull(levels, "No results found for protein type: " + proteinType);
            assertTrue(levels.size() > 1, "Not enough data points for analysis");
            
            double firstValue = levels.get(0);
            double lastValue = levels.get(levels.size() - 1);
            
            assertTrue(lastValue > firstValue, 
                     "Protein level for " + proteinType + " should increase over time");
        }
    }
    
    @Then("the composite protein profile for {string} should contain data for at least {int} protein types")
    public void theCompositeProteinProfileShouldContainDataForProteinTypes(String compositeName, int minTypes) {
        Map<String, Map<String, Object>> profile = getTestContext().getData("protein_profile_" + compositeName);
        
        assertTrue(profile.size() >= minTypes, 
                 "Protein profile should contain data for at least " + minTypes + " protein types");
        
        // Verify that each protein type has the required fields
        for (Map.Entry<String, Map<String, Object>> entry : profile.entrySet()) {
            Map<String, Object> proteinData = entry.getValue();
            
            assertTrue(proteinData.containsKey("average_level"), 
                     "Protein data should contain average_level");
            
            if (proteinData.containsKey("sample_count")) {
                int sampleCount = (Integer) proteinData.get("sample_count");
                if (sampleCount > 1) {
                    assertTrue(proteinData.containsKey("standard_deviation"), 
                             "Protein data with multiple samples should contain standard_deviation");
                }
            }
        }
    }
}