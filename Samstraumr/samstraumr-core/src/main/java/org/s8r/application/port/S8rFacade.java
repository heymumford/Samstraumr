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

package org.s8r.application.port;

import java.util.List;

import org.s8r.application.dto.ComponentDto;
import org.s8r.application.dto.MachineDto;

/**
 * Application port that defines the primary interface to the Samstraumr framework.
 * 
 * <p>This interface follows the Port and Adapter pattern, providing a clearly defined
 * boundary for client applications to interact with the system.
 */
public interface S8rFacade {
    
    /**
     * Creates a new component.
     *
     * @param reason The reason for creating the component
     * @return A string representation of the component ID
     */
    String createComponent(String reason);
    
    /**
     * Gets a component by ID.
     *
     * @param componentId The component ID string
     * @return The component DTO
     */
    ComponentDto getComponent(String componentId);
    
    /**
     * Gets all components.
     *
     * @return A list of component DTOs
     */
    List<ComponentDto> getAllComponents();
    
    /**
     * Creates a machine of the specified type.
     *
     * @param typeName The machine type name
     * @param name The machine name
     * @param description The machine description
     * @return The created machine DTO
     */
    MachineDto createMachine(String typeName, String name, String description);
    
    /**
     * Gets a machine by ID.
     *
     * @param machineId The machine ID string
     * @return The machine DTO
     */
    MachineDto getMachine(String machineId);
    
    /**
     * Gets all machines.
     *
     * @return A list of machine DTOs
     */
    List<MachineDto> getAllMachines();
    
    /**
     * Starts a machine.
     *
     * @param machineId The machine ID string
     */
    void startMachine(String machineId);
    
    /**
     * Stops a machine.
     *
     * @param machineId The machine ID string
     */
    void stopMachine(String machineId);
}