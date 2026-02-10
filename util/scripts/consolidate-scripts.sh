#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# Script to consolidate duplicate scripts in the Samstraumr repository
# This helps clean up the repository by reducing redundancy
#==============================================================================
set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; exit 1; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
heading() { echo -e "${CYAN}${BOLD}$1${RESET}"; }

# Project root directory
PROJECT_ROOT="$(git rev-parse --show-toplevel)"
if [ -z "$PROJECT_ROOT" ]; then
  error "Not in a git repository"
fi

# Script directories to analyze
SCRIPT_DIRS=(
  "${PROJECT_ROOT}/util/scripts"
  "${PROJECT_ROOT}/util/bin"
  "${PROJECT_ROOT}/docs/scripts"
  "${PROJECT_ROOT}/docs/tools"
  "${PROJECT_ROOT}"
)

# Function to find similar scripts
find_similar_scripts() {
  local function_name="$1"
  local script_paths=()
  local script_list=""
  
  # Find scripts that contain the function name
  for dir in "${SCRIPT_DIRS[@]}"; do
    if [ -d "$dir" ]; then
      while IFS= read -r file; do
        if grep -q "$function_name" "$file"; then
          script_paths+=("$file")
          script_list+="$(basename "$file")\n"
        fi
      done < <(find "$dir" -name "*.sh" -type f)
    fi
  done
  
  # If we found more than one script with this function
  if [ ${#script_paths[@]} -gt 1 ]; then
    echo -e "${YELLOW}Function '${BOLD}$function_name${RESET}${YELLOW}' found in multiple scripts:${RESET}"
    for path in "${script_paths[@]}"; do
      echo "  - ${BLUE}$(realpath --relative-to="$PROJECT_ROOT" "$path")${RESET}"
    done
    echo
  fi
}

# Function to find duplicated header scripts
find_header_scripts() {
  local header_scripts=()
  local pattern="header"
  
  # Find header-related scripts
  for dir in "${SCRIPT_DIRS[@]}"; do
    if [ -d "$dir" ]; then
      while IFS= read -r file; do
        header_scripts+=("$file")
      done < <(find "$dir" -name "*header*.sh" -type f)
    fi
  done
  
  # If we found header scripts
  if [ ${#header_scripts[@]} -gt 0 ]; then
    echo -e "${YELLOW}Header-related scripts found:${RESET}"
    for path in "${header_scripts[@]}"; do
      echo "  - ${BLUE}$(realpath --relative-to="$PROJECT_ROOT" "$path")${RESET}"
    done
    echo
  fi
}

# Function to find test scripts
find_test_scripts() {
  local test_scripts=()
  local pattern="test"
  
  # Find test-related scripts
  for dir in "${SCRIPT_DIRS[@]}"; do
    if [ -d "$dir" ]; then
      while IFS= read -r file; do
        test_scripts+=("$file")
      done < <(find "$dir" -name "*test*.sh" -type f)
    fi
  done
  
  # If we found test scripts
  if [ ${#test_scripts[@]} -gt 0 ]; then
    echo -e "${YELLOW}Test-related scripts found:${RESET}"
    for path in "${test_scripts[@]}"; do
      echo "  - ${BLUE}$(realpath --relative-to="$PROJECT_ROOT" "$path")${RESET}"
    done
    echo
  fi
}

# Function to consolidate header scripts
consolidate_header_scripts() {
  local target_dir="${PROJECT_ROOT}/util/lib"
  local consolidated_file="${target_dir}/header-lib.sh"
  
  # Ensure target directory exists
  mkdir -p "$target_dir"
  
  # Create consolidated script header
  cat > "$consolidated_file" << 'EOF'
#!/usr/bin/env bash
#==============================================================================
# Header Library - Consolidated header-related functions
# This library contains functions for managing file headers across the codebase
#==============================================================================

# Add a license header to a file
add_license_header() {
  local file="$1"
  local license_template="$2"
  local file_type="${file##*.}"
  
  # Only add if file doesn't already have a header
  if ! grep -q "Copyright" "$file"; then
    case "$file_type" in
      java)
        # Java-style comment header
        sed -i "1s|^|/*\n * $(cat "$license_template")\n */\n\n|" "$file"
        ;;
      sh|bash)
        # Shell-style comment header
        sed -i "1s|^|#\n# $(cat "$license_template")\n#\n\n|" "$file"
        ;;
      md|markdown)
        # Markdown-style comment header
        sed -i "1s|^|<!-- \n$(cat "$license_template")\n-->\n\n|" "$file"
        ;;
      xml|html)
        # XML-style comment header
        sed -i "1s|^|<!--\n$(cat "$license_template")\n-->\n\n|" "$file"
        ;;
      *)
        # Default to shell-style comments
        sed -i "1s|^|#\n# $(cat "$license_template")\n#\n\n|" "$file"
        ;;
    esac
    echo "Added header to $file"
  else
    echo "Header already exists in $file"
  fi
}

# Update headers in Java files
update_java_headers() {
  local directory="$1"
  local header_template="$2"
  
  find "$directory" -name "*.java" -type f | while read -r file; do
    # Check if file has a header
    if grep -q "Copyright" "$file"; then
      # Replace existing header
      sed -i '/\/\*\(/,/\*\//c\/*\n * '"$(cat "$header_template")"'\n */' "$file"
      echo "Updated header in $file"
    else
      # Add new header
      add_license_header "$file" "$header_template"
    fi
  done
}

# Update headers in Markdown files
update_md_headers() {
  local directory="$1"
  local header_template="$2"
  
  find "$directory" -name "*.md" -type f | while read -r file; do
    # Check if file has a header
    if grep -q "Copyright" "$file"; then
      # Replace existing header
      sed -i '/<!--/,/-->/c\<!--\n'"$(cat "$header_template")"'\n-->' "$file"
      echo "Updated header in $file"
    else
      # Add new header
      add_license_header "$file" "$header_template"
    fi
  done
}

# Simplify existing headers that are too verbose
simplify_headers() {
  local directory="$1"
  local new_header="Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7."
  
  # Process different file types
  find "$directory" \( -name "*.java" -o -name "*.md" -o -name "*.sh" \) -type f | while read -r file; do
    if grep -q "Copyright" "$file"; then
      case "${file##*.}" in
        java)
          # Java files
          sed -i '/\/\*\(/,/\*\//c\/*\n * '"$new_header"'\n */' "$file"
          ;;
        md)
          # Markdown files
          sed -i '/<!--/,/-->/c\<!-- \n'"$new_header"'\n-->' "$file"
          ;;
        sh|bash)
          # Shell scripts
          sed -i '1,10s/^# Copyright.*/# '"$new_header"'/' "$file"
          ;;
      esac
      echo "Simplified header in $file"
    fi
  done
}
EOF
  
  # Make script executable
  chmod +x "$consolidated_file"
  success "Created consolidated header library at ${consolidated_file}"
  
  # Create a simplified wrapper script
  local wrapper_script="${PROJECT_ROOT}/util/scripts/update-headers.sh"
  cat > "$wrapper_script" << EOF
#!/usr/bin/env bash
#==============================================================================
# Wrapper script for updating headers across the codebase
# Uses the consolidated header library
#==============================================================================
set -e

# Source the header library
source "\$(git rev-parse --show-toplevel)/util/lib/header-lib.sh"

# Default header template
HEADER_TEMPLATE="\$(git rev-parse --show-toplevel)/util/config/java-standard-header-template.txt"

# Parse arguments
DIRECTORY="\$(git rev-parse --show-toplevel)"
FILE_TYPE="all"

# Parse arguments
while [[ \$# -gt 0 ]]; do
  case "\$1" in
    --dir)
      DIRECTORY="\$2"
      shift 2
      ;;
    --type)
      FILE_TYPE="\$2"
      shift 2
      ;;
    --template)
      HEADER_TEMPLATE="\$2"
      shift 2
      ;;
    --help)
      echo "Usage: \$(basename "\$0") [--dir DIRECTORY] [--type java|md|all] [--template TEMPLATE_FILE]"
      exit 0
      ;;
    *)
      echo "Unknown argument: \$1"
      exit 1
      ;;
  esac
done

# Ensure directory exists
if [ ! -d "\$DIRECTORY" ]; then
  echo "Error: Directory \$DIRECTORY does not exist"
  exit 1
fi

# Ensure template file exists
if [ ! -f "\$HEADER_TEMPLATE" ]; then
  echo "Error: Template file \$HEADER_TEMPLATE does not exist"
  exit 1
fi

# Update headers based on file type
case "\$FILE_TYPE" in
  java)
    echo "Updating Java headers in \$DIRECTORY"
    update_java_headers "\$DIRECTORY" "\$HEADER_TEMPLATE"
    ;;
  md)
    echo "Updating Markdown headers in \$DIRECTORY"
    update_md_headers "\$DIRECTORY" "\$HEADER_TEMPLATE"
    ;;
  all)
    echo "Updating all headers in \$DIRECTORY"
    update_java_headers "\$DIRECTORY" "\$HEADER_TEMPLATE"
    update_md_headers "\$DIRECTORY" "\$HEADER_TEMPLATE"
    ;;
  *)
    echo "Error: Unknown file type \$FILE_TYPE. Use java, md, or all."
    exit 1
    ;;
esac

echo "Header updates completed successfully"
EOF
  
  # Make wrapper script executable
  chmod +x "$wrapper_script"
  success "Created wrapper script at ${wrapper_script}"
}

# Function to consolidate test scripts
consolidate_test_scripts() {
  local target_dir="${PROJECT_ROOT}/util/lib"
  local consolidated_file="${target_dir}/test-lib.sh"
  
  # Create consolidated script
  cat > "$consolidated_file" << 'EOF'
#!/usr/bin/env bash
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
EOF
  
  # Make script executable
  chmod +x "$consolidated_file"
  success "Created consolidated test library at ${consolidated_file}"
}

# Main function
main() {
  heading "Samstraumr Script Consolidation Tool"
  echo "This tool helps identify and consolidate duplicate scripts"
  echo "======================================================="
  
  # Find similar scripts by common functions
  heading "Analyzing function duplication..."
  find_similar_scripts "add_header"
  find_similar_scripts "update_java"
  find_similar_scripts "update_md"
  find_similar_scripts "run_test"
  find_similar_scripts "map_test_type"
  
  # Find header scripts
  heading "Analyzing header-related scripts..."
  find_header_scripts
  
  # Find test scripts
  heading "Analyzing test-related scripts..."
  find_test_scripts
  
  # Ask for confirmation before consolidating
  echo
  echo -e "${YELLOW}Do you want to consolidate duplicate scripts? This will create:${RESET}"
  echo "  1. A consolidated header-lib.sh in util/lib"
  echo "  2. A consolidated test-lib.sh in util/lib"
  echo "  3. Wrapper scripts that use the consolidated libraries"
  echo
  echo -e "${YELLOW}Note: This will not delete any existing scripts.${RESET}"
  echo
  read -p "Proceed with consolidation? (y/n): " confirm
  
  if [ "$confirm" = "y" ] || [ "$confirm" = "Y" ]; then
    heading "Consolidating header scripts..."
    consolidate_header_scripts
    
    heading "Consolidating test scripts..."
    consolidate_test_scripts
    
    echo
    success "Script consolidation completed successfully!"
    echo "You can now update your scripts to use the consolidated libraries."
    echo "Example: source \"\$(git rev-parse --show-toplevel)/util/lib/header-lib.sh\""
    echo "Example: source \"\$(git rev-parse --show-toplevel)/util/lib/test-lib.sh\""
  else
    echo "Consolidation cancelled."
  fi
}

# Execute main function
main "$@"