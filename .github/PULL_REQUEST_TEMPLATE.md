<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Pull Request Description

## Summary

- Refactored build scripts to eliminate redundant Java tool options messages
- Created a centralized Java environment setup script for consistent Maven configurations
- Updated test scripts to use the shared environment setup

## Changes Made

- Created `util/build/java-env-setup.sh` for centralized Java environment configuration
- Modified `run-tests.sh` and `run-atl-tests.sh` to use the new setup script
- Updated `java17-compat.sh` to properly handle MAVEN_OPTS
- Ensured proper handling of JAVA_TOOL_OPTIONS to avoid redundant messages during builds

## Test Plan

- Verified ATL tests still pass with the new configuration
- Confirmed JVM tool option messages no longer appear three times during builds
- Ensured all build scripts maintain consistent encoding settings

## Documentation Updates

- Updated build scripts with clear comments explaining the environment setup

## Related Issues

- Resolves issue with redundant "Picked up JAVA_TOOL_OPTIONS" messages during builds
