/*
 * Copyright (c) 2025
 * All rights reserved.
 */
package org.s8r.test.steps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Assertions;
import org.s8r.application.port.EventPublisherPort;
import org.s8r.infrastructure.event.EventPublisherAdapter;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/** Step definitions for Event Publisher Port interface tests. */
public class EventPublisherPortSteps {

  private EventPublisherPort eventPublisher;
  private boolean publishResult;
  private TestEventSubscriber currentSubscriber;
  private String currentSubscriptionId;
  private Map<String, TestEventSubscriber> topicSubscribers = new HashMap<>();
  private Map<String, List<String>> subscriptionIds = new HashMap<>();

  private static class TestEventSubscriber implements EventPublisherPort.EventSubscriber {
    private List<Map<String, Object>> receivedEvents = new CopyOnWriteArrayList<>();
    private AtomicBoolean eventReceived = new AtomicBoolean(false);

    @Override
    public void onEvent(
        String topic, String eventType, String payload, Map<String, String> properties) {
      Map<String, Object> event = new HashMap<>();
      event.put("topic", topic);
      event.put("eventType", eventType);
      event.put("payload", payload);
      if (properties != null) {
        event.put("properties", new HashMap<>(properties));
      }

      receivedEvents.add(event);
      eventReceived.set(true);
    }

    public boolean wasEventReceived() {
      return eventReceived.get();
    }

    public List<Map<String, Object>> getReceivedEvents() {
      return new ArrayList<>(receivedEvents);
    }

    public void reset() {
      eventReceived.set(false);
      receivedEvents.clear();
    }
  }

  @Given("an event publisher port implementation is available")
  public void anEventPublisherPortImplementationIsAvailable() {
    // Create mock dependencies for the adapter
    org.s8r.application.port.EventDispatcher mockDispatcher =
        org.mockito.Mockito.mock(org.s8r.application.port.EventDispatcher.class);
    org.s8r.application.port.ComponentRepository mockRepo =
        org.mockito.Mockito.mock(org.s8r.application.port.ComponentRepository.class);
    org.s8r.application.port.LoggerPort mockLogger =
        org.mockito.Mockito.mock(org.s8r.application.port.LoggerPort.class);

    eventPublisher = new EventPublisherAdapter(mockDispatcher, mockRepo, mockLogger);
  }

  @Given("I have an event subscriber for topic {string}")
  public void iHaveAnEventSubscriberForTopic(String topic) {
    currentSubscriber = new TestEventSubscriber();
    currentSubscriptionId = eventPublisher.subscribe(topic, currentSubscriber);
  }

  @When("I publish an event to topic {string} with type {string} and payload {string}")
  public void iPublishAnEventToTopicWithTypeAndPayload(
      String topic, String eventType, String payload) {
    publishResult = eventPublisher.publishEvent(topic, eventType, payload);
  }

  @Then("the event should be published successfully")
  public void theEventShouldBePublishedSuccessfully() {
    Assertions.assertTrue(publishResult, "Event should be published successfully");
  }

  @Then("the subscriber should receive the event")
  public void theSubscriberShouldReceiveTheEvent() {
    Assertions.assertTrue(
        currentSubscriber.wasEventReceived(), "Subscriber should receive the event");
  }

  @Then("the event should have type {string}")
  public void theEventShouldHaveType(String expectedType) {
    List<Map<String, Object>> events = currentSubscriber.getReceivedEvents();
    Assertions.assertFalse(events.isEmpty(), "Subscriber should have received events");

    boolean found = false;
    for (Map<String, Object> event : events) {
      if (event.get("eventType").equals(expectedType)) {
        found = true;
        break;
      }
    }

    Assertions.assertTrue(found, "Event with type '" + expectedType + "' should be found");
  }

  @Then("the event should have payload {string}")
  public void theEventShouldHavePayload(String expectedPayload) {
    List<Map<String, Object>> events = currentSubscriber.getReceivedEvents();
    Assertions.assertFalse(events.isEmpty(), "Subscriber should have received events");

    boolean found = false;
    for (Map<String, Object> event : events) {
      if (event.get("payload").equals(expectedPayload)) {
        found = true;
        break;
      }
    }

    Assertions.assertTrue(found, "Event with payload '" + expectedPayload + "' should be found");
  }

  @When("I publish an event with properties:")
  public void iPublishAnEventWithProperties(DataTable dataTable) {
    Map<String, String> data = dataTable.asMap();

    String topic = data.get("topic");
    String eventType = data.get("type");
    String payload = data.get("payload");

    Map<String, String> properties = new HashMap<>();

    for (Map.Entry<String, String> entry : data.entrySet()) {
      String key = entry.getKey();
      if (!key.equals("topic") && !key.equals("type") && !key.equals("payload")) {
        properties.put(key, entry.getValue());
      }
    }

    publishResult = eventPublisher.publishEvent(topic, eventType, payload, properties);
  }

  @Then("the subscriber should receive the event with properties:")
  public void theSubscriberShouldReceiveTheEventWithProperties(DataTable dataTable) {
    Map<String, String> expectedProperties = dataTable.asMap();
    List<Map<String, Object>> events = currentSubscriber.getReceivedEvents();

    Assertions.assertFalse(events.isEmpty(), "Subscriber should have received events");

    boolean allPropertiesMatch = false;
    for (Map<String, Object> event : events) {
      // Skip events without properties
      if (!event.containsKey("properties")) {
        continue;
      }

      Map<String, String> properties = (Map<String, String>) event.get("properties");
      boolean match = true;

      for (Map.Entry<String, String> entry : expectedProperties.entrySet()) {
        String key = entry.getKey();
        String expectedValue = entry.getValue();

        if (!properties.containsKey(key) || !properties.get(key).equals(expectedValue)) {
          match = false;
          break;
        }
      }

      if (match) {
        allPropertiesMatch = true;
        break;
      }
    }

    Assertions.assertTrue(
        allPropertiesMatch, "An event with all expected properties should be received");
  }

  @Given("I have an event subscriber")
  public void iHaveAnEventSubscriber() {
    currentSubscriber = new TestEventSubscriber();
  }

  @When("I subscribe the subscriber to topic {string}")
  public void iSubscribeTheSubscriberToTopic(String topic) {
    currentSubscriptionId = eventPublisher.subscribe(topic, currentSubscriber);
  }

  @Then("I should receive a valid subscription ID")
  public void iShouldReceiveAValidSubscriptionId() {
    Assertions.assertNotNull(currentSubscriptionId, "Subscription ID should not be null");
    Assertions.assertFalse(currentSubscriptionId.isEmpty(), "Subscription ID should not be empty");
  }

  @When("I unsubscribe using the subscription ID")
  public void iUnsubscribeUsingTheSubscriptionId() {
    boolean result = eventPublisher.unsubscribe(currentSubscriptionId);
    Assertions.assertTrue(result, "Unsubscribe operation should succeed");
  }

  @When("I publish another event to the same topic")
  public void iPublishAnotherEventToTheSameTopic() {
    // Reset the subscriber to ensure a clean state
    currentSubscriber.reset();

    // Publish a new event
    publishResult =
        eventPublisher.publishEvent("subscribe-test", "another-event", "Another test payload");
  }

  @Then("the subscriber should not receive the second event")
  public void theSubscriberShouldNotReceiveTheSecondEvent() {
    Assertions.assertFalse(
        currentSubscriber.wasEventReceived(),
        "Subscriber should not receive events after unsubscribing");
  }

  @Given("I have the following subscribers for topic {string}:")
  public void iHaveTheFollowingSubscribersForTopic(String topic, List<String> subscriberNames) {
    for (String name : subscriberNames) {
      TestEventSubscriber subscriber = new TestEventSubscriber();
      String subscriptionId = eventPublisher.subscribe(topic, subscriber);

      topicSubscribers.put(name, subscriber);
      subscriptionIds.computeIfAbsent(topic, k -> new ArrayList<>()).add(subscriptionId);
    }
  }

  @Then("all subscribers should receive the event")
  public void allSubscribersShouldReceiveTheEvent() {
    for (Map.Entry<String, TestEventSubscriber> entry : topicSubscribers.entrySet()) {
      String subscriberName = entry.getKey();
      TestEventSubscriber subscriber = entry.getValue();

      Assertions.assertTrue(
          subscriber.wasEventReceived(),
          "Subscriber '" + subscriberName + "' should receive the event");
    }
  }

  @Given("I have subscribers for the following topics:")
  public void iHaveSubscribersForTheFollowingTopics(List<String> topics) {
    for (String topic : topics) {
      TestEventSubscriber subscriber = new TestEventSubscriber();
      String subscriptionId = eventPublisher.subscribe(topic, subscriber);

      topicSubscribers.put(topic, subscriber);
      subscriptionIds.computeIfAbsent(topic, k -> new ArrayList<>()).add(subscriptionId);
    }
  }

  @When("I publish events to each topic")
  public void iPublishEventsToEachTopic() {
    for (String topic : topicSubscribers.keySet()) {
      eventPublisher.publishEvent(topic, "test-event", "Payload for " + topic);
    }
  }

  @Then("each subscriber should receive only events for its topic")
  public void eachSubscriberShouldReceiveOnlyEventsForItsTopic() {
    for (Map.Entry<String, TestEventSubscriber> entry : topicSubscribers.entrySet()) {
      String topic = entry.getKey();
      TestEventSubscriber subscriber = entry.getValue();

      Assertions.assertTrue(
          subscriber.wasEventReceived(),
          "Subscriber for topic '" + topic + "' should receive events");

      // Verify this subscriber only received events for its topic
      List<Map<String, Object>> events = subscriber.getReceivedEvents();
      for (Map<String, Object> event : events) {
        Assertions.assertEquals(
            topic,
            event.get("topic"),
            "Subscriber should only receive events for its subscribed topic");
      }
    }
  }
}
