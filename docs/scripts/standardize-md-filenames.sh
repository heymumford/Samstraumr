#!/bin/bash
# Script to standardize Markdown file naming conventions in the project
#
# Naming conventions:
# - README.md files remain in UPPER_CASE
# - CLAUDE.md remains in UPPER_CASE 
# - Other documentation files use PascalCase
# - Acronyms like FAQ, TBD remain in UPPERCASE

# Store the project root directory
PROJECT_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || pwd)
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  echo "Warning: Documentation library not found. Using fallback functions."
  USING_LIB=false
  
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
fi

# Function to convert filename to PascalCase (used for markdown files)
to_pascal_case() {
  local filename="$1"
  local basename="${filename%.*}"  # Remove extension
  local extension="${filename##*.}"  # Keep extension
  
  # Special case for README.md, CLAUDE.md and recognized acronyms
  if [[ "$basename" == "README" || "$basename" == "CLAUDE" || "$basename" =~ ^[A-Z]{2,}$ ]]; then
    echo "$filename"
    return
  fi
  
  # Convert kebab-case or snake_case to PascalCase
  local pascal_case=$(echo "$basename" | sed -E 's/(^|[-_])([a-z])/\U\2/g')
  
  # Return result with extension
  echo "${pascal_case}.${extension}"
}

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
  mv -v docs/testing/atl-btl-strategy.md docs/testing/ATLBTLStrategy.md
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
if [ -f "modules/samstraumr-core/src/main/resources/version-template.md" ]; then
  mv -v "modules/samstraumr-core/src/main/resources/version-template.md" "modules/samstraumr-core/src/main/resources/VersionTemplate.md"
fi

# Check if there are any remaining non-PascalCase files that aren't README.md or CLAUDE.md
print_header "Checking for any remaining files that don't follow conventions"

# Use library function if available for basic checks
if [[ "$USING_LIB" == true ]] && type find_non_kebab_case_files &>/dev/null; then
  if [[ "$USING_LIB" == true ]] && type print_info &>/dev/null; then
    print_info "Using doc-lib.sh to help with file checking"
  fi
  
  # For PascalCase we need a custom approach, but can use the library to find all markdown files
  remaining_files=""
  
  # Find all markdown files (excluding special cases)
  find "${PROJECT_ROOT}" -type f -name "*.md" | grep -v "/README\.md$" | grep -v "/CLAUDE\.md$" | 
    grep -v "/PULL_REQUEST_TEMPLATE\.md$" | sort | while read -r file; do
    
    filename=$(basename "$file")
    
    # Skip acronyms (all uppercase filenames)
    if [[ "$filename" =~ ^[A-Z][A-Z0-9]*\.md$ ]]; then
      continue
    fi
    
    # Skip version template (special case)
    if [[ "$filename" == "VersionTemplate.md" ]]; then
      continue
    fi
    
    # Check if filename doesn't follow PascalCase
    if [[ "$filename" =~ ^[a-z] || "$filename" =~ [-_] ]]; then
      # This file doesn't follow PascalCase convention
      remaining_files+="$file"$'\n'
    fi
  done
else
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
    
  # Now search for the actual paths for reporting
  if [ -n "$remaining_files" ]; then
    tmp_files=""
    for file in $remaining_files; do
      tmp_files+=$(find . -name "$file" | grep -v "node_modules" | grep -v "target" | sort)
      tmp_files+=$'\n'
    done
    remaining_files="$tmp_files"
  fi
fi

if [ -n "$remaining_files" ]; then
  if [[ "$USING_LIB" == true ]] && type print_error &>/dev/null; then
    print_error "The following files still need to be renamed:"
    echo "$remaining_files"
  else
    print_error "The following files still need to be renamed:"
    echo "$remaining_files"
  fi
else
  if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
    print_success "All Markdown files follow the naming conventions!"
  else
    print_success "All Markdown files follow the naming conventions!"
  fi
fi

if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
  print_header "Standardization Complete"
else
  print_header "Standardization Complete"
fi

echo "All Markdown files have been standardized according to the naming conventions:"
echo "- README.md files remain in UPPER_CASE"
echo "- CLAUDE.md remains in UPPER_CASE" 
echo "- Acronyms (like FAQ.md, TBD.md) remain in UPPERCASE"
echo "- Other documentation files use PascalCase"

# Create a report if using the library
if [[ "$USING_LIB" == true ]] && [ -d "$REPORT_DIR" ]; then
  date_stamp=$(date +%Y%m%d)
  report_file="${REPORT_DIR}/markdown-standardization-${date_stamp}.md"
  
  # Create a report
  {
    echo "# Markdown Filename Standardization Report"
    echo "Generated: $(date)"
    echo
    echo "## Naming Conventions"
    echo "- README.md files remain in UPPER_CASE"
    echo "- CLAUDE.md remains in UPPER_CASE"
    echo "- Acronyms (like FAQ.md, TBD.md) remain in UPPERCASE"
    echo "- Other documentation files use PascalCase"
    echo
    echo "## Results"
    
    if [ -n "$remaining_files" ]; then
      echo "### Files Still Needing Standardization"
      echo
      echo "$remaining_files" | while read -r file; do
        if [ -n "$file" ]; then
          echo "- \`$file\`"
        fi
      done
    else
      echo "All markdown files follow the naming conventions!"
    fi
  } > "$report_file"
  
  if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
    print_success "Standardization report created at ${report_file}"
  fi
fi