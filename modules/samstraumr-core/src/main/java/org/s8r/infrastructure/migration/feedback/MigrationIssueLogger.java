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

package org.s8r.infrastructure.migration.feedback;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.s8r.application.port.LoggerPort;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * Logger for migration-specific issues. This class provides methods for reporting different types
 * of migration issues and integrates with the MigrationFeedback system.
 */
public class MigrationIssueLogger {

  private final String category;
  private final LoggerPort logger;
  private final boolean reportToConsole;

  /**
   * Creates a logger for a specific category.
   *
   * @param category The category for this logger
   * @return A new MigrationIssueLogger
   */
  public static MigrationIssueLogger forCategory(String category) {
    return new MigrationIssueLogger(category);
  }

  /**
   * Creates a new MigrationIssueLogger with the specified category.
   *
   * @param category The category for this logger
   */
  public MigrationIssueLogger(String category) {
    this(category, new ConsoleLogger("MigrationIssue-" + category), true);
  }

  /**
   * Creates a new MigrationIssueLogger with the specified category and logger.
   *
   * @param category The category for this logger
   * @param logger The logger to use for console output
   * @param reportToConsole Whether to report issues to the console
   */
  public MigrationIssueLogger(String category, LoggerPort logger, boolean reportToConsole) {
    this.category = category;
    this.logger = logger;
    this.reportToConsole = reportToConsole;
  }

  /**
   * Reports a type mismatch issue.
   *
   * @param property The property with the type mismatch
   * @param legacyType The legacy type
   * @param s8rType The S8r type
   * @return The created MigrationIssue
   */
  public MigrationIssue reportTypeMismatch(String property, String legacyType, String s8rType) {
    String message =
        String.format("Type mismatch: %s expected %s but got %s", property, s8rType, legacyType);

    if (reportToConsole) {
      logger.warn(message);
    }

    MigrationIssue issue =
        createIssue(IssueType.TYPE_MISMATCH, Severity.WARNING)
            .property(property)
            .message(message)
            .legacyValue(legacyType)
            .s8rValue(s8rType)
            .recommendation(
                String.format(
                    "Convert %s to %s before assigning to %s", legacyType, s8rType, property))
            .build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Reports a property not found issue.
   *
   * @param property The property that was not found
   * @return The created MigrationIssue
   */
  public MigrationIssue reportPropertyNotFound(String property) {
    String message = String.format("Property not found: %s", property);

    if (reportToConsole) {
      logger.warn(message);
    }

    MigrationIssue issue =
        createIssue(IssueType.MISSING_PROPERTY, Severity.WARNING)
            .property(property)
            .message(message)
            .recommendation(
                String.format(
                    "Check if property %s should be mapped to a different name", property))
            .build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Reports a state transition issue.
   *
   * @param fromState The from state
   * @param toState The to state
   * @param reason The reason for the issue
   * @return The created MigrationIssue
   */
  public MigrationIssue reportStateTransitionIssue(
      String fromState, String toState, String reason) {
    String message =
        String.format("Invalid state transition from %s to %s: %s", fromState, toState, reason);

    if (reportToConsole) {
      logger.warn(message);
    }

    MigrationIssue issue =
        createIssue(IssueType.STATE_TRANSITION, Severity.WARNING)
            .message(message)
            .legacyValue(fromState)
            .s8rValue(toState)
            .recommendation(
                String.format(
                    "Ensure proper state transition path from %s to %s", fromState, toState))
            .build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Reports a reflection error.
   *
   * @param target The target of the reflection operation
   * @param exception The exception that occurred
   * @return The created MigrationIssue
   */
  public MigrationIssue reportReflectionError(String target, Exception exception) {
    String message =
        String.format("Reflection error accessing %s: %s", target, exception.getMessage());

    if (reportToConsole) {
      logger.error(message);
    }

    StringWriter sw = new StringWriter();
    exception.printStackTrace(new PrintWriter(sw));
    String stackTrace = sw.toString();

    MigrationIssue issue =
        createIssue(IssueType.REFLECTION_ERROR, Severity.ERROR)
            .message(message)
            .property(target)
            .stackTrace(stackTrace)
            .build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Reports a method mapping issue.
   *
   * @param legacyMethod The legacy method
   * @param s8rMethod The S8r method
   * @param reason The reason for the issue
   * @return The created MigrationIssue
   */
  public MigrationIssue reportMethodMappingIssue(
      String legacyMethod, String s8rMethod, String reason) {
    String message =
        String.format(
            "Method mapping issue between %s and %s: %s", legacyMethod, s8rMethod, reason);

    if (reportToConsole) {
      logger.warn(message);
    }

    MigrationIssue issue =
        createIssue(IssueType.METHOD_MAPPING, Severity.WARNING)
            .message(message)
            .legacyValue(legacyMethod)
            .s8rValue(s8rMethod)
            .build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Reports a structural difference.
   *
   * @param legacyStructure The legacy structure
   * @param s8rStructure The S8r structure
   * @return The created MigrationIssue
   */
  public MigrationIssue reportStructuralDifference(String legacyStructure, String s8rStructure) {
    String message = String.format("Structural difference between legacy and S8r components");

    if (reportToConsole) {
      logger.info(message);
    }

    MigrationIssue issue =
        createIssue(IssueType.STRUCTURAL_DIFFERENCE, Severity.INFO)
            .message(message)
            .legacyValue(legacyStructure)
            .s8rValue(s8rStructure)
            .build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Reports a custom migration issue.
   *
   * @param type The issue type
   * @param severity The issue severity
   * @param message The issue message
   * @return The created MigrationIssue
   */
  public MigrationIssue reportCustomIssue(IssueType type, Severity severity, String message) {
    if (reportToConsole) {
      switch (severity) {
        case DEBUG:
          logger.debug(message);
          break;
        case INFO:
          logger.info(message);
          break;
        case WARNING:
          logger.warn(message);
          break;
        case ERROR:
        case CRITICAL:
          logger.error(message);
          break;
      }
    }

    MigrationIssue issue = createIssue(type, severity).message(message).build();

    MigrationFeedback.reportIssue(issue);
    return issue;
  }

  /**
   * Creates a new MigrationIssue.Builder with common fields pre-populated.
   *
   * @param type The issue type
   * @param severity The issue severity
   * @return A new MigrationIssue.Builder
   */
  private MigrationIssue.Builder createIssue(IssueType type, Severity severity) {
    // Get caller information for source tracking
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    String sourceClass = null;
    String sourceMethod = null;

    if (stackTrace.length > 3) {
      // Index 0 is getStackTrace(), 1 is createIssue(), 2 is the reporting method
      // So 3 should be the actual caller
      StackTraceElement caller = stackTrace[3];
      sourceClass = caller.getClassName();
      sourceMethod = caller.getMethodName();
    }

    Map<String, String> context = new HashMap<>();
    if (sourceClass != null) {
      int lastDot = sourceClass.lastIndexOf('.');
      if (lastDot > 0) {
        context.put("package", sourceClass.substring(0, lastDot));
        context.put("class", sourceClass.substring(lastDot + 1));
      }
    }

    return new MigrationIssue.Builder()
        .type(type)
        .severity(severity)
        .component(category)
        .sourceClass(sourceClass)
        .sourceMethod(sourceMethod)
        .addContext(context);
  }
}
