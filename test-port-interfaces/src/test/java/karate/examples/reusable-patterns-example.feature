Feature: Reusable Patterns Example
  As a developer
  I want to use standardized patterns for Karate tests
  So that I can write more consistent and maintainable tests

  Background:
    # Import the reusable utility files
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def perfTest = read('../common/performance-testing.js')
    * def testData = read('../common/test-data.js')
    
    # Create adapters using the standardized initializers
    * def cacheAdapter = adapterInit.createCacheAdapter()
    * def validationAdapter = adapterInit.createValidationAdapter()
    * def configAdapter = adapterInit.createConfigAdapterWithProps(testData.generateConfigData())
    
    # Generate test data with random values
    * def testId = testData.shortUuid()
    * def cacheName = 'test-cache-' + testId
    * def testUser = testData.generateUserData()
    * def testComponent = testData.generateComponentData()

  Scenario: Validate and store a user in cache
    # Initialize cache
    * cacheAdapter.initialize(cacheName)
    
    # Validate user data
    * def validationResult = validationAdapter.validateEntity('user', testUser)
    * def check = validators.processValidationResult(validationResult)
    * assert check.valid == true
    
    # Store user in cache
    * def putResult = cacheAdapter.put('user-' + testUser.id, testUser)
    * assert validators.isSuccessful(putResult)
    
    # Retrieve and validate cached user
    * def getResult = cacheAdapter.get('user-' + testUser.id)
    * assert validators.optionalHasValue(getResult)
    * def cachedUser = validators.optionalValue(getResult)
    * assert cachedUser.id == testUser.id
    * assert cachedUser.username == testUser.username

  Scenario: Measure performance of validation operations
    # Test setup
    * def testValue = 'test@example.com'
    * def validateEmail = function() { validationAdapter.validateString('email', testValue) }
    
    # Run performance test
    * def avgTime = perfTest.measureAverageTime(1000, validateEmail)
    * print 'Average validation time:', avgTime, 'ms'
    * assert avgTime < 5.0 // Should be very fast
    
    # Run throughput test
    * def throughputResults = perfTest.runThroughputTest(validateEmail, 1)
    * def report = perfTest.createPerformanceReport(throughputResults, 'Email Validation')
    * print report
    * assert throughputResults.operationsPerSecond > 1000 // Should process 1000+ operations per second

  Scenario: Test configuration with operation result validation
    # Get multiple config properties
    * def appName = configAdapter.getString('app.name')
    * def appVersion = configAdapter.getString('app.version')
    * def debug = configAdapter.getBoolean('app.debug')
    
    # Validate using the optional helpers
    * assert validators.optionalHasValue(appName)
    * assert validators.optionalValue(appName) == 'Samstraumr'
    * assert validators.optionalHasValue(appVersion)
    * assert validators.optionalHasValue(debug)
    
    # Generate a temporary file path
    * def tempDir = testData.generateFilePaths('/tmp')
    * def configFile = tempDir.jsonFile
    
    # Save and validate result
    * def saveResult = configAdapter.saveConfigurationWithDetails(configFile)
    * assert validators.isSuccessful(saveResult)
    * def errorDetails = validators.getErrorDetails(saveResult)
    * assert errorDetails.message == null