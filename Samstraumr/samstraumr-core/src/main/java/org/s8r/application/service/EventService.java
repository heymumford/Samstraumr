package org.s8r.application.service;

import org.s8r.application.port.EventPublisherPort;
import org.s8r.application.port.NotificationPort;
import org.s8r.domain.event.DomainEvent;

/**
 * Event service that handles publishing events and notifications.
 */
public class EventService {
    
    private final EventPublisherPort eventPublisher;
    private final NotificationPort notificationPort;
    
    /**
     * Creates a new event service with the given event publisher and notification port.
     *
     * @param eventPublisher the event publisher port
     * @param notificationPort the notification port
     */
    public EventService(EventPublisherPort eventPublisher, NotificationPort notificationPort) {
        this.eventPublisher = eventPublisher;
        this.notificationPort = notificationPort;
    }
    
    /**
     * Publishes an event and sends notifications for it.
     *
     * @param event the event to publish
     */
    public void publishEventWithNotification(DomainEvent event) {
        eventPublisher.publishEvent(event);
        notificationPort.sendNotification(
                "Event " + event.getClass().getSimpleName() + " published", 
                event.toString());
    }
    
    /**
     * Registers a notification handler for events.
     *
     * @param eventType the event type to listen for
     */
    public void registerNotificationHandler(Class<? extends DomainEvent> eventType) {
        eventPublisher.registerHandler(eventType, event -> 
                notificationPort.sendNotification(
                        "Event " + event.getClass().getSimpleName() + " received", 
                        event.toString()));
    }
    
    /**
     * Gets the event publisher.
     *
     * @return the event publisher
     */
    public EventPublisherPort getEventPublisher() {
        return eventPublisher;
    }
    
    /**
     * Gets the notification port.
     *
     * @return the notification port
     */
    public NotificationPort getNotificationPort() {
        return notificationPort;
    }
}