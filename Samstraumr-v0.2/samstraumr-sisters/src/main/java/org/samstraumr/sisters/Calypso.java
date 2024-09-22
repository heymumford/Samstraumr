package org.samstraumr.sisters;

import org.samstraumr.core.Yggdrasil;
import org.samstraumr.core.TubeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Calypso facilitates communication between tubes, routing messages appropriately.
 *
 * @param <T> The type of data that Calypso processes.
 */
public class Calypso<T> extends Yggdrasil<T> implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Calypso.class);
    private volatile boolean isRunning = true;

    /**
     * Default constructor.
     */
    public Calypso() {
        super();
    }

    /**
     * Processes data from the input queue and routes it to the appropriate destination.
     */
    @Override
    public void run() {
        logger.info("Calypso started.");
        while (isRunning) {
            try {
                T message = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (message != null) {
                    logger.debug("Received message: {}", message);
                    routeMessage(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Calypso interrupted.");
                break;
            }
        }
        logger.info("Calypso stopped.");
    }

    /**
     * Routes the message to the appropriate tube.
     *
     * @param message The message to route.
     */
    private void routeMessage(T message) {
        // Implement routing logic here
        logger.debug("Routing message: {}", message);
    }

    /**
     * Stops the Calypso process.
     */
    public void stop() {
        isRunning = false;
    }

    /**
     * Configures Calypso with the provided settings.
     *
     * @param config The configuration object containing settings.
     */
    @Override
    public void configure(TubeConfig config) {
        // Implement configuration logic
        logger.info("Calypso configured with settings: {}", config);
    }
}
