package org.samstraumr.tube.test;

import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TagFilter;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Test runner utility that allows running tests with either industry-standard or
 * Samstraumr-specific terminology.
 */
public class RunTests {

    private static final Map<String, String> TERMINOLOGY_MAP = createTerminologyMap();

    private static Map<String, String> createTerminologyMap() {
        Map<String, String> map = new HashMap<>();
        // Industry standard to Samstraumr mapping
        map.put("smoke", "orchestration");
        map.put("unit", "tube");
        map.put("component", "composite");
        map.put("integration", "flow");
        map.put("api", "machine");
        map.put("system", "stream");
        map.put("endtoend", "acceptance");
        map.put("property", "adaptation");
        
        // Also add the reverse mappings
        map.put("orchestration", "smoke");
        map.put("tube", "unit");
        map.put("composite", "component");
        map.put("flow", "integration");
        map.put("machine", "api");
        map.put("stream", "system");
        map.put("acceptance", "endtoend");
        map.put("adaptation", "property");
        return map;
    }

    /**
     * Run tests with the specified tag.
     *
     * @param tag The tag to filter tests by
     * @param includeEquivalent Whether to include tests with equivalent tags in the other terminology
     */
    public static TestExecutionSummary runTestsWithTag(String tag, boolean includeEquivalent) {
        LauncherDiscoveryRequestBuilder requestBuilder = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage("org.samstraumr"));
        
        if (includeEquivalent && TERMINOLOGY_MAP.containsKey(tag.toLowerCase())) {
            String equivalentTag = TERMINOLOGY_MAP.get(tag.toLowerCase());
            requestBuilder.filters(TagFilter.includeTags(tag, equivalentTag));
        } else {
            requestBuilder.filters(TagFilter.includeTags(tag));
        }
        
        LauncherDiscoveryRequest request = requestBuilder.build();
        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
        
        return listener.getSummary();
    }
    
    /**
     * Run tests with the specified tag and print results to the console.
     *
     * @param tag The tag to filter tests by
     * @param includeEquivalent Whether to include tests with equivalent tags in the other terminology
     */
    public static void runAndPrintResults(String tag, boolean includeEquivalent) {
        TestExecutionSummary summary = runTestsWithTag(tag, includeEquivalent);
        
        PrintWriter out = new PrintWriter(System.out);
        summary.printTo(out);
        out.flush();
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: RunTests <tag> [includeEquivalent]");
            System.exit(1);
        }
        
        String tag = args[0];
        boolean includeEquivalent = args.length > 1 && Boolean.parseBoolean(args[1]);
        
        runAndPrintResults(tag, includeEquivalent);
    }
}