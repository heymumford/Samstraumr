/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.domain.component.composite;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.s8r.domain.identity.ComponentId;
import org.s8r.test.annotation.UnitTest;

/**
 * Tests for the CompositeFactory domain factory class.
 *
 * <p>These tests validate the behavior of the factory methods that create different types of
 * composite components according to their patterns and configurations.
 */
@UnitTest
@DisplayName("CompositeFactory Domain Factory Tests")
class CompositeFactoryTest {

  private static final String TEST_REASON = "Test creation reason";

  @Nested
  @DisplayName("Specific Composite Factory Method Tests")
  class SpecificFactoryMethodTests {

    @Test
    @DisplayName("createStandardComposite() should create a standard composite")
    void createStandardCompositeShouldCreateStandardComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createStandardComposite(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.STANDARD, composite.getCompositeType(), "Type should be STANDARD");
      assertEquals(TEST_REASON, composite.getId().getReason(), "Reason should match");
    }

    @Test
    @DisplayName("createPipeline() should create a pipeline composite")
    void createPipelineShouldCreatePipelineComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createPipeline(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.PIPELINE, composite.getCompositeType(), "Type should be PIPELINE");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("Pipeline"), "Reason should include composite type");
    }

    @Test
    @DisplayName("createObserver() should create an observer composite")
    void createObserverShouldCreateObserverComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createObserver(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.OBSERVER, composite.getCompositeType(), "Type should be OBSERVER");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("Observer"), "Reason should include composite type");
    }

    @Test
    @DisplayName("createTransformer() should create a transformer composite")
    void createTransformerShouldCreateTransformerComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createTransformer(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.TRANSFORMER, composite.getCompositeType(), "Type should be TRANSFORMER");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("Transformer"), "Reason should include composite type");
    }

    @Test
    @DisplayName("createValidator() should create a validator composite")
    void createValidatorShouldCreateValidatorComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createValidator(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.VALIDATOR, composite.getCompositeType(), "Type should be VALIDATOR");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("Validator"), "Reason should include composite type");
    }

    @Test
    @DisplayName("createCircuitBreaker() should create a circuit breaker composite")
    void createCircuitBreakerShouldCreateCircuitBreakerComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createCircuitBreaker(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.CIRCUIT_BREAKER, composite.getCompositeType(), "Type should be CIRCUIT_BREAKER");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("CircuitBreaker"), "Reason should include composite type");
    }

    @Test
    @DisplayName("createMediator() should create a mediator composite")
    void createMediatorShouldCreateMediatorComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createMediator(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.MEDIATOR, composite.getCompositeType(), "Type should be MEDIATOR");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("Mediator"), "Reason should include composite type");
    }

    @Test
    @DisplayName("createAdapter() should create an adapter composite")
    void createAdapterShouldCreateAdapterComposite() {
      // When
      CompositeComponent composite = CompositeFactory.createAdapter(TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(CompositeType.ADAPTER, composite.getCompositeType(), "Type should be ADAPTER");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains("Adapter"), "Reason should include composite type");
    }
  }

  @Nested
  @DisplayName("Generic Composite Factory Method Tests")
  class GenericFactoryMethodTests {

    @ParameterizedTest
    @EnumSource(CompositeType.class)
    @DisplayName("createComposite() should create a composite of any specified type")
    void createCompositeShouldCreateSpecifiedType(CompositeType type) {
      // When
      CompositeComponent composite = CompositeFactory.createComposite(type, TEST_REASON);

      // Then
      assertNotNull(composite, "Composite should be created");
      assertEquals(type, composite.getCompositeType(), "Type should match specified type");
      assertTrue(composite.getId().getReason().contains(TEST_REASON), "Reason should contain original reason");
      assertTrue(composite.getId().getReason().contains(type.name()), "Reason should include composite type name");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "  \t\n  "})
    @DisplayName("createComposite() should handle empty or blank reasons")
    void createCompositeShouldHandleEmptyReasons(String reason) {
      // When
      CompositeComponent composite = CompositeFactory.createComposite(CompositeType.STANDARD, reason);

      // Then
      assertNotNull(composite, "Composite should be created with empty/blank reason");
      assertEquals(CompositeType.STANDARD, composite.getCompositeType(), "Type should be correct");
      assertTrue(composite.getId().getReason().contains(reason), "Reason should contain original reason");
    }

    @Test
    @DisplayName("createComposite() should handle null CompositeType")
    void createCompositeShouldHandleNullType() {
      // Then
      assertThrows(
          NullPointerException.class,
          () -> CompositeFactory.createComposite(null, TEST_REASON),
          "Should throw NullPointerException for null type");
    }
  }

  @Nested
  @DisplayName("Integration with CompositeComponent Tests")
  class IntegrationTests {

    @Test
    @DisplayName("factory created components should be in the correct initial state")
    void factoryComponentsShouldBeInCorrectInitialState() {
      // When
      CompositeComponent composite = CompositeFactory.createStandardComposite(TEST_REASON);

      // Then - verify the composite is properly initialized
      assertNotNull(composite.getId(), "ID should be set");
      assertEquals(0, composite.getComponents().size(), "Should have no children initially");
      assertEquals(0, composite.getConnections().size(), "Should have no connections initially");
    }
  }
}