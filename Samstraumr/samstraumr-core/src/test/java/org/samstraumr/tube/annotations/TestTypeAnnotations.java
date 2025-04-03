/*
Filename: TestTypeAnnotations.java
Purpose: Defines annotations for different test types in the Samstraumr testing strategy.
Goals:
  - Provide clear categorization for test types in the codebase
  - Enable filtering of tests by type for targeted test execution
  - Support metadata-driven test reporting and organization
Dependencies:
  - java.lang.annotation: For defining custom annotations
Assumptions:
  - Annotations will be used consistently across test classes
  - Test runners will use these annotations for filtering test execution
*/

package org.samstraumr.tube.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class as a Tube Test (unit test).
 * These tests focus on individual tubes in isolation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface TubeTest {}

/**
 * Marks a test class as a Flow Test (integration test).
 * These tests focus on data flowing through a single tube.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface FlowTest {}

/**
 * Marks a test class as a Bundle Test (component test).
 * These tests focus on connected tubes forming bundles.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BundleTest {}

/**
 * Marks a test class as a Stream Test (system test).
 * These tests focus on external system interactions using TestContainers.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface StreamTest {}

/**
 * Marks a test class as an Adaptation Test (property test).
 * These tests focus on system behavior under changing conditions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AdaptationTest {}

/**
 * Marks a test class as a Machine Test (e2e test).
 * These tests focus on end-to-end functionality using Cucumber.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MachineTest {}

/**
 * Marks a test class as an Acceptance Test (business test).
 * These tests focus on business requirements using Cucumber.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AcceptanceTest {}