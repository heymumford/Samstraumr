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
 * Data Transfer Objects (DTOs) for external communication.
 * 
 * <p>This package contains DTOs used for communication between the application layer and
 * outer layers (adapters and UI). DTOs are simple data containers with no behavior, designed
 * to transfer data across architectural boundaries.
 * 
 * <p>Key responsibilities of DTOs:
 * <ul>
 *   <li>Provide simplified representations of domain entities</li>
 *   <li>Decouple external interfaces from domain model changes</li>
 *   <li>Facilitate data transfer across architectural boundaries</li>
 *   <li>Hide internal domain details from external clients</li>
 * </ul>
 * 
 * <p>Key DTO classes:
 * <ul>
 *   <li>{@link org.s8r.application.dto.ComponentDto} - External representation of a Component</li>
 *   <li>{@link org.s8r.application.dto.CompositeComponentDto} - External representation of a Composite</li>
 *   <li>{@link org.s8r.application.dto.ConnectionDto} - External representation of a Connection</li>
 *   <li>{@link org.s8r.application.dto.MachineDto} - External representation of a Machine</li>
 * </ul>
 * 
 * <p>In Clean Architecture, DTOs are typically defined in the application layer to provide
 * a clear boundary between the domain model and external interfaces. This separation allows
 * the domain model to evolve independently of the external interfaces.
 */
package org.s8r.application.dto;