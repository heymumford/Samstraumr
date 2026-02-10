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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.s8r.application.port.LoggerPort;
import org.s8r.application.port.storage.StoragePort;
import org.s8r.application.port.storage.StoragePort.*;

class StorageServiceTest {

  @Mock private StoragePort storagePort;

  @Mock private LoggerPort logger;

  private StorageService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new StorageService(storagePort, logger);
  }

  @Test
  void createContainer_shouldReturnContainer_whenCreationSucceeds() {
    // Arrange
    String containerName = "test-container";
    Container mockContainer = mock(Container.class);

    when(storagePort.createContainer(containerName))
        .thenReturn(ContainerResult.success("Container created successfully", mockContainer));

    // Act
    Optional<Container> result = service.createContainer(containerName);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockContainer, result.get());
    verify(logger).debug(contains("Creating container"), eq(containerName));
    verify(logger).info(contains("Container created successfully"), eq(containerName));
  }

  @Test
  void createContainer_shouldReturnEmpty_whenCreationFails() {
    // Arrange
    String containerName = "test-container";

    when(storagePort.createContainer(containerName))
        .thenReturn(ContainerResult.failure("Container already exists", "CONTAINER_EXISTS"));

    // Act
    Optional<Container> result = service.createContainer(containerName);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Creating container"), eq(containerName));
    verify(logger).warn(contains("Failed to create container"), eq(containerName), anyString());
  }

  @Test
  void createContainer_withOptions_shouldReturnContainer_whenCreationSucceeds() {
    // Arrange
    String containerName = "test-container";
    ContainerOptions options = mock(ContainerOptions.class);
    Container mockContainer = mock(Container.class);

    when(storagePort.createContainer(containerName, options))
        .thenReturn(ContainerResult.success("Container created successfully", mockContainer));

    // Act
    Optional<Container> result = service.createContainer(containerName, options);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockContainer, result.get());
    verify(logger).debug(contains("Creating container"), eq(containerName));
    verify(logger).info(contains("Container created successfully"), eq(containerName));
  }

  @Test
  void deleteContainer_shouldReturnTrue_whenDeletionSucceeds() {
    // Arrange
    String containerName = "test-container";
    boolean deleteContents = true;

    when(storagePort.deleteContainer(containerName, deleteContents))
        .thenReturn(OperationResult.success("Container deleted successfully"));

    // Act
    boolean result = service.deleteContainer(containerName, deleteContents);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Deleting container"), eq(containerName), eq(deleteContents));
    verify(logger).info(contains("Container deleted successfully"), eq(containerName));
  }

  @Test
  void deleteContainer_shouldReturnFalse_whenDeletionFails() {
    // Arrange
    String containerName = "test-container";
    boolean deleteContents = false;

    when(storagePort.deleteContainer(containerName, deleteContents))
        .thenReturn(OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    boolean result = service.deleteContainer(containerName, deleteContents);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Deleting container"), eq(containerName), eq(deleteContents));
    verify(logger).warn(contains("Failed to delete container"), eq(containerName), anyString());
  }

  @Test
  void getContainer_shouldReturnContainer_whenContainerExists() {
    // Arrange
    String containerName = "test-container";
    Container mockContainer = mock(Container.class);

    when(storagePort.getContainer(containerName))
        .thenReturn(ContainerResult.success("Container retrieved successfully", mockContainer));

    // Act
    Optional<Container> result = service.getContainer(containerName);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockContainer, result.get());
    verify(logger).debug(contains("Getting container"), eq(containerName));
    verify(logger).debug(contains("Container retrieved successfully"), eq(containerName));
  }

  @Test
  void getContainer_shouldReturnEmpty_whenContainerDoesNotExist() {
    // Arrange
    String containerName = "non-existent-container";

    when(storagePort.getContainer(containerName))
        .thenReturn(ContainerResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    Optional<Container> result = service.getContainer(containerName);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Getting container"), eq(containerName));
    verify(logger).warn(contains("Failed to get container"), eq(containerName), anyString());
  }

  @Test
  void listContainers_shouldReturnContainers_whenListingSucceeds() {
    // Arrange
    Optional<String> prefix = Optional.of("test-");
    Optional<Integer> maxResults = Optional.of(10);
    List<Container> mockContainers = Arrays.asList(mock(Container.class), mock(Container.class));

    when(storagePort.listContainers(prefix, maxResults))
        .thenReturn(
            ContainerListResult.success("Containers listed successfully", mockContainers, false));

    // Act
    List<Container> result = service.listContainers(prefix, maxResults);

    // Assert
    assertEquals(mockContainers, result);
    verify(logger).debug(contains("Listing containers"), any(), any());
    verify(logger).debug(contains("Listed"), eq(mockContainers.size()));
  }

  @Test
  void listContainers_shouldReturnEmptyList_whenListingFails() {
    // Arrange
    Optional<String> prefix = Optional.empty();
    Optional<Integer> maxResults = Optional.empty();

    when(storagePort.listContainers(prefix, maxResults))
        .thenReturn(ContainerListResult.failure("Failed to list containers", "ERROR"));

    // Act
    List<Container> result = service.listContainers(prefix, maxResults);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger).debug(contains("Listing containers"), any(), any());
    verify(logger).warn(contains("Failed to list containers"), anyString());
  }

  @Test
  void storeObject_shouldReturnObject_whenStorageSucceeds() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    byte[] data = "test data".getBytes();
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.storeObject(containerName, key, data))
        .thenReturn(ObjectResult.success("Object stored successfully", mockObject));

    // Act
    Optional<StorageObject> result = service.storeObject(containerName, key, data);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockObject, result.get());
    verify(logger).debug(contains("Storing object"), eq(containerName), eq(key), eq(data.length));
    verify(logger).debug(contains("Object stored successfully"), eq(containerName), eq(key));
  }

  @Test
  void storeObject_shouldReturnEmpty_whenStorageFails() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    byte[] data = "test data".getBytes();

    when(storagePort.storeObject(containerName, key, data))
        .thenReturn(ObjectResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    Optional<StorageObject> result = service.storeObject(containerName, key, data);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Storing object"), eq(containerName), eq(key), eq(data.length));
    verify(logger)
        .warn(contains("Failed to store object"), eq(containerName), eq(key), anyString());
  }

  @Test
  void storeObject_withOptions_shouldReturnObject_whenStorageSucceeds() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    byte[] data = "test data".getBytes();
    ObjectOptions options = mock(ObjectOptions.class);
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.storeObject(containerName, key, data, options))
        .thenReturn(ObjectResult.success("Object stored successfully", mockObject));

    // Act
    Optional<StorageObject> result = service.storeObject(containerName, key, data, options);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockObject, result.get());
    verify(logger).debug(contains("Storing object with options"), eq(containerName), eq(key));
    verify(logger)
        .debug(contains("Object stored successfully with options"), eq(containerName), eq(key));
  }

  @Test
  void getObject_shouldReturnObject_whenObjectExists() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.getObject(containerName, key))
        .thenReturn(ObjectResult.success("Object retrieved successfully", mockObject));

    // Act
    Optional<StorageObject> result = service.getObject(containerName, key);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockObject, result.get());
    verify(logger).debug(contains("Getting object"), eq(containerName), eq(key));
    verify(logger).debug(contains("Object retrieved successfully"), eq(containerName), eq(key));
  }

  @Test
  void getObject_shouldReturnEmpty_whenObjectDoesNotExist() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";

    when(storagePort.getObject(containerName, key))
        .thenReturn(ObjectResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    Optional<StorageObject> result = service.getObject(containerName, key);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Getting object"), eq(containerName), eq(key));
    verify(logger).warn(contains("Failed to get object"), eq(containerName), eq(key), anyString());
  }

  @Test
  void getObject_withOutputStream_shouldReturnTrue_whenObjectExists() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    OutputStream outputStream = new ByteArrayOutputStream();

    when(storagePort.getObject(containerName, key, outputStream))
        .thenReturn(OperationResult.success("Object written to stream successfully"));

    // Act
    boolean result = service.getObject(containerName, key, outputStream);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Getting object to stream"), eq(containerName), eq(key));
    verify(logger)
        .debug(contains("Object retrieved successfully to stream"), eq(containerName), eq(key));
  }

  @Test
  void getObject_withOutputStream_shouldReturnFalse_whenObjectDoesNotExist() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";
    OutputStream outputStream = new ByteArrayOutputStream();

    when(storagePort.getObject(containerName, key, outputStream))
        .thenReturn(OperationResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    boolean result = service.getObject(containerName, key, outputStream);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Getting object to stream"), eq(containerName), eq(key));
    verify(logger)
        .warn(contains("Failed to get object to stream"), eq(containerName), eq(key), anyString());
  }

  @Test
  void deleteObject_shouldReturnTrue_whenDeletionSucceeds() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";

    when(storagePort.deleteObject(containerName, key))
        .thenReturn(OperationResult.success("Object deleted successfully"));

    // Act
    boolean result = service.deleteObject(containerName, key);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Deleting object"), eq(containerName), eq(key));
    verify(logger).debug(contains("Object deleted successfully"), eq(containerName), eq(key));
  }

  @Test
  void deleteObject_shouldReturnFalse_whenDeletionFails() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";

    when(storagePort.deleteObject(containerName, key))
        .thenReturn(OperationResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    boolean result = service.deleteObject(containerName, key);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Deleting object"), eq(containerName), eq(key));
    verify(logger)
        .warn(contains("Failed to delete object"), eq(containerName), eq(key), anyString());
  }

  @Test
  void getObjectMetadata_shouldReturnMetadata_whenObjectExists() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    Map<String, String> metadata = Map.of("content-type", "text/plain", "custom", "value");

    when(storagePort.getObjectMetadata(containerName, key))
        .thenReturn(
            ObjectMetadataResult.success(
                "Object metadata retrieved successfully",
                Optional.of("text/plain"),
                Optional.of(100L),
                Optional.of(Instant.now()),
                metadata,
                Optional.of("hash")));

    // Act
    Optional<Map<String, String>> result = service.getObjectMetadata(containerName, key);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(metadata, result.get());
    verify(logger).debug(contains("Getting object metadata"), eq(containerName), eq(key));
    verify(logger)
        .debug(contains("Object metadata retrieved successfully"), eq(containerName), eq(key));
  }

  @Test
  void getObjectMetadata_shouldReturnEmpty_whenObjectDoesNotExist() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";

    when(storagePort.getObjectMetadata(containerName, key))
        .thenReturn(ObjectMetadataResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    Optional<Map<String, String>> result = service.getObjectMetadata(containerName, key);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Getting object metadata"), eq(containerName), eq(key));
    verify(logger)
        .warn(contains("Failed to get object metadata"), eq(containerName), eq(key), anyString());
  }

  @Test
  void updateObjectMetadata_shouldReturnTrue_whenUpdateSucceeds() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    Map<String, String> metadata = Map.of("content-type", "text/plain", "custom", "value");
    boolean replace = true;

    when(storagePort.updateObjectMetadata(containerName, key, metadata, replace))
        .thenReturn(OperationResult.success("Object metadata updated successfully"));

    // Act
    boolean result = service.updateObjectMetadata(containerName, key, metadata, replace);

    // Assert
    assertTrue(result);
    verify(logger)
        .debug(contains("Updating object metadata"), eq(containerName), eq(key), eq(replace));
    verify(logger)
        .debug(contains("Object metadata updated successfully"), eq(containerName), eq(key));
  }

  @Test
  void updateObjectMetadata_shouldReturnFalse_whenUpdateFails() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";
    Map<String, String> metadata = Map.of("content-type", "text/plain", "custom", "value");
    boolean replace = true;

    when(storagePort.updateObjectMetadata(containerName, key, metadata, replace))
        .thenReturn(OperationResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    boolean result = service.updateObjectMetadata(containerName, key, metadata, replace);

    // Assert
    assertFalse(result);
    verify(logger)
        .debug(contains("Updating object metadata"), eq(containerName), eq(key), eq(replace));
    verify(logger)
        .warn(
            contains("Failed to update object metadata"), eq(containerName), eq(key), anyString());
  }

  @Test
  void listObjects_shouldReturnObjects_whenListingSucceeds() {
    // Arrange
    String containerName = "test-container";
    Optional<String> prefix = Optional.of("test-");
    Optional<String> delimiter = Optional.of("/");
    Optional<Integer> maxResults = Optional.of(10);
    List<StorageObject> mockObjects =
        Arrays.asList(mock(StorageObject.class), mock(StorageObject.class));

    when(storagePort.listObjects(containerName, prefix, delimiter, maxResults))
        .thenReturn(
            ObjectListResult.success(
                "Objects listed successfully", mockObjects, false, Collections.emptyList()));

    // Act
    List<StorageObject> result = service.listObjects(containerName, prefix, delimiter, maxResults);

    // Assert
    assertEquals(mockObjects, result);
    verify(logger).debug(contains("Listing objects"), eq(containerName), any(), any(), any());
    verify(logger).debug(contains("Listed"), eq(mockObjects.size()), eq(containerName));
  }

  @Test
  void listObjects_shouldReturnEmptyList_whenListingFails() {
    // Arrange
    String containerName = "test-container";
    Optional<String> prefix = Optional.empty();
    Optional<String> delimiter = Optional.empty();
    Optional<Integer> maxResults = Optional.empty();

    when(storagePort.listObjects(containerName, prefix, delimiter, maxResults))
        .thenReturn(ObjectListResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    List<StorageObject> result = service.listObjects(containerName, prefix, delimiter, maxResults);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger).debug(contains("Listing objects"), eq(containerName), any(), any(), any());
    verify(logger).warn(contains("Failed to list objects"), eq(containerName), anyString());
  }

  @Test
  void listPrefixes_shouldReturnPrefixes_whenListingSucceeds() {
    // Arrange
    String containerName = "test-container";
    Optional<String> prefix = Optional.of("test-");
    String delimiter = "/";
    List<String> mockPrefixes = Arrays.asList("test-a/", "test-b/");

    when(storagePort.listObjects(containerName, prefix, Optional.of(delimiter), Optional.empty()))
        .thenReturn(
            ObjectListResult.success(
                "Objects listed successfully", Collections.emptyList(), false, mockPrefixes));

    // Act
    List<String> result = service.listPrefixes(containerName, prefix, delimiter);

    // Assert
    assertEquals(mockPrefixes, result);
    verify(logger).debug(contains("Listing prefixes"), eq(containerName), any(), eq(delimiter));
    verify(logger).debug(contains("Listed"), eq(mockPrefixes.size()), eq(containerName));
  }

  @Test
  void listPrefixes_shouldReturnEmptyList_whenListingFails() {
    // Arrange
    String containerName = "test-container";
    Optional<String> prefix = Optional.empty();
    String delimiter = "/";

    when(storagePort.listObjects(containerName, prefix, Optional.of(delimiter), Optional.empty()))
        .thenReturn(ObjectListResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    List<String> result = service.listPrefixes(containerName, prefix, delimiter);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger).debug(contains("Listing prefixes"), eq(containerName), any(), eq(delimiter));
    verify(logger).warn(contains("Failed to list prefixes"), eq(containerName), anyString());
  }

  @Test
  void copyObject_shouldReturnObject_whenCopySucceeds() {
    // Arrange
    String sourceContainer = "source-container";
    String sourceKey = "source-key";
    String destinationContainer = "dest-container";
    String destinationKey = "dest-key";
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.copyObject(sourceContainer, sourceKey, destinationContainer, destinationKey))
        .thenReturn(ObjectResult.success("Object copied successfully", mockObject));

    // Act
    Optional<StorageObject> result =
        service.copyObject(sourceContainer, sourceKey, destinationContainer, destinationKey);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockObject, result.get());
    verify(logger)
        .debug(
            contains("Copying object"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey));
    verify(logger)
        .debug(
            contains("Object copied successfully"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey));
  }

  @Test
  void copyObject_shouldReturnEmpty_whenCopyFails() {
    // Arrange
    String sourceContainer = "source-container";
    String sourceKey = "non-existent-key";
    String destinationContainer = "dest-container";
    String destinationKey = "dest-key";

    when(storagePort.copyObject(sourceContainer, sourceKey, destinationContainer, destinationKey))
        .thenReturn(ObjectResult.failure("Source object not found", "OBJECT_NOT_FOUND"));

    // Act
    Optional<StorageObject> result =
        service.copyObject(sourceContainer, sourceKey, destinationContainer, destinationKey);

    // Assert
    assertFalse(result.isPresent());
    verify(logger)
        .debug(
            contains("Copying object"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey));
    verify(logger)
        .warn(
            contains("Failed to copy object"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey),
            anyString());
  }

  @Test
  void moveObject_shouldReturnObject_whenMoveSucceeds() {
    // Arrange
    String sourceContainer = "source-container";
    String sourceKey = "source-key";
    String destinationContainer = "dest-container";
    String destinationKey = "dest-key";
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.moveObject(sourceContainer, sourceKey, destinationContainer, destinationKey))
        .thenReturn(ObjectResult.success("Object moved successfully", mockObject));

    // Act
    Optional<StorageObject> result =
        service.moveObject(sourceContainer, sourceKey, destinationContainer, destinationKey);

    // Assert
    assertTrue(result.isPresent());
    assertSame(mockObject, result.get());
    verify(logger)
        .debug(
            contains("Moving object"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey));
    verify(logger)
        .debug(
            contains("Object moved successfully"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey));
  }

  @Test
  void moveObject_shouldReturnEmpty_whenMoveFails() {
    // Arrange
    String sourceContainer = "source-container";
    String sourceKey = "non-existent-key";
    String destinationContainer = "dest-container";
    String destinationKey = "dest-key";

    when(storagePort.moveObject(sourceContainer, sourceKey, destinationContainer, destinationKey))
        .thenReturn(ObjectResult.failure("Source object not found", "OBJECT_NOT_FOUND"));

    // Act
    Optional<StorageObject> result =
        service.moveObject(sourceContainer, sourceKey, destinationContainer, destinationKey);

    // Assert
    assertFalse(result.isPresent());
    verify(logger)
        .debug(
            contains("Moving object"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey));
    verify(logger)
        .warn(
            contains("Failed to move object"),
            eq(sourceContainer),
            eq(sourceKey),
            eq(destinationContainer),
            eq(destinationKey),
            anyString());
  }

  @Test
  void generatePresignedUrl_shouldReturnUrl_whenGenerationSucceeds() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    UrlOperation operation = UrlOperation.GET;
    String url = "https://example.com/presigned-url";
    Instant expiresAt = Instant.now().plus(Duration.ofHours(1));

    when(storagePort.generatePresignedUrl(containerName, key, operation, Duration.ofHours(1)))
        .thenReturn(UrlResult.success("URL generated successfully", url, expiresAt));

    // Act
    Optional<String> result = service.generatePresignedUrl(containerName, key, operation);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(url, result.get());
    verify(logger)
        .debug(
            contains("Generating presigned URL"), eq(containerName), eq(key), eq(operation), any());
    verify(logger)
        .debug(contains("Presigned URL generated successfully"), eq(containerName), eq(key));
  }

  @Test
  void generatePresignedUrl_shouldReturnEmpty_whenGenerationFails() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    UrlOperation operation = UrlOperation.GET;
    Duration expiration = Duration.ofHours(1);

    when(storagePort.generatePresignedUrl(containerName, key, operation, expiration))
        .thenReturn(UrlResult.failure("Container not public", "CONTAINER_NOT_PUBLIC"));

    // Act
    Optional<String> result =
        service.generatePresignedUrl(containerName, key, operation, expiration);

    // Assert
    assertFalse(result.isPresent());
    verify(logger)
        .debug(
            contains("Generating presigned URL"),
            eq(containerName),
            eq(key),
            eq(operation),
            eq(expiration));
    verify(logger)
        .warn(
            contains("Failed to generate presigned URL"), eq(containerName), eq(key), anyString());
  }

  @Test
  void setRetentionPolicy_shouldReturnTrue_whenSettingSucceeds() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    Duration retentionPeriod = Duration.ofDays(30);

    when(storagePort.setRetentionPolicy(containerName, key, retentionPeriod))
        .thenReturn(OperationResult.success("Retention policy set successfully"));

    // Act
    boolean result = service.setRetentionPolicy(containerName, key, retentionPeriod);

    // Assert
    assertTrue(result);
    verify(logger)
        .debug(
            contains("Setting retention policy"), eq(containerName), eq(key), eq(retentionPeriod));
    verify(logger).debug(contains("Retention policy set successfully"), eq(containerName), eq(key));
  }

  @Test
  void setRetentionPolicy_shouldReturnFalse_whenSettingFails() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";
    Duration retentionPeriod = Duration.ofDays(30);

    when(storagePort.setRetentionPolicy(containerName, key, retentionPeriod))
        .thenReturn(OperationResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    boolean result = service.setRetentionPolicy(containerName, key, retentionPeriod);

    // Assert
    assertFalse(result);
    verify(logger)
        .debug(
            contains("Setting retention policy"), eq(containerName), eq(key), eq(retentionPeriod));
    verify(logger)
        .warn(contains("Failed to set retention policy"), eq(containerName), eq(key), anyString());
  }

  @Test
  void setContainerRetentionPolicy_shouldReturnTrue_whenSettingSucceeds() {
    // Arrange
    String containerName = "test-container";
    Duration retentionPeriod = Duration.ofDays(30);

    when(storagePort.setContainerRetentionPolicy(containerName, retentionPeriod))
        .thenReturn(OperationResult.success("Container retention policy set successfully"));

    // Act
    boolean result = service.setContainerRetentionPolicy(containerName, retentionPeriod);

    // Assert
    assertTrue(result);
    verify(logger)
        .debug(
            contains("Setting container retention policy"), eq(containerName), eq(retentionPeriod));
    verify(logger)
        .debug(contains("Container retention policy set successfully"), eq(containerName));
  }

  @Test
  void setContainerRetentionPolicy_shouldReturnFalse_whenSettingFails() {
    // Arrange
    String containerName = "non-existent-container";
    Duration retentionPeriod = Duration.ofDays(30);

    when(storagePort.setContainerRetentionPolicy(containerName, retentionPeriod))
        .thenReturn(OperationResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    boolean result = service.setContainerRetentionPolicy(containerName, retentionPeriod);

    // Assert
    assertFalse(result);
    verify(logger)
        .debug(
            contains("Setting container retention policy"), eq(containerName), eq(retentionPeriod));
    verify(logger)
        .warn(contains("Failed to set container retention policy"), eq(containerName), anyString());
  }

  @Test
  void storeKeyValue_shouldReturnTrue_whenStorageSucceeds() {
    // Arrange
    String key = "test-key";
    String value = "test-value";

    when(storagePort.storeKeyValue(key, value))
        .thenReturn(KvResult.success("Key-value pair stored successfully", value));

    // Act
    boolean result = service.storeKeyValue(key, value);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Storing key-value pair"), eq(key));
    verify(logger).debug(contains("Key-value pair stored successfully"), eq(key));
  }

  @Test
  void storeKeyValue_shouldReturnFalse_whenStorageFails() {
    // Arrange
    String key = "test-key";
    String value = "test-value";

    when(storagePort.storeKeyValue(key, value))
        .thenReturn(KvResult.failure("Storage error", "STORAGE_ERROR"));

    // Act
    boolean result = service.storeKeyValue(key, value);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Storing key-value pair"), eq(key));
    verify(logger).warn(contains("Failed to store key-value pair"), eq(key), anyString());
  }

  @Test
  void storeKeyValue_withTtl_shouldReturnTrue_whenStorageSucceeds() {
    // Arrange
    String key = "test-key";
    String value = "test-value";
    Duration ttl = Duration.ofMinutes(5);

    when(storagePort.storeKeyValue(key, value, ttl))
        .thenReturn(
            KvResult.success(
                "Key-value pair stored successfully", value, Optional.of(Instant.now().plus(ttl))));

    // Act
    boolean result = service.storeKeyValue(key, value, ttl);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Storing key-value pair with TTL"), eq(key), eq(ttl));
    verify(logger).debug(contains("Key-value pair with TTL stored successfully"), eq(key));
  }

  @Test
  void getKeyValue_shouldReturnValue_whenKeyExists() {
    // Arrange
    String key = "test-key";
    String value = "test-value";

    when(storagePort.getKeyValue(key))
        .thenReturn(KvResult.success("Key-value pair retrieved successfully", value));

    // Act
    Optional<String> result = service.getKeyValue(key);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(value, result.get());
    verify(logger).debug(contains("Getting key-value pair"), eq(key));
    verify(logger).debug(contains("Key-value pair retrieved successfully"), eq(key));
  }

  @Test
  void getKeyValue_shouldReturnEmpty_whenKeyDoesNotExist() {
    // Arrange
    String key = "non-existent-key";

    when(storagePort.getKeyValue(key))
        .thenReturn(KvResult.failure("Key not found", "KEY_NOT_FOUND"));

    // Act
    Optional<String> result = service.getKeyValue(key);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Getting key-value pair"), eq(key));
    verify(logger).warn(contains("Failed to get key-value pair"), eq(key), anyString());
  }

  @Test
  void deleteKeyValue_shouldReturnTrue_whenDeletionSucceeds() {
    // Arrange
    String key = "test-key";

    when(storagePort.deleteKeyValue(key))
        .thenReturn(OperationResult.success("Key-value pair deleted successfully"));

    // Act
    boolean result = service.deleteKeyValue(key);

    // Assert
    assertTrue(result);
    verify(logger).debug(contains("Deleting key-value pair"), eq(key));
    verify(logger).debug(contains("Key-value pair deleted successfully"), eq(key));
  }

  @Test
  void deleteKeyValue_shouldReturnFalse_whenDeletionFails() {
    // Arrange
    String key = "non-existent-key";

    when(storagePort.deleteKeyValue(key))
        .thenReturn(OperationResult.failure("Key not found", "KEY_NOT_FOUND"));

    // Act
    boolean result = service.deleteKeyValue(key);

    // Assert
    assertFalse(result);
    verify(logger).debug(contains("Deleting key-value pair"), eq(key));
    verify(logger).warn(contains("Failed to delete key-value pair"), eq(key), anyString());
  }

  @Test
  void updateKeyValue_shouldReturnNewValue_whenUpdateSucceeds() {
    // Arrange
    String key = "test-key";
    String oldValue = "old-value";
    String newValue = "new-value";
    Function<Optional<String>, String> transformFunction =
        value -> value.map(v -> v + "-updated").orElse("default");

    when(storagePort.updateKeyValue(eq(key), any()))
        .thenReturn(KvResult.success("Key-value pair updated successfully", newValue));

    // Act
    Optional<String> result = service.updateKeyValue(key, transformFunction);

    // Assert
    assertTrue(result.isPresent());
    assertEquals(newValue, result.get());
    verify(logger).debug(contains("Updating key-value pair"), eq(key));
    verify(logger).debug(contains("Key-value pair updated successfully"), eq(key));
  }

  @Test
  void updateKeyValue_shouldReturnEmpty_whenUpdateFails() {
    // Arrange
    String key = "test-key";
    Function<Optional<String>, String> transformFunction =
        value -> value.map(v -> v + "-updated").orElse("default");

    when(storagePort.updateKeyValue(eq(key), any()))
        .thenReturn(KvResult.failure("Update failed", "UPDATE_ERROR"));

    // Act
    Optional<String> result = service.updateKeyValue(key, transformFunction);

    // Assert
    assertFalse(result.isPresent());
    verify(logger).debug(contains("Updating key-value pair"), eq(key));
    verify(logger).warn(contains("Failed to update key-value pair"), eq(key), anyString());
  }

  @Test
  void listKeys_shouldReturnKeys_whenListingSucceeds() {
    // Arrange
    Optional<String> prefix = Optional.of("test-");
    Optional<Integer> maxResults = Optional.of(10);
    List<String> keys = Arrays.asList("test-key1", "test-key2");

    when(storagePort.listKeys(prefix, maxResults))
        .thenReturn(KvListResult.success("Keys listed successfully", keys, false));

    // Act
    List<String> result = service.listKeys(prefix, maxResults);

    // Assert
    assertEquals(keys, result);
    verify(logger).debug(contains("Listing keys"), any(), any());
    verify(logger).debug(contains("Listed"), eq(keys.size()));
  }

  @Test
  void listKeys_shouldReturnEmptyList_whenListingFails() {
    // Arrange
    Optional<String> prefix = Optional.empty();
    Optional<Integer> maxResults = Optional.empty();

    when(storagePort.listKeys(prefix, maxResults))
        .thenReturn(KvListResult.failure("Listing failed", "LIST_ERROR"));

    // Act
    List<String> result = service.listKeys(prefix, maxResults);

    // Assert
    assertTrue(result.isEmpty());
    verify(logger).debug(contains("Listing keys"), any(), any());
    verify(logger).warn(contains("Failed to list keys"), anyString());
  }

  @Test
  void createContainerAsync_shouldCompleteSuccessfully_whenCreationSucceeds() throws Exception {
    // Arrange
    String containerName = "test-container";
    Container mockContainer = mock(Container.class);

    when(storagePort.createContainer(containerName))
        .thenReturn(ContainerResult.success("Container created successfully", mockContainer));

    // Act
    CompletableFuture<Container> future = service.createContainerAsync(containerName);
    Container result =
        future.get(); // This will throw an exception if the future completes exceptionally

    // Assert
    assertSame(mockContainer, result);
    verify(logger).debug(contains("Creating container"), eq(containerName));
    verify(logger).info(contains("Container created successfully"), eq(containerName));
  }

  @Test
  void createContainerAsync_shouldCompleteExceptionally_whenCreationFails() {
    // Arrange
    String containerName = "test-container";

    when(storagePort.createContainer(containerName))
        .thenReturn(ContainerResult.failure("Container already exists", "CONTAINER_EXISTS"));

    // Act
    CompletableFuture<Container> future = service.createContainerAsync(containerName);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    verify(logger).debug(contains("Creating container"), eq(containerName));
    verify(logger).warn(contains("Failed to create container"), eq(containerName), anyString());
  }

  @Test
  void storeObjectAsync_shouldCompleteSuccessfully_whenStorageSucceeds() throws Exception {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    byte[] data = "test data".getBytes();
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.storeObject(containerName, key, data))
        .thenReturn(ObjectResult.success("Object stored successfully", mockObject));

    // Act
    CompletableFuture<StorageObject> future = service.storeObjectAsync(containerName, key, data);
    StorageObject result =
        future.get(); // This will throw an exception if the future completes exceptionally

    // Assert
    assertSame(mockObject, result);
    verify(logger).debug(contains("Storing object"), eq(containerName), eq(key), eq(data.length));
    verify(logger).debug(contains("Object stored successfully"), eq(containerName), eq(key));
  }

  @Test
  void storeObjectAsync_shouldCompleteExceptionally_whenStorageFails() {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    byte[] data = "test data".getBytes();

    when(storagePort.storeObject(containerName, key, data))
        .thenReturn(ObjectResult.failure("Container not found", "CONTAINER_NOT_FOUND"));

    // Act
    CompletableFuture<StorageObject> future = service.storeObjectAsync(containerName, key, data);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    verify(logger).debug(contains("Storing object"), eq(containerName), eq(key), eq(data.length));
    verify(logger)
        .warn(contains("Failed to store object"), eq(containerName), eq(key), anyString());
  }

  @Test
  void getObjectAsync_shouldCompleteSuccessfully_whenObjectExists() throws Exception {
    // Arrange
    String containerName = "test-container";
    String key = "test-key";
    StorageObject mockObject = mock(StorageObject.class);

    when(storagePort.getObject(containerName, key))
        .thenReturn(ObjectResult.success("Object retrieved successfully", mockObject));

    // Act
    CompletableFuture<StorageObject> future = service.getObjectAsync(containerName, key);
    StorageObject result =
        future.get(); // This will throw an exception if the future completes exceptionally

    // Assert
    assertSame(mockObject, result);
    verify(logger).debug(contains("Getting object"), eq(containerName), eq(key));
    verify(logger).debug(contains("Object retrieved successfully"), eq(containerName), eq(key));
  }

  @Test
  void getObjectAsync_shouldCompleteExceptionally_whenObjectDoesNotExist() {
    // Arrange
    String containerName = "test-container";
    String key = "non-existent-key";

    when(storagePort.getObject(containerName, key))
        .thenReturn(ObjectResult.failure("Object not found", "OBJECT_NOT_FOUND"));

    // Act
    CompletableFuture<StorageObject> future = service.getObjectAsync(containerName, key);

    // Assert
    ExecutionException exception = assertThrows(ExecutionException.class, future::get);
    assertTrue(exception.getCause() instanceof RuntimeException);
    verify(logger).debug(contains("Getting object"), eq(containerName), eq(key));
    verify(logger).warn(contains("Failed to get object"), eq(containerName), eq(key), anyString());
  }

  @Test
  void containerOptionsBuilder_shouldCreateOptions() {
    // Act
    ContainerOptions options =
        service
            .containerOptionsBuilder()
            .publicAccess(true)
            .addMetadata("key1", "value1")
            .addMetadata("key2", "value2")
            .build();

    // Assert
    assertTrue(options.isPublicAccess());
    assertEquals(Map.of("key1", "value1", "key2", "value2"), options.getMetadata());
  }

  @Test
  void objectOptionsBuilder_shouldCreateOptions() {
    // Act
    ObjectOptions options =
        service
            .objectOptionsBuilder()
            .contentType("text/plain")
            .addMetadata("key1", "value1")
            .encryptionMode(EncryptionMode.SERVER_SIDE)
            .ttl(Duration.ofHours(1))
            .build();

    // Assert
    assertEquals(Optional.of("text/plain"), options.getContentType());
    assertEquals(Map.of("key1", "value1"), options.getMetadata());
    assertEquals(Optional.of(EncryptionMode.SERVER_SIDE), options.getEncryptionMode());
    assertEquals(Optional.of(Duration.ofHours(1)), options.getTtl());
  }
}
