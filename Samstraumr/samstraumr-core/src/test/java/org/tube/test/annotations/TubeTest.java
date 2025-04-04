package org.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "Tube Test", indicating it focuses on testing individual tubes in isolation
 * (unit tests).
 *
 * <p>Tube Tests have the following characteristics:
 *
 * <ul>
 *   <li>Unit level - They test a single tube component in isolation
 *   <li>Focused - They verify specific tube functionality
 *   <li>Fast - They execute quickly as they don't involve external dependencies
 * </ul>
 *
 * <p>In TBD terminology, Tube Tests align with Atomic Boundary Testing (ABT).
 *
 * <p>Examples of Tube Tests include:
 *
 * <ul>
 *   <li>Tube initialization tests
 *   <li>Tube identity tests
 *   <li>Tube state management tests
 *   <li>Tube logging tests
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("TubeTest")
public @interface TubeTest {}
