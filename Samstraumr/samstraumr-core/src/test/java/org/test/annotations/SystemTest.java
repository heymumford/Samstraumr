package org.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "System Test", indicating it focuses on testing the entire system as a whole.
 *
 * <p>System Tests have the following characteristics:
 *
 * <ul>
 *   <li>Whole system - They test the system end-to-end
 *   <li>Full functionality - They verify complete business processes
 *   <li>Real environment - They run against a system that's as close to production as possible
 * </ul>
 *
 * <p>In traditional testing terminology, system tests verify that the entire system works correctly
 * as a whole. In Samstraumr, they align with Stream Tests that verify data streams through the
 * entire system.
 *
 * <p>Examples of system tests include:
 *
 * <ul>
 *   <li>End-to-end business process validation
 *   <li>System behavior verification under load
 *   <li>System-wide error handling
 *   <li>Full stream processing tests
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("System")
@Tag("Stream")
public @interface SystemTest {}
