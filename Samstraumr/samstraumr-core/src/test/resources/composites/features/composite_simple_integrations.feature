Feature: Source Tube - Data Ingestion and Aggregation
  Scenario Outline: Ensure the source tube collects data from multiple inputs and feeds it into the system correctly
    Given multiple source tubes are initialized to provide <sourceCount> inputs
    And test data of size <inputSize> is generated and available for ingestion
    And all tubes are ready and initialized
    When the data is collected
    Then the system should aggregate the inputs into a single dataset containing all provided data
    And the aggregated dataset should be available for further processing

    Examples:
      | sourceCount | inputSize |
      | 2           | 100       |
      | 5           | 200       |
      | 10          | 500       |

Feature: Connector Tube - Input Validation and Routing
  Scenario Outline: Ensure the connector tube validates inputs and routes them based on predefined rules
    Given a connector tube is initialized and raw input data <inputData> is provided to the system
    And predefined validation rule <validationRule> is configured
    And the connector tube is fully functional and ready for validation
    When the connector tube processes the input data
    Then the input data should be validated according to the predefined validation rule
    And valid data should be routed to the appropriate processing tube if applicable
    And invalid data should be flagged with an appropriate error message if applicable

    Examples:
      | inputData     | validationRule | expectedOutcome     |
      | validData     | rule1          | routed correctly    |
      | invalidData   | rule2          | error message       |
      | partialData   | rule3          | routed with notice  |

Feature: Transformer Tube - Data Transformation with Conditional Logic
  Scenario Outline: Ensure the transformer tube transforms data based on conditional rules
    Given a transformer tube is initialized and data with value <inputValue> requires transformation
    And conditional transformation rule <condition> is configured
    And the transformer tube is in a ready state for processing
    When the transformer tube applies conditional logic
    Then the data should be transformed according to the specified condition
    And the transformation should produce an output of <transformedValue>

    Examples:
      | inputValue | condition  | transformedValue |
      | 5          | greater    | 10               |
      | 3          | less       | 1                |
      | 7          | equal      | 7                |

Feature: Aggregator Tube - Combining Multiple Inputs
  Scenario Outline: Ensure the aggregator tube combines data from multiple sources
    Given multiple input tubes are initialized to provide <inputCount> data points each of size <dataPointSize>
    And the aggregator tube is initialized and ready to process data
    When the aggregator tube processes the data
    Then the outputs should be combined into a single cohesive result with an expected combined size of <expectedCombinedSize>
    And the combined output should be validated to ensure no data loss

    Examples:
      | inputCount | dataPointSize | expectedCombinedSize |
      | 3          | 50            | 150                  |
      | 5          | 20            | 100                  |
      | 10         | 10            | 100                  |

Feature: Splitter Tube - Data Splitting and Sequencing
  Scenario Outline: Ensure the splitter tube splits data into multiple streams and sequences them properly
    Given a splitter tube is initialized with a single input stream of size <inputStreamSize>
    And the input stream contains structured data that can be split
    And the splitter tube is configured and ready for data splitting
    When the splitter tube splits the data into <splitCount> streams
    Then multiple output streams should be created
    And each output stream should be sequenced according to the <sequenceType> sequence rules

    Examples:
      | inputStreamSize | splitCount | sequenceType |
      | 100             | 4          | ascending    |
      | 50              | 2          | descending   |
      | 200             | 5          | random       |

Feature: Filter Tube - Data Filtering and Reduction
  Scenario Outline: Ensure the filter tube filters out unnecessary data before reduction
    Given a filter tube is initialized with data of size <rawDataSize> containing unnecessary information
    And the filtering criteria <filteringCriteria> are defined for the test data
    And the filter tube is fully functional for filtering
    When the filter tube processes the data
    Then irrelevant data should be filtered out based on filtering criteria
    And only <filteredDataSize> necessary data should be passed on for further processing

    Examples:
      | rawDataSize | filteredDataSize | filteringCriteria    |
      | 100         | 50               | remove nulls         |
      | 80          | 30               | remove duplicates    |
      | 150         | 120              | apply threshold      |

Feature: Reducer Tube - Reducing Inputs
  Scenario Outline: Ensure the reducer tube condenses multiple inputs into a single output
    Given a reducer tube is initialized with <inputCount> data points
    And the reduction logic <reductionType> is configured to combine the data points
    And the reducer tube is operational for reduction
    When the reducer tube processes the data
    Then it should produce a single reduced output of <expectedOutput> that represents the combined value of the inputs
    And the reduction logic should be verified to ensure accuracy

    Examples:
      | inputCount | reductionType | expectedOutput |
      | 10         | sum           | 55             |
      | 5          | average       | 3              |
      | 8          | max           | 10             |

Feature: Output Tube - Data Delivery
  Scenario Outline: Ensure the output tube delivers final data to an external system
    Given an output tube is initialized with data of size <dataSize> ready for output
    And the target external system <targetSystemType> is available to receive the output
    And the output tube is ready to deliver the data
    When the output tube processes the data
    Then the data should be delivered to the external system
    And the delivery should be confirmed with a <deliveryStatus> status

    Examples:
      | dataSize | targetSystemType | deliveryStatus |
      | 100      | database         | success        |
      | 200      | API endpoint     | success        |
      | 50       | file system      | success        |

Feature: Stateful Tube - State Retention
  Scenario Outline: Ensure the stateful tube maintains its internal state across multiple operations
    Given a stateful tube is initialized with an initial state of <initialState>
    And the system state needs to be retained across <operationsPerformed>
    And the stateful tube is ready to process and retain state
    When the stateful tube processes data
    Then the internal state should remain consistent and be updated to <finalState>
    And subsequent operations should reflect the updated state

    Examples:
      | initialState | operationsPerformed   | finalState |
      | 10           | increment twice       | 12         |
      | 5            | reset, increment      | 1          |
      | 8            | decrement once        | 7          |

Feature: Control Tube - Conditional Routing
  Scenario Outline: Ensure the control tube directs data flow based on conditional logic
    Given a control tube is initialized with multiple routing paths
    And routing condition <condition> is configured for input <inputData>
    And the control tube is fully operational for routing
    When the control tube processes the data
    Then the data should be routed according to the predefined condition to <expectedRoute>
    And each routing decision should be logged for traceability

    Examples:
      | inputData | condition | expectedRoute |
      | data1     | path1     | routeA        |
      | data2     | path2     | routeB        |
      | data3     | default   | routeC        |

Feature: Enrichment Tube - Data Enrichment
  Scenario Outline: Ensure the enrichment tube adds necessary context to the data
    Given an enrichment tube is initialized and raw data <rawData> requires additional context
    And enrichment data source <enrichmentSource> is defined and available
    And the enrichment tube is ready to enrich the data
    When the enrichment tube processes the data
    Then the data should be enriched with additional information resulting in <enrichedData>
    And the enriched data should meet the expected structure and completeness

    Examples:
      | rawData    | enrichmentSource | enrichedData       |
      | baseData1  | sourceA          | dataWithContextA   |
      | baseData2  | sourceB          | dataWithContextB   |
      | baseData3  | sourceC          | dataWithContextC   |

Feature: Amplifier Tube - Signal Amplification
  Scenario Outline: Ensure the amplifier tube boosts important signals for further processing
    Given an amplifier tube is initialized with a weak signal of strength <inputSignalStrength>
    And amplification factor <amplificationFactor> is configured
    And the amplifier tube is ready for amplification
    When the amplifier tube processes the signal
    Then the signal should be amplified to a specified strength level of <expectedOutputStrength>
    And the amplified signal should be verified to ensure it meets processing requirements

    Examples:
      | inputSignalStrength | amplificationFactor | expectedOutputStrength |
      | 0.1                 | 10                  | 1.0                    |
      | 0.5                 | 5                   | 2.5                    |
      | 0.3                 | 8                   | 2.4                    |

Feature: Fatigue Tube - System Fatigue Monitoring
  Scenario Outline: Ensure the fatigue tube tracks system degradation over time
    Given a fatigue tube is initialized to monitor a process with duration <processDuration>
    And the threshold time is set to <thresholdTime>
    And the fatigue tube is prepared for monitoring
    When the fatigue tube monitors the system
    Then it should detect signs of system fatigue such as increased processing time or error rates
    And it should generate an alert if fatigue thresholds are exceeded, resulting in <expectedAlert>

    Examples:
      | processDuration | thresholdTime | expectedAlert |
      | 1000            | 800           | alert         |
      | 500             | 600           | no alert      |
      | 1500            | 1200          | alert         |

Feature: Monitor Tube - Signal Monitoring
  Scenario Outline: Ensure the monitor tube observes signals without altering them
    Given a monitor tube is initialized to observe multiple signals generated at <signalFrequency> frequency
    And the monitor tube is ready for signal observation
    When the monitor tube observes them
    Then the signals should be logged accurately with <expectedLogCount> entries
    And no modifications should be made to the observed signals

    Examples:
      | signalFrequency | expectedLogCount |
      | high            | 100              |
      | medium          | 50               |
      | low             | 20               |
