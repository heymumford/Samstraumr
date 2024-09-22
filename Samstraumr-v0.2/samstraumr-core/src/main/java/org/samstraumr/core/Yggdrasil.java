package org.samstraumr.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Yggdrasil<T> {

    protected BlockingQueue<T> inputQueue;
    protected BlockingQueue<T> outputQueue;

    public Yggdrasil() {
        this.inputQueue = new LinkedBlockingQueue<>();
        this.outputQueue = new LinkedBlockingQueue<>();
    }

    // Method to process data
    public abstract void process();

    // Method to start the tube's operation
    public void start() {
        // Could be implemented with threading if needed
        process();
    }

    // Getter and Setter for Input Queue
    public BlockingQueue<T> getInputQueue() {
        return inputQueue;
    }

    public void setInputQueue(BlockingQueue<T> inputQueue) {
        this.inputQueue = inputQueue;
    }

    // Getter and Setter for Output Queue
    public BlockingQueue<T> getOutputQueue() {
        return outputQueue;
    }

    public void setOutputQueue(BlockingQueue<T> outputQueue) {
        this.outputQueue = outputQueue;
    }
}
