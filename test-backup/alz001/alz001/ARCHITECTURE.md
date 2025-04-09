# ALZ001 Test Suite Architecture

This document outlines the architecture for implementing the ALZ001 test suite step definitions. The architecture is designed to support the BDD feature files while following TDD principles and aligning with the Samstraumr framework.

## 1. High-Level Architecture

The ALZ001 test suite architecture consists of the following key components:

```
┌───────────────────────────────────────────────┐
│                 Test Execution                │
│  ┌─────────────┐   ┌──────────┐   ┌────────┐  │
│  │ ALZ001Tests │───│ Test Tags│───│ Runner │  │
│  └─────────────┘   └──────────┘   └────────┘  │
└───────────────────────────────────────────────┘
                        │
┌───────────────────────────────────────────────┐
│              Step Definitions                 │
│  ┌─────────────┐   ┌──────────┐   ┌────────┐  │
│  │CapabilitySteps│─│BaseSteps │───│Context │  │
│  └─────────────┘   └──────────┘   └────────┘  │
└───────────────────────────────────────────────┘
                        │
┌───────────────────────────────────────────────┐
│               Mock Components                 │
│  ┌─────────────┐   ┌──────────┐   ┌────────┐  │
│  │ Components  │─┬─│Composites│─┬─│Machines│  │
│  └─────────────┘ │ └──────────┘ │ └────────┘  │
│                  │              │             │
│  ┌─────────────┐ │ ┌──────────┐ │ ┌────────┐  │
│  │Data Structures│─│ Factories │─│Utilities│  │
│  └─────────────┘   └──────────┘   └────────┘  │
└───────────────────────────────────────────────┘
```

## 2. Package Structure

The ALZ001 test suite uses the following package structure:

```
org.s8r.test
├── annotation
│   └── alz001         # Annotations for test categories
├── runner
│   └── ALZ001Tests.java   # Cucumber test runner
├── steps
│   └── alz001         # Step definition classes
│       ├── base       # Base step functionality and context
│       ├── mock       # Mock implementations
│       │   ├── component  # Component-level mocks
│       │   ├── composite  # Composite-level mocks
│       │   └── machine    # Machine-level mocks
│       └── util       # Testing utilities
└── util               # Common test utilities
```

## 3. Core Components

### 3.1 Test Context

A central shared context allows data exchange between steps. The context is thread-safe and provides type-safe access.

```java
public class ALZ001TestContext {
    private final Map<String, Object> contextMap = new ConcurrentHashMap<>();
    
    public <T> void store(String key, T value) {
        contextMap.put(key, value);
    }
    
    public <T> T retrieve(String key) {
        return (T) contextMap.get(key);
    }
    
    public boolean contains(String key) {
        return contextMap.containsKey(key);
    }
    
    public void clear() {
        contextMap.clear();
    }
}
```

### 3.2 Base Steps

The base steps class provides common functionality for all step definitions.

```java
public class ALZ001BaseSteps {
    protected final ALZ001TestContext context = new ALZ001TestContext();
    protected final TestLogger logger = new TestLogger(getClass());
    
    public void setUp() {
        context.clear();
        // Initialize common resources
    }
    
    public void tearDown() {
        // Clean up common resources
    }
    
    // Common step functionality
    protected void assertStateEquals(String expectedState, String actualState) {
        assertEquals(expectedState, actualState, "State should match expected value");
    }
    
    // Other common methods...
}
```

### 3.3 Mock Component Base Class

A base class for all mock components provides common state management and validation.

```java
public abstract class ALZ001MockComponent {
    protected final String name;
    protected String state = "INITIALIZED";
    
    public ALZ001MockComponent(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public boolean validateState(String expectedState) {
        return state.equals(expectedState);
    }
}
```

## 4. Capability-Specific Components

### 4.1 Step Classes

Each capability has its own step definition class that composes the base steps:

```java
public class ProteinExpressionSteps {
    private final ALZ001BaseSteps baseSteps = new ALZ001BaseSteps();
    
    @Before
    public void setup() {
        baseSteps.setUp();
    }
    
    @After
    public void cleanup() {
        baseSteps.tearDown();
    }
    
    // Step definitions using baseSteps.context
}
```

### 4.2 Mock Components

Each capability implements its own mock components, composites, and machines:

```java
// Component level
public class ProteinExpressionComponent extends ALZ001MockComponent {
    private Map<String, Double> thresholds = new HashMap<>();
    private List<ProteinSample> samples = new ArrayList<>();
    
    // Implementation...
}

// Composite level
public class ProteinExpressionComposite extends ALZ001MockComponent {
    private List<ProteinExpressionComponent> components = new ArrayList<>();
    
    // Implementation...
}

// Machine level
public class ProteinExpressionMachine extends ALZ001MockComponent {
    private List<ProteinExpressionComposite> composites = new ArrayList<>();
    
    // Implementation...
}
```

## 5. Data Exchange

### 5.1 Between Step Definitions

Different step definition classes can exchange data through the ALZ001TestContext:

```java
// In ProteinExpressionSteps
@When("I create a protein marker with level {double}")
public void iCreateAProteinMarkerWithLevel(Double level) {
    ProteinMarker marker = new ProteinMarker("marker1", level);
    baseSteps.context.store("currentMarker", marker);
}

// In NeuronalNetworkSteps
@When("I correlate neuronal density with the current protein marker")
public void iCorrelateNeuronalDensityWithCurrentProteinMarker() {
    ProteinMarker marker = baseSteps.context.retrieve("currentMarker");
    // Use the marker...
}
```

### 5.2 Between Steps in Same Definition Class

Steps within the same definition class can share data through instance variables and the context:

```java
public class TimeSeriesAnalysisSteps {
    private final ALZ001BaseSteps baseSteps = new ALZ001BaseSteps();
    private TimeSeriesComponent currentComponent;
    
    @When("I create a time series component")
    public void iCreateATimeSeriesComponent() {
        currentComponent = new TimeSeriesComponent("ts1");
        baseSteps.context.store("currentTimeSeriesComponent", currentComponent);
    }
    
    @When("I add data to the current component")
    public void iAddDataToTheCurrentComponent() {
        // Can use either the instance variable or context
        currentComponent.addData(new TimeSeriesDataPoint(0, 1.0));
        // Or
        TimeSeriesComponent component = baseSteps.context.retrieve("currentTimeSeriesComponent");
        component.addData(new TimeSeriesDataPoint(1, 2.0));
    }
}
```

## 6. Mock Implementations

### 6.1 Realistic Behavior

Mock implementations simulate realistic behavior without requiring actual implementation:

```java
public class NeuronalNetworkSimulator {
    public void simulateDegeneration(double rate, int timePoints) {
        double initialConnectivity = 1.0;
        List<Double> trajectory = new ArrayList<>();
        
        for (int i = 0; i < timePoints; i++) {
            double timeValue = i / (double) timePoints;
            // Sigmoid decrease in connectivity
            double connectivity = initialConnectivity / (1 + Math.exp((timeValue - 0.5) * 10 * rate));
            trajectory.add(connectivity);
        }
        
        return trajectory;
    }
}
```

### 6.2 Configuration-Based Mock Behavior

Mocks can adjust behavior based on configuration:

```java
public class PredictiveModelComponent extends ALZ001MockComponent {
    private Map<String, Object> hyperparameters = new HashMap<>();
    private double accuracy = 0.5; // Default accuracy
    
    public void configure(Map<String, Object> params) {
        hyperparameters.putAll(params);
        
        // Configure mock behavior based on parameters
        if (params.containsKey("learningRate")) {
            double rate = (double) params.get("learningRate");
            // Higher learning rate can lead to better accuracy but risk of overfitting
            accuracy = 0.7 + (rate * 0.5); // Simple mock formula
            if (rate > 0.1) accuracy *= 0.9; // Simulate overfitting with high rates
        }
        
        setState("CONFIGURED");
    }
}
```

## 7. Validation Approach

### 7.1 Step-Level Validation

Each step performs its own validation:

```java
@Then("the component should have {int} protein markers")
public void theComponentShouldHaveProteinMarkers(Integer expectedCount) {
    ProteinExpressionComponent component = baseSteps.context.retrieve("currentComponent");
    assertEquals(expectedCount, component.getMarkers().size(), 
                "Component should have the expected number of markers");
}
```

### 7.2 Composite Validation

Complex validations can use helper methods:

```java
@Then("the network degeneration should follow expected trajectory")
public void theNetworkDegenerationShouldFollowExpectedTrajectory() {
    NeuronalNetworkComponent component = baseSteps.context.retrieve("currentNetwork");
    List<Double> trajectory = component.getDegenerationTrajectory();
    
    validateMonotonicDecrease(trajectory);
    validateDegenerationRate(trajectory);
}

private void validateMonotonicDecrease(List<Double> values) {
    for (int i = 1; i < values.size(); i++) {
        assertTrue(values.get(i) <= values.get(i-1), 
                  "Values should decrease monotonically");
    }
}
```

## 8. Object Creation Pattern

### 8.1 Factory Pattern

Use factory methods to create mock objects with consistent configuration:

```java
public class ALZ001MockFactory {
    public static ProteinExpressionComponent createProteinComponent(String name) {
        ProteinExpressionComponent component = new ProteinExpressionComponent(name);
        // Configure with defaults
        component.setThreshold("amyloid", 42.0);
        component.setThreshold("tau", 30.0);
        return component;
    }
    
    public static NeuronalNetworkComponent createNetworkWithTopology(String name, String topologyType) {
        NeuronalNetworkComponent component = new NeuronalNetworkComponent(name);
        
        switch (topologyType) {
            case "small-world":
                component.configureSmallWorldTopology(100, 500, 0.1);
                break;
            case "scale-free":
                component.configureScaleFreeTopology(100, 3);
                break;
            // Other topologies...
        }
        
        return component;
    }
}
```

### 8.2 Builder Pattern

For complex objects with many configuration options:

```java
public class TimeSeriesComponentBuilder {
    private String name;
    private int dataPoints = 100;
    private String decompositionMethod = "stl";
    private int seasonalPeriods = 12;
    private boolean robust = true;
    
    public TimeSeriesComponentBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public TimeSeriesComponentBuilder withDataPoints(int dataPoints) {
        this.dataPoints = dataPoints;
        return this;
    }
    
    // Other setters...
    
    public TimeSeriesComponent build() {
        TimeSeriesComponent component = new TimeSeriesComponent(name);
        component.setDecompositionMethod(decompositionMethod);
        component.setSeasonalPeriods(seasonalPeriods);
        component.setRobust(robust);
        
        // Generate mock data if needed
        if (dataPoints > 0) {
            component.generateMockData(dataPoints);
        }
        
        return component;
    }
}
```

## 9. Error Handling

### 9.1 Negative Test Cases

For negative test cases, mock objects can simulate specific errors:

```java
public class EnvironmentalFactorsComponent extends ALZ001MockComponent {
    // Normal methods...
    
    public Map<String, String> validateParameters(Map<String, String> params) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate parameters
        if (params.containsKey("diet")) {
            String diet = params.get("diet");
            if (!validDiets.contains(diet)) {
                errors.put("diet", "Invalid diet type: " + diet);
            }
        }
        
        // Other validations...
        
        return errors;
    }
}
```

### 9.2 Exception Handling

Steps can handle exceptions for validation:

```java
@When("I attempt to create a composite with incompatible models")
public void iAttemptToCreateACompositeWithIncompatibleModels() {
    try {
        PredictiveModelComposite composite = new PredictiveModelComposite("composite");
        PredictiveModelComponent model1 = baseSteps.context.retrieve("geneticModel");
        PredictiveModelComponent model2 = baseSteps.context.retrieve("imagingModel");
        
        composite.addModel(model1);
        composite.addModel(model2);
        
        baseSteps.context.store("operationException", null);
    } catch (SchemaCompatibilityException e) {
        baseSteps.context.store("operationException", e);
    }
}

@Then("the operation should fail with a schema compatibility error")
public void theOperationShouldFailWithASchemaCompatibilityError() {
    Exception e = baseSteps.context.retrieve("operationException");
    assertNotNull(e, "Expected exception was not thrown");
    assertTrue(e instanceof SchemaCompatibilityException, 
              "Exception should be SchemaCompatibilityException");
}
```

## 10. Implementation Strategy

### 10.1 Implementation Order

Implement the step definitions in the following order:

1. Common infrastructure (context, base steps, mock base classes)
2. Component-level implementations (L0_Component)
3. Composite-level implementations (L1_Composite)
4. Machine-level implementations (L2_Machine)
5. System-level implementations (L3_System)

### 10.2 Capability Implementation Order

Implement the capabilities in the following order:

1. Protein Expression (foundation for biological modeling)
2. Neuronal Network (uses protein expression components)
3. Time Series Analysis (foundation for data analysis)
4. Environmental Factors (external influences)
5. Predictive Modeling (uses all previous capabilities)
6. System Integration (integrates all capabilities)

## 11. Architecture Benefits

This architecture provides the following benefits:

1. **Separation of Concerns**: Each component has a single responsibility
2. **Reusability**: Common functionality is shared through composition
3. **Testability**: Mock implementations can be tested independently
4. **Maintainability**: Clear structure makes it easy to add or modify tests
5. **Flexibility**: Architecture can accommodate new capabilities
6. **Independence**: Step definitions can be implemented and tested independently
7. **Consistency**: Common patterns ensure consistent implementation

## 12. Implementation Progress

### 12.1 Completed Components

1. **Base Infrastructure**
   - ✅ ALZ001TestContext - Thread-safe context for sharing data between step definitions
   - ✅ Updated ALZ001BaseSteps - Base class with common step functionality
   - ✅ ALZ001MockComponent - Abstract base class for all mock components
   - ✅ ALZ001MockFactory - Factory for creating mock components with consistent configuration

2. **Mock Component Implementations**
   - ✅ ProteinExpressionComponent - Component for modeling protein expression patterns in Alzheimer's
   - ✅ NeuronalNetworkComponent - Component for modeling neural network structures and degradation
   - ✅ TimeSeriesAnalysisComponent - Component for time series analysis of biomarker data
   - ✅ EnvironmentalFactorsComponent - Component for modeling environmental impacts on disease

### 12.2 Next Steps

1. Continue creating mock component implementations:
   - Predictive Modeling component, composite, and machine mocks
   - System Integration component, composite, and machine mocks

2. Create mock composite implementations for each capability:
   - ProteinExpressionComposite
   - NeuronalNetworkComposite
   - TimeSeriesAnalysisComposite
   - EnvironmentalFactorsComposite
   - PredictiveModelingComposite
   - SystemIntegrationComposite

2. Update existing step definitions to use the new architecture:
   - Update ProteinExpressionSteps
   - Update NeuronalNetworkSteps
   - Update TimeSeriesAnalysisSteps
   - Update EnvironmentalFactorsSteps
   - Update PredictiveModelingSteps
   
3. Create remaining step definitions:
   - System Integration steps

4. Test and validate the implementation

## 13. References

- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Test-Driven Development](https://www.agilealliance.org/glossary/tdd/)