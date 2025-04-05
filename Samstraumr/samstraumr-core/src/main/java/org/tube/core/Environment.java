/*
 * Copyright (c) 2025 Eric C. Mumford (@heymumford) - https://github.com/heymumford
 * Gemini Deep Research, Claude 3.7.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * System environment information provider for runtime context awareness.
 */

package org.tube.core;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * Provides comprehensive system environment information.
 *
 * <p>This class abstracts system environment details, enabling tubes to be aware of their execution
 * context and adapt accordingly. It collects hardware, OS, and network details to create a unique
 * environmental fingerprint.
 */
public class Environment {
  private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);
  private static final String UNKNOWN = "unknown";

  private final SystemInfo systemInfo;
  private final Map<String, Object> environmentParameters;
  private final String parametersJson;
  private final String environmentId;
  private final Instant creationTime;

  /** Creates a new environment instance and collects system information. */
  public Environment() {
    this.creationTime = Instant.now();
    this.systemInfo = new SystemInfo();
    this.environmentParameters = collectEnvironmentParameters();
    this.parametersJson = convertToJson(environmentParameters);
    this.environmentId = generateEnvironmentId(parametersJson);

    LOGGER.info("Environment initialized successfully");
  }

  /**
   * Gets the parameters describing this environment.
   *
   * @return a map of environment parameters
   */
  public Map<String, Object> getParameters() {
    return new HashMap<>(environmentParameters);
  }

  /**
   * Gets a JSON representation of the environment parameters.
   *
   * @return a JSON string
   */
  public String getParametersJson() {
    return parametersJson;
  }

  /**
   * Gets the unique identifier for this environment.
   *
   * @return the environment ID
   */
  public String getEnvironmentId() {
    return environmentId;
  }

  /**
   * Gets the time when this environment was created.
   *
   * @return the creation time
   */
  public Instant getCreationTime() {
    return creationTime;
  }

  /**
   * Collects environment parameters from the system.
   *
   * @return a map of environment parameters
   */
  private Map<String, Object> collectEnvironmentParameters() {
    Map<String, Object> parameters = new HashMap<>();

    try {
      // System and OS info
      parameters.put("hostname", getHostname());
      parameters.put("user", System.getProperty("user.name"));
      parameters.put("javaVersion", System.getProperty("java.version"));
      parameters.put("time", creationTime.toString());

      // Hardware info from OSHI
      HardwareAbstractionLayer hardware = systemInfo.getHardware();
      OperatingSystem os = systemInfo.getOperatingSystem();

      // Operating system details
      Map<String, Object> osInfo = new HashMap<>();
      osInfo.put("name", os.getFamily());
      osInfo.put("version", os.getVersionInfo().getVersion());
      osInfo.put("buildNumber", os.getVersionInfo().getBuildNumber());
      parameters.put("os", osInfo);

      // CPU info
      CentralProcessor processor = hardware.getProcessor();
      Map<String, Object> cpuInfo = new HashMap<>();
      cpuInfo.put("model", processor.getProcessorIdentifier().getName());
      cpuInfo.put("cores", processor.getLogicalProcessorCount());
      cpuInfo.put("architecture", processor.getProcessorIdentifier().getMicroarchitecture());
      parameters.put("cpu", cpuInfo);

      // Memory info
      GlobalMemory memory = hardware.getMemory();
      Map<String, Object> memoryInfo = new HashMap<>();
      memoryInfo.put("total", FormatUtil.formatBytes(memory.getTotal()));
      memoryInfo.put("available", FormatUtil.formatBytes(memory.getAvailable()));
      parameters.put("memory", memoryInfo);

      // Computer system info
      ComputerSystem computerSystem = hardware.getComputerSystem();
      Map<String, Object> systemInfo = new HashMap<>();
      systemInfo.put("manufacturer", computerSystem.getManufacturer());
      systemInfo.put("model", computerSystem.getModel());
      parameters.put("system", systemInfo);

      // Network info
      List<NetworkIF> networks = hardware.getNetworkIFs();
      if (!networks.isEmpty()) {
        NetworkIF primaryNetwork = networks.get(0);
        Map<String, Object> networkInfo = new HashMap<>();
        networkInfo.put("name", primaryNetwork.getName());
        networkInfo.put("displayName", primaryNetwork.getDisplayName());
        networkInfo.put("macAddress", primaryNetwork.getMacaddr());
        parameters.put("network", networkInfo);
      }

      // Disk info
      List<HWDiskStore> disks = hardware.getDiskStores();
      if (!disks.isEmpty()) {
        HWDiskStore primaryDisk = disks.get(0);
        Map<String, Object> diskInfo = new HashMap<>();
        diskInfo.put("name", primaryDisk.getName());
        diskInfo.put("model", primaryDisk.getModel());
        diskInfo.put("size", FormatUtil.formatBytes(primaryDisk.getSize()));
        parameters.put("disk", diskInfo);
      }
    } catch (Exception e) {
      LOGGER.warn("Failed to collect some environment information", e);
      parameters.put(
          "error", "Failed to collect complete environment information: " + e.getMessage());
    }

    return parameters;
  }

  /**
   * Gets the hostname of the current machine.
   *
   * @return the hostname or "unknown" if it cannot be determined
   */
  private String getHostname() {
    try {
      return InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      LOGGER.warn("Failed to get hostname", e);
      return UNKNOWN;
    }
  }

  /**
   * Converts a map to a JSON string.
   *
   * @param map the map to convert
   * @return a JSON representation of the map
   */
  private String convertToJson(Map<String, Object> map) {
    try {
      JsonMapper mapper =
          JsonMapper.builder()
              .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
              .build();
      return mapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      LOGGER.error("Failed to convert environment parameters to JSON", e);
      return "{}";
    }
  }

  /**
   * Generates a unique environment ID based on the parameters.
   *
   * @param parametersJson JSON representation of the parameters
   * @return SHA-256 hash of the parameters
   */
  private String generateEnvironmentId(String parametersJson) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(parametersJson.getBytes("UTF-8"));

      // Convert byte array to hex string
      StringBuilder hexString = new StringBuilder();
      for (byte b : hash) {
        String hex = Integer.toHexString(0xff & b);
        if (hex.length() == 1) {
          hexString.append('0');
        }
        hexString.append(hex);
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      LOGGER.error("Failed to generate environment ID", e);
      return UNKNOWN;
    }
  }

  @Override
  public String toString() {
    return "Environment[id=" + environmentId + ", creationTime=" + creationTime + "]";
  }
}
