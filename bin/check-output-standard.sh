#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Filename: check-output-standard.sh
# Description: Check if scripts comply with the Samstraumr output standard
#
# This script analyzes other scripts to verify they are using the standardized
# output functions and patterns defined in the output standard.
#==============================================================================

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the output standard to use its functions
if [ -f "${PROJECT_ROOT}/util/lib/output-standard.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/output-standard.sh"
else
  echo "Error: output-standard.sh not found. This script requires it."
  exit 1
fi

# Parse command line arguments
VERBOSE=false
CHECK_DIR="${PROJECT_ROOT}/bin"

while [[ $# -gt 0 ]]; do
  case "$1" in
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -d|--directory)
      if [[ -n "$2" && "$2" != -* ]]; then
        CHECK_DIR="$2"
        shift 2
      else
        print_error "Directory path missing after -d/--directory"
      fi
      ;;
    -h|--help)
      print_header "Output Standard Checker"
      echo "Usage: $(basename $0) [options]"
      echo
      echo "Options:"
      echo "  -v, --verbose        Show detailed output"
      echo "  -d, --directory DIR  Check scripts in specific directory"
      echo "  -h, --help           Show this help message"
      exit 0
      ;;
    *)
      print_error "Unknown option: $1. Use -h for help."
      ;;
  esac
done

print_header "Checking Script Output Standard Compliance"
print_info "Analyzing scripts in: ${CHECK_DIR}"

# Array of patterns to search for
REQUIRED_PATTERNS=(
  "print_header"
  "print_info"
  "print_success"
  "print_error"
  "print_warning"
)

# Get all bash scripts
SCRIPTS=$(find "${CHECK_DIR}" -type f -name "*.sh" -o -name "s8r-*" | sort)
TOTAL_SCRIPTS=$(echo "${SCRIPTS}" | wc -l)

if [ "${TOTAL_SCRIPTS}" -eq 0 ]; then
  print_warning "No scripts found in ${CHECK_DIR}"
  exit 0
fi

print_info "Found ${TOTAL_SCRIPTS} scripts to check"

# Statistics variables
COMPLIANT_SCRIPTS=0
NON_COMPLIANT_SCRIPTS=0
PARTIALLY_COMPLIANT_SCRIPTS=0

# Check each script
for SCRIPT in ${SCRIPTS}; do
  if [[ "$VERBOSE" == "true" ]]; then
    print_section "Checking $(basename ${SCRIPT})"
  fi
  
  # Skip certain scripts
  if [[ "${SCRIPT}" == *"check-output-standard.sh"* ]]; then
    if [[ "$VERBOSE" == "true" ]]; then
      print_info "Skipping self (output standard checker)"
    fi
    continue
  fi
  
  # Check if script sources the output standard
  if grep -q "source.*output-standard\.sh" "${SCRIPT}" || 
     grep -q "source.*unified-common\.sh" "${SCRIPT}"; then
    SOURCES_STANDARD=true
  else
    SOURCES_STANDARD=false
  fi
  
  # Count how many of the required patterns are found
  FOUND_PATTERNS=0
  MISSING_PATTERNS=()
  
  for PATTERN in "${REQUIRED_PATTERNS[@]}"; do
    if grep -q "${PATTERN}" "${SCRIPT}"; then
      ((FOUND_PATTERNS++))
      if [[ "$VERBOSE" == "true" ]]; then
        print_debug "Found pattern: ${PATTERN}"
      fi
    else
      MISSING_PATTERNS+=("${PATTERN}")
    fi
  done
  
  # Determine compliance level
  SCRIPT_NAME=$(basename "${SCRIPT}")
  
  if [ ${FOUND_PATTERNS} -eq ${#REQUIRED_PATTERNS[@]} ] && [ ${SOURCES_STANDARD} == true ]; then
    if [[ "$VERBOSE" == "true" ]]; then
      print_success "${SCRIPT_NAME} is fully compliant"
    fi
    ((COMPLIANT_SCRIPTS++))
  elif [ ${FOUND_PATTERNS} -eq 0 ] && [ ${SOURCES_STANDARD} == false ]; then
    if [[ "$VERBOSE" == "true" ]]; then
      print_warning "${SCRIPT_NAME} is not compliant"
      print_debug "  Does not source output-standard.sh"
      print_debug "  Does not use any standard output functions"
    fi
    ((NON_COMPLIANT_SCRIPTS++))
  else
    if [[ "$VERBOSE" == "true" ]]; then
      print_warning "${SCRIPT_NAME} is partially compliant"
      if [ ${SOURCES_STANDARD} == false ]; then
        print_debug "  Does not source output-standard.sh"
      fi
      for PATTERN in "${MISSING_PATTERNS[@]}"; do
        print_debug "  Missing pattern: ${PATTERN}"
      done
    fi
    ((PARTIALLY_COMPLIANT_SCRIPTS++))
  fi
done

# Print summary
print_header "Compliance Summary"

COMPLIANT_PERCENT=$((COMPLIANT_SCRIPTS * 100 / TOTAL_SCRIPTS))
print_task "Fully compliant scripts" "info"
echo "${COMPLIANT_SCRIPTS}/${TOTAL_SCRIPTS} (${COMPLIANT_PERCENT}%)"

PARTIALLY_PERCENT=$((PARTIALLY_COMPLIANT_SCRIPTS * 100 / TOTAL_SCRIPTS))
print_task "Partially compliant scripts" "info"
echo "${PARTIALLY_COMPLIANT_SCRIPTS}/${TOTAL_SCRIPTS} (${PARTIALLY_PERCENT}%)"

NON_COMPLIANT_PERCENT=$((NON_COMPLIANT_SCRIPTS * 100 / TOTAL_SCRIPTS))
print_task "Non-compliant scripts" "info"
echo "${NON_COMPLIANT_SCRIPTS}/${TOTAL_SCRIPTS} (${NON_COMPLIANT_PERCENT}%)"

print_section "Recommendations"

if [ ${NON_COMPLIANT_SCRIPTS} -gt 0 ] || [ ${PARTIALLY_COMPLIANT_SCRIPTS} -gt 0 ]; then
  print_info "To make scripts compliant with the output standard:"
  echo "1. Add the following line near the top of each script:"
  echo "   source \"\${PROJECT_ROOT}/util/lib/output-standard.sh\""
  echo "2. Replace echo statements with standardized output functions:"
  echo "   - print_header \"Section Title\""
  echo "   - print_info \"Going to do something\""
  echo "   - print_success \"Done something\""
  echo "   - print_warning \"Warning message\""
  echo "   - print_error \"Error message\""
  echo "3. Use time_exec for commands to get timing information:"
  echo "   time_exec command arg1 arg2..."
  
  if [ ${COMPLIANT_PERCENT} -lt 50 ]; then
    print_warning "Less than 50% of scripts are fully compliant. Consider running the automatic conversion script:"
    echo "bin/convert-to-output-standard.sh"
  fi
else
  print_success "All scripts are compliant with the output standard!"
fi

# Return appropriate exit code
if [ ${NON_COMPLIANT_SCRIPTS} -gt 0 ]; then
  exit 1
else
  exit 0
fi