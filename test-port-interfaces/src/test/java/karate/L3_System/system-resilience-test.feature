Feature: System Resilience and Recovery
  As a system integrator
  I want to verify that complete systems can handle and recover from failures
  So that I can ensure system reliability in production environments

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Initialize test environment
    * def SystemBuilder = Java.type('org.s8r.system.SystemBuilder')
    * def ResilienceTestUtils = Java.type('org.s8r.test.utils.ResilienceTestUtils')
    * def testId = testData.shortUuid()
    * def env = SystemBuilder.createTestEnvironment('resilience-test-' + testId)
      .withParameter('testMode', 'true')
      .withParameter('enableCircuitBreakers', 'true')
      .build()

  @Functional @Runtime @Resilience @CircuitBreaker
  Scenario: System recovers from component failures
    # Create a system with redundant components
    * def system = SystemBuilder.createWithRedundancy(env)
      .withRedundancyFactor(2)
      .withFailureDetection(true)
      .withAutomaticFailover(true)
      .build()
    
    # Verify system is ready
    * system.start()
    * assert system.isRunning()
    
    # Identify a critical component to fail
    * def components = system.getAllComponents()
    * def criticalComponent = ResilienceTestUtils.findCriticalComponent(components)
    * assert criticalComponent != null
    
    # Trigger component failure
    * def failureResult = ResilienceTestUtils.triggerComponentFailure(criticalComponent)
    * assert validators.isSuccessful(failureResult)
    
    # Verify the system detects the failure
    * def detectionResult = system.checkFailureDetection(criticalComponent.getId())
    * assert validators.isSuccessful(detectionResult)
    * assert validators.getAttribute(detectionResult, 'detected') == true
    
    # Verify circuit breakers activate
    * def circuitBreakerResult = system.getCircuitBreakerStatus(criticalComponent.getId())
    * assert validators.isSuccessful(circuitBreakerResult)
    * assert validators.getAttribute(circuitBreakerResult, 'state') == 'OPEN'
    
    # Verify redundant components take over
    * def failoverResult = system.checkFailoverStatus(criticalComponent.getId())
    * assert validators.isSuccessful(failoverResult)
    * assert validators.getAttribute(failoverResult, 'failoverActive') == true
    
    # Verify the system continues operating
    * def operationalStatus = system.checkOperationalStatus()
    * assert validators.isSuccessful(operationalStatus)
    * assert validators.getAttribute(operationalStatus, 'operational') == true
    
    # Measure disruption time
    * def disruptionTime = validators.getAttribute(failoverResult, 'failoverTimeMs')
    * assert disruptionTime < 2000 // Less than 2 seconds disruption

  @Functional @Runtime @Resilience @Performance
  Scenario: System handles resource exhaustion gracefully
    # Create a system with resource monitoring
    * def system = SystemBuilder.createWithResourceMonitoring(env)
      .withMemoryThreshold(0.8) // 80% memory threshold
      .withCpuThreshold(0.9) // 90% CPU threshold
      .withThrottlingEnabled(true)
      .build()
    
    # Start the system and verify normal operation
    * system.start()
    * def baselineStatus = system.getResourceStatus()
    * assert validators.isSuccessful(baselineStatus)
    
    # Simulate resource constraints
    * def resourceConstraintResult = ResilienceTestUtils.simulateResourceConstraint(system, 'MEMORY', 0.85)
    * assert validators.isSuccessful(resourceConstraintResult)
    
    # Verify the system detects the resource limitation
    * def detectionResult = system.getResourceStatus()
    * assert validators.isSuccessful(detectionResult)
    * assert validators.getAttribute(detectionResult, 'memoryUtilization') > 0.8
    * assert validators.getAttribute(detectionResult, 'resourceConstraintsDetected') == true
    
    # Verify throttling of non-critical operations
    * def throttleStatus = system.getThrottlingStatus()
    * assert validators.isSuccessful(throttleStatus)
    * def throttledOperations = validators.getAttribute(throttleStatus, 'throttledOperations')
    * assert throttledOperations.length > 0
    
    # Verify critical functions continue
    * def criticalFunctionTest = function() { return system.executeCriticalOperation('test-' + testId) }
    * def criticalResult = criticalFunctionTest()
    * assert validators.isSuccessful(criticalResult)
    
    # Verify appropriate warnings are logged
    * def logResult = system.getResourceWarningLogs()
    * assert validators.isSuccessful(logResult)
    * def logs = validators.getAttribute(logResult, 'logs')
    * assert logs.length > 0
    * assert logs[0].contains('Resource warning')

  @Scale @Resilience
  Scenario Outline: System adapts to varying load conditions
    # Create a system with auto-scaling
    * def system = SystemBuilder.createWithAutoScaling(env)
      .withMinInstances(1)
      .withMaxInstances(10)
      .withScalingThreshold(<loadLevel>/100.0)
      .build()
    
    # Start the system
    * system.start()
    * def initialInstanceCount = system.getInstanceCount()
    * assert initialInstanceCount == 1
    
    # Generate the specified load
    * def loadResult = ResilienceTestUtils.generateLoad(system, <loadLevel>/100.0)
    * assert validators.isSuccessful(loadResult)
    
    # Wait for auto-scaling to occur
    * Java.type('java.lang.Thread').sleep(3000) // Give time for scaling
    
    # Verify the system scales to expected instance count
    * def scaledInstanceCount = system.getInstanceCount()
    * assert scaledInstanceCount == <instanceCount>
    
    # Verify performance remains within expected response time
    * def operation = function() { return system.executeStandardOperation() }
    * def perfResults = perfTest.measureAverageTime(10, operation)
    * assert perfResults <= <responseTime>
    
    # Verify efficient resource utilization
    * def utilizationResult = system.getResourceUtilization()
    * assert validators.isSuccessful(utilizationResult)
    * def utilization = validators.getAttribute(utilizationResult, 'averageUtilization')
    * assert utilization >= 0.5 && utilization <= 0.9 // Efficient range between 50-90%

    Examples:
      | loadLevel | instanceCount | responseTime |
      | 50        | 2             | 100          |
      | 75        | 3             | 150          |
      | 95        | 5             | 200          |

  @ErrorHandling @Resilience @Monitoring
  Scenario: System self-heals after catastrophic failure
    # Create a system with self-healing capabilities
    * def system = SystemBuilder.createWithSelfHealing(env)
      .withCheckpointing(true)
      .withRecoveryProcedures(true)
      .build()
    
    # Start the system and create initial state
    * system.start()
    * def initialState = ResilienceTestUtils.createInitialSystemState(system)
    * assert validators.isSuccessful(initialState)
    * def stateCheckpoint = system.createCheckpoint('pre-failure')
    * assert validators.isSuccessful(stateCheckpoint)
    
    # Trigger catastrophic failure
    * def shutdownResult = ResilienceTestUtils.triggerCatastrophicFailure(system)
    * assert validators.isSuccessful(shutdownResult)
    * def isShutdown = system.isShutdown()
    * assert isShutdown
    
    # Verify recovery procedures initiate
    * def recoveryResult = ResilienceTestUtils.initiateRecovery(system)
    * assert validators.isSuccessful(recoveryResult)
    * def recoveryStatus = validators.getAttribute(recoveryResult, 'status')
    * assert recoveryStatus == 'RECOVERY_INITIATED'
    
    # Verify system state restored from checkpoints
    * def restoreResult = system.waitForRestoreCompletion(30000) // 30 second timeout
    * assert validators.isSuccessful(restoreResult)
    * def restoredState = system.getSystemState()
    * assert restoredState != null
    
    # Verify operations resume in degraded mode
    * def operationalStatus = system.getOperationalStatus()
    * assert validators.isSuccessful(operationalStatus)
    * def operationalMode = validators.getAttribute(operationalStatus, 'mode')
    * assert operationalMode == 'DEGRADED'
    
    # Verify progressive restoration of functionality
    * def restorationProgress = ResilienceTestUtils.monitorRestorationProgress(system, 60000) // 60 second timeout
    * assert validators.isSuccessful(restorationProgress)
    * def progress = validators.getAttribute(restorationProgress, 'progress')
    * assert progress == 100 // Full restoration
    
    # Final verification of system recovery
    * def finalStatus = system.getOperationalStatus()
    * assert validators.isSuccessful(finalStatus)
    * assert validators.getAttribute(finalStatus, 'mode') == 'NORMAL'