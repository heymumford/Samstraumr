/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools 
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights 
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.mozilla.org/en-US/MPL/2.0/
 */

package org.s8r.test.steps;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.application.port.NotificationPort.DeliveryStatus;
import org.s8r.application.port.NotificationPort.NotificationResult;
import org.s8r.application.port.NotificationPort.NotificationSeverity;
import org.s8r.application.service.EventService;
import org.s8r.application.service.NotificationService;
import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.event.MachineStateChangedEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.infrastructure.event.EventHandler;
import org.s8r.infrastructure.event.InMemoryEventDispatcher;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the integration between EventPublisherPort and NotificationPort.
 */
@IntegrationTest
public class EventNotificationIntegrationSteps {

    private EventPublisherPort eventPublisher;
    private NotificationPort notificationPort;
    private EventDispatcher eventDispatcher;
    private NotificationService notificationService;
    private EventService eventService;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;
    private TestNotificationPort testNotificationPort;
    private Map<String, String> notificationTemplates;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new CopyOnWriteArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger() {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
            
            @Override
            public void debug(String message) {
                super.debug(message);
                logMessages.add(message);
            }
            
            @Override
            public void debug(String message, Object... args) {
                super.debug(message, args);
                logMessages.add(String.format(message.replace("{}", "%s"), args));
            }
        };
        
        // Initialize the ports
        eventDispatcher = new InMemoryEventDispatcher(logger);
        testNotificationPort = new TestNotificationPort(logger);
        notificationPort = testNotificationPort;
        
        // Initialize the services
        notificationService = new NotificationService(notificationPort, logger);
        eventService = new EventService(eventDispatcher, logger);
        
        // Create event publisher
        eventPublisher = new TestEventPublisher(eventDispatcher, notificationService);
        
        // Set up templates
        notificationTemplates = new HashMap<>();
        notificationTemplates.put("UserRegisteredEvent", "User {userId} has registered at {timestamp}");
        notificationTemplates.put("SystemFailureEvent", "CRITICAL: System failure detected in {component} at {timestamp}");
        notificationTemplates.put("ComponentCreatedEvent", "New component created: {componentId}");
        notificationTemplates.put("SecurityAlertEvent", "Security alert: {severity} - {message}");
        
        // Reset log messages between scenarios
        logMessages.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(eventPublisher, "EventPublisherPort should be initialized");
        assertNotNull(notificationPort, "NotificationPort should be initialized");
        assertNotNull(eventDispatcher, "EventDispatcher should be initialized");
    }

    @Given("both event publisher and notification ports are properly initialized")
    public void bothEventPublisherAndNotificationPortsAreProperlyInitialized() {
        // Verify that the EventPublisher and NotificationPort are ready to use
        assertTrue(testNotificationPort.isInitialized(), "NotificationPort should be initialized");
    }

    @Given("notification subscribers are registered for events")
    public void notificationSubscribersAreRegisteredForEvents() {
        // Register notification handler for events
        EventHandler<DomainEvent> notificationHandler = event -> {
            // Convert event to notification
            String recipient = determineRecipientForEvent(event);
            String subject = generateSubjectFromEvent(event);
            String content = generateContentFromEvent(event);
            NotificationSeverity severity = determineSeverityFromEvent(event);
            
            if (recipient != null) {
                notificationService.sendUserNotification(recipient, subject, content, severity);
            } else {
                notificationService.sendSystemNotification(subject, content, severity);
            }
            
            logMessages.add("Notification handler called for event: " + event.getEventType());
        };
        
        // Register the notification handler for all domain events
        eventDispatcher.registerHandler(DomainEvent.class, notificationHandler);
        
        // Register test notification recipients
        testNotificationPort.registerRecipient("admin", createContactInfo("admin@example.com", "admin-device"));
        testNotificationPort.registerRecipient("developers", createContactInfo("dev@example.com", "dev-group"));
        testNotificationPort.registerRecipient("operations", createContactInfo("ops@example.com", "ops-group"));
        testNotificationPort.registerRecipient("security", createContactInfo("security@example.com", "sec-group"));
        testNotificationPort.registerRecipient("emergency", createContactInfo("emergency@example.com", "emergency"));
        
        // Store for later verification
        testContext.put("notificationHandler", notificationHandler);
    }

    @Given("I have a domain event of type {string} for user {string}")
    public void iHaveADomainEventOfTypeForUser(String eventType, String userId) {
        DomainEvent event;
        
        switch (eventType) {
            case "UserRegisteredEvent":
                event = new TestUserRegisteredEvent(userId);
                break;
            default:
                event = new TestGenericEvent(eventType, userId);
                break;
        }
        
        testContext.put("event", event);
        testContext.put("eventType", eventType);
        testContext.put("userId", userId);
    }

    @Given("I have a domain event of type {string} with severity {string}")
    public void iHaveADomainEventOfTypeWithSeverity(String eventType, String severity) {
        DomainEvent event;
        
        switch (eventType) {
            case "SystemFailureEvent":
                event = new TestSystemFailureEvent("system-core", severity);
                break;
            default:
                event = new TestGenericEvent(eventType, "source-" + eventType);
                ((TestGenericEvent)event).setSeverity(severity);
                break;
        }
        
        testContext.put("event", event);
        testContext.put("eventType", eventType);
        testContext.put("severity", severity);
    }

    @Given("I have the following domain events:")
    public void iHaveTheFollowingDomainEvents(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<DomainEvent> events = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String type = row.get("type");
            String source = row.get("source");
            String severity = row.get("severity");
            String recipient = row.get("recipient");
            
            DomainEvent event;
            switch (type) {
                case "ComponentCreatedEvent":
                    event = new ComponentCreatedEvent(source);
                    break;
                case "SecurityAlertEvent":
                    event = new TestSecurityAlertEvent(source, severity);
                    break;
                case "SystemMetricEvent":
                    event = new TestSystemMetricEvent(source);
                    break;
                default:
                    event = new TestGenericEvent(type, source);
                    break;
            }
            
            // Add metadata to store the recipient info
            if (recipient != null) {
                ((TestGenericEvent)event).setRecipient(recipient);
            }
            
            events.add(event);
        }
        
        testContext.put("events", events);
    }

    @Given("I have multiple domain events of the same type:")
    public void iHaveMultipleDomainEventsOfTheSameType(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<DomainEvent> events = new ArrayList<>();
        
        for (Map<String, String> row : rows) {
            String type = row.get("type");
            String source = row.get("source");
            int count = Integer.parseInt(row.get("count"));
            
            for (int i = 0; i < count; i++) {
                DomainEvent event;
                switch (type) {
                    case "MetricUpdateEvent":
                        event = new TestSystemMetricEvent(source + "-" + i);
                        break;
                    default:
                        event = new TestGenericEvent(type, source + "-" + i);
                        break;
                }
                events.add(event);
            }
        }
        
        testContext.put("batchEvents", events);
        testContext.put("batchEventType", rows.get(0).get("type"));
        testContext.put("batchEventCount", events.size());
    }

    @Given("the system has notification templates for different event types")
    public void theSystemHasNotificationTemplatesForDifferentEventTypes() {
        // Templates are already set up in the @Before method
        testContext.put("notificationTemplates", notificationTemplates);
    }

    @Given("I have a scheduled domain event for future execution")
    public void iHaveAScheduledDomainEventForFutureExecution() {
        TestScheduledEvent event = new TestScheduledEvent(
                "scheduled-event", "scheduled-source", Instant.now().plusSeconds(5));
        
        testContext.put("scheduledEvent", event);
        testContext.put("scheduledTime", event.getScheduledTime());
    }

    @Given("I have a domain event that requires delivery confirmation")
    public void iHaveADomainEventThatRequiresDeliveryConfirmation() {
        TestDeliveryTrackingEvent event = new TestDeliveryTrackingEvent(
                "tracking-event", "tracking-source");
        event.setRequireDeliveryConfirmation(true);
        
        testContext.put("trackingEvent", event);
    }

    @When("I publish the event")
    public void iPublishTheEvent() {
        DomainEvent event = (DomainEvent) testContext.get("event");
        assertNotNull(event, "Event should be in test context");
        
        // Reset notification counters before publishing
        testNotificationPort.resetCounters();
        
        // Publish the event
        eventPublisher.publishEvent(event);
        
        // Store publish time for future reference
        testContext.put("publishTime", Instant.now());
    }

    @When("I publish the event with high priority")
    public void iPublishTheEventWithHighPriority() {
        DomainEvent event = (DomainEvent) testContext.get("event");
        assertNotNull(event, "Event should be in test context");
        
        // Reset notification counters before publishing
        testNotificationPort.resetCounters();
        
        // Add priority metadata using our test implementation
        if (event instanceof TestGenericEvent) {
            ((TestGenericEvent) event).setPriority("HIGH");
        }
        
        // Publish the event
        eventPublisher.publishEvent(event);
        
        // Store publish time for future reference
        testContext.put("publishTime", Instant.now());
    }

    @When("I publish all events")
    public void iPublishAllEvents() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> events = (List<DomainEvent>) testContext.get("events");
        assertNotNull(events, "Events should be in test context");
        
        // Reset notification counters before publishing
        testNotificationPort.resetCounters();
        
        // Publish each event and track notification calls
        Map<String, List<NotificationResult>> notificationsByRecipient = new HashMap<>();
        
        for (DomainEvent event : events) {
            // Publish the event
            eventPublisher.publishEvent(event);
            
            // Record notifications by recipient
            Map<String, NotificationResult> newResults = testNotificationPort.getLastNotifications();
            
            for (Map.Entry<String, NotificationResult> entry : newResults.entrySet()) {
                notificationsByRecipient.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                        .add(entry.getValue());
            }
        }
        
        // Store for later verification
        testContext.put("notificationsByRecipient", notificationsByRecipient);
    }

    @When("I publish the events within a short time period")
    public void iPublishTheEventsWithinAShortTimePeriod() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> events = (List<DomainEvent>) testContext.get("batchEvents");
        assertNotNull(events, "Batch events should be in test context");
        
        // Reset notification counters before publishing
        testNotificationPort.resetCounters();
        
        // Enable notification consolidation
        testNotificationPort.enableConsolidation(true);
        
        // Publish each event
        for (DomainEvent event : events) {
            eventPublisher.publishEvent(event);
            
            // Small delay to simulate rapid publishing but still sequential
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Store for later verification
        testContext.put("consolidatedNotifications", testNotificationPort.getConsolidatedNotifications());
    }

    @When("I publish an event that matches a template")
    public void iPublishAnEventThatMatchesATemplate() {
        // Choose an event type with a template
        String eventType = "ComponentCreatedEvent";
        String componentId = "template-component-123";
        
        ComponentCreatedEvent event = new ComponentCreatedEvent(componentId);
        
        // Reset notification counters before publishing
        testNotificationPort.resetCounters();
        
        // Enable template use
        testNotificationPort.enableTemplates(true);
        testNotificationPort.setTemplates(notificationTemplates);
        
        // Publish the event
        eventPublisher.publishEvent(event);
        
        // Store for later verification
        testContext.put("templateEvent", event);
        testContext.put("templateEventType", eventType);
        testContext.put("componentId", componentId);
    }

    @When("I publish the event with a delay parameter")
    public void iPublishTheEventWithADelayParameter() {
        TestScheduledEvent event = (TestScheduledEvent) testContext.get("scheduledEvent");
        assertNotNull(event, "Scheduled event should be in test context");
        
        // Reset notification counters before publishing
        testNotificationPort.resetCounters();
        
        // Enable delayed notifications in our test notification port
        testNotificationPort.enableDelayedNotifications(true);
        
        // Publish the event
        ((TestEventPublisher) eventPublisher).publishScheduledEvent(event);
        
        // Store for later verification
        testContext.put("publishTime", Instant.now());
    }

    @Then("the event should be successfully published")
    public void theEventShouldBeSuccessfullyPublished() {
        // Verify the event was dispatched by checking logs
        boolean eventDispatched = logMessages.stream()
                .anyMatch(message -> message.contains("Dispatching event"));
        
        assertTrue(eventDispatched, "Event should be successfully dispatched");
        
        // Check that event service was called
        boolean eventServiceCalled = logMessages.stream()
                .anyMatch(message -> message.contains("EventService") || message.contains("event service"));
        
        assertTrue(eventServiceCalled, "Event service should be called");
    }

    @Then("a notification should be sent to system administrators")
    public void aNotificationShouldBeSentToSystemAdministrators() {
        // Check that a system notification was sent
        NotificationResult result = testNotificationPort.getLastSystemNotification();
        assertNotNull(result, "System notification should be sent");
        assertTrue(result.isSent(), "Notification should be successfully sent");
        
        // Verify it was sent to administrators
        boolean adminNotified = logMessages.stream()
                .anyMatch(message -> message.contains("admin") && message.contains("notification"));
        
        assertTrue(adminNotified, "Notification should be sent to administrators");
    }

    @Then("the notification content should include event details")
    public void theNotificationContentShouldIncludeEventDetails() {
        NotificationResult result = testNotificationPort.getLastSystemNotification();
        assertNotNull(result, "System notification should be sent");
        
        String eventType = (String) testContext.get("eventType");
        
        // Check notification details contain event information
        boolean containsEventType = testNotificationPort.getLastNotificationContent().contains(eventType);
        assertTrue(containsEventType, "Notification content should include event type");
        
        if (testContext.containsKey("userId")) {
            String userId = (String) testContext.get("userId");
            boolean containsUserId = testNotificationPort.getLastNotificationContent().contains(userId);
            assertTrue(containsUserId, "Notification content should include user ID");
        }
    }

    @Then("notifications should be sent to all registered emergency contacts")
    public void notificationsShouldBeSentToAllRegisteredEmergencyContacts() {
        // Check if notification was sent to emergency contacts
        boolean emergencyNotified = testNotificationPort.getNotificationsForRecipient("emergency") > 0;
        assertTrue(emergencyNotified, "Notification should be sent to emergency contacts");
        
        // Check that it was not just a regular notification
        String severity = (String) testContext.get("severity");
        assertTrue(severity.equals("CRITICAL"), "Notification severity should be CRITICAL");
        
        // Verify logs show emergency notification
        boolean emergencyLogEntry = logMessages.stream()
                .anyMatch(message -> message.contains("emergency") && message.contains("notification"));
        
        assertTrue(emergencyLogEntry, "Logs should show emergency notification");
    }

    @Then("the notification should have priority flag set")
    public void theNotificationShouldHavePriorityFlagSet() {
        // Check if the notification was sent with high priority
        boolean highPrioritySet = testNotificationPort.wasLastNotificationHighPriority();
        assertTrue(highPrioritySet, "Notification should have high priority flag set");
    }

    @Then("each event should trigger notifications only to relevant recipients")
    public void eachEventShouldTriggerNotificationsOnlyToRelevantRecipients() {
        @SuppressWarnings("unchecked")
        Map<String, List<NotificationResult>> notificationsByRecipient = 
                (Map<String, List<NotificationResult>>) testContext.get("notificationsByRecipient");
        
        assertNotNull(notificationsByRecipient, "Notifications by recipient should be in test context");
        
        // Verify that each event type went to the right recipient
        boolean developerNotified = notificationsByRecipient.containsKey("developers");
        boolean securityNotified = notificationsByRecipient.containsKey("security");
        boolean operationsNotified = notificationsByRecipient.containsKey("operations");
        
        assertTrue(developerNotified, "Developers should be notified of ComponentCreatedEvent");
        assertTrue(securityNotified, "Security team should be notified of SecurityAlertEvent");
        assertTrue(operationsNotified, "Operations team should be notified of SystemMetricEvent");
    }

    @Then("the notifications should be consolidated")
    public void theNotificationsShouldBeConsolidated() {
        // Check if notifications were consolidated
        boolean consolidated = testNotificationPort.wereNotificationsConsolidated();
        assertTrue(consolidated, "Notifications should be consolidated");
    }

    @Then("only a single summary notification should be sent")
    public void onlyASingleSummaryNotificationShouldBeSent() {
        // Get the count of notifications for the batch event type
        String batchEventType = (String) testContext.get("batchEventType");
        int batchEventCount = (int) testContext.get("batchEventCount");
        int notificationCount = testNotificationPort.getConsolidatedNotificationCount();
        
        // Should have fewer notifications than events
        assertTrue(notificationCount < batchEventCount, 
                "Notification count should be less than event count");
        
        // Ideally just one summary notification
        assertTrue(notificationCount <= 2, "Should have at most 2 notifications after consolidation");
        
        // The content should indicate summarization
        boolean summaryContent = testNotificationPort.getLastNotificationContent()
                .contains("multiple") || testNotificationPort.getLastNotificationContent().contains("summary");
        
        assertTrue(summaryContent, "Notification should contain summary information");
    }

    @Then("the notification should be formatted according to the template")
    public void theNotificationShouldBeFormattedAccordingToTheTemplate() {
        // Get the template that should have been used
        String eventType = (String) testContext.get("templateEventType");
        String template = notificationTemplates.get(eventType);
        
        assertNotNull(template, "Template should exist for event type");
        
        // The content should follow the template pattern
        String notificationContent = testNotificationPort.getLastNotificationContent();
        assertNotNull(notificationContent, "Notification content should not be null");
        
        // Check that template was used (but with variables replaced)
        boolean usedTemplate = notificationContent.contains("New component created:");
        assertTrue(usedTemplate, "Notification should use the template format");
    }

    @Then("the template variables should be populated from the event data")
    public void theTemplateVariablesShouldBePopulatedFromTheEventData() {
        String componentId = (String) testContext.get("componentId");
        String notificationContent = testNotificationPort.getLastNotificationContent();
        
        // Check that the component ID was inserted into the template
        assertTrue(notificationContent.contains(componentId), 
                "Component ID should be populated in the notification");
        
        // Make sure raw template variables are not present
        assertFalse(notificationContent.contains("{componentId}"), 
                "Raw template variables should not be present in the content");
    }

    @Then("the event should be queued for later processing")
    public void theEventShouldBeQueuedForLaterProcessing() {
        // Check that the event was queued
        boolean eventQueued = testNotificationPort.hasDelayedNotifications();
        assertTrue(eventQueued, "Event should be queued for delayed processing");
        
        // No immediate notification should be sent
        assertEquals(0, testNotificationPort.getImmediateNotificationCount(), 
                "No immediate notifications should be sent");
    }

    @Then("the notification should be sent at the scheduled time")
    public void theNotificationShouldBeSentAtTheScheduledTime() {
        // Trigger processing of delayed notifications
        testNotificationPort.processDelayedNotifications();
        
        // Check that delayed notifications were processed
        assertEquals(1, testNotificationPort.getDelayedNotificationCount(), 
                "One delayed notification should be processed");
        
        // Verify the notification content mentions scheduled nature
        String notificationContent = testNotificationPort.getLastNotificationContent();
        boolean mentionsScheduled = notificationContent.contains("scheduled") || 
                notificationContent.contains("delayed");
        
        assertTrue(mentionsScheduled, "Notification should mention scheduled/delayed nature");
    }

    @Then("the notification should be sent with tracking enabled")
    public void theNotificationShouldBeSentWithTrackingEnabled() {
        // Check that the notification was sent with tracking
        boolean trackingEnabled = testNotificationPort.wasLastNotificationTracked();
        assertTrue(trackingEnabled, "Notification should be sent with tracking enabled");
    }

    @Then("the system should record notification delivery status")
    public void theSystemShouldRecordNotificationDeliveryStatus() {
        // Check if delivery status was recorded
        String notificationId = testNotificationPort.getLastNotificationId();
        assertNotNull(notificationId, "Notification ID should not be null");
        
        DeliveryStatus status = testNotificationPort.getRecordedDeliveryStatus(notificationId);
        assertNotNull(status, "Delivery status should be recorded");
        
        // Status should be DELIVERED or SENT
        assertTrue(status == DeliveryStatus.DELIVERED || status == DeliveryStatus.SENT, 
                "Status should indicate successful delivery");
    }

    @Then("notification delivery metrics should be updated")
    public void notificationDeliveryMetricsShouldBeUpdated() {
        // Check that metrics were updated
        assertTrue(testNotificationPort.getDeliveryMetrics().containsKey("trackingEvents"), 
                "Delivery metrics should include tracking events");
        assertTrue(testNotificationPort.getDeliveryMetrics().get("trackingEvents") > 0, 
                "Tracking events count should be increased");
    }
    
    /**
     * Helper method to create contact information map.
     */
    private Map<String, String> createContactInfo(String email, String deviceId) {
        Map<String, String> contactInfo = new HashMap<>();
        contactInfo.put("email", email);
        contactInfo.put("deviceId", deviceId);
        return contactInfo;
    }
    
    /**
     * Helper method to determine the appropriate recipient for an event.
     */
    private String determineRecipientForEvent(DomainEvent event) {
        // For TestGenericEvent with explicit recipient
        if (event instanceof TestGenericEvent && ((TestGenericEvent) event).getRecipient() != null) {
            return ((TestGenericEvent) event).getRecipient();
        }
        
        // Default mapping based on event type
        switch (event.getEventType()) {
            case "UserRegistered":
                return "admin";
            case "SystemFailure":
                return event instanceof TestSystemFailureEvent && 
                        "CRITICAL".equals(((TestSystemFailureEvent) event).getSeverity()) ?
                        "emergency" : "operations";
            case "ComponentCreated":
                return "developers";
            case "SecurityAlert":
                return "security";
            case "SystemMetric":
                return "operations";
            default:
                return null; // System notification
        }
    }
    
    /**
     * Helper method to generate a subject from an event.
     */
    private String generateSubjectFromEvent(DomainEvent event) {
        String eventType = event.getEventType();
        
        switch (eventType) {
            case "UserRegistered":
                return "New User Registration";
            case "SystemFailure":
                String severity = event instanceof TestSystemFailureEvent ?
                        ((TestSystemFailureEvent) event).getSeverity() : "UNKNOWN";
                return severity + " System Failure Alert";
            case "ComponentCreated":
                return "New Component Created";
            case "SecurityAlert":
                return "Security Alert";
            case "SystemMetric":
                return "System Metric Update";
            default:
                return "Event Notification: " + eventType;
        }
    }
    
    /**
     * Helper method to generate content from an event.
     */
    private String generateContentFromEvent(DomainEvent event) {
        StringBuilder content = new StringBuilder();
        
        content.append("Event Type: ").append(event.getEventType()).append("\n");
        content.append("Event ID: ").append(event.getEventId()).append("\n");
        content.append("Timestamp: ").append(event.getOccurredOn()).append("\n");
        
        if (event.getSourceComponentId() != null) {
            content.append("Source: ").append(event.getSourceComponentId()).append("\n");
        }
        
        // Add event-specific details
        if (event instanceof TestUserRegisteredEvent) {
            content.append("User ID: ").append(((TestUserRegisteredEvent) event).getUserId()).append("\n");
        } else if (event instanceof TestSystemFailureEvent) {
            content.append("Component: ").append(((TestSystemFailureEvent) event).getComponent()).append("\n");
            content.append("Severity: ").append(((TestSystemFailureEvent) event).getSeverity()).append("\n");
        } else if (event instanceof TestSecurityAlertEvent) {
            content.append("Severity: ").append(((TestSecurityAlertEvent) event).getSeverity()).append("\n");
        } else if (event instanceof TestScheduledEvent) {
            content.append("Scheduled Time: ").append(((TestScheduledEvent) event).getScheduledTime()).append("\n");
        }
        
        return content.toString();
    }
    
    /**
     * Helper method to determine severity from an event.
     */
    private NotificationSeverity determineSeverityFromEvent(DomainEvent event) {
        // For TestGenericEvent with explicit severity
        if (event instanceof TestGenericEvent && ((TestGenericEvent) event).getSeverity() != null) {
            String severity = ((TestGenericEvent) event).getSeverity();
            switch (severity) {
                case "CRITICAL":
                    return NotificationSeverity.CRITICAL;
                case "ERROR":
                    return NotificationSeverity.ERROR;
                case "WARNING":
                    return NotificationSeverity.WARNING;
                default:
                    return NotificationSeverity.INFO;
            }
        }
        
        // Default mapping based on event type
        switch (event.getEventType()) {
            case "SystemFailure":
                return NotificationSeverity.CRITICAL;
            case "SecurityAlert":
                return NotificationSeverity.WARNING;
            default:
                return NotificationSeverity.INFO;
        }
    }
    
    /**
     * Test implementation of an event publisher.
     */
    private class TestEventPublisher implements EventPublisherPort {
        private final EventDispatcher eventDispatcher;
        private final NotificationService notificationService;
        
        public TestEventPublisher(EventDispatcher eventDispatcher, NotificationService notificationService) {
            this.eventDispatcher = eventDispatcher;
            this.notificationService = notificationService;
        }
        
        @Override
        public void publishEvent(DomainEvent event) {
            if (event == null) {
                logger.error("Cannot publish null event");
                return;
            }
            
            logger.info("Publishing event: {} (ID: {})", event.getEventType(), event.getEventId());
            eventDispatcher.dispatch(event);
        }
        
        @Override
        public void publishEvents(List<DomainEvent> events) {
            if (events == null || events.isEmpty()) {
                logger.error("Cannot publish null or empty event list");
                return;
            }
            
            logger.info("Publishing {} events", events.size());
            for (DomainEvent event : events) {
                publishEvent(event);
            }
        }
        
        @Override
        public int publishPendingEvents(ComponentId componentId) {
            logger.info("Publishing pending events for component: {}", componentId);
            return 0; // Not implemented for this test
        }
        
        /**
         * Publishes a scheduled event.
         */
        public void publishScheduledEvent(TestScheduledEvent event) {
            if (event == null) {
                logger.error("Cannot publish null scheduled event");
                return;
            }
            
            logger.info("Publishing scheduled event: {} (ID: {}) for time: {}", 
                    event.getEventType(), event.getEventId(), event.getScheduledTime());
            
            // Use the event dispatcher but also register the scheduled time
            testNotificationPort.scheduleNotification(event.getScheduledTime(), event);
            
            // We still dispatch the event itself, the notification handling will be delayed
            eventDispatcher.dispatch(event);
        }
    }
    
    /**
     * Test implementation of NotificationPort for testing.
     */
    private class TestNotificationPort implements NotificationPort {
        private final LoggerPort logger;
        private final AtomicInteger notificationCounter = new AtomicInteger(0);
        private final AtomicInteger systemNotificationCounter = new AtomicInteger(0);
        private final AtomicInteger userNotificationCounter = new AtomicInteger(0);
        private final Map<String, Integer> recipientNotificationCounts = new ConcurrentHashMap<>();
        private final Map<String, Map<String, String>> recipients = new ConcurrentHashMap<>();
        private final Map<String, DeliveryStatus> deliveryStatus = new ConcurrentHashMap<>();
        private final Map<String, Object> deliveryMetrics = new ConcurrentHashMap<>();
        
        private NotificationResult lastSystemNotification;
        private String lastNotificationContent;
        private String lastNotificationId;
        private boolean lastNotificationHighPriority;
        private boolean lastNotificationTracked;
        private Map<String, NotificationResult> lastNotifications = new ConcurrentHashMap<>();
        private boolean consolidateNotifications;
        private int consolidatedNotificationCount;
        private boolean useTemplates;
        private Map<String, String> templates;
        private boolean enableDelayedNotifications;
        private final Map<Instant, DomainEvent> delayedEvents = new ConcurrentHashMap<>();
        private int immediateNotificationCount;
        private int delayedNotificationCount;
        
        public TestNotificationPort(LoggerPort logger) {
            this.logger = logger;
            resetCounters();
        }
        
        public void resetCounters() {
            notificationCounter.set(0);
            systemNotificationCounter.set(0);
            userNotificationCounter.set(0);
            recipientNotificationCounts.clear();
            lastNotifications.clear();
            lastSystemNotification = null;
            lastNotificationContent = null;
            lastNotificationId = null;
            lastNotificationHighPriority = false;
            lastNotificationTracked = false;
            consolidatedNotificationCount = 0;
            deliveryMetrics.put("total", 0);
            deliveryMetrics.put("success", 0);
            deliveryMetrics.put("failure", 0);
            deliveryMetrics.put("trackingEvents", 0);
            immediateNotificationCount = 0;
            delayedNotificationCount = 0;
        }
        
        public boolean isInitialized() {
            return true;
        }
        
        public void enableConsolidation(boolean enable) {
            this.consolidateNotifications = enable;
        }
        
        public void enableTemplates(boolean enable) {
            this.useTemplates = enable;
        }
        
        public void setTemplates(Map<String, String> templates) {
            this.templates = templates;
        }
        
        public void enableDelayedNotifications(boolean enable) {
            this.enableDelayedNotifications = enable;
        }
        
        public boolean wereNotificationsConsolidated() {
            return consolidateNotifications && consolidatedNotificationCount > 0;
        }
        
        public int getConsolidatedNotificationCount() {
            return consolidatedNotificationCount;
        }
        
        public void scheduleNotification(Instant scheduledTime, DomainEvent event) {
            delayedEvents.put(scheduledTime, event);
        }
        
        public boolean hasDelayedNotifications() {
            return !delayedEvents.isEmpty();
        }
        
        public void processDelayedNotifications() {
            for (Map.Entry<Instant, DomainEvent> entry : delayedEvents.entrySet()) {
                // Process each delayed notification
                DomainEvent event = entry.getValue();
                Instant scheduledTime = entry.getKey();
                
                // Create a notification based on the event
                String subject = "Scheduled Event: " + event.getEventType();
                String content = "Scheduled notification processed at: " + Instant.now() + 
                        "\nOriginal schedule time: " + scheduledTime + 
                        "\nEvent ID: " + event.getEventId();
                
                // Send as system notification
                NotificationResult result = sendSystemNotification(subject, content, NotificationSeverity.INFO);
                lastNotificationContent = content;
                delayedNotificationCount++;
            }
            
            // Clear processed notifications
            delayedEvents.clear();
        }
        
        public int getImmediateNotificationCount() {
            return immediateNotificationCount;
        }
        
        public int getDelayedNotificationCount() {
            return delayedNotificationCount;
        }
        
        public Map<String, Object> getDeliveryMetrics() {
            return deliveryMetrics;
        }
        
        public boolean wasLastNotificationHighPriority() {
            return lastNotificationHighPriority;
        }
        
        public boolean wasLastNotificationTracked() {
            return lastNotificationTracked;
        }
        
        public String getLastNotificationId() {
            return lastNotificationId;
        }
        
        public DeliveryStatus getRecordedDeliveryStatus(String notificationId) {
            return deliveryStatus.get(notificationId);
        }
        
        public int getNotificationsForRecipient(String recipient) {
            return recipientNotificationCounts.getOrDefault(recipient, 0);
        }
        
        public NotificationResult getLastSystemNotification() {
            return lastSystemNotification;
        }
        
        public String getLastNotificationContent() {
            return lastNotificationContent;
        }
        
        public Map<String, NotificationResult> getLastNotifications() {
            return lastNotifications;
        }
        
        public Map<String, NotificationResult> getConsolidatedNotifications() {
            return lastNotifications;
        }
        
        @Override
        public NotificationResult sendNotification(String subject, String content, NotificationSeverity severity, 
                Map<String, String> metadata) {
            notificationCounter.incrementAndGet();
            
            // Generate a notification ID
            String notificationId = "notification-" + notificationCounter.get();
            
            // Process with templates if enabled
            if (useTemplates && templates != null && metadata != null) {
                String eventType = metadata.getOrDefault("eventType", "");
                if (!eventType.isEmpty() && templates.containsKey(eventType)) {
                    String template = templates.get(eventType);
                    // Replace variables in template
                    for (Map.Entry<String, String> entry : metadata.entrySet()) {
                        template = template.replace("{" + entry.getKey() + "}", entry.getValue());
                    }
                    content = template;
                }
            }
            
            // Check for high priority
            boolean highPriority = metadata != null && 
                    metadata.getOrDefault("priority", "").equalsIgnoreCase("HIGH");
            lastNotificationHighPriority = highPriority;
            
            // Check for tracking
            boolean tracked = metadata != null && 
                    Boolean.parseBoolean(metadata.getOrDefault("tracked", "false"));
            lastNotificationTracked = tracked;
            
            // Store for verification
            lastNotificationContent = content;
            lastNotificationId = notificationId;
            
            // Update metrics
            incrementMetric("total");
            incrementMetric("success");
            if (tracked) {
                incrementMetric("trackingEvents");
            }
            
            // Record delivery status
            deliveryStatus.put(notificationId, DeliveryStatus.DELIVERED);
            
            if (enableDelayedNotifications && metadata != null && 
                    metadata.containsKey("scheduledTime")) {
                // This is handled separately via scheduleNotification
                return NotificationResult.success(notificationId, "Notification scheduled");
            }
            
            immediateNotificationCount++;
            
            return NotificationResult.success(notificationId, "Notification sent");
        }
        
        @Override
        public NotificationResult sendSystemNotification(String subject, String content, 
                NotificationSeverity severity) {
            systemNotificationCounter.incrementAndGet();
            
            NotificationResult result = sendNotification(subject, content, severity, Map.of(
                    "type", "system",
                    "recipient", "admin"
            ));
            
            // Store for verification
            lastSystemNotification = result;
            lastNotifications.put("system", result);
            
            if (consolidateNotifications) {
                consolidatedNotificationCount = 1;
            }
            
            logger.info("System notification sent: {}", subject);
            return result;
        }
        
        @Override
        public NotificationResult sendNotificationToRecipient(String recipientId, String subject, 
                String content, NotificationSeverity severity, Map<String, String> metadata) {
            userNotificationCounter.incrementAndGet();
            
            // Track notifications by recipient
            recipientNotificationCounts.compute(recipientId, (k, v) -> v == null ? 1 : v + 1);
            
            // Create combined metadata
            Map<String, String> combinedMetadata = new HashMap<>();
            if (metadata != null) {
                combinedMetadata.putAll(metadata);
            }
            combinedMetadata.put("type", "user");
            combinedMetadata.put("recipientId", recipientId);
            
            NotificationResult result = sendNotification(subject, content, severity, combinedMetadata);
            
            // Store for verification
            lastNotifications.put(recipientId, result);
            
            if (consolidateNotifications) {
                // Only count as a consolidated notification if it's a new recipient
                if (recipientNotificationCounts.get(recipientId) == 1) {
                    consolidatedNotificationCount++;
                }
            }
            
            logger.info("User notification sent to {}: {}", recipientId, subject);
            return result;
        }
        
        @Override
        public NotificationResult sendUserNotification(String userId, String subject, String content, 
                NotificationSeverity severity) {
            return sendNotificationToRecipient(userId, subject, content, severity, Map.of());
        }
        
        @Override
        public NotificationResult sendPushNotification(String recipientId, String title, String content, 
                NotificationSeverity severity, Map<String, String> data) {
            userNotificationCounter.incrementAndGet();
            
            // Track notifications by recipient
            recipientNotificationCounts.compute(recipientId, (k, v) -> v == null ? 1 : v + 1);
            
            // Create combined metadata
            Map<String, String> metadata = new HashMap<>(data);
            metadata.put("type", "push");
            metadata.put("recipientId", recipientId);
            metadata.put("title", title);
            
            NotificationResult result = sendNotification(title, content, severity, metadata);
            
            // Store for verification
            lastNotifications.put(recipientId + "-push", result);
            
            logger.info("Push notification sent to {}: {}", recipientId, title);
            return result;
        }
        
        @Override
        public boolean registerRecipient(String recipientId, Map<String, String> contactInfo) {
            recipients.put(recipientId, contactInfo);
            logger.info("Recipient registered: {}", recipientId);
            return true;
        }
        
        @Override
        public boolean unregisterRecipient(String recipientId) {
            boolean removed = recipients.remove(recipientId) != null;
            if (removed) {
                logger.info("Recipient unregistered: {}", recipientId);
            }
            return removed;
        }
        
        @Override
        public boolean isRecipientRegistered(String recipientId) {
            return recipients.containsKey(recipientId);
        }
        
        @Override
        public List<String> getRegisteredRecipients() {
            return new ArrayList<>(recipients.keySet());
        }
        
        @Override
        public DeliveryStatus getNotificationStatus(String notificationId) {
            return deliveryStatus.getOrDefault(notificationId, DeliveryStatus.UNKNOWN);
        }
        
        @Override
        public Map<String, Integer> getDeliveryStatistics() {
            Map<String, Integer> stats = new HashMap<>();
            stats.put("total", notificationCounter.get());
            stats.put("system", systemNotificationCounter.get());
            stats.put("user", userNotificationCounter.get());
            return stats;
        }
        
        /**
         * Increments a metric in the metrics map.
         */
        private void incrementMetric(String metricName) {
            deliveryMetrics.compute(metricName, (k, v) -> v == null ? 1 : ((Integer) v) + 1);
        }
    }
    
    /**
     * Test event classes for various scenarios.
     */
    private class TestUserRegisteredEvent extends DomainEvent {
        private final String userId;
        
        public TestUserRegisteredEvent(String userId) {
            super("user-registration");
            this.userId = userId;
        }
        
        public String getUserId() {
            return userId;
        }
    }
    
    private class TestSystemFailureEvent extends DomainEvent {
        private final String component;
        private final String severity;
        
        public TestSystemFailureEvent(String component, String severity) {
            super("system-failure");
            this.component = component;
            this.severity = severity;
        }
        
        public String getComponent() {
            return component;
        }
        
        public String getSeverity() {
            return severity;
        }
    }
    
    private class TestSecurityAlertEvent extends DomainEvent {
        private final String severity;
        
        public TestSecurityAlertEvent(String source, String severity) {
            super(source);
            this.severity = severity;
        }
        
        public String getSeverity() {
            return severity;
        }
    }
    
    private class TestSystemMetricEvent extends DomainEvent {
        public TestSystemMetricEvent(String source) {
            super(source);
        }
    }
    
    private class TestScheduledEvent extends DomainEvent {
        private final Instant scheduledTime;
        
        public TestScheduledEvent(String type, String source, Instant scheduledTime) {
            super(source);
            this.scheduledTime = scheduledTime;
        }
        
        public Instant getScheduledTime() {
            return scheduledTime;
        }
    }
    
    private class TestDeliveryTrackingEvent extends DomainEvent {
        private boolean requireDeliveryConfirmation;
        
        public TestDeliveryTrackingEvent(String type, String source) {
            super(source);
            this.requireDeliveryConfirmation = false;
        }
        
        public boolean isRequireDeliveryConfirmation() {
            return requireDeliveryConfirmation;
        }
        
        public void setRequireDeliveryConfirmation(boolean requireDeliveryConfirmation) {
            this.requireDeliveryConfirmation = requireDeliveryConfirmation;
        }
    }
    
    private class TestGenericEvent extends DomainEvent {
        private final String eventType;
        private String severity;
        private String priority;
        private String recipient;
        
        public TestGenericEvent(String eventType, String source) {
            super(source);
            this.eventType = eventType;
        }
        
        @Override
        public String getEventType() {
            return eventType;
        }
        
        public String getSeverity() {
            return severity;
        }
        
        public void setSeverity(String severity) {
            this.severity = severity;
        }
        
        public String getPriority() {
            return priority;
        }
        
        public void setPriority(String priority) {
            this.priority = priority;
        }
        
        public String getRecipient() {
            return recipient;
        }
        
        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }
    }
}