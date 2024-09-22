package org.samstraumr.sisters;

import org.samstraumr.core.Yggdrasil;
import org.samstraumr.core.TubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Phoebe recalls past interactions and insights, providing context to the system's operations.
 *
 * @param <T> The type of data that Phoebe processes.
 */
public class Phoebe<T> extends Yggdrasil<T> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Phoebe.class);
    private volatile boolean isRunning = true;
    private List<T> memory;

    /**
     * Default constructor.
     */
    public Phoebe() {
        super();
        this.memory = new ArrayList<>();
    }

    /**
     * Processes data from the input queue, stores it in memory, and places it into the output queue.
     */
    @Override
    public void run() {
        logger.info("Phoebe started.");
        while (isRunning) {
            try {
                T data = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (data != null) {
                    logger.debug("Received data: {}", data);
                    remember(data);
                    outputQueue.offer(data);
                    logger.debug("Data stored in memory and forwarded: {}", data);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Phoebe interrupted.");
                break;
            }
        }
        logger.info("Phoebe stopped.");
    }

    /**
     * Stores data in memory for future recall.
     *
     * @param data The data to remember.
     */
    private void remember(T data) {
        memory.add(data);
    }

    /**
     * Retrieves the stored memory.
     *
     * @return The list of stored data.
     */
    public List<T> recall() {
        return new ArrayList<>(memory);
    }

    /**
     * Stops the Phoebe process.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Configures Phoebe with the provided settings.
     *
     * @param config The configuration object containing settings.
     */
    @Override
    public void configure(TubeConfig config) {
        // Implement configuration logic
        logger.info("Phoebe configured with settings: {}", config);
    }
}
