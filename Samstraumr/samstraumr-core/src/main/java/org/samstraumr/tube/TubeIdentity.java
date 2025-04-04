/*
 * Implementation of the TubeIdentity concept in the Samstraumr framework
 * 
 * This class implements the core functionality for TubeIdentity in the Samstraumr
 * tube-based processing framework. It provides the essential infrastructure for
 * the tube ecosystem to maintain its hierarchical design and data processing capabilities.
 * 
 * Key features:
 * - Implementation of the TubeIdentity concept
 * - Integration with the tube substrate model
 * - Support for hierarchical tube organization
 */

package org.samstraumr.tube;

// Standard Java imports
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

// Third-party imports
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
  /**
   * Creates a new TubeIdentity with the specified properties.
   *
   * @param uniqueId The unique identifier for this tube
   * @param reason The reason for creating this tube
   * @param conceptionTime The timestamp when this tube was created
   * @param environmentalContext The environmental context at creation time
   * @param parent The parent tube's identity (may be null for "Adam" tubes)
   * @param address The hierarchical address of this tube
   */
  /**
   * Creates an "Adam" TubeIdentity with no parent. An Adam tube is the first tube created in a
   * system or environment, and by definition cannot have a parent. It serves as the origin point
   * for a tube hierarchy.
   *
   * @param reason The reason for creating this tube
   * @param environment The environment in which this tube exists
   * @return A new TubeIdentity instance representing an Adam tube
   * @throws IllegalArgumentException if parent parameter is provided (Adam tubes must not have
   *     parents)
   */
  /**
   * Creates a child TubeIdentity with a parent reference. Child tubes must always have a valid
   * parent reference, establishing a clear hierarchy.
   *
   * @param reason The reason for creating this tube
   * @param environment The environment in which this tube exists
   * @param parent The parent tube's identity
   * @return A new TubeIdentity instance
   * @throws IllegalArgumentException if parent is null (use createAdamIdentity for parentless
   *     tubes)
   */
  /**
   * Gets the unique identifier of this tube.
   *
   * @return The unique identifier
   */
  /**
   * Gets the reason for creating this tube.
   *
   * @return The creation reason
   */
  /**
   * Gets the timestamp when this tube was created.
   *
   * @return The conception timestamp
   */
  /**
   * Gets the environmental context at the time of creation.
   *
   * @return An unmodifiable map of environmental context properties
   */
  /**
   * Gets the parent tube's identity if it exists.
   *
   * @return The parent tube's identity or null if this is an "Adam" tube
   */
  /**
   * Gets the hierarchical address of this tube.
   *
   * @return The hierarchical address
   */
  /**
   * Gets the user-defined name for this tube if it exists.
   *
   * @return The user-defined name or null if not set
   */
  /**
   * Sets a user-defined name for this tube.
   *
   * @param name The user-defined name
   */
  /**
   * Gets the list of descendant tubes.
   *
   * @return An unmodifiable list of descendant tube identities
   */
  /**
   * Registers a descendant tube with this tube.
   *
   * @param descendant The descendant tube's identity
   */
  /**
   * Checks if this tube is an "Adam" tube (has no parent). Adam tubes are the first tubes created
   * in a system and serve as origin points for tube hierarchies.
   *
   * @return true if this is an "Adam" tube (no parent), false otherwise
   */
  /**
   * Checks if this tube is a child tube (has a parent). Child tubes always have a parent reference,
   * establishing their position in the tube hierarchy.
   *
   * @return true if this is a child tube (has parent), false otherwise
   */
  /**
   * Generates a birth certificate for this tube containing key identity information.
   *
   * @return A formatted string containing the birth certificate information
   */
public class TubeIdentity {
  private final String uniqueId;
  private final Instant conceptionTime;
  private final String reason;
  private final Map<String, String> environmentalContext;
  private final TubeIdentity parentIdentity;
  private final CopyOnWriteArrayList<TubeIdentity> descendants;
  private final String hierarchicalAddress;
  private volatile String userDefinedName;

  /**
   * Creates a new TubeIdentity with the specified properties.
   *
   * @param uniqueId The unique identifier for this tube
   * @param reason The reason for creating this tube
   * @param conceptionTime The timestamp when this tube was created
   * @param environmentalContext The environmental context at creation time
   * @param parent The parent tube's identity (may be null for "Adam" tubes)
   * @param address The hierarchical address of this tube
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP2",
      justification =
          "TubeIdentity is effectively immutable and copying would break parent-child relations")
  public TubeIdentity(
      String uniqueId,
      String reason,
      Instant conceptionTime,
      Map<String, String> environmentalContext,
      TubeIdentity parent,
      String address) {
    this.uniqueId = uniqueId;
    this.reason = reason;
    this.conceptionTime = conceptionTime;
    this.environmentalContext = Collections.unmodifiableMap(new HashMap<>(environmentalContext));
    this.parentIdentity = parent;
    this.descendants = new CopyOnWriteArrayList<>();
    this.hierarchicalAddress = address;

    // Register with parent if it exists
    if (parent != null) {
      parent.registerDescendant(this);
    }
  }

  /**
   * Creates an "Adam" TubeIdentity with no parent. An Adam tube is the first tube created in a
   * system or environment, and by definition cannot have a parent. It serves as the origin point
   * for a tube hierarchy.
   *
   * @param reason The reason for creating this tube
   * @param environment The environment in which this tube exists
   * @return A new TubeIdentity instance representing an Adam tube
   * @throws IllegalArgumentException if parent parameter is provided (Adam tubes must not have
   *     parents)
   */
  public static TubeIdentity createAdamIdentity(String reason, Environment environment) {
    if (reason == null || reason.trim().isEmpty()) {
      throw new IllegalArgumentException("Adam tube must have a valid creation reason");
    }

    if (environment == null) {
      throw new IllegalArgumentException("Adam tube must have a valid environment");
    }

    String uniqueId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    Map<String, String> context = environment.captureEnvironmentalContext();
    String address = "T<" + uniqueId.substring(0, 8) + ">";

    return new TubeIdentity(uniqueId, reason, now, context, null, address);
  }

  /**
   * Creates a child TubeIdentity with a parent reference. Child tubes must always have a valid
   * parent reference, establishing a clear hierarchy.
   *
   * @param reason The reason for creating this tube
   * @param environment The environment in which this tube exists
   * @param parent The parent tube's identity
   * @return A new TubeIdentity instance
   * @throws IllegalArgumentException if parent is null (use createAdamIdentity for parentless
   *     tubes)
   */
  public static TubeIdentity createChildIdentity(
      String reason, Environment environment, TubeIdentity parent) {
    if (reason == null || reason.trim().isEmpty()) {
      throw new IllegalArgumentException("Child tube must have a valid creation reason");
    }

    if (environment == null) {
      throw new IllegalArgumentException("Child tube must have a valid environment");
    }

    if (parent == null) {
      throw new IllegalArgumentException(
          "Child tube must have a parent reference. "
              + "For tubes without parents, use createAdamIdentity instead.");
    }

    String uniqueId = UUID.randomUUID().toString();
    Instant now = Instant.now();
    Map<String, String> context = environment.captureEnvironmentalContext();
    String address = parent.getHierarchicalAddress() + ".T<" + uniqueId.substring(0, 8) + ">";

    return new TubeIdentity(uniqueId, reason, now, context, parent, address);
  }

  /**
   * Gets the unique identifier of this tube.
   *
   * @return The unique identifier
   */
  public String getUniqueId() {
    return uniqueId;
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
   * Gets the timestamp when this tube was created.
   *
   * @return The conception timestamp
   */
  public Instant getConceptionTime() {
    return conceptionTime;
  }

  /**
   * Gets the environmental context at the time of creation.
   *
   * @return An unmodifiable map of environmental context properties
   */
  public Map<String, String> getEnvironmentalContext() {
    return environmentalContext;
  }

  /**
   * Gets the parent tube's identity if it exists.
   *
   * @return The parent tube's identity or null if this is an "Adam" tube
   */
  @SuppressFBWarnings(
      value = "EI_EXPOSE_REP",
      justification = "TubeIdentity is effectively immutable after construction")
  public TubeIdentity getParentIdentity() {
    return parentIdentity;
  }

  /**
   * Gets the hierarchical address of this tube.
   *
   * @return The hierarchical address
   */
  public String getHierarchicalAddress() {
    return hierarchicalAddress;
  }

  /**
   * Gets the user-defined name for this tube if it exists.
   *
   * @return The user-defined name or null if not set
   */
  public String getUserDefinedName() {
    return userDefinedName;
  }

  /**
   * Sets a user-defined name for this tube.
   *
   * @param name The user-defined name
   */
  public void setUserDefinedName(String name) {
    this.userDefinedName = name;
  }

  /**
   * Gets the list of descendant tubes.
   *
   * @return An unmodifiable list of descendant tube identities
   */
  public List<TubeIdentity> getDescendants() {
    return Collections.unmodifiableList(new ArrayList<>(descendants));
  }

  /**
   * Registers a descendant tube with this tube.
   *
   * @param descendant The descendant tube's identity
   */
  void registerDescendant(TubeIdentity descendant) {
    descendants.add(descendant);
  }

  /**
   * Checks if this tube is an "Adam" tube (has no parent). Adam tubes are the first tubes created
   * in a system and serve as origin points for tube hierarchies.
   *
   * @return true if this is an "Adam" tube (no parent), false otherwise
   */
  public boolean isAdamTube() {
    return parentIdentity == null;
  }

  /**
   * Checks if this tube is a child tube (has a parent). Child tubes always have a parent reference,
   * establishing their position in the tube hierarchy.
   *
   * @return true if this is a child tube (has parent), false otherwise
   */
  public boolean isChildTube() {
    return parentIdentity != null;
  }

  /**
   * Generates a birth certificate for this tube containing key identity information.
   *
   * @return A formatted string containing the birth certificate information
   */
  public String generateBirthCertificate() {
    StringBuilder certificate = new StringBuilder();
    certificate.append("TUBE BIRTH CERTIFICATE\n");
    certificate.append("----------------------\n");
    certificate.append("Unique ID: ").append(uniqueId).append("\n");
    certificate.append("Created: ").append(conceptionTime).append("\n");
    certificate.append("Reason: ").append(reason).append("\n");
    certificate.append("Address: ").append(hierarchicalAddress).append("\n");

    if (userDefinedName != null) {
      certificate.append("Name: ").append(userDefinedName).append("\n");
    }

    if (parentIdentity != null) {
      certificate.append("Parent ID: ").append(parentIdentity.getUniqueId()).append("\n");
      certificate.append("Origin Type: Child Tube\n");
    } else {
      certificate.append("Parent: None\n");
      certificate.append("Origin Type: Adam Tube (System Origin Point)\n");
    }

    certificate.append("\nEnvironmental Context:\n");
    environmentalContext.forEach(
        (key, value) ->
            certificate.append("  ").append(key).append(": ").append(value).append("\n"));

    return certificate.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    TubeIdentity other = (TubeIdentity) obj;
    return uniqueId.equals(other.uniqueId);
  }

  @Override
  public int hashCode() {
    return uniqueId.hashCode();
  }

  @Override
  public String toString() {
    String name = userDefinedName != null ? userDefinedName : uniqueId.substring(0, 8);
    return "Tube[" + name + ", " + reason + "]";
  }
}