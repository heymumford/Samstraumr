package org.samstraumr.tube.core;

import java.time.Instant;
import java.util.UUID;

public class BirthCertificate {
    private final UUID universalId;
    private final Instant birthTime;
    private final String purpose;
    private final String creatorId;

    public BirthCertificate(String uuid, Instant birthTime, String purpose) {
        this.universalId = UUID.fromString(uuid);
        this.birthTime = birthTime;
        this.purpose = purpose;
        this.creatorId = null; // Creator ID can be null for Adam tubes
    }

    // Getters for birth certificate properties
    public UUID getUniversalId() {
        return universalId;
    }

    public Instant getBirthTime() {
        return birthTime;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getCreatorId() {
        return creatorId;
    }

    // Method to display birth certificate details
    public void displayBirthCertificate() {
        System.out.printf("Birth Certificate - UUID: %s, Birth Time: %s, Purpose: %s, Creator ID: %s%n",
                universalId, birthTime, purpose, creatorId != null ? creatorId : "N/A");
    }

    // Additional birth certificate-related functionality can be added here
}
