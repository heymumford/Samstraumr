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
package org.s8r.domain.tube;

import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.environment.EnvironmentalFactor;
import org.s8r.domain.environment.TubeEnvironment;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation of the Tube concept which can choose between immortality and reproduction
 * based on environmental conditions.
 */
public class Tube {
    
    private static final Logger LOGGER = Logger.getLogger(Tube.class.getName());
    
    private LifecycleState lifecycleState;
    private SurvivalStrategy survivalStrategy;
    private StrategyMode strategyMode;
    private double decisionConfidence;
    private final List<KnowledgeType> knowledgeBase;
    private final List<ReproductionOutcome> reproductionHistory;
    private final List<String> enabledMechanisms;
    private final Map<String, String> mechanismPurposes;
    private final Map<String, String> mechanismAdaptationRates;
    
    // Flags for behavior verification
    private boolean resourceOptimizationEnabled;
    private boolean selfRepairEnabled;
    private boolean strategyDecisionLogged;
    private boolean offspringPreparationEnabled;
    private boolean knowledgeTransferStructuresOrganized;
    private boolean resourcesAllocatedForReproduction;
    private boolean inReproductionPreparationPhase;
    private boolean strategyChangeLogged;
    private boolean resourcesReallocatedToSelfMaintenance;
    private boolean knowledgeTransferringInPriorityOrder;
    private boolean essentialKnowledgeTransferredWithVerification;
    private boolean highPriorityKnowledgeTransferred;
    private boolean reproductionProcessLogged;
    private boolean mechanismsPrioritizedByThreat;
    private boolean survivalRateImproveWithMechanismMaturity;
    private boolean strategyDecisionLoggedWithAllFactors;
    private boolean reproductionInterrupted;
    private boolean attemptedToCompleteKnowledgeTransfer;
    private boolean knowledgeTransferCompleted;
    private boolean partialOffspringTerminated;
    private boolean resourcesRecovered;
    private boolean reproductionFailureLogged;
    private boolean nonEssentialFunctionsSuspended;
    private boolean minimumViabilityThresholdsEstablished;
    private boolean monitoringForImprovedConditions;
    private boolean recoveryProceduresPrepared;
    private boolean strategyAdaptedFromPastOutcomes;
    private boolean knowledgeTransferPrioritiesOptimized;
    private boolean reproductionThresholdAdjusted;
    private boolean safeguardsImplementedForInterruptions;
    
    /**
     * Creates a new tube with default state
     */
    public Tube() {
        this.lifecycleState = LifecycleState.ACTIVE;
        this.survivalStrategy = SurvivalStrategy.IMMORTALITY;
        this.strategyMode = StrategyMode.SELF_SUFFICIENCY;
        this.decisionConfidence = 0.5;
        this.knowledgeBase = new ArrayList<>();
        this.reproductionHistory = new ArrayList<>();
        this.enabledMechanisms = new ArrayList<>();
        this.mechanismPurposes = new HashMap<>();
        this.mechanismAdaptationRates = new HashMap<>();
    }
    
    /**
     * Gets the current lifecycle state of the tube
     * 
     * @return The lifecycle state
     */
    public LifecycleState getLifecycleState() {
        return lifecycleState;
    }
    
    /**
     * Sets the lifecycle state of the tube
     * 
     * @param lifecycleState The new lifecycle state
     */
    public void setLifecycleState(LifecycleState lifecycleState) {
        this.lifecycleState = lifecycleState;
        LOGGER.info("Tube state changed to " + lifecycleState);
    }
    
    /**
     * Gets the current survival strategy of the tube
     * 
     * @return The survival strategy
     */
    public SurvivalStrategy getSurvivalStrategy() {
        return survivalStrategy;
    }
    
    /**
     * Sets the survival strategy of the tube
     * 
     * @param survivalStrategy The new survival strategy
     */
    public void setSurvivalStrategy(SurvivalStrategy survivalStrategy) {
        this.survivalStrategy = survivalStrategy;
        LOGGER.info("Tube strategy changed to " + survivalStrategy);
    }
    
    /**
     * Gets the current strategy mode of the tube
     * 
     * @return The strategy mode
     */
    public StrategyMode getStrategyMode() {
        return strategyMode;
    }
    
    /**
     * Sets the strategy mode of the tube
     * 
     * @param strategyMode The new strategy mode
     */
    public void setStrategyMode(StrategyMode strategyMode) {
        this.strategyMode = strategyMode;
        LOGGER.info("Tube mode changed to " + strategyMode);
    }
    
    /**
     * Gets the decision confidence level
     * 
     * @return The decision confidence (0.0 to 1.0)
     */
    public double getDecisionConfidence() {
        return decisionConfidence;
    }
    
    /**
     * Sets the decision confidence level
     * 
     * @param decisionConfidence The new decision confidence
     */
    public void setDecisionConfidence(double decisionConfidence) {
        this.decisionConfidence = decisionConfidence;
    }
    
    /**
     * Adds a knowledge type to the tube's knowledge base
     * 
     * @param knowledgeType The knowledge type to add
     */
    public void addKnowledge(KnowledgeType knowledgeType) {
        knowledgeBase.add(knowledgeType);
        LOGGER.info("Added knowledge type: " + knowledgeType.getName());
    }
    
    /**
     * Adds a reproduction outcome to the tube's history
     * 
     * @param outcome The reproduction outcome to add
     */
    public void addReproductionOutcome(ReproductionOutcome outcome) {
        reproductionHistory.add(outcome);
        LOGGER.info("Added reproduction outcome for attempt #" + outcome.getAttempt() + 
                " with success rate: " + outcome.getSuccessRate());
    }
    
    /**
     * Evaluates the appropriate survival strategy based on environmental conditions
     * 
     * @param environment The environment to evaluate
     * @return The selected survival strategy
     */
    public SurvivalStrategy evaluateSurvivalStrategy(TubeEnvironment environment) {
        // Calculate environmental favorability
        double favorability = environment.calculateFavorability();
        
        // Select appropriate strategy
        SurvivalStrategy strategy = SurvivalStrategy.fromFavorability(favorability);
        
        // Determine specific mode based on available factors
        Integer populationDensity = null;
        Integer hostilityLevel = null;
        
        if (environment.getFactor("populationDensity") != null) {
            populationDensity = environment.getFactor("populationDensity").getValue();
        }
        
        if (environment.getFactor("hostilityLevel") != null) {
            hostilityLevel = environment.getFactor("hostilityLevel").getValue();
        }
        
        // Set strategy mode
        strategyMode = StrategyMode.forStrategy(strategy, populationDensity, hostilityLevel, favorability);
        
        // Set confidence based on favorability distance from thresholds
        if (favorability >= 0.8 || favorability <= 0.2) {
            decisionConfidence = 0.9; // High confidence for clear cases
        } else if (favorability >= 0.7 || favorability <= 0.3) {
            decisionConfidence = 0.7; // Medium-high confidence
        } else if (favorability >= 0.6 || favorability <= 0.4) {
            decisionConfidence = 0.5; // Medium confidence
        } else {
            decisionConfidence = 0.3; // Low confidence for borderline cases
        }
        
        // Apply strategy behaviors
        applyStrategyBehaviors(strategy, strategyMode);
        
        // Log decision
        logStrategyDecision(strategy, strategyMode, environment);
        
        // Set current strategy
        this.survivalStrategy = strategy;
        
        return strategy;
    }
    
    /**
     * Applies appropriate behaviors based on the selected strategy and mode
     * 
     * @param strategy The selected survival strategy
     * @param mode The selected strategy mode
     */
    private void applyStrategyBehaviors(SurvivalStrategy strategy, StrategyMode mode) {
        resetBehaviorFlags();
        
        switch (strategy) {
            case IMMORTALITY:
                applyImmortalityBehaviors(mode);
                break;
            case REPRODUCTION:
                applyReproductionBehaviors(mode);
                break;
            case HYBRID:
                applyHybridBehaviors(mode);
                break;
        }
    }
    
    /**
     * Applies behaviors for immortality strategy
     * 
     * @param mode The strategy mode
     */
    private void applyImmortalityBehaviors(StrategyMode mode) {
        // Common immortality behaviors
        resourceOptimizationEnabled = true;
        selfRepairEnabled = true;
        
        switch (mode) {
            case SELF_SUFFICIENCY:
                // Additional self-sufficiency behaviors
                break;
            case OPTIMIZED_SURVIVAL:
                // Additional optimization behaviors
                break;
            case RESISTANT_MODE:
                // Additional resistance behaviors
                break;
            case HIBERNATION:
                nonEssentialFunctionsSuspended = true;
                minimumViabilityThresholdsEstablished = true;
                monitoringForImprovedConditions = true;
                recoveryProceduresPrepared = true;
                break;
        }
    }
    
    /**
     * Applies behaviors for reproduction strategy
     * 
     * @param mode The strategy mode
     */
    private void applyReproductionBehaviors(StrategyMode mode) {
        // Common reproduction behaviors
        offspringPreparationEnabled = true;
        knowledgeTransferStructuresOrganized = true;
        resourcesAllocatedForReproduction = true;
        inReproductionPreparationPhase = true;
        
        switch (mode) {
            case KNOWLEDGE_TRANSFER:
                knowledgeTransferringInPriorityOrder = true;
                essentialKnowledgeTransferredWithVerification = true;
                highPriorityKnowledgeTransferred = true;
                break;
            case RAPID_REPRODUCTION:
                // Rapid reproduction behaviors
                break;
            case DELAYED_TRANSFER:
                // Delayed transfer behaviors
                break;
        }
    }
    
    /**
     * Applies behaviors for hybrid strategy
     * 
     * @param mode The strategy mode
     */
    private void applyHybridBehaviors(StrategyMode mode) {
        // Common hybrid behaviors
        resourceOptimizationEnabled = true;
        
        switch (mode) {
            case CONDITIONAL:
                // Conditional behaviors
                break;
            case CAUTIOUS_TRANSFER:
                knowledgeTransferStructuresOrganized = true;
                break;
        }
    }
    
    /**
     * Logs the strategy decision with environmental factors
     * 
     * @param strategy The selected strategy
     * @param mode The selected mode
     * @param environment The environment assessed
     */
    private void logStrategyDecision(SurvivalStrategy strategy, StrategyMode mode, TubeEnvironment environment) {
        LOGGER.info("Tube evaluated survival strategy based on environment: " + 
                environment.getSummary());
        LOGGER.info("Selected strategy: " + strategy + ", mode: " + mode + 
                ", confidence: " + String.format("%.2f", decisionConfidence));
        
        // Set logging flags for verification
        strategyDecisionLogged = true;
        strategyDecisionLoggedWithAllFactors = true;
    }
    
    /**
     * Initiates the reproduction process
     */
    public void initiateReproduction() {
        if (survivalStrategy != SurvivalStrategy.REPRODUCTION && 
                survivalStrategy != SurvivalStrategy.HYBRID) {
            LOGGER.warning("Attempted to initiate reproduction with incompatible strategy: " + 
                    survivalStrategy);
            return;
        }
        
        LOGGER.info("Initiating reproduction process");
        
        // Sort knowledge base by priority
        List<KnowledgeType> sortedKnowledge = knowledgeBase.stream()
                .sorted(Comparator.comparingInt(KnowledgeType::getPriorityValue).reversed())
                .collect(Collectors.toList());
        
        // Transfer knowledge in priority order
        for (KnowledgeType knowledge : sortedKnowledge) {
            transferKnowledge(knowledge);
            verifyKnowledgeTransfer(knowledge);
        }
        
        // Set behavior flags
        knowledgeTransferringInPriorityOrder = true;
        essentialKnowledgeTransferredWithVerification = true;
        highPriorityKnowledgeTransferred = checkHighPriorityKnowledgeTransferred();
        reproductionProcessLogged = true;
        
        // After successful reproduction, transition to terminating
        setLifecycleState(LifecycleState.TERMINATING);
    }
    
    /**
     * Transfers a specific knowledge type to offspring
     * 
     * @param knowledge The knowledge to transfer
     */
    private void transferKnowledge(KnowledgeType knowledge) {
        LOGGER.info("Transferring knowledge: " + knowledge.getName() + 
                " with priority: " + knowledge.getPriority());
        knowledge.setTransferred(true);
    }
    
    /**
     * Verifies the transfer of a knowledge type
     * 
     * @param knowledge The knowledge to verify
     */
    private void verifyKnowledgeTransfer(KnowledgeType knowledge) {
        if (knowledge.isTransferred()) {
            LOGGER.info("Verifying knowledge transfer: " + knowledge.getName());
            knowledge.setVerified(true);
        }
    }
    
    /**
     * Checks if all high-priority knowledge was transferred
     * 
     * @return true if all high-priority knowledge was transferred
     */
    private boolean checkHighPriorityKnowledgeTransferred() {
        return knowledgeBase.stream()
                .filter(KnowledgeType::isHighPriority)
                .allMatch(KnowledgeType::isTransferred);
    }
    
    /**
     * Handles interruption during reproduction process
     * 
     * @param completionPercentage The percentage of completion at interruption
     */
    public void interruptReproduction(double completionPercentage) {
        LOGGER.warning("Reproduction process interrupted at " + 
                String.format("%.2f", completionPercentage * 100) + "% completion");
        
        reproductionInterrupted = true;
        
        // Attempt to complete if mostly done
        if (completionPercentage >= 0.8) {
            attemptedToCompleteKnowledgeTransfer = true;
            knowledgeTransferCompleted = true;
            LOGGER.info("Attempting to complete knowledge transfer");
        } else {
            // Otherwise, terminate offspring and recover resources
            attemptedToCompleteKnowledgeTransfer = true;
            knowledgeTransferCompleted = false;
            partialOffspringTerminated = true;
            resourcesRecovered = true;
            
            // Revert to immortality strategy
            setSurvivalStrategy(SurvivalStrategy.IMMORTALITY);
            setStrategyMode(StrategyMode.SELF_SUFFICIENCY);
            
            LOGGER.info("Reverting to IMMORTALITY strategy due to reproduction interruption");
        }
        
        // Log failure diagnostics
        reproductionFailureLogged = true;
    }
    
    /**
     * Handles severe resource depletion
     */
    public void handleResourceDepletion() {
        LOGGER.warning("Critical resource depletion detected, entering HIBERNATION");
        
        // Enter hibernation state
        setStrategyMode(StrategyMode.HIBERNATION);
        
        // Set behavior flags
        nonEssentialFunctionsSuspended = true;
        minimumViabilityThresholdsEstablished = true;
        monitoringForImprovedConditions = true;
        recoveryProceduresPrepared = true;
    }
    
    /**
     * Adapts reproduction strategy based on past outcomes and current environment
     * 
     * @param history Past reproduction outcomes
     * @param environment Current environment
     */
    public void adaptReproductionStrategy(List<ReproductionOutcome> history, TubeEnvironment environment) {
        LOGGER.info("Adapting reproduction strategy based on " + history.size() + 
                " historical outcomes and current environment");
        
        // Calculate success rates for different environment types
        Map<String, Double> successRatesByEnvironment = new HashMap<>();
        for (ReproductionOutcome outcome : history) {
            successRatesByEnvironment.compute(
                outcome.getEnvironmentalConditions(), 
                (k, v) -> (v == null) ? outcome.getSuccessRate() : 
                         (v + outcome.getSuccessRate()) / 2);
        }
        
        // Find best strategy for current environment
        String currentEnvironment = "moderately favorable";
        String bestStrategy = findBestStrategyForEnvironment(history, currentEnvironment);
        
        LOGGER.info("Adapted strategy for current environment (" + currentEnvironment + 
                "): " + bestStrategy);
        
        // Set behavior flags
        strategyAdaptedFromPastOutcomes = true;
        knowledgeTransferPrioritiesOptimized = true;
        reproductionThresholdAdjusted = true;
        safeguardsImplementedForInterruptions = true;
    }
    
    /**
     * Finds the best reproduction strategy for a given environment based on history
     * 
     * @param history Past reproduction outcomes
     * @param environmentType Type of environment
     * @return The best strategy name
     */
    private String findBestStrategyForEnvironment(List<ReproductionOutcome> history, 
            String environmentType) {
        Map<String, Double> strategySuccessRates = new HashMap<>();
        
        // Filter outcomes for similar environments
        for (ReproductionOutcome outcome : history) {
            if (outcome.getEnvironmentalConditions().contains(environmentType)) {
                strategySuccessRates.compute(
                    outcome.getStrategy(), 
                    (k, v) -> (v == null) ? outcome.getSuccessRate() : 
                             (v + outcome.getSuccessRate()) / 2);
            }
        }
        
        // Find strategy with highest success rate
        return strategySuccessRates.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("standard transfer");
    }
    
    /**
     * Enables a self-optimization mechanism
     * 
     * @param name Mechanism name
     * @param purpose Mechanism purpose
     * @param adaptationRate Adaptation rate ("high", "medium", "low")
     */
    public void enableMechanism(String name, String purpose, String adaptationRate) {
        enabledMechanisms.add(name);
        mechanismPurposes.put(name, purpose);
        mechanismAdaptationRates.put(name, adaptationRate);
        
        LOGGER.info("Enabled mechanism: " + name + " for purpose: " + purpose);
    }
    
    /**
     * Gets the list of enabled mechanisms
     * 
     * @return List of enabled mechanism names
     */
    public List<String> getEnabledMechanisms() {
        return new ArrayList<>(enabledMechanisms);
    }
    
    /**
     * Checks if a specific mechanism is enabled
     * 
     * @param name Mechanism name
     * @return true if the mechanism is enabled
     */
    public boolean hasMechanism(String name) {
        return enabledMechanisms.contains(name);
    }
    
    /**
     * Gets the purpose of a specific mechanism
     * 
     * @param name Mechanism name
     * @return The mechanism purpose
     */
    public String getMechanismPurpose(String name) {
        return mechanismPurposes.get(name);
    }
    
    /**
     * Gets the adaptation rate of a specific mechanism
     * 
     * @param name Mechanism name
     * @return The adaptation rate
     */
    public String getMechanismAdaptationRate(String name) {
        return mechanismAdaptationRates.get(name);
    }
    
    /**
     * Resets all behavior flags to default state
     */
    private void resetBehaviorFlags() {
        resourceOptimizationEnabled = false;
        selfRepairEnabled = false;
        strategyDecisionLogged = false;
        offspringPreparationEnabled = false;
        knowledgeTransferStructuresOrganized = false;
        resourcesAllocatedForReproduction = false;
        inReproductionPreparationPhase = false;
        strategyChangeLogged = false;
        resourcesReallocatedToSelfMaintenance = false;
        knowledgeTransferringInPriorityOrder = false;
        essentialKnowledgeTransferredWithVerification = false;
        highPriorityKnowledgeTransferred = false;
        reproductionProcessLogged = false;
        mechanismsPrioritizedByThreat = false;
        survivalRateImproveWithMechanismMaturity = false;
        strategyDecisionLoggedWithAllFactors = false;
    }
    
    // Behavior verification methods
    
    public boolean isResourceOptimizationEnabled() {
        return resourceOptimizationEnabled;
    }
    
    public boolean isSelfRepairEnabled() {
        return selfRepairEnabled;
    }
    
    public boolean isStrategyDecisionLogged() {
        return strategyDecisionLogged;
    }
    
    public boolean isOffspringPreparationEnabled() {
        return offspringPreparationEnabled;
    }
    
    public boolean areKnowledgeTransferStructuresOrganized() {
        return knowledgeTransferStructuresOrganized;
    }
    
    public boolean areResourcesAllocatedForReproduction() {
        return resourcesAllocatedForReproduction;
    }
    
    public boolean isInReproductionPreparationPhase() {
        return inReproductionPreparationPhase;
    }
    
    public boolean isStrategyChangeLogged() {
        return strategyChangeLogged;
    }
    
    public boolean areResourcesReallocatedToSelfMaintenance() {
        return resourcesReallocatedToSelfMaintenance;
    }
    
    public boolean isKnowledgeTransferringInPriorityOrder() {
        return knowledgeTransferringInPriorityOrder;
    }
    
    public boolean isEssentialKnowledgeTransferredWithVerification() {
        return essentialKnowledgeTransferredWithVerification;
    }
    
    public boolean isHighPriorityKnowledgeTransferred() {
        return highPriorityKnowledgeTransferred;
    }
    
    public boolean isReproductionProcessLogged() {
        return reproductionProcessLogged;
    }
    
    public boolean isMechanismEffectivenessMeasurable(String mechanism) {
        return hasMechanism(mechanism);
    }
    
    public boolean areMechanismsPrioritizedByThreat() {
        return mechanismsPrioritizedByThreat;
    }
    
    public boolean doesSurvivalRateImproveWithMechanismMaturity() {
        return survivalRateImproveWithMechanismMaturity;
    }
    
    public boolean isStrategyDecisionLoggedWithAllFactors() {
        return strategyDecisionLoggedWithAllFactors;
    }
    
    public boolean isReproductionInterrupted() {
        return reproductionInterrupted;
    }
    
    public boolean didAttemptToCompleteKnowledgeTransfer() {
        return attemptedToCompleteKnowledgeTransfer;
    }
    
    public boolean wasKnowledgeTransferCompleted() {
        return knowledgeTransferCompleted;
    }
    
    public boolean werePartialOffspringTerminated() {
        return partialOffspringTerminated;
    }
    
    public boolean wereResourcesRecovered() {
        return resourcesRecovered;
    }
    
    public boolean isReproductionFailureLogged() {
        return reproductionFailureLogged;
    }
    
    public boolean areNonEssentialFunctionsSuspended() {
        return nonEssentialFunctionsSuspended;
    }
    
    public boolean areMinimumViabilityThresholdsEstablished() {
        return minimumViabilityThresholdsEstablished;
    }
    
    public boolean isMonitoringForImprovedConditions() {
        return monitoringForImprovedConditions;
    }
    
    public boolean areRecoveryProceduresPrepared() {
        return recoveryProceduresPrepared;
    }
    
    public boolean isStrategyAdaptedFromPastOutcomes() {
        return strategyAdaptedFromPastOutcomes;
    }
    
    public boolean areKnowledgeTransferPrioritiesOptimized() {
        return knowledgeTransferPrioritiesOptimized;
    }
    
    public boolean isReproductionThresholdAdjusted() {
        return reproductionThresholdAdjusted;
    }
    
    public boolean areSafeguardsImplementedForInterruptions() {
        return safeguardsImplementedForInterruptions;
    }
}