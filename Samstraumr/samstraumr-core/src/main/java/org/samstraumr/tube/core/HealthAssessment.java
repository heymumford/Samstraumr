package org.samstraumr.tube.core;

import java.util.HashMap;
import java.util.Map;

public class HealthAssessment {
    private boolean healthy;
    private String healthMessage;
    private final Map<String, HealthMetric> healthAspects;

    public HealthAssessment() {
        this.healthy = true;
        this.healthMessage = "All systems are normal.";
        this.healthAspects = new HashMap<>();

        // Initialize with default health aspects (e.g., CPU, Memory)
        healthAspects.put("Memory Usage", new HealthMetric("Memory Usage", 0, (int) (Runtime.getRuntime().maxMemory() * 0.8)));
    }

    // Method to evaluate health based on vital stats
    public void evaluateHealth(VitalStats vitalStats) {
        HealthMetric memoryMetric = healthAspects.get("Memory Usage");

        if (vitalStats.getMemoryUsage() > memoryMetric.getMax()) {
            this.healthy = false;
            this.healthMessage = "Warning: Memory usage is above 80%.";
            memoryMetric.setStatus(false, "High memory usage");
        } else {
            this.healthy = true;
            this.healthMessage = "All systems are normal.";
            memoryMetric.setStatus(true, "Memory usage normal");
        }
    }

    // Get overall health status
    public boolean isHealthy() {
        return healthy;
    }

    public String getHealthMessage() {
        return healthMessage;
    }

    // Method to retrieve a specific health aspect
    public HealthMetric getHealthAspect(String aspect) {
        return healthAspects.getOrDefault(aspect, null); // Return null if aspect is not found
    }

    // Method to display health status
    public void displayHealthStatus() {
        System.out.printf("Health Status: %s - %s%n", healthy ? "Healthy" : "Unhealthy", healthMessage);
    }

    // Inner class representing a health metric
    public static class HealthMetric {
        private final String name;
        private final int min;
        private final int max;
        private boolean withinRange;
        private String message;

        public HealthMetric(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
            this.withinRange = true;
            this.message = "Normal";
        }

        // Method to check if a value is within the healthy range
        public boolean isWithinRange(int value) {
            return value >= min && value <= max;
        }

        public void setStatus(boolean withinRange, String message) {
            this.withinRange = withinRange;
            this.message = message;
        }

        public String getName() {
            return name;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public boolean isWithinRange() {
            return withinRange;
        }

        public String getMessage() {
            return message;
        }
    }
}
