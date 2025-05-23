<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Work Tracking Guide

This document describes the standardized system for tracking work in the Samstraumr project.

## Overview

Samstraumr uses a structured approach to track all work, from high-level planning to specific code TODOs. This ensures visibility, accountability, and clear prioritization of all tasks.

## Work Tracking Structure

The work tracking system is organized into the following components:

1. **Kanban Board**: Central tracking of all active tasks
2. **Planning Documents**: Detailed plans for significant work items
3. **Code TODOs**: Standardized format for tracking technical debt in code
4. **GitHub Issues**: External tracking for user-facing issues and features

### Directory structure

```
docs/
└── plans/
    ├── kanban.md                  # Main Kanban board
    ├── work-tracking-guide.md     # This guide
    ├── active-*.md                # Active plans with prefix
    ├── complete-*.md              # Completed plans with prefix
    └── archived-*.md              # Archived plans with prefix
```

## Kanban Board

The Kanban board is the central location for tracking all work. It is maintained in `docs/plans/kanban.md`.

### Columns

1. **To Do**: Tasks that are planned but not yet started
2. **In Progress**: Tasks that are currently being worked on
3. **Done**: Completed tasks

### Task structure

Each task on the Kanban board should include:

- **Title**: Clear, concise description of the task
- **Description**: Bullet points with task details
- **Priority**: P0-P3 priority level
- **Category**: Type of work (Feature, Bug, Refactor, etc.)
- **Owner**: Person responsible for the task
- **Plan**: Link to planning document (if applicable)
- **Issue**: Link to GitHub issue (if applicable)
- **Estimated completion**: Target date
- **Related**: Links to related documents or issues

See the templates in the Kanban board for the exact format.

## Planning Documents

Planning documents provide detailed information about significant work items. They use a prefix-based naming system in the `docs/plans/` directory.

### Plan types

1. **Active Plans**: Current work with `active-` prefix (e.g., `active-documentation-standardization.md`)
2. **Completed Plans**: Finished work with `complete-` prefix (e.g., `complete-version-refactoring.md`)
3. **Archived Plans**: Historical plans with `archived-` prefix (e.g., `archived-temp-reorg-plan.md`)

### Plan structure

Each planning document should include:

- **Title**: Clear description of the plan
- **Metadata**: Category, priority, status, owner, and due date
- **Overview**: Summary of the plan
- **Implementation Plan**: Detailed steps with checkboxes
- **Schedule**: Timeline for completion
- **Success Criteria**: Measurable outcomes

## Code TODOs

Technical debt and future improvements in code should be tracked using a standardized TODO format:

```java
// TODO [Priority] (Category) (#Issue): Description
```

Example:

```java
// TODO [P2] (FEAT) (#123): Implement error handling for network failures
```

### Priority levels

- **P0**: Critical - Must be fixed immediately
- **P1**: High - Should be fixed soon
- **P2**: Medium - Fix when time permits
- **P3**: Low - Nice to have

### Categories (optional)

Categories can be added in parentheses:

```java
// TODO [P1] (BUG) (#123): Fix null pointer exception
```

Available categories:
- BUG: Bug fix
- FEAT: New feature
- REFACTOR: Code improvement
- PERF: Performance improvement
- DOC: Documentation
- TEST: Testing improvement

## GitHub Issues

For user-facing issues and features, GitHub Issues should be used. These should be referenced in the Kanban board and planning documents where appropriate.

## Workflow

### Starting new work

1. Add the task to the **To Do** column of the Kanban board
2. Create a planning document with `active-` prefix in `docs/plans/` if needed
3. Create a GitHub issue if user-facing

### During work

1. Move the task to **In Progress** on the Kanban board
2. Update the planning document with progress (check off completed items)
3. Add TODOs in code for future improvements

### Completing work

1. Move the task to **Done** on the Kanban board
2. Rename the planning document with `complete-` prefix
3. Close the GitHub issue if applicable

### Archiving work

1. After a reasonable time, rename completed planning documents with `archived-` prefix
2. Update links in the Kanban board and other documents

## Todo Report Generation

Use the `extract-todos.sh` script to generate a report of all TODOs in the codebase:

```bash
./docs/tools/extract-todos.sh --output docs/plans/todo-report.md
```

This generates a comprehensive report categorized by priority level.

## Additional Guidelines

1. **Regular Reviews**: Review the Kanban board weekly to ensure it's up-to-date
2. **Prioritization**: Re-evaluate priorities regularly
3. **Cleanup**: Archive completed tasks monthly to keep the board manageable
4. **Linking**: Always link between related documents to maintain traceability
