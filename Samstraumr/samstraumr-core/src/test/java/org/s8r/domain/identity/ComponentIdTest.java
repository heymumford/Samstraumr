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

package org.s8r.domain.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.s8r.test.annotation.UnitTest;

/**
 * Unit tests for ComponentId value object.
 * 
 * Testing the core domain entity for component identity that represents a crucial part 
 * of the hierarchical identity system in the Samstraumr framework.
 */
@UnitTest
@DisplayName("ComponentId Domain Value Object Tests")
class ComponentIdTest {

    private static final String TEST_REASON = "Test Component Creation";
    private static final String TEST_UUID = "f47ac10b-58cc-4372-a567-0e02b2c3d479";
    
    @Nested
    @DisplayName("Factory Methods Tests")
    class FactoryMethodsTests {
        
        @Test
        @DisplayName("create() should generate a new ComponentId with random UUID")
        void createShouldGenerateComponentIdWithRandomUUID() {
            // When
            ComponentId id1 = ComponentId.create(TEST_REASON);
            ComponentId id2 = ComponentId.create(TEST_REASON);
            
            // Then
            assertNotNull(id1, "ComponentId should not be null");
            assertNotNull(id2, "ComponentId should not be null");
            assertNotEquals(id1, id2, "Two generated IDs should be different");
            assertEquals(TEST_REASON, id1.getReason(), "Reason should match the provided value");
            assertNotNull(id1.getCreationTime(), "Creation time should be set");
            assertTrue(id1.getLineage().isEmpty(), "Lineage should be empty");
        }
        
        @Test
        @DisplayName("create() with lineage should generate a new ComponentId with the specified lineage")
        void createWithLineageShouldSetLineage() {
            // Given
            List<String> lineage = Arrays.asList(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );
            
            // When
            ComponentId id = ComponentId.create(TEST_REASON, lineage);
            
            // Then
            assertNotNull(id, "ComponentId should not be null");
            assertEquals(TEST_REASON, id.getReason(), "Reason should match the provided value");
            assertEquals(lineage, id.getLineage(), "Lineage should match the provided list");
            assertNotNull(id.getParentId(), "Parent ID should be extracted from lineage");
            assertEquals(UUID.fromString(lineage.get(1)), id.getParentId(), 
                    "Parent ID should match the last item in lineage");
        }
        
        @Test
        @DisplayName("fromString() should create ComponentId from valid UUID string")
        void fromStringShouldCreateComponentIdFromValidUUID() {
            // When
            ComponentId id = ComponentId.fromString(TEST_UUID, TEST_REASON);
            
            // Then
            assertNotNull(id, "ComponentId should not be null");
            assertEquals(UUID.fromString(TEST_UUID), id.getValue(), "UUID should match input string");
            assertEquals(TEST_REASON, id.getReason(), "Reason should match the provided value");
            assertTrue(id.getLineage().isEmpty(), "Lineage should be empty");
        }
        
        @Test
        @DisplayName("fromString() should throw exception for invalid UUID")
        void fromStringShouldThrowExceptionForInvalidUUID() {
            // Given
            String invalidUUID = "not-a-uuid";
            
            // When/Then
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> ComponentId.fromString(invalidUUID, TEST_REASON),
                    "Should throw IllegalArgumentException for invalid UUID"
            );
            
            assertTrue(exception.getMessage().contains("Invalid UUID format"), 
                    "Exception message should contain information about invalid format");
        }
        
        @Test
        @DisplayName("fromValues() should create ComponentId with provided values")
        void fromValuesShouldCreateComponentIdWithProvidedValues() {
            // Given
            List<String> lineage = Arrays.asList(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );
            
            // When
            ComponentId id = ComponentId.fromValues(TEST_UUID, TEST_REASON, lineage);
            
            // Then
            assertNotNull(id, "ComponentId should not be null");
            assertEquals(UUID.fromString(TEST_UUID), id.getValue(), "UUID should match input string");
            assertEquals(TEST_REASON, id.getReason(), "Reason should match the provided value");
            assertEquals(lineage, id.getLineage(), "Lineage should match the provided list");
            assertNotNull(id.getParentId(), "Parent ID should be extracted from lineage");
            assertEquals(UUID.fromString(lineage.get(1)), id.getParentId(), 
                    "Parent ID should match the last item in lineage");
        }
    }
    
    @Nested
    @DisplayName("Value Object Behavior Tests")
    class ValueObjectBehaviorTests {
        
        @Test
        @DisplayName("equals() should return true for ComponentIds with the same UUID")
        void equalsShouldReturnTrueForSameUUID() {
            // Given
            ComponentId id1 = ComponentId.fromString(TEST_UUID, "First reason");
            ComponentId id2 = ComponentId.fromString(TEST_UUID, "Second reason");
            
            // Then
            assertEquals(id1, id2, "ComponentIds with same UUID should be equal");
            assertEquals(id1.hashCode(), id2.hashCode(), "Equal objects should have equal hash codes");
        }
        
        @Test
        @DisplayName("equals() should return false for ComponentIds with different UUIDs")
        void equalsShouldReturnFalseForDifferentUUIDs() {
            // Given
            ComponentId id1 = ComponentId.create(TEST_REASON);
            ComponentId id2 = ComponentId.create(TEST_REASON);
            
            // Then
            assertNotEquals(id1, id2, "ComponentIds with different UUIDs should not be equal");
        }
        
        @Test
        @DisplayName("toString() should include id, reason, and creation time")
        void toStringShouldIncludeIdReasonAndCreationTime() {
            // Given
            ComponentId id = ComponentId.create(TEST_REASON);
            
            // When
            String string = id.toString();
            
            // Then
            assertTrue(string.contains(id.getIdString()), "toString() should contain the ID");
            assertTrue(string.contains(TEST_REASON), "toString() should contain the reason");
            assertTrue(string.contains("creationTime"), "toString() should contain creation time");
        }
        
        @Test
        @DisplayName("toAddress() should return formatted address representation")
        void toAddressShouldReturnFormattedRepresentation() {
            // Given
            ComponentId id = ComponentId.fromString(TEST_UUID, TEST_REASON);
            String shortId = id.getShortId();
            
            // When
            String address = id.toAddress();
            
            // Then
            assertEquals("CO<" + shortId + ">", address, "toAddress() should return formatted string");
        }
    }
    
    @Nested
    @DisplayName("Accessor Methods Tests")
    class AccessorMethodsTests {
        
        @Test
        @DisplayName("getShortId() should return first 8 characters of UUID")
        void getShortIdShouldReturnFirst8Characters() {
            // Given
            ComponentId id = ComponentId.fromString(TEST_UUID, TEST_REASON);
            
            // When
            String shortId = id.getShortId();
            
            // Then
            assertEquals(TEST_UUID.substring(0, 8), shortId, "Short ID should be first 8 characters of UUID");
        }
        
        @Test
        @DisplayName("getLineage() should return unmodifiable list")
        void getLineageShouldReturnUnmodifiableList() {
            // Given
            List<String> initialLineage = Arrays.asList(
                    UUID.randomUUID().toString(),
                    UUID.randomUUID().toString()
            );
            ComponentId id = ComponentId.create(TEST_REASON, initialLineage);
            
            // When
            List<String> lineage = id.getLineage();
            
            // Then
            assertThrows(UnsupportedOperationException.class, 
                    () -> lineage.add("new-entry"), 
                    "Should not be able to modify returned lineage");
        }
        
        @Test
        @DisplayName("getParentId() should return null for root components")
        void getParentIdShouldReturnNullForRootComponents() {
            // Given
            ComponentId id = ComponentId.create(TEST_REASON);
            
            // Then
            assertEquals(null, id.getParentId(), "Parent ID should be null for root components");
        }
    }
    
    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Invalid UUID in lineage should not fail object construction")
        void invalidUUIDInLineageShouldNotFailConstruction() {
            // Given
            List<String> lineageWithInvalidUUID = Arrays.asList(
                    UUID.randomUUID().toString(),
                    "not-a-uuid"
            );
            
            // When
            ComponentId id = ComponentId.create(TEST_REASON, lineageWithInvalidUUID);
            
            // Then
            assertNotNull(id, "ComponentId should still be created");
            assertEquals(null, id.getParentId(), "Parent ID should be null when lineage has invalid UUID");
        }
        
        @Test
        @DisplayName("Empty lineage should be handled properly")
        void emptyLineageShouldBeHandledProperly() {
            // Given
            List<String> emptyLineage = Collections.emptyList();
            
            // When
            ComponentId id = ComponentId.create(TEST_REASON, emptyLineage);
            
            // Then
            assertNotNull(id, "ComponentId should be created with empty lineage");
            assertTrue(id.getLineage().isEmpty(), "Returned lineage should be empty");
            assertEquals(null, id.getParentId(), "Parent ID should be null with empty lineage");
        }
    }
}