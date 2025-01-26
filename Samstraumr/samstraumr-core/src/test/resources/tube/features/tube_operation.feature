Feature: Adam Tube Core Operations
  As a Samstraumr framework user
  I want my tubes to perform reliable operations
  So that they can process data and adapt to changing conditions effectively

  Background:
    Given an operational tube is fully initialized
    And the tube has completed its self-awareness checks
    And the tube is in a READY state

  @core @P0 @lifecycle
  Scenario: Tube manages its operational lifecycle
    When the tube is operational
    Then it should maintain its lifecycle state machine:
      | State       | Description                    | Allowed Next States     |
      | READY       | Initialized and waiting        | ACTIVE, PAUSED         |
      | ACTIVE      | Processing data               | PAUSED, READY          |
      | PAUSED      | Temporarily suspended         | READY, ACTIVE          |
      | STOPPING    | Graceful shutdown in progress | STOPPED                |
      | STOPPED     | Shut down completely          | None                   |
    And each state transition should be logged
    And state history should be maintained

  @core @P0 @processing
  Scenario: Tube handles basic data processing
    Given the tube is in READY state
    When data is submitted for processing
    Then it should perform the following steps:
      | Step           | Action                        | Validation              |
      | validation     | Verify input format           | Format matches spec     |
      | preparation    | Prepare processing context    | Resources available     |
      | execution      | Process the data             | Processing completes    |
      | verification   | Verify results               | Output matches spec     |
    And each step should be monitored
    And processing metrics should be collected

  @core @P0 @feedback
  Scenario: Tube maintains internal feedback loops
    When the tube is processing data
    Then it should maintain these feedback mechanisms:
      | Mechanism      | Purpose                       | Update Frequency   |
      | performance    | Processing speed optimization | Every 100 ops      |
      | resources      | Resource usage optimization   | Every 5 seconds    |
      | errors         | Error pattern detection       | On error           |
      | health         | Overall health maintenance    | Every 10 seconds   |
    And feedback data should influence behavior
    And adaptation patterns should be logged

  @core @P0 @resources
  Scenario: Tube manages resource allocation
    When performing operations
    Then it should manage resources within these constraints:
      | Resource       | Normal Range | Maximum | Action on Exceed     |
      | memory         | 0-75%        | 85%     | Throttle processing  |
      | cpu            | 0-70%        | 80%     | Reduce batch size    |
      | threads        | 0-80%        | 90%     | Queue new work       |
      | queue depth    | 0-1000       | 2000    | Reject new work      |
    And resource usage should be predictable
    And resource limits should be enforced

  @core @P0 @adaptation
  Scenario: Tube adapts operation patterns
    Given the tube has been processing data
    When performance patterns are analyzed
    Then it should adapt these operational parameters:
      | Parameter      | Initial | Adaptive Range | Adaptation Trigger    |
      | batchSize      | 100     | 50-500        | Queue depth trend     |
      | timeout        | 1000ms  | 500-2000ms    | Error rate           |
      | threadPool     | 10      | 5-20          | CPU utilization      |
      | bufferSize     | 1000    | 500-5000      | Memory usage         |
    And adaptations should be gradual
    And changes should be reversible

  @core @P0 @stability
  Scenario: Tube maintains operational stability
    When processing under varying conditions
    Then it should maintain these stability metrics:
      | Metric         | Target | Acceptable Range | Recovery Action      |
      | throughput     | 100/s  | 50-150/s        | Adjust batch size    |
      | latency        | 50ms   | 10-100ms        | Modify thread count  |
      | error rate     | <1%    | 0-2%            | Reduce load          |
      | success rate   | >99%   | 98-100%         | Investigate patterns |
    And stability should be self-correcting
    And chronic instability should be reported

  @core @P0 @shutdown
  Scenario: Tube performs graceful shutdown
    When a shutdown is requested
    Then it should execute these shutdown phases:
      | Phase          | Action                        | Maximum Duration |
      | preparation    | Complete current work         | 5 seconds       |
      | notification   | Notify dependents            | 1 second        |
      | cleanup        | Release resources            | 5 seconds       |
      | verification   | Verify clean state           | 1 second        |
    And all phases should complete successfully
    And shutdown status should be logged
