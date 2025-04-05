<!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# Kanban

This document tracks the current development tasks in a Kanban-style board. Each task is assigned to one of three columns: To Do, In Progress, or Done.

## How to Use

1. Add new tasks to the **To Do** section
2. Move tasks to **In Progress** when work begins
3. Move completed tasks to **Done**
4. Add task details like:
   - Who's working on it
   - Priority level (P0-P3)
   - Category (Feature, Bug, Refactor, etc.)
   - Estimated completion date
   - Related files, issues, or PRs

## Current Board

### 📋 TO DO

- [ ] **Implement unified quality checks**
  - Consolidate quality check scripts
  - Create unified reporting
  - **Priority**: P1
  - **Category**: Build/CI
  - **Plan**: [active-unified-quality-checks.md](./active-unified-quality-checks.md)
  - **Estimated completion**: 2025-04-20
- [ ] **Improve documentation generation**
  - Add automatic README updates
  - Create changelog generation
  - **Priority**: P2
  - **Category**: Documentation
  - **Plan**: [active-documentation-generation.md](./active-documentation-generation.md)
  - **Estimated completion**: 2025-04-30
- [ ] **Standardize TODOs and FIXMEs**
  - Create consistent format for code TODOs
  - Link TODOs to GitHub issues
  - Create tracking system
  - **Priority**: P2
  - **Category**: Code Quality
  - **Plan**: [active-todo-standardization.md](./active-todo-standardization.md)
  - **Estimated completion**: 2025-04-15

### 🔄 IN PROGRESS

- [ ] **Documentation standardization**
  - Convert all documentation files to kebab-case ✅
  - Update header formatting to follow standards ✅
  - Fix cross-references between files ✅
  - Simplify directory structure ✅
  - Create READMEs for new directory structure ✅
  - Create migration script for remaining files ✅
  - Finalize automatic cross-reference updates
  - **Priority**: P1
  - **Category**: Documentation
  - **Owner**: Claude Code
  - **Plan**: [active-documentation-standardization.md](./active-documentation-standardization.md)
  - **Estimated completion**: 2025-04-06
- [ ] **Complete unified testing approach implementation**
  - Fix ATL test discovery issues
  - Add tests for unified test runner
  - Update documentation for test commands
  - **Priority**: P0
  - **Category**: Testing
  - **Owner**: @dev-team
  - **Estimated completion**: 2025-04-08

### ✅ DONE

- [x] **Documentation structure simplification**
  - Reduced directory structure from 20 to 9 folders
  - Implemented file naming conventions with prefixes
  - Updated cross-references to match new structure
  - **Priority**: P2
  - **Category**: Documentation
  - **Completed**: 2025-04-04
  - **Related**: [work-tracking-guide.md](./work-tracking-guide.md)
- [x] **Planning infrastructure improvements**
  - Created Kanban-style planning board
  - Implemented task tracking for To Do, In Progress, and Done
  - Created task templates for consistency
  - Standardized document naming conventions
  - Created standardization script
  - **Priority**: P2
  - **Category**: Project Management
  - **Completed**: 2025-04-04
  - **Related**: [kanban.md](./kanban.md)
- [x] **Git workflow improvements**
  - Added git hooks for code quality
  - Implemented hook to prevent AI signatures in commit messages
  - Created documentation and s8r CLI command
  - **Priority**: P1
  - **Category**: DevOps
  - **Completed**: 2025-04-04
  - **Related**: [../contrib/git-workflow.md](../contrib/git-workflow.md)
- [x] **Script simplification**
  - Created unified test runner
  - Standardized argument parsing
  - Added test status reporting
  - **Priority**: P1
  - **Category**: DevOps
  - **Completed**: 2025-04-04
  - **Related**: [complete-test-refactoring-summary.md](./complete-test-refactoring-summary.md)
- [x] **Version management refactoring**
  - Modularized version management scripts
  - Added semantic versioning support
  - Implemented version synchronization
  - **Priority**: P1
  - **Category**: Build
  - **Completed**: 2025-04-03
  - **Related**: [complete-version-refactoring-summary.md](./complete-version-refactoring-summary.md)
- [x] **Folder structure reorganization**
  - Flattened package hierarchy
  - Standardized resource locations
  - **Priority**: P2
  - **Category**: Refactor
  - **Completed**: 2025-04-02
  - **Related**: [complete-file-reorganization-progress.md](./complete-file-reorganization-progress.md)

## Task Templates

### New task template

```markdown
- [ ] **Task Name**
  - Brief description of the task
  - Additional details
  - **Priority**: P0-P3
  - **Category**: Feature/Bug/Refactor/Documentation/Testing/DevOps/Build
  - **Owner**: @username
  - **Plan**: [Plan Document](./path/to/plan.md)
  - **Issue**: #issue-number (if applicable)
  - **Estimated completion**: YYYY-MM-DD
```

### Completed task template

```markdown
- [x] **Task Name**
  - Brief description of what was done
  - Any notable achievements
  - **Priority**: P0-P3
  - **Category**: Feature/Bug/Refactor/Documentation/Testing/DevOps/Build
  - **Completed**: YYYY-MM-DD
  - **Related**: [document](path/to/document.md), #issue-number, !PR-number
```

## Priority Levels

- **P0**: Critical - Must be fixed/implemented immediately
- **P1**: High - Should be addressed in the current sprint
- **P2**: Medium - Should be addressed in the near future
- **P3**: Low - Nice to have, but not urgent

## Categories

- **Feature**: New functionality
- **Bug**: Bug fix
- **Refactor**: Code improvement without changing functionality
- **Documentation**: Documentation updates or improvements
- **Testing**: Test improvements or additions
- **DevOps**: Build, deployment, or infrastructure improvements
- **Build**: Build system changes
- **Security**: Security-related changes

## Workflow Integration

This Kanban board is designed to work alongside other project management tools:

1. Git issues can be referenced with #issue-number
2. PR numbers can be referenced with !PR-number
3. Team members can be tagged with @username
4. Links to external resources can be included

Updates to this board should be committed with a message like:
"docs: update kanban.md with current status"
