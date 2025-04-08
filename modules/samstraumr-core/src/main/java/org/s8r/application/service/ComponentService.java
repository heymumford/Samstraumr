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

package org.s8r.application.service;

import java.util.List;
import java.util.Optional;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeFactory;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.component.port.CompositeComponentPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.exception.DuplicateComponentException;
import org.s8r.domain.exception.InvalidOperationException;
import org.s8r.domain.identity.ComponentId;

/**
 * Application service for managing components.
 *
 * <p>This service orchestrates operations on components, following Clean Architecture principles by
 * depending only on domain entities and application layer ports (interfaces).
 */
public class ComponentService {
  private final ComponentRepository componentRepository;
  private final LoggerPort logger;
  private final EventPublisherPort eventPublisher;

  /**
   * Creates a new ComponentService.
   *
   * @param componentRepository The component repository
   * @param logger The logger
   * @param eventPublisher The event publisher port
   */
  public ComponentService(
      ComponentRepository componentRepository,
      LoggerPort logger,
      EventPublisherPort eventPublisher) {
    this.componentRepository = componentRepository;
    this.logger = logger;
    this.eventPublisher = eventPublisher;
  }

  /**
   * Creates a new component.
   *
   * @param reason The reason for creating the component
   * @return The ID of the created component
   */
  public ComponentId createComponent(String reason) {
    logger.info("Creating new component with reason: " + reason);

    ComponentId id = ComponentId.create(reason);
    Component component = Component.create(id);

    // Convert to ComponentPort using the adapter
    ComponentPort componentPort = org.s8r.adapter.ComponentAdapter.createComponentPort(component);

    // Dispatch any events raised during creation
    dispatchDomainEvents(componentPort);

    componentRepository.save(componentPort);
    logger.info("Component created successfully: " + id.getIdString());

    return id;
  }

  /**
   * Creates a child component.
   *
   * @param reason The reason for creating the component
   * @param parentId The parent component's ID
   * @return The ID of the created child component
   * @throws ComponentNotFoundException if the parent component doesn't exist
   */
  public ComponentId createChildComponent(String reason, ComponentId parentId) {
    logger.info("Creating child component with reason: " + reason);

    // Find parent component
    ComponentPort parentPort =
        componentRepository
            .findById(parentId)
            .orElseThrow(() -> new ComponentNotFoundException(parentId));

    // Create child component
    ComponentId childId = ComponentId.create(reason);
    Component child = Component.create(childId);
    ComponentPort childPort = org.s8r.adapter.ComponentAdapter.createComponentPort(child);

    // Add parent's lineage to child
    parentPort.getLineage().forEach(childPort::addToLineage);

    // Dispatch any events raised during creation
    dispatchDomainEvents(childPort);

    // Save the child component
    componentRepository.save(childPort);
    logger.info("Child component created successfully: " + childId.getIdString());

    return childId;
  }

  /**
   * Activates a component.
   *
   * @param id The component ID
   * @throws ComponentNotFoundException if the component doesn't exist
   * @throws InvalidOperationException if the component cannot be activated
   */
  public void activateComponent(ComponentId id) {
    logger.info("Activating component: " + id.getIdString());

    ComponentPort componentPort =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    componentPort.activate();

    // Dispatch any events raised during activation
    dispatchDomainEvents(componentPort);

    componentRepository.save(componentPort);

    logger.info("Component activated successfully: " + id.getIdString());
  }

  /**
   * Deactivates a component.
   *
   * @param id The component ID
   * @throws ComponentNotFoundException if the component doesn't exist
   * @throws InvalidOperationException if the component cannot be deactivated
   */
  public void deactivateComponent(ComponentId id) {
    logger.info("Deactivating component: " + id.getIdString());

    ComponentPort componentPort =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    componentPort.deactivate();

    // Dispatch any events raised during deactivation
    dispatchDomainEvents(componentPort);

    componentRepository.save(componentPort);

    logger.info("Component deactivated successfully: " + id.getIdString());
  }

  /**
   * Terminates a component.
   *
   * @param id The component ID
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void terminateComponent(ComponentId id) {
    logger.info("Terminating component: " + id.getIdString());

    ComponentPort componentPort =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    componentPort.terminate();

    // Dispatch any events raised during termination
    dispatchDomainEvents(componentPort);

    componentRepository.save(componentPort);

    logger.info("Component terminated successfully: " + id.getIdString());
  }

  /**
   * Gets a component by ID.
   *
   * @param id The component ID
   * @return An Optional containing the component port if found
   */
  public Optional<ComponentPort> getComponent(ComponentId id) {
    logger.debug("Getting component: " + id.getIdString());
    return componentRepository.findById(id);
  }

  /**
   * Gets all components.
   *
   * @return A list of all component ports
   */
  public List<ComponentPort> getAllComponents() {
    logger.debug("Getting all components");
    return componentRepository.findAll();
  }

  /**
   * Gets child components for a parent component.
   *
   * @param parentId The parent component ID
   * @return A list of child component ports
   */
  public List<ComponentPort> getChildComponents(ComponentId parentId) {
    logger.debug("Getting child components for parent: " + parentId.getIdString());
    return componentRepository.findChildren(parentId);
  }

  /**
   * Adds an entry to a component's lineage.
   *
   * @param id The component ID
   * @param entry The lineage entry to add
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void addToLineage(ComponentId id, String entry) {
    logger.debug("Adding lineage entry to component: " + id.getIdString());

    ComponentPort componentPort =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    componentPort.addToLineage(entry);
    componentRepository.save(componentPort);

    logger.debug("Lineage entry added successfully");
  }

  // Composite component methods

  /**
   * Creates a new composite component.
   *
   * @param reason The reason for creating the composite
   * @param compositeType The type of composite to create
   * @return The ID of the created composite component
   */
  public ComponentId createComposite(String reason, CompositeType compositeType) {
    logger.info(
        "Creating new composite component of type " + compositeType + " with reason: " + reason);

    CompositeComponent composite = CompositeFactory.createComposite(compositeType, reason);

    // Convert to CompositeComponentPort using the adapter
    CompositeComponentPort compositePort =
        org.s8r.adapter.ComponentAdapter.createCompositeComponentPort(composite);

    // Dispatch any events raised during creation
    dispatchDomainEvents(compositePort);

    componentRepository.save(compositePort);
    logger.info("Composite component created successfully: " + compositePort.getId().getIdString());

    return compositePort.getId();
  }

  /**
   * Creates a specific type of composite component using the factory.
   *
   * @param reason The reason for creating the composite
   * @param compositeTypeName The name of the composite type (must match a CompositeType enum value)
   * @return The ID of the created composite component
   * @throws IllegalArgumentException if the composite type name is invalid
   */
  public ComponentId createCompositeByType(String reason, String compositeTypeName) {
    try {
      CompositeType type = CompositeType.valueOf(compositeTypeName.toUpperCase());
      return createComposite(reason, type);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid composite type: " + compositeTypeName);
      throw new IllegalArgumentException(
          "Invalid composite type: "
              + compositeTypeName
              + ". Valid types are: "
              + String.join(", ", getValidCompositeTypes()));
    }
  }

  /**
   * Gets a list of valid composite type names.
   *
   * @return An array of valid composite type names
   */
  private String[] getValidCompositeTypes() {
    CompositeType[] types = CompositeType.values();
    String[] typeNames = new String[types.length];

    for (int i = 0; i < types.length; i++) {
      typeNames[i] = types[i].name();
    }

    return typeNames;
  }

  /**
   * Adds a component to a composite.
   *
   * @param compositeId The ID of the composite component
   * @param componentId The ID of the component to add
   * @throws ComponentNotFoundException if either component is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   * @throws DuplicateComponentException if the component is already in the composite
   */
  public void addComponentToComposite(ComponentId compositeId, ComponentId componentId) {
    logger.info(
        "Adding component "
            + componentId.getShortId()
            + " to composite "
            + compositeId.getShortId());

    // Get the composite port
    ComponentPort compositePort =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(compositePort instanceof CompositeComponentPort)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Get the component port to add
    ComponentPort componentPort =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    // Add the component to the composite
    CompositeComponentPort compositeComponentPort = (CompositeComponentPort) compositePort;
    boolean success = compositeComponentPort.addComponent(componentId.getIdString(), componentPort);

    if (!success) {
      throw new InvalidOperationException(
          "Failed to add component "
              + componentId.getShortId()
              + " to composite "
              + compositeId.getShortId());
    }

    // Dispatch any events raised during the operation
    dispatchDomainEvents(compositeComponentPort);

    // Save the updated composite
    componentRepository.save(compositeComponentPort);
    logger.info("Component added to composite successfully");
  }

  /**
   * Removes a component from a composite.
   *
   * @param compositeId The ID of the composite component
   * @param componentId The ID of the component to remove
   * @throws ComponentNotFoundException if either component is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public void removeComponentFromComposite(ComponentId compositeId, ComponentId componentId) {
    logger.info(
        "Removing component "
            + componentId.getShortId()
            + " from composite "
            + compositeId.getShortId());

    // Get the composite port
    ComponentPort compositePort =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(compositePort instanceof CompositeComponentPort)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Find the component name within the composite
    CompositeComponentPort compositeComponentPort = (CompositeComponentPort) compositePort;

    // This is a simplification - in a real implementation we would need a way to
    // find a component by ID within a composite. Here we assume the component name
    // is the same as its ID string.
    String componentName = componentId.getIdString();

    // Remove the component from the composite
    Optional<ComponentPort> removed = compositeComponentPort.removeComponent(componentName);

    if (!removed.isPresent()) {
      throw new ComponentNotFoundException(componentId);
    }

    // Save the updated composite
    componentRepository.save(compositeComponentPort);
    logger.info("Component removed from composite successfully");
  }

  /**
   * Creates a connection between two components in a composite.
   *
   * @param compositeId The ID of the composite component
   * @param sourceId The ID of the source component
   * @param targetId The ID of the target component
   * @param type The type of connection
   * @param description A description of the connection
   * @return True if the connection was created successfully
   * @throws ComponentNotFoundException if any component is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public boolean createConnection(
      ComponentId compositeId,
      ComponentId sourceId,
      ComponentId targetId,
      ConnectionType type,
      String description) {
    logger.info("Creating " + type + " connection in composite " + compositeId.getShortId());

    // Get the composite port
    ComponentPort compositePort =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(compositePort instanceof CompositeComponentPort)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Create the connection
    CompositeComponentPort compositeComponentPort = (CompositeComponentPort) compositePort;

    // This is a simplification - in a real implementation we would need a way to
    // convert ComponentIds to component names in the composite. Here we assume
    // component names match their ID strings.
    String sourceName = sourceId.getIdString();
    String targetName = targetId.getIdString();

    boolean success = compositeComponentPort.connect(sourceName, targetName);

    if (!success) {
      throw new InvalidOperationException(
          "Failed to create connection from "
              + sourceId.getShortId()
              + " to "
              + targetId.getShortId());
    }

    // Dispatch any events raised during the operation
    dispatchDomainEvents(compositeComponentPort);

    // Save the updated composite
    componentRepository.save(compositeComponentPort);
    logger.info("Connection created successfully");

    return true;
  }

  /**
   * Removes a connection from a composite.
   *
   * @param compositeId The ID of the composite component
   * @param sourceName The name of the source component
   * @param targetName The name of the target component
   * @throws ComponentNotFoundException if the composite is not found
   * @throws IllegalArgumentException if the connection is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public void removeConnection(ComponentId compositeId, String sourceName, String targetName) {
    logger.info(
        "Removing connection from "
            + sourceName
            + " to "
            + targetName
            + " in composite "
            + compositeId.getShortId());

    // Get the composite port
    ComponentPort compositePort =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(compositePort instanceof CompositeComponentPort)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Remove the connection
    CompositeComponentPort compositeComponentPort = (CompositeComponentPort) compositePort;
    boolean success = compositeComponentPort.disconnect(sourceName, targetName);

    if (!success) {
      throw new IllegalArgumentException(
          "Connection from " + sourceName + " to " + targetName + " not found");
    }

    // Save the updated composite
    componentRepository.save(compositeComponentPort);
    logger.info("Connection removed successfully");
  }

  /**
   * Gets a composite component by ID.
   *
   * @param id The component ID
   * @return An Optional containing the composite component port if found
   * @throws IllegalArgumentException if the component is not a composite
   */
  public Optional<CompositeComponentPort> getComposite(ComponentId id) {
    logger.debug("Getting composite component: " + id.getIdString());

    Optional<ComponentPort> componentPort = componentRepository.findById(id);

    return componentPort.map(
        c -> {
          if (c instanceof CompositeComponentPort) {
            return (CompositeComponentPort) c;
          } else {
            throw new IllegalArgumentException(
                "Component " + id.getShortId() + " is not a composite component");
          }
        });
  }

  /**
   * Gets all connections for a component within a composite.
   *
   * @param compositeId The ID of the composite component
   * @param componentId The ID of the component to get connections for
   * @return A list of target component names connected to the specified component
   * @throws ComponentNotFoundException if the composite is not found
   * @throws IllegalArgumentException if the component is not a composite
   */
  public List<String> getComponentConnections(ComponentId compositeId, ComponentId componentId) {
    logger.debug(
        "Getting connections for component "
            + componentId.getShortId()
            + " in composite "
            + compositeId.getShortId());

    // Get the composite port
    Optional<CompositeComponentPort> compositePort = getComposite(compositeId);

    if (!compositePort.isPresent()) {
      throw new ComponentNotFoundException(compositeId);
    }

    // This is a simplification - in a real implementation we would need a way to
    // find a component by ID within a composite. Here we assume the component name
    // is the same as its ID string.
    String componentName = componentId.getIdString();

    return compositePort.get().getConnectionsFrom(componentName);
  }

  /**
   * Dispatches all domain events from a component port and clears them.
   *
   * @param componentPort The component port with events to dispatch
   */
  private void dispatchDomainEvents(ComponentPort componentPort) {
    List<DomainEvent> events = componentPort.getDomainEvents();

    if (!events.isEmpty()) {
      logger.debug(
          "Dispatching {} events from component {}",
          events.size(),
          componentPort.getId().getIdString());
      eventPublisher.publishEvents(events);
      componentPort.clearEvents();
    }
  }
}
