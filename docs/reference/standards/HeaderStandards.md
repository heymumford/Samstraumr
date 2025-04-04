# Header Standards

## Overview
This document outlines the standards for file headers in the Samstraumr project.

## Principles
- Headers should be simple and clean
- No author information, creation dates, or modification dates
- Focus on purpose and functionality, not metadata

## Java File Headers
```java
/*
 * [FILENAME]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * [PURPOSE_DESCRIPTION]
 */
```

## Bash Script Headers
```bash
#!/bin/bash
#==============================================================================
# Filename: script-name.sh
# Description: Brief description of what the script does
#==============================================================================
# Usage: ./script-name.sh [options] <args>
#
# Options:
#   -h, --help          Display this help message
#   -v, --verbose       Enable verbose output
#
# Examples:
#   ./script-name.sh                # Basic usage
#   ./script-name.sh -v             # With verbose output
#==============================================================================
```

## Markdown File Headers
```markdown
# Document Title

Brief description of the document's purpose.
```

## Header Update Tools
To update headers across the codebase, use:

- For Java files: `./util/bin/utils/update-java-headers.sh`
- For script files: `./simplify-headers.sh`

These tools have been configured to use the simple header formats shown above.
