/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.tube.composite;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.s8r.tube.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositeFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(CompositeFactory.class);
  private static final Map<String, Composite> COMPOSITE_REGISTRY = new ConcurrentHashMap<>();

  private CompositeFactory() {
    // Private constructor to prevent instantiation
  }

  /**
   * Creates a new Composite with a generated ID.
   *
   * @param environment The environment for the composite
   * @return The created composite
   */
  public static Composite createComposite(Environment environment) {
    String compositeId = generateCompositeId();
    return createComposite(compositeId, environment);
  }

  /**
   * Creates a new Composite with the specified ID.
   *
   * @param compositeId The ID for the composite
   * @param environment The environment for the composite
   * @return The created composite
   */
  public static Composite createComposite(String compositeId, Environment environment) {
    Composite composite = new Composite(compositeId, environment);
    COMPOSITE_REGISTRY.put(compositeId, composite);
    LOGGER.info("Created and registered composite: {}", compositeId);
    return composite;
  }

  /**
   * Creates a linear transformation composite with source, transformer, and sink tubes.
   *
   * @param environment The environment for the composite
   * @return The created transformation composite
   */
  public static Composite createTransformationComposite(Environment environment) {
    Composite composite = createComposite("transformation-" + generateCompositeId(), environment);

    // Add tubes
    composite
        .createTube("source", "Source Tube")
        .createTube("transformer", "Transformer Tube")
        .createTube("sink", "Sink Tube");

    // Connect tubes
    composite.connect("source", "transformer").connect("transformer", "sink");

    LOGGER.info("Created transformation composite: {}", composite.getCompositeId());
    return composite;
  }

  /**
   * Creates a validation composite with processor, validator, and output tubes.
   *
   * @param environment The environment for the composite
   * @return The created validation composite
   */
  public static Composite createValidationComposite(Environment environment) {
    Composite composite = createComposite("validation-" + generateCompositeId(), environment);

    // Add tubes
    composite
        .createTube("processor", "Processor Tube")
        .createTube("validator", "Validator Tube")
        .createTube("output", "Output Tube");

    // Connect tubes
    composite.connect("processor", "validator").connect("validator", "output");

    // Enable circuit breaker on validator
    composite.enableCircuitBreaker("validator", 3, 5000);

    LOGGER.info("Created validation composite: {}", composite.getCompositeId());
    return composite;
  }

  /**
   * Creates a standard processing composite with input, processing stages, and output.
   *
   * @param environment The environment for the composite
   * @return The created processing composite
   */
  public static Composite createProcessingComposite(Environment environment) {
    Composite composite = createComposite("processing-" + generateCompositeId(), environment);

    // Add tubes
    composite
        .createTube("input", "Input Tube")
        .createTube("parser", "Parser Tube")
        .createTube("validator", "Validator Tube")
        .createTube("processor", "Processor Tube")
        .createTube("formatter", "Formatter Tube")
        .createTube("output", "Output Tube");

    // Connect tubes in sequence
    composite
        .connect("input", "parser")
        .connect("parser", "validator")
        .connect("validator", "processor")
        .connect("processor", "formatter")
        .connect("formatter", "output");

    // Enable circuit breakers
    composite.enableCircuitBreaker("parser", 2, 10000).enableCircuitBreaker("processor", 3, 15000);

    LOGGER.info("Created standard processing composite: {}", composite.getCompositeId());
    return composite;
  }

  /**
   * Creates an observer composite with source and observer tubes.
   *
   * @param environment The environment for the composite
   * @return The created observer composite
   */
  public static Composite createObserverComposite(Environment environment) {
    Composite composite = createComposite("observer-" + generateCompositeId(), environment);

    // Add tubes
    composite
        .createTube("source", "Source Tube")
        .createTube("observer", "Observer Tube")
        .createTube("output", "Output Tube");

    // Connect tubes - observer is connected to source to monitor signals
    composite.connect("source", "observer").connect("observer", "output");

    // Configure observer tube to just pass through data (monitoring only)
    composite.addTransformer(
        "observer",
        data -> {
          // Observer pattern just logs the data without modifying it
          LOGGER.info("Observer tube observed: {}", data);
          // Add to the composite event log for test verification
          composite.logEvent("Observer observed data: " + data);
          return data;
        });

    LOGGER.info("Created observer composite: {}", composite.getCompositeId());
    return composite;
  }

  /**
   * Gets a composite by its ID.
   *
   * @param compositeId The ID of the composite to retrieve
   * @return Optional containing the composite if found, empty otherwise
   */
  public static Optional<Composite> getComposite(String compositeId) {
    return Optional.ofNullable(COMPOSITE_REGISTRY.get(compositeId));
  }

  /**
   * Gets all registered composites.
   *
   * @return An unmodifiable map of composite IDs to composites
   */
  public static Map<String, Composite> getAllComposites() {
    return Collections.unmodifiableMap(COMPOSITE_REGISTRY);
  }

  /**
   * Removes a composite from the registry.
   *
   * @param compositeId The ID of the composite to remove
   * @return true if the composite was removed, false if it wasn't found
   */
  public static boolean removeComposite(String compositeId) {
    Composite composite = COMPOSITE_REGISTRY.remove(compositeId);
    if (composite != null) {
      composite.deactivate();
      LOGGER.info("Removed composite from registry: {}", compositeId);
      return true;
    }
    return false;
  }

  /**
   * Generates a unique composite ID.
   *
   * @return A unique composite ID
   */
  private static String generateCompositeId() {
    return UUID.randomUUID().toString().substring(0, 8);
  }
}
