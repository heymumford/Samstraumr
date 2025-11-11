<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# README

This directory contains scripts for managing and maintaining the documentation in the Samstraumr project.

## Documentation Standards

The Samstraumr project follows these documentation standards:

1. **File naming**: All documentation files use kebab-case (e.g., `folder-structure.md`)
2. **Directory structure**: Documentation is organized into 9 core directories
3. **Headers**: Level 1 headers match file title, Level 2 use title case, Level 3+ use sentence case
4. **Cross-references**: Use relative paths with .md extension
5. **Code blocks**: All code blocks specify a language

## Available Scripts

### Standardization scripts

| Script | Description |
|--------|-------------|
| `standardize-filenames.sh` | Standardizes filenames to follow kebab-case convention |
| `standardize-kebab-case.sh` | Converts various naming styles to kebab-case |
| `standardize-md-filenames.sh` | Specifically targets markdown files for renaming |
| `standardize-feature-filenames.sh` | Standardizes Cucumber feature file names |
| `standardize-planning-filenames.sh` | Standardizes planning document names |

### Verification and fixing scripts

| Script | Description |
|--------|-------------|
| `check-documentation-standards.sh` | Verifies documentation follows project standards |
| `update-cross-references.sh` | Updates cross-references between markdown files |
| `fix-markdown-links.sh` | Fixes broken links in markdown files |
| `update-markdown-headers.sh` | Updates headers to follow standardized formats |

### Todo management scripts

| Script | Description |
|--------|-------------|
| `extract-todos.sh` | Extracts TODOs from code and generates a report |
| `standardize-todos.sh` | Checks and standardizes TODOs in the codebase |
| `check-todo-format.sh` | CI check for TODO format compliance |
| `todo-to-issue.sh` | Creates GitHub issues from high-priority TODOs |

### Conversion scripts

| Script | Description |
|--------|-------------|
| `convert-to-markdown.sh` | Converts various document formats to markdown |

## Usage Examples

### Verify documentation standards

```bash
# README
./check-documentation-standards.sh

# README
./check-documentation-standards.sh --check filenames
./check-documentation-standards.sh --check headers
./check-documentation-standards.sh --check code_blocks
./check-documentation-standards.sh --check broken_links
```

### Manage todos

```bash
# README
./extract-todos.sh --output todo-report.md

# README
./check-todo-format.sh --verbose

# README
./standardize-todos.sh --fix

# README
./todo-to-issue.sh --priority P0,P1 --dry-run
```

### Update cross-references

```bash
# README
./update-cross-references.sh --check

# README
./update-cross-references.sh --dry-run

# README
./update-cross-references.sh --path ../guides
```

### Standardize filenames

```bash
# README
./standardize-filenames.sh --dry-run

# README
./standardize-filenames.sh ../concepts
```

## Integration with CI/CD

These scripts can be integrated into the CI/CD pipeline. For example:

```yaml
# README
documentation_check:
  script:
    - docs/scripts/check-documentation-standards.sh
  artifacts:
    paths:
      - target/doc-standard-reports/

todo_format_check:
  script:
    - docs/scripts/check-todo-format.sh --strict --high-priority
  artifacts:
    paths:
      - target/todo-check-report.md
```

## Related Documentation

- [TODO Standards](../reference/standards/todo-standards.md)
- [Documentation Standards](../reference/standards/documentation-standards.md)
- [File Organization Standards](../reference/standards/file-organization.md)
- [Documentation Standardization Plan](../plans/active-documentation-standardization.md)
