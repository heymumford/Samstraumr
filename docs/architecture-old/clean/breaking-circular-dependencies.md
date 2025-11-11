# Breaking Circular Dependencies

This document outlines the approach taken to break circular dependencies in the Samstraumr framework while adhering to Clean Architecture principles.

## Problem

The architecture tests identified circular dependencies between the following packages:

1. `org.s8r.app` incorrectly depends on `org.s8r.infrastructure.config.DependencyContainer`
2. `org.s8r.infrastructure.config` incorrectly depends on `org.s8r.Samstraumr`

These circular dependencies violate Clean Architecture principles, which mandate that dependencies should only point inward, from outer layers to inner layers.

## Solution

The solution involved applying several design patterns to break these circular dependencies while maintaining the overall Clean Architecture structure:

### 1. dependency inversion

Created interfaces in the application layer that are implemented by the infrastructure layer:

- Created `ServiceFactory` interface in `org.s8r.application.port`
- Updated `DependencyContainer` to implement this interface

### 2. service locator pattern

Used the Service Locator pattern to avoid direct dependencies on the `DependencyContainer` class:

- Created `ServiceLocator` class in the application layer
- `ServiceLocator` provides static access to a `ServiceFactory` instance
- The infrastructure layer registers its implementation with the `ServiceLocator`

### 3. refactoring client code

Updated client code to use these abstractions:

- Modified `CliApplication` to use `ServiceFactory` instead of directly depending on `DependencyContainer`
- Updated `Samstraumr` to register the `DependencyContainer` with the `ServiceLocator`

### 4. package organization

Added package-info.java files to clearly document the purpose and responsibility of each package:

- `/org/s8r/package-info.java`
- `/org/s8r/application/package-info.java`
- `/org/s8r/app/package-info.java`

## Implementation Details

### Servicefactory interface

```java
public interface ServiceFactory {
    <T> T getService(Class<T> serviceType);
    S8rFacade getFramework();
    LoggerPort getLogger(Class<?> clazz);
}
```

### Servicelocator

```java
public final class ServiceLocator {
    private static ServiceFactory serviceFactory;
    
    public static ServiceFactory getServiceFactory() {
        if (serviceFactory == null) {
            throw new IllegalStateException("ServiceFactory has not been initialized");
        }
        return serviceFactory;
    }
    
    public static void setServiceFactory(ServiceFactory factory) {
        serviceFactory = factory;
    }
}
```

### Changes in cliapplication

```java
public class CliApplication {
  private static ServiceFactory serviceFactory;
  private static LoggerPort logger;
  private static S8rFacade framework;
  
  static {
    serviceFactory = ServiceLocator.getServiceFactory();
    logger = serviceFactory.getLogger(CliApplication.class);
    framework = serviceFactory.getFramework();
  }
  
  // Rest of the class unchanged
}
```

### Changes in samstraumr

```java
private Samstraumr() {
  this.container = DependencyContainer.getInstance();
  this.logger = S8rLoggerFactory.getInstance().getLogger(Samstraumr.class);
  
  // Initialize the ServiceLocator with the DependencyContainer
  ServiceLocator.setServiceFactory(container);
  
  logger.info("Samstraumr framework initialized");
}
```

## Benefits

1. **Clean Dependencies**: Dependencies now flow in the correct direction, from outer to inner layers
2. **Testability**: Interfaces allow for easy mocking in tests
3. **Flexibility**: Implementation details are hidden behind interfaces
4. **Documentation**: Package-info files clearly document the responsibility of each package
5. **Maintainability**: The architecture is now easier to understand and modify

## Further Improvements

1. Move the `Samstraumr` class to the infrastructure layer as it's an implementation detail
2. Create a proper application initialization sequence
3. Consider using a dependency injection framework for more complex scenarios
