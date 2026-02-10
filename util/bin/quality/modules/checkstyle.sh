#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# checkstyle.sh
# Checkstyle code style module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_checkstyle" > /dev/null; then
  return 0
fi

# Function to check code style with Checkstyle
check_checkstyle() {
  local fix_issues="${1:-false}"
  
  header "Checking Code Style with Checkstyle"
  
  # Define the standardized config file location
  local config_file="${QUALITY_DIR}/checkstyle/checkstyle.xml"
  local suppressions_file="${QUALITY_DIR}/checkstyle/checkstyle-suppressions.xml"
  
  # Verify configuration files exist
  if [ ! -f "$config_file" ]; then
    error "Checkstyle configuration file not found: $config_file"
    return 1
  fi
  
  if [ ! -f "$suppressions_file" ]; then
    warn "Checkstyle suppressions file not found: $suppressions_file"
  fi
  
  # Build the Checkstyle command
  local checkstyle_cmd="mvn checkstyle:check -Dcheckstyle.config.location=${config_file}"
  
  if [ -f "$suppressions_file" ]; then
    checkstyle_cmd="${checkstyle_cmd} -Dcheckstyle.suppressions.location=${suppressions_file}"
  fi
  
  info "Checking code style..."
  
  # Run the command
  if eval "$checkstyle_cmd"; then
    success "Checkstyle check passed - code follows style guidelines"
    return 0
  else
    error "Checkstyle check failed - code style issues found"
    warn "Check target/checkstyle-result.xml for details"
    
    # Note: Checkstyle can't automatically fix issues
    if [ "$fix_issues" = "true" ]; then
      warn "Checkstyle issues must be fixed manually - automatic fixing not supported"
    fi
    
    return 1
  fi
}