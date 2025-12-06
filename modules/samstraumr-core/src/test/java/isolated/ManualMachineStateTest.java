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

package isolated;

import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.MachineOperation;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.validation.MachineStateValidator;

/**
 * Simple manual test for {@link MachineStateValidator} that runs without JUnit.
 */
public class ManualMachineStateTest {

    public static void main(String[] args) {
        System.out.println("Running manual machine state validation tests...\n");
        
        // Create a test machine ID
        ComponentId machineId = ComponentId.create("TestMachine");
        
        System.out.println("State transition validation:");
        testStateTransition(machineId, MachineState.CREATED, MachineState.READY);
        testStateTransition(machineId, MachineState.READY, MachineState.RUNNING);
        testStateTransition(machineId, MachineState.RUNNING, MachineState.STOPPED);
        testStateTransition(machineId, MachineState.CREATED, MachineState.RUNNING); // Invalid
        testStateTransition(machineId, MachineState.DESTROYED, MachineState.READY); // Invalid
        
        System.out.println("\nOperation validation:");
        testOperationValidation(machineId, MachineOperation.INITIALIZE, MachineState.CREATED);
        testOperationValidation(machineId, MachineOperation.INITIALIZE, MachineState.READY); // Invalid
        testOperationValidation(machineId, MachineOperation.START, MachineState.READY);
        testOperationValidation(machineId, MachineOperation.START, MachineState.RUNNING); // Invalid
        testOperationValidation(machineId, MachineOperation.ADD_COMPONENT, MachineState.CREATED);
        testOperationValidation(machineId, MachineOperation.ADD_COMPONENT, MachineState.RUNNING); // Invalid
        
        System.out.println("\nValid next states:");
        printValidNextStates(MachineState.CREATED);
        printValidNextStates(MachineState.RUNNING);
        printValidNextStates(MachineState.DESTROYED);
        
        System.out.println("\nValid states for operations:");
        printValidStatesForOperation(MachineOperation.INITIALIZE);
        printValidStatesForOperation(MachineOperation.START);
        printValidStatesForOperation(MachineOperation.DESTROY);
        
        System.out.println("\nAll tests completed.");
    }
    
    private static void testStateTransition(
            ComponentId machineId, MachineState fromState, MachineState toState) {
        try {
            boolean isValid = MachineStateValidator.isValidStateTransition(fromState, toState);
            
            if (isValid) {
                MachineStateValidator.validateStateTransition(machineId, fromState, toState);
                System.out.println("✓ Transition from " + fromState + " to " + toState + " is valid");
            } else {
                System.out.println("✗ Transition from " + fromState + " to " + toState + " is invalid");
            }
        } catch (Exception e) {
            System.out.println("✓ Transition from " + fromState + " to " + toState + 
                    " is invalid and throws: " + e.getMessage());
        }
    }
    
    private static void testOperationValidation(
            ComponentId machineId, MachineOperation operation, MachineState state) {
        try {
            boolean isAllowed = MachineStateValidator.isOperationAllowed(operation, state);

            if (isAllowed) {
                MachineStateValidator.validateOperationState(machineId, operation, state);
                System.out.println("✓ Operation '" + operation + "' is allowed in state " + state);
            } else {
                System.out.println("✗ Operation '" + operation + "' is not allowed in state " + state);
            }
        } catch (Exception e) {
            System.out.println("✓ Operation '" + operation + "' is not allowed in state " + state +
                    " and throws: " + e.getMessage());
        }
    }
    
    private static void printValidNextStates(MachineState state) {
        System.out.println("Valid next states from " + state + ":");
        MachineStateValidator.getValidNextStates(state).forEach(nextState -> 
            System.out.println("  - " + nextState));
        if (MachineStateValidator.getValidNextStates(state).isEmpty()) {
            System.out.println("  (No valid transitions)");
        }
    }
    
    private static void printValidStatesForOperation(MachineOperation operation) {
        System.out.println("Valid states for operation '" + operation + "':");
        MachineState[] validStates = MachineStateValidator.getValidStatesForOperation(operation);
        for (MachineState state : validStates) {
            System.out.println("  - " + state);
        }
    }
}