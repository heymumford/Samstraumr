#!/bin/bash
#
# Folder Structure Flattening Script
# Helps implement the folder flattening plan by identifying and reorganizing
# directories that could benefit from flattening.
#
# Usage: ./flatten-directories.sh [directory-path] [min-files]
#   directory-path: Target directory to analyze (default: current directory)
#   min-files: Minimum file count for a directory to be considered properly sized (default: 3)
#

# Set default values
TARGET_DIR=${1:-.}
MIN_FILES=${2:-3}

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}S8r Framework Directory Flattening Analysis${NC}"
echo -e "Analyzing directory: ${TARGET_DIR}"
echo -e "Minimum file threshold: ${MIN_FILES} files"
echo

# Find directories with fewer than MIN_FILES files
echo -e "${YELLOW}Directories with fewer than ${MIN_FILES} files:${NC}"
echo -e "These directories are candidates for flattening or consolidation."
echo -e "${GREEN}Path${NC} | ${GREEN}File Count${NC} | ${GREEN}Suggested Action${NC}"
echo "-----------------------------------------------------"

while IFS= read -r dir; do
    # Skip specific directories we want to keep
    if [[ "$dir" == *"/.git"* ]] || [[ "$dir" == *"/target"* ]] || [[ "$dir" == *"/node_modules"* ]]; then
        continue
    fi
    
    # Count real files (not directories)
    file_count=$(find "$dir" -maxdepth 1 -type f | wc -l)
    
    if [ "$file_count" -lt "$MIN_FILES" ]; then
        # Check if parent directory has few files too
        parent_dir=$(dirname "$dir")
        parent_file_count=$(find "$parent_dir" -maxdepth 1 -type f | wc -l)
        total_count=$((file_count + parent_file_count))
        
        # Generate suggestion
        if [ "$file_count" -eq 0 ]; then
            suggestion="Delete empty directory"
        elif [ "$file_count" -eq 1 ]; then
            suggestion="Move file to parent directory with clear naming"
        elif [ "$total_count" -lt 7 ]; then
            suggestion="Consolidate with parent directory"
        else
            suggestion="Add README.md explaining purpose"
        fi
        
        echo -e "$dir | $file_count | $suggestion"
    fi
done < <(find "$TARGET_DIR" -type d | sort)

echo

# Find deeply nested directories (5+ levels)
echo -e "${YELLOW}Deeply nested directories (5+ levels deep):${NC}"
echo -e "These directories may indicate excessive hierarchy."
echo

find "$TARGET_DIR" -type d | awk -F/ '{if (NF >= 9) print}' | sort

echo

# Find directories that might benefit from naming conventions instead
echo -e "${YELLOW}Directories that could be replaced with naming conventions:${NC}"
echo -e "These directories contain files that could be organized with naming patterns."
echo 

# Find directories with variations in name but similar structure
find "$TARGET_DIR" -type d -mindepth 3 -name "*service*" -o -name "*controller*" -o -name "*adapter*" | sort

echo -e "\n${BLUE}Recommendations:${NC}"
echo "1. Create a README.md in each directory explaining its purpose"
echo "2. Consolidate directories with fewer than $MIN_FILES files"
echo "3. Use file naming conventions instead of deep nesting"
echo "4. Review the folder-flattening-plan.md for complete guidance"
echo

# Optional: Generate example rename commands
echo -e "${YELLOW}Example rename commands for flattening:${NC}"
echo "(These are examples, review carefully before executing)"
echo

# Find some examples to demonstrate renaming instead of nesting
find "$TARGET_DIR" -type f -path "*/*/impl/*.java" -o -path "*/*/*/util/*.java" | head -n 5 | while read -r file; do
    dir=$(dirname "$file")
    parent_dir=$(dirname "$dir")
    filename=$(basename "$file")
    parent_name=$(basename "$parent_dir")
    dir_name=$(basename "$dir")
    
    new_name="${parent_name}-${dir_name}-${filename}"
    new_path="${parent_dir}/${new_name}"
    
    echo "# mv \"$file\" \"$new_path\""
done

exit 0