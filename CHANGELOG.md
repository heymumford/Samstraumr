<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Changelog

All notable changes to the Samstraumr project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.7.3] - 2025-04-04

### Added
- Unified command interface (`s8r`) with extensible design
- New simplified build system (`s8r-build`) with multiple modes
- New simplified test system (`s8r-test`) supporting all test types
- Enhanced Docmosis integration with centralized document generation
- Improved change report generation with change history analysis
- Comprehensive documentation for build, test, and document generation systems
- Fallback mechanisms for document generation when Docmosis isn't available
- Git-based change report generation as alternative to Docmosis

### Changed
- Centralized Docmosis configuration and document generation
- Streamlined build process with improved error handling
- Enhanced test execution with proper Maven profile and tag mapping
- Fixed compilation error in Component.java constructor
- Updated documentation to include build, test, and document generation improvements
- Reorganized bash helper functions into modular library structure
- Made Docmosis integration optional but fully supported
- Improved command help documentation for document generation

### Fixed
- Incorrect dependency versions in POM file (using project version for libraries)
- Syntax error in Component.java ternary operator with throw statement
- Version inconsistencies between properties file and various POM files
- Docmosis configuration handling with more reliable detection

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
