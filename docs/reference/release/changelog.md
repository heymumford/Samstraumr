<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Samstraumr Changelog

All notable changes to this project will be documented in this file.

## [1.3.0] - 2025-04-03
## [2.4.5] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.4.4] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.4.3] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.4.2] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.4.1] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.4.0] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.3.0] - 2025-04-06

### Added

- Local CI workflow compatible with Docker containers
- Pre-commit hooks for CI validation
- Integrated Git hooks installation
- Build system support for CI validation

### Changed

- CI pipeline now supports local testing via nektos/act
- Pre-commit hooks respect Java file changes
- More robust test runners with improved reliability

### Fixed

- Docker compatibility issues with CI tests
- Maven dependency issues in container environments
- Test discovery and execution in CI pipeline
- Coverage reporting for component tests
## [2.2.0] - 2025-04-06

### Added

- Comprehensive repository cleanup plan in `cleanup-plan.md`
- Consolidated script libraries for header and test management
- New research document on testing in the age of AI
- Improved documentation for Cucumber integration
- Enhanced testing infrastructure with dedicated test runners
- Java 21 compatibility adjustments for reflection operations

### Changed

- Simplified repository structure by removing redundant backup directories
- Improved code organization with cleaner package structure
- Consolidated duplicate scripts into shared libraries
- Enhanced .gitignore to prevent buildup of temporary files
- Standardized build and test scripts for consistency

### Fixed

- Test execution issues when running different test types
- Maven profile misalignment in test scripts
- Build system test skipping problems
- JaCoCo coverage integration issues
- Cucumber test discovery mechanism
## [2.1.0] - 2025-04-06

### Added

- 

### Changed

- 

### Fixed

- 
## [2.0.3] - 2025-04-04

### Added

- 

### Changed

- 

### Fixed

- 
## [2.0.2] - 2025-04-04

### Security

- Updated PMD dependencies from 2.0.1 to 7.12.0 to address security vulnerabilities
- Updated Mockito to 5.17.0 and TestContainers to 1.20.6
- Added explicit scope declarations to Jackson dependencies

### Added

- Created component duplicates cleanup plan for improved code organization

### Changed

- Documented migration strategy for legacy Tube implementations to Component architecture
## [2.0.1] - 2025-04-04

### Added

- 

### Changed

- 

### Fixed

- 
## [2.0.0] - 2025-04-04

### Added

- Java 21 support with graceful fallback mechanisms
- Compatibility layer for Java module system restrictions
- Improved runtime environment detection with enhanced JVM property reporting
- Fallback mechanism for document generation when Docmosis is unavailable
- Maven Enforcer Plugin for build environment validation
- Dependency convergence enforcement
- Minimum Maven version requirement (3.8.0)

### Changed

- Updated build system to use Java 21 by default
- Enhanced reflection handling for improved compatibility with Java 21
- Added JVM options for proper module system interaction
- Updated dependencies to latest versions:
  - Jackson Core and Databind: 2.0.0 → 2.18.3
  - OSHI Core: 2.0.0 → 6.8.0
  - SpotBugs Annotations: 2.0.0 → 4.9.3
  - ArchUnit: 1.2.0 → 1.4.0
  - Cucumber: 7.14.0 → 7.22.0
  - Log4j: 2.22.0 → 2.23.1
  - JUnit Jupiter: 5.10.1 → 5.10.2
  - Mockito: 5.8.0 → 5.10.0
  - Lombok: 1.18.30 → 1.18.38
  - SLF4J: 2.0.9 → 2.0.12
  - TestContainers: 1.19.3 → 1.20.0
  - Checkstyle: 10.14.0 → 10.23.0
  - PMD: 2.0.0 → 7.12.0
  - FindSecBugs: 1.12.0 → 1.13.0

### Fixed

- Removed usage of deprecated Runtime.exec() API
- Fixed compatibility issues with Java 21's stronger module boundaries
- Improved exception handling for reflection operations
## [1.7.3] - 2025-04-04

### Added

- 

### Changed

- 

### Fixed

- 
## [1.7.2] - 2025-04-04

### Added

- New simplified version management system (`s8r-version`)
- Automatic changelog entry generation during version updates
- Version inconsistency fixing with `s8r-version fix` command

### Changed

- Updated version management documentation to include both systems
- Enhanced CLAUDE.md with improved version management documentation
- Streamlined version update output with clearer status indicators

### Fixed

- POM file version sync issues with the original version system
- Version inconsistencies between properties file and various POM files
## [1.7.1] - 2025-04-04

### Added

- 

### Changed

- 

### Fixed

- 


### Added

- Docmosis documentation generation to s8r CLI
- s8r unified CLI command for Samstraumr
- Support for version bumping in s8r version command
- Support for combined industry-standard and Samstraumr test tags

### Changed

- Standardized file headers and simplified documentation
- Simplified version management scripts
- Consolidated all test tags to support both industry-standard and Samstraumr terminology
- Reorganized project structure for better maintainability
- Refactored bash scripts with functional programming principles
- Implemented centralized configuration system

### Fixed

- Various small bugs and inconsistencies
- Fixed code formatting and added missing classes

## [1.2.9] and earlier

- Initial development and foundational work
- See Git history for details
