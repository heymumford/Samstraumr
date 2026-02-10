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

package org.s8r.test.steps.alz001.mock.component;

import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;

/**
 * Mock implementation of a protein expression component for Alzheimer's disease modeling.
 * 
 * <p>This component simulates protein expression patterns related to Alzheimer's disease,
 * including amyloid-beta and tau protein levels, aggregation patterns, and interactions.
 */
public class ProteinExpressionComponent extends ALZ001MockComponent {
    
    /**
     * Represents a protein sample with type, level, and timestamp.
     */
    public static class ProteinSample {
        private final String proteinType;
        private final double level;
        private final Instant timestamp;
        private final Map<String, Object> properties;
        
        public ProteinSample(String proteinType, double level) {
            this.proteinType = proteinType;
            this.level = level;
            this.timestamp = Instant.now();
            this.properties = new HashMap<>();
        }
        
        public String getProteinType() {
            return proteinType;
        }
        
        public double getLevel() {
            return level;
        }
        
        public Instant getTimestamp() {
            return timestamp;
        }
        
        public void setProperty(String key, Object value) {
            properties.put(key, value);
        }
        
        public Object getProperty(String key) {
            return properties.get(key);
        }
    }
    
    private final Map<String, Double> thresholds = new HashMap<>();
    private final List<ProteinSample> samples = new ArrayList<>();
    private final Map<String, List<Double>> timeSeriesData = new HashMap<>();
    
    /**
     * Creates a new protein expression component with the given name.
     *
     * @param name The component name
     */
    public ProteinExpressionComponent(String name) {
        super(name);
        
        // Set default thresholds
        thresholds.put("amyloid", 42.0);
        thresholds.put("tau", 30.0);
        thresholds.put("phosphorylated_tau", 20.0);
        thresholds.put("neurofilament", 50.0);
    }
    
    /**
     * Sets a threshold value for a specific protein type.
     *
     * @param proteinType The protein type
     * @param threshold The threshold value
     */
    public void setThreshold(String proteinType, double threshold) {
        thresholds.put(proteinType, threshold);
    }
    
    /**
     * Gets the threshold value for a specific protein type.
     *
     * @param proteinType The protein type
     * @return The threshold value, or 0.0 if not set
     */
    public double getThreshold(String proteinType) {
        return thresholds.getOrDefault(proteinType, 0.0);
    }
    
    /**
     * Adds a protein sample to the component.
     *
     * @param sample The protein sample to add
     */
    public void addSample(ProteinSample sample) {
        samples.add(sample);
        
        // Add to time series data
        List<Double> series = timeSeriesData.computeIfAbsent(
            sample.getProteinType(), k -> new ArrayList<>());
        series.add(sample.getLevel());
        
        setState("PROCESSING");
    }
    
    /**
     * Creates and adds a new protein sample.
     *
     * @param proteinType The protein type
     * @param level The protein level
     * @return The created protein sample
     */
    public ProteinSample createSample(String proteinType, double level) {
        ProteinSample sample = new ProteinSample(proteinType, level);
        addSample(sample);
        return sample;
    }
    
    /**
     * Gets all samples of a specific protein type.
     *
     * @param proteinType The protein type
     * @return A list of protein samples of the specified type
     */
    public List<ProteinSample> getSamplesByType(String proteinType) {
        List<ProteinSample> result = new ArrayList<>();
        for (ProteinSample sample : samples) {
            if (sample.getProteinType().equals(proteinType)) {
                result.add(sample);
            }
        }
        return result;
    }
    
    /**
     * Gets all samples.
     *
     * @return A list of all protein samples
     */
    public List<ProteinSample> getAllSamples() {
        return new ArrayList<>(samples);
    }
    
    /**
     * Gets the average level of a specific protein type.
     *
     * @param proteinType The protein type
     * @return The average level, or 0.0 if no samples
     */
    public double getAverageLevel(String proteinType) {
        List<ProteinSample> typeSamples = getSamplesByType(proteinType);
        if (typeSamples.isEmpty()) {
            return 0.0;
        }
        
        double sum = 0.0;
        for (ProteinSample sample : typeSamples) {
            sum += sample.getLevel();
        }
        
        return sum / typeSamples.size();
    }
    
    /**
     * Checks if the average level of a protein type exceeds its threshold.
     *
     * @param proteinType The protein type
     * @return true if the average level exceeds the threshold, false otherwise
     */
    public boolean isAboveThreshold(String proteinType) {
        double avgLevel = getAverageLevel(proteinType);
        double threshold = getThreshold(proteinType);
        return avgLevel > threshold;
    }
    
    /**
     * Calculates the ratio between two protein types.
     *
     * @param numeratorType The protein type for the numerator
     * @param denominatorType The protein type for the denominator
     * @return The ratio, or 0.0 if the denominator has no samples
     */
    public double calculateRatio(String numeratorType, String denominatorType) {
        double numerator = getAverageLevel(numeratorType);
        double denominator = getAverageLevel(denominatorType);
        
        if (denominator == 0.0) {
            return 0.0;
        }
        
        return numerator / denominator;
    }
    
    /**
     * Simulates protein aggregation over time.
     *
     * @param proteinType The protein type
     * @param initialLevel The initial protein level
     * @param timePoints The number of time points to simulate
     * @param aggregationRate The rate of aggregation
     * @return A list of protein levels over time
     */
    public List<Double> simulateAggregation(String proteinType, double initialLevel, 
                                           int timePoints, double aggregationRate) {
        List<Double> aggregationPattern = new ArrayList<>();
        double currentLevel = initialLevel;
        
        for (int i = 0; i < timePoints; i++) {
            aggregationPattern.add(currentLevel);
            // Sigmoid growth model for protein aggregation
            double timeValue = i / (double) timePoints;
            currentLevel = initialLevel + 
                           (initialLevel * 5 * aggregationRate) / 
                           (1 + Math.exp(-10 * (timeValue - 0.5)));
        }
        
        // Store the simulation result
        String key = "aggregation_" + proteinType;
        setConfig(key, aggregationPattern);
        
        return aggregationPattern;
    }
    
    /**
     * Simulates interaction between two protein types.
     *
     * @param type1 The first protein type
     * @param type2 The second protein type
     * @param interactionStrength The strength of interaction
     * @param timePoints The number of time points to simulate
     * @return A map containing the levels of both proteins over time
     */
    public Map<String, List<Double>> simulateInteraction(String type1, String type2, 
                                                      double interactionStrength,
                                                      int timePoints) {
        Map<String, List<Double>> result = new HashMap<>();
        List<Double> levels1 = new ArrayList<>();
        List<Double> levels2 = new ArrayList<>();
        
        double level1 = getAverageLevel(type1);
        double level2 = getAverageLevel(type2);
        
        if (level1 == 0.0) level1 = getThreshold(type1);
        if (level2 == 0.0) level2 = getThreshold(type2);
        
        for (int i = 0; i < timePoints; i++) {
            levels1.add(level1);
            levels2.add(level2);
            
            // Simple coupled differential equations model
            double timeStep = 0.1;
            double delta1 = timeStep * (interactionStrength * level2);
            double delta2 = timeStep * (interactionStrength * level1);
            
            level1 += delta1;
            level2 += delta2;
        }
        
        result.put(type1, levels1);
        result.put(type2, levels2);
        
        // Store the simulation result
        String key = "interaction_" + type1 + "_" + type2;
        setConfig(key, result);
        
        return result;
    }
    
    /**
     * Gets the time series data for a specific protein type.
     *
     * @param proteinType The protein type
     * @return A list of protein levels over time
     */
    public List<Double> getTimeSeries(String proteinType) {
        return timeSeriesData.getOrDefault(proteinType, new ArrayList<>());
    }
    
    /**
     * Analyzes protein expression patterns for abnormalities.
     *
     * @param proteinType The protein type to analyze
     * @return A map of analysis results
     */
    public Map<String, Object> analyzePattern(String proteinType) {
        Map<String, Object> results = new HashMap<>();
        List<Double> series = getTimeSeries(proteinType);
        
        if (series.size() < 3) {
            results.put("error", "Insufficient data points for analysis");
            return results;
        }
        
        // Simple trend analysis
        double firstAvg = series.subList(0, series.size() / 2).stream()
                             .mapToDouble(Double::doubleValue)
                             .average()
                             .orElse(0.0);
        
        double secondAvg = series.subList(series.size() / 2, series.size()).stream()
                             .mapToDouble(Double::doubleValue)
                             .average()
                             .orElse(0.0);
        
        boolean increasing = secondAvg > firstAvg;
        double changePercent = (secondAvg - firstAvg) / firstAvg * 100.0;
        
        results.put("trend", increasing ? "increasing" : "decreasing");
        results.put("change_percent", changePercent);
        results.put("average_level", series.stream()
                                        .mapToDouble(Double::doubleValue)
                                        .average()
                                        .orElse(0.0));
        results.put("above_threshold", isAboveThreshold(proteinType));
        
        return results;
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
        if (!configuration.containsKey("default_tau_threshold")) {
            errors.put("default_tau_threshold", "Missing required configuration");
        }
        
        if (!configuration.containsKey("default_amyloid_threshold")) {
            errors.put("default_amyloid_threshold", "Missing required configuration");
        }
        
        // Validate thresholds
        for (Map.Entry<String, Double> entry : thresholds.entrySet()) {
            if (entry.getValue() <= 0.0) {
                errors.put(entry.getKey() + "_threshold", "Threshold must be positive");
            }
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
        
        // Update thresholds from configuration if present
        Double tauThreshold = getConfig("default_tau_threshold");
        if (tauThreshold != null) {
            thresholds.put("tau", tauThreshold);
        }
        
        Double amyloidThreshold = getConfig("default_amyloid_threshold");
        if (amyloidThreshold != null) {
            thresholds.put("amyloid", amyloidThreshold);
        }
        
        setState("READY");
    }
    
    /**
     * Destroys the component, releasing any resources.
     */
    @Override
    public void destroy() {
        samples.clear();
        timeSeriesData.clear();
        setState("DESTROYED");
    }
}