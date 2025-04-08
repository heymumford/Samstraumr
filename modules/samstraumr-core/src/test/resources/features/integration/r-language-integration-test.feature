# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

@integration @L1 @r-language @science
Feature: R Language Integration for Scientific Calculations
  As a science researcher
  I want to use Samstraumr to create calculation tubes and composites that use R
  So that I can perform various types of static and time series calculations

  Background:
    Given the R language service adapter is initialized
    And the data transformation service is available

  @smoke @static-calculation
  Scenario: Create a simple statistical calculation tube with R
    Given I have created a basic R calculation tube
    When I configure the tube with the R script:
      """
      calculate <- function(data) {
        result <- list()
        result$mean <- mean(data$values)
        result$sd <- sd(data$values)
        result$median <- median(data$values)
        result$quartiles <- quantile(data$values)
        return(result)
      }
      """
    And I pass the following sample data:
      | values    |
      | 10.2      |
      | 15.6      |
      | 8.3       |
      | 12.7      |
      | 9.5       |
    Then the tube should execute successfully
    And the output should contain valid statistical results
    And the mean value should be approximately 11.26
    And the standard deviation should be approximately 2.93

  @time-series
  Scenario: Create a time series analysis tube with R
    Given I have created a time series R calculation tube
    When I configure the tube with the R script:
      """
      analyze_time_series <- function(data) {
        # Convert to time series object
        ts_data <- ts(data$values, frequency=data$frequency)
        
        # Decompose time series
        decomp <- decompose(ts_data)
        
        # Forecast using auto.arima
        library(forecast)
        model <- auto.arima(ts_data)
        forecast <- forecast(model, h=data$forecast_periods)
        
        return(list(
          decomposition = list(
            trend = as.numeric(decomp$trend),
            seasonal = as.numeric(decomp$seasonal),
            random = as.numeric(decomp$random)
          ),
          forecast = list(
            mean = as.numeric(forecast$mean),
            lower = as.numeric(forecast$lower[,2]),
            upper = as.numeric(forecast$upper[,2])
          )
        ))
      }
      """
    And I pass the following time series data:
      | date       | values | frequency | forecast_periods |
      | 2024-01-01 | 105    | 12        | 6                |
      | 2024-02-01 | 112    | 12        | 6                |
      | 2024-03-01 | 118    | 12        | 6                |
      | 2024-04-01 | 127    | 12        | 6                |
      | 2024-05-01 | 135    | 12        | 6                |
      | 2024-06-01 | 140    | 12        | 6                |
      | 2024-07-01 | 132    | 12        | 6                |
      | 2024-08-01 | 125    | 12        | 6                |
      | 2024-09-01 | 115    | 12        | 6                |
      | 2024-10-01 | 108    | 12        | 6                |
      | 2024-11-01 | 112    | 12        | 6                |
      | 2024-12-01 | 118    | 12        | 6                |
    Then the tube should execute successfully
    And the output should contain decomposition components
    And the output should contain forecast predictions for 6 periods

  @composite @data-transformation
  Scenario: Connect multiple R tubes in a calculation composite
    Given I have created a data preprocessing R tube with script:
      """
      preprocess <- function(data) {
        # Handle missing values
        data$values <- na.omit(data$values)
        
        # Normalize data
        normalized <- (data$values - min(data$values)) / (max(data$values) - min(data$values))
        
        # Outlier detection (simple z-score)
        z_scores <- abs(scale(data$values))
        outliers <- which(z_scores > 2)
        
        return(list(
          original = data$values,
          normalized = normalized,
          outliers = outliers
        ))
      }
      """
    And I have created a statistical analysis R tube with script:
      """
      analyze <- function(data) {
        # Basic statistics
        stats <- list(
          mean = mean(data$normalized),
          sd = sd(data$normalized),
          min = min(data$normalized),
          max = max(data$normalized)
        )
        
        # Correlation if multiple series
        if (!is.null(data$secondary_values)) {
          stats$correlation <- cor(data$normalized, data$secondary_values)
        }
        
        return(stats)
      }
      """
    And I have created a visualization R tube with script:
      """
      visualize <- function(data) {
        # Create visualization data for external rendering
        # (in real implementation this could generate plot files)
        viz_data <- list(
          histogram = hist(data$normalized, plot=FALSE),
          boxplot_data = boxplot.stats(data$normalized),
          outliers_marked = data$outliers
        )
        
        return(viz_data)
      }
      """
    When I create a composite connecting these tubes in sequence
    And I provide the following input data:
      | values    | secondary_values |
      | 42.1      | 10.5             |
      | 38.6      | 11.2             |
      | 65.3      | 15.8             |
      | 29.8      | 8.3              |
      | 51.7      | 12.1             |
      | 44.2      | 11.8             |
      | 33.9      | 9.2              |
    Then the composite should process data through all tubes successfully
    And the final output should contain both statistical results and visualization data
    And the normalized values should be between 0 and 1
    And the correlation between normalized values and secondary values should be high

  @error-handling
  Scenario: Handle R execution errors gracefully
    Given I have created an R calculation tube with potential errors
    When I configure the tube with the R script that contains syntax errors:
      """
      calculate <- function(data) {
        # Missing closing bracket
        if (length(data$values) > 0 {
          result <- mean(data$values)
        }
        return(result)
      }
      """
    And I pass the following data:
      | values    |
      | 10.5      |
      | 15.2      |
    Then the tube should report a syntax error
    And the error message should contain meaningful debugging information
    And the tube state should be marked as failed
    And the system should maintain stability despite the R script error

  @packages @external-libraries
  Scenario: Use advanced R packages for scientific calculation
    Given I have created an R calculation tube with external package dependencies
    When I configure the tube with the R script using specialized libraries:
      """
      advanced_analysis <- function(data) {
        # Load required packages
        library(dplyr)
        library(ggplot2)
        library(tidyr)
        library(broom)
        
        # Convert to data frame for easier manipulation
        df <- data.frame(x = data$x, y = data$y)
        
        # Fit linear model
        model <- lm(y ~ x, data = df)
        model_summary <- tidy(model)
        
        # Predictions
        new_data <- data.frame(x = seq(min(df$x), max(df$x), length.out = 20))
        predictions <- predict(model, new_data, interval = "prediction")
        
        return(list(
          coefficients = model_summary$estimate,
          p_values = model_summary$p.value,
          r_squared = summary(model)$r.squared,
          predictions = predictions
        ))
      }
      """
    And I pass the following scientific data:
      | x         | y         |
      | 1.2       | 3.4       |
      | 2.3       | 4.5       |
      | 3.1       | 5.2       |
      | 4.8       | 7.8       |
      | 5.2       | 8.1       |
      | 6.7       | 10.2      |
      | 7.5       | 11.6      |
    Then the tube should execute successfully
    And the output should contain regression coefficients
    And the output should include predicted values
    And the R-squared value should indicate a good fit