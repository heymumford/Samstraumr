@ALZ001 @L2_Machine
Feature: ALZ001 System Simulation Machine Integration
  As a disease modeling researcher
  I want to coordinate multiple composite components in an integrated simulation
  So that I can model Alzheimer's disease across biological scales

  Background:
    Given a comprehensive Alzheimer's disease modeling environment
    And all five composite subsystems are available for integration

  @Core @Integration
  Scenario: Creating an integrated disease modeling system
    When I create a new integrated disease modeling system
    And I establish the following data flow paths between composites:
      | source_component       | target_component         | data_type                 | refresh_rate |
      | ProteinExpression      | NeuronalNetwork          | protein_aggregation_data  | 1000ms       |
      | NeuronalNetwork        | TimeSeriesAnalysis       | network_activity_data     | 500ms        |
      | TimeSeriesAnalysis     | PredictiveModeling       | temporal_patterns         | 2000ms       |
      | EnvironmentalFactors   | ProteinExpression        | environmental_influences  | 3000ms       |
      | EnvironmentalFactors   | PredictiveModeling       | environmental_context     | 2000ms       |
    Then the system components should communicate through proper channel establishment

  @Validation @DataQuality
  Scenario: Configuring data validation rules for integrated system
    Given I create a new integrated disease modeling system
    When I configure the following data validation rules for the integrated system:
      | data_type                   | validation_rule                                   |
      | protein_aggregation_data    | range: [0.0, 100.0], required: true              |
      | network_activity_data       | range: [-1.0, 1.0], required: true               |
      | temporal_patterns           | min_length: 10, required: true                    |
      | environmental_influences    | range: [0.0, 5.0], allowed_values: [0,1,2,3,4,5] |
      | biomarker_data              | min: 0.0, max: 1000.0, nullable: false           |
    And I establish the following data flow paths between composites:
      | source_component       | target_component         | data_type                 | refresh_rate |
      | ProteinExpression      | NeuronalNetwork          | protein_aggregation_data  | 1000ms       |
      | NeuronalNetwork        | TimeSeriesAnalysis       | network_activity_data     | 500ms        |
    Then the system components should communicate through proper channel establishment

  @Simulation @Core
  Scenario: Executing a full integrated disease simulation
    Given I create a new integrated disease modeling system
    When I load the following patient datasets into the integrated system:
      | dataset_name      | patients | timepoints | features | format     |
      | ADNI_subset       | 100      | 5          | 20       | tabular    |
      | UK_Biobank        | 500      | 3          | 15       | tabular    |
      | Synthetic_Dataset | 1000     | 10         | 25       | time_series|
    And I configure the following parameters for the integrated simulation:
      | parameter                | value   |
      | simulation_duration      | 10      |
      | time_step                | 0.1     |
      | stochasticity            | 0.2     |
      | parallel_execution       | true    |
      | validation_frequency     | 5       |
    And I execute a full integrated disease modeling simulation
    Then the integrated system should generate comprehensive disease trajectories
    And the results from each composite should contribute to the unified model

  @Analysis @CrossScale
  Scenario: Analyzing cross-scale interactions in the integrated model
    Given I create a new integrated disease modeling system
    When I execute a full integrated disease modeling simulation
    And I analyze cross-scale interactions in the integrated model with the following parameters:
      | scale1            | scale2            | analysis_method  | threshold |
      | molecular         | cellular          | correlation      | 0.7       |
      | cellular          | network           | granger_causality| 0.05      |
      | network           | cognitive         | transfer_entropy | 0.3       |
      | environmental     | molecular         | regression       | 0.01      |
    Then the integrated system should identify significant relationships between:
      | scale1            | scale2            | expected_pattern            |
      | molecular         | cellular          | bidirectional_correlation   |
      | cellular          | network           | causal_relationship         |
      | network           | cognitive         | information_transfer        |
      | environmental     | molecular         | modulation_effect           |

  @ClinicalUtility @Personalization
  Scenario: Generating personalized intervention recommendations
    Given I create a new integrated disease modeling system
    When I execute a full integrated disease modeling simulation
    And I request personalized treatment recommendations for patient groups:
      | patient_group       | disease_stage | genetic_profile | environmental_exposure |
      | early_onset_group   | MCI           | APOE4_positive  | high                   |
      | late_onset_group    | mild_AD       | APOE4_negative  | medium                 |
      | high_risk_group     | presymptomatic| APOE4_positive  | low                    |
    Then the integrated system should provide personalized intervention plans

  @ResearchHypothesis
  Scenario: Generating testable research hypotheses
    Given I create a new integrated disease modeling system
    When I execute a full integrated disease modeling simulation
    And I generate research hypotheses with the following parameters:
      | parameter                | value           |
      | novelty_threshold        | 0.7             |
      | evidence_threshold       | 0.6             |
      | domains                  | molecular,cellular,environmental |
      | max_hypotheses           | 10              |
      | include_mechanistic      | true            |
    Then the integrated system should generate at least 5 testable hypotheses

  @Resilience @DataQuality
  Scenario: Testing system resilience to data quality issues
    Given I create a new integrated disease modeling system
    When I execute a full integrated disease modeling simulation
    And I introduce data quality issues to test integrated system resilience:
      | subsystem           | issue_type       | affected_percentage |
      | ProteinExpression   | missing_data     | 15%                 |
      | NeuronalNetwork     | noise            | 20%                 |
      | TimeSeriesAnalysis  | outliers         | 10%                 |
      | EnvironmentalFactors| inconsistency    | 5%                  |
    Then the integrated system should recover from data quality issues with at least 0.8 accuracy

  @Validation @ClinicalUtility
  Scenario: Validating the integrated model against clinical datasets
    Given I create a new integrated disease modeling system
    When I execute a full integrated disease modeling simulation
    And I validate the integrated model against clinical datasets:
      | dataset_name      | patients | timepoints | data_type    | study_duration |
      | ADNI              | 500      | 8          | longitudinal | 36             |
      | AIBL              | 300      | 6          | longitudinal | 24             |
      | OASIS             | 400      | 4          | cross_sectional | 0           |
    Then the integrated model should demonstrate clinical utility with overall accuracy above 0.75
    And the model should identify subgroups where personalization would be most effective