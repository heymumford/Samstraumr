package org.s8r.architecture.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.s8r.application.port.EventDispatcher;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;

/**
 * Factory for creating test machines with predictable behavior. This class provides utility methods
 * for creating mock machines to use in architecture validation tests.
 */
public class TestMachineFactory {

  /**
   * Creates a simple machine with default behavior.
   *
   * @param name The local name for the machine
   * @return A machine implementation
   */
  public static Machine createMachine(String name) {
    return Machine.create(
        ComponentId.create(name),
        MachineType.DATA_PROCESSOR,
        name,
        "Test machine for architecture tests",
        "1.0.0");
  }

  /**
   * Creates a data processing machine for testing data flows.
   *
   * @param name The local name for the machine
   * @param eventDispatcher The event dispatcher to use
   * @return A mock data processing machine
   */
  public static Machine createDataProcessingMachine(String name, EventDispatcher eventDispatcher) {
    MockDataProcessingMachine mock =
        new MockDataProcessingMachine(ComponentId.create(name), eventDispatcher);
    return mock.getMachine();
  }

  /** Mock machine implementation specialized for data processing tests. */
  public static class MockDataProcessingMachine {
    private final Machine delegateMachine;
    private final EventDispatcher eventDispatcher;
    private final Map<ComponentId, List<Object>> processedData = new HashMap<>();

    public MockDataProcessingMachine(ComponentId id, EventDispatcher eventDispatcher) {
      this.delegateMachine =
          Machine.create(
              id,
              MachineType.DATA_PROCESSOR,
              "DataProcessor",
              "Test data processor machine",
              "1.0.0");
      this.eventDispatcher = eventDispatcher;
    }

    public Machine getMachine() {
      return delegateMachine;
    }

    /**
     * Simulates data being sent to a component in this machine.
     *
     * @param targetId The target component ID
     * @param data The data to process
     */
    public void sendData(ComponentId targetId, Object data) {
      // Ensure the machine is running
      if (delegateMachine.getState() != MachineState.RUNNING) {
        throw new IllegalStateException("Cannot send data to a machine that is not running");
      }

      // Record the data being processed
      processedData.computeIfAbsent(targetId, k -> new ArrayList<>()).add(data);

      // Fire event for the data being processed
      Map<String, Object> dataMap = new HashMap<>();
      dataMap.put("value", data);
      ComponentDataEvent event =
          new ComponentDataEvent(delegateMachine.getId(), "data.processed", dataMap);
      eventDispatcher.dispatch(event);
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
  }
}
