package org.samstraumr.cli;

import org.samstraumr.core.YggdrasilCoordinator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * The SamstraumrCLI class provides a command-line interface for interacting with the Samstraumr framework.
 */
public class SamstraumrCLI {

    private static final Logger logger = LoggerFactory.getLogger(SamstraumrCLI.class);

    public static void main(String[] args) {
        YggdrasilCoordinator yggdrasil = new YggdrasilCoordinator();
        yggdrasil.start();

        logger.info("Welcome to the Samstraumr CLI.");
        logger.info("Type 'exit' to quit.");

        Scanner scanner = new Scanner(System.in);
        String input;

        // Add a loop to accept user input
        while (true) {
            System.out.print("samstraumr> ");
            input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input)) {
                break;
            }

            // Process the input
            processInput(input, yggdrasil);
        }

        // Clean up
        yggdrasil.stop();
        scanner.close();
        logger.info("Samstraumr CLI exited.");
    }

    /**
     * Processes the user input and interacts with the YggdrasilCoordinator.
     *
     * @param input      The user input.
     * @param yggdrasil  The YggdrasilCoordinator instance.
     */
    private static void processInput(String input, YggdrasilCoordinator yggdrasil) {
        // For simplicity, we'll add the input directly to Andromeda's input queue
        yggdrasil.getInputQueue().offer(input);
        logger.info("Input '{}' added to the system.", input);
    }
}

