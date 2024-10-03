package org.samstraumr.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TubeWorkload {
    private static final double DANGEROUS_WORKLOAD_FACTOR = 1.5;
    private static final double SCALING_UP_FACTOR = 1.2;
    private static final double SCALING_DOWN_FACTOR = 0.5;

    private final TubeLogger tubeLogger;
    private int currentWorkload;
    private int assignedWorkload;
    private final List<Tube> replicas;

    public TubeWorkload(TubeLogger tubeLogger) {
        this.tubeLogger = tubeLogger;
        this.replicas = new ArrayList<>();
    }

    public void setCurrentWorkload(int workload) {
        this.currentWorkload = workload;
    }

    public int getCurrentWorkload() {
        return currentWorkload;
    }

    public void detectScalingNeed() {
        if (currentWorkload > assignedWorkload * SCALING_UP_FACTOR) {
            createReplica();
        } else if (currentWorkload < assignedWorkload * SCALING_DOWN_FACTOR && !replicas.isEmpty()) {
            removeReplica();
        }
    }

    private void createReplica() {
        // Logic for creating a replica
        tubeLogger.log("info", "New replica created due to high workload.", "scaling");
    }

    private void removeReplica() {
        // Logic for removing a replica
        tubeLogger.log("info", "Replica removed due to low workload.", "scaling");
    }

    public List<Tube> getReplicas() {
        return Collections.unmodifiableList(replicas);
    }

    public int getAssignedWorkload() {
        return assignedWorkload;
    }

    public void setAssignedWorkload(int assignedWorkload) {
        this.assignedWorkload = assignedWorkload;
    }

    public boolean isWorkloadDangerous() {
        return currentWorkload > assignedWorkload * DANGEROUS_WORKLOAD_FACTOR;
    }
}
