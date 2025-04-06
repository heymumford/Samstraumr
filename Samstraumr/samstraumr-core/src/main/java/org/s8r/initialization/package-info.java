/**
 * Initialization package provides system bootstrapping and startup functionality.
 * <p>
 * This package contains utilities and classes for initializing the Samstraumr system,
 * including configuration loading, dependency injection setup, and component registration.
 * The initialization package bridges between the application's entry points and the core
 * system functionality.
 * 
 * @deprecated This package does not conform to Clean Architecture principles and should
 *             be refactored. Initialization logic should be moved to the appropriate
 *             layers based on responsibilities, with infrastructure concerns in the
 *             infrastructure layer and use case initialization in the application layer.
 */
package org.s8r.initialization;