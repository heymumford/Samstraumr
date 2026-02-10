#!/bin/bash
#==============================================================================
# Filename: build-lib.sh
# Description: Shared functions for build scripts
#==============================================================================

# Determine script paths and load common library
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the common library
source "${SCRIPT_LIB_DIR}/common.sh"

#------------------------------------------------------------------------------
# Build Mode Functions
#------------------------------------------------------------------------------

function parse_build_mode() {
  local mode="$1"
  
  case "$(to_lowercase "$mode")" in
    fast)
      echo "compile"
      ;;
    compile|test|package|install|verify|clean)
      echo "$mode"
      ;;
    *)
      print_error "Invalid build mode: $mode"
      echo "compile" # Default to compile
      ;;
  esac
}

function get_profile_flag() {
  local profile="$1"
  
  if [ -n "$profile" ]; then
    echo "-P $profile"
  else
    echo ""
  fi
}

function setup_build_environment() {
  # Set Maven options
  if [ -z "$MAVEN_OPTS" ]; then
    export MAVEN_OPTS="${SAMSTRAUMR_MEMORY_OPTS}"
  fi
  
  # Set Java options
  if [ -z "$_JAVA_OPTIONS" ]; then
    export _JAVA_OPTIONS="-Dfile.encoding=UTF-8"
  fi
  
  # Display Java version
  java -version
  
  # Set up Java 17 targeting if needed
  if [ -f "${PROJECT_ROOT}/util/setup-java17-compat.sh" ]; then
    source "${PROJECT_ROOT}/util/setup-java17-compat.sh"
  fi
}

#------------------------------------------------------------------------------
# Maven Build Functions
#------------------------------------------------------------------------------

function print_build_info() {
  local mode="$1"
  local clean="$2"
  local profile="$3"
  local parallel="$4"
  local additional_args="$5"
  
  print_header "Building Samstraumr with optimized settings"
  echo "📦 Mode: $mode"
  echo "🧹 Clean: ${clean:-No}"
  echo "⚙️ Profile: ${profile:-default}"
  echo "💻 Parallel: ${parallel}"
  echo "🧠 Memory: $MAVEN_OPTS"
  echo "➕ Additional args: $additional_args"
  echo ""
}

function run_quality_checks() {
  local module_path="$1"
  local skip_quality="$2"
  
  if [ "$skip_quality" != "true" ]; then
    print_section "Running Quality Checks"
    
    # Run Spotless
    print_info "Running code formatting with Spotless..."
    if mvn -f "${module_path}/pom.xml" spotless:apply -q; then
      print_success "Code formatting applied successfully"
    else
      print_warning "Spotless formatting failed - proceeding with build anyway"
    fi
    
    # Add other quality checks here as needed
  else
    print_info "Skipping quality checks (--skip-quality flag used)"
  fi
}

function execute_build() {
  local module_path="$1"
  local clean="$2"
  local mode="$3"
  local parallel="$4"
  local profile="$5"
  local additional_args="$6"
  
  print_section "Executing Build"
  
  # Build command
  local build_cmd="mvn -f ${module_path}/pom.xml"
  
  # Add clean if specified
  if [ -n "$clean" ]; then
    build_cmd="$build_cmd $clean"
  fi
  
  # Add mode
  build_cmd="$build_cmd $mode"
  
  # Add parallel flag
  build_cmd="$build_cmd $parallel"
  
  # Add profile
  if [ -n "$profile" ]; then
    build_cmd="$build_cmd $profile"
  fi
  
  # Add additional args
  if [ -n "$additional_args" ]; then
    build_cmd="$build_cmd $additional_args"
  fi
  
  print_debug "Build command: $build_cmd"
  
  # Execute the build
  if eval "$build_cmd"; then
    print_success "Build completed successfully"
    
    # Show report locations if needed
    if [ "$mode" = "test" ]; then
      echo "📊 Test reports available at:"
      echo "  - Cucumber: ${SAMSTRAUMR_CUCUMBER_REPORT}"
      echo "  - JaCoCo: ${SAMSTRAUMR_JACOCO_REPORT} (if enabled)"
    fi
    
    return 0
  else
    print_error "Build failed"
    return 1
  fi
}

function build_project() {
  local clean_flag="$1"
  local mode="$2"
  local profile="$3"
  local additional_args="$4"
  local skip_quality="${5:-false}"
  
  # Set up build environment
  setup_build_environment
  
  # Process arguments
  local clean_arg=""
  if [ "$clean_flag" = "true" ]; then
    clean_arg="clean"
  fi
  
  # Get profile flag
  local profile_flag=$(get_profile_flag "$profile")
  
  # Get parallel flag
  local parallel_flag="${SAMSTRAUMR_PARALLEL_FLAG}"
  
  # Print build info
  print_build_info "$mode" "$clean_arg" "$profile_flag" "$parallel_flag" "$additional_args"
  
  # Run quality checks
  run_quality_checks "${SAMSTRAUMR_CORE_MODULE}" "$skip_quality"
  
  # Execute the build
  execute_build "${SAMSTRAUMR_CORE_MODULE}" "$clean_arg" "$mode" "$parallel_flag" "$profile_flag" "$additional_args"
  
  return $?
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------

# Export functions
export -f parse_build_mode
export -f get_profile_flag
export -f setup_build_environment
export -f print_build_info
export -f run_quality_checks
export -f execute_build
export -f build_project

# Print debug info if verbose
if [ "${VERBOSE:-false}" = "true" ]; then
  print_debug "Loaded build-lib.sh library"
fi