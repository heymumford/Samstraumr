@L0_Unit # language: en
@integration @rlanguage
Feature: R Language Integration for Scientific Computing
  As a scientific workload developer
  I want to integrate R-based calculations with my system
  So that I can perform complex data analysis within the Samstraumr framework

  Background:
    Given a scientific analysis environment with R capabilities

  Scenario: Basic Statistical Analysis with R
    Given an R script for "statistical calculations" is available
    When I configure the R analysis tube with the following parameters:
      | Parameter         | Value |
      | sample_size       | 100   |
      | confidence_level  | 0.95  |
      | include_outliers  | false |
    And I run statistical calculations on a dataset
    Then the R calculation results should be available in the tube
    And the scientific workflow composite should contain all the R calculation results

  Scenario: Time Series Analysis with Seasonal Decomposition
    Given an R script for "time series analysis" is available
    When I configure the R analysis tube with the following parameters:
      | Parameter         | Value |
      | frequency         | 12    |
      | trend_window      | 5     |
    And I run time series analysis with seasonal decomposition
    Then the time series components should include trend and seasonality

  Scenario: Data Preprocessing with R
    Given an R script for "data preprocessing" is available
    When I preprocess data using R to handle outliers and missing values
    Then the preprocessed data should have no missing values

  Scenario: Data Visualization with R
    Given an R script for "data visualization" is available
    When I configure the R analysis tube with the following parameters:
      | Parameter           | Value    |
      | visualization_type  | boxplot  |
    And I generate data visualizations using R
    Then the data visualization output should be in a structure compatible with the UI

  Scenario: Advanced Analytics with R Machine Learning
    Given an R script for "advanced analytics" is available
    When I execute an advanced predictive model in R
    Then the advanced predictive model should achieve at least 0.85 accuracy
