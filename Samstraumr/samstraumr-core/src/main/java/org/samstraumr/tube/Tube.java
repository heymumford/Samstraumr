package org.samstraumr.tube;

import java.time.Instant;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import org.samstraumr.tube.util.SystemInfoUtility;
import org.samstraumr.tube.core.BirthCertificate;
import org.samstraumr.tube.core.VitalStats;
import org.samstraumr.tube.core.HealthAssessment;
import org.samstraumr.tube.core.MetricData;
import org.samstraumr.tube.api.TubeMonitor;
import org.samstraumr.tube.api.TubeProcessor;
import org.samstraumr.tube.api.TubeResourceManager;
import org.samstraumr.tube.api.TubeState;
import org.samstraumr.tube.logging.TubeLogger;
import org.samstraumr.tube.util.TubeValidator;

public class Tube {
    // Immutable core identity fields
    private final String uuid;
    private final Instant birthTime;
    private final JsonNode systemInfo;
    private final BirthCertificate birthCertificate;

    // VitalStats object that tracks resource usage
    private VitalStats vitalStats;
    private HealthAssessment healthStatus;
    private boolean monitoringActive;

    // Feedback mechanism state
    private double adaptationRate;

    // Externalized components
    private final TubeMonitor tubeMonitor;
    private final TubeProcessor tubeProcessor;
    private final TubeResourceManager tubeResourceManager;
    private final TubeState tubeState;

    // Constructor
    public Tube(String purpose) {
        this.uuid = UUID.randomUUID().toString();
        this.birthTime = Instant.now();
        this.systemInfo = SystemInfoUtility.getSystemInfo();
        this.birthCertificate = new BirthCertificate(uuid, birthTime, purpose);

        // Initialize specialized components
        this.tubeMonitor = new TubeMonitor(this);
        this.tubeProcessor = new TubeProcessor(this);
        this.tubeResourceManager = new TubeResourceManager(this);
        this.tubeState = new TubeState();

        initializeEnvironment();
        TubeLogger.info("Tube created with identity: " + getIdentity());
    }

    // Initialize vital stats and monitoring status
    private void initializeEnvironment() {
        this.vitalStats = new VitalStats();
        this.healthStatus = new HealthAssessment();
        this.monitoringActive = true;
        tubeResourceManager.initializeResources();
        this.adaptationRate = 1.0; // Default adaptation rate for feedback loop
    }

    // Get identity information for the Tube
    public String getIdentity() {
        return String.format("UUID: %s, BirthTime: %s, SystemInfo: %s", uuid, birthTime, systemInfo.toString());
    }

    // Update vital stats with current memory and CPU usage
    public void updateResourceUsage() {
        long memoryUsage = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        int cpuCores = Runtime.getRuntime().availableProcessors();
        vitalStats.updateStats(memoryUsage, cpuCores);
    }

    // Getters for vital stats
    public VitalStats getVitalStats() {
        return this.vitalStats;
    }

    // Check if monitoring is active
    public boolean isMonitoringActive() {
        return monitoringActive;
    }

    // Feedback loop simulation
    public void adaptToFeedback(double newAdaptationRate) {
        this.adaptationRate = newAdaptationRate;
        tubeMonitor.adaptToFeedback(newAdaptationRate);
        TubeLogger.info(String.format("Adaptation rate updated to: %.2f", newAdaptationRate));
    }

    // Method to perform a self-check and return vital stats
    public VitalStats checkVitalStats() {
        updateResourceUsage(); // Ensure stats are current
        TubeValidator.areVitalStatsNormal(vitalStats); // Pass vitalStats directly
        return vitalStats;
    }

    // Method to analyze resource usage patterns
    public void analyzeResources() {
        tubeResourceManager.analyzeResources();
    }

    // Health check
    public HealthAssessment checkHealth() {
        HealthAssessment assessment = tubeMonitor.checkHealth();
        if (!assessment.isHealthy()) {
            TubeLogger.warn("Health check indicates issues with Tube: " + getIdentity());
        }
        return assessment;
    }

    // Get birth certificate
    public BirthCertificate getBirthCertificate() {
        return birthCertificate;
    }

    // Transition state
    public void transitionState(TubeState.State newState) {
        if (TubeValidator.isValidStateTransition(this, tubeState.getCurrentState().toString(), newState.toString())) {
            tubeState.setState(newState);
            TubeLogger.info(String.format("State transitioned to: %s", newState));
        } else {
            TubeLogger.warn(String.format("Invalid state transition attempted from %s to %s", tubeState.getCurrentState(), newState));
        }
    }

    // Method to check if tube is fully initialized
    public boolean isInitialized() {
        return monitoringActive && vitalStats != null && healthStatus != null;
    }

    // Placeholder for rolling metrics, can be an empty map for now
    public Map<String, MetricData> getRollingMetrics() {
        return new HashMap<>(); // Placeholder until more functionality is added
    }
}
