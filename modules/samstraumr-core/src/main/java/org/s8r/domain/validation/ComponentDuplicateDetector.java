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

package org.s8r.domain.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.exception.PropertyBasedDuplicateComponentException;
import org.s8r.domain.identity.ComponentId;

/**
 * Component duplicate detector that analyzes components for potential duplicates based on property analysis.
 * 
 * <p>This class implements sophisticated detection of duplicate components beyond simple ID comparison.
 * It uses pattern matching, property analysis, and similarity scoring to identify potential duplicates.
 */
public class ComponentDuplicateDetector {

    private final ComponentRepository componentRepository;
    private final LoggerPort logger;
    private final Map<String, PropertyMatcher> propertyMatchers = new HashMap<>();
    private final Set<String> ignoredProperties = new HashSet<>();
    private final double defaultThreshold;
    private boolean strictMode = false;
    
    // Default properties to compare
    private static final Set<String> DEFAULT_PROPERTIES = new HashSet<>(Arrays.asList(
        "name", 
        "type", 
        "category", 
        "purpose", 
        "function",
        "description"
    ));
    
    /**
     * Creates a new ComponentDuplicateDetector with the specified repository and default threshold.
     * 
     * @param componentRepository The repository to use for component lookup
     * @param logger The logger to use for logging duplicate detection events
     * @param defaultThreshold The default similarity threshold (0-1) for determining duplicates
     */
    public ComponentDuplicateDetector(ComponentRepository componentRepository, LoggerPort logger, double defaultThreshold) {
        this.componentRepository = componentRepository;
        this.logger = logger;
        this.defaultThreshold = validateThreshold(defaultThreshold);
        
        // Initialize with default property matchers
        initializeDefaultPropertyMatchers();
        
        logger.debug("ComponentDuplicateDetector initialized with threshold: {}", defaultThreshold);
    }
    
    /**
     * Creates a new ComponentDuplicateDetector with the specified repository and a default threshold of 0.8.
     * 
     * @param componentRepository The repository to use for component lookup
     * @param logger The logger to use for logging duplicate detection events
     */
    public ComponentDuplicateDetector(ComponentRepository componentRepository, LoggerPort logger) {
        this(componentRepository, logger, 0.8);
    }
    
    /**
     * Initializes default property matchers for common component properties.
     */
    private void initializeDefaultPropertyMatchers() {
        // Name matcher - exact match with case insensitivity 
        addPropertyMatcher("name", 
            (value1, value2) -> value1.toString().equalsIgnoreCase(value2.toString()) ? 1.0 : 0.0);
        
        // Type matcher - exact match with case insensitivity
        addPropertyMatcher("type", 
            (value1, value2) -> value1.toString().equalsIgnoreCase(value2.toString()) ? 1.0 : 0.0);
            
        // Category matcher - case insensitive with partial matching
        addPropertyMatcher("category", this::compareStringsSimilarity);
            
        // Purpose matcher - fuzzy text similarity
        addPropertyMatcher("purpose", this::compareStringsSimilarity);
            
        // Function matcher - fuzzy text similarity
        addPropertyMatcher("function", this::compareStringsSimilarity);
        
        // Description matcher - fuzzy text similarity with lower weight
        addPropertyMatcher("description", (value1, value2) -> compareStringsSimilarity(value1, value2) * 0.7);
    }
    
    /**
     * Validates that a threshold value is between 0 and 1.
     * 
     * @param threshold The threshold to validate
     * @return The validated threshold
     * @throws IllegalArgumentException if the threshold is outside the range 0-1
     */
    private double validateThreshold(double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            throw new IllegalArgumentException("Similarity threshold must be between 0.0 and 1.0");
        }
        return threshold;
    }
    
    /**
     * Sets whether strict mode is enabled. In strict mode, the detector will throw exceptions
     * for potential duplicates rather than just reporting them.
     * 
     * @param strict Whether strict mode should be enabled
     * @return this detector instance for method chaining
     */
    public ComponentDuplicateDetector setStrictMode(boolean strict) {
        this.strictMode = strict;
        logger.debug("ComponentDuplicateDetector strict mode set to: {}", strict);
        return this;
    }
    
    /**
     * Adds a custom property matcher to use for detecting similarities.
     * 
     * @param propertyName The name of the property to compare
     * @param matcher The similarity function to use for comparison
     * @return this detector instance for method chaining
     */
    public ComponentDuplicateDetector addPropertyMatcher(String propertyName, PropertyMatcher matcher) {
        if (propertyName != null && matcher != null) {
            propertyMatchers.put(propertyName, matcher);
            logger.debug("Added property matcher for property: {}", propertyName);
        }
        return this;
    }
    
    /**
     * Adds a property to ignore during duplicate detection.
     * 
     * @param propertyName The name of the property to ignore
     * @return this detector instance for method chaining
     */
    public ComponentDuplicateDetector ignoreProperty(String propertyName) {
        if (propertyName != null) {
            ignoredProperties.add(propertyName);
            logger.debug("Added property to ignore list: {}", propertyName);
        }
        return this;
    }
    
    /**
     * Sets the properties to use for comparison during duplicate detection.
     * This replaces any existing property matchers.
     * 
     * @param propertyNames The set of property names to use for comparison
     * @return this detector instance for method chaining
     */
    public ComponentDuplicateDetector setPropertiesToCompare(Set<String> propertyNames) {
        propertyMatchers.clear();
        
        for (String property : propertyNames) {
            if (DEFAULT_PROPERTIES.contains(property)) {
                // For properties we have default matchers for, use those
                initializeDefaultPropertyMatchers();
            } else {
                // For other properties, use a default string similarity matcher
                addPropertyMatcher(property, this::compareStringsSimilarity);
            }
        }
        
        logger.debug("Set properties to compare: {}", propertyNames);
        return this;
    }
    
    /**
     * Checks a component for potential duplicates among all components in the repository.
     * 
     * @param component The component to check for duplicates
     * @throws PropertyBasedDuplicateComponentException if a duplicate is found and strict mode is enabled
     * @return A list of potential duplicate components, or an empty list if none are found
     */
    public List<ComponentPort> checkForDuplicates(ComponentPort component) {
        return checkForDuplicates(component, defaultThreshold);
    }
    
    /**
     * Checks a component for potential duplicates among all components in the repository,
     * using a specific similarity threshold.
     * 
     * @param component The component to check for duplicates
     * @param threshold The similarity threshold (0-1) above which components are considered duplicates
     * @throws PropertyBasedDuplicateComponentException if a duplicate is found and strict mode is enabled
     * @return A list of potential duplicate components, or an empty list if none are found
     */
    public List<ComponentPort> checkForDuplicates(ComponentPort component, double threshold) {
        if (component == null) {
            return Collections.emptyList();
        }
        
        threshold = validateThreshold(threshold);
        List<ComponentPort> allComponents = componentRepository.findAll();
        List<ComponentPort> potentialDuplicates = new ArrayList<>();
        
        // Skip checking the component against itself by ID
        ComponentId targetId = component.getId();
        
        logger.debug("Checking component {} for duplicates with threshold {}", targetId.getIdString(), threshold);
        
        for (ComponentPort existingComponent : allComponents) {
            // Skip self-comparison
            if (existingComponent.getId().equals(targetId)) {
                continue;
            }
            
            DuplicateAnalysisResult result = analyzePotentialDuplicate(component, existingComponent);
            
            if (result.getSimilarityScore() >= threshold) {
                potentialDuplicates.add(existingComponent);
                
                logger.debug("Potential duplicate found: {} with similarity score {}", 
                    existingComponent.getId().getIdString(), result.getSimilarityScore());
                    
                // In strict mode, immediately throw an exception for the first duplicate found
                if (strictMode) {
                    throw new PropertyBasedDuplicateComponentException(
                        component,
                        existingComponent,
                        result.getMatchingProperties(),
                        result.getSimilarityScore(),
                        result.getDuplicateReason()
                    );
                }
            }
        }
        
        return potentialDuplicates;
    }
    
    /**
     * Analyzes a component against another to determine if they are potential duplicates.
     * 
     * @param newComponent The component to check
     * @param existingComponent The existing component to compare against
     * @return A DuplicateAnalysisResult with similarity score and matching properties
     */
    public DuplicateAnalysisResult analyzePotentialDuplicate(
            ComponentPort newComponent, ComponentPort existingComponent) {
        
        // Get properties from both components
        Map<String, Object> newProps = newComponent.getProperties();
        Map<String, Object> existingProps = existingComponent.getProperties();
        
        // Collect properties to compare (intersection of available properties in both components)
        Set<String> propertiesToCompare = new HashSet<>(propertyMatchers.keySet());
        propertiesToCompare.retainAll(newProps.keySet());
        propertiesToCompare.retainAll(existingProps.keySet());
        propertiesToCompare.removeAll(ignoredProperties);
        
        // Calculate similarity scores for each property
        Map<String, Double> propertyScores = new HashMap<>();
        Set<String> matchingProperties = new HashSet<>();
        double totalScore = 0.0;
        
        for (String property : propertiesToCompare) {
            Object val1 = newProps.get(property);
            Object val2 = existingProps.get(property);
            
            // Skip null properties
            if (val1 == null || val2 == null) {
                continue;
            }
            
            // Get the matcher for this property
            PropertyMatcher matcher = propertyMatchers.getOrDefault(
                property, 
                this::compareGenericValues
            );
            
            // Calculate similarity for this property
            double similarity = matcher.calculateSimilarity(val1, val2);
            propertyScores.put(property, similarity);
            
            // Check if this property is a match (similarity > 0.9)
            if (similarity > 0.9) {
                matchingProperties.add(property);
            }
            
            totalScore += similarity;
        }
        
        // Calculate overall similarity score (average of all property scores)
        double avgScore = propertiesToCompare.isEmpty() ? 0.0 : totalScore / propertiesToCompare.size();
        
        // Generate a reason description
        String reason = generateDuplicateReason(matchingProperties, propertyScores, avgScore);
        
        return new DuplicateAnalysisResult(avgScore, matchingProperties, propertyScores, reason);
    }
    
    /**
     * Generates a human-readable reason for why components are considered duplicates.
     */
    private String generateDuplicateReason(
            Set<String> matchingProperties, 
            Map<String, Double> propertyScores,
            double overallScore) {
        
        if (matchingProperties.isEmpty()) {
            return "No exact property matches, but overall similarity score is high";
        }
        
        // List the top 3 matching properties
        List<String> topMatches = matchingProperties.stream()
            .sorted((p1, p2) -> Double.compare(
                propertyScores.getOrDefault(p2, 0.0),
                propertyScores.getOrDefault(p1, 0.0)
            ))
            .limit(3)
            .collect(Collectors.toList());
            
        return String.format("Matching properties include: %s. Overall similarity score: %.2f", 
            String.join(", ", topMatches), overallScore);
    }
    
    /**
     * Compares strings for similarity using a simple algorithm.
     * 
     * @param val1 The first string value
     * @param val2 The second string value
     * @return A similarity score between 0 and 1
     */
    private double compareStringsSimilarity(Object val1, Object val2) {
        String str1 = val1.toString().toLowerCase();
        String str2 = val2.toString().toLowerCase();
        
        // If strings are identical, return 1.0
        if (str1.equals(str2)) {
            return 1.0;
        }
        
        // Check if one string contains the other
        if (str1.contains(str2) || str2.contains(str1)) {
            int maxLength = Math.max(str1.length(), str2.length());
            int minLength = Math.min(str1.length(), str2.length());
            return (double) minLength / maxLength;
        }
        
        // Calculate Jaccard similarity between word sets
        Set<String> words1 = splitIntoWords(str1);
        Set<String> words2 = splitIntoWords(str2);
        
        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);
        
        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);
        
        if (union.isEmpty()) {
            return 0.0;
        }
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Splits a string into a set of words for comparison.
     */
    private Set<String> splitIntoWords(String text) {
        return Arrays.stream(text.split("\\W+"))
            .filter(word -> !word.isEmpty())
            .collect(Collectors.toSet());
    }
    
    /**
     * Compares generic values for equality.
     * 
     * @param val1 The first value
     * @param val2 The second value
     * @return 1.0 if equal, 0.0 if not equal
     */
    private double compareGenericValues(Object val1, Object val2) {
        return Objects.equals(val1, val2) ? 1.0 : 0.0;
    }
    
    /**
     * Represents the result of a duplicate analysis between two components.
     */
    public static class DuplicateAnalysisResult {
        private final double similarityScore;
        private final Set<String> matchingProperties;
        private final Map<String, Double> propertyScores;
        private final String duplicateReason;
        
        /**
         * Creates a new DuplicateAnalysisResult.
         * 
         * @param similarityScore The overall similarity score
         * @param matchingProperties The set of matching property names
         * @param propertyScores The map of property names to similarity scores
         * @param duplicateReason A description of why the components are considered duplicates
         */
        public DuplicateAnalysisResult(
                double similarityScore,
                Set<String> matchingProperties,
                Map<String, Double> propertyScores,
                String duplicateReason) {
            this.similarityScore = similarityScore;
            this.matchingProperties = new HashSet<>(matchingProperties);
            this.propertyScores = new HashMap<>(propertyScores);
            this.duplicateReason = duplicateReason;
        }
        
        /**
         * Gets the overall similarity score.
         * 
         * @return A score between 0 and 1 indicating how similar the components are
         */
        public double getSimilarityScore() {
            return similarityScore;
        }
        
        /**
         * Gets the set of property names that were found to match.
         * 
         * @return The set of matching property names
         */
        public Set<String> getMatchingProperties() {
            return Collections.unmodifiableSet(matchingProperties);
        }
        
        /**
         * Gets the map of property names to similarity scores.
         * 
         * @return The map of property scores
         */
        public Map<String, Double> getPropertyScores() {
            return Collections.unmodifiableMap(propertyScores);
        }
        
        /**
         * Gets a description of why the components are considered duplicates.
         * 
         * @return The duplicate reason
         */
        public String getDuplicateReason() {
            return duplicateReason;
        }
    }
    
    /**
     * Functional interface for property matchers that calculate similarity between two property values.
     */
    @FunctionalInterface
    public interface PropertyMatcher {
        /**
         * Calculates a similarity score between two property values.
         * 
         * @param value1 The first property value
         * @param value2 The second property value
         * @return A similarity score between 0 (no similarity) and 1 (identical)
         */
        double calculateSimilarity(Object value1, Object value2);
    }
}