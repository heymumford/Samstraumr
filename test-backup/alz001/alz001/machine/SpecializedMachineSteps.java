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

package org.s8r.test.steps.alz001.machine;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.datatable.DataTable;

import org.s8r.test.steps.alz001.ALZ001TestContext;
import org.s8r.test.steps.alz001.ALZ001BaseSteps;
import org.s8r.test.steps.alz001.mock.ALZ001MockFactory;
import org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for specialized machine tests.
 *
 * <p>This class implements the step definitions for testing specialized system simulation machines
 * including protein aggregation, network connectivity, temporal progression, environmental factors,
 * and predictive modeling machines.
 */
public class SpecializedMachineSteps {

    private final ALZ001BaseSteps baseSteps;
    private SystemSimulationMachine currentMachine;
    private Map<String, SystemSimulationMachine> specializedMachines;
    
    /**
     * Constructs a new SpecializedMachineSteps instance.
     *
     * @param baseSteps The base steps with shared test context
     */
    public SpecializedMachineSteps(ALZ001BaseSteps baseSteps) {
        this.baseSteps = baseSteps;
        this.specializedMachines = new HashMap<>();
    }
    
    @When("I create a specialized machine focused on {string}")
    public void createSpecializedMachine(String focusArea) {
        baseSteps.logInfo("Creating specialized machine focused on: " + focusArea);
        
        switch (focusArea.toLowerCase()) {
            case "protein aggregation research":
                currentMachine = ALZ001MockFactory.createProteinAggregationMachine("ProteinAggregationMachine");
                break;
            case "network connectivity analysis":
                currentMachine = ALZ001MockFactory.createNetworkConnectivityMachine("NetworkConnectivityMachine");
                break;
            case "temporal progression analysis":
                currentMachine = ALZ001MockFactory.createTemporalProgressionMachine("TemporalProgressionMachine");
                break;
            case "environmental factors analysis":
                currentMachine = ALZ001MockFactory.createEnvironmentalFactorsMachine("EnvironmentalFactorsMachine");
                break;
            case "predictive modeling and personalization":
                currentMachine = ALZ001MockFactory.createPredictiveModelingMachine("PredictiveModelingMachine");
                break;
            default:
                currentMachine = ALZ001MockFactory.createSystemSimulationMachine("GenericMachine-" + focusArea);
                break;
        }
        
        // Store the machine in the test context
        baseSteps.context.setProperty("current_machine", currentMachine);
        baseSteps.context.setProperty("machine_focus", focusArea);
    }
    
    @When("I configure the machine with the following protein aggregation parameters:")
    public void configureProteinAggregationParameters(DataTable dataTable) {
        baseSteps.logInfo("Configuring protein aggregation parameters");
        
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        Map<String, Object> configParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Convert string values to appropriate types
            if ("true".equalsIgnoreCase(entry.getValue()) || "false".equalsIgnoreCase(entry.getValue())) {
                configParams.put(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
            } else if (entry.getValue().matches("\\d+")) {
                configParams.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else if (entry.getValue().matches("\\d+\\.\\d+")) {
                configParams.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            } else {
                configParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        machine.configureSimulation(configParams);
        
        baseSteps.logInfo("Configured protein aggregation parameters: " + configParams);
    }
    
    @When("I configure the machine with the following network parameters:")
    public void configureNetworkParameters(DataTable dataTable) {
        baseSteps.logInfo("Configuring network parameters");
        
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        Map<String, Object> configParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Convert string values to appropriate types
            if ("true".equalsIgnoreCase(entry.getValue()) || "false".equalsIgnoreCase(entry.getValue())) {
                configParams.put(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
            } else if (entry.getValue().matches("\\d+")) {
                configParams.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else if (entry.getValue().matches("\\d+\\.\\d+")) {
                configParams.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            } else {
                configParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        machine.configureSimulation(configParams);
        
        baseSteps.logInfo("Configured network parameters: " + configParams);
    }
    
    @When("I configure the machine with the following temporal analysis parameters:")
    public void configureTemporalParameters(DataTable dataTable) {
        baseSteps.logInfo("Configuring temporal analysis parameters");
        
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        Map<String, Object> configParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Convert string values to appropriate types
            if ("true".equalsIgnoreCase(entry.getValue()) || "false".equalsIgnoreCase(entry.getValue())) {
                configParams.put(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
            } else if (entry.getValue().matches("\\d+")) {
                configParams.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else if (entry.getValue().matches("\\d+\\.\\d+")) {
                configParams.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            } else {
                configParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        machine.configureSimulation(configParams);
        
        baseSteps.logInfo("Configured temporal analysis parameters: " + configParams);
    }
    
    @When("I configure the machine with the following environmental parameters:")
    public void configureEnvironmentalParameters(DataTable dataTable) {
        baseSteps.logInfo("Configuring environmental parameters");
        
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        Map<String, Object> configParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Convert string values to appropriate types
            if ("true".equalsIgnoreCase(entry.getValue()) || "false".equalsIgnoreCase(entry.getValue())) {
                configParams.put(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
            } else if (entry.getValue().matches("\\d+")) {
                configParams.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else if (entry.getValue().matches("\\d+\\.\\d+")) {
                configParams.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            } else {
                configParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        machine.configureSimulation(configParams);
        
        baseSteps.logInfo("Configured environmental parameters: " + configParams);
    }
    
    @When("I configure the machine with the following prediction parameters:")
    public void configurePredictionParameters(DataTable dataTable) {
        baseSteps.logInfo("Configuring prediction parameters");
        
        Map<String, String> params = dataTable.asMap(String.class, String.class);
        Map<String, Object> configParams = new HashMap<>();
        
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // Convert string values to appropriate types
            if ("true".equalsIgnoreCase(entry.getValue()) || "false".equalsIgnoreCase(entry.getValue())) {
                configParams.put(entry.getKey(), Boolean.parseBoolean(entry.getValue()));
            } else if (entry.getValue().matches("\\d+")) {
                configParams.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            } else if (entry.getValue().matches("\\d+\\.\\d+")) {
                configParams.put(entry.getKey(), Double.parseDouble(entry.getValue()));
            } else {
                configParams.put(entry.getKey(), entry.getValue());
            }
        }
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        machine.configureSimulation(configParams);
        
        baseSteps.logInfo("Configured prediction parameters: " + configParams);
    }
    
    @When("I load specialized protein data sets:")
    public void loadSpecializedProteinDataSets(DataTable dataTable) {
        baseSteps.logInfo("Loading specialized protein data sets");
        
        List<Map<String, String>> datasets = dataTable.asMaps(String.class, String.class);
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        for (Map<String, String> dataset : datasets) {
            String name = dataset.get("dataset_name");
            int patients = Integer.parseInt(dataset.get("patients"));
            int timepoints = Integer.parseInt(dataset.get("timepoints"));
            int features = Integer.parseInt(dataset.get("features"));
            String format = dataset.get("format");
            
            machine.createDataset(name, "Protein dataset - " + name);
            machine.loadPatientData(name, patients, timepoints, features, format);
            
            baseSteps.logInfo("Loaded protein dataset: " + name);
        }
    }
    
    @When("I load specialized connectivity data sets:")
    public void loadSpecializedConnectivityDataSets(DataTable dataTable) {
        baseSteps.logInfo("Loading specialized connectivity data sets");
        
        List<Map<String, String>> datasets = dataTable.asMaps(String.class, String.class);
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        for (Map<String, String> dataset : datasets) {
            String name = dataset.get("dataset_name");
            int patients = Integer.parseInt(dataset.get("patients"));
            int timepoints = Integer.parseInt(dataset.get("timepoints"));
            int features = Integer.parseInt(dataset.get("features"));
            String format = dataset.get("format");
            
            machine.createDataset(name, "Connectivity dataset - " + name);
            machine.loadPatientData(name, patients, timepoints, features, format);
            
            baseSteps.logInfo("Loaded connectivity dataset: " + name);
        }
    }
    
    @When("I load specialized longitudinal data sets:")
    public void loadSpecializedLongitudinalDataSets(DataTable dataTable) {
        baseSteps.logInfo("Loading specialized longitudinal data sets");
        
        List<Map<String, String>> datasets = dataTable.asMaps(String.class, String.class);
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        for (Map<String, String> dataset : datasets) {
            String name = dataset.get("dataset_name");
            int patients = Integer.parseInt(dataset.get("patients"));
            int timepoints = Integer.parseInt(dataset.get("timepoints"));
            int features = Integer.parseInt(dataset.get("features"));
            String format = dataset.get("format");
            
            machine.createDataset(name, "Longitudinal dataset - " + name);
            machine.loadPatientData(name, patients, timepoints, features, format);
            
            baseSteps.logInfo("Loaded longitudinal dataset: " + name);
        }
    }
    
    @When("I load specialized environmental data sets:")
    public void loadSpecializedEnvironmentalDataSets(DataTable dataTable) {
        baseSteps.logInfo("Loading specialized environmental data sets");
        
        List<Map<String, String>> datasets = dataTable.asMaps(String.class, String.class);
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        for (Map<String, String> dataset : datasets) {
            String name = dataset.get("dataset_name");
            int patients = Integer.parseInt(dataset.get("patients"));
            int timepoints = Integer.parseInt(dataset.get("timepoints"));
            int features = Integer.parseInt(dataset.get("features"));
            String format = dataset.get("format");
            
            machine.createDataset(name, "Environmental dataset - " + name);
            machine.loadPatientData(name, patients, timepoints, features, format);
            
            baseSteps.logInfo("Loaded environmental dataset: " + name);
        }
    }
    
    @When("I load comprehensive multimodal data sets:")
    public void loadComprehensiveMultimodalDataSets(DataTable dataTable) {
        baseSteps.logInfo("Loading comprehensive multimodal data sets");
        
        List<Map<String, String>> datasets = dataTable.asMaps(String.class, String.class);
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        for (Map<String, String> dataset : datasets) {
            String name = dataset.get("dataset_name");
            int patients = Integer.parseInt(dataset.get("patients"));
            int timepoints = Integer.parseInt(dataset.get("timepoints"));
            int features = Integer.parseInt(dataset.get("features"));
            String format = dataset.get("format");
            
            machine.createDataset(name, "Multimodal dataset - " + name);
            machine.loadPatientData(name, patients, timepoints, features, format);
            
            baseSteps.logInfo("Loaded multimodal dataset: " + name);
        }
    }
    
    @When("I execute a focused protein aggregation simulation")
    public void executeFocusedProteinAggregationSimulation() {
        baseSteps.logInfo("Executing focused protein aggregation simulation");
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        // Clear existing results
        machine.resetSimulationResults();
        
        // Run the protein-focused simulation
        machine.executeProteinSimulation();
        
        // Wait for simulation to complete
        while (machine.isSimulationRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Store simulation results
        baseSteps.context.setProperty("protein_simulation_results", machine.getSimulationResults());
        
        baseSteps.logInfo("Protein aggregation simulation completed");
    }
    
    @When("I execute a focused network degeneration simulation")
    public void executeFocusedNetworkDegenerationSimulation() {
        baseSteps.logInfo("Executing focused network degeneration simulation");
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        // Clear existing results
        machine.resetSimulationResults();
        
        // Run the network-focused simulation
        machine.executeNetworkSimulation();
        
        // Wait for simulation to complete
        while (machine.isSimulationRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Store simulation results
        baseSteps.context.setProperty("network_simulation_results", machine.getSimulationResults());
        
        baseSteps.logInfo("Network degeneration simulation completed");
    }
    
    @When("I execute a temporal progression tracking simulation")
    public void executeTemporalProgressionTrackingSimulation() {
        baseSteps.logInfo("Executing temporal progression tracking simulation");
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        // Clear existing results
        machine.resetSimulationResults();
        
        // Run the time series-focused simulation
        machine.executeTimeSeriesAnalysis();
        
        // Wait for simulation to complete
        while (machine.isSimulationRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Store simulation results
        baseSteps.context.setProperty("temporal_simulation_results", machine.getSimulationResults());
        
        baseSteps.logInfo("Temporal progression simulation completed");
    }
    
    @When("I execute an environmental factors impact simulation")
    public void executeEnvironmentalFactorsImpactSimulation() {
        baseSteps.logInfo("Executing environmental factors impact simulation");
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        // Clear existing results
        machine.resetSimulationResults();
        
        // Run the environmental-focused simulation
        machine.executeEnvironmentalFactorsAnalysis();
        
        // Wait for simulation to complete
        while (machine.isSimulationRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Store simulation results
        baseSteps.context.setProperty("environmental_simulation_results", machine.getSimulationResults());
        
        baseSteps.logInfo("Environmental factors simulation completed");
    }
    
    @When("I execute a personalized prediction simulation")
    public void executePersonalizedPredictionSimulation() {
        baseSteps.logInfo("Executing personalized prediction simulation");
        
        SystemSimulationMachine machine = baseSteps.context.retrieve("current_machine");
        
        // Clear existing results
        machine.resetSimulationResults();
        
        // Run the prediction-focused simulation
        machine.executePredictiveModeling();
        
        // Wait for simulation to complete
        while (machine.isSimulationRunning()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Store simulation results
        baseSteps.context.setProperty("prediction_simulation_results", machine.getSimulationResults());
        
        baseSteps.logInfo("Personalized prediction simulation completed");
    }
    
    @When("I create the following specialized machines:")
    public void createSpecializedMachines(DataTable dataTable) {
        baseSteps.logInfo("Creating multiple specialized machines");
        
        List<Map<String, String>> machineSpecs = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> spec : machineSpecs) {
            String machineType = spec.get("machine_type");
            String focusArea = spec.get("focus_area");
            
            SystemSimulationMachine machine = null;
            
            // Create appropriate machine type
            switch (machineType.toLowerCase()) {
                case "protein_aggregation":
                    machine = ALZ001MockFactory.createProteinAggregationMachine(machineType + "Machine");
                    break;
                case "network_connectivity":
                    machine = ALZ001MockFactory.createNetworkConnectivityMachine(machineType + "Machine");
                    break;
                case "temporal_progression":
                    machine = ALZ001MockFactory.createTemporalProgressionMachine(machineType + "Machine");
                    break;
                case "environmental_factors":
                    machine = ALZ001MockFactory.createEnvironmentalFactorsMachine(machineType + "Machine");
                    break;
                case "predictive_modeling":
                    machine = ALZ001MockFactory.createPredictiveModelingMachine(machineType + "Machine");
                    break;
                default:
                    machine = ALZ001MockFactory.createSystemSimulationMachine(machineType + "Machine");
                    break;
            }
            
            // Store the machine in our map
            specializedMachines.put(machineType, machine);
            
            baseSteps.logInfo("Created specialized machine: " + machineType + " focused on " + focusArea);
        }
        
        // Store all machines in the context
        baseSteps.context.setProperty("specialized_machines", specializedMachines);
    }
    
    @When("I integrate data flows between all specialized machines")
    public void integrateDataFlowsBetweenSpecializedMachines() {
        baseSteps.logInfo("Integrating data flows between specialized machines");
        
        // This is a simplified simulation of cross-machine data flows
        // In a real implementation, this would involve actual data exchange between machines
        
        Map<String, Map<String, Object>> integratedResults = new HashMap<>();
        Map<String, List<String>> machineConnections = new HashMap<>();
        
        // Define connections between machines
        List<String> proteinConnections = new ArrayList<>();
        proteinConnections.add("network_connectivity");
        proteinConnections.add("temporal_progression");
        machineConnections.put("protein_aggregation", proteinConnections);
        
        List<String> networkConnections = new ArrayList<>();
        networkConnections.add("temporal_progression");
        networkConnections.add("predictive_modeling");
        machineConnections.put("network_connectivity", networkConnections);
        
        List<String> temporalConnections = new ArrayList<>();
        temporalConnections.add("predictive_modeling");
        machineConnections.put("temporal_progression", temporalConnections);
        
        List<String> environmentalConnections = new ArrayList<>();
        environmentalConnections.add("protein_aggregation");
        environmentalConnections.add("predictive_modeling");
        machineConnections.put("environmental_factors", environmentalConnections);
        
        // Store the connection map
        baseSteps.context.setProperty("machine_connections", machineConnections);
        
        baseSteps.logInfo("Integrated data flows between specialized machines");
    }
    
    @When("I execute a multi-scale cross-machine analysis")
    public void executeMultiScaleCrossMachineAnalysis() {
        baseSteps.logInfo("Executing multi-scale cross-machine analysis");
        
        @SuppressWarnings("unchecked")
        Map<String, SystemSimulationMachine> machines = baseSteps.context.retrieve("specialized_machines");
        
        // Execute each specialized machine
        for (Map.Entry<String, SystemSimulationMachine> entry : machines.entrySet()) {
            String machineType = entry.getKey();
            SystemSimulationMachine machine = entry.getValue();
            
            machine.resetSimulationResults();
            
            switch (machineType) {
                case "protein_aggregation":
                    machine.executeProteinSimulation();
                    break;
                case "network_connectivity":
                    machine.executeNetworkSimulation();
                    break;
                case "temporal_progression":
                    machine.executeTimeSeriesAnalysis();
                    break;
                case "environmental_factors":
                    machine.executeEnvironmentalFactorsAnalysis();
                    break;
                case "predictive_modeling":
                    machine.executePredictiveModeling();
                    break;
                default:
                    machine.executeFullSimulation();
                    break;
            }
            
            // Wait for simulation to complete
            while (machine.isSimulationRunning()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // Create integrated results across all machines
        Map<String, Object> crossScaleResults = new HashMap<>();
        
        // Add individual machine results
        for (Map.Entry<String, SystemSimulationMachine> entry : machines.entrySet()) {
            String machineType = entry.getKey();
            SystemSimulationMachine machine = entry.getValue();
            crossScaleResults.put(machineType + "_results", machine.getSimulationResults());
        }
        
        // Add cross-scale analyses
        crossScaleResults.put("cross_scale_correlations", createMockCrossScaleCorrelations());
        crossScaleResults.put("causal_relationships", createMockCausalRelationships());
        crossScaleResults.put("therapeutic_targets", createMockTherapeuticTargets());
        
        // Store the integrated results
        baseSteps.context.setProperty("cross_scale_results", crossScaleResults);
        
        baseSteps.logInfo("Multi-scale cross-machine analysis completed");
    }
    
    // Helper method to create mock cross-scale correlations
    private Map<String, Double> createMockCrossScaleCorrelations() {
        Map<String, Double> correlations = new HashMap<>();
        correlations.put("protein_aggregation_to_network_connectivity", 0.82);
        correlations.put("network_connectivity_to_cognitive_decline", 0.76);
        correlations.put("environmental_to_protein_aggregation", 0.67);
        correlations.put("temporal_progression_to_clinical_outcomes", 0.79);
        return correlations;
    }
    
    // Helper method to create mock causal relationships
    private List<Map<String, Object>> createMockCausalRelationships() {
        List<Map<String, Object>> relationships = new ArrayList<>();
        
        Map<String, Object> relationship1 = new HashMap<>();
        relationship1.put("cause", "protein_misfolding");
        relationship1.put("effect", "synaptic_dysfunction");
        relationship1.put("strength", 0.85);
        relationship1.put("confidence", 0.92);
        relationship1.put("scales", Arrays.asList("molecular", "cellular"));
        relationships.add(relationship1);
        
        Map<String, Object> relationship2 = new HashMap<>();
        relationship2.put("cause", "network_hub_degeneration");
        relationship2.put("effect", "cognitive_impairment");
        relationship2.put("strength", 0.78);
        relationship2.put("confidence", 0.88);
        relationship2.put("scales", Arrays.asList("cellular", "network", "clinical"));
        relationships.add(relationship2);
        
        Map<String, Object> relationship3 = new HashMap<>();
        relationship3.put("cause", "environmental_stress");
        relationship3.put("effect", "increased_protein_aggregation");
        relationship3.put("strength", 0.69);
        relationship3.put("confidence", 0.81);
        relationship3.put("scales", Arrays.asList("environmental", "molecular"));
        relationships.add(relationship3);
        
        return relationships;
    }
    
    // Helper method to create mock therapeutic targets
    private List<Map<String, Object>> createMockTherapeuticTargets() {
        List<Map<String, Object>> targets = new ArrayList<>();
        
        Map<String, Object> target1 = new HashMap<>();
        target1.put("name", "tau_aggregation_inhibitor");
        target1.put("mechanism", "prevent_misfolding");
        target1.put("efficacy_score", 0.88);
        target1.put("novelty_score", 0.75);
        target1.put("target_scale", "molecular");
        targets.add(target1);
        
        Map<String, Object> target2 = new HashMap<>();
        target2.put("name", "network_resilience_enhancer");
        target2.put("mechanism", "enhance_alternative_pathways");
        target2.put("efficacy_score", 0.72);
        target2.put("novelty_score", 0.91);
        target2.put("target_scale", "network");
        targets.add(target2);
        
        Map<String, Object> target3 = new HashMap<>();
        target3.put("name", "combined_environmental_intervention");
        target3.put("mechanism", "reduce_stress_and_improve_diet");
        target3.put("efficacy_score", 0.81);
        target3.put("novelty_score", 0.65);
        target3.put("target_scale", "environmental");
        targets.add(target3);
        
        return targets;
    }
    
    @Then("the machine should generate protein spreading patterns across neural tissues")
    public void machineShouldGenerateProteinSpreadingPatterns() {
        baseSteps.logInfo("Verifying machine generated protein spreading patterns");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("protein_simulation_results");
        
        // Check for protein spreading patterns
        assertTrue(results.containsKey("protein_spreading_patterns"), 
                "Results should include protein spreading patterns");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> patterns = (Map<String, Object>) results.get("protein_spreading_patterns");
        
        assertNotNull(patterns, "Protein spreading patterns should not be null");
        assertTrue(patterns.containsKey("amyloid_pattern"), "Results should include amyloid pattern");
        assertTrue(patterns.containsKey("tau_pattern"), "Results should include tau pattern");
        
        baseSteps.logInfo("Verified protein spreading patterns were generated");
    }
    
    @Then("the simulation should track tau and amyloid progression over time")
    public void simulationShouldTrackTauAndAmyloidProgression() {
        baseSteps.logInfo("Verifying simulation tracked tau and amyloid progression");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("protein_simulation_results");
        
        // Check for protein progression data
        assertTrue(results.containsKey("protein_progressions"), 
                "Results should include protein progressions");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> progressions = (Map<String, Object>) results.get("protein_progressions");
        
        assertNotNull(progressions, "Protein progressions should not be null");
        assertTrue(progressions.containsKey("amyloid_progression"), "Results should include amyloid progression");
        assertTrue(progressions.containsKey("tau_progression"), "Results should include tau progression");
        
        // Check that we have time series data
        @SuppressWarnings("unchecked")
        List<Double> amyloidProgression = (List<Double>) progressions.get("amyloid_progression");
        assertFalse(amyloidProgression.isEmpty(), "Amyloid progression should not be empty");
        
        @SuppressWarnings("unchecked")
        List<Double> tauProgression = (List<Double>) progressions.get("tau_progression");
        assertFalse(tauProgression.isEmpty(), "Tau progression should not be empty");
        
        baseSteps.logInfo("Verified tau and amyloid progression was tracked");
    }
    
    @Then("the results should correlate with clinical cognitive decline measures")
    public void resultsShouldCorrelateWithCognitiveDeclineMeasures() {
        baseSteps.logInfo("Verifying results correlate with cognitive decline measures");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("protein_simulation_results");
        
        // Check for correlations
        assertTrue(results.containsKey("clinical_correlations"), 
                "Results should include clinical correlations");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> correlations = (Map<String, Double>) results.get("clinical_correlations");
        
        assertNotNull(correlations, "Clinical correlations should not be null");
        assertTrue(correlations.containsKey("amyloid_to_cognitive_decline"), 
                "Results should include amyloid to cognitive decline correlation");
        assertTrue(correlations.containsKey("tau_to_cognitive_decline"), 
                "Results should include tau to cognitive decline correlation");
        
        // Check correlation strengths
        double amyloidCorrelation = correlations.get("amyloid_to_cognitive_decline");
        double tauCorrelation = correlations.get("tau_to_cognitive_decline");
        
        assertTrue(amyloidCorrelation > 0.5, 
                "Amyloid correlation should be significant (> 0.5)");
        assertTrue(tauCorrelation > 0.5, 
                "Tau correlation should be significant (> 0.5)");
        
        baseSteps.logInfo("Verified correlations with cognitive decline measures");
    }
    
    @Then("the machine should identify vulnerable network hubs")
    public void machineShouldIdentifyVulnerableNetworkHubs() {
        baseSteps.logInfo("Verifying machine identified vulnerable network hubs");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("network_simulation_results");
        
        // Check for vulnerable hubs
        assertTrue(results.containsKey("vulnerable_hubs"), 
                "Results should include vulnerable hubs");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> hubs = (List<Map<String, Object>>) results.get("vulnerable_hubs");
        
        assertNotNull(hubs, "Vulnerable hubs should not be null");
        assertFalse(hubs.isEmpty(), "Vulnerable hubs list should not be empty");
        
        // Check hub properties
        Map<String, Object> firstHub = hubs.get(0);
        assertTrue(firstHub.containsKey("hub_name"), "Hub should have a name");
        assertTrue(firstHub.containsKey("centrality"), "Hub should have centrality measure");
        assertTrue(firstHub.containsKey("vulnerability_score"), "Hub should have vulnerability score");
        
        baseSteps.logInfo("Verified vulnerable network hubs were identified");
    }
    
    @Then("the simulation should show progressive connectivity loss patterns")
    public void simulationShouldShowProgressiveConnectivityLossPatterns() {
        baseSteps.logInfo("Verifying simulation showed progressive connectivity loss patterns");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("network_simulation_results");
        
        // Check for connectivity loss patterns
        assertTrue(results.containsKey("connectivity_loss_patterns"), 
                "Results should include connectivity loss patterns");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> patterns = (Map<String, Object>) results.get("connectivity_loss_patterns");
        
        assertNotNull(patterns, "Connectivity loss patterns should not be null");
        assertTrue(patterns.containsKey("global_connectivity"), "Results should include global connectivity");
        assertTrue(patterns.containsKey("regional_connectivity"), "Results should include regional connectivity");
        
        // Check that we have time series data for connectivity loss
        @SuppressWarnings("unchecked")
        List<Double> globalConnectivity = (List<Double>) patterns.get("global_connectivity");
        assertFalse(globalConnectivity.isEmpty(), "Global connectivity should not be empty");
        
        // Check progressive decline
        double firstValue = globalConnectivity.get(0);
        double lastValue = globalConnectivity.get(globalConnectivity.size() - 1);
        assertTrue(lastValue < firstValue, "Connectivity should show progressive decline");
        
        baseSteps.logInfo("Verified progressive connectivity loss patterns");
    }
    
    @Then("the results should match known neurodegeneration progression routes")
    public void resultsShouldMatchKnownNeurodegenerationProgressionRoutes() {
        baseSteps.logInfo("Verifying results match known neurodegeneration progression routes");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("network_simulation_results");
        
        // Check for progression routes
        assertTrue(results.containsKey("progression_routes"), 
                "Results should include progression routes");
        
        @SuppressWarnings("unchecked")
        List<String> routes = (List<String>) results.get("progression_routes");
        
        assertNotNull(routes, "Progression routes should not be null");
        assertFalse(routes.isEmpty(), "Progression routes should not be empty");
        
        // Check for known progression routes
        List<String> knownRoutes = Arrays.asList(
            "transentorhinal",
            "limbic",
            "isocortical"
        );
        
        for (String knownRoute : knownRoutes) {
            assertTrue(routes.stream().anyMatch(r -> r.contains(knownRoute)),
                    "Results should include known route: " + knownRoute);
        }
        
        baseSteps.logInfo("Verified match with known neurodegeneration progression routes");
    }
    
    @Then("the machine should detect disease stage transitions")
    public void machineShouldDetectDiseaseStageTransitions() {
        baseSteps.logInfo("Verifying machine detected disease stage transitions");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("temporal_simulation_results");
        
        // Check for disease stage transitions
        assertTrue(results.containsKey("disease_stage_transitions"), 
                "Results should include disease stage transitions");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> transitions = (List<Map<String, Object>>) results.get("disease_stage_transitions");
        
        assertNotNull(transitions, "Disease stage transitions should not be null");
        assertFalse(transitions.isEmpty(), "Disease stage transitions should not be empty");
        
        // Check transition properties
        Map<String, Object> firstTransition = transitions.get(0);
        assertTrue(firstTransition.containsKey("from_stage"), "Transition should have from_stage");
        assertTrue(firstTransition.containsKey("to_stage"), "Transition should have to_stage");
        assertTrue(firstTransition.containsKey("transition_time"), "Transition should have transition_time");
        assertTrue(firstTransition.containsKey("probability"), "Transition should have probability");
        
        baseSteps.logInfo("Verified disease stage transitions were detected");
    }
    
    @Then("the simulation should identify temporal ordering of biomarker changes")
    public void simulationShouldIdentifyTemporalOrderingOfBiomarkerChanges() {
        baseSteps.logInfo("Verifying simulation identified temporal ordering of biomarker changes");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("temporal_simulation_results");
        
        // Check for biomarker ordering
        assertTrue(results.containsKey("biomarker_ordering"), 
                "Results should include biomarker ordering");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> biomarkerOrder = (List<Map<String, Object>>) results.get("biomarker_ordering");
        
        assertNotNull(biomarkerOrder, "Biomarker ordering should not be null");
        assertFalse(biomarkerOrder.isEmpty(), "Biomarker ordering should not be empty");
        
        // Check biomarker properties
        Map<String, Object> firstBiomarker = biomarkerOrder.get(0);
        assertTrue(firstBiomarker.containsKey("biomarker_name"), "Biomarker should have name");
        assertTrue(firstBiomarker.containsKey("order_position"), "Biomarker should have order position");
        assertTrue(firstBiomarker.containsKey("change_point_time"), "Biomarker should have change point time");
        
        // Check ordering is sequential
        for (int i = 1; i < biomarkerOrder.size(); i++) {
            int prevPosition = (int) biomarkerOrder.get(i-1).get("order_position");
            int currPosition = (int) biomarkerOrder.get(i).get("order_position");
            assertTrue(currPosition > prevPosition, "Biomarker ordering should be sequential");
        }
        
        baseSteps.logInfo("Verified temporal ordering of biomarker changes");
    }
    
    @Then("the results should predict future disease trajectory with at least {int}% accuracy")
    public void resultsShouldPredictFutureTrajectoryWithMinimumAccuracy(int minAccuracy) {
        baseSteps.logInfo("Verifying results predict future trajectory with at least " + minAccuracy + "% accuracy");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("temporal_simulation_results");
        
        // Check for prediction accuracy
        assertTrue(results.containsKey("trajectory_predictions"), 
                "Results should include trajectory predictions");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> predictions = (Map<String, Object>) results.get("trajectory_predictions");
        
        assertNotNull(predictions, "Trajectory predictions should not be null");
        assertTrue(predictions.containsKey("prediction_accuracy"), "Predictions should include accuracy");
        
        // Check prediction accuracy meets minimum requirement
        double accuracy = (double) predictions.get("prediction_accuracy") * 100; // Convert to percentage
        assertTrue(accuracy >= minAccuracy, 
                "Prediction accuracy (" + accuracy + "%) should be at least " + minAccuracy + "%");
        
        baseSteps.logInfo("Verified prediction accuracy of " + accuracy + "%");
    }
    
    @Then("the machine should quantify lifestyle contributions to disease risk")
    public void machineShouldQuantifyLifestyleContributionsToRisk() {
        baseSteps.logInfo("Verifying machine quantified lifestyle contributions to disease risk");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("environmental_simulation_results");
        
        // Check for lifestyle contributions
        assertTrue(results.containsKey("lifestyle_contributions"), 
                "Results should include lifestyle contributions");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> contributions = (Map<String, Double>) results.get("lifestyle_contributions");
        
        assertNotNull(contributions, "Lifestyle contributions should not be null");
        assertTrue(contributions.containsKey("diet"), "Contributions should include diet");
        assertTrue(contributions.containsKey("physical_activity"), "Contributions should include physical activity");
        assertTrue(contributions.containsKey("stress"), "Contributions should include stress");
        assertTrue(contributions.containsKey("sleep"), "Contributions should include sleep");
        
        // Check contributions sum to 100%
        double sum = contributions.values().stream().mapToDouble(Double::doubleValue).sum();
        assertEquals(1.0, sum, 0.01, "Contributions should sum to approximately 100%");
        
        baseSteps.logInfo("Verified lifestyle contributions to disease risk");
    }
    
    @Then("the simulation should model environmental interventions efficacy")
    public void simulationShouldModelEnvironmentalInterventionsEfficacy() {
        baseSteps.logInfo("Verifying simulation modeled environmental interventions efficacy");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("environmental_simulation_results");
        
        // Check for intervention efficacy
        assertTrue(results.containsKey("intervention_efficacy"), 
                "Results should include intervention efficacy");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> interventions = (List<Map<String, Object>>) results.get("intervention_efficacy");
        
        assertNotNull(interventions, "Intervention efficacy should not be null");
        assertFalse(interventions.isEmpty(), "Intervention efficacy should not be empty");
        
        // Check intervention properties
        Map<String, Object> firstIntervention = interventions.get(0);
        assertTrue(firstIntervention.containsKey("intervention_name"), "Intervention should have name");
        assertTrue(firstIntervention.containsKey("efficacy_score"), "Intervention should have efficacy score");
        assertTrue(firstIntervention.containsKey("confidence_interval"), "Intervention should have confidence interval");
        
        baseSteps.logInfo("Verified environmental interventions efficacy modeling");
    }
    
    @Then("the results should stratify subjects by modifiable risk factors")
    public void resultsShouldStratifySubjectsByModifiableRiskFactors() {
        baseSteps.logInfo("Verifying results stratified subjects by modifiable risk factors");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("environmental_simulation_results");
        
        // Check for risk stratification
        assertTrue(results.containsKey("risk_stratification"), 
                "Results should include risk stratification");
        
        @SuppressWarnings("unchecked")
        Map<String, List<String>> stratification = (Map<String, List<String>>) results.get("risk_stratification");
        
        assertNotNull(stratification, "Risk stratification should not be null");
        assertTrue(stratification.containsKey("high_risk"), "Stratification should include high risk group");
        assertTrue(stratification.containsKey("medium_risk"), "Stratification should include medium risk group");
        assertTrue(stratification.containsKey("low_risk"), "Stratification should include low risk group");
        
        // Check groups are not empty
        assertFalse(stratification.get("high_risk").isEmpty(), "High risk group should not be empty");
        assertFalse(stratification.get("medium_risk").isEmpty(), "Medium risk group should not be empty");
        assertFalse(stratification.get("low_risk").isEmpty(), "Low risk group should not be empty");
        
        baseSteps.logInfo("Verified stratification by modifiable risk factors");
    }
    
    @Then("the machine should generate personalized risk profiles")
    public void machineShouldGeneratePersonalizedRiskProfiles() {
        baseSteps.logInfo("Verifying machine generated personalized risk profiles");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("prediction_simulation_results");
        
        // Check for risk profiles
        assertTrue(results.containsKey("risk_profiles"), 
                "Results should include risk profiles");
        
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> profiles = (Map<String, Map<String, Object>>) results.get("risk_profiles");
        
        assertNotNull(profiles, "Risk profiles should not be null");
        assertFalse(profiles.isEmpty(), "Risk profiles should not be empty");
        
        // Check profile properties for first patient
        String firstPatientId = profiles.keySet().iterator().next();
        Map<String, Object> firstProfile = profiles.get(firstPatientId);
        
        assertTrue(firstProfile.containsKey("overall_risk"), "Profile should include overall risk");
        assertTrue(firstProfile.containsKey("risk_factors"), "Profile should include risk factors");
        assertTrue(firstProfile.containsKey("progression_timeline"), "Profile should include progression timeline");
        
        baseSteps.logInfo("Verified personalized risk profiles generation");
    }
    
    @Then("the simulation should provide tailored intervention recommendations")
    public void simulationShouldProvideTailoredInterventionRecommendations() {
        baseSteps.logInfo("Verifying simulation provided tailored intervention recommendations");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("prediction_simulation_results");
        
        // Check for intervention recommendations
        assertTrue(results.containsKey("intervention_recommendations"), 
                "Results should include intervention recommendations");
        
        @SuppressWarnings("unchecked")
        Map<String, List<Map<String, Object>>> recommendations = 
            (Map<String, List<Map<String, Object>>>) results.get("intervention_recommendations");
        
        assertNotNull(recommendations, "Intervention recommendations should not be null");
        assertFalse(recommendations.isEmpty(), "Intervention recommendations should not be empty");
        
        // Check recommendations for first patient
        String firstPatientId = recommendations.keySet().iterator().next();
        List<Map<String, Object>> patientRecs = recommendations.get(firstPatientId);
        
        assertFalse(patientRecs.isEmpty(), "Patient should have recommendations");
        
        // Check recommendation properties
        Map<String, Object> firstRec = patientRecs.get(0);
        assertTrue(firstRec.containsKey("intervention_type"), "Recommendation should have type");
        assertTrue(firstRec.containsKey("description"), "Recommendation should have description");
        assertTrue(firstRec.containsKey("expected_benefit"), "Recommendation should have expected benefit");
        
        baseSteps.logInfo("Verified tailored intervention recommendations");
    }
    
    @Then("the results should demonstrate better precision than population-level models")
    public void resultsShouldDemonstrateBetterPrecision() {
        baseSteps.logInfo("Verifying results demonstrated better precision than population-level models");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("prediction_simulation_results");
        
        // Check for model comparison
        assertTrue(results.containsKey("model_comparison"), 
                "Results should include model comparison");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> comparison = (Map<String, Double>) results.get("model_comparison");
        
        assertNotNull(comparison, "Model comparison should not be null");
        assertTrue(comparison.containsKey("personalized_precision"), "Comparison should include personalized precision");
        assertTrue(comparison.containsKey("population_precision"), "Comparison should include population precision");
        
        // Check personalized precision is better
        double personalizedPrecision = comparison.get("personalized_precision");
        double populationPrecision = comparison.get("population_precision");
        
        assertTrue(personalizedPrecision > populationPrecision, 
                "Personalized precision (" + personalizedPrecision + ") should be higher than population precision (" + 
                        populationPrecision + ")");
        
        baseSteps.logInfo("Verified better precision than population-level models: " + 
                personalizedPrecision + " vs " + populationPrecision);
    }
    
    @Then("the analysis should identify causal relationships between scales")
    public void analysisShouldIdentifyCausalRelationshipsBetweenScales() {
        baseSteps.logInfo("Verifying analysis identified causal relationships between scales");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("cross_scale_results");
        
        // Check for causal relationships
        assertTrue(results.containsKey("causal_relationships"), 
                "Results should include causal relationships");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> relationships = (List<Map<String, Object>>) results.get("causal_relationships");
        
        assertNotNull(relationships, "Causal relationships should not be null");
        assertFalse(relationships.isEmpty(), "Causal relationships should not be empty");
        
        // Check relationship properties
        for (Map<String, Object> relationship : relationships) {
            assertTrue(relationship.containsKey("cause"), "Relationship should have cause");
            assertTrue(relationship.containsKey("effect"), "Relationship should have effect");
            assertTrue(relationship.containsKey("strength"), "Relationship should have strength");
            assertTrue(relationship.containsKey("confidence"), "Relationship should have confidence");
            
            // Check scales are included
            assertTrue(relationship.containsKey("scales"), "Relationship should have scales");
            
            @SuppressWarnings("unchecked")
            List<String> scales = (List<String>) relationship.get("scales");
            assertTrue(scales.size() >= 2, "Relationship should involve at least 2 scales");
        }
        
        baseSteps.logInfo("Verified causal relationships between scales");
    }
    
    @Then("the integrated results should have greater explanatory power")
    public void integratedResultsShouldHaveGreaterExplanatoryPower() {
        baseSteps.logInfo("Verifying integrated results have greater explanatory power");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("cross_scale_results");
        
        // Check for explanatory power comparison
        assertTrue(results.containsKey("explanatory_power"), 
                "Results should include explanatory power comparison");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> explanatoryPower = (Map<String, Double>) results.get("explanatory_power");
        
        assertNotNull(explanatoryPower, "Explanatory power comparison should not be null");
        assertTrue(explanatoryPower.containsKey("integrated_model"), "Comparison should include integrated model");
        assertTrue(explanatoryPower.containsKey("individual_models_average"), 
                "Comparison should include individual models average");
        
        // Check integrated model has better explanatory power
        double integratedPower = explanatoryPower.get("integrated_model");
        double individualPower = explanatoryPower.get("individual_models_average");
        
        assertTrue(integratedPower > individualPower, 
                "Integrated model power (" + integratedPower + ") should be higher than individual models (" + 
                        individualPower + ")");
        
        baseSteps.logInfo("Verified greater explanatory power of integrated results: " + 
                integratedPower + " vs " + individualPower);
    }
    
    @Then("the cross-scale model should identify novel therapeutic targets")
    public void crossScaleModelShouldIdentifyNovelTherapeuticTargets() {
        baseSteps.logInfo("Verifying cross-scale model identified novel therapeutic targets");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> results = baseSteps.context.retrieve("cross_scale_results");
        
        // Check for therapeutic targets
        assertTrue(results.containsKey("therapeutic_targets"), 
                "Results should include therapeutic targets");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> targets = (List<Map<String, Object>>) results.get("therapeutic_targets");
        
        assertNotNull(targets, "Therapeutic targets should not be null");
        assertFalse(targets.isEmpty(), "Therapeutic targets should not be empty");
        
        // Check target properties
        for (Map<String, Object> target : targets) {
            assertTrue(target.containsKey("name"), "Target should have name");
            assertTrue(target.containsKey("mechanism"), "Target should have mechanism");
            assertTrue(target.containsKey("efficacy_score"), "Target should have efficacy score");
            assertTrue(target.containsKey("novelty_score"), "Target should have novelty score");
            
            // At least one target should have high novelty
            if ((double) target.get("novelty_score") > 0.8) {
                baseSteps.logInfo("Found novel therapeutic target: " + target.get("name") + 
                               " with novelty score " + target.get("novelty_score"));
            }
        }
        
        // Check that at least one target is novel
        boolean hasNovelTarget = targets.stream()
                .anyMatch(t -> (double) t.get("novelty_score") > 0.8);
        
        assertTrue(hasNovelTarget, "Results should include at least one novel therapeutic target");
        
        baseSteps.logInfo("Verified identification of novel therapeutic targets");
    }
}