/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.tube.legacy.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.s8r.tube.legacy.core.exception.TubeInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Core implementation of the Tube concept in the Samstraumr framework.
 *
 * <p>This class provides the essential infrastructure for tubes to maintain their hierarchical
 * design and data processing capabilities.
 */
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
  private String environmentState = "normal";
  private TubeStatus status = TubeStatus.INITIALIZING;

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
    this.status = TubeStatus.READY;

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
    if (reason == null || reason.trim().isEmpty()) {
      LOGGER.error("Reason cannot be null or empty");
      throw new TubeInitializationException("Reason cannot be null or empty");
    }

    if (environment == null) {
      LOGGER.error("Environment cannot be null");
      throw new TubeInitializationException("Environment cannot be null");
    }
  }

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
      byte[] encodedhash = digest.digest(parameters.getBytes());

      // Convert byte array to hex string
      StringBuilder hexString = new StringBuilder();
      for (byte b : encodedhash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Failed to generate unique ID: {}", e.getMessage());
      throw new TubeInitializationException("Failed to generate unique ID", e);
    }
  }

  /**
   * Gets the current status of this tube.
   *
   * @return the current TubeStatus
   */
  public TubeStatus getStatus() {
    return status;
  }

  /**
   * Sets the status of this tube.
   *
   * @param newStatus the new status to set
   */
  public void setStatus(TubeStatus newStatus) {
    this.status = newStatus;
    logToMimir("Status changed to: " + newStatus);
  }

  /**
   * Sets the termination delay for this tube.
   *
   * @param delaySeconds delay in seconds before the tube is automatically terminated
   * @throws IllegalArgumentException if the delay is not positive
   * @throws IllegalStateException if the tube is already terminated
   */
  public void setTerminationDelay(int delaySeconds) {
    if (delaySeconds <= 0) {
      LOGGER.error("Termination delay must be positive: {}", delaySeconds);
      throw new IllegalArgumentException("Termination delay must be positive");
    }

    if (status == TubeStatus.TERMINATED) {
      LOGGER.error("Cannot set termination delay: tube is already terminated");
      throw new IllegalStateException("Cannot set termination delay: tube is already terminated");
    }

    synchronized (this) {
      if (terminationTimer != null) {
        terminationTimer.cancel();
      }
      terminationTimer = new Timer();
      terminationTimer.schedule(new TerminationTask(), delaySeconds * 1000L);
    }

    LOGGER.info("Setting termination delay to {} seconds", delaySeconds);
    logToMimir("Termination delay set to " + delaySeconds + " seconds");
  }

  /**
   * Gets the unique ID of this tube.
   *
   * @return the unique ID
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Gets the reason for this tube's creation.
   *
   * @return the reason string
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the lineage (history) of this tube.
   *
   * @return an unmodifiable view of the lineage
   */
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  /**
   * Gets the environment in which this tube operates.
   *
   * @return the tube's environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  /**
   * Logs an entry to the Mimir log with timestamp. This internal log allows the tube to maintain
   * state information and debug data.
   *
   * @param logEntry the entry to log
   */
  protected void logToMimir(String logEntry) {
    String timestampedEntry = Instant.now().toString() + ": " + logEntry;
    mimirLog.add(timestampedEntry);
  }

  /**
   * Gets the Mimir log entries for this tube.
   *
   * @return an unmodifiable view of the Mimir log
   */
  public List<String> getMimirLog() {
    return Collections.unmodifiableList(mimirLog);
  }

  /**
   * Gets the current number of entries in the Mimir log.
   *
   * @return the number of log entries
   */
  public int getMimirLogSize() {
    LOGGER.debug("Querying Mimir log. Current size: {}", mimirLog.size());
    return mimirLog.size();
  }

  /**
   * Adds a reason to the tube's lineage.
   *
   * @param reason the reason to add to lineage
   */
  public void addToLineage(String reason) {
    if (reason != null && !reason.isEmpty()) {
      lineage.add(reason);
      logToMimir("Added to lineage: " + reason);
    }
  }

  /** Terminates this tube, canceling any scheduled tasks and releasing resources. */
  public void terminate() {
    synchronized (this) {
      if (status == TubeStatus.TERMINATED) {
        return;
      }

      if (terminationTimer != null) {
        terminationTimer.cancel();
        terminationTimer = null;
      }

      status = TubeStatus.TERMINATED;
    }

    LOGGER.info("Tube terminated: {}", uniqueId);
    logToMimir("Tube terminated");
  }

  /**
   * Updates the tube's awareness of environmental state changes.
   *
   * @param newState the new state of the environment
   */
  public void updateEnvironmentState(String newState) {
    if (newState != null && !newState.equals(environmentState)) {
      String oldState = environmentState;
      environmentState = newState;
      logToMimir("Environment state changed: " + oldState + " -> " + newState);
    }
  }

  /**
   * Gets the current environment state.
   *
   * @return the current environment state
   */
  public String getEnvironmentState() {
    return environmentState;
  }

  /** Task for handling automatic termination of tubes after a delay. */
  private class TerminationTask extends TimerTask {
    @Override
    public void run() {
      terminate();
    }
  }
}
