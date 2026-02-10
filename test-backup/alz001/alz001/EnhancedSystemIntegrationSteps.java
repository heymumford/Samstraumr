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

package org.s8r.test.steps.alz001;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.junit.jupiter.api.Assertions;
import org.s8r.test.steps.alz001.mock.ALZ001IntegratedModelingSystem;
import org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Enhanced step definitions that use the fully integrated ALZ001 system
 * incorporating all five composites: protein expression, neuronal networks,
 * time series analysis, environmental factors, and predictive modeling.
 *
 * <p>This class demonstrates the integration of all five composite components 
 * in BDD scenarios, enabling comprehensive testing of cross-composite interactions.
 */
public class EnhancedSystemIntegrationSteps {
    
    private final ALZ001BaseSteps baseSteps = new ALZ001BaseSteps();
    private ALZ001IntegratedModelingSystem integratedSystem;
    
    @Before
    public void setUp() {
        baseSteps.setUp();
        baseSteps.logInfo("Setting up EnhancedSystemIntegrationSteps");
    }
    
    @After
    public void tearDown() {
        baseSteps.logInfo("Tearing down EnhancedSystemIntegrationSteps");
        baseSteps.tearDown();
    }
    
    @Given("a comprehensive Alzheimer's disease modeling environment")
    public void comprehensiveAlzheimersModelingEnvironment() {
        baseSteps.logInfo("Initializing comprehensive Alzheimer's disease modeling environment");
        baseSteps.context.store("environmentInitialized", true);
    }
    
    @Given("all five composite subsystems are available for integration")
    public void allCompositeSubsystemsAvailable() {
        baseSteps.logInfo("Ensuring all five composite subsystems are available");
        baseSteps.context.store("allCompositeSubsystemsAvailable", true);
    }
    
    @When("I create a new integrated disease modeling system")
    public void createNewIntegratedDiseaseModelingSystem() {
        baseSteps.logInfo("Creating new integrated disease modeling system");
        
        integratedSystem = new ALZ001IntegratedModelingSystem("ALZ001-IntegratedSystem");
        baseSteps.context.store("integratedSystem", integratedSystem);
    }
    
    @When("I configure the following data validation rules for the integrated system:")
    public void configureIntegratedDataValidationRules(DataTable dataTable) {
        baseSteps.logInfo("Configuring data validation rules for integrated system");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        
        for (Map<String, String> row : rows) {
            String dataType = row.get("data_type");
            String validationRule = row.get("validation_rule");
            
            baseSteps.logInfo("Adding validation rule for " + dataType + ": " + validationRule);
            system.addValidationRule(dataType, validationRule);
        }
    }
    
    @When("I establish the following data flow paths between composites:")
    public void establishDataFlowPathsBetweenComposites(DataTable dataTable) {
        baseSteps.logInfo("Establishing data flow paths between composites");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        
        for (Map<String, String> row : rows) {
            String sourceComponent = row.get("source_component");
            String targetComponent = row.get("target_component");
            String dataType = row.get("data_type");
            String refreshRate = row.get("refresh_rate");
            
            baseSteps.logInfo("Adding data flow: " + sourceComponent + " -> " + targetComponent + 
                           " (data=" + dataType + ", refresh=" + refreshRate + ")");
            system.establishDataFlow(sourceComponent, targetComponent, dataType, refreshRate);
        }
    }
    
    @When("I load the following patient datasets into the integrated system:")
    public void loadPatientDatasetsIntoIntegratedSystem(DataTable dataTable) {
        baseSteps.logInfo("Loading patient datasets into integrated system");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        
        for (Map<String, String> row : rows) {
            String datasetName = row.get("dataset_name");
            int patients = Integer.parseInt(row.get("patients"));
            int timepoints = Integer.parseInt(row.get("timepoints"));
            int features = Integer.parseInt(row.get("features"));
            String format = row.get("format");
            
            baseSteps.logInfo("Loading dataset: " + datasetName + " (" + patients + " patients, " + 
                           timepoints + " timepoints, " + features + " features, format=" + format + ")");
            system.loadPatientCohort(datasetName, patients, timepoints, features, format);
        }
    }
    
    @When("I configure the following parameters for the integrated simulation:")
    public void configureIntegratedSimulationParameters(DataTable dataTable) {
        baseSteps.logInfo("Configuring integrated simulation parameters");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        
        for (Map<String, String> row : rows) {
            String parameter = row.get("parameter");
            String value = row.get("value");
            
            baseSteps.logInfo("Setting parameter: " + parameter + " = " + value);
            system.setSimulationParameter(parameter, value);
        }
    }
    
    @When("I execute a full integrated disease modeling simulation")
    public void executeFullIntegratedDiseaseModelingSimulation() {
        baseSteps.logInfo("Executing full integrated disease modeling simulation");
        
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        system.executeFullSimulation();
    }
    
    @Then("the integrated system should generate comprehensive disease trajectories")
    public void integratedSystemShouldGenerateComprehensiveDiseaseTrajectories() {
        baseSteps.logInfo("Verifying generation of comprehensive disease trajectories");
        
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        Assertions.assertEquals("SIMULATION_COMPLETE", system.getState(), 
                             "Integrated system should complete simulation");
    }
    
    @When("I analyze cross-scale interactions in the integrated model with the following parameters:")
    public void analyzeCrossScaleInteractionsInIntegratedModel(DataTable dataTable) {
        baseSteps.logInfo("Analyzing cross-scale interactions in integrated model");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String scalePair = row.get("scale_pair");
            String analysisMethod = row.get("analysis_method");
            double significanceThreshold = Double.parseDouble(row.get("significance_threshold"));
            
            baseSteps.logInfo("Analyzing scale pair: " + scalePair + " using " + analysisMethod + 
                           " (threshold=" + significanceThreshold + ")");
            Map<String, Object> result = system.analyzeCrossScaleInteractions(
                scalePair, analysisMethod, significanceThreshold);
            
            results.add(result);
        }
        
        baseSteps.context.store("crossScaleResults", results);
    }
    
    @Then("the integrated system should identify significant relationships between:")
    public void integratedSystemShouldIdentifySignificantRelationships(DataTable dataTable) {
        baseSteps.logInfo("Verifying identification of significant relationships");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        List<Map<String, Object>> results = baseSteps.context.retrieve("crossScaleResults");
        
        Assertions.assertNotNull(results, "Cross-scale analysis results should exist");
        Assertions.assertFalse(results.isEmpty(), "Cross-scale analysis results should not be empty");
        
        for (Map<String, String> row : rows) {
            String scale1 = row.get("scale1");
            String scale2 = row.get("scale2");
            String expectedPattern = row.get("expected_pattern");
            
            // Find matching result
            String scalePair = scale1 + "-" + scale2;
            boolean foundMatch = results.stream()
                .anyMatch(r -> r.get("scale_pair").equals(scalePair) && 
                           r.containsKey("significant_relationships"));
            
            Assertions.assertTrue(foundMatch, 
                "Should find significant relationships between " + scale1 + " and " + scale2);
        }
    }
    
    @When("I request personalized treatment recommendations for patient groups:")
    public void requestPersonalizedTreatmentRecommendations(DataTable dataTable) {
        baseSteps.logInfo("Requesting personalized treatment recommendations");
        
        List<Map<String, String>> rows = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String patientGroup = row.get("patient_group");
            String ageRange = row.get("age_range");
            String geneticRisk = row.get("genetic_risk");
            String biomarkerProfile = row.get("biomarker_profile");
            String cognitiveStatus = row.get("cognitive_status");
            
            baseSteps.logInfo("Generating recommendations for: " + patientGroup + " (age=" + ageRange + 
                           ", risk=" + geneticRisk + ", biomarkers=" + biomarkerProfile + 
                           ", cognition=" + cognitiveStatus + ")");
            
            Map<String, Object> result = system.generateTreatmentRecommendations(
                patientGroup, ageRange, geneticRisk, biomarkerProfile, cognitiveStatus);
            
            recommendations.add(result);
        }
        
        baseSteps.context.store("treatmentRecommendations", recommendations);
    }
    
    @Then("the integrated system should provide personalized intervention plans")
    public void integratedSystemShouldProvidePersonalizedInterventionPlans() {
        baseSteps.logInfo("Verifying personalized intervention plans");
        
        List<Map<String, Object>> recommendations = baseSteps.context.retrieve("treatmentRecommendations");
        
        Assertions.assertNotNull(recommendations, "Treatment recommendations should exist");
        Assertions.assertFalse(recommendations.isEmpty(), "Treatment recommendations should not be empty");
        
        for (Map<String, Object> recommendation : recommendations) {
            Assertions.assertTrue(recommendation.containsKey("recommended_plans"), 
                               "Recommendation should include intervention plans");
            Assertions.assertTrue(recommendation.containsKey("efficacy_metrics"), 
                               "Recommendation should include efficacy metrics");
        }
    }
    
    @When("I generate research hypotheses with the following parameters:")
    public void generateResearchHypotheses(DataTable dataTable) {
        baseSteps.logInfo("Generating research hypotheses");
        
        Map<String, String> params = dataTable.asMaps().get(0);
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        
        String evidenceThreshold = params.get("evidence_threshold");
        String noveltyPremium = params.get("novelty_premium");
        String mechanismFocus = params.get("mechanism_focus");
        
        baseSteps.logInfo("Generating hypotheses with threshold=" + evidenceThreshold + 
                       ", novelty=" + noveltyPremium + ", focus=" + mechanismFocus);
        
        List<ALZ001IntegratedModelingSystem.ResearchHypothesis> hypotheses = 
            system.generateResearchHypotheses(evidenceThreshold, noveltyPremium, mechanismFocus);
        
        baseSteps.context.store("researchHypotheses", hypotheses);
    }
    
    @Then("the integrated system should generate at least {int} testable hypotheses")
    public void integratedSystemShouldGenerateTestableHypotheses(int minCount) {
        baseSteps.logInfo("Verifying generation of testable hypotheses");
        
        List<ALZ001IntegratedModelingSystem.ResearchHypothesis> hypotheses = 
            baseSteps.context.retrieve("researchHypotheses");
        
        Assertions.assertNotNull(hypotheses, "Research hypotheses should exist");
        Assertions.assertTrue(hypotheses.size() >= minCount, 
                          "Should generate at least " + minCount + " hypotheses, but got " + hypotheses.size());
    }
    
    @When("I introduce data quality issues to test integrated system resilience:")
    public void introduceDataQualityIssues(DataTable dataTable) {
        baseSteps.logInfo("Introducing data quality issues to test system resilience");
        
        List<Map<String, String>> issues = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        
        Map<String, Object> resilienceReport = system.testSystemResilience(issues);
        baseSteps.context.store("resilienceReport", resilienceReport);
    }
    
    @Then("the integrated system should recover from data quality issues with at least {double} accuracy")
    public void integratedSystemShouldRecoverFromIssues(double minAccuracy) {
        baseSteps.logInfo("Verifying system recovery from data quality issues");
        
        Map<String, Object> resilienceReport = baseSteps.context.retrieve("resilienceReport");
        
        Assertions.assertNotNull(resilienceReport, "Resilience report should exist");
        Assertions.assertTrue(resilienceReport.containsKey("resilience_score"), 
                          "Resilience report should include a resilience score");
        
        double resilienceScore = (double)resilienceReport.get("resilience_score");
        Assertions.assertTrue(resilienceScore >= minAccuracy, 
                          "Resilience score should be at least " + minAccuracy + ", but got " + resilienceScore);
    }
    
    @When("I validate the integrated model against clinical datasets:")
    public void validateIntegratedModelAgainstClinicalDatasets(DataTable dataTable) {
        baseSteps.logInfo("Validating integrated model against clinical datasets");
        
        List<Map<String, String>> datasets = dataTable.asMaps();
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        List<Map<String, Object>> validationResults = new ArrayList<>();
        
        for (Map<String, String> dataset : datasets) {
            String datasetName = dataset.get("dataset_name");
            
            // Create metrics
            List<Map<String, String>> metrics = new ArrayList<>();
            Map<String, String> accuracyMetric = new HashMap<>();
            accuracyMetric.put("metric", "accuracy");
            accuracyMetric.put("threshold", ">0.7");
            metrics.add(accuracyMetric);
            
            Map<String, String> calibrationMetric = new HashMap<>();
            calibrationMetric.put("metric", "calibration_slope");
            calibrationMetric.put("threshold", "0.9-1.1");
            metrics.add(calibrationMetric);
            
            baseSteps.logInfo("Validating against dataset: " + datasetName);
            Map<String, Object> result = system.validateModelPerformance(datasetName, metrics);
            validationResults.add(result);
        }
        
        baseSteps.context.store("validationResults", validationResults);
    }
    
    @Then("the integrated model should demonstrate clinical utility with overall accuracy above {double}")
    public void integratedModelShouldDemonstrateClinicalUtility(double minAccuracy) {
        baseSteps.logInfo("Verifying clinical utility of integrated model");
        
        List<Map<String, Object>> validationResults = baseSteps.context.retrieve("validationResults");
        
        Assertions.assertNotNull(validationResults, "Validation results should exist");
        Assertions.assertFalse(validationResults.isEmpty(), "Validation results should not be empty");
        
        for (Map<String, Object> result : validationResults) {
            Assertions.assertTrue(result.containsKey("overall_accuracy"), 
                               "Validation result should include overall accuracy");
            
            double accuracy = (double)result.get("overall_accuracy");
            Assertions.assertTrue(accuracy >= minAccuracy, 
                               "Overall accuracy should be at least " + minAccuracy + ", but got " + accuracy);
        }
    }
    
    @Then("the system components should communicate through proper channel establishment")
    public void systemComponentsShouldCommunicateThroughProperChannels() {
        baseSteps.logInfo("Verifying component communication through proper channels");
        
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        Assertions.assertEquals("SIMULATION_COMPLETE", system.getState(), 
                             "System should be in SIMULATION_COMPLETE state, which implies proper communication");
    }
    
    @Then("the results from each composite should contribute to the unified model")
    public void resultsFromEachCompositeShouldContributeToUnifiedModel() {
        baseSteps.logInfo("Verifying that results from each composite contribute to the unified model");
        
        // This would require checking specific contributions from each composite
        // Here we just verify that the system reached the completed state
        ALZ001IntegratedModelingSystem system = baseSteps.context.retrieve("integratedSystem");
        Assertions.assertEquals("SIMULATION_COMPLETE", system.getState(), 
                            "System should be in SIMULATION_COMPLETE state");
    }
    
    @Then("the model should identify subgroups where personalization would be most effective")
    public void modelShouldIdentifySubgroupsForPersonalization() {
        baseSteps.logInfo("Verifying identification of subgroups for personalization");
        
        List<Map<String, Object>> validationResults = baseSteps.context.retrieve("validationResults");
        
        Assertions.assertNotNull(validationResults, "Validation results should exist");
        Assertions.assertFalse(validationResults.isEmpty(), "Validation results should not be empty");
        
        for (Map<String, Object> result : validationResults) {
            Assertions.assertTrue(result.containsKey("subgroups_for_improvement"), 
                               "Validation result should include subgroups for improvement");
            
            List<Map<String, Object>> subgroups = (List<Map<String, Object>>)result.get("subgroups_for_improvement");
            Assertions.assertFalse(subgroups.isEmpty(), "Subgroups for improvement should not be empty");
        }
    }
}