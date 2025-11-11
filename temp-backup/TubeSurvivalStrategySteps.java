/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.tube.lifecycle.steps;

import io.cucumber.java.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;

import org.junit.jupiter.api.Assertions;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.lifecycle.LifecycleState;
import org.s8r.domain.component.lifecycle.Strategy;
import org.s8r.domain.environment.EnvironmentalCondition;
import org.s8r.domain.environment.EnvironmentalFactor;
import org.s8r.domain.tube.KnowledgeType;
import org.s8r.domain.tube.Tube;
import org.s8r.domain.tube.TubeEnvironment;
import org.s8r.domain.tube.SurvivalStrategy;
import org.s8r.domain.tube.StrategyMode;
import org.s8r.domain.tube.ReproductionOutcome;

import java.util.*;
import java.util.logging.Logger;

/**
 * Step definitions for testing tube survival strategies (immortality vs. reproduction)
 * based on environmental conditions
 */
public class TubeSurvivalStrategySteps extends BaseLifecycleSteps {
    
    private static final Logger LOGGER = Logger.getLogger(TubeSurvivalStrategySteps.class.getName());
    
    private Tube tube;
    private TubeEnvironment environment;
    private SurvivalStrategy selectedStrategy;
    private StrategyMode selectedMode;
    private Map<String, EnvironmentalFactor> environmentalFactors;
    private Map<String, KnowledgeType> accumulatedKnowledge;
    private List<ReproductionOutcome> reproductionHistory;
    private double completionPercentage;
    private boolean reproductionInterrupted;
    private List<String> enabledMechanisms;
    private double decisionConfidence;
    
    public TubeSurvivalStrategySteps() {
        environmentalFactors = new HashMap<>();
        accumulatedKnowledge = new HashMap<>();
        reproductionHistory = new ArrayList<>();
        enabledMechanisms = new ArrayList<>();
    }
    
    @Given("a tube in {string} state")
    public void aTubeInState(String state) {
        tube = createTubeInState(state);
        environment = new TubeEnvironment();
        LOGGER.info("Created tube in state: " + state);
    }
    
    @Given("a tube in {string} state with {string} strategy")
    public void aTubeInStateWithStrategy(String state, String strategy) {
        tube = createTubeInState(state);
        selectedStrategy = SurvivalStrategy.valueOf(strategy);
        tube.setSurvivalStrategy(selectedStrategy);
        environment = new TubeEnvironment();
        LOGGER.info("Created tube in state: " + state + " with strategy: " + strategy);
    }
    
    @Given("the environment has the following conditions:")
    public void theEnvironmentHasTheFollowingConditions(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String factorName = row.get("factor");
            int value = Integer.parseInt(row.get("value"));
            int threshold = Integer.parseInt(row.get("threshold"));
            String classification = row.get("classification");
            
            EnvironmentalFactor factor = new EnvironmentalFactor(factorName, value, threshold, classification);
            environmentalFactors.put(factorName, factor);
            environment.addFactor(factor);
        }
        
        LOGGER.info("Set up environment with " + environmentalFactors.size() + " factors");
    }
    
    @Given("the environment initially has harsh conditions")
    public void theEnvironmentInitiallyHasHarshConditions() {
        // Set up default harsh environment
        environmentalFactors.put("resourceAvailability", new EnvironmentalFactor("resourceAvailability", 3, 5, "scarce"));
        environmentalFactors.put("environmentalStability", new EnvironmentalFactor("environmentalStability", 4, 6, "unstable"));
        environmentalFactors.put("hostilityLevel", new EnvironmentalFactor("hostilityLevel", 6, 5, "hostile"));
        
        for (EnvironmentalFactor factor : environmentalFactors.values()) {
            environment.addFactor(factor);
        }
        
        LOGGER.info("Set up harsh environmental conditions");
    }
    
    @Given("the environment initially has favorable conditions")
    public void theEnvironmentInitiallyHasFavorableConditions() {
        // Set up default favorable environment
        environmentalFactors.put("resourceAvailability", new EnvironmentalFactor("resourceAvailability", 7, 5, "abundant"));
        environmentalFactors.put("environmentalStability", new EnvironmentalFactor("environmentalStability", 8, 6, "stable"));
        environmentalFactors.put("hostilityLevel", new EnvironmentalFactor("hostilityLevel", 2, 5, "benign"));
        
        for (EnvironmentalFactor factor : environmentalFactors.values()) {
            environment.addFactor(factor);
        }
        
        LOGGER.info("Set up favorable environmental conditions");
    }
    
    @Given("the tube has accumulated the following knowledge:")
    public void theTubeHasAccumulatedTheFollowingKnowledge(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String knowledgeTypeName = row.get("knowledgeType");
            String priority = row.get("priority");
            String transferMethod = row.get("transferMethod");
            
            KnowledgeType knowledgeType = new KnowledgeType(knowledgeTypeName, priority, transferMethod);
            accumulatedKnowledge.put(knowledgeTypeName, knowledgeType);
            tube.addKnowledge(knowledgeType);
        }
        
        LOGGER.info("Added " + accumulatedKnowledge.size() + " knowledge types to tube");
    }
    
    @Given("the tube has begun knowledge transfer to offspring")
    public void theTubeHasBegunKnowledgeTransferToOffspring() {
        tube.setSurvivalStrategy(SurvivalStrategy.REPRODUCTION);
        tube.setStrategyMode(StrategyMode.KNOWLEDGE_TRANSFER);
        tube.initiateReproduction();
        completionPercentage = 60;
        LOGGER.info("Tube has begun reproduction with knowledge transfer");
    }
    
    @Given("the tube is operating in self-sufficient mode")
    public void theTubeIsOperatingInSelfSufficientMode() {
        tube.setSurvivalStrategy(SurvivalStrategy.IMMORTALITY);
        tube.setStrategyMode(StrategyMode.SELF_SUFFICIENCY);
        LOGGER.info("Tube is operating in self-sufficient mode");
    }
    
    @Given("a tube with previous reproduction experiences")
    public void aTubeWithPreviousReproductionExperiences() {
        tube = createTubeInState("MATURE");
        LOGGER.info("Created tube with reproduction history");
    }
    
    @Given("the tube has recorded the following reproduction outcomes:")
    public void theTubeHasRecordedTheFollowingReproductionOutcomes(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            int attempt = Integer.parseInt(row.get("attempt"));
            String environmentalConditions = row.get("environmentalConditions");
            String strategy = row.get("strategy");
            String outcome = row.get("outcome");
            double successRate = Double.parseDouble(row.get("successRate").replace("%", "")) / 100.0;
            
            ReproductionOutcome reproductionOutcome = new ReproductionOutcome(
                    attempt, environmentalConditions, strategy, outcome, successRate);
            reproductionHistory.add(reproductionOutcome);
            tube.addReproductionOutcome(reproductionOutcome);
        }
        
        LOGGER.info("Added " + reproductionHistory.size() + " reproduction outcomes");
    }
    
    @When("the tube evaluates its survival strategy")
    public void theTubeEvaluatesItsSurvivalStrategy() {
        selectedStrategy = tube.evaluateSurvivalStrategy(environment);
        selectedMode = tube.getStrategyMode();
        decisionConfidence = tube.getDecisionConfidence();
        LOGGER.info("Tube selected strategy: " + selectedStrategy + " with mode: " + selectedMode);
    }
    
    @When("the environment changes to favorable conditions:")
    public void theEnvironmentChangesToFavorableConditions(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String factorName = row.get("factor");
            int oldValue = Integer.parseInt(row.get("oldValue"));
            int newValue = Integer.parseInt(row.get("newValue"));
            int threshold = Integer.parseInt(row.get("threshold"));
            String newClassification = row.get("newClassification");
            
            // Verify old value matches expected
            EnvironmentalFactor currentFactor = environmentalFactors.get(factorName);
            Assertions.assertEquals(oldValue, currentFactor.getValue(), 
                    "Current factor value doesn't match expected old value");
            
            // Update factor with new value
            EnvironmentalFactor updatedFactor = new EnvironmentalFactor(
                    factorName, newValue, threshold, newClassification);
            environmentalFactors.put(factorName, updatedFactor);
            environment.updateFactor(updatedFactor);
        }
        
        LOGGER.info("Updated environment to favorable conditions");
    }
    
    @When("the environment changes to harsh conditions:")
    public void theEnvironmentChangesToHarshConditions(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String factorName = row.get("factor");
            int oldValue = Integer.parseInt(row.get("oldValue"));
            int newValue = Integer.parseInt(row.get("newValue"));
            int threshold = Integer.parseInt(row.get("threshold"));
            String newClassification = row.get("newClassification");
            
            // Verify old value matches expected
            EnvironmentalFactor currentFactor = environmentalFactors.get(factorName);
            Assertions.assertEquals(oldValue, currentFactor.getValue(), 
                    "Current factor value doesn't match expected old value");
            
            // Update factor with new value
            EnvironmentalFactor updatedFactor = new EnvironmentalFactor(
                    factorName, newValue, threshold, newClassification);
            environmentalFactors.put(factorName, updatedFactor);
            environment.updateFactor(updatedFactor);
        }
        
        LOGGER.info("Updated environment to harsh conditions");
    }
    
    @When("the tube reevaluates its survival strategy")
    public void theTubeReevaluatesItsSurvivalStrategy() {
        SurvivalStrategy previousStrategy = tube.getSurvivalStrategy();
        selectedStrategy = tube.evaluateSurvivalStrategy(environment);
        selectedMode = tube.getStrategyMode();
        
        LOGGER.info("Tube reevaluated strategy from " + previousStrategy + 
                " to " + selectedStrategy + " with mode: " + selectedMode);
    }
    
    @When("the tube initiates reproduction")
    public void theTubeInitiatesReproduction() {
        tube.initiateReproduction();
        completionPercentage = 100;
        LOGGER.info("Tube initiated reproduction process");
    }
    
    @When("the tube operates in harsh environment for an extended period")
    public void theTubeOperatesInHarshEnvironmentForAnExtendedPeriod() {
        // Simulate extended operation period in harsh environment
        tube.setSurvivalStrategy(SurvivalStrategy.IMMORTALITY);
        tube.setStrategyMode(StrategyMode.SELF_SUFFICIENCY);
        
        // Enable self-optimization mechanisms
        tube.enableMechanism("resourceConservation", "minimize resource usage", "high");
        tube.enableMechanism("errorTolerance", "operate despite partial failures", "medium");
        tube.enableMechanism("stateCompression", "reduce memory footprint", "medium");
        tube.enableMechanism("threatAvoidance", "detect and evade threats", "high");
        tube.enableMechanism("cyclicRejuvenation", "clear accumulated runtime artifacts", "low");
        
        enabledMechanisms = tube.getEnabledMechanisms();
        LOGGER.info("Tube has operated in harsh environment and developed " + 
                enabledMechanisms.size() + " optimization mechanisms");
    }
    
    @When("the reproduction process is interrupted at {int}% completion")
    public void theReproductionProcessIsInterruptedAtCompletion(int percentage) {
        completionPercentage = percentage / 100.0;
        reproductionInterrupted = true;
        tube.interruptReproduction(completionPercentage);
        LOGGER.info("Reproduction process interrupted at " + percentage + "% completion");
    }
    
    @When("critical resources are depleted below survival threshold")
    public void criticalResourcesAreDepletedBelowSurvivalThreshold() {
        EnvironmentalFactor resourceFactor = environmentalFactors.get("resourceAvailability");
        resourceFactor.setValue(1); // Critical depletion
        environment.updateFactor(resourceFactor);
        
        tube.handleResourceDepletion();
        LOGGER.info("Critical resources depleted below survival threshold");
    }
    
    @When("the tube evaluates its reproductive strategy in moderately favorable conditions")
    public void theTubeEvaluatesItsReproductiveStrategyInModeratelyFavorableConditions() {
        // Setup moderately favorable environment
        environmentalFactors.put("resourceAvailability", new EnvironmentalFactor("resourceAvailability", 6, 5, "abundant"));
        environmentalFactors.put("environmentalStability", new EnvironmentalFactor("environmentalStability", 6, 6, "stable"));
        environmentalFactors.put("hostilityLevel", new EnvironmentalFactor("hostilityLevel", 4, 5, "moderate"));
        
        for (EnvironmentalFactor factor : environmentalFactors.values()) {
            environment.updateFactor(factor);
        }
        
        tube.adaptReproductionStrategy(reproductionHistory, environment);
        LOGGER.info("Tube evaluated reproductive strategy based on past outcomes");
    }
    
    @Then("the tube should choose {string} strategy")
    public void theTubeShouldChooseStrategy(String strategy) {
        Assertions.assertEquals(SurvivalStrategy.valueOf(strategy), selectedStrategy,
                "Tube did not select the expected strategy");
        LOGGER.info("Verified tube selected " + strategy + " strategy");
    }
    
    @Then("the tube should enter {string} mode")
    public void theTubeShouldEnterMode(String mode) {
        Assertions.assertEquals(StrategyMode.valueOf(mode), selectedMode,
                "Tube did not enter the expected mode");
        LOGGER.info("Verified tube entered " + mode + " mode");
    }
    
    @Then("the tube should switch to {string} strategy")
    public void theTubeShouldSwitchToStrategy(String strategy) {
        Assertions.assertEquals(SurvivalStrategy.valueOf(strategy), selectedStrategy,
                "Tube did not switch to the expected strategy");
        LOGGER.info("Verified tube switched to " + strategy + " strategy");
    }
    
    @Then("the tube should detect the interruption")
    public void theTubeShouldDetectTheInterruption() {
        Assertions.assertTrue(tube.isReproductionInterrupted(), 
                "Tube did not detect the reproduction interruption");
        LOGGER.info("Verified tube detected reproduction interruption");
    }
    
    @Then("the tube should enter {string} state")
    public void theTubeShouldEnterState(String state) {
        Assertions.assertEquals(state, tube.getLifecycleState().toString(),
                "Tube did not enter the expected state");
        LOGGER.info("Verified tube entered " + state + " state");
    }
    
    @Then("the tube should develop the following self-optimization mechanisms:")
    public void theTubeShouldDevelopTheFollowingSelfOptimizationMechanisms(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        for (Map<String, String> row : rows) {
            String mechanismName = row.get("mechanism");
            String purpose = row.get("purpose");
            String adaptationRate = row.get("adaptationRate");
            
            Assertions.assertTrue(tube.hasMechanism(mechanismName),
                    "Tube does not have the expected mechanism: " + mechanismName);
            
            Assertions.assertEquals(purpose, tube.getMechanismPurpose(mechanismName),
                    "Mechanism purpose does not match expected");
            
            Assertions.assertEquals(adaptationRate, tube.getMechanismAdaptationRate(mechanismName),
                    "Mechanism adaptation rate does not match expected");
        }
        
        LOGGER.info("Verified all expected self-optimization mechanisms are present");
    }
    
    @Then("the decision confidence should be {word}")
    public void theDecisionConfidenceShouldBe(String confidence) {
        double expectedConfidence = 0.0;
        switch (confidence) {
            case "high":
                expectedConfidence = 0.8;
                break;
            case "medium":
                expectedConfidence = 0.5;
                break;
            case "low":
                expectedConfidence = 0.3;
                break;
        }
        
        Assertions.assertTrue(Math.abs(decisionConfidence - expectedConfidence) <= 0.2,
                "Decision confidence " + decisionConfidence + " does not match expected " + confidence);
        
        LOGGER.info("Verified decision confidence is " + confidence);
    }
    
    @And("the tube should optimize resource consumption")
    public void theTubeShouldOptimizeResourceConsumption() {
        Assertions.assertTrue(tube.isResourceOptimizationEnabled(),
                "Tube did not enable resource optimization");
        LOGGER.info("Verified tube optimized resource consumption");
    }
    
    @And("the tube should enable self-repair mechanisms")
    public void theTubeShouldEnableSelfRepairMechanisms() {
        Assertions.assertTrue(tube.isSelfRepairEnabled(),
                "Tube did not enable self-repair mechanisms");
        LOGGER.info("Verified tube enabled self-repair mechanisms");
    }
    
    @And("the decision should be logged with environmental factors")
    public void theDecisionShouldBeLoggedWithEnvironmentalFactors() {
        Assertions.assertTrue(tube.isStrategyDecisionLogged(),
                "Tube did not log strategy decision with environmental factors");
        LOGGER.info("Verified strategy decision was logged with factors");
    }
    
    @And("the tube should prepare for offspring creation")
    public void theTubeShouldPrepareForOffspringCreation() {
        Assertions.assertTrue(tube.isOffspringPreparationEnabled(),
                "Tube did not prepare for offspring creation");
        LOGGER.info("Verified tube prepared for offspring creation");
    }
    
    @And("the tube should organize knowledge transfer structures")
    public void theTubeShouldOrganizeKnowledgeTransferStructures() {
        Assertions.assertTrue(tube.areKnowledgeTransferStructuresOrganized(),
                "Tube did not organize knowledge transfer structures");
        LOGGER.info("Verified tube organized knowledge transfer structures");
    }
    
    @And("the tube should allocate resources for reproduction")
    public void theTubeShouldAllocateResourcesForReproduction() {
        Assertions.assertTrue(tube.areResourcesAllocatedForReproduction(),
                "Tube did not allocate resources for reproduction");
        LOGGER.info("Verified tube allocated resources for reproduction");
    }
    
    @And("the tube should prepare for reproduction phase")
    public void theTubeShouldPrepareForReproductionPhase() {
        Assertions.assertTrue(tube.isInReproductionPreparationPhase(),
                "Tube did not prepare for reproduction phase");
        LOGGER.info("Verified tube prepared for reproduction phase");
    }
    
    @And("the tube should log the strategy change with reasons")
    public void theTubeShouldLogTheStrategyChangeWithReasons() {
        Assertions.assertTrue(tube.isStrategyChangeLogged(),
                "Tube did not log strategy change with reasons");
        LOGGER.info("Verified tube logged strategy change with reasons");
    }
    
    @And("the tube should abort reproduction preparations")
    public void theTubeShouldAbortReproductionPreparations() {
        Assertions.assertFalse(tube.isOffspringPreparationEnabled(),
                "Tube did not abort reproduction preparations");
        LOGGER.info("Verified tube aborted reproduction preparations");
    }
    
    @And("the tube should reallocate resources to self-maintenance")
    public void theTubeShouldReallocateResourcesToSelfMaintenance() {
        Assertions.assertTrue(tube.areResourcesReallocatedToSelfMaintenance(),
                "Tube did not reallocate resources to self-maintenance");
        LOGGER.info("Verified tube reallocated resources to self-maintenance");
    }
    
    @And("knowledge transfer should occur in priority order")
    public void knowledgeTransferShouldOccurInPriorityOrder() {
        Assertions.assertTrue(tube.isKnowledgeTransferringInPriorityOrder(),
                "Tube is not transferring knowledge in priority order");
        LOGGER.info("Verified knowledge transfer occurs in priority order");
    }
    
    @And("essential knowledge should be transferred with verification")
    public void essentialKnowledgeShouldBeTransferredWithVerification() {
        Assertions.assertTrue(tube.isEssentialKnowledgeTransferredWithVerification(),
                "Tube is not transferring essential knowledge with verification");
        LOGGER.info("Verified essential knowledge is transferred with verification");
    }
    
    @And("the offspring should receive all high-priority knowledge")
    public void theOffspringShouldReceiveAllHighPriorityKnowledge() {
        Assertions.assertTrue(tube.isHighPriorityKnowledgeTransferred(),
                "Offspring did not receive all high-priority knowledge");
        LOGGER.info("Verified offspring received all high-priority knowledge");
    }
    
    @And("the reproduction process should be monitored and logged")
    public void theReproductionProcessShouldBeMonitoredAndLogged() {
        Assertions.assertTrue(tube.isReproductionProcessLogged(),
                "Reproduction process is not being monitored and logged");
        LOGGER.info("Verified reproduction process is monitored and logged");
    }
    
    @And("the parent tube should transition to {string} state after successful transfer")
    public void theParentTubeShouldTransitionToStateAfterSuccessfulTransfer(String state) {
        Assertions.assertEquals(state, tube.getLifecycleState().toString(),
                "Parent tube did not transition to the expected state");
        LOGGER.info("Verified parent tube transitioned to " + state + " state");
    }
    
    @And("each mechanism should be measurable for effectiveness")
    public void eachMechanismShouldBeMeasurableForEffectiveness() {
        for (String mechanism : enabledMechanisms) {
            Assertions.assertTrue(tube.isMechanismEffectivenessMeasurable(mechanism),
                    "Mechanism is not measurable for effectiveness: " + mechanism);
        }
        LOGGER.info("Verified all mechanisms are measurable for effectiveness");
    }
    
    @And("the tube should prioritize mechanisms based on environmental threats")
    public void theTubeShouldPrioritizeMechanismsBasedOnEnvironmentalThreats() {
        Assertions.assertTrue(tube.areMechanismsPrioritizedByThreat(),
                "Tube is not prioritizing mechanisms based on environmental threats");
        LOGGER.info("Verified mechanisms are prioritized based on threats");
    }
    
    @And("long-term survival rate should improve with mechanism maturity")
    public void longTermSurvivalRateShouldImproveWithMechanismMaturity() {
        Assertions.assertTrue(tube.doesSurvivalRateImproveWithMechanismMaturity(),
                "Long-term survival rate does not improve with mechanism maturity");
        LOGGER.info("Verified survival rate improves with mechanism maturity");
    }
    
    @And("the decision should be logged with all factors")
    public void theDecisionShouldBeLoggedWithAllFactors() {
        Assertions.assertTrue(tube.isStrategyDecisionLoggedWithAllFactors(),
                "Decision is not logged with all factors");
        LOGGER.info("Verified decision is logged with all factors");
    }
    
    @And("the tube should attempt to complete knowledge transfer if possible")
    public void theTubeShouldAttemptToCompleteKnowledgeTransferIfPossible() {
        Assertions.assertTrue(tube.didAttemptToCompleteKnowledgeTransfer(),
                "Tube did not attempt to complete knowledge transfer");
        LOGGER.info("Verified tube attempted to complete knowledge transfer");
    }
    
    @And("if completion is not possible, the tube should revert to {string} strategy")
    public void ifCompletionIsNotPossibleTheTubeShouldRevertToStrategy(String strategy) {
        // Only check if completion wasn't possible
        if (!tube.wasKnowledgeTransferCompleted()) {
            Assertions.assertEquals(SurvivalStrategy.valueOf(strategy), tube.getSurvivalStrategy(),
                    "Tube did not revert to the expected strategy");
            LOGGER.info("Verified tube reverted to " + strategy + " strategy");
        }
    }
    
    @And("partial offspring should be terminated with resource recovery")
    public void partialOffshouldShouldBeTerminatedWithResourceRecovery() {
        if (!tube.wasKnowledgeTransferCompleted()) {
            Assertions.assertTrue(tube.werePartialOffspringTerminated(),
                    "Partial offspring were not terminated");
            Assertions.assertTrue(tube.wereResourcesRecovered(),
                    "Resources were not recovered from terminated offspring");
            LOGGER.info("Verified partial offspring terminated with resource recovery");
        }
    }
    
    @And("the tube should log the reproduction failure with diagnostics")
    public void theTubeShouldLogTheReproductionFailureWithDiagnostics() {
        if (!tube.wasKnowledgeTransferCompleted()) {
            Assertions.assertTrue(tube.isReproductionFailureLogged(),
                    "Reproduction failure was not logged with diagnostics");
            LOGGER.info("Verified reproduction failure logged with diagnostics");
        }
    }
    
    @And("non-essential functions should be suspended")
    public void nonEssentialFunctionsShouldBeSuspended() {
        Assertions.assertTrue(tube.areNonEssentialFunctionsSuspended(),
                "Non-essential functions were not suspended");
        LOGGER.info("Verified non-essential functions are suspended");
    }
    
    @And("the tube should establish minimum viability thresholds")
    public void theTubeShouldEstablishMinimumViabilityThresholds() {
        Assertions.assertTrue(tube.areMinimumViabilityThresholdsEstablished(),
                "Minimum viability thresholds were not established");
        LOGGER.info("Verified minimum viability thresholds established");
    }
    
    @And("the tube should continuously monitor for improved conditions")
    public void theTubeShouldContinuouslyMonitorForImprovedConditions() {
        Assertions.assertTrue(tube.isMonitoringForImprovedConditions(),
                "Tube is not monitoring for improved conditions");
        LOGGER.info("Verified tube is monitoring for improved conditions");
    }
    
    @And("recovery procedures should be prepared for resource availability")
    public void recoveryProceduresShouldBePreparedForResourceAvailability() {
        Assertions.assertTrue(tube.areRecoveryProceduresPrepared(),
                "Recovery procedures are not prepared");
        LOGGER.info("Verified recovery procedures are prepared");
    }
    
    @And("the tube should adapt its strategy based on past outcomes")
    public void theTubeShouldAdaptItsStrategyBasedOnPastOutcomes() {
        Assertions.assertTrue(tube.isStrategyAdaptedFromPastOutcomes(),
                "Tube strategy is not adapted based on past outcomes");
        LOGGER.info("Verified tube adapts strategy based on past outcomes");
    }
    
    @And("the tube should optimize knowledge transfer priorities")
    public void theTubeShouldOptimizeKnowledgeTransferPriorities() {
        Assertions.assertTrue(tube.areKnowledgeTransferPrioritiesOptimized(),
                "Knowledge transfer priorities are not optimized");
        LOGGER.info("Verified knowledge transfer priorities are optimized");
    }
    
    @And("the reproduction threshold should be adjusted based on success rate")
    public void theReproductionThresholdShouldBeAdjustedBasedOnSuccessRate() {
        Assertions.assertTrue(tube.isReproductionThresholdAdjusted(),
                "Reproduction threshold is not adjusted based on success rate");
        LOGGER.info("Verified reproduction threshold is adjusted based on success rate");
    }
    
    @And("the tube should implement safeguards for potential interruptions")
    public void theTubeShouldImplementSafeguardsForPotentialInterruptions() {
        Assertions.assertTrue(tube.areSafeguardsImplementedForInterruptions(),
                "Safeguards are not implemented for potential interruptions");
        LOGGER.info("Verified safeguards are implemented for potential interruptions");
    }
    
    // Helper methods
    private Tube createTubeInState(String state) {
        Tube tube = new Tube();
        tube.setLifecycleState(LifecycleState.valueOf(state));
        return tube;
    }
}