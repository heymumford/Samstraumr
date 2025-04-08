# ---------------------------------------------------------------------------------------
# TransformerTubeTest.feature - Tests for Transformer Tube Pattern
#
# This feature file contains tests for tubes that implement the Transformer pattern,
# which transforms input data according to specified rules.
# ---------------------------------------------------------------------------------------

@L1_Component @Functional @DataFlow
Feature: Transformer Tube - Data Transformation with Conditional Logic
  # This feature verifies that Transformer tubes correctly apply transformation rules to input data

  @ATL @L1_Composite @Init @Flow @Transformer
  Scenario Outline: Transformer tube transforms data based on conditional rules
    # Purpose: Verify that transformer tubes correctly apply conditional transformation rules to data
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

  @BTL @L1_Composite @Runtime @Flow @Transformer @Performance
  Scenario Outline: Transformer tube maintains performance under high volume
    # Purpose: Ensure transformer tubes can handle high data volumes efficiently
    Given a transformer tube is configured with transformation rule <rule>
    When <dataVolume> data items are processed through the transformer
    Then all transformations should complete within <timeLimit> milliseconds
    And all output data should correctly reflect the applied transformation

    Examples:
      | rule            | dataVolume | timeLimit |
      | simple mapping  | 1000       | 500       |
      | complex formula | 500        | 750       |
      | conditional     | 2000       | 1000      |

@ErrorHandling  @L1_Composite @Runtime @DataFlow @Transformer @Resilience
  Scenario: Transformer tube handles invalid input data gracefully
    # Purpose: Verify transformer tubes properly handle malformed input data
    Given a transformer tube is configured for string transformation
    When invalid data of incorrect type is provided
    Then the transformer should detect the invalid input
    And an appropriate error should be logged for the invalid input
    And the transformer should continue operating without failure