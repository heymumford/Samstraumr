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
 * Machine domain model in the Samstraumr framework.
 * 
 * <p>This package contains the domain model for machines, which provide
 * orchestration and state management for groups of components.</p>
 * 
 * <h2>Key Classes</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.machine.Machine} - Domain machine entity</li>
 *   <li>{@link org.s8r.domain.machine.MachineFactory} - Factory for machine creation</li>
 *   <li>{@link org.s8r.domain.machine.MachineState} - Machine states</li>
 *   <li>{@link org.s8r.domain.machine.MachineType} - Types of machines</li>
 * </ul>
 * 
 * <h2>Related Packages</h2>
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Component domain model used by machines</li>
 *   <li>{@link org.s8r.domain.event} - Domain events related to machines</li>
 *   <li>{@link org.s8r.component.machine} - Machine abstraction layer</li>
 * </ul>
 */
package org.s8r.domain.machine;
