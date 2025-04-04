package org.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "Unit Test", indicating it focuses on testing individual tubes in isolation.
 *
 * <p>Unit tests have the following characteristics:
 *
 * <ul>
 *   <li>Fast - They run quickly and provide immediate feedback
 *   <li>Isolated - They test a single unit of functionality
 *   <li>Repeatable - They always produce the same result for the same input
 *   <li>Self-validating - They determine on their own whether they pass or fail
 * </ul>
 *
 * <p>In traditional testing terminology, unit tests verify individual methods or classes. In
 * Samstraumr, they test individual tubes in isolation with dependencies mocked or stubbed.
 *
 * <p>Examples of unit tests include:
 *
 * <ul>
 *   <li>Tube creation and initialization
 *   <li>Basic tube functionality
 *   <li>Tube state transitions
 *   <li>Individual tube behaviors
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Unit")
@Tag("Tube")
public @interface UnitTest {}
