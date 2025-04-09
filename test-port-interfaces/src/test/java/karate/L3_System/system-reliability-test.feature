Feature: System Reliability
  As a system operator
  I want to verify that the system operates reliably under various conditions
  So that I can ensure robust performance in production environments

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Initialize test environment
    * def SystemBuilder = Java.type('org.s8r.system.SystemBuilder')
    * def ReliabilityTestUtils = Java.type('org.s8r.test.utils.ReliabilityTestUtils')
    * def testId = testData.shortUuid()
    * def env = SystemBuilder.createTestEnvironment('reliability-test-' + testId)
      .withParameter('testMode', 'true')
      .withParameter('enableRedundancy', 'true')
      .build()

  @Reliability @FaultTolerance
  Scenario: System should continue functioning with partial component failures
    # Create a system with redundant components
    * def system = SystemBuilder.createWithRedundancy(env)
      .withRedundancyFactor(3) // Triple redundancy
      .withFailureDetection(true)
      .build()
    
    # Start the system and verify it's running
    * system.start()
    * assert system.isRunning()
    
    # Get all components and identify processing components
    * def allComponents = system.getAllComponents()
    * def processingComponents = ReliabilityTestUtils.filterProcessingComponents(allComponents)
    * assert processingComponents.length > 0
    
    # Calculate 30% of processing components to fail
    * def componentsToFail = Math.ceil(processingComponents.length * 0.3)
    * def failTargets = ReliabilityTestUtils.selectRandomComponents(processingComponents, componentsToFail)
    * assert failTargets.length > 0
    
    # Capture pre-failure throughput
    * def preThroughput = system.measureThroughput()
    * assert validators.isSuccessful(preThroughput)
    * def baselineTps = validators.getAttribute(preThroughput, 'transactionsPerSecond')
    
    # Trigger component failures (30% of processing components)
    * def failureResult = ReliabilityTestUtils.triggerComponentFailures(failTargets)
    * assert validators.isSuccessful(failureResult)
    
    # Verify the system detects all failures
    * def detectionResults = system.verifyFailureDetection(failTargets)
    * assert validators.isSuccessful(detectionResults)
    * assert validators.getAttribute(detectionResults, 'detectedCount') == failTargets.length
    
    # Verify successful processing reallocation
    * def reallocationResult = system.checkProcessingReallocation()
    * assert validators.isSuccessful(reallocationResult)
    * assert validators.getAttribute(reallocationResult, 'reallocated') == true
    
    # Check for data integrity
    * def dataIntegrityResult = system.verifyDataIntegrity()
    * assert validators.isSuccessful(dataIntegrityResult)
    * assert validators.getAttribute(dataIntegrityResult, 'dataLoss') == false
    
    # Verify degraded but functional throughput
    * def postThroughput = system.measureThroughput()
    * assert validators.isSuccessful(postThroughput)
    * def currentTps = validators.getAttribute(postThroughput, 'transactionsPerSecond')
    * assert currentTps > 0 // System still processes transactions
    * assert currentTps < baselineTps // But at reduced throughput
    * assert currentTps >= baselineTps * 0.6 // Throughput degraded gracefully (not below 60%)
    
    # Verify monitoring accuracy
    * def healthStatus = system.getHealthStatus()
    * assert validators.isSuccessful(healthStatus)
    * def reportedFailedCount = validators.getAttribute(healthStatus, 'failedComponentCount')
    * assert reportedFailedCount == failTargets.length

  @Reliability @DataIntegrity
  Scenario: System should maintain data integrity during component failures
    # Create data processing pipeline with guaranteed delivery
    * def pipeline = SystemBuilder.createProcessingPipeline(env)
      .withGuaranteedDelivery(true)
      .withTransactionSupport(true)
      .build()
    
    # Start the pipeline
    * pipeline.start()
    * assert pipeline.isRunning()
    
    # Create a critical transaction
    * def transactionData = testData.generateEventData('CRITICAL_TRANSACTION')
    * def transactionId = transactionData.id
    
    # Identify a component in the middle of the pipeline
    * def pipelineComponents = pipeline.getComponents()
    * def midComponent = ReliabilityTestUtils.findMiddleComponent(pipelineComponents)
    * assert midComponent != null
    
    # Begin processing the critical transaction
    * def processingStart = pipeline.submitTransaction(transactionData)
    * assert validators.isSuccessful(processingStart)
    
    # Wait until the transaction reaches the target component
    * def reachedComponent = pipeline.waitForTransactionAtComponent(transactionId, midComponent.getId(), 5000)
    * assert validators.isSuccessful(reachedComponent)
    
    # Trigger failure of the component during processing
    * def failureResult = ReliabilityTestUtils.triggerComponentFailure(midComponent)
    * assert validators.isSuccessful(failureResult)
    
    # Verify transaction rolled back
    * def rollbackStatus = pipeline.getTransactionStatus(transactionId)
    * assert validators.isSuccessful(rollbackStatus)
    * assert validators.getAttribute(rollbackStatus, 'status') == 'ROLLED_BACK'
    
    # Restore the component
    * def restoreResult = ReliabilityTestUtils.restoreComponent(midComponent)
    * assert validators.isSuccessful(restoreResult)
    
    # Verify transaction is reprocessed
    * def reprocessStatus = pipeline.waitForTransactionCompletion(transactionId, 10000)
    * assert validators.isSuccessful(reprocessStatus)
    * assert validators.getAttribute(reprocessStatus, 'status') == 'COMPLETED'
    
    # Verify data consistency
    * def consistencyCheck = pipeline.verifyConsistency()
    * assert validators.isSuccessful(consistencyCheck)
    * assert validators.getAttribute(consistencyCheck, 'consistent') == true
    
    # Check downstream components didn't receive partial results
    * def downstreamCheck = pipeline.checkDownstreamDataIntegrity(transactionId)
    * assert validators.isSuccessful(downstreamCheck)
    * assert validators.getAttribute(downstreamCheck, 'receivedPartialData') == false

  @Reliability @ResourceExhaustion
  Scenario: System should handle resource exhaustion gracefully
    # Create a system with constrained resources
    * def system = SystemBuilder.createWithConstrainedResources(env)
      .withMemoryLimit(512)  // MB
      .withCpuLimit(2)       // cores
      .withBackpressureEnabled(true)
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Get initial resource availability
    * def initialResources = system.getResourceAvailability()
    * assert validators.isSuccessful(initialResources)
    
    # Drive the system toward resource limits
    * def loadResult = ReliabilityTestUtils.generateResourceExhaustingLoad(system)
    * assert validators.isSuccessful(loadResult)
    
    # Verify the system approaches resource limits
    * def resourceStatus = system.waitForResourceThreshold(0.9, 30000) // Wait for 90% utilization
    * assert validators.isSuccessful(resourceStatus)
    * assert validators.getAttribute(resourceStatus, 'thresholdReached') == true
    
    # Verify backpressure is applied to data sources
    * def backpressureStatus = system.getBackpressureStatus()
    * assert validators.isSuccessful(backpressureStatus)
    * assert validators.getAttribute(backpressureStatus, 'backpressureApplied') == true
    * def affectedSources = validators.getAttribute(backpressureStatus, 'affectedSources')
    * assert affectedSources.length > 0
    
    # Verify critical processing prioritization
    * def criticalOperation = function() { return system.executeCriticalOperation() }
    * def criticalResult = criticalOperation()
    * assert validators.isSuccessful(criticalResult)
    * def criticalResponseTime = validators.getAttribute(criticalResult, 'responseTimeMs')
    
    # Verify non-critical processing deprioritization
    * def nonCriticalOperation = function() { return system.executeNonCriticalOperation() }
    * def nonCriticalResult = nonCriticalOperation()
    * assert validators.isSuccessful(nonCriticalResult)
    * def nonCriticalResponseTime = validators.getAttribute(nonCriticalResult, 'responseTimeMs')
    * assert nonCriticalResponseTime > criticalResponseTime
    
    # Verify resource release
    * def resourceReleaseInfo = system.getNonEssentialResourceReleaseInfo()
    * assert validators.isSuccessful(resourceReleaseInfo)
    * assert validators.getAttribute(resourceReleaseInfo, 'resourcesReleased') == true
    
    # Verify acceptable performance degradation
    * def performanceCheck = system.getPerformanceDegradationMetrics()
    * assert validators.isSuccessful(performanceCheck)
    * def degradationPercentage = validators.getAttribute(performanceCheck, 'degradationPercentage')
    * assert degradationPercentage <= 50 // Less than 50% degradation
    
    # Verify resource constraints are monitored and alerted
    * def alertStatus = system.getResourceAlertStatus()
    * assert validators.isSuccessful(alertStatus)
    * assert validators.getAttribute(alertStatus, 'alertsGenerated') == true

  @Reliability @NetworkIssues
  Scenario: System should handle network communication issues
    # Create a distributed system across multiple nodes
    * def distributedSystem = SystemBuilder.createDistributedSystem(env)
      .withNodeCount(4)
      .withCircuitBreakerEnabled(true)
      .build()
    
    # Start the distributed system
    * distributedSystem.start()
    * assert distributedSystem.isRunning()
    
    # Verify initial connectivity between nodes
    * def initialConnectivity = distributedSystem.verifyNodeConnectivity()
    * assert validators.isSuccessful(initialConnectivity)
    * assert validators.getAttribute(initialConnectivity, 'fullyConnected') == true
    
    # Simulate unstable network connectivity between nodes
    * def networkInstabilityResult = ReliabilityTestUtils.simulateNetworkInstability(distributedSystem, 0.6) // 60% packet loss
    * assert validators.isSuccessful(networkInstabilityResult)
    
    # Verify retry mechanism for failed communications
    * def retryCapabilityTest = distributedSystem.testCommunicationRetryCapability()
    * assert validators.isSuccessful(retryCapabilityTest)
    * assert validators.getAttribute(retryCapabilityTest, 'retriesAttempted') > 0
    * assert validators.getAttribute(retryCapabilityTest, 'eventuallySucceeded') == true
    
    # Verify circuit breakers are activated
    * def circuitBreakerStatus = distributedSystem.getCircuitBreakerStatus()
    * assert validators.isSuccessful(circuitBreakerStatus)
    * def openCircuitBreakers = validators.getAttribute(circuitBreakerStatus, 'openCircuitBreakers')
    * assert openCircuitBreakers.length > 0
    
    # Verify local processing continues
    * def localProcessingStatus = distributedSystem.checkLocalProcessingContinuation()
    * assert validators.isSuccessful(localProcessingStatus)
    * assert validators.getAttribute(localProcessingStatus, 'localProcessingActive') == true
    
    # Simulate network recovery
    * def recoveryResult = ReliabilityTestUtils.restoreNetworkConnectivity(distributedSystem)
    * assert validators.isSuccessful(recoveryResult)
    
    # Verify connections reestablish
    * def connectionStatus = distributedSystem.waitForConnectionRecovery(30000) // 30 second timeout
    * assert validators.isSuccessful(connectionStatus)
    * assert validators.getAttribute(connectionStatus, 'connectionsReestablished') == true
    
    # Verify circuit breakers close after stability returns
    * def circuitRecoveryStatus = distributedSystem.waitForCircuitRecovery(30000) // 30 second timeout
    * assert validators.isSuccessful(circuitRecoveryStatus)
    * assert validators.getAttribute(circuitRecoveryStatus, 'allCircuitsClosed') == true
    
    # Verify no data loss during connectivity issues
    * def dataIntegrityCheck = distributedSystem.verifyDataIntegrity()
    * assert validators.isSuccessful(dataIntegrityCheck)
    * assert validators.getAttribute(dataIntegrityCheck, 'dataLosses') == 0

  @Reliability @CorruptData
  Scenario: System should detect and handle corrupt data
    # Create a system processing data from external sources
    * def dataProcessingSystem = SystemBuilder.createDataProcessingSystem(env)
      .withCorruptionDetection(true)
      .withDataIsolation(true)
      .build()
    
    # Start the system
    * dataProcessingSystem.start()
    * assert dataProcessingSystem.isRunning()
    
    # Generate valid and corrupt test data
    * def validData = ReliabilityTestUtils.generateValidTestData(10)
    * def corruptData = ReliabilityTestUtils.generateCorruptData(5)
    * def allData = validData.concat(corruptData)
    * assert allData.length == 15
    
    # Shuffle data to randomize valid/corrupt sequence
    * def shuffledData = ReliabilityTestUtils.shuffleArray(allData)
    
    # Submit data for processing
    * def submissionResult = dataProcessingSystem.submitDataBatch(shuffledData)
    * assert validators.isSuccessful(submissionResult)
    
    # Verify corrupt data detection
    * def corruptionDetectionResult = dataProcessingSystem.waitForCorruptionDetection(10000) // 10 second timeout
    * assert validators.isSuccessful(corruptionDetectionResult)
    * def detectedCount = validators.getAttribute(corruptionDetectionResult, 'corruptDataDetected')
    * assert detectedCount == corruptData.length
    
    # Verify isolation of corrupt data
    * def isolationResult = dataProcessingSystem.getCorruptDataIsolationStatus()
    * assert validators.isSuccessful(isolationResult)
    * def isolatedItems = validators.getAttribute(isolationResult, 'isolatedItems')
    * assert isolatedItems.length == corruptData.length
    
    # Verify continued processing of valid data
    * def validProcessingStatus = dataProcessingSystem.getProcessingStatus()
    * assert validators.isSuccessful(validProcessingStatus)
    * def processedCount = validators.getAttribute(validProcessingStatus, 'successfullyProcessed')
    * assert processedCount == validData.length
    
    # Verify appropriate error handling triggered
    * def errorHandlerStatus = dataProcessingSystem.getErrorHandlerInvocations()
    * assert validators.isSuccessful(errorHandlerStatus)
    * assert validators.getAttribute(errorHandlerStatus, 'invocationCount') == corruptData.length
    
    # Verify detailed corruption information logged
    * def corruptionLogs = dataProcessingSystem.getCorruptionLogs()
    * assert validators.isSuccessful(corruptionLogs)
    * def loggedDetails = validators.getAttribute(corruptionLogs, 'details')
    * assert loggedDetails.length == corruptData.length
    * assert loggedDetails[0].contains('Corruption type:')

  @Reliability @LongRunning
  Scenario: System should maintain stability during long-running operations
    # Create a system for continuous operation
    * def longRunningSystem = SystemBuilder.createContinuousOperationSystem(env)
      .withStabilityMonitoring(true)
      .withLeakDetection(true)
      .build()
    
    # Start the system
    * longRunningSystem.start()
    * assert longRunningSystem.isRunning()
    
    # Capture initial metrics
    * def initialMetrics = longRunningSystem.captureOperationalMetrics()
    * assert validators.isSuccessful(initialMetrics)
    
    # Simulate 24 hours of varied load (compressed into test timeframe)
    * def simulationResult = ReliabilityTestUtils.simulateContinuousOperation(longRunningSystem, 24, 60) // 24 hours in 60 seconds
    * assert validators.isSuccessful(simulationResult)
    
    # Verify stable resource utilization
    * def resourceMetrics = longRunningSystem.getResourceUtilizationTrend()
    * assert validators.isSuccessful(resourceMetrics)
    * def stabilityScore = validators.getAttribute(resourceMetrics, 'stabilityScore')
    * assert stabilityScore >= 0.8 // Score of 0.8 or higher indicates stability
    
    # Verify no memory leaks
    * def memoryReport = longRunningSystem.getMemoryUsageReport()
    * assert validators.isSuccessful(memoryReport)
    * def leakDetected = validators.getAttribute(memoryReport, 'leakDetected')
    * assert leakDetected == false
    
    # Verify performance stability over time
    * def performanceTrend = longRunningSystem.getPerformanceTrend()
    * assert validators.isSuccessful(performanceTrend)
    * def degradationPercentage = validators.getAttribute(performanceTrend, 'degradationPercentage')
    * assert degradationPercentage < 10 // Less than 10% degradation over time
    
    # Verify all components remain responsive
    * def responsiveCheck = longRunningSystem.checkAllComponentsResponsive()
    * assert validators.isSuccessful(responsiveCheck)
    * def unresponsiveCount = validators.getAttribute(responsiveCheck, 'unresponsiveCount')
    * assert unresponsiveCount == 0
    
    # Verify all health checks pass
    * def healthCheckResult = longRunningSystem.runFullHealthCheck()
    * assert validators.isSuccessful(healthCheckResult)
    * def failedChecks = validators.getAttribute(healthCheckResult, 'failedChecks')
    * assert failedChecks.length == 0

  @Reliability @StateRecovery
  Scenario: System should recover state after restart
    # Create a system with persistent state management
    * def statefulSystem = SystemBuilder.createStatefulSystem(env)
      .withStateCheckpointing(true)
      .withPersistentConfiguration(true)
      .build()
    
    # Start the system
    * statefulSystem.start()
    * assert statefulSystem.isRunning()
    
    # Generate and store system state
    * def stateGenerationResult = ReliabilityTestUtils.generateSystemState(statefulSystem, 100) // Generate 100 state items
    * assert validators.isSuccessful(stateGenerationResult)
    
    # Verify state is persisted
    * def persistenceCheck = statefulSystem.verifyStatePersistence()
    * assert validators.isSuccessful(persistenceCheck)
    * def persistedCount = validators.getAttribute(persistenceCheck, 'persistedStateCount')
    * assert persistedCount == 100
    
    # Capture the machine configuration
    * def configSnapshot = statefulSystem.captureConfiguration()
    * assert validators.isSuccessful(configSnapshot)
    
    # Create a processing checkpoint
    * def checkpointResult = statefulSystem.createCheckpoint('pre-restart')
    * assert validators.isSuccessful(checkpointResult)
    
    # Restart the entire system
    * def restartResult = statefulSystem.restart()
    * assert validators.isSuccessful(restartResult)
    
    # Verify the system is running after restart
    * assert statefulSystem.isRunning()
    
    # Verify state reload
    * def stateReloadCheck = statefulSystem.verifyStateReload()
    * assert validators.isSuccessful(stateReloadCheck)
    * def reloadedCount = validators.getAttribute(stateReloadCheck, 'reloadedStateCount')
    * assert reloadedCount == persistedCount
    
    # Verify machine configuration recovery
    * def configRecoveryCheck = statefulSystem.verifyConfigurationRecovery(configSnapshot)
    * assert validators.isSuccessful(configRecoveryCheck)
    * assert validators.getAttribute(configRecoveryCheck, 'configurationRestored') == true
    
    # Verify processing resumes from checkpoint
    * def processingResumeCheck = statefulSystem.verifyProcessingResumedFromCheckpoint('pre-restart')
    * assert validators.isSuccessful(processingResumeCheck)
    * assert validators.getAttribute(processingResumeCheck, 'resumedFromCheckpoint') == true
    
    # Verify inter-component connections are reestablished
    * def connectionCheck = statefulSystem.verifyComponentConnections()
    * assert validators.isSuccessful(connectionCheck)
    * assert validators.getAttribute(connectionCheck, 'allConnectionsRestored') == true
    
    # Verify state consistency
    * def consistencyCheck = statefulSystem.verifyStateConsistency()
    * assert validators.isSuccessful(consistencyCheck)
    * assert validators.getAttribute(consistencyCheck, 'stateConsistent') == true

  @Reliability @Versioning
  Scenario: System should handle component version mismatches
    # Create a system with components at different version levels
    * def mixedVersionSystem = SystemBuilder.createMixedVersionSystem(env)
      .withVersionVariance(true)
      .withCompatibilityDetection(true)
      .build()
    
    # Start the system
    * mixedVersionSystem.start()
    * assert mixedVersionSystem.isRunning()
    
    # Get component version details
    * def versionDetails = mixedVersionSystem.getComponentVersionDetails()
    * assert validators.isSuccessful(versionDetails)
    * def versionMap = validators.getAttribute(versionDetails, 'versionMap')
    * assert Object.keys(versionMap).length > 1 // Multiple version levels
    
    # Force communication across version boundaries
    * def crossVersionResult = ReliabilityTestUtils.forceCrossVersionCommunication(mixedVersionSystem)
    * assert validators.isSuccessful(crossVersionResult)
    
    # Verify version compatibility detection
    * def compatibilityResult = mixedVersionSystem.getVersionCompatibilityResults()
    * assert validators.isSuccessful(compatibilityResult)
    * def detectedIssues = validators.getAttribute(compatibilityResult, 'detectedIssues')
    * assert detectedIssues.length > 0
    
    # Verify versioning strategies applied
    * def strategyResult = mixedVersionSystem.getVersioningStrategyApplication()
    * assert validators.isSuccessful(strategyResult)
    * def appliedStrategies = validators.getAttribute(strategyResult, 'appliedStrategies')
    * assert appliedStrategies.length > 0
    
    # Verify critical function operation
    * def criticalFunctionCheck = mixedVersionSystem.verifyCriticalFunctions()
    * assert validators.isSuccessful(criticalFunctionCheck)
    * def functionalPercentage = validators.getAttribute(criticalFunctionCheck, 'functionalPercentage')
    * assert functionalPercentage == 100 // All critical functions working
    
    # Verify compatibility logging
    * def compatibilityLogs = mixedVersionSystem.getCompatibilityLogs()
    * assert validators.isSuccessful(compatibilityLogs)
    * def logs = validators.getAttribute(compatibilityLogs, 'logs')
    * assert logs.length > 0
    * assert logs[0].contains('Version compatibility')
    
    # Verify administrator notifications
    * def notificationCheck = mixedVersionSystem.getAdminNotifications()
    * assert validators.isSuccessful(notificationCheck)
    * def notificationsSent = validators.getAttribute(notificationCheck, 'sentNotifications')
    * assert notificationsSent.length > 0
    * assert notificationsSent[0].type == 'VERSION_CONFLICT'