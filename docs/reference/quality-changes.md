<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Quality Changes

## PMD Removal (April 2025)

PMD has been removed from the project in favor of using SonarQube as the primary code quality tool.

### Rationale for removal

- PMD was causing compatibility issues with ruleset schemas
- SonarQube provides a more comprehensive set of rules and better integration with CI/CD pipelines
- Maintaining multiple static analysis tools created redundancy

### What was removed

- PMD plugin configuration from all pom.xml files
- pmd-ruleset.xml file
- PMD-related properties and version declarations

### Impact

- Builds are now faster and more reliable
- Static code analysis is now consolidated in SonarQube
- Developers no longer need to fix PMD-specific issues that might conflict with SonarQube

### Reintroducing pmd (if needed)

If specific PMD rules are needed that SonarQube doesn't cover:

1. Add the PMD plugin back to the pom.xml files
2. Create a minimal ruleset with only the specific rules needed
