<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Fix Scripts

This directory contains scripts to fix various issues in the project.

## Available Scripts

### `fix-logger.sh`

- Fixes logger variables in the PatternSteps.java file
- Replaces all instances of the variable `logger` with the correct constant `LOGGER`

### `fix-pmd.sh`

- Fixes PMD compatibility issues
- Either use this script, or run the build with quality checks skipped:

```bash
mvn test -P skip-quality-checks -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false
```
