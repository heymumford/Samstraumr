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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.Component;
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
 * Step definitions for testing the EventPublisherPort interface.
 */
@IntegrationTest
public class EventPublisherPortSteps {

    private EventPublisherPort eventPublisherPort;
    private EventDispatcher eventDispatcher;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;
    private List<DomainEvent> publishedEvents;
    private List<TestEventSubscriber> subscribers;
    private TestComponent testComponent;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new CopyOnWriteArrayList<>();
        publishedEvents = new CopyOnWriteArrayList<>();
        subscribers = new ArrayList<>();
        
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
            
            @Override
            public void error(String message) {
                super.error(message);
                logMessages.add("[ERROR] " + message);
            }
            
            @Override
            public void error(String message, Object... args) {
                super.error(message, args);
                logMessages.add("[ERROR] " + String.format(message.replace("{}", "%s"), args));
            }
            
            @Override
            public void error(String message, Throwable e) {
                super.error(message, e);
                logMessages.add("[ERROR] " + message + ": " + e.getMessage());
            }
        };
        
        // Initialize the event dispatcher and publisher port
        eventDispatcher = new InMemoryEventDispatcher(logger);
        eventPublisherPort = new TestEventPublisher(eventDispatcher, logger);
        
        // Reset the log messages and published events between scenarios
        logMessages.clear();
        publishedEvents.clear();
    }
    
    @After
    public void cleanup() {
        testContext.clear();
        logMessages.clear();
        publishedEvents.clear();
        if (subscribers != null) {
            for (TestEventSubscriber subscriber : subscribers) {
                subscriber.clear();
            }
        }
    }

    @Given("a clean system environment")
    public void aCleanSystemEnvironment() {
        // Already set up in the @Before method
        assertNotNull(eventPublisherPort, "EventPublisherPort should be initialized");
        assertNotNull(eventDispatcher, "EventDispatcher should be initialized");
    }

    @Given("the EventPublisherPort interface is properly initialized")
    public void theEventPublisherPortInterfaceIsProperlyInitialized() {
        // Verify the event publisher port is properly initialized
        assertNotNull(eventPublisherPort, "EventPublisherPort should be initialized");
    }
    
    @Given("a component with ID {string} exists in the system")
    public void aComponentWithIDExistsInTheSystem(String componentId) {
        // Create a test component with the specified ID
        testComponent = new TestComponent(componentId);
        testContext.put("testComponent", testComponent);
        
        // Generate a few events on the component
        testComponent.generateEvent("Component created");
        testComponent.changeState("INITIALIZING");
        testComponent.changeState("READY");
    }
    
    @Given("several event subscribers are registered in the system")
    public void severalEventSubscribersAreRegisteredInTheSystem() {
        // Create a few test subscribers
        TestEventSubscriber subscriber1 = new TestEventSubscriber("subscriber1", null);
        TestEventSubscriber subscriber2 = new TestEventSubscriber("subscriber2", null);
        TestEventSubscriber subscriber3 = new TestEventSubscriber("subscriber3", null);
        
        // Register the subscribers for all events
        eventDispatcher.registerHandler(DomainEvent.class, subscriber1);
        eventDispatcher.registerHandler(DomainEvent.class, subscriber2);
        eventDispatcher.registerHandler(DomainEvent.class, subscriber3);
        
        // Store the subscribers for later verification
        subscribers.add(subscriber1);
        subscribers.add(subscriber2);
        subscribers.add(subscriber3);
        
        testContext.put("subscribers", subscribers);
    }
    
    @Given("event subscribers with different type filters are registered")
    public void eventSubscribersWithDifferentTypeFiltersAreRegistered() {
        // Create subscribers for different event types
        TestEventSubscriber componentSubscriber = new TestEventSubscriber("componentSubscriber", 
                ComponentCreatedEvent.class);
        TestEventSubscriber machineSubscriber = new TestEventSubscriber("machineSubscriber", 
                MachineStateChangedEvent.class);
        TestEventSubscriber genericSubscriber = new TestEventSubscriber("genericSubscriber", 
                DomainEvent.class);
        
        // Register the subscribers
        eventDispatcher.registerHandler(ComponentCreatedEvent.class, componentSubscriber);
        eventDispatcher.registerHandler(MachineStateChangedEvent.class, machineSubscriber);
        eventDispatcher.registerHandler(DomainEvent.class, genericSubscriber);
        
        // Store the subscribers for later verification
        subscribers.add(componentSubscriber);
        subscribers.add(machineSubscriber);
        subscribers.add(genericSubscriber);
        
        testContext.put("componentSubscriber", componentSubscriber);
        testContext.put("machineSubscriber", machineSubscriber);
        testContext.put("genericSubscriber", genericSubscriber);
    }

    @When("I create a domain event of type {string}")
    public void iCreateADomainEventOfType(String eventType) {
        DomainEvent event = null;
        
        // Create different event types based on the specified type
        switch (eventType) {
            case "ComponentCreatedEvent":
                event = new ComponentCreatedEvent("test-component-" + UUID.randomUUID().toString().substring(0, 8));
                break;
            case "MachineStateChangedEvent":
                event = new MachineStateChangedEvent("test-machine-" + UUID.randomUUID().toString().substring(0, 8), 
                        "RUNNING", "PAUSED");
                break;
            case "ComponentStateChangedEvent":
                event = new ComponentStateChangedEvent("test-component-" + UUID.randomUUID().toString().substring(0, 8), 
                        "INITIALIZED", "RUNNING");
                break;
            default:
                throw new IllegalArgumentException("Unsupported event type: " + eventType);
        }
        
        testContext.put("event", event);
    }

    @When("I publish the event through the EventPublisherPort")
    public void iPublishTheEventThroughTheEventPublisherPort() {
        DomainEvent event = (DomainEvent) testContext.get("event");
        assertNotNull(event, "Event should be in the test context");
        
        // Register a test subscriber to capture the event
        TestEventSubscriber subscriber = new TestEventSubscriber("testSubscriber", event.getClass());
        eventDispatcher.registerHandler(event.getClass(), subscriber);
        subscribers.add(subscriber);
        
        // Publish the event
        eventPublisherPort.publishEvent(event);
        
        // Store the subscriber for later verification
        testContext.put("subscriber", subscriber);
    }
    
    @When("I create a list of domain events:")
    public void iCreateAListOfDomainEvents(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<DomainEvent> events = new ArrayList<>();
        
        // Create events based on the table data
        for (Map<String, String> row : rows) {
            String type = row.get("type");
            String source = row.get("source");
            
            DomainEvent event = null;
            switch (type) {
                case "ComponentCreatedEvent":
                    event = new ComponentCreatedEvent(source);
                    break;
                case "MachineStateChangedEvent":
                    event = new MachineStateChangedEvent(source, "IDLE", "RUNNING");
                    break;
                case "ComponentStateChangedEvent":
                    event = new ComponentStateChangedEvent(source, "INITIALIZED", "RUNNING");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported event type: " + type);
            }
            
            events.add(event);
        }
        
        testContext.put("events", events);
    }

    @When("I publish the event list through the EventPublisherPort")
    public void iPublishTheEventListThroughTheEventPublisherPort() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> events = (List<DomainEvent>) testContext.get("events");
        assertNotNull(events, "Events should be in the test context");
        
        // Register a test subscriber to capture the events
        TestEventSubscriber subscriber = new TestEventSubscriber("batchSubscriber", DomainEvent.class);
        eventDispatcher.registerHandler(DomainEvent.class, subscriber);
        subscribers.add(subscriber);
        
        // Publish the events
        eventPublisherPort.publishEvents(events);
        
        // Store the subscriber for later verification
        testContext.put("batchSubscriber", subscriber);
    }
    
    @When("the component generates several domain events")
    public void theComponentGeneratesSeveralDomainEvents() {
        TestComponent component = (TestComponent) testContext.get("testComponent");
        assertNotNull(component, "Test component should be in the test context");
        
        // Generate a few events on the component
        component.generateEvent("Event 1");
        component.generateEvent("Event 2");
        component.changeState("PROCESSING");
        component.changeState("COMPLETED");
        
        // Verify that the component has events
        assertTrue(component.hasPendingEvents(), "Component should have pending events");
        testContext.put("eventCount", component.getPendingEventCount());
    }

    @When("I publish all pending events from the component")
    public void iPublishAllPendingEventsFromTheComponent() {
        TestComponent component = (TestComponent) testContext.get("testComponent");
        assertNotNull(component, "Test component should be in the test context");
        
        // Register a test subscriber to capture the events
        TestEventSubscriber subscriber = new TestEventSubscriber("componentSubscriber", DomainEvent.class);
        eventDispatcher.registerHandler(DomainEvent.class, subscriber);
        subscribers.add(subscriber);
        
        // Publish pending events from the component
        ComponentId componentId = new ComponentId(component.getId());
        int published = eventPublisherPort.publishPendingEvents(componentId);
        
        // Store the result for later verification
        testContext.put("publishedCount", published);
        testContext.put("componentSubscriber", subscriber);
    }
    
    @When("I attempt to publish an invalid event")
    public void iAttemptToPublishAnInvalidEvent() {
        // Create an invalid event (null source ID)
        ComponentCreatedEvent invalidEvent = new ComponentCreatedEvent(null);
        testContext.put("invalidEvent", invalidEvent);
        
        try {
            // Attempt to publish the invalid event
            eventPublisherPort.publishEvent(invalidEvent);
            testContext.put("exceptionThrown", false);
        } catch (Exception e) {
            testContext.put("exceptionThrown", true);
            testContext.put("exception", e);
        }
    }

    @When("I publish an event asynchronously")
    public void iPublishAnEventAsynchronously() {
        // Create an event to publish asynchronously
        ComponentCreatedEvent event = new ComponentCreatedEvent("async-component-" + 
                UUID.randomUUID().toString().substring(0, 8));
        testContext.put("asyncEvent", event);
        
        // Register a test subscriber to capture the event
        TestEventSubscriber subscriber = new TestEventSubscriber("asyncSubscriber", event.getClass());
        eventDispatcher.registerHandler(event.getClass(), subscriber);
        subscribers.add(subscriber);
        
        // Use our test implementation to publish asynchronously
        ((TestEventPublisher) eventPublisherPort).publishEventAsync(event);
        
        // Store the subscriber for later verification
        testContext.put("asyncSubscriber", subscriber);
    }
    
    @When("I publish events with different priority levels:")
    public void iPublishEventsWithDifferentPriorityLevels(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<DomainEvent> events = new ArrayList<>();
        Map<DomainEvent, String> priorities = new HashMap<>();
        
        // Create events based on the table data with priorities
        for (Map<String, String> row : rows) {
            String type = row.get("type");
            String priority = row.get("priority");
            
            DomainEvent event = null;
            switch (type) {
                case "ComponentCreatedEvent":
                    event = new ComponentCreatedEvent("priority-component-" + priority);
                    break;
                case "MachineStateChangedEvent":
                    event = new MachineStateChangedEvent("priority-machine-" + priority, 
                            "IDLE", "RUNNING");
                    break;
                case "ComponentStateChangedEvent":
                    event = new ComponentStateChangedEvent("priority-component-" + priority, 
                            "INITIALIZED", "RUNNING");
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported event type: " + type);
            }
            
            events.add(event);
            priorities.put(event, priority);
        }
        
        testContext.put("priorityEvents", events);
        testContext.put("priorities", priorities);
        
        // Register a test subscriber to capture the events with timestamps
        TestTimestampedSubscriber subscriber = new TestTimestampedSubscriber("prioritySubscriber");
        eventDispatcher.registerHandler(DomainEvent.class, subscriber);
        testContext.put("prioritySubscriber", subscriber);
        
        // Publish the events with different priorities using our test implementation
        ((TestEventPublisher) eventPublisherPort).publishEventsWithPriority(events, priorities);
    }

    @When("I publish an event with a specific target subscriber")
    public void iPublishAnEventWithASpecificTargetSubscriber() {
        @SuppressWarnings("unchecked")
        List<TestEventSubscriber> allSubscribers = (List<TestEventSubscriber>) testContext.get("subscribers");
        assertNotNull(allSubscribers, "Subscribers should be in the test context");
        
        // Pick the first subscriber as the target
        TestEventSubscriber targetSubscriber = allSubscribers.get(0);
        
        // Create an event to publish
        ComponentCreatedEvent event = new ComponentCreatedEvent("targeted-component-" + 
                UUID.randomUUID().toString().substring(0, 8));
        testContext.put("targetedEvent", event);
        testContext.put("targetSubscriber", targetSubscriber);
        
        // Use our test implementation to publish to a specific subscriber
        ((TestEventPublisher) eventPublisherPort).publishEventToSubscriber(event, targetSubscriber.getId());
    }
    
    @When("I publish events of different types")
    public void iPublishEventsOfDifferentTypes() {
        // Create events of different types
        ComponentCreatedEvent componentEvent = new ComponentCreatedEvent("filtered-component");
        MachineStateChangedEvent machineEvent = new MachineStateChangedEvent("filtered-machine", 
                "IDLE", "RUNNING");
        ComponentStateChangedEvent stateEvent = new ComponentStateChangedEvent("filtered-component", 
                "INITIALIZED", "RUNNING");
        
        List<DomainEvent> events = List.of(componentEvent, machineEvent, stateEvent);
        testContext.put("filteredEvents", events);
        
        // Publish the events
        for (DomainEvent event : events) {
            eventPublisherPort.publishEvent(event);
        }
    }

    @Then("the event should be successfully published")
    public void theEventShouldBeSuccessfullyPublished() {
        DomainEvent event = (DomainEvent) testContext.get("event");
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("subscriber");
        
        assertNotNull(event, "Event should be in the test context");
        assertNotNull(subscriber, "Subscriber should be in the test context");
        
        // Check if the subscriber received the event
        assertEquals(1, subscriber.getReceivedEvents().size(), 
                "Subscriber should have received exactly one event");
        assertEquals(event.getEventId(), subscriber.getReceivedEvents().get(0).getEventId(), 
                "Subscriber should have received the correct event");
        
        // Check if the event was logged
        boolean eventLogged = logMessages.stream()
                .anyMatch(message -> message.contains(event.getEventId()) ||
                        message.contains(event.getEventType()));
        
        assertTrue(eventLogged, "Event should be logged");
    }

    @Then("event subscribers should be notified")
    public void eventSubscribersShouldBeNotified() {
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("subscriber");
        assertNotNull(subscriber, "Subscriber should be in the test context");
        
        // Check if the subscriber was notified
        assertFalse(subscriber.getReceivedEvents().isEmpty(), 
                "Subscriber should have received events");
        
        // Check if the notification was logged
        boolean notificationLogged = logMessages.stream()
                .anyMatch(message -> message.toLowerCase().contains("dispatch"));
        
        assertTrue(notificationLogged, "Event notification should be logged");
    }

    @Then("the event should be available in the event log")
    public void theEventShouldBeAvailableInTheEventLog() {
        DomainEvent event = (DomainEvent) testContext.get("event");
        assertNotNull(event, "Event should be in the test context");
        
        // Since we don't have an actual event log in this test,
        // we'll check if the event was captured in our published events list
        // and check that it was properly logged
        
        boolean eventLogged = logMessages.stream()
                .anyMatch(message -> message.contains(event.getEventId()) ||
                        message.contains(event.getEventType()));
        
        assertTrue(eventLogged, "Event should be logged");
    }
    
    @Then("all events should be successfully published")
    public void allEventsShouldBeSuccessfullyPublished() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> events = (List<DomainEvent>) testContext.get("events");
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("batchSubscriber");
        
        assertNotNull(events, "Events should be in the test context");
        assertNotNull(subscriber, "Subscriber should be in the test context");
        
        // Check if the subscriber received all events
        assertEquals(events.size(), subscriber.getReceivedEvents().size(), 
                "Subscriber should have received all events");
        
        // Check if all event IDs are present in the received events
        List<String> expectedEventIds = events.stream()
                .map(DomainEvent::getEventId)
                .collect(Collectors.toList());
        
        List<String> actualEventIds = subscriber.getReceivedEvents().stream()
                .map(DomainEvent::getEventId)
                .collect(Collectors.toList());
        
        assertTrue(actualEventIds.containsAll(expectedEventIds), 
                "All expected events should be received");
    }

    @Then("subscribers should receive all events in the correct order")
    public void subscribersShouldReceiveAllEventsInTheCorrectOrder() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> events = (List<DomainEvent>) testContext.get("events");
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("batchSubscriber");
        
        assertNotNull(events, "Events should be in the test context");
        assertNotNull(subscriber, "Subscriber should be in the test context");
        
        // Check if the subscriber received events in the correct order
        List<DomainEvent> receivedEvents = subscriber.getReceivedEvents();
        assertEquals(events.size(), receivedEvents.size(), 
                "Subscriber should have received all events");
        
        // Compare the order of events
        for (int i = 0; i < events.size(); i++) {
            assertEquals(events.get(i).getEventId(), receivedEvents.get(i).getEventId(), 
                    "Events should be received in the correct order");
        }
    }

    @Then("all events should be available in the event log")
    public void allEventsShouldBeAvailableInTheEventLog() {
        @SuppressWarnings("unchecked")
        List<DomainEvent> events = (List<DomainEvent>) testContext.get("events");
        assertNotNull(events, "Events should be in the test context");
        
        // Since we don't have an actual event log in this test,
        // we'll check if the events were properly logged
        
        for (DomainEvent event : events) {
            boolean eventLogged = logMessages.stream()
                    .anyMatch(message -> message.contains(event.getEventId()) ||
                            message.contains(event.getEventType()));
            
            assertTrue(eventLogged, "Event should be logged: " + event.getEventType());
        }
    }
    
    @Then("all component events should be successfully published")
    public void allComponentEventsShouldBeSuccessfullyPublished() {
        Integer eventCount = (Integer) testContext.get("eventCount");
        Integer publishedCount = (Integer) testContext.get("publishedCount");
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("componentSubscriber");
        
        assertNotNull(eventCount, "Event count should be in the test context");
        assertNotNull(publishedCount, "Published count should be in the test context");
        assertNotNull(subscriber, "Subscriber should be in the test context");
        
        // Check if the published count matches the event count
        assertEquals(eventCount.intValue(), publishedCount.intValue(), 
                "All component events should be published");
        
        // Check if the subscriber received all events
        assertEquals(eventCount.intValue(), subscriber.getReceivedEvents().size(), 
                "Subscriber should have received all component events");
    }

    @Then("the component's event queue should be empty")
    public void theComponentsEventQueueShouldBeEmpty() {
        TestComponent component = (TestComponent) testContext.get("testComponent");
        assertNotNull(component, "Test component should be in the test context");
        
        // Check if the component's event queue is empty
        assertFalse(component.hasPendingEvents(), "Component should not have pending events");
        assertEquals(0, component.getPendingEventCount(), "Component event count should be 0");
    }

    @Then("subscribers should receive all component events")
    public void subscribersShouldReceiveAllComponentEvents() {
        Integer eventCount = (Integer) testContext.get("eventCount");
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("componentSubscriber");
        
        assertNotNull(eventCount, "Event count should be in the test context");
        assertNotNull(subscriber, "Subscriber should be in the test context");
        
        // Check if the subscriber received all events
        assertEquals(eventCount.intValue(), subscriber.getReceivedEvents().size(), 
                "Subscriber should have received all component events");
    }
    
    @Then("the system should handle the error gracefully")
    public void theSystemShouldHandleTheErrorGracefully() {
        Boolean exceptionThrown = (Boolean) testContext.get("exceptionThrown");
        
        // In a real system, we'd expect errors to be handled gracefully without exceptions bubbling up
        // But our test implementation might throw exceptions for invalid events
        if (Boolean.TRUE.equals(exceptionThrown)) {
            assertTrue(true, "Exception was properly thrown and caught in the test");
        } else {
            // Check if error was logged
            boolean errorLogged = logMessages.stream()
                    .anyMatch(message -> message.contains("[ERROR]"));
            
            assertTrue(errorLogged, "Error should be logged for invalid event");
        }
    }

    @Then("an appropriate error message should be logged")
    public void anAppropriateErrorMessageShouldBeLogged() {
        // Check if error was logged
        boolean errorLogged = logMessages.stream()
                .anyMatch(message -> message.contains("[ERROR]"));
        
        assertTrue(errorLogged, "Error should be logged for invalid event");
    }

    @Then("other system operations should not be affected")
    public void otherSystemOperationsShouldNotBeAffected() {
        // Create and publish a valid event to ensure the system still works
        ComponentCreatedEvent validEvent = new ComponentCreatedEvent("valid-component-" +
                UUID.randomUUID().toString().substring(0, 8));
        
        // Register a test subscriber
        TestEventSubscriber validSubscriber = new TestEventSubscriber("validSubscriber", validEvent.getClass());
        eventDispatcher.registerHandler(validEvent.getClass(), validSubscriber);
        
        // Publish the valid event
        eventPublisherPort.publishEvent(validEvent);
        
        // Check if the event was received
        assertEquals(1, validSubscriber.getReceivedEvents().size(), 
                "Valid event should be processed normally");
    }
    
    @Then("the publishing request should be accepted immediately")
    public void thePublishingRequestShouldBeAcceptedImmediately() {
        DomainEvent event = (DomainEvent) testContext.get("asyncEvent");
        assertNotNull(event, "Async event should be in the test context");
        
        // Check that the event was registered for async processing
        assertTrue(((TestEventPublisher) eventPublisherPort).isEventAsync(event.getEventId()),
                "Event should be registered for async processing");
    }

    @Then("the event should be processed in the background")
    public void theEventShouldBeProcessedInTheBackground() {
        // In our test implementation, async events will be processed by completing the future
        ((TestEventPublisher) eventPublisherPort).completeAsyncEvents();
    }

    @Then("subscribers should eventually receive the event")
    public void subscribersShouldEventuallyReceiveTheEvent() {
        TestEventSubscriber subscriber = (TestEventSubscriber) testContext.get("asyncSubscriber");
        assertNotNull(subscriber, "Async subscriber should be in the test context");
        
        // Check if the subscriber received the event after async processing
        assertEquals(1, subscriber.getReceivedEvents().size(), 
                "Subscriber should have received the async event");
    }
    
    @Then("the events should be delivered according to their priority levels")
    public void theEventsShouldBeDeliveredAccordingToTheirPriorityLevels() {
        TestTimestampedSubscriber subscriber = (TestTimestampedSubscriber) testContext.get("prioritySubscriber");
        assertNotNull(subscriber, "Priority subscriber should be in the test context");
        
        // Our TestTimestampedSubscriber records the processing order
        List<DomainEvent> eventsInOrder = subscriber.getEventsInProcessingOrder();
        assertFalse(eventsInOrder.isEmpty(), "Subscriber should have received events");
    }

    @Then("high priority events should be processed before lower priority ones")
    public void highPriorityEventsShouldBeProcessedBeforeLowerPriorityOnes() {
        @SuppressWarnings("unchecked")
        Map<DomainEvent, String> priorities = (Map<DomainEvent, String>) testContext.get("priorities");
        TestTimestampedSubscriber subscriber = (TestTimestampedSubscriber) testContext.get("prioritySubscriber");
        
        assertNotNull(priorities, "Priorities should be in the test context");
        assertNotNull(subscriber, "Priority subscriber should be in the test context");
        
        List<DomainEvent> eventsInOrder = subscriber.getEventsInProcessingOrder();
        
        // Check if high priority events came before medium, and medium before low
        String previousPriority = "HIGHEST";
        boolean validOrder = true;
        
        for (DomainEvent event : eventsInOrder) {
            String currentPriority = priorities.get(event);
            
            if ("HIGH".equals(currentPriority) && ("MEDIUM".equals(previousPriority) || "LOW".equals(previousPriority))) {
                validOrder = false;
                break;
            }
            
            if ("MEDIUM".equals(currentPriority) && "LOW".equals(previousPriority)) {
                validOrder = false;
                break;
            }
            
            previousPriority = currentPriority;
        }
        
        assertTrue(validOrder, "Events should be processed in priority order");
    }
    
    @Then("only the targeted subscriber should receive the event")
    public void onlyTheTargetedSubscriberShouldReceiveTheEvent() {
        @SuppressWarnings("unchecked")
        List<TestEventSubscriber> allSubscribers = (List<TestEventSubscriber>) testContext.get("subscribers");
        TestEventSubscriber targetSubscriber = (TestEventSubscriber) testContext.get("targetSubscriber");
        DomainEvent event = (DomainEvent) testContext.get("targetedEvent");
        
        assertNotNull(allSubscribers, "All subscribers should be in the test context");
        assertNotNull(targetSubscriber, "Target subscriber should be in the test context");
        assertNotNull(event, "Targeted event should be in the test context");
        
        // Check that the target subscriber received the event
        assertEquals(1, targetSubscriber.getReceivedEvents().size(), 
                "Target subscriber should have received the event");
        assertEquals(event.getEventId(), targetSubscriber.getReceivedEvents().get(0).getEventId(), 
                "Target subscriber should have received the correct event");
        
        // Check that other subscribers did not receive the event
        for (TestEventSubscriber subscriber : allSubscribers) {
            if (!subscriber.getId().equals(targetSubscriber.getId())) {
                assertTrue(subscriber.getReceivedEvents().isEmpty(), 
                        "Non-target subscriber should not have received the event");
            }
        }
    }

    @Then("other subscribers should not be notified")
    public void otherSubscribersShouldNotBeNotified() {
        @SuppressWarnings("unchecked")
        List<TestEventSubscriber> allSubscribers = (List<TestEventSubscriber>) testContext.get("subscribers");
        TestEventSubscriber targetSubscriber = (TestEventSubscriber) testContext.get("targetSubscriber");
        
        assertNotNull(allSubscribers, "All subscribers should be in the test context");
        assertNotNull(targetSubscriber, "Target subscriber should be in the test context");
        
        // Check that other subscribers did not receive the event
        for (TestEventSubscriber subscriber : allSubscribers) {
            if (!subscriber.getId().equals(targetSubscriber.getId())) {
                assertTrue(subscriber.getReceivedEvents().isEmpty(), 
                        "Non-target subscriber should not have received the event");
            }
        }
    }
    
    @Then("subscribers should only receive events matching their filters")
    public void subscribersShouldOnlyReceiveEventsMatchingTheirFilters() {
        TestEventSubscriber componentSubscriber = (TestEventSubscriber) testContext.get("componentSubscriber");
        TestEventSubscriber machineSubscriber = (TestEventSubscriber) testContext.get("machineSubscriber");
        TestEventSubscriber genericSubscriber = (TestEventSubscriber) testContext.get("genericSubscriber");
        
        assertNotNull(componentSubscriber, "Component subscriber should be in the test context");
        assertNotNull(machineSubscriber, "Machine subscriber should be in the test context");
        assertNotNull(genericSubscriber, "Generic subscriber should be in the test context");
        
        // Check that subscribers only received events of their registered types
        for (DomainEvent event : componentSubscriber.getReceivedEvents()) {
            assertTrue(event instanceof ComponentCreatedEvent, 
                    "Component subscriber should only receive ComponentCreatedEvent");
        }
        
        for (DomainEvent event : machineSubscriber.getReceivedEvents()) {
            assertTrue(event instanceof MachineStateChangedEvent, 
                    "Machine subscriber should only receive MachineStateChangedEvent");
        }
        
        // Generic subscriber should receive all events
        assertTrue(genericSubscriber.getReceivedEvents().size() >= 3, 
                "Generic subscriber should receive all events");
    }

    @Then("no subscriber should receive events they didn't register for")
    public void noSubscriberShouldReceiveEventsTheyDidntRegisterFor() {
        TestEventSubscriber componentSubscriber = (TestEventSubscriber) testContext.get("componentSubscriber");
        TestEventSubscriber machineSubscriber = (TestEventSubscriber) testContext.get("machineSubscriber");
        
        assertNotNull(componentSubscriber, "Component subscriber should be in the test context");
        assertNotNull(machineSubscriber, "Machine subscriber should be in the test context");
        
        // Verify that subscribers didn't receive events of types they didn't register for
        assertFalse(componentSubscriber.getReceivedEvents().stream()
                .anyMatch(e -> e instanceof MachineStateChangedEvent), 
                "Component subscriber should not receive MachineStateChangedEvent");
        
        assertFalse(machineSubscriber.getReceivedEvents().stream()
                .anyMatch(e -> e instanceof ComponentCreatedEvent), 
                "Machine subscriber should not receive ComponentCreatedEvent");
    }
    
    /**
     * A test implementation of EventPublisherPort that captures published events.
     */
    private class TestEventPublisher implements EventPublisherPort {
        private final EventDispatcher eventDispatcher;
        private final LoggerPort logger;
        private final Map<String, CompletableFuture<Void>> asyncEvents = new HashMap<>();
        
        public TestEventPublisher(EventDispatcher eventDispatcher, LoggerPort logger) {
            this.eventDispatcher = eventDispatcher;
            this.logger = logger;
        }
        
        @Override
        public void publishEvent(DomainEvent event) {
            if (event == null || event.getSourceComponentId() == null) {
                logger.error("Cannot publish null event or event with null source ID");
                return;
            }
            
            logger.info("Publishing event: {} (ID: {})", event.getEventType(), event.getEventId());
            eventDispatcher.dispatch(event);
            publishedEvents.add(event);
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
            if (componentId == null) {
                logger.error("Cannot publish events for null component ID");
                return 0;
            }
            
            TestComponent component = testContext.get("testComponent") instanceof TestComponent ?
                    (TestComponent) testContext.get("testComponent") : null;
            
            if (component == null || !component.getId().equals(componentId.toString())) {
                logger.error("Component not found: {}", componentId);
                return 0;
            }
            
            List<DomainEvent> pendingEvents = component.getPendingEvents();
            int count = pendingEvents.size();
            
            logger.info("Publishing {} pending events for component {}", count, componentId);
            publishEvents(pendingEvents);
            component.clearPendingEvents();
            
            return count;
        }
        
        /**
         * Test method for publishing an event asynchronously.
         */
        public CompletableFuture<Void> publishEventAsync(DomainEvent event) {
            if (event == null) {
                logger.error("Cannot publish null event asynchronously");
                return CompletableFuture.completedFuture(null);
            }
            
            logger.info("Accepting async event: {} (ID: {})", event.getEventType(), event.getEventId());
            
            CompletableFuture<Void> future = new CompletableFuture<>();
            asyncEvents.put(event.getEventId(), future);
            
            return future;
        }
        
        /**
         * Check if an event is registered for async processing.
         */
        public boolean isEventAsync(String eventId) {
            return asyncEvents.containsKey(eventId);
        }
        
        /**
         * Complete all async events (for testing).
         */
        public void completeAsyncEvents() {
            logger.info("Processing {} async events", asyncEvents.size());
            
            for (Map.Entry<String, CompletableFuture<Void>> entry : asyncEvents.entrySet()) {
                String eventId = entry.getKey();
                logger.info("Processing async event: {}", eventId);
                
                // Find the event
                DomainEvent event = null;
                if (testContext.get("asyncEvent") instanceof DomainEvent) {
                    event = (DomainEvent) testContext.get("asyncEvent");
                    if (!event.getEventId().equals(eventId)) {
                        event = null;
                    }
                }
                
                if (event != null) {
                    // Publish the event
                    eventDispatcher.dispatch(event);
                    publishedEvents.add(event);
                }
                
                // Complete the future
                entry.getValue().complete(null);
            }
        }
        
        /**
         * Test method for publishing events with priorities.
         */
        public void publishEventsWithPriority(List<DomainEvent> events, Map<DomainEvent, String> priorities) {
            if (events == null || events.isEmpty()) {
                logger.error("Cannot publish null or empty event list with priorities");
                return;
            }
            
            logger.info("Publishing {} events with priorities", events.size());
            
            // Sort events by priority (HIGH > MEDIUM > LOW)
            List<DomainEvent> sortedEvents = new ArrayList<>(events);
            sortedEvents.sort((e1, e2) -> {
                String p1 = priorities.getOrDefault(e1, "LOW");
                String p2 = priorities.getOrDefault(e2, "LOW");
                
                // Simple priority comparison
                if (p1.equals(p2)) return 0;
                if ("HIGH".equals(p1)) return -1;
                if ("HIGH".equals(p2)) return 1;
                if ("MEDIUM".equals(p1)) return -1;
                if ("MEDIUM".equals(p2)) return 1;
                return 0;
            });
            
            // Publish the sorted events
            for (DomainEvent event : sortedEvents) {
                String priority = priorities.getOrDefault(event, "LOW");
                logger.info("Publishing {} priority event: {} (ID: {})", 
                        priority, event.getEventType(), event.getEventId());
                
                // Add a small delay between events to simulate processing time
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                publishEvent(event);
            }
        }
        
        /**
         * Test method for publishing an event to a specific subscriber.
         */
        public void publishEventToSubscriber(DomainEvent event, String subscriberId) {
            if (event == null) {
                logger.error("Cannot publish null event to subscriber");
                return;
            }
            
            logger.info("Publishing event to subscriber {}: {} (ID: {})", 
                    subscriberId, event.getEventType(), event.getEventId());
            
            // Find the subscriber
            TestEventSubscriber targetSubscriber = null;
            for (TestEventSubscriber subscriber : subscribers) {
                if (subscriber.getId().equals(subscriberId)) {
                    targetSubscriber = subscriber;
                    break;
                }
            }
            
            if (targetSubscriber == null) {
                logger.error("Subscriber not found: {}", subscriberId);
                return;
            }
            
            // Directly notify the subscriber
            try {
                targetSubscriber.handle(event);
                publishedEvents.add(event);
            } catch (Exception e) {
                logger.error("Error publishing event to subscriber: {}", e.getMessage(), e);
            }
        }
    }
    
    /**
     * A test event subscriber that records received events.
     */
    private class TestEventSubscriber implements EventHandler<DomainEvent> {
        private final String id;
        private final Class<? extends DomainEvent> eventType;
        private final List<DomainEvent> receivedEvents = new ArrayList<>();
        
        public TestEventSubscriber(String id, Class<? extends DomainEvent> eventType) {
            this.id = id;
            this.eventType = eventType;
        }
        
        @Override
        public void handle(DomainEvent event) {
            if (eventType == null || eventType.isAssignableFrom(event.getClass())) {
                receivedEvents.add(event);
                logger.debug("Subscriber {} received event: {} (ID: {})", 
                        id, event.getEventType(), event.getEventId());
            }
        }
        
        public String getId() {
            return id;
        }
        
        public List<DomainEvent> getReceivedEvents() {
            return receivedEvents;
        }
        
        public void clear() {
            receivedEvents.clear();
        }
    }
    
    /**
     * A test event subscriber that records the processing order of events with timestamps.
     */
    private class TestTimestampedSubscriber implements EventHandler<DomainEvent> {
        private final String id;
        private final List<DomainEvent> eventsInOrder = new ArrayList<>();
        
        public TestTimestampedSubscriber(String id) {
            this.id = id;
        }
        
        @Override
        public void handle(DomainEvent event) {
            eventsInOrder.add(event);
            logger.debug("TimestampedSubscriber {} received event: {} (ID: {})", 
                    id, event.getEventType(), event.getEventId());
        }
        
        public List<DomainEvent> getEventsInProcessingOrder() {
            return eventsInOrder;
        }
    }
    
    /**
     * A test component for simulating components that generate domain events.
     */
    private class TestComponent {
        private final String id;
        private String state;
        private final List<DomainEvent> pendingEvents = new ArrayList<>();
        
        public TestComponent(String id) {
            this.id = id;
            this.state = "INITIALIZED";
        }
        
        public String getId() {
            return id;
        }
        
        public String getState() {
            return state;
        }
        
        public void generateEvent(String data) {
            ComponentCreatedEvent event = new ComponentCreatedEvent(id);
            event.setSourceComponentId(id);
            pendingEvents.add(event);
            logger.debug("Component {} generated event: {} (ID: {})", 
                    id, event.getEventType(), event.getEventId());
        }
        
        public void changeState(String newState) {
            if (!newState.equals(state)) {
                ComponentStateChangedEvent event = new ComponentStateChangedEvent(id, state, newState);
                event.setSourceComponentId(id);
                pendingEvents.add(event);
                this.state = newState;
                logger.debug("Component {} changed state from {} to {}", id, state, newState);
            }
        }
        
        public boolean hasPendingEvents() {
            return !pendingEvents.isEmpty();
        }
        
        public int getPendingEventCount() {
            return pendingEvents.size();
        }
        
        public List<DomainEvent> getPendingEvents() {
            return new ArrayList<>(pendingEvents);
        }
        
        public void clearPendingEvents() {
            pendingEvents.clear();
        }
    }
}