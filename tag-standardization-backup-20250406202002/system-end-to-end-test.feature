# ---------------------------------------------------------------------------------------
# SystemEndToEndTest.feature - End-to-End System Tests
#
# This feature file contains end-to-end tests for full system functionality, 
# validating that multiple machines can work together to accomplish complex
# processing tasks.
# ---------------------------------------------------------------------------------------

@L3_System @Functional @DataFlow
Feature: End-to-End System Integration
  As a system architect
  I want to verify that multiple machines can work together as a complete system
  So that I can ensure the framework supports complex end-to-end scenarios

  Background:
    Given a clean system environment
    And all system dependencies are available

  @L3_System @Functional @DataFlow
  Scenario: Complete data processing pipeline should handle end-to-end flow
    Given a data ingestion machine that reads from external sources
    And a data transformation machine that normalizes data
    And a data enrichment machine that adds contextual information
    And a data storage machine that persists results
    When 100 records are processed through the complete pipeline
    Then all records should be successfully processed
    And the data should maintain integrity throughout the pipeline
    And the system should track the full data lineage
    And the processing time should be within acceptable limits

  @L3_System @Functional @Resilience
  Scenario: System should recover from component failures
    Given a multi-machine processing system
    And a monitoring system that detects failures
    When a critical component in the processing pipeline fails
    Then the system should detect the failure
    And the system should isolate the failed component
    And the system should reroute traffic to a backup component
    And processing should continue with minimal disruption
    And the system should attempt to recover the failed component

  @L3_System @Performance
  Scenario: System should scale to handle varying load
    Given a system configured for dynamic scaling
    And current resource utilization is monitored
    When the input load increases by 300%
    Then the system should allocate additional resources
    And processing throughput should increase proportionally
    And latency should remain within acceptable parameters
    And resource utilization should stabilize at an efficient level
    And when the load decreases, resources should be released

  @L3_System @Functional @Configuration
  Scenario: System configuration should be applied across all components
    Given a system-wide configuration profile for "high-security" processing
    When the configuration is deployed to the system
    Then all machines should apply the security settings
    And all components should enforce the security constraints
    And data processing should comply with the security requirements
    And the system should log the configuration changes across components

  @L3_System @Functional @Monitoring
  Scenario: System-wide monitoring should track health and performance
    Given a system with 5 interconnected machines
    And a system monitoring service
    When the system processes data for 10 minutes under load
    Then the monitoring service should track performance metrics
    And the system should generate health status for all components
    And performance bottlenecks should be identified
    And resource utilization should be reported accurately
    And monitoring data should be available through the monitoring API

  @L3_System @Resilience @ErrorHandling
  Scenario: System should recover from catastrophic failure
    Given a system with persistent state
    And a system backup mechanism
    When a catastrophic failure affects multiple machines
    Then the system should initiate recovery procedures
    And system state should be restored from persistent storage
    And machines should be reinitialized with correct configuration
    And connections between machines should be reestablished
    And processing should resume from the last consistent checkpoint

  @L3_System @Functional @Lifecycle
  Scenario: System should support rolling upgrades
    Given a running system processing live data
    When a new version is deployed using a rolling upgrade strategy
    Then each component should be upgraded without stopping the system
    And data processing should continue during the upgrade
    And the system should verify compatibility between components
    And after upgrade completion, all components should be on the new version
    And the system should function correctly with the new version

  @L3_System @Performance
  Scenario Outline: System should maintain performance under various load conditions
    Given a complete system with all components
    When processing <records> records with <concurrency> concurrent streams
    Then throughput should be at least <min_throughput> records per second
    And average latency should be less than <max_latency> milliseconds
    And error rate should be below <max_error_rate> percent
    And resource utilization should not exceed <max_resource> percent

    Examples:
      | records | concurrency | min_throughput | max_latency | max_error_rate | max_resource |
      | 1000    | 5           | 50             | 200         | 0.1            | 60           |
      | 10000   | 10          | 100            | 300         | 0.5            | 75           |
      | 100000  | 20          | 200            | 500         | 1.0            | 85           |