/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */

package org.s8r.test.steps.consciousness;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Base step definitions for consciousness testing.
 *
 * <p>This class provides the foundational step definitions for the 300-scenario consciousness test
 * suite. It implements steps for:
 *
 * <ul>
 *   <li>Genesis tests - Adam Tube instantiation
 *   <li>Identity tests - Persistence and uniqueness
 *   <li>Consciousness tests - Self-observation validation
 *   <li>Feedback tests - Loop closure verification
 *   <li>Emergence tests - Unexpected behavior capture
 *   <li>Adaptation tests - Learning and evolution
 *   <li>Holistic tests - Systems theory properties
 * </ul>
 *
 * @see <a href="docs/concepts/philosophical-synthesis-identity-time-consciousness.md">Philosophical
 *     Synthesis</a>
 * @see <a
 *     href="docs/architecture/decisions/0014-adopt-contract-first-testing-strategy.md">ADR-0014</a>
 */
public class ConsciousnessStepDefinitions {

  // =========================================================================
  // Test Context
  // =========================================================================

  protected ConsciousnessTestContext context;
  protected Map<String, Object> testData;
  protected List<String> observationLog;
  protected Map<String, FeedbackLoop> activeLoops;

  @Before
  public void setUp() {
    context = new ConsciousnessTestContext();
    testData = new HashMap<>();
    observationLog = new ArrayList<>();
    activeLoops = new ConcurrentHashMap<>();
  }

  @After
  public void tearDown() {
    context.cleanup();
    testData.clear();
    observationLog.clear();
    activeLoops.clear();
  }

  // =========================================================================
  // Genesis Step Definitions
  // =========================================================================

  @Given("a clean system environment")
  public void aCleanSystemEnvironment() {
    context.resetEnvironment();
    assertNotNull(context.getEnvironment(), "Environment should be initialized");
  }

  @Given("the consciousness framework is initialized")
  public void theConsciousnessFrameworkIsInitialized() {
    context.initializeConsciousnessFramework();
    assertTrue(context.isConsciousnessEnabled(), "Consciousness framework should be enabled");
  }

  @When("the Adam Tube is instantiated with reason {string}")
  public void theAdamTubeIsInstantiatedWithReason(String reason) {
    context.createAdamTube(reason);
    assertNotNull(context.getAdamTube(), "Adam Tube should be created");
  }

  @When("the Adam Tube is instantiated")
  public void theAdamTubeIsInstantiated() {
    theAdamTubeIsInstantiatedWithReason("Default Genesis Reason");
  }

  @Then("the tube should be identified as an Adam Tube")
  public void theTubeShouldBeIdentifiedAsAnAdamTube() {
    assertTrue(context.getAdamTube().getIdentity().isAdam(), "Tube should be identified as Adam");
  }

  @Then("the tube should have no parent reference")
  public void theTubeShouldHaveNoParentReference() {
    assertNull(context.getAdamTube().getParentIdentity(), "Adam Tube should have no parent");
  }

  @Then("the tube should have a unique reproducible identity string")
  public void theTubeShouldHaveAUniqueReproducibleIdentityString() {
    String id1 = context.getAdamTube().getUniqueId();
    String id2 = context.getAdamTube().getUniqueId();
    assertNotNull(id1, "Identity string should not be null");
    assertEquals(id1, id2, "Identity string should be reproducible");
  }

  @Then("the tube should capture the environmental context at genesis")
  public void theTubeShouldCaptureTheEnvironmentalContextAtGenesis() {
    Map<String, String> envContext = context.getAdamTube().getIdentity().getEnvironmentContext();
    assertNotNull(envContext, "Environmental context should be captured");
    // Environment context may be empty for minimal test environments
  }

  @Then("the tube should log {string}")
  public void theTubeShouldLog(String expectedLog) {
    List<String> logs = context.getAdamTube().getMemoryLog();
    assertTrue(
        logs.stream().anyMatch(log -> log.contains(expectedLog)),
        "Log should contain: " + expectedLog);
  }

  // =========================================================================
  // Identity Step Definitions
  // =========================================================================

  @Given("a component with reason {string}")
  public void aComponentWithReason(String reason) {
    context.createComponent(reason);
    assertNotNull(context.getCurrentComponent(), "Component should be created");
  }

  @Given("the component UUID is recorded as {string}")
  public void theComponentUuidIsRecordedAs(String recordKey) {
    testData.put(recordKey, context.getCurrentComponent().getUniqueId());
  }

  @When("the component transitions through all lifecycle states")
  public void theComponentTransitionsThroughAllLifecycleStates() {
    context.transitionThroughLifecycle(context.getCurrentComponent());
  }

  @Then("the component UUID should match {string}")
  public void theComponentUuidShouldMatch(String recordKey) {
    String originalUuid = (String) testData.get(recordKey);
    assertEquals(
        originalUuid, context.getCurrentComponent().getUniqueId(), "UUID should remain unchanged");
  }

  // =========================================================================
  // Consciousness Step Definitions
  // =========================================================================

  @Given("an active component with reason {string}")
  public void anActiveComponentWithReason(String reason) {
    context.createActiveComponent(reason);
    assertTrue(context.getCurrentComponent().isOperational(), "Component should be operational");
  }

  @Given("self-observation logging is enabled")
  public void selfObservationLoggingIsEnabled() {
    context.enableSelfObservation();
  }

  @When("the component is instructed to observe its own state")
  public void theComponentIsInstructedToObserveItsOwnState() {
    context.triggerSelfObservation();
    assertNotNull(context.getLastObservation(), "Observation should be recorded");
  }

  @Then("the component should produce an observation record")
  public void theComponentShouldProduceAnObservationRecord() {
    assertNotNull(context.getLastObservation(), "Observation record should exist");
  }

  @Then("the observation should include:")
  public void theObservationShouldInclude(DataTable dataTable) {
    Map<String, Object> observation = context.getLastObservation();
    List<Map<String, String>> expected = dataTable.asMaps();

    for (Map<String, String> row : expected) {
      String field = row.get("Field");
      assertTrue(observation.containsKey(field), "Observation should include field: " + field);
    }
  }

  @Then("observer_id should equal observed_id")
  public void observerIdShouldEqualObservedId() {
    Map<String, Object> observation = context.getLastObservation();
    assertEquals(
        observation.get("observer_id"),
        observation.get("observed_id"),
        "Self-observation should have matching observer and observed IDs");
  }

  // =========================================================================
  // Feedback Step Definitions
  // =========================================================================

  @Given("an active component with feedback enabled")
  public void anActiveComponentWithFeedbackEnabled() {
    context.createActiveComponent("Feedback Test Component");
    context.enableFeedbackLoop();
    assertTrue(context.isFeedbackEnabled(), "Feedback should be enabled");
  }

  @Given("feedback loop monitoring is enabled")
  public void feedbackLoopMonitoringIsEnabled() {
    context.enableFeedbackMonitoring();
  }

  @When("the component:")
  public void theComponent(DataTable dataTable) {
    List<Map<String, String>> steps = dataTable.asMaps();
    for (Map<String, String> step : steps) {
      String action = step.get("Action");
      context.executeAction(action);
      String result = step.get("Result");
      if (result != null) {
        testData.put(result, context.getLastActionResult());
      }
    }
  }

  @Then("the feedback loop should be marked as {string}")
  public void theFeedbackLoopShouldBeMarkedAs(String expectedStatus) {
    assertEquals(
        expectedStatus, context.getFeedbackLoopStatus(), "Feedback loop status should match");
  }

  @Then("loop closure should be logged with cycle ID")
  public void loopClosureShouldBeLoggedWithCycleId() {
    assertNotNull(context.getLastLoopCycleId(), "Loop cycle ID should be logged");
  }

  @Then("cycle time should be recorded")
  public void cycleTimeShouldBeRecorded() {
    assertTrue(context.getLastCycleTimeMs() > 0, "Cycle time should be positive");
  }

  // =========================================================================
  // Emergence Step Definitions
  // =========================================================================

  @Given("a system with {int} interacting components")
  public void aSystemWithInteractingComponents(int count) {
    context.createComponentSystem(count);
    assertEquals(
        count, context.getSystemComponentCount(), "System should have specified component count");
  }

  @Given("emergence detection is enabled")
  public void emergenceDetectionIsEnabled() {
    context.enableEmergenceDetection();
  }

  @When("the system operates for {int} cycles")
  public void theSystemOperatesForCycles(int cycles) {
    context.runSystemCycles(cycles);
  }

  @When("a recurring interaction pattern emerges")
  public void aRecurringInteractionPatternEmerges() {
    context.simulateEmergentPattern();
  }

  @Then("the system should detect the pattern")
  public void theSystemShouldDetectThePattern() {
    assertTrue(context.hasDetectedPattern(), "Pattern should be detected");
  }

  @Then("the pattern should be classified as {string}")
  public void thePatternShouldBeClassifiedAs(String classification) {
    assertEquals(
        classification, context.getPatternClassification(), "Pattern classification should match");
  }

  // =========================================================================
  // Adaptation Step Definitions
  // =========================================================================

  @Given("an active component with memory enabled")
  public void anActiveComponentWithMemoryEnabled() {
    context.createActiveComponent("Memory Test Component");
    context.enableMemory();
    assertTrue(context.isMemoryEnabled(), "Memory should be enabled");
  }

  @Given("learning subsystem is enabled")
  public void learningSubsystemIsEnabled() {
    context.enableLearning();
  }

  @Given("memory persistence is configured")
  public void memoryPersistenceIsConfigured() {
    context.configureMemoryPersistence();
  }

  @When("the component processes signals:")
  public void theComponentProcessesSignals(DataTable dataTable) {
    List<Map<String, String>> signals = dataTable.asMaps();
    for (Map<String, String> signal : signals) {
      context.processSignal(signal.get("Signal ID"), signal.get("Type"), signal.get("Outcome"));
    }
  }

  @Then("the component should remember all experiences")
  public void theComponentShouldRememberAllExperiences() {
    assertTrue(
        context.getAllExperiences().size() > 0, "Component should have experiences in memory");
  }

  @Then("experiences should be retrievable by ID")
  public void experiencesShouldBeRetrievableById() {
    for (String signalId : context.getProcessedSignalIds()) {
      assertNotNull(
          context.getExperienceById(signalId),
          "Experience should be retrievable by ID: " + signalId);
    }
  }

  // =========================================================================
  // Holistic Step Definitions
  // =========================================================================

  @Given("a complete Samstraumr system with:")
  public void aCompleteSamstraumrSystemWith(DataTable dataTable) {
    List<Map<String, String>> components = dataTable.asMaps();
    for (Map<String, String> component : components) {
      String type = component.get("Component Type");
      int count = Integer.parseInt(component.get("Count"));
      context.createSystemComponents(type, count);
    }
  }

  @Given("all components are initialized to ACTIVE state")
  public void allComponentsAreInitializedToActiveState() {
    context.activateAllComponents();
  }

  @Given("consciousness monitoring is enabled")
  public void consciousnessMonitoringIsEnabled() {
    context.enableConsciousnessMonitoring();
  }

  @When("conformance is verified")
  public void conformanceIsVerified() {
    context.verifyConformance();
  }

  @Then("all components should meet:")
  public void allComponentsShouldMeet(DataTable dataTable) {
    Map<String, String> conformanceResults = context.getConformanceResults();
    List<Map<String, String>> requirements = dataTable.asMaps();

    for (Map<String, String> requirement : requirements) {
      String req = requirement.get("Requirement");
      String expectedStatus = requirement.get("Status");
      assertEquals(
          expectedStatus,
          conformanceResults.get(req),
          "Conformance requirement should be met: " + req);
    }
  }

  // =========================================================================
  // Helper Inner Classes
  // =========================================================================

  /** Represents a feedback loop for testing purposes. */
  protected static class FeedbackLoop {
    private final String id;
    private String status;
    private long startTime;
    private long endTime;
    private List<String> stages;

    public FeedbackLoop(String id) {
      this.id = id;
      this.status = "open";
      this.startTime = System.currentTimeMillis();
      this.stages = new ArrayList<>();
    }

    public void addStage(String stage) {
      stages.add(stage);
    }

    public void close() {
      this.status = "closed";
      this.endTime = System.currentTimeMillis();
    }

    public String getStatus() {
      return status;
    }

    public long getCycleTimeMs() {
      return endTime - startTime;
    }

    public String getId() {
      return id;
    }
  }
}
