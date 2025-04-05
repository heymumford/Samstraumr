<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# HeaderStandards

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
# HeaderStandards
# HeaderStandards
# HeaderStandards
# HeaderStandards
# HeaderStandards
# HeaderStandards
#
# HeaderStandards
# HeaderStandards
# HeaderStandards
#
# HeaderStandards
# HeaderStandards
# HeaderStandards
# HeaderStandards
```

## Markdown File Headers

```markdown
# HeaderStandards

Brief description of the document's purpose.
```

## Header Update Tools

To update headers across the codebase, use:

- For Java files: `./util/bin/utils/update-java-headers.sh`
- For script files: `./simplify-headers.sh`

These tools have been configured to use the simple header formats shown above.
