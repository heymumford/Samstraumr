package org.samstraumr.tube.core;

public class TubeState {
    public enum State {
        READY,
        ACTIVE,
        PAUSED,
        STOPPING,
        STOPPED
    }

    private State currentState;

    public TubeState() {
        this.currentState = State.READY;
    }

    // Method to get the current state
    public State getCurrentState() {
        return currentState;
    }

    // Method to set the state
    public void setState(State newState) {
        if (isValidTransition(newState)) {
            System.out.printf("Transitioning from %s to %s%n", currentState, newState);
            this.currentState = newState;
        } else {
            System.out.printf("Invalid state transition from %s to %s%n", currentState, newState);
        }
    }

    // Method to check if a state transition is valid
    private boolean isValidTransition(State newState) {
        return switch (currentState) {
            case READY -> newState == State.ACTIVE || newState == State.PAUSED;
            case ACTIVE -> newState == State.PAUSED || newState == State.READY;
            case PAUSED -> newState == State.READY || newState == State.ACTIVE;
            case STOPPING -> newState == State.STOPPED;
            case STOPPED -> false;
        };
    }

    // Method to display the current state
    public void displayState() {
        System.out.printf("Current Tube State: %s%n", currentState);
    }

    // Additional state-related functionality can be added here
}
