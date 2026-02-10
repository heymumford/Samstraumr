#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# encoding.sh
# File encoding check module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_encoding" > /dev/null; then
  return 0
fi

# Function to check file encodings
check_encoding() {
  local fix_issues="${1:-false}"
  
  header "Checking File Encodings and Line Endings"
  
  local verbose=false
  if [ "$VERBOSE" = "true" ]; then
    verbose=true
  fi
  
  # Use the built-in function from quality-lib.sh if available
  if declare -f "check_file_encoding" > /dev/null; then
    check_file_encoding "${PROJECT_ROOT}" "$verbose" "$fix_issues"
    return $?
  else
    # Fall back to the script if function not available
    if [ -f "${PROJECT_ROOT}/util/scripts/check-encoding.sh" ]; then
      info "Using check-encoding.sh script"
      
      local encoding_cmd="${PROJECT_ROOT}/util/scripts/check-encoding.sh"
      
      if [ "$verbose" = "true" ]; then
        encoding_cmd="${encoding_cmd} --verbose"
      fi
      
      if [ "$fix_issues" = "true" ]; then
        encoding_cmd="${encoding_cmd} --fix"
      fi
      
      if eval "$encoding_cmd"; then
        success "File encoding check passed - all files have correct encoding and line endings"
        return 0
      else
        error "File encoding check failed - some files have incorrect encoding or line endings"
        
        if [ "$fix_issues" = "true" ]; then
          warn "Some files could not be fixed automatically"
        else
          warn "Run with --fix to automatically fix encoding issues"
        fi
        
        return 1
      fi
    else
      error "File encoding check script not found: ${PROJECT_ROOT}/util/scripts/check-encoding.sh"
      error "Cannot check file encodings"
      return 1
    fi
  fi
}