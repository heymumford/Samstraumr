package org.s8r.architecture.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility for analyzing architectural compliance with Clean Architecture principles.
 * This class provides methods to verify package dependencies, detect circular dependencies,
 * and analyze the overall package structure.
 */
public class ArchitectureAnalyzer {

    /**
     * Dependency rule definition for Clean Architecture.
     */
    private static final Map<String, List<String>> ALLOWED_DEPENDENCIES = Map.of(
        "domain", List.of(),
        "application", List.of("domain"),
        "infrastructure", List.of("domain", "application"),
        "adapter", List.of("domain", "application")
    );
    
    /**
     * Checks if a package dependency from source to target violates Clean Architecture rules.
     *
     * @param sourcePackage The source package (e.g., "org.s8r.infrastructure")
     * @param targetPackage The target package (e.g., "org.s8r.domain")
     * @return true if the dependency is allowed, false otherwise
     */
    public static boolean isAllowedDependency(String sourcePackage, String targetPackage) {
        // Extract the layer names
        String sourceLayer = extractLayerName(sourcePackage);
        String targetLayer = extractLayerName(targetPackage);
        
        // Self-dependencies are always allowed
        if (sourceLayer.equals(targetLayer)) {
            return true;
        }
        
        // Check if the dependency is allowed according to Clean Architecture rules
        List<String> allowedTargets = ALLOWED_DEPENDENCIES.getOrDefault(sourceLayer, List.of());
        return allowedTargets.contains(targetLayer);
    }
    
    /**
     * Extracts the layer name (domain, application, infrastructure, adapter) from a package name.
     *
     * @param packageName The full package name
     * @return The layer name
     */
    private static String extractLayerName(String packageName) {
        if (packageName.contains("domain")) return "domain";
        if (packageName.contains("application")) return "application";
        if (packageName.contains("infrastructure")) return "infrastructure";
        if (packageName.contains("adapter")) return "adapter";
        return "unknown";
    }
    
    /**
     * Lists all Java files in a given source directory recursively.
     *
     * @param sourcePath The source directory
     * @return A list of Java file paths
     * @throws IOException if an I/O error occurs
     */
    public static List<Path> findJavaFiles(String sourcePath) throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        Files.walkFileTree(Paths.get(sourcePath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, java.nio.file.attribute.BasicFileAttributes attrs) {
                if (file.toString().endsWith(".java")) {
                    javaFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return javaFiles;
    }
    
    /**
     * Extracts the package name from a Java file.
     *
     * @param javaFile The Java file path
     * @return The package name or null if not found
     * @throws IOException if an I/O error occurs
     */
    public static String extractPackageName(Path javaFile) throws IOException {
        List<String> lines = Files.readAllLines(javaFile);
        for (String line : lines) {
            if (line.trim().startsWith("package ")) {
                return line.trim().replace("package ", "").replace(";", "").trim();
            }
        }
        return null;
    }
    
    /**
     * Extracts import statements from a Java file.
     *
     * @param javaFile The Java file path
     * @return A list of imported packages
     * @throws IOException if an I/O error occurs
     */
    public static List<String> extractImports(Path javaFile) throws IOException {
        List<String> imports = new ArrayList<>();
        List<String> lines = Files.readAllLines(javaFile);
        
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.startsWith("import ") && !trimmed.contains("static")) {
                String importStatement = trimmed.replace("import ", "").replace(";", "").trim();
                imports.add(importStatement);
            }
        }
        
        return imports;
    }
    
    /**
     * Analyzes the package structure and dependency relationships.
     *
     * @param sourcePath The source directory
     * @return A map of dependency violations
     * @throws IOException if an I/O error occurs
     */
    public static Map<String, List<String>> analyzePackageDependencies(String sourcePath) throws IOException {
        List<Path> javaFiles = findJavaFiles(sourcePath);
        Map<String, List<String>> violations = new HashMap<>();
        
        for (Path javaFile : javaFiles) {
            String packageName = extractPackageName(javaFile);
            if (packageName == null) continue;
            
            List<String> imports = extractImports(javaFile);
            for (String importPackage : imports) {
                // Only analyze imports within our project
                if (!importPackage.startsWith("org.s8r")) continue;
                
                // Skip imports that are within the same package
                if (importPackage.startsWith(packageName)) continue;
                
                // Check if this dependency violates Clean Architecture rules
                if (!isAllowedDependency(packageName, importPackage) && 
                    !DependencySuppressions.isSuppressed(packageName, importPackage)) {
                    violations.computeIfAbsent(packageName, k -> new ArrayList<>()).add(importPackage);
                }
            }
        }
        
        return violations;
    }
    
    /**
     * Analyzes circular dependencies between packages.
     *
     * @param sourcePath The source directory
     * @return A list of circular dependency chains
     * @throws IOException if an I/O error occurs
     */
    public static List<List<String>> detectCircularDependencies(String sourcePath) throws IOException {
        List<Path> javaFiles = findJavaFiles(sourcePath);
        Map<String, Set<String>> dependencyGraph = new HashMap<>();
        
        // Build the dependency graph
        for (Path javaFile : javaFiles) {
            String packageName = extractPackageName(javaFile);
            if (packageName == null) continue;
            
            List<String> imports = extractImports(javaFile);
            Set<String> dependencies = dependencyGraph.computeIfAbsent(packageName, k -> new HashSet<>());
            
            for (String importPackage : imports) {
                if (importPackage.startsWith("org.s8r") && !importPackage.startsWith(packageName)) {
                    dependencies.add(importPackage);
                }
            }
        }
        
        // Detect circular dependencies using DFS
        List<List<String>> circularDependencies = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String packageName : dependencyGraph.keySet()) {
            detectCircularDependenciesDFS(packageName, dependencyGraph, visited, recursionStack, 
                    new ArrayList<>(), circularDependencies);
        }
        
        return circularDependencies;
    }
    
    private static void detectCircularDependenciesDFS(
            String current, 
            Map<String, Set<String>> dependencyGraph,
            Set<String> visited,
            Set<String> recursionStack,
            List<String> currentPath,
            List<List<String>> circularDependencies) {
        
        if (recursionStack.contains(current)) {
            // We found a cycle
            int startIndex = currentPath.indexOf(current);
            List<String> cycle = new ArrayList<>(currentPath.subList(startIndex, currentPath.size()));
            cycle.add(current); // Add the current node to complete the cycle
            circularDependencies.add(cycle);
            return;
        }
        
        if (visited.contains(current)) {
            return;
        }
        
        visited.add(current);
        recursionStack.add(current);
        currentPath.add(current);
        
        Set<String> dependencies = dependencyGraph.getOrDefault(current, Set.of());
        for (String dependency : dependencies) {
            detectCircularDependenciesDFS(dependency, dependencyGraph, visited, recursionStack, 
                    new ArrayList<>(currentPath), circularDependencies);
        }
        
        recursionStack.remove(current);
    }
}