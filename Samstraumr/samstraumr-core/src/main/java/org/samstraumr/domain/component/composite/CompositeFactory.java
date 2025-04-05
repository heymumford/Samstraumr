/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Domain factory for composite components in the S8r framework
 */

package org.samstraumr.domain.component.composite;

import org.samstraumr.domain.identity.ComponentId;

/**
 * Factory for creating various types of composite components.
 *
 * <p>This factory provides methods for creating different types of composite components with
 * appropriate configurations. It follows Clean Architecture principles with no dependencies on
 * infrastructure or frameworks.
 */
public class CompositeFactory {

  /**
   * Creates a standard composite component.
   *
   * @param reason The reason for creation
   * @return A new standard composite component
   */
  public static CompositeComponent createStandardComposite(String reason) {
    ComponentId id = ComponentId.create(reason);
    return CompositeComponent.create(id, CompositeType.STANDARD);
  }

  /**
   * Creates a pipeline composite for sequential data processing.
   *
   * @param reason The reason for creation
   * @return A new pipeline composite component
   */
  public static CompositeComponent createPipeline(String reason) {
    ComponentId id = ComponentId.create("Pipeline: " + reason);
    return CompositeComponent.create(id, CompositeType.PIPELINE);
  }

  /**
   * Creates an observer composite that reacts to changes.
   *
   * @param reason The reason for creation
   * @return A new observer composite component
   */
  public static CompositeComponent createObserver(String reason) {
    ComponentId id = ComponentId.create("Observer: " + reason);
    return CompositeComponent.create(id, CompositeType.OBSERVER);
  }

  /**
   * Creates a transformer composite for data transformation.
   *
   * @param reason The reason for creation
   * @return A new transformer composite component
   */
  public static CompositeComponent createTransformer(String reason) {
    ComponentId id = ComponentId.create("Transformer: " + reason);
    return CompositeComponent.create(id, CompositeType.TRANSFORMER);
  }

  /**
   * Creates a validator composite for data validation.
   *
   * @param reason The reason for creation
   * @return A new validator composite component
   */
  public static CompositeComponent createValidator(String reason) {
    ComponentId id = ComponentId.create("Validator: " + reason);
    return CompositeComponent.create(id, CompositeType.VALIDATOR);
  }

  /**
   * Creates a circuit breaker composite for fault tolerance.
   *
   * @param reason The reason for creation
   * @return A new circuit breaker composite component
   */
  public static CompositeComponent createCircuitBreaker(String reason) {
    ComponentId id = ComponentId.create("CircuitBreaker: " + reason);
    return CompositeComponent.create(id, CompositeType.CIRCUIT_BREAKER);
  }

  /**
   * Creates a mediator composite for coordinating component interactions.
   *
   * @param reason The reason for creation
   * @return A new mediator composite component
   */
  public static CompositeComponent createMediator(String reason) {
    ComponentId id = ComponentId.create("Mediator: " + reason);
    return CompositeComponent.create(id, CompositeType.MEDIATOR);
  }

  /**
   * Creates an adapter composite for interface adaptation.
   *
   * @param reason The reason for creation
   * @return A new adapter composite component
   */
  public static CompositeComponent createAdapter(String reason) {
    ComponentId id = ComponentId.create("Adapter: " + reason);
    return CompositeComponent.create(id, CompositeType.ADAPTER);
  }

  /**
   * Creates a composite of the specified type.
   *
   * @param type The type of composite to create
   * @param reason The reason for creation
   * @return A new composite component of the specified type
   */
  public static CompositeComponent createComposite(CompositeType type, String reason) {
    ComponentId id = ComponentId.create(type.name() + ": " + reason);
    return CompositeComponent.create(id, type);
  }
}
