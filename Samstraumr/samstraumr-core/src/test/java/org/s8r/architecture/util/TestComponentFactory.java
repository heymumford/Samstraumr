package org.s8r.architecture.util;

import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.ComponentConnection;
import org.s8r.domain.component.composite.ConnectionType;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.event.DomainEvent;
import org.s8r.application.port.EventDispatcher;

/**
 * Factory for creating test components with predictable behavior.
 * This class provides utility methods for creating mock components
 * to use in architecture validation tests.
 */
public class TestComponentFactory {

    /**
     * Creates a simple mock component with default behavior.
     *
     * @param name The local name for the component
     * @return A mock component implementation
     */
    public static Component createComponent(String name) {
        return new MockComponent(new ComponentId(name));
    }
    
    /**
     * Creates a composite component that can contain other components.
     *
     * @param name The local name for the composite component
     * @return A mock composite component
     */
    public static CompositeComponent createCompositeComponent(String name) {
        return new MockCompositeComponent(new ComponentId(name));
    }
    
    /**
     * Creates a mock event dispatcher for testing event-driven communication.
     *
     * @return A mock event dispatcher
     */
    public static EventDispatcher createEventDispatcher() {
        return new MockEventDispatcher();
    }
    
    /**
     * Basic mock component implementation.
     */
    public static class MockComponent implements Component {
        private final ComponentId id;
        private LifecycleState state;
        private final Map<String, Object> attributes = new HashMap<>();
        
        public MockComponent(ComponentId id) {
            this.id = id;
            this.state = LifecycleState.CREATED;
        }
        
        @Override
        public ComponentId getId() {
            return id;
        }
        
        @Override
        public LifecycleState getState() {
            return state;
        }
        
        @Override
        public void initialize() {
            this.state = LifecycleState.INITIALIZED;
        }
        
        @Override
        public void start() {
            this.state = LifecycleState.RUNNING;
        }
        
        @Override
        public void stop() {
            this.state = LifecycleState.STOPPED;
        }
        
        @Override
        public void destroy() {
            this.state = LifecycleState.DESTROYED;
        }
        
        @Override
        public boolean isRunning() {
            return state == LifecycleState.RUNNING;
        }
        
        public void setAttribute(String key, Object value) {
            attributes.put(key, value);
        }
        
        public Object getAttribute(String key) {
            return attributes.get(key);
        }
    }
    
    /**
     * Mock composite component implementation.
     */
    public static class MockCompositeComponent extends MockComponent implements CompositeComponent {
        private final List<Component> children = new ArrayList<>();
        private final List<ComponentConnection> connections = new ArrayList<>();
        
        public MockCompositeComponent(ComponentId id) {
            super(id);
        }
        
        @Override
        public void addComponent(Component component) {
            children.add(component);
        }
        
        @Override
        public void removeComponent(ComponentId componentId) {
            children.removeIf(c -> c.getId().equals(componentId));
        }
        
        @Override
        public Component getComponent(ComponentId componentId) {
            return children.stream()
                    .filter(c -> c.getId().equals(componentId))
                    .findFirst()
                    .orElse(null);
        }
        
        @Override
        public List<Component> getComponents() {
            return new ArrayList<>(children);
        }
        
        @Override
        public void connect(ComponentId sourceId, ComponentId targetId, ConnectionType type) {
            connections.add(new ComponentConnection(sourceId, targetId, type));
        }
        
        @Override
        public void disconnect(ComponentId sourceId, ComponentId targetId) {
            connections.removeIf(c -> 
                c.getSourceId().equals(sourceId) && 
                c.getTargetId().equals(targetId));
        }
        
        @Override
        public List<ComponentConnection> getConnections() {
            return new ArrayList<>(connections);
        }
    }
    
    /**
     * Mock event dispatcher for testing event-driven communication.
     */
    public static class MockEventDispatcher implements EventDispatcher {
        private final List<DomainEvent> publishedEvents = new ArrayList<>();
        private final Map<Class<?>, List<Consumer<?>>> subscribers = new HashMap<>();
        
        @Override
        public <T extends DomainEvent> void publish(T event) {
            publishedEvents.add(event);
            
            // Notify subscribers for this event type
            List<Consumer<T>> eventSubscribers = getSubscribers(event.getClass());
            for (Consumer<T> subscriber : eventSubscribers) {
                subscriber.accept(event);
            }
        }
        
        @SuppressWarnings("unchecked")
        private <T extends DomainEvent> List<Consumer<T>> getSubscribers(Class<?> eventType) {
            return (List<Consumer<T>>) (List<?>) subscribers.getOrDefault(eventType, new ArrayList<>());
        }
        
        @Override
        public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> subscriber) {
            subscribers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(subscriber);
        }
        
        public List<DomainEvent> getPublishedEvents() {
            return new ArrayList<>(publishedEvents);
        }
        
        public <T extends DomainEvent> List<T> getPublishedEventsOfType(Class<T> eventType) {
            List<T> result = new ArrayList<>();
            for (DomainEvent event : publishedEvents) {
                if (eventType.isInstance(event)) {
                    result.add(eventType.cast(event));
                }
            }
            return result;
        }
    }
}