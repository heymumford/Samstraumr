#!/bin/bash

# Script to check for circular dependencies in the codebase
# This should be run as part of the CI/CD pipeline and before commits

# Source common functions
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "$DIR/../lib/common.sh"

# Display header
echo "=================================="
echo " Circular Dependency Check"
echo "=================================="
echo "Analyzing code for circular dependencies between packages"
echo

# Set directories to analyze
SOURCE_DIR="$REPO_ROOT/modules/samstraumr-core/src/main/java/org/s8r"
TEST_DIR="$REPO_ROOT/modules/samstraumr-core/src/test/java/org/s8r"

# Java class to run the dependency analysis
ANALYZER_CLASS="org.s8r.architecture.util.CircularDependencyAnalyzer"
MAIN_CLASS="org.s8r.architecture.util.DependencyAnalyzerMain"

# Create temporary directory for the analyzer
TEMP_DIR=$(mktemp -d)
ANALYZER_DIR="$TEMP_DIR/org/s8r/architecture/util"
mkdir -p "$ANALYZER_DIR"

# Create the analyzer source files in the temporary directory
cat > "$ANALYZER_DIR/CircularDependencyAnalyzer.java" << 'EOF'
package org.s8r.architecture.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility for detecting circular dependencies between packages.
 */
public class CircularDependencyAnalyzer {

    /**
     * Detects circular dependencies between packages.
     *
     * @param sourcePath The source directory to analyze
     * @param verbose If true, prints verbose output during analysis
     * @return A list of circular dependency chains, or an empty list if none are found
     * @throws IOException if an I/O error occurs
     */
    public static List<List<String>> detectCircularDependencies(String sourcePath, boolean verbose) throws IOException {
        if (verbose) {
            System.out.println("Analyzing source directory: " + sourcePath);
        }
        
        Map<String, Set<String>> dependencyGraph = buildDependencyGraph(sourcePath, verbose);
        
        if (verbose) {
            System.out.println("Dependency graph built with " + dependencyGraph.size() + " packages");
            System.out.println("Detecting circular dependencies...");
        }
        
        // Detect circular dependencies using DFS
        List<List<String>> circularDependencies = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        for (String packageName : dependencyGraph.keySet()) {
            if (!visited.contains(packageName)) {
                Set<String> recursionStack = new HashSet<>();
                List<String> currentPath = new ArrayList<>();
                detectCircularDependenciesDFS(packageName, dependencyGraph, visited, recursionStack, 
                        currentPath, circularDependencies);
            }
        }
        
        if (verbose) {
            System.out.println("Found " + circularDependencies.size() + " circular dependencies");
        }
        
        return circularDependencies;
    }
    
    private static Map<String, Set<String>> buildDependencyGraph(String sourcePath, boolean verbose) throws IOException {
        Map<String, Set<String>> dependencyGraph = new HashMap<>();
        List<Path> javaFiles = findJavaFiles(sourcePath);
        
        if (verbose) {
            System.out.println("Found " + javaFiles.size() + " Java files");
        }
        
        // First pass: collect all package names
        Set<String> allPackages = new HashSet<>();
        for (Path javaFile : javaFiles) {
            String packageName = extractPackageName(javaFile);
            if (packageName != null) {
                // Store at most granular level for package hierarchy
                String[] parts = packageName.split("\\.");
                for (int i = 1; i <= parts.length; i++) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < i; j++) {
                        if (j > 0) sb.append(".");
                        sb.append(parts[j]);
                    }
                    allPackages.add(sb.toString());
                }
            }
        }
        
        if (verbose) {
            System.out.println("Found " + allPackages.size() + " packages");
        }
        
        // Second pass: build dependency graph
        for (Path javaFile : javaFiles) {
            String packageName = extractPackageName(javaFile);
            if (packageName == null) continue;
            
            List<String> imports = extractImports(javaFile);
            
            // Get list of packages this file depends on
            for (String importStmt : imports) {
                // Only consider imports within our project
                if (!importStmt.contains("org.s8r")) continue;
                
                // Find the most specific package in our list
                String dependencyPkg = findMostSpecificPackage(importStmt, allPackages);
                if (dependencyPkg == null) continue;
                
                // Find the most specific package for the source file
                String sourcePkg = findMostSpecificPackage(packageName, allPackages);
                if (sourcePkg == null) continue;
                
                // Skip self dependencies and internal package dependencies
                if (dependencyPkg.equals(sourcePkg) || dependencyPkg.startsWith(sourcePkg + ".")) {
                    continue;
                }
                
                // Add to dependency graph at package level (not class level)
                dependencyGraph.computeIfAbsent(sourcePkg, k -> new HashSet<>()).add(dependencyPkg);
            }
        }
        
        return dependencyGraph;
    }
    
    private static String findMostSpecificPackage(String fullName, Set<String> knownPackages) {
        // Extract just the package part for class imports
        String packageName = fullName;
        int lastDot = fullName.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = fullName.substring(0, lastDot);
        }
        
        // Find the most specific package that matches
        String mostSpecific = null;
        for (String pkg : knownPackages) {
            if (packageName.equals(pkg) || packageName.startsWith(pkg + ".")) {
                if (mostSpecific == null || pkg.length() > mostSpecific.length()) {
                    mostSpecific = pkg;
                }
            }
        }
        
        return mostSpecific;
    }
    
    private static void detectCircularDependenciesDFS(
            String current, 
            Map<String, Set<String>> dependencyGraph,
            Set<String> visited,
            Set<String> recursionStack,
            List<String> currentPath,
            List<List<String>> circularDependencies) {
        
        visited.add(current);
        recursionStack.add(current);
        currentPath.add(current);
        
        for (String dependency : dependencyGraph.getOrDefault(current, Collections.emptySet())) {
            if (!visited.contains(dependency)) {
                List<String> newPath = new ArrayList<>(currentPath);
                detectCircularDependenciesDFS(dependency, dependencyGraph, visited, recursionStack, 
                        newPath, circularDependencies);
            } else if (recursionStack.contains(dependency)) {
                // We found a cycle
                int startIndex = currentPath.indexOf(dependency);
                if (startIndex >= 0) { // Safety check
                    List<String> cycle = new ArrayList<>(currentPath.subList(startIndex, currentPath.size()));
                    cycle.add(dependency); // Add the dependency to complete the cycle
                    circularDependencies.add(cycle);
                }
            }
        }
        
        // Remove current node from recursion stack when returning
        recursionStack.remove(current);
    }
    
    private static List<Path> findJavaFiles(String sourcePath) throws IOException {
        List<Path> javaFiles = new ArrayList<>();
        Files.walkFileTree(Paths.get(sourcePath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (file.toString().endsWith(".java")) {
                    javaFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return javaFiles;
    }
    
    private static String extractPackageName(Path javaFile) throws IOException {
        List<String> lines = Files.readAllLines(javaFile);
        for (String line : lines) {
            if (line.trim().startsWith("package ")) {
                return line.trim().replace("package ", "").replace(";", "").trim();
            }
        }
        return null;
    }
    
    private static List<String> extractImports(Path javaFile) throws IOException {
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
     * Generates a DOT format graph for visualization of dependencies.
     *
     * @param packageDependencies The package dependency map
     * @return A string in DOT format
     */
    public static String generateDotGraph(Map<String, Set<String>> packageDependencies) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph DependencyGraph {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape=box, style=filled, fillcolor=lightblue];\n\n");
        
        // Add nodes
        for (String pkg : packageDependencies.keySet()) {
            String nodeName = pkg.replaceAll("\\.", "_");
            String label = pkg;
            dot.append("  ").append(nodeName).append(" [label=\"").append(label).append("\"];\n");
        }
        
        dot.append("\n");
        
        // Add edges
        for (Map.Entry<String, Set<String>> entry : packageDependencies.entrySet()) {
            String source = entry.getKey().replaceAll("\\.", "_");
            for (String target : entry.getValue()) {
                String targetNode = target.replaceAll("\\.", "_");
                dot.append("  ").append(source).append(" -> ").append(targetNode).append(";\n");
            }
        }
        
        dot.append("}\n");
        return dot.toString();
    }
    
    /**
     * Suggests refactoring options to resolve circular dependencies.
     *
     * @param circularDependencies List of circular dependency chains
     * @return A string with refactoring suggestions
     */
    public static String suggestRefactoring(List<List<String>> circularDependencies) {
        if (circularDependencies.isEmpty()) {
            return "No circular dependencies found. No refactoring needed.";
        }
        
        StringBuilder suggestions = new StringBuilder();
        suggestions.append("Suggested refactoring options to resolve ").append(circularDependencies.size())
                  .append(" circular dependencies:\n\n");
        
        for (int i = 0; i < circularDependencies.size(); i++) {
            List<String> cycle = circularDependencies.get(i);
            suggestions.append("Circular Dependency #").append(i + 1).append(": ");
            
            for (int j = 0; j < cycle.size(); j++) {
                suggestions.append(cycle.get(j));
                if (j < cycle.size() - 1) {
                    suggestions.append(" → ");
                }
            }
            
            suggestions.append("\n");
            suggestions.append("  Options to resolve:\n");
            
            // Option 1: Extract common interface/abstract class
            suggestions.append("  1. Extract a common interface/abstract class:\n");
            suggestions.append("     - Create a new package '").append(findCommonParent(cycle)).append(".common'\n");
            suggestions.append("     - Move shared abstractions there\n");
            suggestions.append("     - Make both packages depend on this new common package\n\n");
            
            // Option 2: Merge packages
            suggestions.append("  2. Merge packages:\n");
            suggestions.append("     - Consider merging '").append(cycle.get(0)).append("' and '")
                       .append(cycle.get(cycle.size() - 1)).append("'\n");
            suggestions.append("     - This is appropriate if they're closely related\n\n");
            
            // Option 3: Dependency Inversion
            suggestions.append("  3. Apply Dependency Inversion Principle:\n");
            String inversion = findCyclicEdge(cycle);
            if (inversion != null) {
                String[] parts = inversion.split("->");
                suggestions.append("     - Create an interface in '").append(parts[1]).append("'\n");
                suggestions.append("     - Have '").append(parts[0]).append("' implement this interface\n");
                suggestions.append("     - Reverse the dependency direction\n\n");
            }
            
            suggestions.append("\n");
        }
        
        return suggestions.toString();
    }
    
    private static String findCommonParent(List<String> packages) {
        if (packages.isEmpty()) return "";
        if (packages.size() == 1) return packages.get(0);
        
        String[] parts = packages.get(0).split("\\.");
        StringBuilder commonPrefix = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String currentPrefix = i == 0 ? parts[0] : commonPrefix.toString() + "." + parts[i];
            
            boolean allMatch = true;
            for (int j = 1; j < packages.size(); j++) {
                if (!packages.get(j).startsWith(currentPrefix)) {
                    allMatch = false;
                    break;
                }
            }
            
            if (allMatch) {
                if (i == 0) {
                    commonPrefix.append(parts[0]);
                } else {
                    commonPrefix.append(".").append(parts[i]);
                }
            } else {
                break;
            }
        }
        
        return commonPrefix.toString();
    }
    
    private static String findCyclicEdge(List<String> cycle) {
        if (cycle.size() < 2) return null;
        return cycle.get(cycle.size() - 2) + "->" + cycle.get(cycle.size() - 1);
    }
}
EOF

cat > "$ANALYZER_DIR/DependencyAnalyzerMain.java" << 'EOF'
package org.s8r.architecture.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DependencyAnalyzerMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java DependencyAnalyzerMain <source-dir> [--verbose] [--dot <output-file>] [--suggestions <output-file>]");
            System.exit(1);
        }
        
        String sourceDir = args[0];
        boolean verbose = Arrays.asList(args).contains("--verbose");
        String dotFile = null;
        String suggestionsFile = null;
        
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--dot") && i + 1 < args.length) {
                dotFile = args[i + 1];
                i++;
            } else if (args[i].equals("--suggestions") && i + 1 < args.length) {
                suggestionsFile = args[i + 1];
                i++;
            }
        }
        
        try {
            // Detect circular dependencies
            List<List<String>> circularDependencies = 
                CircularDependencyAnalyzer.detectCircularDependencies(sourceDir, verbose);
            
            // Output results
            if (circularDependencies.isEmpty()) {
                System.out.println("No circular dependencies found.");
                System.exit(0);
            } else {
                System.out.println("Found " + circularDependencies.size() + " circular dependencies:");
                for (List<String> cycle : circularDependencies) {
                    System.out.print("  ");
                    for (int i = 0; i < cycle.size(); i++) {
                        System.out.print(cycle.get(i));
                        if (i < cycle.size() - 1) {
                            System.out.print(" -> ");
                        }
                    }
                    System.out.println();
                }
                
                // Generate suggestions
                if (suggestionsFile != null) {
                    String suggestions = CircularDependencyAnalyzer.suggestRefactoring(circularDependencies);
                    Files.writeString(Paths.get(suggestionsFile), suggestions);
                    System.out.println("Wrote refactoring suggestions to " + suggestionsFile);
                }
                
                System.exit(1); // Exit with error if circular dependencies found
            }
        } catch (IOException e) {
            System.err.println("Error analyzing dependencies: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}
EOF

# Compile the analyzer
cd "$TEMP_DIR"
javac org/s8r/architecture/util/*.java

# Check if compilation succeeded
if [ $? -ne 0 ]; then
    echo -e "\033[31mError: Failed to compile dependency analyzer.\033[0m"
    exit 1
fi

# Run the analyzer on source and test directories
echo "Analyzing source code..."
java -cp "$TEMP_DIR" "$MAIN_CLASS" "$SOURCE_DIR" --suggestions "$TEMP_DIR/refactoring-suggestions.txt"
SOURCE_RESULT=$?

echo -e "\nAnalyzing test code..."
java -cp "$TEMP_DIR" "$MAIN_CLASS" "$TEST_DIR"
TEST_RESULT=$?

# Display results
if [ $SOURCE_RESULT -eq 0 ] && [ $TEST_RESULT -eq 0 ]; then
    echo -e "\n\033[32m✓ No circular dependencies found.\033[0m"
    echo "Your code is free of circular dependencies. Good job!"
else
    echo -e "\n\033[31m✗ Circular dependencies detected!\033[0m"
    
    if [ -f "$TEMP_DIR/refactoring-suggestions.txt" ]; then
        echo -e "\n\033[33mRefactoring Suggestions:\033[0m"
        cat "$TEMP_DIR/refactoring-suggestions.txt"
    fi
    
    echo -e "\nCircular dependencies can cause:"
    echo "  - Hard-to-find bugs"
    echo "  - Difficulties in testing"
    echo "  - Problems with build order"
    echo "  - Code that's hard to understand and maintain"
    
    echo -e "\nPlease resolve these issues before continuing."
    exit 1
fi

# Clean up
rm -rf "$TEMP_DIR"
exit 0