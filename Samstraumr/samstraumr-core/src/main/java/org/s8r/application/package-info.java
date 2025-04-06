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
 * Application layer for the Samstraumr framework.
 * 
 * <p>This package contains application-specific business rules and orchestrates the flow of data
 * between the domain layer and the outer infrastructure and adapter layers. It defines interfaces
 * (ports) that outer layers must implement, and contains the use cases of the system.
 * 
 * <p>Key components in this layer:
 * <ul>
 *   <li>Services: Implement use cases that operate on domain entities</li>
 *   <li>DTOs: Data Transfer Objects for moving data across layer boundaries</li>
 *   <li>Ports: Interfaces that outer layers implement, enabling Dependency Inversion</li>
 *   <li>ServiceLocator: Breaks circular dependencies while preserving Clean Architecture</li>
 * </ul>
 * 
 * <p>This layer depends only on the domain layer and follows Clean Architecture principles
 * by not having any dependencies on infrastructure, adapters, or UI components.
 */
package org.s8r.application;