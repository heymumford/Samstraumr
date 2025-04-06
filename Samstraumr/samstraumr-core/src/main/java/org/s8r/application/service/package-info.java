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
 * Application services (use cases) that implement the business logic of the application.
 * 
 * <p>This package contains service classes that implement the application's use cases. Each service
 * class represents a set of related use cases for interacting with domain entities. These services
 * coordinate the activities of domain entities and interact with ports to access external resources.
 * 
 * <p>Key responsibilities of application services:
 * <ul>
 *   <li>Implement use cases by coordinating domain entities</li>
 *   <li>Interact with ports to access external resources (repositories, event handlers, etc.)</li>
 *   <li>Apply transaction boundaries and enforce high-level application rules</li>
 *   <li>Convert between domain entities and DTOs for external communication</li>
 * </ul>
 * 
 * <p>Key service classes:
 * <ul>
 *   <li>{@link org.s8r.application.service.ComponentService} - For creating and managing components</li>
 *   <li>{@link org.s8r.application.service.MachineService} - For creating and managing machines</li>
 *   <li>{@link org.s8r.application.service.DataFlowService} - For handling data flow between components</li>
 * </ul>
 * 
 * <p>In Clean Architecture, application services are part of the application layer, which
 * sits between the domain layer and the infrastructure/adapter layers. Services depend on
 * the domain layer for entity definitions and on ports for external resource access. This
 * organization enforces the Dependency Inversion Principle.
 */
package org.s8r.application.service;