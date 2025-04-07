<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# README

This directory contains tools for generating, maintaining, and ensuring the integrity of the S8r documentation.

## Purpose

These tools help automate the process of creating, checking, maintaining, and improving documentation across the repository. They help ensure consistent formatting, accurate references, and up-to-date content.

## Contents

- `doc-integrity-check.sh` - Checks and fixes documentation integrity issues
- `doc-integrity-check-auto.sh` - Non-interactive version for CI/CD and automation
- `generate-changelog.sh` - Generates a changelog from git commit history
- `update-readme.sh` - Updates README.md with the latest project information
- `generate-javadoc.sh` - Generates JavaDoc API documentation with custom styling
- `s8r-docs` - CLI wrapper for all documentation tools (linked as `/s8r-docs` in project root)

## Usage

### Cli wrapper

The `s8r-docs` script provides a unified interface for all documentation tools. This is also available as `/s8r-docs` in the project root and integrated with the main `s8r` CLI.

```bash
# README
./s8r-docs all

# README
./s8r-docs api

# README
./s8r-docs readme

# README
./s8r-docs changelog

# README
./s8r-docs check
```

### Changelog generation

The `generate-changelog.sh` script automatically generates a changelog from git commit history using conventional commit messages.

```bash
# README
./generate-changelog.sh

# README
./generate-changelog.sh --from v1.0.0

# README
./generate-changelog.sh --update

# README
./generate-changelog.sh --output docs/CHANGELOG.md
```

### Readme updates

The `update-readme.sh` script updates the README.md file with the latest project information, including version, badges, and usage examples.

```bash
# README
./update-readme.sh

# README
./update-readme.sh --sections version,badges

# README
./update-readme.sh --template docs/templates/README-template.md
```

### Javadoc api documentation

The `generate-javadoc.sh` script generates JavaDoc API documentation with customizations for the Samstraumr project.

```bash
# README
./generate-javadoc.sh

# README
./generate-javadoc.sh --packages org.s8r.component,org.s8r.domain

# README
./generate-javadoc.sh --links

# README
./generate-javadoc.sh --markdown

# README
./generate-javadoc.sh --output docs/api
```

### Documentation integrity check

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
