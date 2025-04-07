# Port Interfaces in Samstraumr Clean Architecture

This package contains the port interfaces for the Samstraumr Clean Architecture implementation. These ports define the boundaries between the application core and external infrastructure.

## Overview

Port interfaces are a key component of Clean Architecture, allowing the application's core business logic to remain independent of external concerns like databases, UI frameworks, or external services. They represent the "doors" through which the application communicates with the outside world.

## Port Types

Samstraumr implements the following port interfaces:

### Primary (Driving) Ports

These ports are used by external actors to interact with the system:

- `ComponentPort`: Interface for component management operations
- `MachinePort`: Interface for machine orchestration
- `CompositePort`: Interface for composite component operations

### Secondary (Driven) Ports

These ports are used by the application to interact with external systems:

- `CachePort`: Interface for caching operations
- `ConfigurationPort`: Interface for configuration management
- `DataFlowEventPort`: Interface for data flow events
- `EventPublisherPort`: Interface for publishing events
- `FileSystemPort`: Interface for file system operations
- `MessagingPort`: Interface for messaging systems
- `NotificationPort`: Interface for sending notifications
- `PersistencePort`: Interface for data persistence
- `SecurityPort`: Interface for security operations
- `TaskExecutionPort`: Interface for executing tasks asynchronously
- `ValidationPort`: Interface for data validation
- `TemplatePort`: Interface for template rendering
- `StoragePort`: Interface for generic storage operations

## Implementation Pattern

Each port interface follows these implementation patterns:

1. The port interface is defined in the application layer
2. Service classes in the application layer use these ports via dependency injection
3. Adapter implementations are provided in the infrastructure layer
4. The wiring between ports and adapters is handled by a dependency container

## Integration Patterns

Ports are designed to work together through the following integration patterns:

### Cache-FileSystem Integration

The `CachePort` and `FileSystemPort` work together to provide efficient file access with caching capabilities:

- File content is cached to minimize disk access
- Cache invalidation when files are modified
- Cache expiration policies for file metadata
- Batch operations with caching support

### Event-Notification Integration

The `EventPublisherPort` and `NotificationPort` are integrated to provide event-driven notifications:

- Domain events trigger notifications based on event type
- Event metadata is used to format notifications
- Recipients are determined based on event context
- Batching of events for consolidated notifications

### Validation-Persistence Integration

The `ValidationPort` and `PersistencePort` work together to ensure data integrity:

- Entities are validated before persistence operations
- Invalid entities are rejected with detailed error messages
- Validation context is shared with persistence operations
- Query results can be validated against business rules

## Diagrams

Port interface diagrams are available in the `/docs/diagrams` directory:

- `samstraumr_ports_component_diagram.svg`: Shows how port interfaces connect components
- `samstraumr_ports_integration_diagram.svg`: Shows the integration patterns between ports
- `samstraumr_detailed_ports_diagram.svg`: Shows the methods and responsibilities of each port
- `samstraumr_clean_arch_ports_diagram.svg`: Shows how ports fit into Clean Architecture

## Usage Examples

### Using a Port in an Application Service

```java
@Service
public class NotificationService {
    private final NotificationPort notificationPort;
    
    public NotificationService(NotificationPort notificationPort) {
        this.notificationPort = notificationPort;
    }
    
    public void sendSystemAlert(String subject, String content) {
        Notification notification = Notification.builder()
            .type(NotificationType.SYSTEM_ALERT)
            .subject(subject)
            .content(content)
            .build();
            
        notificationPort.send(notification);
    }
}
```

### Implementing a Port with an Adapter

```java
@Component
public class EmailNotificationAdapter implements NotificationPort {
    private final EmailClient emailClient;
    
    public EmailNotificationAdapter(EmailClient emailClient) {
        this.emailClient = emailClient;
    }
    
    @Override
    public NotificationResult send(Notification notification) {
        try {
            Email email = convertToEmail(notification);
            EmailResponse response = emailClient.send(email);
            return NotificationResult.success(notification.getId(), response.getMessageId());
        } catch (Exception e) {
            return NotificationResult.failure(notification.getId(), e.getMessage());
        }
    }
    
    // Other implemented methods...
}
```

## Port Contract Tests

Each port interface has associated contract tests to ensure that adapter implementations properly fulfill the port contract. These tests are located in the `src/test/java/org/s8r/adapter/contract` package.

## Related Documentation

- [Clean Architecture Principles](../../docs/architecture/clean/adapter-pattern-implementation.md)
- [Port Implementation Guidelines](../../docs/architecture/clean/port-interface-implementation.md)
- [Adapter Contract Testing](../../docs/architecture/clean/adapter-contract-testing.md)