package org.samstraumr.tube;

import java.util.*;
import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Tube {
    private final String uniqueId;
    private String reason;
    private final List<String> lineage;
    private final Environment environment;
    private final List<String> mimirLog;  // Internal log (temporary solution)
    private Timer terminationTimer;

    public Tube(String reason, Environment environment) {
        this.uniqueId = generateUniqueId(reason + environment.getParameters());
        this.reason = reason;
        this.environment = environment;
        this.lineage = new ArrayList<>(Collections.singletonList(reason));
        this.mimirLog = new LinkedList<>();  // Using LinkedList for simple in-memory logging
        logToMimir("Tube initialized with ID: " + this.uniqueId);

        // Set a default self-termination delay of 60 seconds
        this.terminationTimer = new Timer();
        terminationTimer.schedule(new TerminationTask(), 60 * 1000);
    }

    private String generateUniqueId(String parameters) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((parameters + Instant.now().toString()).getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            logToMimir("Error generating unique ID: " + e.getMessage());
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

    // Logging method to log internal events
    private void logToMimir(String logEntry) {
        mimirLog.add(Instant.now().toString() + ": " + logEntry);
    }

    // Method to query the internal log
    public List<String> queryMimirLog() {
        return Collections.unmodifiableList(mimirLog);
    }

    // Self-termination task
    private class TerminationTask extends TimerTask {
        @Override
        public void run() {
            logToMimir("Tube self-terminating after 60 seconds.");
            terminationTimer.cancel();
            // Simulate termination by clearing log and data
            mimirLog.clear();
            lineage.clear();
        }
    }

    // Method to set a custom termination delay
    public void setTerminationDelay(int seconds) {
        terminationTimer.cancel();
        terminationTimer = new Timer();
        terminationTimer.schedule(new TerminationTask(), seconds * 1000);
        logToMimir("Custom termination delay set to " + seconds + " seconds.");
    }
}
