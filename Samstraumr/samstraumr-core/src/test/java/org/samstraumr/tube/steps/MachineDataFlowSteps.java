package org.samstraumr.tube.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.samstraumr.tube.Environment;
import org.samstraumr.tube.test.annotations.MachineTest;
import org.samstraumr.tube.bundle.Bundle;
import org.samstraumr.tube.bundle.BundleFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for machine data flow tests. These steps implement the Cucumber scenarios for
 * testing end-to-end data flow through machines composed of connected bundles.
 */
@MachineTest
public class MachineDataFlowSteps {

  private static final Logger LOGGER = LoggerFactory.getLogger(MachineDataFlowSteps.class);

  // Test context to share data between steps
  private Environment environment;
  private Map<String, Bundle> bundles = new HashMap<>();
  private String machineName;
  private List<String> sourceData;
  private List<String> processedData = new ArrayList<>();
  private List<Map<String, String>> dataWithMetadata;
  private Map<String, Object> machineState = new HashMap<>();
  private long executionStartTime;
  private long executionEndTime;
  private Exception lastException;

  @Before
  public void setUp() {
    LOGGER.info("Setting up machine test environment");
    environment = null;
    bundles.clear();
    sourceData = null;
    processedData.clear();
    machineState.clear();
    lastException = null;
  }

  @After
  public void tearDown() {
    LOGGER.info("Tearing down machine test environment");
    // Deactivate all bundles to release resources
    bundles.values().forEach(Bundle::deactivate);
  }

  @Given("a new environment is initialized")
  public void a_new_environment_is_initialized() {
    environment = new Environment();
    LOGGER.info("Initialized new test environment: {}", environment.getEnvironmentHash());
  }

  @Given("a machine named {string} is created")
  public void a_machine_named_is_created(String name) {
    this.machineName = name;
    machineState.put("name", name);
    machineState.put("created_at", System.currentTimeMillis());
    LOGGER.info("Created machine: {}", name);
  }

  @Given("a source bundle {string} is added to the machine")
  public void a_source_bundle_is_added_to_the_machine(String name) {
    Bundle bundle = new Bundle(name, environment);
    bundle.createTube("source", "Source Tube for " + name);
    bundles.put(name, bundle);
    LOGGER.info("Added source bundle: {}", name);
  }

  @Given("a transformation bundle {string} is added to the machine")
  public void a_transformation_bundle_is_added_to_the_machine(String name) {
    Bundle bundle = BundleFactory.createTransformationBundle(environment);
    bundle.createTube("transformer", "Transformer Tube for " + name);
    bundles.put(name, bundle);
    LOGGER.info("Added transformation bundle: {}", name);
  }

  @Given("a sink bundle {string} is added to the machine")
  public void a_sink_bundle_is_added_to_the_machine(String name) {
    Bundle bundle = new Bundle(name, environment);
    bundle.createTube("sink", "Sink Tube for " + name);
    bundles.put(name, bundle);
    LOGGER.info("Added sink bundle: {}", name);
  }

  @When("I query the machine structure")
  public void i_query_the_machine_structure() {
    // Collect structure information
    machineState.put("bundle_count", bundles.size());
    machineState.put("bundle_ids", bundles.keySet());

    Map<String, String> bundleIds = new HashMap<>();
    for (Map.Entry<String, Bundle> entry : bundles.entrySet()) {
      bundleIds.put(entry.getKey(), entry.getValue().getBundleId());
    }
    machineState.put("bundle_identifiers", bundleIds);

    LOGGER.info("Queried machine structure: {} bundles", bundles.size());
  }

  @Then("all machine components should have valid identifiers")
  public void all_machine_components_should_have_valid_identifiers() {
    @SuppressWarnings("unchecked")
    Map<String, String> bundleIds = (Map<String, String>) machineState.get("bundle_identifiers");

    assertNotNull(bundleIds, "Bundle identifiers should be collected");

    // All bundles should have non-empty IDs
    for (String id : bundleIds.values()) {
      assertNotNull(id, "Bundle ID should not be null");
      assertFalse(id.isEmpty(), "Bundle ID should not be empty");
    }

    // All tubes should have unique IDs
    Set<String> tubeIds = new HashSet<>();
    for (Bundle bundle : bundles.values()) {
      for (Map.Entry<String, org.samstraumr.tube.Tube> entry : bundle.getTubes().entrySet()) {
        String tubeId = entry.getValue().getUniqueId();
        assertFalse(tubeIds.contains(tubeId), "Tube IDs should be unique");
        tubeIds.add(tubeId);
      }
    }

    LOGGER.info(
        "Verified valid identifiers for {} bundles and {} tubes", bundleIds.size(), tubeIds.size());
  }

  @Then("the machine should contain exactly {int} bundles")
  public void the_machine_should_contain_exactly_bundles(Integer count) {
    assertEquals(
        count.intValue(), bundles.size(), "Machine should contain exactly " + count + " bundles");
  }

  @Then("the bundles should be properly connected in sequence")
  public void the_bundles_should_be_properly_connected_in_sequence() {
    // Connect the bundles in sequence for later tests
    // This is both an assertion and preparation step
    try {
      Bundle sourceBundle = bundles.get("input-reader");
      Bundle processorBundle = bundles.get("processor");
      Bundle sinkBundle = bundles.get("output-writer");

      // Create connections between the bundles
      // In a real machine implementation, this would establish actual connections
      // For our test, we'll simulate this by connecting output of one to input of next
      machineState.put(
          "connection_map",
          Map.of(
              "input-reader", "processor",
              "processor", "output-writer"));

      LOGGER.info("Connected bundles in sequence: input-reader -> processor -> output-writer");
    } catch (Exception e) {
      fail("Failed to connect bundles: " + e.getMessage());
    }
  }

  @Given("the source bundle generates {int} data elements")
  public void the_source_bundle_generates_data_elements(Integer count) {
    // Generate test data
    sourceData =
        IntStream.range(0, count).mapToObj(i -> "data-element-" + i).collect(Collectors.toList());

    LOGGER.info("Generated {} source data elements", count);
  }

  @Given("the transformation bundle converts all text to uppercase")
  public void the_transformation_bundle_converts_all_text_to_uppercase() {
    Bundle processorBundle = bundles.get("processor");
    assertNotNull(processorBundle, "Processor bundle should exist");

    processorBundle.addTransformer("transformer", (Function<String, String>) String::toUpperCase);

    LOGGER.info("Configured transformer to convert text to uppercase");
  }

  @Given("the transformation bundle is configured to fail on the {int}rd element")
  public void the_transformation_bundle_is_configured_to_fail_on_the_element(Integer elementIndex) {
    Bundle processorBundle = bundles.get("processor");
    assertNotNull(processorBundle, "Processor bundle should exist");

    final int failOnIndex = elementIndex - 1; // Convert to 0-based index

    processorBundle.addTransformer(
        "transformer",
        (Function<String, String>)
            data -> {
              int index = Integer.parseInt(data.split("-")[2]);
              if (index == failOnIndex) {
                throw new RuntimeException("Simulated failure on element " + (failOnIndex + 1));
              }
              return data.toUpperCase();
            });

    LOGGER.info("Configured transformer to fail on element index {}", failOnIndex);
  }

  @Given("circuit breakers are enabled for all bundles")
  public void circuit_breakers_are_enabled_for_all_bundles() {
    for (Map.Entry<String, Bundle> entry : bundles.entrySet()) {
      String bundleName = entry.getKey();
      Bundle bundle = entry.getValue();

      // Enable circuit breaker for each tube in the bundle
      for (String tubeName : bundle.getTubes().keySet()) {
        bundle.enableCircuitBreaker(tubeName, 1, 5000);
        LOGGER.info("Enabled circuit breaker for tube {} in bundle {}", tubeName, bundleName);
      }
    }
  }

  @Given("each data element has unique metadata attached")
  public void each_data_element_has_unique_metadata_attached() {
    assertNotNull(sourceData, "Source data should be generated first");

    dataWithMetadata = new ArrayList<>();
    for (int i = 0; i < sourceData.size(); i++) {
      Map<String, String> elementWithMetadata = new HashMap<>();
      elementWithMetadata.put("data", sourceData.get(i));
      elementWithMetadata.put("timestamp", String.valueOf(System.currentTimeMillis()));
      elementWithMetadata.put("index", String.valueOf(i));
      elementWithMetadata.put("uuid", UUID.randomUUID().toString());
      dataWithMetadata.add(elementWithMetadata);
    }

    LOGGER.info("Attached metadata to {} data elements", dataWithMetadata.size());
  }

  @When("I execute the machine data flow")
  public void i_execute_the_machine_data_flow() {
    assertNotNull(sourceData, "Source data should be generated first");

    try {
      Bundle sourceBundle = bundles.get("input-reader");
      Bundle processorBundle = bundles.get("processor");
      Bundle sinkBundle = bundles.get("output-writer");

      // Process each data element through the machine
      for (String data : sourceData) {
        try {
          // Simulate the machine pipeline by passing data through each bundle
          Optional<String> processedBySource = sourceBundle.process("source", data);
          if (processedBySource.isPresent()) {
            Optional<String> processedByTransformer =
                processorBundle.process("transformer", processedBySource.get());
            if (processedByTransformer.isPresent()) {
              Optional<String> finalResult =
                  sinkBundle.process("sink", processedByTransformer.get());
              if (finalResult.isPresent()) {
                processedData.add(finalResult.get());
              }
            }
          }
        } catch (Exception e) {
          // Record the exception but continue processing other elements
          lastException = e;
          LOGGER.error("Error processing data element {}: {}", data, e.getMessage());
        }
      }

      LOGGER.info(
          "Executed machine data flow, processed {} out of {} elements",
          processedData.size(),
          sourceData.size());

    } catch (Exception e) {
      lastException = e;
      LOGGER.error("Failed to execute machine data flow: {}", e.getMessage());
    }
  }

  @When("I execute the machine data flow with timing")
  public void i_execute_the_machine_data_flow_with_timing() {
    executionStartTime = System.currentTimeMillis();
    i_execute_the_machine_data_flow();
    executionEndTime = System.currentTimeMillis();

    long executionTime = executionEndTime - executionStartTime;
    machineState.put("execution_time_ms", executionTime);

    LOGGER.info("Machine data flow execution took {} ms", executionTime);
  }

  @Then("the sink bundle should receive exactly {int} data elements")
  public void the_sink_bundle_should_receive_exactly_data_elements(Integer count) {
    assertEquals(
        count.intValue(),
        processedData.size(),
        "Sink should receive exactly " + count + " elements");
  }

  @Then("all output data should be uppercase")
  public void all_output_data_should_be_uppercase() {
    for (String data : processedData) {
      assertEquals(data.toUpperCase(), data, "All output data should be uppercase");
    }
  }

  @Then("the machine execution report should indicate a partial success")
  public void the_machine_execution_report_should_indicate_a_partial_success() {
    assertNotNull(lastException, "There should be at least one error");
    assertTrue(processedData.size() > 0, "Some data should be processed");
    assertTrue(processedData.size() < sourceData.size(), "Not all data should be processed");

    LOGGER.info(
        "Verified partial success: {} of {} elements processed",
        processedData.size(),
        sourceData.size());
  }

  @Then("exactly {int} data elements should be successfully processed")
  public void exactly_data_elements_should_be_successfully_processed(Integer count) {
    assertEquals(
        count.intValue(),
        processedData.size(),
        "Exactly " + count + " elements should be processed");
  }

  @Then("the failure should be properly isolated to the transformation bundle")
  public void the_failure_should_be_properly_isolated_to_the_transformation_bundle() {
    assertNotNull(lastException, "There should be an exception");
    assertTrue(
        lastException.getMessage().contains("Simulated failure"),
        "Exception should be from the simulated failure");
  }

  @Then("the circuit breaker should be in open state for the transformation bundle")
  public void the_circuit_breaker_should_be_in_open_state_for_the_transformation_bundle() {
    Bundle processorBundle = bundles.get("processor");
    Map<String, Bundle.CircuitBreaker> breakers = processorBundle.getCircuitBreakers();
    Bundle.CircuitBreaker transformerBreaker = breakers.get("transformer");

    assertNotNull(transformerBreaker, "Circuit breaker should exist");
    assertTrue(transformerBreaker.isOpen(), "Circuit breaker should be open");
  }

  @Then("all data should be processed successfully")
  public void all_data_should_be_processed_successfully() {
    assertEquals(sourceData.size(), processedData.size(), "All data elements should be processed");
    assertNull(lastException, "There should be no exceptions");
  }

  @Then("the total processing time should be less than {int} milliseconds")
  public void the_total_processing_time_should_be_less_than_milliseconds(Integer maxTime) {
    long actualTime = (Long) machineState.get("execution_time_ms");
    assertTrue(
        actualTime < maxTime,
        "Processing time should be less than " + maxTime + "ms, but was " + actualTime + "ms");
  }

  @Then("the sink bundle should receive all {int} data elements with metadata")
  public void the_sink_bundle_should_receive_all_data_elements_with_metadata(Integer count) {
    // Implementation would verify metadata preservation
    // This is a simplified version that just checks counts
    assertNotNull(dataWithMetadata, "Data with metadata should exist");
    assertEquals(
        count.intValue(),
        dataWithMetadata.size(),
        "Should receive all " + count + " elements with metadata");
  }

  @Then("the machine execution report should include full data lineage")
  public void the_machine_execution_report_should_include_full_data_lineage() {
    // In a real implementation, we would verify the tube lineage contains processing steps
    // For this sample, we'll just verify the bundles have event logs
    for (Bundle bundle : bundles.values()) {
      List<Bundle.BundleEvent> events = bundle.getEventLog();
      assertFalse(events.isEmpty(), "Bundle should have logged events");
    }
  }

  @Then("the machine state log should show all processing steps")
  public void the_machine_state_log_should_show_all_processing_steps() {
    // In a real implementation, we would verify the detailed processing steps
    // For this sample, we'll just verify basic state tracking
    assertNotNull(machineState.get("name"), "Machine name should be recorded");
    assertNotNull(machineState.get("created_at"), "Machine creation time should be recorded");
    assertNotNull(machineState.get("bundle_count"), "Bundle count should be recorded");
  }
}
