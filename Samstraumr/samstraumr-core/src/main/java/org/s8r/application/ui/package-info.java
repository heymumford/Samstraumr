/**
 * Application UI package for user interfaces within the application layer.
 * <p>
 * This package contains user interface components that are part of the application
 * layer in the Clean Architecture. These UI components use the application's
 * services and ports to provide user interaction with the system.
 * </p>
 * <p>
 * Key components in this package:
 * <ul>
 *   <li>CliApplication - A command-line interface application</li>
 * </ul>
 * </p>
 * <p>
 * As part of the application layer, this package can depend on other application
 * layer components but should not depend on infrastructure or adapter implementations
 * directly.
 * </p>
 */
package org.s8r.application.ui;