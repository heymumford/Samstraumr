# Test Utilities

This directory contains test-related utility scripts and configuration files for the Samstraumr project.

## Contents

- `move-problematic-tests.sh` - Script to temporarily move problematic test files out of the way
- `restore-problematic-tests.sh` - Script to restore previously moved test files
- `run-isolated-test.sh` - Script to compile and run isolated tests directly
- `run-tests.sh` - Script to run all tests 
- `s8r-test-alz001` - Script to run ALZ001-specific tests
- `surefire-settings.xml` - Maven Surefire plugin configuration for enabling tests

## Usage

All scripts can be run from their location in this directory or via symlinks in the project root.

### Example usage:

```bash
# Move problematic test files temporarily
./move-problematic-tests.sh

# Restore problematic test files
./restore-problematic-tests.sh

# Run isolated tests
./run-isolated-test.sh

# Run ALZ001 tests
./s8r-test-alz001
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
- `/bin/xml-utils` - XML-specific utility scripts 
- `/docs/test-reports` - Test documentation and reports