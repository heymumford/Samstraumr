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
 * Composite component implementation for creating component hierarchies.
 * 
 * <p>This package contains classes that implement the Composite pattern,
 * allowing individual components to be grouped and treated as a single unit.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.component.composite.Composite} - Container for multiple components</li>
 *   <li>{@link org.s8r.component.composite.CompositeFactory} - Factory for creating composite components</li>
 *   <li>{@link org.s8r.component.composite.CompositeException} - Exceptions related to composite operations</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.component.core} - Core component abstractions</li>
 *   <li>{@link org.s8r.component.machine} - Machine component implementation</li>
 *   <li>{@link org.s8r.domain.component.composite} - Domain model for composites</li>
 * </ul>
 */
package org.s8r.component.composite;
