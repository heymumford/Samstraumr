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
 * Application ports (interfaces) that define boundaries between the application and other layers.
 * 
 * <p>This package contains interfaces that define how the application layer interacts with
 * outer layers (infrastructure and adapters) and how outer layers interact with the application.
 * These interfaces follow the Ports and Adapters pattern from Hexagonal Architecture.
 * 
 * <p>Key types of ports:
 * <ul>
 *   <li>Primary (Driven) Ports: Interfaces that allow the application to drive secondary actors</li>
 *   <li>Secondary (Driving) Ports: Interfaces that allow the application to be driven by primary actors</li>
 * </ul>
 * 
 * <p>Examples of primary ports:
 * <ul>
 *   <li>{@link org.s8r.application.port.ComponentRepository} - For persisting components</li>
 *   <li>{@link org.s8r.application.port.MachineRepository} - For persisting machines</li>
 *   <li>{@link org.s8r.application.port.EventDispatcher} - For dispatching events</li>
 *   <li>{@link org.s8r.application.port.LoggerPort} - For logging</li>
 * </ul>
 * 
 * <p>Examples of secondary ports:
 * <ul>
 *   <li>{@link org.s8r.application.port.S8rFacade} - The primary API for using the framework</li>
 *   <li>{@link org.s8r.application.port.ServiceFactory} - Factory for accessing application services</li>
 * </ul>
 * 
 * <p>In Clean Architecture, these interfaces are defined in the application layer, but implemented
 * in outer layers (e.g., infrastructure for primary ports, adapters for secondary ports). This follows
 * the Dependency Inversion Principle, as the application defines what it needs without depending
 * on specific implementations.
 */
package org.s8r.application.port;