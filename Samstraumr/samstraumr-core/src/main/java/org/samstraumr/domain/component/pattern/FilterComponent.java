/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Filter component for the S8r framework
 */

package org.samstraumr.domain.component.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.event.ComponentDataEvent;
import org.samstraumr.domain.identity.ComponentId;

/**
 * A specialized component that filters data based on conditions.
 *
 * <p>FilterComponent subscribes to input channels, applies filters, and publishes only the data
 * that passes the filters to output channels. It follows the Pipes and Filters architectural
 * pattern.
 */
public class FilterComponent extends Component {
  private final String inputChannel;
  private final String outputChannel;
  private final Map<String, Predicate<Object>> filters;
  private boolean requireAllFilters = true;

  /**
   * Creates a new filter component.
   *
   * @param id The component ID
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish filtered data to
   */
  private FilterComponent(ComponentId id, String inputChannel, String outputChannel) {
    super(id);
    this.inputChannel = inputChannel;
    this.outputChannel = outputChannel;
    this.filters = new HashMap<>();

    logActivity(
        "Created filter from channel [" + inputChannel + "] to channel [" + outputChannel + "]");
  }

  /**
   * Creates a new filter component.
   *
   * @param reason The reason for creating this component
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish filtered data to
   * @return A new filter component
   */
  public static FilterComponent create(String reason, String inputChannel, String outputChannel) {
    ComponentId id = ComponentId.create(reason);
    return new FilterComponent(id, inputChannel, outputChannel);
  }

  /**
   * Adds a filter predicate for a specific data key.
   *
   * @param key The data key to filter
   * @param filter The filter predicate
   * @return This filter for method chaining
   */
  public FilterComponent addFilter(String key, Predicate<Object> filter) {
    filters.put(key, filter);
    logActivity("Added filter for key: " + key);
    return this;
  }

  /**
   * Sets whether all filters must pass (AND) or just one filter (OR).
   *
   * @param requireAll true for AND logic (default), false for OR logic
   * @return This filter for method chaining
   */
  public FilterComponent requireAllFilters(boolean requireAll) {
    this.requireAllFilters = requireAll;
    logActivity("Set filter logic to " + (requireAll ? "AND" : "OR"));
    return this;
  }

  /**
   * Processes input data, applying filters and publishing the results.
   *
   * @param event The data event to process
   */
  public void processData(ComponentDataEvent event) {
    logActivity("Processing data from channel: " + event.getDataChannel());

    if (filters.isEmpty()) {
      // No filters means pass everything through
      publishData(outputChannel, event.getData());
      logActivity("No filters defined, passing all data to channel: " + outputChannel);
      return;
    }

    // Check if data passes filters
    boolean passesFilters = checkFilters(event.getData());

    if (passesFilters) {
      // Data passes filters, publish to output channel
      publishData(outputChannel, event.getData());
      logActivity("Data passed filters, published to channel: " + outputChannel);
    } else {
      logActivity("Data did not pass filters, discarded");
    }
  }

  /**
   * Checks if the data passes the defined filters.
   *
   * @param data The data to check
   * @return true if the data passes the filters, false otherwise
   */
  private boolean checkFilters(Map<String, Object> data) {
    if (requireAllFilters) {
      // AND logic - all filters must pass
      for (Map.Entry<String, Predicate<Object>> entry : filters.entrySet()) {
        String key = entry.getKey();
        Predicate<Object> filter = entry.getValue();

        Object value = data.get(key);
        if (value == null) {
          logActivity("Filter failed: key [" + key + "] not found in data");
          return false;
        }

        if (!filter.test(value)) {
          logActivity("Filter failed: key [" + key + "] value [" + value + "] did not pass filter");
          return false;
        }
      }

      // All filters passed
      return true;
    } else {
      // OR logic - at least one filter must pass
      for (Map.Entry<String, Predicate<Object>> entry : filters.entrySet()) {
        String key = entry.getKey();
        Predicate<Object> filter = entry.getValue();

        Object value = data.get(key);
        if (value != null && filter.test(value)) {
          logActivity("Filter passed: key [" + key + "] value [" + value + "] passed filter");
          return true;
        }
      }

      // No filters passed
      return false;
    }
  }

  /**
   * Gets the input channel.
   *
   * @return The input channel
   */
  public String getInputChannel() {
    return inputChannel;
  }

  /**
   * Gets the output channel.
   *
   * @return The output channel
   */
  public String getOutputChannel() {
    return outputChannel;
  }
}
