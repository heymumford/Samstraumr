Feature: System Scalability
  As a system architect
  I want to verify that the system scales efficiently under increasing load
  So that I can ensure it meets performance requirements in production

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Initialize test environment
    * def SystemBuilder = Java.type('org.s8r.system.SystemBuilder')
    * def ScalabilityTestUtils = Java.type('org.s8r.test.utils.ScalabilityTestUtils')
    * def testId = testData.shortUuid()
    * def env = SystemBuilder.createTestEnvironment('scalability-test-' + testId)
      .withParameter('performanceMonitoring', 'true')
      .withParameter('metricCollection', 'true')
      .build()

  @Scalability @Horizontal
  Scenario: System should scale horizontally by adding processing nodes
    # Create a system configured for horizontal scaling
    * def system = SystemBuilder.createHorizontallyScalableSystem(env)
      .withInitialNodeCount(3)
      .withLoadBalancing(true)
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Verify initial node count
    * def initialNodes = system.getActiveNodeCount()
    * assert initialNodes == 3
    
    # Measure baseline processing capacity
    * def baselineCapacity = ScalabilityTestUtils.measureProcessingCapacity(system)
    * assert validators.isSuccessful(baselineCapacity)
    * def initialCapacity = validators.getAttribute(baselineCapacity, 'processingCapacity')
    * assert initialCapacity > 0
    
    # Scale to 6 processing nodes
    * def scalingResult = system.scaleToNodeCount(6)
    * assert validators.isSuccessful(scalingResult)
    
    # Verify new node count
    * def newNodeCount = system.getActiveNodeCount()
    * assert newNodeCount == 6
    
    # Measure new processing capacity
    * def newCapacity = ScalabilityTestUtils.measureProcessingCapacity(system)
    * assert validators.isSuccessful(newCapacity)
    * def scaledCapacity = validators.getAttribute(newCapacity, 'processingCapacity')
    
    # Verify capacity approximately doubled
    * def capacityRatio = scaledCapacity / initialCapacity
    * assert capacityRatio >= 1.8 // At least 1.8x improvement (allowing for some overhead)
    
    # Verify work distribution
    * def distributionCheck = system.verifyWorkDistribution()
    * assert validators.isSuccessful(distributionCheck)
    * def distributionVariance = validators.getAttribute(distributionCheck, 'workloadVariance')
    * assert distributionVariance < 0.2 // Less than 20% variance indicates even distribution
    
    # Verify coordination overhead
    * def overheadCheck = system.measureCoordinationOverhead()
    * assert validators.isSuccessful(overheadCheck)
    * def overheadPercentage = validators.getAttribute(overheadCheck, 'overheadPercentage')
    * assert overheadPercentage < 10 // Less than 10% overhead
    
    # Verify no disruption to processing during scaling
    * def disruptionCheck = system.getScalingDisruptionMetrics()
    * assert validators.isSuccessful(disruptionCheck)
    * def disruptions = validators.getAttribute(disruptionCheck, 'processingDisruptions')
    * assert disruptions == 0

  @Scalability @Vertical
  Scenario: System should utilize additional resources on a single node
    # Create a system for vertical scaling test
    * def system = SystemBuilder.createVerticallyScalableSystem(env)
      .withInitialResources(4, 8192) // 4 CPU cores, 8GB memory
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Verify initial resource configuration
    * def initialConfig = system.getResourceConfiguration()
    * assert validators.isSuccessful(initialConfig)
    * assert validators.getAttribute(initialConfig, 'cpuCores') == 4
    * assert validators.getAttribute(initialConfig, 'memoryMB') == 8192
    
    # Measure baseline performance
    * def baselinePerf = ScalabilityTestUtils.measureSystemPerformance(system)
    * assert validators.isSuccessful(baselinePerf)
    * def initialThroughput = validators.getAttribute(baselinePerf, 'throughput')
    * assert initialThroughput > 0
    
    # Migrate to node with more resources
    * def migrationResult = system.migrateToResources(8, 16384) // 8 CPU cores, 16GB memory
    * assert validators.isSuccessful(migrationResult)
    
    # Verify new resource configuration
    * def newConfig = system.getResourceConfiguration()
    * assert validators.isSuccessful(newConfig)
    * assert validators.getAttribute(newConfig, 'cpuCores') == 8
    * assert validators.getAttribute(newConfig, 'memoryMB') == 16384
    
    # Measure new performance
    * def newPerf = ScalabilityTestUtils.measureSystemPerformance(system)
    * assert validators.isSuccessful(newPerf)
    * def newThroughput = validators.getAttribute(newPerf, 'throughput')
    
    # Verify throughput increased proportionally
    * def throughputRatio = newThroughput / initialThroughput
    * assert throughputRatio >= 1.7 // At least 1.7x improvement (allowing for non-linear scaling)
    
    # Verify efficient resource utilization
    * def utilizationCheck = system.verifyResourceUtilization()
    * assert validators.isSuccessful(utilizationCheck)
    * def efficiencyScore = validators.getAttribute(utilizationCheck, 'resourceEfficiency')
    * assert efficiencyScore >= 0.8 // At least 80% efficiency
    
    # Verify performance metrics reflect improvements
    * def metricsCheck = system.verifyPerformanceMetrics()
    * assert validators.isSuccessful(metricsCheck)
    * assert validators.getAttribute(metricsCheck, 'metricsReflectImprovement') == true
    
    # Verify strategy adjustment
    * def strategyCheck = system.verifyProcessingStrategy()
    * assert validators.isSuccessful(strategyCheck)
    * assert validators.getAttribute(strategyCheck, 'strategyAdjusted') == true

  @Scalability @DataVolume
  Scenario Outline: System should handle increasing data volumes efficiently
    # Create a system for data volume scaling tests
    * def system = SystemBuilder.createDataVolumeScalableSystem(env)
      .withBaselineDataVolume(1000) // 1000 records as baseline
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Measure baseline throughput
    * def baselineResult = ScalabilityTestUtils.runBaselineVolumeTest(system)
    * assert validators.isSuccessful(baselineResult)
    * def baselineThroughput = validators.getAttribute(baselineResult, 'throughput')
    * def baselineResourceUsage = validators.getAttribute(baselineResult, 'resourceUtilization')
    * assert baselineThroughput > 0
    
    # Scale data volume by factor
    * def volumeResult = ScalabilityTestUtils.scaleDataVolume(system, <scale_factor>)
    * assert validators.isSuccessful(volumeResult)
    
    # Measure scaled throughput
    * def scaledResult = ScalabilityTestUtils.runScaledVolumeTest(system)
    * assert validators.isSuccessful(scaledResult)
    * def scaledThroughput = validators.getAttribute(scaledResult, 'throughput')
    * def scaledResourceUsage = validators.getAttribute(scaledResult, 'resourceUtilization')
    
    # Calculate efficiency = (scaledThroughput / baselineThroughput) / scale_factor * 100
    * def actualScalingFactor = scaledThroughput / baselineThroughput
    * def efficiency = (actualScalingFactor / <scale_factor>) * 100
    * assert efficiency >= <efficiency>
    
    # Verify predictable resource utilization
    * def resourceRatio = scaledResourceUsage / baselineResourceUsage
    * def expectedResourceRatio = Math.sqrt(<scale_factor>) // Sub-linear expected growth
    * assert resourceRatio < <scale_factor> // Resource usage should grow slower than data volume
    * assert Math.abs(resourceRatio - expectedResourceRatio) / expectedResourceRatio < 0.3 // Within 30% of prediction
    
    # Verify data integrity maintained
    * def integrityCheck = system.verifyDataIntegrity(<scale_factor>)
    * assert validators.isSuccessful(integrityCheck)
    * assert validators.getAttribute(integrityCheck, 'integrityMaintained') == true
    
    # Verify latency stays within limits
    * def latencyCheck = system.verifyProcessingLatency(<scale_factor>)
    * assert validators.isSuccessful(latencyCheck)
    * assert validators.getAttribute(latencyCheck, 'withinAcceptableParameters') == true

    Examples:
      | scale_factor | efficiency |
      | 2            | 90         |
      | 5            | 85         |
      | 10           | 80         |
      | 100          | 70         |

  @Scalability @Concurrency
  Scenario: System should handle increasing concurrent users/connections
    # Create a system for concurrency scaling tests
    * def system = SystemBuilder.createConcurrencyScalableSystem(env)
      .withInitialConnections(10)
      .withConnectionTracking(true)
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Verify initial connections
    * def initialConnections = system.getActiveConnectionCount()
    * assert initialConnections == 10
    
    # Measure baseline performance with 10 connections
    * def baselinePerf = ScalabilityTestUtils.measureConcurrentPerformance(system, 10)
    * assert validators.isSuccessful(baselinePerf)
    * def baselineLatency = validators.getAttribute(baselinePerf, 'averageLatency')
    * def baselineResources = validators.getAttribute(baselinePerf, 'resourceUtilization')
    * assert baselineLatency > 0
    
    # Scale to 100 concurrent connections
    * def scalingResult = ScalabilityTestUtils.scaleConnections(system, 100)
    * assert validators.isSuccessful(scalingResult)
    
    # Verify 100 connections established
    * def scaledConnections = system.getActiveConnectionCount()
    * assert scaledConnections == 100
    
    # Measure performance with 100 connections
    * def scaledPerf = ScalabilityTestUtils.measureConcurrentPerformance(system, 100)
    * assert validators.isSuccessful(scaledPerf)
    * def scaledLatency = validators.getAttribute(scaledPerf, 'averageLatency')
    * def scaledResources = validators.getAttribute(scaledPerf, 'resourceUtilization')
    
    # Verify connection integrity
    * def integrityCheck = system.verifyConnectionIntegrity()
    * assert validators.isSuccessful(integrityCheck)
    * assert validators.getAttribute(integrityCheck, 'integrityMaintained') == true
    
    # Verify latency increase within bounds (less than 50%)
    * def latencyIncrease = (scaledLatency - baselineLatency) / baselineLatency * 100
    * assert latencyIncrease < 50
    
    # Verify sub-linear resource scaling
    * def resourceRatio = scaledResources / baselineResources
    * assert resourceRatio < 10 // Should be less than linear (10x)
    
    # Verify no timeouts occurred
    * def timeoutCheck = system.getRequestTimeoutMetrics()
    * assert validators.isSuccessful(timeoutCheck)
    * def timeoutCount = validators.getAttribute(timeoutCheck, 'timeoutCount')
    * assert timeoutCount == 0

  @Scalability @DynamicScaling
  Scenario: System should scale resources dynamically based on load
    # Create auto-scaling system
    * def system = SystemBuilder.createAutoScalingSystem(env)
      .withScalingThresholds(30, 80) // Scale up at 80%, down at 30%
      .withInitialUtilization(30)
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Verify initial utilization around 30%
    * def initialUtil = system.getCurrentUtilization()
    * assert validators.isSuccessful(initialUtil)
    * def utilPercent = validators.getAttribute(initialUtil, 'utilizationPercentage')
    * assert utilPercent >= 25 && utilPercent <= 35 // Around 30%
    
    # Record initial resources
    * def initialResources = system.getAllocatedResources()
    * assert validators.isSuccessful(initialResources)
    
    # Increase workload to 80% of current capacity
    * def workloadResult = ScalabilityTestUtils.increaseWorkload(system, 0.8)
    * assert validators.isSuccessful(workloadResult)
    
    # Wait for auto-scaling to trigger (max 10 seconds)
    * def scalingWait = ScalabilityTestUtils.waitForAutoScaling(system, 10000)
    * assert validators.isSuccessful(scalingWait)
    
    # Verify resources increased
    * def scaledResources = system.getAllocatedResources()
    * assert validators.isSuccessful(scaledResources)
    * def resourceRatio = ScalabilityTestUtils.calculateResourceRatio(scaledResources, initialResources)
    * assert resourceRatio > 1.3 // At least 30% more resources
    
    # Verify performance maintained
    * def perfCheck = system.verifyPerformanceUnderLoad()
    * assert validators.isSuccessful(perfCheck)
    * assert validators.getAttribute(perfCheck, 'performanceMaintained') == true
    
    # Decrease workload
    * def decreaseResult = ScalabilityTestUtils.decreaseWorkload(system, 0.2) // Down to 20%
    * assert validators.isSuccessful(decreaseResult)
    
    # Wait for resources to be released (max 15 seconds)
    * def releaseWait = ScalabilityTestUtils.waitForResourceRelease(system, 15000)
    * assert validators.isSuccessful(releaseWait)
    
    # Verify resources decreased
    * def finalResources = system.getAllocatedResources()
    * assert validators.isSuccessful(finalResources)
    * def finalRatio = ScalabilityTestUtils.calculateResourceRatio(finalResources, scaledResources)
    * assert finalRatio < 0.8 // At least 20% fewer resources than peak
    
    # Verify scaling decisions logged
    * def logCheck = system.getScalingDecisionLogs()
    * assert validators.isSuccessful(logCheck)
    * def scalingLogs = validators.getAttribute(logCheck, 'logs')
    * assert scalingLogs.length >= 2 // At least one scale up and one scale down entry
    * assert scalingLogs[0].contains('metrics')

  @Scalability @PartitionTolerance
  Scenario: System should scale by partitioning data processing
    # Create a system that supports data partitioning
    * def system = SystemBuilder.createPartitionableSystem(env)
      .withPartitioningStrategy('HASH')
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Generate large dataset requiring partitioning
    * def dataResult = ScalabilityTestUtils.generateLargePartitionableDataset(system, 1000000) // 1M records
    * assert validators.isSuccessful(dataResult)
    
    # Process the dataset (system should auto-partition)
    * def processResult = system.processLargeDataset()
    * assert validators.isSuccessful(processResult)
    
    # Verify partitioning occurred
    * def partitionCheck = system.getPartitioningDetails()
    * assert validators.isSuccessful(partitionCheck)
    * def partitionCount = validators.getAttribute(partitionCheck, 'partitionCount')
    * assert partitionCount > 1 // Multiple partitions created
    
    # Verify independent processing
    * def independenceCheck = system.verifyPartitionIndependence()
    * assert validators.isSuccessful(independenceCheck)
    * assert validators.getAttribute(independenceCheck, 'independentProcessing') == true
    
    # Verify partition boundaries managed correctly
    * def boundaryCheck = system.verifyPartitionBoundaries()
    * assert validators.isSuccessful(boundaryCheck)
    * assert validators.getAttribute(boundaryCheck, 'boundariesRespected') == true
    
    # Verify results aggregated properly
    * def aggregationCheck = system.verifyResultAggregation()
    * assert validators.isSuccessful(aggregationCheck)
    * def missingResults = validators.getAttribute(aggregationCheck, 'missingResults')
    * def duplicateResults = validators.getAttribute(aggregationCheck, 'duplicateResults')
    * assert missingResults == 0
    * assert duplicateResults == 0
    
    # Test partition rebalancing
    * def rebalanceResult = system.triggerPartitionRebalance()
    * assert validators.isSuccessful(rebalanceResult)
    
    # Verify partitions were rebalanced effectively
    * def rebalanceCheck = system.verifyRebalancingEffectiveness()
    * assert validators.isSuccessful(rebalanceCheck)
    * assert validators.getAttribute(rebalanceCheck, 'rebalancingEffective') == true

  @Scalability @ResourceEfficiency
  Scenario: System should maintain resource efficiency while scaling
    # Create a system for resource efficiency testing
    * def system = SystemBuilder.createResourceEfficientSystem(env)
      .withBaseCapacity(1000) // 1000 records per second
      .withResourceMonitoring(true)
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Measure baseline resource usage
    * def baselineUsage = system.measureResourceUsage()
    * assert validators.isSuccessful(baselineUsage)
    * def baselineMemory = validators.getAttribute(baselineUsage, 'memoryPerRecord')
    * def baselineCpu = validators.getAttribute(baselineUsage, 'cpuPerRecord')
    * def baselineConnections = validators.getAttribute(baselineUsage, 'dbConnectionCount')
    * assert baselineMemory > 0
    * assert baselineCpu > 0
    
    # Scale to 10k records per second
    * def scalingResult = system.scaleProcessingRate(10000)
    * assert validators.isSuccessful(scalingResult)
    
    # Measure scaled resource usage
    * def scaledUsage = system.measureResourceUsage()
    * assert validators.isSuccessful(scaledUsage)
    * def scaledMemory = validators.getAttribute(scaledUsage, 'memoryPerRecord')
    * def scaledCpu = validators.getAttribute(scaledUsage, 'cpuPerRecord')
    * def scaledConnections = validators.getAttribute(scaledUsage, 'dbConnectionCount')
    
    # Verify resource efficiency maintained per record
    * assert scaledMemory <= baselineMemory * 1.1 // No more than 10% increase per record
    * assert scaledCpu <= baselineCpu * 1.1 // No more than 10% increase per record
    
    # Verify sub-linear memory scaling
    * def memoryScalingFactor = validators.getAttribute(scaledUsage, 'totalMemory') / 
                                validators.getAttribute(baselineUsage, 'totalMemory')
    * assert memoryScalingFactor < 10 // Less than linear (10x) increase
    
    # Verify connection pooling efficiency
    * assert scaledConnections < baselineConnections * 5 // Significantly less than 10x increase
    
    # Check for redundant operations
    * def operationCheck = system.getOperationEfficiency()
    * assert validators.isSuccessful(operationCheck)
    * def redundancyScore = validators.getAttribute(operationCheck, 'redundancyScore')
    * assert redundancyScore < 0.1 // Less than 10% redundant operations

  @Scalability @GlobalDistribution
  Scenario: System should support geographical distribution
    # Create multi-region deployable system
    * def system = SystemBuilder.createMultiRegionSystem(env)
      .withGlobalConsistency(true)
      .build()
    
    # Start the system
    * system.start()
    * assert system.isRunning()
    
    # Deploy across three regions
    * def regions = ['us-east', 'eu-west', 'asia-east']
    * def deploymentResult = system.deployToRegions(regions)
    * assert validators.isSuccessful(deploymentResult)
    
    # Verify deployment across all regions
    * def deploymentCheck = system.verifyRegionalDeployment()
    * assert validators.isSuccessful(deploymentCheck)
    * def deployedRegions = validators.getAttribute(deploymentCheck, 'deployedRegions')
    * assert deployedRegions.length == 3
    
    # Generate region-specific workloads
    * def workloadResult = ScalabilityTestUtils.generateRegionalWorkloads(system, regions)
    * assert validators.isSuccessful(workloadResult)
    
    # Verify processing distribution
    * def distributionCheck = system.verifyRegionalProcessingDistribution()
    * assert validators.isSuccessful(distributionCheck)
    * def distributionMap = validators.getAttribute(distributionCheck, 'regionProcessingMap')
    * assert distributionMap['us-east'] > 0
    * assert distributionMap['eu-west'] > 0
    * assert distributionMap['asia-east'] > 0
    
    # Check cross-region communication optimization
    * def communicationCheck = system.analyzeCrossRegionCommunication()
    * assert validators.isSuccessful(communicationCheck)
    * def optimizationScore = validators.getAttribute(communicationCheck, 'optimizationScore')
    * assert optimizationScore >= 0.8 // At least 80% optimized
    
    # Verify global consistency
    * def consistencyCheck = system.verifyGlobalConsistency()
    * assert validators.isSuccessful(consistencyCheck)
    * assert validators.getAttribute(consistencyCheck, 'consistencyMaintained') == true
    
    # Verify region-local latency
    * def latencyCheck = system.measureRegionalLatency()
    * assert validators.isSuccessful(latencyCheck)
    * def localLatencies = validators.getAttribute(latencyCheck, 'localLatencies')
    * def crossRegionLatencies = validators.getAttribute(latencyCheck, 'crossRegionLatencies')
    * assert localLatencies['us-east'] < crossRegionLatencies['us-east-to-eu-west'] // Local operations faster
    
    # Test regional failure handling
    * def failureResult = ScalabilityTestUtils.simulateRegionFailure(system, 'eu-west')
    * assert validators.isSuccessful(failureResult)
    
    # Verify failure handled gracefully
    * def recoveryCheck = system.verifyRegionalFailureHandling()
    * assert validators.isSuccessful(recoveryCheck)
    * assert validators.getAttribute(recoveryCheck, 'continuedOperationInOtherRegions') == true
    * assert validators.getAttribute(recoveryCheck, 'dataConsistencyMaintained') == true