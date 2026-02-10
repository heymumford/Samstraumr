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

package org.s8r.test.steps.alz001.mock;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating mock components in the ALZ001 test suite.
 * 
 * <p>This class provides factory methods for creating pre-configured mock components
 * for each capability. The factory ensures that components are created with consistent
 * default configurations and in the appropriate initial state.
 */
public class ALZ001MockFactory {
    
    /**
     * Private constructor to prevent instantiation.
     * This class should be used statically.
     */
    private ALZ001MockFactory() {
        // Prevent instantiation
    }
    
    /**
     * Creates a default configuration map with common settings.
     * 
     * @return A map with default configuration values
     */
    public static Map<String, Object> createDefaultConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("simulation_timestep_ms", 100);
        config.put("log_level", "INFO");
        config.put("max_memory_mb", 512);
        return config;
    }
    
    /**
     * Creates a configuration map for protein expression components.
     * 
     * @return A configuration map for protein expression
     */
    public static Map<String, Object> createProteinExpressionConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("default_tau_threshold", 120.0);
        config.put("default_amyloid_threshold", 800.0);
        config.put("protein_expression_interval_ms", 500);
        config.put("sensitivity", 0.85);
        config.put("specificity", 0.9);
        return config;
    }
    
    /**
     * Creates a configuration map for neuronal network components.
     * 
     * @return A configuration map for neuronal networks
     */
    public static Map<String, Object> createNeuronalNetworkConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("default_connectivity_threshold", 0.7);
        config.put("neuronal_update_interval_ms", 250);
        config.put("network_size", 1000);
        config.put("synapse_count", 10000);
        config.put("degradation_rate", 0.01);
        return config;
    }
    
    /**
     * Creates a configuration map for time series analysis components.
     * 
     * @return A configuration map for time series analysis
     */
    public static Map<String, Object> createTimeSeriesConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("window_size", 30);
        config.put("overlap_percent", 50);
        config.put("seasonality_periods", 12);
        config.put("decomposition_method", "STL");
        config.put("trend_detection_threshold", 0.05);
        return config;
    }
    
    /**
     * Creates a configuration map for time series analysis composites.
     * 
     * @return A configuration map for time series analysis composites
     */
    public static Map<String, Object> createTimeSeriesCompositeConfig() {
        Map<String, Object> config = createDefaultCompositeConfiguration();
        config.put("window_size", 30);
        config.put("decomposition_method", "STL");
        config.put("seasonality_periods", 12);
        config.put("initialize_default_dataset", true);
        config.put("forecast_horizon", 24);
        return config;
    }
    
    /**
     * Creates a configuration map for environmental factors components.
     * 
     * @return A configuration map for environmental factors
     */
    public static Map<String, Object> createEnvironmentalFactorsConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("default_oxidative_stress_baseline", 10.0);
        config.put("default_inflammatory_response_baseline", 5.0);
        config.put("environmental_update_interval_ms", 1000);
        config.put("stress_impact_factor", 1.2);
        config.put("diet_impact_factor", 0.8);
        return config;
    }
    
    /**
     * Creates a configuration map for environmental factors composites.
     * 
     * @return A configuration map for environmental factors composites
     */
    public static Map<String, Object> createEnvironmentalFactorsCompositeConfig() {
        Map<String, Object> config = createDefaultCompositeConfiguration();
        config.put("environmental_update_interval_ms", 1000);
        config.put("stress_impact_factor", 1.2);
        config.put("diet_impact_factor", 0.8);
        config.put("create_default_programs", true);
        config.put("enable_cohort_analysis", true);
        return config;
    }
    
    /**
     * Creates a configuration map for predictive modeling components.
     * 
     * @return A configuration map for predictive modeling
     */
    public static Map<String, Object> createPredictiveModelingConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("prediction_window_ms", 10000);
        config.put("default_confidence_threshold", 0.8);
        config.put("default_cognitive_reserve", 85.0);
        config.put("learning_rate", 0.01);
        config.put("max_iterations", 1000);
        return config;
    }
    
    /**
     * Creates a factory method for each mock component type.
     * These methods should be added as the mock component classes are implemented.
     */
    
    /**
     * Creates a protein expression component with the given name.
     *
     * @param name The component name
     * @return A configured protein expression component
     */
    public static org.s8r.test.steps.alz001.mock.component.ProteinExpressionComponent createProteinComponent(String name) {
        org.s8r.test.steps.alz001.mock.component.ProteinExpressionComponent component = 
            new org.s8r.test.steps.alz001.mock.component.ProteinExpressionComponent(name);
        component.configure(createProteinExpressionConfig());
        component.initialize();
        return component;
    }
    
    /**
     * Creates a neuronal network component with the given name.
     *
     * @param name The component name
     * @return A configured neuronal network component
     */
    public static org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent createNetworkComponent(String name) {
        org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent component = 
            new org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent(name);
        component.configure(createNeuronalNetworkConfig());
        component.initialize();
        return component;
    }
    
    /**
     * Creates a neuronal network component with a small-world topology.
     *
     * @param name The component name
     * @param neuronCount The number of neurons
     * @param meanDegree The mean degree (connections per neuron)
     * @param rewireProb The rewiring probability
     * @return A configured small-world network component
     */
    public static org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent createSmallWorldNetwork(
            String name, int neuronCount, int meanDegree, double rewireProb) {
        org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent component = 
            new org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent(name);
        component.configureSmallWorldTopology(neuronCount, meanDegree, rewireProb);
        return component;
    }
    
    /**
     * Creates a neuronal network component with a scale-free topology.
     *
     * @param name The component name
     * @param neuronCount The number of neurons
     * @param initialConnections The number of initial connections for each new node
     * @return A configured scale-free network component
     */
    public static org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent createScaleFreeNetwork(
            String name, int neuronCount, int initialConnections) {
        org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent component = 
            new org.s8r.test.steps.alz001.mock.component.NeuronalNetworkComponent(name);
        component.configureScaleFreeTopology(neuronCount, initialConnections);
        return component;
    }
    
    /**
     * Creates a time series analysis component with the given name.
     *
     * @param name The component name
     * @return A configured time series analysis component
     */
    public static org.s8r.test.steps.alz001.mock.component.TimeSeriesAnalysisComponent createTimeSeriesComponent(String name) {
        org.s8r.test.steps.alz001.mock.component.TimeSeriesAnalysisComponent component = 
            new org.s8r.test.steps.alz001.mock.component.TimeSeriesAnalysisComponent(name);
        component.configure(createTimeSeriesConfig());
        component.initialize();
        return component;
    }
    
    /**
     * Creates a time series component with simulated biomarker data.
     *
     * @param name The component name
     * @param biomarkerName The biomarker to simulate
     * @param stages The number of disease stages to simulate
     * @param samplesPerStage The number of samples per stage
     * @return A time series component with simulated biomarker data
     */
    public static org.s8r.test.steps.alz001.mock.component.TimeSeriesAnalysisComponent createBiomarkerTimeSeries(
            String name, String biomarkerName, int stages, int samplesPerStage) {
        org.s8r.test.steps.alz001.mock.component.TimeSeriesAnalysisComponent component = 
            new org.s8r.test.steps.alz001.mock.component.TimeSeriesAnalysisComponent(name);
        component.configure(createTimeSeriesConfig());
        component.initialize();
        component.simulateBiomarkerProgression(biomarkerName, stages, samplesPerStage, 1.0, 5.0);
        return component;
    }
    
    /**
     * Creates an environmental factors component with the given name.
     *
     * @param name The component name
     * @return A configured environmental factors component
     */
    public static org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent createEnvironmentalFactorsComponent(String name) {
        org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent component = 
            new org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent(name);
        component.configure(createEnvironmentalFactorsConfig());
        component.initialize();
        return component;
    }
    
    /**
     * Creates an environmental factors component with a patient profile.
     *
     * @param name The component name
     * @param patientId The patient ID
     * @return A configured environmental factors component with a patient profile
     */
    public static org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent createEnvironmentalFactorsWithPatient(
            String name, String patientId) {
        org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent component = 
            createEnvironmentalFactorsComponent(name);
        component.createProfile(patientId);
        return component;
    }
    
    /**
     * Creates a predictive modeling component with the given name.
     *
     * @param name The component name
     * @return A configured predictive modeling component
     */
    public static org.s8r.test.steps.alz001.mock.component.PredictiveModelingComponent createPredictiveModelingComponent(String name) {
        org.s8r.test.steps.alz001.mock.component.PredictiveModelingComponent component = 
            new org.s8r.test.steps.alz001.mock.component.PredictiveModelingComponent(name);
        component.configure(createPredictiveModelingConfig());
        component.initialize();
        return component;
    }
    
    /**
     * Creates a predictive modeling component with a neural network model.
     *
     * @param name The component name
     * @param modelName The model name
     * @return A configured predictive modeling component with a neural network model
     */
    public static org.s8r.test.steps.alz001.mock.component.PredictiveModelingComponent createPredictiveModelingWithModel(
            String name, String modelName) {
        org.s8r.test.steps.alz001.mock.component.PredictiveModelingComponent component = 
            createPredictiveModelingComponent(name);
        component.createModel(modelName, "neuralNetwork");
        return component;
    }
    
    /**
     * Creates a configuration map for predictive modeling components.
     *
     * @return A configuration map for predictive modeling
     */
    public static Map<String, Object> createPredictiveModelingConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("prediction_window_ms", 10000);
        config.put("default_confidence_threshold", 0.8);
        config.put("default_cognitive_reserve", 85.0);
        config.put("learning_rate", 0.01);
        config.put("max_iterations", 1000);
        return config;
    }
    
    /**
     * Creates a configuration map for predictive modeling composites.
     *
     * @return A configuration map for predictive modeling composites
     */
    public static Map<String, Object> createPredictiveModelingCompositeConfig() {
        Map<String, Object> config = createDefaultCompositeConfiguration();
        config.put("max_models", 10);
        config.put("max_ensembles", 5);
        config.put("max_cohorts", 20);
        config.put("max_data_sources", 15);
        config.put("enable_ensemble_methods", true);
        config.put("enable_hyperparameter_optimization", true);
        config.put("clinical_decision_support_enabled", true);
        return config;
    }
    
    /**
     * Creates a configuration map for system integration components.
     *
     * @return A configuration map for system integration
     */
    public static Map<String, Object> createSystemIntegrationConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("max_modules", 10);
        config.put("communications_timeout_ms", 5000);
        config.put("data_validation_enabled", true);
        config.put("secure_channels", true);
        config.put("logging_level", "DETAILED");
        return config;
    }
    
    /**
     * Creates a system integration component with the given name.
     *
     * @param name The component name
     * @return A configured system integration component
     */
    public static org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent createSystemIntegrationComponent(String name) {
        org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent component = 
            new org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent(name);
        component.configure(createSystemIntegrationConfig());
        component.initialize();
        return component;
    }
    
    /**
     * Creates a system integration component with pre-registered standard modules.
     *
     * @param name The component name
     * @return A configured system integration component with standard modules
     */
    public static org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent createSystemWithModules(String name) {
        org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent system = 
            createSystemIntegrationComponent(name);
        
        // Register basic modules
        system.registerModule("ProteinExpressionModule", "enabled", "1.0", 
                            createProteinComponent("ProteinExpressionComponent"));
        system.registerModule("NeuronalNetworkModule", "enabled", "1.0", 
                            createNetworkComponent("NeuronalNetworkComponent"));
        system.registerModule("TimeSeriesAnalysisModule", "enabled", "1.0", 
                            createTimeSeriesComponent("TimeSeriesAnalysisComponent"));
        system.registerModule("EnvironmentalFactorsModule", "enabled", "1.0", 
                            createEnvironmentalFactorsComponent("EnvironmentalFactorsComponent"));
        system.registerModule("PredictiveModelingModule", "enabled", "1.0", 
                            createPredictiveModelingComponent("PredictiveModelingComponent"));
        
        return system;
    }
    
    /**
     * Creates a system integration component with data flow paths established.
     *
     * @param name The component name
     * @return A configured system integration component with data flow paths
     */
    public static org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent createSystemWithDataFlow(String name) {
        org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent system = 
            createSystemWithModules(name);
        
        // Add validation rules
        system.addValidationRule("protein_levels", "range:0-100, units:ng/ml, max_missing:0.1");
        system.addValidationRule("network_metrics", "connectivity:0.0-1.0, centrality:0-10, sparsity:>0");
        system.addValidationRule("temporal_patterns", "min_length:10, max_gaps:3, periodicity:allowed");
        system.addValidationRule("factor_effects", "categorical:validated, temporal:continuous");
        
        // Add data flow paths
        system.addDataFlowPath("ProteinExpressionModule", "NeuronalNetworkModule", "protein_levels", "1h");
        system.addDataFlowPath("NeuronalNetworkModule", "TimeSeriesAnalysisModule", "network_metrics", "1h");
        system.addDataFlowPath("TimeSeriesAnalysisModule", "PredictiveModelingModule", "temporal_patterns", "1d");
        system.addDataFlowPath("EnvironmentalFactorsModule", "PredictiveModelingModule", "factor_effects", "1d");
        
        // Establish secure transfer
        system.establishSecureDataTransfer();
        
        return system;
    }
    
    /**
     * Creates a system integration component with datasets loaded.
     *
     * @param name The component name
     * @return A configured system integration component with datasets
     */
    public static org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent createSystemWithDatasets(String name) {
        org.s8r.test.steps.alz001.mock.component.SystemIntegrationComponent system = 
            createSystemWithDataFlow(name);
        
        // Load datasets
        system.loadDataset("ADNI", 500, 10, 50, "tabular");
        system.loadDataset("DIAN", 300, 8, 40, "tabular");
        system.loadDataset("UK Biobank", 1000, 5, 30, "tabular");
        
        // Set simulation parameters
        Map<String, Object> params = new HashMap<>();
        params.put("temporal_resolution", "1 month");
        params.put("simulation_duration", "5 years");
        params.put("random_seed", 12345);
        params.put("stochasticity_level", "medium");
        params.put("intervention_enabled", true);
        system.setSimulationParameters(params);
        
        return system;
    }
    
    /**
     * COMPOSITE FACTORY METHODS
     * 
     * The following methods create and configure mock composites
     * for each capability in the ALZ001 test suite.
     */
    
    /**
     * Creates a default configuration map for composites.
     * 
     * @return A map with default composite configuration values
     */
    public static Map<String, Object> createDefaultCompositeConfiguration() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("max_children", 10);
        config.put("connection_timeout_ms", 1000);
        config.put("max_connections", 20);
        return config;
    }
    
    /**
     * Creates a configuration map for protein expression composites.
     * 
     * @return A configuration map for protein expression composites
     */
    public static Map<String, Object> createProteinExpressionCompositeConfig() {
        Map<String, Object> config = createDefaultCompositeConfiguration();
        config.put("max_interaction_networks", 5);
        config.put("max_compartments", 8);
        config.put("max_transport_processes", 15);
        config.put("enable_cross_seeding", true);
        return config;
    }
    
    /**
     * Creates an empty protein expression composite.
     *
     * @param name The composite name
     * @return A configured protein expression composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite createProteinExpressionComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite composite = 
            new org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite(name);
        composite.configure(createProteinExpressionCompositeConfig());
        return composite;
    }
    
    /**
     * Creates a protein expression composite with default components.
     *
     * @param name The composite name
     * @return A configured protein expression composite with components
     */
    public static org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite createProteinExpressionCompositeWithComponents(String name) {
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite composite = 
            createProteinExpressionComposite(name);
        
        // Add protein expression components
        composite.addProteinComponent(createProteinComponent(name + "-Amyloid"));
        composite.addProteinComponent(createProteinComponent(name + "-Tau"));
        composite.addProteinComponent(createProteinComponent(name + "-Neurofilament"));
        
        // Create connections between components
        composite.connect(
            composite.getChild(name + "-Amyloid"),
            composite.getChild(name + "-Tau"),
            "DATA_FLOW"
        );
        
        composite.connect(
            composite.getChild(name + "-Tau"),
            composite.getChild(name + "-Neurofilament"),
            "DATA_FLOW"
        );
        
        // Initialize the composite
        composite.initialize();
        
        return composite;
    }
    
    /**
     * Creates a fully configured protein expression composite with interaction networks,
     * cellular compartments, and transport processes.
     *
     * @param name The composite name
     * @return A fully configured protein expression composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite createFullProteinExpressionComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite composite = 
            createProteinExpressionCompositeWithComponents(name);
        
        // Create interaction network
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite.ProteinInteractionNetwork network = 
            composite.createInteractionNetwork("AlzheimerProteins");
        network.addProteinType("amyloid");
        network.addProteinType("tau");
        network.addProteinType("phosphorylated_tau");
        network.addProteinType("neurofilament");
        
        network.setInteractionStrength("amyloid", "tau", 0.8);
        network.setInteractionStrength("tau", "phosphorylated_tau", 0.9);
        network.setInteractionStrength("phosphorylated_tau", "neurofilament", 0.5);
        network.setInteractionStrength("amyloid", "neurofilament", 0.3);
        
        // Create compartments
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite.CellularCompartment extracellular = 
            composite.createCompartment("extracellular");
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite.CellularCompartment cytoplasm = 
            composite.createCompartment("cytoplasm");
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite.CellularCompartment nucleus = 
            composite.createCompartment("nucleus");
        
        extracellular.setProteinLevel("amyloid", 100.0);
        extracellular.setProteinLevel("tau", 10.0);
        cytoplasm.setProteinLevel("tau", 50.0);
        cytoplasm.setProteinLevel("phosphorylated_tau", 20.0);
        nucleus.setProteinLevel("neurofilament", 30.0);
        
        // Create transport processes
        composite.createTransport("amyloid", "extracellular", "cytoplasm", 0.05);
        composite.createTransport("tau", "cytoplasm", "extracellular", 0.02);
        composite.createTransport("phosphorylated_tau", "cytoplasm", "nucleus", 0.01);
        
        return composite;
    }
    
    /**
     * Creates an empty time series analysis composite.
     *
     * @param name The composite name
     * @return A configured time series analysis composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite createTimeSeriesAnalysisComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite composite = 
            new org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite(name);
        composite.configure(createTimeSeriesCompositeConfig());
        return composite;
    }
    
    /**
     * Creates a time series analysis composite with default components.
     *
     * @param name The composite name
     * @return A configured time series analysis composite with components
     */
    public static org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite createTimeSeriesAnalysisCompositeWithComponents(String name) {
        org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite composite = 
            createTimeSeriesAnalysisComposite(name);
        
        // Add time series components
        composite.addTimeSeriesComponent(createTimeSeriesComponent(name + "-Biomarker"));
        composite.addTimeSeriesComponent(createTimeSeriesComponent(name + "-Clinical"));
        composite.addTimeSeriesComponent(createBiomarkerTimeSeries(name + "-AmyloidSeries", "amyloid_beta", 5, 20));
        
        // Create connections between components
        composite.connect(
            composite.getChild(name + "-Biomarker"),
            composite.getChild(name + "-Clinical"),
            "DATA_FLOW"
        );
        
        composite.connect(
            composite.getChild(name + "-AmyloidSeries"),
            composite.getChild(name + "-Clinical"),
            "DATA_FLOW"
        );
        
        // Initialize the composite
        composite.initialize();
        
        return composite;
    }
    
    /**
     * Creates a fully configured time series analysis composite with datasets,
     * correlation analyses, and detected patterns.
     *
     * @param name The composite name
     * @return A fully configured time series analysis composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite createFullTimeSeriesAnalysisComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite composite = 
            createTimeSeriesAnalysisCompositeWithComponents(name);
        
        // Create biomarker dataset
        org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite.TimeSeriesDataset biomarkerDataset = 
            composite.createDataset("alzheimer_biomarkers", "biomarker");
        
        // Add key biomarkers to dataset
        List<String> biomarkers = Arrays.asList("amyloid_beta", "tau", "phosphorylated_tau", "neurofilament", "cognitive_score");
        composite.simulateCorrelatedBiomarkers(biomarkers, 5, 20);
        
        for (String biomarker : biomarkers) {
            biomarkerDataset.addSeries(biomarker);
        }
        
        // Add metadata to dataset
        biomarkerDataset.setMetadata("source", "ADNI");
        biomarkerDataset.setMetadata("subject_count", 100);
        biomarkerDataset.setMetadata("time_unit", "months");
        
        // Perform correlation analysis
        composite.analyzeDatasetCorrelations("alzheimer_biomarkers", 10);
        
        // Detect patterns
        composite.detectPatterns("alzheimer_biomarkers");
        
        // Detect change points for key biomarkers
        composite.detectChangePoints("amyloid_beta", 10, 1.5);
        composite.detectChangePoints("tau", 10, 1.5);
        
        // Generate forecasts
        composite.forecast("amyloid_beta", 12, "exponential_smoothing");
        composite.forecast("tau", 12, "exponential_smoothing");
        composite.forecast("cognitive_score", 12, "exponential_smoothing");
        
        return composite;
    }
    
    /**
     * Creates an empty environmental factors composite.
     *
     * @param name The composite name
     * @return A configured environmental factors composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite createEnvironmentalFactorsComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite composite = 
            new org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite(name);
        composite.configure(createEnvironmentalFactorsCompositeConfig());
        return composite;
    }
    
    /**
     * Creates an environmental factors composite with default components.
     *
     * @param name The composite name
     * @return A configured environmental factors composite with components
     */
    public static org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite createEnvironmentalFactorsCompositeWithComponents(String name) {
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite composite = 
            createEnvironmentalFactorsComposite(name);
        
        // Add environmental factors components
        composite.addEnvironmentalComponent(createEnvironmentalFactorsComponent(name + "-Diet"));
        composite.addEnvironmentalComponent(createEnvironmentalFactorsComponent(name + "-Exercise"));
        composite.addEnvironmentalComponent(createEnvironmentalFactorsComponent(name + "-Stress"));
        
        // Create connections between components
        composite.connect(
            composite.getChild(name + "-Diet"),
            composite.getChild(name + "-Exercise"),
            "DATA_FLOW"
        );
        
        composite.connect(
            composite.getChild(name + "-Exercise"),
            composite.getChild(name + "-Stress"),
            "DATA_FLOW"
        );
        
        // Initialize the composite
        composite.initialize();
        
        return composite;
    }
    
    /**
     * Creates a fully configured environmental factors composite with cohorts,
     * intervention programs, and risk analyses.
     *
     * @param name The composite name
     * @return A fully configured environmental factors composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite createFullEnvironmentalFactorsComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite composite = 
            createEnvironmentalFactorsCompositeWithComponents(name);
        
        // Create default cohorts
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite.PatientCohort controlCohort = 
            composite.createRandomCohort("control_cohort", "Control group with no intervention", 30);
        
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite.PatientCohort interventionCohort = 
            composite.createRandomCohort("intervention_cohort", "Intervention group receiving treatment", 30);
        
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite.PatientCohort highRiskCohort = 
            composite.createRandomCohort("high_risk_cohort", "High-risk population", 20);
        
        // Modify high-risk cohort to have worse baseline factors
        for (String patientId : highRiskCohort.getPatients()) {
            org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent.EnvironmentalProfile profile = 
                composite.getPatientProfile(patientId);
            
            if (profile != null) {
                // Higher stress, lower activity and diet quality
                profile.setFactor("stress_level", 0.7 + 0.3 * Math.random());
                profile.setFactor("physical_activity", 0.1 + 0.2 * Math.random());
                profile.setFactor("diet_quality", 0.1 + 0.3 * Math.random());
                profile.setFactor("sleep_quality", 0.2 + 0.2 * Math.random());
            }
        }
        
        // Create intervention programs
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite.InterventionProgram dietProgram = 
            composite.createInterventionProgram("diet_program", "Mediterranean diet intervention", 0.7, 0.2, 180.0);
        dietProgram.setFactorTarget("diet_quality", 0.8);
        
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite.InterventionProgram exerciseProgram = 
            composite.createInterventionProgram("exercise_program", "Moderate exercise intervention", 0.6, 0.3, 180.0);
        exerciseProgram.setFactorTarget("physical_activity", 0.7);
        exerciseProgram.setFactorTarget("stress_level", 0.4);
        
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite.InterventionProgram comprehensiveProgram = 
            composite.createDefaultComprehensiveProgram();
        
        // Apply interventions to intervention cohort
        composite.applyInterventionToCohort("intervention_cohort", "comprehensive_program", 10.0);
        
        // Perform risk stratification
        composite.performRiskStratification("control_cohort");
        composite.performRiskStratification("intervention_cohort");
        composite.performRiskStratification("high_risk_cohort");
        
        // Simulate intervention impact
        composite.simulateInterventionImpact("intervention_cohort", "comprehensive_program", 50.0, 100, 10.0);
        
        // Create biomarker data for correlation analysis
        Map<String, Double> mockBiomarkerValues = new HashMap<>();
        for (String patientId : controlCohort.getPatients()) {
            // Generate mock biomarker values influenced by environmental factors
            org.s8r.test.steps.alz001.mock.component.EnvironmentalFactorsComponent.EnvironmentalProfile profile = 
                composite.getPatientProfile(patientId);
            
            if (profile != null) {
                double baseValue = 50.0;
                baseValue += (profile.getFactor("stress_level") - 0.5) * 20.0;
                baseValue -= (profile.getFactor("physical_activity") - 0.5) * 15.0;
                baseValue -= (profile.getFactor("diet_quality") - 0.5) * 10.0;
                
                // Add some random noise
                baseValue += (Math.random() - 0.5) * 10.0;
                
                mockBiomarkerValues.put(patientId, baseValue);
            }
        }
        
        // Perform correlation analysis
        composite.analyzeEnvironmentalCorrelations("control_cohort", "amyloid_beta", mockBiomarkerValues);
        
        // Compare intervention programs
        composite.compareInterventionPrograms("high_risk_cohort", 70.0, 100, 10.0);
        
        return composite;
    }
    
    /**
     * Creates an empty predictive modeling composite.
     *
     * @param name The composite name
     * @return A configured predictive modeling composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite createPredictiveModelingComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite composite = 
            new org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite(name);
        composite.configure(createPredictiveModelingCompositeConfig());
        return composite;
    }
    
    /**
     * Creates a predictive modeling composite with default components.
     *
     * @param name The composite name
     * @return A configured predictive modeling composite with components
     */
    public static org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite createPredictiveModelingCompositeWithComponents(String name) {
        org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite composite = 
            createPredictiveModelingComposite(name);
        
        // Add predictive modeling components
        composite.addPredictiveComponent(createPredictiveModelingComponent(name + "-NeuralNet"));
        composite.addPredictiveComponent(createPredictiveModelingComponent(name + "-Biomarker"));
        composite.addPredictiveComponent(createPredictiveModelingWithModel(name + "-Clinical", "cognitiveModel"));
        
        // Create connections between components
        composite.connect(
            composite.getChild(name + "-NeuralNet"),
            composite.getChild(name + "-Clinical"),
            "DATA_FLOW"
        );
        
        composite.connect(
            composite.getChild(name + "-Biomarker"),
            composite.getChild(name + "-Clinical"),
            "DATA_FLOW"
        );
        
        // Initialize the composite
        composite.initialize();
        
        return composite;
    }
    
    /**
     * Creates a fully configured predictive modeling composite with models,
     * ensembles, data sources, cohorts, and intervention plans.
     *
     * @param name The composite name
     * @return A fully configured predictive modeling composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite createFullPredictiveModelingComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite composite = 
            createPredictiveModelingCompositeWithComponents(name);
        
        // Create standard models
        composite.createStandardModels();
        
        // Create standard data sources
        composite.createStandardDataSources();
        
        // Create standard cohorts
        composite.createStandardCohorts();
        
        // Create standard intervention plans
        composite.createStandardInterventionPlans();
        
        // Train the models
        composite.trainModel("NeuralNetworkModel", "ClinicalData", 0.2);
        composite.trainModel("RandomForestModel", "BiomarkerData", 0.2);
        composite.trainModel("SVMModel", "ClinicalData", 0.2);
        
        // Train the ensemble
        composite.trainEnsemble("CombinedModel", "ClinicalData", 0.2);
        
        // Generate predictions for cohorts
        composite.predictOutcomes("CombinedModel", "ControlCohort", "biomarkerLevel");
        composite.predictOutcomes("CombinedModel", "InterventionCohort", "biomarkerLevel");
        
        // Evaluate intervention plans
        composite.evaluateInterventionPlans("HighRiskCohort", "CombinedModel");
        
        // Simulate intervention effects
        composite.simulateInterventionEffect("HighRiskCohort", "ComprehensivePlan", "cognitiveScore", 10);
        
        // Analyze feature importance
        composite.analyzeFeatureImportance("RandomForestModel");
        
        // Generate sample clinical reports
        String patientId = composite.getCohort("HighRiskCohort").getPatients().get(0);
        composite.generateClinicalReport(patientId, "CombinedModel");
        
        return composite;
    }
    
    /**
     * MACHINE FACTORY METHODS
     * 
     * The following methods create and configure mock machines
     * for the ALZ001 test suite.
     */
     
    /**
     * Creates a configuration map for system simulation machines.
     * 
     * @return A configuration map for system simulation machines
     */
    public static Map<String, Object> createSystemSimulationMachineConfig() {
        Map<String, Object> config = createDefaultConfiguration();
        config.put("max_composites", 5);
        config.put("temporal_resolution", "1 month");
        config.put("simulation_duration", "5 years");
        config.put("random_seed", 12345);
        config.put("stochasticity_level", "medium");
        config.put("intervention_enabled", true);
        config.put("thread_pool_size", 8);
        return config;
    }
    
    /**
     * Creates an empty system simulation machine.
     *
     * @param name The machine name
     * @return A configured system simulation machine
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createSystemSimulationMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            new org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine(name);
        machine.configure(createSystemSimulationMachineConfig());
        return machine;
    }
    
    /**
     * Creates a system simulation machine with all five composites set up.
     *
     * @param name The machine name
     * @return A system simulation machine with all five composites
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createSystemSimulationMachineWithComposites(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachine(name);
        
        // Create the five required composites
        org.s8r.test.steps.alz001.mock.composite.ProteinExpressionComposite proteinExpression = 
            createProteinExpressionComposite(name + "-ProteinExpression");
        
        org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite neuronalNetwork = 
            createNeuronalNetworkComposite(name + "-NeuronalNetwork");
        
        org.s8r.test.steps.alz001.mock.composite.TimeSeriesAnalysisComposite timeSeriesAnalysis = 
            createTimeSeriesAnalysisComposite(name + "-TimeSeriesAnalysis");
        
        org.s8r.test.steps.alz001.mock.composite.EnvironmentalFactorsComposite environmentalFactors = 
            createEnvironmentalFactorsComposite(name + "-EnvironmentalFactors");
        
        org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite predictiveModeling = 
            createPredictiveModelingComposite(name + "-PredictiveModeling");
        
        // Set up the composites in the machine
        machine.setupComposites(
            proteinExpression,
            neuronalNetwork,
            timeSeriesAnalysis,
            environmentalFactors,
            predictiveModeling
        );
        
        // Initialize the machine
        machine.initialize();
        
        return machine;
    }
    
    /**
     * Creates a system simulation machine with all five fully configured composites
     * and data flow paths established between them.
     *
     * @param name The machine name
     * @return A fully configured system simulation machine
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createFullSystemSimulationMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachineWithComposites(name);
        
        // Establish data flow paths between composites
        machine.establishDataFlowPaths();
        
        // Create validation rules
        machine.createValidationRuleFromSpec("protein_measurements", "range:0-100, units:ng/ml, max_missing:0.1");
        machine.createValidationRuleFromSpec("network_metrics", "range:0-10, precision:0.01, min_connectivity:0.2");
        machine.createValidationRuleFromSpec("time_series", "min_length:10, max_gaps:3, periodicity:allowed");
        machine.createValidationRuleFromSpec("environmental_data", "categorical:validated, temporal:continuous");
        machine.createValidationRuleFromSpec("clinical_outcomes", "scales:standardized, assessor:blinded");
        
        // Create datasets
        machine.createDataset("ADNI", "Alzheimer's Disease Neuroimaging Initiative");
        machine.loadPatientData("ADNI", 500, 10, 50, "tabular");
        
        machine.createDataset("DIAN", "Dominantly Inherited Alzheimer Network");
        machine.loadPatientData("DIAN", 300, 8, 40, "tabular");
        
        machine.createDataset("UK_Biobank", "UK Biobank Dataset");
        machine.loadPatientData("UK_Biobank", 1000, 5, 30, "tabular");
        
        // Configure simulation parameters
        Map<String, Object> simParams = new HashMap<>();
        simParams.put("temporal_resolution", "1 month");
        simParams.put("simulation_duration", "5 years");
        simParams.put("random_seed", 12345);
        simParams.put("stochasticity_level", "medium");
        simParams.put("intervention_enabled", true);
        machine.configureSimulation(simParams);
        
        return machine;
    }
    
    /**
     * Creates a system simulation machine focused on protein aggregation research.
     *
     * @param name The machine name
     * @return A fully configured system simulation machine specialized for protein aggregation
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createProteinAggregationMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachineWithComposites(name);
        
        // Establish data flow paths focused on protein-network interactions
        machine.createDataFlowByName(
            "protein_to_network",
            "ProteinExpression",
            "NeuronalNetwork",
            "PROTEIN_IMPACT",
            "protein_aggregation_data"
        );
        
        machine.createDataFlowByName(
            "network_to_timeseries",
            "NeuronalNetwork",
            "TimeSeriesAnalysis",
            "NETWORK_DEGRADATION",
            "network_activity_data"
        );
        
        machine.createDataFlowByName(
            "environment_to_protein",
            "EnvironmentalFactors",
            "ProteinExpression",
            "ENVIRONMENTAL_MODULATION",
            "environmental_influences"
        );
        
        // Configure specialized parameters for protein research
        Map<String, Object> proteinParams = new HashMap<>();
        proteinParams.put("aggregation_rate", 0.05);
        proteinParams.put("clearance_rate", 0.02);
        proteinParams.put("seeding_threshold", 10.0);
        proteinParams.put("spreading_model", "prion-like");
        proteinParams.put("detailed_molecular_simulation", true);
        machine.configureSimulation(proteinParams);
        
        // Create specialized datasets
        machine.createDataset("DIAN_CSF", "DIAN CSF Biomarker Data");
        machine.loadPatientData("DIAN_CSF", 300, 6, 10, "longitudinal");
        
        machine.createDataset("ADNI_PET", "ADNI Amyloid PET Imaging");
        machine.loadPatientData("ADNI_PET", 400, 4, 20, "imaging");
        
        return machine;
    }
    
    /**
     * Creates a system simulation machine focused on network connectivity analysis.
     *
     * @param name The machine name
     * @return A fully configured system simulation machine specialized for network connectivity
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createNetworkConnectivityMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachineWithComposites(name);
        
        // Establish data flow paths focused on neuronal networks
        machine.createDataFlowByName(
            "protein_to_network",
            "ProteinExpression",
            "NeuronalNetwork",
            "PROTEIN_IMPACT",
            "protein_aggregation_data"
        );
        
        machine.createDataFlowByName(
            "network_to_timeseries",
            "NeuronalNetwork",
            "TimeSeriesAnalysis",
            "NETWORK_DEGRADATION",
            "network_activity_data"
        );
        
        // Configure specialized parameters for network research
        Map<String, Object> networkParams = new HashMap<>();
        networkParams.put("network_type", "small-world");
        networkParams.put("rewiring_probability", 0.1);
        networkParams.put("mean_degree", 4);
        networkParams.put("functional_connectivity_threshold", 0.3);
        networkParams.put("structural_connectivity_threshold", 0.5);
        networkParams.put("simulate_hub_vulnerability", true);
        machine.configureSimulation(networkParams);
        
        // Create specialized datasets
        machine.createDataset("ADNI_fMRI", "ADNI Functional MRI Data");
        machine.loadPatientData("ADNI_fMRI", 200, 3, 100, "imaging");
        
        machine.createDataset("HCP_DTI", "Human Connectome Project DTI Data");
        machine.loadPatientData("HCP_DTI", 100, 1, 200, "imaging");
        
        return machine;
    }
    
    /**
     * Creates a system simulation machine focused on temporal progression analysis.
     *
     * @param name The machine name
     * @return A fully configured system simulation machine specialized for temporal analysis
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createTemporalProgressionMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachineWithComposites(name);
        
        // Establish data flow paths focused on temporal patterns
        machine.createDataFlowByName(
            "protein_to_timeseries",
            "ProteinExpression",
            "TimeSeriesAnalysis",
            "BIOMARKER_PROGRESSION",
            "biomarker_time_series"
        );
        
        machine.createDataFlowByName(
            "network_to_timeseries",
            "NeuronalNetwork",
            "TimeSeriesAnalysis",
            "NETWORK_DEGRADATION",
            "network_activity_data"
        );
        
        machine.createDataFlowByName(
            "timeseries_to_prediction",
            "TimeSeriesAnalysis",
            "PredictiveModeling",
            "TEMPORAL_PATTERNS",
            "temporal_progression_data"
        );
        
        // Configure specialized parameters for temporal analysis
        Map<String, Object> temporalParams = new HashMap<>();
        temporalParams.put("change_point_detection", true);
        temporalParams.put("periodicity_analysis", true);
        temporalParams.put("trend_extraction_method", "STL_decomposition");
        temporalParams.put("event_sequence_analysis", true);
        temporalParams.put("multi_scale_entropy", true);
        temporalParams.put("disease_stage_detection", true);
        machine.configureSimulation(temporalParams);
        
        // Create specialized datasets
        machine.createDataset("ADNI_Longitudinal", "ADNI Longitudinal Dataset");
        machine.loadPatientData("ADNI_Longitudinal", 800, 10, 50, "longitudinal");
        
        machine.createDataset("NACC", "National Alzheimer's Coordinating Center");
        machine.loadPatientData("NACC", 2000, 8, 40, "longitudinal");
        
        return machine;
    }
    
    /**
     * Creates a system simulation machine focused on environmental factors analysis.
     *
     * @param name The machine name
     * @return A fully configured system simulation machine specialized for environmental analysis
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createEnvironmentalFactorsMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachineWithComposites(name);
        
        // Establish data flow paths focused on environmental factors
        machine.createDataFlowByName(
            "environment_to_protein",
            "EnvironmentalFactors",
            "ProteinExpression",
            "ENVIRONMENTAL_MODULATION",
            "environmental_influences"
        );
        
        machine.createDataFlowByName(
            "environment_to_network",
            "EnvironmentalFactors",
            "NeuronalNetwork",
            "ENVIRONMENTAL_MODULATION",
            "environmental_stress_data"
        );
        
        machine.createDataFlowByName(
            "environment_to_prediction",
            "EnvironmentalFactors",
            "PredictiveModeling",
            "RISK_FACTOR_DATA",
            "environmental_context"
        );
        
        // Configure specialized parameters for environmental analysis
        Map<String, Object> environmentParams = new HashMap<>();
        environmentParams.put("stress_model", "allostatic_load");
        environmentParams.put("diet_model", "mediterranean_score");
        environmentParams.put("physical_activity_model", "met_minutes");
        environmentParams.put("sleep_model", "fragmentation_index");
        environmentParams.put("social_engagement_model", "network_size");
        environmentParams.put("pollution_model", "air_quality_index");
        machine.configureSimulation(environmentParams);
        
        // Create specialized datasets
        machine.createDataset("UK_Biobank_Lifestyle", "UK Biobank Lifestyle Data");
        machine.loadPatientData("UK_Biobank_Lifestyle", 5000, 3, 100, "tabular");
        
        machine.createDataset("FINGER", "Finnish Geriatric Intervention Study");
        machine.loadPatientData("FINGER", 1200, 4, 50, "longitudinal");
        
        return machine;
    }
    
    /**
     * Creates a system simulation machine focused on predictive modeling and personalization.
     *
     * @param name The machine name
     * @return A fully configured system simulation machine specialized for predictive modeling
     */
    public static org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine createPredictiveModelingMachine(String name) {
        org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine machine = 
            createSystemSimulationMachineWithComposites(name);
        
        // Establish data flow paths focused on prediction
        machine.createDataFlowByName(
            "protein_to_prediction",
            "ProteinExpression",
            "PredictiveModeling",
            "BIOMARKER_DATA",
            "protein_biomarker_data"
        );
        
        machine.createDataFlowByName(
            "network_to_prediction",
            "NeuronalNetwork",
            "PredictiveModeling",
            "CONNECTIVITY_DATA",
            "network_connectivity_data"
        );
        
        machine.createDataFlowByName(
            "timeseries_to_prediction",
            "TimeSeriesAnalysis",
            "PredictiveModeling",
            "TEMPORAL_PATTERNS",
            "temporal_progression_data"
        );
        
        machine.createDataFlowByName(
            "environment_to_prediction",
            "EnvironmentalFactors",
            "PredictiveModeling",
            "RISK_FACTOR_DATA",
            "environmental_context"
        );
        
        // Configure specialized parameters for predictive modeling
        Map<String, Object> predictionParams = new HashMap<>();
        predictionParams.put("model_type", "ensemble");
        predictionParams.put("cross_validation_folds", 5);
        predictionParams.put("hyperparameter_optimization", true);
        predictionParams.put("feature_selection", "recursive");
        predictionParams.put("calibration_method", "platt_scaling");
        predictionParams.put("personalization_level", "high");
        machine.configureSimulation(predictionParams);
        
        // Create specialized datasets
        machine.createDataset("ADNI_Complete", "ADNI Complete Dataset");
        machine.loadPatientData("ADNI_Complete", 1500, 10, 200, "multimodal");
        
        machine.createDataset("Synthetic_Cohort", "Synthetic Patient Cohort");
        machine.loadPatientData("Synthetic_Cohort", 10000, 20, 500, "synthetic");
        
        return machine;
    }
    
    /**
     * Creates a neuronal network composite configuration.
     * 
     * @return A configuration map for neuronal network composites
     */
    public static Map<String, Object> createNeuronalNetworkCompositeConfig() {
        Map<String, Object> config = createDefaultCompositeConfiguration();
        config.put("max_brain_regions", 10);
        config.put("max_functional_networks", 5);
        config.put("max_simulations", 20);
        config.put("default_signal_decay", 0.1);
        config.put("damage_model", "progressive");
        return config;
    }
    
    /**
     * Creates an empty neuronal network composite.
     *
     * @param name The composite name
     * @return A configured neuronal network composite
     */
    public static org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite createNeuronalNetworkComposite(String name) {
        org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite composite = 
            new org.s8r.test.steps.alz001.mock.composite.NeuronalNetworkComposite(name);
        composite.configure(createNeuronalNetworkCompositeConfig());
        return composite;
    }
}