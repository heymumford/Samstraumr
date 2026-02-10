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

# Find deeply nested directories (exceeding 9 levels from repository root)
echo -e "${YELLOW}Deeply nested directories (exceeding 9 levels):${NC}"
echo -e "These directories exceed our maximum depth limit and must be flattened."
echo -e "${RED}CRITICAL: These paths must be restructured according to the folder-flattening-plan.md${NC}"
echo

# The value 14 accounts for the absolute path prefix (e.g., /home/user/project = 4 segments) plus 10 levels
find "$TARGET_DIR" -type d | awk -F/ '{if (NF >= 14) print $0 " (" NF-4 " levels)"}' | sort

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

# Final verification section
echo -e "\n${BLUE}Depth Verification${NC}"
echo -e "Maximum recommended depth is 9 levels from repository root"

# Count directories exceeding depth limit
EXCESS_COUNT=$(find "$TARGET_DIR" -type d | awk -F/ '{if (NF >= 14) print}' | wc -l)

if [ "$EXCESS_COUNT" -gt 0 ]; then
    echo -e "${RED}Found $EXCESS_COUNT directories exceeding maximum depth limit${NC}"
    echo -e "Run this script with --recommend to generate specific refactoring commands"
    
    # Check if --recommend flag was passed
    if [[ "$*" == *"--recommend"* ]]; then
        echo -e "\n${GREEN}Recommended Refactoring Commands:${NC}"
        echo -e "# Save these commands to a file and review before executing\n"
        
        # Generate package refactoring recommendations for Java files
        find "$TARGET_DIR" -path "*/org/s8r/core/tube/test/*" -name "*.java" | while read -r file; do
            dir=$(dirname "$file")
            filename=$(basename "$file")
            package=$(echo "$dir" | grep -o "org/s8r/.*" | sed 's/\//./g')
            new_package="org.s8r.test.tube.$(echo "$dir" | grep -o "/test/[^/]*$" | sed 's/\///g')"
            new_dir=$(echo "$dir" | sed 's/core\/tube\/test/test\/tube/g')
            
            echo "# Refactor: $file"
            echo "# From package: $package"
            echo "# To package: $new_package"
            echo "mkdir -p \"$new_dir\""
            echo "# Update package declaration in file, then move:"
            echo "mv \"$file\" \"$new_dir/$filename\""
            echo ""
        done
        
        # Generate feature file refactoring recommendations
        find "$TARGET_DIR" -path "*/test/resources/*/features/*/*/*" -name "*.feature" | while read -r file; do
            dir=$(dirname "$file")
            filename=$(basename "$file")
            parent_dir=$(basename "$(dirname "$dir")")
            feature_type=$(basename "$dir")
            new_name="${parent_dir}-${feature_type}-${filename}"
            new_dir=$(echo "$dir" | sed -E 's/([^/]+)\/features\/[^/]+\/[^/]+/features\/\1/g')
            
            echo "# Refactor: $file"
            echo "mkdir -p \"$new_dir\""
            echo "mv \"$file\" \"$new_dir/$new_name\""
            echo ""
        done
    fi
else
    echo -e "${GREEN}✓ All directories are within the maximum depth limit${NC}"
fi

exit 0