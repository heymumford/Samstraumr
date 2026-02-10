/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/**
 * CLI adapter package for the inbound/primary side of the hexagonal architecture.
 *
 * <p>This package contains adapters that allow users to interact with the application through a
 * command-line interface. These adapters convert CLI commands into calls to the application layer,
 * and present application responses back to the user.
 *
 * <p>Key components in this package:
 *
 * <ul>
 *   <li>ComponentCliAdapter - CLI adapter for component management
 * </ul>
 *
 * <p>As part of the adapter layer in Clean Architecture, this package depends on the application
 * layer but not vice versa. It adapts external CLI requests to the internal application services
 * and ports.
 */
package org.s8r.adapter.in.cli;
