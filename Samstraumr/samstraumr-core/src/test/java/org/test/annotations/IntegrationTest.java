package org.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as an "Integration Test", indicating it focuses on testing the interaction between
 * different parts of the system.
 *
 * <p>Integration Tests have the following characteristics:
 *
 * <ul>
 *   <li>Cross-boundary - They test interactions across system boundaries
 *   <li>Flow-oriented - They verify data and control flow through the system
 *   <li>Realistic environment - They often run against actual dependencies
 * </ul>
 *
 * <p>In traditional testing terminology, integration tests verify that components interact
 * correctly with each other. In Samstraumr, they align with Flow Tests that verify data flows
 * correctly through tube assemblies.
 *
 * <p>Examples of integration tests include:
 *
 * <ul>
 *   <li>End-to-end data flow through multiple composites
 *   <li>Transformation and validation of data as it flows
 *   <li>Cross-component interaction behavior
 *   <li>Error propagation across integration points
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Integration")
@Tag("Flow")
public @interface IntegrationTest {}
