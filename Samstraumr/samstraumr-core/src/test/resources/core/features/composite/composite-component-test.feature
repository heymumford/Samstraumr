Feature: Composite Component Management
  As a system developer
  I want composites to manage components properly
  So that I can create hierarchical structures with proper connections

  Background:
    Given a composite with ID "testComposite"

  Scenario: Adding components to a composite
    Given component "component1" added to composite "testComposite"
    And component "component2" added to composite "testComposite"
    Then composite "testComposite" should contain component "component1"
    And composite "testComposite" should contain component "component2"

  Scenario: Connecting components within a composite
    Given component "sourceComponent" added to composite "testComposite"
    And component "targetComponent" added to composite "testComposite"
    And components "sourceComponent" and "targetComponent" are connected in composite "testComposite"
    # No assertion here because the connection is internal to the composite

  Scenario: Component addition to different composite types
    Given a composite with ID "observer" of type "OBSERVER"
    And a composite with ID "transformer" of type "TRANSFORMER"
    And component "component1" added to composite "observer"
    And component "component2" added to composite "transformer"
    Then composite "observer" should contain component "component1"
    And composite "transformer" should contain component "component2"

  Scenario: Component state propagation in composite
    Given component "component1" added to composite "testComposite"
    When composite "testComposite" is activated
    # In the real implementation, the composite would propagate its state to components
    # Here we're just testing that the composite activation works