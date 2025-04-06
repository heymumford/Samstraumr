/**
 * Infrastructure logging package provides logging implementations and utilities.
 * <p>
 * This package contains concrete logging implementations that fulfill the logging interfaces
 * defined in the domain and application layers. It includes adapters for popular logging
 * frameworks like SLF4J and Log4j, as well as console-based logging implementations.
 * Following Clean Architecture principles, this package depends on inner layer interfaces
 * but does not expose implementation details to those layers.
 */
package org.s8r.infrastructure.logging;