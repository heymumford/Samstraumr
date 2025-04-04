#!/bin/bash
#==============================================================================
# Filename: common.sh
# Description: Common utility functions for Samstraumr bash scripts
#
# This library provides centralized utility functions for all Samstraumr
# bash scripts, ensuring consistent formatting, error handling, and common
# operations across the project.
#
# USAGE:
#   source "${PROJECT_ROOT}/util/lib/common.sh"
#
# PROVIDES:
#   - Color formatting and standardized output functions
#   - Argument parsing utilities
#   - File and path manipulation
#   - Maven integration functions
#   - String processing utilities
#   - Validation functions
#   - OS detection and system utilities
#   - Git integration functions
#
# DEPENDENCIES:
#   - Requires .samstraumr.config for project configuration
#   - Uses standard Unix utilities (sed, grep, tr)
#==============================================================================

# Strict mode - exit if any command has non-zero exit status
# Remove this line if scripts need to continue after command failures
set -e

# Determine script paths
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Global variables initialized as undefined
SAMSTRAUMR_CORE_MODULE=""
SAMSTRAUMR_MEMORY_OPTS=""
VERBOSE="${VERBOSE:-false}"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Error: Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  echo "The common.sh library requires this configuration file to function correctly."
  exit 1
fi

#------------------------------------------------------------------------------
# ANSI Color Codes
#------------------------------------------------------------------------------
# These color codes are used for consistent terminal output styling
# The print_* functions should be used instead of direct color references
# to ensure compatibility across scripts
#
# IMPORTANT: These variables are readonly - scripts should never attempt
# to redefine them. If importing scripts define their own colors, they
# should check if these variables exist first.
#------------------------------------------------------------------------------

# Only define color variables if they don't already exist
# This allows scripts to define their own versions if needed
if ! declare -p COLOR_RED &>/dev/null; then
  readonly COLOR_RED='\033[0;31m'      # Error, failure
  readonly COLOR_GREEN='\033[0;32m'    # Success
  readonly COLOR_YELLOW='\033[0;33m'   # Warning
  readonly COLOR_BLUE='\033[0;34m'     # Information
  readonly COLOR_MAGENTA='\033[0;35m'  # Debug
  readonly COLOR_CYAN='\033[0;36m'     # Section headers
  readonly COLOR_RESET='\033[0m'       # Reset formatting
  readonly COLOR_BOLD='\033[1m'        # Bold text
fi

# Detect if colors should be disabled (for non-interactive use)
if [[ -n "${NO_COLOR:-}" || "${TERM:-}" == "dumb" ]]; then
  USE_COLOR=false
else
  USE_COLOR=true
fi

#------------------------------------------------------------------------------
# Output Functions
#------------------------------------------------------------------------------
# These functions provide consistent, styled terminal output
# They handle:
# - Color formatting with fallback to plain text when colors are disabled
# - Proper output redirection (errors go to stderr)
# - Conditional output based on verbosity settings
#
# USAGE EXAMPLES:
#   print_header "Building Project"
#   print_success "Tests passed successfully"
#   print_error "Failed to compile project"
#   print_info "Starting build process..."
#------------------------------------------------------------------------------

# Print a major section header with underlined text
# Usage: print_header "Header Text" [no_underline]
function print_header() {
  local text="$1"
  local no_underline="${2:-false}"
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BLUE}${COLOR_BOLD}${text}${COLOR_RESET}"
    if [[ "$no_underline" != "true" ]]; then
      # Create underline with proper length
      local underline=""
      for ((i=0; i<${#text}; i++)); do
        underline="${underline}="
      done
      echo -e "${COLOR_BLUE}${COLOR_BOLD}${underline}${COLOR_RESET}"
    fi
  else
    echo "$text"
    if [[ "$no_underline" != "true" ]]; then
      local underline=""
      for ((i=0; i<${#text}; i++)); do
        underline="${underline}="
      done
      echo "${underline}"
    fi
  fi
}

# Print a subsection header with dashed underline
# Usage: print_section "Section Title" [no_underline]
function print_section() {
  local text="$1"
  local no_underline="${2:-false}"
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_CYAN}${COLOR_BOLD}${text}${COLOR_RESET}"
    if [[ "$no_underline" != "true" ]]; then
      # Create dashed underline with proper length
      local underline=""
      for ((i=0; i<${#text}; i++)); do
        underline="${underline}-"
      done
      echo -e "${COLOR_CYAN}${COLOR_BOLD}${underline}${COLOR_RESET}"
    fi
  else
    echo "$text"
    if [[ "$no_underline" != "true" ]]; then
      local underline=""
      for ((i=0; i<${#text}; i++)); do
        underline="${underline}-"
      done
      echo "${underline}"
    fi
  fi
}

# Print a success message with checkmark
# Usage: print_success "Operation completed successfully"
function print_success() {
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_GREEN}✓ $1${COLOR_RESET}"
  else
    echo "[SUCCESS] $1"
  fi
}

# Print an error message with X symbol (goes to stderr)
# Usage: print_error "Failed to create directory"
function print_error() {
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_RED}✗ $1${COLOR_RESET}" >&2
  else
    echo "[ERROR] $1" >&2
  fi
}

# Print a warning message with ! symbol
# Usage: print_warning "This operation might take a long time"
function print_warning() {
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_YELLOW}! $1${COLOR_RESET}"
  else
    echo "[WARNING] $1"
  fi
}

# Print an informational message with arrow
# Usage: print_info "Processing file: example.txt"
function print_info() {
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BLUE}→ $1${COLOR_RESET}"
  else
    echo "[INFO] $1"
  fi
}

# Print bold text, optionally without newline
# Usage: print_bold "Important text" [no_newline]
function print_bold() {
  local no_newline=false
  local text="$1"
  
  # Check for -n flag to suppress newline
  if [[ "$1" == "-n" ]]; then
    no_newline=true
    text="$2"
  fi
  
  if [[ "$USE_COLOR" == "true" ]]; then
    if [[ "$no_newline" == "true" ]]; then
      echo -en "${COLOR_BOLD}${text}${COLOR_RESET}"
    else
      echo -e "${COLOR_BOLD}${text}${COLOR_RESET}"
    fi
  else
    if [[ "$no_newline" == "true" ]]; then
      echo -n "$text"
    else
      echo "$text"
    fi
  fi
}

# Print debug message (only when VERBOSE is true)
# Usage: print_debug "Variable value: $value"
function print_debug() {
  if [[ "${VERBOSE:-false}" == "true" ]]; then
    if [[ "$USE_COLOR" == "true" ]]; then
      echo -e "${COLOR_MAGENTA}DEBUG: $1${COLOR_RESET}" >&2
    else
      echo "[DEBUG] $1" >&2
    fi
  fi
}

#------------------------------------------------------------------------------
# Argument Parsing
#------------------------------------------------------------------------------
# Functions to handle command-line arguments consistently across scripts
# Provides standardized help formatting and common argument handling
#
# USAGE EXAMPLE:
#   show_help_template "$0" "Build the project" \
#     "-c, --clean     Clean before building\n-v, --verbose   Show detailed output" \
#     "./build.sh -c   # Clean and build"
#------------------------------------------------------------------------------

# Display standardized help information for a script
# Usage: show_help_template "script_path" "description" "options" "examples"
function show_help_template() {
  local script_name="$1"
  local description="$2"
  local options="$3"
  local examples="$4"
  
  # Use print_bold if available, otherwise fall back to direct formatting
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}$(basename "$script_name")${COLOR_RESET} - $description"
  else
    echo "$(basename "$script_name") - $description"
  fi
  echo ""
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "${COLOR_BOLD}USAGE:${COLOR_RESET}"
  else
    echo "USAGE:"
  fi
  echo "  $(basename "$script_name") [options]"
  echo ""
  
  if [ -n "$options" ]; then
    if [[ "$USE_COLOR" == "true" ]]; then
      echo -e "${COLOR_BOLD}OPTIONS:${COLOR_RESET}"
    else
      echo "OPTIONS:"
    fi
    echo -e "$options"
    echo ""
  fi
  
  if [ -n "$examples" ]; then
    if [[ "$USE_COLOR" == "true" ]]; then
      echo -e "${COLOR_BOLD}EXAMPLES:${COLOR_RESET}"
    else
      echo "EXAMPLES:"
    fi
    echo -e "$examples"
    echo ""
  fi
}

# Parse common arguments that should be handled consistently in all scripts
# Usage: parse_common_args "$@"
# Returns: 0 for success, 1 if help should be shown
# Side effects: Sets VERBOSE=true if --verbose flag is present
#               Shifts processed arguments from "$@"
function parse_common_args() {
  # Initialize variables with defaults
  VERBOSE=false
  
  # Process arguments one by one
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        return 1  # Signal to show help
        ;;
      -v|--verbose)
        VERBOSE=true
        shift
        ;;
      --no-color)
        USE_COLOR=false
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

# Validate required arguments are present
# Usage: validate_required_args "arg1" "arg2" ...
# Returns: 0 if all args are non-empty, 1 otherwise
function validate_required_args() {
  local missing=false
  
  for arg in "$@"; do
    if [ -z "$arg" ]; then
      missing=true
      break
    fi
  done
  
  if [ "$missing" = "true" ]; then
    print_error "Missing required argument(s)"
    return 1
  fi
  
  return 0
}

#------------------------------------------------------------------------------
# File & Path Functions
#------------------------------------------------------------------------------
# Helper functions for file operations and path manipulations
# These are particularly useful for Java/Maven projects
#
# USAGE EXAMPLES:
#   ensure_directory_exists "/path/to/dir"
#   java_package_path=$(path_for_package "org.example.project")
#------------------------------------------------------------------------------

# Create a directory if it doesn't exist
# Usage: ensure_directory_exists "/path/to/directory"
# Returns: 0 on success, non-zero on failure
function ensure_directory_exists() {
  local dir="$1"
  
  if [[ -z "$dir" ]]; then
    print_error "ensure_directory_exists: No directory specified"
    return 1
  fi
  
  if [[ ! -d "$dir" ]]; then
    print_info "Creating directory: $dir"
    if mkdir -p "$dir"; then
      print_debug "Directory created: $dir"
      return 0
    else
      print_error "Failed to create directory: $dir"
      return 1
    fi
  else
    print_debug "Directory already exists: $dir"
    return 0
  fi
}

# Check if a path is valid (exists)
# Usage: is_valid_path "/path/to/check"
# Returns: 0 if path exists, 1 otherwise
function is_valid_path() {
  local path="$1"
  
  if [[ -z "$path" ]]; then
    print_error "is_valid_path: No path specified"
    return 1
  fi
  
  if [[ -e "$path" ]]; then
    return 0
  else
    return 1
  fi
}

# Convert Java package notation to directory path
# Usage: path=$(path_for_package "org.example.package")
# Returns: Directory path (e.g., "org/example/package")
function path_for_package() {
  local package="$1"
  
  if [[ -z "$package" ]]; then
    print_error "path_for_package: No package specified"
    return 1
  fi
  
  # Replace dots with slashes
  echo "${package//./\/}"
}

# Find Maven's location of a Java package
# Usage: dir=$(find_package_directory "/project/root" "org.example.package")
# Returns: Full path to package directory or empty string if not found
function find_package_directory() {
  local base_dir="$1"
  local package="$2"
  
  if [[ -z "$base_dir" || -z "$package" ]]; then
    print_error "find_package_directory: Missing required arguments"
    return 1
  fi
  
  if [[ ! -d "$base_dir" ]]; then
    print_error "find_package_directory: Base directory does not exist: $base_dir"
    return 1
  fi
  
  local package_path=$(path_for_package "$package")
  
  # Try to find the package directory in any of Maven's standard source directories
  for src_dir in "src/main/java" "src/test/java" "src/main/resources" "src/test/resources"; do
    local potential_path="${base_dir}/${src_dir}/${package_path}"
    if [[ -d "$potential_path" ]]; then
      echo "$potential_path"
      return 0
    fi
  done
  
  print_debug "Package directory not found: $package"
  return 1
}

# Get the absolute path of a file or directory
# Usage: abs_path=$(get_absolute_path "relative/path")
# Returns: Absolute path
function get_absolute_path() {
  local path="$1"
  
  if [[ -z "$path" ]]; then
    print_error "get_absolute_path: No path specified"
    return 1
  fi
  
  if [[ -d "$path" ]]; then
    # For directories
    (cd "$path" && pwd)
  elif [[ -f "$path" ]]; then
    # For files
    local dir=$(dirname "$path")
    local filename=$(basename "$path")
    echo "$(cd "$dir" && pwd)/$filename"
  else
    # Path doesn't exist
    print_error "get_absolute_path: Path does not exist: $path"
    return 1
  fi
}

#------------------------------------------------------------------------------
# Maven Functions
#------------------------------------------------------------------------------
# Functions for interacting with Maven projects and POM files
#
# USAGE EXAMPLES:
#   version=$(get_maven_property "/path/to/pom.xml" "project.version" "1.0.0")
#   run_maven_command "${PROJECT_ROOT}" clean test
#------------------------------------------------------------------------------

# Extract a property value from a Maven POM file
# Usage: get_maven_property "/path/to/pom.xml" "propertyName" ["defaultValue"]
# Returns: The property value or default value if not found
function get_maven_property() {
  local pom_file="$1"
  local property_name="$2"
  local default_value="${3:-}"
  
  if [[ ! -f "$pom_file" ]]; then
    print_error "get_maven_property: POM file not found: $pom_file"
    echo "$default_value"
    return 1
  fi
  
  if [[ -z "$property_name" ]]; then
    print_error "get_maven_property: No property name specified"
    echo "$default_value"
    return 1
  fi
  
  # Try extracting from properties section first
  local prop_value=$(grep -oP "<${property_name}>\K[^<]+" "$pom_file" | head -n 1)
  
  # If not found in properties, try extracting as a direct tag (like version)
  if [[ -z "$prop_value" ]]; then
    prop_value=$(grep -oP "<${property_name}>\K[^<]+" "$pom_file" | head -n 1)
  fi
  
  if [[ -n "$prop_value" ]]; then
    echo "$prop_value"
    return 0
  else
    print_debug "Property not found in POM: $property_name, using default: $default_value"
    echo "$default_value"
    return 1
  fi
}

# Run a Maven command with proper error handling
# Usage: run_maven_command [module_path] <maven_args...>
# Returns: Maven's exit code
function run_maven_command() {
  local module_path="${1:-${SAMSTRAUMR_CORE_MODULE}}"
  shift
  
  if [[ -z "$module_path" ]]; then
    print_error "run_maven_command: No module path specified"
    return 1
  fi
  
  local pom_file="${module_path}/pom.xml"
  if [[ ! -f "$pom_file" ]]; then
    print_error "run_maven_command: POM file not found: $pom_file"
    return 1
  fi
  
  # Set Maven options if not already set
  if [[ -z "$MAVEN_OPTS" ]]; then
    if [[ -n "${SAMSTRAUMR_MEMORY_OPTS}" ]]; then
      export MAVEN_OPTS="${SAMSTRAUMR_MEMORY_OPTS}"
      print_debug "Set MAVEN_OPTS to ${MAVEN_OPTS}"
    fi
  fi
  
  print_info "Running Maven command: mvn -f ${pom_file} $*"
  
  # Run the Maven command
  if [[ "${VERBOSE}" == "true" ]]; then
    mvn -f "${pom_file}" "$@"
  else
    # In non-verbose mode, only show warnings and errors
    mvn -f "${pom_file}" --quiet "$@"
  fi
  
  local exit_code=$?
  if [[ $exit_code -ne 0 ]]; then
    print_error "Maven command failed with exit code $exit_code"
  else
    print_debug "Maven command completed successfully"
  fi
  
  return $exit_code
}

# Check if a Maven profile exists in a POM file
# Usage: has_maven_profile "/path/to/pom.xml" "profile-id"
# Returns: 0 if profile exists, 1 otherwise
function has_maven_profile() {
  local pom_file="$1"
  local profile_id="$2"
  
  if [[ ! -f "$pom_file" ]]; then
    print_error "has_maven_profile: POM file not found: $pom_file"
    return 1
  fi
  
  if [[ -z "$profile_id" ]]; then
    print_error "has_maven_profile: No profile ID specified"
    return 1
  fi
  
  # Look for profile ID in the POM file
  if grep -q "<id>${profile_id}</id>" "$pom_file"; then
    return 0
  else
    return 1
  fi
}

#------------------------------------------------------------------------------
# String Functions
#------------------------------------------------------------------------------
# Helper functions for string manipulation and formatting
#
# USAGE EXAMPLES:
#   lowercase=$(to_lowercase "HELLO")  # hello
#   trimmed=$(trim_string "  text  ")  # "text"
#------------------------------------------------------------------------------

# Convert a string to lowercase
# Usage: lowercase=$(to_lowercase "UPPERCASE")
# Returns: Lowercase string
function to_lowercase() {
  local string="${1:-}"
  
  if [[ -z "$string" ]]; then
    return 0
  fi
  
  echo "$string" | tr '[:upper:]' '[:lower:]'
}

# Convert a string to uppercase
# Usage: uppercase=$(to_uppercase "lowercase")
# Returns: Uppercase string
function to_uppercase() {
  local string="${1:-}"
  
  if [[ -z "$string" ]]; then
    return 0
  fi
  
  echo "$string" | tr '[:lower:]' '[:upper:]'
}

# Trim whitespace from beginning and end of string
# Usage: trimmed=$(trim_string "  text with spaces  ")
# Returns: Trimmed string
function trim_string() {
  local string="${1:-}"
  
  if [[ -z "$string" ]]; then
    return 0
  fi
  
  echo "$string" | sed -e 's/^[[:space:]]*//' -e 's/[[:space:]]*$//'
}

# Check if string contains a substring
# Usage: if string_contains "main string" "substring"; then ...
# Returns: 0 if substring is found, 1 otherwise
function string_contains() {
  local string="$1"
  local substring="$2"
  
  if [[ -z "$string" ]]; then
    return 1
  fi
  
  if [[ "$string" == *"$substring"* ]]; then
    return 0
  else
    return 1
  fi
}

# Format a string with ANSI color codes
# Usage: colored_text=$(color_text "text" "red")
# Supports: black, red, green, yellow, blue, magenta, cyan, white, bold, underline, reset
function color_text() {
  local text="$1"
  local color="$2"
  
  if [[ "$USE_COLOR" != "true" ]]; then
    echo "$text"
    return 0
  fi
  
  case "$color" in
    black)     echo -e "\033[0;30m${text}\033[0m" ;;
    red)       echo -e "\033[0;31m${text}\033[0m" ;;
    green)     echo -e "\033[0;32m${text}\033[0m" ;;
    yellow)    echo -e "\033[0;33m${text}\033[0m" ;;
    blue)      echo -e "\033[0;34m${text}\033[0m" ;;
    magenta)   echo -e "\033[0;35m${text}\033[0m" ;;
    cyan)      echo -e "\033[0;36m${text}\033[0m" ;;
    white)     echo -e "\033[0;37m${text}\033[0m" ;;
    bold)      echo -e "\033[1m${text}\033[0m" ;;
    underline) echo -e "\033[4m${text}\033[0m" ;;
    reset)     echo -e "\033[0m${text}" ;;
    *)         echo "$text" ;;
  esac
}

#------------------------------------------------------------------------------
# Validation Functions
#------------------------------------------------------------------------------
# Helper functions for validating inputs and data formats
#
# USAGE EXAMPLES:
#   validate_not_empty "$input" "Username"
#   validate_is_number "$count" "Item count"
#------------------------------------------------------------------------------

# Check if a value is not empty
# Usage: validate_not_empty "$value" "Parameter name"
# Returns: 0 if value is not empty, 1 otherwise
function validate_not_empty() {
  local value="$1"
  local name="${2:-Value}"
  
  if [[ -z "$value" ]]; then
    print_error "$name cannot be empty"
    return 1
  fi
  
  return 0
}

# Check if a value is a valid number
# Usage: validate_is_number "$value" "Parameter name"
# Returns: 0 if value is a number, 1 otherwise
function validate_is_number() {
  local value="$1"
  local name="${2:-Value}"
  
  if ! [[ "$value" =~ ^[0-9]+$ ]]; then
    print_error "$name must be a number"
    return 1
  fi
  
  return 0
}

# Check if a value is in a list of valid options
# Usage: validate_in_list "$value" "Parameter name" "option1" "option2" ...
# Returns: 0 if value is in list, 1 otherwise
function validate_in_list() {
  local value="$1"
  local name="$2"
  shift 2
  
  if [[ $# -eq 0 ]]; then
    print_error "No valid options provided for $name"
    return 1
  fi
  
  local valid=false
  for option in "$@"; do
    if [[ "$value" == "$option" ]]; then
      valid=true
      break
    fi
  done
  
  if [[ "$valid" == "false" ]]; then
    print_error "$name must be one of: $*"
    return 1
  fi
  
  return 0
}

# Validate a semantic version string (x.y.z)
# Usage: validate_semantic_version "$version"
# Returns: 0 if valid, 1 otherwise
function validate_semantic_version() {
  local version="$1"
  
  if ! [[ "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    print_error "Invalid semantic version: $version (must be in format x.y.z)"
    return 1
  fi
  
  return 0
}

#------------------------------------------------------------------------------
# System Functions
#------------------------------------------------------------------------------
# Functions for detecting and working with the operating system environment
#
# USAGE EXAMPLES:
#   if is_linux; then ... fi
#   cpu_cores=$(get_cpu_count)
#------------------------------------------------------------------------------

# Get the operating system type
# Usage: os_type=$(get_os_type)
# Returns: Operating system identifier (Linux, Darwin, etc.)
function get_os_type() {
  uname -s
}

# Check if running on Linux
# Usage: if is_linux; then ... fi
# Returns: 0 if Linux, 1 otherwise
function is_linux() {
  [[ "$(get_os_type)" == "Linux" ]]
}

# Check if running on macOS
# Usage: if is_mac; then ... fi
# Returns: 0 if macOS, 1 otherwise
function is_mac() {
  [[ "$(get_os_type)" == "Darwin" ]]
}

# Get the number of CPU cores
# Usage: cores=$(get_cpu_count)
# Returns: Number of CPU cores
function get_cpu_count() {
  if is_linux; then
    grep -c processor /proc/cpuinfo
  elif is_mac; then
    sysctl -n hw.ncpu
  else
    # Default to 1 for unknown systems
    echo "1" 
  fi
}

# Check if a command exists
# Usage: if command_exists "git"; then ... fi
# Returns: 0 if command exists, 1 otherwise
function command_exists() {
  local cmd="$1"
  
  if [[ -z "$cmd" ]]; then
    return 1
  fi
  
  if command -v "$cmd" &>/dev/null; then
    return 0
  else
    return 1
  fi
}

# Get current available memory in KB
# Usage: mem=$(get_available_memory)
# Returns: Available memory in KB or "unknown"
function get_available_memory() {
  if is_linux; then
    # On Linux, use free
    if command_exists "free"; then
      free -k | grep -i mem | awk '{print $7}'
    else
      echo "unknown"
    fi
  elif is_mac; then
    # On macOS, parse vm_stat
    if command_exists "vm_stat"; then
      local page_size=$(sysctl -n hw.pagesize)
      local free_pages=$(vm_stat | grep "Pages free" | awk '{print $3}' | tr -d '.')
      echo $((free_pages * page_size / 1024))
    else
      echo "unknown"
    fi
  else
    echo "unknown"
  fi
}

#------------------------------------------------------------------------------
# Build and Test Functions
#------------------------------------------------------------------------------
# Functions for building and testing projects consistently across scripts
#
# USAGE EXAMPLES:
#   maven_cmd=$(build_maven_command "test" "atl-tests" true false)
#   run_test_with_status "Unit Tests" "$maven_cmd"
#   parse_args config "$@"
#------------------------------------------------------------------------------

# Standardized argument parsing using associative arrays
# Usage: parse_args config_array "$@"
# The config_array should be a pre-initialized associative array with defaults
# Example:
#   declare -A config=([verbose]=false [clean]=false [profile]="")
#   parse_args config "$@"
function parse_args() {
  local -n args_ref=$1  # Reference to an associative array
  shift

  while [[ $# -gt 0 ]]; do
    case "$1" in
      -h|--help)
        args_ref[help]=true
        shift
        ;;
      -v|--verbose)
        args_ref[verbose]=true
        shift
        ;;
      -c|--clean)
        args_ref[clean]=true
        shift
        ;;
      -p|--profile)
        if [[ -n "$2" && "$2" != -* ]]; then
          args_ref[profile]="$2"
          shift 2
        else
          print_error "Error: --profile requires an argument"
          args_ref[help]=true
          return 1
        fi
        ;;
      --skip-quality)
        args_ref[skip_quality]=true
        shift
        ;;
      --cyclename)
        if [[ -n "$2" && "$2" != -* ]]; then
          args_ref[cyclename]="$2"
          shift 2
        else
          print_error "Error: --cyclename requires an argument"
          args_ref[help]=true
          return 1
        fi
        ;;
      -o|--output)
        if [[ -n "$2" && "$2" != -* ]]; then
          args_ref[output]="$2"
          shift 2
        else
          print_error "Error: --output requires an argument"
          args_ref[help]=true
          return 1
        fi
        ;;
      --)
        # Stop processing options after --
        shift
        args_ref[positional]="${args_ref[positional]} $*"
        break
        ;;
      -*)
        print_error "Unknown option: $1"
        args_ref[help]=true
        return 1
        ;;
      *)
        # Positional arguments
        args_ref[positional]="${args_ref[positional]} $1"
        shift
        ;;
    esac
  done
  
  # Trim leading space from positional args
  args_ref[positional]="${args_ref[positional]# }"
  
  return 0
}

# Build a standardized Maven command with consistent options
# Usage: cmd=$(build_maven_command "goal" "profile" clean skip_quality)
# Returns: Properly formatted Maven command string
function build_maven_command() {
  local goal="$1"
  local profile="$2"
  local clean="${3:-false}"
  local skip_quality="${4:-false}"
  
  local cmd="mvn"
  
  # Add clean goal if requested
  if [[ "$clean" == "true" ]]; then
    cmd="$cmd clean"
  fi
  
  # Add the main goal
  cmd="$cmd $goal"
  
  # Add profile handling
  if [[ -n "$profile" ]]; then
    # Explicitly deactivate the fast profile which skips tests,
    # and enable the requested profile, with skipTests=false
    cmd="$cmd -P !fast,$profile -DskipTests=false -Dmaven.test.skip=false"
  fi
  
  # Add skip quality flag if requested
  if [[ "$skip_quality" == "true" ]]; then
    cmd="$cmd -P skip-quality-checks"
  fi
  
  echo "$cmd"
}

# Run a test with status reporting
# Usage: run_test_with_status "Test name" "maven command" [verbose] [output_file]
# Returns: 0 on success, non-zero on failure
function run_test_with_status() {
  local test_name="$1"
  local maven_cmd="$2"
  local verbose="${3:-false}"
  local output_file="${4:-}"
  local cyclename="${5:-Tests}"
  
  local timestamp
  timestamp=$(date +"%H:%M:%S")
  
  # No run status
  print_test_status "$cyclename" "$test_name" "no run"
  
  # Running status
  print_test_status "$cyclename" "$test_name" "running"
  
  local result=0
  local output
  
  if [[ -n "$output_file" ]]; then
    # Run with output to file
    print_debug "Executing: $maven_cmd > $output_file 2>&1"
    eval "$maven_cmd" > "$output_file" 2>&1 || result=$?
  else
    # Run with normal output
    print_debug "Executing: $maven_cmd"
    eval "$maven_cmd" || result=$?
  fi
  
  # Final status based on result
  if [[ $result -eq 0 ]]; then
    print_test_status "$cyclename" "$test_name" "pass"
  else
    print_test_status "$cyclename" "$test_name" "fail"
  fi
  
  return $result
}

# Print test status with consistent formatting
# Usage: print_test_status "cycle_name" "test_name" "status"
# Status can be: no run, running, pass, fail, skip
function print_test_status() {
  local cycle_name="$1"
  local test_name="$2"
  local status="$3"
  local timestamp
  timestamp=$(date +"%H:%M:%S")
  
  case "$status" in
    "no run")
      local status_color=""
      local status_text="no run"
      if [[ "$USE_COLOR" == "true" ]]; then
        status_color="${COLOR_RESET}"
        status_text="\033[0;37mno run\033[0m"
      fi
      ;;
    "running")
      local status_color=""
      local status_text="running"
      if [[ "$USE_COLOR" == "true" ]]; then
        status_color="${COLOR_BLUE}"
        status_text="\033[0;34mrunning\033[0m"
      fi
      ;;
    "pass")
      local status_color=""
      local status_text="pass"
      if [[ "$USE_COLOR" == "true" ]]; then
        status_color="${COLOR_GREEN}"
        status_text="\033[0;32mpass\033[0m"
      fi
      ;;
    "fail")
      local status_color=""
      local status_text="fail"
      if [[ "$USE_COLOR" == "true" ]]; then
        status_color="${COLOR_RED}"
        status_text="\033[0;31mfail\033[0m"
      fi
      ;;
    "skip")
      local status_color=""
      local status_text="skip"
      if [[ "$USE_COLOR" == "true" ]]; then
        status_color="${COLOR_YELLOW}"
        status_text="\033[0;33mskip\033[0m"
      fi
      ;;
    *)
      local status_color=""
      local status_text="$status"
      ;;
  esac
  
  if [[ "$USE_COLOR" == "true" ]]; then
    echo -e "[$timestamp] ${COLOR_CYAN}${cycle_name}${COLOR_RESET} - ${COLOR_BOLD}$test_name${COLOR_RESET}: $status_text"
  else
    echo "[$timestamp] $cycle_name - $test_name: $status"
  fi
}

# Standard script initialization
# Usage: initialize_script [script_name]
# Returns: 0 on success
function initialize_script() {
  local script_name="${1:-Script}"
  
  # Set strict mode
  set -e
  
  # Determine paths
  SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"  # Adjust as needed
  
  # Source configuration
  if [[ -f "${PROJECT_ROOT}/.samstraumr.config" ]]; then
    source "${PROJECT_ROOT}/.samstraumr.config"
  else
    echo "Warning: Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  fi
  
  # Set default options
  VERBOSE=false
  
  print_debug "$script_name initialized"
  return 0
}

#------------------------------------------------------------------------------
# Git Functions
#------------------------------------------------------------------------------
# Functions for interacting with Git repositories
#
# USAGE EXAMPLES:
#   branch=$(get_git_branch)
#   if is_git_repo; then ... fi
#   if has_git_changes; then echo "Uncommitted changes"; fi
#------------------------------------------------------------------------------

# Get the current Git branch name
# Usage: branch=$(get_git_branch)
# Returns: Current branch name or "unknown" if not in a git repo
function get_git_branch() {
  if ! command_exists "git"; then
    print_error "Git is not installed"
    echo "unknown"
    return 1
  fi
  
  git rev-parse --abbrev-ref HEAD 2>/dev/null || echo "unknown"
}

# Check if the current directory is in a Git repository
# Usage: if is_git_repo; then ... fi
# Returns: 0 if in a git repo, 1 otherwise
function is_git_repo() {
  if ! command_exists "git"; then
    print_error "Git is not installed"
    return 1
  fi
  
  git rev-parse --is-inside-work-tree &>/dev/null
  return $?
}

# Check if the Git repository has uncommitted changes
# Usage: if has_git_changes; then ... fi
# Returns: 0 if there are changes, 1 otherwise
function has_git_changes() {
  if ! command_exists "git"; then
    print_error "Git is not installed"
    return 1
  fi
  
  if ! is_git_repo; then
    print_debug "Not in a git repository"
    return 1
  fi
  
  # Check for staged or unstaged changes
  if ! git diff-index --quiet HEAD --; then
    return 0
  else
    return 1
  fi
}

# Get the last Git commit message
# Usage: message=$(get_last_commit_message)
# Returns: The last commit message or empty if error
function get_last_commit_message() {
  if ! command_exists "git"; then
    print_error "Git is not installed"
    return 1
  fi
  
  if ! is_git_repo; then
    print_debug "Not in a git repository"
    return 1
  fi
  
  git log -1 --pretty=%B 2>/dev/null || echo ""
}

# Get the Git root directory
# Usage: root=$(get_git_root)
# Returns: The root directory of the git repo or empty if error
function get_git_root() {
  if ! command_exists "git"; then
    print_error "Git is not installed"
    return 1
  fi
  
  if ! is_git_repo; then
    print_debug "Not in a git repository"
    return 1
  fi
  
  git rev-parse --show-toplevel 2>/dev/null || echo ""
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------
# Initialization section for the common library
# This exports all functions and performs final setup tasks
#------------------------------------------------------------------------------

# Export all library functions to make them available to sourcing scripts
# Output Functions
export -f print_header
export -f print_section
export -f print_success
export -f print_error
export -f print_warning
export -f print_info
export -f print_debug
export -f print_bold

# Argument Parsing
export -f show_help_template
export -f parse_common_args
export -f validate_required_args

# File & Path Functions
export -f ensure_directory_exists
export -f is_valid_path
export -f path_for_package
export -f find_package_directory
export -f get_absolute_path

# Maven Functions
export -f get_maven_property
export -f run_maven_command
export -f has_maven_profile

# String Functions
export -f to_lowercase
export -f to_uppercase
export -f trim_string
export -f string_contains
export -f color_text

# Validation Functions
export -f validate_not_empty
export -f validate_is_number
export -f validate_in_list
export -f validate_semantic_version

# System Functions
export -f get_os_type
export -f is_linux
export -f is_mac
export -f get_cpu_count
export -f command_exists
export -f get_available_memory

# Build and Test Functions
export -f parse_args
export -f build_maven_command
export -f run_test_with_status
export -f print_test_status
export -f initialize_script

# Git Functions
export -f get_git_branch
export -f is_git_repo
export -f has_git_changes
export -f get_last_commit_message
export -f get_git_root

# Initialize library
function initialize_common_library() {
  # Check for required binaries
  local missing_tools=false
  local required_tools=("grep" "sed" "tr" "find" "awk")
  
  for tool in "${required_tools[@]}"; do
    if ! command_exists "$tool"; then
      print_error "Required tool not found: $tool"
      missing_tools=true
    fi
  done
  
  if [[ "$missing_tools" == "true" ]]; then
    print_warning "Some required tools are missing. Common library functionality may be limited."
  fi
  
  # Print debug info if verbose
  if [[ "${VERBOSE:-false}" == "true" ]]; then
    print_debug "Loaded common.sh library"
    print_debug "PROJECT_ROOT: $PROJECT_ROOT"
    print_debug "SAMSTRAUMR_CORE_MODULE: $SAMSTRAUMR_CORE_MODULE"
    print_debug "OS type: $(get_os_type)"
    print_debug "CPU cores: $(get_cpu_count)"
  fi
  
  return 0
}

# Run initialization
initialize_common_library