#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# spotless.sh
# Spotless code formatting module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_spotless" > /dev/null; then
  return 0
fi

# Function to check code formatting with Spotless
check_spotless() {
  local fix_issues="${1:-false}"
  
  header "Checking Code Formatting with Spotless"
  
  # Build the Spotless command based on arguments
  local spotless_cmd="mvn spotless:"
  if [ "$fix_issues" = "true" ]; then
    spotless_cmd="${spotless_cmd}apply"
    info "Applying Spotless formatting..."
  else
    spotless_cmd="${spotless_cmd}check"
    info "Checking Spotless formatting..."
  fi
  
  # Run the command
  if eval "$spotless_cmd"; then
    success "Spotless check passed - code is properly formatted"
    return 0
  else
    if [ "$fix_issues" = "true" ]; then
      error "Spotless failed to apply formatting to some files"
    else
      error "Spotless check failed - run with --fix to automatically apply formatting"
      warn "Run 'mvn spotless:apply' to fix formatting issues"
    fi
    return 1
  fi
}