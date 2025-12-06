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
package org.s8r.test.steps.lifecycle;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * This class serves as a mapper to explicitly match step definitions from the feature files to
 * their implementation methods in step classes.
 *
 * <p>This is used to help Cucumber resolve step definitions when there are issues with automatic
 * matching due to package structure or similar issues.
 */
public class StepDefinitionMapper {

  // Reference the actual step definition classes
  private final EarlyConceptionPhaseSteps earlyConceptionSteps = new EarlyConceptionPhaseSteps();
  private final LifecycleTransitionSteps lifecycleTransitionSteps = new LifecycleTransitionSteps();
  private final LifecycleStateSteps lifecycleStateSteps = new LifecycleStateSteps();
  private final ChildhoodPhaseSteps childhoodPhaseSteps = new ChildhoodPhaseSteps();
  private final InfancyPhaseSteps infancyPhaseSteps = new InfancyPhaseSteps();
  private final PreConceptionPhaseSteps preConceptionPhaseSteps = new PreConceptionPhaseSteps();
  private final ConceptionPhaseSteps conceptionPhaseSteps = new ConceptionPhaseSteps();
  private final EmbryonicPhaseSteps embryonicPhaseSteps = new EmbryonicPhaseSteps();

  // Early conception phase mappings
  @Then("the identity should contain a unique identifier")
  public void the_identity_should_contain_a_unique_identifier() {
    earlyConceptionSteps.the_identity_should_contain_a_unique_identifier();
  }

  @And("the identity should capture the exact moment of creation")
  public void the_identity_should_capture_the_exact_moment_of_creation() {
    earlyConceptionSteps.the_identity_should_capture_the_exact_moment_of_creation();
  }

  @And("the identity should record the creation purpose")
  public void the_identity_should_record_the_creation_purpose() {
    earlyConceptionSteps.the_identity_should_record_the_creation_purpose();
  }

  @And("the identity should establish existence boundaries")
  public void the_identity_should_establish_existence_boundaries() {
    earlyConceptionSteps.the_identity_should_establish_existence_boundaries();
  }

  @And("the identity should establish a root address")
  public void the_identity_should_establish_a_root_address() {
    earlyConceptionSteps.the_identity_should_establish_a_root_address();
  }

  @And("the identity should be capable of tracking descendants")
  public void the_identity_should_be_capable_of_tracking_descendants() {
    earlyConceptionSteps.the_identity_should_be_capable_of_tracking_descendants();
  }

  @And("the identity should adapt to environmental constraints")
  public void the_identity_should_adapt_to_environmental_constraints() {
    earlyConceptionSteps.the_identity_should_adapt_to_environmental_constraints();
  }

  @And("the identity should be traceable to its environment")
  public void the_identity_should_be_traceable_to_its_environment() {
    earlyConceptionSteps.the_identity_should_be_traceable_to_its_environment();
  }

  @And("the identity should maintain environmental awareness")
  public void the_identity_should_maintain_environmental_awareness() {
    earlyConceptionSteps.the_identity_should_maintain_environmental_awareness();
  }

  @And("the child tube should receive a distinct identity")
  public void the_child_tube_should_receive_a_distinct_identity() {
    earlyConceptionSteps.the_child_tube_should_receive_a_distinct_identity();
  }

  @And("the child tube should maintain parent reference")
  public void the_child_tube_should_maintain_parent_reference() {
    earlyConceptionSteps.the_child_tube_should_maintain_parent_reference();
  }

  @And("the parent tube should recognize the child")
  public void the_parent_tube_should_recognize_the_child() {
    earlyConceptionSteps.the_parent_tube_should_recognize_the_child();
  }

  @And("both tubes should update their lineage records")
  public void both_tubes_should_update_their_lineage_records() {
    earlyConceptionSteps.both_tubes_should_update_their_lineage_records();
  }

  @And("all allocated resources should be released")
  public void all_allocated_resources_should_be_released() {
    earlyConceptionSteps.all_allocated_resources_should_be_released();
  }

  @And("appropriate error information should be provided")
  public void appropriate_error_information_should_be_provided() {
    earlyConceptionSteps.appropriate_error_information_should_be_provided();
  }

  @And("the environment should log the failure details")
  public void the_environment_should_log_the_failure_details() {
    earlyConceptionSteps.the_environment_should_log_the_failure_details();
  }

  // Lifecycle transition mappings
  @When("the component transitions to {string} state")
  public void the_component_transitions_to_state(String stateName) {
    lifecycleTransitionSteps.the_component_transitions_to_state(stateName);
  }

  @And("each state should have a biological analog")
  public void each_state_should_have_a_biological_analog() {
    lifecycleTransitionSteps.each_state_should_have_a_biological_analog();
  }

  @And("each state should belong to a specific category")
  public void each_state_should_belong_to_a_specific_category() {
    lifecycleTransitionSteps.each_state_should_belong_to_a_specific_category();
  }

  @And("transitions between states should follow logical progression")
  public void transitions_between_states_should_follow_logical_progression() {
    lifecycleTransitionSteps.transitions_between_states_should_follow_logical_progression();
  }

  @And("appropriate errors should be generated for invalid transitions")
  public void appropriate_errors_should_be_generated_for_invalid_transitions() {
    lifecycleTransitionSteps.appropriate_errors_should_be_generated_for_invalid_transitions();
  }

  // Childhood phase mappings
  @Given("the tube is in the {string} lifecycle state")
  public void the_tube_is_in_the_lifecycle_state(String state) {
    childhoodPhaseSteps.the_tube_is_in_the_lifecycle_state(state);
  }

  @Then("the tube should produce expected output")
  public void the_tube_should_produce_expected_output() {
    childhoodPhaseSteps.the_tube_should_produce_expected_output();
  }

  @And("the processing should be recorded in operational logs")
  public void the_processing_should_be_recorded_in_operational_logs() {
    childhoodPhaseSteps.the_processing_should_be_recorded_in_operational_logs();
  }

  @And("the tube should develop operational patterns")
  public void the_tube_should_develop_operational_patterns() {
    childhoodPhaseSteps.the_tube_should_develop_operational_patterns();
  }

  @And("the tube should show improved efficiency metrics")
  public void the_tube_should_show_improved_efficiency_metrics() {
    childhoodPhaseSteps.the_tube_should_show_improved_efficiency_metrics();
  }

  @And("the tube should attempt to recover operational state")
  public void the_tube_should_attempt_to_recover_operational_state() {
    childhoodPhaseSteps.the_tube_should_attempt_to_recover_operational_state();
  }

  @And("the tube should learn from the error experience")
  public void the_tube_should_learn_from_the_error_experience() {
    childhoodPhaseSteps.the_tube_should_learn_from_the_error_experience();
  }

  // Infancy phase mappings
  @Given("a tube in the {string} lifecycle state")
  public void a_tube_in_the_lifecycle_state(String state) {
    infancyPhaseSteps.a_tube_in_the_lifecycle_state(state);
  }

  @And("the experience should include input and output details")
  public void the_experience_should_include_input_and_output_details() {
    infancyPhaseSteps.the_experience_should_include_input_and_output_details();
  }

  @And("the experience should be marked as a formative memory")
  public void the_experience_should_be_marked_as_a_formative_memory() {
    infancyPhaseSteps.the_experience_should_be_marked_as_a_formative_memory();
  }

  @When("the tube transitions from {string} to {string} state")
  public void the_tube_transitions_from_to_state(String fromState, String toState) {
    infancyPhaseSteps.the_tube_transitions_from_to_state(fromState, toState);
  }

  @And("the tube should maintain awareness of its previous state")
  public void the_tube_should_maintain_awareness_of_its_previous_state() {
    infancyPhaseSteps.the_tube_should_maintain_awareness_of_its_previous_state();
  }
}
