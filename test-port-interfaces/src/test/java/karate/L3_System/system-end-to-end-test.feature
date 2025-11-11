Feature: End-to-End System Integration
  As a system architect
  I want to verify that multiple machines can work together as a complete system
  So that I can ensure the framework supports complex end-to-end scenarios

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Setup environment and test data
    * def envBuilder = Java.type('org.s8r.core.env.Environment').Builder
    * def env = new envBuilder('system-test-env-' + testData.shortUuid())
        .withParameter('testMode', 'true')
        .withParameter('logLevel', 'INFO')
        .build()

  @Functional @DataFlow
  Scenario: Complete data processing pipeline should handle end-to-end flow
    # Set up pipeline components
    * def ComponentFactory = Java.type('org.s8r.component.ComponentFactory')
    * def ingestionMachine = ComponentFactory.createMachine(env, 'DataIngestionMachine')
    * def transformationMachine = ComponentFactory.createMachine(env, 'DataTransformationMachine')
    * def enrichmentMachine = ComponentFactory.createMachine(env, 'DataEnrichmentMachine')
    * def storageMachine = ComponentFactory.createMachine(env, 'DataStorageMachine')
    
    # Connect the pipeline
    * def pipelineBuilder = Java.type('org.s8r.core.pipeline.PipelineBuilder').create(env)
    * def pipeline = pipelineBuilder
        .addMachine(ingestionMachine)
        .addMachine(transformationMachine)
        .addMachine(enrichmentMachine)
        .addMachine(storageMachine)
        .connect(ingestionMachine, 'output', transformationMachine, 'input')
        .connect(transformationMachine, 'output', enrichmentMachine, 'input')
        .connect(enrichmentMachine, 'output', storageMachine, 'input')
        .build()
    
    # Create test data records
    * def testRecords = []
    * for (var i = 0; i < 100; i++) testRecords.push({ id: i, data: 'Record-' + i, timestamp: new Date().getTime() })
    
    # Process records through pipeline
    * pipeline.start()
    * def ingestionInput = ingestionMachine.getPort('input')
    * def processingResult = ingestionInput.submitBatch(testRecords)
    * assert validators.isSuccessful(processingResult)
    
    # Wait for processing to complete
    * def waitForCompletion = function() { return storageMachine.getProcessedCount() >= 100 }
    * def completionResult = pipeline.waitFor(waitForCompletion, 10000) // 10 second timeout
    * assert validators.isSuccessful(completionResult)
    
    # Verify all records were processed correctly
    * def storedRecords = storageMachine.getAllStoredRecords()
    * assert storedRecords.length == 100
    
    # Verify data integrity
    * def verifyIntegrity = function(record) { 
        return record.id != null && 
               record.data != null &&
               record.data.startsWith('Record-') &&
               record.timestamp != null && 
               record.enriched === true; 
      }
    * def integrityCounts = { intact: 0, corrupt: 0 }
    * for (var i = 0; i < storedRecords.length; i++) {
        if (verifyIntegrity(storedRecords[i])) {
          integrityCounts.intact++;
        } else {
          integrityCounts.corrupt++;
        }
      }
    * assert integrityCounts.intact == 100
    * assert integrityCounts.corrupt == 0
    
    # Verify data lineage tracking
    * def lineageResult = pipeline.getLineageInfo(storedRecords[0].id)
    * assert validators.isSuccessful(lineageResult)
    * def lineage = validators.getAttribute(lineageResult, 'lineage')
    * assert lineage.path.length >= 4 // Should have passed through all machines
    
    # Verify performance
    * def processingTime = validators.getAttribute(completionResult, 'processingTimeMs')
    * assert processingTime < 5000 // Less than 5 seconds for 100 records

  @Functional @Resilience
  Scenario: System should recover from component failures
    # Set up a multi-machine processing system
    * def SystemBuilder = Java.type('org.s8r.system.SystemBuilder')
    * def systemBuilder = SystemBuilder.create(env)
    * def processingSystem = systemBuilder
        .addMachine('processor1', 'ProcessorMachine')
        .addMachine('processor2', 'ProcessorMachine')
        .addMachine('processor3', 'ProcessorMachine')
        .addLoadBalancer('loadBalancer', ['processor1', 'processor2', 'processor3'])
        .addMonitor('systemMonitor')
        .build()
    
    # Start the system and verify it's running
    * processingSystem.start()
    * def systemStatus = processingSystem.getStatus()
    * assert systemStatus.isRunning()
    * assert systemStatus.getRunningMachineCount() == 4 // 3 processors + load balancer
    
    # Submit a batch of messages to process
    * def testMessages = []
    * for (var i = 0; i < 50; i++) testMessages.push({ id: i, content: 'Message-' + i })
    * def submitResult = processingSystem.submit(testMessages)
    * assert validators.isSuccessful(submitResult)
    
    # Simulate a processor failure
    * def processor2 = processingSystem.getMachine('processor2')
    * processor2.simulateFailure('OUT_OF_MEMORY')
    
    # Verify the monitor detects the failure
    * Java.type('java.lang.Thread').sleep(500) // Give monitor time to detect
    * def monitorState = processingSystem.getMonitor().getState()
    * def failedComponents = monitorState.getFailedComponents()
    * assert failedComponents.length == 1
    * assert failedComponents[0].getId().contains('processor2')
    
    # Verify the system isolates the failed component
    * def processor2Status = processingSystem.getMachine('processor2').getStatus()
    * assert processor2Status.isIsolated()
    
    # Verify traffic is rerouted
    * def routingMap = processingSystem.getLoadBalancer().getRoutingMap()
    * assert routingMap.size() == 2
    * assert !routingMap.containsKey('processor2')
    
    # Submit more messages and verify they're processed
    * def moreMessages = []
    * for (var i = 50; i < 100; i++) moreMessages.push({ id: i, content: 'Message-' + i })
    * def secondSubmitResult = processingSystem.submit(moreMessages)
    * assert validators.isSuccessful(secondSubmitResult)
    
    # Verify all messages were processed despite the failure
    * def waitResult = processingSystem.waitForCompletion(5000) // 5 second timeout
    * assert validators.isSuccessful(waitResult)
    * def processedCount = processingSystem.getProcessedMessageCount()
    * assert processedCount == 100
    
    # Verify recovery attempts are made
    * def recoveryAttempts = processingSystem.getMonitor().getRecoveryAttempts('processor2')
    * assert recoveryAttempts > 0

  @Performance
  Scenario: System should scale to handle varying load
    # Set up a scalable system
    * def ScalableSystemBuilder = Java.type('org.s8r.system.ScalableSystemBuilder')
    * def scalableSystem = ScalableSystemBuilder.create(env)
        .withMinWorkers(1)
        .withMaxWorkers(5)
        .withScalingThreshold(0.7) // Scale up at 70% capacity
        .withScaleDownThreshold(0.3) // Scale down at 30% capacity
        .build()
    
    # Start the system
    * scalableSystem.start()
    * def initialWorkerCount = scalableSystem.getWorkerCount()
    * assert initialWorkerCount == 1
    
    # Generate a high load
    * def highLoadMessages = []
    * for (var i = 0; i < 1000; i++) highLoadMessages.push({ id: i, size: 'large', processingTimeMs: 50 })
    
    # Submit high load in parallel threads
    * def submitThreads = Java.type('org.s8r.test.utils.ConcurrentTestUtils').createSubmitThreads(scalableSystem, highLoadMessages, 10)
    * submitThreads.startAll()
    
    # Wait for scaling to occur
    * Java.type('java.lang.Thread').sleep(2000) // Give time for scaling
    
    # Verify the system scaled up
    * def scaledUpWorkerCount = scalableSystem.getWorkerCount()
    * assert scaledUpWorkerCount > initialWorkerCount
    
    # Check utilization metrics
    * def metrics = scalableSystem.getResourceMetrics()
    * def utilizationLevel = metrics.getUtilizationLevel()
    * print 'System utilization: ' + utilizationLevel
    
    # Verify throughput increased with scaling
    * def throughputBefore = metrics.getInitialThroughput()
    * def throughputAfter = metrics.getCurrentThroughput()
    * assert throughputAfter > throughputBefore
    
    # Wait for processing to complete
    * submitThreads.waitForCompletion(30000) // 30 second timeout
    
    # Verify scaled system handled the load properly
    * def processedCount = scalableSystem.getProcessedCount()
    * assert processedCount == 1000
    
    # Reduce the load and observe scale down
    * Java.type('java.lang.Thread').sleep(5000) // Wait for system to idle
    * def finalWorkerCount = scalableSystem.getWorkerCount()
    * assert finalWorkerCount < scaledUpWorkerCount

  @Functional @Configuration
  Scenario: System configuration should be applied across all components
    # Create a system-wide configuration
    * def securityConfig = {
        'security.encryption.enabled': 'true',
        'security.encryption.algorithm': 'AES256',
        'security.audit.enabled': 'true',
        'security.accessControl.enforceStrictMode': 'true',
        'security.authentication.requireMfa': 'true'
      }
    
    # Create a system with multiple components
    * def secureSystemBuilder = Java.type('org.s8r.system.SystemBuilder').create(env)
    * def secureSystem = secureSystemBuilder
        .addMachine('dataProcessor', 'DataProcessorMachine')
        .addMachine('apiGateway', 'ApiGatewayMachine')
        .addMachine('storageService', 'StorageServiceMachine')
        .addMachine('identityService', 'IdentityServiceMachine')
        .build()
    
    # Deploy security configuration to the system
    * def configDeployment = secureSystem.deployConfiguration('high-security', securityConfig)
    * assert validators.isSuccessful(configDeployment)
    
    # Verify configuration was applied to all machines
    * def machines = secureSystem.getAllMachines()
    * def configVerification = { compliant: 0, nonCompliant: 0 }
    * for (var i = 0; i < machines.length; i++) {
        var machineConfig = machines[i].getConfiguration();
        var isCompliant = true;
        
        for (var key in securityConfig) {
          if (machineConfig.getProperty(key) != securityConfig[key]) {
            isCompliant = false;
            break;
          }
        }
        
        if (isCompliant) {
          configVerification.compliant++;
        } else {
          configVerification.nonCompliant++;
        }
      }
    
    # Assert all machines are compliant
    * assert configVerification.compliant == machines.length
    * assert configVerification.nonCompliant == 0
    
    # Verify security enforcement in operations
    * def testRequest = { operation: 'getData', parameters: { id: 'secret-data-123' } }
    * def requestResult = secureSystem.process(testRequest)
    * assert !validators.isSuccessful(requestResult) // Should be denied without authentication
    * def errorDetails = validators.getErrorDetails(requestResult)
    * assert errorDetails.message.contains('authentication required')
    
    # Verify system logs configuration changes
    * def systemLogs = secureSystem.getLogs()
    * def configChangeLogCount = Java.type('org.s8r.test.utils.LogAnalyzer').countLogEntriesContaining(systemLogs, 'Configuration applied: high-security')
    * assert configChangeLogCount >= machines.length

  @Functional @Monitoring
  Scenario: System-wide monitoring should track health and performance
    # Create a system with multiple machines
    * def monitoredSystemBuilder = Java.type('org.s8r.system.SystemBuilder').create(env)
    * def monitoredSystem = monitoredSystemBuilder
        .addMachine('service1', 'WorkerMachine')
        .addMachine('service2', 'WorkerMachine')
        .addMachine('service3', 'WorkerMachine')
        .addMachine('service4', 'WorkerMachine')
        .addMachine('service5', 'WorkerMachine')
        .addMonitor('systemMonitor')
        .build()
    
    # Start the system
    * monitoredSystem.start()
    
    # Generate work for 10 minutes (simulated)
    * def simulator = Java.type('org.s8r.test.utils.LoadSimulator').create(monitoredSystem)
    * simulator.simulateLoad(600, 1.0) // 600 seconds (10 min), 100% intensity
    
    # Get monitoring data
    * def monitoringService = monitoredSystem.getMonitoringService()
    * def monitoringData = monitoringService.getMonitoringData()
    * assert monitoringData != null
    
    # Verify performance metrics are tracked
    * def metrics = monitoringData.getMetrics()
    * assert metrics.containsKey('cpu_utilization')
    * assert metrics.containsKey('memory_utilization')
    * assert metrics.containsKey('throughput')
    * assert metrics.containsKey('latency')
    * assert metrics.containsKey('error_rate')
    
    # Verify component health statuses
    * def healthStatuses = monitoringData.getHealthStatuses()
    * assert healthStatuses.size() >= 5 // At least one status per machine
    * var healthyCount = 0
    * for (var key in healthStatuses) {
        if (healthStatuses[key] === 'HEALTHY') {
          healthyCount++;
        }
      }
    * assert healthyCount >= 5
    
    # Verify bottleneck identification
    * def bottleneckAnalysis = monitoringService.identifyBottlenecks()
    * assert bottleneckAnalysis != null
    * print 'Bottlenecks: ' + bottleneckAnalysis.getBottlenecks()
    
    # Verify resource utilization reporting
    * def resourceUtilization = monitoringData.getResourceUtilization()
    * for (var machine in resourceUtilization) {
        assert resourceUtilization[machine].cpu != null
        assert resourceUtilization[machine].memory != null
        assert resourceUtilization[machine].disk != null
        assert resourceUtilization[machine].network != null
      }
    
    # Verify monitoring API
    * def apiClient = Java.type('org.s8r.test.utils.MonitoringApiClient').create(monitoringService.getApiEndpoint())
    * def apiResult = apiClient.fetchMetrics()
    * assert validators.isSuccessful(apiResult)
    * def apiMetrics = validators.getAttribute(apiResult, 'metrics')
    * assert apiMetrics !== null
    * assert Object.keys(apiMetrics).length > 0

  @Resilience @ErrorHandling
  Scenario: System should recover from catastrophic failure
    # Set up a system with persistent state
    * def statefulSystemBuilder = Java.type('org.s8r.system.StatefulSystemBuilder').create(env)
    * def backupManager = Java.type('org.s8r.core.backup.BackupManager').create(env)
    * def statefulSystem = statefulSystemBuilder
        .withPersistenceEnabled(true)
        .withBackupManager(backupManager)
        .addMachine('dataManager', 'PersistentDataMachine')
        .addMachine('stateTracker', 'StateTrackingMachine')
        .addMachine('configurationManager', 'ConfigurationMachine')
        .build()
    
    # Initialize system with data and take backup
    * statefulSystem.start()
    * statefulSystem.initialize({ initialState: 'RUNNING', configVersion: '1.2.3', dataTimestamp: new Date().getTime() })
    * backupManager.createBackup('system-backup-' + testData.shortUuid())
    
    # Verify backup was created
    * def backupsList = backupManager.listBackups()
    * assert backupsList.length >= 1
    * def lastBackup = backupsList[backupsList.length - 1]
    * def backupVerification = backupManager.verifyBackup(lastBackup)
    * assert backupVerification.isValid()
    
    # Simulate catastrophic failure
    * statefulSystem.simulateCatastrophicFailure()
    * def systemState = statefulSystem.getState()
    * assert systemState == 'FAILED'
    
    # Initiate recovery
    * def recoveryManager = Java.type('org.s8r.core.recovery.RecoveryManager').create(env)
    * def recoveryResult = recoveryManager.recoverFromBackup(lastBackup, statefulSystem)
    * assert validators.isSuccessful(recoveryResult)
    
    # Verify system state restoration
    * statefulSystem.waitForInitialization(5000) // Wait for restoration
    * def restoredState = statefulSystem.getState()
    * assert restoredState == 'RUNNING'
    
    # Verify system data and configuration was restored
    * def configVersion = statefulSystem.getConfiguration().getVersion()
    * assert configVersion == '1.2.3'
    
    # Verify machines were reinitialized properly
    * def machineStates = statefulSystem.getMachineStates()
    * for (var machine in machineStates) {
        assert machineStates[machine] == 'RUNNING'
      }
    
    # Verify connections between machines were reestablished
    * def connectionTest = statefulSystem.testConnections()
    * assert validators.isSuccessful(connectionTest)
    
    # Verify processing can resume
    * def testData = { id: 'post-recovery-test', timestamp: new Date().getTime() }
    * def processingTest = statefulSystem.process(testData)
    * assert validators.isSuccessful(processingTest)

  @Functional @Lifecycle
  Scenario: System should support rolling upgrades
    # Set up a running system
    * def upgradeableSystemBuilder = Java.type('org.s8r.system.UpgradeableSystemBuilder').create(env)
    * def upgradeableSystem = upgradeableSystemBuilder
        .addMachine('service1', 'WorkerMachine', '1.0.0')
        .addMachine('service2', 'WorkerMachine', '1.0.0')
        .addMachine('service3', 'WorkerMachine', '1.0.0')
        .withUpgradeStrategy('ROLLING')
        .build()
    
    # Start the system and verify it's processing
    * upgradeableSystem.start()
    * def generatorThread = Java.type('org.s8r.test.utils.BackgroundProcessingThread').start(upgradeableSystem)
    * Java.type('java.lang.Thread').sleep(1000) // Give time for processing to start
    
    # Verify initial state
    * def initialVersions = upgradeableSystem.getComponentVersions()
    * for (var component in initialVersions) {
        assert initialVersions[component] == '1.0.0'
      }
    
    # Initiate rolling upgrade
    * def upgradeResult = upgradeableSystem.upgrade('2.0.0')
    * assert validators.isSuccessful(upgradeResult)
    
    # Monitor upgrade progress
    * def upgradeStatus = function() { return upgradeableSystem.getUpgradeStatus() }
    * def startTime = java.lang.System.currentTimeMillis()
    * def maxWaitTimeMs = 10000 // 10 seconds
    * def status = upgradeStatus()
    * while (status.inProgress && java.lang.System.currentTimeMillis() - startTime < maxWaitTimeMs) {
        java.lang.Thread.sleep(500)
        status = upgradeStatus()
        print 'Upgrade progress: ' + status.progress + '%'
      }
    
    # Verify upgrade completed
    * assert status.completed
    
    # Verify all components were upgraded
    * def upgradedVersions = upgradeableSystem.getComponentVersions()
    * for (var component in upgradedVersions) {
        assert upgradedVersions[component] == '2.0.0'
      }
    
    # Verify data processing continued during upgrade
    * generatorThread.stop()
    * def processedCount = generatorThread.getProcessedCount()
    * print 'Messages processed during upgrade: ' + processedCount
    * assert processedCount > 0
    
    # Verify version compatibility checks were performed
    * def compatibilityChecks = upgradeableSystem.getCompatibilityCheckLog()
    * assert compatibilityChecks.length > 0
    
    # Verify system is functioning after upgrade
    * def postUpgradeCheck = upgradeableSystem.runSystemCheck()
    * assert validators.isSuccessful(postUpgradeCheck)

  @Performance
  Scenario Outline: System should maintain performance under various load conditions
    # Set up performance test system
    * def perfTestBuilder = Java.type('org.s8r.test.performance.PerformanceTestBuilder').create(env)
    * def perfTestSystem = perfTestBuilder
        .withConcurrency(<concurrency>)
        .withRecordCount(<records>)
        .withPerformanceAnalyzer()
        .build()
    
    # Configure performance test
    * def testConfig = {
        recordSize: 1024, // 1KB per record
        operationType: 'STANDARD',
        enableMetrics: true,
        warmupRecords: 100
      }
    * perfTestSystem.configure(testConfig)
    
    # Run performance test
    * perfTestSystem.start()
    * def testResult = perfTestSystem.runTest()
    * assert validators.isSuccessful(testResult)
    
    # Extract performance metrics
    * def metrics = perfTestSystem.getPerformanceMetrics()
    * def throughput = metrics.getThroughput() // records per second
    * def latency = metrics.getAverageLatency() // milliseconds
    * def errorRate = metrics.getErrorRate() // percentage
    * def resourceUsage = metrics.getResourceUtilization() // percentage
    
    # Verify performance meets requirements
    * assert throughput >= <min_throughput>
    * assert latency <= <max_latency>
    * assert errorRate <= <max_error_rate>
    * assert resourceUsage <= <max_resource>
    
    # Log performance results
    * print 'Load condition: ' + <records> + ' records, ' + <concurrency> + ' threads'
    * print 'Throughput: ' + throughput + ' rps (required: ' + <min_throughput> + ')'
    * print 'Latency: ' + latency + ' ms (limit: ' + <max_latency> + ')'
    * print 'Error rate: ' + errorRate + '% (limit: ' + <max_error_rate> + '%)'
    * print 'Resource usage: ' + resourceUsage + '% (limit: ' + <max_resource> + '%)'

    Examples:
      | records | concurrency | min_throughput | max_latency | max_error_rate | max_resource |
      | 1000    | 5           | 50             | 200         | 0.1            | 60           |
      | 10000   | 10          | 100            | 300         | 0.5            | 75           |
      | 100000  | 20          | 200            | 500         | 1.0            | 85           |