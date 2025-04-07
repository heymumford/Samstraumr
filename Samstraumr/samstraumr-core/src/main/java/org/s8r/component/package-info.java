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
 * Component layer of the Samstraumr framework.
 * 
 * <p>This package and its subpackages contain the core component abstractions
 * that form the foundation of the Samstraumr framework. Components are the
 * building blocks of the system, and they follow a biological-inspired lifecycle
 * model.</p>
 * 
 * <h2>Subpackages</h2>
 * <ul>
 *   <li>{@link org.s8r.component.core} - Core component interfaces and abstractions</li>
 *   <li>{@link org.s8r.component.composite} - Composite component implementation</li>
 *   <li>{@link org.s8r.component.identity} - Component identity management</li>
 *   <li>{@link org.s8r.component.machine} - Machine component implementation</li>
 *   <li>{@link org.s8r.component.exception} - Component-related exceptions</li>
 *   <li>{@link org.s8r.component.logging} - Component logging</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Domain model implementation of components</li>
 *   <li>{@link org.s8r.application.service} - Application services that use components</li>
 *   <li>{@link org.s8r.infrastructure.persistence} - Repository implementations for components</li>
 * </ul>
 * 
 * <p>For detailed information about the component layer organization and design principles,
 * see the README.md file in this package directory.</p>
 */
package org.s8r.component;
