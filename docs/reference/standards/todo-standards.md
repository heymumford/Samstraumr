<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# TODO Standards

This document outlines the standard format for TODOs in the Samstraumr codebase.

## Standard Format

TODOs should follow this format:

```
// TODO [Priority] (Category) (#Issue): Description
```

### Examples

```java
// TODO [P1] (BUG) (#123): Fix null pointer exception in TubeIdentity constructor
// TODO [P2] (FEAT): Implement automatic reconnection for disconnected components
// TODO [P0] (SECURITY): Fix potential security vulnerability in authentication
```

## Priority Levels

TODOs must include a priority level:

- **[P0]**: Critical - Must be fixed immediately
  - Security vulnerabilities
  - Crashes
  - Data corruption issues
  - Blocker bugs preventing core functionality
  
- **[P1]**: High - Should be fixed soon
  - Important bugs
  - Performance issues affecting users
  - Missing core features
  - Issues that should be fixed before the next release
  
- **[P2]**: Medium - Fix when time permits
  - Minor bugs
  - Code improvements
  - Features that would be nice to have
  - Technical debt items
  
- **[P3]**: Low - Nice to have
  - Cosmetic issues
  - Very minor bugs
  - Code style improvements
  - Nice-to-have features

## Categories

TODOs should include a category to help with organization and filtering:

- **BUG**: Bug fix
- **FEAT**: New feature implementation
- **REFACTOR**: Code refactoring (no functional change)
- **PERF**: Performance improvement
- **DOC**: Documentation improvement
- **TEST**: Test improvements or additions
- **INFRA**: Infrastructure (build, CI/CD, etc.)
- **SECURITY**: Security-related issues
- **TASK**: General task that doesn't fit other categories

## GitHub Issues

High-priority TODOs (P0 and P1) must have a corresponding GitHub issue:

```java
// TODO [P0] (SECURITY) (#456): Fix potential security vulnerability in authentication
```

The issue number should be the actual GitHub issue number. This helps track high-priority work items and ensures they're not forgotten.

## Tools

The following tools are available to work with TODOs:

### Extract TODOs

Lists all TODOs in the codebase and generates a report:

```bash
./docs/scripts/extract-todos.sh
```

Options include filtering by priority, category, and more.

### Standardize TODOs

Automatically fixes non-compliant TODOs to follow the standard format:

```bash
./docs/scripts/standardize-todos.sh --fix
```

### GitHub Issue Creation

Creates GitHub issues from high-priority TODOs:

```bash
./docs/scripts/todo-to-issue.sh [--dry-run]
```

### CI Check

Verifies that TODOs follow the standard format in CI:

```bash
./docs/scripts/check-todo-format.sh [--strict]
```

## Handling TODOs

TODOs should be regularly reviewed and addressed:

1. **P0 TODOs**: Should be fixed immediately
2. **P1 TODOs**: Should be planned for the next sprint
3. **P2 and P3 TODOs**: Should be reviewed in regular tech debt sessions

## Benefits

Standardizing TODOs provides several benefits:

1. **Prioritization**: Helps team focus on the most critical issues
2. **Traceability**: Links TODOs to GitHub issues for tracking
3. **Categorization**: Organizes TODOs by type for better management
4. **Automation**: Enables automatic extraction and reporting
5. **Consistency**: Makes TODOs easier to read and understand