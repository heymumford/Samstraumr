# XML Tools Reference

This document provides reference information for the XML tools included in the Samstraumr project.

## Overview

The Samstraumr XML tools are designed to:

1. Validate XML files, especially Maven POM files
2. Fix common XML issues, such as `<n>` vs `<name>` in POM files
3. Prevent XML errors from being committed
4. Provide precise XML manipulation capabilities

## Command Line Interface

The `s8r-xml` script provides a convenient interface to the XML utilities:

```bash
./s8r-xml <command> [options] [path]
```

### Commands

- `validate`: Validate XML files
- `fix`: Fix common XML problems
- `check`: Check for XML issues without fixing them
- `pom`: Check/fix POM files specifically
- `help`: Show help message

### Options

- `-r, --recursive`: Process directories recursively
- `-v, --verbose`: Show verbose output

### Examples

```bash
# Validate a specific POM file
./s8r-xml validate ./modules/samstraumr-core/pom.xml

# Fix all POM files in the modules directory recursively
./s8r-xml fix -r ./modules

# Check a specific POM file for issues
./s8r-xml pom ./modules/samstraumr-core/pom.xml

# Fix a specific POM file
./s8r-xml pom --fix ./modules/samstraumr-core/pom.xml
```

## Pre-commit Hook

The XML validation is integrated into the pre-commit hook to prevent XML issues from being committed:

1. When you commit XML files, the hook will:
   - Validate XML syntax
   - Check for `<n>` vs `<name>` issues in POM files
   - Reject the commit if issues are found

2. If an issue is found, the hook will:
   - Display an error message
   - Suggest a fix command
   - Prevent the commit

3. To fix issues:
   ```bash
   ./s8r-xml fix <file-path>
   ```

4. To bypass the hook (not recommended):
   ```bash
   git commit --no-verify
   ```

## Specialized XML Utilities

### XML Validation

For XML validation:

```bash
./util/scripts/check-pom-files.sh --action validate [path]
```

This will:
- Check XML syntax
- Validate against XML schema if available
- Report any issues found

### POM File Fixes

For fixing POM files:

```bash
./util/scripts/fix-pom-tags.sh --fix [path]
```

This will:
- Fix `<n>` tags to proper `<name>` tags
- Validate XML after fixes
- Report the changes made

## Important Guidelines

1. **Never abbreviate XML element names**
   - Always use standard Maven element names (e.g., `<name>`, never `<n>`)
   - Follow the XML schema for the file type

2. **Validate XML files before committing**
   - Run `./s8r-xml validate` on modified files
   - Fix any issues found

3. **Use XML-aware tools for editing**
   - Avoid text-based search/replace on XML files
   - Use XMLStarlet or xmllint for precise XML manipulation

## Technical Details

The XML tools are built on:

- **XMLStarlet**: For precise XML manipulation
- **xmllint**: For validation and formatting
- **Bash scripts**: For workflow integration

The library is located at `./util/lib/xml-lib.sh` and provides functions for:

- XML validation
- POM file analysis
- XML element access and modification
- Error reporting and fixing

## Additional Resources

- [Maven POM Reference](https://maven.apache.org/pom.html)
- [XMLStarlet Documentation](http://xmlstar.sourceforge.net/doc.php)
- [XML Schema Documentation](https://www.w3.org/XML/Schema)