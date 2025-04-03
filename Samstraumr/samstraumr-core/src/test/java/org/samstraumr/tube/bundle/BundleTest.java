import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.test.annotations.BelowTheLine;
import org.samstraumr.tube.test.annotations.BundleTest;

/**
 * Unit tests for the Bundle class and BundleFactory.
 *
 * <p>These are Below The Line (BTL) tests that provide comprehensive coverage of bundle
 * functionality but are not critical for every build.
 */
@BundleTest
@BelowTheLine
public class BundleTest {

  @Test
  public void testBasicBundleCreation() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("test-bundle", env);

    assertEquals("test-bundle", bundle.getBundleId());
    assertTrue(bundle.isActive());
    assertTrue(bundle.getTubes().isEmpty()); // Initially, no tubes have been added
  }

  @Test
  public void testBundleWithTubes() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("test-bundle", env);

    // Add tubes
    bundle.createTube("source", "Source Tube").createTube("sink", "Sink Tube");

    assertEquals(2, bundle.getTubes().size());
    assertNotNull(bundle.getTube("source"));
    assertNotNull(bundle.getTube("sink"));
  }

  @Test
  public void testBundleConnections() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("connection-test", env);

    // Add tubes
    bundle
        .createTube("source", "Source Tube")
        .createTube("middle", "Middle Tube")
        .createTube("sink", "Sink Tube");

    // Connect tubes
    bundle.connect("source", "middle").connect("middle", "sink");

    // Check connections
    Map<String, List<String>> connections = bundle.getConnections();
    assertEquals(2, connections.size());
    assertTrue(connections.containsKey("source"));
    assertTrue(connections.containsKey("middle"));
    assertEquals("middle", connections.get("source").get(0));
    assertEquals("sink", connections.get("middle").get(0));
  }

  @Test
  public void testTransformationBundle() {
    Environment env = new Environment();
    Bundle bundle = BundleFactory.createTransformationBundle(env);

    // Add a simple uppercase transformer
    bundle.addTransformer("transformer", (Function<String, String>) String::toUpperCase);

    // Process data
    Optional<String> result = bundle.process("source", "test data");

    assertTrue(result.isPresent());
    assertEquals("TEST DATA", result.get());
  }

  @Test
  public void testValidationBundle() {
    Environment env = new Environment();
    Bundle bundle = BundleFactory.createValidationBundle(env);

    // Add a length validator
    bundle.addValidator("validator", (Function<String, Boolean>) s -> s.length() > 5);

    // Process valid data
    Optional<String> validResult = bundle.process("processor", "valid data");
    assertTrue(validResult.isPresent());

    // Process invalid data
    Optional<String> invalidResult = bundle.process("processor", "short");
    assertFalse(invalidResult.isPresent());
  }

  @Test
  public void testCircuitBreaker() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("circuit-test", env);

    // Add tube with circuit breaker
    bundle.createTube("processor", "Processor Tube");
    bundle.enableCircuitBreaker("processor", 2, 1000);

    // Add transformer that fails
    bundle.addTransformer(
        "processor",
        (Function<String, String>)
            s -> {
              if (s.contains("fail")) {
                throw new RuntimeException("Simulated failure");
              }
              return s.toUpperCase();
            });

    // Process valid data
    Optional<String> validResult = bundle.process("processor", "valid data");
    assertTrue(validResult.isPresent());

    // Process failing data twice to open circuit
    bundle.process("processor", "fail1");
    bundle.process("processor", "fail2");

    // Now valid data should be rejected due to open circuit
    Optional<String> rejectedResult = bundle.process("processor", "should be rejected");
    assertFalse(rejectedResult.isPresent());
  }

  @Test
  public void testEventLogging() {
    Environment env = new Environment();
    Bundle bundle = new Bundle("logging-test", env);

    // Add a source tube
    bundle.createTube("source", "Source Tube");

    // Process data - should log but not complete successfully
    Optional<?> result = bundle.process("source", "test data");

    // Since source is a terminal tube (no connections), it should return the data
    assertTrue(result.isPresent());

    // Verify events were logged
    List<Bundle.BundleEvent> events = bundle.getEventLog();
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
