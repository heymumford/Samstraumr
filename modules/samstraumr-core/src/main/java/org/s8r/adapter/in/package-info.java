/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * Inbound/primary adapter package for the hexagonal architecture.
 *
 * <p>This package contains all inbound adapters that allow external actors to interact with the
 * application. These adapters convert external requests into calls to the application layer
 * services and ports.
 *
 * <p>Subpackages include:
 *
 * <ul>
 *   <li>cli - Command-line interface adapters
 * </ul>
 *
 * <p>As part of the adapter layer in Clean Architecture, this package depends on the application
 * layer but not vice versa. These adapters represent the "driving" or primary side of the hexagonal
 * architecture.
 */
package org.s8r.adapter.in;
