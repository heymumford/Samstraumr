<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Maven Structure Guide

This document outlines the Maven structure for the S8r project (formerly Samstraumr), including naming conventions, directory organization, and migration instructions.

## Overview

Starting with version ${samstraumr.version}, the project is transitioning from the name "Samstraumr" to the abbreviated form "S8r" (where 8 represents the 8 letters between 'S' and 'r'). This change affects Maven coordinates, package names, and directory structures.

## Maven Coordinates

### Old Structure

```xml
<groupId>org.samstraumr</groupId>
<artifactId>samstraumr</artifactId>
<version>${previous.version}</version>
```

### New Structure

```xml
<groupId>org.s8r</groupId>
<artifactId>s8r-parent</artifactId>
<version>${samstraumr.version}</version>
```

## Directory Structure

### Old Structure

```
Samstraumr/
├── pom.xml
├── Samstraumr/
│   ├── pom.xml
│   ├── version.properties
│   ├── samstraumr-core/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/
│   │       └── test/
```

### New Structure

```
s8r/
├── pom.xml
├── s8r/
│   ├── pom.xml
│   ├── version.properties
│   ├── s8r-core/
│   │   ├── pom.xml
│   │   └── src/
│   │       ├── main/
│   │       └── test/
```

## Package Names

### Old Structure

```java
package org.samstraumr.tube;
package org.samstraumr.tube.composite;
package org.samstraumr.tube.machine;
```

### New Structure

```java
package org.s8r.component.core;
package org.s8r.component.composite;
package org.s8r.component.machine;
```

## Migration Strategy

The migration will be performed incrementally:

1. Create the new structure in a separate folder
2. Copy and adapt the necessary files (POMs, configuration, Java sources)
3. Test the new structure to ensure everything works
4. Update scripts and documentation
5. Replace the old structure with the new one

We've prepared a migration script (`migrate-to-s8r.sh`) to automate most of this process.

## Impact on Scripts and Utilities

All references to "Samstraumr" in scripts and utilities will need to be updated:

- Replace `samstraumr` with `s8r` in script variables
- Update any hardcoded paths
- Update configuration files (.samstraumr.config → .s8r.config)

## Version Changes

- The version.properties file has been updated to use the s8r prefix
- Project version will be incremented to ${samstraumr.version} for the full migration

## Migration Steps for Developers

1. Check out the latest version from the main branch
2. Run the migration script (`./temp/migrate-to-s8r.sh`)
3. Review the generated files in the `s8r-migration` directory
4. Run tests in the new structure to ensure everything works
5. Update any local scripts to reference the new structure

## Timeline

- Phase 1: Initial structure and POM files (Complete)
- Phase 2: Core component migration (Complete)
- Phase 3: Test implementation and verification (In Progress)
- Phase 4: Script updates and full deployment (Planned)

## API Compatibility

The API changes are significant, with new class names and package structures. This is a major version change that will require client code updates. A separate migration guide for client code will be provided.
