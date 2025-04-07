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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.component.port.ComponentPort;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.infrastructure.persistence.InMemoryComponentRepository;
import org.s8r.test.annotation.IntegrationTest;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for testing the ComponentRepository interface.
 */
@IntegrationTest
public class ComponentRepositorySteps {

    private ComponentRepository componentRepository;
    private LoggerPort logger;
    private Map<String, Object> testContext;
    private List<String> logMessages;

    @Before
    public void setup() {
        testContext = new HashMap<>();
        logMessages = new ArrayList<>();
        
        // Create a test logger that captures log messages
        logger = new ConsoleLogger("ComponentRepositoryTest") {
            @Override
            public void info(String message) {
                super.info(message);
                logMessages.add(message);
            }
            
            @Override
            public void info(String message, Object... args) {
                super.info(message);
                String formattedMessage = String.format(message.replace("{}", "%s"), args);
                logMessages.add(formattedMessage);
            }
        };
        
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
        // Set up a fresh environment
        testContext.clear();
        logMessages.clear();
    }

    @Given("the ComponentRepository interface is properly initialized")
    public void theComponentRepositoryInterfaceIsProperlyInitialized() {
        // Initialize the component repository
        componentRepository = new InMemoryComponentRepository(logger);
        assertNotNull(componentRepository, "ComponentRepository should be initialized");
        testContext.put("componentRepository", componentRepository);
    }

    @When("I create a new component with ID {string} and name {string}")
    public void iCreateANewComponentWithIdAndName(String id, String name) {
        // Create a mock component for testing
        ComponentPort component = createMockComponent(id, name, Collections.emptyList());
        testContext.put("component", component);
    }

    @When("I save the component to the repository")
    public void iSaveTheComponentToTheRepository() {
        ComponentPort component = (ComponentPort) testContext.get("component");
        assertNotNull(component, "Component should be in the test context");
        
        componentRepository.save(component);
        testContext.put("savedId", component.getId().getIdString());
    }

    @Then("the component should be successfully saved")
    public void theComponentShouldBeSuccessfullySaved() {
        String savedId = (String) testContext.get("savedId");
        assertNotNull(savedId, "Saved ID should be in the test context");
        
        ComponentId componentId = ComponentId.fromString(savedId, "test");
        Optional<ComponentPort> retrieved = componentRepository.findById(componentId);
        
        assertTrue(retrieved.isPresent(), "Component should be successfully saved and retrievable");
    }

    @Then("I should be able to retrieve the component by its ID")
    public void iShouldBeAbleToRetrieveTheComponentByItsId() {
        String savedId = (String) testContext.get("savedId");
        assertNotNull(savedId, "Saved ID should be in the test context");
        
        ComponentId componentId = ComponentId.fromString(savedId, "test");
        Optional<ComponentPort> retrieved = componentRepository.findById(componentId);
        
        assertTrue(retrieved.isPresent(), "Component should be retrievable by ID");
        testContext.put("retrievedComponent", retrieved.get());
    }

    @Then("the retrieved component should have the correct ID and name")
    public void theRetrievedComponentShouldHaveTheCorrectIdAndName() {
        ComponentPort originalComponent = (ComponentPort) testContext.get("component");
        ComponentPort retrievedComponent = (ComponentPort) testContext.get("retrievedComponent");
        
        assertNotNull(originalComponent, "Original component should be in the test context");
        assertNotNull(retrievedComponent, "Retrieved component should be in the test context");
        
        assertEquals(originalComponent.getId().getIdString(), retrievedComponent.getId().getIdString(), 
                "Retrieved component should have the correct ID");
        assertEquals(originalComponent.getName(), retrievedComponent.getName(), 
                "Retrieved component should have the correct name");
    }

    @When("I try to find a component with ID {string}")
    public void iTryToFindAComponentWithId(String id) {
        ComponentId componentId = ComponentId.fromString(id, "test");
        Optional<ComponentPort> retrieved = componentRepository.findById(componentId);
        testContext.put("retrievedComponent", retrieved);
    }

    @Then("the result should be empty")
    public void theResultShouldBeEmpty() {
        Optional<ComponentPort> retrieved = (Optional<ComponentPort>) testContext.get("retrievedComponent");
        assertFalse(retrieved.isPresent(), "Result should be empty for non-existent component");
    }

    @Given("I have saved the following components:")
    public void iHaveSavedTheFollowingComponents(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        List<ComponentPort> components = new ArrayList<>();
        
        // Create and save components from the DataTable
        for (Map<String, String> row : rows) {
            String id = row.get("id");
            String name = row.get("name");
            
            ComponentPort component = createMockComponent(id, name, Collections.emptyList());
            componentRepository.save(component);
            components.add(component);
        }
        
        testContext.put("savedComponents", components);
    }

    @When("I request all components")
    public void iRequestAllComponents() {
        List<ComponentPort> allComponents = componentRepository.findAll();
        testContext.put("allComponents", allComponents);
    }

    @Then("I should get {int} components")
    public void iShouldGetComponents(Integer count) {
        List<ComponentPort> components = (List<ComponentPort>) testContext.get("allComponents");
        assertNotNull(components, "Components list should be in the test context");
        assertEquals(count, components.size(), "Should get the expected number of components");
    }

    @Then("the components should include all saved components")
    public void theComponentsShouldIncludeAllSavedComponents() {
        List<ComponentPort> savedComponents = (List<ComponentPort>) testContext.get("savedComponents");
        List<ComponentPort> allComponents = (List<ComponentPort>) testContext.get("allComponents");
        
        assertNotNull(savedComponents, "Saved components should be in the test context");
        assertNotNull(allComponents, "All components should be in the test context");
        
        // Convert both lists to sets of IDs for easier comparison
        List<String> savedIds = savedComponents.stream()
                .map(c -> c.getId().getIdString())
                .toList();
        
        List<String> retrievedIds = allComponents.stream()
                .map(c -> c.getId().getIdString())
                .toList();
        
        // Check that all saved IDs are in the retrieved IDs
        for (String savedId : savedIds) {
            assertTrue(retrievedIds.contains(savedId), 
                    "All components should include saved component with ID: " + savedId);
        }
    }

    @Given("I have saved the following component hierarchy:")
    public void iHaveSavedTheFollowingComponentHierarchy(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps();
        Map<String, ComponentPort> components = new HashMap<>();
        
        // First pass: create all components
        for (Map<String, String> row : rows) {
            String id = row.get("id");
            String name = row.get("name");
            String parentId = row.get("parent_id");
            
            List<String> lineage = new ArrayList<>();
            if (parentId != null && !parentId.isEmpty()) {
                lineage.add(parentId);
            }
            
            ComponentPort component = createMockComponent(id, name, lineage);
            components.put(id, component);
        }
        
        // Second pass: save all components to the repository
        for (ComponentPort component : components.values()) {
            componentRepository.save(component);
        }
        
        testContext.put("componentHierarchy", components);
    }

    @When("I search for children of component {string}")
    public void iSearchForChildrenOfComponent(String parentId) {
        ComponentId componentId = ComponentId.fromString(parentId, "test");
        List<ComponentPort> children = componentRepository.findChildren(componentId);
        testContext.put("childComponents", children);
    }

    @Then("the components should include {string} and {string}")
    public void theComponentsShouldIncludeAnd(String id1, String id2) {
        List<ComponentPort> children = (List<ComponentPort>) testContext.get("childComponents");
        assertNotNull(children, "Child components should be in the test context");
        
        List<String> childIds = children.stream()
                .map(c -> c.getId().getIdString())
                .toList();
        
        assertTrue(childIds.contains(id1), "Children should include component with ID: " + id1);
        assertTrue(childIds.contains(id2), "Children should include component with ID: " + id2);
    }

    @Then("the components should include {string}")
    public void theComponentsShouldInclude(String id) {
        List<ComponentPort> children = (List<ComponentPort>) testContext.get("childComponents");
        assertNotNull(children, "Child components should be in the test context");
        
        List<String> childIds = children.stream()
                .map(c -> c.getId().getIdString())
                .toList();
        
        assertTrue(childIds.contains(id), "Children should include component with ID: " + id);
    }

    @Given("I have saved a component with ID {string} and name {string}")
    public void iHaveSavedAComponentWithIdAndName(String id, String name) {
        ComponentPort component = createMockComponent(id, name, Collections.emptyList());
        componentRepository.save(component);
        testContext.put("savedComponent", component);
    }

    @When("I delete the component with ID {string}")
    public void iDeleteTheComponentWithId(String id) {
        ComponentId componentId = ComponentId.fromString(id, "test");
        componentRepository.delete(componentId);
        testContext.put("deletedId", id);
    }

    @Then("the component should no longer exist in the repository")
    public void theComponentShouldNoLongerExistInTheRepository() {
        String deletedId = (String) testContext.get("deletedId");
        assertNotNull(deletedId, "Deleted ID should be in the test context");
        
        ComponentId componentId = ComponentId.fromString(deletedId, "test");
        Optional<ComponentPort> retrieved = componentRepository.findById(componentId);
        
        assertFalse(retrieved.isPresent(), "Component should no longer exist after deletion");
    }

    @Then("searching for the deleted component should return empty")
    public void searchingForTheDeletedComponentShouldReturnEmpty() {
        String deletedId = (String) testContext.get("deletedId");
        assertNotNull(deletedId, "Deleted ID should be in the test context");
        
        ComponentId componentId = ComponentId.fromString(deletedId, "test");
        Optional<ComponentPort> retrieved = componentRepository.findById(componentId);
        
        assertFalse(retrieved.isPresent(), "Searching for deleted component should return empty");
    }

    /**
     * Creates a mock ComponentPort implementation for testing.
     *
     * @param id The component ID string
     * @param name The component name
     * @param lineage The component's lineage
     * @return A mock ComponentPort
     */
    private ComponentPort createMockComponent(String id, String name, List<String> lineage) {
        return new TestComponentPort(id, name, lineage);
    }

    /**
     * Test implementation of ComponentPort for use in tests
     */
    private static class TestComponentPort implements ComponentPort {
        private final ComponentId id;
        private final String name;
        private final List<String> lineage;
        private final List<String> activityLog;
        private LifecycleState lifecycleState;

        public TestComponentPort(String id, String name, List<String> lineage) {
            this.id = ComponentId.fromValues(id, "test", new ArrayList<>(lineage));
            this.name = name;
            this.lineage = new ArrayList<>(lineage);
            this.activityLog = new ArrayList<>();
            this.lifecycleState = LifecycleState.INITIALIZED;
        }

        @Override
        public ComponentId getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public LifecycleState getLifecycleState() {
            return lifecycleState;
        }

        @Override
        public List<String> getLineage() {
            return Collections.unmodifiableList(lineage);
        }

        @Override
        public List<String> getActivityLog() {
            return Collections.unmodifiableList(activityLog);
        }

        @Override
        public java.time.Instant getCreationTime() {
            return id.getCreationTime();
        }

        @Override
        public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
            return Collections.emptyList();
        }

        @Override
        public void addToLineage(String entry) {
            lineage.add(entry);
        }

        @Override
        public void clearEvents() {
            // No events to clear in this test implementation
        }

        @Override
        public void publishData(String channel, Map<String, Object> data) {
            activityLog.add("Published data to channel: " + channel);
        }

        @Override
        public void publishData(String channel, String key, Object value) {
            activityLog.add("Published data to channel: " + channel + ", key: " + key);
        }

        @Override
        public void transitionTo(LifecycleState newState) {
            this.lifecycleState = newState;
            activityLog.add("Transitioned to state: " + newState);
        }

        @Override
        public void activate() {
            transitionTo(LifecycleState.ACTIVE);
        }

        @Override
        public void deactivate() {
            transitionTo(LifecycleState.WAITING);
        }

        @Override
        public void terminate() {
            transitionTo(LifecycleState.TERMINATED);
        }
    }
}