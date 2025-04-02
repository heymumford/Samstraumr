package org.samstraumr.tube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.time.Instant;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;


public class Tube {
    private static final Logger logger = LoggerFactory.getLogger(Tube.class);
    private static final int DEFAULT_TERMINATION_DELAY = 60; // seconds
    private static final String DIGEST_ALGORITHM = "SHA-256";

    private final String uniqueId;
    private final String reason;
    private final List<String> lineage;
    private final List<String> mimirLog;
    private final Environment environment;
    private volatile Timer terminationTimer;

    public Tube(String reason, Environment environment) {
        logger.info("Initializing new Tube with reason: {}", reason);
        try {
            if (environment == null) {
                logger.error("Environment cannot be null");
                throw new TubeInitializationException("Environment cannot be null", 
                    new NullPointerException("Environment cannot be null"));
            }
            
            this.reason = reason;
            this.environment = environment;
            this.lineage = Collections.synchronizedList(new ArrayList<>(Collections.singletonList(reason)));
            this.mimirLog = Collections.synchronizedList(new LinkedList<>());
            this.uniqueId = generateSHA256UniqueId(reason + environment.getParameters());
            initializeTube();
        } catch (Exception e) {
            logger.error("Failed to initialize Tube: {} - initialization failed", reason);
            if (e instanceof TubeInitializationException) {
                throw e;
            }
            throw new TubeInitializationException("Failed to initialize Tube", e);
        }
    }

    private void initializeTube() {
        logToMimir("Tube initialized with ID: " + this.uniqueId);
        logger.debug("Tube initialized with ID: {}", this.uniqueId);
        logToMimir("Initialization reason: " + this.reason);
        setTerminationDelay(DEFAULT_TERMINATION_DELAY);
        logToMimir("Environment: " + environment.getParameters());
    }

    private String generateSHA256UniqueId(String parameters) {
        try {
            MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] hash = digest.digest((parameters + Instant.now().toString()).getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to generate unique ID: SHA-256 algorithm not found", e);
            throw new TubeInitializationException("Failed to generate unique ID", e);
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

    public List<String> queryMimirLog() {
        logger.debug("Querying Mimir log. Current size: {}", mimirLog.size());
        return Collections.unmodifiableList(mimirLog);
    }

    public synchronized void setTerminationDelay(int seconds) {
        logger.info("Setting termination delay to {} seconds", seconds);
        try {
            synchronized (this) {
                if (terminationTimer != null) {
                    terminationTimer.cancel();
                }
                terminationTimer = new Timer();
                terminationTimer.schedule(new TerminationTask(), seconds * 1000L);
            }
            logToMimir("Custom termination delay set to " + seconds + " seconds.");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid termination delay: {}", seconds, e);
            throw new IllegalArgumentException("Invalid termination delay", e);
        }
    }

    private void logToMimir(String logEntry) {
        String timestampedEntry = Instant.now().toString() + ": " + logEntry;
        mimirLog.add(timestampedEntry);
        logger.trace("Mimir Log: {}", timestampedEntry);
    }


    private class TerminationTask extends TimerTask {
        @Override
        public void run() {
            synchronized (Tube.this) {
                logger.info("Executing termination task for Tube: {}", uniqueId);
                logToMimir("Tube self-terminating.");
                terminationTimer.cancel();
                clearLogsAndLineage();
                logger.debug("Tube {} terminated. Mimir log and lineage cleared.", uniqueId);
            }
        }

        private void clearLogsAndLineage() {
            synchronized (mimirLog) {
                mimirLog.clear();
            }
            synchronized (lineage) {
                lineage.clear();
            }
        }
    }

    public static class TubeInitializationException extends RuntimeException {
        public TubeInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}