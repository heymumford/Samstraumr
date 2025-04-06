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

package org.s8r.domain.component.pattern;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import org.s8r.application.service.DataFlowService;
import org.s8r.domain.component.Component;
import org.s8r.domain.identity.ComponentId;

/**
 * Factory for creating and connecting pattern-based components.
 *
 * <p>This factory simplifies the creation and wiring of specialized components that implement
 * common integration patterns using the event-driven architecture.
 */
public class PatternFactory {
  private final DataFlowService dataFlowService;

  /**
   * Creates a new pattern factory.
   *
   * @param dataFlowService The data flow service to use for component communication
   */
  public PatternFactory(DataFlowService dataFlowService) {
    this.dataFlowService = dataFlowService;
  }

  /**
   * Creates a transformer component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @return The created transformer component
   */
  public TransformerComponent createTransformer(
      String reason, String inputChannel, String outputChannel) {
    TransformerComponent transformer =
        TransformerComponent.create(reason, inputChannel, outputChannel);

    // Subscribe to the input channel
    dataFlowService.subscribe(transformer.getId(), inputChannel, transformer::processData);

    return transformer;
  }

  /**
   * Creates a transformer with a specified transformation function.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @param key The data key to transform
   * @param transformation The transformation function
   * @return The created transformer component
   */
  public TransformerComponent createTransformer(
      String reason,
      String inputChannel,
      String outputChannel,
      String key,
      Function<Object, Object> transformation) {
    TransformerComponent transformer = createTransformer(reason, inputChannel, outputChannel);
    transformer.addTransformation(key, transformation);
    return transformer;
  }

  /**
   * Creates a filter component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @return The created filter component
   */
  public FilterComponent createFilter(String reason, String inputChannel, String outputChannel) {
    FilterComponent filter = FilterComponent.create(reason, inputChannel, outputChannel);

    // Subscribe to the input channel
    dataFlowService.subscribe(filter.getId(), inputChannel, filter::processData);

    return filter;
  }

  /**
   * Creates a filter with a specified filter predicate.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @param key The data key to filter on
   * @param predicate The filter predicate
   * @return The created filter component
   */
  public FilterComponent createFilter(
      String reason,
      String inputChannel,
      String outputChannel,
      String key,
      Predicate<Object> predicate) {
    FilterComponent filter = createFilter(reason, inputChannel, outputChannel);
    filter.addFilter(key, predicate);
    return filter;
  }

  /**
   * Creates a count-based aggregator component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @param countThreshold The count threshold for publishing aggregated data
   * @return The created aggregator component
   */
  public AggregatorComponent createCountAggregator(
      String reason, String inputChannel, String outputChannel, int countThreshold) {
    AggregatorComponent aggregator =
        AggregatorComponent.createCountBased(reason, inputChannel, outputChannel, countThreshold);

    // Subscribe to the input channel
    dataFlowService.subscribe(aggregator.getId(), inputChannel, aggregator::processData);

    return aggregator;
  }

  /**
   * Creates a time-based aggregator component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @param timeThreshold The time threshold for publishing aggregated data
   * @return The created aggregator component
   */
  public AggregatorComponent createTimeAggregator(
      String reason, String inputChannel, String outputChannel, Duration timeThreshold) {
    AggregatorComponent aggregator =
        AggregatorComponent.createTimeBased(reason, inputChannel, outputChannel, timeThreshold);

    // Subscribe to the input channel
    dataFlowService.subscribe(aggregator.getId(), inputChannel, aggregator::processData);

    return aggregator;
  }

  /**
   * Creates a hybrid aggregator component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @param countThreshold The count threshold for publishing aggregated data
   * @param timeThreshold The time threshold for publishing aggregated data
   * @return The created aggregator component
   */
  public AggregatorComponent createHybridAggregator(
      String reason,
      String inputChannel,
      String outputChannel,
      int countThreshold,
      Duration timeThreshold) {
    AggregatorComponent aggregator =
        AggregatorComponent.createHybrid(
            reason, inputChannel, outputChannel, countThreshold, timeThreshold);

    // Subscribe to the input channel
    dataFlowService.subscribe(aggregator.getId(), inputChannel, aggregator::processData);

    return aggregator;
  }

  /**
   * Creates an aggregator with specified aggregation functions.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param outputChannel The output channel
   * @param countThreshold The count threshold for publishing aggregated data
   * @param aggregators Map of key-aggregator function pairs
   * @return The created aggregator component
   */
  public AggregatorComponent createAggregator(
      String reason,
      String inputChannel,
      String outputChannel,
      int countThreshold,
      Map<String, BiFunction<Object, Object, Object>> aggregators) {
    AggregatorComponent aggregator =
        createCountAggregator(reason, inputChannel, outputChannel, countThreshold);

    // Add aggregators
    for (Map.Entry<String, BiFunction<Object, Object, Object>> entry : aggregators.entrySet()) {
      aggregator.addAggregator(entry.getKey(), entry.getValue());
    }

    return aggregator;
  }

  /**
   * Creates a router component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @return The created router component
   */
  public RouterComponent createRouter(String reason, String inputChannel) {
    RouterComponent router = RouterComponent.create(reason, inputChannel);

    // Subscribe to the input channel
    dataFlowService.subscribe(router.getId(), inputChannel, router::processData);

    return router;
  }

  /**
   * Creates a content-based router with specified routing rules.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param routingRules Map of output channel-routing rule pairs
   * @param multicast Whether to allow routing to multiple channels
   * @return The created router component
   */
  public RouterComponent createContentRouter(
      String reason,
      String inputChannel,
      Map<String, Predicate<Map<String, Object>>> routingRules,
      boolean multicast) {
    RouterComponent router = createRouter(reason, inputChannel);
    router.setMulticast(multicast);

    // Add routing rules
    for (Map.Entry<String, Predicate<Map<String, Object>>> entry : routingRules.entrySet()) {
      router.addRoute(entry.getKey(), entry.getValue());
    }

    return router;
  }

  /**
   * Creates a key-based router component.
   *
   * @param reason The reason for creating the component
   * @param inputChannel The input channel
   * @param routingKey The key to use for routing
   * @param routeMap Map of value-output channel pairs
   * @return The created router component
   */
  public RouterComponent createKeyRouter(
      String reason, String inputChannel, String routingKey, Map<Object, String> routeMap) {
    RouterComponent router = createRouter(reason, inputChannel);

    // Create a routing rule for each value-channel pair
    for (Map.Entry<Object, String> entry : routeMap.entrySet()) {
      Object value = entry.getKey();
      String outputChannel = entry.getValue();

      router.addKeyRoute(outputChannel, routingKey, obj -> value.equals(obj));
    }

    return router;
  }

  /**
   * Creates a processing pipeline by connecting components in sequence.
   *
   * @param components The components to connect in order
   * @return The ID of the first component in the pipeline
   */
  public ComponentId createPipeline(Component... components) {
    if (components.length < 2) {
      throw new IllegalArgumentException("Pipeline must have at least two components");
    }

    // Create channels to connect components
    for (int i = 0; i < components.length - 1; i++) {
      Component current = components[i];
      Component next = components[i + 1];

      String channelName =
          "pipeline-" + current.getId().getShortId() + "-to-" + next.getId().getShortId();

      // Create publisher for current component
      Map<String, Object> initialData = new HashMap<>();
      initialData.put("pipelineInitialized", true);
      dataFlowService.publishData(current.getId(), channelName, initialData);

      // Create subscriber for next component
      dataFlowService.subscribe(
          next.getId(),
          channelName,
          event -> {
            // Handle the data in the next component
            if (next instanceof TransformerComponent) {
              ((TransformerComponent) next).processData(event);
            } else if (next instanceof FilterComponent) {
              ((FilterComponent) next).processData(event);
            } else if (next instanceof AggregatorComponent) {
              ((AggregatorComponent) next).processData(event);
            } else if (next instanceof RouterComponent) {
              ((RouterComponent) next).processData(event);
            } else {
              // Generic component handling
              Map<String, Object> data = event.getData();
              next.publishData(channelName + "-out", data);
            }
          });
    }

    return components[0].getId();
  }
}
