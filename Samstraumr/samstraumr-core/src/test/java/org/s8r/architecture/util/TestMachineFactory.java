package org.s8r.architecture.util;

import java.util.*;
import java.util.function.Consumer;

import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.event.DomainEvent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.MachineStateChangedEvent;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;
import org.s8r.application.port.EventDispatcher;

/**
 * Factory for creating test machines with predictable behavior.
 * This class provides utility methods for creating mock machines
 * to use in architecture validation tests.
 */
public class TestMachineFactory {

    /**
     * Creates a simple mock machine with default behavior.
     *
     * @param name The local name for the machine
     * @return A mock machine implementation
     */
    public static Machine createMachine(String name) {
        return new MockMachine(new ComponentId(name), MachineType.PROCESSING);
    }
    
    /**
     * Creates a data processing machine for testing data flows.
     *
     * @param name The local name for the machine
     * @param eventDispatcher The event dispatcher to use
     * @return A mock data processing machine
     */
    public static Machine createDataProcessingMachine(String name, EventDispatcher eventDispatcher) {
        return new MockDataProcessingMachine(new ComponentId(name), eventDispatcher);
    }
    
    /**
     * Basic mock machine implementation.
     */
    public static class MockMachine implements Machine {
        private final ComponentId id;
        private final MachineType type;
        private MachineState state;
        private final Map<String, Object> attributes = new HashMap<>();
        private final List<Component> components = new ArrayList<>();
        
        public MockMachine(ComponentId id, MachineType type) {
            this.id = id;
            this.type = type;
            this.state = MachineState.CREATED;
        }
        
        @Override
        public ComponentId getId() {
            return id;
        }
        
        @Override
        public MachineType getType() {
            return type;
        }
        
        @Override
        public MachineState getState() {
            return state;
        }
        
        @Override
        public void initialize() {
            this.state = MachineState.INITIALIZED;
            components.forEach(Component::initialize);
        }
        
        @Override
        public void start() {
            this.state = MachineState.RUNNING;
            components.forEach(Component::start);
        }
        
        @Override
        public void stop() {
            this.state = MachineState.STOPPED;
            components.forEach(Component::stop);
        }
        
        @Override
        public void destroy() {
            this.state = MachineState.DESTROYED;
            components.forEach(Component::destroy);
        }
        
        @Override
        public boolean isRunning() {
            return state == MachineState.RUNNING;
        }
        
        @Override
        public void addComponent(Component component) {
            components.add(component);
        }
        
        @Override
        public void removeComponent(ComponentId componentId) {
            components.removeIf(c -> c.getId().equals(componentId));
        }
        
        @Override
        public Component getComponent(ComponentId componentId) {
            return components.stream()
                    .filter(c -> c.getId().equals(componentId))
                    .findFirst()
                    .orElse(null);
        }
        
        @Override
        public List<Component> getComponents() {
            return new ArrayList<>(components);
        }
        
        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }
        
        public Object getAttribute(String key) {
            return attributes.get(key);
        }
    }
    
    /**
     * Mock machine implementation specialized for data processing tests.
     */
    public static class MockDataProcessingMachine extends MockMachine {
        private final EventDispatcher eventDispatcher;
        private final Map<ComponentId, List<Object>> processedData = new HashMap<>();
        
        public MockDataProcessingMachine(ComponentId id, EventDispatcher eventDispatcher) {
            super(id, MachineType.PROCESSING);
            this.eventDispatcher = eventDispatcher;
        }
        
        /**
         * Simulates data being sent to a component in this machine.
         *
         * @param targetId The target component ID
         * @param data The data to process
         */
        public void sendData(ComponentId targetId, Object data) {
            // Ensure the machine is running
            if (!isRunning()) {
                throw new IllegalStateException("Cannot send data to a machine that is not running");
            }
            
            // Find the target component
            Component target = getComponent(targetId);
            if (target == null) {
                throw new IllegalArgumentException("Component with ID " + targetId + " not found in this machine");
            }
            
            // Record the data being processed
            processedData.computeIfAbsent(targetId, k -> new ArrayList<>()).add(data);
            
            // Fire event for the data being processed
            ComponentDataEvent event = new ComponentDataEvent(
                getId(), targetId, System.currentTimeMillis(), data);
            eventDispatcher.publish(event);
        }
        
        /**
         * Gets the data that has been processed by a component.
         *
         * @param componentId The component ID
         * @return List of data objects processed by the component
         */
        public List<Object> getProcessedData(ComponentId componentId) {
            return new ArrayList<>(processedData.getOrDefault(componentId, new ArrayList<>()));
        }
        
        @Override
        public void start() {
            super.start();
            eventDispatcher.publish(new MachineStateChangedEvent(
                getId(), MachineState.INITIALIZED, MachineState.RUNNING, System.currentTimeMillis()));
        }
        
        @Override
        public void stop() {
            super.stop();
            eventDispatcher.publish(new MachineStateChangedEvent(
                getId(), MachineState.RUNNING, MachineState.STOPPED, System.currentTimeMillis()));
        }
    }
}