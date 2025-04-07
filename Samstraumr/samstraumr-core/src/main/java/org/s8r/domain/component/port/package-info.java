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
 * Port interfaces for the component domain model - a key part of the Clean Architecture implementation.
 * <p>
 * This package contains the port interfaces that define the contracts for components in the
 * domain layer. These interfaces are a crucial part of implementing the Dependency Inversion
 * Principle (DIP) from SOLID principles and Clean Architecture.
 * </p>
 * <p>
 * Key interfaces in this package:
 * <ul>
 *   <li>{@link org.s8r.domain.component.port.ComponentPort} - Base interface for all components</li>
 *   <li>{@link org.s8r.domain.component.port.CompositeComponentPort} - Interface for composite components that can contain other components</li>
 *   <li>{@link org.s8r.domain.component.port.MachinePort} - Interface for machines that orchestrate composites</li>
 * </ul>
 * </p>
 * <p>
 * Benefits of using these port interfaces:
 * <ul>
 *   <li>Higher-level modules (application layer) depend on abstractions (these interfaces) rather than concrete implementations</li>
 *   <li>Domain rules can be expressed through these interfaces while implementations can vary</li>
 *   <li>Legacy implementations can be adapted to these interfaces without changing the domain model</li>
 *   <li>Testing is simplified since mock implementations can be provided</li>
 *   <li>The system becomes more modular and maintainable</li>
 * </ul>
 * </p>
 * <p>
 * Implementation notes:
 * <ul>
 *   <li>The adapter package provides adapter implementations that convert between concrete domain entities and these interfaces</li>
 *   <li>Repositories in the application layer work with these port interfaces rather than concrete implementations</li>
 *   <li>Domain events can be raised by both concrete implementations and port interface implementations</li>
 *   <li>Infrastructure components only depend on these interfaces, not on concrete domain implementations</li>
 * </ul>
 * </p>
 * <p>
 * Related packages:
 * <ul>
 *   <li>{@link org.s8r.domain.component} - Concrete component implementations</li>
 *   <li>{@link org.s8r.adapter} - Adapters that convert between domain entities and these port interfaces</li>
 *   <li>{@link org.s8r.application.port} - Application layer ports that use these domain interfaces</li>
 *   <li>{@link org.s8r.infrastructure.persistence} - Infrastructure implementations that store and retrieve these interfaces</li>
 * </ul>
 * </p>
 */
package org.s8r.domain.component.port;