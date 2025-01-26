package org.samstraumr.tube.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class SystemInfoUtility {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Method to get system information as a JsonNode
    public static JsonNode getSystemInfo() {
        Map<String, Object> systemDetails = new HashMap<>();
        systemDetails.put("CPU_Cores", Runtime.getRuntime().availableProcessors());
        systemDetails.put("Total_Memory", Runtime.getRuntime().totalMemory());
        systemDetails.put("Free_Memory", Runtime.getRuntime().freeMemory());
        systemDetails.put("Max_Memory", Runtime.getRuntime().maxMemory());
        return mapper.valueToTree(systemDetails);
    }

    // Additional system information retrieval methods can be added here
}
