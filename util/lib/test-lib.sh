#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Test Library - Consolidated test-related functions
# This library contains functions for running tests across the codebase
#==============================================================================

# Run ATL (Above The Line) tests
run_atl_tests() {
  local profile="${1:-atl-tests}"
  
  # Construct Maven command
  local mvn_cmd="mvn test -P${profile} -Dcucumber.filter.tags=\"@ATL\" -DskipTests=false -Dmaven.test.skip=false"
  
  # Run with settings file if it exists
  if [ -f "$(git rev-parse --show-toplevel)/surefire-settings.xml" ]; then
    mvn_cmd+=" -s $(git rev-parse --show-toplevel)/surefire-settings.xml"
  fi
  
  # Execute command
  echo "Running ATL tests with profile: ${profile}"
  eval "$mvn_cmd"
}

# Run component tests
run_component_tests() {
  local profile="${1:-composite-tests}"
  
  # Construct Maven command
  local mvn_cmd="mvn test -P${profile} -Dcucumber.filter.tags=\"@CompositeTest\" -DskipTests=false -Dmaven.test.skip=false"
  
  # Run with settings file if it exists
  if [ -f "$(git rev-parse --show-toplevel)/surefire-settings.xml" ]; then
    mvn_cmd+=" -s $(git rev-parse --show-toplevel)/surefire-settings.xml"
  fi
  
  # Execute command
  echo "Running component tests with profile: ${profile}"
  eval "$mvn_cmd"
}

# Map test type to Maven profile
map_test_type() {
  local test_type="$1"
  local profile=""
  
  case "$test_type" in
    unit|tube)
      profile="unit-tests"
      ;;
    component|composite)
      profile="composite-tests"
      ;;
    integration|flow)
      profile="flow-tests"
      ;;
    api|machine)
      profile="machine-tests"
      ;;
    system|stream)
      profile="stream-tests"
      ;;
    endtoend|acceptance)
      profile="acceptance-tests"
      ;;
    orchestration)
      profile="orchestration-tests"
      ;;
    atl)
      profile="atl-tests"
      ;;
    btl)
      profile="btl-tests"
      ;;
    adam)
      profile="adam-tube-tests"
      ;;
    all)
      profile="tests"
      ;;
    *)
      echo "Unknown test type: $test_type" >&2
      return 1
      ;;
  esac
  
  echo "$profile"
}
