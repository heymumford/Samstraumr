Feature: System Security
  As a system administrator
  I want to verify that the system implements proper security measures
  So that I can ensure data protection and regulatory compliance

  Background:
    # Import reusable JavaScript utilities
    * def adapterInit = read('../common/adapter-initializer.js')
    * def validators = read('../common/result-validator.js')
    * def testData = read('../common/test-data.js')
    * def perfTest = read('../common/performance-testing.js')
    
    # Import security-specific helpers
    * def secUtils = read('../helpers/security-utils.js')
    
    # Setup a secure test environment
    * def SecurityTestBuilder = Java.type('org.s8r.security.SecurityTestBuilder')
    * def testId = testData.shortUuid()
    * def env = SecurityTestBuilder.createSecureTestEnvironment('security-test-' + testId)
      .withSecurityMonitoring(true)
      .withAuditLogging(true)
      .build()

  @Security @Functional @Identity
  Scenario: System should authenticate access to protected resources
    # Configure authentication system
    * def authSystem = Java.type('org.s8r.security.AuthenticationSystemFactory').createTestInstance(env)
    * def protectedResource = Java.type('org.s8r.security.ProtectedResourceFactory').create('secret-data-' + testId)
    
    # Add authentication requirements to resource
    * def authConfig = { type: 'BASIC', level: 'HIGH' }
    * protectedResource.configureAuthentication(authConfig)
    
    # Create test client
    * def testClient = Java.type('org.s8r.test.client.TestClientFactory').create()
    
    # Attempt unauthenticated access
    * def unauthResult = testClient.accessResource(protectedResource.getUrl())
    * assert !validators.isSuccessful(unauthResult)
    * def errorDetails = validators.getErrorDetails(unauthResult)
    * assert errorDetails.message.contains('Authentication required')
    
    # Verify authentication request sent
    * def authRequest = protectedResource.getLastAuthenticationRequest()
    * assert authRequest != null
    * assert authRequest.challenged == true
    
    # Check authentication logging
    * def authLogs = authSystem.getAuthenticationLogs()
    * def failureLogs = secUtils.filterLogs(authLogs, 'AUTHENTICATION_FAILURE')
    * assert failureLogs.length > 0
    
    # Simulate repeated failures
    * for (var i = 0; i < 5; i++) testClient.accessResource(protectedResource.getUrl())
    
    # Verify security alerts
    * def alerts = authSystem.getSecurityAlerts()
    * assert alerts.length > 0
    * def brute = secUtils.findAlert(alerts, 'POTENTIAL_BRUTE_FORCE')
    * assert brute != null

  @Security @Functional @Configuration
  Scenario: System should enforce proper authorization for operations
    # Setup a system with multiple permission levels
    * def securedSystem = SecurityTestBuilder.createMultiLevelSystem(env)
    * def permissionLevels = securedSystem.getAvailablePermissionLevels()
    * assert permissionLevels.includes('ADMIN')
    * assert permissionLevels.includes('USER')
    
    # Create a limited permission user
    * def limitedUser = securedSystem.createUser('limited-user-' + testId, ['USER'])
    * def userContext = securedSystem.createUserContext(limitedUser)
    
    # Attempt operations requiring elevated permissions
    * def elevatedOperations = securedSystem.getAdminOperations()
    * def results = []
    * for (var i = 0; i < elevatedOperations.length; i++) {
        var op = elevatedOperations[i];
        results.push(userContext.attemptOperation(op));
      }
    
    # Verify unauthorized operations were denied
    * def successfulOps = results.filter(function(r) { return validators.isSuccessful(r); })
    * assert successfulOps.length == 0
    
    # Attempt authorized operations
    * def allowedOperations = securedSystem.getUserOperations()
    * def allowedResults = []
    * for (var i = 0; i < allowedOperations.length; i++) {
        var op = allowedOperations[i];
        allowedResults.push(userContext.attemptOperation(op));
      }
    
    # Verify authorized operations succeeded
    * def successfulAllowedOps = allowedResults.filter(function(r) { return validators.isSuccessful(r); })
    * assert successfulAllowedOps.length == allowedOperations.length
    
    # Check logging of authorization decisions
    * def authzLogs = securedSystem.getAuthorizationLogs()
    * assert authzLogs.length >= (elevatedOperations.length + allowedOperations.length)
    
    # Verify security violation reporting
    * def violations = securedSystem.getSecurityViolations()
    * assert violations.length >= elevatedOperations.length

  @Security @DataFlow @Pipeline
  Scenario: System should protect sensitive data throughout processing
    # Create a system processing different sensitivity levels
    * def dataSystem = Java.type('org.s8r.data.DataProcessingSystem').create(env)
    * def dataFactory = Java.type('org.s8r.data.SensitiveDataFactory')
    
    # Create test data with varying sensitivity
    * def publicData = dataFactory.createPublicData('public-' + testId)
    * def internalData = dataFactory.createInternalData('internal-' + testId)
    * def confidentialData = dataFactory.createConfidentialData('conf-' + testId)
    * def restrictedData = dataFactory.createRestrictedData('restricted-' + testId)
    
    # Process through pipeline
    * def processResult = dataSystem.processData([publicData, internalData, confidentialData, restrictedData])
    * assert validators.isSuccessful(processResult)
    
    # Verify data protection at rest
    * def storedDataResult = dataSystem.getStoredDataProtectionStatus()
    * assert validators.isSuccessful(storedDataResult)
    * def restProtection = validators.getAttribute(storedDataResult, 'protection')
    * assert restProtection.encrypted == true
    * assert restProtection.accessControlled == true
    
    # Verify protection in transit
    * def transitResult = dataSystem.getDataInTransitProtectionStatus()
    * assert validators.isSuccessful(transitResult)
    * def transitProtection = validators.getAttribute(transitResult, 'protection')
    * assert transitProtection.encrypted == true
    * assert transitProtection.integrityProtected == true
    
    # Verify access restrictions
    * def restrictedAccess = dataSystem.verifyRestrictedDataAccess()
    * assert validators.isSuccessful(restrictedAccess)
    * def restrictions = validators.getAttribute(restrictedAccess, 'restrictions')
    * assert restrictions.confidentialAccessible == false
    * assert restrictions.restrictedAccessible == false
    
    # Verify compliance with protection policies
    * def complianceResult = dataSystem.checkComplianceStatus()
    * assert validators.isSuccessful(complianceResult)
    * def compliance = validators.getAttribute(complianceResult, 'complianceStatus')
    * assert compliance.compliant == true

  @Security @Connection @Functional
  Scenario: System components should communicate securely
    # Setup distributed system spanning domains
    * def domainA = Java.type('org.s8r.security.SecurityDomainFactory').create('domain-a-' + testId)
    * def domainB = Java.type('org.s8r.security.SecurityDomainFactory').create('domain-b-' + testId)
    * def compA = Java.type('org.s8r.component.ComponentFactory').createInDomain(domainA, 'component-a')
    * def compB = Java.type('org.s8r.component.ComponentFactory').createInDomain(domainB, 'component-b')
    
    # Establish cross-domain communication
    * def connectionManager = Java.type('org.s8r.security.ConnectionManager').getInstance()
    * def connection = connectionManager.createConnection(compA, compB)
    * assert connection != null
    
    # Test communication encryption
    * def encryptionResult = connection.testEncryption()
    * assert validators.isSuccessful(encryptionResult)
    * def encryption = validators.getAttribute(encryptionResult, 'encryption')
    * assert encryption.enabled == true
    * assert encryption.algorithm != null
    
    # Test peer authentication
    * def authResult = connection.testPeerAuthentication()
    * assert validators.isSuccessful(authResult)
    * def peerAuth = validators.getAttribute(authResult, 'peerAuthentication')
    * assert peerAuth.authenticated == true
    * assert peerAuth.certificateValidated == true
    
    # Test tamper protection
    * def tamperedMessage = { id: 'msg-' + testId, content: 'test message', tampered: true }
    * def tamperResult = connection.sendMessage(tamperedMessage)
    * assert !validators.isSuccessful(tamperResult)
    * def error = validators.getErrorDetails(tamperResult)
    * assert error.message.contains('message integrity')
    
    # Verify secure protocol usage
    * def protocolResult = connection.getProtocolInfo()
    * assert validators.isSuccessful(protocolResult)
    * def protocol = validators.getAttribute(protocolResult, 'protocol')
    * assert protocol.name.startsWith('TLS')
    * assert protocol.version >= '1.2'

  @Security @Monitoring @Functional
  Scenario: System should maintain comprehensive security audit logs
    # Setup audit-enabled system
    * def auditSystem = Java.type('org.s8r.security.AuditSystem').create(env)
    
    # Generate security events
    * def events = Java.type('org.s8r.security.SecurityEventGenerator').generateEvents(10)
    * for (var i = 0; i < events.length; i++) auditSystem.recordEvent(events[i])
    
    # Verify all events were recorded
    * def auditLogs = auditSystem.getAuditLogs()
    * assert auditLogs.length >= events.length
    
    # Check log details
    * def firstLog = auditLogs[0]
    * assert firstLog.eventId != null
    * assert firstLog.timestamp != null
    * assert firstLog.userId != null
    * assert firstLog.action != null
    * assert firstLog.result != null
    * assert firstLog.sourceIp != null
    
    # Test log tampering protection
    * def tamperResult = Java.type('org.s8r.test.security.LogTamperingTest').attemptLogTampering(auditSystem)
    * assert !validators.isSuccessful(tamperResult)
    * def tamperError = validators.getErrorDetails(tamperResult)
    * assert tamperError.message.contains('tamper protection')
    
    # Verify retention policies
    * def retentionResult = auditSystem.checkRetentionPolicies()
    * assert validators.isSuccessful(retentionResult)
    * def retention = validators.getAttribute(retentionResult, 'retention')
    * assert retention.configured == true
    * assert retention.enforced == true

  @Security @Configuration @Functional
  Scenario: System should maintain proper isolation between tenants
    # Setup multi-tenant environment
    * def tenantSystem = Java.type('org.s8r.security.MultiTenantSystem').create(env)
    * def tenant1 = tenantSystem.createTenant('tenant1-' + testId)
    * def tenant2 = tenantSystem.createTenant('tenant2-' + testId)
    
    # Create tenant-specific data
    * def tenant1Data = Java.type('org.s8r.data.DataFactory').createForTenant(tenant1, 10)
    * def tenant2Data = Java.type('org.s8r.data.DataFactory').createForTenant(tenant2, 10)
    
    # Process data simultaneously
    * tenantSystem.processData(tenant1, tenant1Data)
    * tenantSystem.processData(tenant2, tenant2Data)
    
    # Verify tenant isolation
    * def tenant1Context = tenantSystem.getTenantContext(tenant1)
    * def tenant2Context = tenantSystem.getTenantContext(tenant2)
    
    # Verify data isolation
    * def tenant1Access = tenant1Context.getAccessibleData()
    * def tenant2Access = tenant2Context.getAccessibleData()
    * assert tenant1Access.length == 10 // Can only see own data
    * assert tenant2Access.length == 10 // Can only see own data
    
    # Attempt cross-tenant access
    * def crossAccessResult = tenant1Context.attemptAccessTenantData(tenant2)
    * assert !validators.isSuccessful(crossAccessResult)
    * def crossError = validators.getErrorDetails(crossAccessResult)
    * assert crossError.message.contains('unauthorized tenant access')
    
    # Verify isolation at processing stages
    * def isolationReport = tenantSystem.getTenantIsolationReport()
    * assert validators.isSuccessful(isolationReport) 
    * def isolation = validators.getAttribute(isolationReport, 'isolation')
    * assert isolation.dataIsolation == true
    * assert isolation.processingIsolation == true
    * assert isolation.storageIsolation == true
    * assert isolation.networkIsolation == true

  @Security @Resilience @Functional
  Scenario: System should be resilient against known security vulnerabilities
    # Setup vulnerability scanning
    * def scanSystem = Java.type('org.s8r.security.VulnerabilityScanSystem').create(env)
    * def scanner = scanSystem.getScanner()
    
    # Run vulnerability scan
    * def scanResult = scanner.performFullScan()
    * assert validators.isSuccessful(scanResult)
    
    # Check for critical/high vulnerabilities
    * def findings = validators.getAttribute(scanResult, 'findings')
    * def criticalFindings = findings.filter(function(f) { return f.severity === 'CRITICAL' || f.severity === 'HIGH'; })
    * assert criticalFindings.length == 0
    
    # Check dependency vulnerabilities
    * def depScanResult = scanner.checkDependencies()
    * assert validators.isSuccessful(depScanResult)
    * def vulnDeps = validators.getAttribute(depScanResult, 'vulnerableDependencies')
    * assert vulnDeps.unmitigated.length == 0
    
    # Verify secure coding practices
    * def codingResult = scanner.checkSecureCodingPractices()
    * assert validators.isSuccessful(codingResult)
    * def secureCoding = validators.getAttribute(codingResult, 'secureCoding')
    * assert secureCoding.compliant == true
    
    # Check security configuration
    * def configResult = scanner.checkSecurityConfiguration()
    * assert validators.isSuccessful(configResult)
    * def securityConfig = validators.getAttribute(configResult, 'securityConfiguration')
    * assert securityConfig.meetsBaseline == true

  @Security @ErrorHandling @Resilience
  Scenario: System should support security incident detection and response
    # Setup security monitoring
    * def securityMonitor = Java.type('org.s8r.security.SecurityMonitoringSystem').create(env)
    
    # Simulate security incident
    * def incident = Java.type('org.s8r.security.TestIncidentGenerator').createIncident('BRUTE_FORCE_ATTEMPT')
    * securityMonitor.injectSecurityEvent(incident)
    
    # Verify detection
    * def detectionResult = securityMonitor.checkDetection(incident.getId())
    * assert validators.isSuccessful(detectionResult)
    * def detected = validators.getAttribute(detectionResult, 'detected')
    * assert detected == true
    
    # Check alert generation
    * def alertResult = securityMonitor.getAlerts()
    * assert validators.isSuccessful(alertResult)
    * def alerts = validators.getAttribute(alertResult, 'alerts')
    * assert alerts.length > 0
    * def matchedAlert = secUtils.findMatchingAlert(alerts, incident.getId())
    * assert matchedAlert != null
    
    # Verify incident logging
    * def logResult = securityMonitor.getIncidentLogs()
    * assert validators.isSuccessful(logResult)
    * def logs = validators.getAttribute(logResult, 'logs')
    * assert logs.length > 0
    * def incidentLog = secUtils.findIncidentLog(logs, incident.getId())
    * assert incidentLog != null
    * assert incidentLog.details != null
    
    # Check investigation support
    * def investigationResult = securityMonitor.supportInvestigation(incident.getId())
    * assert validators.isSuccessful(investigationResult)
    * def investigation = validators.getAttribute(investigationResult, 'investigation')
    * assert investigation.contextAvailable == true
    * assert investigation.timeline != null
    * assert investigation.relatedEvents != null
    
    # Test containment capabilities
    * def containmentResult = securityMonitor.testContainment(incident.getId())
    * assert validators.isSuccessful(containmentResult)
    * def containment = validators.getAttribute(containmentResult, 'containment')
    * assert containment.available == true
    * assert containment.effective == true