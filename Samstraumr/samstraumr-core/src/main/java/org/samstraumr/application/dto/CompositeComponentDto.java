/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * DTO for CompositeComponent in the S8r framework
 */

package org.samstraumr.application.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.component.composite.CompositeComponent;
import org.samstraumr.domain.component.composite.CompositeType;

/**
 * Data Transfer Object (DTO) for CompositeComponent entities.
 *
 * <p>This class extends ComponentDto to include composite-specific information about child
 * components and connections. It follows Clean Architecture principles by keeping the domain
 * entities isolated from external concerns.
 */
public class CompositeComponentDto extends ComponentDto {
  private final String compositeType;
  private final String compositeTypeDescription;
  private final List<ComponentDto> children;
  private final List<ConnectionDto> connections;

  /**
   * Creates a new CompositeComponentDto from a domain CompositeComponent entity.
   *
   * @param composite The domain composite component entity
   */
  public CompositeComponentDto(CompositeComponent composite) {
    super(composite);

    CompositeType type = composite.getCompositeType();
    this.compositeType = type.name();
    this.compositeTypeDescription = type.getDescription();

    // Convert child components to DTOs
    this.children =
        composite.getComponents().stream().map(ComponentDto::new).collect(Collectors.toList());

    // Convert connections to DTOs
    this.connections =
        composite.getConnections().stream().map(ConnectionDto::new).collect(Collectors.toList());
  }

  /**
   * Gets the composite type name.
   *
   * @return The composite type name
   */
  public String getCompositeType() {
    return compositeType;
  }

  /**
   * Gets the composite type description.
   *
   * @return The composite type description
   */
  public String getCompositeTypeDescription() {
    return compositeTypeDescription;
  }

  /**
   * Gets the child components.
   *
   * @return A list of child component DTOs
   */
  public List<ComponentDto> getChildren() {
    return new ArrayList<>(children);
  }

  /**
   * Gets the connections between components.
   *
   * @return A list of connection DTOs
   */
  public List<ConnectionDto> getConnections() {
    return new ArrayList<>(connections);
  }

  /**
   * Factory method to create a CompositeComponentDto from a domain Component entity.
   *
   * @param component The domain component entity
   * @return A new CompositeComponentDto or null if the component is null or not a composite
   */
  public static CompositeComponentDto fromDomain(Component component) {
    if (component == null || !(component instanceof CompositeComponent)) {
      return null;
    }
    return new CompositeComponentDto((CompositeComponent) component);
  }

  @Override
  public String toString() {
    return "CompositeComponentDto{"
        + "id='"
        + getShortId()
        + '\''
        + ", reason='"
        + getReason()
        + '\''
        + ", compositeType='"
        + compositeType
        + '\''
        + ", childCount="
        + children.size()
        + ", connectionCount="
        + connections.size()
        + ", lifecycleState='"
        + getLifecycleState()
        + '\''
        + '}';
  }
}
