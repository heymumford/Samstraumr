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

package org.s8r.test.steps.alz001.mock.component;

import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.time.Instant;

/**
 * Mock implementation of an environmental factors component for Alzheimer's disease modeling.
 * 
 * <p>This component models environmental factors that influence Alzheimer's disease,
 * including lifestyle, diet, stress, social engagement, and other external factors.
 */
public class EnvironmentalFactorsComponent extends ALZ001MockComponent {
    
    /**
     * Represents a patient's environmental profile.
     */
    public static class EnvironmentalProfile {
        private final String id;
        private final Map<String, Double> factors;
        private final List<EnvironmentalEvent> events;
        private final Map<String, Object> metadata;
        
        public EnvironmentalProfile(String id) {
            this.id = id;
            this.factors = new HashMap<>();
            this.events = new ArrayList<>();
            this.metadata = new HashMap<>();
            
            // Set default factors
            setFactor("physical_activity", 0.5);
            setFactor("diet_quality", 0.5);
            setFactor("sleep_quality", 0.5);
            setFactor("stress_level", 0.5);
            setFactor("social_engagement", 0.5);
            setFactor("cognitive_stimulation", 0.5);
            setFactor("environmental_toxins", 0.5);
        }
        
        public String getId() {
            return id;
        }
        
        public Map<String, Double> getFactors() {
            return new HashMap<>(factors);
        }
        
        public Double getFactor(String factorName) {
            return factors.getOrDefault(factorName, 0.0);
        }
        
        public void setFactor(String factorName, Double value) {
            factors.put(factorName, value);
        }
        
        public void addEvent(EnvironmentalEvent event) {
            events.add(event);
        }
        
        public List<EnvironmentalEvent> getEvents() {
            return new ArrayList<>(events);
        }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
    }
    
    /**
     * Represents an environmental intervention or change.
     */
    public static class EnvironmentalEvent {
        private final String type;
        private final double time;
        private final Map<String, Double> factorChanges;
        private final Map<String, Object> metadata;
        
        public EnvironmentalEvent(String type, double time) {
            this.type = type;
            this.time = time;
            this.factorChanges = new HashMap<>();
            this.metadata = new HashMap<>();
        }
        
        public String getType() {
            return type;
        }
        
        public double getTime() {
            return time;
        }
        
        public void setFactorChange(String factorName, Double change) {
            factorChanges.put(factorName, change);
        }
        
        public Double getFactorChange(String factorName) {
            return factorChanges.getOrDefault(factorName, 0.0);
        }
        
        public Map<String, Double> getFactorChanges() {
            return new HashMap<>(factorChanges);
        }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
    }
    
    /**
     * Represents a pre-defined intervention strategy.
     */
    public static class InterventionStrategy {
        private final String name;
        private final String description;
        private final Map<String, Double> factorChanges;
        private final double adherenceRate;
        private final double dropoutRate;
        
        public InterventionStrategy(String name, String description, 
                                   double adherenceRate, double dropoutRate) {
            this.name = name;
            this.description = description;
            this.factorChanges = new HashMap<>();
            this.adherenceRate = adherenceRate;
            this.dropoutRate = dropoutRate;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setFactorChange(String factorName, Double change) {
            factorChanges.put(factorName, change);
        }
        
        public Double getFactorChange(String factorName) {
            return factorChanges.getOrDefault(factorName, 0.0);
        }
        
        public Map<String, Double> getFactorChanges() {
            return new HashMap<>(factorChanges);
        }
        
        public double getAdherenceRate() {
            return adherenceRate;
        }
        
        public double getDropoutRate() {
            return dropoutRate;
        }
    }
    
    private final Map<String, EnvironmentalProfile> profiles = new HashMap<>();
    private final Map<String, InterventionStrategy> strategies = new HashMap<>();
    private final Map<String, Double> factorWeights = new HashMap<>();
    private final Random random = new Random();
    
    /**
     * Creates a new environmental factors component with the given name.
     *
     * @param name The component name
     */
    public EnvironmentalFactorsComponent(String name) {
        super(name);
        
        // Set default factor weights (impact on disease progression)
        setFactorWeight("physical_activity", -0.2);
        setFactorWeight("diet_quality", -0.15);
        setFactorWeight("sleep_quality", -0.15);
        setFactorWeight("stress_level", 0.25);
        setFactorWeight("social_engagement", -0.1);
        setFactorWeight("cognitive_stimulation", -0.15);
        setFactorWeight("environmental_toxins", 0.15);
        
        // Create default intervention strategies
        createDefaultInterventionStrategies();
    }
    
    /**
     * Creates default intervention strategies.
     */
    private void createDefaultInterventionStrategies() {
        // Exercise intervention
        InterventionStrategy exercise = new InterventionStrategy(
            "exercise_program", 
            "Regular physical exercise program",
            0.7, 0.2);
        exercise.setFactorChange("physical_activity", 0.3);
        exercise.setFactorChange("sleep_quality", 0.1);
        exercise.setFactorChange("stress_level", -0.1);
        strategies.put(exercise.getName(), exercise);
        
        // Diet intervention
        InterventionStrategy diet = new InterventionStrategy(
            "diet_improvement", 
            "Mediterranean diet program",
            0.6, 0.3);
        diet.setFactorChange("diet_quality", 0.3);
        strategies.put(diet.getName(), diet);
        
        // Cognitive training
        InterventionStrategy cognitive = new InterventionStrategy(
            "cognitive_training", 
            "Regular cognitive stimulation activities",
            0.8, 0.1);
        cognitive.setFactorChange("cognitive_stimulation", 0.4);
        cognitive.setFactorChange("social_engagement", 0.1);
        strategies.put(cognitive.getName(), cognitive);
        
        // Stress management
        InterventionStrategy stress = new InterventionStrategy(
            "stress_management", 
            "Stress reduction techniques including meditation",
            0.5, 0.4);
        stress.setFactorChange("stress_level", -0.3);
        stress.setFactorChange("sleep_quality", 0.2);
        strategies.put(stress.getName(), stress);
        
        // Combined intervention
        InterventionStrategy combined = new InterventionStrategy(
            "combined_intervention", 
            "Comprehensive lifestyle intervention program",
            0.5, 0.3);
        combined.setFactorChange("physical_activity", 0.25);
        combined.setFactorChange("diet_quality", 0.25);
        combined.setFactorChange("cognitive_stimulation", 0.25);
        combined.setFactorChange("stress_level", -0.25);
        combined.setFactorChange("sleep_quality", 0.15);
        combined.setFactorChange("social_engagement", 0.15);
        strategies.put(combined.getName(), combined);
    }
    
    /**
     * Creates a new environmental profile.
     *
     * @param id The profile ID
     * @return The created profile
     */
    public EnvironmentalProfile createProfile(String id) {
        EnvironmentalProfile profile = new EnvironmentalProfile(id);
        profiles.put(id, profile);
        setState("PROCESSING");
        return profile;
    }
    
    /**
     * Gets an environmental profile by ID.
     *
     * @param id The profile ID
     * @return The profile, or null if not found
     */
    public EnvironmentalProfile getProfile(String id) {
        return profiles.get(id);
    }
    
    /**
     * Gets all environmental profiles.
     *
     * @return A map of profile IDs to profiles
     */
    public Map<String, EnvironmentalProfile> getAllProfiles() {
        return new HashMap<>(profiles);
    }
    
    /**
     * Creates an environmental event for a profile.
     *
     * @param profileId The profile ID
     * @param eventType The event type
     * @param time The time of the event
     * @return The created event, or null if the profile doesn't exist
     */
    public EnvironmentalEvent createEvent(String profileId, String eventType, double time) {
        EnvironmentalProfile profile = profiles.get(profileId);
        if (profile == null) {
            return null;
        }
        
        EnvironmentalEvent event = new EnvironmentalEvent(eventType, time);
        profile.addEvent(event);
        return event;
    }
    
    /**
     * Sets the weight of a factor (its impact on disease progression).
     *
     * @param factorName The factor name
     * @param weight The weight (-1.0 to 1.0, negative = protective, positive = risk)
     */
    public void setFactorWeight(String factorName, Double weight) {
        factorWeights.put(factorName, weight);
    }
    
    /**
     * Gets the weight of a factor.
     *
     * @param factorName The factor name
     * @return The weight, or 0.0 if not set
     */
    public Double getFactorWeight(String factorName) {
        return factorWeights.getOrDefault(factorName, 0.0);
    }
    
    /**
     * Gets all factor weights.
     *
     * @return A map of factor names to weights
     */
    public Map<String, Double> getAllFactorWeights() {
        return new HashMap<>(factorWeights);
    }
    
    /**
     * Applies an intervention strategy to a profile.
     *
     * @param profileId The profile ID
     * @param strategyName The strategy name
     * @param time The time to apply the intervention
     * @param duration The duration of the intervention
     * @return The created event, or null if the profile or strategy doesn't exist
     */
    public EnvironmentalEvent applyIntervention(String profileId, String strategyName, 
                                              double time, double duration) {
        EnvironmentalProfile profile = profiles.get(profileId);
        InterventionStrategy strategy = strategies.get(strategyName);
        
        if (profile == null || strategy == null) {
            return null;
        }
        
        EnvironmentalEvent event = createEvent(profileId, "intervention_" + strategyName, time);
        
        // Apply factor changes based on the intervention strategy
        for (Map.Entry<String, Double> entry : strategy.getFactorChanges().entrySet()) {
            String factorName = entry.getKey();
            Double change = entry.getValue();
            
            // Adjust for adherence
            double adherenceFactor = random.nextDouble() < strategy.getAdherenceRate() ? 1.0 : 0.2;
            double adjustedChange = change * adherenceFactor;
            
            event.setFactorChange(factorName, adjustedChange);
        }
        
        event.setMetadata("duration", duration);
        event.setMetadata("adherence", random.nextDouble() < strategy.getAdherenceRate());
        event.setMetadata("dropout", random.nextDouble() < strategy.getDropoutRate());
        
        return event;
    }
    
    /**
     * Creates an intervention strategy.
     *
     * @param name The strategy name
     * @param description The strategy description
     * @param adherenceRate The expected adherence rate (0.0 to 1.0)
     * @param dropoutRate The expected dropout rate (0.0 to 1.0)
     * @return The created strategy
     */
    public InterventionStrategy createInterventionStrategy(String name, String description,
                                                         double adherenceRate, double dropoutRate) {
        InterventionStrategy strategy = new InterventionStrategy(name, description, adherenceRate, dropoutRate);
        strategies.put(name, strategy);
        return strategy;
    }
    
    /**
     * Gets an intervention strategy by name.
     *
     * @param name The strategy name
     * @return The strategy, or null if not found
     */
    public InterventionStrategy getInterventionStrategy(String name) {
        return strategies.get(name);
    }
    
    /**
     * Gets all intervention strategies.
     *
     * @return A map of strategy names to strategies
     */
    public Map<String, InterventionStrategy> getAllInterventionStrategies() {
        return new HashMap<>(strategies);
    }
    
    /**
     * Simulates the combined effect of environmental factors on disease progression.
     *
     * @param profileId The profile ID
     * @param baselineRisk The baseline risk score
     * @param timePoints The number of time points to simulate
     * @return A list of risk scores over time, or empty list if profile doesn't exist
     */
    public List<Double> simulateEnvironmentalImpact(String profileId, double baselineRisk, int timePoints) {
        EnvironmentalProfile profile = profiles.get(profileId);
        if (profile == null) {
            return new ArrayList<>();
        }
        
        List<Double> riskScores = new ArrayList<>(timePoints);
        
        // Start with baseline risk
        double currentRisk = baselineRisk;
        riskScores.add(currentRisk);
        
        // Get all events sorted by time
        List<EnvironmentalEvent> events = profile.getEvents();
        events.sort((e1, e2) -> Double.compare(e1.getTime(), e2.getTime()));
        
        // Current factor values
        Map<String, Double> currentFactors = new HashMap<>(profile.getFactors());
        
        // Simulate risk changes over time
        for (int t = 1; t < timePoints; t++) {
            double timePoint = t / (double) timePoints;
            
            // Apply events that occur at this time point
            for (EnvironmentalEvent event : events) {
                if (event.getTime() <= timePoint && event.getTime() > (t - 1) / (double) timePoints) {
                    // Apply factor changes from this event
                    for (Map.Entry<String, Double> change : event.getFactorChanges().entrySet()) {
                        String factorName = change.getKey();
                        Double changeValue = change.getValue();
                        
                        // Update the factor value
                        double oldValue = currentFactors.getOrDefault(factorName, 0.0);
                        double newValue = Math.max(0.0, Math.min(1.0, oldValue + changeValue));
                        currentFactors.put(factorName, newValue);
                    }
                }
            }
            
            // Calculate risk modification based on current factors
            double riskModifier = 0.0;
            for (Map.Entry<String, Double> factor : currentFactors.entrySet()) {
                String factorName = factor.getKey();
                Double factorValue = factor.getValue();
                Double weight = getFactorWeight(factorName);
                
                // Add to risk modifier
                riskModifier += factorValue * weight;
            }
            
            // Apply risk modifier (sigmoid function to keep in reasonable range)
            double modifierImpact = 0.2 * riskModifier / (1.0 + 0.2 * Math.abs(riskModifier));
            currentRisk = currentRisk * (1.0 + modifierImpact);
            
            // Natural progression (small increase)
            currentRisk += baselineRisk * 0.01;
            
            // Add some randomness
            currentRisk += baselineRisk * 0.01 * (random.nextGaussian());
            
            // Ensure risk stays positive
            currentRisk = Math.max(0.1, currentRisk);
            
            riskScores.add(currentRisk);
        }
        
        return riskScores;
    }
    
    /**
     * Compares the effects of different intervention strategies on a profile.
     *
     * @param profileId The profile ID
     * @param baselineRisk The baseline risk score
     * @param timePoints The number of time points to simulate
     * @param interventionTime The time to apply interventions
     * @param duration The duration of interventions
     * @return A map of strategy names to risk score trajectories
     */
    public Map<String, List<Double>> compareInterventions(String profileId, double baselineRisk,
                                                        int timePoints, double interventionTime,
                                                        double duration) {
        Map<String, List<Double>> results = new HashMap<>();
        
        // Create a temporary copy of the profile
        EnvironmentalProfile originalProfile = profiles.get(profileId);
        if (originalProfile == null) {
            return results;
        }
        
        // Add baseline trajectory (no intervention)
        results.put("baseline", simulateEnvironmentalImpact(profileId, baselineRisk, timePoints));
        
        // Compare each intervention strategy
        for (String strategyName : strategies.keySet()) {
            // Create a temporary profile copy
            String tempId = profileId + "_temp_" + strategyName;
            EnvironmentalProfile tempProfile = createProfile(tempId);
            
            // Copy all factors
            for (Map.Entry<String, Double> factor : originalProfile.getFactors().entrySet()) {
                tempProfile.setFactor(factor.getKey(), factor.getValue());
            }
            
            // Copy existing events
            for (EnvironmentalEvent event : originalProfile.getEvents()) {
                EnvironmentalEvent newEvent = createEvent(tempId, event.getType(), event.getTime());
                for (Map.Entry<String, Double> change : event.getFactorChanges().entrySet()) {
                    newEvent.setFactorChange(change.getKey(), change.getValue());
                }
            }
            
            // Apply the intervention
            applyIntervention(tempId, strategyName, interventionTime, duration);
            
            // Simulate and store the results
            List<Double> trajectory = simulateEnvironmentalImpact(tempId, baselineRisk, timePoints);
            results.put(strategyName, trajectory);
            
            // Remove the temporary profile
            profiles.remove(tempId);
        }
        
        return results;
    }
    
    /**
     * Calculates the environmental risk score for a profile.
     *
     * @param profileId The profile ID
     * @return The risk score, or 0.0 if the profile doesn't exist
     */
    public double calculateRiskScore(String profileId) {
        EnvironmentalProfile profile = profiles.get(profileId);
        if (profile == null) {
            return 0.0;
        }
        
        double riskScore = 0.0;
        
        // Calculate weighted sum of factors
        for (Map.Entry<String, Double> factor : profile.getFactors().entrySet()) {
            String factorName = factor.getKey();
            Double factorValue = factor.getValue();
            Double weight = getFactorWeight(factorName);
            
            riskScore += factorValue * weight;
        }
        
        // Normalize to a 0-100 scale
        riskScore = 50 + riskScore * 50;
        riskScore = Math.max(0, Math.min(100, riskScore));
        
        return riskScore;
    }
    
    /**
     * Creates a random environmental profile.
     *
     * @param id The profile ID
     * @return The created profile
     */
    public EnvironmentalProfile createRandomProfile(String id) {
        EnvironmentalProfile profile = createProfile(id);
        
        // Set random factor values
        for (String factorName : factorWeights.keySet()) {
            profile.setFactor(factorName, random.nextDouble());
        }
        
        return profile;
    }
    
    /**
     * Creates a cohort of random profiles.
     *
     * @param count The number of profiles to create
     * @return A list of created profile IDs
     */
    public List<String> createRandomCohort(int count) {
        List<String> cohort = new ArrayList<>(count);
        
        for (int i = 0; i < count; i++) {
            String id = "patient_" + i;
            createRandomProfile(id);
            cohort.add(id);
        }
        
        return cohort;
    }
    
    /**
     * Runs a cohort study comparing intervention strategies.
     *
     * @param baselineRisk The baseline risk score
     * @param cohortSize The size of each cohort
     * @param timePoints The number of time points to simulate
     * @param interventionTime The time to apply interventions
     * @param duration The duration of interventions
     * @return A map of strategy names to average risk trajectories
     */
    public Map<String, List<Double>> runCohortStudy(double baselineRisk, int cohortSize,
                                                  int timePoints, double interventionTime,
                                                  double duration) {
        Map<String, List<List<Double>>> allTrajectories = new HashMap<>();
        
        // Initialize trajectories for baseline and each strategy
        allTrajectories.put("baseline", new ArrayList<>());
        for (String strategyName : strategies.keySet()) {
            allTrajectories.put(strategyName, new ArrayList<>());
        }
        
        // Create and simulate cohort
        List<String> cohort = createRandomCohort(cohortSize);
        
        for (String patientId : cohort) {
            Map<String, List<Double>> patientResults = compareInterventions(
                patientId, baselineRisk, timePoints, interventionTime, duration);
            
            // Add to collected trajectories
            for (Map.Entry<String, List<Double>> entry : patientResults.entrySet()) {
                String strategyName = entry.getKey();
                List<Double> trajectory = entry.getValue();
                
                allTrajectories.get(strategyName).add(trajectory);
            }
        }
        
        // Calculate average trajectories
        Map<String, List<Double>> averageTrajectories = new HashMap<>();
        
        for (Map.Entry<String, List<List<Double>>> entry : allTrajectories.entrySet()) {
            String strategyName = entry.getKey();
            List<List<Double>> trajectories = entry.getValue();
            
            if (trajectories.isEmpty() || trajectories.get(0).isEmpty()) {
                continue;
            }
            
            int trajLength = trajectories.get(0).size();
            List<Double> avgTrajectory = new ArrayList<>(trajLength);
            
            // Initialize with zeros
            for (int i = 0; i < trajLength; i++) {
                avgTrajectory.add(0.0);
            }
            
            // Sum all trajectories
            for (List<Double> trajectory : trajectories) {
                for (int i = 0; i < trajLength; i++) {
                    avgTrajectory.set(i, avgTrajectory.get(i) + trajectory.get(i));
                }
            }
            
            // Divide by number of trajectories
            for (int i = 0; i < trajLength; i++) {
                avgTrajectory.set(i, avgTrajectory.get(i) / trajectories.size());
            }
            
            averageTrajectories.put(strategyName, avgTrajectory);
        }
        
        return averageTrajectories;
    }
    
    /**
     * Validates the component's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = new HashMap<>();
        
        // Check required configuration
        if (!configuration.containsKey("default_oxidative_stress_baseline")) {
            errors.put("default_oxidative_stress_baseline", "Missing required configuration");
        }
        
        if (!configuration.containsKey("default_inflammatory_response_baseline")) {
            errors.put("default_inflammatory_response_baseline", "Missing required configuration");
        }
        
        return errors;
    }
    
    /**
     * Initializes the component.
     */
    @Override
    public void initialize() {
        Map<String, String> validationErrors = validateConfiguration();
        if (!validationErrors.isEmpty()) {
            setMetadata("initialization_errors", String.join(", ", validationErrors.values()));
            setState("ERROR");
            return;
        }
        
        setState("READY");
    }
    
    /**
     * Clears all profiles and events.
     */
    public void clearData() {
        profiles.clear();
    }
    
    /**
     * Destroys the component, releasing any resources.
     */
    @Override
    public void destroy() {
        clearData();
        strategies.clear();
        setState("DESTROYED");
    }
}