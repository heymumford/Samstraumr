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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a migration issue detected during runtime. MigrationIssue instances are created by the
 * MigrationIssueLogger and stored in the MigrationFeedback system.
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

  /** Creates a new MigrationIssue. */
  private MigrationIssue(Builder builder) {
    this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
    this.severity = builder.severity;
    this.type = builder.type;
    this.component = builder.component;
    this.property = builder.property;
    this.message = builder.message;
    this.legacyValue = builder.legacyValue;
    this.s8rValue = builder.s8rValue;
    this.stackTrace = builder.stackTrace;
    this.timestamp = builder.timestamp != 0 ? builder.timestamp : Instant.now().toEpochMilli();
    this.recommendation = builder.recommendation;
    this.sourceClass = builder.sourceClass;
    this.sourceMethod = builder.sourceMethod;
    this.context = new HashMap<>(builder.context);
  }

  /**
   * Gets the unique ID of this issue.
   *
   * @return The issue ID
   */
  public String getId() {
    return id;
  }

  /**
   * Gets the severity of this issue.
   *
   * @return The issue severity
   */
  public Severity getSeverity() {
    return severity;
  }

  /**
   * Gets the type of this issue.
   *
   * @return The issue type
   */
  public IssueType getType() {
    return type;
  }

  /**
   * Gets the component associated with this issue.
   *
   * @return The component name
   */
  public String getComponent() {
    return component;
  }

  /**
   * Gets the property associated with this issue.
   *
   * @return The property name
   */
  public String getProperty() {
    return property;
  }

  /**
   * Gets the message describing this issue.
   *
   * @return The issue message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the legacy value associated with this issue.
   *
   * @return The legacy value
   */
  public String getLegacyValue() {
    return legacyValue;
  }

  /**
   * Gets the S8r value associated with this issue.
   *
   * @return The S8r value
   */
  public String getS8rValue() {
    return s8rValue;
  }

  /**
   * Gets the stack trace for this issue.
   *
   * @return The stack trace
   */
  public String getStackTrace() {
    return stackTrace;
  }

  /**
   * Gets the timestamp when this issue was recorded.
   *
   * @return The timestamp in milliseconds since epoch
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * Gets the recommendation for resolving this issue.
   *
   * @return The recommendation
   */
  public String getRecommendation() {
    return recommendation;
  }

  /**
   * Gets the source class associated with this issue.
   *
   * @return The source class name
   */
  public String getSourceClass() {
    return sourceClass;
  }

  /**
   * Gets the source method associated with this issue.
   *
   * @return The source method name
   */
  public String getSourceMethod() {
    return sourceMethod;
  }

  /**
   * Gets the context map for this issue.
   *
   * @return The context map
   */
  public Map<String, String> getContext() {
    return new HashMap<>(context);
  }

  /**
   * Gets a specific context value.
   *
   * @param key The context key
   * @return The context value, or null if not found
   */
  public String getContextValue(String key) {
    return context.get(key);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MigrationIssue that = (MigrationIssue) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
        "%s [%s] %s: %s",
        severity, type, component != null ? component : "", message != null ? message : "");
  }

  /**
   * Returns a detailed string representation of this issue.
   *
   * @return A detailed string
   */
  public String toDetailedString() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("Migration Issue: %s%n", id));
    sb.append(String.format("Type: %s, Severity: %s%n", type, severity));
    sb.append(
        String.format(
            "Component: %s, Property: %s%n",
            component != null ? component : "N/A", property != null ? property : "N/A"));
    sb.append(String.format("Message: %s%n", message != null ? message : ""));

    if (legacyValue != null) {
      sb.append(String.format("Legacy Value: %s%n", legacyValue));
    }

    if (s8rValue != null) {
      sb.append(String.format("S8r Value: %s%n", s8rValue));
    }

    if (recommendation != null) {
      sb.append(String.format("Recommendation: %s%n", recommendation));
    }

    if (sourceClass != null) {
      sb.append(
          String.format("Source: %s.%s%n", sourceClass, sourceMethod != null ? sourceMethod : ""));
    }

    if (!context.isEmpty()) {
      sb.append("Context:%n");
      for (Map.Entry<String, String> entry : context.entrySet()) {
        sb.append(String.format("  %s: %s%n", entry.getKey(), entry.getValue()));
      }
    }

    return sb.toString();
  }

  /** Builder for creating MigrationIssue instances. */
  public static class Builder {
    private String id;
    private Severity severity = Severity.INFO;
    private IssueType type = IssueType.UNKNOWN;
    private String component;
    private String property;
    private String message;
    private String legacyValue;
    private String s8rValue;
    private String stackTrace;
    private long timestamp;
    private String recommendation;
    private String sourceClass;
    private String sourceMethod;
    private final Map<String, String> context = new HashMap<>();

    /**
     * Sets the issue ID.
     *
     * @param id The issue ID
     * @return This builder
     */
    public Builder id(String id) {
      this.id = id;
      return this;
    }

    /**
     * Sets the issue severity.
     *
     * @param severity The issue severity
     * @return This builder
     */
    public Builder severity(Severity severity) {
      this.severity = severity;
      return this;
    }

    /**
     * Sets the issue type.
     *
     * @param type The issue type
     * @return This builder
     */
    public Builder type(IssueType type) {
      this.type = type;
      return this;
    }

    /**
     * Sets the component associated with this issue.
     *
     * @param component The component name
     * @return This builder
     */
    public Builder component(String component) {
      this.component = component;
      return this;
    }

    /**
     * Sets the property associated with this issue.
     *
     * @param property The property name
     * @return This builder
     */
    public Builder property(String property) {
      this.property = property;
      return this;
    }

    /**
     * Sets the message describing this issue.
     *
     * @param message The issue message
     * @return This builder
     */
    public Builder message(String message) {
      this.message = message;
      return this;
    }

    /**
     * Sets the legacy value associated with this issue.
     *
     * @param legacyValue The legacy value
     * @return This builder
     */
    public Builder legacyValue(String legacyValue) {
      this.legacyValue = legacyValue;
      return this;
    }

    /**
     * Sets the S8r value associated with this issue.
     *
     * @param s8rValue The S8r value
     * @return This builder
     */
    public Builder s8rValue(String s8rValue) {
      this.s8rValue = s8rValue;
      return this;
    }

    /**
     * Sets the stack trace for this issue.
     *
     * @param stackTrace The stack trace
     * @return This builder
     */
    public Builder stackTrace(String stackTrace) {
      this.stackTrace = stackTrace;
      return this;
    }

    /**
     * Sets the timestamp when this issue was recorded.
     *
     * @param timestamp The timestamp in milliseconds since epoch
     * @return This builder
     */
    public Builder timestamp(long timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    /**
     * Sets the recommendation for resolving this issue.
     *
     * @param recommendation The recommendation
     * @return This builder
     */
    public Builder recommendation(String recommendation) {
      this.recommendation = recommendation;
      return this;
    }

    /**
     * Sets the source class associated with this issue.
     *
     * @param sourceClass The source class name
     * @return This builder
     */
    public Builder sourceClass(String sourceClass) {
      this.sourceClass = sourceClass;
      return this;
    }

    /**
     * Sets the source method associated with this issue.
     *
     * @param sourceMethod The source method name
     * @return This builder
     */
    public Builder sourceMethod(String sourceMethod) {
      this.sourceMethod = sourceMethod;
      return this;
    }

    /**
     * Adds a context entry to this issue.
     *
     * @param key The context key
     * @param value The context value
     * @return This builder
     */
    public Builder addContext(String key, String value) {
      this.context.put(key, value);
      return this;
    }

    /**
     * Adds multiple context entries to this issue.
     *
     * @param context The context map
     * @return This builder
     */
    public Builder addContext(Map<String, String> context) {
      this.context.putAll(context);
      return this;
    }

    /**
     * Builds a new MigrationIssue instance.
     *
     * @return A new MigrationIssue
     */
    public MigrationIssue build() {
      return new MigrationIssue(this);
    }
  }
}
