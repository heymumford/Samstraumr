<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Monitoring and Management

This document outlines the monitoring and management capabilities implemented in the Samstraumr framework as part of our Clean Architecture and event-driven design.

## Overview

The monitoring system provides observability into the runtime behavior of components, helping detect issues, track performance, and manage the overall health of the system. It follows these design principles:

- **Non-invasive**: Monitoring should not interfere with core business logic
- **Configurable**: Monitoring can be enabled/disabled as needed
- **Extensible**: Easy to add new monitoring capabilities
- **Lightweight**: Minimal performance impact
- **Decoupled**: Monitoring is a cross-cutting concern separate from business logic

## Core Components

### MetricsCollector

The `MetricsCollector` tracks performance metrics for components and channels:

```java
public class MetricsCollector extends Component {
    private final Map<String, ChannelMetrics> channelMetrics;
    private final Map<ComponentId, ComponentMetrics> componentMetrics;
    private final Duration publishInterval;
    
    // Key metrics tracked:
    // - Message throughput (messages/second)
    // - Processing time (min/max/avg)
    // - Error rates and types
    // - Queue depths
    // - Resource utilization
}
```

Key capabilities:
- Collects metrics at component and channel levels
- Publishes metrics on a configurable interval
- Supports custom metric definitions
- Tracks historical trends

### HealthMonitor

The `HealthMonitor` tracks the health status of components and resources:

```java
public class HealthMonitor extends Component {
    private final Map<ComponentId, ComponentHealth> componentHealth;
    private final Map<String, ChannelHealth> channelHealth;
    private final Map<String, ResourceMonitor> resourceMonitors;
    
    // Health checks:
    // - Component availability
    // - Channel connectivity
    // - Resource utilization
    // - Error conditions
    // - Performance thresholds
}
```

Key capabilities:
- Detects component failures
- Monitors resource exhaustion
- Generates alerts based on configured thresholds
- Provides health status API
- Supports self-healing actions

### MonitoringFactory

The `MonitoringFactory` creates and configures monitoring components:

```java
public class MonitoringFactory {
    private final DependencyContainer container;
    
    public MetricsCollector createMetricsCollector(
        Duration publishInterval,
        List<String> monitoredChannels,
        List<ComponentId> monitoredComponents) {
        // Creates and configures a metrics collector
    }
    
    public HealthMonitor createHealthMonitor(
        Duration checkInterval,
        Map<String, ResourceThreshold> resourceThresholds) {
        // Creates and configures a health monitor
    }
}
```

## Integration Patterns

### Monitor Decorator

The monitor decorator wraps components to add metrics collection:

```java
public class MonitoredComponent extends ComponentDecorator {
    private final MetricsCollector metricsCollector;
    
    @Override
    public void process(Message message) {
        long startTime = System.nanoTime();
        try {
            wrappedComponent.process(message);
            recordSuccess(message, startTime);
        } catch (Exception e) {
            recordError(message, e, startTime);
            throw e;
        }
    }
}
```

### Health Check Integration

Components can implement the `HealthCheckable` interface:

```java
public interface HealthCheckable {
    HealthStatus checkHealth();
    Map<String, Object> getHealthMetrics();
}
```

## Configuration

### Metrics Configuration

Configure metrics collection through the `monitoring.properties` file:

```properties
# General settings
metrics.enabled=true
metrics.publishInterval=30s

# Component metrics
metrics.component.enabled=true
metrics.component.includePatterns=*
metrics.component.excludePatterns=

# Channel metrics
metrics.channel.enabled=true
metrics.channel.includePatterns=*
metrics.channel.excludePatterns=
```

### Health Check Configuration

Configure health checks through the `health.properties` file:

```properties
# General settings
health.enabled=true
health.checkInterval=15s

# Resource thresholds
health.resource.cpu.warning=70
health.resource.cpu.critical=90
health.resource.memory.warning=80
health.resource.memory.critical=95
health.resource.disk.warning=85
health.resource.disk.critical=95

# Alert settings
alerts.enabled=true
alerts.channel=system.alerts
```

## Usage Examples

### Basic Metrics Collection

```java
// Create and configure metrics collector
MetricsCollector metricsCollector = new MetricsCollector(
    Map.of(), // Initial channel metrics
    Map.of(), // Initial component metrics
    Duration.ofSeconds(30) // Publish interval
);

// Register with event system
eventDispatcher.registerHandler(
    ComponentProcessedEvent.class,
    metricsCollector::recordComponentProcessing
);

// Get metrics report
MetricsReport report = metricsCollector.generateReport();
```

### Health Monitoring

```java
// Create health monitor
HealthMonitor healthMonitor = new HealthMonitor(
    Map.of(), // Initial component health
    Map.of(), // Initial channel health
    Map.of(   // Resource monitors
        "cpu", new CpuMonitor(70, 90),
        "memory", new MemoryMonitor(80, 95)
    )
);

// Register health-checkable component
healthMonitor.registerComponent(componentId, component);

// Get health status
SystemHealthStatus status = healthMonitor.getSystemStatus();
if (!status.isHealthy()) {
    // Handle unhealthy system
}
```

### Creating Monitored Components

```java
// Create a component with monitoring
Component component = new TransformerComponent(...);
Component monitoredComponent = new MonitoredComponent(
    component,
    metricsCollector
);

// Or via factory
Component monitoredComponent = MonitoringFactory.createMonitoredComponent(
    component,
    metricsCollector
);
```

## Best Practices

1. **Selective Monitoring**: Monitor the most critical components rather than everything
2. **Appropriate Intervals**: Balance monitoring frequency with performance impact
3. **Meaningful Metrics**: Focus on actionable metrics that provide insights
4. **Health Definitions**: Define clear health criteria for components
5. **Alerting Strategy**: Set appropriate thresholds to avoid alert fatigue
6. **Monitoring Security**: Protect sensitive monitoring data
7. **Performance Considerations**: Ensure monitoring doesn't become a bottleneck

## Future Enhancements

- **Distributed Tracing**: Add support for distributed tracing across components
- **Visualization**: Integrate with graphing and dashboard tools
- **Machine Learning**: Add anomaly detection for proactive monitoring
- **External Systems**: Support for publishing metrics to external monitoring systems
