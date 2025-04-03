# Bundle to Composite Transition

This package provides backward compatibility for the transition from `Bundle` to `Composite` architecture.

## Overview

The `Bundle` class has been deprecated in favor of the new `Composite` class. This package includes:

1. `Bundle` - A compatibility wrapper around `Composite`
2. `BundleFactory` - A compatibility factory that delegates to `CompositeFactory`
3. Support classes like `Bundle.CircuitBreaker` and `Bundle.BundleEvent` that wrap their `Composite` counterparts

## Using the Compatibility Layer

While existing code can continue to use the `Bundle` and `BundleFactory` classes, it is recommended to migrate to the new `Composite` architecture:

```java
// Old code
Bundle bundle = BundleFactory.createBundle(environment);

// New code
Composite composite = CompositeFactory.createComposite(environment);
```

## Migration Path

1. Replace all imports from `org.samstraumr.tube.bundle.*` with `org.samstraumr.tube.composite.*`
2. Replace all type references to `Bundle` with `Composite`
3. Replace all factory method calls from `BundleFactory` to `CompositeFactory`
4. Update any references to `Bundle.BundleEvent` to use `Composite.CompositeEvent` instead
5. Update any references to `Bundle.CircuitBreaker` to use `Composite.CircuitBreaker` instead

## Testing Compatibility

The test compatibility utilities in `org.samstraumr.tube.test.RunTests` provide a unified interface for running tests with both terminology systems.

To support both legacy and new code during the transition, these utilities map between industry-standard terminology and Samstraumr-specific terminology, such as:

- Unit ⟷ Tube
- Component ⟷ Composite
- Integration ⟷ Flow
- System ⟷ Stream
- API ⟷ Machine
- Property ⟷ Adaptation
- End-to-End ⟷ Acceptance

## Timeline

The `Bundle` class and related utilities are scheduled for removal in a future release. It is recommended to complete migration to the `Composite` architecture as soon as possible.