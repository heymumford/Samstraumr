package org.samstraumr.tube;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.UUID;

/**
 * Represents the identity of a Tube based on the biological continuity model.
 *
 * <p>The TubeIdentity class encapsulates all aspects of a tube's substrate identity:
 * <ul>
 *   <li>Universally Unique Identifier (UUID)</li>
 *   <li>Creation timestamp (conception time)</li>
 *   <li>Lineage information (parent-child relationships)</li>
 *   <li>Environmental context at creation</li>
 *   <li>Creation reason and metadata</li>
 * </ul>
 *
 * <p>Once created, the core identity properties are immutable, ensuring a stable foundation
 * for a tube's existence throughout its lifecycle.
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
    public TubeIdentity(String uniqueId, String reason, Instant conceptionTime,
                        Map<String, String> environmentalContext, TubeIdentity parent,
                        String address) {
        this.uniqueId = uniqueId;
        this.reason = reason;
        this.conceptionTime = conceptionTime;
        this.environmentalContext = Collections.unmodifiableMap(
                new HashMap<>(environmentalContext));
        this.parentIdentity = parent;
        this.descendants = new CopyOnWriteArrayList<>();
        this.hierarchicalAddress = address;
        
        // Register with parent if it exists
        if (parent != null) {
            parent.registerDescendant(this);
        }
    }
    
    /**
     * Creates an "Adam" TubeIdentity with no parent.
     *
     * @param reason The reason for creating this tube
     * @param environment The environment in which this tube exists
     * @return A new TubeIdentity instance
     */
    public static TubeIdentity createAdamIdentity(String reason, Environment environment) {
        String uniqueId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        Map<String, String> context = environment.captureEnvironmentalContext();
        String address = "T<" + uniqueId.substring(0, 8) + ">";
        
        return new TubeIdentity(uniqueId, reason, now, context, null, address);
    }
    
    /**
     * Creates a child TubeIdentity with a parent reference.
     *
     * @param reason The reason for creating this tube
     * @param environment The environment in which this tube exists
     * @param parent The parent tube's identity
     * @return A new TubeIdentity instance
     */
    public static TubeIdentity createChildIdentity(String reason, Environment environment, 
                                                 TubeIdentity parent) {
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
     * Checks if this tube is an "Adam" tube (has no parent).
     *
     * @return true if this is an "Adam" tube, false otherwise
     */
    public boolean isAdamTube() {
        return parentIdentity == null;
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
        } else {
            certificate.append("Parent: None (Adam Tube)\n");
        }
        
        certificate.append("\nEnvironmental Context:\n");
        environmentalContext.forEach((key, value) -> 
            certificate.append("  ").append(key).append(": ").append(value).append("\n"));
        
        return certificate.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
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