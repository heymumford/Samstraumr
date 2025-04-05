<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Documentation Tools

This directory contains tools for maintaining and ensuring the integrity of the S8r documentation.

## Purpose

These tools help automate the process of checking, maintaining, and improving documentation across the repository. They help ensure consistent formatting, accurate references, and up-to-date content.

## Contents

- `doc-integrity-check.sh` - Checks and fixes documentation integrity issues
- `doc-integrity-check-auto.sh` - Non-interactive version for CI/CD and automation
- Additional documentation maintenance tools will be added here

## Usage

### Documentation Integrity Check

The `doc-integrity-check.sh` script performs the following checks:

1. Verifies all internal links in markdown files reference existing files
2. Ensures consistency in section references
3. Validates that key sections and document structures are consistent
4. Checks for outdated package references
5. Reviews code examples for correct package usage
6. Verifies consistent header format with copyright notices
7. Checks for kebab-case filenames in markdown files
8. Validates header hierarchy and conventions
9. Generates a comprehensive documentation report

The script can automatically fix common issues:

1. Updates old package references (org.samstraumr → org.s8r, org.tube → org.s8r.tube.legacy)
2. Adds missing titles to README.md files
3. Adds standard sections (Purpose, Contents, Related) to documentation README files
4. Adds copyright headers to files missing them
5. Converts non-kebab-case filenames to kebab-case

To run the integrity check:

```bash
./docs/tools/doc-integrity-check.sh
```

The script will generate a report in `docs/documentation-integrity-report.md` and can optionally fix common issues automatically.

For CI/CD or automated environments, use the non-interactive version:

```bash
./docs/tools/doc-integrity-check-auto.sh
```

This version will automatically fix issues without prompting and is suitable for integration with build pipelines.

## Automation

It's recommended to run these tools:

1. Before creating documentation PRs
2. As part of the CI/CD pipeline
3. Periodically to maintain documentation quality
4. After major refactoring or package migrations

## Related

- [Documentation Standards](../reference/standards/documentation-standards.md)
- [File Naming Conventions](../reference/standards/file-naming-conventions.md)
- [Documentation Template](../documentation-template.md)