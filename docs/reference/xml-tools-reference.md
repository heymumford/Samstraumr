# XML Standardization Tools Reference

## Overview

This document describes the XML standardization tools available in the Samstraumr project, which help maintain consistent formatting and structure across XML files, particularly Maven POM files.

## Tools

### s8r-xml-standardize (s8r-xml)

**Purpose**: Standardize XML files with consistent formatting and detect common issues.

**Location**: `/s8r-xml` (symlink to `/s8r-xml-standardize`)

**Features**:
- Formats XML files with consistent indentation
- Validates required elements
- Detects missing plugin versions
- Finds duplicate dependencies and plugins
- Identifies incorrect element names (e.g., `<n>` should be `<name>`)

**Usage**:
```bash
# Format all XML files
./s8r-xml

# Check only (no changes)
./s8r-xml --check

# Format only POM files
./s8r-xml --pom-only

# Verbose output
./s8r-xml --verbose

# Process a specific file
./s8r-xml path/to/file.xml
```

## Automated Checks

The XML standardization is integrated into the Git workflow via hooks:

1. **Pre-push Hook**: Runs the XML standardization check every 10 builds
2. **Build Number Tracking**: Increments the build number in `modules/version.properties` on every push
3. **Counter Mechanism**: Tracks which builds should trigger XML checks

### How it works

1. The pre-push hook increments the build number in `version.properties`
2. If the new build number is a multiple of 10, the XML check is performed
3. If issues are found, the push is blocked until they're resolved
4. A counter file (`.xml_check_counter`) tracks the check sequence

## Common Issues Detected

1. **Missing plugin versions**: All Maven plugins should specify explicit versions for reproducible builds
2. **Duplicate dependencies**: Redundant dependencies that may cause conflicts
3. **Duplicate plugins**: Multiple declarations of the same plugin
4. **Invalid element names**: Using abbreviated elements like `<n>` instead of `<name>`

## Resolving Issues

When the XML standardization check fails, you have two options:

1. **Automatic Fix**: Run `./s8r-xml` without the `--check` flag to automatically fix formatting issues
2. **Manual Fix**: Edit the files directly to address structural issues like missing versions or duplicates

## Implementation Details

The XML standardization system uses [XMLStarlet](http://xmlstar.sourceforge.net/) for XML processing, which provides a command-line tool for querying, transforming, and validating XML documents.

Key files:
- `/s8r-xml-standardize`: Main standardization script
- `/util/git/pre-push-hook.sh`: Git hook that runs the standardization check
- `/util/git/install-hooks.sh`: Script to install the Git hooks

## Configuration

The frequency of XML checks can be adjusted by modifying the `XML_CHECK_FREQUENCY` variable in `/util/git/pre-push-hook.sh`. The default is set to 10 (every 10th build).
