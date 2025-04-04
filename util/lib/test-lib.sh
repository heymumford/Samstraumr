#!/bin/bash
#==============================================================================
# Filename: test-lib.sh
# Description: Shared functions for test scripts
#==============================================================================

# Determine script paths and load common library
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the common library
source "${SCRIPT_LIB_DIR}/common.sh"

#------------------------------------------------------------------------------
# Test Type Mapping Functions
#------------------------------------------------------------------------------

# Map between industry-standard and Samstraumr-specific test terminology
function map_test_type() {
  local test_type="$1"
  
  # Convert to lowercase for case-insensitive matching
  test_type=$(to_lowercase "$test_type")
  
  case "$test_type" in
    # Industry standard -> Samstraumr specific
    smoke)
      echo "orchestration"
      ;;
    unit)
      echo "tube"
      ;;
    component)
      echo "composite"
      ;;
    integration)
      echo "flow"
      ;;
    api)
      echo "machine"
      ;;
    system)
      echo "stream"
      ;;
    endtoend)
      echo "acceptance"
      ;;
    property)
      echo "adaptation"
      ;;
      
    # Samstraumr specific -> Industry standard
    orchestration)
      echo "smoke"
      ;;
    tube)
      echo "unit"
      ;;
    composite)
      echo "component"
      ;;
    flow)
      echo "integration"
      ;;
    machine)
      echo "api"
      ;;
    stream)
      echo "system"
      ;;
    acceptance)
      echo "endtoend"
      ;;
    adaptation)
      echo "property"
      ;;
      
    # Handle bundle (legacy term for composite)
    bundle)
      echo "component"
      ;;
      
    # Special case for all tests
    all)
      echo "all"
      ;;
      
    # Special case for adam tube tests
    adam|adamtube)
      echo "adam"
      ;;
      
    # Return unknown for anything else
    *)
      echo "unknown"
      ;;
  esac
}

# Get Maven profile for test type
function get_test_profile() {
  local test_type="$1"
  
  # Convert to lowercase for case-insensitive matching
  test_type=$(to_lowercase "$test_type")
  
  case "$test_type" in
    # Special test profiles
    atl|above-the-line)
      echo "${SAMSTRAUMR_ATL_PROFILE}"
      ;;
    btl|below-the-line)
      echo "${SAMSTRAUMR_BTL_PROFILE}"
      ;;
    adam|adamtube)
      echo "${SAMSTRAUMR_ADAM_TUBE_PROFILE}"
      ;;
    adam-atl|adamtube-atl)
      echo "${SAMSTRAUMR_ADAM_TUBE_ATL_PROFILE}"
      ;;
      
    # Convert test type to profile
    *)
      local mapped_type=$(map_test_type "$test_type")
      if [ "$mapped_type" = "unknown" ]; then
        # Default to ATL profile if unknown
        echo "${SAMSTRAUMR_ATL_PROFILE}"
      else
        echo "${mapped_type}-tests"
      fi
      ;;
  esac
}

# Generate Cucumber filter tag expression
function get_cucumber_tag_expression() {
  local test_type="$1"
  local include_equivalent="${2:-false}"
  
  # Convert to lowercase for case-insensitive matching
  test_type=$(to_lowercase "$test_type")
  
  if [ "$test_type" = "all" ]; then
    # No tag filter for 'all'
    echo ""
  elif [ "$include_equivalent" = "true" ]; then
    local equivalent_type=$(map_test_type "$test_type")
    if [ "$equivalent_type" = "unknown" ]; then
      echo "-Dcucumber.filter.tags=\"@$test_type\""
    else
      echo "-Dcucumber.filter.tags=\"@$test_type or @$equivalent_type\""
    fi
  else
    echo "-Dcucumber.filter.tags=\"@$test_type\""
  fi
}

#------------------------------------------------------------------------------
# Test Execution Functions
#------------------------------------------------------------------------------

# Unified test execution function for all test types
# This replaces the need for separate functions for different test types
# Usage: run_test test_type [options]
# Options can be passed via associative array: declare -A options=([clean]=true [verbose]=true)
function run_test() {
  local test_type="$1"
  local -n options_ref="${2:-}"
  
  # Set default options if not provided
  local clean="${options_ref[clean]:-false}"
  local verbose="${options_ref[verbose]:-false}"
  local profile="${options_ref[profile]:-}"
  local output_file="${options_ref[output]:-}"
  local skip_quality="${options_ref[skip_quality]:-false}"
  local cyclename="${options_ref[cyclename]:-Tests}"
  local include_equivalent="${options_ref[both]:-false}"
  
  # Map test type to profile if not explicitly provided
  if [[ -z "$profile" ]]; then
    profile=$(get_test_profile "$test_type")
  fi
  
  # Get tag expression if needed for Cucumber
  local tag_expression=""
  if [[ "$include_equivalent" = "true" ]]; then
    local equivalent_type=$(map_test_type "$test_type")
    tag_expression=$(get_cucumber_tag_expression "$test_type" "true")
  else
    tag_expression=$(get_cucumber_tag_expression "$test_type" "false")
  fi
  
  # Build Maven command using standardized function
  local maven_cmd
  maven_cmd=$(build_maven_command "test" "$profile" "$clean" "$skip_quality")
  
  # Add Cucumber tag expression if present
  if [[ -n "$tag_expression" ]]; then
    maven_cmd="$maven_cmd $tag_expression"
  fi
  
  # Set test name based on type
  local test_name
  if [[ -n "$(map_test_type "$test_type")" && "$(map_test_type "$test_type")" != "unknown" ]]; then
    # Use pretty names for known test types
    test_name="$(capitalize_first "$test_type") Tests"
  else
    # Use as-is for special test types
    test_name="$test_type"
  fi
  
  # Add working directory
  maven_cmd="cd ${SAMSTRAUMR_CORE_MODULE} && $maven_cmd"
  
  # Run the test with status reporting
  run_test_with_status "$test_name" "$maven_cmd" "$verbose" "$output_file" "$cyclename"
  local result=$?
  
  # Return result
  return $result
}

# Run Cucumber tests specifically
function run_cucumber_tests() {
  local test_type="$1"
  local profile="$2"
  local tag_expression="$3"
  local output_file="$4"
  
  # Create options associative array
  declare -A options
  options[profile]="$profile"
  options[output]="$output_file"
  
  # Use the unified run_test function
  run_test "$test_type" options
  return $?
}

# Run Adam tube tests specifically
function run_adam_tube_tests() {
  local profile="$1"
  local output_file="$2"
  
  # Create options associative array
  declare -A options
  options[profile]="$profile"
  options[output]="$output_file"
  options[cyclename]="Adam Tube Tests"
  
  # Use the unified run_test function
  run_test "adam" options
  return $?
}

# Helper function to capitalize first letter of a string
function capitalize_first() {
  local input="$1"
  if [[ -z "$input" ]]; then
    echo ""
    return
  fi
  
  local first_char
  local rest
  
  first_char=$(echo "${input:0:1}" | tr '[:lower:]' '[:upper:]')
  rest="${input:1}"
  
  echo "${first_char}${rest}"
}

#------------------------------------------------------------------------------
# Test Reporting Functions
#------------------------------------------------------------------------------

function print_test_summary() {
  local success="$1"
  local test_type="$2"
  
  print_header "Test Summary"
  
  if [ "$success" = "true" ]; then
    print_success "All $test_type tests passed"
    if [ -f "${SAMSTRAUMR_CUCUMBER_REPORT}" ]; then
      print_info "Cucumber report available at: ${SAMSTRAUMR_CUCUMBER_REPORT}"
    fi
    if [ -f "${SAMSTRAUMR_JACOCO_REPORT}" ]; then
      print_info "JaCoCo coverage report available at: ${SAMSTRAUMR_JACOCO_REPORT}"
    fi
  else
    print_error "Some $test_type tests failed"
    print_info "Check the test output for details"
  fi
}

function show_test_types_help() {
  print_section "Test Types"
  echo -e "  Industry Standard       |  Samstraumr Equivalent"
  echo -e "  --------------------    |  ----------------------"
  echo -e "  smoke                   |  orchestration"
  echo -e "  unit                    |  tube"
  echo -e "  component               |  composite"
  echo -e "  integration             |  flow"
  echo -e "  api                     |  machine"
  echo -e "  system                  |  stream"
  echo -e "  endtoend                |  acceptance"
  echo -e "  property                |  adaptation"
  echo -e ""
  echo -e "  Special test types: all, atl (above-the-line), btl (below-the-line), adam (adam-tube)"
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------

# Export functions
export -f map_test_type
export -f get_test_profile
export -f get_cucumber_tag_expression
export -f run_test
export -f run_cucumber_tests
export -f run_adam_tube_tests
export -f capitalize_first
export -f print_test_summary
export -f show_test_types_help

# Print debug info if verbose
if [ "${VERBOSE:-false}" = "true" ]; then
  print_debug "Loaded test-lib.sh library"
fi