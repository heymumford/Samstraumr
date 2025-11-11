# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@ALZ001 @ProteinExpression @L2_Composite
Feature: Protein Expression Composite for Alzheimer's Disease Modeling
  As an Alzheimer's disease researcher
  I want to model complex protein interactions across multiple compartments
  So that I can better understand the mechanisms of protein-based pathology

  Background:
    Given a protein expression composite named "AlzheimerProteins" with default components

  @BDD @Embryonic @Positive
  Scenario: Initialize composite with protein expression components
    Then the composite "AlzheimerProteins" should contain 3 protein expression components

  @BDD @Infancy @Positive
  Scenario: Add components to a protein expression composite
    When I add the following protein expression components to composite "AlzheimerProteins":
      | component_name       | protein_type        | initial_level |
      | AlphaComponent       | alpha_synuclein     | 25.0          |
      | PresenComponent      | presenilin          | 35.0          |
    Then the composite "AlzheimerProteins" should contain 5 protein expression components

  @BDD @Childhood @Positive
  Scenario: Create protein interaction network in composite
    When I create an interaction network named "MainNetwork" in composite "AlzheimerProteins"
    And I add the following protein types to network "MainNetwork" in composite "AlzheimerProteins":
      | amyloid             |
      | tau                 |
      | alpha_synuclein     |
      | presenilin          |
    And I set the following interaction strengths in network "MainNetwork" of composite "AlzheimerProteins":
      | protein_type_1   | protein_type_2     | strength |
      | amyloid          | tau                | 0.7      |
      | amyloid          | alpha_synuclein    | 0.4      |
      | tau              | presenilin         | 0.3      |
    Then the interaction network "MainNetwork" in composite "AlzheimerProteins" should contain 4 protein types
    And the interaction strength between "amyloid" and "tau" in network "MainNetwork" of composite "AlzheimerProteins" should be 0.7

  @BDD @Childhood @Positive
  Scenario: Configure cellular compartments in composite
    When I create the following cellular compartments in composite "AlzheimerProteins":
      | extracellular        |
      | cytoplasm            |
      | nucleus              |
      | mitochondria         |
    And I set the following protein levels in compartments of composite "AlzheimerProteins":
      | compartment      | protein_type     | level    |
      | extracellular    | amyloid          | 150.0    |
      | cytoplasm        | tau              | 75.0     |
      | cytoplasm        | presenilin       | 25.0     |
      | nucleus          | tau              | 10.0     |
    Then the composite "AlzheimerProteins" should have 4 cellular compartments
    And the level of protein "amyloid" in compartment "extracellular" of composite "AlzheimerProteins" should be 150.0

  @BDD @Adolescence @Positive
  Scenario: Configure protein transport processes
    Given a fully configured protein expression composite named "TransportTest"
    When I create the following transport processes in composite "TransportTest":
      | protein_type     | source           | target          | rate    |
      | amyloid          | extracellular    | cytoplasm       | 0.08    |
      | tau              | cytoplasm        | nucleus         | 0.05    |
      | neurofilament    | nucleus          | cytoplasm       | 0.02    |
    Then the composite "TransportTest" should have 6 transport processes

  @BDD @Maturity @Positive
  Scenario: Simulate multi-protein interactions
    Given a fully configured protein expression composite named "InteractionTest"
    When I simulate multi-protein interaction in network "AlzheimerProteins" of composite "InteractionTest" for 50 timepoints
    Then the multi-protein interaction results for network "AlzheimerProteins" in composite "InteractionTest" should show increasing levels for "tau"

  @BDD @Maturity @Positive
  Scenario: Simulate protein transport between compartments
    Given a fully configured protein expression composite named "TransportTest"
    When I simulate protein transport in composite "TransportTest" for 10 timesteps with step size 0.5
    Then the protein transport simulation for composite "TransportTest" should show decreased levels in source compartments

  @BDD @Maturity @Positive
  Scenario: Simulate protein aggregation with cross-seeding effects
    Given a fully configured protein expression composite named "AggregationTest"
    When I simulate aggregation with cross-seeding in composite "AggregationTest" for 50 timepoints
    Then the cross-seeding aggregation results for composite "AggregationTest" should show all protein levels increasing

  @BDD @Maturity @Positive
  Scenario: Create integrated protein profile across components
    Given a fully configured protein expression composite named "ProfileTest"
    When I create a composite protein profile for composite "ProfileTest"
    Then the composite protein profile for "ProfileTest" should contain data for at least 4 protein types