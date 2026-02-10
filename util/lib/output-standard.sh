#!/usr/bin/env bash
#==============================================================================
# Filename: output-standard.sh
# Description: Standardized output functions for Samstraumr build and test scripts
#
# This library provides consistent output formatting functions for all Samstraumr
# build, test, and utility scripts. It ensures that all scripts present information
# in a consistent, user-friendly way.
#==============================================================================

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
BOLD='\033[1m'
RESET='\033[0m'

# Set default verbosity level
VERBOSE=${VERBOSE:-false}

#------------------------------------------------------------------------------
# Output Formatting Functions
#------------------------------------------------------------------------------

# Print a major section header
# Usage: print_header "Section Title"
print_header() {
  local title="$1"
  local width=80
  local line=$(printf '%0.s=' $(seq 1 $width))
  
  echo -e "\n${BLUE}${BOLD}${line}${RESET}"
  echo -e "${BLUE}${BOLD}  ${title}${RESET}"
  echo -e "${BLUE}${BOLD}${line}${RESET}\n"
}

# Print a subsection header
# Usage: print_section "Subsection Title"
print_section() {
  echo -e "\n${BLUE}${BOLD}$1${RESET}"
  echo -e "${BLUE}${BOLD}$(printf '%0.s-' $(seq 1 ${#1}))${RESET}"
}

# Print an informational message (Going to do)
# Usage: print_info "About to perform operation X"
print_info() {
  echo -e "${BLUE}→ $1${RESET}"
}

# Print a success message (Done)
# Usage: print_success "Operation X completed successfully"
print_success() {
  echo -e "${GREEN}✓ $1${RESET}"
}

# Print a warning message
# Usage: print_warning "Warning: Potential issue detected"
print_warning() {
  echo -e "${YELLOW}! $1${RESET}" >&2
}

# Print an error message and exit with error code
# Usage: print_error "Error: Operation failed"
print_error() {
  echo -e "${RED}✗ $1${RESET}" >&2
  exit 1
}

# Print a debug message (only shown when VERBOSE=true)
# Usage: print_debug "Debug information"
print_debug() {
  if [[ "$VERBOSE" == "true" ]]; then
    echo -e "${CYAN}DEBUG: $1${RESET}" >&2
  fi
}

# Print timing information
# Usage: print_timing $duration
print_timing() {
  local duration=$1
  if [ $duration -gt 60 ]; then
    local minutes=$((duration / 60))
    local seconds=$((duration % 60))
    echo -e "${YELLOW}Execution time: ${minutes}m ${seconds}s${RESET}"
  else
    echo -e "${YELLOW}Execution time: ${duration}s${RESET}"
  fi
}

# Execute command with timing information
# Usage: time_exec command arg1 arg2...
time_exec() {
  local start_time=$(date +%s)
  "$@"
  local exit_code=$?
  local end_time=$(date +%s)
  local duration=$((end_time - start_time))
  
  print_timing $duration
  
  return $exit_code
}

# Print progress percentage
# Usage: print_progress $current $total
print_progress() {
  local current=$1
  local total=$2
  local percent=$((current * 100 / total))
  local width=50
  local filled=$((width * current / total))
  local empty=$((width - filled))
  
  printf "${BLUE}[${GREEN}"
  printf "%0.s=" $(seq 1 $filled)
  printf "${BLUE}"
  printf "%0.s-" $(seq 1 $empty)
  printf "${BLUE}] ${GREEN}%3d%%${RESET}\r" $percent
  
  if [ $current -eq $total ]; then
    echo
  fi
}

# Print a task with status
# Usage: print_task "Task description" "status"
# Status can be: "success", "warning", "error", "info"
print_task() {
  local description="$1"
  local status="$2"
  local max_width=60
  
  # Truncate description if too long
  if [ ${#description} -gt $max_width ]; then
    description="${description:0:$max_width}..."
  fi
  
  # Pad with dots
  local padding=$((70 - ${#description}))
  local dots=$(printf '%0.s.' $(seq 1 $padding))
  
  printf "${description} ${dots} "
  
  case "$status" in
    success)
      echo -e "${GREEN}[SUCCESS]${RESET}"
      ;;
    warning)
      echo -e "${YELLOW}[WARNING]${RESET}"
      ;;
    error)
      echo -e "${RED}[FAILED]${RESET}"
      ;;
    info)
      echo -e "${BLUE}[INFO]${RESET}"
      ;;
    *)
      echo -e "[${status}]"
      ;;
  esac
}

# Setup Maven options with consistent memory settings
# Usage: setup_maven_opts
setup_maven_opts() {
  MVN_OPTS=""
  if [ -n "$MAVEN_MEMORY_OPTS" ]; then
    MVN_OPTS="$MAVEN_MEMORY_OPTS"
  else
    MVN_OPTS="-Xmx1g"
  fi
  export MAVEN_OPTS="$MVN_OPTS"
  
  print_debug "Maven options set to: $MAVEN_OPTS"
}

# Create directory if it doesn't exist
# Usage: ensure_directory_exists "/path/to/directory"
ensure_directory_exists() {
  local dir="$1"
  if [[ ! -d "$dir" ]]; then
    mkdir -p "$dir"
    print_debug "Created directory: $dir"
  fi
}

# Test if this script is being sourced or executed directly
# If executed directly, show a usage example
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
  print_header "Samstraumr Output Standard"
  print_info "This script should be sourced, not executed directly"
  print_section "Usage Example"
  echo "source $(basename ${BASH_SOURCE[0]})"
  echo
  print_section "Example Output"
  print_info "This is an info message (going to do something)"
  print_success "This is a success message (done something)"
  print_warning "This is a warning message"
  print_error "This is an error message (script will exit)"
fi