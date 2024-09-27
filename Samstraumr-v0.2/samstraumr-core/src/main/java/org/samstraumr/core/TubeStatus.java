package org.samstraumr.core;

/**
 * Enum representing the phases of existence or situational conditions that a tube can experience.
 * These phases are inspired by biological stem cells and their life cycle.
 */
public enum TubeStatus {
    RESTING,           // Initial or quiescent state, waiting for input
    ACTIVE,            // Actively processing input
    RENEWING,          // Self-replicating or instantiating new tubes
    DIFFERENTIATING,   // Specializing or adapting to a specific function
    SIGNALING,         // Sending signals or communicating with other tubes
    RECEIVING_INPUT,   // Receiving signals or input from other tubes
    DORMANT,           // Inactive but poised for activation
    STRESSED,          // Encountering environmental challenges
    DEACTIVATING,      // Shutting down or entering self-destruction
    RECOVERING,        // Recovering from errors or damage
    REPOSITIONING      // Changing or migrating its function or configuration
}
