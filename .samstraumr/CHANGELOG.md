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

# S8r Framework - Change Log

## 1.7.0 - 2025-04-04

### Added

- New `Machine` implementation in domain layer with improved state management

### Changed

- Optimized domain entity classes for conciseness while maintaining clean architecture
- Reduced verbosity in Component, LifecycleState, and ComponentId classes
- Streamlined domain event implementations with cleaner interfaces
- Simplified exception classes while preserving functionality
- Overall 42% code reduction across core domain layer classes

### Fixed

- Improved readability of core domain components
- Enhanced maintainability through more concise implementation

## 1.0.0 - 2025-04-04

### Added

- Created `.samstraumr` directory for unified configuration
- Added `config.json` as the central configuration store
- Added `config.sh` auto-generated for shell script compatibility
- Added script to convert between JSON and shell formats
- Created template for standardized script headers
- Added test script to validate configuration loading
- Added migration utilities for legacy configurations

### Changed

- Consolidated configuration from `.s8r/config.json` and `.samstraumr.config`
- Updated scripts to use the new unified configuration
- Added configuration system documentation to main README.md
- Standardized path and package variable naming

### Fixed

- Resolved inconsistent path definitions between CLI and scripts
- Eliminated duplication of configuration settings
- Created proper isolation between system and user settings
