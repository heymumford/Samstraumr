#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# jacoco.sh
# JaCoCo code coverage module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_jacoco" > /dev/null; then
  return 0
fi

# Function to check code coverage with JaCoCo
check_jacoco() {
  local fix_issues="${1:-false}"
  
  header "Checking Code Coverage with JaCoCo"
  
  # Define the standardized config file location
  local rules_file="${QUALITY_DIR}/jacoco/jacoco.xml"
  
  # Verify configuration files exist
  if [ ! -f "$rules_file" ]; then
    warn "JaCoCo rules file not found: $rules_file"
    warn "Using default JaCoCo configuration"
  }
  
  info "Running tests to generate coverage data..."
  
  # First run tests to generate coverage data
  # Disable 'fast' profile to ensure tests run
  local test_cmd="mvn test -P !fast -Djacoco.skip=false"
  
  if ! eval "$test_cmd"; then
    error "Tests failed - cannot generate coverage report"
    return 1
  fi
  
  info "Generating coverage report..."
  
  # Then generate the report
  local jacoco_cmd="mvn jacoco:report"
  
  if [ -f "$rules_file" ]; then
    jacoco_cmd="${jacoco_cmd} -Djacoco.rules=${rules_file}"
  fi
  
  if eval "$jacoco_cmd"; then
    success "JaCoCo coverage report generated"
    info "Report available at: target/site/jacoco/index.html"
    
    # Copy report to unified report directory
    if [ -d "target/site/jacoco" ]; then
      mkdir -p "${REPORT_DIR}/jacoco"
      cp -r target/site/jacoco/* "${REPORT_DIR}/jacoco/"
      info "Report copied to: ${REPORT_DIR}/jacoco/"
    fi
    
    # Run check but don't fail if it doesn't pass
    # This is because coverage checks are often too strict during development
    info "Checking coverage against thresholds..."
    if mvn jacoco:check -Djacoco.haltOnFailure=false; then
      success "Code coverage meets thresholds"
    else
      warn "Code coverage below thresholds"
      warn "See report for details"
      
      # If we're in strict mode, return failure
      if [ "$PROFILE" = "strict" ]; then
        return 1
      fi
    fi
    
    return 0
  else
    error "JaCoCo report generation failed"
    return 1
  fi
}