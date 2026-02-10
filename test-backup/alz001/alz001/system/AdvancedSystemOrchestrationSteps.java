/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

package org.s8r.test.steps.alz001.system;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import java.util.*;
import java.util.concurrent.*;

import org.s8r.test.steps.alz001.ALZ001BaseSteps;
import org.s8r.test.steps.alz001.ALZ001TestContext;
import org.s8r.test.steps.alz001.mock.ALZ001MockFactory;
import org.s8r.test.steps.alz001.mock.machine.SystemSimulationMachine;
import org.s8r.test.steps.alz001.mock.system.ALZ001SystemOrchestrator;
import org.s8r.test.steps.alz001.machine.SystemSimulationMachineSteps;

/**
 * Step definitions for testing the L3_System layer of the ALZ001 test suite.
 * <p>
 * These steps implement scenarios for creating a multi-machine orchestration system,
 * configuring cross-machine data exchange, loading multi-modal datasets, executing
 * coordinated simulations, analyzing cross-scale interactions, and generating
 * clinical recommendations.
 * </p>
 */
public class AdvancedSystemOrchestrationSteps {

    private final ALZ001BaseSteps baseSteps;
    private final SystemSimulationMachineSteps machineSteps;
    private final ALZ001TestContext context;
    
    private ALZ001SystemOrchestrator systemOrchestrator;
    private Map<String, SystemSimulationMachine> specializedMachines;
    private Map<String, Object> simulationResults;
    private List<Map<String, Object>> clinicalRecommendations;
    private boolean orchestrationSuccessful;
    private String executionStrategy;
    private Set<String> failedMachines;
    
    /**
     * Constructor that initializes dependencies through dependency injection.
     *
     * @param baseSteps The base steps providing common functionality
     * @param machineSteps The machine steps for interacting with simulation machines
     */
    public AdvancedSystemOrchestrationSteps(ALZ001BaseSteps baseSteps, SystemSimulationMachineSteps machineSteps) {
        this.baseSteps = baseSteps;
        this.machineSteps = machineSteps;
        this.context = baseSteps.getContext();
        this.specializedMachines = new HashMap<>();
        this.failedMachines = new HashSet<>();
    }
    
    @Given("I create a multi-machine orchestration system named {string}")
    public void createMultiMachineOrchestrationSystem(String name) {
        baseSteps.logInfo("Creating multi-machine orchestration system: " + name);
        
        // Initialize the system orchestrator
        systemOrchestrator = new ALZ001SystemOrchestrator(name);
        
        // Store in context for other steps to access
        context.setProperty("system_orchestrator", systemOrchestrator);
        context.setProperty("orchestrator_name", name);
        
        baseSteps.logInfo("Created system orchestrator: " + systemOrchestrator.getName());
    }
    
    @When("I add the following specialized machines to the orchestration system:")
    public void addSpecializedMachinesToOrchestrationSystem(List<Map<String, String>> machineSpecs) {
        baseSteps.logInfo("Adding specialized machines to orchestration system");
        
        for (Map<String, String> machineSpec : machineSpecs) {
            String machineName = machineSpec.get("name");
            String machineType = machineSpec.get("type");
            
            baseSteps.logInfo("Adding machine: " + machineName + " of type: " + machineType);
            
            SystemSimulationMachine machine = null;
            
            // Create the appropriate specialized machine based on type
            switch (machineType.toLowerCase()) {
                case "protein aggregation":
                    machine = ALZ001MockFactory.createProteinAggregationMachine(machineName);
                    break;
                case "neuronal network":
                    machine = ALZ001MockFactory.createNetworkConnectivityMachine(machineName);
                    break;
                case "temporal progression":
                    machine = ALZ001MockFactory.createTemporalProgressionMachine(machineName);
                    break;
                case "environmental factors":
                    machine = ALZ001MockFactory.createEnvironmentalFactorsMachine(machineName);
                    break;
                case "predictive modeling":
                    machine = ALZ001MockFactory.createPredictiveModelingMachine(machineName);
                    break;
                default:
                    machine = ALZ001MockFactory.createSystemSimulationMachineWithComposites(machineName);
                    break;
            }
            
            specializedMachines.put(machineName, machine);
            systemOrchestrator.addMachine(machineName, machine);
        }
        
        // Store in context
        context.setProperty("specialized_machines", specializedMachines);
        
        baseSteps.logInfo("Added " + specializedMachines.size() + " specialized machines to the orchestration system");
    }
    
    @When("I configure cross-machine data exchange between {string} and {string} for {string} data")
    public void configureDataExchangeBetweenMachines(String sourceMachine, String targetMachine, String dataType) {
        baseSteps.logInfo("Configuring data exchange: " + sourceMachine + " -> " + targetMachine + " for " + dataType);
        
        // Establish data exchange protocol
        boolean success = systemOrchestrator.establishDataExchange(
            sourceMachine,
            targetMachine,
            dataType,
            "cross_machine_" + dataType.toLowerCase().replace(" ", "_")
        );
        
        if (success) {
            baseSteps.logInfo("Successfully established data exchange protocol");
        } else {
            baseSteps.logError("Failed to establish data exchange protocol");
        }
        
        // Store current exchange config
        Map<String, Object> exchangeConfig = context.getPropertyOrDefault("exchange_config", new HashMap<>());
        String exchangeKey = sourceMachine + "->" + targetMachine + ":" + dataType;
        ((Map<String, Object>) exchangeConfig).put(exchangeKey, success);
        context.setProperty("exchange_config", exchangeConfig);
    }
    
    @When("I load multi-modal datasets for the orchestration system")
    public void loadMultiModalDatasets() {
        baseSteps.logInfo("Loading multi-modal datasets for orchestration system");
        
        // Generate mock datasets for different data modalities
        Map<String, Object> proteinDataset = ALZ001MockFactory.createProteinExpressionDataset();
        Map<String, Object> neuronalDataset = ALZ001MockFactory.createNeuronalNetworkDataset();
        Map<String, Object> temporalDataset = ALZ001MockFactory.createTimeSeriesDataset();
        Map<String, Object> environmentalDataset = ALZ001MockFactory.createEnvironmentalFactorsDataset();
        
        // Create combined dataset map
        Map<String, Map<String, Object>> multiModalDatasets = new HashMap<>();
        multiModalDatasets.put("protein_expression", proteinDataset);
        multiModalDatasets.put("neuronal_network", neuronalDataset);
        multiModalDatasets.put("time_series", temporalDataset);
        multiModalDatasets.put("environmental_factors", environmentalDataset);
        
        // Load datasets into orchestrator
        systemOrchestrator.loadMultiModalDatasets(multiModalDatasets);
        
        // Store in context
        context.setProperty("multi_modal_datasets", multiModalDatasets);
        
        baseSteps.logInfo("Loaded " + multiModalDatasets.size() + " multi-modal datasets");
    }
    
    @When("I execute a coordinated simulation using {string} execution strategy")
    public void executeCoordinatedSimulation(String strategy) {
        baseSteps.logInfo("Executing coordinated simulation using strategy: " + strategy);
        
        this.executionStrategy = strategy;
        
        try {
            // Execute the coordinated simulation with the specified strategy
            simulationResults = systemOrchestrator.executeCoordinatedSimulation(strategy);
            orchestrationSuccessful = true;
            
            // Store results in context
            context.setProperty("simulation_results", simulationResults);
            context.setProperty("execution_strategy", strategy);
            
            baseSteps.logInfo("Coordinated simulation completed successfully");
            baseSteps.logInfo("Generated " + simulationResults.size() + " result categories");
        } catch (Exception e) {
            orchestrationSuccessful = false;
            baseSteps.logError("Coordinated simulation failed: " + e.getMessage());
            context.setProperty("simulation_error", e.getMessage());
        }
    }
    
    @When("I introduce failure in the {string} machine during orchestration")
    public void introduceFailureDuringOrchestration(String machineName) {
        baseSteps.logInfo("Introducing failure in machine: " + machineName);
        
        // Retrieve the machine
        SystemSimulationMachine machine = specializedMachines.get(machineName);
        
        if (machine != null) {
            // Simulate machine failure
            machine.simulateFailure();
            failedMachines.add(machineName);
            
            baseSteps.logInfo("Successfully introduced failure in machine: " + machineName);
        } else {
            baseSteps.logError("Could not find machine: " + machineName);
        }
        
        // Store in context
        context.setProperty("failed_machines", failedMachines);
    }
    
    @When("I analyze cross-scale interactions in the orchestration system")
    public void analyzeCrossScaleInteractions() {
        baseSteps.logInfo("Analyzing cross-scale interactions in orchestration system");
        
        if (simulationResults == null) {
            baseSteps.logError("Cannot analyze cross-scale interactions without simulation results");
            return;
        }
        
        // Perform cross-scale analysis
        Map<String, Object> crossScaleAnalysis = systemOrchestrator.performCrossScaleAnalysis();
        
        // Store in context
        context.setProperty("cross_scale_analysis", crossScaleAnalysis);
        
        baseSteps.logInfo("Completed cross-scale analysis with " + crossScaleAnalysis.size() + " interaction patterns");
    }
    
    @When("I generate clinical recommendations from orchestration results")
    public void generateClinicalRecommendations() {
        baseSteps.logInfo("Generating clinical recommendations from orchestration results");
        
        if (simulationResults == null) {
            baseSteps.logError("Cannot generate recommendations without simulation results");
            return;
        }
        
        // Generate clinical recommendations
        clinicalRecommendations = systemOrchestrator.generateClinicalRecommendations();
        
        // Store in context
        context.setProperty("clinical_recommendations", clinicalRecommendations);
        
        baseSteps.logInfo("Generated " + clinicalRecommendations.size() + " clinical recommendations");
    }
    
    @Then("the orchestration system should coordinate all machines successfully")
    public void verifyOrchestrationSuccess() {
        baseSteps.logInfo("Verifying orchestration success");
        
        if (orchestrationSuccessful) {
            baseSteps.logInfo("Orchestration completed successfully");
        } else {
            String errorMessage = (String) context.getPropertyOrDefault("simulation_error", "Unknown error");
            baseSteps.logError("Orchestration failed: " + errorMessage);
            throw new AssertionError("Orchestration was not successful: " + errorMessage);
        }
    }
    
    @Then("the orchestration system should recover from the machine failure")
    public void verifyRecoveryFromFailure() {
        baseSteps.logInfo("Verifying recovery from machine failure");
        
        boolean recoverySuccessful = systemOrchestrator.verifyRecoveryFromFailure(failedMachines);
        
        if (recoverySuccessful) {
            baseSteps.logInfo("Successfully recovered from machine failures");
        } else {
            baseSteps.logError("Failed to recover from machine failures");
            throw new AssertionError("Failed to recover from machine failures");
        }
    }
    
    @Then("the orchestration results should contain data from at least {int} machines")
    public void verifyResultsContainDataFromMultipleMachines(int minMachineCount) {
        baseSteps.logInfo("Verifying results contain data from at least " + minMachineCount + " machines");
        
        if (simulationResults == null) {
            throw new AssertionError("No simulation results available");
        }
        
        // Count machines that contributed data to results
        Set<String> contributingMachines = systemOrchestrator.getContributingMachines();
        
        baseSteps.logInfo("Found data from " + contributingMachines.size() + " contributing machines");
        
        if (contributingMachines.size() < minMachineCount) {
            throw new AssertionError(
                "Expected data from at least " + minMachineCount + " machines, but found " + 
                contributingMachines.size() + ": " + String.join(", ", contributingMachines)
            );
        }
    }
    
    @Then("the cross-scale analysis should identify at least {int} interaction patterns")
    public void verifyCrossScaleAnalysisPatterns(int minPatternCount) {
        baseSteps.logInfo("Verifying cross-scale analysis identified at least " + minPatternCount + " interaction patterns");
        
        Map<String, Object> crossScaleAnalysis = context.getPropertyOrDefault("cross_scale_analysis", Collections.emptyMap());
        
        int patternCount = crossScaleAnalysis.size();
        baseSteps.logInfo("Found " + patternCount + " interaction patterns");
        
        if (patternCount < minPatternCount) {
            throw new AssertionError(
                "Expected at least " + minPatternCount + " interaction patterns, but found " + patternCount
            );
        }
    }
    
    @Then("the clinical recommendations should include at least {int} actionable insights")
    public void verifyClinicalRecommendations(int minInsightCount) {
        baseSteps.logInfo("Verifying clinical recommendations include at least " + minInsightCount + " actionable insights");
        
        if (clinicalRecommendations == null) {
            throw new AssertionError("No clinical recommendations available");
        }
        
        int insightCount = 0;
        for (Map<String, Object> recommendation : clinicalRecommendations) {
            if (recommendation.containsKey("actionable") && (boolean) recommendation.get("actionable")) {
                insightCount++;
            }
        }
        
        baseSteps.logInfo("Found " + insightCount + " actionable insights in recommendations");
        
        if (insightCount < minInsightCount) {
            throw new AssertionError(
                "Expected at least " + minInsightCount + " actionable insights, but found " + insightCount
            );
        }
    }
    
    @Then("the system should log orchestration metrics with at least {int} performance indicators")
    public void verifyOrchestrationMetrics(int minMetricsCount) {
        baseSteps.logInfo("Verifying orchestration metrics with at least " + minMetricsCount + " performance indicators");
        
        Map<String, Object> orchestrationMetrics = systemOrchestrator.getOrchestrationMetrics();
        
        int metricsCount = orchestrationMetrics.size();
        baseSteps.logInfo("Found " + metricsCount + " performance indicators in orchestration metrics");
        
        if (metricsCount < minMetricsCount) {
            throw new AssertionError(
                "Expected at least " + minMetricsCount + " performance indicators, but found " + metricsCount
            );
        }
    }
    
    @And("the {string} execution strategy should complete within {int} seconds")
    public void verifyExecutionStrategyPerformance(String strategy, int maxSeconds) {
        if (!strategy.equals(executionStrategy)) {
            baseSteps.logInfo("Skipping performance check for strategy " + strategy + 
                " as the test used " + executionStrategy);
            return;
        }
        
        baseSteps.logInfo("Verifying " + strategy + " execution strategy completed within " + maxSeconds + " seconds");
        
        long executionTime = systemOrchestrator.getExecutionTimeSeconds();
        
        baseSteps.logInfo("Actual execution time: " + executionTime + " seconds");
        
        if (executionTime > maxSeconds) {
            throw new AssertionError(
                "Execution strategy " + strategy + " took " + executionTime + 
                " seconds, which exceeds the maximum of " + maxSeconds + " seconds"
            );
        }
    }
}