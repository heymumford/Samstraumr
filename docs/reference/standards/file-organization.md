<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# FileOrganization

This document describes the standardized file organization for the Samstraumr project.

## Root Directory

The root directory should contain only essential files:

- **README.md** - Project overview and quick start guide
- **CLAUDE.md** - Special configuration file for AI assistant tools
- **LICENSE** - Project license
- **pom.xml** - Parent Maven build file
- **.gitignore** - Git ignore rules
- **.gitattributes** - Git attributes
- **.editorconfig** - Editor standardization

## Directory Structure

### Code organization

- **Samstraumr/** - Main code module
  - **samstraumr-core/** - Core library functionality
  - **pom.xml** - Module build file
  - **version.properties** - Version information

### Documentation

- **docs/** - All project documentation
  - **README.md** - Documentation overview
  - **concepts/** - Core concepts and architectural information
  - **guides/** - User guides and tutorials
  - **reference/** - Reference documentation, standards, glossary
    - **standards/** - Project standards and conventions
  - **contribution/** - Contributor documentation
  - **planning/** - Documentation planning and drafts
  - **proposals/** - Feature proposals and enhancements
  - **research/** - Research documents and evaluations
  - **testing/** - Testing strategy and documentation
  - **compatibility/** - Compatibility notes and fixes

### Utilities

- **util/** - All utility scripts
  - **README.md** - Script documentation
  - **scripts/** - Helper scripts
  - Various main utility scripts with function-based prefixes

### Temporary files

- **temp/** - Temporary work files (not committed to Git)

## File Naming Conventions

### Documentation files

- For standard project docs in project root, use **UPPER_CASE** (e.g., `README.md`, `LICENSE`)
- For supporting documentation files, use **PascalCase** (e.g., `Contributing.md`, `UserGuide.md`, `DeveloperGuide.md`)
- For reference documentation in subdirectories, prefer **PascalCase** (e.g., `JavaNamingStandards.md`, `CodeConventions.md`)
- For specific technical docs containing multiple words, use **PascalCase** (e.g., `ReleaseNotes.md`, `TestingStrategy.md`)

### Scripts

- Use **kebab-case** for all script files (e.g., `build-optimal.sh`)
- Use function-based prefixes:
  - `build-*` - Build-related scripts
  - `test-*` - Test-related scripts
  - `check-*` - Verification scripts
  - `fix-*` - Fix/repair scripts
  - `update-*` - Update scripts
  - `setup-*` - Setup and configuration scripts

## Organization Principles

1. **Essential files only at root** - Keep the root directory clean and focused on standard Java project files (README.md, pom.xml, LICENSE)
2. **Follow Java conventions** - Adhere to standard Java project layout and naming conventions
3. **Logical grouping** - Files should be grouped according to their function
4. **Consistent naming** - Use consistent naming conventions appropriate to file type:
   - UPPER_CASE for standard project files
   - PascalCase for supporting documentation
   - kebab-case for utility scripts
5. **Minimal nesting** - Avoid deep directory hierarchies
6. **Documentation proximity** - Keep documentation close to the code it describes

## Implementation Notes
