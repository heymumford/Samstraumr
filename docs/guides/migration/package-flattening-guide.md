# Package Flattening Migration Guide

## Introduction

This guide helps developers navigate through the recent package flattening changes in the Samstraumr codebase. It explains how to resolve import issues and type incompatibilities that have arisen from the flattening operation.

## Package Flattening Overview

The Samstraumr codebase recently underwent a package flattening operation that moved files from deeply nested subdirectories to their parent directories. This was done to simplify the package structure and make it more maintainable.

### Key Changes

1. Files moved from `org.s8r.component.core.*` to `org.s8r.component.*`
2. Files moved from `org.s8r.component.exception.*` to `org.s8r.component.*`
3. Files moved from `org.s8r.component.identity.*` to `org.s8r.component.*`
4. Files moved from `org.s8r.component.machine.*` to `org.s8r.component.*`
5. Files moved from `org.s8r.component.composite.*` to `org.s8r.component.*`

## Common Issues

### Import Statement Mismatches

The most common issue is import statements that still reference the old package structure. For example:

```java
// Before flattening
import org.s8r.component.core.State;

// After flattening
import org.s8r.component.State; // This is the correct import
```

### Type Incompatibilities

Type incompatibilities can occur when different parts of the code use different import paths for the same class:

```java
// In one file
import org.s8r.component.core.State;
// In another file
import org.s8r.component.State;

// This can lead to incompatible type errors
```

Other type incompatibilities include domain vs. implementation models:
- `org.s8r.domain.component.Component` ≠ `org.s8r.component.Component`

### Method Signature Changes

Some method signatures may have changed during the flattening process:

```java
// Before
environment.getParameter(key, defaultValue);

// After
environment.getParameter(key);
```

### Method Name Changes

Some method names have changed:

```java
// Before
ComponentId.generate("reason");

// After
ComponentId.create("reason");
```

## How to Fix These Issues

### 1. Update Import Statements

Systematically update all import statements to use the new flattened package structure:

```java
// Find all instances
import org.s8r.component.core.*;
import org.s8r.component.exception.*;
import org.s8r.component.identity.*;
import org.s8r.component.machine.*;
import org.s8r.component.composite.*;

// Replace with
import org.s8r.component.*;
```

### 2. Use Type Adapters for Incompatible Types

For type incompatibilities between `org.s8r.component.*` and `org.s8r.domain.*` packages, use the provided adapter utilities:

```java
// Convert domain component to component
org.s8r.component.Component component = 
    ComponentTypeAdapter.fromDomainComponent(domainComponent);

// Convert component to domain component
org.s8r.domain.component.Component domainComponent = 
    ComponentTypeAdapter.toDomainComponent(component);
```

### 3. Update Method Calls

Update method calls to match the new signatures:

```java
// Before
String value = environment.getParameter("key", "default");

// After
String value = environment.getParameter("key");
if (value == null) {
    value = "default";
}
```

## Working with Adapter Utilities

The codebase now includes adapter utilities to help with the transition period:

### ComponentAdapter and ComponentTypeAdapter

Basic conversion between domain and implementation components:

```java
// Domain to implementation
Component component = ComponentTypeAdapter.fromDomainComponent(domainComponent);

// Implementation to domain
DomainComponent domainComponent = ComponentTypeAdapter.toDomainComponent(component);

// Wrapping for use in tests expecting domain components
DomainComponent wrapped = ComponentTypeAdapter.wrap(component);

// Unwrapping when domain component is needed as implementation
Component unwrapped = ComponentTypeAdapter.unwrap(domainComponent);
```

## Testing Your Changes

After making these changes, test your code thoroughly:

1. Run a compilation check: `./s8r-build compile`
2. Run unit tests: `./s8r-test unit`
3. Run component tests: `./s8r-test component`

## Long-term Solution

While the immediate fixes described above will get the codebase compiling, a more comprehensive refactoring is planned to fully migrate to the Clean Architecture model. See the [Clean Architecture Migration Plan](/docs/architecture/clean/clean-architecture-migration.md) for details.

## Common Error Messages and Solutions

### "cannot find symbol: class State; location: package org.s8r.component.core"

**Solution**: Update the import to `import org.s8r.component.State;`

### "incompatible types: org.s8r.domain.component.Component cannot be converted to org.s8r.component.Component"

**Solution**: Use the ComponentTypeAdapter to convert between types:
```java
component = ComponentTypeAdapter.fromDomainComponent(domainComponent);
```

### "cannot find symbol: method generate(java.lang.String)"

**Solution**: Update the method call from `ComponentId.generate()` to `ComponentId.create()`

### "method getParameter in class Environment cannot be applied to given types"

**Solution**: Update the method call to match the new signature.

## Conclusion

The package flattening operation is a step towards a cleaner and more maintainable codebase. By following the guidelines in this document, you can resolve issues related to the flattening and continue development without disruption.

For more information, see:
- [Package Flattening Fixes](/package-flattening-fixes.md)
- [Clean Architecture Migration Plan](/docs/architecture/clean/clean-architecture-migration.md)