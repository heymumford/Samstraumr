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

package org.s8r.domain.component;

import java.util.List;

import org.s8r.domain.component.port.ComponentPort;

/**
 * Domain port for querying components from the system.
 *
 * <p>This interface provides read-only access to the component repository for domain logic that
 * needs to query existing components. It follows the Port-Adapter pattern from Hexagonal
 * Architecture, defining what the domain needs without specifying how it's implemented.
 *
 * <p>Implementations in the infrastructure layer will bridge to concrete persistence mechanisms
 * (in-memory collections, databases, etc.).
 *
 * <p>This is a pure domain interface with dependencies only on other domain types, complying with
 * the Dependency Inversion Principle of Clean Architecture.
 */
public interface ComponentQueryPort {

  /**
   * Retrieves all components currently in the system.
   *
   * <p>This method returns all components regardless of their lifecycle state. Callers should
   * filter by state if needed.
   *
   * @return a list of all components, or an empty list if no components exist
   */
  List<ComponentPort> findAll();
}
