<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Documentation Generation Plan

This document outlines the plan to implement an automated documentation generation system for the Samstraumr project.

## Metadata

- **Category**: Documentation
- **Priority**: P2
- **Status**: Planned
- **Owner**: Development Team
- **Due**: 2025-04-30
- **Issue**: N/A

## Overview

Currently, the README and other documentation files are maintained manually, which makes it difficult to keep them updated with the latest project information. This plan aims to automate documentation generation to ensure that documentation stays current and consistent.

## Current Issues

1. README needs manual updates when project changes
2. No automatic changelog generation from commits
3. No integration between Javadoc and Markdown documentation
4. Inconsistent documentation formatting
5. No automatic API documentation

## Goals

1. **Automated README**: Automatically update README with latest version and features
2. **Changelog Generation**: Generate changelog from commit history
3. **API Documentation**: Automatically generate API documentation
4. **Documentation Testing**: Verify documentation links and content
5. **Consistent Formatting**: Ensure consistent documentation formatting

## Implementation Plan

### Phase 1: Research and Planning

1. ⬜ Research documentation generation tools:
   - MkDocs
   - Docusaurus
   - Docmosis (currently used)
   - GitHub Pages
   - JavaDoc
2. ⬜ Evaluate integration options:
   - Maven plugins
   - CI/CD integration
   - Local generation scripts
3. ⬜ Define documentation requirements:
   - API documentation
   - User guides
   - Development guides
   - Reference documentation

### Phase 2: Changelog Generation

1. ✅ Create changelog generation script:
   - Parse conventional commits
   - Categorize changes (Features, Bug Fixes, etc.)
   - Format as Markdown
2. ✅ Integrate with version bumping:
   - Auto-update changelog on version bump
   - Link to GitHub issues/PRs
3. ⬜ Add CI support:
   - Generate changelog preview for PRs
   - Update docs/reference/release/changelog.md on release

### Phase 3: README Automation

1. ✅ Create README template with placeholders:
   - Version information
   - Feature list
   - Quick start guide
   - Documentation links
2. ✅ Implement README generation script:
   - Replace placeholders with actual data
   - Update badges automatically
   - Maintain custom sections
3. ✅ Add integration with s8r CLI:
   - `s8r docs update-readme`
   - Options for different sections

### Phase 4: API Documentation

1. ✅ Configure JavaDoc generation:
   - Custom template
   - Package organization
   - Link to Markdown docs
2. ✅ Add Markdown generation for Java code:
   - Generate Markdown from Java classes
   - Link to GitHub source
3. ✅ Create unified documentation site:
   - Combine JavaDoc and Markdown
   - Search functionality
   - Version switching

### Phase 5: Integration and Automation

1. ⬜ Create documentation CI job:
   - Generate documentation on commit
   - Test links and formatting
   - Deploy to GitHub Pages
2. ⬜ Implement pre-release checks:
   - Verify documentation completeness
   - Check for broken links
   - Validate examples

## Implementation Schedule

| Phase |            Task            | Target Start | Target Completion |    Status     |
|-------|----------------------------|--------------|-------------------|---------------|
| 1     | Research and Planning      | 2025-04-20   | 2025-04-22        | ✅ Completed   |
| 2     | Changelog Generation       | 2025-04-22   | 2025-04-24        | ✅ Completed   |
| 3     | README Automation          | 2025-04-24   | 2025-04-26        | ✅ Completed   |
| 4     | API Documentation          | 2025-04-26   | 2025-04-28        | ✅ Completed   |
| 5     | Integration and Automation | 2025-04-28   | 2025-04-30        | ⬜ Not Started |

## Success Criteria

1. **Automation**: Documentation updates automatically with code changes
2. **Completeness**: All public APIs are documented
3. **Consistency**: Documentation follows consistent formatting
4. **Usability**: Documentation is easy to navigate and search
5. **Maintainability**: Documentation system is easy to maintain

## References

- [Conventional Commits](https://www.conventionalcommits.org/)
- [JavaDoc Documentation](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html)
- [MkDocs Documentation](https://www.mkdocs.org/)
- [Docusaurus Documentation](https://docusaurus.io/)
