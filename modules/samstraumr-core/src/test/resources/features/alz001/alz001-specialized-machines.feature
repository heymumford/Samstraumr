@ALZ001 @L2_Machine @Specialized
Feature: ALZ001 Specialized Machine Implementation
  As a specialized Alzheimer's disease researcher
  I want to use domain-specific machine configurations
  So that I can focus on particular aspects of disease pathology

  Background:
    Given a comprehensive Alzheimer's disease modeling environment
    And all five composite subsystems are available for integration

  @ProteinAggregation
  Scenario: Protein Aggregation Research Machine
    When I create a specialized machine focused on "protein aggregation research"
    And I configure the machine with the following protein aggregation parameters:
      | parameter                   | value     |
      | aggregation_rate            | 0.05      |
      | clearance_rate              | 0.02      |
      | seeding_threshold           | 10.0      |
      | spreading_model             | prion-like|
    And I load specialized protein data sets:
      | dataset_name | patients | timepoints | features | format       |
      | DIAN_CSF     | 300      | 6          | 10       | longitudinal |
      | ADNI_PET     | 400      | 4          | 20       | imaging      |
    When I execute a focused protein aggregation simulation
    Then the machine should generate protein spreading patterns across neural tissues
    And the simulation should track tau and amyloid progression over time
    And the results should correlate with clinical cognitive decline measures

  @NetworkConnectivity
  Scenario: Neural Network Connectivity Machine
    When I create a specialized machine focused on "network connectivity analysis"
    And I configure the machine with the following network parameters:
      | parameter                       | value      |
      | network_type                    | small-world|
      | rewiring_probability            | 0.1        |
      | mean_degree                     | 4          |
      | functional_connectivity_threshold | 0.3      |
      | structural_connectivity_threshold | 0.5      |
    And I load specialized connectivity data sets:
      | dataset_name | patients | timepoints | features | format  |
      | ADNI_fMRI    | 200      | 3          | 100      | imaging |
      | HCP_DTI      | 100      | 1          | 200      | imaging |
    When I execute a focused network degeneration simulation
    Then the machine should identify vulnerable network hubs
    And the simulation should show progressive connectivity loss patterns
    And the results should match known neurodegeneration progression routes

  @TemporalProgression
  Scenario: Temporal Disease Progression Machine
    When I create a specialized machine focused on "temporal progression analysis"
    And I configure the machine with the following temporal analysis parameters:
      | parameter                | value             |
      | change_point_detection   | true              |
      | periodicity_analysis     | true              |
      | trend_extraction_method  | STL_decomposition |
      | event_sequence_analysis  | true              |
    And I load specialized longitudinal data sets:
      | dataset_name      | patients | timepoints | features | format       |
      | ADNI_Longitudinal | 800      | 10         | 50       | longitudinal |
      | NACC              | 2000     | 8          | 40       | longitudinal |
    When I execute a temporal progression tracking simulation
    Then the machine should detect disease stage transitions
    And the simulation should identify temporal ordering of biomarker changes
    And the results should predict future disease trajectory with at least 75% accuracy

  @EnvironmentalFactors
  Scenario: Environmental Risk Factors Machine
    When I create a specialized machine focused on "environmental factors analysis"
    And I configure the machine with the following environmental parameters:
      | parameter                  | value               |
      | stress_model               | allostatic_load     |
      | diet_model                 | mediterranean_score |
      | physical_activity_model    | met_minutes         |
      | sleep_model                | fragmentation_index |
    And I load specialized environmental data sets:
      | dataset_name        | patients | timepoints | features | format       |
      | UK_Biobank_Lifestyle| 5000     | 3          | 100      | tabular      |
      | FINGER              | 1200     | 4          | 50       | longitudinal |
    When I execute an environmental factors impact simulation
    Then the machine should quantify lifestyle contributions to disease risk
    And the simulation should model environmental interventions efficacy
    And the results should stratify subjects by modifiable risk factors

  @PredictiveModeling
  Scenario: Personalized Prediction Machine
    When I create a specialized machine focused on "predictive modeling and personalization"
    And I configure the machine with the following prediction parameters:
      | parameter                  | value     |
      | model_type                 | ensemble  |
      | cross_validation_folds     | 5         |
      | hyperparameter_optimization| true      |
      | feature_selection          | recursive |
      | personalization_level      | high      |
    And I load comprehensive multimodal data sets:
      | dataset_name     | patients | timepoints | features | format     |
      | ADNI_Complete    | 1500     | 10         | 200      | multimodal |
      | Synthetic_Cohort | 10000    | 20         | 500      | synthetic  |
    When I execute a personalized prediction simulation
    Then the machine should generate personalized risk profiles
    And the simulation should provide tailored intervention recommendations
    And the results should demonstrate better precision than population-level models

  @IntegratedAnalysis
  Scenario: Cross-Scale Machine Analysis
    When I create the following specialized machines:
      | machine_type            | focus_area               |
      | protein_aggregation     | molecular mechanisms     |
      | network_connectivity    | cellular dysfunction     |
      | temporal_progression    | disease trajectories     |
      | environmental_factors   | external risk modifiers  |
      | predictive_modeling     | personalized predictions |
    And I integrate data flows between all specialized machines
    When I execute a multi-scale cross-machine analysis
    Then the analysis should identify causal relationships between scales
    And the integrated results should have greater explanatory power
    And the cross-scale model should identify novel therapeutic targets