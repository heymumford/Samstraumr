<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
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

### Directory Structure

```
docs/
└── planning/
    ├── kanban.md               # Main Kanban board
    ├── work-tracking-guide.md  # This guide
    ├── active/                 # Active plans
    │   ├── plan-1.md
    │   └── plan-2.md
    ├── completed/              # Completed plans
    │   ├── plan-3.md
    │   └── plan-4.md
    └── archived/               # Archived plans
        ├── plan-5.md
        └── plan-6.md
```

## Kanban Board

The Kanban board is the central location for tracking all work. It is maintained in `docs/planning/kanban.md`.

### Columns

1. **To Do**: Tasks that are planned but not yet started
2. **In Progress**: Tasks that are currently being worked on
3. **Done**: Completed tasks

### Task Structure

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

Planning documents provide detailed information about significant work items. They are stored in the `docs/planning/` directory.

### Plan Categories

1. **Active Plans**: Current work in `docs/planning/active/`
2. **Completed Plans**: Finished work in `docs/planning/completed/`
3. **Archived Plans**: Historical plans in `docs/planning/archived/`

### Plan Structure

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
// TODO [Priority] (#Issue): Description
```

Example:

```java
// TODO [P2] (#123): Implement error handling for network failures
```

### Priority Levels

- **P0**: Critical - Must be fixed immediately
- **P1**: High - Should be fixed soon
- **P2**: Medium - Fix when time permits
- **P3**: Low - Nice to have

### Categories (Optional)

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

### Starting New Work

1. Add the task to the **To Do** column of the Kanban board
2. Create a planning document in `docs/planning/active/` if needed
3. Create a GitHub issue if user-facing

### During Work

1. Move the task to **In Progress** on the Kanban board
2. Update the planning document with progress (check off completed items)
3. Add TODOs in code for future improvements

### Completing Work

1. Move the task to **Done** on the Kanban board
2. Move the planning document to `docs/planning/completed/`
3. Close the GitHub issue if applicable

### Archiving Work

1. After a reasonable time, move completed planning documents to `docs/planning/archived/`
2. Prefix archived files with `archived-` if they have special historical value

## Additional Guidelines

1. **Regular Reviews**: Review the Kanban board weekly to ensure it's up-to-date
2. **Prioritization**: Re-evaluate priorities regularly
3. **Cleanup**: Archive completed tasks monthly to keep the board manageable
4. **Linking**: Always link between related documents to maintain traceability
