<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Folder Management Guidelines

## Overview

The S8r Framework follows a carefully designed directory structure to balance organization with accessibility. This document provides comprehensive guidelines for managing folders and implementing the folder organization philosophy: **prefer file naming conventions over deep nesting whenever possible**.

## Folder Organization Philosophy

Our approach to folder organization is guided by these core principles:

1. **Simplicity**: Minimize folder depth to improve navigation and discoverability
2. **Clarity**: Each directory should have a clear and singular purpose
3. **Consistency**: Follow consistent patterns for similar concerns
4. **Documentation**: Explicitly document the purpose and organization of each directory
5. **Evolution**: Allow for growth without requiring major restructuring

## When to Create a New Folder

A new folder should only be created when ALL of these criteria are met:

1. **Critical Mass**: You have 5+ related files that share a distinct responsibility
2. **Bounded Context**: The grouping represents a meaningful domain concept
3. **Lifecycle Alignment**: Files are developed, tested, and released together
4. **Architectural Cohesion**: Files collectively belong to the same architectural layer
5. **Significant Capability**: The files implement an important system capability

Each folder creation should be a deliberate decision with clear justification, not an automatic response to having multiple files.

## When NOT to Create a New Folder

Avoid creating new folders when:

1. You have fewer than 5 related files
2. The distinction can be clearly expressed in the filename
3. The grouping is temporary or experimental
4. The folder would create paths that exceed our 9-level maximum depth limit
5. The categorization duplicates an existing organizational concept
6. The categorization is based on arbitrary or changing criteria

> **MAXIMUM DEPTH POLICY**: The repository enforces a strict maximum directory depth of 9 levels from the repository root. This is a hard limit that all code must adhere to.

## Alternatives to Deep Nesting

Instead of creating deep directory structures, use these strategies:

### 1. File Prefixing Pattern

```
Instead of:
/validators/
  /identity/
    /format/
      specialized-format.java

Use:
/validators/
  identity-format-specialized.java
```

### 2. Package Consolidation

```
Instead of:
/model/
  /user/
    User.java
    UserMapper.java
  /profile/
    Profile.java
    ProfileMapper.java

Use:
/model/
  UserEntity.java
  UserEntityMapper.java
  ProfileEntity.java
  ProfileEntityMapper.java
```

### 3. README Documentation

Use README files to document logical groupings without creating physical folder boundaries:

```markdown
# Validators

## Identity Validators
The following files handle identity validation:
- `identity-format-validator.java`: Validates identity format rules
- `identity-uniqueness-validator.java`: Ensures identity uniqueness
- `identity-reference-validator.java`: Validates referenced identities

## Component Validators
The following files handle component validation:
...
```

### 4. Interface Segregation

Split large interfaces/classes instead of grouping many small ones:

```java
// Instead of many small validators in subdirectories:
public interface IdentityValidator {
    boolean validateFormat(Identity identity);
    boolean validateUniqueness(Identity identity);
    boolean validateReferences(Identity identity);
}

// Use interface segregation:
public interface IdentityFormatValidator {
    boolean validate(Identity identity);
}

public interface IdentityUniquenessValidator {
    boolean validate(Identity identity);
}

public interface IdentityReferenceValidator {
    boolean validate(Identity identity);
}
```

## File Naming Conventions

When using file naming to replace folder structure, follow these patterns:

### For Java Classes:

**Pattern**: `[Domain][Entity][Concern][Type].java`

**Examples**:
- `ComponentInitializer.java`
- `IdentityFormatValidator.java`
- `MachineStateFactory.java`

### For Documentation:

**Pattern**: `[domain]-[concept]-[aspect].md`

**Examples**:
- `component-lifecycle-states.md`
- `identity-addressing-scheme.md`
- `machine-orchestration-patterns.md`

### For Test Files:

**Pattern**: `[Entity][Scenario]Test.java`

**Examples**:
- `ComponentInitializationTest.java`
- `IdentityValidationTest.java`
- `MachineOrchestrationTest.java`

## Folder Documentation Requirements

Every directory must contain a README.md file that follows the template in the repository root (`FOLDER_README_TEMPLATE.md`). This file must include:

1. **Purpose Statement**: Clear explanation of the directory's role and responsibility
2. **Key Responsibilities**: Bullet list of primary responsibilities
3. **Content Description**: Table of key files with descriptions
4. **Architectural Context**: Where the directory fits in the architecture
5. **Naming Conventions**: File naming patterns specific to the directory
6. **Related Directories**: Links to related parts of the system

## Process for Adding a New Directory

When you determine a new directory is necessary:

1. **Justify the Need**: Document why file naming conventions are insufficient
2. **Define the Bounded Context**: Clearly articulate what belongs and doesn't belong
3. **Create the README**: Use the `FOLDER_README_TEMPLATE.md` template
4. **Update the FOLDERS.md**: Add the new directory to the global structure
5. **Update Architecture Docs**: Update `docs/architecture/directory-structure.md` if needed
6. **Announce the Change**: Note the addition in the pull request and changelog

## Directory Flattening Analysis

The repository includes a tool to help identify opportunities for directory flattening:

```bash
# Run the directory analysis script
./util/scripts/flatten-directories.sh

# Sample output:
# Directories with fewer than 5 files:
# /src/component/creation | 2 files | Consider flattening to /src/component with prefixes
# /src/validation/format | 3 files | Consider flattening to /src/validation with prefixes
```

Use this tool regularly to analyze the repository structure and identify optimization opportunities.

## Example Decision Process

### Scenario 1: Component Validators

**Situation**: You have 3 validator classes for different aspects of component identity.

**Bad Decision**: Create a `/validators/identity/` folder structure.

**Good Decision**: Name the files `identity-format-validator.java`, `identity-uniqueness-validator.java`, and `identity-reference-validator.java` and place them in the existing `/validators/` directory.

### Scenario 2: Event Handlers

**Situation**: You have 7 event handler classes, all related to component lifecycle events.

**Bad Decision**: Create separate folders for each event type.

**Good Decision**: Create a single `/event/lifecycle/` directory with all 7 handlers, with README documentation explaining the relationships.

### Scenario 3: Utility Methods

**Situation**: You have 2 utility classes for string manipulation and 3 for date handling.

**Bad Decision**: Create `/util/string/` and `/util/date/` folders.

**Good Decision**: Keep all 5 classes in the `/util/` directory with clear naming: `StringFormatter.java`, `StringParser.java`, `DateConverter.java`, etc.

## Directory Audit Schedule

The repository directory structure should be reviewed:

1. Before each major release
2. When the codebase exceeds 20% growth since the last audit
3. When new architectural patterns are introduced
4. After completion of major features

During audits:
1. Run the `flatten-directories.sh` script to identify candidates for consolidation
2. Check all directories for README.md files
3. Validate adherence to naming conventions
4. Identify and document any emerging patterns

## Enforcing Maximum Directory Depth

To enforce our 9-level maximum directory depth policy:

### Checking for Depth Violations

Run the depth verification tool to identify paths that exceed the maximum:

```bash
./util/scripts/flatten-directories.sh
```

The tool will highlight paths that exceed our 9-level limit and mark them as requiring immediate attention.

### Fixing Depth Violations

When you encounter paths that exceed the depth limit:

1. Generate specific refactoring commands:
   ```bash
   ./util/scripts/flatten-directories.sh --recommend
   ```

2. Follow the recommendations in `docs/planning/folder-flattening-plan.md`

3. Use these strategies to reduce path depth:
   - Move test classes to a dedicated top-level test package
   - Flatten feature file directories using descriptive directory names
   - Use file naming conventions instead of deep nesting
   - Consolidate small, similar-purpose directories

### Pre-commit Verification

All pull requests must pass the directory depth verification check before they can be merged. The CI pipeline includes this check to ensure no path exceeds 9 levels.

## Conclusion

By following these guidelines, we maintain a clean, navigable directory structure that scales with the project's growth while avoiding unnecessary complexity. When in doubt, prefer file naming conventions and README documentation over creating additional directory levels.
