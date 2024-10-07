package org.samstraumr.tube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

public class TubeLogger {
    private static final Logger logger = LoggerFactory.getLogger(TubeLogger.class);
    private final TubeLoggerInfo loggerInfo;

    public TubeLogger(String tubeId, String compositeId, String machineId) {
        this.loggerInfo = new TubeLoggerInfo(tubeId, compositeId, machineId);
    }

    public void log(String level, String message, String... tags) {
        logWithContext(level, message, null, tags);
    }

    public void logWithContext(String level, String message, Map<String, Object> context, String... tags) {
        Map<String, Object> logEntry = loggerInfo.assembleLogInfo(message, level, tags, context);

        switch (level.toLowerCase()) {
            case "debug":
                logger.debug("{}", logEntry);
                break;
            case "info":
                logger.info("{}", logEntry);
                break;
            case "warn":
                logger.warn("{}", logEntry);
                break;
            case "error":
                logger.error("{}", logEntry);
                break;
            default:
                logger.info("{}", logEntry);
        }
    }
}