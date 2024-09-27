package org.samstraumr.core;

import java.util.Optional;
import java.util.UUID;

/**
 * The Tube class represents a core building block in the Samstraumr system.
 * Each Tube is categorized into one of three types based on philosophical principles:
 * <ul>
 *     <li><strong>VirtueTube</strong> - Aligned with higher goals and purpose (Virtue), inspired by Aristotle's Virtue Ethics.</li>
 *     <li><strong>CharacterTube</strong> - Resilient and adaptive, focusing on strength and endurance, inspired by Stoic philosophy.</li>
 *     <li><strong>BodyTube</strong> - Task-oriented, focusing on execution and interaction with the external environment, aligned with materialism and functionalism.</li>
 * </ul>
 *
 * @param <I> The type of input data the tube will receive
 * @param <O> The type of output data the tube will produce
 */
public abstract class Tube<I, O> {

    public enum TubeType { VIRTUE_TUBE, CHARACTER_TUBE, BODY_TUBE }

    private final String tubeID;        // Unique identifier for each tube
    private final String motherTubeID;  // ID of the tube that instantiated this tube
    private TubeType tubeType;          // Classification of this tube (Virtue, Character, or Body)
    private Optional<I> input = Optional.empty();
    private Optional<O> output = Optional.empty();
    private boolean isActive = true;
    private String purpose;             // The "Virtue" or higher goal this tube is aligned with
    private TubeStatus status = TubeStatus.RESTING;

    /**
     * Constructs a Tube with the specified purpose, parent tube, and type.
     *
     * @param purpose The higher goal or purpose (Virtue) this tube serves.
     * @param motherTubeID The ID of the tube that instantiated this tube, or "Yggdrasil" for the first tube.
     * @param tubeType The classification of this tube (Virtue, Character, or Body).
     */
    public Tube(String purpose, String motherTubeID, TubeType tubeType) {
        this.purpose = purpose;
        this.tubeID = generateUUID("TUBE");
        this.motherTubeID = (motherTubeID != null) ? motherTubeID : "Yggdrasil";
        this.tubeType = tubeType;
    }

    /**
     * Generates a UUID for uniquely identifying this tube, with a specific prefix.
     *
     * @param prefix The prefix to use for the ID (e.g., "TUBE").
     * @return A unique identifier string for the tube.
     */
    private String generateUUID(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString();
    }

    /**
     * Processes the input data and produces an output.
     * Subclasses are expected to implement the specific processing logic.
     *
     * @return The processed result, or null if no result is produced.
     */
    protected abstract O process();

    /**
     * Receives input to the tube. If no input is provided, the tube will wait for direction.
     *
     * @param input The input data to be processed by the tube.
     */
    public void receive(I input) {
        if (input != null) {
            this.input = Optional.of(input);
            this.status = TubeStatus.ACTIVE;
        } else {
            awaitInput();
        }
    }

    /**
     * Executes the tube's processing logic and passes the result to the next tube.
     * If no output is produced, the tube waits for further instructions.
     *
     * @param nextTube The next tube in the chain to receive the output of this tube.
     * @param <T> The type of data the next tube will receive.
     */
    public <T> void execute(Tube<O, T> nextTube) {
        if (isActive && input.isPresent()) {
            output = Optional.ofNullable(process());
            output.ifPresentOrElse(
                    nextTube::receive,
                    this::awaitOutput
            );
        } else {
            awaitInput();
        }
    }

    /**
     * Communicates with another tube based on its type.
     * Tubes can adjust their behavior or processing logic based on the type of tube they are communicating with.
     *
     * @param otherTube The other tube with which communication is occurring.
     */
    public void communicateWith(Tube<?, ?> otherTube) {
        switch (otherTube.getTubeType()) {
            case VIRTUE_TUBE:
                System.out.println("Communicating with Virtue Tube (Higher Purpose)");
                break;
            case CHARACTER_TUBE:
                System.out.println("Communicating with Character Tube (Resilience)");
                break;
            case BODY_TUBE:
                System.out.println("Communicating with Body Tube (Execution)");
                break;
        }
    }

    /**
     * Waits for input and updates the tube's status to RECEIVING_INPUT.
     */
    private void awaitInput() {
        status = TubeStatus.RECEIVING_INPUT;
    }

    /**
     * Waits for output and updates the tube's status to PROCESSING_OUTPUT.
     */
    private void awaitOutput() {
        status = TubeStatus.PROCESSING_OUTPUT;
    }

    // Getters for essential fields

    /**
     * Returns the unique ID of the tube.
     *
     * @return The tube's unique identifier.
     */
    public String getTubeID() {
        return tubeID;
    }

    /**
     * Returns the ID of the tube that instantiated this tube, or "Yggdrasil" if it's the first tube.
     *
     * @return The mother tube's ID.
     */
    public String getMotherTubeID() {
        return motherTubeID;
    }

    /**
     * Returns the type of this tube (Virtue, Character, or Body).
     *
     * @return The classification of the tube.
     */
    public TubeType getTubeType() {
        return tubeType;
    }

    /**
     * Returns the current status of the tube.
     *
     * @return The tube's status (e.g., ACTIVE, RESTING, RECEIVING_INPUT, etc.).
     */
    public TubeStatus getStatus() {
        return status;
    }

    /**
     * Sets the current status of the tube.
     *
     * @param status The new status to set for the tube.
     */
    public void setStatus(TubeStatus status) {
        this.status = status;
    }
}
