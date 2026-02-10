#!/bin/bash
#==============================================================================
# validate-examples.sh
# Validates that code examples in the documentation actually work
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color
BOLD='\033[1m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
header() { echo -e "\n${BOLD}${YELLOW}$1${RESET}\n"; }

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Create a directory for example validation
WORK_DIR="${PROJECT_ROOT}/target/example-validation"
REPORT_DIR="${WORK_DIR}/reports"
mkdir -p "$REPORT_DIR"
mkdir -p "${WORK_DIR}/src"

# Timestamp for reports
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
REPORT_FILE="${REPORT_DIR}/example-validation-${TIMESTAMP}.md"

# Core APIs that we'll specifically look for examples of
CORE_APIS=(
  "Component"
  "Composite"
  "Machine"
  "Identity"
  "EventDispatcher"
)

# Initialize report
initialize_report() {
  {
    echo "# Documentation Example Validation Report"
    echo "Generated: $(date)"
    echo
    echo "This report checks if code examples in the documentation actually compile and run."
    echo
    echo "## Summary"
    echo
    echo "| Category | Total Examples | Valid | Invalid | Skipped |"
    echo "|----------|----------------|-------|---------|---------|"
  } > "$REPORT_FILE"
}

# Extract Java code blocks from a file
extract_java_examples() {
  local file=$1
  local output_dir=$2
  local file_basename=$(basename "$file" .md)
  local file_dirname=$(dirname "$file")
  local rel_path="${file#${PROJECT_ROOT}/}"
  
  # Extract all Java code blocks from the markdown file
  local java_block_count=0
  local current_block=""
  local in_java_block=false
  local line_num=0
  
  {
    echo "## Examples from $rel_path"
    echo
    echo "| Example | Status | Details |"
    echo "|---------|--------|---------|"
  } >> "$REPORT_FILE"
  
  while IFS= read -r line; do
    ((line_num++))
    
    # Detect start of Java code block
    if [[ "$line" =~ ^[[:space:]]*\`\`\`(java|Java)[[:space:]]*$ ]]; then
      in_java_block=true
      current_block=""
      continue
    fi
    
    # Detect end of code block
    if [[ "$in_java_block" == true && "$line" =~ ^[[:space:]]*\`\`\`[[:space:]]*$ ]]; then
      in_java_block=false
      
      # Generate filename for this example
      ((java_block_count++))
      local example_file="${output_dir}/${file_basename}_example_${java_block_count}.java"
      
      # Skip small snippets (import statements, single lines, etc.)
      local line_count=$(echo "$current_block" | wc -l)
      if [[ $line_count -lt 3 ]]; then
        echo "| Example ${java_block_count} | ⚠️ Skipped | Too small (${line_count} lines) |" >> "$REPORT_FILE"
        continue
      fi
      
      # Skip examples that are just code fragments (no class/method declaration)
      if ! echo "$current_block" | grep -q "class" && ! echo "$current_block" | grep -q "public.*interface"; then
        echo "| Example ${java_block_count} | ⚠️ Skipped | Not a complete class definition |" >> "$REPORT_FILE"
        continue
      fi
      
      # Extract class name from code block
      local class_name=$(echo "$current_block" | grep -o "class\s\+[A-Za-z0-9_]\+" | awk '{print $2}' | head -1)
      if [[ -z "$class_name" ]]; then
        class_name="Example${java_block_count}"
      fi
      
      # Add imports for our core components if not already present
      local updated_block=""
      local has_package=false
      
      # Check if the example has a package declaration
      if echo "$current_block" | grep -q "^[[:space:]]*package[[:space:]]\+"; then
        has_package=true
      else
        # Add a default package declaration if none exists
        updated_block="package org.s8r.examples;\n\n"
      fi
      
      # Add necessary imports
      updated_block+="import org.s8r.component.core.Component;\n"
      updated_block+="import org.s8r.component.composite.Composite;\n"
      updated_block+="import org.s8r.component.machine.Machine;\n"
      updated_block+="import org.s8r.component.identity.Identity;\n"
      updated_block+="import org.s8r.domain.event.DomainEvent;\n"
      updated_block+="import org.s8r.domain.identity.ComponentId;\n"
      updated_block+="import org.s8r.domain.lifecycle.LifecycleState;\n"
      updated_block+="import org.s8r.infrastructure.event.InMemoryEventDispatcher;\n\n"
      
      # Add the actual code block
      updated_block+="$current_block"
      
      # Check if we need to make the class public
      if ! echo "$updated_block" | grep -q "public\s\+class\s\+$class_name"; then
        updated_block=$(echo "$updated_block" | sed "s/class\s\+$class_name/public class $class_name/g")
      fi
      
      # Write the example to a file
      echo -e "$updated_block" > "$example_file"
      
      # Collect referenced APIs
      local api_matches=""
      for api in "${CORE_APIS[@]}"; do
        if grep -q "$api" "$example_file"; then
          api_matches+="$api, "
        fi
      done
      api_matches=${api_matches%, }
      
      # Attempt to compile the example
      if javac -cp "${PROJECT_ROOT}/modules/samstraumr-core/target/classes" "$example_file" 2>/dev/null; then
        echo "| Example ${java_block_count} | ✅ Valid | Uses: ${api_matches:-none} |" >> "$REPORT_FILE"
        ((valid_examples++))
      else
        echo "| Example ${java_block_count} | ❌ Invalid | Compilation failed |" >> "$REPORT_FILE"
        ((invalid_examples++))
      fi
      
      continue
    fi
    
    # Collect lines while in a Java block
    if [[ "$in_java_block" == true ]]; then
      current_block+="$line\n"
    fi
  done < "$file"
  
  # Return the number of examples found
  echo $java_block_count
}

# Main validation function
validate_examples() {
  header "Validating code examples in documentation"
  
  # Find all markdown files with potential examples
  local md_files=$(find "${PROJECT_ROOT}/docs" -type f -name "*.md" | grep -v "CHANGELOG.md")
  
  # Counters for summary
  local total_examples=0
  local total_valid=0
  local total_invalid=0
  local total_skipped=0
  
  # Process each file
  for file in $md_files; do
    # Skip certain files that don't contain examples
    if [[ "$file" =~ README.md$ || "$file" =~ KANBAN.md$ ]]; then
      continue
    fi
    
    local rel_path="${file#${PROJECT_ROOT}/}"
    info "Processing examples in $rel_path"
    
    # Create output directory for this file's examples
    local file_basename=$(basename "$file" .md)
    local output_dir="${WORK_DIR}/src/${file_basename}"
    mkdir -p "$output_dir"
    
    # Process this file
    valid_examples=0
    invalid_examples=0
    skipped_examples=0
    
    # Extract and validate examples
    local file_examples=$(extract_java_examples "$file" "$output_dir")
    
    # Update counters
    total_examples=$((total_examples + file_examples))
    total_valid=$((total_valid + valid_examples))
    total_invalid=$((total_invalid + invalid_examples))
    total_skipped=$((total_skipped + (file_examples - valid_examples - invalid_examples)))
    
    # Summary message for this file
    info "Found $file_examples examples: $valid_examples valid, $invalid_examples invalid, $((file_examples - valid_examples - invalid_examples)) skipped"
  done
  
  # Update summary in report
  {
    echo "| Java Code | $total_examples | $total_valid | $total_invalid | $total_skipped |"
  } >> "$REPORT_FILE"
  
  # Add overall validation result
  {
    echo
    echo "## Overall Result"
    echo
    if [[ $total_invalid -eq 0 ]]; then
      echo "✅ **PASSED** - All non-skipped examples are valid."
    else
      echo "❌ **FAILED** - $total_invalid examples are invalid and need to be fixed."
    fi
    echo
    echo "### Notes"
    echo
    echo "- Valid: Example compiles successfully."
    echo "- Invalid: Example has compilation errors."
    echo "- Skipped: Example is too small, incomplete, or not meant to be compilable."
  } >> "$REPORT_FILE"
  
  # Print final summary
  if [[ $total_invalid -eq 0 ]]; then
    success "Example validation passed: All $total_valid examples are valid (skipped $total_skipped)"
    return 0
  else
    error "Example validation failed: $total_invalid invalid examples found"
    info "See detailed report at: $REPORT_FILE"
    return 1
  fi
}

# Main function
main() {
  header "Documentation Example Validation"
  
  # Check for Java compiler
  if ! command -v javac &> /dev/null; then
    error "Java compiler (javac) not found. Cannot validate examples."
    exit 1
  fi
  
  # Initialize the report
  initialize_report
  
  # Run the validation
  validate_examples
  local validation_status=$?
  
  if [ "$validation_status" -eq 0 ]; then
    success "All examples are valid"
  else
    error "Some examples are invalid"
  fi
  
  return $validation_status
}

# Run the main function
main "$@"