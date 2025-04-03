package org.samstraumr.tube.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

/**
 * Marks a test as an "API Test", indicating it focuses on testing public interfaces
 * and contracts.
 *
 * <p>API Tests have the following characteristics:
 *
 * <ul>
 *   <li>Contract verification - They ensure the API adheres to its specification
 *   <li>Interface focused - They test the public interface, not the implementation
 *   <li>Boundary testing - They verify behavior at system boundaries
 * </ul>
 *
 * <p>In traditional testing terminology, API/contract tests verify that components
 * fulfill their contracts. In Samstraumr, they align with Machine Tests that verify
 * machines (collections of tubes) adhere to their interfaces.
 *
 * <p>Examples of API tests include:
 *
 * <ul>
 *   <li>API behavior validation
 *   <li>Contract adherence verification
 *   <li>Machine interface testing
 *   <li>Protocol compliance verification
 * </ul>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag("Api")
@Tag("Machine")
public @interface ApiTest {}