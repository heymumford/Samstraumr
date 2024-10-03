package org.samstraumr.core;

import java.util.ArrayList;
import java.util.List;

public class TubeOperations {
    private static final double MISALIGNMENT_THRESHOLD = 0.2;
    private static final double PERFORMANCE_THRESHOLD = 0.8;

    private final TubeLogger tubeLogger;
    private final List<OperationRecord> operationHistory;
    private boolean paused;
    private boolean terminated;

    public TubeOperations(TubeLogger tubeLogger) {
        this.tubeLogger = tubeLogger;
        this.operationHistory = new ArrayList<>();
    }

    public void detectMisalignment() {
        tubeLogger.log("debug", "Detecting misalignment", "performance");
        double failureRate = getFailureRate();
        if (failureRate > MISALIGNMENT_THRESHOLD) {
            tubeLogger.log("warn", "Misalignment detected. Failure rate: " + failureRate, "performance", "misalignment");
        } else {
            tubeLogger.log("info", "No misalignment detected. Failure rate: " + failureRate, "performance");
        }
    }

    private double getFailureRate() {
        if (operationHistory.isEmpty()) {
            return 0;
        }
        long failedOperations = operationHistory.stream()
                .filter(record -> !record.isSuccess())
                .count();
        return (double) failedOperations / operationHistory.size();
    }

    public void recordOperation(String operationName, boolean success) {
        this.operationHistory.add(new OperationRecord(operationName, success));
    }

    public void analyzePerformance() {
        double successRate = 1 - getFailureRate();
        if (successRate < PERFORMANCE_THRESHOLD) {
            tubeLogger.log("warn", "Performance below threshold. Considering purpose evolution.", "performance");
        }
    }

    public void detectDangerousConditions() {
        // Implementation depends on specific conditions defined for the tube
        // This method might need to interact with TubeWorkload
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }
}
