package org.s8r.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as "Below The Line" (BTL), indicating it is a non-critical test that should pass but
 * doesn't block the build.
 *
 * <p>BTL tests have the following characteristics:
 *
 * <ul>
 *   <li>More comprehensive - They verify edge cases and error conditions
 *   <li>Slower - They might take longer to execute than ATL tests
 *   <li>Important but not blocking - They provide valuable verification but don't block the build
 * </ul>
 *
 * <p>BTL tests are executed in a separate pipeline or as part of a nightly build process. They
 * complement ATL tests by providing more comprehensive verification of the system.
 *
 * <p>Examples of BTL tests include:
 *
 * <ul>
 *   <li>Performance and stress tests
 *   <li>Exhaustive edge cases
 *   <li>Less common user scenarios
 *   <li>Integration tests with external systems
 * </ul>
 *
 * <p>NOTE: As of version 1.3.1, BTL tests have been disabled due to brittleness. This annotation is
 * maintained for backwards compatibility but tests will be skipped unless explicitly enabled.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("BTL")
public @interface BTL {}