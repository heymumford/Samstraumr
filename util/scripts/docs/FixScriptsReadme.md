<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
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
