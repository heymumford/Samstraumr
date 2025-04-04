# Markdown File Renaming Plan

This document outlines the plan for standardizing the naming of Markdown (.md) files in the Samstraumr project.

## Naming Conventions

According to our file organization standards:

1. **README.md** files should remain in UPPER_CASE
2. **CLAUDE.md** should remain in UPPER_CASE as a special project file
3. Other documentation files should follow PascalCase naming convention

## Files to Rename

### Root Level
- CLAUDE.md - (keep as is)
- README.md - (keep as is)

### docs/ directory

#### All-caps files to rename to PascalCase
- DOCUMENTATION_STANDARDS.md → DocumentationStandards.md
- JAVA_NAMING_STANDARDS.md → JavaNamingStandards.md
- LOGGING_STANDARDS.md → LoggingStandards.md
- FOLDER_STRUCTURE.md → FolderStructure.md

#### Documentation files needing consistency
- ATL-BTL-Strategy.md → ATLBTLStrategy.md (ATL and BTL are acronyms that should be uppercase)
- BundleToCompositeRefactoring.md → (Already PascalCase, keep)
- CompositesAndMachines.md → (Already PascalCase, keep)
- CoreConcepts.md → (Already PascalCase, keep)
- FAQ.md → (Already PascalCase, keep)
- Glossary.md → (Already PascalCase, keep)
- GettingStarted.md → (Already PascalCase, keep)
- Migration.md → (Already PascalCase, keep)
- StateManagement.md → (Already PascalCase, keep)
- SystemsTheoryFoundation.md → (Already PascalCase, keep)
- Testing-Strategy-TBD.md → TestingStrategyTbd.md
- Testing.md → (Already PascalCase, keep)
- TestingStrategy.md → (Already PascalCase, keep)
- TubePatterns.md → (Already PascalCase, keep)

#### docs/compatibility/
- COMPATIBILITY_FIXES.md → CompatibilityFixes.md
- COMPATIBILITY_REPORT.md → CompatibilityReport.md

#### docs/planning/
- DOCUMENTATION_PLAN.md → DocumentationPlan.md
- FILE_REORGANIZATION_PROGRESS.md → FileReorganizationProgress.md
- FOLDER_REFACTORING_PLAN.md → FolderRefactoringPlan.md
- README-IMPLEMENTATION.md → ReadmeImplementation.md
- README-NEW-DRAFT.md → ReadmeNewDraft.md
- SCRIPT_REORGANIZATION.md → ScriptReorganization.md

#### docs/testing/
- TEST_STANDARDIZATION.md → TestStandardization.md

#### docs/contribution/
- WORKFLOW_ANALYSIS.md → WorkflowAnalysis.md

### util/ directory
- VERSION_SCRIPTS.md → VersionScripts.md (in keeping with PascalCase convention)
- README.md - (keep as is)

## Implementation Steps

1. Create backup of important files
2. Systematically rename files according to the plan
3. Update references to these files in other documents
4. Test links to ensure proper navigation
5. Commit changes with a clear commit message