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

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.s8r.test.steps.alz001.mock.ALZ001MockFactory;
import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;
import org.s8r.test.steps.alz001.mock.component.PredictiveModelingComponent;
import org.s8r.test.steps.alz001.mock.composite.PredictiveModelingComposite;
import org.s8r.test.steps.alz001.mock.composite.ALZ001MockComposite.ComponentConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Step definitions for testing the PredictiveModelingComposite.
 */
public class PredictiveModelingCompositeSteps {
    private PredictiveModelingComposite composite;
    private Map<String, Object> testContext = new HashMap<>();
    
    @Given("the Predictive Modeling Composite system is initialized")
    public void thePredictiveModelingCompositeSystemIsInitialized() {
        // Just an initialization step, actual composite creation happens in later steps
        testContext.put("systemInitialized", true);
    }
    
    @When("I create a predictive modeling composite named {string}")
    public void iCreateAPredictiveModelingCompositeNamed(String name) {
        composite = ALZ001MockFactory.createPredictiveModelingComposite(name);
        testContext.put("compositeName", name);
    }
    
    @Then("the composite should be successfully created")
    public void theCompositeShouldBeSuccessfullyCreated() {
        Assertions.assertNotNull(composite, "Composite should be created successfully");
        Assertions.assertEquals("READY", composite.getState(), "Composite should be in READY state");
    }
    
    @Then("the composite type should be {string}")
    public void theCompositeTypeShouldBe(String expectedType) {
        Assertions.assertEquals(expectedType, composite.getCompositeType(), 
                               "Composite should have the correct type");
    }
    
    @Then("the composite should have the correct configuration")
    public void theCompositeShouldHaveTheCorrectConfiguration() {
        Map<String, Object> config = ALZ001MockFactory.createPredictiveModelingCompositeConfig();
        
        // Check if important configuration keys are present with expected values
        Assertions.assertEquals(config.get("max_models"), composite.getConfig("max_models"),
                               "Composite should have correct max_models configuration");
        Assertions.assertEquals(config.get("max_cohorts"), composite.getConfig("max_cohorts"),
                               "Composite should have correct max_cohorts configuration");
        Assertions.assertEquals(config.get("clinical_decision_support_enabled"), 
                               composite.getConfig("clinical_decision_support_enabled"),
                               "Composite should have correct clinical_decision_support_enabled configuration");
    }
    
    @Given("I have a predictive modeling composite named {string}")
    public void iHaveAPredictiveModelingCompositeNamed(String name) {
        composite = ALZ001MockFactory.createPredictiveModelingComposite(name);
        testContext.put("compositeName", name);
    }
    
    @When("I add a neural network component to the composite")
    public void iAddANeuralNetworkComponentToTheComposite() {
        String compositeName = (String) testContext.get("compositeName");
        PredictiveModelingComponent component = 
            ALZ001MockFactory.createPredictiveModelingComponent(compositeName + "-NeuralNet");
        composite.addPredictiveComponent(component);
        testContext.put("neuralNetComponentName", compositeName + "-NeuralNet");
    }
    
    @When("I add a biomarker analysis component to the composite")
    public void iAddABiomarkerAnalysisComponentToTheComposite() {
        String compositeName = (String) testContext.get("compositeName");
        PredictiveModelingComponent component = 
            ALZ001MockFactory.createPredictiveModelingComponent(compositeName + "-Biomarker");
        composite.addPredictiveComponent(component);
        testContext.put("biomarkerComponentName", compositeName + "-Biomarker");
    }
    
    @When("I connect the neural network component to the biomarker component")
    public void iConnectTheNeuralNetworkComponentToTheBiomarkerComponent() {
        String neuralNetName = (String) testContext.get("neuralNetComponentName");
        String biomarkerName = (String) testContext.get("biomarkerComponentName");
        
        ALZ001MockComponent neuralNet = composite.getChild(neuralNetName);
        ALZ001MockComponent biomarker = composite.getChild(biomarkerName);
        
        ComponentConnection connection = composite.connect(neuralNet, biomarker, "DATA_FLOW");
        testContext.put("connection", connection);
    }
    
    @Then("the composite should contain {int} child components")
    public void theCompositeShouldContainChildComponents(int expectedCount) {
        Assertions.assertEquals(expectedCount, composite.getChildren().size(), 
                               "Composite should contain the expected number of child components");
    }
    
    @Then("the components should be properly connected")
    public void theComponentsShouldBeProperlyConnected() {
        ComponentConnection connection = (ComponentConnection) testContext.get("connection");
        Assertions.assertNotNull(connection, "Connection should be created");
        Assertions.assertTrue(connection.isActive(), "Connection should be active");
        Assertions.assertEquals("DATA_FLOW", connection.getConnectionType(), 
                               "Connection should have the correct type");
    }
    
    @Then("I should be able to retrieve each component by name")
    public void iShouldBeAbleToRetrieveEachComponentByName() {
        String neuralNetName = (String) testContext.get("neuralNetComponentName");
        String biomarkerName = (String) testContext.get("biomarkerComponentName");
        
        Assertions.assertNotNull(composite.getChild(neuralNetName), 
                               "Should retrieve neural network component by name");
        Assertions.assertNotNull(composite.getChild(biomarkerName), 
                               "Should retrieve biomarker component by name");
    }
    
    @When("I create a neural network model named {string}")
    public void iCreateANeuralNetworkModelNamed(String modelName) {
        PredictiveModelingComposite.PredictiveModel model = 
            composite.createModel(modelName, "neuralNetwork");
        
        // Set some parameters
        model.setParameter("learningRate", 0.001);
        model.setParameter("epochs", 100);
        model.setParameter("hiddenLayers", 3);
        
        // Add some features
        model.addFeature("age");
        model.addFeature("apoe");
        model.addFeature("amyloid_beta");
        model.addFeature("tau");
        
        testContext.put("neuralNetworkModelName", modelName);
    }
    
    @When("I create a random forest model named {string}")
    public void iCreateARandomForestModelNamed(String modelName) {
        PredictiveModelingComposite.PredictiveModel model = 
            composite.createModel(modelName, "randomForest");
        
        // Set some parameters
        model.setParameter("trees", 100);
        model.setParameter("maxDepth", 10);
        model.setParameter("featureSubsetSize", 0.7);
        
        // Add some features
        model.addFeature("age");
        model.addFeature("apoe");
        model.addFeature("amyloid_beta");
        model.addFeature("tau");
        
        testContext.put("randomForestModelName", modelName);
    }
    
    @When("I create a clinical data source with {int} samples")
    public void iCreateAClinicalDataSourceWithSamples(int sampleCount) {
        PredictiveModelingComposite.DataSource dataSource = 
            composite.createDataSource("ClinicalData", "clinical", sampleCount);
        
        // Add fields to the data source schema
        dataSource.addField("patient_id", "string");
        dataSource.addField("age", "numeric");
        dataSource.addField("gender", "categorical");
        dataSource.addField("apoe", "categorical");
        dataSource.addField("amyloid_beta", "numeric");
        dataSource.addField("tau", "numeric");
        dataSource.addField("cognitive_score", "numeric");
        
        testContext.put("dataSourceName", "ClinicalData");
        testContext.put("dataSourceSampleCount", sampleCount);
    }
    
    @When("I train the {string} model on the clinical data source")
    public void iTrainTheModelOnTheClinicalDataSource(String modelName) {
        String dataSourceName = (String) testContext.get("dataSourceName");
        
        PredictiveModelingComposite.ValidationResult result = 
            composite.trainModel(modelName, dataSourceName, 0.2);
        
        testContext.put(modelName + "_validationResult", result);
    }
    
    @Then("both models should be marked as trained")
    public void bothModelsShouldBeMarkedAsTrained() {
        String neuralNetworkModelName = (String) testContext.get("neuralNetworkModelName");
        String randomForestModelName = (String) testContext.get("randomForestModelName");
        
        PredictiveModelingComposite.PredictiveModel nnModel = composite.getModel(neuralNetworkModelName);
        PredictiveModelingComposite.PredictiveModel rfModel = composite.getModel(randomForestModelName);
        
        Assertions.assertTrue(nnModel.isTrained(), "Neural network model should be marked as trained");
        Assertions.assertTrue(rfModel.isTrained(), "Random forest model should be marked as trained");
    }
    
    @Then("both models should have performance metrics")
    public void bothModelsShouldHavePerformanceMetrics() {
        String neuralNetworkModelName = (String) testContext.get("neuralNetworkModelName");
        String randomForestModelName = (String) testContext.get("randomForestModelName");
        
        PredictiveModelingComposite.PredictiveModel nnModel = composite.getModel(neuralNetworkModelName);
        PredictiveModelingComposite.PredictiveModel rfModel = composite.getModel(randomForestModelName);
        
        // Check accuracy metric
        Assertions.assertTrue(nnModel.getPerformance("accuracy") > 0, 
                            "Neural network model should have positive accuracy");
        Assertions.assertTrue(rfModel.getPerformance("accuracy") > 0, 
                            "Random forest model should have positive accuracy");
        
        // Check other metrics
        Assertions.assertTrue(nnModel.getPerformance("precision") > 0, 
                            "Neural network model should have positive precision");
        Assertions.assertTrue(nnModel.getPerformance("recall") > 0, 
                            "Neural network model should have positive recall");
        Assertions.assertTrue(nnModel.getPerformance("f1Score") > 0, 
                            "Neural network model should have positive F1 score");
    }
    
    @Then("the neural network model should have an accuracy above {double}")
    public void theNeuralNetworkModelShouldHaveAnAccuracyAbove(double minAccuracy) {
        String neuralNetworkModelName = (String) testContext.get("neuralNetworkModelName");
        PredictiveModelingComposite.PredictiveModel nnModel = composite.getModel(neuralNetworkModelName);
        
        Assertions.assertTrue(nnModel.getPerformance("accuracy") >= minAccuracy, 
                            "Neural network model should have accuracy above " + minAccuracy);
    }
    
    @Given("I have trained multiple predictive models")
    public void iHaveTrainedMultiplePredictiveModels() {
        // Create and train multiple models
        iCreateANeuralNetworkModelNamed("Model1");
        iCreateARandomForestModelNamed("Model2");
        
        // Create an SVM model manually
        PredictiveModelingComposite.PredictiveModel svmModel = 
            composite.createModel("Model3", "svm");
        svmModel.addFeature("age");
        svmModel.addFeature("amyloid_beta");
        svmModel.addFeature("tau");
        
        // Create data source
        iCreateAClinicalDataSourceWithSamples(500);
        
        // Train all models
        iTrainTheModelOnTheClinicalDataSource("Model1");
        iTrainTheModelOnTheClinicalDataSource("Model2");
        iTrainTheModelOnTheClinicalDataSource("Model3");
        
        // Save model names
        List<String> modelNames = new ArrayList<>();
        modelNames.add("Model1");
        modelNames.add("Model2");
        modelNames.add("Model3");
        testContext.put("modelNames", modelNames);
    }
    
    @When("I create an ensemble named {string} using the voting method")
    public void iCreateAnEnsembleNamedUsingTheVotingMethod(String ensembleName) {
        PredictiveModelingComposite.ModelEnsemble ensemble = 
            composite.createEnsemble(ensembleName, "voting");
        testContext.put("ensembleName", ensembleName);
    }
    
    @When("I add all trained models to the ensemble")
    public void iAddAllTrainedModelsToTheEnsemble() {
        String ensembleName = (String) testContext.get("ensembleName");
        @SuppressWarnings("unchecked")
        List<String> modelNames = (List<String>) testContext.get("modelNames");
        
        PredictiveModelingComposite.ModelEnsemble ensemble = composite.getEnsemble(ensembleName);
        
        for (String modelName : modelNames) {
            ensemble.addModel(modelName);
        }
        
        // Train the ensemble
        composite.trainEnsemble(ensembleName, (String) testContext.get("dataSourceName"), 0.2);
    }
    
    @Then("the ensemble should contain all the models")
    public void theEnsembleShouldContainAllTheModels() {
        String ensembleName = (String) testContext.get("ensembleName");
        @SuppressWarnings("unchecked")
        List<String> modelNames = (List<String>) testContext.get("modelNames");
        
        PredictiveModelingComposite.ModelEnsemble ensemble = composite.getEnsemble(ensembleName);
        List<String> ensembleModels = ensemble.getModels();
        
        Assertions.assertEquals(modelNames.size(), ensembleModels.size(), 
                              "Ensemble should contain all models");
        
        for (String modelName : modelNames) {
            Assertions.assertTrue(ensembleModels.contains(modelName), 
                                "Ensemble should contain model: " + modelName);
        }
    }
    
    @Then("the ensemble should have weights assigned to each model")
    public void theEnsembleShouldHaveWeightsAssignedToEachModel() {
        String ensembleName = (String) testContext.get("ensembleName");
        @SuppressWarnings("unchecked")
        List<String> modelNames = (List<String>) testContext.get("modelNames");
        
        PredictiveModelingComposite.ModelEnsemble ensemble = composite.getEnsemble(ensembleName);
        
        double totalWeight = 0.0;
        for (String modelName : modelNames) {
            double weight = ensemble.getWeight(modelName);
            Assertions.assertTrue(weight > 0, "Model should have positive weight: " + modelName);
            totalWeight += weight;
        }
        
        // Weights should sum to approximately 1.0 (accounting for floating-point precision)
        Assertions.assertEquals(1.0, totalWeight, 0.001, 
                              "Model weights should sum to approximately 1.0");
    }
    
    @Then("the ensemble performance should be higher than the average model performance")
    public void theEnsemblePerformanceShouldBeHigherThanTheAverageModelPerformance() {
        String ensembleName = (String) testContext.get("ensembleName");
        @SuppressWarnings("unchecked")
        List<String> modelNames = (List<String>) testContext.get("modelNames");
        
        PredictiveModelingComposite.ModelEnsemble ensemble = composite.getEnsemble(ensembleName);
        
        // Calculate average model accuracy
        double totalAccuracy = 0.0;
        for (String modelName : modelNames) {
            PredictiveModelingComposite.PredictiveModel model = composite.getModel(modelName);
            totalAccuracy += model.getPerformance("accuracy");
        }
        double averageAccuracy = totalAccuracy / modelNames.size();
        
        // Get ensemble accuracy
        double ensembleAccuracy = ensemble.getPerformance("accuracy");
        
        Assertions.assertTrue(ensembleAccuracy > averageAccuracy, 
                            "Ensemble accuracy should be higher than average model accuracy");
    }
    
    @Given("I have a trained predictive model")
    public void iHaveATrainedPredictiveModel() {
        iCreateANeuralNetworkModelNamed("PredictionModel");
        iCreateAClinicalDataSourceWithSamples(500);
        iTrainTheModelOnTheClinicalDataSource("PredictionModel");
        testContext.put("trainedModelName", "PredictionModel");
    }
    
    @When("I create a patient cohort with {int} patients")
    public void iCreateAPatientCohortWithPatients(int patientCount) {
        PredictiveModelingComposite.PatientCohort cohort = 
            composite.createRandomCohort("TestCohort", "Test cohort for prediction", patientCount);
        testContext.put("cohortName", "TestCohort");
        testContext.put("cohortSize", patientCount);
    }
    
    @When("I predict biomarker levels for all patients in the cohort")
    public void iPredictBiomarkerLevelsForAllPatientsInTheCohort() {
        String modelName = (String) testContext.get("trainedModelName");
        String cohortName = (String) testContext.get("cohortName");
        
        Map<String, Double> predictions = 
            composite.predictOutcomes(modelName, cohortName, "biomarkerLevel");
        
        testContext.put("predictions", predictions);
    }
    
    @Then("each patient should have a predicted biomarker level")
    public void eachPatientShouldHaveAPredictedBiomarkerLevel() {
        @SuppressWarnings("unchecked")
        Map<String, Double> predictions = (Map<String, Double>) testContext.get("predictions");
        String cohortName = (String) testContext.get("cohortName");
        
        PredictiveModelingComposite.PatientCohort cohort = composite.getCohort(cohortName);
        List<String> patients = cohort.getPatients();
        
        Assertions.assertEquals(patients.size(), predictions.size(), 
                              "There should be a prediction for each patient");
        
        for (String patientId : patients) {
            Assertions.assertTrue(predictions.containsKey(patientId), 
                                "There should be a prediction for patient: " + patientId);
            Assertions.assertNotNull(predictions.get(patientId), 
                                   "Prediction should not be null for patient: " + patientId);
        }
    }
    
    @Then("I should be able to stratify patients by risk level")
    public void iShouldBeAbleToStratifyPatientsByRiskLevel() {
        @SuppressWarnings("unchecked")
        Map<String, Double> predictions = (Map<String, Double>) testContext.get("predictions");
        
        // Stratify patients into risk categories
        Map<String, List<String>> riskCategories = new HashMap<>();
        riskCategories.put("high", new ArrayList<>());
        riskCategories.put("moderate", new ArrayList<>());
        riskCategories.put("low", new ArrayList<>());
        
        for (Map.Entry<String, Double> entry : predictions.entrySet()) {
            String patientId = entry.getKey();
            Double biomarkerLevel = entry.getValue();
            
            if (biomarkerLevel >= 80.0) {
                riskCategories.get("high").add(patientId);
            } else if (biomarkerLevel >= 60.0) {
                riskCategories.get("moderate").add(patientId);
            } else {
                riskCategories.get("low").add(patientId);
            }
        }
        
        testContext.put("riskCategories", riskCategories);
        
        // Verify that we have at least one patient in each category
        // (this is a probabilistic test, so it might occasionally fail)
        Assertions.assertTrue(riskCategories.get("high").size() > 0, 
                            "There should be at least one high-risk patient");
        Assertions.assertTrue(riskCategories.get("moderate").size() > 0, 
                            "There should be at least one moderate-risk patient");
        Assertions.assertTrue(riskCategories.get("low").size() > 0, 
                            "There should be at least one low-risk patient");
    }
    
    @Then("high-risk patients should have higher biomarker levels")
    public void highRiskPatientsShouldHaveHigherBiomarkerLevels() {
        @SuppressWarnings("unchecked")
        Map<String, Double> predictions = (Map<String, Double>) testContext.get("predictions");
        @SuppressWarnings("unchecked")
        Map<String, List<String>> riskCategories = (Map<String, List<String>>) testContext.get("riskCategories");
        
        // Calculate average biomarker level for each risk category
        double highRiskAvg = calculateAverageBiomarkerLevel(riskCategories.get("high"), predictions);
        double moderateRiskAvg = calculateAverageBiomarkerLevel(riskCategories.get("moderate"), predictions);
        double lowRiskAvg = calculateAverageBiomarkerLevel(riskCategories.get("low"), predictions);
        
        Assertions.assertTrue(highRiskAvg > moderateRiskAvg, 
                            "High-risk patients should have higher biomarker levels than moderate-risk patients");
        Assertions.assertTrue(moderateRiskAvg > lowRiskAvg, 
                            "Moderate-risk patients should have higher biomarker levels than low-risk patients");
    }
    
    private double calculateAverageBiomarkerLevel(List<String> patients, Map<String, Double> predictions) {
        if (patients.isEmpty()) {
            return 0.0;
        }
        
        double total = 0.0;
        for (String patientId : patients) {
            total += predictions.get(patientId);
        }
        return total / patients.size();
    }
    
    @Given("I have a high-risk patient cohort")
    public void iHaveAHighRiskPatientCohort() {
        PredictiveModelingComposite.PatientCohort cohort = 
            composite.createRandomCohort("HighRiskCohort", "High-risk patient cohort", 20);
        
        // Set metadata indicating high risk
        cohort.setMetadata("averageAge", 75.5);
        cohort.setMetadata("apoeE4Prevalence", 0.7);
        
        testContext.put("highRiskCohortName", "HighRiskCohort");
    }
    
    @When("I create a pharmacological intervention plan")
    public void iCreateAPharmacologicalInterventionPlan() {
        PredictiveModelingComposite.InterventionPlan plan = 
            composite.createInterventionPlan("PharmaPlan", "Pharmaceutical intervention");
        
        plan.setParameter("medicationType", "Cholinesterase");
        plan.setParameter("medicationDosage", "Standard");
        plan.setParameter("duration", 12);
        
        plan.setExpectedOutcome("cognitionImprovement", 0.15);
        plan.setExpectedOutcome("biomarkerReduction", 0.10);
        
        testContext.put("pharmaPlanName", "PharmaPlan");
    }
    
    @When("I create a lifestyle intervention plan")
    public void iCreateALifestyleInterventionPlan() {
        PredictiveModelingComposite.InterventionPlan plan = 
            composite.createInterventionPlan("LifestylePlan", "Lifestyle intervention");
        
        plan.setParameter("exerciseRegimen", "Moderate");
        plan.setParameter("dietaryApproach", "Mediterranean");
        plan.setParameter("cognitiveTraining", "Weekly");
        
        plan.setExpectedOutcome("cognitionImprovement", 0.12);
        plan.setExpectedOutcome("biomarkerReduction", 0.08);
        
        testContext.put("lifestylePlanName", "LifestylePlan");
    }
    
    @When("I create a comprehensive intervention plan")
    public void iCreateAComprehensiveInterventionPlan() {
        PredictiveModelingComposite.InterventionPlan plan = 
            composite.createComprehensiveInterventionPlan("ComprehensivePlan", 
                                                       "Combined pharmacological and lifestyle intervention");
        
        testContext.put("comprehensivePlanName", "ComprehensivePlan");
    }
    
    @When("I evaluate all intervention plans on the high-risk cohort")
    public void iEvaluateAllInterventionPlansOnTheHighRiskCohort() {
        String cohortName = (String) testContext.get("highRiskCohortName");
        String modelName = (String) testContext.get("trainedModelName");
        
        Map<String, Double> efficacyScores = composite.evaluateInterventionPlans(cohortName, modelName);
        testContext.put("efficacyScores", efficacyScores);
    }
    
    @Then("each intervention plan should have an efficacy score")
    public void eachInterventionPlanShouldHaveAnEfficacyScore() {
        @SuppressWarnings("unchecked")
        Map<String, Double> efficacyScores = (Map<String, Double>) testContext.get("efficacyScores");
        String pharmaPlanName = (String) testContext.get("pharmaPlanName");
        String lifestylePlanName = (String) testContext.get("lifestylePlanName");
        String comprehensivePlanName = (String) testContext.get("comprehensivePlanName");
        
        Assertions.assertTrue(efficacyScores.containsKey(pharmaPlanName), 
                            "Pharmacological plan should have an efficacy score");
        Assertions.assertTrue(efficacyScores.containsKey(lifestylePlanName), 
                            "Lifestyle plan should have an efficacy score");
        Assertions.assertTrue(efficacyScores.containsKey(comprehensivePlanName), 
                            "Comprehensive plan should have an efficacy score");
    }
    
    @Then("the comprehensive plan should have the highest efficacy")
    public void theComprehensivePlanShouldHaveTheHighestEfficacy() {
        @SuppressWarnings("unchecked")
        Map<String, Double> efficacyScores = (Map<String, Double>) testContext.get("efficacyScores");
        String pharmaPlanName = (String) testContext.get("pharmaPlanName");
        String lifestylePlanName = (String) testContext.get("lifestylePlanName");
        String comprehensivePlanName = (String) testContext.get("comprehensivePlanName");
        
        double pharmaEfficacy = efficacyScores.get(pharmaPlanName);
        double lifestyleEfficacy = efficacyScores.get(lifestylePlanName);
        double comprehensiveEfficacy = efficacyScores.get(comprehensivePlanName);
        
        Assertions.assertTrue(comprehensiveEfficacy > pharmaEfficacy, 
                            "Comprehensive plan should be more effective than pharmacological plan");
        Assertions.assertTrue(comprehensiveEfficacy > lifestyleEfficacy, 
                            "Comprehensive plan should be more effective than lifestyle plan");
    }
    
    @Then("I should be able to simulate the effect of interventions over time")
    public void iShouldBeAbleToSimulateTheEffectOfInterventionsOverTime() {
        String cohortName = (String) testContext.get("highRiskCohortName");
        String planName = (String) testContext.get("comprehensivePlanName");
        
        Map<String, List<Double>> trajectories = 
            composite.simulateInterventionEffect(cohortName, planName, "cognitiveScore", 10);
        
        Assertions.assertNotNull(trajectories, "Should be able to simulate intervention effects");
        Assertions.assertFalse(trajectories.isEmpty(), "Simulated trajectories should not be empty");
        
        // Verify that at least one trajectory shows improvement over time
        boolean foundImprovement = false;
        for (List<Double> trajectory : trajectories.values()) {
            if (trajectory.size() >= 2 && trajectory.get(trajectory.size() - 1) > trajectory.get(0)) {
                foundImprovement = true;
                break;
            }
        }
        
        Assertions.assertTrue(foundImprovement, 
                            "At least one trajectory should show improvement over time");
    }
    
    @Given("I have a fully configured predictive modeling system")
    public void iHaveAFullyConfiguredPredictiveModelingSystem() {
        String compositeName = (String) testContext.get("compositeName");
        composite = ALZ001MockFactory.createFullPredictiveModelingComposite(compositeName);
    }
    
    @When("I generate a clinical report for a high-risk patient")
    public void iGenerateAClinicalReportForAHighRiskPatient() {
        PredictiveModelingComposite.PatientCohort highRiskCohort = composite.getCohort("HighRiskCohort");
        String patientId = highRiskCohort.getPatients().get(0);
        
        Map<String, Object> report = composite.generateClinicalReport(patientId, "CombinedModel");
        testContext.put("clinicalReport", report);
        testContext.put("reportPatientId", patientId);
    }
    
    @Then("the report should contain patient information")
    public void theReportShouldContainPatientInformation() {
        @SuppressWarnings("unchecked")
        Map<String, Object> report = (Map<String, Object>) testContext.get("clinicalReport");
        String patientId = (String) testContext.get("reportPatientId");
        
        Assertions.assertTrue(report.containsKey("patientInformation"), 
                            "Report should contain patient information section");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> patientInfo = (Map<String, Object>) report.get("patientInformation");
        Assertions.assertEquals(patientId, patientInfo.get("patientId"), 
                              "Patient information should contain the correct patient ID");
    }
    
    @Then("the report should contain a risk assessment")
    public void theReportShouldContainARiskAssessment() {
        @SuppressWarnings("unchecked")
        Map<String, Object> report = (Map<String, Object>) testContext.get("clinicalReport");
        
        Assertions.assertTrue(report.containsKey("riskAssessment"), 
                            "Report should contain risk assessment section");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> riskAssessment = (Map<String, Object>) report.get("riskAssessment");
        Assertions.assertTrue(riskAssessment.containsKey("riskCategory"), 
                            "Risk assessment should classify risk category");
        Assertions.assertTrue(riskAssessment.containsKey("biomarkerLevel"), 
                            "Risk assessment should include biomarker levels");
    }
    
    @Then("the report should contain intervention recommendations")
    public void theReportShouldContainInterventionRecommendations() {
        @SuppressWarnings("unchecked")
        Map<String, Object> report = (Map<String, Object>) testContext.get("clinicalReport");
        
        Assertions.assertTrue(report.containsKey("interventionRecommendations"), 
                            "Report should contain intervention recommendations section");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> recommendations = (Map<String, Object>) report.get("interventionRecommendations");
        Assertions.assertTrue(recommendations.containsKey("recommendedPlans"), 
                            "Recommendations should include recommended plans");
    }
    
    @Then("the report should contain predicted disease trajectories")
    public void theReportShouldContainPredictedDiseaseTrajectories() {
        @SuppressWarnings("unchecked")
        Map<String, Object> report = (Map<String, Object>) testContext.get("clinicalReport");
        
        Assertions.assertTrue(report.containsKey("trajectories"), 
                            "Report should contain disease trajectories section");
        
        @SuppressWarnings("unchecked")
        Map<String, Object> trajectories = (Map<String, Object>) report.get("trajectories");
        Assertions.assertTrue(trajectories.containsKey("withIntervention") || 
                            trajectories.containsKey("timePoints"), 
                            "Trajectories should include intervention effects");
    }
    
    @Then("the recommended interventions should be ranked by efficacy")
    public void theRecommendedInterventionsShouldBeRankedByEfficacy() {
        @SuppressWarnings("unchecked")
        Map<String, Object> report = (Map<String, Object>) testContext.get("clinicalReport");
        @SuppressWarnings("unchecked")
        Map<String, Object> recommendations = (Map<String, Object>) report.get("interventionRecommendations");
        
        if (recommendations.containsKey("recommendedPlans")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> plans = (List<Map<String, Object>>) recommendations.get("recommendedPlans");
            
            // Verify that plans are ordered by efficacy (if multiple plans exist)
            if (plans.size() >= 2) {
                double prevEfficacy = Double.MAX_VALUE;
                for (Map<String, Object> plan : plans) {
                    double efficacy = (Double) plan.get("efficacyScore");
                    Assertions.assertTrue(efficacy <= prevEfficacy, 
                                        "Plans should be ranked by descending efficacy");
                    prevEfficacy = efficacy;
                }
            }
        }
    }
    
    @Given("I have a trained random forest model with Alzheimer's features")
    public void iHaveATrainedRandomForestModelWithAlzheimersFeatures() {
        PredictiveModelingComposite.PredictiveModel model = 
            composite.createModel("FeatureImportanceModel", "randomForest");
        
        // Add Alzheimer's features
        model.addFeature("age");
        model.addFeature("apoe");
        model.addFeature("amyloid_beta");
        model.addFeature("tau");
        model.addFeature("phosphorylated_tau");
        model.addFeature("cognitive_score");
        model.addFeature("education_years");
        model.addFeature("physical_activity");
        model.addFeature("diet_quality");
        model.addFeature("sleep_quality");
        
        // Create data source
        PredictiveModelingComposite.DataSource dataSource = 
            composite.createDataSource("AlzheimersData", "clinical", 500);
        
        // Train the model
        composite.trainModel("FeatureImportanceModel", "AlzheimersData", 0.2);
        
        testContext.put("featureImportanceModelName", "FeatureImportanceModel");
    }
    
    @When("I analyze the feature importance of the model")
    public void iAnalyzeTheFeatureImportanceOfTheModel() {
        String modelName = (String) testContext.get("featureImportanceModelName");
        Map<String, Double> featureImportance = composite.analyzeFeatureImportance(modelName);
        testContext.put("featureImportance", featureImportance);
    }
    
    @Then("APOE genotype should be among the top features")
    public void apoeGenotypeShouldBeAmongTheTopFeatures() {
        @SuppressWarnings("unchecked")
        Map<String, Double> featureImportance = (Map<String, Double>) testContext.get("featureImportance");
        
        // Get top 3 features
        List<String> topFeatures = featureImportance.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(3)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        Assertions.assertTrue(topFeatures.contains("apoe"), 
                            "APOE genotype should be among top 3 features");
    }
    
    @Then("amyloid beta levels should be among the top features")
    public void amyloidBetaLevelsShouldBeAmongTheTopFeatures() {
        @SuppressWarnings("unchecked")
        Map<String, Double> featureImportance = (Map<String, Double>) testContext.get("featureImportance");
        
        // Get top 5 features
        List<String> topFeatures = featureImportance.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        Assertions.assertTrue(topFeatures.contains("amyloid_beta"), 
                            "Amyloid beta should be among top 5 features");
    }
    
    @Then("tau protein levels should be among the top features")
    public void tauProteinLevelsShouldBeAmongTheTopFeatures() {
        @SuppressWarnings("unchecked")
        Map<String, Double> featureImportance = (Map<String, Double>) testContext.get("featureImportance");
        
        // Get top 5 features
        List<String> topFeatures = featureImportance.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        Assertions.assertTrue(topFeatures.contains("tau"), 
                            "Tau protein should be among top 5 features");
    }
    
    @Then("the feature importance scores should sum to approximately {double}")
    public void theFeatureImportanceScoresShouldSumToApproximately(double expectedSum) {
        @SuppressWarnings("unchecked")
        Map<String, Double> featureImportance = (Map<String, Double>) testContext.get("featureImportance");
        
        double sum = featureImportance.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        Assertions.assertEquals(expectedSum, sum, 0.01, 
                              "Feature importance scores should sum to approximately " + expectedSum);
    }
    
    @Given("I have a predictive model with initial parameters")
    public void iHaveAPredictiveModelWithInitialParameters() {
        PredictiveModelingComposite.PredictiveModel model = 
            composite.createModel("OptimizationModel", "neuralNetwork");
        
        // Set initial parameters
        model.setParameter("learningRate", 0.01);
        model.setParameter("epochs", 50);
        model.setParameter("batchSize", 16);
        model.setParameter("hiddenLayers", 2);
        model.setParameter("dropout", 0.5);
        
        // Add features
        model.addFeature("age");
        model.addFeature("apoe");
        model.addFeature("amyloid_beta");
        model.addFeature("tau");
        
        // Create data source and train with initial parameters
        PredictiveModelingComposite.DataSource dataSource = 
            composite.createDataSource("OptimizationData", "clinical", 500);
        
        composite.trainModel("OptimizationModel", "OptimizationData", 0.2);
        
        // Save initial performance and parameters
        Map<String, Object> initialParams = new HashMap<>();
        initialParams.put("learningRate", model.getParameter("learningRate"));
        initialParams.put("epochs", model.getParameter("epochs"));
        initialParams.put("batchSize", model.getParameter("batchSize"));
        initialParams.put("hiddenLayers", model.getParameter("hiddenLayers"));
        initialParams.put("dropout", model.getParameter("dropout"));
        
        double initialAccuracy = model.getPerformance("accuracy");
        
        testContext.put("optimizationModelName", "OptimizationModel");
        testContext.put("initialParams", initialParams);
        testContext.put("initialAccuracy", initialAccuracy);
    }
    
    @When("I create a parameter grid for optimization")
    public void iCreateAParameterGridForOptimization() {
        Map<String, List<Object>> paramGrid = new HashMap<>();
        
        // Define parameter grid
        List<Object> learningRates = new ArrayList<>();
        learningRates.add(0.001);
        learningRates.add(0.01);
        learningRates.add(0.1);
        paramGrid.put("learningRate", learningRates);
        
        List<Object> epochs = new ArrayList<>();
        epochs.add(50);
        epochs.add(100);
        epochs.add(200);
        paramGrid.put("epochs", epochs);
        
        List<Object> batchSizes = new ArrayList<>();
        batchSizes.add(16);
        batchSizes.add(32);
        batchSizes.add(64);
        paramGrid.put("batchSize", batchSizes);
        
        List<Object> hiddenLayers = new ArrayList<>();
        hiddenLayers.add(2);
        hiddenLayers.add(3);
        hiddenLayers.add(4);
        paramGrid.put("hiddenLayers", hiddenLayers);
        
        List<Object> dropouts = new ArrayList<>();
        dropouts.add(0.2);
        dropouts.add(0.5);
        dropouts.add(0.7);
        paramGrid.put("dropout", dropouts);
        
        testContext.put("paramGrid", paramGrid);
    }
    
    @When("I optimize the model hyperparameters")
    public void iOptimizeTheModelHyperparameters() {
        String modelName = (String) testContext.get("optimizationModelName");
        @SuppressWarnings("unchecked")
        Map<String, List<Object>> paramGrid = (Map<String, List<Object>>) testContext.get("paramGrid");
        
        Map<String, Object> bestParams = composite.optimizeHyperparameters(modelName, paramGrid);
        testContext.put("bestParams", bestParams);
        
        // Get updated performance
        PredictiveModelingComposite.PredictiveModel model = composite.getModel(modelName);
        double optimizedAccuracy = model.getPerformance("accuracy");
        testContext.put("optimizedAccuracy", optimizedAccuracy);
    }
    
    @Then("the optimized parameters should differ from the initial parameters")
    public void theOptimizedParametersShouldDifferFromTheInitialParameters() {
        @SuppressWarnings("unchecked")
        Map<String, Object> initialParams = (Map<String, Object>) testContext.get("initialParams");
        @SuppressWarnings("unchecked")
        Map<String, Object> bestParams = (Map<String, Object>) testContext.get("bestParams");
        
        // At least one parameter should differ
        boolean anyDifferent = false;
        for (String key : initialParams.keySet()) {
            if (bestParams.containsKey(key) && !initialParams.get(key).equals(bestParams.get(key))) {
                anyDifferent = true;
                break;
            }
        }
        
        Assertions.assertTrue(anyDifferent, 
                            "At least one parameter should differ after optimization");
    }
    
    @Then("the model performance should improve after optimization")
    public void theModelPerformanceShouldImproveAfterOptimization() {
        double initialAccuracy = (Double) testContext.get("initialAccuracy");
        double optimizedAccuracy = (Double) testContext.get("optimizedAccuracy");
        
        Assertions.assertTrue(optimizedAccuracy >= initialAccuracy, 
                            "Model performance should improve after optimization");
    }
    
    @When("I create a fully configured predictive modeling composite")
    public void iCreateAFullyConfiguredPredictiveModelingComposite() {
        composite = ALZ001MockFactory.createFullPredictiveModelingComposite("FullPredictiveComposite");
    }
    
    @Then("the composite should contain multiple predictive components")
    public void theCompositeShouldContainMultiplePredictiveComponents() {
        List<ALZ001MockComponent> children = composite.getChildren();
        Assertions.assertTrue(children.size() >= 2, 
                            "Composite should contain multiple predictive components");
    }
    
    @Then("the composite should have standard models created")
    public void theCompositeShouldHaveStandardModelsCreated() {
        Map<String, PredictiveModelingComposite.PredictiveModel> models = composite.getAllModels();
        Assertions.assertTrue(models.size() >= 3, 
                            "Composite should have standard models created");
        
        Assertions.assertTrue(models.containsKey("NeuralNetworkModel"), 
                            "Composite should have a neural network model");
        Assertions.assertTrue(models.containsKey("RandomForestModel"), 
                            "Composite should have a random forest model");
    }
    
    @Then("the composite should have standard data sources created")
    public void theCompositeShouldHaveStandardDataSourcesCreated() {
        Map<String, PredictiveModelingComposite.DataSource> dataSources = composite.getAllDataSources();
        Assertions.assertTrue(dataSources.size() >= 2, 
                            "Composite should have standard data sources created");
        
        Assertions.assertTrue(dataSources.containsKey("ClinicalData"), 
                            "Composite should have clinical data source");
        Assertions.assertTrue(dataSources.containsKey("BiomarkerData"), 
                            "Composite should have biomarker data source");
    }
    
    @Then("the composite should have standard cohorts created")
    public void theCompositeShouldHaveStandardCohortsCreated() {
        Map<String, PredictiveModelingComposite.PatientCohort> cohorts = composite.getAllCohorts();
        Assertions.assertTrue(cohorts.size() >= 2, 
                            "Composite should have standard cohorts created");
        
        Assertions.assertTrue(cohorts.containsKey("ControlCohort"), 
                            "Composite should have control cohort");
        Assertions.assertTrue(cohorts.containsKey("HighRiskCohort"), 
                            "Composite should have high-risk cohort");
    }
    
    @Then("the composite should have standard intervention plans created")
    public void theCompositeShouldHaveStandardInterventionPlansCreated() {
        Map<String, PredictiveModelingComposite.InterventionPlan> plans = composite.getAllInterventionPlans();
        Assertions.assertTrue(plans.size() >= 2, 
                            "Composite should have standard intervention plans created");
        
        Assertions.assertTrue(plans.containsKey("PharmacologicalPlan"), 
                            "Composite should have pharmacological plan");
        Assertions.assertTrue(plans.containsKey("ComprehensivePlan"), 
                            "Composite should have comprehensive plan");
    }
    
    @Then("all models should be trained and ready for predictions")
    public void allModelsShouldBeTrainedAndReadyForPredictions() {
        Map<String, PredictiveModelingComposite.PredictiveModel> models = composite.getAllModels();
        
        for (PredictiveModelingComposite.PredictiveModel model : models.values()) {
            Assertions.assertTrue(model.isTrained(), 
                                "Model should be trained: " + model.getName());
            Assertions.assertTrue(model.getPerformance("accuracy") > 0, 
                                "Model should have positive accuracy: " + model.getName());
        }
        
        Map<String, PredictiveModelingComposite.ModelEnsemble> ensembles = composite.getAllEnsembles();
        
        for (PredictiveModelingComposite.ModelEnsemble ensemble : ensembles.values()) {
            Assertions.assertTrue(ensemble.getPerformance("accuracy") > 0, 
                                "Ensemble should have positive accuracy: " + ensemble.getName());
        }
    }
}