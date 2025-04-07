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
 * Provides step definitions for BDD testing of S8r components using Cucumber.
 * 
 * <p>This package contains classes that implement step definitions for Gherkin
 * scenarios in Cucumber BDD tests. Each class focuses on a specific area of
 * the S8r framework:
 * 
 * <ul>
 *   <li>{@link org.s8r.test.cucumber.steps.ComponentSteps} - Steps for testing Components
 *   <li>CompositeSteps - Steps for testing Composites
 *   <li>MachineSteps - Steps for testing Machines
 * </ul>
 * 
 * <p>These step definition classes work with feature files located in the
 * src/test/resources/core/features directory and are executed by the
 * {@link org.s8r.test.cucumber.S8rCucumberRunner}.
 * 
 * <p>All step classes use a shared {@link org.s8r.test.cucumber.context.TestContext}
 * instance to maintain state between steps in a scenario.
 */
package org.s8r.test.cucumber.steps;