<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# TODO Review Process

This document outlines the standard process for regularly reviewing and managing TODOs in the Samstraumr codebase.

## Review Cycle

TODOs should be reviewed on a regular schedule to prevent technical debt accumulation:

1. **Weekly**: Team members review TODOs in their active work areas
2. **Bi-weekly**: Team lead reviews P0/P1 TODOs during sprint planning
3. **Monthly**: Full team reviews the TODO report during the monthly tech retrospective
4. **Quarterly**: Comprehensive review of all TODOs to evaluate progress and prioritize resolution

## Review Process

### 1. Generate TODO Report

Before each review, generate an up-to-date TODO report:

```bash
# Generate a TODO report
./docs/scripts/extract-todos.sh --output ./docs/reports/todo-status.md
```

### 2. Categorize TODOs

During the review, categorize TODOs into action groups:

- **Fix Now**: High-priority TODOs (P0/P1) that should be addressed immediately
- **Next Sprint**: Medium-priority TODOs (P2) that should be scheduled for the next sprint
- **Backlog**: Lower-priority TODOs (P3) that should be added to the backlog
- **Delegate**: TODOs that should be assigned to a specific team member
- **Obsolete**: TODOs that are no longer relevant and can be removed

### 3. Create GitHub Issues

For all TODOs in the "Fix Now" and "Next Sprint" categories, ensure GitHub issues exist:

```bash
# Verify GitHub issues exist for high-priority TODOs
./docs/scripts/check-todo-format.sh --github-issues --high-priority
```

If an issue doesn't exist, create one using:

```bash
# Create a GitHub issue for a TODO
./docs/scripts/todo-to-issue.sh "path/to/file.java:line_number"
```

### 4. Update TODO Priorities

Based on the review, update TODO priorities as needed:

```bash
# Fix a TODO's priority
./docs/scripts/standardize-todos.sh --fix --directory path/to/specific/file.java
```

### 5. Track Progress

At the end of each review cycle, generate metrics to track progress:

- Total number of TODOs by priority
- TODOs resolved since last review
- Average age of TODOs
- TODOs by category
- TODOs by module/component

## Integration with Development Workflow

### Pre-commit Checks

Before committing code, run the TODO format check to ensure new TODOs follow the standard format:

```bash
# Check TODO format
./docs/scripts/check-todo-format.sh
```

### CI/CD Integration

The CI pipeline includes a check for TODO format compliance:

1. All TODOs must follow the standard format
2. P0/P1 TODOs must have associated GitHub issues

### Sprint Planning

During sprint planning:

1. Review the "Fix Now" and "Next Sprint" TODO categories
2. Allocate time for addressing high-priority TODOs
3. Assign TODOs to team members

## Tools

The following tools support the TODO review process:

- **extract-todos.sh**: Extracts TODOs from the codebase and generates a report
- **standardize-todos.sh**: Standardizes TODO format
- **check-todo-format.sh**: Validates TODO format compliance
- **todo-to-issue.sh**: Creates GitHub issues from TODOs

## Reporting

A TODO status dashboard is available in the project documentation, showing:

1. TODO distribution by priority
2. TODO trend over time
3. Recent TODOs added/resolved
4. Oldest open TODOs

## Best Practices

1. **Keep TODOs specific**: Each TODO should describe a specific task
2. **Include context**: Provide enough information for another developer to understand the issue
3. **Set appropriate priorities**: Use P0 only for critical issues, P1 for important issues
4. **Link to issues**: All P0/P1 TODOs should link to GitHub issues
5. **Resolve promptly**: Don't let TODOs linger; either fix them or lower their priority
6. **Remove obsolete TODOs**: If a TODO is no longer relevant, remove it
7. **Don't use TODOs for features**: Use the issue tracker for feature requests

## Example Review Session

Here's an example of a typical TODO review session:

1. Generate the TODO report
2. Filter TODOs by priority
3. For each P0/P1 TODO:
   - Verify it has a GitHub issue
   - Assign an owner
   - Set a target resolution date
4. For each P2 TODO:
   - Evaluate if it should be elevated to P1
   - Determine if it should be addressed in the next sprint
5. For each P3 TODO:
   - Determine if it's still relevant
   - Consider if it should be converted to a GitHub issue for the backlog
6. Generate summary statistics
7. Update the TODO status dashboard