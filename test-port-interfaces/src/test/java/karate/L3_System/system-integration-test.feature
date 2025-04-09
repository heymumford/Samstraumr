Feature: System External Integration
  As a system integrator
  I want to verify that the system integrates correctly with external systems
  So that I can ensure seamless operation within a broader ecosystem

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Initialize common test components
    * def mockServerManager = Java.type('org.s8r.test.mock.MockServerManager')
    * def mockServer = mockServerManager.createMockServer(8080)
    * def testId = testData.shortUuid()
    
    # Create system adapters using standardized initializers
    * def fileSystemAdapter = adapterInit.createFileSystemAdapter()
    * def cacheAdapter = adapterInit.createCacheAdapter()
    * def configAdapter = adapterInit.createConfigAdapter()
    * def eventAdapter = adapterInit.createEventPublisherAdapter()

  @Integration @ExternalAPI
  Scenario: System should communicate with external REST APIs
    # Set up mock REST API server
    * mockServer.reset()
    * mockServer.whenGet('/api/v1/data').thenReturn(200, { 'message': 'Success', 'data': [1, 2, 3] })
    * mockServer.whenPost('/api/v1/data').thenReturn(201, { 'status': 'Created', 'id': '123' })
    * mockServer.whenGet('/api/v1/error').thenReturn(500, { 'error': 'Internal Server Error' })
    
    # Create REST client adapter
    * def restAdapter = Java.type('org.s8r.infrastructure.rest.RESTClientAdapter').createInstance('http://localhost:8080')
    
    # Verify proper request formatting
    * def getResult = restAdapter.get('/api/v1/data')
    * assert validators.isSuccessful(getResult)
    * def responseData = validators.getAttribute(getResult, 'data')
    * match responseData.data == [1, 2, 3]
    
    # Verify handling of different response codes
    * def postData = { 'name': 'Test Object', 'value': 42 }
    * def postResult = restAdapter.post('/api/v1/data', postData)
    * assert validators.isSuccessful(postResult)
    * assert validators.getAttribute(postResult, 'id') == '123'
    
    # Verify error handling
    * def errorResult = restAdapter.get('/api/v1/error')
    * assert !validators.isSuccessful(errorResult)
    * def errorDetails = validators.getErrorDetails(errorResult)
    * assert errorDetails.message.contains('Internal Server Error')
    
    # Verify security mechanisms
    * def secureAdapter = Java.type('org.s8r.infrastructure.rest.RESTClientAdapter').createWithSecurity('http://localhost:8080', 'bearer', 'test-token')
    * mockServer.verifyRequest('/api/v1/data', { 'headers': { 'Authorization': 'Bearer test-token' } })
    
    # Verify performance metrics
    * def getOperation = function() { restAdapter.get('/api/v1/data') }
    * def perfResults = perfTest.measureAverageTime(10, getOperation)
    * assert perfResults < 500 // Less than 500ms average

  @Integration @MessageQueue
  Scenario: System should integrate with message queuing systems
    # Initialize mock message queue
    * def mockQueue = Java.type('org.s8r.test.mock.MockMessageQueue').createInstance('test-queue-' + testId)
    * def messageAdapter = Java.type('org.s8r.infrastructure.messaging.MessageQueueAdapter').createInstance(mockQueue)
    
    # Publish test messages
    * def testMessages = [{ 'id': 1, 'payload': 'Message 1' }, { 'id': 2, 'payload': 'Message 2' }]
    * messageAdapter.publishBatch('test-topic', testMessages)
    
    # Verify message consumption
    * def consumer = messageAdapter.createConsumer('test-topic', 'test-consumer')
    * def messages = consumer.consumeBatch(10, 1000) // Batch size 10, timeout 1000ms
    * assert messages.length == 2
    * match messages[0].payload == 'Message 1'
    * match messages[1].payload == 'Message 2'
    
    # Verify acknowledgment handling
    * consumer.acknowledge(messages[0])
    * assert mockQueue.isAcknowledged(messages[0].id)
    * assert !mockQueue.isAcknowledged(messages[1].id)
    
    # Verify retry logic for failed processing
    * consumer.fail(messages[1], true) // Mark for retry
    * def retryMessage = consumer.consume(1000) // Timeout 1000ms
    * assert retryMessage.id == 2
    * assert retryMessage.retryCount == 1

  @Integration @Database
  Scenario: System should interact correctly with external databases
    # Initialize in-memory test database
    * def dbManager = Java.type('org.s8r.test.db.TestDatabaseManager')
    * def db = dbManager.createTestDatabase('test-db-' + testId)
    * def dbAdapter = Java.type('org.s8r.infrastructure.db.DatabaseAdapter').createInstance(db)
    
    # Create test table and data
    * db.execute('CREATE TABLE test_table (id INTEGER PRIMARY KEY, name VARCHAR(100), value DOUBLE)')
    * db.execute('INSERT INTO test_table VALUES (1, "Test1", 10.5), (2, "Test2", 20.5)')
    
    # Verify database operations
    * def result = dbAdapter.query('SELECT * FROM test_table ORDER BY id')
    * assert validators.isSuccessful(result)
    * def rows = validators.getAttribute(result, 'rows')
    * assert rows.length == 2
    * match rows[0].name == 'Test1'
    * match rows[1].value == 20.5
    
    # Verify transaction management
    * def txResult = dbAdapter.executeInTransaction(function() {
        dbAdapter.execute('INSERT INTO test_table VALUES (3, "Test3", 30.5)');
        dbAdapter.execute('UPDATE test_table SET value = 15.5 WHERE id = 1');
        return true; // Commit transaction
      })
    * assert validators.isSuccessful(txResult)
    * def updatedResult = dbAdapter.query('SELECT * FROM test_table WHERE id = 1')
    * assert validators.getAttribute(updatedResult, 'rows')[0].value == 15.5
    
    # Verify transaction rollback
    * def failedTxResult = dbAdapter.executeInTransaction(function() {
        dbAdapter.execute('INSERT INTO test_table VALUES (4, "Test4", 40.5)');
        throw new Error('Deliberate transaction failure');
        return false; // Never reached, transaction should roll back
      })
    * assert !validators.isSuccessful(failedTxResult)
    * def countResult = dbAdapter.query('SELECT COUNT(*) as count FROM test_table WHERE id = 4')
    * assert validators.getAttribute(countResult, 'rows')[0].count == 0

  @Integration @FileSystem
  Scenario: System should handle external file system operations
    # Set up test directory
    * def baseDir = fileSystemAdapter.createTemporaryDirectory('file-test-' + testId)
    * def testFilePath = baseDir + '/test-file.txt'
    * def largeFilePath = baseDir + '/large-file.dat'
    
    # Test basic file operations
    * def writeResult = fileSystemAdapter.writeFile(testFilePath, 'Test content')
    * assert validators.isSuccessful(writeResult)
    * def readResult = fileSystemAdapter.readFile(testFilePath)
    * assert validators.isSuccessful(readResult)
    * assert validators.getAttribute(readResult, 'content') == 'Test content'
    
    # Test file system latency handling
    * def slowFileSystem = Java.type('org.s8r.test.mock.SlowFileSystemAdapter').createWithLatency(200) // 200ms latency
    * def slowWriteResult = slowFileSystem.writeFile(baseDir + '/slow-file.txt', 'Slow operation')
    * assert validators.isSuccessful(slowWriteResult)
    * assert validators.getAttribute(slowWriteResult, 'duration') >= 200
    
    # Test file locking
    * def lockManager = Java.type('org.s8r.infrastructure.filesystem.FileLockManager').getInstance()
    * def lock = lockManager.acquireLock(testFilePath)
    * assert lock.isAcquired()
    * def concurrentAdapter = Java.type('org.s8r.infrastructure.filesystem.BufferedFileSystemAdapter').createInstance()
    * def concurrentWriteResult = concurrentAdapter.writeFile(testFilePath, 'Concurrent write')
    * assert !validators.isSuccessful(concurrentWriteResult)
    * lock.release()
    * def afterReleaseWriteResult = concurrentAdapter.writeFile(testFilePath, 'After release')
    * assert validators.isSuccessful(afterReleaseWriteResult)
    
    # Test large file handling
    * def largeContent = new Array(10 * 1024 * 1024).fill('X').join('') // 10MB file
    * def largeWriteResult = fileSystemAdapter.writeFileStream(largeFilePath, largeContent)
    * assert validators.isSuccessful(largeWriteResult)
    * def largeFileInfo = fileSystemAdapter.getFileInfo(largeFilePath)
    * assert validators.getAttribute(largeFileInfo, 'size') >= 10 * 1024 * 1024
    * def streamReadResult = fileSystemAdapter.readFileStream(largeFilePath)
    * assert validators.isSuccessful(streamReadResult)
    * assert validators.getAttribute(streamReadResult, 'size') >= 10 * 1024 * 1024

  @Integration @Authentication
  Scenario: System should integrate with external authentication services
    # Set up mock authentication service
    * def authService = Java.type('org.s8r.test.mock.MockAuthenticationService').createInstance()
    * def authAdapter = Java.type('org.s8r.infrastructure.security.AuthenticationAdapter').createWithService(authService)
    
    # Configure test users
    * authService.addUser('user1', 'password1', ['USER'])
    * authService.addUser('admin1', 'adminpass', ['USER', 'ADMIN'])
    
    # Test successful authentication
    * def userAuthResult = authAdapter.authenticate('user1', 'password1')
    * assert validators.isSuccessful(userAuthResult)
    * def userToken = validators.getAttribute(userAuthResult, 'token')
    * assert userToken != null
    * def userValidation = authAdapter.validateToken(userToken)
    * assert validators.isSuccessful(userValidation)
    * def userRoles = validators.getAttribute(userValidation, 'roles')
    * match userRoles contains 'USER'
    
    # Test failed authentication
    * def failedAuthResult = authAdapter.authenticate('user1', 'wrongpassword')
    * assert !validators.isSuccessful(failedAuthResult)
    * def errorDetails = validators.getErrorDetails(failedAuthResult)
    * assert errorDetails.message.contains('Invalid credentials')
    
    # Test service outage handling
    * authService.setAvailable(false)
    * def unavailableResult = authAdapter.authenticate('admin1', 'adminpass')
    * assert !validators.isSuccessful(unavailableResult)
    * def serviceError = validators.getErrorDetails(unavailableResult)
    * assert serviceError.message.contains('service unavailable')
    * authService.setAvailable(true)
    
    # Test single sign-on
    * def ssoAdapter = Java.type('org.s8r.infrastructure.security.SSOAdapter').createWithService(authService)
    * def ssoResult = ssoAdapter.authenticateWithSSO('admin1')
    * assert validators.isSuccessful(ssoResult)
    * def ssoToken = validators.getAttribute(ssoResult, 'token')
    * assert ssoToken != null
    * def ssoValidation = authAdapter.validateToken(ssoToken)
    * assert validators.isSuccessful(ssoValidation)

  @Integration @ThirdPartyServices
  Scenario Outline: System should integrate with various third-party services
    # Set up mock third-party service of specific type
    * def serviceConfig = { type: '<service_type>', endpoint: 'http://localhost:8080/api/' + '<service_type>' }
    * mockServer.reset()
    * mockServer.whenPost('/api/' + '<service_type>').thenReturn(200, { status: 'success' })
    * mockServer.whenGet('/api/' + '<service_type>' + '/status').thenReturn(200, { status: 'operational' })
    
    # Create appropriate adapter for service type
    * def serviceAdapter = Java.type('org.s8r.infrastructure.thirdparty.ThirdPartyServiceAdapter').createForType('<service_type>', serviceConfig)
    
    # Test basic integration
    * def serviceStatus = serviceAdapter.checkStatus()
    * assert validators.isSuccessful(serviceStatus)
    * assert validators.getAttribute(serviceStatus, 'status') == 'operational'
    
    # Test service-specific operations
    * def operationResult = serviceAdapter.performOperation('<service_type>' + '-operation', { data: 'test' })
    * assert validators.isSuccessful(operationResult)
    
    # Test error handling for service-specific errors
    * mockServer.whenPost('/api/' + '<service_type>' + '/error').thenReturn(400, { error: '<service_type>' + ' specific error' })
    * def errorResult = serviceAdapter.performOperation('<service_type>' + '-error', { data: 'test' })
    * assert !validators.isSuccessful(errorResult)
    * def errorDetails = validators.getErrorDetails(errorResult)
    * assert errorDetails.message.contains('<service_type>' + ' specific error')
    
    # Test performance metrics
    * def operation = function() { serviceAdapter.checkStatus() }
    * def perfResults = perfTest.runThroughputTest(operation, 1)
    * def report = perfTest.createPerformanceReport(perfResults, '<service_type>' + ' Service Integration')
    * print report

    Examples:
      | service_type   |
      | email          |
      | notification   |
      | analytics      |
      | payment        |
      | geolocation    |

  @Integration @DataSynchronization
  Scenario: System should synchronize data with external systems
    # Set up mock external system and synchronization adapter
    * def externalSystem = Java.type('org.s8r.test.mock.MockExternalSystem').createInstance('ext-sys-' + testId)
    * def syncAdapter = Java.type('org.s8r.infrastructure.sync.DataSynchronizationAdapter').createInstance(externalSystem)
    
    # Create test data
    * def initialData = [
        { id: '1', name: 'Item 1', value: 100 },
        { id: '2', name: 'Item 2', value: 200 }
      ]
    * syncAdapter.initializeData(initialData)
    * externalSystem.initializeData(initialData)
    
    # Test data change propagation from local to external
    * def updateResult = syncAdapter.updateLocalData({ id: '1', name: 'Item 1 Updated', value: 150 })
    * assert validators.isSuccessful(updateResult)
    * def syncResult = syncAdapter.synchronize()
    * assert validators.isSuccessful(syncResult)
    * def externalItem = externalSystem.getData('1')
    * assert externalItem.name == 'Item 1 Updated'
    * assert externalItem.value == 150
    
    # Test data change propagation from external to local
    * externalSystem.updateData({ id: '2', name: 'Item 2 Updated', value: 250 })
    * def syncResult2 = syncAdapter.synchronize()
    * assert validators.isSuccessful(syncResult2)
    * def localData = syncAdapter.getLocalData('2')
    * assert localData.name == 'Item 2 Updated'
    * assert localData.value == 250
    
    # Test conflict resolution
    * syncAdapter.updateLocalData({ id: '1', name: 'Local Update', value: 160 })
    * externalSystem.updateData({ id: '1', name: 'External Update', value: 170 })
    * def conflictResult = syncAdapter.synchronize()
    * assert validators.isSuccessful(conflictResult)
    * def resolvedItem = syncAdapter.getLocalData('1')
    * def externalItem2 = externalSystem.getData('1')
    * assert resolvedItem.value == externalItem2.value
    
    # Test intermittent connectivity
    * externalSystem.setAvailable(false)
    * syncAdapter.updateLocalData({ id: '2', name: 'Offline Update', value: 300 })
    * def offlineSyncResult = syncAdapter.synchronize()
    * assert !validators.isSuccessful(offlineSyncResult)
    * assert syncAdapter.getPendingChanges().length == 1
    * externalSystem.setAvailable(true)
    * def retrySyncResult = syncAdapter.synchronize()
    * assert validators.isSuccessful(retrySyncResult)
    * def externalItem3 = externalSystem.getData('2')
    * assert externalItem3.name == 'Offline Update'
    * assert externalItem3.value == 300

  @Integration @Legacy
  Scenario: System should integrate with legacy systems
    # Set up mock legacy system with proprietary format
    * def legacySystem = Java.type('org.s8r.test.mock.MockLegacySystem').createInstance()
    * def legacyAdapter = Java.type('org.s8r.infrastructure.legacy.LegacySystemAdapter').createInstance(legacySystem)
    
    # Test data format transformation
    * def modernData = { customerId: 1001, name: 'Test Customer', orders: [{ id: 'ORD-001', amount: 150.75 }] }
    * def transformResult = legacyAdapter.exportData(modernData)
    * assert validators.isSuccessful(transformResult)
    * def legacyData = validators.getAttribute(transformResult, 'legacyData')
    * assert legacyData.startsWith('CUST:1001|Test Customer')
    * assert legacyData.contains('ORD:ORD-001:150.75')
    
    # Test import from legacy format
    * def legacyImportData = 'CUST:2001|Import Customer|ADDR:123 Main St|ORD:ORD-101:200.50|ORD:ORD-102:75.25'
    * def importResult = legacyAdapter.importData(legacyImportData)
    * assert validators.isSuccessful(importResult)
    * def importedData = validators.getAttribute(importResult, 'data')
    * assert importedData.customerId == 2001
    * assert importedData.name == 'Import Customer'
    * assert importedData.address == '123 Main St'
    * assert importedData.orders.length == 2
    * assert importedData.orders[0].id == 'ORD-101'
    * assert importedData.orders[1].amount == 75.25
    
    # Test legacy system constraints
    * def largeData = { customerId: 3001, name: Array(1000).fill('X').join(''), orders: [] }
    * def largeDataResult = legacyAdapter.exportData(largeData)
    * assert !validators.isSuccessful(largeDataResult)
    * def errorDetails = validators.getErrorDetails(largeDataResult)
    * assert errorDetails.message.contains('maximum field length')
    
    # Test legacy system performance characteristics
    * legacySystem.setProcessingDelay(500) // 500ms processing delay
    * def operation = function() { legacyAdapter.ping() }
    * def perfResults = perfTest.measureAverageTime(5, operation)
    * assert perfResults >= 500 // Should take at least 500ms due to legacy system delay