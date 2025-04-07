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
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.pattern.DataFlowPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.exception.ComponentNotFoundException;
import org.s8r.domain.identity.ComponentId;

/**
 * Application service for managing data flow between components.
 *
 * <p>This service provides a facade for the event-driven data flow system, allowing components to
 * publish and subscribe to data channels without direct dependencies on each other.
 */
public class DataFlowService implements DataFlowPort {
  private final ComponentRepository componentRepository;
  private final DataFlowEventPort dataFlowHandler;
  private final EventPublisherPort eventPublisher;
  private final LoggerPort logger;

  /**
   * Creates a new DataFlowService.
   *
   * @param componentRepository The component repository
   * @param dataFlowHandler The data flow event handler
   * @param eventPublisher The event publisher
   * @param logger The logger
   */
  public DataFlowService(
      ComponentRepository componentRepository,
      DataFlowEventPort dataFlowHandler,
      EventPublisherPort eventPublisher,
      LoggerPort logger) {
    this.componentRepository = componentRepository;
    this.dataFlowHandler = dataFlowHandler;
    this.eventPublisher = eventPublisher;
    this.logger = logger;
  }

  /**
   * Publishes data from a component to a channel.
   *
   * @param componentId The ID of the component publishing the data
   * @param channel The channel to publish to
   * @param data The data to publish
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void publishData(ComponentId componentId, String channel, Map<String, Object> data) {
    logger.debug(
        "Component [{}] publishing data to channel [{}]", componentId.getShortId(), channel);

    // Use ComponentPort interface instead of concrete Component class
    ComponentPort componentPort =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    componentPort.publishData(channel, data);

    // Process and dispatch events
    dispatchDomainEvents(componentPort);
  }

  /**
   * Publishes a single data value from a component to a channel.
   *
   * @param componentId The ID of the component publishing the data
   * @param channel The channel to publish to
   * @param key The key for the data value
   * @param value The data value
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void publishData(ComponentId componentId, String channel, String key, Object value) {
    logger.debug(
        "Component [{}] publishing data to channel [{}] with key [{}]",
        componentId.getShortId(),
        channel,
        key);

    // Use ComponentPort interface instead of concrete Component class
    ComponentPort componentPort =
        componentRepository
            .findById(componentId)
            .orElseThrow(() -> new ComponentNotFoundException(componentId));

    componentPort.publishData(channel, key, value);

    // Process and dispatch events
    dispatchDomainEvents(componentPort);
  }
  
  /**
   * Dispatches all domain events from a component port and clears them.
   *
   * @param componentPort The component port with events to dispatch
   */
  private void dispatchDomainEvents(ComponentPort componentPort) {
    List<DomainEvent> events = componentPort.getDomainEvents();
    
    if (!events.isEmpty()) {
      logger.debug("Publishing {} events from component {}", 
          events.size(), componentPort.getId().getIdString());
      eventPublisher.publishEvents(events);
      componentPort.clearEvents();
    }
  }

  /**
   * Subscribes a component to a data channel.
   *
   * @param componentId The ID of the component subscribing
   * @param channel The channel to subscribe to
   * @param handler The handler to process data events
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void subscribe(
      ComponentId componentId, String channel, Consumer<ComponentDataEvent> handler) {
    // Verify component exists using ComponentPort interface
    if (!componentRepository.findById(componentId).isPresent()) {
      throw new ComponentNotFoundException(componentId);
    }

    dataFlowHandler.subscribe(componentId, channel, handler);
    logger.info("Component [{}] subscribed to channel [{}]", componentId.getShortId(), channel);
  }

  /**
   * Subscribes a component to a data channel with a simple key-value callback.
   *
   * @param componentId The ID of the component subscribing
   * @param channel The channel to subscribe to
   * @param callback A callback that receives the key and value for each data item
   * @throws ComponentNotFoundException if the component doesn't exist
   */
  public void subscribeSimple(
      ComponentId componentId, String channel, BiConsumer<String, Object> callback) {
    // Verify component exists using ComponentPort interface
    if (!componentRepository.findById(componentId).isPresent()) {
      throw new ComponentNotFoundException(componentId);
    }

    // Create a handler that unpacks the data map and calls the callback for each entry
    Consumer<ComponentDataEvent> handler =
        event -> {
          Map<String, Object> data = event.getData();
          data.forEach(callback::accept);
        };

    dataFlowHandler.subscribe(componentId, channel, handler);
    logger.info(
        "Component [{}] subscribed to channel [{}] with simple callback",
        componentId.getShortId(),
        channel);
  }

  /**
   * Unsubscribes a component from a data channel.
   *
   * @param componentId The ID of the component
   * @param channel The channel to unsubscribe from
   */
  public void unsubscribe(ComponentId componentId, String channel) {
    dataFlowHandler.unsubscribe(componentId, channel);
    logger.info("Component [{}] unsubscribed from channel [{}]", componentId.getShortId(), channel);
  }

  /**
   * Unsubscribes a component from all data channels.
   *
   * @param componentId The ID of the component
   */
  public void unsubscribeAll(ComponentId componentId) {
    dataFlowHandler.unsubscribeAll(componentId);
    logger.info("Component [{}] unsubscribed from all channels", componentId.getShortId());
  }

  /**
   * Gets all available data channels in the system.
   *
   * @return A set of channel names
   */
  public Set<String> getAvailableChannels() {
    return dataFlowHandler.getAvailableChannels();
  }

  /**
   * Gets all channels a component is subscribed to.
   *
   * @param componentId The ID of the component
   * @return A set of channel names
   */
  public Set<String> getComponentSubscriptions(ComponentId componentId) {
    return dataFlowHandler.getComponentSubscriptions(componentId);
  }

  /**
   * Functional interface for simple data callbacks.
   *
   * @param <K> The key type
   * @param <V> The value type
   */
  @FunctionalInterface
  public interface BiConsumer<K, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param key The key
     * @param value The value
     */
    void accept(K key, V value);
  }
}
