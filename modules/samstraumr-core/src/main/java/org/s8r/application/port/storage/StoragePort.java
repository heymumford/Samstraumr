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

package org.s8r.application.port.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Port interface for generic data storage operations.
 *
 * <p>This interface defines the contract for storing and retrieving data in a generic manner.
 * Following the ports and adapters pattern, this is an output port in the application layer, which
 * will be implemented by adapters in the infrastructure layer.
 *
 * <p>The StoragePort provides operations for working with different storage types (object, blob,
 * key-value, etc.) in a consistent manner, abstracting away the details of the underlying storage
 * implementation.
 */
public interface StoragePort {

  /**
   * Creates a storage container with the given name and default options.
   *
   * @param containerName The name of the container to create
   * @return The result of the operation
   */
  ContainerResult createContainer(String containerName);

  /**
   * Creates a storage container with the given name and options.
   *
   * @param containerName The name of the container to create
   * @param options The options for the container
   * @return The result of the operation
   */
  ContainerResult createContainer(String containerName, ContainerOptions options);

  /**
   * Deletes a storage container with the given name.
   *
   * @param containerName The name of the container to delete
   * @param deleteContents Whether to delete the contents of the container
   * @return The result of the operation
   */
  OperationResult deleteContainer(String containerName, boolean deleteContents);

  /**
   * Gets information about a storage container.
   *
   * @param containerName The name of the container
   * @return The result of the operation
   */
  ContainerResult getContainer(String containerName);

  /**
   * Lists all storage containers.
   *
   * @param prefix Optional prefix to filter containers by name
   * @param maxResults Optional maximum number of results to return
   * @return The result of the operation
   */
  ContainerListResult listContainers(Optional<String> prefix, Optional<Integer> maxResults);

  /**
   * Stores an object in the specified container with the given key and data.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param data The data to store
   * @return The result of the operation
   */
  ObjectResult storeObject(String containerName, String key, byte[] data);

  /**
   * Stores an object in the specified container with the given key, data, and options.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param data The data to store
   * @param options The options for the store operation
   * @return The result of the operation
   */
  ObjectResult storeObject(String containerName, String key, byte[] data, ObjectOptions options);

  /**
   * Stores an object in the specified container with the given key and input stream.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param inputStream The input stream to read data from
   * @param size The size of the data in bytes
   * @return The result of the operation
   */
  ObjectResult storeObject(String containerName, String key, InputStream inputStream, long size);

  /**
   * Stores an object in the specified container with the given key, input stream, and options.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param inputStream The input stream to read data from
   * @param size The size of the data in bytes
   * @param options The options for the store operation
   * @return The result of the operation
   */
  ObjectResult storeObject(
      String containerName, String key, InputStream inputStream, long size, ObjectOptions options);

  /**
   * Retrieves an object from the specified container with the given key.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return The result of the operation
   */
  ObjectResult getObject(String containerName, String key);

  /**
   * Retrieves an object from the specified container and writes it to the output stream.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param outputStream The output stream to write the data to
   * @return The result of the operation
   */
  OperationResult getObject(String containerName, String key, OutputStream outputStream);

  /**
   * Deletes an object from the specified container.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return The result of the operation
   */
  OperationResult deleteObject(String containerName, String key);

  /**
   * Gets metadata for an object in the specified container.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @return The result of the operation
   */
  ObjectMetadataResult getObjectMetadata(String containerName, String key);

  /**
   * Updates metadata for an object in the specified container.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param metadata The metadata to update
   * @param replace Whether to replace all existing metadata or merge with it
   * @return The result of the operation
   */
  OperationResult updateObjectMetadata(
      String containerName, String key, Map<String, String> metadata, boolean replace);

  /**
   * Lists objects in the specified container.
   *
   * @param containerName The name of the container
   * @param prefix Optional prefix to filter objects by key
   * @param delimiter Optional delimiter for hierarchical listing
   * @param maxResults Optional maximum number of results to return
   * @return The result of the operation
   */
  ObjectListResult listObjects(
      String containerName,
      Optional<String> prefix,
      Optional<String> delimiter,
      Optional<Integer> maxResults);

  /**
   * Copies an object from one location to another.
   *
   * @param sourceContainer The source container name
   * @param sourceKey The source object key
   * @param destinationContainer The destination container name
   * @param destinationKey The destination object key
   * @return The result of the operation
   */
  ObjectResult copyObject(
      String sourceContainer, String sourceKey, String destinationContainer, String destinationKey);

  /**
   * Moves an object from one location to another.
   *
   * @param sourceContainer The source container name
   * @param sourceKey The source object key
   * @param destinationContainer The destination container name
   * @param destinationKey The destination object key
   * @return The result of the operation
   */
  ObjectResult moveObject(
      String sourceContainer, String sourceKey, String destinationContainer, String destinationKey);

  /**
   * Generates a pre-signed URL for an object with default expiration time.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param operation The operation to allow with the URL (GET, PUT, etc.)
   * @return The result of the operation
   */
  UrlResult generatePresignedUrl(String containerName, String key, UrlOperation operation);

  /**
   * Generates a pre-signed URL for an object with the specified expiration time.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param operation The operation to allow with the URL (GET, PUT, etc.)
   * @param expiration The expiration time for the URL
   * @return The result of the operation
   */
  UrlResult generatePresignedUrl(
      String containerName, String key, UrlOperation operation, Duration expiration);

  /**
   * Sets a retention policy for an object.
   *
   * @param containerName The name of the container
   * @param key The key for the object
   * @param retentionPeriod The retention period
   * @return The result of the operation
   */
  OperationResult setRetentionPolicy(String containerName, String key, Duration retentionPeriod);

  /**
   * Sets a retention policy for a container.
   *
   * @param containerName The name of the container
   * @param retentionPeriod The retention period
   * @return The result of the operation
   */
  OperationResult setContainerRetentionPolicy(String containerName, Duration retentionPeriod);

  /**
   * Stores a key-value pair in the storage service.
   *
   * @param key The key
   * @param value The value
   * @return The result of the operation
   */
  KvResult storeKeyValue(String key, String value);

  /**
   * Stores a key-value pair in the storage service with expiration.
   *
   * @param key The key
   * @param value The value
   * @param ttl Time to live for the key-value pair
   * @return The result of the operation
   */
  KvResult storeKeyValue(String key, String value, Duration ttl);

  /**
   * Retrieves a value by key from the key-value storage.
   *
   * @param key The key
   * @return The result of the operation
   */
  KvResult getKeyValue(String key);

  /**
   * Deletes a key-value pair from the storage service.
   *
   * @param key The key
   * @return The result of the operation
   */
  OperationResult deleteKeyValue(String key);

  /**
   * Atomically updates a key-value pair using a transform function.
   *
   * @param key The key
   * @param transformFunction The function to apply to the current value
   * @return The result of the operation
   */
  KvResult updateKeyValue(String key, Function<Optional<String>, String> transformFunction);

  /**
   * Lists keys in the key-value storage.
   *
   * @param prefix Optional prefix to filter keys
   * @param maxResults Optional maximum number of results to return
   * @return The result of the operation
   */
  KvListResult listKeys(Optional<String> prefix, Optional<Integer> maxResults);

  // Nested interfaces and classes

  /** Options for storage containers. */
  interface ContainerOptions {
    /**
     * Get whether the container should be publicly accessible.
     *
     * @return True if the container should be publicly accessible, false otherwise
     */
    boolean isPublicAccess();

    /**
     * Get the metadata for the container.
     *
     * @return A map of metadata key-value pairs
     */
    Map<String, String> getMetadata();

    /** Builder for creating ContainerOptions instances. */
    interface Builder {
      /**
       * Sets whether the container should be publicly accessible.
       *
       * @param publicAccess True if the container should be publicly accessible, false otherwise
       * @return This builder
       */
      Builder publicAccess(boolean publicAccess);

      /**
       * Adds metadata to the container.
       *
       * @param key The metadata key
       * @param value The metadata value
       * @return This builder
       */
      Builder addMetadata(String key, String value);

      /**
       * Sets the metadata for the container.
       *
       * @param metadata A map of metadata key-value pairs
       * @return This builder
       */
      Builder metadata(Map<String, String> metadata);

      /**
       * Builds the ContainerOptions.
       *
       * @return The built ContainerOptions
       */
      ContainerOptions build();
    }
  }

  /** Options for storage objects. */
  interface ObjectOptions {
    /**
     * Get the content type of the object.
     *
     * @return The content type
     */
    Optional<String> getContentType();

    /**
     * Get the metadata for the object.
     *
     * @return A map of metadata key-value pairs
     */
    Map<String, String> getMetadata();

    /**
     * Get the encryption mode for the object.
     *
     * @return The encryption mode
     */
    Optional<EncryptionMode> getEncryptionMode();

    /**
     * Get the time to live for the object.
     *
     * @return The time to live
     */
    Optional<Duration> getTtl();

    /** Builder for creating ObjectOptions instances. */
    interface Builder {
      /**
       * Sets the content type of the object.
       *
       * @param contentType The content type
       * @return This builder
       */
      Builder contentType(String contentType);

      /**
       * Adds metadata to the object.
       *
       * @param key The metadata key
       * @param value The metadata value
       * @return This builder
       */
      Builder addMetadata(String key, String value);

      /**
       * Sets the metadata for the object.
       *
       * @param metadata A map of metadata key-value pairs
       * @return This builder
       */
      Builder metadata(Map<String, String> metadata);

      /**
       * Sets the encryption mode for the object.
       *
       * @param encryptionMode The encryption mode
       * @return This builder
       */
      Builder encryptionMode(EncryptionMode encryptionMode);

      /**
       * Sets the time to live for the object.
       *
       * @param ttl The time to live
       * @return This builder
       */
      Builder ttl(Duration ttl);

      /**
       * Builds the ObjectOptions.
       *
       * @return The built ObjectOptions
       */
      ObjectOptions build();
    }
  }

  /** Encryption modes for storage objects. */
  enum EncryptionMode {
    /** No encryption. */
    NONE,

    /** Server-side encryption. */
    SERVER_SIDE,

    /** Client-side encryption. */
    CLIENT_SIDE
  }

  /** Operations for pre-signed URLs. */
  enum UrlOperation {
    /** GET operation. */
    GET,

    /** PUT operation. */
    PUT,

    /** DELETE operation. */
    DELETE
  }

  /** Represents a storage container. */
  interface Container {
    /**
     * Gets the name of the container.
     *
     * @return The container name
     */
    String getName();

    /**
     * Gets the creation time of the container.
     *
     * @return The creation time
     */
    Instant getCreationTime();

    /**
     * Gets whether the container is publicly accessible.
     *
     * @return True if the container is publicly accessible, false otherwise
     */
    boolean isPublicAccess();

    /**
     * Gets the metadata for the container.
     *
     * @return A map of metadata key-value pairs
     */
    Map<String, String> getMetadata();

    /**
     * Gets the retention policy for the container.
     *
     * @return The retention policy, if set
     */
    Optional<Duration> getRetentionPolicy();
  }

  /** Represents a storage object. */
  interface StorageObject {
    /**
     * Gets the key of the object.
     *
     * @return The object key
     */
    String getKey();

    /**
     * Gets the size of the object in bytes.
     *
     * @return The object size
     */
    long getSize();

    /**
     * Gets the last modified time of the object.
     *
     * @return The last modified time
     */
    Instant getLastModified();

    /**
     * Gets the content type of the object.
     *
     * @return The content type, if set
     */
    Optional<String> getContentType();

    /**
     * Gets the metadata for the object.
     *
     * @return A map of metadata key-value pairs
     */
    Map<String, String> getMetadata();

    /**
     * Gets the data for the object.
     *
     * @return The object data
     */
    byte[] getData();

    /**
     * Gets the content hash for the object.
     *
     * @return The content hash, if available
     */
    Optional<String> getContentHash();
  }

  /** Base class for operation results. */
  abstract class OperationResult {
    private final boolean successful;
    private final String message;
    private final Optional<String> errorCode;
    private final Map<String, String> attributes;

    /**
     * Constructs a successful OperationResult.
     *
     * @param message The success message
     * @param attributes Additional attributes for the result
     */
    protected OperationResult(String message, Map<String, String> attributes) {
      this.successful = true;
      this.message = message;
      this.errorCode = Optional.empty();
      this.attributes = attributes;
    }

    /**
     * Constructs a failed OperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     */
    protected OperationResult(String message, String errorCode, Map<String, String> attributes) {
      this.successful = false;
      this.message = message;
      this.errorCode = Optional.of(errorCode);
      this.attributes = attributes;
    }

    /**
     * Gets whether the operation was successful.
     *
     * @return True if successful, false otherwise
     */
    public boolean isSuccessful() {
      return successful;
    }

    /**
     * Gets the result message.
     *
     * @return The message
     */
    public String getMessage() {
      return message;
    }

    /**
     * Gets the error code, if any.
     *
     * @return The error code, if available
     */
    public Optional<String> getErrorCode() {
      return errorCode;
    }

    /**
     * Gets additional attributes for the result.
     *
     * @return A map of attribute key-value pairs
     */
    public Map<String, String> getAttributes() {
      return attributes;
    }

    /**
     * Creates a successful OperationResult.
     *
     * @param message The success message
     * @return The OperationResult
     */
    public static OperationResult success(String message) {
      return success(message, Map.of());
    }

    /**
     * Creates a successful OperationResult with attributes.
     *
     * @param message The success message
     * @param attributes Additional attributes for the result
     * @return The OperationResult
     */
    public static OperationResult success(String message, Map<String, String> attributes) {
      return new OperationResult(message, attributes) {};
    }

    /**
     * Creates a failed OperationResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The OperationResult
     */
    public static OperationResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed OperationResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The OperationResult
     */
    public static OperationResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new OperationResult(message, errorCode, attributes) {};
    }
  }

  /** Result of a container operation. */
  class ContainerResult extends OperationResult {
    private final Optional<Container> container;

    private ContainerResult(String message, Container container, Map<String, String> attributes) {
      super(message, attributes);
      this.container = Optional.of(container);
    }

    private ContainerResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.container = Optional.empty();
    }

    /**
     * Gets the container, if the operation was successful.
     *
     * @return The container, if available
     */
    public Optional<Container> getContainer() {
      return container;
    }

    /**
     * Creates a successful ContainerResult.
     *
     * @param message The success message
     * @param container The container
     * @return The ContainerResult
     */
    public static ContainerResult success(String message, Container container) {
      return success(message, container, Map.of());
    }

    /**
     * Creates a successful ContainerResult with attributes.
     *
     * @param message The success message
     * @param container The container
     * @param attributes Additional attributes for the result
     * @return The ContainerResult
     */
    public static ContainerResult success(
        String message, Container container, Map<String, String> attributes) {
      return new ContainerResult(message, container, attributes);
    }

    /**
     * Creates a failed ContainerResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The ContainerResult
     */
    public static ContainerResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed ContainerResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The ContainerResult
     */
    public static ContainerResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new ContainerResult(message, errorCode, attributes);
    }
  }

  /** Result of a container list operation. */
  class ContainerListResult extends OperationResult {
    private final List<Container> containers;
    private final boolean hasMoreResults;

    private ContainerListResult(
        String message,
        List<Container> containers,
        boolean hasMoreResults,
        Map<String, String> attributes) {
      super(message, attributes);
      this.containers = containers;
      this.hasMoreResults = hasMoreResults;
    }

    private ContainerListResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.containers = List.of();
      this.hasMoreResults = false;
    }

    /**
     * Gets the list of containers.
     *
     * @return The list of containers
     */
    public List<Container> getContainers() {
      return containers;
    }

    /**
     * Gets whether there are more results available.
     *
     * @return True if there are more results, false otherwise
     */
    public boolean hasMoreResults() {
      return hasMoreResults;
    }

    /**
     * Creates a successful ContainerListResult.
     *
     * @param message The success message
     * @param containers The list of containers
     * @param hasMoreResults Whether there are more results available
     * @return The ContainerListResult
     */
    public static ContainerListResult success(
        String message, List<Container> containers, boolean hasMoreResults) {
      return success(message, containers, hasMoreResults, Map.of());
    }

    /**
     * Creates a successful ContainerListResult with attributes.
     *
     * @param message The success message
     * @param containers The list of containers
     * @param hasMoreResults Whether there are more results available
     * @param attributes Additional attributes for the result
     * @return The ContainerListResult
     */
    public static ContainerListResult success(
        String message,
        List<Container> containers,
        boolean hasMoreResults,
        Map<String, String> attributes) {
      return new ContainerListResult(message, containers, hasMoreResults, attributes);
    }

    /**
     * Creates a failed ContainerListResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The ContainerListResult
     */
    public static ContainerListResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed ContainerListResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The ContainerListResult
     */
    public static ContainerListResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new ContainerListResult(message, errorCode, attributes);
    }
  }

  /** Result of an object operation. */
  class ObjectResult extends OperationResult {
    private final Optional<StorageObject> object;

    private ObjectResult(String message, StorageObject object, Map<String, String> attributes) {
      super(message, attributes);
      this.object = Optional.of(object);
    }

    private ObjectResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.object = Optional.empty();
    }

    /**
     * Gets the object, if the operation was successful.
     *
     * @return The object, if available
     */
    public Optional<StorageObject> getObject() {
      return object;
    }

    /**
     * Creates a successful ObjectResult.
     *
     * @param message The success message
     * @param object The object
     * @return The ObjectResult
     */
    public static ObjectResult success(String message, StorageObject object) {
      return success(message, object, Map.of());
    }

    /**
     * Creates a successful ObjectResult with attributes.
     *
     * @param message The success message
     * @param object The object
     * @param attributes Additional attributes for the result
     * @return The ObjectResult
     */
    public static ObjectResult success(
        String message, StorageObject object, Map<String, String> attributes) {
      return new ObjectResult(message, object, attributes);
    }

    /**
     * Creates a failed ObjectResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The ObjectResult
     */
    public static ObjectResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed ObjectResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The ObjectResult
     */
    public static ObjectResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new ObjectResult(message, errorCode, attributes);
    }
  }

  /** Result of an object list operation. */
  class ObjectListResult extends OperationResult {
    private final List<StorageObject> objects;
    private final boolean hasMoreResults;
    private final List<String> commonPrefixes;

    private ObjectListResult(
        String message,
        List<StorageObject> objects,
        boolean hasMoreResults,
        List<String> commonPrefixes,
        Map<String, String> attributes) {
      super(message, attributes);
      this.objects = objects;
      this.hasMoreResults = hasMoreResults;
      this.commonPrefixes = commonPrefixes;
    }

    private ObjectListResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.objects = List.of();
      this.hasMoreResults = false;
      this.commonPrefixes = List.of();
    }

    /**
     * Gets the list of objects.
     *
     * @return The list of objects
     */
    public List<StorageObject> getObjects() {
      return objects;
    }

    /**
     * Gets whether there are more results available.
     *
     * @return True if there are more results, false otherwise
     */
    public boolean hasMoreResults() {
      return hasMoreResults;
    }

    /**
     * Gets the list of common prefixes (for hierarchical listings).
     *
     * @return The list of common prefixes
     */
    public List<String> getCommonPrefixes() {
      return commonPrefixes;
    }

    /**
     * Creates a successful ObjectListResult.
     *
     * @param message The success message
     * @param objects The list of objects
     * @param hasMoreResults Whether there are more results available
     * @param commonPrefixes The list of common prefixes
     * @return The ObjectListResult
     */
    public static ObjectListResult success(
        String message,
        List<StorageObject> objects,
        boolean hasMoreResults,
        List<String> commonPrefixes) {
      return success(message, objects, hasMoreResults, commonPrefixes, Map.of());
    }

    /**
     * Creates a successful ObjectListResult with attributes.
     *
     * @param message The success message
     * @param objects The list of objects
     * @param hasMoreResults Whether there are more results available
     * @param commonPrefixes The list of common prefixes
     * @param attributes Additional attributes for the result
     * @return The ObjectListResult
     */
    public static ObjectListResult success(
        String message,
        List<StorageObject> objects,
        boolean hasMoreResults,
        List<String> commonPrefixes,
        Map<String, String> attributes) {
      return new ObjectListResult(message, objects, hasMoreResults, commonPrefixes, attributes);
    }

    /**
     * Creates a failed ObjectListResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The ObjectListResult
     */
    public static ObjectListResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed ObjectListResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The ObjectListResult
     */
    public static ObjectListResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new ObjectListResult(message, errorCode, attributes);
    }
  }

  /** Result of an object metadata operation. */
  class ObjectMetadataResult extends OperationResult {
    private final Optional<String> contentType;
    private final Optional<Long> contentLength;
    private final Optional<Instant> lastModified;
    private final Map<String, String> metadata;
    private final Optional<String> contentHash;

    private ObjectMetadataResult(
        String message,
        Optional<String> contentType,
        Optional<Long> contentLength,
        Optional<Instant> lastModified,
        Map<String, String> metadata,
        Optional<String> contentHash,
        Map<String, String> attributes) {
      super(message, attributes);
      this.contentType = contentType;
      this.contentLength = contentLength;
      this.lastModified = lastModified;
      this.metadata = metadata;
      this.contentHash = contentHash;
    }

    private ObjectMetadataResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.contentType = Optional.empty();
      this.contentLength = Optional.empty();
      this.lastModified = Optional.empty();
      this.metadata = Map.of();
      this.contentHash = Optional.empty();
    }

    /**
     * Gets the content type, if available.
     *
     * @return The content type, if available
     */
    public Optional<String> getContentType() {
      return contentType;
    }

    /**
     * Gets the content length, if available.
     *
     * @return The content length, if available
     */
    public Optional<Long> getContentLength() {
      return contentLength;
    }

    /**
     * Gets the last modified time, if available.
     *
     * @return The last modified time, if available
     */
    public Optional<Instant> getLastModified() {
      return lastModified;
    }

    /**
     * Gets the metadata.
     *
     * @return The metadata
     */
    public Map<String, String> getMetadata() {
      return metadata;
    }

    /**
     * Gets the content hash, if available.
     *
     * @return The content hash, if available
     */
    public Optional<String> getContentHash() {
      return contentHash;
    }

    /**
     * Creates a successful ObjectMetadataResult.
     *
     * @param message The success message
     * @param contentType The content type
     * @param contentLength The content length
     * @param lastModified The last modified time
     * @param metadata The metadata
     * @param contentHash The content hash
     * @return The ObjectMetadataResult
     */
    public static ObjectMetadataResult success(
        String message,
        Optional<String> contentType,
        Optional<Long> contentLength,
        Optional<Instant> lastModified,
        Map<String, String> metadata,
        Optional<String> contentHash) {
      return success(
          message, contentType, contentLength, lastModified, metadata, contentHash, Map.of());
    }

    /**
     * Creates a successful ObjectMetadataResult with attributes.
     *
     * @param message The success message
     * @param contentType The content type
     * @param contentLength The content length
     * @param lastModified The last modified time
     * @param metadata The metadata
     * @param contentHash The content hash
     * @param attributes Additional attributes for the result
     * @return The ObjectMetadataResult
     */
    public static ObjectMetadataResult success(
        String message,
        Optional<String> contentType,
        Optional<Long> contentLength,
        Optional<Instant> lastModified,
        Map<String, String> metadata,
        Optional<String> contentHash,
        Map<String, String> attributes) {
      return new ObjectMetadataResult(
          message, contentType, contentLength, lastModified, metadata, contentHash, attributes);
    }

    /**
     * Creates a failed ObjectMetadataResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The ObjectMetadataResult
     */
    public static ObjectMetadataResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed ObjectMetadataResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The ObjectMetadataResult
     */
    public static ObjectMetadataResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new ObjectMetadataResult(message, errorCode, attributes);
    }
  }

  /** Result of a URL generation operation. */
  class UrlResult extends OperationResult {
    private final Optional<String> url;
    private final Optional<Instant> expiresAt;

    private UrlResult(
        String message, String url, Instant expiresAt, Map<String, String> attributes) {
      super(message, attributes);
      this.url = Optional.of(url);
      this.expiresAt = Optional.of(expiresAt);
    }

    private UrlResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.url = Optional.empty();
      this.expiresAt = Optional.empty();
    }

    /**
     * Gets the URL, if generation was successful.
     *
     * @return The URL, if available
     */
    public Optional<String> getUrl() {
      return url;
    }

    /**
     * Gets the expiration time of the URL, if generation was successful.
     *
     * @return The expiration time, if available
     */
    public Optional<Instant> getExpiresAt() {
      return expiresAt;
    }

    /**
     * Creates a successful UrlResult.
     *
     * @param message The success message
     * @param url The URL
     * @param expiresAt The expiration time
     * @return The UrlResult
     */
    public static UrlResult success(String message, String url, Instant expiresAt) {
      return success(message, url, expiresAt, Map.of());
    }

    /**
     * Creates a successful UrlResult with attributes.
     *
     * @param message The success message
     * @param url The URL
     * @param expiresAt The expiration time
     * @param attributes Additional attributes for the result
     * @return The UrlResult
     */
    public static UrlResult success(
        String message, String url, Instant expiresAt, Map<String, String> attributes) {
      return new UrlResult(message, url, expiresAt, attributes);
    }

    /**
     * Creates a failed UrlResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The UrlResult
     */
    public static UrlResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed UrlResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The UrlResult
     */
    public static UrlResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new UrlResult(message, errorCode, attributes);
    }
  }

  /** Result of a key-value operation. */
  class KvResult extends OperationResult {
    private final Optional<String> value;
    private final Optional<Instant> expiresAt;

    private KvResult(
        String message, String value, Optional<Instant> expiresAt, Map<String, String> attributes) {
      super(message, attributes);
      this.value = Optional.of(value);
      this.expiresAt = expiresAt;
    }

    private KvResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.value = Optional.empty();
      this.expiresAt = Optional.empty();
    }

    /**
     * Gets the value, if the operation was successful.
     *
     * @return The value, if available
     */
    public Optional<String> getValue() {
      return value;
    }

    /**
     * Gets the expiration time, if the operation was successful and TTL is set.
     *
     * @return The expiration time, if available
     */
    public Optional<Instant> getExpiresAt() {
      return expiresAt;
    }

    /**
     * Creates a successful KvResult.
     *
     * @param message The success message
     * @param value The value
     * @return The KvResult
     */
    public static KvResult success(String message, String value) {
      return success(message, value, Optional.empty());
    }

    /**
     * Creates a successful KvResult with expiration.
     *
     * @param message The success message
     * @param value The value
     * @param expiresAt The expiration time
     * @return The KvResult
     */
    public static KvResult success(String message, String value, Optional<Instant> expiresAt) {
      return success(message, value, expiresAt, Map.of());
    }

    /**
     * Creates a successful KvResult with expiration and attributes.
     *
     * @param message The success message
     * @param value The value
     * @param expiresAt The expiration time
     * @param attributes Additional attributes for the result
     * @return The KvResult
     */
    public static KvResult success(
        String message, String value, Optional<Instant> expiresAt, Map<String, String> attributes) {
      return new KvResult(message, value, expiresAt, attributes);
    }

    /**
     * Creates a failed KvResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The KvResult
     */
    public static KvResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed KvResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The KvResult
     */
    public static KvResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new KvResult(message, errorCode, attributes);
    }
  }

  /** Result of a key-value list operation. */
  class KvListResult extends OperationResult {
    private final List<String> keys;
    private final boolean hasMoreResults;

    private KvListResult(
        String message, List<String> keys, boolean hasMoreResults, Map<String, String> attributes) {
      super(message, attributes);
      this.keys = keys;
      this.hasMoreResults = hasMoreResults;
    }

    private KvListResult(String message, String errorCode, Map<String, String> attributes) {
      super(message, errorCode, attributes);
      this.keys = List.of();
      this.hasMoreResults = false;
    }

    /**
     * Gets the list of keys.
     *
     * @return The list of keys
     */
    public List<String> getKeys() {
      return keys;
    }

    /**
     * Gets whether there are more results available.
     *
     * @return True if there are more results, false otherwise
     */
    public boolean hasMoreResults() {
      return hasMoreResults;
    }

    /**
     * Creates a successful KvListResult.
     *
     * @param message The success message
     * @param keys The list of keys
     * @param hasMoreResults Whether there are more results available
     * @return The KvListResult
     */
    public static KvListResult success(String message, List<String> keys, boolean hasMoreResults) {
      return success(message, keys, hasMoreResults, Map.of());
    }

    /**
     * Creates a successful KvListResult with attributes.
     *
     * @param message The success message
     * @param keys The list of keys
     * @param hasMoreResults Whether there are more results available
     * @param attributes Additional attributes for the result
     * @return The KvListResult
     */
    public static KvListResult success(
        String message, List<String> keys, boolean hasMoreResults, Map<String, String> attributes) {
      return new KvListResult(message, keys, hasMoreResults, attributes);
    }

    /**
     * Creates a failed KvListResult.
     *
     * @param message The error message
     * @param errorCode The error code
     * @return The KvListResult
     */
    public static KvListResult failure(String message, String errorCode) {
      return failure(message, errorCode, Map.of());
    }

    /**
     * Creates a failed KvListResult with attributes.
     *
     * @param message The error message
     * @param errorCode The error code
     * @param attributes Additional attributes for the result
     * @return The KvListResult
     */
    public static KvListResult failure(
        String message, String errorCode, Map<String, String> attributes) {
      return new KvListResult(message, errorCode, attributes);
    }
  }
}
