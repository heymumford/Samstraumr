package org.samstraumr.core;

import java.util.*;
import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tube {
    private final TubeLogger tubeLogger;
    private final String uniqueId;
    private String reason;
    private final List<String> lineage;
    private final Environment environment;
    private final List<Tube> parentTubes;

    private final TubeOperations operations;
    private final TubeWorkload workload;
    private final TubeTransformation transformation;
    private final TubeConnection connection;

    public Tube(String reason, Environment environment, String compositeId, String machineId) {
        this.uniqueId = generateUniqueId(reason + environment.getParameters());
        this.tubeLogger = new TubeLogger(this.uniqueId, compositeId, machineId);
        this.reason = reason;
        this.environment = environment;
        this.lineage = new ArrayList<>(Collections.singletonList(reason));
        this.parentTubes = new ArrayList<>();

        this.operations = new TubeOperations(tubeLogger);
        this.workload = new TubeWorkload(tubeLogger);
        this.transformation = new TubeTransformation(this, tubeLogger);
        this.connection = new TubeConnection(tubeLogger);

        tubeLogger.log("info", "Creating new Tube", "initialization");
    }

    public Tube(String reason, Environment environment, Tube parentTube, String compositeId, String machineId) {
        this(reason, environment, compositeId, machineId);
        this.parentTubes.add(parentTube);
        this.lineage.addAll(0, parentTube.getLineage());
        tubeLogger.log("info", "Created new Tube with parent", "initialization", "parentTube");
    }

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

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getLineage() {
        return Collections.unmodifiableList(lineage);
    }

    // Delegate methods to other classes
    public void detectMisalignment() {
        operations.detectMisalignment();
    }

    public void evolvePurpose(String newReason) {
        transformation.evolvePurpose(newReason);
    }

    public void connectTo(Tube otherTube) {
        connection.connectTo(otherTube);
    }

    public Map<String, String> getConnectedTubes() {
        return connection.getConnectedTubes();
    }

    public void recordOperation(String operationName, boolean success) {
        operations.recordOperation(operationName, success);
    }

    public void analyzePerformance() {
        operations.analyzePerformance();
    }

    public void detectDangerousConditions() {
        operations.detectDangerousConditions();
    }

    public boolean isPaused() {
        return operations.isPaused();
    }

    public boolean isTerminated() {
        return operations.isTerminated();
    }

    public void transformIntoNewEntity(String newReason) {
        transformation.transformIntoNewEntity(newReason);
    }

    public Tube getTransformedTube() {
        return transformation.getTransformedTube();
    }

    public void setCurrentWorkload(int workload) {
        this.workload.setCurrentWorkload(workload);
    }

    public int getCurrentWorkload() {
        return workload.getCurrentWorkload();
    }

    public void detectScalingNeed() {
        workload.detectScalingNeed();
    }

    public List<Tube> getReplicas() {
        return workload.getReplicas();
    }

    public int getAssignedWorkload() {
        return workload.getAssignedWorkload();
    }
}