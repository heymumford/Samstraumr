Feature: Adam Tube Self-Awareness and Monitoring
  As a Samstraumr framework user
  I want my tubes to maintain continuous self-awareness
  So that they can operate efficiently and adapt to changing conditions

  Background: Tube Initialization
    Given a fully initialized tube exists
    And basic monitoring systems are active

  @self-awareness @vitals @P0
  Scenario: Tube maintains awareness of vital statistics
    When the tube performs a self-check
    Then it should report the following current vital statistics:
      | Vital          | Type      | Update Frequency | Description                    |
      | memoryUsage    | Numeric   | 1 second         | Current memory consumption    |
      | cpuUtilization | Numeric   | 1 second         | Current CPU usage             |
      | threadCount    | Integer   | 1 second         | Active thread count           |
      | uptime         | Duration  | Continuous       | Time since initialization     |
    And each vital statistic should be within normal operating ranges
    And abnormal readings should trigger alerts with descriptions

  @monitoring @resource @P0
  Scenario: Tube monitors and trends resource utilization
    When the tube analyzes its resource usage patterns
    Then it should maintain rolling metrics for:
      | Metric         | Window     | Aggregation     | Threshold Alert      |
      | memoryTrend    | 5 minutes  | Average         | > 85% memory usage   |
      | cpuTrend       | 5 minutes  | Average         | > 75% CPU utilization|
      | gcFrequency    | 5 minutes  | Count           | > 10 per minute      |
      | threadGrowth   | 5 minutes  | Delta           | > 20% increase       |
    And all metrics should include timestamps for tracking
    And the system should retain historical data for the last hour

  @health @assessment @P0
  Scenario: Tube performs periodic health assessments
    When a health check cycle executes
    Then it should evaluate health in the following aspects:
      | Aspect           | Metric                          | Healthy Range    |
      | memoryHeadroom   | Available memory percentage     | >= 20%           |
      | cpuHeadroom      | Available CPU percentage        | >= 25%           |
      | responseTime     | Operation latency               | < 100ms          |
      | errorRate        | Errors per minute               | < 1              |
    And all health checks should be logged with details
    And critical alerts should be triggered for unhealthy states

  @adaptation @monitoring @P0
  Scenario: Tube adjusts monitoring parameters under resource constraints
    When system resources become constrained
    Then the tube should adjust monitoring parameters:
      | Parameter        | Normal State | Constrained State |
      | checkFrequency   | 1 second     | 5 seconds         |
      | metricResolution | High         | Medium            |
      | historyRetention | 1 hour       | 15 minutes        |
    And the adjustments should be recorded in logs
    And critical monitoring should remain unaffected

  @reporting @self-awareness @P0
  Scenario: Tube generates comprehensive self-awareness reports
    When a status report is requested
    Then the report should contain the following sections:
      | Section         | Contents                        | Format    |
      | identity        | Birth certificate details       | JSON      |
      | vitals          | Current vital statistics        | JSON      |
      | health          | Health assessment results       | JSON      |
      | metrics         | Recent performance metrics      | JSON      |
      | alerts          | Active and recent alerts        | JSON      |
    And the report should be timestamped and detailed
    And include indicators of data freshness and reliability

  @boundaries @monitoring @P0
  Scenario: Tube recognizes and respects operational boundaries
    When the tube evaluates its operational capacity
    Then it should maintain awareness of its operational limits:
      | Boundary        | Measurement                     | Limit Type |
      | maxMemory       | Maximum memory allocation       | Hard       |
      | maxThreads      | Maximum thread count            | Soft       |
      | maxCpu          | Maximum CPU utilization         | Soft       |
      | maxLatency      | Maximum operation latency       | Hard       |
    And it should maintain a safety margin below each limit
    And issue predictive alerts when approaching limits
