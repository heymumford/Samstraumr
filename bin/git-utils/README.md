# Git Utilities

This directory contains Git-related utility scripts and configuration files for the Samstraumr project.

## Contents

- `push` - Git push configuration file

## File Organization Standard

According to our project standards:

1. All utility scripts should be organized in appropriate directories by function
2. The main bin directory should contain only high-level executables
3. Specialized utilities should be in specific subdirectories (test-utils, xml-utils, git-utils, etc.)
4. Symbolic links may be created in the root directory for backward compatibility
5. In case of file location changes, we prefer to update references rather than maintain symlinks
6. Tests should fail if file locations break functionality, prompting proper fixes

## Related Directories

- `/bin` - Main executables and scripts
- `/bin/utils` - General utility scripts
- `/bin/test-utils` - Test-specific utility scripts
- `/util/scripts` - Implementation of utility functions