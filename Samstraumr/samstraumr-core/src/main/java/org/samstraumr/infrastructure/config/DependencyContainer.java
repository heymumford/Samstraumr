/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Dependency injection container for the S8r framework
 */

package org.samstraumr.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.samstraumr.adapter.out.InMemoryComponentRepository;
import org.samstraumr.application.port.ComponentRepository;
import org.samstraumr.application.port.EventDispatcher;
import org.samstraumr.application.port.LoggerPort;
import org.samstraumr.application.port.MachineRepository;
import org.samstraumr.application.service.ComponentService;
import org.samstraumr.application.service.DataFlowService;
import org.samstraumr.application.service.MachineService;
import org.samstraumr.domain.component.monitoring.MonitoringFactory;
import org.samstraumr.domain.component.pattern.PatternFactory;
import org.samstraumr.domain.event.ComponentConnectionEvent;
import org.samstraumr.domain.event.ComponentCreatedEvent;
import org.samstraumr.domain.event.ComponentDataEvent;
import org.samstraumr.domain.event.ComponentStateChangedEvent;
import org.samstraumr.domain.event.MachineStateChangedEvent;
import org.samstraumr.infrastructure.event.DataFlowEventHandler;
import org.samstraumr.infrastructure.event.InMemoryEventDispatcher;
import org.samstraumr.infrastructure.event.LoggingEventHandler;
import org.samstraumr.infrastructure.logging.LoggerFactory;
import org.samstraumr.infrastructure.persistence.InMemoryMachineRepository;

/**
 * Dependency injection container for the S8r framework.
 *
 * <p>This class provides a simple dependency injection container that creates and manages instances
 * of framework components, ensuring proper wiring between layers according to Clean Architecture
 * principles.
 */
public class DependencyContainer {
  private final Map<Class<?>, Object> instances = new HashMap<>();
  private final Configuration config;
  private static final DependencyContainer instance = new DependencyContainer();

  /** Private constructor to enforce singleton pattern. */
  private DependencyContainer() {
    this.config = Configuration.getInstance();
    initialize();
  }

  /**
   * Gets the singleton instance.
   *
   * @return The DependencyContainer instance
   */
  public static DependencyContainer getInstance() {
    return instance;
  }

  /** Initializes the container with standard dependencies. */
  private void initialize() {
    // Set up logger first
    setupLogger();

    // Set up event system
    setupEventSystem();

    // Create repositories
    setupRepositories();

    // Create services
    setupServices();
  }

  /** Sets up the logger based on configuration. */
  private void setupLogger() {
    String logImpl = config.get("log.implementation", "SLF4J");

    if ("CONSOLE".equalsIgnoreCase(logImpl)) {
      LoggerFactory.setImplementation(LoggerFactory.LoggerImplementation.CONSOLE);
    } else {
      LoggerFactory.setImplementation(LoggerFactory.LoggerImplementation.SLF4J);
    }

    LoggerPort logger = LoggerFactory.getLogger(DependencyContainer.class);
    logger.info("Initialized logger with implementation: " + logImpl);

    register(LoggerPort.class, logger);
  }

  /** Sets up repositories based on configuration. */
  private void setupRepositories() {
    String repoImpl = config.get("repository.implementation", "IN_MEMORY");
    LoggerPort logger = get(LoggerPort.class);

    if ("IN_MEMORY".equalsIgnoreCase(repoImpl)) {
      // Create in-memory repositories
      InMemoryComponentRepository componentRepository = new InMemoryComponentRepository();
      InMemoryMachineRepository machineRepository = new InMemoryMachineRepository();

      register(ComponentRepository.class, componentRepository);
      register(MachineRepository.class, machineRepository);

      logger.info("Initialized repositories with IN_MEMORY implementation");
    } else {
      // Default to in-memory if not recognized
      InMemoryComponentRepository componentRepository = new InMemoryComponentRepository();
      InMemoryMachineRepository machineRepository = new InMemoryMachineRepository();

      register(ComponentRepository.class, componentRepository);
      register(MachineRepository.class, machineRepository);

      logger.warn("Unknown repository implementation: " + repoImpl + ", using IN_MEMORY instead");
    }
  }

  /** Sets up services with their dependencies. */
  private void setupServices() {
    LoggerPort logger = get(LoggerPort.class);
    ComponentRepository componentRepository = get(ComponentRepository.class);
    MachineRepository machineRepository = get(MachineRepository.class);
    EventDispatcher eventDispatcher = get(EventDispatcher.class);
    DataFlowEventHandler dataFlowHandler = get(DataFlowEventHandler.class);

    // Create ComponentService
    ComponentService componentService =
        new ComponentService(componentRepository, logger, eventDispatcher);
    register(ComponentService.class, componentService);

    // Create MachineService
    MachineService machineService =
        new MachineService(machineRepository, componentRepository, logger);
    register(MachineService.class, machineService);

    // Create DataFlowService
    DataFlowService dataFlowService =
        new DataFlowService(componentRepository, dataFlowHandler, eventDispatcher, logger);
    register(DataFlowService.class, dataFlowService);

    // Create PatternFactory
    PatternFactory patternFactory = new PatternFactory(dataFlowService);
    register(PatternFactory.class, patternFactory);

    // Create MonitoringFactory
    MonitoringFactory monitoringFactory = new MonitoringFactory(dataFlowService);
    register(MonitoringFactory.class, monitoringFactory);

    logger.info("Initialized services");
  }

  /** Sets up the event system. */
  private void setupEventSystem() {
    LoggerPort logger = get(LoggerPort.class);

    // Create event dispatcher
    InMemoryEventDispatcher eventDispatcher = new InMemoryEventDispatcher(logger);
    register(EventDispatcher.class, eventDispatcher);

    // Create event handlers
    LoggingEventHandler loggingHandler = new LoggingEventHandler(logger);
    DataFlowEventHandler dataFlowHandler = new DataFlowEventHandler(logger);
    register(LoggingEventHandler.class, loggingHandler);
    register(DataFlowEventHandler.class, dataFlowHandler);

    // Register handlers with the dispatcher
    eventDispatcher.registerHandler(
        ComponentCreatedEvent.class, loggingHandler.componentCreatedHandler());
    eventDispatcher.registerHandler(
        ComponentStateChangedEvent.class, loggingHandler.componentStateChangedHandler());
    eventDispatcher.registerHandler(
        ComponentConnectionEvent.class, loggingHandler.componentConnectionHandler());
    eventDispatcher.registerHandler(
        MachineStateChangedEvent.class, loggingHandler.machineStateChangedHandler());
    eventDispatcher.registerHandler(ComponentDataEvent.class, dataFlowHandler.dataEventHandler());

    logger.info("Initialized event system with {} event handlers", 5);
  }

  /**
   * Registers an instance for a specific type.
   *
   * @param <T> The type
   * @param type The class of the type
   * @param instance The instance to register
   */
  public <T> void register(Class<T> type, T instance) {
    instances.put(type, instance);
  }

  /**
   * Gets an instance of the specified type.
   *
   * @param <T> The type
   * @param type The class of the type
   * @return The instance, or null if not found
   */
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    return (T) instances.get(type);
  }

  /** Resets the container, clearing all registered instances. */
  public void reset() {
    instances.clear();
    initialize();
  }
}
