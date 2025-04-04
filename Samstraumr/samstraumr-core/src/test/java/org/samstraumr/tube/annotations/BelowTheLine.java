package org.samstraumr.tube.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Legacy annotation for "Below The Line" tests.
 *
 * <p><strong>NOTE: BTL test implementations have been temporarily removed (v1.3.1) due to
 * brittleness and integration issues. The BTL infrastructure is preserved for future
 * reimplementation.</strong>
 *
 * @deprecated Use {@link BTL} instead
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("BTL")
@Deprecated
public @interface BelowTheLine {}

