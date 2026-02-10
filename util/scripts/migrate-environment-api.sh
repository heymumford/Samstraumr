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

# Script to migrate Environment API from getValue/setValue to getParameter/setParameter

# Determine script directory for loading libraries
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"

# Include common libraries
if [ -f "$SCRIPT_DIR/../lib/unified-common.sh" ]; then
    source "$SCRIPT_DIR/../lib/unified-common.sh"
else
    echo "Error: Failed to load unified-common.sh"
    exit 1
fi

# Check if on Linux for GNU sed
is_gnu_sed=false
if [[ "$(uname)" == "Linux" ]]; then
    is_gnu_sed=true
fi

# Print script banner
print_banner "Environment API Migration Script"
echo "This script will migrate Environment API from getValue/setValue to getParameter/setParameter."
echo "It will search for all Java files in the project and update the method calls."
echo

# Ask for confirmation
read -p "Do you want to proceed? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Migration cancelled."
    exit 0
fi

# First, perform a search to identify files that need migration
echo "Identifying files that use the old API..."
getValue_count=$(grep -r "\.getValue(" --include="*.java" "${PROJECT_ROOT}/src" | wc -l)
setValue_count=$(grep -r "\.setValue(" --include="*.java" "${PROJECT_ROOT}/src" | wc -l)

echo "Found approximately ${getValue_count} occurrences of .getValue() and ${setValue_count} occurrences of .setValue()"
echo

if [ $getValue_count -eq 0 ] && [ $setValue_count -eq 0 ]; then
    echo "No occurrences found. Migration not needed."
    exit 0
fi

# Create backup directory
backup_dir="${PROJECT_ROOT}/backups/environment-api-migration-$(date +%Y%m%d%H%M%S)"
mkdir -p "$backup_dir"
echo "Creating backups in: $backup_dir"

# Find all Java files that use the old API and create backups
find "${PROJECT_ROOT}/src" -name "*.java" -type f \
    -exec grep -l "\.getValue(" {} \; \
    -o -exec grep -l "\.setValue(" {} \; | sort | uniq | \
while read -r file; do
    relative_path=${file#$PROJECT_ROOT/}
    backup_path="$backup_dir/$relative_path"
    backup_dir=$(dirname "$backup_path")
    mkdir -p "$backup_dir"
    cp "$file" "$backup_path"
    echo "Backed up: $relative_path"
done

# Count backed up files
backup_count=$(find "$backup_dir" -type f | wc -l)
echo "Backed up $backup_count files."
echo

# Perform replacement
echo "Performing replacements..."

# Different sed command for GNU sed (Linux) and BSD sed (macOS)
if [ "$is_gnu_sed" = true ]; then
    # GNU sed (Linux)
    find "${PROJECT_ROOT}/src" -name "*.java" -type f -exec sed -i 's/\([^A-Za-z0-9_]\)getValue(/\1getParameter(/g' {} \;
    find "${PROJECT_ROOT}/src" -name "*.java" -type f -exec sed -i 's/\([^A-Za-z0-9_]\)setValue(/\1setParameter(/g' {} \;
else
    # BSD sed (macOS)
    find "${PROJECT_ROOT}/src" -name "*.java" -type f -exec sed -i '' 's/\([^A-Za-z0-9_]\)getValue(/\1getParameter(/g' {} \;
    find "${PROJECT_ROOT}/src" -name "*.java" -type f -exec sed -i '' 's/\([^A-Za-z0-9_]\)setValue(/\1setParameter(/g' {} \;
fi

echo "Replacement completed."
echo

# Verify replacements
echo "Verifying replacements..."
new_getValue_count=$(grep -r "\.getValue(" --include="*.java" "${PROJECT_ROOT}/src" | wc -l)
new_setValue_count=$(grep -r "\.setValue(" --include="*.java" "${PROJECT_ROOT}/src" | wc -l)

echo "After migration:"
echo "- getValue() occurrences: $new_getValue_count (was $getValue_count)"
echo "- setValue() occurrences: $new_setValue_count (was $setValue_count)"
echo

# Recommend additional actions
echo "Migration completed. Please take the following actions:"
echo
echo "1. Review the changes to ensure correctness."
echo "2. Run the full test suite to verify functionality:"
echo "   ./s8r-test all"
echo
echo "3. If there are issues, restore from backup:"
echo "   cp -r $backup_dir/* $PROJECT_ROOT/"
echo
echo "4. Read the migration guide for more information:"
echo "   docs/guides/migration/environment-api-migration-guide.md"
echo

print_success "Environment API migration completed."