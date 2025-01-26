package org.samstraumr.tube.impl.monitoring;

import org.samstraumr.tube.Tube;
import org.samstraumr.tube.core.VitalStats;

import java.time.Instant;

public class MonitoringSystem {
    private final Tube tube;
    private VitalStats currentVitalStats;
    private Instant lastUpdated;

    public MonitoringSystem(Tube tube) {
        this.tube = tube;
        this.currentVitalStats = new VitalStats();
        this.lastUpdated = Instant.now();
    }

    // Method to start monitoring the tube
    public void startMonitoring() {
        System.out.printf("Starting monitoring for Tube: %s%n", tube.getIdentity());
        updateVitalStats();
    }

    // Method to update vital statistics
    public void updateVitalStats() {
        currentVitalStats.updateStats(tube.getMemoryUsage(), tube.getCpuCores());
        lastUpdated = Instant.now();
        System.out.printf("Updated vital stats at %s - Memory Usage: %d bytes, CPU Cores: %d%n",
                lastUpdated, currentVitalStats.getMemoryUsage(), currentVitalStats.getCpuCores());
    }

    // Method to get the latest vital statistics
    public VitalStats getCurrentVitalStats() {
        return currentVitalStats;
    }

    // Method to get the last updated time
    public Instant getLastUpdated() {
        return lastUpdated;
    }

    // Additional monitoring-related functionality can be added here
}
