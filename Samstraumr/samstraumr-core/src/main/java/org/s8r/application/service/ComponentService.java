/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Application layer service for Component management
 */

package org.s8r.application.service;

import java.util.List;
import java.util.Optional;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeFactory;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.component.composite.ConnectionType;
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
  private final EventDispatcher eventDispatcher;

  /**
   * Creates a new ComponentService.
   *
   * @param componentRepository The component repository
   * @param logger The logger
   * @param eventDispatcher The event dispatcher
   */
  public ComponentService(
      ComponentRepository componentRepository, LoggerPort logger, EventDispatcher eventDispatcher) {
    this.componentRepository = componentRepository;
    this.logger = logger;
    this.eventDispatcher = eventDispatcher;
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

    // Dispatch any events raised during creation
    dispatchDomainEvents(component);

    componentRepository.save(component);
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
    Component parent =
        componentRepository
            .findById(parentId)
            .orElseThrow(() -> new ComponentNotFoundException(parentId));

    // Create child component
    ComponentId childId = ComponentId.create(reason);
    Component child = Component.create(childId);

    // Add parent's lineage to child
    parent.getLineage().forEach(child::addToLineage);

    // Dispatch any events raised during creation
    dispatchDomainEvents(child);

    // Save the child component
    componentRepository.save(child);
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

    Component component =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    component.activate();

    // Dispatch any events raised during activation
    dispatchDomainEvents(component);

    componentRepository.save(component);

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

    Component component =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    component.deactivate();

    // Dispatch any events raised during deactivation
    dispatchDomainEvents(component);

    componentRepository.save(component);

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

    Component component =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    component.terminate();

    // Dispatch any events raised during termination
    dispatchDomainEvents(component);

    componentRepository.save(component);

    logger.info("Component terminated successfully: " + id.getIdString());
  }

  /**
   * Gets a component by ID.
   *
   * @param id The component ID
   * @return An Optional containing the component if found
   */
  public Optional<Component> getComponent(ComponentId id) {
    logger.debug("Getting component: " + id.getIdString());
    return componentRepository.findById(id);
  }

  /**
   * Gets all components.
   *
   * @return A list of all components
   */
  public List<Component> getAllComponents() {
    logger.debug("Getting all components");
    return componentRepository.findAll();
  }

  /**
   * Gets child components for a parent component.
   *
   * @param parentId The parent component ID
   * @return A list of child components
   */
  public List<Component> getChildComponents(ComponentId parentId) {
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

    Component component =
        componentRepository.findById(id).orElseThrow(() -> new ComponentNotFoundException(id));

    component.addToLineage(entry);
    componentRepository.save(component);

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

    // Dispatch any events raised during creation
    dispatchDomainEvents(composite);

    componentRepository.save(composite);
    logger.info("Composite component created successfully: " + composite.getId().getIdString());

    return composite.getId();
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

    // Get the composite
    Component composite =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(composite instanceof CompositeComponent)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Get the component to add
    Component component =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    // Add the component to the composite
    CompositeComponent compositeComponent = (CompositeComponent) composite;
    compositeComponent.addComponent(component);

    // Dispatch any events raised during the operation
    dispatchDomainEvents(compositeComponent);

    // Save the updated composite
    componentRepository.save(compositeComponent);
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

    // Get the composite
    Component composite =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(composite instanceof CompositeComponent)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Remove the component from the composite
    CompositeComponent compositeComponent = (CompositeComponent) composite;
    compositeComponent.removeComponent(componentId);

    // Save the updated composite
    componentRepository.save(compositeComponent);
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
   * @return The ID of the created connection
   * @throws ComponentNotFoundException if any component is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public String createConnection(
      ComponentId compositeId,
      ComponentId sourceId,
      ComponentId targetId,
      ConnectionType type,
      String description) {
    logger.info("Creating " + type + " connection in composite " + compositeId.getShortId());

    // Get the composite
    Component composite =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(composite instanceof CompositeComponent)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Create the connection
    CompositeComponent compositeComponent = (CompositeComponent) composite;
    ComponentConnection connection =
        compositeComponent.connect(sourceId, targetId, type, description);

    // Dispatch any events raised during the operation
    dispatchDomainEvents(compositeComponent);

    // Save the updated composite
    componentRepository.save(compositeComponent);
    logger.info("Connection created successfully");

    return connection.getConnectionId();
  }

  /**
   * Removes a connection from a composite.
   *
   * @param compositeId The ID of the composite component
   * @param connectionId The ID of the connection to remove
   * @throws ComponentNotFoundException if the composite is not found
   * @throws IllegalArgumentException if the connection is not found
   * @throws InvalidOperationException if the composite is not in a valid state
   */
  public void removeConnection(ComponentId compositeId, String connectionId) {
    logger.info(
        "Removing connection " + connectionId + " from composite " + compositeId.getShortId());

    // Get the composite
    Component composite =
        componentRepository
            .findById(compositeId)
            .orElseThrow(() -> new ComponentNotFoundException(compositeId));

    if (!(composite instanceof CompositeComponent)) {
      throw new IllegalArgumentException(
          "Component " + compositeId.getShortId() + " is not a composite component");
    }

    // Remove the connection
    CompositeComponent compositeComponent = (CompositeComponent) composite;
    compositeComponent.disconnect(connectionId);

    // Save the updated composite
    componentRepository.save(compositeComponent);
    logger.info("Connection removed successfully");
  }

  /**
   * Gets a composite component by ID.
   *
   * @param id The component ID
   * @return An Optional containing the composite component if found
   * @throws IllegalArgumentException if the component is not a composite
   */
  public Optional<CompositeComponent> getComposite(ComponentId id) {
    logger.debug("Getting composite component: " + id.getIdString());

    Optional<Component> component = componentRepository.findById(id);

    return component.map(
        c -> {
          if (c instanceof CompositeComponent) {
            return (CompositeComponent) c;
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
   * @return A list of connections involving the specified component
   * @throws ComponentNotFoundException if the composite is not found
   * @throws IllegalArgumentException if the component is not a composite
   */
  public List<ComponentConnection> getComponentConnections(
      ComponentId compositeId, ComponentId componentId) {
    logger.debug(
        "Getting connections for component "
            + componentId.getShortId()
            + " in composite "
            + compositeId.getShortId());

    // Get the composite
    Optional<CompositeComponent> composite = getComposite(compositeId);

    if (!composite.isPresent()) {
      throw new ComponentNotFoundException(compositeId);
    }

    return composite.get().getConnectionsForComponent(componentId);
  }

  /**
   * Dispatches all domain events from a component and clears them.
   *
   * @param component The component with events to dispatch
   */
  private void dispatchDomainEvents(Component component) {
    List<DomainEvent> events = component.getDomainEvents();

    for (DomainEvent event : events) {
      logger.debug("Dispatching event: {}", event.getEventType());
      eventDispatcher.dispatch(event);
    }

    component.clearEvents();
  }
}
