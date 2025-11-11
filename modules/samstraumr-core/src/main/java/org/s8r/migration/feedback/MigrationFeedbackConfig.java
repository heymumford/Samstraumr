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

/** Configuration for the migration feedback system. */
public class MigrationFeedbackConfig {

  private final Severity minSeverity;
  private final int maxIssuesPerComponent;
  private final boolean enableRecommendations;
  private final int recommendationThreshold;
  private final String reportOutputPath;

  private MigrationFeedbackConfig(Builder builder) {
    this.minSeverity = builder.minSeverity;
    this.maxIssuesPerComponent = builder.maxIssuesPerComponent;
    this.enableRecommendations = builder.enableRecommendations;
    this.recommendationThreshold = builder.recommendationThreshold;
    this.reportOutputPath = builder.reportOutputPath;
  }

  /**
   * Gets the minimum severity of issues to report.
   *
   * @return The minimum severity
   */
  public Severity getMinSeverity() {
    return minSeverity;
  }

  /**
   * Gets the maximum number of issues to report per component.
   *
   * @return The maximum number of issues per component
   */
  public int getMaxIssuesPerComponent() {
    return maxIssuesPerComponent;
  }

  /**
   * Gets whether to enable recommendations.
   *
   * @return Whether recommendations are enabled
   */
  public boolean isEnableRecommendations() {
    return enableRecommendations;
  }

  /**
   * Gets the threshold for generating recommendations.
   *
   * @return The recommendation threshold
   */
  public int getRecommendationThreshold() {
    return recommendationThreshold;
  }

  /**
   * Gets the path to write reports to.
   *
   * @return The report output path
   */
  public String getReportOutputPath() {
    return reportOutputPath;
  }

  /** Builder for MigrationFeedbackConfig. */
  public static class Builder {
    private Severity minSeverity = Severity.INFO;
    private int maxIssuesPerComponent = 100;
    private boolean enableRecommendations = true;
    private int recommendationThreshold = 3;
    private String reportOutputPath = "migration-report";

    /**
     * Sets the minimum severity of issues to report.
     *
     * @param minSeverity The minimum severity
     * @return This builder
     */
    public Builder setMinSeverity(Severity minSeverity) {
      this.minSeverity = minSeverity;
      return this;
    }

    /**
     * Sets the maximum number of issues to report per component.
     *
     * @param maxIssuesPerComponent The maximum number of issues per component
     * @return This builder
     */
    public Builder setMaxIssuesPerComponent(int maxIssuesPerComponent) {
      this.maxIssuesPerComponent = maxIssuesPerComponent;
      return this;
    }

    /**
     * Sets whether to enable recommendations.
     *
     * @param enableRecommendations Whether recommendations are enabled
     * @return This builder
     */
    public Builder enableRecommendations(boolean enableRecommendations) {
      this.enableRecommendations = enableRecommendations;
      return this;
    }

    /**
     * Sets the threshold for generating recommendations.
     *
     * @param recommendationThreshold The recommendation threshold
     * @return This builder
     */
    public Builder setRecommendationThreshold(int recommendationThreshold) {
      this.recommendationThreshold = recommendationThreshold;
      return this;
    }

    /**
     * Sets the path to write reports to.
     *
     * @param reportOutputPath The report output path
     * @return This builder
     */
    public Builder setReportOutputPath(String reportOutputPath) {
      this.reportOutputPath = reportOutputPath;
      return this;
    }

    /**
     * Builds a new MigrationFeedbackConfig.
     *
     * @return A new MigrationFeedbackConfig
     */
    public MigrationFeedbackConfig build() {
      return new MigrationFeedbackConfig(this);
    }
  }
}
