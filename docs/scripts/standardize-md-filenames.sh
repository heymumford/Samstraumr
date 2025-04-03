#!/bin/bash
# Script to standardize Markdown file naming conventions in the project
#
# Naming conventions:
# - README.md files remain in UPPER_CASE
# - CLAUDE.md remains in UPPER_CASE 
# - Other documentation files use PascalCase
# - Acronyms like FAQ, TBD remain in UPPERCASE

# Set colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

# Function to print a header
print_header() {
  echo -e "\n${YELLOW}==== $1 ====${NC}"
}

# Function to print success message
print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error message
print_error() {
  echo -e "${RED}✗ $1${NC}"
}

# Store the project root directory
PROJECT_ROOT=$(pwd)
cd "$PROJECT_ROOT"

print_header "Standardizing Markdown Filenames"
echo "Following naming conventions:"
echo "- README.md files remain in UPPER_CASE"
echo "- CLAUDE.md remains in UPPER_CASE"
echo "- Acronyms (like FAQ.md, TBD.md) remain in UPPERCASE"
echo "- Other documentation files use PascalCase"
echo ""

# Rename files in the planning directory
print_header "Planning Directory"
if [ -f "docs/planning/SCRIPT_REORGANIZATION.md" ]; then
  mv -v docs/planning/SCRIPT_REORGANIZATION.md docs/planning/ScriptReorganization.md
fi
if [ -f "docs/planning/README-IMPLEMENTATION.md" ]; then
  mv -v docs/planning/README-IMPLEMENTATION.md docs/planning/ReadmeImplementation.md
fi
if [ -f "docs/planning/FILE_REORGANIZATION_PROGRESS.md" ]; then
  mv -v docs/planning/FILE_REORGANIZATION_PROGRESS.md docs/planning/FileReorganizationProgress.md
fi
if [ -f "docs/planning/FOLDER_REFACTORING_PLAN.md" ]; then
  mv -v docs/planning/FOLDER_REFACTORING_PLAN.md docs/planning/FolderRefactoringPlan.md
fi
if [ -f "docs/planning/README-NEW-DRAFT.md" ]; then
  mv -v docs/planning/README-NEW-DRAFT.md docs/planning/ReadmeNewDraft.md
fi
if [ -f "docs/planning/DOCUMENTATION_PLAN.md" ]; then
  mv -v docs/planning/DOCUMENTATION_PLAN.md docs/planning/DocumentationPlan.md
fi
if [ -f "docs/planning/tmp-reorg-plan.md" ]; then
  mv -v docs/planning/tmp-reorg-plan.md docs/planning/TempReorgPlan.md
fi

# Rename files in the testing directory
print_header "Testing Directory"
if [ -f "docs/testing/TEST_STANDARDIZATION.md" ]; then
  mv -v docs/testing/TEST_STANDARDIZATION.md docs/testing/TestStandardization.md
fi
if [ -f "docs/testing/testing-annotations.md" ]; then
  mv -v docs/testing/testing-annotations.md docs/testing/TestingAnnotations.md
fi
if [ -f "docs/testing/atl-btl-strategy.md" ]; then
  mv -v docs/testing/atl-btl-strategy.md docs/testing/AtlBtlStrategy.md
fi
if [ -f "docs/testing/test-strategy.md" ]; then
  mv -v docs/testing/test-strategy.md docs/testing/TestStrategy.md
fi
if [ -f "docs/testing/bdd-with-cucumber.md" ]; then
  mv -v docs/testing/bdd-with-cucumber.md docs/testing/BddWithCucumber.md
fi

# Rename files in the proposals directory
print_header "Proposals Directory"
if [ -f "docs/proposals/samstraumr-testing-strategy.md" ]; then
  mv -v docs/proposals/samstraumr-testing-strategy.md docs/proposals/SamstraumrTestingStrategy.md
fi
if [ -f "docs/proposals/llm-context-composite-tube-proposal.md" ]; then
  mv -v docs/proposals/llm-context-composite-tube-proposal.md docs/proposals/LlmContextCompositeTubeProposal.md
fi

# Rename files in the contribution directory
print_header "Contribution Directory"
if [ -f "docs/contribution/code-standards.md" ]; then
  mv -v docs/contribution/code-standards.md docs/contribution/CodeStandards.md
fi
if [ -f "docs/contribution/build-report-guide.md" ]; then
  mv -v docs/contribution/build-report-guide.md docs/contribution/BuildReportGuide.md
fi
if [ -f "docs/contribution/configuration-standards.md" ]; then
  mv -v docs/contribution/configuration-standards.md docs/contribution/ConfigurationStandards.md
fi
if [ -f "docs/contribution/WORKFLOW_ANALYSIS.md" ]; then
  mv -v docs/contribution/WORKFLOW_ANALYSIS.md docs/contribution/WorkflowAnalysis.md
fi
if [ -f "docs/contribution/quality-checks.md" ]; then
  mv -v docs/contribution/quality-checks.md docs/contribution/QualityChecks.md
fi
if [ -f "docs/contribution/ci-cd-guide.md" ]; then
  mv -v docs/contribution/ci-cd-guide.md docs/contribution/CICDGuide.md
fi
if [ -f "docs/contribution/contributing.md" ]; then
  mv -v docs/contribution/contributing.md docs/contribution/Contributing.md
fi

# Rename files in the concepts directory
print_header "Concepts Directory"
if [ -f "docs/concepts/state-management.md" ]; then
  mv -v docs/concepts/state-management.md docs/concepts/StateManagement.md
fi
if [ -f "docs/concepts/composites-and-machines.md" ]; then
  mv -v docs/concepts/composites-and-machines.md docs/concepts/CompositesAndMachines.md
fi
if [ -f "docs/concepts/identity-addressing.md" ]; then
  mv -v docs/concepts/identity-addressing.md docs/concepts/IdentityAddressing.md
fi
if [ -f "docs/concepts/systems-theory-foundation.md" ]; then
  mv -v docs/concepts/systems-theory-foundation.md docs/concepts/SystemsTheoryFoundation.md
fi
if [ -f "docs/concepts/core-concepts.md" ]; then
  mv -v docs/concepts/core-concepts.md docs/concepts/CoreConcepts.md
fi

# Rename files in the research directory
print_header "Research Directory"
if [ -f "docs/research/llm-context-proposal.md" ]; then
  mv -v docs/research/llm-context-proposal.md docs/research/LlmContextProposal.md
fi

# Rename files in the reference directory
print_header "Reference Directory"
if [ -f "docs/reference/configuration-reference.md" ]; then
  mv -v docs/reference/configuration-reference.md docs/reference/ConfigurationReference.md
fi
if [ -f "docs/reference/quality-changes.md" ]; then
  mv -v docs/reference/quality-changes.md docs/reference/QualityChanges.md
fi
if [ -f "docs/reference/folder-structure.md" ]; then
  mv -v docs/reference/folder-structure.md docs/reference/FolderStructure.md
fi
if [ -f "docs/reference/glossary.md" ]; then
  mv -v "docs/reference/glossary.md" "docs/reference/Glossary.md"
fi
if [ -f "docs/reference/faq.md" ]; then
  mv -v "docs/reference/faq.md" "docs/reference/FAQ.md"
fi

# Rename files in the reference standards directory
print_header "Reference Standards Directory"
if [ -f "docs/reference/standards/logging-standards.md" ]; then
  mv -v docs/reference/standards/logging-standards.md docs/reference/standards/LoggingStandards.md
fi
if [ -f "docs/reference/standards/documentation-standards.md" ]; then
  mv -v docs/reference/standards/documentation-standards.md docs/reference/standards/DocumentationStandards.md
fi
if [ -f "docs/reference/standards/java-naming-standards.md" ]; then
  mv -v docs/reference/standards/java-naming-standards.md docs/reference/standards/JavaNamingStandards.md
fi
if [ -f "docs/reference/standards/file-organization.md" ]; then
  mv -v docs/reference/standards/file-organization.md docs/reference/standards/FileOrganization.md
fi

# Rename files in the guides directory
print_header "Guides Directory"
if [ -f "docs/guides/migration/bundle-to-composite-refactoring.md" ]; then
  mv -v docs/guides/migration/bundle-to-composite-refactoring.md docs/guides/migration/BundleToCompositeRefactoring.md
fi
if [ -f "docs/guides/tube-patterns.md" ]; then
  mv -v docs/guides/tube-patterns.md docs/guides/TubePatterns.md
fi
if [ -f "docs/guides/composition-strategies.md" ]; then
  mv -v docs/guides/composition-strategies.md docs/guides/CompositionStrategies.md
fi
if [ -f "docs/guides/migration-guide.md" ]; then
  mv -v docs/guides/migration-guide.md docs/guides/MigrationGuide.md
fi
if [ -f "docs/guides/getting-started.md" ]; then
  mv -v docs/guides/getting-started.md docs/guides/GettingStarted.md
fi
if [ -f "docs/guides/prerequisites.md" ]; then
  mv -v "docs/guides/prerequisites.md" "docs/guides/Prerequisites.md"
fi

# Rename files in the compatibility directory
print_header "Compatibility Directory"
if [ -f "docs/compatibility/COMPATIBILITY_REPORT.md" ]; then
  mv -v docs/compatibility/COMPATIBILITY_REPORT.md docs/compatibility/CompatibilityReport.md
fi
if [ -f "docs/compatibility/COMPATIBILITY_FIXES.md" ]; then
  mv -v docs/compatibility/COMPATIBILITY_FIXES.md docs/compatibility/CompatibilityFixes.md
fi

# Rename files in the site markdown directory
print_header "Site Markdown Directory"
if [ -f "docs/site/markdown/build-metrics.md" ]; then
  mv -v docs/site/markdown/build-metrics.md docs/site/markdown/BuildMetrics.md
fi
if [ -f "docs/site/markdown/build-status.md" ]; then
  mv -v docs/site/markdown/build-status.md docs/site/markdown/BuildStatus.md
fi
if [ -f "docs/site/markdown/index.md" ]; then
  mv -v "docs/site/markdown/index.md" "docs/site/markdown/Index.md"
fi

# Rename files in the util directory
print_header "Util Directory"
if [ -f "util/VERSION_SCRIPTS.md" ]; then
  mv -v util/VERSION_SCRIPTS.md util/VersionScripts.md
fi

# Rename files in the util scripts docs directory
print_header "Util Scripts Docs Directory"
if [ -f "util/scripts/docs/build-README.md" ]; then
  mv -v util/scripts/docs/build-README.md util/scripts/docs/BuildReadme.md
fi
if [ -f "util/scripts/docs/maintenance-README.md" ]; then
  mv -v util/scripts/docs/maintenance-README.md util/scripts/docs/MaintenanceReadme.md
fi
if [ -f "util/scripts/docs/quality-README.md" ]; then
  mv -v util/scripts/docs/quality-README.md util/scripts/docs/QualityReadme.md
fi
if [ -f "util/scripts/docs/fix-scripts-README.md" ]; then
  mv -v util/scripts/docs/fix-scripts-README.md util/scripts/docs/FixScriptsReadme.md
fi

# Rename files in the root directory
print_header "Root Directory"
if [ -f "COMPATIBILITY_FIXES.md" ]; then
  mv -v "COMPATIBILITY_FIXES.md" "CompatibilityFixes.md"
fi
if [ -f "COMPATIBILITY_REPORT.md" ]; then
  mv -v "COMPATIBILITY_REPORT.md" "CompatibilityReport.md"
fi
if [ -f "TEST_STANDARDIZATION.md" ]; then
  mv -v "TEST_STANDARDIZATION.md" "TestStandardization.md"
fi

# Rename files in the Samstraumr core resources directory
print_header "Samstraumr Core Resources"
if [ -f "Samstraumr/samstraumr-core/src/main/resources/version-template.md" ]; then
  mv -v "Samstraumr/samstraumr-core/src/main/resources/version-template.md" "Samstraumr/samstraumr-core/src/main/resources/VersionTemplate.md"
fi

# Check if there are any remaining non-PascalCase files that aren't README.md or CLAUDE.md
print_header "Checking for any remaining files that don't follow conventions"

# This improved pattern detects files that:
# 1. Have lowercase first letter (not PascalCase) for the filename (not path)
# 2. Have underscores or hyphens in the filename (not path)
# 3. Are not README.md, CLAUDE.md, or recognized acronyms like FAQ.md, TBD.md, etc.
# 4. Special cases for version-template.md
remaining_files=$(find . -name "*.md" | grep -v "/README\.md$" | grep -v "/CLAUDE\.md$" | 
  grep -v "/PULL_REQUEST_TEMPLATE\.md$" | 
  awk -F/ '{print $NF}' | 
  grep -E '^[a-z]|[-_]' | 
  grep -v '^version-template\.md$' |
  grep -v '^[A-Z][A-Z0-9]*\.md$')

if [ -n "$remaining_files" ]; then
  # Now search for the actual paths for reporting
  print_error "The following files still need to be renamed:"
  for file in $remaining_files; do
    find . -name "$file" | grep -v "node_modules" | grep -v "target" | sort
  done
else
  print_success "All Markdown files follow the naming conventions!"
fi

print_header "Standardization Complete"
echo "All Markdown files have been standardized according to the naming conventions:"
echo "- README.md files remain in UPPER_CASE"
echo "- CLAUDE.md remains in UPPER_CASE"
echo "- Acronyms (like FAQ.md, TBD.md) remain in UPPERCASE"
echo "- Other documentation files use PascalCase"