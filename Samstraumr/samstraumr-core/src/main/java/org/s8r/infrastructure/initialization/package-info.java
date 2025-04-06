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
 * Infrastructure layer implementation for project initialization.
 * <p>
 * This package provides the concrete implementations of the project initialization
 * interfaces defined in the application layer. It contains the classes responsible
 * for initializing new projects, setting up directory structures, and creating
 * configuration files.
 * </p>
 * <p>
 * Key components in this package:
 * <ul>
 *   <li>FileSystemProjectInitializer - Implementation that initializes projects on the file system</li>
 * </ul>
 * </p>
 * <p>
 * This package is part of the infrastructure layer in the Clean Architecture structure
 * and depends on the application and domain layers. It provides implementations
 * of interfaces defined in the application layer.
 * </p>
 */
package org.s8r.infrastructure.initialization;