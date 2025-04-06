<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Migration Guide

## Table of Contents

- [Introduction](#introduction)
- [Assessing Your Current Landscape](#assessing-your-current-landscape)
- [The Strangler Fig Pattern](#the-strangler-fig-pattern)
- [Identifying Your First Tubes](#identifying-your-first-tubes)
- [Integration Strategies](#integration-strategies)
- [Testing During Migration](#testing-during-migration)
- [Organizational Considerations](#organizational-considerations)
- [Case Studies](#case-studies)
- [Common Challenges](#common-challenges)
- [Migration Roadmap](#migration-roadmap)

## Introduction

Migration to Samstraumr is not merely a technical refactoring—it's a transformation in how we think about and structure software. This guide provides a practical approach to gradually introducing Samstraumr concepts into legacy codebases, focusing on incremental changes that deliver value at each step.

## Assessing Your Current Landscape

Before beginning your migration journey:

### Technical Assessment

1. **Identify Existing Flows**: Map data and control flows in your application
2. **Map Dependencies**: Understand component interactions
3. **Locate Pain Points**: Identify areas with technical debt or maintenance challenges
4. **Assess Test Coverage**: Determine which areas have good test coverage

```java
// Tool for automated dependency mapping
public class DependencyMapper {
    private final Set<String> analyzedClasses = new HashSet<>();
    private final Map<String, Set<String>> dependencyGraph = new HashMap<>();

    public void analyzeCodebase(String rootPackage) {
        Set<Class<?>> allClasses = findClasses(rootPackage);
        for (Class<?> clazz : allClasses) {
            analyzeClass(clazz);
        }
        generateDependencyReport();
    }
    
    // Identify potential tubes based on dependency patterns
    public List<String> identifyPotentialTubes() {
        List<String> candidates = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : dependencyGraph.entrySet()) {
            if (isTubeCandidate(entry.getKey(), entry.getValue())) {
                candidates.add(entry.getKey());
            }
        }
        return candidates;
    }
}
```

## The Strangler Fig Pattern

Gradually replace components of a legacy system while keeping it operational:

### Key Benefits

- **Risk Reduction**: Incremental changes reduce risk of system-wide failures
- **Continuous Delivery**: System remains operational during migration
- **Early Validation**: Each migrated component provides immediate feedback
- **Manageable Scope**: Teams focus on well-defined migration targets

## Identifying Your First Tubes

### Ideal Candidates

1. **Well-Bounded Functionality**: Clear inputs, outputs, and purpose
2. **Moderate Complexity**: Not too simple or too complex
3. **Manageable Dependencies**: Minimal integration with other components
4. **Good Test Coverage**: Existing tests to validate behavior

### Evaluation Matrix

| Component | Bounded | Complexity | Dependencies | Improvement Potential | Test Coverage | Score |
|-----------|---------|------------|--------------|----------------------|---------------|-------|
| Payment Processor | High | Moderate | Low | High | High | ⭐⭐⭐⭐⭐ |
| Email Notification | High | Low | Low | Medium | High | ⭐⭐⭐⭐ |
| Report Generator | Medium | High | High | Medium | Low | ⭐⭐ |

### Transformation Approach

1. **Extract Core Logic**: Identify essential transformation logic
2. **Define Clear Interface**: Establish input/output contracts
3. **Implement State Awareness**: Add dual state model
4. **Add Monitoring**: Implement self-monitoring capabilities
5. **Refactor for Adaptability**: Enhance component response to changing conditions

```java
// Before: Traditional service
public class PaymentProcessorService {
    private final PaymentGateway gateway;
    private final TransactionRepository repository;

    public TransactionResult processPayment(PaymentRequest request) {
        // Validate request
        validateRequest(request);

        // Process payment
        PaymentResponse response = gateway.submitPayment(
            request.getAmount(),
            request.getCardDetails(),
            request.getCustomerId()
        );

        // Store transaction
        Transaction transaction = new Transaction(
            UUID.randomUUID().toString(),
            request.getCustomerId(),
            request.getAmount(),
            response.isSuccessful() ? "COMPLETED" : "FAILED",
            response.getTransactionId(),
            response.getErrorCode()
        );

        repository.save(transaction);

        // Return result
        return new TransactionResult(
            response.isSuccessful(),
            transaction.getId(),
            response.getErrorMessage()
        );
    }
}

// After: Samstraumr tube
public class PaymentProcessorTube implements Tube {
    private TubeState designState = TubeState.FLOWING;
    private DynamicState dynamicState;
    private final PaymentGateway gateway;
    private final TransactionRepository repository;
    private final AtomicLong totalProcessed = new AtomicLong();
    
    @Override
    public Object process(Object input) {
        if (designState != TubeState.FLOWING) {
            return new TransactionResult(false, null, 
                      "Payment processor is " + designState);
        }

        PaymentRequest request = (PaymentRequest) input;
        updateDynamicState(/* processing info */);
        
        try {
            // Process payment (similar to original)
            PaymentResponse response = gateway.submitPayment(
                request.getAmount(), 
                request.getCardDetails(),
                request.getCustomerId()
            );
            
            // Store transaction
            Transaction transaction = new Transaction(/* details */);
            repository.save(transaction);
            
            // Update metrics
            totalProcessed.incrementAndGet();
            updateDynamicState(/* success metrics */);
            
            // Check health and potentially adapt
            checkGatewayHealth();
            
            return new TransactionResult(/* details */);
        } catch (Exception e) {
            // Update error metrics and potentially change state
            updateDynamicState(/* error info */);
            return new TransactionResult(false, null, e.getMessage());
        }
    }
}
```

## Integration Strategies

### Sidecar Pattern

Deploy Samstraumr components alongside existing services:

```java
public class SidecarRouter {
    private final LegacyService legacyService;
    private final Tube newTube;
    private final MetricsCollector metrics;

    public Response processRequest(Request request) {
        // Process through legacy service
        Response legacyResponse = legacyService.process(request);

        // Shadow traffic to new tube (for testing)
        TubeInput tubeInput = convertRequest(request.clone());
        Response tubeResponse = convertOutput(newTube.process(tubeInput));

        // Compare responses
        boolean resultsMatch = compareResponses(legacyResponse, tubeResponse);
        metrics.recordComparison(request, legacyResponse, tubeResponse, resultsMatch);

        // Return legacy response during shadow period
        return legacyResponse;
    }
}
```

### Feature Flag Integration

Use feature flags to control which implementation handles requests:

```java
public class FeatureFlagRouter {
    private final LegacyService legacyService;
    private final Tube newTube;
    private final FeatureFlagService featureFlags;

    public Response handleRequest(Request request, Context context) {
        if (featureFlags.isEnabled("USE_SAMSTRAUMR", context)) {
            return (Response) newTube.process(request);
        } else {
            return legacyService.process(request);
        }
    }
}
```

### Dependency-Based Migration Order

```
Legacy System:
    ├── Core Service ← depends on ← User Service ← depends on ← Notification Service
    └── Core Service ← depends on ← Payment Service

Migration Order:
    1. Notification Service (leaf node)
    2. Payment Service (leaf node)
    3. User Service (depends only on migrated components)
    4. Core Service (depends on migrated components)
```

## Testing During Migration

### Behavior Equivalence Testing

```java
@Test
public void shouldProduceSameResultAsLegacyImplementation() {
    // Given
    PaymentRequest request = createSamplePaymentRequest();

    // When - process with both implementations
    TransactionResult legacyResult = legacyProcessor.processPayment(request);
    TransactionResult tubeResult = (TransactionResult) tube.process(request);

    // Then - results should be equivalent
    assertThat(tubeResult.isSuccessful()).isEqualTo(legacyResult.isSuccessful());
    assertThat(tubeResult.getErrorMessage()).isEqualTo(legacyResult.getErrorMessage());
    
    // And - tube should track metrics
    DynamicState state = tube.getDynamicState();
    assertThat(state.getMetric("totalProcessed").intValue()).isEqualTo(1);
}
```

### Integration Testing

Test interaction between legacy and Samstraumr components:

```java
@Test
public void shouldInteractCorrectlyWithLegacyComponents() {
    // Set up
    UserServiceTube userTube = new UserServiceTube();
    LegacyNotificationService legacyNotification = new LegacyNotificationService();
    NotificationAdapter adapter = new NotificationAdapter(legacyNotification);
    userTube.setNotificationService(adapter);

    // When
    User user = new User("user123", "jane@example.com");
    userTube.process(new UpdateProfileRequest(user, "New Name"));

    // Then
    verify(legacyNotification).sendNotification(
        eq("jane@example.com"),
        contains("profile updated")
    );
}
```

## Organizational Considerations

### Knowledge Transfer

1. **Tube Development Workshops**: Hands-on sessions
2. **Migration Pairing**: Pair engineers familiar with Samstraumr with newcomers
3. **Documentation**: Create organization-specific guides
4. **Champions Program**: Designate Samstraumr champions within each team

### Success Metrics

- **Migrated Functionality**: Percentage of system in Samstraumr
- **Incident Reduction**: Decrease in production issues in migrated components
- **Development Velocity**: Time-to-delivery for features in migrated areas
- **Team Confidence**: Developer satisfaction with the new architecture

## Case Studies

### E-commerce Platform Migration

**Challenge:** Decade-old monolith with slow development and cascading failures

**Approach:**
1. Identified notification system, payment processing, and product search for migration
2. Built adapters for monolith interaction
3. Gradually replaced components with tubes using feature flags
4. Organized related tubes into bundles

**Results:**
- 70% reduction in notification system incidents
- Payment processor automatically adapting to traffic spikes
- Improved developer onboarding (days vs. weeks to understand code)

### Microservices Consolidation

**Challenge:** Over 200 fragmented microservices with complex dependencies

**Approach:**
1. Mapped services to business domains
2. Converted services to tubes with standardized interfaces
3. Consolidated related tubes into bundles
4. Updated API gateways to route to new components

**Results:**
- Reduced from 200+ services to 40 bundles
- 45% decrease in average response time
- Simplified deployment while maintaining loose coupling

## Common Challenges

### State Synchronization

```java
public class StateSynchronizer {
    private final Object legacyComponent;
    private final Tube newTube;

    public void synchronizeState() {
        // Extract state from legacy component
        LegacyState legacyState = extractLegacyState();

        // Convert to tube state format
        DynamicState tubeState = convertState(legacyState);

        // Update tube state
        newTube.setDynamicState(tubeState);
    }
}
```

### Cross-cutting Concerns

```java
public class AspectAdapter {
    private final List<CrossCuttingConcern> concerns = new ArrayList<>();

    public Object wrapTube(Tube tube) {
        return Proxy.newProxyInstance(
            tube.getClass().getClassLoader(),
            new Class<?>[] { Tube.class },
            (proxy, method, args) -> {
                // Apply "before" aspects
                for (CrossCuttingConcern concern : concerns) {
                    concern.before(tube, method, args);
                }

                // Invoke method with aspect handling
                try {
                    Object result = method.invoke(tube, args);
                    // Success handling
                    return result;
                } catch (Exception e) {
                    // Exception handling
                    throw e;
                }
            }
        );
    }
}
```

## Migration Roadmap

### Phase 1: Foundation (3-4 months)

1. **Assessment** (Weeks 1-2)
   - Map architecture and dependencies
   - Identify migration candidates
   - Establish baseline metrics

2. **Setup** (Weeks 3-4)
   - Set up Samstraumr libraries and tools
   - Create testing framework
   - Establish feature flag infrastructure

3. **First Migration** (Weeks 5-8)
   - Convert first candidate to tube implementation
   - Create adapters for legacy integration
   - Test in parallel with legacy implementation

4. **Pilot Deployment** (Weeks 9-12)
   - Gradually increase traffic to new implementation
   - Monitor and collect metrics
   - Document lessons learned

### Phase 2: Expansion (6-9 months)

1. **Additional Migrations**
   - Convert 3-5 additional components to tubes
   - Refine migration patterns
   - Begin formation of first bundle

2. **Bundle Implementation**
   - Organize related tubes into bundles
   - Implement bundle-level state management
   - Test bundle operations and resilience

3. **Knowledge Scaling**
   - Conduct workshops for broader team
   - Create internal documentation
   - Establish champions program

### Phase 3: System Transformation (12-18 months)

1. **Machine Formation**
   - Organize bundles into machines
   - Implement machine-level coordination
   - Test system-wide adaptability

2. **Core Component Migration**
   - Migrate core system components
   - Implement state synchronization
   - Test end-to-end flows

3. **Legacy Decommissioning**
   - Gradually decommission legacy implementations
   - Optimize bundle and machine organization

[← Return to Core Concepts](./core-concepts.md) | [Explore Testing →](./testing.md)