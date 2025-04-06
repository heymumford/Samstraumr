<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# FileNamingConventions

This document outlines the standard naming conventions for files within the Samstraumr project.

## Markdown Files

### General rules

1. **README.md Files**
   - Use **UPPER_CASE** (e.g., `README.md`)
   - These files provide orientation for a directory and should be easily visible
2. **Special Project Files**
   - Use **UPPER_CASE** for special configuration files (e.g., `CLAUDE.md`, `LICENSE`)
   - These files provide global project settings or legal information
3. **Documentation Files**
   - Use **PascalCase** for all other documentation files (e.g., `GettingStarted.md`, `CoreConcepts.md`)
   - Multi-word names in PascalCase, no separators (e.g., `LoggingStandards.md`, not `Logging-Standards.md`)
   - For acronyms, keep the acronym in all caps (e.g., `FAQ.md`, `TBD.md`, `APIReference.md`, `CICDGuide.md`)

### Directory-specific patterns

- **docs/concepts/**: `CoreConcepts.md`, `StateManagement.md`
- **docs/guides/**: `GettingStarted.md`, `MigrationGuide.md`
- **docs/reference/**: `Glossary.md`, `FAQ.md`
- **docs/testing/**: `TestStrategy.md`, `BddWithCucumber.md`
- **docs/contribution/**: `Contributing.md`, `CodeStandards.md`, `CICDGuide.md`

## Source Code

### Java files

1. **Class Files**
   - Use **PascalCase** for class names (e.g., `TubeLogger.java`, `BundleFactory.java`)
   - Class name must match the public class within the file
   - Interfaces, enums, and annotations follow the same convention
2. **Package Names**
   - Use **lowercase** for package names (e.g., `org.samstraumr.tube`)
   - Use periods to separate hierarchical packages

### Cucumber feature files

1. **Feature Files**
   - Use **kebab-case** with descriptive names ending in `-test.feature` (e.g., `tube-initialization-test.feature`)
   - For example files, use the pattern: `<prefix>-<descriptive-name>-example.feature`
   - Separate words with hyphens for readability
2. **Special TBD Files**
   - Files related to Tube Based Development (TBD) should keep TBD acronym capitalized
   - Format: `TBD-<descriptive-name-in-kebab-case>.feature` (e.g., `TBD-atomic-boundary-test-example.feature`)
   - This ensures TBD stands out in the filesystem while maintaining consistency

### Scripts

1. **Shell Scripts**
   - Use **kebab-case** for shell scripts (e.g., `update-version.sh`, `run-tests.sh`)
   - Ensure all scripts have the appropriate extension (`.sh`, `.bat`) and execution permissions
2. **Utility Scripts**
   - Use descriptive names that indicate the script's purpose
   - Begin with a verb when possible (e.g., `run-`, `update-`, `check-`, `build-`)

## Properties and Configuration Files

1. **Properties Files**
   - Use **lowercase** with hyphens if needed (e.g., `version.properties`, `log4j2.xml`)
   - Configuration files associated with specific frameworks should follow their conventions
2. **XML Files**
   - Use **lowercase** with hyphens if needed (e.g., `checkstyle.xml`, `pom.xml`)
   - Exception: when the XML file represents a specific entity or component, PascalCase may be used

## Benefits of Consistent Naming

1. **Improved Discoverability**
   - Files are easier to find when following consistent patterns
   - Special files like README.md stand out when in UPPER_CASE
2. **Enhanced Code Organization**
   - Easier to maintain and navigate the codebase
   - Reduces cognitive load when switching between files
3. **Professional Appearance**
   - Demonstrates attention to detail and code quality
   - Follows industry best practices

## Implementation

To ensure all files follow these conventions, we have created standardization scripts:

```bash
# FileNamingConventions
./docs/scripts/standardize-md-filenames.sh

# FileNamingConventions
./docs/scripts/standardize-feature-filenames.sh
```

These scripts will detect and rename files that don't follow the conventions, ensuring consistency across the project. All scripts must be run from the project root directory.

### Maintenance

When adding new files to the project, follow these guidelines:

1. For new Markdown files:
   - Use PascalCase for the file name (e.g., `ProjectOverview.md`)
   - Only use UPPER_CASE for README.md files
2. For checking conformance:
   - Run the standardization scripts periodically:

     ```bash
     # For Markdown files
     ./docs/scripts/standardize-md-filenames.sh

     # For Cucumber feature files
     ./docs/scripts/standardize-feature-filenames.sh
     ```
   - The scripts will identify and fix any non-conforming files
   - Consider running these checks as part of a pre-commit hook or CI process
3. If adding new file types not covered by these conventions:
   - Update this document with the appropriate conventions
