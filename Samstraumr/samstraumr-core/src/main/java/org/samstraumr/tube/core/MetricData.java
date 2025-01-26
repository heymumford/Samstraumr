package org.samstraumr.tube.core;

public class MetricData {
    // Fields for metric data
    private final String metricName;
    private final long windowSize;  // e.g., time window for the metric in milliseconds
    private final String aggregation; // e.g., "average", "sum", etc.
    private final String thresholdAlert;

    // Constructor
    public MetricData(String metricName, long windowSize, String aggregation, String thresholdAlert) {
        this.metricName = metricName;
        this.windowSize = windowSize;
        this.aggregation = aggregation;
        this.thresholdAlert = thresholdAlert;
    }

    // Getters for each field
    public String getMetricName() {
        return metricName;
    }

    public long getWindowSize() {
        return windowSize;
    }

    public String getAggregation() {
        return aggregation;
    }

    public String getThresholdAlert() {
        return thresholdAlert;
    }

    // Method to check if this MetricData supports a specific aggregation type
    public boolean hasAggregation(String aggregationType) {
        return this.aggregation.equalsIgnoreCase(aggregationType);
    }
}
