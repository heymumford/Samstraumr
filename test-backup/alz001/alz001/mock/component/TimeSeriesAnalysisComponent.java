/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.steps.alz001.mock.component;

import org.s8r.test.steps.alz001.mock.ALZ001MockComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.time.Instant;

/**
 * Mock implementation of a time series analysis component for Alzheimer's disease modeling.
 * 
 * <p>This component provides time series analysis capabilities for biomarker data,
 * including decomposition, trend analysis, anomaly detection, and forecasting.
 */
public class TimeSeriesAnalysisComponent extends ALZ001MockComponent {
    
    /**
     * Represents a time series data point.
     */
    public static class TimePoint {
        private final double timestamp; // Time in arbitrary units
        private final double value;
        private final Map<String, Object> metadata;
        
        public TimePoint(double timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
            this.metadata = new HashMap<>();
        }
        
        public double getTimestamp() {
            return timestamp;
        }
        
        public double getValue() {
            return value;
        }
        
        public void setMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        public Object getMetadata(String key) {
            return metadata.get(key);
        }
    }
    
    /**
     * Represents a decomposed time series with trend, seasonal, and residual components.
     */
    public static class DecomposedSeries {
        private final List<Double> original;
        private final List<Double> trend;
        private final List<Double> seasonal;
        private final List<Double> residual;
        
        public DecomposedSeries(List<Double> original, List<Double> trend, 
                                List<Double> seasonal, List<Double> residual) {
            this.original = new ArrayList<>(original);
            this.trend = new ArrayList<>(trend);
            this.seasonal = new ArrayList<>(seasonal);
            this.residual = new ArrayList<>(residual);
        }
        
        public List<Double> getOriginal() {
            return new ArrayList<>(original);
        }
        
        public List<Double> getTrend() {
            return new ArrayList<>(trend);
        }
        
        public List<Double> getSeasonal() {
            return new ArrayList<>(seasonal);
        }
        
        public List<Double> getResidual() {
            return new ArrayList<>(residual);
        }
    }
    
    private final Map<String, List<TimePoint>> timeSeries = new HashMap<>();
    private final Map<String, DecomposedSeries> decompositions = new HashMap<>();
    private final Map<String, List<Double>> forecasts = new HashMap<>();
    private final Map<String, List<Integer>> anomalyIndices = new HashMap<>();
    private final Random random = new Random();
    
    /**
     * Creates a new time series analysis component with the given name.
     *
     * @param name The component name
     */
    public TimeSeriesAnalysisComponent(String name) {
        super(name);
    }
    
    /**
     * Adds a time point to a time series.
     *
     * @param seriesName The time series name
     * @param timestamp The timestamp
     * @param value The value
     * @return The created time point
     */
    public TimePoint addTimePoint(String seriesName, double timestamp, double value) {
        TimePoint point = new TimePoint(timestamp, value);
        
        // Get or create series
        List<TimePoint> series = timeSeries.computeIfAbsent(seriesName, k -> new ArrayList<>());
        
        // Add point to series
        series.add(point);
        
        // Sort by timestamp
        series.sort((p1, p2) -> Double.compare(p1.getTimestamp(), p2.getTimestamp()));
        
        setState("PROCESSING");
        return point;
    }
    
    /**
     * Adds multiple time points to a time series.
     *
     * @param seriesName The time series name
     * @param timestamps The timestamps
     * @param values The values
     */
    public void addTimePoints(String seriesName, List<Double> timestamps, List<Double> values) {
        if (timestamps.size() != values.size()) {
            throw new IllegalArgumentException("Timestamps and values must have the same length");
        }
        
        for (int i = 0; i < timestamps.size(); i++) {
            addTimePoint(seriesName, timestamps.get(i), values.get(i));
        }
    }
    
    /**
     * Gets a time series by name.
     *
     * @param seriesName The time series name
     * @return The time series points, or an empty list if not found
     */
    public List<TimePoint> getTimeSeries(String seriesName) {
        return new ArrayList<>(timeSeries.getOrDefault(seriesName, new ArrayList<>()));
    }
    
    /**
     * Gets all time series names.
     *
     * @return A list of time series names
     */
    public List<String> getAllTimeSeriesNames() {
        return new ArrayList<>(timeSeries.keySet());
    }
    
    /**
     * Extracts the values from a time series.
     *
     * @param seriesName The time series name
     * @return The time series values, or an empty list if not found
     */
    public List<Double> getTimeSeriesValues(String seriesName) {
        List<TimePoint> series = getTimeSeries(seriesName);
        List<Double> values = new ArrayList<>(series.size());
        
        for (TimePoint point : series) {
            values.add(point.getValue());
        }
        
        return values;
    }
    
    /**
     * Extracts the timestamps from a time series.
     *
     * @param seriesName The time series name
     * @return The time series timestamps, or an empty list if not found
     */
    public List<Double> getTimeSeriesTimestamps(String seriesName) {
        List<TimePoint> series = getTimeSeries(seriesName);
        List<Double> timestamps = new ArrayList<>(series.size());
        
        for (TimePoint point : series) {
            timestamps.add(point.getTimestamp());
        }
        
        return timestamps;
    }
    
    /**
     * Generates a synthetic time series with specified components.
     *
     * @param seriesName The time series name
     * @param length The number of points to generate
     * @param trendMagnitude The magnitude of the trend component
     * @param seasonalMagnitude The magnitude of the seasonal component
     * @param noiseMagnitude The magnitude of the noise component
     * @param seasonalPeriod The period of the seasonal component
     * @return The generated time series values
     */
    public List<Double> generateSyntheticTimeSeries(String seriesName, int length, 
                                                  double trendMagnitude, 
                                                  double seasonalMagnitude, 
                                                  double noiseMagnitude,
                                                  int seasonalPeriod) {
        List<Double> timestamps = new ArrayList<>(length);
        List<Double> values = new ArrayList<>(length);
        
        for (int i = 0; i < length; i++) {
            double t = i / (double) length;
            
            // Trend component (sigmoid function for Alzheimer's progression)
            double trend = trendMagnitude / (1 + Math.exp(-10 * (t - 0.5)));
            
            // Seasonal component (sine wave)
            double seasonal = seasonalMagnitude * Math.sin(2 * Math.PI * i / seasonalPeriod);
            
            // Noise component
            double noise = noiseMagnitude * (random.nextGaussian());
            
            // Combine components
            double value = trend + seasonal + noise;
            
            timestamps.add((double) i);
            values.add(value);
        }
        
        // Add to the component
        addTimePoints(seriesName, timestamps, values);
        
        return values;
    }
    
    /**
     * Decomposes a time series into trend, seasonal, and residual components.
     *
     * @param seriesName The time series name
     * @param period The seasonal period
     * @return The decomposed time series
     */
    public DecomposedSeries decomposeTimeSeries(String seriesName, int period) {
        List<Double> values = getTimeSeriesValues(seriesName);
        
        if (values.size() < period * 2) {
            throw new IllegalArgumentException("Time series too short for decomposition with period " + period);
        }
        
        int n = values.size();
        
        // Extract trend using moving average
        List<Double> trend = new ArrayList<>(n);
        int halfPeriod = period / 2;
        
        for (int i = 0; i < n; i++) {
            if (i < halfPeriod || i >= n - halfPeriod) {
                // Use nearest available moving average for endpoints
                trend.add(values.get(i));
            } else {
                // Calculate centered moving average
                double sum = 0;
                for (int j = i - halfPeriod; j <= i + halfPeriod; j++) {
                    sum += values.get(j);
                }
                trend.add(sum / period);
            }
        }
        
        // Extract seasonal component
        List<Double> detrended = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            detrended.add(values.get(i) - trend.get(i));
        }
        
        // Average seasonal component over periods
        double[] seasonalPattern = new double[period];
        int[][] seasonalCounts = new int[period][1];
        
        for (int i = 0; i < n; i++) {
            int season = i % period;
            seasonalPattern[season] += detrended.get(i);
            seasonalCounts[season][0]++;
        }
        
        // Normalize seasonal pattern
        for (int i = 0; i < period; i++) {
            seasonalPattern[i] /= seasonalCounts[i][0];
        }
        
        // Calculate mean of the seasonal pattern
        double seasonalMean = 0;
        for (double v : seasonalPattern) {
            seasonalMean += v;
        }
        seasonalMean /= period;
        
        // Center the seasonal pattern
        for (int i = 0; i < period; i++) {
            seasonalPattern[i] -= seasonalMean;
        }
        
        // Generate seasonal component
        List<Double> seasonal = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            seasonal.add(seasonalPattern[i % period]);
        }
        
        // Calculate residuals
        List<Double> residual = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            residual.add(values.get(i) - trend.get(i) - seasonal.get(i));
        }
        
        // Store decomposition
        DecomposedSeries decomposed = new DecomposedSeries(values, trend, seasonal, residual);
        decompositions.put(seriesName, decomposed);
        
        return decomposed;
    }
    
    /**
     * Gets the decomposition for a time series.
     *
     * @param seriesName The time series name
     * @return The decomposed time series, or null if not decomposed
     */
    public DecomposedSeries getDecomposition(String seriesName) {
        return decompositions.get(seriesName);
    }
    
    /**
     * Detects anomalies in a time series using the Z-score method.
     *
     * @param seriesName The time series name
     * @param threshold The Z-score threshold for anomaly detection
     * @return The indices of anomalies in the time series
     */
    public List<Integer> detectAnomalies(String seriesName, double threshold) {
        List<Double> values = getTimeSeriesValues(seriesName);
        
        if (values.size() < 3) {
            return new ArrayList<>();
        }
        
        // Calculate mean and standard deviation
        double mean = 0;
        for (Double value : values) {
            mean += value;
        }
        mean /= values.size();
        
        double variance = 0;
        for (Double value : values) {
            variance += Math.pow(value - mean, 2);
        }
        variance /= values.size();
        double stdDev = Math.sqrt(variance);
        
        // Calculate Z-scores and detect anomalies
        List<Integer> anomalies = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            double zscore = Math.abs((values.get(i) - mean) / stdDev);
            if (zscore > threshold) {
                anomalies.add(i);
            }
        }
        
        // Store anomalies
        anomalyIndices.put(seriesName, anomalies);
        
        return anomalies;
    }
    
    /**
     * Gets the detected anomalies for a time series.
     *
     * @param seriesName The time series name
     * @return The indices of anomalies, or an empty list if not available
     */
    public List<Integer> getAnomalies(String seriesName) {
        return new ArrayList<>(anomalyIndices.getOrDefault(seriesName, new ArrayList<>()));
    }
    
    /**
     * Forecasts future values of a time series using simple exponential smoothing.
     *
     * @param seriesName The time series name
     * @param horizon The number of future points to forecast
     * @param alpha The smoothing factor (0.0 to 1.0)
     * @return The forecasted values
     */
    public List<Double> forecast(String seriesName, int horizon, double alpha) {
        List<Double> values = getTimeSeriesValues(seriesName);
        
        if (values.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Exponential smoothing
        double lastSmoothed = values.get(0);
        List<Double> smoothed = new ArrayList<>(values.size());
        smoothed.add(lastSmoothed);
        
        for (int i = 1; i < values.size(); i++) {
            lastSmoothed = alpha * values.get(i) + (1 - alpha) * lastSmoothed;
            smoothed.add(lastSmoothed);
        }
        
        // Forecast future values
        List<Double> forecast = new ArrayList<>(horizon);
        for (int i = 0; i < horizon; i++) {
            forecast.add(lastSmoothed);
        }
        
        // Store forecast
        forecasts.put(seriesName, forecast);
        
        return forecast;
    }
    
    /**
     * Gets the forecast for a time series.
     *
     * @param seriesName The time series name
     * @return The forecasted values, or an empty list if not available
     */
    public List<Double> getForecast(String seriesName) {
        return new ArrayList<>(forecasts.getOrDefault(seriesName, new ArrayList<>()));
    }
    
    /**
     * Calculates the cross-correlation between two time series.
     *
     * @param series1Name The first time series name
     * @param series2Name The second time series name
     * @param maxLag The maximum lag to consider
     * @return A map of lag to correlation coefficient
     */
    public Map<Integer, Double> calculateCrossCorrelation(String series1Name, String series2Name, int maxLag) {
        List<Double> series1 = getTimeSeriesValues(series1Name);
        List<Double> series2 = getTimeSeriesValues(series2Name);
        
        if (series1.isEmpty() || series2.isEmpty()) {
            return new HashMap<>();
        }
        
        // Calculate means
        double mean1 = 0;
        for (Double value : series1) {
            mean1 += value;
        }
        mean1 /= series1.size();
        
        double mean2 = 0;
        for (Double value : series2) {
            mean2 += value;
        }
        mean2 /= series2.size();
        
        // Calculate cross-correlation
        Map<Integer, Double> correlation = new LinkedHashMap<>();
        
        for (int lag = -maxLag; lag <= maxLag; lag++) {
            double sum = 0;
            int count = 0;
            
            for (int i = 0; i < series1.size(); i++) {
                int j = i + lag;
                
                if (j >= 0 && j < series2.size()) {
                    sum += (series1.get(i) - mean1) * (series2.get(j) - mean2);
                    count++;
                }
            }
            
            // Normalize
            if (count > 0) {
                double normalization = 0;
                double sum1 = 0;
                double sum2 = 0;
                
                for (int i = 0; i < count; i++) {
                    if (i < series1.size()) {
                        sum1 += Math.pow(series1.get(i) - mean1, 2);
                    }
                    if (i < series2.size()) {
                        sum2 += Math.pow(series2.get(i) - mean2, 2);
                    }
                }
                
                normalization = Math.sqrt(sum1 * sum2);
                
                if (normalization > 0) {
                    correlation.put(lag, sum / normalization);
                } else {
                    correlation.put(lag, 0.0);
                }
            } else {
                correlation.put(lag, 0.0);
            }
        }
        
        return correlation;
    }
    
    /**
     * Calculates basic statistics for a time series.
     *
     * @param seriesName The time series name
     * @return A map of statistic names to values
     */
    public Map<String, Double> calculateStatistics(String seriesName) {
        List<Double> values = getTimeSeriesValues(seriesName);
        Map<String, Double> stats = new HashMap<>();
        
        if (values.isEmpty()) {
            return stats;
        }
        
        // Calculate mean
        double mean = 0;
        for (Double value : values) {
            mean += value;
        }
        mean /= values.size();
        stats.put("mean", mean);
        
        // Calculate variance and standard deviation
        double variance = 0;
        for (Double value : values) {
            variance += Math.pow(value - mean, 2);
        }
        variance /= values.size();
        stats.put("variance", variance);
        stats.put("std_dev", Math.sqrt(variance));
        
        // Calculate min and max
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (Double value : values) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        stats.put("min", min);
        stats.put("max", max);
        stats.put("range", max - min);
        
        // Calculate median
        List<Double> sorted = new ArrayList<>(values);
        sorted.sort(Double::compare);
        double median;
        if (sorted.size() % 2 == 0) {
            median = (sorted.get(sorted.size() / 2 - 1) + sorted.get(sorted.size() / 2)) / 2;
        } else {
            median = sorted.get(sorted.size() / 2);
        }
        stats.put("median", median);
        
        return stats;
    }
    
    /**
     * Finds change points in a time series using a simple sliding window approach.
     *
     * @param seriesName The time series name
     * @param windowSize The window size for change point detection
     * @param threshold The threshold for detecting a change
     * @return The indices of change points
     */
    public List<Integer> findChangePoints(String seriesName, int windowSize, double threshold) {
        List<Double> values = getTimeSeriesValues(seriesName);
        List<Integer> changePoints = new ArrayList<>();
        
        if (values.size() < windowSize * 2) {
            return changePoints;
        }
        
        for (int i = windowSize; i < values.size() - windowSize; i++) {
            // Calculate mean before and after current point
            double meanBefore = 0;
            for (int j = i - windowSize; j < i; j++) {
                meanBefore += values.get(j);
            }
            meanBefore /= windowSize;
            
            double meanAfter = 0;
            for (int j = i; j < i + windowSize; j++) {
                meanAfter += values.get(j);
            }
            meanAfter /= windowSize;
            
            // Calculate variance before and after
            double varBefore = 0;
            for (int j = i - windowSize; j < i; j++) {
                varBefore += Math.pow(values.get(j) - meanBefore, 2);
            }
            varBefore /= windowSize;
            
            double varAfter = 0;
            for (int j = i; j < i + windowSize; j++) {
                varAfter += Math.pow(values.get(j) - meanAfter, 2);
            }
            varAfter /= windowSize;
            
            // Calculate change score
            double meanChange = Math.abs(meanAfter - meanBefore);
            double varChange = Math.abs(varAfter - varBefore);
            double changeScore = meanChange / (Math.sqrt(varBefore + varAfter) + 1e-10);
            
            if (changeScore > threshold) {
                changePoints.add(i);
                // Skip ahead to avoid detecting multiple changes in the same area
                i += windowSize / 2;
            }
        }
        
        return changePoints;
    }
    
    /**
     * Simulates biomarker progression for Alzheimer's disease.
     *
     * @param biomarkerName The biomarker name
     * @param stages The number of disease stages to simulate
     * @param samplesPerStage The number of samples per stage
     * @param progressionRate The rate of progression
     * @param variability The variability in the biomarker levels
     * @return The simulated biomarker values
     */
    public List<Double> simulateBiomarkerProgression(String biomarkerName, int stages, 
                                                    int samplesPerStage, 
                                                    double progressionRate,
                                                    double variability) {
        int totalSamples = stages * samplesPerStage;
        List<Double> timestamps = new ArrayList<>(totalSamples);
        List<Double> values = new ArrayList<>(totalSamples);
        
        for (int stage = 0; stage < stages; stage++) {
            for (int sample = 0; sample < samplesPerStage; sample++) {
                double t = (stage * samplesPerStage + sample) / (double) totalSamples;
                
                // Calculate baseline based on disease stage
                double stageProgress = stage / (double) (stages - 1);
                
                // Biomarker progression follows sigmoidal pattern
                double baseline;
                
                if ("amyloid_beta".equals(biomarkerName)) {
                    // Amyloid increases early
                    baseline = 100 + 900 / (1 + Math.exp(-10 * (stageProgress - 0.3)));
                } else if ("tau".equals(biomarkerName)) {
                    // Tau increases after amyloid
                    baseline = 50 + 450 / (1 + Math.exp(-10 * (stageProgress - 0.5)));
                } else if ("neurodegeneration".equals(biomarkerName)) {
                    // Neurodegeneration follows tau
                    baseline = 10 + 90 / (1 + Math.exp(-10 * (stageProgress - 0.6)));
                } else if ("cognitive_score".equals(biomarkerName)) {
                    // Cognitive scores decrease (higher is better)
                    baseline = 100 - 80 / (1 + Math.exp(-10 * (stageProgress - 0.7)));
                } else {
                    // Default progression
                    baseline = 100 * stageProgress;
                }
                
                // Add variability
                double value = baseline + variability * random.nextGaussian();
                
                timestamps.add(t * 100); // Scale to more intuitive time range
                values.add(value);
            }
        }
        
        // Add to component
        addTimePoints(biomarkerName, timestamps, values);
        
        return values;
    }
    
    /**
     * Simulates the effect of an intervention on biomarker progression.
     *
     * @param baselineName The baseline biomarker series name
     * @param treatmentName The name for the treated series
     * @param interventionTime The time at which intervention occurs
     * @param effectStrength The strength of the intervention effect (0.0 to 1.0)
     * @param effectDelay The delay before the effect becomes apparent
     * @return The biomarker values with intervention effect
     */
    public List<Double> simulateIntervention(String baselineName, String treatmentName,
                                            double interventionTime, double effectStrength,
                                            double effectDelay) {
        List<TimePoint> baselineSeries = getTimeSeries(baselineName);
        List<Double> timestamps = new ArrayList<>(baselineSeries.size());
        List<Double> values = new ArrayList<>(baselineSeries.size());
        
        for (TimePoint point : baselineSeries) {
            double t = point.getTimestamp();
            double originalValue = point.getValue();
            
            double newValue;
            if (t < interventionTime) {
                // Before intervention, values are the same
                newValue = originalValue;
            } else {
                // After intervention, calculate intervention effect
                double timeAfterIntervention = t - interventionTime;
                double effectFactor = 0.0;
                
                if (timeAfterIntervention > effectDelay) {
                    // Gradual effect using sigmoid function
                    effectFactor = effectStrength / (1 + Math.exp(-0.5 * (timeAfterIntervention - effectDelay)));
                }
                
                // Apply effect (assuming lower values are better for disease biomarkers)
                newValue = originalValue * (1 - effectFactor);
            }
            
            timestamps.add(t);
            values.add(newValue);
        }
        
        // Add to component
        addTimePoints(treatmentName, timestamps, values);
        
        return values;
    }
    
    /**
     * Validates the component's configuration.
     *
     * @return A map of validation errors (empty if valid)
     */
    @Override
    public Map<String, String> validateConfiguration() {
        Map<String, String> errors = new HashMap<>();
        
        // Check required configuration
        if (!configuration.containsKey("window_size")) {
            errors.put("window_size", "Missing required configuration");
        }
        
        if (!configuration.containsKey("seasonality_periods")) {
            errors.put("seasonality_periods", "Missing required configuration");
        }
        
        return errors;
    }
    
    /**
     * Initializes the component.
     */
    @Override
    public void initialize() {
        Map<String, String> validationErrors = validateConfiguration();
        if (!validationErrors.isEmpty()) {
            setMetadata("initialization_errors", String.join(", ", validationErrors.values()));
            setState("ERROR");
            return;
        }
        
        setState("READY");
    }
    
    /**
     * Clears all time series data.
     */
    public void clearData() {
        timeSeries.clear();
        decompositions.clear();
        forecasts.clear();
        anomalyIndices.clear();
    }
    
    /**
     * Destroys the component, releasing any resources.
     */
    @Override
    public void destroy() {
        clearData();
        setState("DESTROYED");
    }
}