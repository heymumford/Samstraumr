package org.samstraumr.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Yggdrasil class serves as the foundational abstract class for all tube components.
 * It manages the processing of data and provides logging capabilities using SLF4J.
 *
 * @param <T> The type of data that flows through the tube.
 */
public abstract class Yggdrasil<T> {

    protected BlockingQueue<T> inputQueue;
    protected BlockingQueue<T> outputQueue;

    private static final Logger logger = LoggerFactory.getLogger(Yggdrasil.class);

    /**
     * Default constructor initializes the input and output queues.
     */
    public Yggdrasil() {
        this.inputQueue = new LinkedBlockingQueue<>();
        this.outputQueue = new LinkedBlockingQueue<>();
        logger.debug("Yggdrasil instance created with new input and output queues.");
    }

    /**
     * Processes data from the input queue and places results into the output queue.
     * This method should be implemented by subclasses to define specific processing logic.
     */
    public abstract void process();

    /**
     * Starts the tube's operation.
     */
    public void start() {
        logger.info("{} started.", this.getClass().getSimpleName());
        // Could be implemented with threading if needed
        process();
    }

    /**
     * Gets the input queue.
     *
     * @return The input queue.
     */
    public BlockingQueue<T> getInputQueue() {
        return inputQueue;
    }

    /**
     * Sets the input queue.
     *
     * @param inputQueue The input queue to set.
     */
    public void setInputQueue(BlockingQueue<T> inputQueue) {
        this.inputQueue = inputQueue;
        logger.debug("Input queue set in {}.", this.getClass().getSimpleName());
    }

    /**
     * Gets the output queue.
     *
     * @return The output queue.
     */
    public BlockingQueue<T> getOutputQueue() {
        return outputQueue;
    }

    /**
     * Sets the output queue.
     *
     * @param outputQueue The output queue to set.
     */
    public void setOutputQueue(BlockingQueue<T> outputQueue) {
        this.outputQueue = outputQueue;
        logger.debug("Output queue set in {}.", this.getClass().getSimpleName());
    }
}
