package org.samstraumr.tube.util;

import org.samstraumr.tube.Tube;

public class TubeValidator {

    // Method to validate if a tube is properly initialized
    public static boolean isTubeInitialized(Tube tube) {
        if (tube == null) {
            System.out.println("Validation failed: Tube is null.");
            return false;
        }
        if (tube.getIdentity() == null || tube.getIdentity().isEmpty()) {
            System.out.println("Validation failed: Tube identity is not set.");
            return false;
        }
        System.out.println("Validation passed: Tube is properly initialized.");
        return true;
    }

    // Method to validate if a tube's vital stats are within normal ranges
    public static boolean areVitalStatsNormal(Tube tube) {
        if (tube.getMemoryUsage() > Runtime.getRuntime().maxMemory() * 0.85) {
            System.out.println("Validation warning: Memory usage is above 85% threshold.");
            return false;
        }
        if (tube.getCpuCores() <= 0) {
            System.out.println("Validation failed: CPU core count is invalid.");
            return false;
        }
        System.out.println("Validation passed: Vital stats are within normal ranges.");
        return true;
    }

    // Method to validate if a given state transition is allowed
    public static boolean isValidStateTransition(Tube tube, String currentState, String newState) {
        // Placeholder for state transition validation logic
        System.out.printf("Validating state transition from %s to %s for Tube: %s%n", currentState, newState, tube.getIdentity());
        // Assume all transitions are valid for this placeholder
        return true;
    }

    // Additional validation-related functionality can be added here
}
