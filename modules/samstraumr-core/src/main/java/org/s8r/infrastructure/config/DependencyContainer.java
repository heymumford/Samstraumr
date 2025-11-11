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

package org.s8r.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.s8r.application.port.ComponentRepository;
import org.s8r.application.port.DataFlowEventPort;
import org.s8r.application.port.EventDispatcher;
import org.s8r.application.port.EventHandler;
import org.s8r.application.port.LegacyAdapterResolver;
import org.s8r.application.port.LoggerFactory;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.MachineRepository;
import org.s8r.application.port.S8rFacade;
import org.s8r.application.port.ServiceFactory;
import org.s8r.application.service.ComponentService;
import org.s8r.application.service.DataFlowService;
import org.s8r.application.service.MachineService;
import org.s8r.domain.component.monitoring.MonitoringFactory;
import org.s8r.domain.component.pattern.PatternFactory;
import org.s8r.domain.event.ComponentConnectionEvent;
import org.s8r.domain.event.ComponentCreated;
import org.s8r.domain.event.ComponentCreatedEvent;
import org.s8r.domain.event.ComponentDataEvent;
import org.s8r.domain.event.ComponentStateChangedEvent;
import org.s8r.domain.event.MachineStateChangedEvent;
import org.s8r.infrastructure.event.DataFlowEventAdapter;
import org.s8r.infrastructure.event.DataFlowEventHandler;
import org.s8r.infrastructure.event.InMemoryEventDispatcher;
import org.s8r.infrastructure.event.LoggingEventHandler;
import org.s8r.infrastructure.logging.S8rLoggerFactory;
import org.s8r.infrastructure.persistence.InMemoryComponentRepository;
import org.s8r.infrastructure.persistence.InMemoryMachineRepository;

/**
 * Dependency injection container for the S8r framework.
 *
 * <p>This class provides a simple dependency injection container that creates and manages instances
 * of framework components, ensuring proper wiring between layers according to Clean Architecture
 * principles.
 *
 * <p>It implements the ServiceFactory interface to provide a clean abstraction for client code,
 * allowing access to services without direct dependency on this infrastructure component.
 */
public class DependencyContainer implements ServiceFactory {
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
    // Set up configuration port first
    setupConfiguration();

    // Set up logger next
    setupLogger();

    // Set up validation port
    setupValidation();

    // Set up notification port
    setupNotification();

    // Set up security port
    setupSecurity();

    // Set up event system
    setupEventSystem();

    // Create repositories
    setupRepositories();

    // Set up legacy adapters
    setupLegacyAdapters();

    // Create services
    setupServices();
  }

  /** Sets up the configuration port. */
  private void setupConfiguration() {
    // Create the configuration adapter
    ConfigurationAdapter configurationAdapter = new ConfigurationAdapter();

    // Register it as implementation of the port interface
    register(org.s8r.application.port.ConfigurationPort.class, configurationAdapter);
  }

  /** Sets up the validation port. */
  private void setupValidation() {
    LoggerPort logger = get(LoggerPort.class);

    // Create the validation adapter
    org.s8r.infrastructure.validation.ValidationAdapter validationAdapter =
        new org.s8r.infrastructure.validation.ValidationAdapter(logger);

    // Register it as implementation of the port interface
    register(org.s8r.application.port.ValidationPort.class, validationAdapter);

    logger.debug("Validation port initialized");
  }

  /** Sets up the notification port. */
  private void setupNotification() {
    LoggerPort logger = get(LoggerPort.class);
    org.s8r.application.port.ConfigurationPort configurationPort =
        get(org.s8r.application.port.ConfigurationPort.class);

    // Create the notification adapter
    org.s8r.infrastructure.notification.NotificationAdapter notificationAdapter =
        new org.s8r.infrastructure.notification.NotificationAdapter();

    // Register it as implementation of the port interface
    register(org.s8r.application.port.NotificationPort.class, notificationAdapter);

    logger.debug("Notification port initialized");
  }

  /** Sets up the security port. */
  private void setupSecurity() {
    LoggerPort logger = get(LoggerPort.class);

    // Create the security adapter
    org.s8r.infrastructure.security.InMemorySecurityAdapter securityAdapter =
        new org.s8r.infrastructure.security.InMemorySecurityAdapter(logger);

    // Register it as implementation of the port interface
    register(org.s8r.application.port.SecurityPort.class, securityAdapter);

    // Create the security service
    org.s8r.application.service.SecurityService securityService =
        new org.s8r.application.service.SecurityService(securityAdapter, logger);
    register(org.s8r.application.service.SecurityService.class, securityService);

    // Initialize the security system
    securityAdapter.initialize();

    logger.debug("Security port initialized");
  }

  /** Sets up legacy adapters. */
  private void setupLegacyAdapters() {
    LoggerPort logger = get(LoggerPort.class);

    // Create and register the adapter resolver
    LegacyAdapterFactory adapterFactory = LegacyAdapterFactory.getInstance();
    register(LegacyAdapterResolver.class, adapterFactory);

    // Create reflective adapter factory
    try {
      org.s8r.adapter.ReflectiveAdapterFactory reflectiveFactory =
          new org.s8r.adapter.ReflectiveAdapterFactory(logger);

      // Register reflective converters as the primary implementation
      register(
          org.s8r.domain.identity.LegacyEnvironmentConverter.class,
          reflectiveFactory.getTubeEnvironmentConverter());
      register(
          org.s8r.domain.identity.LegacyIdentityConverter.class,
          reflectiveFactory.getTubeIdentityConverter());

      logger.info("Initialized reflective legacy adapters");
    } catch (Exception e) {
      // Fall back to direct implementation if reflection fails
      logger.warn(
          "Failed to initialize reflective adapters, falling back to direct implementation: {}",
          e.getMessage());

      // Register the specific converter implementations
      register(
          org.s8r.domain.identity.LegacyEnvironmentConverter.class,
          adapterFactory.getTubeEnvironmentConverter());
      register(
          org.s8r.domain.identity.LegacyIdentityConverter.class,
          adapterFactory.getTubeIdentityConverter());
    }

    logger.info("Initialized legacy adapters");
  }

  /** Sets up the logger based on configuration. */
  private void setupLogger() {
    // Get the ConfigurationPort from the container
    org.s8r.application.port.ConfigurationPort configurationPort =
        get(org.s8r.application.port.ConfigurationPort.class);

    String logImpl = configurationPort.getString("log.implementation", "SLF4J");

    // Create and configure the logger factory
    S8rLoggerFactory loggerFactory = S8rLoggerFactory.getInstance();

    if ("CONSOLE".equalsIgnoreCase(logImpl)) {
      loggerFactory.setImplementation(S8rLoggerFactory.LoggerImplementation.CONSOLE);
    } else {
      loggerFactory.setImplementation(S8rLoggerFactory.LoggerImplementation.SLF4J);
    }

    // Register the logger factory as implementation of the application port
    register(LoggerFactory.class, loggerFactory);

    // Get a logger instance for this class
    LoggerPort logger = loggerFactory.getLogger(DependencyContainer.class);
    logger.info("Initialized logger with implementation: " + logImpl);

    register(LoggerPort.class, logger);
  }

  /** Sets up repositories based on configuration. */
  private void setupRepositories() {
    // Get the ConfigurationPort from the container
    org.s8r.application.port.ConfigurationPort configurationPort =
        get(org.s8r.application.port.ConfigurationPort.class);

    String repoImpl = configurationPort.getString("repository.implementation", "IN_MEMORY");
    LoggerPort logger = get(LoggerPort.class);

    if ("IN_MEMORY".equalsIgnoreCase(repoImpl)) {
      // Create in-memory repositories with port interface support
      InMemoryComponentRepository componentRepository = new InMemoryComponentRepository(logger);
      InMemoryMachineRepository machineRepository = new InMemoryMachineRepository(logger);

      register(ComponentRepository.class, componentRepository);
      register(MachineRepository.class, machineRepository);

      // Create and register the port adapter factory
      PortAdapterFactory portAdapterFactory = new PortAdapterFactory(logger);
      register(PortAdapterFactory.class, portAdapterFactory);

      logger.info("Initialized repositories with IN_MEMORY implementation and port adapters");
    } else {
      // Default to in-memory if not recognized
      InMemoryComponentRepository componentRepository = new InMemoryComponentRepository(logger);
      InMemoryMachineRepository machineRepository = new InMemoryMachineRepository(logger);

      register(ComponentRepository.class, componentRepository);
      register(MachineRepository.class, machineRepository);

      // Create and register the port adapter factory
      PortAdapterFactory portAdapterFactory = new PortAdapterFactory(logger);
      register(PortAdapterFactory.class, portAdapterFactory);

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
    org.s8r.application.port.ConfigurationPort configurationPort =
        get(org.s8r.application.port.ConfigurationPort.class);
    org.s8r.application.port.ValidationPort validationPort =
        get(org.s8r.application.port.ValidationPort.class);
    org.s8r.application.port.NotificationPort notificationPort =
        get(org.s8r.application.port.NotificationPort.class);

    // Create ConfigurationService
    org.s8r.application.service.ConfigurationService configurationService =
        new org.s8r.application.service.ConfigurationService(configurationPort, logger);
    register(org.s8r.application.service.ConfigurationService.class, configurationService);

    // Create ValidationService
    org.s8r.application.service.ValidationService validationService =
        new org.s8r.application.service.ValidationService(validationPort, logger);
    register(org.s8r.application.service.ValidationService.class, validationService);

    // Create NotificationService
    org.s8r.application.service.NotificationService notificationService =
        new org.s8r.application.service.NotificationService(notificationPort, logger);
    register(org.s8r.application.service.NotificationService.class, notificationService);

    // Create EventPublisherAdapter
    org.s8r.infrastructure.event.EventPublisherAdapter eventPublisherAdapter =
        new org.s8r.infrastructure.event.EventPublisherAdapter(
            eventDispatcher, componentRepository, logger);
    register(org.s8r.application.port.EventPublisherPort.class, eventPublisherAdapter);

    // Create ComponentService
    ComponentService componentService =
        new ComponentService(componentRepository, logger, eventPublisherAdapter);
    register(ComponentService.class, componentService);

    // Create MachineFactoryAdapter
    org.s8r.adapter.MachineFactoryAdapter machineFactoryAdapter =
        new org.s8r.adapter.MachineFactoryAdapter(logger);
    register(org.s8r.domain.component.port.MachineFactoryPort.class, machineFactoryAdapter);

    // Create MachineService
    MachineService machineService =
        new MachineService(machineRepository, componentRepository, machineFactoryAdapter, logger);
    register(MachineService.class, machineService);

    // Create DataFlowService
    org.s8r.application.port.DataFlowEventPort dataFlowEventPort = get(org.s8r.application.port.DataFlowEventPort.class);
    DataFlowService dataFlowService =
        new DataFlowService(componentRepository, dataFlowEventPort, eventPublisherAdapter, logger);
    register(DataFlowService.class, dataFlowService);

    // Create PatternFactory
    PatternFactory patternFactory = new PatternFactory(dataFlowService);
    register(PatternFactory.class, patternFactory);

    // Create MonitoringFactory
    MonitoringFactory monitoringFactory = new MonitoringFactory(dataFlowService);
    register(MonitoringFactory.class, monitoringFactory);

    // Set up initialization services
    setupInitializationServices();

    // We'll register the S8rFacade implementation later through registerFramework()

    logger.info("Initialized services");
  }

  /** Sets up project initialization services. */
  private void setupInitializationServices() {
    LoggerPort logger = get(LoggerPort.class);

    // Create project initialization infrastructure adapter
    org.s8r.infrastructure.initialization.FileSystemProjectInitializer initializer =
        new org.s8r.infrastructure.initialization.FileSystemProjectInitializer(logger);
    register(org.s8r.application.port.ProjectInitializationPort.class, initializer);

    // Create project initialization service
    org.s8r.application.service.ProjectInitializationService initService =
        new org.s8r.application.service.ProjectInitializationService(initializer, logger);
    register(org.s8r.application.service.ProjectInitializationService.class, initService);

    logger.info("Initialized project initialization services");
  }

  /** Sets up the event system. */
  private void setupEventSystem() {
    LoggerPort logger = get(LoggerPort.class);

    // Create event dispatcher
    InMemoryEventDispatcher eventDispatcher = new InMemoryEventDispatcher(logger);
    register(EventDispatcher.class, eventDispatcher);

    // Create event handlers
    LoggingEventHandler loggingHandler = new LoggingEventHandler(logger);
    register(LoggingEventHandler.class, loggingHandler);
    
    // Create data flow event adapter
    DataFlowEventAdapter dataFlowEventAdapter = new DataFlowEventAdapter(logger);
    register(org.s8r.application.port.DataFlowEventPort.class, dataFlowEventAdapter);
    
    // Get the data flow event handler from the adapter
    DataFlowEventHandler dataFlowHandler = dataFlowEventAdapter.getDataFlowHandler();
    register(DataFlowEventHandler.class, dataFlowHandler);

    // Register handlers with the dispatcher
    eventDispatcher.registerHandler(
        "component.created", loggingHandler.componentCreatedHandler());

    // Register the new-style ComponentCreated event handler (without Event suffix)
    // This supports the new Clean Architecture event model
    eventDispatcher.registerHandler(
        "component.created.v2", loggingHandler.componentCreatedHandlerNew());
    logger.info("Registered handler for new-style ComponentCreated events");

    eventDispatcher.registerHandler(
        "component.state.changed", loggingHandler.componentStateChangedHandler());
    eventDispatcher.registerHandler(
        "component.connection", loggingHandler.componentConnectionHandler());
    eventDispatcher.registerHandler(
        "machine.state.changed", loggingHandler.machineStateChangedHandler());
    eventDispatcher.registerHandler("data.flow", dataFlowHandler.dataEventHandler());

    logger.info("Initialized event system with hierarchical event propagation");
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

  /**
   * Implementation of ServiceFactory.getService.
   *
   * @param <T> The service type
   * @param serviceType The class of the service type
   * @return The service instance
   */
  @Override
  public <T> T getService(Class<T> serviceType) {
    return get(serviceType);
  }

  /**
   * Implementation of ServiceFactory.getFramework.
   *
   * @return The framework facade
   */
  @Override
  public S8rFacade getFramework() {
    return get(S8rFacade.class);
  }

  /**
   * Implementation of ServiceFactory.getLogger.
   *
   * @param clazz The class requesting the logger
   * @return The logger
   */
  @Override
  public LoggerPort getLogger(Class<?> clazz) {
    LoggerFactory loggerFactory = get(LoggerFactory.class);
    return loggerFactory.getLogger(clazz);
  }

  /**
   * Registers the framework facade implementation. This is called from the framework itself to
   * avoid circular dependencies.
   *
   * @param framework The framework facade implementation
   */
  public void registerFramework(S8rFacade framework) {
    if (framework != null) {
      register(S8rFacade.class, framework);
      LoggerPort logger = get(LoggerPort.class);
      logger.info("Framework facade registered");
    }
  }

  /** Resets the container, clearing all registered instances. */
  public void reset() {
    instances.clear();
    initialize();
  }

  /**
   * Helper method to register an event handler for backward compatibility. Uses reflection to work
   * around generic type constraints during the migration.
   *
   * @param dispatcher The event dispatcher
   * @param eventClass The event class to register for
   * @param handler The handler to register
   * @param logger Logger for error reporting
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void registerHandlerForCompatibility(
      EventDispatcher dispatcher,
      Class<?> eventClass,
      org.s8r.application.port.EventHandler handler,
      LoggerPort logger) {
    try {
      // Use reflection to bypass the generic type checking
      java.lang.reflect.Method registerMethod =
          InMemoryEventDispatcher.class.getMethod(
              "registerHandler", Class.class, org.s8r.application.port.EventHandler.class);

      registerMethod.invoke(dispatcher, eventClass, handler);
    } catch (Exception e) {
      logger.error("Failed to register handler for compatibility: {}", e.getMessage(), e);
    }
  }
}
