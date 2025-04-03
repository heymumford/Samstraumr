/*
Filename: AcceptanceTest.java
Purpose: Defines the AcceptanceTest annotation for the Samstraumr testing strategy.
*/

package org.samstraumr.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test class as an Acceptance Test (business test). These tests focus on business
 * requirements using Cucumber.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AcceptanceTest {}
