#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# pmd.sh
# PMD static analysis module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_pmd" > /dev/null; then
  return 0
fi

# Function to check code with PMD
check_pmd() {
  local fix_issues="${1:-false}"
  
  header "Checking Code with PMD"
  
  # Define the standardized config file locations
  local ruleset_file="${QUALITY_DIR}/pmd/pmd-ruleset.xml"
  local exclude_file="${QUALITY_DIR}/pmd/pmd-exclude.properties"
  
  # Verify configuration files exist
  if [ ! -f "$ruleset_file" ]; then
    error "PMD ruleset file not found: $ruleset_file"
    return 1
  fi
  
  if [ ! -f "$exclude_file" ]; then
    warn "PMD exclude file not found: $exclude_file"
  }
  
  # Build the PMD command
  local pmd_cmd="mvn pmd:check -Dpmd.rulesets=${ruleset_file}"
  
  if [ -f "$exclude_file" ]; then
    pmd_cmd="${pmd_cmd} -Dpmd.excludeFromFailureFile=${exclude_file}"
  fi
  
  info "Checking code with PMD..."
  
  # Run the command
  if eval "$pmd_cmd"; then
    success "PMD check passed - no code issues detected"
    return 0
  else
    error "PMD check failed - code issues detected"
    warn "Check target/pmd.xml for details"
    
    # If fix mode is enabled, try to automatically fix PMD issues
    if [ "$fix_issues" = "true" ]; then
      info "Attempting to fix PMD issues..."
      
      # Use the built-in function from quality-lib.sh if available
      if declare -f "fix_pmd_issues" > /dev/null; then
        fix_pmd_issues
        
        # Run PMD check again to see if issues were fixed
        info "Re-running PMD check after fixes..."
        if eval "$pmd_cmd"; then
          success "PMD issues fixed successfully"
          return 0
        else
          warn "Some PMD issues could not be fixed automatically"
          warn "Check target/pmd.xml for remaining issues"
          return 1
        fi
      else
        warn "PMD auto-fix function not available - issues must be fixed manually"
        return 1
      fi
    else
      warn "Run with --fix to automatically fix some PMD issues"
      return 1
    fi
  fi
}