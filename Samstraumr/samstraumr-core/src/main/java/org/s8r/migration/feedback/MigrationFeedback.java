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

package org.s8r.migration.feedback;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Central API for accessing migration feedback information.
 * This class provides static methods for reporting and retrieving migration issues,
 * generating reports, and managing the migration feedback system.
 */
public class MigrationFeedback {
    
    private static final List<MigrationIssue> issues = new CopyOnWriteArrayList<>();
    private static final Map<String, List<MigrationIssue>> issuesByComponent = new ConcurrentHashMap<>();
    private static final Map<IssueType, List<MigrationIssue>> issuesByType = new ConcurrentHashMap<>();
    private static final Map<Severity, List<MigrationIssue>> issuesBySeverity = new ConcurrentHashMap<>();
    private static MigrationFeedbackConfig config = new MigrationFeedbackConfig.Builder().build();
    
    // Private constructor to prevent instantiation
    private MigrationFeedback() {
    }
    
    /**
     * Configures the migration feedback system.
     *
     * @param newConfig The new configuration
     */
    public static void configure(MigrationFeedbackConfig newConfig) {
        config = newConfig;
    }
    
    /**
     * Gets the current configuration.
     *
     * @return The current configuration
     */
    public static MigrationFeedbackConfig getConfig() {
        return config;
    }
    
    /**
     * Reports a migration issue.
     *
     * @param issue The issue to report
     */
    public static void reportIssue(MigrationIssue issue) {
        // Skip issues below the minimum severity
        if (issue.getSeverity().getLevel() < config.getMinSeverity().getLevel()) {
            return;
        }
        
        // Check if we've hit the limit for this component
        String component = issue.getComponent();
        if (component != null) {
            List<MigrationIssue> componentIssues = issuesByComponent.computeIfAbsent(component, k -> new ArrayList<>());
            if (componentIssues.size() >= config.getMaxIssuesPerComponent() && config.getMaxIssuesPerComponent() > 0) {
                // Too many issues for this component already
                return;
            }
        }
        
        // Add to main list
        issues.add(issue);
        
        // Add to component map
        if (component != null) {
            issuesByComponent.computeIfAbsent(component, k -> new ArrayList<>()).add(issue);
        }
        
        // Add to type map
        issuesByType.computeIfAbsent(issue.getType(), k -> new ArrayList<>()).add(issue);
        
        // Add to severity map
        issuesBySeverity.computeIfAbsent(issue.getSeverity(), k -> new ArrayList<>()).add(issue);
    }
    
    /**
     * Gets migration statistics.
     *
     * @return The migration statistics
     */
    public static MigrationStats getStats() {
        // Count issues by severity
        Map<Severity, Integer> issueCountBySeverity = new EnumMap<>(Severity.class);
        for (Severity severity : Severity.values()) {
            List<MigrationIssue> severityIssues = issuesBySeverity.getOrDefault(severity, Collections.emptyList());
            issueCountBySeverity.put(severity, severityIssues.size());
        }
        
        // Count issues by type
        Map<IssueType, Integer> issueCountByType = new EnumMap<>(IssueType.class);
        for (IssueType type : IssueType.values()) {
            List<MigrationIssue> typeIssues = issuesByType.getOrDefault(type, Collections.emptyList());
            issueCountByType.put(type, typeIssues.size());
        }
        
        return new MigrationStats(
                issues.size(),
                issuesByComponent.size(),
                issueCountBySeverity,
                issueCountByType
        );
    }
    
    /**
     * Gets all migration issues.
     *
     * @return A list of all migration issues
     */
    public static List<MigrationIssue> getIssues() {
        return new ArrayList<>(issues);
    }
    
    /**
     * Gets migration issues filtered by type.
     *
     * @param type The issue type to filter by
     * @return A list of migration issues of the specified type
     */
    public static List<MigrationIssue> getIssuesByType(IssueType type) {
        return new ArrayList<>(issuesByType.getOrDefault(type, Collections.emptyList()));
    }
    
    /**
     * Gets migration issues filtered by severity.
     *
     * @param severity The issue severity to filter by
     * @return A list of migration issues of the specified severity
     */
    public static List<MigrationIssue> getIssuesBySeverity(Severity severity) {
        return new ArrayList<>(issuesBySeverity.getOrDefault(severity, Collections.emptyList()));
    }
    
    /**
     * Gets migration issues filtered by minimum severity.
     *
     * @param minSeverity The minimum issue severity to include
     * @return A list of migration issues with at least the specified severity
     */
    public static List<MigrationIssue> getIssuesByMinSeverity(Severity minSeverity) {
        return issues.stream()
                .filter(issue -> issue.getSeverity().isAtLeast(minSeverity))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets migration issues for a specific component.
     *
     * @param component The component to filter by
     * @return A list of migration issues for the specified component
     */
    public static List<MigrationIssue> getIssuesForComponent(String component) {
        return new ArrayList<>(issuesByComponent.getOrDefault(component, Collections.emptyList()));
    }
    
    /**
     * Gets migration issues filtered by property.
     *
     * @param property The property to filter by
     * @return A list of migration issues for the specified property
     */
    public static List<MigrationIssue> getIssuesByProperty(String property) {
        return issues.stream()
                .filter(issue -> property.equals(issue.getProperty()))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets the most common issue types.
     *
     * @param limit The maximum number of types to return
     * @return A map of issue types to counts, sorted by count descending
     */
    public static Map<IssueType, Integer> getMostCommonIssueTypes(int limit) {
        Map<IssueType, Integer> counts = new HashMap<>();
        for (IssueType type : IssueType.values()) {
            List<MigrationIssue> typeIssues = issuesByType.getOrDefault(type, Collections.emptyList());
            if (!typeIssues.isEmpty()) {
                counts.put(type, typeIssues.size());
            }
        }
        
        return counts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));
    }
    
    /**
     * Gets the most affected components.
     *
     * @param limit The maximum number of components to return
     * @return A map of component names to issue counts, sorted by count descending
     */
    public static Map<String, Integer> getMostAffectedComponents(int limit) {
        return issuesByComponent.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((list1, list2) -> Integer.compare(list2.size(), list1.size())))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                ));
    }
    
    /**
     * Gets recommendations for resolving migration issues.
     *
     * @return A list of recommendations
     */
    public static List<MigrationRecommendation> getRecommendations() {
        if (!config.isEnableRecommendations()) {
            return Collections.emptyList();
        }
        
        // Group issues by type and analyze patterns
        List<MigrationRecommendation> recommendations = new ArrayList<>();
        
        // Process type mismatch issues
        List<MigrationIssue> typeMismatchIssues = getIssuesByType(IssueType.TYPE_MISMATCH);
        if (!typeMismatchIssues.isEmpty()) {
            // Group by legacy type and S8r type
            Map<String, Map<String, List<MigrationIssue>>> issuesByLegacyAndS8rType = new HashMap<>();
            
            for (MigrationIssue issue : typeMismatchIssues) {
                String legacyType = issue.getLegacyValue();
                String s8rType = issue.getS8rValue();
                
                if (legacyType != null && s8rType != null) {
                    issuesByLegacyAndS8rType
                            .computeIfAbsent(legacyType, k -> new HashMap<>())
                            .computeIfAbsent(s8rType, k -> new ArrayList<>())
                            .add(issue);
                }
            }
            
            // Generate recommendations for common type mismatches
            for (Map.Entry<String, Map<String, List<MigrationIssue>>> legacyTypeEntry : issuesByLegacyAndS8rType.entrySet()) {
                String legacyType = legacyTypeEntry.getKey();
                
                for (Map.Entry<String, List<MigrationIssue>> s8rTypeEntry : legacyTypeEntry.getValue().entrySet()) {
                    String s8rType = s8rTypeEntry.getKey();
                    List<MigrationIssue> issues = s8rTypeEntry.getValue();
                    
                    if (issues.size() >= config.getRecommendationThreshold()) {
                        String description = String.format("Type mismatch between %s and %s", legacyType, s8rType);
                        String solution = String.format("Create a converter that maps %s to %s consistently", 
                                legacyType, s8rType);
                        
                        recommendations.add(new MigrationRecommendation(
                                "TYPE_MISMATCH_" + legacyType + "_" + s8rType,
                                description,
                                solution,
                                Severity.WARNING,
                                issues.size()
                        ));
                    }
                }
            }
        }
        
        // Process state transition issues
        List<MigrationIssue> stateTransitionIssues = getIssuesByType(IssueType.STATE_TRANSITION);
        if (!stateTransitionIssues.isEmpty()) {
            Map<String, List<MigrationIssue>> issuesByTransition = new HashMap<>();
            
            for (MigrationIssue issue : stateTransitionIssues) {
                String from = issue.getLegacyValue();
                String to = issue.getS8rValue();
                
                if (from != null && to != null) {
                    String transition = from + "->" + to;
                    issuesByTransition.computeIfAbsent(transition, k -> new ArrayList<>()).add(issue);
                }
            }
            
            for (Map.Entry<String, List<MigrationIssue>> entry : issuesByTransition.entrySet()) {
                String transition = entry.getKey();
                List<MigrationIssue> issues = entry.getValue();
                
                if (issues.size() >= config.getRecommendationThreshold()) {
                    String[] parts = transition.split("->");
                    String from = parts[0];
                    String to = parts[1];
                    
                    String description = String.format("Invalid state transition from %s to %s", from, to);
                    String solution = String.format("Use a StateTransitionAdapter to handle the conversion between %s and %s", 
                            from, to);
                    
                    recommendations.add(new MigrationRecommendation(
                            "STATE_TRANSITION_" + from + "_" + to,
                            description,
                            solution,
                            Severity.WARNING,
                            issues.size()
                    ));
                }
            }
        }
        
        // Sort recommendations by count descending
        recommendations.sort(Comparator.comparingInt(MigrationRecommendation::getIssueCount).reversed());
        
        return recommendations;
    }
    
    /**
     * Generates a text report of migration issues.
     *
     * @return A string containing the report
     */
    public static String generateTextReport() {
        StringBuilder sb = new StringBuilder();
        MigrationStats stats = getStats();
        
        sb.append("Migration Feedback Report\n");
        sb.append("========================\n\n");
        
        sb.append("Summary:\n");
        sb.append(String.format("- Total Issues: %d\n", stats.getTotalIssues()));
        sb.append(String.format("- Affected Components: %d\n", stats.getAffectedComponents()));
        sb.append("\n");
        
        sb.append("Issues by Severity:\n");
        for (Severity severity : Severity.values()) {
            int count = stats.getIssueCountBySeverity().getOrDefault(severity, 0);
            if (count > 0) {
                sb.append(String.format("- %s: %d\n", severity.getDisplayName(), count));
            }
        }
        sb.append("\n");
        
        sb.append("Issues by Type:\n");
        for (IssueType type : IssueType.values()) {
            int count = stats.getIssueCountByType().getOrDefault(type, 0);
            if (count > 0) {
                sb.append(String.format("- %s: %d\n", type.getDisplayName(), count));
            }
        }
        sb.append("\n");
        
        sb.append("Most Affected Components:\n");
        Map<String, Integer> topComponents = getMostAffectedComponents(10);
        for (Map.Entry<String, Integer> entry : topComponents.entrySet()) {
            sb.append(String.format("- %s: %d issues\n", entry.getKey(), entry.getValue()));
        }
        sb.append("\n");
        
        sb.append("Recommendations:\n");
        List<MigrationRecommendation> recommendations = getRecommendations();
        for (MigrationRecommendation recommendation : recommendations) {
            sb.append(String.format("- %s (%d issues)\n", recommendation.getDescription(), recommendation.getIssueCount()));
            sb.append(String.format("  Solution: %s\n", recommendation.getSolution()));
            sb.append("\n");
        }
        
        sb.append("Recent Issues:\n");
        List<MigrationIssue> recentIssues = issues.stream()
                .sorted(Comparator.comparingLong(MigrationIssue::getTimestamp).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        for (MigrationIssue issue : recentIssues) {
            sb.append(String.format("- [%s] %s: %s\n", 
                    issue.getSeverity(), issue.getType().getDisplayName(), issue.getMessage()));
        }
        
        return sb.toString();
    }
    
    /**
     * Exports migration issues to a file in the specified format.
     *
     * @param format The export format (text, json, csv)
     * @param filepath The file to export to
     * @throws IOException If an I/O error occurs
     */
    public static void exportIssues(String format, String filepath) throws IOException {
        File file = new File(filepath);
        
        try (FileWriter writer = new FileWriter(file)) {
            switch (format.toLowerCase()) {
                case "text":
                    writer.write(generateTextReport());
                    break;
                case "json":
                    writer.write(generateJsonReport());
                    break;
                case "csv":
                    writer.write(generateCsvReport());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported export format: " + format);
            }
        }
    }
    
    /**
     * Generates a JSON report of migration issues.
     *
     * @return A string containing the JSON report
     */
    private static String generateJsonReport() {
        // This is a very basic JSON implementation
        // In a real implementation, use a JSON library
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        
        // Stats
        MigrationStats stats = getStats();
        sb.append("  \"stats\": {\n");
        sb.append(String.format("    \"totalIssues\": %d,\n", stats.getTotalIssues()));
        sb.append(String.format("    \"affectedComponents\": %d,\n", stats.getAffectedComponents()));
        
        // Issues by severity
        sb.append("    \"issuesBySeverity\": {\n");
        boolean first = true;
        for (Severity severity : Severity.values()) {
            int count = stats.getIssueCountBySeverity().getOrDefault(severity, 0);
            if (count > 0) {
                if (!first) {
                    sb.append(",\n");
                }
                sb.append(String.format("      \"%s\": %d", severity.name(), count));
                first = false;
            }
        }
        sb.append("\n    },\n");
        
        // Issues by type
        sb.append("    \"issuesByType\": {\n");
        first = true;
        for (IssueType type : IssueType.values()) {
            int count = stats.getIssueCountByType().getOrDefault(type, 0);
            if (count > 0) {
                if (!first) {
                    sb.append(",\n");
                }
                sb.append(String.format("      \"%s\": %d", type.name(), count));
                first = false;
            }
        }
        sb.append("\n    }\n");
        sb.append("  },\n");
        
        // Issues
        sb.append("  \"issues\": [\n");
        first = true;
        for (MigrationIssue issue : issues) {
            if (!first) {
                sb.append(",\n");
            }
            
            sb.append("    {\n");
            sb.append(String.format("      \"id\": \"%s\",\n", issue.getId()));
            sb.append(String.format("      \"severity\": \"%s\",\n", issue.getSeverity().name()));
            sb.append(String.format("      \"type\": \"%s\",\n", issue.getType().name()));
            
            if (issue.getComponent() != null) {
                sb.append(String.format("      \"component\": \"%s\",\n", escapeJson(issue.getComponent())));
            }
            
            if (issue.getProperty() != null) {
                sb.append(String.format("      \"property\": \"%s\",\n", escapeJson(issue.getProperty())));
            }
            
            if (issue.getMessage() != null) {
                sb.append(String.format("      \"message\": \"%s\",\n", escapeJson(issue.getMessage())));
            }
            
            if (issue.getLegacyValue() != null) {
                sb.append(String.format("      \"legacyValue\": \"%s\",\n", escapeJson(issue.getLegacyValue())));
            }
            
            if (issue.getS8rValue() != null) {
                sb.append(String.format("      \"s8rValue\": \"%s\",\n", escapeJson(issue.getS8rValue())));
            }
            
            sb.append(String.format("      \"timestamp\": %d\n", issue.getTimestamp()));
            sb.append("    }");
            
            first = false;
        }
        sb.append("\n  ],\n");
        
        // Recommendations
        sb.append("  \"recommendations\": [\n");
        List<MigrationRecommendation> recommendations = getRecommendations();
        first = true;
        for (MigrationRecommendation recommendation : recommendations) {
            if (!first) {
                sb.append(",\n");
            }
            
            sb.append("    {\n");
            sb.append(String.format("      \"id\": \"%s\",\n", recommendation.getId()));
            sb.append(String.format("      \"description\": \"%s\",\n", escapeJson(recommendation.getDescription())));
            sb.append(String.format("      \"solution\": \"%s\",\n", escapeJson(recommendation.getSolution())));
            sb.append(String.format("      \"severity\": \"%s\",\n", recommendation.getSeverity().name()));
            sb.append(String.format("      \"issueCount\": %d\n", recommendation.getIssueCount()));
            sb.append("    }");
            
            first = false;
        }
        sb.append("\n  ]\n");
        
        sb.append("}\n");
        return sb.toString();
    }
    
    /**
     * Generates a CSV report of migration issues.
     *
     * @return A string containing the CSV report
     */
    private static String generateCsvReport() {
        StringBuilder sb = new StringBuilder();
        
        // Header
        sb.append("ID,Timestamp,Severity,Type,Component,Property,Message,Legacy Value,S8r Value\n");
        
        // Issues
        for (MigrationIssue issue : issues) {
            sb.append(String.format("\"%s\",", issue.getId()));
            sb.append(String.format("%d,", issue.getTimestamp()));
            sb.append(String.format("\"%s\",", issue.getSeverity().name()));
            sb.append(String.format("\"%s\",", issue.getType().name()));
            sb.append(String.format("\"%s\",", issue.getComponent() != null ? escapeCsv(issue.getComponent()) : ""));
            sb.append(String.format("\"%s\",", issue.getProperty() != null ? escapeCsv(issue.getProperty()) : ""));
            sb.append(String.format("\"%s\",", issue.getMessage() != null ? escapeCsv(issue.getMessage()) : ""));
            sb.append(String.format("\"%s\",", issue.getLegacyValue() != null ? escapeCsv(issue.getLegacyValue()) : ""));
            sb.append(String.format("\"%s\"\n", issue.getS8rValue() != null ? escapeCsv(issue.getS8rValue()) : ""));
        }
        
        return sb.toString();
    }
    
    /**
     * Escapes special characters in JSON strings.
     *
     * @param input The input string
     * @return The escaped string
     */
    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
    
    /**
     * Escapes special characters in CSV strings.
     *
     * @param input The input string
     * @return The escaped string
     */
    private static String escapeCsv(String input) {
        if (input == null) {
            return "";
        }
        
        return input.replace("\"", "\"\"");
    }
    
    /**
     * Clears all reported issues.
     */
    public static void clear() {
        issues.clear();
        issuesByComponent.clear();
        issuesByType.clear();
        issuesBySeverity.clear();
    }
}