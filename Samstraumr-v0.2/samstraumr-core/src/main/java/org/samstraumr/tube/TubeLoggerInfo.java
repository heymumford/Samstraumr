package org.samstraumr.tube;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class TubeLoggerInfo {
    private final String tubeId;
    private final String compositeId;
    private final String machineId;
    private final AtomicLong sequenceNumber;
    private final String visualHash;

    public TubeLoggerInfo(String tubeId, String compositeId, String machineId) {
        this.tubeId = tubeId;
        this.compositeId = compositeId;
        this.machineId = machineId;
        this.sequenceNumber = new AtomicLong(0);
        this.visualHash = generateVisualHash(tubeId);
    }

    public Map<String, Object> assembleLogInfo(String message, String level, String[] tags, Map<String, Object> additionalContext) {
        Map<String, Object> logInfo = new HashMap<>();
        logInfo.put("tubeId", tubeId);
        logInfo.put("compositeId", compositeId);
        logInfo.put("machineId", machineId);
        logInfo.put("message", message);
        logInfo.put("level", level);
        logInfo.put("sequenceNumber", sequenceNumber.getAndIncrement());
        logInfo.put("tags", tags);
        logInfo.put("visualHash", visualHash);
        logInfo.put("timestamp", System.currentTimeMillis());

        if (additionalContext != null) {
            logInfo.putAll(additionalContext);
        }

        return logInfo;
    }

    private String generateVisualHash(String id) {
        int hash = id.hashCode();
        Color color = new Color(Math.abs(hash) % 255, Math.abs(hash >> 8) % 255, Math.abs(hash >> 16) % 255);
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public String getTubeId() {
        return tubeId;
    }

    public String getCompositeId() {
        return compositeId;
    }

    public String getMachineId() {
        return machineId;
    }

    public long getNextSequenceNumber() {
        return sequenceNumber.get();
    }

    public String getVisualHash() {
        return visualHash;
    }
}
