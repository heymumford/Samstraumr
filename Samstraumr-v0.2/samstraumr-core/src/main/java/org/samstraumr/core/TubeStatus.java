package org.samstraumr.core;

public enum TubeStatus {
    INITIALIZING,         // Tube is being prepared and not yet ready
    READY,                // Tube is initialized and ready for input
    RECEIVING_INPUT,       // Tube is actively receiving input
    PROCESSING_INPUT,      // Tube is instantly processing the received input
    OUTPUTTING_RESULT,     // Tube is outputting its result after processing
    RECOVERING,            // Tube encountered an issue and is recovering
    DORMANT,               // Tube is inactive but ready to be reactivated
    DEACTIVATING           // Tube is shutting down
}