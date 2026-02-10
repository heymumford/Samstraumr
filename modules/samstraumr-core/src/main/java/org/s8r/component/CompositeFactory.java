/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.component;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating and managing composites of various patterns.
 *
 * <p>This factory class provides methods for creating standard composite structures and maintains a
 * registry of all created composites. The factory offers predefined patterns such as
 * transformation, validation, processing, and observer composites.
 *
 * <p>The composites created by this factory follow common architectural patterns used in data
 * processing pipelines, making it easy to implement standard flows without having to manually wire
 * components together.
 */
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
   * Creates a linear transformation composite with source, transformer, and sink components.
   *
   * @param environment The environment for the composite
   * @return The created transformation composite
   */
  public static Composite createTransformationComposite(Environment environment) {
    Composite composite = createComposite("transformation-" + generateCompositeId(), environment);

    // Add components
    composite
        .createComponent("source", "Source Component")
        .createComponent("transformer", "Transformer Component")
        .createComponent("sink", "Sink Component");

    // Connect components
    composite.connect("source", "transformer").connect("transformer", "sink");

    LOGGER.info("Created transformation composite: {}", composite.getCompositeId());
    return composite;
  }

  /**
   * Creates a validation composite with processor, validator, and output components.
   *
   * @param environment The environment for the composite
   * @return The created validation composite
   */
  public static Composite createValidationComposite(Environment environment) {
    Composite composite = createComposite("validation-" + generateCompositeId(), environment);

    // Add components
    composite
        .createComponent("processor", "Processor Component")
        .createComponent("validator", "Validator Component")
        .createComponent("output", "Output Component");

    // Connect components
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

    // Add components
    composite
        .createComponent("input", "Input Component")
        .createComponent("parser", "Parser Component")
        .createComponent("validator", "Validator Component")
        .createComponent("processor", "Processor Component")
        .createComponent("formatter", "Formatter Component")
        .createComponent("output", "Output Component");

    // Connect components in sequence
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
   * Creates an observer composite with source and observer components.
   *
   * @param environment The environment for the composite
   * @return The created observer composite
   */
  public static Composite createObserverComposite(Environment environment) {
    Composite composite = createComposite("observer-" + generateCompositeId(), environment);

    // Add components
    composite
        .createComponent("source", "Source Component")
        .createComponent("observer", "Observer Component")
        .createComponent("output", "Output Component");

    // Connect components - observer is connected to source to monitor signals
    composite.connect("source", "observer").connect("observer", "output");

    // Configure observer component to just pass through data (monitoring only)
    composite.addTransformer(
        "observer",
        data -> {
          // Observer pattern just logs the data without modifying it
          LOGGER.info("Observer component observed: {}", data);
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
