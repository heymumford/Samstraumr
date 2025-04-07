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
 * Machine component implementation for component orchestration.
 * 
 * <p>This package contains classes that implement the Machine pattern,
 * which provides orchestration and state management for groups of components.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.component.machine.Machine} - Component orchestration</li>
 *   <li>{@link org.s8r.component.machine.MachineFactory} - Factory for creating machines</li>
 *   <li>{@link org.s8r.component.machine.MachineException} - Exceptions related to machine operations</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.component.core} - Core component abstractions</li>
 *   <li>{@link org.s8r.component.composite} - Composite component implementation</li>
 *   <li>{@link org.s8r.domain.machine} - Domain model for machines</li>
 * </ul>
 */
package org.s8r.component.machine;
