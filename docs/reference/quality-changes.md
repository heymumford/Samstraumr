<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
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
