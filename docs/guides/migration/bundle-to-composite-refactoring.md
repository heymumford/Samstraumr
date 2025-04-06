<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


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

## First Phase Changes

The following changes were implemented in the initial code refactoring:

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
   - `docs/BundlesAndMachines.md` → `docs/concepts/composites-and-machines.md`
   - Updated all references from "Bundle" to "Composite" in docs

## Second Phase Changes

As part of the ongoing refactoring effort, additional updates were made to ensure consistent terminology throughout the codebase:

### 1. Feature File Updates

1. **Renamed Directory Structure**
   - `/tube/features/L1_Bundle/` → `/tube/features/L1_Composite/`
   - `/composites/features/L1_Bundle/` → `/composites/features/L1_Composite/`
2. **Renamed Feature Files**
   - `bundle-connection-test.feature` → `composite-connection-test.feature`
3. **Updated Feature File Content**
   - Updated references to "bundle" in feature file headers to "composite"
   - Updated all `@L1_Bundle` tags to `@L1_Composite`
   - Updated scenario descriptions with "composite" instead of "bundle"

### 2. Test Tag Ontology Updates

1. **Updated `TagOntology.md`**
   - Changed `@BundleTest` → `@CompositeTest`
   - Changed `@L1_Bundle` → `@L1_Composite`
   - Modified test type descriptions to refer to composites instead of bundles

### 3. Java File Updates

1. **Updated JavaDoc in `CompositeConnectionSteps.java`**
   - Updated the reference to feature file from `CompositeConnectionTest.feature` to `composite-connection-test.feature`

### 4. Build Configuration Updates

1. **Updated Maven Profile Descriptions**
   - Changed Maven profile description from "Component (Composite/Bundle) Tests Profile" to "Component (Composite) Tests Profile"

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

## Benefits

1. **Consistent Terminology**
   - The codebase now consistently uses "composite" instead of "bundle"
   - Removes confusion for new developers working with the code
   - Aligns with documentation and class naming
2. **Better Design Pattern Alignment**
   - The Composite pattern from Gang of Four design patterns is now consistently referenced
3. **Forward Compatibility**
   - Tests and documentation no longer refer to deprecated concepts

## Affected Components

1. **Feature Files**: 4 feature files updated
   - composite-connection-test.feature
   - observer-tube-test.feature
   - transformer-tube-test.feature
   - validator-tube-test.feature
2. **Java Files**: 1 file updated
   - CompositeConnectionSteps.java
3. **Build Files**: 1 file updated
   - pom.xml
4. **Documentation**: 1 file updated
   - TagOntology.md
