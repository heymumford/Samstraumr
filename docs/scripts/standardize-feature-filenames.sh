#!/bin/bash
# Script to standardize Cucumber feature file naming conventions in the project
#
# Naming conventions:
# - Feature files use kebab-case with descriptive names ending in `-test.feature`
# - Acronyms like TBD remain in UPPERCASE

# Store the project root directory
PROJECT_ROOT=$(git rev-parse --show-toplevel 2>/dev/null || pwd)
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  echo "Warning: Documentation library not found. Using fallback functions."
  USING_LIB=false
  
  # Set colors for output
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  NC='\033[0m' # No Color

  # Function to print a header
  print_header() {
    echo -e "\n${YELLOW}==== $1 ====${NC}"
  }

  # Function to print success message
  print_success() {
    echo -e "${GREEN}✓ $1${NC}"
  }

  # Function to print error message
  print_error() {
    echo -e "${RED}✗ $1${NC}"
  }

  # Function to convert PascalCase to kebab-case
  to_kebab_case() {
    echo "$1" | sed 's/\(.\)\([A-Z]\)/\1-\2/g' | tr '[:upper:]' '[:lower:]'
  }
fi

print_header "Standardizing Cucumber Feature Filenames"
echo "Following naming conventions:"
echo "- Feature files use kebab-case with descriptive names ending in '-test.feature'"
echo "- Acronyms like TBD remain in UPPERCASE"
echo ""

# Rename feature files with lowercase "tbd" to uppercase "TBD"
print_header "Converting 'tbd' to 'TBD' in Feature Files"

# Find feature files with lowercase "tbd" in their names
files_with_lowercase_tbd=$(find . -name "*-tbd-*.feature" -or -name "*tbd*.feature" | grep -v "*TBD*" || true)

if [ -n "$files_with_lowercase_tbd" ]; then
  for file in $files_with_lowercase_tbd; do
    new_file=$(echo "$file" | sed 's/\([^-]\)tbd\([^-]\)/\1TBD\2/g' | sed 's/-tbd-/-TBD-/g')
    mv -v "$file" "$new_file"
  done
  print_success "Renamed files with lowercase 'tbd' to uppercase 'TBD'"
else
  print_success "No files with lowercase 'tbd' found"
fi

# Standardize naming for TBD example files to use consistent format 
print_header "Standardizing TBD Example Files"

# Handle different formats and convert to consistent kebab-case with uppercase TBD
if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-AtomicBoundaryTest-Example.feature" ]; then
  mv -v "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-AtomicBoundaryTest-Example.feature" \
       "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-atomic-boundary-test-example.feature"
fi

if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-CompositeTubeInteractionTest-Example.feature" ]; then
  mv -v "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-CompositeTubeInteractionTest-Example.feature" \
       "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-composite-tube-interaction-test-example.feature"
fi

if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-InterTubeFeatureTest-Example.feature" ]; then
  mv -v "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-InterTubeFeatureTest-Example.feature" \
       "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-inter-tube-feature-test-example.feature"
fi

if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-MachineConstructValidationTest-Example.feature" ]; then
  mv -v "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-MachineConstructValidationTest-Example.feature" \
       "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-machine-construct-validation-test-example.feature"
fi

# Handle duplicate kebab-case files 
if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/atomic-boundary-test-example.feature" ] && [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-atomic-boundary-test-example.feature" ]; then
  rm -v "modules/samstraumr-core/src/test/resources/tube/features/examples/atomic-boundary-test-example.feature"
fi

if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/composite-tube-interaction-test-example.feature" ] && [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-composite-tube-interaction-test-example.feature" ]; then
  rm -v "modules/samstraumr-core/src/test/resources/tube/features/examples/composite-tube-interaction-test-example.feature"
fi

if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/inter-tube-feature-test-example.feature" ] && [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-inter-tube-feature-test-example.feature" ]; then
  rm -v "modules/samstraumr-core/src/test/resources/tube/features/examples/inter-tube-feature-test-example.feature"
fi

if [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/machine-construct-validation-test-example.feature" ] && [ -f "modules/samstraumr-core/src/test/resources/tube/features/examples/TBD-machine-construct-validation-test-example.feature" ]; then
  rm -v "modules/samstraumr-core/src/test/resources/tube/features/examples/machine-construct-validation-test-example.feature"
fi

# Convert PascalCase feature files to kebab-case
print_header "Converting PascalCase Feature Files to kebab-case"

# Use the library function if available
if [[ "$USING_LIB" == true ]] && type standardize_directory_filenames &>/dev/null; then
  # Use library function to standardize filenames in directories
  if [[ "$USING_LIB" == true ]] && type print_info &>/dev/null; then
    print_info "Using doc-lib.sh standardization functions"
  fi
  
  # Standardize filenames in composites features directory
  if [ -d "./modules/samstraumr-core/src/test/resources/composites/features" ]; then
    standardize_directory_filenames "./modules/samstraumr-core/src/test/resources/composites/features"
  fi
  
  # Standardize filenames in tube features directory
  if [ -d "./modules/samstraumr-core/src/test/resources/tube/features" ]; then
    standardize_directory_filenames "./modules/samstraumr-core/src/test/resources/tube/features"
  fi
else
  # Handle pattern files in composites directory
  for file in $(find ./modules/samstraumr-core/src/test/resources/composites/features -name "*[A-Z]*.feature" 2>/dev/null || true); do
    directory=$(dirname "$file")
    filename=$(basename "$file")
    # Check if this is a PascalCase filename
    if [[ "$filename" =~ [A-Z] && "$filename" != "README.md" ]]; then
      # Convert to kebab-case
      kebab_filename=$(to_kebab_case "$filename")
      # Rename the file
      mv -v "$file" "$directory/$kebab_filename"
    fi
  done

  # Handle regular feature files
  for file in $(find ./modules/samstraumr-core/src/test/resources/tube/features -name "*[A-Z]*.feature" 2>/dev/null | grep -v "TBD" || true); do
    directory=$(dirname "$file")
    filename=$(basename "$file")
    # Check if this is a PascalCase filename
    if [[ "$filename" =~ [A-Z] && "$filename" != "README.md" ]]; then
      # Convert to kebab-case
      kebab_filename=$(to_kebab_case "$filename")
      # Rename the file
      mv -v "$file" "$directory/$kebab_filename"
    fi
  done
fi

# Check if there are any remaining non-standard files
print_header "Checking for any remaining files that don't follow conventions"

# Use custom check function or library function if available
if [[ "$USING_LIB" == true ]] && type find_non_kebab_case_files &>/dev/null; then
  # We need a specialized check for feature files, but can use the library for part of it
  print_info "Using doc-lib.sh for checking non-standard files"
  
  # Find any feature files
  feature_files=$(find . -name "*.feature" | grep -v "node_modules" | grep -v "target" || true)
  
  # Check each file for proper conventions
  remaining_files=""
  for file in $feature_files; do
    filename=$(basename "$file")
    
    # Skip TBD example files
    if [[ "$filename" == TBD-*-test-example.feature ]]; then
      continue
    fi
    
    # Check if regular feature file follows convention
    if ! [[ "$filename" =~ ^[a-z0-9]+(-[a-z0-9]+)*-test\.feature$ ]]; then
      remaining_files+="$file"$'\n'
    fi
  done
else
  # Find any feature files that don't follow the naming convention
  # This would find files that:
  # 1. Have uppercase letters other than in acronyms like TBD
  # 2. Don't use kebab-case (have underscores or no separators)
  # 3. Don't end with test.feature or test-example.feature
  remaining_files=$(find . -name "*.feature" | grep -v "node_modules" | grep -v "target" | 
                   grep -Ev '(^.*[^A-Z]|^.*/)(([a-z]+-)+test\.feature|TBD-([a-z]+-)+test-example\.feature)$' || true)
fi

if [ -n "$remaining_files" ]; then
  if [[ "$USING_LIB" == true ]] && type print_error &>/dev/null; then
    print_error "The following files still need to be renamed:"
    echo "$remaining_files"
  else
    print_error "The following files still need to be renamed:"
    echo "$remaining_files"
  fi
else
  if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
    print_success "All Feature files follow the naming conventions!"
  else
    print_success "All Feature files follow the naming conventions!"
  fi
fi

if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null; then
  print_header "Standardization Complete"
else
  print_header "Standardization Complete"
fi

echo "All Cucumber Feature files have been standardized according to the naming conventions:"
echo "- Feature files use kebab-case with descriptive names ending in '-test.feature'"
echo "- Acronyms like TBD remain in UPPERCASE"

# Create a report if using the library
if [[ "$USING_LIB" == true ]] && [ -d "$REPORT_DIR" ]; then
  date_stamp=$(date +%Y%m%d)
  report_file="${REPORT_DIR}/feature-standardization-${date_stamp}.md"
  
  # Create a report
  {
    echo "# Feature File Standardization Report"
    echo "Generated: $(date)"
    echo
    echo "## Naming Conventions"
    echo "- Feature files use kebab-case with descriptive names ending in '-test.feature'"
    echo "- Acronyms like TBD remain in UPPERCASE"
    echo
    echo "## Results"
    
    if [ -n "$remaining_files" ]; then
      echo "### Files Still Needing Standardization"
      echo
      echo "$remaining_files" | while read -r file; do
        if [ -n "$file" ]; then
          echo "- \`$file\`"
        fi
      done
    else
      echo "All feature files follow the naming conventions!"
    fi
  } > "$report_file"
  
  if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
    print_success "Standardization report created at ${report_file}"
  fi
fi