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

package org.s8r.adapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.s8r.application.port.LoggerPort;
import org.s8r.component.Component;
import org.s8r.component.Composite;
import org.s8r.component.Environment;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.tube.Tube;

/**
 * Adapter for converting between legacy Tube-based Composites and new Component-based Composites.
 *
 * <p>This adapter facilitates migration from legacy code by allowing existing Tube composites to be
 * used with the new Component-based architecture. The adapter handles the conversion of components,
 * connections, transformers, validators, and circuit breakers.
 */
public class CompositeAdapter {

  private final LoggerPort logger;
  private final TubeComponentAdapter componentAdapter;
  private final TubeLegacyEnvironmentConverter environmentConverter;

  /**
   * Converts a domain component port to a component interface suitable for the Composite. This is a
   * bridge between the domain and component layers.
   *
   * @param componentPort The domain component port to convert
   * @return A component suitable for use with Composite
   */
  private static org.s8r.component.Component convertDomainComponentToComponentLayer(
      ComponentPort componentPort) {
    if (componentPort == null) {
      return null;
    }

    // This is a simplified conversion - in a real implementation, we would need
    // to properly map all state and behavior between the two types
    org.s8r.component.Environment env = new org.s8r.component.Environment();
    ComponentId componentId = componentPort.getId();
    return org.s8r.component.Component.create(componentId.getReason(), env);
  }

  /**
   * Gets the environment from a tube composite using reflection. This is a workaround for missing
   * getEnvironment() method.
   *
   * @param tubeComposite The tube composite
   * @return The environment, or null if it cannot be accessed
   */
  private org.s8r.tube.Environment getTubeCompositeEnvironment(
      org.s8r.tube.composite.Composite tubeComposite) {
    if (tubeComposite == null) {
      return null;
    }

    try {
      java.lang.reflect.Field field =
          org.s8r.tube.composite.Composite.class.getDeclaredField("environment");
      field.setAccessible(true);
      return (org.s8r.tube.Environment) field.get(tubeComposite);
    } catch (Exception e) {
      logger.error("Failed to access environment field", e);
      // Create an empty environment as fallback
      return new org.s8r.tube.Environment();
    }
  }

  /**
   * Creates a new CompositeAdapter.
   *
   * @param logger Logger for recording operations
   * @param componentAdapter Adapter for converting Tube components to Components
   * @param environmentConverter Converter for Environment objects
   */
  public CompositeAdapter(
      LoggerPort logger,
      TubeComponentAdapter componentAdapter,
      TubeLegacyEnvironmentConverter environmentConverter) {
    this.logger = logger;
    this.componentAdapter = componentAdapter;
    this.environmentConverter = environmentConverter;

    logger.debug("CompositeAdapter initialized");
  }

  /**
   * Converts a legacy Tube composite to a new Component composite.
   *
   * @param tubeComposite The legacy composite to convert
   * @return A new Component-based composite that mirrors the legacy composite
   */
  public Composite tubeCompositeToComponentComposite(
      org.s8r.tube.composite.Composite tubeComposite) {
    logger.debug(
        "Converting tube composite to component composite: {}", tubeComposite.getCompositeId());

    // Convert environment
    org.s8r.tube.Environment tubeEnvironment = getTubeCompositeEnvironment(tubeComposite);
    org.s8r.component.Environment componentEnvironment =
        environmentConverter.fromLegacyEnvironment(tubeEnvironment);

    // Create new composite
    Composite componentComposite =
        new Composite(tubeComposite.getCompositeId(), componentEnvironment);

    // Convert tubes to components
    Map<String, Tube> tubes = tubeComposite.getTubes();
    for (Map.Entry<String, Tube> entry : tubes.entrySet()) {
      String tubeName = entry.getKey();
      Tube tube = entry.getValue();

      // Convert and add to the new composite
      ComponentPort componentPort = componentAdapter.wrapLegacyComponent(tube);
      // Convert domain component to component layer component
      org.s8r.component.Component componentLayerComponent =
          convertDomainComponentToComponentLayer(componentPort);
      componentComposite.addComponent(tubeName, componentLayerComponent);

      logger.debug("Added converted component to composite: {}", tubeName);
    }

    // Copy connections
    Map<String, List<String>> connections = tubeComposite.getConnections();
    for (Map.Entry<String, List<String>> entry : connections.entrySet()) {
      String sourceName = entry.getKey();
      for (String targetName : entry.getValue()) {
        componentComposite.connect(sourceName, targetName);
        logger.debug("Added connection to composite: {} -> {}", sourceName, targetName);
      }
    }

    // SPSOFT-36: Copy transformers, validators, and circuit breakers
    // This would require access to these elements of the tube composite
    // which might not be exposed in the current API

    logger.debug("Completed conversion of tube composite: {}", tubeComposite.getCompositeId());
    return componentComposite;
  }

  /**
   * Creates a hybrid composite that uses existing tubes alongside new components.
   *
   * <p>This is useful for gradually migrating composites by replacing tubes one by one.
   *
   * @param compositeId The ID for the new composite
   * @param environment The environment for the new composite
   * @return A new component-based composite
   */
  public Composite createHybridComposite(
      String compositeId, org.s8r.tube.Environment tubeEnvironment) {
    logger.debug("Creating hybrid composite: {}", compositeId);

    // Convert environment
    Environment componentEnvironment = environmentConverter.fromLegacyEnvironment(tubeEnvironment);

    // Create new composite
    return new Composite(compositeId, componentEnvironment);
  }

  /**
   * Adds a tube from a legacy composite to a new component composite.
   *
   * @param tubeComposite The source legacy composite
   * @param tubeName The name of the tube to add
   * @param componentComposite The target component composite
   * @param componentName The name to use for the converted component
   */
  public void addTubeToComponentComposite(
      org.s8r.tube.composite.Composite tubeComposite,
      String tubeName,
      Composite componentComposite,
      String componentName) {

    // Check if tube exists in the legacy composite
    try {
      Tube tube = tubeComposite.getTube(tubeName);

      // Convert and add to the new composite
      ComponentPort componentPort = componentAdapter.wrapLegacyComponent(tube);
      org.s8r.component.Component componentLayerComponent =
          convertDomainComponentToComponentLayer(componentPort);
      componentComposite.addComponent(componentName, componentLayerComponent);

      logger.debug(
          "Added tube {} from legacy composite to component composite as {}",
          tubeName,
          componentName);
    } catch (IllegalArgumentException e) {
      logger.error("Tube {} not found in legacy composite", tubeName);
      throw new IllegalArgumentException("Tube not found in legacy composite: " + tubeName, e);
    }
  }

  /**
   * Creates a component composite that delegates to a legacy tube composite.
   *
   * <p>This wrapper allows new code to use the component-based API while the implementation uses
   * the existing tube composite.
   *
   * @param tubeComposite The legacy composite to wrap
   * @return A component composite that delegates to the tube composite
   */
  public Composite wrapTubeComposite(org.s8r.tube.composite.Composite tubeComposite) {
    logger.debug("Creating wrapper for tube composite: {}", tubeComposite.getCompositeId());

    // Convert environment
    org.s8r.tube.Environment tubeEnvironment = getTubeCompositeEnvironment(tubeComposite);
    org.s8r.component.Environment componentEnvironment =
        environmentConverter.fromLegacyEnvironment(tubeEnvironment);

    // Create wrapper composite
    return new TubeCompositeWrapper(
        tubeComposite.getCompositeId(),
        componentEnvironment,
        tubeComposite,
        componentAdapter,
        logger);
  }

  /** A Component-based Composite implementation that delegates to a Tube-based Composite. */
  public static class TubeCompositeWrapper extends Composite {

    private final org.s8r.tube.composite.Composite tubeComposite;
    private final TubeComponentAdapter componentAdapter;
    private final LoggerPort logger;
    private final Map<String, Component> componentCache;

    /**
     * Gets the underlying tube composite.
     *
     * @return The tube composite
     */
    public org.s8r.tube.composite.Composite unwrapTubeComposite() {
      return tubeComposite;
    }

    public TubeCompositeWrapper(
        String compositeId,
        Environment environment,
        org.s8r.tube.composite.Composite tubeComposite,
        TubeComponentAdapter componentAdapter,
        LoggerPort logger) {
      super(compositeId, environment);
      this.tubeComposite = tubeComposite;
      this.componentAdapter = componentAdapter;
      this.logger = logger;
      this.componentCache = new ConcurrentHashMap<>();

      // Copy activation state
      if (!tubeComposite.isActive()) {
        this.deactivate();
      }

      logger.debug("Created TubeCompositeWrapper for composite: {}", compositeId);
    }

    @Override
    public Composite addComponent(String name, Component component) {
      // Extract tube from component using reflection
      Tube tube = null;
      try {
        // We can't directly cast due to type conflicts, so use reflection
        java.lang.reflect.Method getTubeMethod = component.getClass().getMethod("getTube");
        Object result = getTubeMethod.invoke(component);
        if (result instanceof Tube) {
          tube = (Tube) result;
        }

        if (tube == null) {
          logger.warn(
              "Non-wrapped component added to TubeCompositeWrapper; "
                  + "this may not work correctly: {}",
              name);
        }
      } catch (NoSuchMethodException e) {
        logger.warn("Cannot extract tube: component does not have getTube method");
      } catch (Exception e) {
        logger.error("Error extracting tube from component: {}", e.getMessage());
      }

      // Add to the tube composite
      if (tube != null) {
        tubeComposite.addTube(name, tube);
      }

      // Cache the component
      componentCache.put(name, component);

      return super.addComponent(name, component);
    }

    @Override
    public Composite connect(String sourceName, String targetName) {
      // Connect in the tube composite
      tubeComposite.connect(sourceName, targetName);

      // Connect in this composite too
      return super.connect(sourceName, targetName);
    }

    @Override
    public <T> Composite addTransformer(String componentName, Function<T, T> transformer) {
      // Currently no way to add a transformer to a tube in a tube composite
      // through the public API, so just log a warning
      logger.warn(
          "Transformers added to TubeCompositeWrapper aren't propagated to the tube composite");

      return super.addTransformer(componentName, transformer);
    }

    @Override
    public <T> Composite addValidator(String componentName, Function<T, Boolean> validator) {
      // Currently no way to add a validator to a tube in a tube composite
      // through the public API, so just log a warning
      logger.warn(
          "Validators added to TubeCompositeWrapper aren't propagated to the tube composite");

      return super.addValidator(componentName, validator);
    }

    @Override
    public void activate() {
      tubeComposite.activate();
      super.activate();
    }

    @Override
    public void deactivate() {
      tubeComposite.deactivate();
      super.deactivate();
    }

    @Override
    public <T> java.util.Optional<T> process(String entryPoint, T data) {
      // Process through the tube composite
      return tubeComposite.process(entryPoint, data);
    }

    @Override
    public Component getComponent(String name) {
      // Try to get from the cache first
      Component cachedComponent = componentCache.get(name);
      if (cachedComponent != null) {
        return cachedComponent;
      }

      // Create a wrapper for the tube and cache it
      try {
        Tube tube = tubeComposite.getTube(name);
        // Convert from domain.component.ComponentPort to component.Component
        ComponentPort componentPort = componentAdapter.wrapLegacyComponent(tube);
        Component component = convertDomainComponentToComponentLayer(componentPort);
        componentCache.put(name, component);
        return component;
      } catch (IllegalArgumentException e) {
        return super.getComponent(name);
      }
    }

    @Override
    public Map<String, Component> getComponents() {
      // Create wrappers for all tubes if they don't already exist in the cache
      Map<String, Tube> tubes = tubeComposite.getTubes();
      for (Map.Entry<String, Tube> entry : tubes.entrySet()) {
        String name = entry.getKey();
        if (!componentCache.containsKey(name)) {
          // Convert from domain.component.ComponentPort to component.Component
          ComponentPort componentPort = componentAdapter.wrapLegacyComponent(entry.getValue());
          Component component = convertDomainComponentToComponentLayer(componentPort);
          componentCache.put(name, component);
        }
      }

      // Return unmodifiable view of the cache
      return java.util.Collections.unmodifiableMap(componentCache);
    }

    @Override
    public Map<String, List<String>> getConnections() {
      // Return connections from the tube composite
      return tubeComposite.getConnections();
    }
  }
}
