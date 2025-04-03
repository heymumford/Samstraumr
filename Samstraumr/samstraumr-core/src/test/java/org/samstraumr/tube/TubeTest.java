/*
Filename: TubeTest.java
Purpose: Unit tests for the Tube class, focusing on atomic behavior and functionality.
Goals:
  - Ensure that tubes initialize correctly with proper identifiers
  - Verify tube state transitions and lifecycle management
  - Test tube unique identifier generation and immutability
Dependencies:
  - JUnit 5 for test framework
  - Tube.java and Environment.java for the components under test
Assumptions:
  - Tests run in isolation without external system dependencies
  - Environment mock provides consistent responses for reproducible tests
*/

package org.samstraumr.tube;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Tube Tests focus on the core functionality of individual tubes.
 * These tests follow the unit testing approach, verifying atomic behaviors
 * in isolation from other components.
 */
@TubeTest
@ExtendWith(MockitoExtension.class)
public class TubeTest {

    private static final String TEST_REASON = "Test initialization";
    
    @Mock
    private Environment mockEnvironment;
    
    @BeforeEach
    void setUp() {
        // Configure mock environment
        when(mockEnvironment.getParameters()).thenReturn("{\"hostname\":\"test-host\"}");
        when(mockEnvironment.getEnvironmentHash()).thenReturn("test-hash");
    }
    
    @Test
    @DisplayName("Tube should initialize with unique ID")
    void shouldInitializeWithUniqueIdentifier() {
        // When
        Tube tube = Tube.create(TEST_REASON, mockEnvironment);
        
        // Then
        assertNotNull(tube.getUniqueId());
        assertTrue(tube.getUniqueId().length() > 0);
    }
    
    @Test
    @DisplayName("Tube should log environment details during initialization")
    void shouldLogEnvironmentDetails() {
        // When
        Tube tube = Tube.create(TEST_REASON, mockEnvironment);
        
        // Then
        List<String> log = tube.queryMimirLog();
        assertTrue(log.stream().anyMatch(entry -> entry.contains("Environment:")));
        assertTrue(log.stream().anyMatch(entry -> entry.contains("test-host")));
    }
    
    @Test
    @DisplayName("Tube should maintain immutable unique ID")
    void shouldMaintainImmutableIdentifier() {
        // Given
        Tube tube = Tube.create(TEST_REASON, mockEnvironment);
        String initialId = tube.getUniqueId();
        
        // When - perform operations that might affect state
        tube.setTerminationDelay(100);
        
        // Then - ID should remain the same
        assertEquals(initialId, tube.getUniqueId());
    }
    
    @ParameterizedTest
    @ValueSource(ints = {10, 60, 120})
    @DisplayName("Tube should accept different termination delays")
    void shouldAcceptTerminationDelays(int seconds) {
        // Given
        Tube tube = Tube.create(TEST_REASON, mockEnvironment);
        
        // When
        tube.setTerminationDelay(seconds);
        
        // Then
        List<String> log = tube.queryMimirLog();
        assertTrue(log.stream().anyMatch(entry -> 
            entry.contains("termination delay") && entry.contains(String.valueOf(seconds))));
    }
    
    @Test
    @DisplayName("Tube should reject null environment")
    void shouldRejectNullEnvironment() {
        // When/Then
        Exception exception = assertThrows(Tube.TubeInitializationException.class, () -> {
            Tube.create(TEST_REASON, null);
        });
        
        assertTrue(exception.getMessage().contains("Environment cannot be null"));
    }
    
    @Test
    @DisplayName("Tube should reject null reason")
    void shouldRejectNullReason() {
        // When/Then
        Exception exception = assertThrows(Tube.TubeInitializationException.class, () -> {
            Tube.create(null, mockEnvironment);
        });
        
        assertTrue(exception.getMessage().contains("Reason cannot be null"));
    }
}