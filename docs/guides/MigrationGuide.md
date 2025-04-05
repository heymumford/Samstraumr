<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr Migration Guide

This document provides a comprehensive guide to migrating from the legacy Samstraumr framework to the new S8r implementation.

## Migration Overview

The Samstraumr project has undergone a significant refactoring to improve its architecture, package structure, and overall usability. The main changes include:

1. **Maven Structure**: New Maven coordinates and module organization
2. **Package Structure**: Streamlined package hierarchy with reduced depth
3. **Component Model**: Replacing the Tube abstraction with a more flexible Component model
4. **Build System**: Improved build scripts and utilities
5. **Migration Tools**: Tools to assist with the migration process

## Migration Implementation Status

### Accomplished

1. **Maven Structure**
   - Created new Maven structure with `org.s8r` groupId and `s8r-parent`, `s8r-modules`, and `s8r-core` artifactIds
   - Set up proper parent-child relationships between POMs
   - Updated version to ${samstraumr.version} to reflect the significant changes
2. **Package Structure**
   - Implemented new package structure at `org.s8r.component.*` with reduced depth
   - Organized packages by functionality: core, composite, machine, identity, logging, exception
   - Consolidated related classes (Status and LifecycleState → State)
3. **Component Model**
   - Implemented Component class as a replacement for Tube
   - Created a unified State enum to replace separate Status and LifecycleState enums
   - Improved environment interaction and identity management
4. **Migration Tools**
   - Created `migrate-to-s8r.sh` to set up the migration directory structure
   - Implemented `migrate-code.sh` to help clients migrate their code
   - Documented the process in comprehensive migration guides
5. **Documentation**
   - Created Maven structure guide with detailed information about the changes
   - Wrote a client migration guide with examples and API mapping
   - Updated Kanban board with migration roadmap and task tracking

### Current Status

- The new structure is set up in the temporary directory
- Basic builds are working
- Migration tools are in place
- Documentation is ready for review

### Next Steps

1. **Testing**
   - Set up and run tests in the new structure
   - Verify all functionality works correctly
   - Ensure all scripts function with the new names and paths
2. **Script Updates**
   - Update utility scripts to use `s8r` instead of `samstraumr`
   - Update configuration to point to the new structure
   - Create symbolic links for backward compatibility
3. **Final Implementation**
   - Move from temporary directory to final location
   - Tag as version ${samstraumr.version}
   - Set up CI/CD for the new structure
4. **Support**
   - Provide examples for common migration patterns
   - Monitor adoption and gather feedback
   - Address any issues that arise during migration

## Migration Guide for Client Code

### Maven Dependency Updates

Update your Maven dependencies:

```xml
<!-- Old dependency -->
<dependency>
    <groupId>org.samstraumr</groupId>
    <artifactId>samstraumr-core</artifactId>
    <version>1.6.x</version>
</dependency>

<!-- New dependency -->
<dependency>
    <groupId>org.s8r</groupId>
    <artifactId>s8r-core</artifactId>
    <version>${samstraumr.version}</version>
</dependency>
```

### Package Import Updates

Update your import statements:

```java
// Old imports
import org.samstraumr.tube.Tube;
import org.samstraumr.tube.Environment;
import org.samstraumr.tube.TubeStatus;
import org.samstraumr.tube.TubeLifecycleState;

// New imports
import org.s8r.component.Component;
import org.s8r.component.Environment;
import org.s8r.component.State;
```

### API Changes

Update your code to use the new API:

```java
// Old code
Tube tube = new Tube(env);
tube.initialize();
TubeStatus status = tube.getStatus();
TubeLifecycleState lifecycle = tube.getLifecycleState();

// New code
Component component = new Component(env);
component.initialize();
State state = component.getState();
```

### Using Migration Tools

To assist with migration, use the provided migration tools:

1. Run the setup script to create a migration directory:

   ```bash
   ./s8r migrate setup
   ```
2. Run the code migration script to update your code:

   ```bash
   ./s8r migrate code --source=/path/to/your/code --target=/path/to/migration/dir
   ```
3. Review the migrated code and make any necessary adjustments.
4. Update your build scripts to use the new paths and dependencies.

### Testing After Migration

After migration, thoroughly test your code:

1. Run all unit tests to ensure basic functionality.
2. Run integration tests to verify component interactions.
3. Check for any deprecation warnings and address them.
4. Verify that all lifecycle events are properly handled.

## Support Resources

- Migration documentation: `docs/guides/Migration.md`
- API mapping guide: `docs/guides/ApiMapping.md`
- Migration tools: `util/bin/migrate/`
- Support: Open an issue on GitHub or contact the Samstraumr team

## Timeline

The migration period will last for 6 months, during which time both the old and new APIs will be maintained. After this period, the old API will be deprecated and eventually removed.

## Feedback

We welcome feedback on the migration process. Please share your experiences, suggestions, and issues through our GitHub repository or by contacting the development team.
