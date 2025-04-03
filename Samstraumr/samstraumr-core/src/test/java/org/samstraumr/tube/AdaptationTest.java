/*
Filename: AdaptationTest.java
Purpose: Property tests for tube behavior under changing conditions.
Goals:
  - Verify that tubes maintain key properties under changing inputs and conditions
  - Test resilience of tubes to handle a wide range of inputs automatically
  - Ensure adaptive behaviors respond correctly to environmental changes
Dependencies:
  - JUnit 5 for test framework
  - JUnit Params for property testing
  - Tube.java for component under test
Assumptions:
  - Property-based tests run deterministically
  - Tests capture edge cases that might not be caught in directed testing
*/

package org.samstraumr.tube;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.samstraumr.tube.annotations.AdaptationTest;

/**
 * Adaptation Tests focus on tube behavior under changing conditions.
 * These property-based tests verify that key properties hold true across
 * a wide range of inputs and environmental conditions.
 */
@AdaptationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdaptationTest {

    private static final String TEST_REASON = "Test adaptation";

    /**
     * Tests that tube IDs remain unique across a large number of instances.
     */
    @RepeatedTest(100)
    @DisplayName("Tube IDs should be unique across many instances")
    void tubeIdsShouldBeUnique() {
        Environment env1 = new Environment();
        Environment env2 = new Environment();
        
        Tube tube1 = Tube.create(TEST_REASON, env1);
        Tube tube2 = Tube.create(TEST_REASON, env2);
        
        assertNotEquals(tube1.getUniqueId(), tube2.getUniqueId());
    }
    
    /**
     * Tests that tubes can handle variable delay settings.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 30, 60, 120, 300})
    @DisplayName("Tube should accept different termination delays")
    void shouldHandleVariableDelays(int seconds) {
        Environment env = new Environment();
        Tube tube = Tube.create(TEST_REASON, env);
        
        tube.setTerminationDelay(seconds);
        
        // Verify the delay was set
        assertTrue(tube.queryMimirLog().stream()
            .anyMatch(entry -> entry.contains("termination delay") && 
                               entry.contains(String.valueOf(seconds))));
    }
    
    /**
     * Tests that tubes are resilient to high-frequency operations.
     */
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("Tube should handle high-frequency operations")
    void shouldHandleHighFrequencyOperations() {
        Environment env = new Environment();
        Tube tube = Tube.create(TEST_REASON, env);
        
        // Perform a large number of rapid logging operations
        for (int i = 0; i < 1000; i++) {
            tube.logToMimir("Test message " + i);
        }
        
        // Verify logs were recorded and performance remained acceptable
        assertTrue(tube.queryMimirLog().size() >= 1000);
    }
    
    /**
     * Tests that tube lineage maintains integrity with variable content.
     */
    @ParameterizedTest
    @ArgumentsSource(RandomStringProvider.class)
    @DisplayName("Tube lineage should maintain integrity with variable content")
    void lineageShouldMaintainIntegrity(String lineageEntry) {
        Environment env = new Environment();
        Tube tube = Tube.create(TEST_REASON, env);
        
        // Add the random lineage entry
        tube.addToLineage(lineageEntry);
        
        // Verify lineage contains the entry
        assertTrue(tube.getLineage().contains(lineageEntry));
        assertTrue(tube.getLineage().size() > 1); // Original reason + new entry
    }
    
    /**
     * Tests that tubes reject invalid termination delays.
     */
    @ParameterizedTest
    @ValueSource(ints = {-10, -1, 0})
    @DisplayName("Tube should reject invalid termination delays")
    void shouldRejectInvalidDelays(int seconds) {
        Environment env = new Environment();
        Tube tube = Tube.create(TEST_REASON, env);
        
        // Expect an exception for invalid delays
        assertThrows(IllegalArgumentException.class, () -> {
            tube.setTerminationDelay(seconds);
        });
    }
    
    /**
     * Tests that tubes maintain their properties under stress.
     */
    @Test
    @DisplayName("Tube should maintain properties under stress")
    void shouldMaintainPropertiesUnderStress() {
        Environment env = new Environment();
        Tube tube = Tube.create(TEST_REASON, env);
        String initialId = tube.getUniqueId();
        
        // Perform a variety of operations rapidly
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            int operation = random.nextInt(3);
            switch (operation) {
                case 0:
                    tube.logToMimir("Random log entry " + random.nextInt(1000));
                    break;
                case 1:
                    tube.addToLineage("Random lineage entry " + random.nextInt(1000));
                    break;
                case 2:
                    tube.setTerminationDelay(random.nextInt(100) + 1);
                    break;
            }
        }
        
        // Verify key properties are maintained
        assertEquals(initialId, tube.getUniqueId());
        assertTrue(tube.getLineage().contains(TEST_REASON));
        assertTrue(tube.queryMimirLog().size() > 0);
    }
    
    /**
     * Provider for random string test inputs.
     */
    static class RandomStringProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            Random random = new Random();
            return Stream.of(
                Arguments.of(""), // Empty string
                Arguments.of("Short"), // Short string
                Arguments.of("This is a somewhat longer string for testing"), // Medium string
                Arguments.of("A".repeat(100)), // Long repeated string
                Arguments.of(generateRandomString(random, 50)), // Random string
                Arguments.of(generateRandomString(random, 200)), // Longer random string
                Arguments.of("Special $#@*&^ characters!"), // Special characters
                Arguments.of("Line1\nLine2\nLine3") // Multi-line string
            );
        }
        
        private String generateRandomString(Random random, int length) {
            int leftLimit = 32; // space
            int rightLimit = 126; // '~'
            return random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        }
    }
}