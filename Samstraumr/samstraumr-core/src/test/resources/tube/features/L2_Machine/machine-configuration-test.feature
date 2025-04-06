# ---------------------------------------------------------------------------------------
# MachineConfigurationTest.feature - Machine Configuration Tests
#
# This feature file contains tests for machine configuration and initialization,
# validating that machines can be properly configured with different settings,
# parameters, and policies.
# ---------------------------------------------------------------------------------------

@L2_Machine @Configuration @Integration
Feature: Machine Configuration
  As a system integrator
  I want to configure machines with different settings and parameters
  So that they can be tailored to specific processing requirements

  Background:
    Given a new environment is initialized

  @Configuration @Basic
  Scenario: Basic machine configuration properties should be applied
    Given a configuration with the following properties:
      | Property              | Value         |
      | name                  | test-machine  |
      | maxConcurrency        | 4             |
      | bufferSize            | 1000          |
      | processingTimeout     | 5000          |
    When I create a machine with this configuration
    Then the machine should have the name "test-machine"
    And the machine should use a max concurrency of 4
    And the machine should have a buffer size of 1000
    And the machine should have a processing timeout of 5000 ms

  @Configuration @DefaultValues
  Scenario: Default configuration values should be applied when not specified
    Given a minimal configuration with only required properties
    When I create a machine with this configuration
    Then the machine should have default values for optional properties
    And the machine should be in a valid initial state

  @Configuration @Validation
  Scenario: Invalid configuration should be rejected
    Given a configuration with invalid properties:
      | Property              | Value         |
      | maxConcurrency        | -1            |
      | bufferSize            | 0             |
    When I attempt to create a machine with this configuration
    Then the creation should fail with appropriate validation errors
    And no machine resources should be allocated

  @Configuration @Profiles
  Scenario Outline: Configuration profiles should apply appropriate settings
    Given a machine configuration with profile "<profile>"
    When I create a machine with this configuration
    Then the machine should have settings appropriate for a <profile> workload
    And the machine's performance characteristics should match the profile

    Examples:
      | profile       |
      | high-throughput |
      | low-latency   |
      | balanced      |
      | memory-optimized |

  @Configuration @Dynamic
  Scenario: Machine configuration should support runtime updates
    Given a machine with initial configuration
    When I update the configuration parameter "bufferSize" to 2000
    Then the machine should apply the new buffer size
    And running processes should not be interrupted
    And subsequent operations should use the new configuration value

  @Configuration @Component
  Scenario: Machine should configure its components consistently
    Given a machine configuration with component-specific settings
    When I create a machine with this configuration
    Then each component should receive its specific configuration
    And common settings should be propagated to all components
    And component configuration should be validated

  @Configuration @Persistence
  Scenario: Machine configuration should be persistable and restorable
    Given a machine with a complex configuration
    When the machine configuration is saved to storage
    And a new machine is created from the stored configuration
    Then the new machine should have identical configuration
    And the restored machine should function identically to the original