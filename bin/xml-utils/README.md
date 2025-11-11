# XML Utilities

This directory contains XML-related utility scripts and tools for the Samstraumr project.

## Contents

- `s8r-xml` - Script to manage XML files, especially POM files

## Usage

The s8r-xml script provides functionality for validating, checking, and fixing XML files:

```bash
# Validate an XML file
./s8r-xml validate ./modules/samstraumr-core/pom.xml

# Fix common XML problems in a directory recursively
./s8r-xml fix -r ./modules

# Check POM files without fixing
./s8r-xml pom ./modules/samstraumr-core/pom.xml

# Show help information
./s8r-xml help
```

## File Organization Standard

According to our project standards:

1. All utility scripts should be organized in appropriate directories by function
2. The main bin directory should contain only high-level executables
3. Specialized utilities should be in specific subdirectories (test-utils, xml-utils, etc.)
4. Symbolic links may be created in the root directory for backward compatibility
5. In case of file location changes, we prefer to update references rather than maintain symlinks
6. Tests should fail if file locations break functionality, prompting proper fixes

## Related Directories

- `/bin` - Main executables and scripts
- `/bin/utils` - General utility scripts
- `/bin/test-utils` - Test-specific utility scripts
- `/util/scripts` - Implementation of XML utilities referenced by this script