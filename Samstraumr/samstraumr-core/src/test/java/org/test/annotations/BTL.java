package org.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as "Below The Line" (BTL), indicating it is an important but non-blocking test that
 * can run separately from the main build pipeline.
 *
 * <p>BTL tests have the following characteristics:
 *
 * <ul>
 *   <li>May be slower - They may take longer to execute
 *   <li>More complex - They often test edge cases or rare scenarios
 *   <li>Lower priority - They don't block the build if failing
 *   <li>Comprehensive - They provide broader coverage of the system
 * </ul>
 *
 * <p>BTL tests are executed in separate processes (nightly builds, separate CI jobs) to provide
 * comprehensive coverage without blocking development.
 *
 * <p>Examples of BTL tests include:
 *
 * <ul>
 *   <li>Edge cases and boundary tests
 *   <li>Performance and stress tests
 *   <li>Rare user scenarios
 *   <li>Resource-intensive tests
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("BTL")
public @interface BTL {}
