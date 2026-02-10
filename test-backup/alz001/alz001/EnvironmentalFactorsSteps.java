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
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Step definitions for environmental factors modeling in Alzheimer's disease research.
 * These steps implement the scenarios defined in alz001-environmental-factors.feature.
 */
public class EnvironmentalFactorsSteps {
    // Use composition instead of inheritance to avoid Cucumber issues
    private final ALZ001BaseSteps baseSteps = new ALZ001BaseSteps();
    private final Map<String, Object> testContext = new ConcurrentHashMap<>();
    
    // Mock component classes for environmental factors modeling
    private EnvironmentalFactorsComponent currentComponent;
    private EnvironmentalFactorsComposite currentComposite;
    private EnvironmentalInterventionMachine currentMachine;
    private List<EnvironmentalFactorsComponent> components = new ArrayList<>();
    private List<String> componentNames = new ArrayList<>();
    
    @Before
    public void setupTest() {
        baseSteps.setUp();
        testContext.clear();
        components.clear();
        componentNames.clear();
    }
    
    // Step Definitions
    
    @Given("an environmental factors modeling environment is initialized")
    public void anEnvironmentalFactorsModelingEnvironmentIsInitialized() {
        testContext.put("environmentInitialized", true);
        testContext.put("environmentModel", new EnvironmentalModel());
    }
    
    @Given("the simulation timestep is set to {int} {string}")
    public void theSimulationTimestepIsSetTo(Integer value, String unit) {
        EnvironmentalModel model = (EnvironmentalModel) testContext.get("environmentModel");
        model.setTimestep(value, unit);
        testContext.put("timestep", Map.of("value", value, "unit", unit));
    }
    
    @Given("I have a complete Alzheimer's disease model")
    public void iHaveACompleteAlzheimersModelingEnvironment() {
        // Create mock disease model with multiple components
        DiseaseModel model = new DiseaseModel();
        model.setState("INITIALIZED");
        model.addComponent("protein", new ProteinComponent());
        model.addComponent("network", new NetworkComponent());
        model.addComponent("timeSeries", new TimeSeriesComponent());
        
        testContext.put("diseaseModel", model);
    }
    
    @When("I create a new environmental factors component")
    public void iCreateANewEnvironmentalFactorsComponent() {
        currentComponent = new EnvironmentalFactorsComponent("default");
        components.add(currentComponent);
        testContext.put("currentComponent", currentComponent);
    }
    
    @When("I create an environmental factors component named {string}")
    public void iCreateAnEnvironmentalFactorsComponentNamed(String name) {
        currentComponent = new EnvironmentalFactorsComponent(name);
        components.add(currentComponent);
        componentNames.add(name);
        testContext.put("currentComponent", currentComponent);
        testContext.put(name + "Component", currentComponent);
    }
    
    @When("I configure the following environmental parameters:")
    public void iConfigureTheFollowingEnvironmentalParameters(DataTable dataTable) {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        Map<String, String> parameters = new HashMap<>();
        for (Map<String, String> row : rows) {
            String parameter = row.get("parameter");
            String value = row.get("value");
            parameters.put(parameter, value);
        }
        
        component.configureParameters(parameters);
        testContext.put("configuredParameters", parameters);
    }
    
    @When("I load the following environmental history:")
    public void iLoadTheFollowingEnvironmentalHistory(DataTable dataTable) {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<EnvironmentalDataPoint> dataPoints = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            LocalDate timestamp = LocalDate.parse(row.get("timestamp"), formatter);
            Map<String, String> factors = new HashMap<>();
            
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (!entry.getKey().equals("timestamp")) {
                    factors.put(entry.getKey(), entry.getValue());
                }
            }
            
            dataPoints.add(new EnvironmentalDataPoint(timestamp, factors));
        }
        
        component.loadHistory(dataPoints);
        testContext.put("loadedHistory", dataPoints);
    }
    
    @When("I create a biomarker component named {string}")
    public void iCreateABiomarkerComponentNamed(String name) {
        BiomarkerComponent component = new BiomarkerComponent(name);
        testContext.put(name + "Component", component);
    }
    
    @When("I load the following biomarker data:")
    public void iLoadTheFollowingBiomarkerData(DataTable dataTable) {
        BiomarkerComponent component = (BiomarkerComponent) testContext.get("amyloidComponent");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<BiomarkerDataPoint> dataPoints = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            LocalDate timestamp = LocalDate.parse(row.get("timestamp"), formatter);
            double value = Double.parseDouble(row.get("value"));
            dataPoints.add(new BiomarkerDataPoint(timestamp, value));
        }
        
        component.loadData(dataPoints);
        testContext.put("biomarkerData", dataPoints);
    }
    
    @When("I create an environmental factors composite from components {string}, {string}, and {string}")
    public void iCreateAnEnvironmentalFactorsCompositeFromComponents(String comp1, String comp2, String comp3) {
        EnvironmentalFactorsComponent component1 = (EnvironmentalFactorsComponent) testContext.get(comp1 + "Component");
        EnvironmentalFactorsComponent component2 = (EnvironmentalFactorsComponent) testContext.get(comp2 + "Component");
        EnvironmentalFactorsComponent component3 = (EnvironmentalFactorsComponent) testContext.get(comp3 + "Component");
        
        currentComposite = new EnvironmentalFactorsComposite("composite_" + comp1 + "_" + comp2 + "_" + comp3);
        currentComposite.addComponent(component1);
        currentComposite.addComponent(component2);
        currentComposite.addComponent(component3);
        
        testContext.put("currentComposite", currentComposite);
    }
    
    @When("I create an environment-biomarker composite from {string} and {string}")
    public void iCreateAnEnvironmentBiomarkerCompositeFrom(String envComponent, String biomarkerComponent) {
        EnvironmentalFactorsComponent env = (EnvironmentalFactorsComponent) testContext.get(envComponent + "Component");
        BiomarkerComponent biomarker = (BiomarkerComponent) testContext.get(biomarkerComponent + "Component");
        
        EnvironmentBiomarkerComposite composite = new EnvironmentBiomarkerComposite(
            "composite_" + envComponent + "_" + biomarkerComponent);
        composite.setEnvironmentalComponent(env);
        composite.setBiomarkerComponent(biomarker);
        
        testContext.put("envBiomarkerComposite", composite);
    }
    
    @When("I analyze correlation between environmental factors and biomarker levels")
    public void iAnalyzeCorrelationBetweenEnvironmentalFactorsAndBiomarkerLevels() {
        EnvironmentBiomarkerComposite composite = (EnvironmentBiomarkerComposite) testContext.get("envBiomarkerComposite");
        Map<String, Double> correlations = composite.analyzeCorrelations();
        testContext.put("factorCorrelations", correlations);
    }
    
    @When("I create an environmental intervention machine")
    public void iCreateAnEnvironmentalInterventionMachine() {
        currentMachine = new EnvironmentalInterventionMachine("interventionMachine");
        testContext.put("interventionMachine", currentMachine);
    }
    
    @When("I configure the following intervention plan:")
    public void iConfigureTheFollowingInterventionPlan(DataTable dataTable) {
        EnvironmentalInterventionMachine machine = (EnvironmentalInterventionMachine) testContext.get("interventionMachine");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<InterventionPlan> plans = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            LocalDate startDate = LocalDate.parse(row.get("startDate"), formatter);
            LocalDate endDate = LocalDate.parse(row.get("endDate"), formatter);
            
            Map<String, String> interventions = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (!entry.getKey().equals("startDate") && !entry.getKey().equals("endDate")) {
                    interventions.put(entry.getKey(), entry.getValue());
                }
            }
            
            plans.add(new InterventionPlan(startDate, endDate, interventions));
        }
        
        machine.configureInterventionPlan(plans);
        testContext.put("interventionPlans", plans);
    }
    
    @When("I try to configure an intervention with invalid parameters:")
    public void iTryToConfigureAnInterventionWithInvalidParameters(DataTable dataTable) {
        EnvironmentalInterventionMachine machine = (EnvironmentalInterventionMachine) testContext.get("interventionMachine");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<InterventionPlan> plans = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            LocalDate startDate = LocalDate.parse(row.get("startDate"), formatter);
            LocalDate endDate = LocalDate.parse(row.get("endDate"), formatter);
            
            Map<String, String> interventions = new HashMap<>();
            for (Map.Entry<String, String> entry : row.entrySet()) {
                if (!entry.getKey().equals("startDate") && !entry.getKey().equals("endDate")) {
                    interventions.put(entry.getKey(), entry.getValue());
                }
            }
            
            plans.add(new InterventionPlan(startDate, endDate, interventions));
        }
        
        Map<String, String> validationErrors = machine.validateInterventionPlan(plans);
        testContext.put("validationErrors", validationErrors);
    }
    
    @When("I set the baseline progression rate to {double} {string}")
    public void iSetTheBaselineProgressionRateTo(Double rate, String unit) {
        EnvironmentalInterventionMachine machine = (EnvironmentalInterventionMachine) testContext.get("interventionMachine");
        machine.setBaselineProgressionRate(rate, unit);
        testContext.put("baselineProgressionRate", Map.of("rate", rate, "unit", unit));
    }
    
    @When("I simulate intervention impact over {int} {string}")
    public void iSimulateInterventionImpactOver(Integer duration, String unit) {
        EnvironmentalInterventionMachine machine = (EnvironmentalInterventionMachine) testContext.get("interventionMachine");
        Map<String, Object> results = machine.simulateIntervention(duration, unit);
        testContext.put("simulationResults", results);
    }
    
    @When("I integrate environmental factors data into the model")
    public void iIntegrateEnvironmentalFactorsDataIntoTheModel() {
        DiseaseModel model = (DiseaseModel) testContext.get("diseaseModel");
        model.integrateEnvironmentalFactors();
        testContext.put("environmentIntegrated", true);
    }
    
    @When("I simulate disease progression with different environmental scenarios:")
    public void iSimulateDiseaseProgressionWithDifferentEnvironmentalScenarios(DataTable dataTable) {
        DiseaseModel model = (DiseaseModel) testContext.get("diseaseModel");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        Map<String, SimulationResult> scenarioResults = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String scenarioName = row.get("scenario");
            String diet = row.get("diet");
            String exercise = row.get("exercise");
            String stress = row.get("stress");
            String sleep = row.get("sleep");
            String medication = row.get("medication");
            String durationStr = row.get("duration");
            
            int duration = Integer.parseInt(durationStr.split(" ")[0]);
            String unit = durationStr.split(" ")[1];
            
            // Create scenario and run simulation
            Map<String, String> scenarioConfig = new HashMap<>();
            scenarioConfig.put("diet", diet);
            scenarioConfig.put("exercise", exercise);
            scenarioConfig.put("stress", stress);
            scenarioConfig.put("sleep", sleep);
            scenarioConfig.put("medication", medication);
            
            SimulationResult result = model.simulateScenario(scenarioName, scenarioConfig, duration, unit);
            scenarioResults.put(scenarioName, result);
        }
        
        testContext.put("scenarioResults", scenarioResults);
    }
    
    // Then steps
    
    @Then("the component should be successfully created")
    public void theComponentShouldBeSuccessfullyCreated() {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        Assertions.assertNotNull(component, "Component should be created");
        Assertions.assertEquals("INITIALIZED", component.getState(), "Component should be initialized");
    }
    
    @Then("the component should have default environmental parameters")
    public void theComponentShouldHaveDefaultEnvironmentalParameters() {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        Map<String, String> parameters = component.getParameters();
        
        Assertions.assertEquals("Mixed", parameters.get("diet"), "Default diet should be Mixed");
        Assertions.assertEquals("Moderate", parameters.get("exercise"), "Default exercise should be Moderate");
        Assertions.assertEquals("Medium", parameters.get("stress"), "Default stress should be Medium");
        Assertions.assertEquals("Normal", parameters.get("sleep"), "Default sleep should be Normal");
        Assertions.assertEquals("None", parameters.get("medication"), "Default medication should be None");
    }
    
    @Then("the component should be in an initialized state")
    public void theComponentShouldBeInAnInitializedState() {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        Assertions.assertEquals("INITIALIZED", component.getState(), "Component should be in initialized state");
    }
    
    @Then("the component should have the configured environmental parameters")
    public void theComponentShouldHaveTheConfiguredEnvironmentalParameters() {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        Map<String, String> configuredParams = (Map<String, String>) testContext.get("configuredParameters");
        Map<String, String> componentParams = component.getParameters();
        
        for (Map.Entry<String, String> entry : configuredParams.entrySet()) {
            Assertions.assertEquals(entry.getValue(), componentParams.get(entry.getKey()), 
                "Parameter " + entry.getKey() + " should match configured value");
        }
    }
    
    @Then("the component should be in a configured state")
    public void theComponentShouldBeInAConfiguredState() {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        Assertions.assertEquals("CONFIGURED", component.getState(), "Component should be in configured state");
    }
    
    @Then("the environmental history should be loaded with {int} data points")
    public void theEnvironmentalHistoryShouldBeLoadedWithDataPoints(Integer count) {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        List<EnvironmentalDataPoint> history = component.getHistory();
        
        Assertions.assertEquals(count, history.size(), "History should contain the expected number of data points");
    }
    
    @Then("the component should be in a data loaded state")
    public void theComponentShouldBeInADataLoadedState() {
        EnvironmentalFactorsComponent component = (EnvironmentalFactorsComponent) testContext.get("currentComponent");
        Assertions.assertEquals("DATA_LOADED", component.getState(), "Component should be in data loaded state");
    }
    
    @Then("the composite should contain {int} components")
    public void theCompositeShouldContainComponents(Integer count) {
        EnvironmentalFactorsComposite composite = (EnvironmentalFactorsComposite) testContext.get("currentComposite");
        Assertions.assertEquals(count, composite.getComponents().size(), 
            "Composite should contain the expected number of components");
    }
    
    @Then("the composite should be in an initialized state")
    public void theCompositeShouldBeInAnInitializedState() {
        EnvironmentalFactorsComposite composite = (EnvironmentalFactorsComposite) testContext.get("currentComposite");
        Assertions.assertEquals("INITIALIZED", composite.getState(), "Composite should be in initialized state");
    }
    
    @Then("the analysis should identify significant correlations")
    public void theAnalysisShouldIdentifySignificantCorrelations() {
        Map<String, Double> correlations = (Map<String, Double>) testContext.get("factorCorrelations");
        Assertions.assertFalse(correlations.isEmpty(), "Correlations should not be empty");
        
        // At least one correlation should be significant (abs > 0.3)
        boolean hasSignificantCorrelation = correlations.values().stream()
            .anyMatch(corr -> Math.abs(corr) > 0.3);
        
        Assertions.assertTrue(hasSignificantCorrelation, 
            "At least one correlation should be significant (abs > 0.3)");
    }
    
    @Then("the {word} factor should show {word} {word} correlation with amyloid levels")
    public void theFactorShouldShowCorrelationWithAmyloidLevels(String factor, String strength, String direction) {
        Map<String, Double> correlations = (Map<String, Double>) testContext.get("factorCorrelations");
        Double correlation = correlations.get(factor);
        
        Assertions.assertNotNull(correlation, "Correlation for " + factor + " should exist");
        
        // Check direction
        if (direction.equals("positive")) {
            Assertions.assertTrue(correlation > 0, factor + " should have positive correlation");
        } else if (direction.equals("negative")) {
            Assertions.assertTrue(correlation < 0, factor + " should have negative correlation");
        }
        
        // Check strength
        double absCorrelation = Math.abs(correlation);
        switch (strength) {
            case "strong":
                Assertions.assertTrue(absCorrelation > 0.5, 
                    factor + " should have strong correlation (> 0.5), but was: " + absCorrelation);
                break;
            case "moderate":
                Assertions.assertTrue(absCorrelation > 0.3 && absCorrelation <= 0.5, 
                    factor + " should have moderate correlation (0.3-0.5), but was: " + absCorrelation);
                break;
            case "weak":
                Assertions.assertTrue(absCorrelation <= 0.3, 
                    factor + " should have weak correlation (<= 0.3), but was: " + absCorrelation);
                break;
        }
    }
    
    @Then("the machine should reject the invalid configuration")
    public void theMachineShouldRejectTheInvalidConfiguration() {
        Map<String, String> errors = (Map<String, String>) testContext.get("validationErrors");
        Assertions.assertFalse(errors.isEmpty(), "Validation should produce errors for invalid configuration");
    }
    
    @Then("the machine should report specific validation errors for each parameter")
    public void theMachineShouldReportSpecificValidationErrorsForEachParameter() {
        Map<String, String> errors = (Map<String, String>) testContext.get("validationErrors");
        
        Assertions.assertTrue(errors.containsKey("dateRange"), "Should have error for invalid date range");
        Assertions.assertTrue(errors.containsKey("diet"), "Should have error for invalid diet");
        Assertions.assertTrue(errors.containsKey("exercise"), "Should have error for invalid exercise");
        Assertions.assertTrue(errors.containsKey("stress"), "Should have error for invalid stress");
        Assertions.assertTrue(errors.containsKey("sleep"), "Should have error for invalid sleep");
        Assertions.assertTrue(errors.containsKey("medication"), "Should have error for invalid medication");
    }
    
    @Then("the simulation should predict reduced progression rate")
    public void theSimulationShouldPredictReducedProgressionRate() {
        Map<String, Object> results = (Map<String, Object>) testContext.get("simulationResults");
        double baselineRate = ((Map<String, Double>) testContext.get("baselineProgressionRate")).get("rate");
        double predictedRate = (double) results.get("predictedProgressionRate");
        
        Assertions.assertTrue(predictedRate < baselineRate, 
            "Predicted progression rate (" + predictedRate + ") should be less than baseline (" + baselineRate + ")");
    }
    
    @Then("the predicted progression rate should be less than {double} {string}")
    public void thePredictedProgressionRateShouldBeLessThan(Double threshold, String unit) {
        Map<String, Object> results = (Map<String, Object>) testContext.get("simulationResults");
        double predictedRate = (double) results.get("predictedProgressionRate");
        
        Assertions.assertTrue(predictedRate < threshold, 
            "Predicted progression rate (" + predictedRate + ") should be less than " + threshold);
    }
    
    @Then("the simulation should generate impact metrics for each factor")
    public void theSimulationShouldGenerateImpactMetricsForEachFactor() {
        Map<String, Object> results = (Map<String, Object>) testContext.get("simulationResults");
        Map<String, Double> factorImpacts = (Map<String, Double>) results.get("factorImpacts");
        
        Assertions.assertNotNull(factorImpacts, "Factor impacts should not be null");
        Assertions.assertTrue(factorImpacts.containsKey("diet"), "Should have impact for diet");
        Assertions.assertTrue(factorImpacts.containsKey("exercise"), "Should have impact for exercise");
        Assertions.assertTrue(factorImpacts.containsKey("stress"), "Should have impact for stress");
        Assertions.assertTrue(factorImpacts.containsKey("sleep"), "Should have impact for sleep");
        Assertions.assertTrue(factorImpacts.containsKey("medication"), "Should have impact for medication");
    }
    
    @Then("each scenario should produce distinct progression trajectories")
    public void eachScenarioShouldProduceDistinctProgressionTrajectories() {
        Map<String, SimulationResult> results = (Map<String, SimulationResult>) testContext.get("scenarioResults");
        
        SimulationResult baseline = results.get("Baseline");
        SimulationResult conservative = results.get("Conservative");
        SimulationResult aggressive = results.get("Aggressive");
        
        Assertions.assertNotNull(baseline, "Baseline results should not be null");
        Assertions.assertNotNull(conservative, "Conservative results should not be null");
        Assertions.assertNotNull(aggressive, "Aggressive results should not be null");
        
        // Check that trajectories are different
        double baselineEndValue = baseline.getTrajectory().get(baseline.getTrajectory().size() - 1).getValue();
        double conservativeEndValue = conservative.getTrajectory().get(conservative.getTrajectory().size() - 1).getValue();
        double aggressiveEndValue = aggressive.getTrajectory().get(aggressive.getTrajectory().size() - 1).getValue();
        
        Assertions.assertNotEquals(baselineEndValue, conservativeEndValue, 
            "Baseline and Conservative should have different end values");
        Assertions.assertNotEquals(baselineEndValue, aggressiveEndValue, 
            "Baseline and Aggressive should have different end values");
        Assertions.assertNotEquals(conservativeEndValue, aggressiveEndValue, 
            "Conservative and Aggressive should have different end values");
    }
    
    @Then("the aggressive intervention scenario should show the slowest progression")
    public void theAggressiveInterventionScenarioShouldShowTheSlowestProgression() {
        Map<String, SimulationResult> results = (Map<String, SimulationResult>) testContext.get("scenarioResults");
        
        SimulationResult baseline = results.get("Baseline");
        SimulationResult conservative = results.get("Conservative");
        SimulationResult aggressive = results.get("Aggressive");
        
        double baselineEndValue = baseline.getTrajectory().get(baseline.getTrajectory().size() - 1).getValue();
        double conservativeEndValue = conservative.getTrajectory().get(conservative.getTrajectory().size() - 1).getValue();
        double aggressiveEndValue = aggressive.getTrajectory().get(aggressive.getTrajectory().size() - 1).getValue();
        
        Assertions.assertTrue(aggressiveEndValue < conservativeEndValue, 
            "Aggressive should have lower end value than Conservative");
        Assertions.assertTrue(aggressiveEndValue < baselineEndValue, 
            "Aggressive should have lower end value than Baseline");
    }
    
    @Then("the baseline scenario should show the fastest progression")
    public void theBaselineScenarioShouldShowTheFastestProgression() {
        Map<String, SimulationResult> results = (Map<String, SimulationResult>) testContext.get("scenarioResults");
        
        SimulationResult baseline = results.get("Baseline");
        SimulationResult conservative = results.get("Conservative");
        SimulationResult aggressive = results.get("Aggressive");
        
        double baselineEndValue = baseline.getTrajectory().get(baseline.getTrajectory().size() - 1).getValue();
        double conservativeEndValue = conservative.getTrajectory().get(conservative.getTrajectory().size() - 1).getValue();
        double aggressiveEndValue = aggressive.getTrajectory().get(aggressive.getTrajectory().size() - 1).getValue();
        
        Assertions.assertTrue(baselineEndValue > conservativeEndValue, 
            "Baseline should have higher end value than Conservative");
        Assertions.assertTrue(baselineEndValue > aggressiveEndValue, 
            "Baseline should have higher end value than Aggressive");
    }
    
    @Then("the system should calculate quality of life metrics for each scenario")
    public void theSystemShouldCalculateQualityOfLifeMetricsForEachScenario() {
        Map<String, SimulationResult> results = (Map<String, SimulationResult>) testContext.get("scenarioResults");
        
        for (Map.Entry<String, SimulationResult> entry : results.entrySet()) {
            SimulationResult result = entry.getValue();
            Assertions.assertNotNull(result.getQualityOfLifeMetrics(), 
                "Quality of life metrics should be calculated for " + entry.getKey());
            Assertions.assertTrue(result.getQualityOfLifeMetrics().containsKey("cognitiveFunction"), 
                "Should include cognitive function metric");
            Assertions.assertTrue(result.getQualityOfLifeMetrics().containsKey("dailyActivities"), 
                "Should include daily activities metric");
            Assertions.assertTrue(result.getQualityOfLifeMetrics().containsKey("overallWellbeing"), 
                "Should include overall wellbeing metric");
        }
    }
    
    // Mock classes
    
    /**
     * Represents an environmental factors component for monitoring and analyzing environmental influences.
     */
    private static class EnvironmentalFactorsComponent {
        private final String name;
        private String state = "INITIALIZED";
        private final Map<String, String> parameters = new HashMap<>();
        private final List<EnvironmentalDataPoint> history = new ArrayList<>();
        
        public EnvironmentalFactorsComponent(String name) {
            this.name = name;
            // Set default parameters
            parameters.put("diet", "Mixed");
            parameters.put("exercise", "Moderate");
            parameters.put("stress", "Medium");
            parameters.put("sleep", "Normal");
            parameters.put("medication", "None");
        }
        
        public String getName() {
            return name;
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public Map<String, String> getParameters() {
            return new HashMap<>(parameters);
        }
        
        public void configureParameters(Map<String, String> newParameters) {
            parameters.putAll(newParameters);
            setState("CONFIGURED");
        }
        
        public void loadHistory(List<EnvironmentalDataPoint> dataPoints) {
            history.clear();
            history.addAll(dataPoints);
            setState("DATA_LOADED");
        }
        
        public List<EnvironmentalDataPoint> getHistory() {
            return new ArrayList<>(history);
        }
    }
    
    /**
     * Represents a composite of environmental factors components.
     */
    private static class EnvironmentalFactorsComposite {
        private final String name;
        private final List<EnvironmentalFactorsComponent> components = new ArrayList<>();
        private String state = "INITIALIZED";
        
        public EnvironmentalFactorsComposite(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void addComponent(EnvironmentalFactorsComponent component) {
            components.add(component);
        }
        
        public List<EnvironmentalFactorsComponent> getComponents() {
            return new ArrayList<>(components);
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
    }
    
    /**
     * Represents a biomarker component for tracking biomarker levels.
     */
    private static class BiomarkerComponent {
        private final String name;
        private final List<BiomarkerDataPoint> data = new ArrayList<>();
        private String state = "INITIALIZED";
        
        public BiomarkerComponent(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void loadData(List<BiomarkerDataPoint> dataPoints) {
            data.clear();
            data.addAll(dataPoints);
            setState("DATA_LOADED");
        }
        
        public List<BiomarkerDataPoint> getData() {
            return new ArrayList<>(data);
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
    }
    
    /**
     * Represents a composite that integrates environmental factors with biomarker data.
     */
    private static class EnvironmentBiomarkerComposite {
        private final String name;
        private EnvironmentalFactorsComponent environmentalComponent;
        private BiomarkerComponent biomarkerComponent;
        private String state = "INITIALIZED";
        
        public EnvironmentBiomarkerComposite(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setEnvironmentalComponent(EnvironmentalFactorsComponent component) {
            this.environmentalComponent = component;
        }
        
        public void setBiomarkerComponent(BiomarkerComponent component) {
            this.biomarkerComponent = component;
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public Map<String, Double> analyzeCorrelations() {
            // Mock correlation analysis
            setState("ANALYZED");
            
            Map<String, Double> correlations = new HashMap<>();
            
            // Return mock correlations
            correlations.put("diet", -0.65);      // Strong negative
            correlations.put("exercise", -0.42);  // Moderate negative
            correlations.put("stress", 0.58);     // Strong positive
            correlations.put("sleep", -0.38);     // Moderate negative
            correlations.put("medication", -0.72); // Strong negative
            
            return correlations;
        }
    }
    
    /**
     * Represents a machine that simulates environmental interventions.
     */
    private static class EnvironmentalInterventionMachine {
        private final String name;
        private String state = "INITIALIZED";
        private List<InterventionPlan> interventionPlans = new ArrayList<>();
        private Map<String, Object> progressionRate = new HashMap<>();
        
        public EnvironmentalInterventionMachine(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public void configureInterventionPlan(List<InterventionPlan> plans) {
            this.interventionPlans = new ArrayList<>(plans);
            setState("CONFIGURED");
        }
        
        public Map<String, String> validateInterventionPlan(List<InterventionPlan> plans) {
            Map<String, String> errors = new HashMap<>();
            
            // Validate each plan
            for (InterventionPlan plan : plans) {
                // Check date range
                if (plan.getStartDate().isAfter(plan.getEndDate())) {
                    errors.put("dateRange", "Start date must be before end date");
                }
                
                // Check diet
                if (!Arrays.asList("Mediterranean", "Western", "Mixed", "DASH", "Low-carb").contains(plan.getInterventions().get("diet"))) {
                    errors.put("diet", "Invalid diet type: " + plan.getInterventions().get("diet"));
                }
                
                // Check exercise
                if (!Arrays.asList("High", "Moderate", "Low", "None").contains(plan.getInterventions().get("exercise"))) {
                    errors.put("exercise", "Invalid exercise level: " + plan.getInterventions().get("exercise"));
                }
                
                // Check stress
                if (!Arrays.asList("High", "Medium", "Low").contains(plan.getInterventions().get("stress"))) {
                    errors.put("stress", "Invalid stress level: " + plan.getInterventions().get("stress"));
                }
                
                // Check sleep
                if (!Arrays.asList("Good", "Normal", "Poor").contains(plan.getInterventions().get("sleep"))) {
                    errors.put("sleep", "Invalid sleep quality: " + plan.getInterventions().get("sleep"));
                }
                
                // Check medication
                if (!Arrays.asList("None", "Cholinesterase", "Memantine", "Combined").contains(plan.getInterventions().get("medication"))) {
                    errors.put("medication", "Invalid medication: " + plan.getInterventions().get("medication"));
                }
            }
            
            setState("VALIDATION_FAILED");
            return errors;
        }
        
        public void setBaselineProgressionRate(double rate, String unit) {
            progressionRate.put("rate", rate);
            progressionRate.put("unit", unit);
        }
        
        public Map<String, Object> simulateIntervention(int duration, String unit) {
            // Simulate intervention impact
            setState("SIMULATED");
            
            Map<String, Object> results = new HashMap<>();
            
            // Calculate mock impact of intervention
            double baselineRate = (double) progressionRate.get("rate");
            double dietImpact = getDietImpact(interventionPlans.get(0).getInterventions().get("diet"));
            double exerciseImpact = getExerciseImpact(interventionPlans.get(0).getInterventions().get("exercise"));
            double stressImpact = getStressImpact(interventionPlans.get(0).getInterventions().get("stress"));
            double sleepImpact = getSleepImpact(interventionPlans.get(0).getInterventions().get("sleep"));
            double medicationImpact = getMedicationImpact(interventionPlans.get(0).getInterventions().get("medication"));
            
            double totalImpact = dietImpact + exerciseImpact + stressImpact + sleepImpact + medicationImpact;
            double predictedRate = baselineRate * (1 - totalImpact);
            
            // Store factor impacts
            Map<String, Double> factorImpacts = new HashMap<>();
            factorImpacts.put("diet", dietImpact);
            factorImpacts.put("exercise", exerciseImpact);
            factorImpacts.put("stress", stressImpact);
            factorImpacts.put("sleep", sleepImpact);
            factorImpacts.put("medication", medicationImpact);
            
            results.put("predictedProgressionRate", predictedRate);
            results.put("factorImpacts", factorImpacts);
            results.put("durationSimulated", duration);
            results.put("durationUnit", unit);
            
            return results;
        }
        
        // Helper methods for calculating impacts
        
        private double getDietImpact(String diet) {
            switch (diet) {
                case "Mediterranean": return 0.20;
                case "DASH": return 0.15;
                case "Low-carb": return 0.10;
                case "Mixed": return 0.05;
                case "Western": return 0.0;
                default: return 0.0;
            }
        }
        
        private double getExerciseImpact(String exercise) {
            switch (exercise) {
                case "High": return 0.15;
                case "Moderate": return 0.10;
                case "Low": return 0.05;
                case "None": return 0.0;
                default: return 0.0;
            }
        }
        
        private double getStressImpact(String stress) {
            switch (stress) {
                case "Low": return 0.10;
                case "Medium": return 0.05;
                case "High": return 0.0;
                default: return 0.0;
            }
        }
        
        private double getSleepImpact(String sleep) {
            switch (sleep) {
                case "Good": return 0.10;
                case "Normal": return 0.05;
                case "Poor": return 0.0;
                default: return 0.0;
            }
        }
        
        private double getMedicationImpact(String medication) {
            switch (medication) {
                case "Combined": return 0.25;
                case "Memantine": return 0.15;
                case "Cholinesterase": return 0.20;
                case "None": return 0.0;
                default: return 0.0;
            }
        }
    }
    
    /**
     * Represents the environmental model for simulations.
     */
    private static class EnvironmentalModel {
        private Map<String, Object> timestep = new HashMap<>();
        
        public void setTimestep(int value, String unit) {
            timestep.put("value", value);
            timestep.put("unit", unit);
        }
        
        public Map<String, Object> getTimestep() {
            return new HashMap<>(timestep);
        }
    }
    
    /**
     * Helper classes for mocking disease model components
     */
    private static class ProteinComponent {
        // Mock implementation for protein expression component
    }
    
    private static class NetworkComponent {
        // Mock implementation for neuronal network component
    }
    
    private static class TimeSeriesComponent {
        // Mock implementation for time series component
    }
    
    /**
     * Represents a complete disease model for end-to-end simulation.
     */
    private static class DiseaseModel {
        private String state;
        private final Map<String, Object> components = new HashMap<>();
        
        public void setState(String state) {
            this.state = state;
        }
        
        public String getState() {
            return state;
        }
        
        public void addComponent(String name, Object component) {
            components.put(name, component);
        }
        
        public void integrateEnvironmentalFactors() {
            // Mock integration of environmental factors
        }
        
        public SimulationResult simulateScenario(String name, Map<String, String> config, int duration, String unit) {
            // Generate mock trajectory
            List<TrajectoryPoint> trajectory = new ArrayList<>();
            double startValue = 30.0; // Baseline cognitive score
            
            // Generate trajectory based on scenario type
            double slope;
            switch (name) {
                case "Baseline":
                    slope = -0.05; // Fast decline
                    break;
                case "Conservative":
                    slope = -0.03; // Medium decline
                    break;
                case "Aggressive":
                    slope = -0.01; // Slow decline
                    break;
                default:
                    slope = -0.04;
            }
            
            // Generate points
            for (int i = 0; i <= duration; i += 30) { // Monthly data points
                double timePoint = i;
                double value = startValue + (slope * timePoint) + ((Math.random() - 0.5) * 0.5);
                trajectory.add(new TrajectoryPoint(timePoint, value));
            }
            
            // Generate quality of life metrics
            Map<String, Double> qualityMetrics = new HashMap<>();
            double endValue = trajectory.get(trajectory.size() - 1).getValue();
            
            // Scale from 0-10 based on end cognitive score
            qualityMetrics.put("cognitiveFunction", scaleToDomainMetric(endValue, 0, 30, 0, 10));
            qualityMetrics.put("dailyActivities", scaleToDomainMetric(endValue, 0, 30, 0, 10));
            qualityMetrics.put("overallWellbeing", scaleToDomainMetric(endValue, 0, 30, 0, 10));
            
            return new SimulationResult(name, trajectory, qualityMetrics);
        }
        
        // Helper to scale values to domain metric
        private double scaleToDomainMetric(double value, double minValue, double maxValue, double minMetric, double maxMetric) {
            return ((value - minValue) / (maxValue - minValue)) * (maxMetric - minMetric) + minMetric;
        }
    }
    
    /**
     * Represents a data point in environmental history.
     */
    private static class EnvironmentalDataPoint {
        private final LocalDate timestamp;
        private final Map<String, String> factors;
        
        public EnvironmentalDataPoint(LocalDate timestamp, Map<String, String> factors) {
            this.timestamp = timestamp;
            this.factors = factors;
        }
        
        public LocalDate getTimestamp() {
            return timestamp;
        }
        
        public Map<String, String> getFactors() {
            return new HashMap<>(factors);
        }
    }
    
    /**
     * Represents a data point in biomarker data.
     */
    private static class BiomarkerDataPoint {
        private final LocalDate timestamp;
        private final double value;
        
        public BiomarkerDataPoint(LocalDate timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }
        
        public LocalDate getTimestamp() {
            return timestamp;
        }
        
        public double getValue() {
            return value;
        }
    }
    
    /**
     * Represents an intervention plan for environmental factors.
     */
    private static class InterventionPlan {
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final Map<String, String> interventions;
        
        public InterventionPlan(LocalDate startDate, LocalDate endDate, Map<String, String> interventions) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.interventions = new HashMap<>(interventions);
        }
        
        public LocalDate getStartDate() {
            return startDate;
        }
        
        public LocalDate getEndDate() {
            return endDate;
        }
        
        public Map<String, String> getInterventions() {
            return new HashMap<>(interventions);
        }
    }
    
    /**
     * Represents a point in a disease progression trajectory.
     */
    private static class TrajectoryPoint {
        private final double time;
        private final double value;
        
        public TrajectoryPoint(double time, double value) {
            this.time = time;
            this.value = value;
        }
        
        public double getTime() {
            return time;
        }
        
        public double getValue() {
            return value;
        }
    }
    
    /**
     * Represents the result of a disease progression simulation.
     */
    private static class SimulationResult {
        private final String scenarioName;
        private final List<TrajectoryPoint> trajectory;
        private final Map<String, Double> qualityOfLifeMetrics;
        
        public SimulationResult(String scenarioName, List<TrajectoryPoint> trajectory, Map<String, Double> qualityOfLifeMetrics) {
            this.scenarioName = scenarioName;
            this.trajectory = new ArrayList<>(trajectory);
            this.qualityOfLifeMetrics = new HashMap<>(qualityOfLifeMetrics);
        }
        
        public String getScenarioName() {
            return scenarioName;
        }
        
        public List<TrajectoryPoint> getTrajectory() {
            return new ArrayList<>(trajectory);
        }
        
        public Map<String, Double> getQualityOfLifeMetrics() {
            return new HashMap<>(qualityOfLifeMetrics);
        }
    }
}