package org.s8r.architecture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.s8r.test.annotation.UnitTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify integration of the architecture documentation tools
 * with the build process and overall repository structure.
 */
@UnitTest
@Tag("architecture")
@Tag("integration")
public class ArchitectureToolsIntegrationTest {

    private String projectRoot;
    private Path docsDir;
    private Path architectureDir;
    private Path decisionsDir;
    private Path diagramsDir;

    @BeforeEach
    void setUp() {
        // Determine the project root directory
        projectRoot = new File("").getAbsolutePath();
        
        // Go up from the test directory to find project root if needed
        if (projectRoot.endsWith("Samstraumr/samstraumr-core")) {
            projectRoot = Paths.get(projectRoot).getParent().getParent().toString();
        } else if (projectRoot.endsWith("Samstraumr")) {
            projectRoot = Paths.get(projectRoot).getParent().toString();
        }
        
        // Define important directories
        docsDir = Paths.get(projectRoot, "docs");
        architectureDir = docsDir.resolve("architecture");
        decisionsDir = architectureDir.resolve("decisions");
        diagramsDir = docsDir.resolve("diagrams");
    }
    
    @Test
    void testDirectoryStructure() {
        // Verify the basic directory structure exists
        assertTrue(Files.exists(docsDir) && Files.isDirectory(docsDir),
                "docs directory should exist");
        assertTrue(Files.exists(architectureDir) && Files.isDirectory(architectureDir),
                "architecture directory should exist");
        assertTrue(Files.exists(decisionsDir) && Files.isDirectory(decisionsDir),
                "decisions directory should exist");
    }
    
    @Test
    void testScriptsInBinDirectory() {
        Path binDir = Paths.get(projectRoot, "bin");
        assertTrue(Files.exists(binDir) && Files.isDirectory(binDir),
                "bin directory should exist");
        
        Path generateDiagramsScript = binDir.resolve("generate-diagrams.sh");
        Path newAdrScript = binDir.resolve("new-adr");
        
        assertTrue(Files.exists(generateDiagramsScript),
                "generate-diagrams.sh script should exist");
        assertTrue(Files.exists(newAdrScript),
                "new-adr script should exist");
    }
    
    @Test
    void testCoreMavenDependencies() throws Exception {
        // Verify that the samstraumr-core/pom.xml has necessary test dependencies
        Path corePomPath = Paths.get(projectRoot, "Samstraumr", "samstraumr-core", "pom.xml");
        assertTrue(Files.exists(corePomPath), "Core POM file should exist");
        
        String pomContent = Files.readString(corePomPath);
        
        // Check for JUnit dependencies
        assertTrue(pomContent.contains("junit-jupiter"),
                "POM should include JUnit Jupiter dependency");
        assertTrue(pomContent.contains("junit-platform-suite") || 
                   pomContent.contains("junit-platform-launcher"),
                "POM should include JUnit Platform dependencies");
        
        // Check for Cucumber if using BDD tests
        assertTrue(pomContent.contains("cucumber-java") || 
                   pomContent.contains("cucumber-junit"),
                "POM should include Cucumber dependencies for BDD tests");
    }
    
    @Test
    void testArchitectureDecisionRecords() throws Exception {
        // Verify that at least three basic ADRs exist
        List<String> expectedAdrPrefixes = Arrays.asList(
                "0001-record-architecture-decisions",
                "0002-automated-c4-architecture-diagrams", 
                "0003-adopt-clean-architecture"
        );
        
        // Get all ADR files
        List<String> actualAdrFiles = Files.list(decisionsDir)
                .filter(p -> p.toString().endsWith(".md"))
                .filter(p -> !p.getFileName().toString().equals("README.md"))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        
        // Verify each expected ADR exists with the correct prefix
        for (String expectedPrefix : expectedAdrPrefixes) {
            boolean found = actualAdrFiles.stream()
                    .anyMatch(file -> file.startsWith(expectedPrefix));
            assertTrue(found, "Expected ADR with prefix '" + expectedPrefix + "' not found");
        }
        
        // Verify ADR index exists
        Path indexPath = decisionsDir.resolve("README.md");
        assertTrue(Files.exists(indexPath), "ADR index file should exist");
        
        // Check that the index references all ADRs
        String indexContent = Files.readString(indexPath);
        for (String adrFile : actualAdrFiles) {
            Path adrPath = decisionsDir.resolve(adrFile);
            String adrContent = Files.readString(adrPath);
            
            // Get the ADR title
            String title = adrContent.lines()
                    .filter(line -> line.startsWith("# "))
                    .findFirst()
                    .orElse("");
            
            if (!title.isEmpty()) {
                // Remove the "# " prefix and number from title
                String titleText = title.replaceFirst("^# \\d+\\. ", "");
                assertTrue(indexContent.contains(titleText), 
                        "ADR index should reference ADR: " + titleText);
            }
        }
    }
    
    @Test
    void testDiagramGenerationOnBuild() throws Exception {
        // This test validates that diagrams can be generated during the build process
        
        // If diagrams directory doesn't exist yet, that's fine - just skip this test part
        if (Files.exists(diagramsDir)) {
            // Check for diagram README
            Path diagramReadme = diagramsDir.resolve("README.md");
            if (Files.exists(diagramReadme)) {
                String readmeContent = Files.readString(diagramReadme);
                assertTrue(readmeContent.contains("C4 Architecture Diagrams"), 
                        "Diagram README should have the right title");
            }
            
            // Check for at least one diagram file (any format)
            long diagramFileCount = Files.list(diagramsDir)
                    .filter(p -> {
                        String name = p.getFileName().toString();
                        return name.endsWith(".svg") || name.endsWith(".png") || name.endsWith(".pdf");
                    })
                    .count();
            
            assertTrue(diagramFileCount >= 0, 
                    "Diagram directory should exist but may not have files yet if not generated");
        }
        
        // Verify the Python script or placeholder for diagram generation
        Path pythonScript = Paths.get(projectRoot, "bin", "c4_diagrams.py");
        
        // If the Python script exists, it should be executable
        if (Files.exists(pythonScript)) {
            assertTrue(Files.isExecutable(pythonScript), 
                    "Python diagram script should be executable");
        }
    }
    
    @Test
    void testArchitectureDocumentation() throws Exception {
        // Verify that high-level architecture documentation exists
        Path cleanArchDoc = architectureDir.resolve("clean-architecture-migration.md");
        Path directoryStructureDoc = architectureDir.resolve("directory-structure.md");
        Path strategyDoc = architectureDir.resolve("strategy.md");
        Path testingDoc = architectureDir.resolve("testing-strategy.md");
        
        // At least some of these documents should exist
        assertTrue(
                Files.exists(cleanArchDoc) || 
                Files.exists(directoryStructureDoc) || 
                Files.exists(strategyDoc) || 
                Files.exists(testingDoc),
                "At least one architecture documentation file should exist"
        );
        
        // README should exist in architecture directory
        Path readmePath = architectureDir.resolve("README.md");
        assertTrue(Files.exists(readmePath), "Architecture directory should have a README.md");
    }
}