# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@ALZ001 @ATL @EnvironmentalFactors @L1_Composite
Feature: ALZ001 - Environmental Factors Influence on Disease Progression
  As a neuroscience researcher
  I want to model environmental factors affecting disease progression
  So that I can understand external influences on pathology development

  Background:
    Given a scientific analysis environment for environmental modeling
    And a baseline disease progression model

  @Embryonic @Positive
  Scenario: Initialize environmental factors composite
    When I create an environmental factors composite
    Then the composite should be properly initialized
    And the composite should have environmental modeling capabilities
    And the composite should be in "READY" state

  @Infancy @Positive
  Scenario: Configure oxidative stress parameters
    Given an initialized environmental factors composite
    When I configure the following oxidative stress parameters:
      | parameter                  | value   |
      | baseline_level             | 1.0     |
      | daily_fluctuation          | 0.2     |
      | stress_response_threshold  | 2.5     |
      | chronic_threshold          | 3.0     |
      | acute_duration_days        | 5       |
    Then the parameters should be successfully configured
    And the component should create an oxidative stress model
    And the model should include baseline and fluctuation patterns

  @Infancy @Positive
  Scenario: Simulate inflammatory response triggers
    Given an environmental factors composite with oxidative stress configured
    When I configure inflammatory response with the following triggers:
      | trigger              | threshold | delay_hours | duration_days | intensity |
      | acute_infection      | 0.7       | 24          | 7             | 0.8       |
      | chronic_stress       | 0.5       | 72          | 30            | 0.6       |
      | sleep_disruption     | 0.4       | 48          | 14            | 0.5       |
      | dietary_factors      | 0.3       | 120         | 21            | 0.4       |
    Then each trigger should be properly modeled
    And trigger thresholds should determine activation
    And delayed responses should be temporally accurate
    And inflammatory intensity should scale with trigger strength

  @Maturity @Positive
  Scenario: Model cognitive reserve protective effects
    Given an environmental factors composite with baseline configuration
    When I configure cognitive reserve with the following parameters:
      | parameter                     | value   |
      | baseline_reserve              | 0.7     |
      | education_contribution        | 0.15    |
      | mental_activity_contribution  | 0.10    |
      | social_engagement_factor      | 0.05    |
      | depletion_rate_percent        | 0.5     |
    And I run simulations with varying cognitive reserve levels:
      | reserve_level | expected_protection |
      | 0.2           | 0.15                |
      | 0.5           | 0.40                |
      | 0.8           | 0.65                |
      | 1.0           | 0.80                |
    Then the simulation should show protective effects increasing with reserve
    And protection should diminish over time according to depletion rate
    And critical threshold for significant protection should be identified

  @Maturity @Positive
  Scenario: Analyze environmental factor interactions
    Given an environmental factors composite with multiple factors configured
    When I analyze interactions between the following factor pairs:
      | factor_a             | factor_b             | interaction_type |
      | oxidative_stress     | inflammatory_response| synergistic      |
      | sleep_quality        | oxidative_stress     | antagonistic     |
      | cognitive_stimulation| cognitive_reserve    | reinforcing      |
      | physical_exercise    | inflammatory_response| opposing         |
    Then the composite should calculate interaction coefficients
    And identify factor combinations with highest impact
    And quantify the cumulative effects of multiple factors
    And generate a factor interaction matrix

  @Maturity @Negative
  Scenario: Handle conflicting environmental influences
    Given an environmental factors composite with baseline configuration
    When I configure contradictory parameters:
      | parameter_a                  | value_a | parameter_b                  | value_b | conflict_type        |
      | oxidative_stress.daily_min   | 2.0     | oxidative_stress.daily_max   | 1.5     | logical_contradiction|
      | cognitive_reserve.baseline   | 0.7     | cognitive_reserve.maximum    | 0.5     | ceiling_below_baseline|
      | inflammation.recovery_rate   | 0.2     | inflammation.minimum_duration| 10      | timing_conflict      |
    Then the composite should detect each parameter conflict
    And reject contradictory configurations
    And provide specific error messages for each conflict
    And suggest resolution strategies