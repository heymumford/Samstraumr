package org.samstraumr.core;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Represents the environment in which a Tube operates.
 * This class provides methods to gather and hash environmental parameters.
 */
public class Environment {

    private static final String UNKNOWN_HOSTNAME = "unknown";
    private static final int MEGABYTES = 1024 * 1024;

    /**
     * Collects and returns environmental parameters as a formatted string.
     *
     * @return A string containing hostname, CPU type, CPU count, memory size, and OS version
     */
    public String getParameters() {
        return String.format("hostname:%s cpuType:%s cpuCount:%d memorySize:%dMB osVersion:%s",
                getHostName(),
                getCpuType(),
                getCpuCount(),
                getMemorySizeInMB(),
                getOsVersion());
    }

    /**
     * Gets the hostname of the machine.
     *
     * @return The hostname, or "unknown" if it cannot be determined
     */
    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return UNKNOWN_HOSTNAME;
        }
    }

    /**
     * Gets the CPU architecture.
     *
     * @return The CPU architecture as a string
     */
    private String getCpuType() {
        return System.getProperty("os.arch");
    }

    /**
     * Gets the number of available processors.
     *
     * @return The number of available processors
     */
    private int getCpuCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /**
     * Gets the maximum memory size in megabytes.
     *
     * @return The maximum memory size in MB
     */
    private long getMemorySizeInMB() {
        return Runtime.getRuntime().maxMemory() / MEGABYTES;
    }

    /**
     * Gets the OS name and version.
     *
     * @return A string containing the OS name and version
     */
    private String getOsVersion() {
        return System.getProperty("os.name") + " " + System.getProperty("os.version");
    }

    /**
     * Generates a hash of the environment parameters.
     * This could be used in Tube's unique ID generation.
     *
     * @return A SHA-256 hash of the environment parameters as a hexadecimal string
     */
    public String getEnvironmentHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(getParameters().getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param hash The byte array to convert
     * @return A hexadecimal string representation of the byte array
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}