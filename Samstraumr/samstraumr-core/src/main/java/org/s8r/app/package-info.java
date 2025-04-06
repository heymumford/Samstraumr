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
 * Framework and drivers layer for the Samstraumr framework (Clean Architecture).
 * 
 * <p>This package contains the outermost layer of the Clean Architecture, 
 * providing entry points to the application and implementations of delivery mechanisms
 * such as CLI interfaces, web UIs, or API endpoints.
 * 
 * <p>Components in this layer:
 * <ul>
 *   <li>CliApplication: A command-line interface for interacting with the framework</li>
 *   <li>Main application entry points</li>
 *   <li>External framework integrations</li>
 * </ul>
 * 
 * <p>This layer interacts with the application layer through the defined ports/interfaces
 * and never directly with the domain or infrastructure layers. This ensures proper 
 * separation of concerns and adherence to Clean Architecture principles.
 * 
 * <p>The app layer uses the Service Locator pattern to obtain services without directly
 * depending on the infrastructure layer, breaking potential circular dependencies.
 */
package org.s8r.app;