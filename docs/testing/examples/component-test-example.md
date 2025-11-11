# Component Test Example

This document provides a reference example for creating component tests in the Samstraumr framework.

## Testing Component Functionality

Components in Samstraumr follow the clean architecture pattern and need thorough testing. The example below demonstrates how to create a comprehensive component test.

### BDD Feature File Example

```gherkin
# File: features/component/component-state-tests.feature
Feature: Component State Management
  As a developer
  I want to verify that components properly manage their state
  So that the system behaves predictably

  Background:
    Given a clean component environment
    And a standard component implementation

  @L1_Component @State @Functional
  Scenario: Component transitions through valid state changes
    When the component is initialized
    Then the component should be in "INITIALIZED" state
    When the component is started
    Then the component should be in "RUNNING" state
    When the component is stopped
    Then the component should be in "STOPPED" state

  @L1_Component @State @ErrorHandling
  Scenario: Component prevents invalid state transitions
    Given the component is in "INITIALIZED" state
    When I attempt to transition the component to "STOPPED" state
    Then the operation should fail with "InvalidStateTransitionException"
    And the component should remain in "INITIALIZED" state
```

### Step Definition Example

```java
package org.s8r.test.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.s8r.component.Component;
import org.s8r.component.ComponentException;
import org.s8r.component.State;
import org.s8r.test.annotation.ComponentTest;
import static org.junit.jupiter.api.Assertions.*;

@ComponentTest
public class ComponentStateSteps {
    
    private Component component;
    private Exception lastException;
    
    @Given("a clean component environment")
    public void setupEnvironment() {
        // Initialize test environment
        this.lastException = null;
    }
    
    @Given("a standard component implementation")
    public void createStandardComponent() {
        // Create a test component
        this.component = new TestComponent();
    }
    
    @Given("the component is in {string} state")
    public void setComponentState(String stateName) {
        State state = State.valueOf(stateName);
        // Set component to the specified state
        if (state == State.INITIALIZED) {
            component.initialize();
        } else if (state == State.RUNNING) {
            component.initialize();
            component.start();
        } else if (state == State.STOPPED) {
            component.initialize();
            component.start();
            component.stop();
        }
    }
    
    @When("the component is initialized")
    public void initializeComponent() {
        component.initialize();
    }
    
    @When("the component is started")
    public void startComponent() {
        component.start();
    }
    
    @When("the component is stopped")
    public void stopComponent() {
        component.stop();
    }
    
    @When("I attempt to transition the component to {string} state")
    public void attemptStateTransition(String stateName) {
        try {
            if (stateName.equals("RUNNING")) {
                component.start();
            } else if (stateName.equals("STOPPED")) {
                component.stop();
            }
        } catch (Exception e) {
            lastException = e;
        }
    }
    
    @Then("the component should be in {string} state")
    public void verifyComponentState(String stateName) {
        State expectedState = State.valueOf(stateName);
        assertEquals(expectedState, component.getState());
    }
    
    @Then("the operation should fail with {string}")
    public void verifyOperationFailed(String exceptionName) {
        assertNotNull(lastException);
        assertEquals(exceptionName, lastException.getClass().getSimpleName());
    }
    
    @Then("the component should remain in {string} state")
    public void verifyComponentStateUnchanged(String stateName) {
        State expectedState = State.valueOf(stateName);
        assertEquals(expectedState, component.getState());
    }
    
    // Inner test component implementation
    private static class TestComponent implements Component {
        private State state = State.CREATED;
        
        @Override
        public void initialize() {
            if (state != State.CREATED) {
                throw new ComponentException("Cannot initialize from " + state);
            }
            state = State.INITIALIZED;
        }
        
        @Override
        public void start() {
            if (state != State.INITIALIZED) {
                throw new ComponentException("Cannot start from " + state);
            }
            state = State.RUNNING;
        }
        
        @Override
        public void stop() {
            if (state != State.RUNNING) {
                throw new ComponentException("Cannot stop from " + state);
            }
            state = State.STOPPED;
        }
        
        @Override
        public State getState() {
            return state;
        }
    }
}
```

## Test Runner Integration

For this type of component test, you'd run it with:

```bash
# Run all component tests
./s8r-test component

# Run only state management component tests
./s8r-test --tags "@L1_Component and @State"

# Run the component functional tests
./s8r-test --tags "@L1_Component and @Functional"
```

## Key Components of Good Component Tests

1. **Clear Scenarios**: Each scenario tests one specific aspect of component behavior
2. **Proper Tagging**: Tests are tagged with both layer (@L1_Component) and functionality (@State)
3. **Isolation**: Tests use a clean environment for each scenario
4. **Full State Coverage**: Test both valid and invalid transitions
5. **Error Cases**: Explicitly test error conditions and exceptions
6. **Clean Setup**: Background steps ensure consistent test environment
7. **Assertions**: Clear and specific assertions about the expected outcomes