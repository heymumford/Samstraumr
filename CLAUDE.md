<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr Context for Claude

This document serves as a reference for Claude when working with the Samstraumr repository, providing key information about project standards, commands, and organization.

## Quality Check Commands

When making changes to the code, the following commands should be run to ensure quality standards:

```bash
# Run quality checks (style, static analysis)
./s8r quality check

# Run full test suite 
./s8r test all

# Run specific test types
./s8r test component  # Component-level tests
./s8r test composite  # Composite-level tests
./s8r test machine    # Machine-level tests
./s8r test unit       # Unit tests only
```

## Build Commands

For building the project:

```bash
# Standard build with tests
./s8r build

# Fast build (skips tests)
./s8r build fast

# Build with package generation
./s8r build package
```

## Java Version

The project is configured to use Java 21, which is now the default Java version in your environment. Java 21 includes enhanced module system security that requires special JVM options for reflection-based code. These options are automatically set when you use the Java 21 environment.

You can switch between different Java versions using the `java-switch` function:

```bash
# Switch to Java 17
java-switch 17

# Switch to Java 21 (OpenJDK)
java-switch 21

# Switch to Java 21 (Oracle JDK)
java-switch oracle21

# Check current Java version
java -version
```

If you need to temporarily use Java 21 for a single command without changing your environment, you can use the provided script:

```bash
# Run any command with Java 21
./use-java21.sh <command>
```

### Java 21 Compatibility Notes

The codebase has been updated to be compatible with Java 21's stronger module boundaries by:

1. Adding JVM options to allow reflection access where needed:
   ```
   --add-opens java.base/java.lang=ALL-UNNAMED
   --add-opens java.base/java.util=ALL-UNNAMED
   --add-opens java.base/java.lang.reflect=ALL-UNNAMED
   ```

2. Adding graceful fallbacks for reflection-based code like document generation
3. Enhancing environment detection to properly identify Java 21's runtime properties
4. Using more robust exception handling for reflective operations

If you encounter any Java 21 compatibility issues, please run tests with the appropriate JVM options or use the provided script which adds them automatically.

## Folder Organization Guidelines

The project follows these folder organization principles:

1. **Prefer file naming conventions over deep nesting**
   - Use descriptive prefixes to indicate relationships
   - Example: `component-service.js`, `component-repository.js` instead of `/component/service.js`, `/component/repository.js`

2. **Create folders only when necessary**
   - Minimum 5+ related files with a clear bounded context
   - Each folder should have a clear and unique purpose
   - Should represent a significant conceptual grouping

3. **Document folder purpose**
   - Each folder must have a README.md describing its purpose and contents
   - README should list naming conventions for files within the folder
   - README should document relationships with other directories

4. **Follow Clean Architecture structure**
   - Maintain clear separation between layers (domain, application, infrastructure, etc.)
   - Use consistent naming patterns within each layer
   - See `docs/architecture/directory-structure.md` for the reference map

## Documentation Standards

Documentation follows these standards:

1. All markdown files use kebab-case naming (e.g., `folder-structure.md`)
2. READMEs include purpose, contents, naming conventions, and related directories
3. Code follows standard Java naming conventions from `docs/reference/standards/java-naming-standards.md`
4. Package-info.java files document package purpose and usage

## Version Management System

Version management in the Samstraumr project follows a consistent approach:

- **Single Source of Truth**: `/Samstraumr/version.properties` is the canonical source for the current version
- **Synchronized Files**: When the version is updated, all related files are automatically kept in sync
- **Reliable Consistency**: No more manual updates or out-of-sync files

### Files Automatically Synchronized

1. `/Samstraumr/version.properties` - Primary source of truth
2. `/pom.xml` - Root Maven POM file 
3. `/Samstraumr/pom.xml` - Module-level POM file
4. `/Samstraumr/samstraumr-core/pom.xml` - Core module POM file
5. `/README.md` - Version badge URL and Maven dependency example 
6. `CLAUDE.md` - Version references in the AI assistant context

### Version Management Systems

Samstraumr has two version management systems:

1. **Original System** (`./s8r version`) - Full-featured but complex
2. **Simplified System** (`./s8r-version`) - Lightweight alternative (<200 lines)

#### Original CLI

```bash
# Get the current version
./s8r version get

# Bump the patch version
./s8r version bump patch

# Set a specific version
./s8r version set 2.1.0

# Test, bump and tag
./s8r version test patch
```

#### Simplified CLI (Recommended)

```bash
# Show current version
./s8r-version get

# Bump patch version
./s8r-version bump patch

# Bump minor version
./s8r-version bump minor

# Set specific version
./s8r-version set 2.1.0

# Fix version inconsistencies
./s8r-version fix
```

The simplified system provides clearer output, has no external dependencies, and focuses on core version management functionality.

## Commit Guidelines

When creating commits:

1. Use conventional commit format: `type: subject` 
2. Types include: feat, fix, docs, style, refactor, test, chore
3. Keep subject under 50 characters
4. Add detailed description if needed

Example: `docs: improve folder organization documentation`

## Code Style Verification

Before committing code, verify these style standards:

```bash
# Verify proper import organization (alphabetical, no comments)
./util/scripts/check-imports.sh

# Fix import organization if issues are found
./util/scripts/fix-imports.sh --all

# Check folder depth (max 9 levels)
./util/scripts/flatten-directories.sh
```

## Current Version

The current version of Samstraumr is: 2.1.0 (as of April 04, 2025)
