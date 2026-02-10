#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# spotbugs.sh
# SpotBugs bug detection module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_spotbugs" > /dev/null; then
  return 0
fi

# Function to check for bugs with SpotBugs
check_spotbugs() {
  local fix_issues="${1:-false}"
  
  header "Checking for Bugs with SpotBugs"
  
  # Define the standardized config file location
  local exclude_file="${QUALITY_DIR}/spotbugs/spotbugs-exclude.xml"
  
  # Verify configuration files exist
  if [ ! -f "$exclude_file" ]; then
    warn "SpotBugs exclude file not found: $exclude_file"
    warn "Using default SpotBugs configuration"
  }
  
  # Build the SpotBugs command
  local spotbugs_cmd="mvn spotbugs:check"
  
  if [ -f "$exclude_file" ]; then
    spotbugs_cmd="${spotbugs_cmd} -Dspotbugs.excludeFilterFile=${exclude_file}"
  fi
  
  # Add HTML report generation
  spotbugs_cmd="${spotbugs_cmd} -Dspotbugs.outputFormat=html -Dspotbugs.reportPath=${REPORT_DIR}/spotbugs.html"
  
  info "Checking for bugs..."
  
  # Run the command
  if eval "$spotbugs_cmd"; then
    success "SpotBugs check passed - no potential bugs detected"
    return 0
  else
    error "SpotBugs check failed - potential bugs detected"
    warn "Check ${REPORT_DIR}/spotbugs.html for details"
    
    # Note: SpotBugs can't automatically fix issues
    if [ "$fix_issues" = "true" ]; then
      warn "SpotBugs issues must be fixed manually - automatic fixing not supported"
    fi
    
    return 1
  fi
}