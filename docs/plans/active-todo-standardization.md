<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# TODO Standardization Plan

This document outlines a plan to standardize the format and tracking of TODOs across the Samstraumr codebase.

## Metadata

- **Category**: Code Quality
- **Priority**: P2
- **Status**: Planned
- **Owner**: Development Team
- **Due**: 2025-04-15
- **Issue**: N/A

## Overview

Currently, TODOs and FIXMEs are scattered throughout the codebase in various formats, making it difficult to track and prioritize technical debt. This plan aims to establish a consistent format for TODOs and create a system to track them centrally.

## Current Issues

1. Inconsistent TODO formats in code comments
2. No connection between code TODOs and GitHub issues
3. No way to prioritize or categorize TODOs
4. TODOs often get forgotten and accumulate as technical debt
5. No regular process to review and address TODOs

## Standardization Goals

1. **Consistent format**: Establish a standard format for all TODOs in code comments
2. **Issue linking**: Connect TODOs to GitHub issues where appropriate
3. **Prioritization**: Add priority levels to TODOs
4. **Categorization**: Add categories to TODOs (bug, feature, refactor, etc.)
5. **Tracking**: Create a system to extract and track TODOs

## Implementation Plan

### Phase 1: Define Standards

1. ✅ Define standard format for TODOs:

   ```java
   // TODO [Priority] (Category) (#Issue): Description
   ```

   Example:

   ```java
   // TODO [P2] (FEAT) (#123): Implement error handling for network failures
   ```
2. ✅ Define priority levels:
   - P0: Critical (must be fixed immediately)
   - P1: High (should be fixed soon)
   - P2: Medium (fix when time permits)
   - P3: Low (nice to have)
3. ✅ Define categories:
   - BUG: Bug fix
   - FEAT: New feature
   - REFACTOR: Code refactoring
   - PERF: Performance improvement
   - DOC: Documentation
   - TEST: Testing improvement
   - INFRA: Infrastructure
   - SECURITY: Security issues
   - TASK: General tasks

### Phase 2: Create Tracking Tools

1. ✅ Create a script to extract TODOs from code:
   - Extract TODO comments with pattern matching
   - Parse priority, issue, and description
   - Generate a Markdown report
2. ✅ Integrate with GitHub:
   - Create issues for high-priority TODOs
   - Add labels based on categories
   - Link TODOs to existing issues
3. ✅ Add CI check for TODO format:
   - Verify TODOs follow the standard format
   - Require issue numbers for P0/P1 TODOs

### Phase 3: Standardize Existing TODOs

1. ⬜ Scan the codebase for existing TODOs
2. ⬜ Convert high-priority TODOs to the new format
3. ⬜ Create GitHub issues for critical TODOs
4. ⬜ Decide which TODOs to fix immediately vs. track

### Phase 4: Documentation and Training

1. ✅ Create documentation for TODO standards
2. ✅ Update contribution guidelines
3. ⬜ Create a process for regular TODO review

## Implementation Schedule

| Phase |            Task            | Target Start | Target Completion |     Status     |
|-------|----------------------------|--------------|-------------------|----------------|
| 1     | Define Standards           | 2025-04-10   | 2025-04-11        | ✅ Completed   |
| 2     | Create Tracking Tools      | 2025-04-11   | 2025-04-13        | ✅ Completed   |
| 3     | Standardize Existing TODOs | 2025-04-13   | 2025-04-14        | 🔄 In Progress |
| 4     | Documentation and Training | 2025-04-14   | 2025-04-15        | 🔄 In Progress |

## Success Criteria

1. **Consistency**: >90% of TODOs follow the new format
2. **Traceability**: All P0/P1 TODOs have corresponding GitHub issues
3. **Visibility**: TODO summary report available in CI pipeline
4. **Process**: Regular TODO review process established

## References

- [Google Engineering Practices Documentation](https://google.github.io/eng-practices/)
- [TODO Comments - The Good, the Bad and the Ugly](https://medium.com/@hardikshah_18328/todo-comment-the-good-the-bad-and-the-ugly-faa5e67abe6a)
- [extract-todos.sh](../tools/extract-todos.sh)
