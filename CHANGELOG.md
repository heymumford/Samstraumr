# Changelog

All notable changes to the Samstraumr project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.7.3] - 2025-04-04

### Added
- New simplified build system (`s8r-build`) with multiple modes
- New simplified test system (`s8r-test`) supporting all test types
- Comprehensive documentation for build and test systems

### Changed
- Streamlined build process with improved error handling
- Enhanced test execution with proper Maven profile and tag mapping
- Fixed compilation error in Component.java constructor
- Updated documentation to include build and test improvements

### Fixed
- Incorrect dependency versions in POM file (using project version for libraries)
- Syntax error in Component.java ternary operator with throw statement
- Version inconsistencies between properties file and various POM files

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