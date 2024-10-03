package org.samstraumr.core;

public class TubeTransformation {
    private final Tube parentTube;
    private final TubeLogger tubeLogger;
    private Tube transformedTube;

    public TubeTransformation(Tube parentTube, TubeLogger tubeLogger) {
        this.parentTube = parentTube;
        this.tubeLogger = tubeLogger;
    }

    public void evolvePurpose(String newReason) {
        // Logic for evolving purpose
        tubeLogger.log("info", "Purpose evolved to: " + newReason, "evolution");
    }

    public void transformIntoNewEntity(String newReason) {
        // Logic for transforming into a new entity
        tubeLogger.log("info", "Transformed into new entity with reason: " + newReason, "transformation");
    }

    public Tube getTransformedTube() {
        return transformedTube;
    }
}
