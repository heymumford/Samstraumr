/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford)
 *
 * This software was developed with analytical assistance from AI tools
 * including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
 * which were used as paid services. All intellectual property rights
 * remain exclusively with the copyright holder listed above.
 *
 * Licensed under the Mozilla Public License 2.0
 */
package org.s8r.test.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.s8r.component.Component;
import org.s8r.component.EventListener;
import org.s8r.component.State;
import org.s8r.component.StateTransitionListener;

/**
 * Factory for creating test implementations of listener interfaces. This simplifies writing test
 * code that needs to verify listener behavior.
 */
public class ListenerFactory {

  /**
   * Creates a simple event listener that records events.
   *
   * @param eventType The event type to listen for
   * @return An EventListener implementation
   */
  public static RecordingEventListener createEventListener(String eventType) {
    return new RecordingEventListener(eventType);
  }

  /**
   * Creates a simple state transition listener that records transitions.
   *
   * @return A StateTransitionListener implementation
   */
  public static RecordingStateTransitionListener createStateTransitionListener() {
    return new RecordingStateTransitionListener();
  }

  /** Event listener implementation that records events for testing. */
  public static class RecordingEventListener implements EventListener {
    private String eventType;
    private final List<EventRecord> receivedEvents = new CopyOnWriteArrayList<>();
    private boolean terminated = false;

    public RecordingEventListener(String eventType) {
      this.eventType = eventType;
    }

    @Override
    public void onEvent(Component component, String eventType, Map<String, Object> data) {
      receivedEvents.add(new EventRecord(component, eventType, data));
    }

    @Override
    public void setEventType(String eventType) {
      this.eventType = eventType;
    }

    @Override
    public String getEventType() {
      return eventType;
    }

    @Override
    public void onTermination() {
      this.terminated = true;
    }

    /**
     * Gets all recorded events.
     *
     * @return List of event records
     */
    public List<EventRecord> getReceivedEvents() {
      return receivedEvents;
    }

    /**
     * Gets the number of events received.
     *
     * @return Event count
     */
    public int getEventCount() {
      return receivedEvents.size();
    }

    /**
     * Checks if this listener has been terminated.
     *
     * @return true if terminated
     */
    public boolean isTerminated() {
      return terminated;
    }

    /** Clears all recorded events. */
    public void clearEvents() {
      receivedEvents.clear();
    }

    /** Record of a received event. */
    public static class EventRecord {
      private final Component component;
      private final String eventType;
      private final Map<String, Object> data;
      private final long timestamp;

      public EventRecord(Component component, String eventType, Map<String, Object> data) {
        this.component = component;
        this.eventType = eventType;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
      }

      public Component getComponent() {
        return component;
      }

      public String getEventType() {
        return eventType;
      }

      public Map<String, Object> getData() {
        return data;
      }

      public long getTimestamp() {
        return timestamp;
      }
    }
  }

  /** State transition listener implementation that records transitions for testing. */
  public static class RecordingStateTransitionListener implements StateTransitionListener {
    private final List<TransitionRecord> transitions = new CopyOnWriteArrayList<>();
    private boolean terminated = false;

    @Override
    public void onStateTransition(Component component, State oldState, State newState) {
      transitions.add(new TransitionRecord(component, oldState, newState));
    }

    @Override
    public void onTermination() {
      this.terminated = true;
    }

    /**
     * Gets all recorded transitions.
     *
     * @return List of transition records
     */
    public List<TransitionRecord> getTransitions() {
      return transitions;
    }

    /**
     * Gets the number of transitions recorded.
     *
     * @return Transition count
     */
    public int getTransitionCount() {
      return transitions.size();
    }

    /**
     * Checks if this listener has been terminated.
     *
     * @return true if terminated
     */
    public boolean isTerminated() {
      return terminated;
    }

    /** Record of a state transition. */
    public static class TransitionRecord {
      private final Component component;
      private final State oldState;
      private final State newState;
      private final long timestamp;

      public TransitionRecord(Component component, State oldState, State newState) {
        this.component = component;
        this.oldState = oldState;
        this.newState = newState;
        this.timestamp = System.currentTimeMillis();
      }

      public Component getComponent() {
        return component;
      }

      public State getOldState() {
        return oldState;
      }

      public State getNewState() {
        return newState;
      }

      public long getTimestamp() {
        return timestamp;
      }
    }
  }
}
