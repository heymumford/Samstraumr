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
 * Core component abstractions that form the foundation of the Samstraumr framework.
 *
 * <p>This package contains the fundamental interfaces and classes that define what a component is
 * and how it behaves within the system. The core classes here are used throughout the codebase.
 *
 * <h2>Key Classes</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.component.core.Component} - The fundamental component interface
 *   <li>{@link org.s8r.component.core.Environment} - Environment configuration for components
 *   <li>{@link org.s8r.component.core.State} - Component state management
 * </ul>
 *
 * <h2>Related Packages</h2>
 *
 * <ul>
 *   <li>{@link org.s8r.component.composite} - Composite component implementation
 *   <li>{@link org.s8r.component.identity} - Component identity management
 *   <li>{@link org.s8r.component.machine} - Machine component implementation
 * </ul>
 */
package org.s8r.component.core;
