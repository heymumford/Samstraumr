<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Project Structure Refactoring Summary

## Changes Made

1. **Consolidated Script Organization**
   - Moved all scripts into organized subdirectories under `util/bin/`
   - Categorized scripts into clear functional groups:
     - `build/` - Build-related scripts
     - `test/` - Testing-related scripts
     - `quality/` - Quality check scripts
     - `docs/` - Documentation generation scripts
     - `version/` - Version management scripts
     - `utils/` - Utility scripts
     - `config/` - Configuration scripts
2. **Removed Redundancy**
   - Eliminated duplicate scripts in root directory
   - Removed all `.bak` files
   - Removed unnecessary symlinks
   - Cleaned up build artifacts
3. **Implemented Standardized Configuration**
   - Created `.s8r/` directory for project-specific configuration
   - Added `config.json` with project settings
   - Set up user-specific configuration in `~/.s8r/config.json`
   - Moved Docmosis configuration to proper location
4. **Improved Documentation**
   - Updated build documentation
   - Created proper changelog in `docs/reference/release/changelog.md`
   - Reorganized documentation files
5. **Enhanced Build Process**
   - Updated `.gitignore` to exclude proper files
   - Ensured all scripts are executable
   - Modified `s8r` to use new script locations

## New Directory Structure

```
/
├── s8r                    # Main entry point script (recommended)
├── .s8r/                  # Project-specific configuration
│   ├── config.json        # Project configuration
│   └── config/            # Additional configuration files
├── util/
│   ├── bin/               # Consolidated executable scripts
│   │   ├── build/         # Build-related scripts
│   │   ├── test/          # Testing-related scripts
│   │   ├── quality/       # Quality check scripts
│   │   ├── docs/          # Documentation generation scripts
│   │   ├── version/       # Version management scripts
│   │   ├── utils/         # Utility scripts
│   │   └── config/        # Configuration scripts
│   ├── lib/               # Shared bash libraries
│   └── samstraumr         # Alternative entry point CLI
├── Samstraumr/            # Main code modules
├── docs/                  # Documentation
└── [other project files]
```

## Usage

The standard workflow remains the same, but with a cleaner structure:
- Use `./s8r <command>` for all operations
- Configuration is automatically loaded from `.s8r/config.json`
- User-specific settings are stored in `~/.s8r/config.json`

## Benefits

1. **Maintainability**: Clear organization makes it easier to find and update scripts
2. **Consistency**: Standardized structure reduces confusion
3. **Separation of Concerns**: Better isolation of functionality
4. **Configuration Management**: Proper separation of project vs. user settings
5. **Reduced Clutter**: Cleaner root directory makes project navigation simpler
