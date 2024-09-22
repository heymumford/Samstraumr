package org.samstraumr.sisters;

import org.samstraumr.core.Yggdrasil;
import org.samstraumr.core.TubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Andromeda class validates incoming data.
 *
 * @param <T> The type of data that Andromeda processes.
 */
public class Andromeda<T> extends Yggdrasil<T> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Andromeda.class);
    private volatile boolean isRunning = true;

    /**
     * Default constructor.
     */
    public Andromeda() {
        super();
    }

    /**
     * Processes data from the input queue, validates it, and places valid data into the output queue.
     */
    @Override
    public void run() {
        logger.info("Andromeda started.");
        while (isRunning) {
            try {
                T data = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (data != null) {
                    logger.debug("Polled data: {}", data);
                    if (validate(data)) {
                        outputQueue.offer(data);
                        logger.debug("Valid data passed: {}", data);
                    } else {
                        logger.warn("Invalid data detected: {}", data);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Andromeda interrupted.");
                break;
            }
        }
        logger.info("Andromeda stopped.");
    }

    /**
     * Validates the incoming data.
     *
     * @param data The data to validate.
     * @return True if data is valid; false otherwise.
     */
    private boolean validate(T data) {
        // Implement validation logic here
        return data != null; // Simple validation example
    }

    /**
     * Stops the Andromeda process.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Configures Andromeda with the provided settings.
     *
     * @param config The configuration object containing settings.
     */
    @Override
    public void configure(TubeConfig config) {
        // Implement configuration logic
        logger.info("Andromeda configured with settings: {}", config);
    }
}
