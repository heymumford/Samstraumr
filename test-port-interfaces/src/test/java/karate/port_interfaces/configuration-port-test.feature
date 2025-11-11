Feature: Configuration Port Interface Tests
  As an application developer
  I want to use a standardized Configuration Port interface
  So that I can manage application configuration properties in a consistent way

  Background:
    * def configAdapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createInstance()
    * def UUID = Java.type('java.util.UUID')
    * def testProperties = 
    """
    {
      'app.name': 'Samstraumr',
      'app.version': '2.7.1',
      'app.debug': 'true',
      'app.maxConnections': '100',
      'app.tags': 'java,clean-architecture,testing',
      'module.cache.enabled': 'true',
      'module.cache.size': '1024',
      'module.auth.enabled': 'false'
    }
    """
    * def FileUtils = Java.type('org.s8r.test.utils.FileSystemTestUtils')
    * def tempDir = FileUtils.createTempDirectory('karate-config-test-' + UUID.randomUUID().toString().substring(0, 8))

  Scenario: Initialize a configuration adapter
    Given configAdapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createInstance()
    When configAdapter.setProperty('test.key', 'test.value')
    Then assert configAdapter.getString('test.key').isPresent()
    And assert configAdapter.getString('test.key').get() == 'test.value'

  Scenario: Initialize with default properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def value = adapter.getString('app.name').get()
    Then assert value == 'Samstraumr'
    And def version = adapter.getString('app.version').get()
    And assert version == '2.7.1'

  Scenario: Get string properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def appName = adapter.getString('app.name').get()
    Then assert appName == 'Samstraumr'
    And def defaultValue = adapter.getString('nonexistent', 'DefaultValue')
    And assert defaultValue == 'DefaultValue'

  Scenario: Get integer properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def maxConnections = adapter.getInteger('app.maxConnections').get()
    Then assert maxConnections == 100
    And def defaultValue = adapter.getInteger('nonexistent', 42)
    And assert defaultValue == 42

  Scenario: Get boolean properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def debug = adapter.getBoolean('app.debug').get()
    Then assert debug == true
    And def cacheEnabled = adapter.getBoolean('module.cache.enabled').get()
    And assert cacheEnabled == true
    And def authEnabled = adapter.getBoolean('module.auth.enabled').get()
    And assert authEnabled == false
    And def defaultValue = adapter.getBoolean('nonexistent', true)
    And assert defaultValue == true

  Scenario: Set and retrieve configuration properties
    Given configAdapter.clear()
    When configAdapter.setProperty('test.string', 'test-value')
    And configAdapter.setProperty('test.integer', '42')
    And configAdapter.setProperty('test.boolean', 'true')
    Then assert configAdapter.getString('test.string').get() == 'test-value'
    And assert configAdapter.getInteger('test.integer').get() == 42
    And assert configAdapter.getBoolean('test.boolean').get() == true

  Scenario: Remove configuration properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def removed = adapter.removeProperty('app.name')
    Then assert removed == true
    And assert adapter.hasProperty('app.name') == false
    When def removedNonExistent = adapter.removeProperty('nonexistent')
    Then assert removedNonExistent == false

  Scenario: Check if properties exist
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def exists = adapter.hasProperty('app.name')
    Then assert exists == true
    When def nonExistentExists = adapter.hasProperty('nonexistent')
    Then assert nonExistentExists == false

  Scenario: Get all configuration properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def allProps = adapter.getAllProperties()
    Then assert Object.keys(allProps).length == 8
    And assert allProps['app.name'] == 'Samstraumr'
    And assert allProps['app.version'] == '2.7.1'
    And assert allProps['nonexistent'] == null

  Scenario: Get properties with prefix
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def moduleProps = adapter.getPropertiesWithPrefix('module.')
    Then assert Object.keys(moduleProps).length == 3
    And assert moduleProps['module.cache.enabled'] == 'true'
    And assert moduleProps['module.cache.size'] == '1024'
    And assert moduleProps['module.auth.enabled'] == 'false'
    And assert moduleProps['app.name'] == null
    
    When def cacheProps = adapter.getPropertiesWithPrefix('module.cache.')
    Then assert Object.keys(cacheProps).length == 2
    And assert cacheProps['module.cache.enabled'] == 'true'
    And assert cacheProps['module.cache.size'] == '1024'
    And assert cacheProps['module.auth.enabled'] == null

  Scenario: Split comma-separated values into list
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When def tagsList = adapter.getStringList('app.tags')
    Then assert tagsList.length == 3
    And assert tagsList[0] == 'java'
    And assert tagsList[1] == 'clean-architecture'
    And assert tagsList[2] == 'testing'

  Scenario: Load configuration from file
    Given configAdapter.clear()
    And def props = karate.fromJson(testProperties)
    And def tempFile = FileUtils.createTempFile(tempDir, 'test.properties')
    And def javaProps = Java.type('java.util.Properties').new()
    And eval for (var key in props) javaProps.setProperty(key, props[key])
    And FileUtils.storeProperties(javaProps, tempFile, 'Test properties for Karate test')
    When def result = configAdapter.loadConfigurationWithDetails(tempFile)
    Then assert result.isSuccess() == true
    And def appName = configAdapter.getString('app.name').get()
    And assert appName == 'Samstraumr'

  Scenario: Save configuration to file
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    And def tempFile = FileUtils.createTempFile(tempDir, 'save.properties')
    When def result = adapter.saveConfigurationWithDetails(tempFile)
    Then assert result.isSuccess() == true
    And def savedProps = Java.type('java.util.Properties').new()
    And FileUtils.loadProperties(savedProps, tempFile)
    And assert savedProps.getProperty('app.name') == 'Samstraumr'
    And assert savedProps.getProperty('app.version') == '2.7.1'

  Scenario: Clear all configuration properties
    Given def props = karate.fromJson(testProperties)
    And def adapter = Java.type('org.s8r.infrastructure.config.PropertiesConfigurationAdapter').createWithProperties(props)
    When adapter.clear()
    Then def allProps = adapter.getAllProperties()
    And assert Object.keys(allProps).length == 0

  Scenario: Handle loading configuration from nonexistent file
    Given configAdapter.clear()
    When def result = configAdapter.loadConfigurationWithDetails('/nonexistent/file.properties')
    Then assert result.isSuccess() == false
    And assert result.getErrorMessage().isPresent() == true
    And assert result.getException().isPresent() == true

  Scenario: Handle parsing invalid integer values
    Given configAdapter.clear()
    When configAdapter.setProperty('invalid.integer', 'not-a-number')
    Then def intOptional = configAdapter.getInteger('invalid.integer')
    And assert intOptional.isPresent() == false
    But def defaultValue = configAdapter.getInteger('invalid.integer', 42)
    And assert defaultValue == 42

  Scenario: Handle parsing invalid boolean values
    Given configAdapter.clear()
    When configAdapter.setProperty('invalid.boolean', 'not-a-boolean')
    Then def boolOptional = configAdapter.getBoolean('invalid.boolean')
    And assert boolOptional.isPresent() == false
    But def defaultValue = configAdapter.getBoolean('invalid.boolean', true)
    And assert defaultValue == true

  Scenario: Register and notify configuration change listener
    Given configAdapter.clear()
    And def listener = Java.type('org.s8r.test.mock.MockConfigurationListener').createInstance()
    When def listenerId = configAdapter.registerChangeListener(listener)
    And configAdapter.setProperty('test.property', 'new-value')
    Then assert listener.wasNotified() == true
    And assert listener.getLastKey() == 'test.property'
    And assert listener.getLastValue() == 'new-value'
    And assert listener.getLastChangeType().name() == 'SET'

  Scenario: Unregister configuration change listener
    Given configAdapter.clear()
    And def listener = Java.type('org.s8r.test.mock.MockConfigurationListener').createInstance()
    When def listenerId = configAdapter.registerChangeListener(listener)
    And def unregistered = configAdapter.unregisterChangeListener(listenerId)
    And configAdapter.setProperty('test.after.unregister', 'should-not-notify')
    Then assert unregistered == true
    And assert listener.getLastKey() == null