# Maven Structure Testing

This document describes the Maven structure testing framework implemented in the Samstraumr project. The framework provides comprehensive validation of the project's Maven structure, ensuring it follows architectural best practices and standards.

## Overview

The Maven structure tests verify:

1. **Three-Tier Maven Structure** - Validating the parent-module-core hierarchy
2. **Dependency Management** - Ensuring proper dependency configuration
3. **Plugin Management** - Verifying plugin configuration and versioning
4. **Maven Profiles** - Validating profile setup for various build scenarios
5. **Clean Architecture Alignment** - Confirming compliance with architectural principles
6. **Test Framework Configuration** - Verifying test dependency and plugin setup

## Running the Tests

The Maven structure tests can be run using the provided script:

```bash
# Maven Structure Testing
./test-maven-structure.sh

# Maven Structure Testing
./test-maven-structure.sh quick

# Maven Structure Testing
./test-maven-structure.sh --verbose
```

The script will test the following aspects of the Maven structure:

1. POM hierarchy and versions
2. Module references
3. Dependency management
4. Plugin configuration
5. Maven profiles
6. Clean Architecture alignment

## Test Components

### Maven structure test

The `maven-structure-test.sh` script checks basic Maven structure:

- POM file structure and module references
- Version consistency across all POMs and version.properties
- Dependency management and direct version specifications
- Plugin management and configuration
- Presence of critical profiles like tdd-development and quality-checks
- Clean Architecture package structure

### Maven profile test

The `maven-profile-test.sh` script tests Maven profiles:

- Activation of parent POM profiles
- Activation of core module profiles
- Verification of profile-specific properties
- Testing of test configuration properties like cucumber.filter.tags

## Integration with Architecture Tests

The Maven structure tests are part of the larger architecture test suite, ensuring the project's structure adheres to architectural decisions. They validate that:

1. The structure follows Clean Architecture principles
2. Dependencies flow in the correct direction
3. The layering is properly maintained
4. Testing infrastructure is correctly configured
5. Profiles enable different build and test scenarios

## Best Practices

When working with Maven in this project, follow these guidelines:

1. **Maintain Dependency Management** - Always manage dependencies in the parent POM
2. **Use Properties for Versions** - Define version numbers as properties
3. **Define Plugins in PluginManagement** - Configure plugins in the parent POM's pluginManagement section
4. **Use Profiles for Build Variants** - Create profiles for specific build scenarios
5. **Respect Clean Architecture** - Ensure dependencies follow the dependency rule (pointing inward)
6. **Keep Versions Consistent** - Ensure the same version is used in all POMs and version.properties
7. **Define Test Framework Properly** - Configure test dependencies and plugins correctly

## Troubleshooting

If the Maven structure tests fail, check:

1. **Version Inconsistency** - Ensure all POMs have the same version
2. **Missing Properties** - Verify needed properties are defined
3. **Direct Versions** - Check for versions defined directly instead of via properties
4. **Missing Plugin Management** - Ensure plugins are defined in pluginManagement
5. **Profile Configuration** - Verify profiles have correct properties and plugins
6. **Architecture Violations** - Check for Clean Architecture dependency violations
