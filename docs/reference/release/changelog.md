<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Samstraumr Changelog

All notable changes to this project will be documented in this file.

## [1.3.0] - 2025-04-03
## [2.0.0] - 2025-04-04

### Added

- Java 21 support with graceful fallback mechanisms
- Compatibility layer for Java module system restrictions
- Improved runtime environment detection with enhanced JVM property reporting
- Fallback mechanism for document generation when Docmosis is unavailable

### Changed

- Updated build system to use Java 21 by default
- Enhanced reflection handling for improved compatibility with Java 21
- Added JVM options for proper module system interaction

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
