import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.composite.Composite;
import org.samstraumr.tube.composite.CompositeFactory;
import org.samstraumr.tube.test.annotations.BelowTheLine;
import org.samstraumr.tube.test.annotations.CompositeTest;

/**
 * Unit tests for the Composite class and CompositeFactory.
 *
 * <p>These are Below The Line (BTL) tests that provide comprehensive coverage of composite
 * functionality but are not critical for every build.
 */
@CompositeTest
@BelowTheLine
public class CompositeTestSuite {

  @Test
  public void testBasicCompositeCreation() {
    Environment env = new Environment();
    Composite composite = new Composite("test-composite", env);

    assertEquals("test-composite", composite.getCompositeId());
    assertTrue(composite.isActive());
    assertTrue(composite.getTubes().isEmpty()); // Initially, no tubes have been added
  }

  @Test
  public void testCompositeWithTubes() {
    Environment env = new Environment();
    Composite composite = new Composite("test-composite", env);

    // Add tubes
    composite.createTube("source", "Source Tube").createTube("sink", "Sink Tube");

    assertEquals(2, composite.getTubes().size());
    assertNotNull(composite.getTube("source"));
    assertNotNull(composite.getTube("sink"));
  }

  @Test
  public void testCompositeConnections() {
    Environment env = new Environment();
    Composite composite = new Composite("connection-test", env);

    // Add tubes
    composite
        .createTube("source", "Source Tube")
        .createTube("middle", "Middle Tube")
        .createTube("sink", "Sink Tube");

    // Connect tubes
    composite.connect("source", "middle").connect("middle", "sink");

    // Check connections
    Map<String, List<String>> connections = composite.getConnections();
    assertEquals(2, connections.size());
    assertTrue(connections.containsKey("source"));
    assertTrue(connections.containsKey("middle"));
    assertEquals("middle", connections.get("source").get(0));
    assertEquals("sink", connections.get("middle").get(0));
  }

  @Test
  public void testTransformationComposite() {
    Environment env = new Environment();
    Composite composite = CompositeFactory.createTransformationComposite(env);

    // Add a simple uppercase transformer
    composite.addTransformer("transformer", (Function<String, String>) String::toUpperCase);

    // Process data
    Optional<String> result = composite.process("source", "test data");

    assertTrue(result.isPresent());
    assertEquals("TEST DATA", result.get());
  }

  @Test
  public void testValidationComposite() {
    Environment env = new Environment();
    Composite composite = CompositeFactory.createValidationComposite(env);

    // Add a length validator
    composite.addValidator("validator", (Function<String, Boolean>) s -> s.length() > 5);

    // Process valid data
    Optional<String> validResult = composite.process("processor", "valid data");
    assertTrue(validResult.isPresent());

    // Process invalid data
    Optional<String> invalidResult = composite.process("processor", "short");
    assertFalse(invalidResult.isPresent());
  }

  @Test
  public void testCircuitBreaker() {
    Environment env = new Environment();
    Composite composite = new Composite("circuit-test", env);

    // Add tube with circuit breaker
    composite.createTube("processor", "Processor Tube");
    composite.enableCircuitBreaker("processor", 2, 1000);

    // Add transformer that fails
    composite.addTransformer(
        "processor",
        (Function<String, String>)
            s -> {
              if (s.contains("fail")) {
                throw new RuntimeException("Simulated failure");
              }
              return s.toUpperCase();
            });

    // Process valid data
    Optional<String> validResult = composite.process("processor", "valid data");
    assertTrue(validResult.isPresent());

    // Process failing data twice to open circuit
    composite.process("processor", "fail1");
    composite.process("processor", "fail2");

    // Now valid data should be rejected due to open circuit
    Optional<String> rejectedResult = composite.process("processor", "should be rejected");
    assertFalse(rejectedResult.isPresent());
  }

  @Test
  public void testEventLogging() {
    Environment env = new Environment();
    Composite composite = new Composite("logging-test", env);

    // Add a source tube
    composite.createTube("source", "Source Tube");

    // Process data - should log but not complete successfully
    Optional<?> result = composite.process("source", "test data");

    // Since source is a terminal tube (no connections), it should return the data
    assertTrue(result.isPresent());

    // Verify events were logged
    List<Composite.CompositeEvent> events = composite.getEventLog();
    assertFalse(events.isEmpty());

    // Check log contains expected messages
    boolean foundStartEvent =
        events.stream().anyMatch(e -> e.getDescription().contains("Starting data processing"));

    boolean foundCompletionEvent =
        events.stream().anyMatch(e -> e.getDescription().contains("Data processing completed"));

    assertTrue(foundStartEvent, "Should log start of processing");
    assertTrue(foundCompletionEvent, "Should log completion of processing");
  }
}
