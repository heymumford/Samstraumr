# Bundle to Composite Refactoring

## Overview

The Samstraumr project has undergone a significant refactoring to rename the "Bundle" concept to "Composite". This change was made to better align our terminology with established design patterns, specifically the Composite pattern described in Gang of Four (GoF) design patterns.

## Rationale

After code analysis, we determined that what we called a "Bundle" in Samstraumr is essentially an implementation of the Composite design pattern. The Composite pattern allows you to compose objects into tree structures to represent part-whole hierarchies. This is exactly what our Bundle class does - it composes multiple Tube objects together in a tree-like structure and treats them uniformly.

By renaming Bundle to Composite, we:

1. Improve the clarity and intention of our code
2. Align with industry-standard terminology
3. Make our codebase more approachable to new developers familiar with design patterns
4. Ensure our documentation and code share the same vocabulary

## Changes Made

The following changes were implemented:

1. **Core Classes**:
   - `Bundle` → `Composite`
   - `BundleFactory` → `CompositeFactory`
   - `BundleEvent` → `CompositeEvent`
   - `BundleFunction` → `CompositeFunction`
   - `BundleData` → `CompositeData`

2. **Package Structure**:
   - `org.samstraumr.tube.bundle` → `org.samstraumr.tube.composite`

3. **Test Classes**:
   - `BundleTest` → `CompositeTest`
   - `@BundleTest` annotation → `@CompositeTest` annotation

4. **Documentation**:
   - `BundlesAndMachines.md` → `CompositesAndMachines.md`
   - Updated all references from "Bundle" to "Composite" in docs

## Backward Compatibility

To maintain backward compatibility during the transition period:

1. The old `Bundle` and `BundleFactory` classes are kept but marked as `@Deprecated`.
2. These deprecated classes now delegate to the new `Composite` and `CompositeFactory` implementations.
3. The old `@BundleTest` annotation also forwards to the new `@CompositeTest` annotation.
4. The old documentation files remain but include redirect notices to the new ones.

## Migration Guide

If you're working with existing code that uses the Bundle pattern, here's how to migrate to the new Composite pattern:

1. Replace all imports:
   ```java
   // Old
   import org.samstraumr.tube.bundle.Bundle;
   import org.samstraumr.tube.bundle.BundleFactory;
   
   // New
   import org.samstraumr.tube.composite.Composite;
   import org.samstraumr.tube.composite.CompositeFactory;
   ```

2. Update method calls:
   ```java
   // Old
   Bundle bundle = BundleFactory.createBundle(env);
   bundle.getBundleId();
   
   // New
   Composite composite = CompositeFactory.createComposite(env);
   composite.getCompositeId();
   ```

3. Replace test annotations:
   ```java
   // Old
   @BundleTest
   
   // New
   @CompositeTest
   ```

4. Update documentation references to point to the new docs.

## Timeline

The deprecated classes and annotations will be maintained for compatibility until the next major version release. After that, they will be removed from the codebase.