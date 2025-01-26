package org.samstraumr.tube.core;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class VitalStats {
    private long memoryUsage;
    private int cpuCores;
    private final Map<String, Stat> stats;

    public VitalStats() {
        this.memoryUsage = 0;
        this.cpuCores = 0;
        this.stats = new HashMap<>();

        // Initialize default statistics with keys
        stats.put("memoryUsage", new Stat("Memory", memoryUsage, Instant.now()));
        stats.put("cpuCores", new Stat("CPU", cpuCores, Instant.now()));
    }

    // Update the memory usage and CPU core stats
    public void updateStats(long memoryUsage, int cpuCores) {
        this.memoryUsage = memoryUsage;
        this.cpuCores = cpuCores;

        // Update the stats map with the latest values
        stats.put("memoryUsage", new Stat("Memory", memoryUsage, Instant.now()));
        stats.put("cpuCores", new Stat("CPU", cpuCores, Instant.now()));
    }

    // Getters for memory usage and CPU cores
    public long getMemoryUsage() {
        return memoryUsage;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    // Retrieve a specific statistic by key
    public Stat getStatistic(String key) {
        return stats.getOrDefault(key, null);
    }

    // Inner class to represent a single stat
    public static class Stat {
        private final String type;
        private final long value;
        private final Instant lastUpdate;

        public Stat(String type, long value, Instant lastUpdate) {
            this.type = type;
            this.value = value;
            this.lastUpdate = lastUpdate;
        }

        public String getType() {
            return type;
        }

        public long getValue() {
            return value;
        }

        public Instant getLastUpdate() {
            return lastUpdate;
        }
    }
}
