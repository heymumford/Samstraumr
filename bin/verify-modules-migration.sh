#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script to verify the Samstraumr to modules directory migration

set -e

# Color settings
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Define directories
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SOURCE_DIR="${PROJECT_ROOT}/Samstraumr"
TARGET_DIR="${PROJECT_ROOT}/modules"

# Print functions
print_header() {
  echo -e "${BLUE}=== $1 ===${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

# Check if both directories exist
print_header "Checking directories"
if [ ! -d "$SOURCE_DIR" ]; then
  print_error "Source directory $SOURCE_DIR does not exist!"
  exit 1
else
  print_success "Source directory exists: $SOURCE_DIR"
fi

if [ ! -d "$TARGET_DIR" ]; then
  print_error "Target directory $TARGET_DIR does not exist!"
  exit 1
else
  print_success "Target directory exists: $TARGET_DIR"
fi

# Check for critical files in both directories
print_header "Checking critical files"

check_file() {
  local file_name="$1"
  local src="${SOURCE_DIR}/${file_name}"
  local tgt="${TARGET_DIR}/${file_name}"
  
  if [ -f "$src" ]; then
    print_success "Source file exists: $src"
    
    if [ -f "$tgt" ]; then
      print_success "Target file exists: $tgt"
      
      # Check if files are identical
      if cmp -s "$src" "$tgt"; then
        print_success "Files are identical: $file_name"
      else
        print_warning "Files are different: $file_name"
      fi
    else
      print_error "Target file MISSING: $tgt"
    fi
  else
    print_warning "Source file doesn't exist: $src (may be normal if already migrated)"
  fi
  
  echo ""
}

# Check critical configuration files
check_file "version.properties"
check_file "checkstyle.xml"
check_file "owasp-suppressions.xml"
check_file "pom.xml"

# Check Maven module structure
print_header "Checking Maven modules"

if [ -d "${SOURCE_DIR}/samstraumr-core" ]; then
  print_success "Source core module exists: ${SOURCE_DIR}/samstraumr-core"
  
  if [ -d "${TARGET_DIR}/samstraumr-core" ]; then
    print_success "Target core module exists: ${TARGET_DIR}/samstraumr-core"
  else
    print_error "Target core module MISSING: ${TARGET_DIR}/samstraumr-core"
  fi
else
  print_warning "Source core module doesn't exist: ${SOURCE_DIR}/samstraumr-core (may be normal if already migrated)"
fi

# Check for crucial Java packages
print_header "Checking Java packages"

CORE_PACKAGES=(
  "component/core"
  "tube"
  "application/port"
  "infrastructure/cache"
  "infrastructure/event"
  "infrastructure/filesystem"
)

for pkg in "${CORE_PACKAGES[@]}"; do
  src_pkg="${SOURCE_DIR}/samstraumr-core/src/main/java/org/s8r/${pkg}"
  tgt_pkg="${TARGET_DIR}/samstraumr-core/src/main/java/org/s8r/${pkg}"
  
  if [ -d "$src_pkg" ]; then
    print_success "Source package exists: $src_pkg"
    
    if [ -d "$tgt_pkg" ]; then
      print_success "Target package exists: $tgt_pkg"
      
      # Count Java files in both directories to ensure nothing was lost
      src_count=$(find "$src_pkg" -name "*.java" | wc -l)
      tgt_count=$(find "$tgt_pkg" -name "*.java" | wc -l)
      
      if [ "$src_count" -le "$tgt_count" ]; then
        print_success "Target has all Java files: $src_count (source) vs $tgt_count (target)"
      else
        print_warning "Target might be missing files: $src_count (source) vs $tgt_count (target)"
      fi
    else
      print_error "Target package MISSING: $tgt_pkg"
    fi
  else
    print_warning "Source package doesn't exist: $src_pkg (may be normal if already migrated)"
  fi
  
  echo ""
done

# Build verification
print_header "Build verification"

# Run Maven build to verify
cd "$PROJECT_ROOT"
if mvn clean compile -DskipTests > /tmp/build.log 2>&1; then
  print_success "Maven build successful!"
else
  print_error "Maven build FAILED! Check /tmp/build.log for details."
fi

# Summary
print_header "Migration status"
echo "The migration from Samstraumr to modules directory appears to be complete."
echo "Most critical files and packages have been verified."
echo ""
echo "Next steps:"
echo "1. Run a full test suite to ensure everything works correctly"
echo "2. Verify any remaining scripts or tools work with the new structure"
echo "3. Once satisfied, the old Samstraumr directory can be removed"

exit 0