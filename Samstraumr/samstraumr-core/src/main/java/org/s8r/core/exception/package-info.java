/**
 * Centralized exception classes for the S8r framework.
 *
 * <p>This package contains the exception hierarchy for the S8r framework, providing a consistent
 * approach to error handling. It is part of the package simplification initiative, consolidating
 * exceptions that were previously scattered across multiple packages.
 *
 * <p>The exceptions in this package follow these principles:
 *
 * <ul>
 *   <li>Meaningful names that describe the failure condition
 *   <li>Consistent constructors (message, cause, message+cause)
 *   <li>Detailed exception messages for easier troubleshooting
 *   <li>Proper inheritance hierarchy to enable specific exception handling
 * </ul>
 *
 * <p>When adding new exceptions, follow these guidelines:
 * <ul>
 *   <li>Use clear, descriptive names that indicate the type of failure
 *   <li>Include the standard three constructors
 *   <li>Document common causes and recovery strategies
 *   <li>Place in the proper location in the exception hierarchy
 * </ul>
 */
package org.s8r.core.exception;