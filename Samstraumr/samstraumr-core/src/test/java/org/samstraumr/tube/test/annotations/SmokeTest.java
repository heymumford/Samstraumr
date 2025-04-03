package org.samstraumr.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as a "Smoke Test", indicating it focuses on verifying that the basic
 * functionality of the system works correctly.
 *
 * <p>Smoke Tests have the following characteristics:
 *
 * <ul>
 *   <li>First-line verification - They run before other tests
 *   <li>Basic functionality - They verify core features work
 *   <li>Fast - They run quickly to provide rapid feedback
 * </ul>
 *
 * <p>In traditional testing terminology, smoke tests verify that the basic functionality
 * works correctly. In Samstraumr, they align with Orchestration Tests that verify the
 * system assembles and initializes correctly.
 *
 * <p>Examples of smoke tests include:
 *
 * <ul>
 *   <li>System startup tests
 *   <li>Basic feature verification
 *   <li>Core functionality tests
 *   <li>System assembly verification
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Smoke")
@Tag("Orchestration")
public @interface SmokeTest {}