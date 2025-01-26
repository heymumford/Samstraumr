package org.samstraumr.tube.api;

import org.samstraumr.tube.logging.TubeLogger;

public class TubeState {

    // Enum to represent various states a Tube can be in
    public enum State {
        INITIALIZING,
        ACTIVE,
        PAUSED,
        TERMINATED
    }

    // The current state of the Tube
    private State currentState;

    // Constructor
    public TubeState() {
        this.currentState = State.INITIALIZING;
        TubeLogger.info("Tube initialized in state: " + currentState);
    }

    // Get the current state
    public State getCurrentState() {
        return currentState;
    }

    // Set a new state with validation
    public void setState(State newState) {
        if (isValidTransition(newState)) {
            TubeLogger.info("Transitioning state from " + currentState + " to " + newState);
            this.currentState = newState;
        } else {
            TubeLogger.warn("Invalid state transition from " + currentState + " to " + newState);
        }
    }

    // Validate if a transition is allowed between states
    private boolean isValidTransition(State newState) {
        switch (currentState) {
            case INITIALIZING:
                // Can transition to ACTIVE or TERMINATED from INITIALIZING
                return newState == State.ACTIVE || newState == State.TERMINATED;
            case ACTIVE:
                // Can transition to PAUSED or TERMINATED from ACTIVE
                return newState == State.PAUSED || newState == State.TERMINATED;
            case PAUSED:
                // Can transition back to ACTIVE or to TERMINATED from PAUSED
                return newState == State.ACTIVE || newState == State.TERMINATED;
            case TERMINATED:
                // TERMINATED is an end state; no further transitions allowed
                return false;
            default:
                return false;
        }
    }
}
