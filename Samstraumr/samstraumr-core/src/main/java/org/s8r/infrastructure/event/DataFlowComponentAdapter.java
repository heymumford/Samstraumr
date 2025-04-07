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

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.pattern.DataFlowComponentPort;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;

/**
 * Adapter implementation for the DataFlowComponentPort interface.
 *
 * <p>This adapter connects a component to the data flow event infrastructure,
 * simplifying the subscription and publication of data events without the
 * component directly depending on the infrastructure implementation.
 *
 * <p>This follows the adapter pattern of Clean Architecture, adapting between
 * the domain layer's port interface and the infrastructure layer's implementation.
 */
public class DataFlowComponentAdapter implements DataFlowComponentPort {
  
  private final ComponentId componentId;
  private final DataFlowEventPort dataFlowEventPort;
  private final LoggerPort logger;
  private final Set<String> subscriptions = new HashSet<>();

  /**
   * Creates a new DataFlowComponentAdapter.
   *
   * @param componentId The ID of the component using this adapter
   * @param dataFlowEventPort The data flow event port to adapt to
   * @param logger The logger to use
   */
  public DataFlowComponentAdapter(
      ComponentId componentId,
      DataFlowEventPort dataFlowEventPort,
      LoggerPort logger) {
    this.componentId = componentId;
    this.dataFlowEventPort = dataFlowEventPort;
    this.logger = logger;
  }

  @Override
  public boolean subscribe(String channel, Consumer<ComponentDataEvent> handler) {
    if (channel == null || channel.isEmpty()) {
      logger.warn("Cannot subscribe to null or empty channel");
      return false;
    }
    
    if (handler == null) {
      logger.warn("Cannot subscribe with null handler");
      return false;
    }
    
    try {
      dataFlowEventPort.subscribe(componentId, channel, handler);
      subscriptions.add(channel);
      logger.debug("Component {} subscribed to channel: {}", 
          componentId.getIdString(), channel);
      return true;
    } catch (Exception e) {
      logger.error("Error subscribing component {} to channel {}: {}", 
          componentId.getIdString(), channel, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean unsubscribe(String channel) {
    if (channel == null || channel.isEmpty()) {
      logger.warn("Cannot unsubscribe from null or empty channel");
      return false;
    }
    
    try {
      dataFlowEventPort.unsubscribe(componentId, channel);
      subscriptions.remove(channel);
      logger.debug("Component {} unsubscribed from channel: {}", 
          componentId.getIdString(), channel);
      return true;
    } catch (Exception e) {
      logger.error("Error unsubscribing component {} from channel {}: {}", 
          componentId.getIdString(), channel, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean unsubscribeAll() {
    try {
      dataFlowEventPort.unsubscribeAll(componentId);
      int count = subscriptions.size();
      subscriptions.clear();
      logger.debug("Component {} unsubscribed from all channels ({})", 
          componentId.getIdString(), count);
      return true;
    } catch (Exception e) {
      logger.error("Error unsubscribing component {} from all channels: {}", 
          componentId.getIdString(), e.getMessage(), e);
      return false;
    }
  }

  @Override
  public boolean publish(String channel, Map<String, Object> data) {
    if (channel == null || channel.isEmpty()) {
      logger.warn("Cannot publish to null or empty channel");
      return false;
    }
    
    if (data == null) {
      logger.warn("Cannot publish null data");
      return false;
    }
    
    try {
      dataFlowEventPort.publishData(componentId, channel, data);
      logger.debug("Component {} published data to channel: {}", 
          componentId.getIdString(), channel);
      return true;
    } catch (Exception e) {
      logger.error("Error publishing data from component {} to channel {}: {}", 
          componentId.getIdString(), channel, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public Set<String> getSubscriptions() {
    return Collections.unmodifiableSet(subscriptions);
  }
}