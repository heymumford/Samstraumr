/**
 * Inbound/primary adapter package for the hexagonal architecture.
 * <p>
 * This package contains all inbound adapters that allow external actors to
 * interact with the application. These adapters convert external requests into
 * calls to the application layer services and ports.
 * </p>
 * <p>
 * Subpackages include:
 * <ul>
 *   <li>cli - Command-line interface adapters</li>
 * </ul>
 * </p>
 * <p>
 * As part of the adapter layer in Clean Architecture, this package depends on the
 * application layer but not vice versa. These adapters represent the "driving" or
 * primary side of the hexagonal architecture.
 * </p>
 */
package org.s8r.adapter.in;