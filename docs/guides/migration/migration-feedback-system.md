<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Migration Feedback System

This document describes the feedback system for reporting and tracking migration issues when moving from Samstraumr to S8r.

## Overview

The Migration Feedback System provides a comprehensive way to:

1. **Detect** migration issues during runtime
2. **Report** issues with detailed context
3. **Analyze** patterns of common problems
4. **Resolve** issues systematically
5. **Track** progress of migration efforts

## Components

### 1. migration issue logger

The Migration Issue Logger is a specialized logging component that captures migration-specific issues:

```java
import org.s8r.migration.feedback.MigrationIssueLogger;

// Create a logger with a specific category
MigrationIssueLogger logger = MigrationIssueLogger.forCategory("ComponentAdapter");

// Log different types of issues
logger.reportTypeMismatch("component.status", "TubeStatus", "State");
logger.reportPropertyNotFound("component.metadata.lifecycleStage");
logger.reportStateTransitionIssue("ACTIVE", "TERMINATED", "Invalid direct transition");
```

### 2. migration issue database

Issues are collected in a lightweight in-memory database that can be:

- Queried during runtime
- Exported to JSON/CSV for analysis
- Filtered by type, component, or severity

### 3. real-time feedback api

```java
import org.s8r.migration.feedback.MigrationFeedback;

// Get current migration statistics
MigrationStats stats = MigrationFeedback.getStats();
System.out.println("Total migration issues: " + stats.getTotalIssues());
System.out.println("Components affected: " + stats.getAffectedComponents());

// Get issue details
List<MigrationIssue> issues = MigrationFeedback.getIssues();
issues.stream()
    .filter(issue -> issue.getSeverity() == Severity.ERROR)
    .forEach(System.out::println);

// Get recommendations
List<MigrationRecommendation> recommendations = 
    MigrationFeedback.getRecommendations();
recommendations.forEach(System.out::println);
```

### 4. migration issue types

The system captures different types of migration issues:

| Issue Type | Description | Example |
|------------|-------------|---------|
| Type Mismatch | Incompatible types between S8r and Samstraumr | `TubeStatus.READY` vs `State.READY` |
| Missing Property | Property exists in one system but not the other | `tube.metadata` vs `component.properties` |
| State Transition | Invalid state transition paths | Direct `ACTIVE` to `TERMINATED` |
| Reflection Error | Errors in reflective access | Field not accessible |
| Method Mapping | Method signature mismatches | Parameter count or types |
| Structural Difference | Component hierarchy differences | Parent-child relationships |

### 5. migration issue reporter cli

A command-line tool to analyze and report on migration issues:

```bash
# Migration Feedback System
./s8r-migration-report

# Migration Feedback System
./s8r-migration-monitor --watch

# Migration Feedback System
./s8r-migration-report --export=json --output=migration-issues.json

# Migration Feedback System
./s8r-migration-report --recommendations
```

## Implementation

### Migrationissue class

```java
package org.s8r.migration.feedback;

/**
 * Represents a migration issue detected during runtime.
 */
public class MigrationIssue {
    // Issue metadata
    private final String id;
    private final Severity severity;
    private final IssueType type;
    private final String component;
    private final String property;
    
    // Issue details
    private final String message;
    private final String legacyValue;
    private final String s8rValue;
    private final String stackTrace;
    private final long timestamp;
    
    // Recommendation
    private final String recommendation;
    
    // Issue context
    private final String sourceClass;
    private final String sourceMethod;
    private final Map<String, String> context;
    
    // Constructors, getters, and utility methods...
}
```

### Migrationissuelogger class

```java
package org.s8r.migration.feedback;

/**
 * Logger for migration-specific issues.
 */
public class MigrationIssueLogger {
    private final String category;
    
    /**
     * Creates a logger for a specific category.
     */
    public static MigrationIssueLogger forCategory(String category) {
        return new MigrationIssueLogger(category);
    }
    
    /**
     * Reports a type mismatch issue.
     */
    public void reportTypeMismatch(String property, String legacyType, String s8rType) {
        // Implementation...
    }
    
    /**
     * Reports a property not found issue.
     */
    public void reportPropertyNotFound(String property) {
        // Implementation...
    }
    
    /**
     * Reports a state transition issue.
     */
    public void reportStateTransitionIssue(String fromState, String toState, String reason) {
        // Implementation...
    }
    
    /**
     * Reports a reflection error.
     */
    public void reportReflectionError(String target, Exception exception) {
        // Implementation...
    }
    
    /**
     * Reports a method mapping issue.
     */
    public void reportMethodMappingIssue(String legacyMethod, String s8rMethod, String reason) {
        // Implementation...
    }
    
    /**
     * Reports a structural difference.
     */
    public void reportStructuralDifference(String legacyStructure, String s8rStructure) {
        // Implementation...
    }
    
    /**
     * Reports a custom migration issue.
     */
    public void reportCustomIssue(IssueType type, Severity severity, String message) {
        // Implementation...
    }
}
```

### Migrationfeedback class

```java
package org.s8r.migration.feedback;

/**
 * Central API for accessing migration feedback information.
 */
public class MigrationFeedback {
    /**
     * Gets migration statistics.
     */
    public static MigrationStats getStats() {
        // Implementation...
    }
    
    /**
     * Gets all migration issues.
     */
    public static List<MigrationIssue> getIssues() {
        // Implementation...
    }
    
    /**
     * Gets migration issues filtered by type.
     */
    public static List<MigrationIssue> getIssuesByType(IssueType type) {
        // Implementation...
    }
    
    /**
     * Gets migration issues filtered by severity.
     */
    public static List<MigrationIssue> getIssuesBySeverity(Severity severity) {
        // Implementation...
    }
    
    /**
     * Gets migration issues for a specific component.
     */
    public static List<MigrationIssue> getIssuesForComponent(String component) {
        // Implementation...
    }
    
    /**
     * Gets recommendations for resolving migration issues.
     */
    public static List<MigrationRecommendation> getRecommendations() {
        // Implementation...
    }
    
    /**
     * Exports migration issues to a file in the specified format.
     */
    public static void exportIssues(String format, String filepath) {
        // Implementation...
    }
}
```

## Integration with Existing Tools

### Enhanced migration script

The migration feedback system integrates with the existing migration scripts:

```bash
# Migration Feedback System
./util/migrate-code.sh --feedback path/to/your/code

# Migration Feedback System
./util/migrate-code.sh --feedback --report path/to/your/code

# Migration Feedback System
./util/migrate-code.sh --feedback --interactive path/to/your/code
```

### Adapter integration

All adapters in the S8r migration system are enhanced to use the feedback mechanism:

```java
public class TubeComponentAdapter implements ComponentPort {
    private final MigrationIssueLogger logger = 
        MigrationIssueLogger.forCategory("TubeComponentAdapter");
        
    @Override
    public void setState(State state) {
        try {
            TubeStatus tubeStatus = convertStateToStatus(state);
            tube.setStatus(tubeStatus);
        } catch (IllegalArgumentException e) {
            logger.reportStateTransitionIssue(
                tube.getStatus().name(),
                state.name(),
                "Invalid state transition: " + e.getMessage()
            );
            throw e;
        }
    }
    
    // Other methods...
}
```

## Reporting and Dashboards

### Command-line reports

The system includes a command-line tool for generating reports:

```bash
# Migration Feedback System
./s8r-migration-report

# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
# Migration Feedback System
```

### Visual dashboard

For teams using continuous integration, a web-based dashboard is available:

```bash
# Migration Feedback System
./s8r-migration-dashboard --port=8080

# Migration Feedback System
./s8r-migration-report --html --output=migration-report.html
```

## Recommendations Engine

The system includes an AI-assisted recommendations engine that provides:

1. Code fix suggestions for common issues
2. Migration strategy recommendations
3. Prioritized issue lists

Example recommendations:

```
Recommendation #1:
Issue: Type mismatch between TubeStatus.READY and State.READY
Suggested fix:
   - Replace: tube.setStatus(TubeStatus.READY);
   - With: component.setState(State.READY);

Recommendation #2:
Issue: Multiple state transition issues in UserManager
Strategy: Consider using a state machine wrapper:
   - Create a StateMachineAdapter for UserManager
   - Use the TubeStateConverter utility
   - Update state transition logic to match S8r expectations
```

## Best Practices

### When to use the migration feedback system

- During active migration of large codebases
- When testing migrated components with legacy components
- To identify patterns of migration issues
- For generating migration progress reports

### Customizing the system

The migration feedback system can be customized through configuration:

```java
MigrationFeedbackConfig config = new MigrationFeedbackConfig.Builder()
    .setMinSeverity(Severity.WARNING)  // Ignore INFO level issues
    .enableRecommendations(true)       // Enable AI recommendations
    .setMaxIssuesPerComponent(100)     // Limit issues per component
    .build();

MigrationFeedback.configure(config);
```

## Conclusion

The Migration Feedback System provides a comprehensive framework for tracking, analyzing, and resolving migration issues when moving from Samstraumr to S8r. By integrating with existing tooling and providing both CLI and programmable interfaces, it enables developers to:

1. Identify and resolve migration issues quickly
2. Track migration progress over time
3. Understand patterns of common issues
4. Generate reports for stakeholders
