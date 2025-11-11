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

/**
 * The migration feedback package provides tools for monitoring, tracking, and reporting issues that
 * occur during migration from Samstraumr to S8r.
 *
 * <p>This package includes:
 *
 * <ul>
 *   <li>MigrationIssue: Represents a specific migration issue with metadata
 *   <li>MigrationIssueLogger: Logs migration issues for specific components
 *   <li>MigrationFeedback: Central API for accessing migration feedback
 *   <li>MigrationRecommendation: Recommendations for addressing common issues
 *   <li>MigrationStats: Statistics about migration issues
 * </ul>
 *
 * <p>Usage Example:
 *
 * <pre>
 * // Create a migration issue logger for your component
 * MigrationIssueLogger logger = MigrationIssueLogger.forCategory("MyComponent");
 *
 * // Report issues when they occur
 * logger.reportTypeMismatch("status", "TubeStatus", "State");
 * logger.reportPropertyNotFound("metadata.lifecycle");
 *
 * // Get statistics and recommendations
 * MigrationStats stats = MigrationFeedback.getStats();
 * List&lt;MigrationRecommendation&gt; recommendations = MigrationFeedback.getRecommendations();
 * </pre>
 *
 * <p>This package follows Clean Architecture principles by providing a domain-focused API for
 * migration feedback that is independent of the specific migration details.
 */
package org.s8r.migration.feedback;
