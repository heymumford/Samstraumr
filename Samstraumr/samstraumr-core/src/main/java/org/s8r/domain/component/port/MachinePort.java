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

package org.s8r.domain.component.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

/**
 * Port interface for machines in the domain layer.
 * <p>
 * This interface extends the CompositeComponentPort with operations specific to
 * machines, which are orchestrating components that manage composites and their
 * behavior.
 * </p>
 */
public interface MachinePort extends CompositeComponentPort {
    
    /**
     * Gets the machine's identifier.
     *
     * @return The machine ID string
     */
    String getMachineId();
    
    /**
     * Gets the machine's type.
     *
     * @return The machine type
     */
    MachineType getMachineType();
    
    /**
     * Gets the machine's current state.
     *
     * @return The machine state
     */
    MachineState getMachineState();
    
    /**
     * Sets the machine's state.
     *
     * @param state The new machine state
     */
    void setMachineState(MachineState state);
    
    /**
     * Starts the machine, activating all components in the proper order.
     *
     * @return true if the machine started successfully, false otherwise
     */
    boolean start();
    
    /**
     * Stops the machine, deactivating all components in the proper order.
     *
     * @return true if the machine stopped successfully, false otherwise
     */
    boolean stop();
    
    /**
     * Adds a composite to this machine.
     *
     * @param name The name for the composite within this machine
     * @param composite The composite to add
     * @return true if the composite was added successfully, false otherwise
     */
    boolean addComposite(String name, CompositeComponentPort composite);
    
    /**
     * Removes a composite from this machine.
     *
     * @param name The name of the composite to remove
     * @return The removed composite, or empty if not found
     */
    Optional<CompositeComponentPort> removeComposite(String name);
    
    /**
     * Gets a composite by name.
     *
     * @param name The name of the composite to retrieve
     * @return The composite
     */
    CompositeComponentPort getComposite(String name);
    
    /**
     * Gets all composites in this machine.
     *
     * @return A map of composite names to composites
     */
    Map<String, CompositeComponentPort> getComposites();
    
    /**
     * Connects two composites within this machine.
     *
     * @param sourceCompositeName The name of the source composite
     * @param targetCompositeName The name of the target composite
     * @return true if the connection was established, false otherwise
     */
    boolean connectComposites(String sourceCompositeName, String targetCompositeName);
    
    /**
     * Gets all composite connections in this machine.
     *
     * @return A map of source composite names to lists of target composite names
     */
    Map<String, List<String>> getCompositeConnections();
    
    /**
     * Checks if the machine is active (running).
     *
     * @return true if the machine is active, false otherwise
     */
    default boolean isActive() {
        return getMachineState() == MachineState.RUNNING;
    }
}