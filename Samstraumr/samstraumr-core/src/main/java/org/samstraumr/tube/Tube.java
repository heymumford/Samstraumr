package org.samstraumr.tube;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tube {
  private static final Logger LOGGER = LoggerFactory.getLogger(Tube.class);
  private static final int DEFAULT_TERMINATION_DELAY = 60; // seconds
  private static final String DIGEST_ALGORITHM = "SHA-256";

  private final String uniqueId;
  private final String reason;
  private final List<String> lineage;
  private final List<String> mimirLog;
  private final Environment environment;
  private volatile Timer terminationTimer;

  private Tube(String reason, Environment environment, String uniqueId) {
    this.reason = reason;
    this.environment = environment;
    this.lineage = Collections.synchronizedList(new ArrayList<>(Collections.singletonList(reason)));
    this.mimirLog = Collections.synchronizedList(new LinkedList<>());
    this.uniqueId = uniqueId;

    // Initialize in the constructor without throwing exceptions
    logToMimir("Tube initialized with ID: " + this.uniqueId);
    LOGGER.debug("Tube initialized with ID: {}", this.uniqueId);
    logToMimir("Initialization reason: " + this.reason);

    // Initialize timer directly instead of using setTerminationDelay which might throw exceptions
    try {
      synchronized (this) {
        terminationTimer = new Timer();
        terminationTimer.schedule(new TerminationTask(), DEFAULT_TERMINATION_DELAY * 1000L);
      }
      logToMimir("Termination delay set to " + DEFAULT_TERMINATION_DELAY + " seconds.");
    } catch (Exception e) {
      // Just log the error without throwing
      LOGGER.error("Failed to set termination delay: {}", e.getMessage());
      logToMimir("Warning: Failed to set termination delay");
    }

    logToMimir("Environment: " + environment.getParameters());
  }

  /**
   * Factory method to create a new Tube instance.
   *
   * @param reason the reason for creating this tube
   * @param environment the environment in which this tube operates
   * @return a new Tube instance
   * @throws TubeInitializationException if initialization fails
   */
  public static Tube create(String reason, Environment environment) {
    LOGGER.info("Creating new Tube with reason: {}", reason);

    // Validate parameters
    validateParameters(reason, environment);

    try {
      // Generate unique ID
      String uniqueId = generateSHA256UniqueId(reason + environment.getParameters());

      // Create and return new tube
      return new Tube(reason, environment, uniqueId);
    } catch (Exception e) {
      LOGGER.error("Failed to initialize Tube: {} - initialization failed", reason);
      throw new TubeInitializationException("Failed to initialize Tube", e);
    }
  }

  /**
   * Validates the parameters used to create a tube.
   *
   * @param reason the reason for creating the tube
   * @param environment the environment in which the tube operates
   * @throws TubeInitializationException if parameters are invalid
   */
  private static void validateParameters(String reason, Environment environment) {
    if (environment == null) {
      LOGGER.error("Environment cannot be null");
      throw new TubeInitializationException(
          "Environment cannot be null", new NullPointerException("Environment cannot be null"));
    }

    if (reason == null || reason.trim().isEmpty()) {
      LOGGER.error("Reason cannot be null or empty");
      throw new TubeInitializationException(
          "Reason cannot be null or empty",
          new IllegalArgumentException("Reason cannot be null or empty"));
    }
  }

  // initializeTube method has been inlined in the constructor to avoid exceptions

  /**
   * Generates a unique SHA-256 ID from the provided parameters.
   *
   * @param parameters the parameters to hash
   * @return a SHA-256 hash of the parameters as a hex string
   * @throws TubeInitializationException if the hash generation fails
   */
  private static String generateSHA256UniqueId(String parameters) {
    try {
      MessageDigest digest = MessageDigest.getInstance(DIGEST_ALGORITHM);
      byte[] hash = digest.digest((parameters + Instant.now().toString()).getBytes("UTF-8"));
      return bytesToHex(hash);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Failed to generate unique ID: SHA-256 algorithm not found", e);
      throw new TubeInitializationException("Failed to generate unique ID", e);
    } catch (java.io.UnsupportedEncodingException e) {
      LOGGER.error("UTF-8 encoding not supported", e);
      throw new TubeInitializationException("UTF-8 encoding not supported", e);
    }
  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
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
    LOGGER.debug("Querying Mimir log. Current size: {}", mimirLog.size());
    return Collections.unmodifiableList(mimirLog);
  }

  public synchronized void setTerminationDelay(int seconds) {
    LOGGER.info("Setting termination delay to {} seconds", seconds);
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
      LOGGER.error("Invalid termination delay: {}", seconds, e);
      throw new IllegalArgumentException("Invalid termination delay", e);
    }
  }

  /**
   * Logs an entry to the Mimir log.
   *
   * @param logEntry the entry to log
   */
  public void logToMimir(String logEntry) {
    String timestampedEntry = Instant.now().toString() + ": " + logEntry;
    mimirLog.add(timestampedEntry);
    LOGGER.trace("Mimir Log: {}", timestampedEntry);
  }

  /**
   * Adds a reason to the tube's lineage.
   *
   * @param reason the reason to add to lineage
   */
  public void addToLineage(String reason) {
    LOGGER.debug("Adding to lineage: {}", reason);
    if (reason != null && !reason.trim().isEmpty()) {
      lineage.add(reason);
      logToMimir("Added to lineage: " + reason);
    }
  }

  private class TerminationTask extends TimerTask {
    @Override
    public void run() {
      synchronized (Tube.this) {
        LOGGER.info("Executing termination task for Tube: {}", uniqueId);
        logToMimir("Tube self-terminating.");
        terminationTimer.cancel();
        clearLogsAndLineage();
        LOGGER.debug("Tube {} terminated. Mimir log and lineage cleared.", uniqueId);
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
