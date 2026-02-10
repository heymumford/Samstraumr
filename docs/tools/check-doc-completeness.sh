#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# check-doc-completeness.sh
# Verifies documentation completeness for releases
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color
BOLD='\033[1m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
header() { echo -e "\n${BOLD}${YELLOW}$1${RESET}\n"; }

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Create a directory for reports
REPORT_DIR="${PROJECT_ROOT}/target/doc-completeness-reports"
mkdir -p "$REPORT_DIR"

# Timestamp for reports
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_FILE="${REPORT_DIR}/doc-completeness-${TIMESTAMP}.md"

# Check that each Java package has a corresponding package-info.java file
check_package_info() {
  header "Checking package-info.java files"
  
  local missing_package_info=0
  local packages=()
  
  # Find all directories containing Java files
  readarray -t java_dirs < <(find "${PROJECT_ROOT}/Samstraumr" -type f -name "*.java" -not -name "package-info.java" | xargs dirname | sort | uniq)
  
  {
    echo "# Package Documentation Completeness Check"
    echo "Generated: $(date)"
    echo
    echo "## Packages Missing Documentation"
    echo
  } >> "$REPORT_FILE"
  
  for dir in "${java_dirs[@]}"; do
    # Check if package-info.java exists in this directory
    if [ ! -f "${dir}/package-info.java" ]; then
      echo "- \`${dir#${PROJECT_ROOT}/}\` - Missing package-info.java" >> "$REPORT_FILE"
      missing_package_info=$((missing_package_info + 1))
      warn "Missing package-info.java in ${dir#${PROJECT_ROOT}/}"
    fi
  done
  
  if [ "$missing_package_info" -gt 0 ]; then
    error "Found $missing_package_info packages without package-info.java"
    return 1
  else
    success "All packages have package-info.java files"
    return 0
  fi
}

# Check that all @link tags in Javadoc resolve correctly
check_javadoc_links() {
  header "Checking Javadoc @link resolution"
  
  local invalid_links=0
  local temp_file=$(mktemp)
  
  {
    echo "## Invalid Javadoc @link References"
    echo
  } >> "$REPORT_FILE"
  
  # Use Javadoc in dryrun mode to find broken links
  if command -v javadoc &> /dev/null; then
    info "Running Javadoc link check..."
    
    # Find all Java files except package-info
    javadoc -quiet -Xdoclint:all,-missing -d "${REPORT_DIR}/temp-javadoc" \
      $(find "${PROJECT_ROOT}/Samstraumr" -type f -name "*.java" -not -path "*/target/*") \
      2> "$temp_file" || true
    
    # Parse Javadoc output for broken links
    if grep -q "warning: Cannot find" "$temp_file"; then
      grep "warning: Cannot find" "$temp_file" | while read -r line; do
        echo "- $line" >> "$REPORT_FILE"
        invalid_links=$((invalid_links + 1))
        warn "$line"
      done
      
      error "Found $invalid_links invalid Javadoc @link references"
      rm -f "$temp_file"
      return 1
    else
      success "All Javadoc @link references resolve correctly"
      rm -f "$temp_file"
      return 0
    fi
  else
    warn "javadoc command not found, skipping @link check"
    echo "- Javadoc command not available, skipping @link checks" >> "$REPORT_FILE"
    return 0
  fi
}

# Check that all READMEs in directories conform to the template
check_readme_completeness() {
  header "Checking README completeness"
  
  local incomplete_readmes=0
  
  {
    echo "## Incomplete README Files"
    echo
  } >> "$REPORT_FILE"
  
  # Find all READMEs
  find "${PROJECT_ROOT}" -type f -name "README.md" -not -path "*/target/*" -not -path "*/node_modules/*" | while read -r readme; do
    local dir_name=$(dirname "$readme")
    local rel_path="${readme#${PROJECT_ROOT}/}"
    local is_incomplete=false
    local missing_sections=""
    
    # Check for required sections
    if ! grep -q "^# " "$readme"; then
      is_incomplete=true
      missing_sections="$missing_sections Title,"
    fi
    
    if ! grep -q "^## Purpose" "$readme" && ! grep -q "^## Overview" "$readme"; then
      is_incomplete=true
      missing_sections="$missing_sections Purpose/Overview,"
    fi
    
    if ! grep -q "^## Contents" "$readme" && ! grep -q "^## Files" "$readme"; then
      is_incomplete=true
      missing_sections="$missing_sections Contents,"
    fi
    
    # Report incomplete READMEs
    if [ "$is_incomplete" = true ]; then
      missing_sections=${missing_sections%,}
      echo "- \`$rel_path\` - Missing sections: $missing_sections" >> "$REPORT_FILE"
      incomplete_readmes=$((incomplete_readmes + 1))
      warn "Incomplete README: $rel_path (Missing: $missing_sections)"
    fi
  done
  
  if [ "$incomplete_readmes" -gt 0 ]; then
    error "Found $incomplete_readmes incomplete README files"
    return 1
  else
    success "All README files are complete"
    return 0
  fi
}

# Check for example code documentation
check_examples_documentation() {
  header "Checking examples documentation"
  
  local missing_examples=0
  
  {
    echo "## Public API Example Coverage"
    echo
  } >> "$REPORT_FILE"
  
  # Define key API classes that should have examples
  KEY_APIS=(
    "org.s8r.component.core.Component"
    "org.s8r.component.composite.Composite"
    "org.s8r.component.machine.Machine"
    "org.s8r.infrastructure.config.Configuration"
    "org.s8r.infrastructure.event.InMemoryEventDispatcher"
  )
  
  # Check examples directory
  local examples_dir="${PROJECT_ROOT}/docs/examples"
  if [ ! -d "$examples_dir" ]; then
    error "Examples directory does not exist: $examples_dir"
    echo "- Examples directory not found" >> "$REPORT_FILE"
    return 1
  fi
  
  # Check if each key API has corresponding examples
  for api in "${KEY_APIS[@]}"; do
    local class_name=$(echo "$api" | awk -F. '{print $NF}')
    local example_count=$(grep -l -r "$class_name" "$examples_dir" | wc -l)
    
    if [ "$example_count" -eq 0 ]; then
      echo "- No examples found for \`$api\`" >> "$REPORT_FILE"
      missing_examples=$((missing_examples + 1))
      warn "No examples found for key API: $api"
    fi
  done
  
  if [ "$missing_examples" -gt 0 ]; then
    error "Found $missing_examples key APIs without examples"
    return 1
  else
    success "All key APIs have examples"
    return 0
  fi
}

# Main function
main() {
  header "Documentation Completeness Check"
  
  # Initialize report
  {
    echo "# Documentation Completeness Report"
    echo "Generated: $(date)"
    echo
    echo "This report checks if the documentation is complete enough for a release."
    echo
  } > "$REPORT_FILE"
  
  # Run checks
  local exit_status=0
  
  check_package_info
  local package_status=$?
  
  check_javadoc_links
  local javadoc_status=$?
  
  check_readme_completeness
  local readme_status=$?
  
  check_examples_documentation
  local examples_status=$?
  
  # Generate overall summary
  {
    echo "## Summary"
    echo
    echo "| Check | Status |"
    echo "|-------|--------|"
    
    if [ "$package_status" -eq 0 ]; then
      echo "| Package Documentation | ✅ Pass |"
    else
      echo "| Package Documentation | ❌ Fail |"
      exit_status=1
    fi
    
    if [ "$javadoc_status" -eq 0 ]; then
      echo "| Javadoc Links | ✅ Pass |"
    else
      echo "| Javadoc Links | ❌ Fail |"
      exit_status=1
    fi
    
    if [ "$readme_status" -eq 0 ]; then
      echo "| README Completeness | ✅ Pass |"
    else
      echo "| README Completeness | ❌ Fail |"
      exit_status=1
    fi
    
    if [ "$examples_status" -eq 0 ]; then
      echo "| Examples Documentation | ✅ Pass |"
    else
      echo "| Examples Documentation | ❌ Fail |"
      exit_status=1
    fi
    
    echo
    if [ "$exit_status" -eq 0 ]; then
      echo "✅ **OVERALL RESULT: PASS** - Documentation is complete and ready for release."
    else
      echo "❌ **OVERALL RESULT: FAIL** - Documentation has issues that should be addressed before release."
    fi
  } >> "$REPORT_FILE"
  
  # Print summary
  if [ "$exit_status" -eq 0 ]; then
    success "Documentation is complete and ready for release"
    info "Detailed report: $REPORT_FILE"
  else
    error "Documentation has issues that should be addressed before release"
    info "See detailed report at: $REPORT_FILE"
  fi
  
  return $exit_status
}

# Run the main function
main "$@"