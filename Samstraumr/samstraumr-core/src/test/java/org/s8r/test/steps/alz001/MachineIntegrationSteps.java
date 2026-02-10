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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions for machine integration test scenarios.
 * 
 * <p>Implements steps for creating, configuring, and testing integrated disease
 * modeling machines that combine multiple composites.
 */
public class MachineIntegrationSteps extends ALZ001BaseSteps {

    /**
     * Sets up a scientific analysis environment for integrated modeling.
     */
    @Given("a scientific analysis environment for integrated modeling")
    public void scientificAnalysisEnvironmentForIntegratedModeling() {
        // Create a mock or real scientific environment for testing
        ScientificAnalysisEnvironment environment = new ScientificAnalysisEnvironment();
        environment.enableCapability("protein_expression_modeling");
        environment.enableCapability("neuronal_network_modeling");
        environment.enableCapability("time_series_analysis");
        environment.enableCapability("machine_learning");
        
        // Store in context
        storeInContext("environment", environment);
    }
    
    /**
     * Prepares a machine configuration for Alzheimer's disease simulation.
     */
    @Given("a machine configuration for Alzheimer's disease simulation")
    public void machineConfigurationForAlzheimersSimulation() {
        Map<String, Object> config = new HashMap<>();
        
        // Default machine configuration
        config.put("simulation_duration_days", 365);
        config.put("time_step_hours", 24);
        config.put("protein_sampling_frequency", 7);
        config.put("neuronal_assessment_frequency", 30);
        config.put("random_seed", 12345);
        
        // Store in context
        storeInContext("machineConfig", config);
    }
    
    /**
     * Creates an Alzheimer's disease modeling machine.
     */
    @When("I create an Alzheimer's disease modeling machine")
    public void createAlzheimersModelingMachine() {
        // Create the machine
        AlzheimersMachine machine = new AlzheimersMachine();
        machine.initialize();
        
        // Store in test context
        storeInContext("alzheimersMachine", machine);
    }
    
    /**
     * Verifies the machine contains specified composites.
     */
    @Then("the machine should contain the following composites:")
    public void machineShouldContainComposites(DataTable dataTable) {
        AlzheimersMachine machine = getFromContext("alzheimersMachine");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Verify each composite exists
        for (Map<String, String> row : rows) {
            String compositeType = row.get("composite_type");
            assertTrue(machine.hasComposite(compositeType),
                    "Machine should contain composite: " + compositeType);
        }
    }
    
    /**
     * Sets up an initialized Alzheimer's disease modeling machine.
     */
    @Given("an initialized Alzheimer's disease modeling machine")
    public void initializedAlzheimersModelingMachine() {
        createAlzheimersModelingMachine();
    }
    
    /**
     * Configures the machine with simulation parameters.
     */
    @When("I configure the machine with the following parameters:")
    public void configureMachineWithParameters(DataTable dataTable) {
        AlzheimersMachine machine = getFromContext("alzheimersMachine");
        
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
        machine.configure(parameters);
        
        // Store parameters
        storeInContext("machineParameters", parameters);
    }
    
    /**
     * Verifies the machine is successfully configured.
     */
    @Then("the machine should be successfully configured")
    public void machineShouldBeSuccessfullyConfigured() {
        AlzheimersMachine machine = getFromContext("alzheimersMachine");
        Map<String, Object> configuredParams = machine.getConfiguration();
        
        Map<String, Object> expectedParams = getFromContext("machineParameters");
        
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
     * Verifies parameters are propagated to appropriate composites.
     */
    @Then("the parameters should be propagated to the appropriate composites")
    public void parametersShouldBePropagatedToComposites() {
        AlzheimersMachine machine = getFromContext("alzheimersMachine");
        
        // Verify propagation to protein expression composite
        Map<String, Object> proteinConfig = machine.getCompositeConfiguration("protein_expression");
        assertTrue(proteinConfig.containsKey("sampling_frequency"), 
                "Protein expression composite should have sampling frequency parameter");
        
        // Verify propagation to neuronal network composite
        Map<String, Object> neuronalConfig = machine.getCompositeConfiguration("neuronal_network");
        assertTrue(neuronalConfig.containsKey("assessment_frequency"), 
                "Neuronal network composite should have assessment frequency parameter");
        
        // Verify propagation to time series composite
        Map<String, Object> timeSeriesConfig = machine.getCompositeConfiguration("time_series_analysis");
        assertTrue(timeSeriesConfig.containsKey("time_step_hours"), 
                "Time series composite should have time step parameter");
    }
    
    /**
     * Sets up a configured Alzheimer's disease modeling machine.
     */
    @Given("a configured Alzheimer's disease modeling machine")
    public void configuredAlzheimersModelingMachine() {
        initializedAlzheimersModelingMachine();
        
        // Create parameter data table
        List<Map<String, String>> rows = List.of(
                Map.of("parameter", "simulation_duration_days", "value", "365"),
                Map.of("parameter", "time_step_hours", "value", "24"),
                Map.of("parameter", "protein_sampling_frequency", "value", "7"),
                Map.of("parameter", "neuronal_assessment_frequency", "value", "30"),
                Map.of("parameter", "random_seed", "value", "12345")
        );
        
        DataTable dataTable = DataTable.create(rows);
        configureMachineWithParameters(dataTable);
    }
    
    /**
     * Loads baseline patient data.
     */
    @When("I load baseline patient data")
    public void loadBaselinePatientData() {
        AlzheimersMachine machine = getFromContext("alzheimersMachine");
        
        // Load default baseline data
        PatientDataset dataset = new PatientDataset("baseline");
        machine.loadData(dataset);
        
        // Store in context
        storeInContext("patientDataset", dataset);
    }
    
    /**
     * Runs simulation with specified progression factors.
     */
    @When("I run the simulation with the following progression factors:")
    public void runSimulationWithProgressionFactors(DataTable dataTable) {
        AlzheimersMachine machine = getFromContext("alzheimersMachine");
        
        // Parse data table
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        // Configure progression factors
        Map<String, Map<String, Object>> factors = new HashMap<>();
        for (Map<String, String> row : rows) {
            String factor = row.get("factor");
            double strength = Double.parseDouble(row.get("influence_strength"));
            String pattern = row.get("temporal_pattern");
            
            Map<String, Object> factorConfig = new HashMap<>();
            factorConfig.put("strength", strength);
            factorConfig.put("pattern", pattern);
            
            factors.put(factor, factorConfig);
        }
        
        // Run simulation
        machine.configureProgressionFactors(factors);
        machine.runSimulation();
        
        // Store simulation results
        storeInContext("simulationResults", machine.getResults());
    }
    
    /**
     * Verifies machine generates a complete disease progression model.
     */
    @Then("the machine should generate a complete disease progression model")
    public void machineShouldGenerateCompleteModel() {
        Map<String, Object> results = getFromContext("simulationResults");
        
        assertNotNull(results, "Simulation results should not be null");
        assertTrue(results.containsKey("disease_model"), 
                "Results should include a disease model");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> model = (Map<String, Object>) results.get("disease_model");
        
        assertTrue(model.containsKey("time_points"), "Model should include time points");
        assertTrue(model.containsKey("protein_profiles"), "Model should include protein profiles");
        assertTrue(model.containsKey("neuronal_states"), "Model should include neuronal states");
        assertTrue(model.containsKey("correlations"), "Model should include correlations");
    }
    
    /**
     * Verifies model includes temporal protein expression profiles.
     */
    @Then("the model should include temporal protein expression profiles")
    public void modelShouldIncludeProteinProfiles() {
        Map<String, Object> results = getFromContext("simulationResults");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> model = (Map<String, Object>) results.get("disease_model");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> proteinProfiles = (Map<String, Object>) model.get("protein_profiles");
        
        assertTrue(proteinProfiles.containsKey("tau"), 
                "Protein profiles should include tau");
        assertTrue(proteinProfiles.containsKey("amyloid_beta"), 
                "Protein profiles should include amyloid_beta");
        
        // Verify temporal nature of data
        @SuppressWarnings("unchecked")
        List<Double> tauProfile = (List<Double>) proteinProfiles.get("tau");
        assertTrue(tauProfile.size() > 1, "Protein profile should include multiple time points");
    }
    
    /**
     * Verifies model includes neuronal network degradation patterns.
     */
    @Then("the model should include neuronal network degradation patterns")
    public void modelShouldIncludeNeuronalDegradation() {
        Map<String, Object> results = getFromContext("simulationResults");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> model = (Map<String, Object>) results.get("disease_model");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> neuronalStates = (Map<String, Object>) model.get("neuronal_states");
        
        assertTrue(neuronalStates.containsKey("connectivity"), 
                "Neuronal states should include connectivity");
        assertTrue(neuronalStates.containsKey("hub_integrity"), 
                "Neuronal states should include hub integrity");
        assertTrue(neuronalStates.containsKey("functional_clusters"), 
                "Neuronal states should include functional clusters");
    }
    
    /**
     * Mock class representing an Alzheimer's disease modeling machine.
     */
    public static class AlzheimersMachine {
        private boolean initialized = false;
        private String state = "CREATED";
        private final Map<String, Object> composites = new HashMap<>();
        private final Map<String, Object> configuration = new HashMap<>();
        private boolean configured = false;
        private PatientDataset loadedData;
        private Map<String, Map<String, Object>> progressionFactors;
        private Map<String, Object> results;
        
        public void initialize() {
            this.initialized = true;
            this.state = "READY";
            
            // Create composites
            composites.put("protein_expression", new HashMap<String, Object>());
            composites.put("neuronal_network", new HashMap<String, Object>());
            composites.put("time_series_analysis", new HashMap<String, Object>());
            composites.put("correlation_engine", new HashMap<String, Object>());
            composites.put("visualization", new HashMap<String, Object>());
        }
        
        public boolean isInitialized() {
            return initialized;
        }
        
        public boolean hasComposite(String compositeType) {
            return composites.containsKey(compositeType);
        }
        
        public String getState() {
            return state;
        }
        
        public void configure(Map<String, Object> parameters) {
            configuration.putAll(parameters);
            configured = true;
            state = "CONFIGURED";
            
            // Propagate configuration to composites
            @SuppressWarnings("unchecked")
            Map<String, Object> proteinComposite = (Map<String, Object>) composites.get("protein_expression");
            proteinComposite.put("sampling_frequency", parameters.get("protein_sampling_frequency"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> neuronalComposite = (Map<String, Object>) composites.get("neuronal_network");
            neuronalComposite.put("assessment_frequency", parameters.get("neuronal_assessment_frequency"));
            
            @SuppressWarnings("unchecked")
            Map<String, Object> timeSeriesComposite = (Map<String, Object>) composites.get("time_series_analysis");
            timeSeriesComposite.put("time_step_hours", parameters.get("time_step_hours"));
            timeSeriesComposite.put("duration_days", parameters.get("simulation_duration_days"));
        }
        
        public Map<String, Object> getConfiguration() {
            return configuration;
        }
        
        @SuppressWarnings("unchecked")
        public Map<String, Object> getCompositeConfiguration(String compositeType) {
            return (Map<String, Object>) composites.get(compositeType);
        }
        
        public boolean isConfigured() {
            return configured;
        }
        
        public void loadData(PatientDataset dataset) {
            this.loadedData = dataset;
            state = "DATA_LOADED";
        }
        
        public void configureProgressionFactors(Map<String, Map<String, Object>> factors) {
            this.progressionFactors = factors;
        }
        
        public void runSimulation() {
            if (!configured || loadedData == null) {
                throw new IllegalStateException("Machine must be configured and data loaded before running simulation");
            }
            
            // Create simulation results
            results = new HashMap<>();
            
            // Create disease model
            Map<String, Object> diseaseModel = new HashMap<>();
            
            // Add time points
            int days = (int) configuration.get("simulation_duration_days");
            List<Integer> timePoints = new java.util.ArrayList<>();
            for (int i = 0; i <= days; i++) {
                timePoints.add(i);
            }
            diseaseModel.put("time_points", timePoints);
            
            // Add protein profiles
            Map<String, Object> proteinProfiles = new HashMap<>();
            
            // Tau profile (increasing over time)
            List<Double> tauProfile = new java.util.ArrayList<>();
            Map<String, Object> tauFactor = progressionFactors.get("tau_accumulation");
            double tauStrength = (double) tauFactor.get("strength");
            String tauPattern = (String) tauFactor.get("pattern");
            
            for (int i = 0; i <= days; i++) {
                if ("exponential_increase".equals(tauPattern)) {
                    tauProfile.add(100.0 + 50.0 * tauStrength * Math.exp(0.005 * i));
                } else {
                    tauProfile.add(100.0 + i * tauStrength);
                }
            }
            proteinProfiles.put("tau", tauProfile);
            
            // Amyloid profile
            List<Double> amyloidProfile = new java.util.ArrayList<>();
            Map<String, Object> amyloidFactor = progressionFactors.get("amyloid_deposition");
            double amyloidStrength = (double) amyloidFactor.get("strength");
            String amyloidPattern = (String) amyloidFactor.get("pattern");
            
            for (int i = 0; i <= days; i++) {
                if ("sigmoidal".equals(amyloidPattern)) {
                    amyloidProfile.add(500.0 + 700.0 * amyloidStrength / (1.0 + Math.exp(-(i - days/2.0) / (days/10.0))));
                } else {
                    amyloidProfile.add(500.0 + i * amyloidStrength);
                }
            }
            proteinProfiles.put("amyloid_beta", amyloidProfile);
            
            diseaseModel.put("protein_profiles", proteinProfiles);
            
            // Add neuronal states
            Map<String, Object> neuronalStates = new HashMap<>();
            
            // Connectivity (decreasing over time)
            List<Double> connectivity = new java.util.ArrayList<>();
            Map<String, Object> neuronalFactor = progressionFactors.get("neuronal_loss");
            double neuronalStrength = (double) neuronalFactor.get("strength");
            
            for (int i = 0; i <= days; i++) {
                connectivity.add(1.0 - neuronalStrength * i / days);
            }
            neuronalStates.put("connectivity", connectivity);
            
            // Hub integrity
            List<Double> hubIntegrity = new java.util.ArrayList<>();
            for (int i = 0; i <= days; i++) {
                hubIntegrity.add(1.0 - 1.2 * neuronalStrength * i / days);
            }
            neuronalStates.put("hub_integrity", hubIntegrity);
            
            // Functional clusters
            List<Integer> functionalClusters = new java.util.ArrayList<>();
            int startClusters = 20;
            for (int i = 0; i <= days; i++) {
                functionalClusters.add(Math.max(1, (int)(startClusters * (1.0 - neuronalStrength * i / days))));
            }
            neuronalStates.put("functional_clusters", functionalClusters);
            
            diseaseModel.put("neuronal_states", neuronalStates);
            
            // Add correlations
            Map<String, Double> correlations = new HashMap<>();
            correlations.put("tau_vs_connectivity", -0.85);
            correlations.put("amyloid_vs_connectivity", -0.65);
            correlations.put("tau_vs_hub_integrity", -0.90);
            correlations.put("amyloid_vs_hub_integrity", -0.70);
            
            diseaseModel.put("correlations", correlations);
            
            results.put("disease_model", diseaseModel);
            state = "SIMULATION_COMPLETE";
        }
        
        public Map<String, Object> getResults() {
            return results;
        }
    }
    
    /**
     * Mock class representing a patient dataset.
     */
    public static class PatientDataset {
        private final String dataType;
        
        public PatientDataset(String dataType) {
            this.dataType = dataType;
        }
        
        public String getDataType() {
            return dataType;
        }
    }
}