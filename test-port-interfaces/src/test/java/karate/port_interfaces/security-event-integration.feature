Feature: Security Event Integration Tests
  As a system operator
  I want security events to be properly published via the event system
  So that security incidents can be monitored and addressed

  Background:
    * def SecurityUtils = read('classpath:karate/helpers/security-utils.js')
    * def securityAdapter = Java.type('org.s8r.test.mock.MockSecurityAdapter').createInstance()
    * def eventPublisher = Java.type('org.s8r.test.mock.MockEventPublisherAdapter').createInstance()
    * def securityEventBridge = Java.type('org.s8r.infrastructure.security.SecurityEventBridge').create(securityAdapter, eventPublisher)
    * def username = 'testuser'
    * def validPassword = 'validPassword123'
    * def invalidPassword = 'wrongPassword'

  Scenario: Authentication success events should be published
    Given securityAdapter.configure({})
    When def authResult = securityAdapter.authenticate(username, validPassword)
    Then assert authResult.success
    And def events = eventPublisher.getCapturedEvents()
    And match events contains deep { eventType: 'AUTHENTICATION_SUCCESS', username: '#(username)' }
    And def successEvent = karate.jsonPath(events, "$[?(@.eventType == 'AUTHENTICATION_SUCCESS')]")[0]
    And assert successEvent.timestamp != null
    And assert successEvent.sourceIp != null

  Scenario: Authentication failure events should be published
    Given securityAdapter.configure({})
    When def authResult = securityAdapter.authenticate(username, invalidPassword)
    Then assert !authResult.success
    And def events = eventPublisher.getCapturedEvents()
    And match events contains deep { eventType: 'AUTHENTICATION_FAILURE', username: '#(username)' }
    And def failureEvent = karate.jsonPath(events, "$[?(@.eventType == 'AUTHENTICATION_FAILURE')]")[0]
    And match failureEvent.details.reason == 'INVALID_CREDENTIALS'
    And assert failureEvent.timestamp != null
    And assert failureEvent.sourceIp != null

  Scenario: Multiple authentication failures should trigger a brute force detection event
    Given securityAdapter.configure({ maxFailedAttempts: 5, bruteForceWindowSeconds: 60 })
    When eval
      """
      for(var i = 0; i < 5; i++) {
        securityAdapter.authenticate(username, invalidPassword);
      }
      """
    Then def events = eventPublisher.getCapturedEvents()
    And match events contains deep { eventType: 'BRUTE_FORCE_ATTEMPT_DETECTED', username: '#(username)' }
    And def bruteForceEvent = karate.jsonPath(events, "$[?(@.eventType == 'BRUTE_FORCE_ATTEMPT_DETECTED')]")[0]
    And match bruteForceEvent.details.attemptCount == 5
    And match bruteForceEvent.details.timeWindowSeconds == 60
    And assert bruteForceEvent.sourceIp != null

  Scenario: Authorization failure events should be published
    Given def token = SecurityUtils.generateAuthToken(username, ['USER'])
    And securityAdapter.configure({ requiredRoleForResource: 'ADMIN' })
    When def authzResult = securityAdapter.checkAuthorization(token, '/admin/resource')
    Then assert !authzResult.authorized
    And def events = eventPublisher.getCapturedEvents()
    And match events contains deep { eventType: 'AUTHORIZATION_FAILURE', username: '#(username)' }
    And def authzEvent = karate.jsonPath(events, "$[?(@.eventType == 'AUTHORIZATION_FAILURE')]")[0]
    And match authzEvent.details.resource == '/admin/resource'
    And match authzEvent.details.requiredRole == 'ADMIN'
    And match authzEvent.details.userRoles contains 'USER'

  Scenario: Session expiration events should be published
    Given def token = SecurityUtils.generateAuthToken(username, ['USER'])
    And securityAdapter.configure({ sessionTimeoutSeconds: 1 })
    When securityAdapter.createSession(token)
    And eval
      """
      var Thread = Java.type('java.lang.Thread');
      Thread.sleep(1500); // Wait for session to expire
      securityAdapter.checkSession(token);
      """
    Then def events = eventPublisher.getCapturedEvents()
    And match events contains deep { eventType: 'SESSION_EXPIRED', username: '#(username)' }
    And def sessionEvent = karate.jsonPath(events, "$[?(@.eventType == 'SESSION_EXPIRED')]")[0]
    And assert sessionEvent.details.sessionId != null
    And assert sessionEvent.details.creationTime != null
    And assert sessionEvent.details.expirationTime != null

  Scenario Outline: Security configuration change events should be published
    Given securityAdapter.configure({})
    When securityAdapter.updateSecuritySetting('<settingName>', '<oldValue>', '<newValue>', 'admin')
    Then def events = eventPublisher.getCapturedEvents()
    And match events contains deep { eventType: 'SECURITY_CONFIGURATION_CHANGED' }
    And def configEvent = karate.jsonPath(events, "$[?(@.eventType == 'SECURITY_CONFIGURATION_CHANGED')]")[0]
    And match configEvent.details.settingName == '<settingName>'
    And match configEvent.details.oldValue == '<oldValue>'
    And match configEvent.details.newValue == '<newValue>'
    And match configEvent.details.changedBy == 'admin'

    Examples:
      | settingName           | oldValue | newValue |
      | passwordMinLength     | 8        | 12       |
      | maxLoginAttempts      | 3        | 5        |
      | sessionTimeoutMinutes | 30       | 15       |
      | mfaRequired           | false    | true     |