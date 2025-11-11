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

package org.s8r.domain.component.port;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.s8r.domain.component.Component;
import org.s8r.domain.component.composite.CompositeComponent;
import org.s8r.domain.component.composite.CompositeFactory;
import org.s8r.domain.component.composite.CompositeType;
import org.s8r.domain.identity.ComponentId;
import org.s8r.domain.lifecycle.LifecycleState;
import org.s8r.domain.machine.Machine;
import org.s8r.domain.machine.MachineFactory;
import org.s8r.domain.machine.MachineState;
import org.s8r.domain.machine.MachineType;
import org.s8r.test.annotation.UnitTest;

/**
 * Enhanced tests for the port interface pattern implementation.
 *
 * <p>This test demonstrates how port interfaces enable Clean Architecture by allowing high-level
 * modules to depend on abstractions.
 */
@UnitTest
public class EnhancedPortInterfaceTest {

  // Simple implementation of ComponentPort for testing
  private static class TestComponentPort implements ComponentPort {
    private final ComponentId id;
    private LifecycleState state;
    private final List<String> lineage = new ArrayList<>();
    private final List<String> activityLog = new ArrayList<>();
    private final java.time.Instant creationTime = java.time.Instant.now();

    public TestComponentPort(ComponentId id) {
      this.id = id;
      this.state = LifecycleState.INITIALIZED;
      this.lineage.add(id.getIdString());
    }

    @Override
    public ComponentId getId() {
      return id;
    }

    @Override
    public LifecycleState getLifecycleState() {
      return state;
    }

    @Override
    public List<String> getLineage() {
      return java.util.Collections.unmodifiableList(lineage);
    }

    @Override
    public List<String> getActivityLog() {
      return java.util.Collections.unmodifiableList(activityLog);
    }

    @Override
    public java.time.Instant getCreationTime() {
      return creationTime;
    }

    @Override
    public List<org.s8r.domain.event.DomainEvent> getDomainEvents() {
      return java.util.Collections.emptyList();
    }

    @Override
    public void addToLineage(String entry) {
      lineage.add(entry);
    }

    @Override
    public void clearEvents() {
      // No-op for this simple implementation
    }

    @Override
    public void publishData(String channel, Map<String, Object> data) {
      activityLog.add("Published data to channel: " + channel);
    }

    @Override
    public void publishData(String channel, String key, Object value) {
      activityLog.add("Published " + key + "=" + value + " to channel: " + channel);
    }

    @Override
    public void transitionTo(LifecycleState newState) {
      activityLog.add("Transitioned from " + state + " to " + newState);
      state = newState;
    }

    @Override
    public void activate() {
      transitionTo(LifecycleState.ACTIVE);
    }

    @Override
    public void deactivate() {
      transitionTo(LifecycleState.READY);
    }

    @Override
    public void terminate() {
      transitionTo(LifecycleState.TERMINATED);
    }
  }

  // Simple composite port implementation for testing
  private static class TestCompositeComponentPort extends TestComponentPort
      implements CompositeComponentPort {
    private final Map<String, ComponentPort> components = new HashMap<>();
    private final Map<String, List<String>> connections = new HashMap<>();

    public TestCompositeComponentPort(ComponentId id) {
      super(id);
    }

    @Override
    public String getCompositeId() {
      return getId().getIdString();
    }

    @Override
    public boolean addComponent(String name, ComponentPort component) {
      components.put(name, component);
      return true;
    }

    @Override
    public Optional<ComponentPort> removeComponent(String name) {
      ComponentPort removed = components.remove(name);
      return Optional.ofNullable(removed);
    }

    @Override
    public ComponentPort getComponent(String name) {
      return components.get(name);
    }

    @Override
    public boolean hasComponent(String name) {
      return components.containsKey(name);
    }

    @Override
    public Map<String, ComponentPort> getComponents() {
      return java.util.Collections.unmodifiableMap(components);
    }

    @Override
    public boolean connect(String sourceName, String targetName) {
      List<String> targets = connections.computeIfAbsent(sourceName, k -> new ArrayList<>());
      if (!targets.contains(targetName)) {
        targets.add(targetName);
        return true;
      }
      return false;
    }

    @Override
    public boolean disconnect(String sourceName, String targetName) {
      List<String> targets = connections.get(sourceName);
      if (targets != null && targets.contains(targetName)) {
        targets.remove(targetName);
        return true;
      }
      return false;
    }

    @Override
    public Map<String, List<String>> getConnections() {
      return java.util.Collections.unmodifiableMap(connections);
    }

    @Override
    public List<String> getConnectionsFrom(String sourceName) {
      return connections.getOrDefault(sourceName, java.util.Collections.emptyList());
    }
  }

  // Simple repository implementation for testing
  private static class TestComponentRepository {
    private final Map<String, ComponentPort> components = new HashMap<>();

    public void save(ComponentPort component) {
      components.put(component.getId().getIdString(), component);
    }

    public Optional<ComponentPort> findById(ComponentId id) {
      return Optional.ofNullable(components.get(id.getIdString()));
    }

    public List<ComponentPort> findAll() {
      return new ArrayList<>(components.values());
    }
  }

  // Machine repository for testing
  private static class TestMachineRepository {
    private final Map<String, MachinePort> machines = new HashMap<>();

    public void save(MachinePort machine) {
      machines.put(machine.getId().getIdString(), machine);
    }

    public Optional<MachinePort> findById(ComponentId id) {
      return Optional.ofNullable(machines.get(id.getIdString()));
    }

    public List<MachinePort> findAll() {
      return new ArrayList<>(machines.values());
    }
  }

  // Service for working with components via port interfaces
  private static class ComponentService {
    private final TestComponentRepository repository;

    public ComponentService(TestComponentRepository repository) {
      this.repository = repository;
    }

    public ComponentPort createComponent(String name) {
      ComponentId id = ComponentId.fromString(UUID.randomUUID().toString(), name);
      ComponentPort component = new TestComponentPort(id);
      repository.save(component);
      return component;
    }

    public CompositeComponentPort createComposite(String name) {
      ComponentId id = ComponentId.fromString(UUID.randomUUID().toString(), name);
      CompositeComponentPort composite = new TestCompositeComponentPort(id);
      repository.save(composite);
      return composite;
    }

    public void activateComponent(ComponentId id) {
      repository.findById(id).ifPresent(ComponentPort::activate);
    }
  }

  // Service for working with machines via port interfaces
  private static class MachineService {
    private final TestMachineRepository repository;

    public MachineService(TestMachineRepository repository) {
      this.repository = repository;
    }

    public MachinePort createMachine(String name, MachineType type) {
      // Create a domain machine first
      Machine machine = MachineFactory.createMachine(type, name, "Created via service", "1.0");

      // Create a port adapter for it
      MachinePort machinePort = new SimpleMachineAdapter(machine);

      // Save to repository
      repository.save(machinePort);

      return machinePort;
    }

    public void startMachine(ComponentId id) {
      repository.findById(id).ifPresent(MachinePort::start);
    }

    public void stopMachine(ComponentId id) {
      repository.findById(id).ifPresent(MachinePort::stop);
    }

    public void connectMachineComposites(
        ComponentId machineId, String sourceComposite, String targetComposite) {
      repository
          .findById(machineId)
          .ifPresent(machine -> machine.connectComposites(sourceComposite, targetComposite));
    }
  }

  private TestComponentRepository componentRepository;
  private ComponentService componentService;
  private TestMachineRepository machineRepository;
  private MachineService machineService;

  @BeforeEach
  void setUp() {
    componentRepository = new TestComponentRepository();
    componentService = new ComponentService(componentRepository);
    machineRepository = new TestMachineRepository();
    machineService = new MachineService(machineRepository);
  }

  @Test
  @DisplayName("Test creating and working with components through port interfaces")
  void testComponentPortInterfaces() {
    // Create a component through the service
    ComponentPort component = componentService.createComponent("Test Component");

    // Verify component was created
    assertNotNull(component);
    assertEquals(LifecycleState.INITIALIZED, component.getLifecycleState());

    // Activate the component
    component.activate();
    assertEquals(LifecycleState.ACTIVE, component.getLifecycleState());

    // Retrieve from repository
    Optional<ComponentPort> retrieved = componentRepository.findById(component.getId());
    assertTrue(retrieved.isPresent());

    // Verify component state was preserved
    assertEquals(LifecycleState.ACTIVE, retrieved.get().getLifecycleState());
  }

  @Test
  @DisplayName("Test creating and working with composites through port interfaces")
  void testCompositePortInterfaces() {
    // Create a composite
    CompositeComponentPort composite = componentService.createComposite("Test Composite");

    // Create and add components to the composite
    ComponentPort component1 = componentService.createComponent("Component 1");
    ComponentPort component2 = componentService.createComponent("Component 2");

    composite.addComponent("comp1", component1);
    composite.addComponent("comp2", component2);

    // Connect components
    boolean connected = composite.connect("comp1", "comp2");
    assertTrue(connected);

    // Verify connections
    List<String> connections = composite.getConnectionsFrom("comp1");
    assertEquals(1, connections.size());
    assertEquals("comp2", connections.get(0));

    // Retrieve composite from repository
    Optional<ComponentPort> retrieved = componentRepository.findById(composite.getId());
    assertTrue(retrieved.isPresent());
    assertTrue(retrieved.get() instanceof CompositeComponentPort);

    // Verify composite structure
    CompositeComponentPort retrievedComposite = (CompositeComponentPort) retrieved.get();
    assertEquals(2, retrievedComposite.getComponents().size());
    assertTrue(retrievedComposite.hasComponent("comp1"));
    assertTrue(retrievedComposite.hasComponent("comp2"));
  }

  @Test
  @DisplayName("Test creating and working with machines through port interfaces")
  void testMachinePortInterfaces() {
    // Create a machine
    MachinePort machine = machineService.createMachine("Test Machine", MachineType.DATA_PROCESSOR);

    // Verify machine was created
    assertNotNull(machine);
    assertEquals(MachineState.STOPPED, machine.getMachineState());
    assertEquals(MachineType.DATA_PROCESSOR, machine.getMachineType());

    // Start the machine
    boolean started = machine.start();
    assertTrue(started);
    assertEquals(MachineState.RUNNING, machine.getMachineState());

    // Create and add composites to the machine
    CompositeComponentPort composite1 = componentService.createComposite("Composite 1");
    CompositeComponentPort composite2 = componentService.createComposite("Composite 2");

    machine.addComposite("comp1", composite1);
    machine.addComposite("comp2", composite2);

    // Connect composites
    boolean connected = machine.connectComposites("comp1", "comp2");
    assertTrue(connected);

    // Verify machine structure
    assertEquals(2, machine.getComposites().size());
    assertNotNull(machine.getComposite("comp1"));
    assertNotNull(machine.getComposite("comp2"));

    // Verify connections
    List<String> connections = machine.getConnectionsFrom("comp1");
    assertEquals(1, connections.size());
    assertEquals("comp2", connections.get(0));

    // Retrieve machine from repository
    Optional<MachinePort> retrieved = machineRepository.findById(machine.getId());
    assertTrue(retrieved.isPresent());

    // Verify machine state was preserved
    assertEquals(MachineState.RUNNING, retrieved.get().getMachineState());
  }

  @Test
  @DisplayName("Test integrating domain entities with port interfaces")
  void testDomainEntitiesWithPortInterfaces() {
    // Create a domain component
    Component domainComponent = Component.create(ComponentId.create("Domain Component"));

    // Create a port interface for it
    ComponentPort componentPort = new TestComponentPort(domainComponent.getId());

    // Save to repository
    componentRepository.save(componentPort);

    // Create a domain composite
    CompositeComponent domainComposite =
        CompositeFactory.createComposite(CompositeType.STANDARD, "Domain Composite");

    // Create a port interface for it
    CompositeComponentPort compositePort = new TestCompositeComponentPort(domainComposite.getId());

    // Add the domain component to the composite via port interfaces
    compositePort.addComponent("component", componentPort);

    // Save to repository
    componentRepository.save(compositePort);

    // Create a domain machine
    Machine domainMachine =
        MachineFactory.createMachine(
            MachineType.WORKFLOW, "Domain Machine", "Created for testing", "1.0");

    // Create a port interface for it
    MachinePort machinePort = new SimpleMachineAdapter(domainMachine);

    // Add the composite to the machine via port interfaces
    machinePort.addComposite("composite", compositePort);

    // Save to repository
    machineRepository.save(machinePort);

    // Verify everything works together
    Optional<MachinePort> retrievedMachine = machineRepository.findById(domainMachine.getId());
    assertTrue(retrievedMachine.isPresent());

    MachinePort machine = retrievedMachine.get();
    CompositeComponentPort composite = machine.getComposite("composite");
    assertNotNull(composite);

    ComponentPort component = composite.getComponent("component");
    assertNotNull(component);

    assertEquals(domainComponent.getId().getIdString(), component.getId().getIdString());
  }

  @Test
  @DisplayName("Test port interface lifecycle management")
  void testPortInterfaceLifecycleManagement() {
    // Create a machine with composites and components
    MachinePort machine = machineService.createMachine("Lifecycle Machine", MachineType.WORKFLOW);

    CompositeComponentPort composite1 = componentService.createComposite("Lifecycle Composite 1");
    CompositeComponentPort composite2 = componentService.createComposite("Lifecycle Composite 2");

    ComponentPort component1 = componentService.createComponent("Lifecycle Component 1");
    ComponentPort component2 = componentService.createComponent("Lifecycle Component 2");

    composite1.addComponent("comp1", component1);
    composite2.addComponent("comp2", component2);

    machine.addComposite("comp1", composite1);
    machine.addComposite("comp2", composite2);

    // Activate each component independently
    component1.activate();
    assertEquals(LifecycleState.ACTIVE, component1.getLifecycleState());

    composite1.activate();
    assertEquals(LifecycleState.ACTIVE, composite1.getLifecycleState());

    // Start the machine
    machine.start();
    assertEquals(MachineState.RUNNING, machine.getMachineState());

    // Stop the machine
    machine.stop();
    assertEquals(MachineState.STOPPED, machine.getMachineState());

    // Terminate components
    component1.terminate();
    assertEquals(LifecycleState.TERMINATED, component1.getLifecycleState());

    composite1.terminate();
    assertEquals(LifecycleState.TERMINATED, composite1.getLifecycleState());
  }
}
