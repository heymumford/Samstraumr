# K a N B a N

This document tracks the current development tasks in a simple Kanban-style board. Each task is assigned to one of three columns: To Do, In Progress, or Done.

## How to Use

1. Add new tasks to the **To Do** section
2. Move tasks to **In Progress** when work begins
3. Move completed tasks to **Done**
4. Add task details like:
   - Who's working on it
   - Estimated completion
   - Related files or issues

## Current Board

### 📋 to do

- [ ] **Complete unified testing approach implementation**
  - Fix ATL test discovery issues
  - Add tests for unified test runner
  - Update documentation for test commands

- [ ] **Implement unified quality checks**
  - Consolidate quality check scripts
  - Create unified reporting

- [ ] **Improve documentation generation**
  - Add automatic README updates
  - Create changelog generation

### ✅ done

- [x] **Document format standardization**
  - Converted all .docx and .rtf files to Markdown format
  - Created script for future conversions
  - Standardized filename conventions across documentation
  - Created centralized standardization script for all doc directories
  - Completed: 2025-04-04
  - Related: docs/scripts/convert-to-markdown.sh, docs/scripts/standardize-filenames.sh

- [x] **Planning infrastructure improvements**
  - Created Kanban-style planning board
  - Implemented task tracking for To Do, In Progress, and Done
  - Created task templates for consistency
  - Standardized document naming conventions
  - Created standardization script
  - Completed: 2025-04-04
  - Related: docs/planning/KANBAN.md, docs/scripts/standardize-planning-filenames.sh

- [x] **Git workflow improvements**
  - Added git hooks for code quality
  - Implemented hook to prevent AI signatures in commit messages
  - Created documentation and s8r CLI command
  - Completed: 2025-04-04
  - Related: docs/contribution/GitCommits.md

- [x] **Script simplification**
  - Created unified test runner
  - Standardized argument parsing
  - Added test status reporting
  - Completed: 2025-04-04
  - Related: docs/planning/test-refactoring-summary.md

- [x] **Version management refactoring**
  - Modularized version management scripts
  - Added semantic versioning support
  - Implemented version synchronization
  - Completed: 2025-04-03
  - Related: docs/planning/version-refactoring-summary.md

- [x] **Folder structure reorganization**
  - Flattened package hierarchy
  - Standardized resource locations
  - Completed: 2025-04-02
  - Related: docs/planning/refactoring-summary.md

## Task Templates

### New task template
```markdown
- [ ] **Task Name**
  - Brief description of the task
  - Additional details
  - Owner: @username
  - Estimated completion: YYYY-MM-DD
  - Related: path/to/related/files
```

### Completed task template
```markdown
- [x] **Task Name**
  - Brief description of what was done
  - Any notable achievements
  - Completed: YYYY-MM-DD
  - Related: path/to/related/files
```

## Workflow Integration

This Kanban board is designed to work alongside other project management tools:

1. Git issues can be referenced with #issue-number
2. PR numbers can be referenced with !PR-number
3. Team members can be tagged with @username
4. Links to external resources can be included

Updates to this board should be committed with a message like:
