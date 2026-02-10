/*
 * Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
 *
 * This file is part of Samstraumr.
 * Licensed under Mozilla Public License 2.0.
 * See LICENSE file for details.
 */

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

package org.s8r.application.service.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.storage.StoragePort;
import org.s8r.application.port.storage.StoragePort.*;

/**
 * Service layer for storage operations.
 *
 * <p>This service provides a simplified API for storage operations by encapsulating the StoragePort
 * interface and providing methods that handle error logging and conversion between different result
 * types.
 */
public class StorageService {
  private final StoragePort storagePort;
  private final LoggerPort logger;

  private static final Duration DEFAULT_URL_EXPIRATION = Duration.ofHours(1);

  /**
   * Constructs a StorageService with the given StoragePort and LoggerPort.
   *
   * @param storagePort The StoragePort to use
   * @param logger The LoggerPort to use for logging
   */
  public StorageService(StoragePort storagePort, LoggerPort logger) {
    this.storagePort = storagePort;
    this.logger = logger;
  }

  /**
   * Creates a storage container with the given name.
   *
   * @param containerName The name of the container to create
   * @return An Optional containing the container if creation was successful, or empty if
   *     unsuccessful
   */
  public Optional<Container> createContainer(String containerName) {
    logger.debug("Creating container: {}", containerName);
    ContainerResult result = storagePort.createContainer(containerName);

    if (!result.isSuccessful()) {
      logger.warn("Failed to create container {}: {}", containerName, result.getMessage());
      return Optional.empty();
    }

    logger.info("Container created successfully: {}", containerName);
    return result.getContainer();
  }

  /**
   * Creates a storage container with the given name and options.
   *
   * @param containerName The name of the container to create
   * @param options The options for the container
   * @return An Optional containing the container if creation was successful, or empty if
   *     unsuccessful
   */
  public Optional<Container> createContainer(String containerName, ContainerOptions options) {
    logger.debug("Creating container {} with options", containerName);
    ContainerResult result = storagePort.createContainer(containerName, options);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to create container {} with options: {}", containerName, result.getMessage());
      return Optional.empty();
    }

    logger.info("Container created successfully with options: {}", containerName);
    return result.getContainer();
  }

  /**
   * Deletes a storage container with the given name.
   *
   * @param containerName The name of the container to delete
   * @param deleteContents Whether to delete the contents of the container
   * @return True if deletion was successful, false otherwise
   */
  public boolean deleteContainer(String containerName, boolean deleteContents) {
    logger.debug("Deleting container: {} (deleteContents={})", containerName, deleteContents);
    OperationResult result = storagePort.deleteContainer(containerName, deleteContents);

    if (!result.isSuccessful()) {
      logger.warn("Failed to delete container {}: {}", containerName, result.getMessage());
      return false;
    }

    logger.info("Container deleted successfully: {}", containerName);
    return true;
  }

  /**
   * Gets information about a storage container.
   *
   * @param containerName The name of the container
   * @return An Optional containing the container if it exists, or empty if it doesn't
   */
  public Optional<Container> getContainer(String containerName) {
    logger.debug("Getting container: {}", containerName);
    ContainerResult result = storagePort.getContainer(containerName);

    if (!result.isSuccessful()) {
      logger.warn("Failed to get container {}: {}", containerName, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Container retrieved successfully: {}", containerName);
    return result.getContainer();
  }

  /**
   * Lists all storage containers.
   *
   * @return A list of containers
   */
  public List<Container> listContainers() {
    return listContainers(Optional.empty(), Optional.empty());
  }

  /**
   * Lists storage containers with the given prefix.
   *
   * @param prefix The prefix to filter containers by name
   * @return A list of containers
   */
  public List<Container> listContainers(String prefix) {
    return listContainers(Optional.of(prefix), Optional.empty());
  }

  /**
   * Lists storage containers with the given prefix and maximum number of results.
   *
   * @param prefix Optional prefix to filter containers by name
   * @param maxResults Optional maximum number of results to return
   * @return A list of containers
   */
  public List<Container> listContainers(Optional<String> prefix, Optional<Integer> maxResults) {
    logger.debug(
        "Listing containers: prefix={}, maxResults={}",
        prefix.orElse("any"),
        maxResults.map(Object::toString).orElse("unlimited"));
    ContainerListResult result = storagePort.listContainers(prefix, maxResults);

    if (!result.isSuccessful()) {
      logger.warn("Failed to list containers: {}", result.getMessage());
      return Collections.emptyList();
    }

    logger.debug("Listed {} containers", result.getContainers().size());
    return result.getContainers();
  }

  /**
   * Stores an object in the specified container with the given key and data.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param data The data to store
   * @return An Optional containing the object if storage was successful, or empty if unsuccessful
   */
  public Optional<StorageObject> storeObject(String containerName, String key, byte[] data) {
    logger.debug(
        "Storing object: container={}, key={}, size={} bytes", containerName, key, data.length);
    ObjectResult result = storagePort.storeObject(containerName, key, data);

    if (!result.isSuccessful()) {
      logger.warn("Failed to store object {}/{}: {}", containerName, key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Object stored successfully: {}/{}", containerName, key);
    return result.getObject();
  }

  /**
   * Stores an object in the specified container with the given key, data, and options.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param data The data to store
   * @param options The options for the store operation
   * @return An Optional containing the object if storage was successful, or empty if unsuccessful
   */
  public Optional<StorageObject> storeObject(
      String containerName, String key, byte[] data, ObjectOptions options) {
    logger.debug("Storing object with options: container={}, key={}", containerName, key);
    ObjectResult result = storagePort.storeObject(containerName, key, data, options);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to store object with options {}/{}: {}", containerName, key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Object stored successfully with options: {}/{}", containerName, key);
    return result.getObject();
  }

  /**
   * Stores an object in the specified container with the given key and input stream.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param inputStream The input stream to read data from
   * @param size The size of the data in bytes
   * @return An Optional containing the object if storage was successful, or empty if unsuccessful
   */
  public Optional<StorageObject> storeObject(
      String containerName, String key, InputStream inputStream, long size) {
    logger.debug(
        "Storing object from stream: container={}, key={}, size={} bytes",
        containerName,
        key,
        size);
    ObjectResult result = storagePort.storeObject(containerName, key, inputStream, size);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to store object from stream {}/{}: {}", containerName, key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Object stored successfully from stream: {}/{}", containerName, key);
    return result.getObject();
  }

  /**
   * Retrieves an object from the specified container with the given key.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return An Optional containing the object if retrieval was successful, or empty if unsuccessful
   */
  public Optional<StorageObject> getObject(String containerName, String key) {
    logger.debug("Getting object: container={}, key={}", containerName, key);
    ObjectResult result = storagePort.getObject(containerName, key);

    if (!result.isSuccessful()) {
      logger.warn("Failed to get object {}/{}: {}", containerName, key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Object retrieved successfully: {}/{}", containerName, key);
    return result.getObject();
  }

  /**
   * Retrieves an object from the specified container and writes it to the output stream.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param outputStream The output stream to write the data to
   * @return True if retrieval was successful, false otherwise
   */
  public boolean getObject(String containerName, String key, OutputStream outputStream) {
    logger.debug("Getting object to stream: container={}, key={}", containerName, key);
    OperationResult result = storagePort.getObject(containerName, key, outputStream);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to get object to stream {}/{}: {}", containerName, key, result.getMessage());
      return false;
    }

    logger.debug("Object retrieved successfully to stream: {}/{}", containerName, key);
    return true;
  }

  /**
   * Deletes an object from the specified container.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return True if deletion was successful, false otherwise
   */
  public boolean deleteObject(String containerName, String key) {
    logger.debug("Deleting object: container={}, key={}", containerName, key);
    OperationResult result = storagePort.deleteObject(containerName, key);

    if (!result.isSuccessful()) {
      logger.warn("Failed to delete object {}/{}: {}", containerName, key, result.getMessage());
      return false;
    }

    logger.debug("Object deleted successfully: {}/{}", containerName, key);
    return true;
  }

  /**
   * Gets metadata for an object in the specified container.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return An Optional containing a map of metadata if retrieval was successful, or empty if
   *     unsuccessful
   */
  public Optional<Map<String, String>> getObjectMetadata(String containerName, String key) {
    logger.debug("Getting object metadata: container={}, key={}", containerName, key);
    ObjectMetadataResult result = storagePort.getObjectMetadata(containerName, key);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to get object metadata {}/{}: {}", containerName, key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Object metadata retrieved successfully: {}/{}", containerName, key);
    return Optional.of(result.getMetadata());
  }

  /**
   * Updates metadata for an object in the specified container.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param metadata The metadata to update
   * @param replace Whether to replace all existing metadata or merge with it
   * @return True if update was successful, false otherwise
   */
  public boolean updateObjectMetadata(
      String containerName, String key, Map<String, String> metadata, boolean replace) {
    logger.debug(
        "Updating object metadata: container={}, key={}, replace={}", containerName, key, replace);
    OperationResult result =
        storagePort.updateObjectMetadata(containerName, key, metadata, replace);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to update object metadata {}/{}: {}", containerName, key, result.getMessage());
      return false;
    }

    logger.debug("Object metadata updated successfully: {}/{}", containerName, key);
    return true;
  }

  /**
   * Lists objects in the specified container.
   *
   * @param containerName The name of the container
   * @return A list of objects
   */
  public List<StorageObject> listObjects(String containerName) {
    return listObjects(containerName, Optional.empty(), Optional.empty(), Optional.empty());
  }

  /**
   * Lists objects in the specified container with the given prefix.
   *
   * @param containerName The name of the container
   * @param prefix The prefix to filter objects by key
   * @return A list of objects
   */
  public List<StorageObject> listObjects(String containerName, String prefix) {
    return listObjects(containerName, Optional.of(prefix), Optional.empty(), Optional.empty());
  }

  /**
   * Lists objects in the specified container with the given prefix, delimiter, and maximum number
   * of results.
   *
   * @param containerName The name of the container
   * @param prefix Optional prefix to filter objects by key
   * @param delimiter Optional delimiter for hierarchical listing
   * @param maxResults Optional maximum number of results to return
   * @return A list of objects
   */
  public List<StorageObject> listObjects(
      String containerName,
      Optional<String> prefix,
      Optional<String> delimiter,
      Optional<Integer> maxResults) {
    logger.debug(
        "Listing objects: container={}, prefix={}, delimiter={}, maxResults={}",
        containerName,
        prefix.orElse("any"),
        delimiter.orElse("none"),
        maxResults.map(Object::toString).orElse("unlimited"));
    ObjectListResult result = storagePort.listObjects(containerName, prefix, delimiter, maxResults);

    if (!result.isSuccessful()) {
      logger.warn("Failed to list objects in container {}: {}", containerName, result.getMessage());
      return Collections.emptyList();
    }

    logger.debug("Listed {} objects in container {}", result.getObjects().size(), containerName);
    return result.getObjects();
  }

  /**
   * Lists prefixes in the specified container (for hierarchical listings).
   *
   * @param containerName The name of the container
   * @param prefix Optional prefix to filter by
   * @param delimiter The delimiter for hierarchical listing
   * @return A list of common prefixes
   */
  public List<String> listPrefixes(
      String containerName, Optional<String> prefix, String delimiter) {
    logger.debug(
        "Listing prefixes: container={}, prefix={}, delimiter={}",
        containerName,
        prefix.orElse("any"),
        delimiter);
    ObjectListResult result =
        storagePort.listObjects(containerName, prefix, Optional.of(delimiter), Optional.empty());

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to list prefixes in container {}: {}", containerName, result.getMessage());
      return Collections.emptyList();
    }

    logger.debug(
        "Listed {} prefixes in container {}", result.getCommonPrefixes().size(), containerName);
    return result.getCommonPrefixes();
  }

  /**
   * Copies an object from one location to another.
   *
   * @param sourceContainer The source container name
   * @param sourceKey The source object key
   * @param destinationContainer The destination container name
   * @param destinationKey The destination object key
   * @return An Optional containing the copied object if copy was successful, or empty if
   *     unsuccessful
   */
  public Optional<StorageObject> copyObject(
      String sourceContainer,
      String sourceKey,
      String destinationContainer,
      String destinationKey) {
    logger.debug(
        "Copying object: {}/{} to {}/{}",
        sourceContainer,
        sourceKey,
        destinationContainer,
        destinationKey);
    ObjectResult result =
        storagePort.copyObject(sourceContainer, sourceKey, destinationContainer, destinationKey);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to copy object {}/{} to {}/{}: {}",
          sourceContainer,
          sourceKey,
          destinationContainer,
          destinationKey,
          result.getMessage());
      return Optional.empty();
    }

    logger.debug(
        "Object copied successfully: {}/{} to {}/{}",
        sourceContainer,
        sourceKey,
        destinationContainer,
        destinationKey);
    return result.getObject();
  }

  /**
   * Moves an object from one location to another.
   *
   * @param sourceContainer The source container name
   * @param sourceKey The source object key
   * @param destinationContainer The destination container name
   * @param destinationKey The destination object key
   * @return An Optional containing the moved object if move was successful, or empty if
   *     unsuccessful
   */
  public Optional<StorageObject> moveObject(
      String sourceContainer,
      String sourceKey,
      String destinationContainer,
      String destinationKey) {
    logger.debug(
        "Moving object: {}/{} to {}/{}",
        sourceContainer,
        sourceKey,
        destinationContainer,
        destinationKey);
    ObjectResult result =
        storagePort.moveObject(sourceContainer, sourceKey, destinationContainer, destinationKey);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to move object {}/{} to {}/{}: {}",
          sourceContainer,
          sourceKey,
          destinationContainer,
          destinationKey,
          result.getMessage());
      return Optional.empty();
    }

    logger.debug(
        "Object moved successfully: {}/{} to {}/{}",
        sourceContainer,
        sourceKey,
        destinationContainer,
        destinationKey);
    return result.getObject();
  }

  /**
   * Generates a pre-signed URL for an object with default expiration time.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param operation The operation to allow with the URL (GET, PUT, etc.)
   * @return An Optional containing the URL if generation was successful, or empty if unsuccessful
   */
  public Optional<String> generatePresignedUrl(
      String containerName, String key, UrlOperation operation) {
    return generatePresignedUrl(containerName, key, operation, DEFAULT_URL_EXPIRATION);
  }

  /**
   * Generates a pre-signed URL for an object with the specified expiration time.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param operation The operation to allow with the URL (GET, PUT, etc.)
   * @param expiration The expiration time for the URL
   * @return An Optional containing the URL if generation was successful, or empty if unsuccessful
   */
  public Optional<String> generatePresignedUrl(
      String containerName, String key, UrlOperation operation, Duration expiration) {
    logger.debug(
        "Generating presigned URL: container={}, key={}, operation={}, expiration={}",
        containerName,
        key,
        operation,
        expiration);
    UrlResult result = storagePort.generatePresignedUrl(containerName, key, operation, expiration);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to generate presigned URL for {}/{}: {}",
          containerName,
          key,
          result.getMessage());
      return Optional.empty();
    }

    logger.debug("Presigned URL generated successfully for {}/{}", containerName, key);
    return result.getUrl();
  }

  /**
   * Sets a retention policy for an object.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param retentionPeriod The retention period
   * @return True if the policy was set successfully, false otherwise
   */
  public boolean setRetentionPolicy(String containerName, String key, Duration retentionPeriod) {
    logger.debug(
        "Setting retention policy: container={}, key={}, period={}",
        containerName,
        key,
        retentionPeriod);
    OperationResult result = storagePort.setRetentionPolicy(containerName, key, retentionPeriod);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to set retention policy for {}/{}: {}", containerName, key, result.getMessage());
      return false;
    }

    logger.debug("Retention policy set successfully for {}/{}", containerName, key);
    return true;
  }

  /**
   * Sets a retention policy for a container.
   *
   * @param containerName The name of the container
   * @param retentionPeriod The retention period
   * @return True if the policy was set successfully, false otherwise
   */
  public boolean setContainerRetentionPolicy(String containerName, Duration retentionPeriod) {
    logger.debug(
        "Setting container retention policy: container={}, period={}",
        containerName,
        retentionPeriod);
    OperationResult result =
        storagePort.setContainerRetentionPolicy(containerName, retentionPeriod);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to set container retention policy for {}: {}",
          containerName,
          result.getMessage());
      return false;
    }

    logger.debug("Container retention policy set successfully for {}", containerName);
    return true;
  }

  /**
   * Stores a key-value pair in the storage service.
   *
   * @param key The key
   * @param value The value
   * @return True if the pair was stored successfully, false otherwise
   */
  public boolean storeKeyValue(String key, String value) {
    logger.debug("Storing key-value pair: key={}", key);
    KvResult result = storagePort.storeKeyValue(key, value);

    if (!result.isSuccessful()) {
      logger.warn("Failed to store key-value pair for key {}: {}", key, result.getMessage());
      return false;
    }

    logger.debug("Key-value pair stored successfully for key {}", key);
    return true;
  }

  /**
   * Stores a key-value pair in the storage service with expiration.
   *
   * @param key The key
   * @param value The value
   * @param ttl Time to live for the key-value pair
   * @return True if the pair was stored successfully, false otherwise
   */
  public boolean storeKeyValue(String key, String value, Duration ttl) {
    logger.debug("Storing key-value pair with TTL: key={}, ttl={}", key, ttl);
    KvResult result = storagePort.storeKeyValue(key, value, ttl);

    if (!result.isSuccessful()) {
      logger.warn(
          "Failed to store key-value pair with TTL for key {}: {}", key, result.getMessage());
      return false;
    }

    logger.debug("Key-value pair with TTL stored successfully for key {}", key);
    return true;
  }

  /**
   * Retrieves a value by key from the key-value storage.
   *
   * @param key The key
   * @return An Optional containing the value if retrieval was successful, or empty if unsuccessful
   */
  public Optional<String> getKeyValue(String key) {
    logger.debug("Getting key-value pair: key={}", key);
    KvResult result = storagePort.getKeyValue(key);

    if (!result.isSuccessful()) {
      logger.warn("Failed to get key-value pair for key {}: {}", key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Key-value pair retrieved successfully for key {}", key);
    return result.getValue();
  }

  /**
   * Deletes a key-value pair from the storage service.
   *
   * @param key The key
   * @return True if the pair was deleted successfully, false otherwise
   */
  public boolean deleteKeyValue(String key) {
    logger.debug("Deleting key-value pair: key={}", key);
    OperationResult result = storagePort.deleteKeyValue(key);

    if (!result.isSuccessful()) {
      logger.warn("Failed to delete key-value pair for key {}: {}", key, result.getMessage());
      return false;
    }

    logger.debug("Key-value pair deleted successfully for key {}", key);
    return true;
  }

  /**
   * Atomically updates a key-value pair using a transform function.
   *
   * @param key The key
   * @param transformFunction The function to apply to the current value
   * @return An Optional containing the new value if update was successful, or empty if unsuccessful
   */
  public Optional<String> updateKeyValue(
      String key, Function<Optional<String>, String> transformFunction) {
    logger.debug("Updating key-value pair: key={}", key);
    KvResult result = storagePort.updateKeyValue(key, transformFunction);

    if (!result.isSuccessful()) {
      logger.warn("Failed to update key-value pair for key {}: {}", key, result.getMessage());
      return Optional.empty();
    }

    logger.debug("Key-value pair updated successfully for key {}", key);
    return result.getValue();
  }

  /**
   * Lists keys in the key-value storage.
   *
   * @return A list of keys
   */
  public List<String> listKeys() {
    return listKeys(Optional.empty(), Optional.empty());
  }

  /**
   * Lists keys in the key-value storage with the given prefix.
   *
   * @param prefix The prefix to filter keys
   * @return A list of keys
   */
  public List<String> listKeys(String prefix) {
    return listKeys(Optional.of(prefix), Optional.empty());
  }

  /**
   * Lists keys in the key-value storage with the given prefix and maximum number of results.
   *
   * @param prefix Optional prefix to filter keys
   * @param maxResults Optional maximum number of results to return
   * @return A list of keys
   */
  public List<String> listKeys(Optional<String> prefix, Optional<Integer> maxResults) {
    logger.debug(
        "Listing keys: prefix={}, maxResults={}",
        prefix.orElse("any"),
        maxResults.map(Object::toString).orElse("unlimited"));
    KvListResult result = storagePort.listKeys(prefix, maxResults);

    if (!result.isSuccessful()) {
      logger.warn("Failed to list keys: {}", result.getMessage());
      return Collections.emptyList();
    }

    logger.debug("Listed {} keys", result.getKeys().size());
    return result.getKeys();
  }

  /**
   * Creates a ContainerOptions builder.
   *
   * @return A new ContainerOptions.Builder
   */
  public ContainerOptions.Builder containerOptionsBuilder() {
    return new ContainerOptions.Builder() {
      private boolean publicAccess = false;
      private final Map<String, String> metadata = new HashMap<>();

      @Override
      public ContainerOptions.Builder publicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
        return this;
      }

      @Override
      public ContainerOptions.Builder addMetadata(String key, String value) {
        metadata.put(key, value);
        return this;
      }

      @Override
      public ContainerOptions.Builder metadata(Map<String, String> metadata) {
        this.metadata.clear();
        this.metadata.putAll(metadata);
        return this;
      }

      @Override
      public ContainerOptions build() {
        return new ContainerOptions() {
          @Override
          public boolean isPublicAccess() {
            return publicAccess;
          }

          @Override
          public Map<String, String> getMetadata() {
            return Collections.unmodifiableMap(metadata);
          }
        };
      }
    };
  }

  /**
   * Creates an ObjectOptions builder.
   *
   * @return A new ObjectOptions.Builder
   */
  public ObjectOptions.Builder objectOptionsBuilder() {
    return new ObjectOptions.Builder() {
      private String contentType;
      private final Map<String, String> metadata = new HashMap<>();
      private EncryptionMode encryptionMode;
      private Duration ttl;

      @Override
      public ObjectOptions.Builder contentType(String contentType) {
        this.contentType = contentType;
        return this;
      }

      @Override
      public ObjectOptions.Builder addMetadata(String key, String value) {
        metadata.put(key, value);
        return this;
      }

      @Override
      public ObjectOptions.Builder metadata(Map<String, String> metadata) {
        this.metadata.clear();
        this.metadata.putAll(metadata);
        return this;
      }

      @Override
      public ObjectOptions.Builder encryptionMode(EncryptionMode encryptionMode) {
        this.encryptionMode = encryptionMode;
        return this;
      }

      @Override
      public ObjectOptions.Builder ttl(Duration ttl) {
        this.ttl = ttl;
        return this;
      }

      @Override
      public ObjectOptions build() {
        return new ObjectOptions() {
          @Override
          public Optional<String> getContentType() {
            return Optional.ofNullable(contentType);
          }

          @Override
          public Map<String, String> getMetadata() {
            return Collections.unmodifiableMap(metadata);
          }

          @Override
          public Optional<EncryptionMode> getEncryptionMode() {
            return Optional.ofNullable(encryptionMode);
          }

          @Override
          public Optional<Duration> getTtl() {
            return Optional.ofNullable(ttl);
          }
        };
      }
    };
  }

  /**
   * Creates a container asynchronously.
   *
   * @param containerName The name of the container to create
   * @return A CompletableFuture that will contain the container if creation was successful, or will
   *     complete exceptionally if unsuccessful
   */
  public CompletableFuture<Container> createContainerAsync(String containerName) {
    return CompletableFuture.supplyAsync(
        () -> {
          Optional<Container> container = createContainer(containerName);
          return container.orElseThrow(
              () -> new RuntimeException("Failed to create container: " + containerName));
        });
  }

  /**
   * Stores an object asynchronously.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param data The data to store
   * @return A CompletableFuture that will contain the object if storage was successful, or will
   *     complete exceptionally if unsuccessful
   */
  public CompletableFuture<StorageObject> storeObjectAsync(
      String containerName, String key, byte[] data) {
    return CompletableFuture.supplyAsync(
        () -> {
          Optional<StorageObject> object = storeObject(containerName, key, data);
          return object.orElseThrow(
              () -> new RuntimeException("Failed to store object: " + containerName + "/" + key));
        });
  }

  /**
   * Retrieves an object asynchronously.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return A CompletableFuture that will contain the object if retrieval was successful, or will
   *     complete exceptionally if unsuccessful
   */
  public CompletableFuture<StorageObject> getObjectAsync(String containerName, String key) {
    return CompletableFuture.supplyAsync(
        () -> {
          Optional<StorageObject> object = getObject(containerName, key);
          return object.orElseThrow(
              () -> new RuntimeException("Failed to get object: " + containerName + "/" + key));
        });
  }
}
