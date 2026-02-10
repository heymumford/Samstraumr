/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.infrastructure.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.s8r.application.port.EventHandler;
import org.s8r.application.port.LoggerPort;

/** Event handler for data flow events. */
public class DataFlowEventHandler implements EventHandler {

  private final LoggerPort logger;
  private final String[] eventTypes;
  private final Consumer<Map<String, Object>> dataProcessor;

  /**
   * Creates a new DataFlowEventHandler.
   *
   * @param logger The logger to use
   * @param eventTypes The event types to handle
   * @param dataProcessor The data processor to invoke for events
   */
  public DataFlowEventHandler(
      LoggerPort logger, String[] eventTypes, Consumer<Map<String, Object>> dataProcessor) {
    this.logger = logger;
    this.eventTypes = eventTypes;
    this.dataProcessor = dataProcessor;
  }

  @Override
  public void handleEvent(
      String eventType, String source, String payload, Map<String, String> properties) {
    logger.debug("Processing data flow event - Type: {}, Source: {}", eventType, source);

    // Convert to the format expected by the data processor
    Map<String, Object> data = new HashMap<>();
    data.put("eventType", eventType);
    data.put("source", source);
    data.put("payload", payload);

    // Copy properties into the map
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      data.put(entry.getKey(), entry.getValue());
    }

    // Add original properties as a separate map
    data.put("properties", properties);

    // Process the data
    try {
      dataProcessor.accept(data);
      logger.debug("Data flow event processed successfully");
    } catch (Exception e) {
      logger.error("Error processing data flow event: {}", e.getMessage(), e);
    }
  }

  @Override
  public String[] getEventTypes() {
    return Arrays.copyOf(eventTypes, eventTypes.length);
  }

  /**
   * Creates a specialized event handler for data flow events.
   *
   * @return A specialized event handler for data flow events
   */
  public EventHandler dataEventHandler() {
    return new EventHandler() {
      @Override
      public void handleEvent(
          String eventType, String source, String payload, Map<String, String> properties) {
        DataFlowEventHandler.this.handleEvent(eventType, source, payload, properties);
      }

      @Override
      public String[] getEventTypes() {
        return DataFlowEventHandler.this.getEventTypes();
      }
    };
  }
}
