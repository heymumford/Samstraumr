Feature: Validation Port Interface Tests
  As an application developer
  I want to use a standardized Validation Port interface
  So that I can validate data consistently across the application

  Background:
    * def ValidationAdapter = Java.type('org.s8r.infrastructure.validation.ValidationAdapter')
    * def validationAdapter = ValidationAdapter.createInstance()
    * def validationCheck = function(result) { return { valid: result.isValid(), errors: result.getErrors() } }

  Scenario: Validate strings against predefined patterns
    When def result = validationAdapter.validateString('email', 'test@example.com')
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateString('email', 'invalid-email')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('does not match pattern')
  
  Scenario: Validate numbers against numeric rules
    When def result = validationAdapter.validateNumber('positive', 10)
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateNumber('positive', -5)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('does not match rule')

  Scenario: Validate required fields
    When def result = validationAdapter.validateRequired('username', 'john')
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateRequired('username', '')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('is required')
    
    When def result = validationAdapter.validateRequired('username', null)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('is required')

  Scenario: Validate string length
    When def result = validationAdapter.validateLength('username', 'john', 3, 10)
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateLength('username', 'jo', 3, 10)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('length must be between')
    
    When def result = validationAdapter.validateLength('username', 'johnjohnjohn', 3, 10)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('length must be between')

  Scenario: Validate numeric ranges
    When def result = validationAdapter.validateRange('age', 25, 18, 65)
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateRange('age', 15, 18, 65)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('must be between')
    
    When def result = validationAdapter.validateRange('age', 70, 18, 65)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('must be between')

  Scenario: Validate against custom regular expressions
    When def result = validationAdapter.validatePattern('zipcode', '12345', '^\\d{5}$')
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validatePattern('zipcode', '1234', '^\\d{5}$')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('does not match the required pattern')
    
    When def result = validationAdapter.validatePattern('zipcode', 'A1234', '^\\d{5}$')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('does not match the required pattern')

  Scenario: Validate against allowed values
    # Convert Java list to JavaScript array
    * def allowedValues = Java.to([
        'active',
        'inactive',
        'pending'
      ], 'java.util.List')
    
    When def result = validationAdapter.validateAllowedValues('status', 'active', allowedValues)
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateAllowedValues('status', 'deleted', allowedValues)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('must be one of')

  Scenario: Register and validate against rule sets
    # Prepare a rule set
    * def ruleSet = { 'username': 'alphanumeric', 'email': 'email', 'age': 'positive' }
    * validationAdapter.registerRuleSet('user', ruleSet)
    
    # Valid data
    * def userData = { 'username': 'john123', 'email': 'john@example.com', 'age': 30 }
    When def result = validationAdapter.validateMap('user', userData)
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    # Invalid data
    * def invalidUserData = { 'username': 'john@123', 'email': 'not-an-email', 'age': -5 }
    When def result = validationAdapter.validateMap('user', invalidUserData)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 3
    And match check.errors[*] contains '#regex .*username.*'
    And match check.errors[*] contains '#regex .*email.*'
    And match check.errors[*] contains '#regex .*age.*'

  Scenario: Validate entity data
    # Valid component data
    * def validComponent = { 'id': 'comp-123', 'name': 'Test Component', 'state': 'ACTIVE' }
    When def result = validationAdapter.validateEntity('component', validComponent)
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    # Invalid component data
    * def invalidComponent = { 'id': 'c', 'name': '', 'state': 'ACTIVE' }
    When def result = validationAdapter.validateEntity('component', invalidComponent)
    Then def check = validationCheck(result)
    And assert check.valid == false
    And match check.errors[*] contains '#regex .*id.*'
    And match check.errors[*] contains '#regex .*name.*'

  Scenario: Register and use custom validation rules
    # Register a custom pattern rule
    * validationAdapter.registerRule('uppercase', java.util.regex.Pattern.compile('^[A-Z]+$'))
    
    # Test the custom rule
    When def result = validationAdapter.validateString('uppercase', 'ABC')
    Then def check = validationCheck(result)
    And assert check.valid == true
    And assert check.errors.length == 0
    
    When def result = validationAdapter.validateString('uppercase', 'abc')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1

  Scenario: Handle non-existent validation rules
    When def result = validationAdapter.validateString('non-existent-rule', 'any-value')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('rule not found')

  Scenario: Handle null values in validation
    When def result = validationAdapter.validatePattern('field', null, '^test$')
    Then def check = validationCheck(result)
    And assert check.valid == false
    And assert check.errors.length == 1
    And assert check.errors[0].contains('cannot be null')

  Scenario: Performance test for basic validation
    # Define a function to measure performance
    * def measureTime = function(count, op) { var start = java.lang.System.nanoTime(); for (var i = 0; i < count; i++) { op(); }; return (java.lang.System.nanoTime() - start) / (count * 1000000); }
    
    # Measure the average time for string validation
    * def validateEmail = function() { validationAdapter.validateString('email', 'test@example.com'); }
    * def avgTimeMs = measureTime(1000, validateEmail)
    
    Then assert avgTimeMs < 1.0 // Should take less than 1ms per validation on average