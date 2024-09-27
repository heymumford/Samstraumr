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
 * The Tube class allows tubes to identify their type, communicate with other tubes,
 * and process input/output based on their classification.
 *
 * <p>This class also handles interactions with a Queen Tube, which manages overall system coordination.</p>
 *
 * @param <I> The type of input data the tube will receive
 * @param <O> The type of output data the tube will produce
 *
 * @see QueenTube
 */
public abstract class Tube<I, O> {

    /**
     * Enum representing the classification of tubes:
     * <ul>
     *     <li><strong>VirtueTube</strong> - Focuses on higher purpose and system-level goals.</li>
     *     <li><strong>CharacterTube</strong> - Represents strength, resilience, and adaptability.</li>
     *     <li><strong>BodyTube</strong> - Executes tasks and handles input/output operations.</li>
     * </ul>
     */
    public enum TubeType { VIRTUE_TUBE, CHARACTER_TUBE, BODY_TUBE }

    private final String tubeID;            // Unique identifier for each tube
    private final String materialBodyID;    // Identifier for the physical/container environment
    private final String motherTubeID;      // ID of the tube that instantiated this tube
    private TubeType tubeType;              // The classification of this tube (Virtue, Character, or Body)
    private Optional<I> input = Optional.empty();
    private Optional<O> output = Optional.empty();
    private boolean isActive = true;
    private String purpose;                 // The "Virtue" or higher goal that this tube is aligned with
    private TubeStatus status = TubeStatus.WAITING;
    private final QueenTube queenTube;

    /**
     * Constructs a Tube with the specified purpose, queen, and type.
     *
     * @param purpose The higher goal or purpose (Virtue) this tube serves.
     * @param queenTube The Queen Tube managing this tube.
     * @param motherTubeID The ID of the tube that instantiated this tube, or "Yggdrasil" for the queen tube.
     * @param tubeType The classification of this tube (Virtue, Character, or Body).
     */
    public Tube(String purpose, QueenTube queenTube, String motherTubeID, TubeType tubeType) {
        this.purpose = purpose;
        this.tubeID = generateUUID("TUBE");
        this.materialBodyID = MaterialBodyIdentifier.generateMaterialBodyHash();
        this.motherTubeID = (motherTubeID != null) ? motherTubeID : "Yggdrasil";
        this.queenTube = queenTube;
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
     * If no output is produced, the tube waits for further instructions from the queen.
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
     * Sends a ping to the Queen Tube for direction when the tube is unable to proceed.
     */
    private void pingQueen() {
        System.out.println("Pinging Queen Tube: " + queenTube.getTubeID());
        queenTube.receivePing(this);
    }

    /**
     * Waits for input and updates the tube's status, sending a ping to the Queen Tube for direction.
     */
    private void awaitInput() {
        status = TubeStatus.WAITING_FOR_INPUT;
        pingQueen();
    }

    /**
     * Waits for output and updates the tube's status, sending a ping to the Queen Tube for direction.
     */
    private void awaitOutput() {
        status = TubeStatus.WAITING_FOR_OUTPUT;
        pingQueen();
    }

    // Getters for essential fields

    /**
     * Returns the unique ID of the tube.
     *
     * @return The tube's unique identifier.
     */
    public String getTubeID() { return tubeID; }

    /**
     * Returns the unique identifier of the material body (physical environment or container).
     *
     * @return The material body identifier.
     */
    public String getMaterialBodyID() { return materialBodyID; }

    /**
     * Returns the ID of the tube that instantiated this tube, or "Yggdrasil" if it's the queen tube.
     *
     * @return The mother tube's ID.
     */
    public String getMotherTubeID() { return motherTubeID; }

    /**
     * Returns the type of this tube (Virtue, Character, or Body).
     *
     * @return The classification of the tube.
     */
    public TubeType getTubeType() { return tubeType; }

    /**
     * Returns the current status of the tube.
     *
     * @return The tube's status (e.g., ACTIVE, WAITING, DEACTIVATED).
     */
    public TubeStatus getStatus() { return status; }
}
