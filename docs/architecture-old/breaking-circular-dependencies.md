# Breaking Circular Dependencies

This document describes the approach used to break circular dependencies between layers in the Samstraumr framework.

## Infrastructure ↔ Application Layer Circular Dependency

### Problem

We identified a circular dependency between:
- `org.s8r.app` package depending on `org.s8r.infrastructure.config.DependencyContainer`
- `org.s8r.infrastructure.config` package depending on `org.s8r.Samstraumr`

This circular dependency violated Clean Architecture principles, which require dependencies to point inward (domain ← application ← infrastructure).

### Solution: Service Locator Pattern

We applied the Service Locator pattern to break this circular dependency:

1. Created a `ServiceFactory` interface in the application layer to define operations needed by client code:

```java
// In org.s8r.application.port.ServiceFactory
public interface ServiceFactory {
    <T> T getService(Class<T> serviceType);
    S8rFacade getFramework();
    LoggerPort getLogger(Class<?> clazz);
}
```

2. Created a `ServiceLocator` class in the application layer to provide static access to the factory:

```java
// In org.s8r.application.ServiceLocator
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

3. Updated `DependencyContainer` to implement `ServiceFactory`:

```java
// In org.s8r.infrastructure.config.DependencyContainer
public class DependencyContainer implements ServiceFactory {
    // Implementation of ServiceFactory methods
    @Override
    public <T> T getService(Class<T> serviceType) {
        return get(serviceType);
    }
    
    @Override
    public S8rFacade getFramework() {
        return get(S8rFacade.class);
    }
    
    @Override
    public LoggerPort getLogger(Class<?> clazz) {
        LoggerFactory loggerFactory = get(LoggerFactory.class);
        return loggerFactory.getLogger(clazz);
    }
}
```

4. Modified application code to get services through the `ServiceLocator`:

```java
// In org.s8r.app.CliApplication
public class CliApplication {
    private static ServiceFactory serviceFactory;
    private static LoggerPort logger;
    private static S8rFacade framework;
    
    static {
        serviceFactory = ServiceLocator.getServiceFactory();
        logger = serviceFactory.getLogger(CliApplication.class);
        framework = serviceFactory.getFramework();
    }
}
```

5. Added a method to register the framework facade without direct dependency:

```java
// In org.s8r.infrastructure.config.DependencyContainer
public void registerFramework(S8rFacade framework) {
    if (framework != null) {
        register(S8rFacade.class, framework);
        LoggerPort logger = get(LoggerPort.class);
        logger.info("Framework facade registered");
    }
}
```

6. Updated `Samstraumr` to register itself with the container while initializing the `ServiceLocator`:

```java
// In org.s8r.Samstraumr
private Samstraumr() {
    this.container = DependencyContainer.getInstance();
    this.logger = S8rLoggerFactory.getInstance().getLogger(Samstraumr.class);
    
    // Initialize the ServiceLocator with the DependencyContainer
    ServiceLocator.setServiceFactory(container);
    
    // Register this instance as the S8rFacade implementation
    container.registerFramework(this);
    
    logger.info("Samstraumr framework initialized");
}
```

### Breaking Dependencies in Legacy Adapters

To address dependencies between infrastructure and adapter layers, we:

1. Created a `LegacyAdapterResolver` interface in the application layer:

```java
// In org.s8r.application.port.LegacyAdapterResolver
public interface LegacyAdapterResolver {
    LegacyEnvironmentConverter getCoreEnvironmentConverter();
    LegacyEnvironmentConverter getTubeEnvironmentConverter();
    LegacyIdentityConverter getCoreIdentityConverter();
    LegacyIdentityConverter getTubeIdentityConverter();
}
```

2. Implemented this interface in the infrastructure layer using reflection to avoid direct dependencies:

```java
// In org.s8r.infrastructure.config.LegacyAdapterFactory
public class LegacyAdapterFactory implements LegacyAdapterResolver {
    // Using reflection to create adapter instances
    @Override
    public LegacyEnvironmentConverter getCoreEnvironmentConverter() {
        if (coreEnvConverter == null) {
            try {
                Class<?> converterClass = Class.forName("org.s8r.adapter.CoreLegacyEnvironmentConverter");
                coreEnvConverter = (LegacyEnvironmentConverter) converterClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create CoreLegacyEnvironmentConverter", e);
            }
        }
        return coreEnvConverter;
    }
}
```

### Results

This approach:
- Broke the circular dependency between application and infrastructure layers
- Applied the Dependency Inversion Principle by depending on abstractions, not implementations
- Used the Service Locator pattern to provide framework services without creating circular dependencies
- Applied reflection to avoid direct adapter dependencies in the infrastructure layer

### Remaining Issues

The adapter layer still directly depends on legacy code from the `org.s8r.tube` and `org.s8r.core` packages. These dependencies need to be addressed by:
- Creating domain interfaces for these legacy types
- Using reflection in adapter implementations to avoid direct imports
- Or potentially moving legacy code to a separate "legacy" module

### References

- [Dependency Inversion Principle](https://en.wikipedia.org/wiki/Dependency_inversion_principle)
- [Service Locator Pattern](https://en.wikipedia.org/wiki/Service_locator_pattern)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)