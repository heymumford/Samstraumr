Feature: Adam Tube Creation and Initialization
  As a Samstraumr framework user
  I want to create foundational "Adam" tubes
  So that I can establish self-aware processing units that understand their environment

  Background:
    Given the Samstraumr framework is initialized
    And the system environment is stable

  @core @P0 @birth
  Scenario: Adam tube generates immutable birth certificate
    When a new tube is created with purpose "data_processor"
    Then it should establish an immutable birth certificate containing:
      | Property         | Validation                  | Description                         |
      | universalId     | UUID v4 format              | Globally unique identifier         |
      | birthTime       | UTC timestamp               | Moment of creation                 |
      | purpose         | Non-empty string            | Declared reason for existence      |
      | creatorId       | String or null              | ID of creating entity (if any)     |
    And these properties should be accessible but unmodifiable
    And the birth certificate should be logged with INFO level

  @core @P0 @environment
  Scenario: Adam tube performs comprehensive environment detection
    When a new tube initializes
    Then it should detect and record its complete environment:
      | Category    | Property           | Description                  | Validation               |
      | Hardware    | processorId        | CPU identifier              | Non-empty string         |
      | Hardware    | physicalCores      | Physical CPU cores          | Integer > 0              |
      | Hardware    | logicalCores       | Logical CPU cores           | Integer > 0              |
      | Hardware    | architecture       | CPU architecture            | Non-empty string         |
      | Memory      | totalMemory        | Total system memory         | Long > 0                 |
      | Memory      | availableMemory    | Available memory            | Long > 0                 |
      | Memory      | swapTotal          | Total swap space            | Long >= 0                |
      | System      | osName             | Operating system name       | Non-empty string         |
      | System      | osVersion          | Operating system version    | Non-empty string         |
      | System      | osArchitecture     | Operating system arch       | Non-empty string         |
      | Platform    | containerized      | Container detection         | Boolean                  |
      | Platform    | containerType      | Container technology        | String                   |
      | Platform    | virtualized        | Virtualization detection    | Boolean                  |
      | Platform    | virtualizationType | Virtualization technology   | String                   |
      | Runtime     | javaVersion        | Java version                | Non-empty string         |
      | Runtime     | javaVendor         | Java vendor                 | Non-empty string         |
      | Runtime     | javaHome           | Java installation path      | Valid file path          |
    And all environment properties should be properly logged
    And environment detection warnings should be handled gracefully

  @core @P0 @resources
  Scenario: Adam tube establishes resource monitoring baselines
    When a new tube evaluates available resources
    Then it should establish monitoring baselines for:
      | Resource        | Metric                | Validation                        |
      | cpu            | baselineUsage         | Percentage between 0-100          |
      | cpu            | availableThreads      | Integer > 0                       |
      | memory         | baselineUsage         | Bytes > 0                         |
      | memory         | maxAllowedUsage       | Bytes > baselineUsage            |
      | disk           | availableSpace        | Bytes > 0                         |
      | network        | interfaceStatus       | List of active interfaces         |
    And it should create resource monitoring strategies
    And it should log baseline measurements

  @core @P0 @monitoring
  Scenario: Adam tube initializes self-monitoring systems
    When a new tube sets up monitoring
    Then it should establish the following monitoring components:
      | Component       | Purpose                          | Update Frequency  |
      | healthCheck     | Basic operational status         | Continuous       |
      | resourceMonitor | Resource usage tracking          | Every 5 seconds  |
      | stateTracker    | Lifecycle state management      | On change        |
      | errorDetector   | Anomaly and error detection     | Continuous       |
    And each component should begin collecting data
    And monitoring initialization should be logged

  @core @P0 @readiness
  Scenario: Adam tube verifies operational readiness
    When tube initialization completes
    Then it should verify all systems are operational:
      | System          | Status Check                     | Required State   |
      | identity        | Birth certificate verification   | VALID           |
      | environment     | Environment detection complete   | DETECTED        |
      | resources       | Resource baselines established   | MEASURED        |
      | monitoring      | Monitoring systems active        | OPERATIONAL     |
    And it should publish a readiness event
    And it should log successful initialization
    And it should be ready to process work

  @core @P0 @introspection
  Scenario: Adam tube demonstrates self-awareness
    Given a fully initialized tube
    When the tube is queried about its state
    Then it should provide accurate information about:
      | Aspect          | Information Type                | Validation          |
      | identity        | Complete birth certificate      | All fields valid    |
      | environment     | Current system state            | Recently updated    |
      | resources       | Current usage metrics           | Within baselines    |
      | health          | Overall operational status      | HEALTHY            |
    And all information should be current within the last 5 seconds
