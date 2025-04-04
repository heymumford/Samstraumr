# Migration Guide

## Table of Contents

- [Introduction: The Journey of Transformation](#introduction-the-journey-of-transformation)
- [Assessing Your Current Landscape](#assessing-your-current-landscape)
- [The Strangler Fig Pattern](#the-strangler-fig-pattern)
- [Identifying Your First Tubes](#identifying-your-first-tubes)
- [Integration Strategies](#integration-strategies)
- [Migration Patterns](#migration-patterns)
- [Testing During Migration](#testing-during-migration)
- [Organizational Considerations](#organizational-considerations)
- [Case Studies](#case-studies)
- [Common Challenges and Solutions](#common-challenges-and-solutions)
- [Migration Roadmap Template](#migration-roadmap-template)

## Introduction: the Journey of Transformation

Migration to Samstraumr is not merely a technical refactoring—it's a transformation in how we think about and structure software. Like converting a man-made canal into a natural flowing stream, this process requires patience, strategic thinking, and a willingness to work with the inherent properties of your existing system rather than fighting against them.

This guide provides a realistic, practical approach to gradually introducing Samstraumr concepts into legacy codebases. Rather than advocating for a complete rewrite—which is rarely feasible or advisable—we focus on incremental transformation that delivers value at each step while moving steadily toward a more adaptable, maintainable architecture.

## Assessing Your Current Landscape

Before beginning your migration journey, take time to understand your existing system through the lens of flow:

### Technical assessment

1. **Identify Existing Flows**: Look for data and control flows that already exist in your application

2. **Map Dependencies**: Understand how components currently interact and depend on each other

3. **Locate Pain Points**: Identify areas with high technical debt, frequent bugs, or maintenance challenges

4. **Assess Test Coverage**: Determine which areas have good test coverage to support safe refactoring

```java
// Example tool for automated dependency mapping
public class DependencyMapper {
    private final Set<String> analyzedClasses = new HashSet<>();
    private final Map<String, Set<String>> dependencyGraph = new HashMap<>();

    public void analyzeCodebase(String rootPackage) {
        // Find all classes in the package and subpackages
        Set<Class<?>> allClasses = findClasses(rootPackage);

        // Analyze each class for dependencies
        for (Class<?> clazz : allClasses) {
            analyzeClass(clazz);
        }

        // Generate visualization or report
        generateDependencyReport();
    }

    private void analyzeClass(Class<?> clazz) {
        String className = clazz.getName();

        if (analyzedClasses.contains(className)) {
            return;
        }

        analyzedClasses.add(className);
        Set<String> dependencies = new HashSet<>();

        // Analyze fields
        for (Field field : clazz.getDeclaredFields()) {
            dependencies.add(field.getType().getName());
        }

        // Analyze method parameters and return types
        for (Method method : clazz.getDeclaredMethods()) {
            dependencies.add(method.getReturnType().getName());
            for (Class<?> paramType : method.getParameterTypes()) {
                dependencies.add(paramType.getName());
            }
        }

        // Store dependencies
        dependencyGraph.put(className, dependencies);
    }

    // Identify potential tubes based on dependency patterns
    public List<String> identifyPotentialTubes() {
        List<String> candidates = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : dependencyGraph.entrySet()) {
            String className = entry.getKey();
            Set<String> dependencies = entry.getValue();

            // Classes with few dependencies and clear input/output patterns
            // make good tube candidates
            if (isTubeCandidate(className, dependencies)) {
                candidates.add(className);
            }
        }

        return candidates;
    }
}

### Key benefits of the strangler fig approach

- **Risk Reduction**: Changes are made incrementally, reducing the risk of system-wide failures
- **Continuous Delivery**: The system remains operational and can continue to be enhanced during migration
- **Early Validation**: Each migrated component provides immediate feedback on the new architecture
- **Manageable Scope**: Teams can focus on well-defined, achievable migration targets
- **Organizational Adaptation**: Teams have time to learn and adjust to the new patterns gradually

## Identifying Your First Tubes

Selecting the right components for initial migration is crucial for building momentum and demonstrating value:

### Ideal characteristics for first candidates

1. **Well-Bounded Functionality**: Clear inputs, outputs, and purpose
2. **Moderate Complexity**: Not too simple (no benefit) or too complex (high risk)
3. **Manageable Dependencies**: Minimal integration with other components
4. **Potential for Improvement**: Areas where Samstraumr benefits will be clearly visible
5. **Frequent Changes**: Components that would benefit from increased adaptability
6. **Good Test Coverage**: Existing tests to validate behavior before and after migration

### Example evaluation matrix

| Component | Bounded | Complexity | Dependencies | Improvement Potential | Change Frequency | Test Coverage | Overall Score |
|-----------|---------|------------|--------------|----------------------|------------------|---------------|---------------|
| Payment Processor | High | Moderate | Low | High | Medium | High | ⭐⭐⭐⭐⭐ |
| User Profile Manager | High | Low | Medium | Medium | High | Medium | ⭐⭐⭐⭐ |
| Report Generator | Medium | High | High | Medium | Low | Low | ⭐⭐ |
| Email Notification | High | Low | Low | Medium | Medium | High | ⭐⭐⭐⭐ |
| Admin Dashboard | Low | High | High | High | Medium | Low | ⭐⭐ |

### From component to tube: transformation approach

1. **Extract Core Logic**: Identify the essential transformation logic within the component
2. **Define Clear Interface**: Establish clean input and output contracts
3. **Implement State Awareness**: Add the dual state model (Design State and Dynamic State)
4. **Add Monitoring**: Implement self-monitoring capabilities
5. **Refactor for Adaptability**: Enhance the component to respond to changing conditions

```java
// Before: Traditional service implementation
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

// After: Transformed into a Samstraumr tube
public class PaymentProcessorTube implements Tube {
    private final TubeMonitor monitor;
    private final BirthCertificate identity;
    private TubeState designState = TubeState.FLOWING;
    private DynamicState dynamicState;

    // Core dependencies
    private final PaymentGateway gateway;
    private final TransactionRepository repository;

    // Performance metrics
    private final AtomicLong totalProcessed = new AtomicLong();
    private final AtomicLong successCount = new AtomicLong();
    private final AtomicLong failureCount = new AtomicLong();
    private final AtomicLong avgProcessingTime = new AtomicLong();

    public PaymentProcessorTube() {
        this.identity = new BirthCertificate.Builder()
            .withID("PaymentProcessor")
            .withPurpose("Process payment transactions")
            .withCreationTime(Instant.now())
            .build();

        this.monitor = new PaymentProcessorMonitor(this);
        this.dynamicState = new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .withMetric("totalProcessed", 0)
            .withMetric("successRate", 100.0)
            .build();
    }

    @Override
    public Object process(Object input) {
        if (designState != TubeState.FLOWING) {
            return new TransactionResult(
                false,
                null,
                "Payment processor is currently " + designState
            );
        }

        if (!(input instanceof PaymentRequest)) {
            return new TransactionResult(
                false,
                null,
                "Invalid input type: " + input.getClass().getSimpleName()
            );
        }

        PaymentRequest request = (PaymentRequest) input;

        // Update dynamic state to reflect processing
        updateDynamicState(new DynamicState.Builder()
            .withTimestamp(Instant.now())
            .withProperty("processing", true)
            .withProperty("customerID", request.getCustomerId())
            .withMetric("amount", request.getAmount().doubleValue())
            .build());

        long startTime = System.nanoTime();

        try {
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

            // Update metrics
            totalProcessed.incrementAndGet();
            if (response.isSuccessful()) {
                successCount.incrementAndGet();
            } else {
                failureCount.incrementAndGet();
            }

            long processingTime = System.nanoTime() - startTime;
            updateAverageProcessingTime(processingTime);

            // Update final state
            updateDynamicState(new DynamicState.Builder()
                .withTimestamp(Instant.now())
                .withProperty("processing", false)
                .withProperty("customerID", request.getCustomerId())
                .withProperty("transactionID", transaction.getId())
                .withMetric("processingTimeMs", processingTime / 1_000_000.0)
                .withMetric("totalProcessed", totalProcessed.get())
                .withMetric("successRate", calculateSuccessRate())
                .withMetric("avgProcessingTimeMs", avgProcessingTime.get() / 1_000_000.0)
                .build());

            // Check gateway health after processing
            checkGatewayHealth();

            // Return result
            return new TransactionResult(
                response.isSuccessful(),
                transaction.getId(),
                response.getErrorMessage()
            );
        } catch (Exception e) {
            failureCount.incrementAndGet();

            // Update error state
            updateDynamicState(new DynamicState.Builder()
                .withTimestamp(Instant.now())
                .withProperty("processing", false)
                .withProperty("error", e.getMessage())
                .withProperty("customerID", request.getCustomerId())
                .withMetric("totalProcessed", totalProcessed.get())
                .withMetric("successRate", calculateSuccessRate())
                .withMetric("errorCount", failureCount.get())
                .build());

            // Check if we need to change design state
            if (calculateSuccessRate() < criticalSuccessRateThreshold) {
                setDesignState(TubeState.ERROR);
            }

            return new TransactionResult(
                false,
                null,
                "Payment processing failed: " + e.getMessage()
            );
        }
    }

    private void checkGatewayHealth() {
        if (!gateway.isHealthy() && designState == TubeState.FLOWING) {
            logger.warn("Payment gateway reporting unhealthy status");
            setDesignState(TubeState.ADAPTING);

            // Attempt recovery
            if (attemptGatewayRecovery()) {
                setDesignState(TubeState.FLOWING);
            } else {
                setDesignState(TubeState.ERROR);
            }
        }
    }

    // Other tube interface methods and helpers
}

### Sidecar pattern

Deploy Samstraumr components alongside existing services, gradually shifting traffic:

```java
public class SidecarRouter {
    private final LegacyService legacyService;
    private final Tube newTube;
    private final MetricsCollector metrics;

    public Response processRequest(Request request) {
        // Clone the request for comparison purposes
        Request requestCopy = request.clone();

        // Process through legacy service
        Response legacyResponse = legacyService.process(request);

        // Process through new tube (shadow traffic)
        TubeInput tubeInput = convertRequest(requestCopy);
        Object tubeOutput = newTube.process(tubeInput);
        Response tubeResponse = convertOutput(tubeOutput);

        // Compare responses
        boolean resultsMatch = compareResponses(legacyResponse, tubeResponse);
        metrics.recordComparison(request, legacyResponse, tubeResponse, resultsMatch);

        // Always return legacy response during shadow period
        return legacyResponse;
    }
}

### Feature flag integration

Use feature flags to control which implementation handles requests:

```java
public class FeatureFlagRouter {
    private final LegacyService legacyService;
    private final Tube newTube;
    private final FeatureFlagService featureFlags;

    public Response handleRequest(Request request, Context context) {
        // Check feature flag
        if (featureFlags.isEnabled("USE_SAMSTRAUMR_IMPLEMENTATION", context)) {
            // Route to new implementation
            return (Response) newTube.process(request);
        } else {
            // Use legacy implementation
            return legacyService.process(request);
        }
    }
}
Legacy System:
    ├── Core Service ← depends on ← User Service ← depends on ← Notification Service
    └── Core Service ← depends on ← Payment Service

Migration Order:
    1. Notification Service (leaf node)
    2. Payment Service (leaf node)
    3. User Service (depends only on migrated component)
    4. Core Service (depends on migrated components)

### Parallel implementation

Build Samstraumr implementations alongside existing code, using adapters to maintain compatibility:

```java
public class ParallelImplementationManager {
    private final Map<String, Object> legacyImplementations = new HashMap<>();
    private final Map<String, Tube> tubeImplementations = new HashMap<>();
    private final FeatureFlagService featureFlags;

    public void registerImplementations(String componentId,
                                      Object legacyImpl,
                                      Tube tubeImpl) {
        legacyImplementations.put(componentId, legacyImpl);
        tubeImplementations.put(componentId, tubeImpl);
    }

    public Object getImplementation(String componentId, Context context) {
        if (featureFlags.isEnabled("USE_TUBE_" + componentId, context)) {
            return tubeImplementations.get(componentId);
        } else {
            return legacyImplementations.get(componentId);
        }
    }

    public void migrateAllTraffic(String componentId) {
        featureFlags.enableGlobally("USE_TUBE_" + componentId);
    }

    public void rollbackMigration(String componentId) {
        featureFlags.disableGlobally("USE_TUBE_" + componentId);
    }
}

## Testing During Migration

Effective testing strategies are essential during Samstraumr migration:

### Behavior equivalence testing

Ensure new implementations behave the same as legacy ones:

```java
@Test
public void shouldProduceSameResultAsLegacyImplementation() {
    // Given
    PaymentRequest request = createSamplePaymentRequest();

    // When - process with legacy implementation
    LegacyPaymentProcessor legacyProcessor = new LegacyPaymentProcessor();
    TransactionResult legacyResult = legacyProcessor.processPayment(request);

    // When - process with new tube implementation
    PaymentProcessorTube tube = new PaymentProcessorTube();
    TransactionResult tubeResult = (TransactionResult) tube.process(request);

    // Then - results should be equivalent
    assertThat(tubeResult.isSuccessful()).isEqualTo(legacyResult.isSuccessful());
    assertThat(tubeResult.getErrorMessage()).isEqualTo(legacyResult.getErrorMessage());

    // And - tube should track metrics
    DynamicState state = tube.getDynamicState();
    assertThat(state.getMetric("totalProcessed").intValue()).isEqualTo(1);
}

### Integration testing with mixed components

Test the interaction between legacy and Samstraumr components:

```java
@Test
public void shouldInteractCorrectlyWithLegacyComponents() {
    // Given
    UserServiceTube userTube = new UserServiceTube();
    LegacyNotificationService legacyNotification = new LegacyNotificationService();
    NotificationAdapter adapter = new NotificationAdapter(legacyNotification);

    userTube.setNotificationService(adapter);

    // When - perform operation that triggers notification
    User user = new User("user123", "jane@example.com");
    userTube.process(new UpdateProfileRequest(user, "New Name"));

    // Then - legacy notification service should receive the notification
    verify(legacyNotification).sendNotification(
        eq("jane@example.com"),
        contains("profile updated")
    );
}

## Organizational Considerations

Technical migration is only part of the journey—organizational aspects are equally important:

### Team structure evolution

Consider how team structures might evolve as your system architecture changes:


### Knowledge transfer

Develop strategies to share Samstraumr knowledge across the organization:

1. **Tube Development Workshops**: Hands-on sessions to build simple tubes
2. **Migration Pairing**: Pair engineers familiar with Samstraumr with those new to it
3. **Documentation**: Create organization-specific Samstraumr guides and examples
4. **Brownbag Sessions**: Regular knowledge-sharing sessions on migration progress
5. **Champions Program**: Designate Samstraumr champions within each team

### Incremental success metrics

Define clear metrics to measure migration progress and success:

- **Migrated Functionality Percentage**: Portion of system functionality now in Samstraumr
- **Code Quality Metrics**: Improvements in maintainability, complexity, and test coverage
- **Incident Reduction**: Decrease in production incidents in migrated components
- **Development Velocity**: Changes in time-to-delivery for features in migrated areas
- **Team Confidence**: Developer satisfaction and confidence with the new architecture

## Case Studies

### Legacy monolith to tube-based architecture

**Company:** Regional E-commerce Platform

**Challenge:** A decade-old monolithic Java application was becoming increasingly difficult to maintain and extend. New features took months to implement, and the system was prone to cascading failures.

**Approach:**
1. **Border Component Identification**: The team identified the notification system, payment processing, and product search as ideal candidates for initial migration.
2. **Adapter Creation**: They built adapters to allow these components to interact with the monolith.
3. **Gradual Replacement**: One component at a time was rewritten as Samstraumr tubes, with feature flags controlling the transition.
4. **Bundle Formation**: As related tubes emerged, they were organized into logical bundles.

**Results:**
- The system became self-healing, automatically recovering from various failure modes
## Common Challenges and Solutions

### Challenge: resistance to new patterns

**Solution: Incremental Demonstration**

Start with small, low-risk migrations that demonstrate clear benefits. Use metrics to show improvements in maintainability, performance, or developer productivity. Create opportunities for hands-on experience with Samstraumr concepts through workshops and collaborative sessions.

### Challenge: handling state during migration

**Solution: State Synchronization Mechanism**

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

        logger.info("State synchronized from legacy component to tube");
    }

    public void bidirectionalSync() {
        // Determine authoritative values for each state element
        Map<String, Object> authoritativeState = determineAuthoritativeState(
            extractLegacyState(),
            newTube.getDynamicState()
        );

        // Update both components
        updateLegacyState(authoritativeState);
        updateTubeState(authoritativeState);

        logger.info("Bidirectional state synchronization complete");
    }
}

### Challenge: cross-cutting concerns

**Solution: Aspect Adaptation**

```java
public class AspectAdapter {
    private final List<CrossCuttingConcern> concerns = new ArrayList<>();

    public void registerConcern(CrossCuttingConcern concern) {
        concerns.add(concern);
    }

    public Object wrapTube(Tube tube) {
        // Create a proxy that applies all cross-cutting concerns
        return Proxy.newProxyInstance(
            tube.getClass().getClassLoader(),
            new Class<?>[] { Tube.class },
            (proxy, method, args) -> {
                // Apply "before" aspects
                for (CrossCuttingConcern concern : concerns) {
                    concern.before(tube, method, args);
                }

                // Invoke the actual method
                Object result;
                try {
                    result = method.invoke(tube, args);

                    // Apply "after" aspects
                    for (CrossCuttingConcern concern : concerns) {
                        concern.afterSuccess(tube, method, args, result);
                    }
                } catch (Exception e) {
                    // Apply "exception" aspects
                    for (CrossCuttingConcern concern : concerns) {
                        concern.afterException(tube, method, args, e);
                    }
                    throw e;
                }

                return result;
            }
        );
    }
}

// Example usage for common cross-cutting concerns
public void setupCrossCuttingConcerns() {
    AspectAdapter adapter = new AspectAdapter();

    // Add authentication aspect
    adapter.registerConcern(new AuthenticationConcern(securityService));

    // Add transaction management
    adapter.registerConcern(new TransactionConcern(transactionManager));

    // Add logging
    adapter.registerConcern(new LoggingConcern());

    // Wrap tubes with aspects
    Tube wrappedTube = (Tube) adapter.wrapTube(new PaymentProcessorTube());
}

## Migration Roadmap Template

A structured roadmap helps maintain momentum and clarity throughout the migration process:

### Phase 1: foundation and first candidates (3-4 months)

1. **Week 1-2: Assessment**
    - Map system architecture and dependencies
    - Identify candidate components for migration
    - Establish baseline metrics

2. **Week 3-4: Foundation Setup**
    - Set up Samstraumr core libraries and tools
    - Create initial testing framework
    - Establish feature flag infrastructure

3. **Week 5-8: First Tube Migration**
    - Convert first candidate to tube implementation
    - Create adapter for legacy integration
    - Test in parallel with legacy implementation

4. **Week 9-12: Pilot Deployment**
    - Gradually increase traffic to new implementation
    - Monitor and collect metrics
    - Document lessons learned
    - Share success stories with broader team

### Phase 2: expansion and bundle formation (6-9 months)

1. **Month 4-5: Additional Tube Migrations**
    - Convert 3-5 additional components to tubes
    - Refine migration patterns based on experience
    - Begin formation of first bundle

2. **Month 6-7: First Bundle Implementation**
    - Organize related tubes into cohesive bundle
    - Implement bundle-level state management
    - Test bundle operations and resilience

3. **Month 8-9: Scaling Team Knowledge**
    - Conduct workshops for broader team
    - Create internal documentation and examples
    - Establish Samstraumr champions program

4. **Month 10-12: Expanded Deployment**
    - Deploy several bundles to production
    - Begin migration of more complex components
    - Measure and report on improvements

### Phase 3: acceleration and system-level transformation (12-18 months)

1. **Month 13-15: Machine Formation**
    - Organize bundles into first machine
    - Implement machine-level coordination
    - Test system-wide adaptability

2. **Month 16-18: Core Component Migration**
    - Begin migration of core system components
    - Implement comprehensive state synchronization
    - Test end-to-end flows through the transformed system

3. **Month 19-24: Legacy Decommissioning**
    - Gradually decommission legacy implementations
    - Complete migration of remaining components
    - Optimize bundle and machine organization

4. **Month 25-30: Optimization and Evolution**
    - Refine architecture based on production experience
    - Implement advanced adaptation capabilities
    - Document architecture for new development

### Sample timeline visualization


---

*"Migration is not merely a technical process but a journey of transformation—for both your system and your team."*

[← Return to Core Concepts](./core-concepts.md) | [Explore Testing →](./testing.md)
notification system saw a 70% reduction in incident rate after migration
- The payment processing tube automatically adapted to traffic spikes that previously caused outages
- Product search became more maintainable, allowing for three new search features to be implemented in one sprint
- New developers reported understanding the tube-based components in days, compared to weeks for the monolith

### Microservices consolidation

**Company:** Financial Technology Startup

**Challenge:** Their microservices architecture had become fragmented, with over 200 services, many with overlapping responsibilities. This led to complex dependencies, deployment challenges, and performance issues due to excessive network calls.

**Approach:**
1. **Domain Analysis**: They mapped their microservices to business domains and identified areas of fragmentation.
2. **Tube Conversion**: Individual microservices were converted to tubes with standardized interfaces.
3. **Bundle Consolidation**: Related tubes were composed into bundles, effectively consolidating multiple microservices.
4. **Gateway Adaptation**: API gateways were updated to route to the new Samstraumr components.

**Results:**
- Reduced their service count from 200+ to 40 bundles
- Decreased average response time by 45% due to reduced network hops
- Simplified deployment pipelines and reduced operational complexity
- Maintained the benefits of loose coupling while improving cohesion

### Legacy data processing pipeline transformation

**Company:** Healthcare Analytics Provider

**Challenge:** Their data processing pipeline was built with a mix of batch jobs, message queues, and custom processors. It was difficult to monitor, prone to data loss during failures, and couldn't adapt to varying data volumes.

**Approach:**
1. **Flow Mapping**: They mapped their entire data pipeline as a flow diagram.
2. **Processor Conversion**: Each processing step was converted to a tube with self-monitoring capabilities.
3. **Adaptive Configuration**: Tubes were designed to adapt their processing approach based on data volume and system load.
4. **State Persistence**: They implemented state persistence to enable recovery from failures without data loss.

**Results:**
- Processing throughput increased by 60% due to adaptive batch sizing
- Data loss incidents were eliminated through improved state management
- End-to-end visibility improved with consistent monitoring across all tubes
- The
```
