# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@ALZ001 @EnvironmentalFactors
Feature: Environmental Factors Modeling for Alzheimer's Research
  As an Alzheimer's disease researcher
  I want to model environmental influences on disease progression
  So that I can analyze their impact and potential interventions

  Background:
    Given an environmental factors modeling environment is initialized
    And the simulation timestep is set to 1 "day"

  @L0_Component @Embryonic @Positive
  Scenario: Initialize environmental factors component
    When I create a new environmental factors component
    Then the component should be successfully created
    And the component should have default environmental parameters
    And the component should be in an initialized state

  @L0_Component @Infancy @Positive
  Scenario: Configure environmental factor parameters
    When I create a new environmental factors component
    And I configure the following environmental parameters:
      | parameter | value |
      | diet      | Mediterranean |
      | exercise  | Moderate |
      | stress    | Low |
      | sleep     | Normal |
    Then the component should have the configured environmental parameters
    And the component should be in a configured state

  @L0_Component @Maturity @Positive
  Scenario: Load patient environmental history data
    When I create a new environmental factors component
    And I load the following environmental history:
      | timestamp  | diet          | exercise  | stress | sleep  | medication       |
      | 2025-01-01 | Mediterranean | Moderate  | Low    | Normal | None             |
      | 2025-01-30 | Western       | Low       | High   | Poor   | None             |
      | 2025-02-28 | Western       | Low       | High   | Poor   | Cholinesterase   |
      | 2025-03-30 | Mediterranean | Moderate  | Medium | Good   | Cholinesterase   |
      | 2025-04-30 | Mediterranean | High      | Low    | Good   | Cholinesterase   |
    Then the environmental history should be loaded with 5 data points
    And the component should be in a data loaded state

  @L1_Composite @Infancy @Positive
  Scenario: Create environmental factors composite with multiple components
    When I create an environmental factors component named "diet"
    And I create an environmental factors component named "exercise"
    And I create an environmental factors component named "stress"
    And I create an environmental factors composite from components "diet", "exercise", and "stress"
    Then the composite should contain 3 components
    And the composite should be in an initialized state

  @L1_Composite @Maturity @Positive
  Scenario: Analyze correlation between environmental factors and biomarkers
    When I create an environmental factors component named "lifestyle"
    And I load the following environmental history:
      | timestamp  | diet          | exercise  | stress | sleep  | medication       |
      | 2025-01-01 | Mediterranean | Moderate  | Low    | Normal | None             |
      | 2025-01-30 | Western       | Low       | High   | Poor   | None             |
      | 2025-02-28 | Western       | Low       | High   | Poor   | Cholinesterase   |
      | 2025-03-30 | Mediterranean | Moderate  | Medium | Good   | Cholinesterase   |
      | 2025-04-30 | Mediterranean | High      | Low    | Good   | Cholinesterase   |
    And I create a biomarker component named "amyloid"
    And I load the following biomarker data:
      | timestamp  | value |
      | 2025-01-01 | 42.5  |
      | 2025-01-30 | 45.3  |
      | 2025-02-28 | 48.2  |
      | 2025-03-30 | 46.1  |
      | 2025-04-30 | 43.7  |
    And I create an environment-biomarker composite from "lifestyle" and "amyloid"
    When I analyze correlation between environmental factors and biomarker levels
    Then the analysis should identify significant correlations
    And the diet factor should show strong negative correlation with amyloid levels
    And the exercise factor should show moderate negative correlation with amyloid levels
    And the stress factor should show positive correlation with amyloid levels

  @L2_Machine @Maturity @Positive
  Scenario: Simulate intervention impact on disease progression
    When I create an environmental intervention machine
    And I configure the following intervention plan:
      | startDate  | endDate    | diet          | exercise  | stress | sleep  | medication       |
      | 2025-05-01 | 2025-07-31 | Mediterranean | High      | Low    | Good   | Cholinesterase   |
    And I set the baseline progression rate to 1.5 "points/year"
    When I simulate intervention impact over 90 "days"
    Then the simulation should predict reduced progression rate
    And the predicted progression rate should be less than 1.0 "points/year"
    And the simulation should generate impact metrics for each factor

  @L2_Machine @Maturity @Negative
  Scenario: Handle invalid intervention plans
    When I create an environmental intervention machine
    And I try to configure an intervention with invalid parameters:
      | startDate  | endDate    | diet      | exercise | stress | sleep | medication |
      | 2025-05-01 | 2025-04-01 | Invalid   | Extreme  | Ultra  | Bad   | Unknown    |
    Then the machine should reject the invalid configuration
    And the machine should report specific validation errors for each parameter

  @L3_System @Maturity @Positive
  Scenario: Integrate environmental factors with comprehensive disease model
    Given I have a complete Alzheimer's disease model
    When I integrate environmental factors data into the model
    And I simulate disease progression with different environmental scenarios:
      | scenario       | diet          | exercise  | stress | sleep  | medication       | duration |
      | Baseline       | Western       | Low       | High   | Poor   | None             | 365 days |
      | Conservative   | Mediterranean | Moderate  | Medium | Normal | None             | 365 days |
      | Aggressive     | Mediterranean | High      | Low    | Good   | Cholinesterase   | 365 days |
    Then each scenario should produce distinct progression trajectories
    And the aggressive intervention scenario should show the slowest progression
    And the baseline scenario should show the fastest progression
    And the system should calculate quality of life metrics for each scenario