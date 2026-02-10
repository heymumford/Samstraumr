/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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
 * Provides the core machine framework for the S8r system.
 *
 * <p>The machine package contains classes that represent the highest level of composition in the
 * S8r architecture. Machines manage and orchestrate multiple Composite components to form complete
 * processing systems.
 *
 * <p>The primary classes in this package include:
 *
 * <ul>
 *   <li>{@link org.s8r.core.machine.Machine} - Represents a machine that coordinates multiple
 *       composite components
 *   <li>{@link org.s8r.core.machine.MachineFactory} - Factory for creating and managing machine
 *       instances
 *   <li>{@link org.s8r.core.machine.MachineState} - Enum representing the possible states of a
 *       machine
 *   <li>{@link org.s8r.core.machine.MachineType} - Enum representing the different types of
 *       machines
 *   <li>{@link org.s8r.core.machine.MachineException} - Exception thrown when a machine encounters
 *       an error
 * </ul>
 *
 * <p>Usage example:
 *
 * <pre>{@code
 * // Create an environment
 * Environment env = Environment.create();
 *
 * // Create a data processor machine
 * Machine machine = MachineFactory.createDataProcessor("MyProcessor", "Processes data", env);
 *
 * // Initialize and start the machine
 * machine.initialize();
 * machine.start();
 *
 * // Perform operations with the machine...
 *
 * // Stop and destroy the machine when done
 * machine.stop();
 * machine.destroy();
 * }</pre>
 *
 * <p>Machines follow a defined lifecycle represented by the {@link
 * org.s8r.core.machine.MachineState} enum, transitioning through states like CREATED, READY,
 * RUNNING, STOPPED, and DESTROYED.
 */
package org.s8r.core.machine;
