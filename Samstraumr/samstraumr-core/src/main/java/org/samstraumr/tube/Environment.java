package org.samstraumr.tube;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class Environment {
    private static final Logger logger = LoggerFactory.getLogger(Environment.class);
    private static final String UNKNOWN = "unknown";

    private final SystemInfo systemInfo;
    private final HardwareAbstractionLayer hardware;
    private final OperatingSystem os;
    private final JsonMapper objectMapper;

    public Environment() {
        SystemInfo si = null;
        HardwareAbstractionLayer hal = null;
        OperatingSystem operatingSystem = null;
        JsonMapper mapper = null;

        try {
            si = new SystemInfo();
            hal = si.getHardware();
            operatingSystem = si.getOperatingSystem();
            mapper = JsonMapper.builder()
                    .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                    .build();
            logger.info("Environment initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Environment", e);
        }

        this.systemInfo = si;
        this.hardware = hal;
        this.os = operatingSystem;
        this.objectMapper = mapper;
    }

    public String getParameters() {
        if (systemInfo == null || hardware == null || os == null || objectMapper == null) {
            return "{\"error\": \"Environment initialization failed\"}";
        }

        EnvironmentInfo info = new EnvironmentInfo(
                getHostName(),
                getMacAddress(),
                getCpuModel(),
                getCpuCores(),
                getCpuThreads(),
                getTotalMemory(),
                getOsName(),
                getOsVersion(),
                getPrimaryIPAddress(),
                getVirtualizationStatus(),
                getDiskInfo()
        );

        try {
            return objectMapper.writeValueAsString(info);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize environment parameters to JSON", e);
            return "{\"error\": \"Failed to serialize environment parameters to JSON\"}";
        }
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.warn("Failed to get hostname", e);
            return UNKNOWN;
        }
    }

    private String getMacAddress() {
        if (hardware == null) return UNKNOWN;
        try {
            List<NetworkIF> networkIFs = hardware.getNetworkIFs();
            for (NetworkIF net : networkIFs) {
                if (!net.queryNetworkInterface().isLoopback() && net.getMacaddr() != null && !net.getMacaddr().isEmpty()) {
                    return net.getMacaddr();
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get MAC address", e);
        }
        return UNKNOWN;
    }

    private String getCpuModel() {
        if (hardware == null) return UNKNOWN;
        CentralProcessor processor = hardware.getProcessor();
        return processor != null ? processor.getProcessorIdentifier().getName() : UNKNOWN;
    }

    private int getCpuCores() {
        if (hardware == null) return 0;
        CentralProcessor processor = hardware.getProcessor();
        return processor != null ? processor.getPhysicalProcessorCount() : 0;
    }

    private int getCpuThreads() {
        if (hardware == null) return 0;
        CentralProcessor processor = hardware.getProcessor();
        return processor != null ? processor.getLogicalProcessorCount() : 0;
    }

    private String getTotalMemory() {
        if (hardware == null) return UNKNOWN;
        GlobalMemory memory = hardware.getMemory();
        return memory != null ? FormatUtil.formatBytes(memory.getTotal()) : UNKNOWN;
    }

    private String getOsName() {
        return os != null ? os.getFamily() : UNKNOWN;
    }

    private String getOsVersion() {
        return os != null ? os.getVersionInfo().getVersion() : UNKNOWN;
    }

    private String getPrimaryIPAddress() {
        if (hardware == null) return UNKNOWN;
        try {
            List<NetworkIF> networkIFs = hardware.getNetworkIFs();
            for (NetworkIF net : networkIFs) {
                if (!net.queryNetworkInterface().isLoopback() && net.getIPv4addr().length > 0) {
                    return net.getIPv4addr()[0];
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to get primary IP address", e);
        }
        return UNKNOWN;
    }

    private String getVirtualizationStatus() {
        if (hardware == null) return UNKNOWN;
        try {
            String manufacturer = hardware.getComputerSystem().getManufacturer().toLowerCase();
            if (manufacturer.contains("vmware") || manufacturer.contains("virtual") ||
                    manufacturer.contains("kvm") || manufacturer.contains("hyper-v") ||
                    manufacturer.contains("qemu")) {
                return "Virtual";
            }
        } catch (Exception e) {
            logger.warn("Failed to get virtualization status", e);
        }
        return "Physical";
    }

    private String getDiskInfo() {
        if (hardware == null) return UNKNOWN;
        try {
            List<HWDiskStore> diskStores = hardware.getDiskStores();
            if (!diskStores.isEmpty()) {
                HWDiskStore disk = diskStores.get(0);
                String type = disk.getModel();
                String size = FormatUtil.formatBytesDecimal(disk.getSize());
                return type + ":" + size;
            }
        } catch (Exception e) {
            logger.warn("Failed to get disk info", e);
        }
        return UNKNOWN;
    }

    public String getEnvironmentHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(getParameters().getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static class EnvironmentInfo {
        private final String hostname;
        private final String macAddress;
        private final String cpuModel;
        private final int cpuCores;
        private final int cpuThreads;
        private final String totalMemory;
        private final String osName;
        private final String osVersion;
        private final String primaryIP;
        private final String virtualization;
        private final String disk;

        public EnvironmentInfo(String hostname, String macAddress, String cpuModel, int cpuCores, int cpuThreads,
                               String totalMemory, String osName, String osVersion, String primaryIP,
                               String virtualization, String disk) {
            this.hostname = hostname;
            this.macAddress = macAddress;
            this.cpuModel = cpuModel;
            this.cpuCores = cpuCores;
            this.cpuThreads = cpuThreads;
            this.totalMemory = totalMemory;
            this.osName = osName;
            this.osVersion = osVersion;
            this.primaryIP = primaryIP;
            this.virtualization = virtualization;
            this.disk = disk;
        }
        // Add getters for all fields
        public String getHostname() { return hostname; }
        public String getMacAddress() { return macAddress; }
        public String getCpuModel() { return cpuModel; }
        public int getCpuCores() { return cpuCores; }
        public int getCpuThreads() { return cpuThreads; }
        public String getTotalMemory() { return totalMemory; }
        public String getOsName() { return osName; }
        public String getOsVersion() { return osVersion; }
        public String getPrimaryIP() { return primaryIP; }
        public String getVirtualization() { return virtualization; }
        public String getDisk() { return disk; }
    }
}