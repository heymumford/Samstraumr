package org.s8r.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test that verifies the basic assembly, connectivity, and initialization of the system.
 *
 * <p>Orchestration Tests have the following characteristics:
 *
 * <ul>
 *   <li>Verify that the system can be assembled correctly
 *   <li>Check basic connectivity between components
 *   <li>Ensure initialization sequences complete properly
 *   <li>Fast and reliable, focusing on basic system readiness
 * </ul>
 *
 * <p>This annotation corresponds to the industry-standard concept of "Smoke Tests" but uses
 * domain-specific terminology. These tests serve as the foundational verification that the system
 * is ready for more detailed testing.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("OrchestrationTest")
public @interface OrchestrationTest {}
