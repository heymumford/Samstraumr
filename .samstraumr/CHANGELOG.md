# Unified Configuration System - Change Log

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