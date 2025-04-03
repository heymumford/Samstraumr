package org.samstraumr.tube;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.samstraumr.tube.test.annotations.FlowTest;

/**
 * Flow Tests focus on data flowing through a single tube. These tests follow the integration
 * testing approach, verifying data transformation, validation, and state tracking.
 */
@FlowTest
@ExtendWith(MockitoExtension.class)
public class FlowTestSuite {

  private static final String TEST_REASON = "Test data flow";

  @Mock private Environment mockEnvironment;

  private Tube tube;

  @BeforeEach
  void setUp() {
    // Configure mock environment
    when(mockEnvironment.getParameters()).thenReturn("{\"hostname\":\"flow-test-host\"}");
    when(mockEnvironment.getEnvironmentHash()).thenReturn("flow-test-hash");

    // Create tube for testing
    tube = Tube.create(TEST_REASON, mockEnvironment);
  }

  @Test
  @DisplayName("Data should flow through tube processor")
  void dataShouldFlowThroughProcessor() {
    // Given
    String inputData = "test input";
    String expectedOutput = "TEST INPUT";

    // When - Create a processor that transforms data
    Function<String, String> processor = String::toUpperCase;

    // Then - Process data and verify results
    String result = processDataThroughTube(tube, inputData, processor);
    assertEquals(expectedOutput, result);

    // Verify tube logged the data flow
    assertTrue(
        tube.queryMimirLog().stream()
            .anyMatch(entry -> entry.contains("Data processing") && entry.contains("completed")));
  }

  @ParameterizedTest
  @CsvSource({
    "empty string,,EMPTY",
    "lowercase string,hello,HELLO",
    "mixed case string,Hello World,HELLO WORLD"
  })
  @DisplayName("Tube should consistently process different inputs")
  void shouldProcessDifferentInputs(String description, String input, String expected) {
    // Given - Handle null input for empty string test case
    String actualInput = input == null ? "" : input;
    String expectedOutput = "EMPTY".equals(expected) ? "" : expected;

    // When
    Function<String, String> processor = s -> s.toUpperCase();
    String result = processDataThroughTube(tube, actualInput, processor);

    // Then
    assertEquals(expectedOutput, result);
  }

  @Test
  @DisplayName("Tube should handle error in data flow")
  void shouldHandleProcessingError() {
    // Given
    String inputData = "invalid data";

    // When - Create a processor that throws an exception
    Function<String, String> errorProcessor =
        data -> {
          throw new RuntimeException("Simulated processing error");
        };

    // Then - Process should capture and log error
    Exception exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              processDataThroughTube(tube, inputData, errorProcessor);
            });

    assertTrue(exception.getMessage().contains("Simulated processing error"));

    // Verify tube logged the error
    assertTrue(
        tube.queryMimirLog().stream()
            .anyMatch(entry -> entry.contains("error") || entry.contains("Error")));
  }

  @Test
  @DisplayName("Tube should track data lineage")
  void shouldTrackDataLineage() {
    // Given
    String inputData = "source data";
    String transformReason = "uppercase transformation";

    // When - Process with lineage tracking
    String result = processDataWithLineage(tube, inputData, String::toUpperCase, transformReason);

    // Then
    assertEquals("SOURCE DATA", result);

    // Verify lineage was tracked
    assertTrue(tube.getLineage().contains(transformReason));
    assertTrue(tube.getLineage().size() > 1); // Original reason + transform reason
  }

  // Helper method to simulate data processing through a tube
  private <T, R> R processDataThroughTube(Tube tube, T input, Function<T, R> processor) {
    tube.logToMimir("Starting data processing: " + input);
    try {
      R result = processor.apply(input);
      tube.logToMimir("Data processing completed: " + result);
      return result;
    } catch (Exception e) {
      tube.logToMimir("Error processing data: " + e.getMessage());
      throw e;
    }
  }

  // Helper method to simulate data processing with lineage tracking
  private <T, R> R processDataWithLineage(
      Tube tube, T input, Function<T, R> processor, String reason) {
    // Add to tube lineage
    tube.addToLineage(reason);

    // Process data
    return processDataThroughTube(tube, input, processor);
  }
}
