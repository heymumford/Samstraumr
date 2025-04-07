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

package org.s8r.infrastructure.storage;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.storage.StoragePort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * In-memory implementation of the StoragePort interface.
 *
 * <p>This adapter provides a simple in-memory implementation of the StoragePort interface for
 * development and testing purposes. It stores all data in memory and will lose data when the
 * application is restarted. This implementation supports all of the functionality defined in the
 * StoragePort interface, including containers, objects, and key-value pairs.
 */
public class InMemoryStorageAdapter implements StoragePort {
  private final Map<String, InMemoryContainer> containers = new ConcurrentHashMap<>();
  private final Map<String, Map<String, InMemoryObject>> objects = new ConcurrentHashMap<>();
  private final Map<String, KeyValueEntry> keyValueStore = new ConcurrentHashMap<>();
  private final LoggerPort logger;
  private final ScheduledExecutorService cleanupScheduler;
  private final MessageDigest messageDigest;

  /**
   * Constructs an InMemoryStorageAdapter with the given LoggerPort.
   *
   * @param logger The LoggerPort to use for logging
   */
  public InMemoryStorageAdapter(LoggerPort logger) {
    this.logger = logger;
    this.cleanupScheduler = Executors.newScheduledThreadPool(1);
    try {
      this.messageDigest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Failed to initialize message digest", e);
    }
    
    // Schedule cleanup task for expired items
    cleanupScheduler.scheduleAtFixedRate(
        this::cleanupExpiredItems, 1, 1, TimeUnit.MINUTES);
    
    logger.info("InMemoryStorageAdapter initialized");
  }

  @Override
  public ContainerResult createContainer(String containerName) {
    return createContainer(containerName, new ContainerOptions() {
      @Override
      public boolean isPublicAccess() {
        return false;
      }

      @Override
      public Map<String, String> getMetadata() {
        return Collections.emptyMap();
      }
    });
  }

  @Override
  public ContainerResult createContainer(String containerName, ContainerOptions options) {
    logger.debug("Creating container: {}", containerName);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to create container - name is null or empty");
      return ContainerResult.failure("Container name cannot be null or empty", "INVALID_NAME");
    }
    
    if (containers.containsKey(containerName)) {
      logger.warn("Failed to create container {} - already exists", containerName);
      return ContainerResult.failure("Container already exists", "CONTAINER_EXISTS");
    }
    
    InMemoryContainer container = new InMemoryContainer(
        containerName,
        Instant.now(),
        options.isPublicAccess(),
        new HashMap<>(options.getMetadata()),
        Optional.empty());
    
    containers.put(containerName, container);
    objects.put(containerName, new ConcurrentHashMap<>());
    
    logger.info("Container created successfully: {}", containerName);
    return ContainerResult.success("Container created successfully", container);
  }

  @Override
  public OperationResult deleteContainer(String containerName, boolean deleteContents) {
    logger.debug("Deleting container: {} (deleteContents={})", containerName, deleteContents);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to delete container - name is null or empty");
      return OperationResult.failure("Container name cannot be null or empty", "INVALID_NAME");
    }
    
    InMemoryContainer container = containers.get(containerName);
    if (container == null) {
      logger.warn("Failed to delete container {} - not found", containerName);
      return OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (!deleteContents && !containerObjects.isEmpty()) {
      logger.warn("Failed to delete container {} - not empty", containerName);
      return OperationResult.failure(
          "Container is not empty and deleteContents=false", "CONTAINER_NOT_EMPTY");
    }
    
    containers.remove(containerName);
    objects.remove(containerName);
    
    logger.info("Container deleted successfully: {}", containerName);
    return OperationResult.success("Container deleted successfully");
  }

  @Override
  public ContainerResult getContainer(String containerName) {
    logger.debug("Getting container: {}", containerName);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to get container - name is null or empty");
      return ContainerResult.failure("Container name cannot be null or empty", "INVALID_NAME");
    }
    
    InMemoryContainer container = containers.get(containerName);
    if (container == null) {
      logger.warn("Failed to get container {} - not found", containerName);
      return ContainerResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    logger.debug("Container retrieved successfully: {}", containerName);
    return ContainerResult.success("Container retrieved successfully", container);
  }

  @Override
  public ContainerListResult listContainers(Optional<String> prefix, Optional<Integer> maxResults) {
    logger.debug("Listing containers: prefix={}, maxResults={}", 
        prefix.orElse("none"), maxResults.orElse(-1));
    
    List<Container> containerList = containers.values().stream()
        .filter(container -> !prefix.isPresent() || container.getName().startsWith(prefix.get()))
        .collect(Collectors.toList());
    
    boolean hasMoreResults = false;
    if (maxResults.isPresent() && containerList.size() > maxResults.get()) {
      hasMoreResults = true;
      containerList = containerList.subList(0, maxResults.get());
    }
    
    logger.debug("Listed {} containers", containerList.size());
    return ContainerListResult.success(
        "Containers listed successfully", containerList, hasMoreResults);
  }

  @Override
  public ObjectResult storeObject(String containerName, String key, byte[] data) {
    logger.debug("Storing object: container={}, key={}, size={} bytes", 
        containerName, key, data.length);
    
    return storeObject(containerName, key, data, new ObjectOptions() {
      @Override
      public Optional<String> getContentType() {
        return Optional.empty();
      }

      @Override
      public Map<String, String> getMetadata() {
        return Collections.emptyMap();
      }

      @Override
      public Optional<EncryptionMode> getEncryptionMode() {
        return Optional.empty();
      }

      @Override
      public Optional<Duration> getTtl() {
        return Optional.empty();
      }
    });
  }

  @Override
  public ObjectResult storeObject(String containerName, String key, byte[] data, ObjectOptions options) {
    logger.debug("Storing object with options: container={}, key={}", containerName, key);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to store object - container name is null or empty");
      return ObjectResult.failure("Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to store object - key is null or empty");
      return ObjectResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    if (data == null) {
      logger.warn("Failed to store object {}/{} - data is null", containerName, key);
      return ObjectResult.failure("Object data cannot be null", "INVALID_DATA");
    }
    
    InMemoryContainer container = containers.get(containerName);
    if (container == null) {
      logger.warn("Failed to store object {}/{} - container not found", containerName, key);
      return ObjectResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    
    String contentHash = calculateHash(data);
    Optional<Instant> expiresAt = options.getTtl().map(ttl -> Instant.now().plus(ttl));
    
    InMemoryObject object = new InMemoryObject(
        key,
        data.length,
        Instant.now(),
        options.getContentType(),
        new HashMap<>(options.getMetadata()),
        data,
        contentHash,
        expiresAt);
    
    containerObjects.put(key, object);
    
    // If TTL is set, schedule cleanup
    options.getTtl().ifPresent(ttl -> {
      cleanupScheduler.schedule(
          () -> removeExpiredObject(containerName, key),
          ttl.toMillis(),
          TimeUnit.MILLISECONDS);
    });
    
    logger.debug("Object stored successfully: {}/{}", containerName, key);
    return ObjectResult.success("Object stored successfully", object);
  }

  @Override
  public ObjectResult storeObject(String containerName, String key, InputStream inputStream, long size) {
    logger.debug("Storing object from stream: container={}, key={}, size={} bytes", 
        containerName, key, size);
    
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      byte[] data = outputStream.toByteArray();
      
      return storeObject(containerName, key, data);
    } catch (IOException e) {
      logger.error("Failed to store object {}/{} from stream: {}", 
          containerName, key, e.getMessage(), e);
      return ObjectResult.failure(
          "Failed to read from input stream: " + e.getMessage(), "IO_ERROR");
    }
  }

  @Override
  public ObjectResult storeObject(
      String containerName, String key, InputStream inputStream, long size, ObjectOptions options) {
    logger.debug("Storing object from stream with options: container={}, key={}", containerName, key);
    
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
      byte[] data = outputStream.toByteArray();
      
      return storeObject(containerName, key, data, options);
    } catch (IOException e) {
      logger.error("Failed to store object {}/{} from stream with options: {}", 
          containerName, key, e.getMessage(), e);
      return ObjectResult.failure(
          "Failed to read from input stream: " + e.getMessage(), "IO_ERROR");
    }
  }

  @Override
  public ObjectResult getObject(String containerName, String key) {
    logger.debug("Getting object: container={}, key={}", containerName, key);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to get object - container name is null or empty");
      return ObjectResult.failure("Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to get object - key is null or empty");
      return ObjectResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to get object {}/{} - container not found", containerName, key);
      return ObjectResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    InMemoryObject object = containerObjects.get(key);
    if (object == null) {
      logger.warn("Failed to get object {}/{} - not found", containerName, key);
      return ObjectResult.failure("Object not found", "OBJECT_NOT_FOUND");
    }
    
    // Check if object has expired
    if (object.getExpiresAt().isPresent() && object.getExpiresAt().get().isBefore(Instant.now())) {
      containerObjects.remove(key);
      logger.debug("Object {}/{} has expired", containerName, key);
      return ObjectResult.failure("Object has expired", "OBJECT_EXPIRED");
    }
    
    logger.debug("Object retrieved successfully: {}/{}", containerName, key);
    return ObjectResult.success("Object retrieved successfully", object);
  }

  @Override
  public OperationResult getObject(String containerName, String key, OutputStream outputStream) {
    logger.debug("Getting object to stream: container={}, key={}", containerName, key);
    
    ObjectResult result = getObject(containerName, key);
    if (!result.isSuccessful()) {
      return OperationResult.failure(result.getMessage(), result.getErrorCode().orElse("ERROR"));
    }
    
    try {
      outputStream.write(result.getObject().get().getData());
      outputStream.flush();
      
      logger.debug("Object written to stream successfully: {}/{}", containerName, key);
      return OperationResult.success("Object written to stream successfully");
    } catch (IOException e) {
      logger.error("Failed to write object {}/{} to stream: {}", 
          containerName, key, e.getMessage(), e);
      return OperationResult.failure(
          "Failed to write to output stream: " + e.getMessage(), "IO_ERROR");
    }
  }

  @Override
  public OperationResult deleteObject(String containerName, String key) {
    logger.debug("Deleting object: container={}, key={}", containerName, key);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to delete object - container name is null or empty");
      return OperationResult.failure("Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to delete object - key is null or empty");
      return OperationResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to delete object {}/{} - container not found", containerName, key);
      return OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    InMemoryObject object = containerObjects.remove(key);
    if (object == null) {
      logger.warn("Failed to delete object {}/{} - not found", containerName, key);
      return OperationResult.failure("Object not found", "OBJECT_NOT_FOUND");
    }
    
    logger.debug("Object deleted successfully: {}/{}", containerName, key);
    return OperationResult.success("Object deleted successfully");
  }

  @Override
  public ObjectMetadataResult getObjectMetadata(String containerName, String key) {
    logger.debug("Getting object metadata: container={}, key={}", containerName, key);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to get object metadata - container name is null or empty");
      return ObjectMetadataResult.failure(
          "Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to get object metadata - key is null or empty");
      return ObjectMetadataResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to get object metadata {}/{} - container not found", containerName, key);
      return ObjectMetadataResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    InMemoryObject object = containerObjects.get(key);
    if (object == null) {
      logger.warn("Failed to get object metadata {}/{} - not found", containerName, key);
      return ObjectMetadataResult.failure("Object not found", "OBJECT_NOT_FOUND");
    }
    
    // Check if object has expired
    if (object.getExpiresAt().isPresent() && object.getExpiresAt().get().isBefore(Instant.now())) {
      containerObjects.remove(key);
      logger.debug("Object {}/{} has expired", containerName, key);
      return ObjectMetadataResult.failure("Object has expired", "OBJECT_EXPIRED");
    }
    
    logger.debug("Object metadata retrieved successfully: {}/{}", containerName, key);
    return ObjectMetadataResult.success(
        "Object metadata retrieved successfully",
        object.getContentType(),
        Optional.of(object.getSize()),
        Optional.of(object.getLastModified()),
        object.getMetadata(),
        object.getContentHash());
  }

  @Override
  public OperationResult updateObjectMetadata(
      String containerName, String key, Map<String, String> metadata, boolean replace) {
    logger.debug("Updating object metadata: container={}, key={}, replace={}", 
        containerName, key, replace);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to update object metadata - container name is null or empty");
      return OperationResult.failure(
          "Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to update object metadata - key is null or empty");
      return OperationResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    if (metadata == null) {
      logger.warn("Failed to update object metadata {}/{} - metadata is null", containerName, key);
      return OperationResult.failure("Metadata cannot be null", "INVALID_METADATA");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to update object metadata {}/{} - container not found", containerName, key);
      return OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    InMemoryObject object = containerObjects.get(key);
    if (object == null) {
      logger.warn("Failed to update object metadata {}/{} - not found", containerName, key);
      return OperationResult.failure("Object not found", "OBJECT_NOT_FOUND");
    }
    
    // Check if object has expired
    if (object.getExpiresAt().isPresent() && object.getExpiresAt().get().isBefore(Instant.now())) {
      containerObjects.remove(key);
      logger.debug("Object {}/{} has expired", containerName, key);
      return OperationResult.failure("Object has expired", "OBJECT_EXPIRED");
    }
    
    // Update metadata
    Map<String, String> newMetadata = new HashMap<>();
    if (!replace) {
      newMetadata.putAll(object.getMetadata());
    }
    newMetadata.putAll(metadata);
    
    // Replace the object with updated metadata
    InMemoryObject updatedObject = new InMemoryObject(
        object.getKey(),
        object.getSize(),
        object.getLastModified(),
        object.getContentType(),
        newMetadata,
        object.getData(),
        object.getContentHash().orElse(""),
        object.getExpiresAt());
    
    containerObjects.put(key, updatedObject);
    
    logger.debug("Object metadata updated successfully: {}/{}", containerName, key);
    return OperationResult.success("Object metadata updated successfully");
  }

  @Override
  public ObjectListResult listObjects(
      String containerName,
      Optional<String> prefix,
      Optional<String> delimiter,
      Optional<Integer> maxResults) {
    logger.debug("Listing objects: container={}, prefix={}, delimiter={}, maxResults={}", 
        containerName, 
        prefix.orElse("none"), 
        delimiter.orElse("none"), 
        maxResults.orElse(-1));
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to list objects - container name is null or empty");
      return ObjectListResult.failure(
          "Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to list objects - container {} not found", containerName);
      return ObjectListResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    // Filter by prefix and handle delimiters
    List<StorageObject> objectList = new ArrayList<>();
    Set<String> commonPrefixes = new HashSet<>();
    
    for (InMemoryObject object : containerObjects.values()) {
      String key = object.getKey();
      
      // Skip expired objects
      if (object.getExpiresAt().isPresent() && object.getExpiresAt().get().isBefore(Instant.now())) {
        continue;
      }
      
      // Apply prefix filter
      if (prefix.isPresent() && !key.startsWith(prefix.get())) {
        continue;
      }
      
      // Handle delimiter
      if (delimiter.isPresent() && !delimiter.get().isEmpty()) {
        String rest = prefix.isPresent() ? key.substring(prefix.get().length()) : key;
        int delimiterIndex = rest.indexOf(delimiter.get());
        
        if (delimiterIndex >= 0) {
          String commonPrefix = key.substring(0, 
              (prefix.isPresent() ? prefix.get().length() : 0) + delimiterIndex + 1);
          commonPrefixes.add(commonPrefix);
          continue;
        }
      }
      
      objectList.add(object);
    }
    
    // Apply max results and check if there are more results
    boolean hasMoreResults = false;
    if (maxResults.isPresent() && objectList.size() > maxResults.get()) {
      hasMoreResults = true;
      objectList = objectList.subList(0, maxResults.get());
    }
    
    List<String> commonPrefixList = new ArrayList<>(commonPrefixes);
    Collections.sort(commonPrefixList);
    
    logger.debug("Listed {} objects and {} common prefixes in container {}", 
        objectList.size(), commonPrefixList.size(), containerName);
    return ObjectListResult.success(
        "Objects listed successfully", objectList, hasMoreResults, commonPrefixList);
  }

  @Override
  public ObjectResult copyObject(
      String sourceContainer, String sourceKey, String destinationContainer, String destinationKey) {
    logger.debug("Copying object: {}/{} to {}/{}", 
        sourceContainer, sourceKey, destinationContainer, destinationKey);
    
    // Get the source object
    ObjectResult sourceResult = getObject(sourceContainer, sourceKey);
    if (!sourceResult.isSuccessful()) {
      return sourceResult; // Return the error result
    }
    
    StorageObject sourceObject = sourceResult.getObject().get();
    
    // Store the object in the destination
    return storeObject(
        destinationContainer,
        destinationKey,
        sourceObject.getData(),
        new ObjectOptions() {
          @Override
          public Optional<String> getContentType() {
            return sourceObject.getContentType();
          }

          @Override
          public Map<String, String> getMetadata() {
            return sourceObject.getMetadata();
          }

          @Override
          public Optional<EncryptionMode> getEncryptionMode() {
            return Optional.empty();
          }

          @Override
          public Optional<Duration> getTtl() {
            return Optional.empty();
          }
        });
  }

  @Override
  public ObjectResult moveObject(
      String sourceContainer, String sourceKey, String destinationContainer, String destinationKey) {
    logger.debug("Moving object: {}/{} to {}/{}", 
        sourceContainer, sourceKey, destinationContainer, destinationKey);
    
    // Copy the object
    ObjectResult copyResult = copyObject(
        sourceContainer, sourceKey, destinationContainer, destinationKey);
    if (!copyResult.isSuccessful()) {
      return copyResult; // Return the error result
    }
    
    // Delete the source object
    OperationResult deleteResult = deleteObject(sourceContainer, sourceKey);
    if (!deleteResult.isSuccessful()) {
      // If delete failed, try to delete the copy to avoid duplication
      deleteObject(destinationContainer, destinationKey);
      
      logger.warn("Failed to delete source object during move operation: {}/{} to {}/{}", 
          sourceContainer, sourceKey, destinationContainer, destinationKey);
      return ObjectResult.failure(
          "Failed to delete source object: " + deleteResult.getMessage(),
          deleteResult.getErrorCode().orElse("DELETE_ERROR"));
    }
    
    return copyResult;
  }

  @Override
  public UrlResult generatePresignedUrl(String containerName, String key, UrlOperation operation) {
    return generatePresignedUrl(containerName, key, operation, Duration.ofHours(1));
  }

  @Override
  public UrlResult generatePresignedUrl(
      String containerName, String key, UrlOperation operation, Duration expiration) {
    logger.debug("Generating presigned URL: container={}, key={}, operation={}, expiration={}", 
        containerName, key, operation, expiration);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to generate presigned URL - container name is null or empty");
      return UrlResult.failure(
          "Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to generate presigned URL - key is null or empty");
      return UrlResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to generate presigned URL - container {} not found", containerName);
      return UrlResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    if (operation == UrlOperation.GET || operation == UrlOperation.DELETE) {
      InMemoryObject object = containerObjects.get(key);
      if (object == null) {
        logger.warn("Failed to generate presigned URL - object {}/{} not found", containerName, key);
        return UrlResult.failure("Object not found", "OBJECT_NOT_FOUND");
      }
      
      // Check if object has expired
      if (object.getExpiresAt().isPresent() && object.getExpiresAt().get().isBefore(Instant.now())) {
        containerObjects.remove(key);
        logger.debug("Object {}/{} has expired", containerName, key);
        return UrlResult.failure("Object has expired", "OBJECT_EXPIRED");
      }
    }
    
    InMemoryContainer container = containers.get(containerName);
    if (!container.isPublicAccess() && operation == UrlOperation.GET) {
      logger.warn("Failed to generate presigned URL - container {} is not public", containerName);
      return UrlResult.failure(
          "Container is not public and operation is GET", "CONTAINER_NOT_PUBLIC");
    }
    
    // Build a simple URL-like string
    String url = String.format("memory://%s/%s?operation=%s", 
        containerName, key, operation.name());
    Instant expiresAt = Instant.now().plus(expiration);
    
    logger.debug("Presigned URL generated successfully for {}/{}", containerName, key);
    return UrlResult.success("Presigned URL generated successfully", url, expiresAt);
  }

  @Override
  public OperationResult setRetentionPolicy(String containerName, String key, Duration retentionPeriod) {
    logger.debug("Setting retention policy: container={}, key={}, period={}", 
        containerName, key, retentionPeriod);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to set retention policy - container name is null or empty");
      return OperationResult.failure(
          "Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to set retention policy - key is null or empty");
      return OperationResult.failure("Object key cannot be null or empty", "INVALID_KEY");
    }
    
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects == null) {
      logger.warn("Failed to set retention policy - container {} not found", containerName);
      return OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    InMemoryObject object = containerObjects.get(key);
    if (object == null) {
      logger.warn("Failed to set retention policy - object {}/{} not found", containerName, key);
      return OperationResult.failure("Object not found", "OBJECT_NOT_FOUND");
    }
    
    // Check if object has expired
    if (object.getExpiresAt().isPresent() && object.getExpiresAt().get().isBefore(Instant.now())) {
      containerObjects.remove(key);
      logger.debug("Object {}/{} has expired", containerName, key);
      return OperationResult.failure("Object has expired", "OBJECT_EXPIRED");
    }
    
    // In a real implementation, we would enforce retention policy
    // For this in-memory implementation, we just update metadata
    Map<String, String> newMetadata = new HashMap<>(object.getMetadata());
    newMetadata.put("retention-period", retentionPeriod.toString());
    newMetadata.put("retention-until", Instant.now().plus(retentionPeriod).toString());
    
    // Replace the object with updated metadata
    InMemoryObject updatedObject = new InMemoryObject(
        object.getKey(),
        object.getSize(),
        object.getLastModified(),
        object.getContentType(),
        newMetadata,
        object.getData(),
        object.getContentHash().orElse(""),
        object.getExpiresAt());
    
    containerObjects.put(key, updatedObject);
    
    logger.debug("Retention policy set successfully for {}/{}", containerName, key);
    return OperationResult.success("Retention policy set successfully");
  }

  @Override
  public OperationResult setContainerRetentionPolicy(String containerName, Duration retentionPeriod) {
    logger.debug("Setting container retention policy: container={}, period={}", 
        containerName, retentionPeriod);
    
    if (containerName == null || containerName.trim().isEmpty()) {
      logger.warn("Failed to set container retention policy - container name is null or empty");
      return OperationResult.failure(
          "Container name cannot be null or empty", "INVALID_CONTAINER_NAME");
    }
    
    InMemoryContainer container = containers.get(containerName);
    if (container == null) {
      logger.warn("Failed to set container retention policy - container {} not found", containerName);
      return OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND");
    }
    
    // Update the container with retention policy
    InMemoryContainer updatedContainer = new InMemoryContainer(
        container.getName(),
        container.getCreationTime(),
        container.isPublicAccess(),
        container.getMetadata(),
        Optional.of(retentionPeriod));
    
    containers.put(containerName, updatedContainer);
    
    logger.debug("Container retention policy set successfully for {}", containerName);
    return OperationResult.success("Container retention policy set successfully");
  }

  @Override
  public KvResult storeKeyValue(String key, String value) {
    logger.debug("Storing key-value pair: key={}", key);
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to store key-value pair - key is null or empty");
      return KvResult.failure("Key cannot be null or empty", "INVALID_KEY");
    }
    
    if (value == null) {
      logger.warn("Failed to store key-value pair - value is null");
      return KvResult.failure("Value cannot be null", "INVALID_VALUE");
    }
    
    KeyValueEntry entry = new KeyValueEntry(key, value, Optional.empty());
    keyValueStore.put(key, entry);
    
    logger.debug("Key-value pair stored successfully for key {}", key);
    return KvResult.success("Key-value pair stored successfully", value);
  }

  @Override
  public KvResult storeKeyValue(String key, String value, Duration ttl) {
    logger.debug("Storing key-value pair with TTL: key={}, ttl={}", key, ttl);
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to store key-value pair with TTL - key is null or empty");
      return KvResult.failure("Key cannot be null or empty", "INVALID_KEY");
    }
    
    if (value == null) {
      logger.warn("Failed to store key-value pair with TTL - value is null");
      return KvResult.failure("Value cannot be null", "INVALID_VALUE");
    }
    
    if (ttl == null) {
      logger.warn("Failed to store key-value pair with TTL - TTL is null");
      return KvResult.failure("TTL cannot be null", "INVALID_TTL");
    }
    
    Instant expiresAt = Instant.now().plus(ttl);
    KeyValueEntry entry = new KeyValueEntry(key, value, Optional.of(expiresAt));
    keyValueStore.put(key, entry);
    
    // Schedule cleanup
    cleanupScheduler.schedule(
        () -> removeExpiredKeyValue(key),
        ttl.toMillis(),
        TimeUnit.MILLISECONDS);
    
    logger.debug("Key-value pair with TTL stored successfully for key {}", key);
    return KvResult.success("Key-value pair stored successfully", value, Optional.of(expiresAt));
  }

  @Override
  public KvResult getKeyValue(String key) {
    logger.debug("Getting key-value pair: key={}", key);
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to get key-value pair - key is null or empty");
      return KvResult.failure("Key cannot be null or empty", "INVALID_KEY");
    }
    
    KeyValueEntry entry = keyValueStore.get(key);
    if (entry == null) {
      logger.debug("Key-value pair not found for key {}", key);
      return KvResult.failure("Key not found", "KEY_NOT_FOUND");
    }
    
    // Check if the entry has expired
    if (entry.getExpiresAt().isPresent() && entry.getExpiresAt().get().isBefore(Instant.now())) {
      keyValueStore.remove(key);
      logger.debug("Key-value pair has expired for key {}", key);
      return KvResult.failure("Key has expired", "KEY_EXPIRED");
    }
    
    logger.debug("Key-value pair retrieved successfully for key {}", key);
    return KvResult.success(
        "Key-value pair retrieved successfully", entry.getValue(), entry.getExpiresAt());
  }

  @Override
  public OperationResult deleteKeyValue(String key) {
    logger.debug("Deleting key-value pair: key={}", key);
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to delete key-value pair - key is null or empty");
      return OperationResult.failure("Key cannot be null or empty", "INVALID_KEY");
    }
    
    KeyValueEntry entry = keyValueStore.remove(key);
    if (entry == null) {
      logger.debug("Key-value pair not found for key {}", key);
      return OperationResult.failure("Key not found", "KEY_NOT_FOUND");
    }
    
    logger.debug("Key-value pair deleted successfully for key {}", key);
    return OperationResult.success("Key-value pair deleted successfully");
  }

  @Override
  public KvResult updateKeyValue(String key, Function<Optional<String>, String> transformFunction) {
    logger.debug("Updating key-value pair: key={}", key);
    
    if (key == null || key.trim().isEmpty()) {
      logger.warn("Failed to update key-value pair - key is null or empty");
      return KvResult.failure("Key cannot be null or empty", "INVALID_KEY");
    }
    
    if (transformFunction == null) {
      logger.warn("Failed to update key-value pair - transform function is null");
      return KvResult.failure("Transform function cannot be null", "INVALID_FUNCTION");
    }
    
    synchronized (keyValueStore) {
      KeyValueEntry entry = keyValueStore.get(key);
      
      // Check if the entry has expired
      if (entry != null && entry.getExpiresAt().isPresent() 
          && entry.getExpiresAt().get().isBefore(Instant.now())) {
        keyValueStore.remove(key);
        entry = null;
        logger.debug("Key-value pair has expired for key {}", key);
      }
      
      Optional<String> currentValue = Optional.ofNullable(entry).map(KeyValueEntry::getValue);
      String newValue = transformFunction.apply(currentValue);
      
      if (newValue == null) {
        logger.warn("Failed to update key-value pair - new value is null");
        return KvResult.failure("New value cannot be null", "INVALID_VALUE");
      }
      
      // Preserve expiration if it exists
      Optional<Instant> expiresAt = Optional.ofNullable(entry)
          .flatMap(KeyValueEntry::getExpiresAt);
      
      KeyValueEntry newEntry = new KeyValueEntry(key, newValue, expiresAt);
      keyValueStore.put(key, newEntry);
      
      logger.debug("Key-value pair updated successfully for key {}", key);
      return KvResult.success("Key-value pair updated successfully", newValue, expiresAt);
    }
  }

  @Override
  public KvListResult listKeys(Optional<String> prefix, Optional<Integer> maxResults) {
    logger.debug("Listing keys: prefix={}, maxResults={}", 
        prefix.orElse("none"), maxResults.orElse(-1));
    
    // Filter out expired entries
    List<String> keys = keyValueStore.entrySet().stream()
        .filter(entry -> {
          Optional<Instant> expiresAt = entry.getValue().getExpiresAt();
          return !expiresAt.isPresent() || !expiresAt.get().isBefore(Instant.now());
        })
        .map(Map.Entry::getKey)
        .filter(key -> !prefix.isPresent() || key.startsWith(prefix.get()))
        .collect(Collectors.toList());
    
    boolean hasMoreResults = false;
    if (maxResults.isPresent() && keys.size() > maxResults.get()) {
      hasMoreResults = true;
      keys = keys.subList(0, maxResults.get());
    }
    
    logger.debug("Listed {} keys", keys.size());
    return KvListResult.success("Keys listed successfully", keys, hasMoreResults);
  }

  /**
   * Shuts down the storage adapter and cleans up resources.
   */
  public void shutdown() {
    logger.info("Shutting down InMemoryStorageAdapter");
    cleanupScheduler.shutdown();
    try {
      if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
        cleanupScheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      cleanupScheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Cleans up expired items.
   */
  private void cleanupExpiredItems() {
    logger.debug("Running cleanup task for expired items");
    
    // Clean up expired objects
    for (Map.Entry<String, Map<String, InMemoryObject>> containerEntry : objects.entrySet()) {
      String containerName = containerEntry.getKey();
      Map<String, InMemoryObject> containerObjects = containerEntry.getValue();
      
      List<String> keysToRemove = containerObjects.entrySet().stream()
          .filter(entry -> {
            Optional<Instant> expiresAt = entry.getValue().getExpiresAt();
            return expiresAt.isPresent() && expiresAt.get().isBefore(Instant.now());
          })
          .map(Map.Entry::getKey)
          .collect(Collectors.toList());
      
      for (String key : keysToRemove) {
        containerObjects.remove(key);
        logger.debug("Removed expired object: {}/{}", containerName, key);
      }
    }
    
    // Clean up expired key-value pairs
    List<String> keysToRemove = keyValueStore.entrySet().stream()
        .filter(entry -> {
          Optional<Instant> expiresAt = entry.getValue().getExpiresAt();
          return expiresAt.isPresent() && expiresAt.get().isBefore(Instant.now());
        })
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    
    for (String key : keysToRemove) {
      keyValueStore.remove(key);
      logger.debug("Removed expired key-value pair: {}", key);
    }
    
    logger.debug("Cleanup task completed - removed {} objects and {} key-value pairs",
        keysToRemove.size(), keysToRemove.size());
  }

  /**
   * Removes an expired object.
   *
   * @param containerName The container name
   * @param key The object key
   */
  private void removeExpiredObject(String containerName, String key) {
    Map<String, InMemoryObject> containerObjects = objects.get(containerName);
    if (containerObjects != null) {
      InMemoryObject object = containerObjects.get(key);
      if (object != null
          && object.getExpiresAt().isPresent()
          && object.getExpiresAt().get().isBefore(Instant.now())) {
        containerObjects.remove(key);
        logger.debug("Removed expired object: {}/{}", containerName, key);
      }
    }
  }

  /**
   * Removes an expired key-value pair.
   *
   * @param key The key
   */
  private void removeExpiredKeyValue(String key) {
    KeyValueEntry entry = keyValueStore.get(key);
    if (entry != null
        && entry.getExpiresAt().isPresent()
        && entry.getExpiresAt().get().isBefore(Instant.now())) {
      keyValueStore.remove(key);
      logger.debug("Removed expired key-value pair: {}", key);
    }
  }

  /**
   * Calculates a hash for the given data.
   *
   * @param data The data to hash
   * @return The hash as a base64-encoded string
   */
  private String calculateHash(byte[] data) {
    synchronized (messageDigest) {
      messageDigest.update(data);
      return Base64.getEncoder().encodeToString(messageDigest.digest());
    }
  }

  /**
   * In-memory implementation of the Container interface.
   */
  private static class InMemoryContainer implements Container {
    private final String name;
    private final Instant creationTime;
    private final boolean publicAccess;
    private final Map<String, String> metadata;
    private final Optional<Duration> retentionPolicy;

    /**
     * Constructs an InMemoryContainer.
     *
     * @param name The container name
     * @param creationTime The creation time
     * @param publicAccess Whether the container is publicly accessible
     * @param metadata The metadata
     * @param retentionPolicy The retention policy
     */
    public InMemoryContainer(
        String name,
        Instant creationTime,
        boolean publicAccess,
        Map<String, String> metadata,
        Optional<Duration> retentionPolicy) {
      this.name = name;
      this.creationTime = creationTime;
      this.publicAccess = publicAccess;
      this.metadata = metadata;
      this.retentionPolicy = retentionPolicy;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public Instant getCreationTime() {
      return creationTime;
    }

    @Override
    public boolean isPublicAccess() {
      return publicAccess;
    }

    @Override
    public Map<String, String> getMetadata() {
      return Collections.unmodifiableMap(metadata);
    }

    @Override
    public Optional<Duration> getRetentionPolicy() {
      return retentionPolicy;
    }
  }

  /**
   * In-memory implementation of the StorageObject interface.
   */
  private static class InMemoryObject implements StorageObject {
    private final String key;
    private final long size;
    private final Instant lastModified;
    private final Optional<String> contentType;
    private final Map<String, String> metadata;
    private final byte[] data;
    private final String contentHash;
    private final Optional<Instant> expiresAt;

    /**
     * Constructs an InMemoryObject.
     *
     * @param key The object key
     * @param size The size in bytes
     * @param lastModified The last modified time
     * @param contentType The content type
     * @param metadata The metadata
     * @param data The data
     * @param contentHash The content hash
     * @param expiresAt The expiration time
     */
    public InMemoryObject(
        String key,
        long size,
        Instant lastModified,
        Optional<String> contentType,
        Map<String, String> metadata,
        byte[] data,
        String contentHash,
        Optional<Instant> expiresAt) {
      this.key = key;
      this.size = size;
      this.lastModified = lastModified;
      this.contentType = contentType;
      this.metadata = metadata;
      this.data = data;
      this.contentHash = contentHash;
      this.expiresAt = expiresAt;
    }

    @Override
    public String getKey() {
      return key;
    }

    @Override
    public long getSize() {
      return size;
    }

    @Override
    public Instant getLastModified() {
      return lastModified;
    }

    @Override
    public Optional<String> getContentType() {
      return contentType;
    }

    @Override
    public Map<String, String> getMetadata() {
      return Collections.unmodifiableMap(metadata);
    }

    @Override
    public byte[] getData() {
      return data;
    }

    @Override
    public Optional<String> getContentHash() {
      return Optional.ofNullable(contentHash);
    }

    /**
     * Gets the expiration time, if set.
     *
     * @return The expiration time, if set
     */
    public Optional<Instant> getExpiresAt() {
      return expiresAt;
    }
  }

  /**
   * Represents a key-value entry in the storage.
   */
  private static class KeyValueEntry {
    private final String key;
    private final String value;
    private final Optional<Instant> expiresAt;

    /**
     * Constructs a KeyValueEntry.
     *
     * @param key The key
     * @param value The value
     * @param expiresAt The expiration time
     */
    public KeyValueEntry(String key, String value, Optional<Instant> expiresAt) {
      this.key = key;
      this.value = value;
      this.expiresAt = expiresAt;
    }

    /**
     * Gets the key.
     *
     * @return The key
     */
    public String getKey() {
      return key;
    }

    /**
     * Gets the value.
     *
     * @return The value
     */
    public String getValue() {
      return value;
    }

    /**
     * Gets the expiration time, if set.
     *
     * @return The expiration time, if set
     */
    public Optional<Instant> getExpiresAt() {
      return expiresAt;
    }
  }
}