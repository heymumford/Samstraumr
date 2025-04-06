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

package org.s8r.architecture.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.LoggerPort;
import org.s8r.domain.event.DomainEvent;
import org.s8r.infrastructure.logging.ConsoleLogger;

/**
 * A hierarchical event dispatcher that supports event propagation through the class hierarchy.
 * This implementation enables hierarchical event propagation to correctly handle parent-child relationships
 * in the event model.
 */
public class HierarchicalEventDispatcher implements EventDispatcher {
    private final Map<Class<? extends DomainEvent>, List<EventHandler<? extends DomainEvent>>> handlers;
    private final LoggerPort logger;
    private final List<DomainEvent> publishedEvents = new ArrayList<>();

    /**
     * Creates a new hierarchical event dispatcher.
     */
    public HierarchicalEventDispatcher() {
        this.handlers = new HashMap<>();
        this.logger = new ConsoleLogger(getClass().getSimpleName());
    }
    
    /**
     * Gets all published events for testing and verification.
     * 
     * @return A list of all published events
     */
    public List<DomainEvent> getPublishedEvents() {
        return new ArrayList<>(publishedEvents);
    }
    
    /**
     * Gets all published events of a specific type for testing and verification.
     * 
     * @param <T> The type of events to get
     * @param eventType The class of events to get
     * @return A list of published events of the specified type
     */
    public <T extends DomainEvent> List<T> getPublishedEventsOfType(Class<T> eventType) {
        List<T> result = new ArrayList<>();
        for (DomainEvent event : publishedEvents) {
            if (eventType.isInstance(event)) {
                result.add(eventType.cast(event));
            }
        }
        return result;
    }
    
    /**
     * Publishes an event for testing. This is a test-only method that simulates 
     * publishing an event through this dispatcher.
     * 
     * @param event The event to publish
     */
    public void publish(DomainEvent event) {
        dispatch(event);
    }
    
    /**
     * Subscribes a consumer to an event type for testing. This is useful when
     * testing with lambdas and provides compatibility with the old API.
     * 
     * @param <T> The type of event to subscribe to
     * @param eventType The class of event to subscribe to
     * @param consumer The consumer that will handle the event
     */
    public <T extends DomainEvent> void subscribe(Class<T> eventType, java.util.function.Consumer<T> consumer) {
        registerHandler(eventType, event -> consumer.accept(event));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void dispatch(DomainEvent event) {
        if (event == null) {
            return;
        }

        // Add the event to published events for test verification
        publishedEvents.add(event);
        
        // Get the event class
        Class<? extends DomainEvent> eventClass = event.getClass();
        logger.debug("Dispatching event: {} (ID: {})", eventClass.getSimpleName(), event.getEventId());

        // Process handlers for the exact event type
        dispatchToHandlers(event, eventClass);

        // Process handlers for parent classes for hierarchical propagation
        processParentEventHandlers(event, eventClass);
    }
    
    /**
     * Recursively processes handlers for parent event classes to implement hierarchical dispatch.
     * This enables polymorphic event handling where handlers registered for a parent event type
     * will receive events of derived types.
     *
     * @param event The event to dispatch
     * @param eventClass The current event class being processed
     */
    private void processParentEventHandlers(DomainEvent event, Class<? extends DomainEvent> eventClass) {
        // Get the superclass
        Class<?> superClass = eventClass.getSuperclass();
        
        // If the superclass is DomainEvent or a subclass of DomainEvent, dispatch to its handlers
        if (DomainEvent.class.isAssignableFrom(superClass) && superClass != Object.class) {
            @SuppressWarnings("unchecked")
            Class<? extends DomainEvent> parentEventClass = (Class<? extends DomainEvent>) superClass;
            
            // Dispatch to handlers for this parent class
            dispatchToHandlers(event, parentEventClass);
            
            // Continue with the next parent in the hierarchy
            processParentEventHandlers(event, parentEventClass);
        }
    }

    @SuppressWarnings("unchecked")
    private void dispatchToHandlers(DomainEvent event, Class<? extends DomainEvent> eventType) {
        List<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
        if (eventHandlers == null || eventHandlers.isEmpty()) {
            logger.debug("No handlers registered for event type: {}", eventType.getSimpleName());
            return;
        }

        for (EventHandler<? extends DomainEvent> handler : eventHandlers) {
            try {
                ((EventHandler<DomainEvent>) handler).handle(event);
            } catch (Exception e) {
                logger.error(
                    "Error dispatching event {} to handler {}: {}",
                    eventType.getSimpleName(),
                    handler.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler<T> handler) {
        List<EventHandler<? extends DomainEvent>> eventHandlers =
            handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>());

        eventHandlers.add(handler);
        logger.debug(
            "Registered handler {} for event type: {}",
            handler.getClass().getSimpleName(),
            eventType.getSimpleName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends DomainEvent> void unregisterHandler(
        Class<T> eventType, EventHandler<T> handler) {
        List<EventHandler<? extends DomainEvent>> eventHandlers = handlers.get(eventType);
        if (eventHandlers != null) {
            eventHandlers.remove(handler);
            logger.debug(
                "Unregistered handler {} for event type: {}",
                handler.getClass().getSimpleName(),
                eventType.getSimpleName());
        }
    }
}