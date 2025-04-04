/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Implementation of the TubeIdentity concept in the Samstraumr framework
 */

package org.samstraumr.tube;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Represents the identity of a Tube based on the biological continuity model.
 *
 * <p>The TubeIdentity class encapsulates all aspects of a tube's substrate identity:
 *
 * <ul>
 *   <li>Universally Unique Identifier (UUID)
 *   <li>Creation timestamp (conception time)
 *   <li>Lineage information (parent-child relationships)
 *   <li>Environmental context at creation
 *   <li>Creation reason and metadata
 * </ul>
 *
 * <p>Once created, the core identity properties are immutable, ensuring a stable foundation for a
 * tube's existence throughout its lifecycle.
 */
public class TubeIdentity {
  private final String uniqueId;
  private final Instant conceptionTime;
  private final String reason;
  private final Map<String, String> environmentContext;
  private final List<String> lineage;

  /**
   * Creates a new TubeIdentity with the specified properties.
   *
   * @param uniqueId The unique identifier for this tube
   * @param reason The reason for creating this tube
   */
  public TubeIdentity(String uniqueId, String reason) {
    this.uniqueId = uniqueId;
    this.conceptionTime = Instant.now();
    this.reason = reason;
    this.environmentContext = new HashMap<>();
    this.lineage = new CopyOnWriteArrayList<>();
    this.lineage.add(reason);
  }

  /**
   * Creates a new TubeIdentity with a randomly generated UUID.
   *
   * @param reason The reason for creating this tube
   * @return A new TubeIdentity instance
   */
  public static TubeIdentity createWithRandomId(String reason) {
    String uniqueId = UUID.randomUUID().toString();
    return new TubeIdentity(uniqueId, reason);
  }

  /**
   * Gets the unique identifier for this tube.
   *
   * @return The unique identifier
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Gets the conception time (creation timestamp) for this tube.
   *
   * @return The conception time
   */
  public Instant getConceptionTime() {
    return conceptionTime;
  }

  /**
   * Gets the reason for creating this tube.
   *
   * @return The creation reason
   */
  public String getReason() {
    return reason;
  }

  /**
   * Gets the environmental context at the time of tube creation.
   *
   * @return An unmodifiable view of the environmental context
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "Returning unmodifiable view")
  public Map<String, String> getEnvironmentContext() {
    return Collections.unmodifiableMap(environmentContext);
  }

  /**
   * Adds environmental context information.
   *
   * @param key The context key
   * @param value The context value
   */
  public void addEnvironmentContext(String key, String value) {
    environmentContext.put(key, value);
  }

  /**
   * Gets the lineage information for this tube.
   *
   * @return An unmodifiable view of the lineage
   */
  public List<String> getLineage() {
    return Collections.unmodifiableList(lineage);
  }

  /**
   * Adds an entry to the tube's lineage.
   *
   * @param entry The lineage entry to add
   */
  public void addToLineage(String entry) {
    lineage.add(entry);
  }

  @Override
  public String toString() {
    return "TubeIdentity["
        + "id="
        + uniqueId
        + ", conception="
        + conceptionTime
        + ", reason='"
        + reason
        + "'"
        + "]";
  }
}
