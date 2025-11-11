<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# README

This document describes the integration patterns implemented in the Samstraumr framework using the event-driven architecture.

## Overview

Samstraumr implements a set of common integration patterns that facilitate the construction of complex data processing pipelines. These patterns are implemented as specialized components that communicate through the event system, enabling flexible and loosely coupled architectures.

The implemented patterns are inspired by the Enterprise Integration Patterns catalog and are designed to solve common integration challenges.

## Core Integration Patterns

### Transformer pattern

The Transformer pattern is used to convert data from one format to another without changing its meaning.

**Implementation: `TransformerComponent`**

```java
public class TransformerComponent extends Component {
    private final String inputChannel;
    private final String outputChannel;
    private final Map<String, Function<Object, Object>> transformations;
    
    // Methods...
}
```

**Key Features:**
- Subscribes to an input channel for receiving data
- Applies transformation functions to specific data keys
- Publishes transformed data to an output channel
- Supports method chaining for adding multiple transformations

**Example Usage:**

```java
// Create a transformer component
TransformerComponent transformer = patternFactory.createTransformer(
    "Temperature converter", "raw.temperatures", "converted.temperatures");

// Add transformation functions
transformer.addTransformation("celsius", value -> {
    Double celsius = (Double) value;
    return celsius * 9/5 + 32; // Convert to Fahrenheit
});
```

### Filter pattern

The Filter pattern selects data based on specific conditions, allowing only data that meets the criteria to pass through.

**Implementation: `FilterComponent`**

```java
public class FilterComponent extends Component {
    private final String inputChannel;
    private final String outputChannel;
    private final Map<String, Predicate<Object>> filters;
    private boolean requireAllFilters = true;
    
    // Methods...
}
```

**Key Features:**
- Subscribes to an input channel for receiving data
- Applies filter predicates to specific data keys
- Publishes data that passes the filters to an output channel
- Supports AND/OR logic for multiple filters
- Includes detailed logging of filter decisions

**Example Usage:**

```java
// Create a filter component
FilterComponent filter = patternFactory.createFilter(
    "High temperature filter", "temperatures", "high.temperatures");

// Add filter predicates
filter.addFilter("temperature", value -> ((Double) value) > 30.0);
filter.addFilter("location", value -> "outdoor".equals(value));

// Configure filter logic (AND or OR)
filter.requireAllFilters(true); // AND logic - both filters must pass
```

### Aggregator pattern

The Aggregator pattern collects and combines multiple related messages into a single message.

**Implementation: `AggregatorComponent`**

```java
public class AggregatorComponent extends Component {
    private final String inputChannel;
    private final String outputChannel;
    private final Map<String, List<Object>> aggregationBuffer;
    private final Map<String, BiFunction<Object, Object, Object>> aggregators;
    private final AggregationStrategy strategy;
    
    // Methods...
    
    public enum AggregationStrategy {
        COUNT, TIME, HYBRID
    }
}
```

**Key Features:**
- Supports count-based aggregation (collect N messages)
- Supports time-based aggregation (publish every N seconds)
- Supports hybrid aggregation (whichever threshold is reached first)
- Custom aggregation functions for each data key
- Default aggregation uses the last value if no custom function is provided

**Example Usage:**

```java
// Create a count-based aggregator
AggregatorComponent countAggregator = patternFactory.createCountAggregator(
    "Sales aggregator", "sales.data", "sales.summary", 10);

// Add aggregation functions
countAggregator.addAggregator("totalSales", (current, next) -> 
    ((Double) current) + ((Double) next));
countAggregator.addAggregator("maxSale", (current, next) -> 
    Math.max(((Double) current), ((Double) next)));

// Create a time-based aggregator
AggregatorComponent timeAggregator = patternFactory.createTimeAggregator(
    "Hourly metrics", "metrics", "hourly.metrics", Duration.ofHours(1));
```

### Router pattern

The Router pattern directs messages to different destinations based on their content or metadata.

**Implementation: `RouterComponent`**

```java
public class RouterComponent extends Component {
    private final String inputChannel;
    private final Map<String, Predicate<Map<String, Object>>> routingRules;
    private final Map<String, Function<Map<String, Object>, Map<String, Object>>> transformers;
    private boolean multicastMode = false;
    
    // Methods...
}
```

**Key Features:**
- Content-based routing using predicates
- Support for multicast mode (route to all matching destinations)
- Channel-specific transformation functions
- Key-based routing for simple cases

**Example Usage:**

```java
// Create a router component
RouterComponent router = patternFactory.createRouter(
    "Order router", "orders");

// Add routing rules
router.addKeyRoute("high-value-orders", "amount", value -> ((Double) value) > 1000.0);
router.addKeyRoute("priority-orders", "priority", value -> "high".equals(value));
router.addKeyRoute("standard-orders", "priority", value -> "standard".equals(value));

// Enable multicast mode (optional)
router.setMulticast(true);

// Add transformers for specific channels (optional)
router.addTransformer("high-value-orders", data -> {
    Map<String, Object> transformed = new HashMap<>(data);
    transformed.put("specialHandling", true);
    return transformed;
});
```

## Pattern Factory

The `PatternFactory` simplifies the creation and wiring of pattern components.

```java
public class PatternFactory {
    private final DataFlowService dataFlowService;
    
    // Factory methods for creating pattern components...
    
    public ComponentId createPipeline(Component... components) {
        // Create a pipeline by connecting components in sequence
        // ...
    }
}
```

**Key Features:**
- Factory methods for all pattern components
- Handles event subscription automatically
- Creates complete pipelines by connecting components

## Advanced Integration Patterns

### Pipeline pattern

The Pipeline pattern processes data through a sequence of processing stages.

**Implementation:**

```java
// Create a pipeline using the pattern factory
patternFactory.createPipeline(
    transformer,
    filter,
    aggregator
);
```

This connects the components in sequence, automatically creating channels between them.

### Publish-subscribe pattern

The Publish-Subscribe pattern allows components to communicate through named channels without direct references to each other.

**Implementation:**
This pattern is implemented through the event system's subscription mechanism.

```java
// Publishing
component.publishData("temperature.updates", temperatureData);

// Subscribing
dataFlowService.subscribe(receiverId, "temperature.updates", event -> {
    // Handle the temperature update
    // ...
});
```

### Content-based router pattern

The Content-Based Router pattern examines message content and routes it to the appropriate channel based on the content.

**Implementation:**
This pattern is implemented through the `RouterComponent` with content-based routing rules.

```java
// Create a content-based router
Map<String, Predicate<Map<String, Object>>> routingRules = new HashMap<>();
routingRules.put("orders.urgent", data -> "urgent".equals(data.get("priority")));
routingRules.put("orders.standard", data -> "standard".equals(data.get("priority")));

RouterComponent router = patternFactory.createContentRouter(
    "Order router", "orders.incoming", routingRules, false);
```

### Splitter pattern

The Splitter pattern breaks a composite message into multiple smaller messages.

**Implementation:**
This pattern can be implemented using the `TransformerComponent` with a transformation function that splits the data.

```java
// Create a splitter
TransformerComponent splitter = patternFactory.createTransformer(
    "Order splitter", "orders.batch", "orders.individual");

splitter.addTransformation("orders", value -> {
    List<Map<String, Object>> orders = (List<Map<String, Object>>) value;
    
    // Publish each order individually
    for (Map<String, Object> order : orders) {
        dataFlowService.publishData(splitter.getId(), "orders.individual", order);
    }
    
    return null; // Don't publish a combined result
});
```

### Wiretap pattern

The Wiretap pattern captures message exchanges for monitoring, debugging, or testing purposes.

**Implementation:**
This pattern is implemented through the monitoring system.

```java
// Create a monitored component
Consumer<ComponentDataEvent> monitoredHandler = systemMonitor.monitorComponent(
    component, "data.channel", handler);
```

## Building Complex Integration Solutions

Complex integration solutions can be built by combining these patterns. Here's an example of a complete data processing pipeline:

```java
// Create components
TransformerComponent normalizer = patternFactory.createTransformer(
    "Data normalizer", "data.raw", "data.normalized");
normalizer.addTransformation("value", value -> ((String) value).trim().toLowerCase());

FilterComponent validator = patternFactory.createFilter(
    "Data validator", "data.normalized", "data.valid");
validator.addFilter("value", value -> ((String) value).length() > 0);

RouterComponent router = patternFactory.createRouter(
    "Data router", "data.valid");
router.addKeyRoute("data.important", "priority", value -> ((Integer) value) > 5);
router.addKeyRoute("data.standard", "priority", value -> ((Integer) value) <= 5);

AggregatorComponent aggregator = patternFactory.createCountAggregator(
    "Data aggregator", "data.important", "data.summary", 10);
aggregator.addAggregator("count", (current, next) -> ((Integer) current) + 1);

// Connect components into a pipeline
patternFactory.createPipeline(
    normalizer,
    validator,
    router
);

// Connect aggregator separately since it's not in the main pipeline
dataFlowService.subscribe(aggregator.getId(), "data.important", aggregator::processData);
```

## Best Practices

1. **Use Descriptive Names**: Give components clear names that describe their purpose
2. **Single Responsibility**: Each component should have a single responsibility
3. **Chain Components**: Build pipelines by chaining components together
4. **Monitor Performance**: Use metrics to monitor the performance of components
5. **Handle Errors**: Implement proper error handling in all components
6. **Design for Testability**: Make components testable in isolation
7. **Document Channels**: Document the structure and meaning of data on each channel

## Conclusion

The integration patterns implemented in Samstraumr provide a powerful toolbox for building complex data processing solutions. By leveraging the event-driven architecture and Clean Architecture principles, these patterns enable flexible, maintainable, and loosely coupled systems.
