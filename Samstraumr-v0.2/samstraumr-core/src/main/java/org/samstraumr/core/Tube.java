package org.samstraumr.core;

import java.util.*;
import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents a Tube in the Samstraumr framework.
 * A Tube is an autonomous, adaptive component with a unique identity and evolving purpose.
 */

public class Tube {
    private final TubeLogger tubeLogger;
    private final String uniqueId;
    private String reason;
    private final List<String> lineage;
    private final Environment environment;
    private final List<Tube> parentTubes;
    private final Map<String, String> connectedTubes; // Tube ID to Reason
    private final List<OperationRecord> operationHistory;
    private boolean paused;
    private boolean terminated;
    private Tube transformedTube;
    private final List<Tube> replicas;
    private int currentWorkload;
    private int assignedWorkload;

    private static final double MISALIGNMENT_THRESHOLD = 0.2;
    private static final double PERFORMANCE_THRESHOLD = 0.8;
    private static final double DANGEROUS_WORKLOAD_FACTOR = 1.5;
    private static final double SCALING_UP_FACTOR = 1.2;
    private static final double SCALING_DOWN_FACTOR = 0.5;

    public Tube(String reason, Environment environment, String compositeId, String machineId) {
        this.uniqueId = generateUniqueId(reason + environment.getParameters());
        this.tubeLogger = new TubeLogger(this.uniqueId, compositeId, machineId);
        this.reason = reason;
        this.environment = environment;
        this.lineage = new ArrayList<>(Collections.singletonList(reason));
        this.parentTubes = new ArrayList<>();
        this.connectedTubes = new HashMap<>();
        this.operationHistory = new ArrayList<>();
        this.replicas = new ArrayList<>();

        tubeLogger.log("info", "Creating new Tube", "initialization");
    }

    public Tube(String reason, Environment environment, Tube parentTube, String compositeId, String machineId) {
        this(reason, environment, compositeId, machineId);
        this.parentTubes.add(parentTube);
        this.lineage.addAll(0, parentTube.getLineage());
        tubeLogger.log("info", "Created new Tube with parent", "initialization", "parentTube");
    }

    /**
     * Generates a unique identifier for the Tube.
     *
     * @param parameters Combined parameters used to generate the unique ID
     * @return A SHA-256 hash string representing the unique ID
     */
    private String generateUniqueId(String parameters) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((parameters + Instant.now().toString()).getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            tubeLogger.log("error", "Failed to generate unique ID: " + e.getMessage(), "error", "initialization");
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param hash The byte array to convert
     * @return A hexadecimal string representation of the byte array
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Gets the unique identifier of the Tube.
     *
     * @return The unique identifier
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Gets the current reason or purpose of the Tube.
     *
     * @return The current reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Gets the lineage of the Tube.
     *
     * @return An unmodifiable list representing the Tube's lineage
     */
    public List<String> getLineage() {
        return Collections.unmodifiableList(lineage);
    }

    /**
     * Detects if the Tube's current behavior is misaligned with its reason.
     */
    public void detectMisalignment() {
        tubeLogger.log("debug", "Detecting misalignment", "performance");
        double failureRate = getFailureRate();
        if (failureRate > MISALIGNMENT_THRESHOLD) {
            tubeLogger.log("warn", "Misalignment detected. Failure rate: " + failureRate, "performance", "misalignment");
        } else {
            tubeLogger.log("info", "No misalignment detected. Failure rate: " + failureRate, "performance");
        }
    }


    /**
     * Calculates the failure rate of operations.
     *
     * @return The failure rate as a double between 0 and 1
     */
    private double getFailureRate() {
        if (operationHistory.isEmpty()) {
            return 0;
        }
        long failedOperations = operationHistory.stream()
                .filter(record -> !record.isSuccess())
                .count();
        return (double) failedOperations / operationHistory.size();
    }

    /**
     * Evolves the purpose of the Tube by updating its reason.
     *
     * @param newReason The new reason or purpose for the Tube
     */
    public void evolvePurpose(String newReason) {
        this.reason = newReason;
        this.lineage.add(newReason);
        System.out.println("Purpose evolved to: " + newReason);
    }

    /**
     * Connects this Tube to another Tube.
     *
     * @param otherTube The Tube to connect to
     */
    public void connectTo(Tube otherTube) {
        this.connectedTubes.put(otherTube.getUniqueId(), otherTube.getReason());
        otherTube.connectedTubes.put(this.uniqueId, this.reason);
    }

    /**
     * Gets the connected Tubes.
     *
     * @return An unmodifiable map of connected Tube IDs to their reasons
     */
    public Map<String, String> getConnectedTubes() {
        return Collections.unmodifiableMap(connectedTubes);
    }

    /**
     * Records an operation performed by the Tube.
     *
     * @param operationName The name of the operation
     * @param success       Whether the operation was successful
     */
    public void recordOperation(String operationName, boolean success) {
        this.operationHistory.add(new OperationRecord(operationName, success));
    }

    /**
     * Analyzes the Tube's performance and considers purpose evolution if necessary.
     */
    public void analyzePerformance() {
        double successRate = 1 - getFailureRate();
        if (successRate < PERFORMANCE_THRESHOLD) {
            System.out.println("Performance below threshold. Considering purpose evolution.");
        }
    }

    /**
     * Detects dangerous conditions and takes appropriate action.
     */
    public void detectDangerousConditions() {
        if (isWorkloadDangerous()) {
            this.paused = true;
            System.out.println("Dangerous conditions detected. Tube paused.");
        }

        if (hasContinuousFailure()) {
            this.terminated = true;
            System.out.println("Continuous failure detected. Tube terminated.");
        }
    }

    /**
     * Checks if the current workload is dangerously high.
     *
     * @return true if the workload is dangerous, false otherwise
     */
    private boolean isWorkloadDangerous() {
        return currentWorkload > assignedWorkload * DANGEROUS_WORKLOAD_FACTOR;
    }

    /**
     * Checks if the Tube has experienced continuous failure.
     *
     * @return true if there's continuous failure, false otherwise
     */
    private boolean hasContinuousFailure() {
        return operationHistory.size() > 1000 && operationHistory.stream().noneMatch(OperationRecord::isSuccess);
    }

    /**
     * Checks if the Tube is paused.
     *
     * @return true if the Tube is paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Checks if the Tube is terminated.
     *
     * @return true if the Tube is terminated, false otherwise
     */
    public boolean isTerminated() {
        return terminated;
    }

    /**
     * Transforms the Tube into a new entity with a new reason.
     *
     * @param newReason The reason for the new entity
     */
    public void transformIntoNewEntity(String newReason) {
        this.transformedTube = new Tube(newReason, this.environment, this);
        this.terminated = true;
        System.out.println("Transformed into new entity with reason: " + newReason);
    }

    /**
     * Gets the transformed Tube if a transformation has occurred.
     *
     * @return The transformed Tube, or null if no transformation has occurred
     */
    public Tube getTransformedTube() {
        return transformedTube;
    }

    /**
     * Sets the current workload of the Tube.
     *
     * @param workload The current workload
     */
    public void setCurrentWorkload(int workload) {
        this.currentWorkload = workload;
    }

    /**
     * Gets the current workload of the Tube.
     *
     * @return The current workload
     */
    public int getCurrentWorkload() {
        return currentWorkload;
    }

    /**
     * Detects if scaling is needed based on the current workload.
     */
    public void detectScalingNeed() {
        if (currentWorkload > assignedWorkload * SCALING_UP_FACTOR) {
            createReplica();
        } else if (currentWorkload < assignedWorkload * SCALING_DOWN_FACTOR && !replicas.isEmpty()) {
            removeReplica();
        }
    }

    /**
     * Creates a new replica of the Tube.
     */
    private void createReplica() {
        Tube replica = new Tube(this.reason, this.environment, this);
        this.replicas.add(replica);
        System.out.println("New replica created due to high workload.");
    }

    /**
     * Removes a replica of the Tube.
     */
    private void removeReplica() {
        Tube removedReplica = this.replicas.remove(this.replicas.size() - 1);
        removedReplica.terminated = true;
        System.out.println("Replica removed due to low workload.");
    }

    /**
     * Gets the replicas of the Tube.
     *
     * @return An unmodifiable list of replicas
     */
    public List<Tube> getReplicas() {
        return Collections.unmodifiableList(replicas);
    }

    /**
     * Gets the assigned workload of the Tube.
     *
     * @return The assigned workload
     */
    public int getAssignedWorkload() {
        return assignedWorkload;
    }

    /**
     * Represents a record of an operation performed by the Tube.
     */
    private static class OperationRecord {
        private final String operationName;
        private final boolean success;

        /**
         * Constructs a new OperationRecord.
         *
         * @param operationName The name of the operation
         * @param success       Whether the operation was successful
         */
        public OperationRecord(String operationName, boolean success) {
            this.operationName = operationName;
            this.success = success;
        }

        /**
         * Checks if the operation was successful.
         *
         * @return true if the operation was successful, false otherwise
         */
        public boolean isSuccess() {
            return success;
        }
    }
}