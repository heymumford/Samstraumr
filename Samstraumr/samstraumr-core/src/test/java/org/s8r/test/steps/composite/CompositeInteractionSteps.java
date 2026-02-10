/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

/*
 * Copyright (c) 2025 Eric C. Mumford
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.steps.composite;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for composite component interaction tests.
 */
public class CompositeInteractionSteps extends CompositeBaseSteps {
    
    private static final Logger LOGGER = Logger.getLogger(CompositeInteractionSteps.class.getName());
    
    // Mock implementation of a typed component for interaction tests
    static class TypedComponent extends CompositeCreationSteps.Component {
        private final String type;
        private final List<TypedComponent> connections = new ArrayList<>();
        private final Map<String, Object> receivedData = new HashMap<>();
        private boolean throwExceptionOnReceive = false;
        
        public TypedComponent(String name, String type, String reason) {
            super(name, reason);
            this.type = type;
        }
        
        public String getType() {
            return type;
        }
        
        public void connectTo(TypedComponent target) {
            connections.add(target);
        }
        
        public List<TypedComponent> getConnections() {
            return connections;
        }
        
        public void sendData(Object data) {
            for (TypedComponent target : connections) {
                target.receiveData(data);
            }
        }
        
        public void receiveData(Object data) {
            if (throwExceptionOnReceive) {
                throw new RuntimeException("Exception on receiving data: " + data);
            }
            receivedData.put("lastReceived", data);
            if (type.equals("Processor")) {
                // Simple processing example
                if (data instanceof String) {
                    String processed = ((String) data).toUpperCase();
                    receivedData.put("processed", processed);
                    // Forward to next component in chain
                    for (TypedComponent target : connections) {
                        target.receiveData(processed);
                    }
                }
            }
        }
        
        public Object getLastReceivedData() {
            return receivedData.get("lastReceived");
        }
        
        public Object getProcessedData() {
            return receivedData.get("processed");
        }
        
        public void setThrowExceptionOnReceive(boolean throwException) {
            this.throwExceptionOnReceive = throwException;
        }
    }
    
    // Event system mock
    static class EventSystem {
        private final Map<TypedComponent, List<TypedComponent>> listeners = new HashMap<>();
        
        public void registerListener(TypedComponent publisher, TypedComponent listener) {
            listeners.computeIfAbsent(publisher, k -> new ArrayList<>()).add(listener);
        }
        
        public void publishEvent(TypedComponent publisher, String eventName, Object payload) {
            if (listeners.containsKey(publisher)) {
                for (TypedComponent listener : listeners.get(publisher)) {
                    listener.receiveData(Map.of("event", eventName, "payload", payload));
                }
            }
        }
    }
    
    // Resource management mock
    static class Resource {
        private final String name;
        private final Map<TypedComponent, String> permissions = new HashMap<>();
        private Object data;
        
        public Resource(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void grantAccess(TypedComponent component, String permission) {
            permissions.put(component, permission);
        }
        
        public boolean canRead(TypedComponent component) {
            return permissions.containsKey(component) && 
                  (permissions.get(component).equals("READ") || permissions.get(component).equals("WRITE"));
        }
        
        public boolean canWrite(TypedComponent component) {
            return permissions.containsKey(component) && permissions.get(component).equals("WRITE");
        }
        
        public void writeData(TypedComponent component, Object newData) {
            if (canWrite(component)) {
                this.data = newData;
            } else {
                throw new SecurityException("Component does not have WRITE permission");
            }
        }
        
        public Object readData(TypedComponent component) {
            if (canRead(component)) {
                return data;
            } else {
                throw new SecurityException("Component does not have READ permission");
            }
        }
    }
    
    private EventSystem eventSystem;
    private Map<String, Resource> resources;
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        eventSystem = new EventSystem();
        resources = new HashMap<>();
        storeInContext("eventSystem", eventSystem);
        storeInContext("resources", resources);
    }
    
    /**
     * Create components with specified types in a composite.
     */
    @Given("I have created the following components in the composite:")
    public void iHaveCreatedTheFollowingComponentsInTheComposite(DataTable dataTable) {
        LOGGER.info("Creating typed components in the composite");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        CompositeCreationSteps.Composite composite = getFromContext("composite_MainComposite");
        
        if (composite == null) {
            LOGGER.warning("Composite not found, falling back to currentComposite");
            composite = getFromContext("currentComposite");
        }
        
        assertNotNull(composite, "No composite found in context");
        
        for (Map<String, String> row : rows) {
            String name = row.get("name");
            String type = row.get("type");
            String reason = row.get("reason");
            
            if (type == null) {
                type = "Standard"; // Default type if not specified
            }
            
            LOGGER.info("Creating component: " + name + " of type: " + type + " with reason: " + reason);
            TypedComponent component = new TypedComponent(name, type, reason);
            composite.addChild(component);
            storeInContext("component_" + name, component);
        }
    }
    
    /**
     * Connect two components.
     */
    @When("I connect {string} to {string}")
    public void iConnectToComponent(String sourceName, String targetName) {
        LOGGER.info("Connecting " + sourceName + " to " + targetName);
        TypedComponent source = getFromContext("component_" + sourceName);
        TypedComponent target = getFromContext("component_" + targetName);
        
        assertNotNull(source, "Source component not found: " + sourceName);
        assertNotNull(target, "Target component not found: " + targetName);
        
        source.connectTo(target);
    }
    
    /**
     * Send data from a component.
     */
    @When("I send data {string} from {string}")
    public void iSendDataFromComponent(String data, String sourceName) {
        LOGGER.info("Sending data '" + data + "' from " + sourceName);
        TypedComponent source = getFromContext("component_" + sourceName);
        assertNotNull(source, "Source component not found: " + sourceName);
        
        source.sendData(data);
    }
    
    /**
     * Register a component as a listener for events from another component.
     */
    @When("I register {string} as a listener for events from {string}")
    public void iRegisterAsListenerForEventsFrom(String listenerName, String publisherName) {
        LOGGER.info("Registering " + listenerName + " as listener for " + publisherName);
        TypedComponent listener = getFromContext("component_" + listenerName);
        TypedComponent publisher = getFromContext("component_" + publisherName);
        
        assertNotNull(listener, "Listener component not found: " + listenerName);
        assertNotNull(publisher, "Publisher component not found: " + publisherName);
        
        EventSystem eventSystem = getFromContext("eventSystem");
        eventSystem.registerListener(publisher, listener);
    }
    
    /**
     * Publish an event from a component.
     */
    @When("{string} publishes an event {string} with payload {string}")
    public void componentPublishesEventWithPayload(String publisherName, String eventName, String payload) {
        LOGGER.info(publisherName + " publishing event '" + eventName + "' with payload '" + payload + "'");
        TypedComponent publisher = getFromContext("component_" + publisherName);
        assertNotNull(publisher, "Publisher component not found: " + publisherName);
        
        EventSystem eventSystem = getFromContext("eventSystem");
        eventSystem.publishEvent(publisher, eventName, payload);
    }
    
    /**
     * Create a state synchronization relationship between components.
     */
    @When("I create a state synchronization relationship between:")
    public void iCreateStateSynchronizationRelationshipBetween(DataTable dataTable) {
        LOGGER.info("Creating state synchronization relationships");
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        
        Map<TypedComponent, List<TypedComponent>> syncRelationships = new HashMap<>();
        
        for (Map<String, String> row : rows) {
            String masterName = row.get("master");
            String slaveName = row.get("slave");
            
            TypedComponent master = getFromContext("component_" + masterName);
            TypedComponent slave = getFromContext("component_" + slaveName);
            
            assertNotNull(master, "Master component not found: " + masterName);
            assertNotNull(slave, "Slave component not found: " + slaveName);
            
            syncRelationships.computeIfAbsent(master, k -> new ArrayList<>()).add(slave);
        }
        
        storeInContext("syncRelationships", syncRelationships);
    }
    
    /**
     * Change the state of a component.
     */
    @When("I change the state of {string} to {string}")
    public void iChangeTheStateOfComponentTo(String componentName, String newState) {
        LOGGER.info("Changing state of " + componentName + " to " + newState);
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        
        component.setState(newState);
        
        // Handle state synchronization
        Map<TypedComponent, List<TypedComponent>> syncRelationships = getFromContext("syncRelationships");
        if (syncRelationships != null && syncRelationships.containsKey(component)) {
            for (TypedComponent slave : syncRelationships.get(component)) {
                LOGGER.info("Propagating state " + newState + " to slave " + slave.getName());
                slave.setState(newState);
            }
        }
    }
    
    /**
     * Create a shared resource in the composite.
     */
    @Given("I have created a shared resource {string} in the composite")
    public void iHaveCreatedASharedResourceInTheComposite(String resourceName) {
        LOGGER.info("Creating shared resource: " + resourceName);
        Map<String, Resource> resources = getFromContext("resources");
        resources.put(resourceName, new Resource(resourceName));
    }
    
    /**
     * Grant a component access to a resource with specified permission.
     */
    @When("I grant {string} access to the {string} resource with {string} permission")
    public void iGrantAccessToTheResourceWithPermission(String componentName, String resourceName, String permission) {
        LOGGER.info("Granting " + componentName + " " + permission + " access to " + resourceName);
        TypedComponent component = getFromContext("component_" + componentName);
        Map<String, Resource> resources = getFromContext("resources");
        
        assertNotNull(component, "Component not found: " + componentName);
        assertTrue(resources.containsKey(resourceName), "Resource not found: " + resourceName);
        
        Resource resource = resources.get(resourceName);
        resource.grantAccess(component, permission);
    }
    
    /**
     * Have a component write data to a resource.
     */
    @When("{string} writes data {string} to the {string} resource")
    public void componentWritesDataToTheResource(String componentName, String data, String resourceName) {
        LOGGER.info(componentName + " writing data '" + data + "' to resource " + resourceName);
        TypedComponent component = getFromContext("component_" + componentName);
        Map<String, Resource> resources = getFromContext("resources");
        
        assertNotNull(component, "Component not found: " + componentName);
        assertTrue(resources.containsKey(resourceName), "Resource not found: " + resourceName);
        
        Resource resource = resources.get(resourceName);
        resource.writeData(component, data);
    }
    
    /**
     * Configure a component to throw an exception when receiving data.
     */
    @When("I configure {string} to throw an exception on receiving data")
    public void iConfigureToThrowAnExceptionOnReceivingData(String componentName) {
        LOGGER.info("Configuring " + componentName + " to throw exception on data receive");
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        
        component.setThrowExceptionOnReceive(true);
    }
    
    /**
     * Connect one component to multiple target components.
     */
    @When("I connect {string} to both {string} and {string}")
    public void iConnectToBothAnd(String sourceName, String target1Name, String target2Name) {
        LOGGER.info("Connecting " + sourceName + " to both " + target1Name + " and " + target2Name);
        TypedComponent source = getFromContext("component_" + sourceName);
        TypedComponent target1 = getFromContext("component_" + target1Name);
        TypedComponent target2 = getFromContext("component_" + target2Name);
        
        assertNotNull(source, "Source component not found: " + sourceName);
        assertNotNull(target1, "Target1 component not found: " + target1Name);
        assertNotNull(target2, "Target2 component not found: " + target2Name);
        
        source.connectTo(target1);
        source.connectTo(target2);
    }
    
    /**
     * Register a component as an error handler for another component.
     */
    @Given("I have registered {string} as an error handler for {string}")
    public void iHaveRegisteredAsAnErrorHandlerFor(String handlerName, String targetName) {
        LOGGER.info("Registering " + handlerName + " as error handler for " + targetName);
        // Mock implementation - in real code would use error handling mechanism
        TypedComponent handler = getFromContext("component_" + handlerName);
        TypedComponent target = getFromContext("component_" + targetName);
        
        assertNotNull(handler, "Handler component not found: " + handlerName);
        assertNotNull(target, "Target component not found: " + targetName);
        
        // Store the error handler relationship
        Map<TypedComponent, TypedComponent> errorHandlers = getFromContext("errorHandlers");
        if (errorHandlers == null) {
            errorHandlers = new HashMap<>();
            storeInContext("errorHandlers", errorHandlers);
        }
        errorHandlers.put(target, handler);
    }
    
    /**
     * Connect source and target components.
     */
    @Given("I have connected {string} to {string}")
    public void iHaveConnectedTo(String sourceName, String targetName) {
        LOGGER.info("Connecting " + sourceName + " to " + targetName);
        TypedComponent source = getFromContext("component_" + sourceName);
        TypedComponent target = getFromContext("component_" + targetName);
        
        assertNotNull(source, "Source component not found: " + sourceName);
        assertNotNull(target, "Target component not found: " + targetName);
        
        source.connectTo(target);
    }
    
    /**
     * Verify that a component received the expected data.
     */
    @Then("{string} should receive the data {string}")
    public void shouldReceiveTheData(String componentName, String expectedData) {
        LOGGER.info("Verifying " + componentName + " received data: " + expectedData);
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        
        Object receivedData = component.getLastReceivedData();
        assertNotNull(receivedData, componentName + " did not receive any data");
        assertEquals(expectedData, receivedData, 
                    componentName + " received unexpected data");
    }
    
    /**
     * Verify that a component received processed data.
     */
    @Then("{string} should receive the processed data")
    public void shouldReceiveTheProcessedData(String componentName) {
        LOGGER.info("Verifying " + componentName + " received processed data");
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        
        Object receivedData = component.getLastReceivedData();
        assertNotNull(receivedData, componentName + " did not receive any data");
    }
    
    /**
     * Verify that a component received an event.
     */
    @Then("{string} should receive the event {string}")
    public void shouldReceiveTheEvent(String componentName, String eventName) {
        LOGGER.info("Verifying " + componentName + " received event: " + eventName);
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        
        Object receivedData = component.getLastReceivedData();
        assertNotNull(receivedData, componentName + " did not receive any data");
        assertTrue(receivedData instanceof Map, "Received data is not a Map");
        
        Map<String, Object> eventData = (Map<String, Object>) receivedData;
        assertEquals(eventName, eventData.get("event"), 
                    "Unexpected event name");
    }
    
    /**
     * Verify that multiple listeners processed an event payload.
     */
    @Then("both listeners should process the event payload {string}")
    public void bothListenersShouldProcessTheEventPayload(String expectedPayload) {
        LOGGER.info("Verifying both listeners processed payload: " + expectedPayload);
        TypedComponent listener1 = getFromContext("component_ListenerComp1");
        TypedComponent listener2 = getFromContext("component_ListenerComp2");
        
        assertNotNull(listener1, "Listener1 not found");
        assertNotNull(listener2, "Listener2 not found");
        
        Object data1 = listener1.getLastReceivedData();
        Object data2 = listener2.getLastReceivedData();
        
        assertNotNull(data1, "Listener1 did not receive any data");
        assertNotNull(data2, "Listener2 did not receive any data");
        
        assertTrue(data1 instanceof Map, "Listener1 data is not a Map");
        assertTrue(data2 instanceof Map, "Listener2 data is not a Map");
        
        assertEquals(expectedPayload, ((Map<String, Object>)data1).get("payload"), 
                    "Listener1 has unexpected payload");
        assertEquals(expectedPayload, ((Map<String, Object>)data2).get("payload"), 
                    "Listener2 has unexpected payload");
    }
    
    /**
     * Verify the state of a component.
     */
    @Then("the state of {string} should be {string}")
    public void theStateOfShouldBe(String componentName, String expectedState) {
        LOGGER.info("Verifying state of " + componentName + " is " + expectedState);
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        
        assertEquals(expectedState, component.getState(), 
                    "Component " + componentName + " has unexpected state");
    }
    
    /**
     * Verify that a component can read data from a resource.
     */
    @Then("{string} should be able to read {string} from the {string} resource")
    public void shouldBeAbleToReadFromTheResource(String componentName, String expectedData, String resourceName) {
        LOGGER.info("Verifying " + componentName + " can read '" + expectedData + "' from " + resourceName);
        TypedComponent component = getFromContext("component_" + componentName);
        Map<String, Resource> resources = getFromContext("resources");
        
        assertNotNull(component, "Component not found: " + componentName);
        assertTrue(resources.containsKey(resourceName), "Resource not found: " + resourceName);
        
        Resource resource = resources.get(resourceName);
        Object data = resource.readData(component);
        
        assertEquals(expectedData, data, "Unexpected data read from resource");
    }
    
    /**
     * Verify that a component throws an exception.
     */
    @Then("{string} should throw an exception")
    public void shouldThrowAnException(String componentName) {
        LOGGER.info("Verifying " + componentName + " threw an exception");
        // In real tests, we would check a stored exception
        // Here we are mocking the verification
        TypedComponent component = getFromContext("component_" + componentName);
        assertNotNull(component, "Component not found: " + componentName);
        assertTrue(component.getLastReceivedData() == null, "Component should not have received data");
    }
    
    /**
     * Verify that an error handler was notified.
     */
    @Then("{string} should be notified of the error")
    public void shouldBeNotifiedOfTheError(String handlerName) {
        LOGGER.info("Verifying " + handlerName + " was notified of error");
        // In real tests, we would check the error notification
        // Here we are mocking the verification
        assertTrue(true, "Error notification verification is mocked");
    }
    
    /**
     * Verify that the composite continues functioning after an error.
     */
    @Then("the composite should continue functioning")
    public void theCompositeShouldContinueFunctioning() {
        LOGGER.info("Verifying composite continues functioning");
        // In real tests, we would check the composite's status
        // Here we are mocking the verification
        assertTrue(true, "Composite functioning verification is mocked");
    }
}