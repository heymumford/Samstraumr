#!/bin/bash
# Script to standardize Cucumber feature file naming conventions in the project
#
# Naming conventions:
# - Feature files use kebab-case with descriptive names ending in `-test.feature`
# - Acronyms like TBD remain in UPPERCASE

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

# Store the project root directory
PROJECT_ROOT=$(pwd)
cd "$PROJECT_ROOT"

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
if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-AtomicBoundaryTest-Example.feature" ]; then
  mv -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-AtomicBoundaryTest-Example.feature" \
       "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-atomic-boundary-test-example.feature"
fi

if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-CompositeTubeInteractionTest-Example.feature" ]; then
  mv -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-CompositeTubeInteractionTest-Example.feature" \
       "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-composite-tube-interaction-test-example.feature"
fi

if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-InterTubeFeatureTest-Example.feature" ]; then
  mv -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-InterTubeFeatureTest-Example.feature" \
       "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-inter-tube-feature-test-example.feature"
fi

if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-MachineConstructValidationTest-Example.feature" ]; then
  mv -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-MachineConstructValidationTest-Example.feature" \
       "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-machine-construct-validation-test-example.feature"
fi

# Handle duplicate kebab-case files 
if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/atomic-boundary-test-example.feature" ] && [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-atomic-boundary-test-example.feature" ]; then
  rm -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/atomic-boundary-test-example.feature"
fi

if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/composite-tube-interaction-test-example.feature" ] && [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-composite-tube-interaction-test-example.feature" ]; then
  rm -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/composite-tube-interaction-test-example.feature"
fi

if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/inter-tube-feature-test-example.feature" ] && [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-inter-tube-feature-test-example.feature" ]; then
  rm -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/inter-tube-feature-test-example.feature"
fi

if [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/machine-construct-validation-test-example.feature" ] && [ -f "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/TBD-machine-construct-validation-test-example.feature" ]; then
  rm -v "Samstraumr/samstraumr-core/src/test/resources/tube/features/examples/machine-construct-validation-test-example.feature"
fi

# Convert PascalCase feature files to kebab-case
print_header "Converting PascalCase Feature Files to kebab-case"

# Handle pattern files in composites directory
for file in $(find ./Samstraumr/samstraumr-core/src/test/resources/composites/features -name "*[A-Z]*.feature"); do
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
for file in $(find ./Samstraumr/samstraumr-core/src/test/resources/tube/features -name "*[A-Z]*.feature" | grep -v "TBD"); do
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

# Check if there are any remaining non-standard files
print_header "Checking for any remaining files that don't follow conventions"

# Find any feature files that don't follow the naming convention
# This would find files that:
# 1. Have uppercase letters other than in acronyms like TBD
# 2. Don't use kebab-case (have underscores or no separators)
# 3. Don't end with test.feature or test-example.feature
remaining_files=$(find . -name "*.feature" | grep -v "node_modules" | grep -v "target" | 
                 grep -Ev '(^.*[^A-Z]|^.*/)(([a-z]+-)+test\.feature|TBD-([a-z]+-)+test-example\.feature)$' || true)

if [ -n "$remaining_files" ]; then
  print_error "The following files still need to be renamed:"
  echo "$remaining_files"
else
  print_success "All Feature files follow the naming conventions!"
fi

print_header "Standardization Complete"
echo "All Cucumber Feature files have been standardized according to the naming conventions:"
echo "- Feature files use kebab-case with descriptive names ending in '-test.feature'"
echo "- Acronyms like TBD remain in UPPERCASE"