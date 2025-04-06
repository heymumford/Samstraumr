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

package org.s8r.infrastructure.event;

import org.s8r.domain.event.DomainEvent;

/**
 * Functional interface for event handlers in infrastructure layer.
 * <p>
 * This interface provides a consistent handler pattern for domain events 
 * in the infrastructure layer. It's implemented by classes that need to
 * react to specific domain events.
 * </p>
 *
 * @param <T> The type of domain event this handler can process
 */
@FunctionalInterface
public interface EventHandler<T extends DomainEvent> {

  /**
   * Handles a domain event.
   *
   * @param event The event to handle
   */
  void handle(T event);
}