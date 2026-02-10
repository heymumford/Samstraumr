#!/bin/bash
#==============================================================================
# Filename: quality-lib.sh
# Description: Shared functions for quality scripts
#==============================================================================

# Determine script paths and load common library
SCRIPT_LIB_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_LIB_DIR}/../.." && pwd)"

# Source the common library
source "${SCRIPT_LIB_DIR}/common.sh"

#------------------------------------------------------------------------------
# Quality Check Functions
#------------------------------------------------------------------------------

function run_spotless_check() {
  local fix_issues="${1:-false}"
  
  print_section "Running Spotless Code Formatting Check"
  
  local spotless_cmd="mvn -f \"${SAMSTRAUMR_CORE_MODULE}/pom.xml\" spotless:"
  if [ "$fix_issues" = "true" ]; then
    spotless_cmd="${spotless_cmd}apply"
    print_info "Applying Spotless formatting..."
  else
    spotless_cmd="${spotless_cmd}check"
    print_info "Checking Spotless formatting..."
  fi
  
  if eval "$spotless_cmd"; then
    print_success "Spotless check passed"
    return 0
  else
    if [ "$fix_issues" = "true" ]; then
      print_error "Spotless failed to apply formatting to some files"
    else
      print_error "Spotless check failed - run with --fix to apply formatting"
    fi
    return 1
  fi
}

function run_checkstyle() {
  local fix_issues="${1:-false}"
  
  print_section "Running CheckStyle Code Style Check"
  
  local checkstyle_cmd="mvn -f \"${SAMSTRAUMR_CORE_MODULE}/pom.xml\" checkstyle:check"
  print_info "Checking code style with CheckStyle..."
  
  if eval "$checkstyle_cmd"; then
    print_success "CheckStyle check passed"
    return 0
  else
    print_error "CheckStyle check failed"
    return 1
  fi
}

function run_spotbugs() {
  print_section "Running SpotBugs Bug Detection"
  
  local spotbugs_cmd="mvn -f \"${SAMSTRAUMR_CORE_MODULE}/pom.xml\" spotbugs:check"
  print_info "Checking for bugs with SpotBugs..."
  
  if eval "$spotbugs_cmd"; then
    print_success "SpotBugs check passed"
    return 0
  else
    print_error "SpotBugs check failed"
    return 1
  fi
}

function run_jacoco_coverage() {
  print_section "Running JaCoCo Code Coverage Analysis"
  
  # First run tests to generate coverage data
  print_info "Running tests to generate coverage data..."
  local test_cmd="mvn -f \"${SAMSTRAUMR_CORE_MODULE}/pom.xml\" test"
  
  if ! eval "$test_cmd"; then
    print_error "Tests failed - cannot generate coverage report"
    return 1
  fi
  
  # Then generate the report
  print_info "Generating coverage report..."
  local jacoco_cmd="mvn -f \"${SAMSTRAUMR_CORE_MODULE}/pom.xml\" jacoco:report"
  
  if eval "$jacoco_cmd"; then
    print_success "JaCoCo coverage report generated"
    print_info "Report available at: ${SAMSTRAUMR_JACOCO_REPORT}"
    return 0
  else
    print_error "JaCoCo report generation failed"
    return 1
  fi
}

function check_file_encoding() {
  local path="${1:-${PROJECT_ROOT}}"
  local verbose="${2:-false}"
  local fix_issues="${3:-false}"
  
  print_section "Checking File Encodings"
  
  # Files that should have Unix-style line endings (LF)
  local lf_files=()
  # Files that should have Windows-style line endings (CRLF)
  local crlf_files=()
  # Files with incorrect line endings
  local incorrect_files=()
  
  print_info "Scanning files in $path..."
  
  # Find all text files
  while IFS= read -r -d '' file; do
    # Skip binary files and special files
    if file -b --mime-encoding "$file" | grep -q "binary"; then
      continue
    fi
    
    # Check file extension to determine correct line ending style
    if [[ "$file" == *.bat || "$file" == *.cmd ]]; then
      # Windows batch files should have CRLF
      crlf_files+=("$file")
      
      # Check if it has incorrect line endings (not CRLF)
      if ! file -b "$file" | grep -q "CRLF"; then
        incorrect_files+=("$file")
        if [ "$verbose" = "true" ]; then
          print_warning "Incorrect line endings (should be CRLF): $file"
        fi
      fi
    else
      # All other text files should have LF
      lf_files+=("$file")
      
      # Check if it has incorrect line endings (not LF)
      if file -b "$file" | grep -q "CRLF"; then
        incorrect_files+=("$file")
        if [ "$verbose" = "true" ]; then
          print_warning "Incorrect line endings (should be LF): $file"
        fi
      fi
    fi
  done < <(find "$path" -type f -not -path "*/\.*" -not -path "*/target/*" -not -path "*/node_modules/*" -print0)
  
  # Print summary
  print_info "Found ${#lf_files[@]} files that should have LF line endings"
  print_info "Found ${#crlf_files[@]} files that should have CRLF line endings"
  
  if [ ${#incorrect_files[@]} -eq 0 ]; then
    print_success "All files have correct line endings"
    return 0
  else
    print_warning "Found ${#incorrect_files[@]} files with incorrect line endings"
    
    if [ "$fix_issues" = "true" ]; then
      print_info "Fixing line endings..."
      
      # Fix files with incorrect line endings
      for file in "${incorrect_files[@]}"; do
        if [[ "$file" == *.bat || "$file" == *.cmd ]]; then
          # Convert to CRLF
          if [ "$verbose" = "true" ]; then
            print_info "Converting to CRLF: $file"
          fi
          # Use sed to convert LF to CRLF
          sed -i 's/$/\r/' "$file"
        else
          # Convert to LF
          if [ "$verbose" = "true" ]; then
            print_info "Converting to LF: $file"
          fi
          # Use dos2unix to convert CRLF to LF
          dos2unix "$file" 2>/dev/null
        fi
      done
      
      print_success "Fixed line endings in ${#incorrect_files[@]} files"
    else
      print_error "Some files have incorrect line endings - run with --fix to correct them"
    fi
    
    # Return error if not fixed
    if [ "$fix_issues" = "true" ]; then
      return 0
    else
      return 1
    fi
  fi
}

#------------------------------------------------------------------------------
# Code Fix Functions
#------------------------------------------------------------------------------

function fix_pmd_issues() {
  print_section "Fixing PMD Issues"
  
  # Find Java files with PMD issues
  local pmd_issues=$(find "${SAMSTRAUMR_JAVA_MAIN}" "${SAMSTRAUMR_JAVA_TEST}" -name "*.java" -exec grep -l "//NOPMD" {} \;)
  
  if [ -z "$pmd_issues" ]; then
    print_success "No PMD suppression comments found"
    return 0
  fi
  
  print_info "Found files with PMD suppression comments:"
  for file in $pmd_issues; do
    print_info "- $file"
  done
  
  # Fix each file
  for file in $pmd_issues; do
    print_info "Fixing PMD issues in $file..."
    
    # Add proper PMD suppression annotations
    sed -i 's/\/\/NOPMD - /@SuppressWarnings("PMD.") \/\/ /g' "$file"
    sed -i 's/\/\/NOPMD/@SuppressWarnings("PMD.")/g' "$file"
    
    print_success "Fixed PMD issues in $file"
  done
  
  print_success "Fixed PMD issues in all files"
  return 0
}

function update_file_headers() {
  local file_type="$1"
  local header_template="$2"
  local target_dir="$3"
  
  print_section "Updating Headers for $file_type Files"
  
  if [ ! -f "$header_template" ]; then
    print_error "Header template file not found: $header_template"
    return 1
  fi
  
  # Read the header template
  local header_content=$(<"$header_template")
  
  # Find files to update
  local file_extension=""
  case "$file_type" in
    java)
      file_extension="java"
      ;;
    markdown|md)
      file_extension="md"
      ;;
    *)
      print_error "Unsupported file type: $file_type"
      return 1
      ;;
  esac
  
  # Find files with the specified extension
  local files=$(find "$target_dir" -name "*.$file_extension" -type f)
  
  if [ -z "$files" ]; then
    print_info "No $file_type files found in $target_dir"
    return 0
  fi
  
  local updated_count=0
  
  # Update each file
  for file in $files; do
    print_debug "Processing file: $file"
    
    # Skip files in target directory
    if [[ "$file" == */target/* ]]; then
      continue
    fi
    
    local file_content=$(<"$file")
    local header_to_apply="$header_content"
    
    # Replace placeholders in the header
    header_to_apply="${header_to_apply//FILENAME/$(basename "$file")}"
    
    # Check if file already has a header
    if grep -q "Licensed" "$file" || grep -q "/*" "$file"; then
      # Replace existing header
      if [ "$file_type" = "java" ]; then
        # For Java files, replace everything before the package declaration
        awk -v header="$header_to_apply" 'BEGIN{print header} /^package/{f=1} f' "$file" > "${file}.new"
      else
        # For other files, replace the first comment block
        awk -v header="$header_to_apply" '/^---/{if(c==0){print header;c=1}else{print}} c==1{print}' "$file" > "${file}.new"
      fi
    else
      # Add header at the beginning
      echo -e "${header_to_apply}\n\n${file_content}" > "${file}.new"
    fi
    
    # Replace the original file
    mv "${file}.new" "$file"
    updated_count=$((updated_count + 1))
    
    print_debug "Updated header in: $file"
  done
  
  if [ $updated_count -eq 0 ]; then
    print_info "All $file_type files already have up-to-date headers"
  else
    print_success "Updated headers in $updated_count $file_type files"
  fi
  
  return 0
}

#------------------------------------------------------------------------------
# Init
#------------------------------------------------------------------------------

# Export functions
export -f run_spotless_check
export -f run_checkstyle
export -f run_spotbugs
export -f run_jacoco_coverage
export -f check_file_encoding
export -f fix_pmd_issues
export -f update_file_headers

# Print debug info if verbose
if [ "${VERBOSE:-false}" = "true" ]; then
  print_debug "Loaded quality-lib.sh library"
fi