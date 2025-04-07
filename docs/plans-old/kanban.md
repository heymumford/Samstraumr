<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
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

### ✅ DONE

- [x] **Implement unified quality checks**
  - Consolidated quality check scripts ✅
  - Created unified command interface ✅
  - Implemented quality profiles ✅
  - Added comprehensive HTML reporting ✅
  - Integrated with s8r CLI ✅
  - Created quality metrics documentation ✅
  - **Priority**: P1
  - **Category**: Build/CI
  - **Completed**: 2025-04-06
  - **Related**: [active-unified-quality-checks.md](./active-unified-quality-checks.md), [quality-checks.md](../reference/quality-checks.md)

- [x] **Documentation standardization**
  - Converted all documentation files to kebab-case ✅
  - Updated header formatting to follow standards ✅
  - Fixed cross-references between files ✅
  - Simplified directory structure from 20 to 9 directories ✅
  - Created READMEs for new directory structure ✅
  - Created migration script for remaining files ✅
  - Implemented automatic cross-reference checker ✅
  - Created documentation standards verification tool ✅
  - **Priority**: P1
  - **Category**: Documentation
  - **Completed**: 2025-04-06
  - **Related**: [active-documentation-standardization.md](./active-documentation-standardization.md)

- [x] **Complete unified testing approach implementation**
  - Fixed ATL test discovery issues ✅
  - Added new ATL test mapping tool ✅
  - Implemented proper ATL test runner ✅
  - Created ATL test discovery documentation ✅
  - Updated s8r-test script ✅
  - Fixed run-atl-tests.sh script ✅
  - **Priority**: P0
  - **Category**: Testing
  - **Completed**: 2025-04-06
  - **Related**: [../testing/atl-test-discovery.md](../testing/atl-test-discovery.md)

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
