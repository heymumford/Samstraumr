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

/**
 * Provides test context classes for managing state in Cucumber BDD tests.
 *
 * <p>This package contains classes that implement the Context Object pattern for Cucumber BDD tests
 * in the S8r framework. These classes maintain state between step definitions, allowing different
 * step classes to share data during test execution.
 *
 * <p>The primary class in this package is {@link org.s8r.test.cucumber.context.TestContext}, which
 * provides storage and management for components, composites, machines, and other test-related
 * objects.
 *
 * <p>Context objects are typically:
 *
 * <ul>
 *   <li>Created at the start of a scenario
 *   <li>Shared among step definition classes using dependency injection
 *   <li>Used to store and retrieve objects during the scenario
 *   <li>Cleaned up at the end of the scenario
 * </ul>
 */
package org.s8r.test.cucumber.context;
