#!/bin/bash
#==============================================================================
# Filename: common.sh
# Description: Common utility functions for Samstraumr bash scripts
# Author: Claude
# Created: 2025-04-03
# Updated: 2025-04-03
#==============================================================================

# Exit on error
set -e

# Determine script paths
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Error: Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

#------------------------------------------------------------------------------
# ANSI Color Codes
#------------------------------------------------------------------------------
readonly COLOR_RED='\033[0;31m'
readonly COLOR_GREEN='\033[0;32m'
readonly COLOR_YELLOW='\033[0;33m'
readonly COLOR_BLUE='\033[0;34m'
readonly COLOR_MAGENTA='\033[0;35m'
readonly COLOR_CYAN='\033[0;36m'
readonly COLOR_RESET='\033[0m'
readonly COLOR_BOLD='\033[1m'

#------------------------------------------------------------------------------
# Output Functions
#------------------------------------------------------------------------------

function print_header() {
  echo -e "${COLOR_BLUE}${COLOR_BOLD}$1${COLOR_RESET}"
  echo -e "${COLOR_BLUE}${COLOR_BOLD}$(printf '=%.0s' $(seq 1 ${#1}))${COLOR_RESET}"
}

function print_section() {
  echo -e "${COLOR_CYAN}${COLOR_BOLD}$1${COLOR_RESET}"
  echo -e "${COLOR_CYAN}${COLOR_BOLD}$(printf '-%.0s' $(seq 1 ${#1}))${COLOR_RESET}"
}

function print_success() {
  echo -e "${COLOR_GREEN}✓ $1${COLOR_RESET}"
}

function print_error() {
  echo -e "${COLOR_RED}✗ $1${COLOR_RESET}" >&2
}

function print_warning() {
  echo -e "${COLOR_YELLOW}! $1${COLOR_RESET}"
}

function print_info() {
  echo -e "${COLOR_BLUE}→ $1${COLOR_RESET}"
}

function print_debug() {
  if [ "${VERBOSE:-false}" = "true" ]; then
    echo -e "${COLOR_MAGENTA}DEBUG: $1${COLOR_RESET}"
  fi
}

#------------------------------------------------------------------------------
# Argument Parsing
#------------------------------------------------------------------------------

function show_help_template() {
  local script_name="$1"
  local description="$2"
  local options="$3"
  local examples="$4"
  
  echo -e "${COLOR_BOLD}$(basename "$script_name")${COLOR_RESET} - $description"
  echo ""
  echo -e "${COLOR_BOLD}USAGE:${COLOR_RESET}"
  echo "  $(basename "$script_name") [options]"
  echo ""
  
  if [ -n "$options" ]; then
    echo -e "${COLOR_BOLD}OPTIONS:${COLOR_RESET}"
    echo -e "$options"
    echo ""
  fi
  
  if [ -n "$examples" ]; then
    echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
    echo -e "$examples"
    echo ""
  fi
}

function parse_common_args() {
  # Parse common arguments
  VERBOSE=false
  
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        return 1  # Signal to show help
        ;;
      -v|--verbose)
        VERBOSE=true
        shift
        ;;
      *)
        # Leave other arguments for the calling script to handle
        return 0
        ;;
    esac
  done
  
  return 0
}

#------------------------------------------------------------------------------
# File & Path Functions
#------------------------------------------------------------------------------

function ensure_directory_exists() {
  local dir="$1"
  if [ ! -d "$dir" ]; then
    print_info "Creating directory: $dir"
    mkdir -p "$dir"
  fi
}

function is_valid_path() {
  local path="$1"
  # Check if the path exists
  if [ -e "$path" ]; then
    return 0
  else
    return 1
  fi
}

# Converts Java package notation to directory path
# e.g., org.example.package -> org/example/package
function path_for_package() {
  echo "${1//./\/}"
}

# Finds Maven's location of a package
function find_package_directory() {
  local base_dir="$1"
  local package="$2"
  local package_path=$(path_for_package "$package")
  
  # Try to find the package directory in any of Maven's standard source directories
  for src_dir in "src/main/java" "src/test/java" "src/main/resources" "src/test/resources"; do
    local potential_path="${base_dir}/${src_dir}/${package_path}"
    if [ -d "$potential_path" ]; then
      echo "$potential_path"
      return 0
    fi
  done
  
  # Not found
  return 1
}

#------------------------------------------------------------------------------
# Maven Functions
#------------------------------------------------------------------------------

function get_maven_property() {
  local pom_file="$1"
  local property_name="$2"
  local default_value="${3:-}"
  
  # Try to extract the property value from the POM file
  local value=$(grep -oP "<${property_name}>\K[^<]+" "$pom_file" | head -n 1)
  
  if [ -n "$value" ]; then
    echo "$value"
  else
    echo "$default_value"
  fi
}

function run_maven_command() {
  local module_path="${1:-${SAMSTRAUMR_CORE_MODULE}}"
  shift
  
  # Set Maven options if not already set
  if [ -z "$MAVEN_OPTS" ]; then
    export MAVEN_OPTS="${SAMSTRAUMR_MEMORY_OPTS}"
  fi
  
  # Run the Maven command
  mvn -f "${module_path}/pom.xml" "$@"
  
  # Return Maven's exit code
  return $?
}

#------------------------------------------------------------------------------
# String Functions
#------------------------------------------------------------------------------

function to_lowercase() {
  echo "$1" | tr '[:upper:]' '[:lower:]'
}

function to_uppercase() {
  echo "$1" | tr '[:lower:]' '[:upper:]'
}

function trim_string() {
  echo "$1" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'
}

#------------------------------------------------------------------------------
# Validation Functions
#------------------------------------------------------------------------------

function validate_not_empty() {
  local value="$1"
  local name="$2"
  
  if [ -z "$value" ]; then
    print_error "$name cannot be empty"
    return 1
  fi
  
  return 0
}

function validate_is_number() {
  local value="$1"
  local name="$2"
  
  if ! [[ "$value" =~ ^[0-9]+$ ]]; then
    print_error "$name must be a number"
    return 1
  fi
  
  return 0
}

#------------------------------------------------------------------------------
# System Functions
#------------------------------------------------------------------------------

function get_os_type() {
  local os=$(uname -s)
  echo "$os"
}

function is_linux() {
  [[ "$(get_os_type)" == "Linux" ]]
}

function is_mac() {
  [[ "$(get_os_type)" == "Darwin" ]]
}

function get_cpu_count() {
  if is_linux; then
    grep -c processor /proc/cpuinfo
  elif is_mac; then
    sysctl -n hw.ncpu
  else
    echo "1" # Default to 1 for unknown systems
  fi
}

#------------------------------------------------------------------------------
# Git Functions
#------------------------------------------------------------------------------

function get_git_branch() {
  git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown"
}

function is_git_repo() {
  git rev-parse --is-inside-work-tree &>/dev/null
  return $?
}

function has_git_changes() {
  if ! is_git_repo; then
    return 1
  fi
  
  # Check for staged or unstaged changes
  ! git diff-index --quiet HEAD --
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------

# Export common functions
export -f print_header
export -f print_section
export -f print_success
export -f print_error
export -f print_warning
export -f print_info
export -f print_debug
export -f parse_common_args
export -f ensure_directory_exists
export -f is_valid_path
export -f path_for_package
export -f find_package_directory
export -f get_maven_property
export -f run_maven_command
export -f to_lowercase
export -f to_uppercase
export -f trim_string
export -f validate_not_empty
export -f validate_is_number
export -f get_os_type
export -f is_linux
export -f is_mac
export -f get_cpu_count
export -f get_git_branch
export -f is_git_repo
export -f has_git_changes

# Print debug info if verbose
if [ "${VERBOSE:-false}" = "true" ]; then
  print_debug "Loaded common.sh library"
  print_debug "PROJECT_ROOT: $PROJECT_ROOT"
  print_debug "SAMSTRAUMR_CORE_MODULE: $SAMSTRAUMR_CORE_MODULE"
fi