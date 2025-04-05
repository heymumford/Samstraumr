/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Transformer component for the S8r framework
 */

package org.samstraumr.domain.component.pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.samstraumr.domain.component.Component;
import org.samstraumr.domain.event.ComponentDataEvent;
import org.samstraumr.domain.identity.ComponentId;

/**
 * A specialized component that transforms data from one format to another.
 *
 * <p>TransformerComponent subscribes to input channels, applies transformations, and publishes the
 * results to output channels. It follows the Pipes and Filters architectural pattern.
 */
public class TransformerComponent extends Component {
  private final String inputChannel;
  private final String outputChannel;
  private final Map<String, Function<Object, Object>> transformations;

  /**
   * Creates a new transformer component.
   *
   * @param id The component ID
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish transformed data to
   */
  private TransformerComponent(ComponentId id, String inputChannel, String outputChannel) {
    super(id);
    this.inputChannel = inputChannel;
    this.outputChannel = outputChannel;
    this.transformations = new HashMap<>();

    logActivity(
        "Created transformer from channel ["
            + inputChannel
            + "] to channel ["
            + outputChannel
            + "]");
  }

  /**
   * Creates a new transformer component.
   *
   * @param reason The reason for creating this component
   * @param inputChannel The channel to subscribe to for input data
   * @param outputChannel The channel to publish transformed data to
   * @return A new transformer component
   */
  public static TransformerComponent create(
      String reason, String inputChannel, String outputChannel) {
    ComponentId id = ComponentId.create(reason);
    return new TransformerComponent(id, inputChannel, outputChannel);
  }

  /**
   * Adds a transformation function for a specific data key.
   *
   * @param key The data key to transform
   * @param transformation The transformation function
   * @return This transformer for method chaining
   */
  public TransformerComponent addTransformation(
      String key, Function<Object, Object> transformation) {
    transformations.put(key, transformation);
    logActivity("Added transformation for key: " + key);
    return this;
  }

  /**
   * Processes input data, applying transformations and publishing the results.
   *
   * @param event The data event to process
   */
  public void processData(ComponentDataEvent event) {
    logActivity("Processing data from channel: " + event.getDataChannel());

    // Create a map for the transformed data
    Map<String, Object> transformedData = new HashMap<>();

    // Apply transformations to each data key
    for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      // Apply transformation if one exists for this key
      Function<Object, Object> transformation = transformations.get(key);
      if (transformation != null) {
        try {
          Object transformedValue = transformation.apply(value);
          transformedData.put(key, transformedValue);
          logActivity(
              "Transformed key [" + key + "] from [" + value + "] to [" + transformedValue + "]");
        } catch (Exception e) {
          logActivity("Error transforming key [" + key + "]: " + e.getMessage());
        }
      } else {
        // Pass through unchanged
        transformedData.put(key, value);
      }
    }

    // Publish the transformed data
    publishData(outputChannel, transformedData);
    logActivity("Published transformed data to channel: " + outputChannel);
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
