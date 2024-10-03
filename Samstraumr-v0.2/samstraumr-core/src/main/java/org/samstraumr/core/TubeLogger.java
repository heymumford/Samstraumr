package org.samstraumr.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import java.util.Map;

public class TubeLogger {
    private static final Logger logger = LogManager.getLogger(TubeLogger.class);
    private final TubeLoggerInfo loggerInfo;

    public TubeLogger(String tubeId, String compositeId, String machineId) {
        this.loggerInfo = new TubeLoggerInfo(tubeId, compositeId, machineId);
    }

    public void log(String level, String message, String... tags) {
        logWithContext(level, message, null, tags);
    }

    public void logWithContext(String level, String message, Map<String, Object> context, String... tags) {
        Map<String, Object> logEntry = loggerInfo.assembleLogInfo(message, level, tags, context);
        ObjectMessage objectMessage = new ObjectMessage(logEntry);

        switch (level.toLowerCase()) {
            case "debug":
                logger.debug(objectMessage);
                break;
            case "info":
                logger.info(objectMessage);
                break;
            case "warn":
                logger.warn(objectMessage);
                break;
            case "error":
                logger.error(objectMessage);
                break;
            default:
                logger.info(objectMessage);
        }
    }
}
