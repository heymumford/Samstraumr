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

package org.s8r.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.infrastructure.logging.ConsoleLogger;
import org.s8r.test.annotation.UnitTest;
import org.s8r.tube.Environment;
import org.s8r.tube.Tube;
import org.s8r.tube.TubeIdentity;
import org.s8r.tube.TubeStatus;

/**
 * Tests for the S8r migration utilities.
 * <p>
 * These tests verify that the migration utilities correctly bridge between the legacy
 * Tube-based system and the new Component-based architecture.
 */
@UnitTest
public class S8rMigrationTest {

    @Test
    public void testTubeToComponentConversion() {
        // Create test objects
        Environment env = new Environment();
        env.setParameter("name", "TestTube");
        env.setParameter("type", "UnitTest");
        
        Tube tube = Tube.create("Testing migration", env);
        
        // Create factory and convert
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        Component component = factory.tubeToComponent(tube);
        
        // Verify basic conversion
        assertNotNull(component);
        assertEquals(tube.getUniqueId(), component.getId().getIdString());
        assertEquals(LifecycleState.READY, component.getLifecycleState());
        
        // Test state sync
        tube.setStatus(TubeStatus.ACTIVE);
        assertEquals(LifecycleState.ACTIVE, component.getLifecycleState());
        
        // Test getting back the original
        Tube extractedTube = factory.extractTube(component);
        assertEquals(tube, extractedTube);
    }
    
    @Test
    public void testIdentityConversion() {
        // Create test objects
        Environment env = new Environment();
        TubeIdentity tubeIdentity = TubeIdentity.createAdamIdentity("Test identity", env);
        
        // Create factory and convert
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        ComponentId componentId = factory.tubeIdentityToComponentId(tubeIdentity);
        
        // Verify conversion
        assertNotNull(componentId);
        assertEquals(tubeIdentity.getUniqueId(), componentId.getIdString());
        assertEquals(tubeIdentity.getReason(), componentId.getReason());
        assertEquals(tubeIdentity.getLineage().size(), componentId.getLineage().size());
    }
    
    @Test
    public void testEnvironmentConversion() {
        // Create test objects
        Environment tubeEnv = new Environment();
        tubeEnv.setParameter("key1", "value1");
        tubeEnv.setParameter("key2", "value2");
        
        // Create factory and convert
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        org.s8r.component.core.Environment s8rEnv = factory.tubeEnvironmentToS8rEnvironment(tubeEnv);
        
        // Verify conversion
        assertNotNull(s8rEnv);
        assertEquals("value1", s8rEnv.getParameter("key1"));
        assertEquals("value2", s8rEnv.getParameter("key2"));
        
        // Convert back
        Environment roundTripEnv = factory.s8rEnvironmentToTubeEnvironment(s8rEnv);
        assertEquals("value1", roundTripEnv.getParameter("key1"));
        assertEquals("value2", roundTripEnv.getParameter("key2"));
    }
    
    @Test
    public void testCreateTubeComponent() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create new component backed by tube
        Environment env = new Environment();
        env.setParameter("name", "NewComponent");
        Component component = factory.createTubeComponent("New component test", env);
        
        // Verify creation
        assertNotNull(component);
        assertEquals(LifecycleState.READY, component.getLifecycleState());
        
        // Verify it's backed by a tube
        Tube tube = factory.extractTube(component);
        assertNotNull(tube);
        assertEquals("New component test", tube.getReason());
    }
    
    @Test
    public void testChildCreation() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create parent tube
        Environment env = new Environment();
        Tube parentTube = Tube.create("Parent tube", env);
        
        // Create child component
        Component childComponent = factory.createChildTubeComponent("Child component", parentTube);
        
        // Verify child was created correctly
        assertNotNull(childComponent);
        
        // Verify parent-child relationship in the underlying tube
        Tube childTube = factory.extractTube(childComponent);
        assertNotNull(childTube);
        assertEquals("Child component", childTube.getReason());
        
        // Verify identity relationship
        TubeIdentity childIdentity = childTube.getIdentity();
        assertTrue(childIdentity.getLineage().size() > 1);
    }
    
    @Test
    public void testTubeCompositeConversion() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create a tube composite
        Environment env = new Environment();
        org.s8r.tube.composite.Composite tubeComposite = 
                new org.s8r.tube.composite.Composite("test-composite", env);
        
        // Add tubes
        Tube tube1 = Tube.create("First tube", env);
        Tube tube2 = Tube.create("Second tube", env);
        tubeComposite.addTube("first", tube1);
        tubeComposite.addTube("second", tube2);
        tubeComposite.connect("first", "second");
        
        // Convert to component composite
        org.s8r.component.Composite componentComposite = 
                factory.tubeCompositeToComponentComposite(tubeComposite);
        
        // Verify conversion
        assertNotNull(componentComposite);
        assertEquals("test-composite", componentComposite.getCompositeId());
        assertEquals(2, componentComposite.getComponents().size());
        assertTrue(componentComposite.getConnections().get("first").contains("second"));
    }
    
    @Test
    public void testWrapTubeComposite() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create a tube composite
        Environment env = new Environment();
        org.s8r.tube.composite.Composite tubeComposite = 
                new org.s8r.tube.composite.Composite("wrapped-composite", env);
        
        // Add tubes
        tubeComposite.addTube("source", Tube.create("Source", env));
        tubeComposite.addTube("target", Tube.create("Target", env));
        tubeComposite.connect("source", "target");
        
        // Wrap the composite
        org.s8r.component.Composite wrapper = factory.wrapTubeComposite(tubeComposite);
        
        // Verify wrapping
        assertNotNull(wrapper);
        assertEquals("wrapped-composite", wrapper.getCompositeId());
        assertNotNull(wrapper.getComponent("source"));
        assertNotNull(wrapper.getComponent("target"));
        assertTrue(wrapper.getConnections().get("source").contains("target"));
    }
    
    @Test
    public void testHybridComposite() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create tube environment and a tube
        Environment tubeEnv = new Environment();
        Tube tube = Tube.create("Legacy tube", tubeEnv);
        
        // Create a hybrid composite
        org.s8r.component.Composite hybrid = factory.createHybridComposite("hybrid", tubeEnv);
        
        // Add the wrapped tube
        Component wrappedTube = factory.tubeToComponent(tube);
        hybrid.addComponent("legacy", wrappedTube);
        
        // Verify the tube was added
        assertNotNull(hybrid.getComponent("legacy"));
        assertEquals(tube, factory.extractTube(hybrid.getComponent("legacy")));
    }
    
    @Test
    public void testTubeMachineConversion() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create a tube machine
        Environment env = new Environment();
        org.s8r.tube.machine.Machine tubeMachine = 
                new org.s8r.tube.machine.Machine("test-machine", env);
        
        // Add composites
        org.s8r.tube.composite.Composite composite1 = new org.s8r.tube.composite.Composite("comp1", env);
        org.s8r.tube.composite.Composite composite2 = new org.s8r.tube.composite.Composite("comp2", env);
        
        tubeMachine.addComposite("first", composite1);
        tubeMachine.addComposite("second", composite2);
        tubeMachine.connect("first", "second");
        
        // Convert to component machine
        org.s8r.component.Machine componentMachine = 
                factory.tubeMachineToComponentMachine(tubeMachine);
        
        // Verify conversion
        assertNotNull(componentMachine);
        assertEquals("test-machine", componentMachine.getMachineId());
        assertEquals(2, componentMachine.getComposites().size());
        assertTrue(componentMachine.getComposites().containsKey("first"));
        assertTrue(componentMachine.getComposites().containsKey("second"));
        assertTrue(componentMachine.getConnections().get("first").contains("second"));
    }
    
    @Test
    public void testWrapTubeMachine() {
        // Create factory
        S8rMigrationFactory factory = new S8rMigrationFactory(new ConsoleLogger());
        
        // Create a tube machine
        Environment env = new Environment();
        org.s8r.tube.machine.Machine tubeMachine = 
                new org.s8r.tube.machine.Machine("wrapped-machine", env);
        
        // Add composites
        tubeMachine.addComposite("source", new org.s8r.tube.composite.Composite("source", env));
        tubeMachine.addComposite("target", new org.s8r.tube.composite.Composite("target", env));
        tubeMachine.connect("source", "target");
        
        // Wrap the machine
        org.s8r.component.Machine wrapper = factory.wrapTubeMachine(tubeMachine);
        
        // Verify wrapping
        assertNotNull(wrapper);
        assertEquals("wrapped-machine", wrapper.getMachineId());
        assertNotNull(wrapper.getComposite("source"));
        assertNotNull(wrapper.getComposite("target"));
        assertTrue(wrapper.getConnections().get("source").contains("target"));
        
        // Test state propagation
        tubeMachine.updateState("testKey", "testValue");
        assertEquals("testValue", wrapper.getState().get("testKey"));
    }
}