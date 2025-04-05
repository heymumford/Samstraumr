/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/**
 * Test runners for the S8r framework, providing a consolidated location for all test execution
 * configurations.
 *
 * <p>This package is part of the package structure simplification initiative, centralizing all test
 * runners into a single package under the simplified org.s8r root package.
 *
 * <p>The runners in this package simplify test execution in various scenarios:
 *
 * <ul>
 *   <li>General Cucumber test execution (CucumberRunner)
 *   <li>Critical tests only (CriticalTestRunner)
 *   <li>Domain-specific test groups (OrchestrationTestRunner, etc.)
 * </ul>
 *
 * <p>When adding new runners to this package, follow these guidelines:
 *
 * <ul>
 *   <li>Use clear, descriptive names
 *   <li>Extend base runners where possible to maintain configuration consistency
 *   <li>Include detailed JavaDoc explaining the purpose and usage
 *   <li>Annotate with appropriate test tags and configuration parameters
 * </ul>
 */
package org.s8r.test.runner;
