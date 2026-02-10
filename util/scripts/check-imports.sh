#!/bin/bash
#
# Import Statement Analyzer
# Checks Java files for properly organized import statements
#
# Usage: ./check-imports.sh [directory-path]
#   directory-path: Target directory to analyze (default: current directory)
#

# Set default values
TARGET_DIR=${1:-.}

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Java Import Statement Analysis${NC}"
echo -e "Analyzing directory: ${TARGET_DIR}"
echo

# Statistics
TOTAL_FILES=0
UNSORTED_FILES=0
COMMENT_FILES=0
MISSING_FILES=0

# Arrays to store the names of files with issues
unsorted_files=()
comment_files=()
missing_imports=()

# Function to check if imports are alphabetically sorted
check_imports() {
    local file=$1
    local has_unsorted=0
    local has_comments=0
    local has_imports=0
    
    # Extract the import section
    import_section=$(awk '
        /^import / {in_import=1; has_imports=1}
        /^[[:space:]]*$/ && in_import {in_import=0}
        /^[[:space:]]*[^[:space:]]*[[:space:]]*[^i]/ && in_import {in_import=0}
        in_import {imports[n++]=$0}
        END {
            if (has_imports) {
                for (i=0; i<n; i++) print imports[i]
            } else {
                print "NO_IMPORTS_FOUND"
            }
        }
    ' "$file")
    
    if [[ "$import_section" == "NO_IMPORTS_FOUND" ]]; then
        # Check if the file actually needs imports
        local has_package=$(grep -c "^package " "$file")
        if [[ $has_package -gt 0 ]]; then
            missing_imports+=("$file")
            return 2
        else
            # This is probably a package-info.java or another file that doesn't need imports
            return 0
        fi
    fi
    
    # Check if there are comments in the import section
    if echo "$import_section" | grep -q "//"; then
        has_comments=1
        comment_files+=("$file")
    fi
    
    # Check if imports are alphabetically sorted
    local sorted_imports=$(echo "$import_section" | grep "^import " | sort)
    local actual_imports=$(echo "$import_section" | grep "^import ")
    
    if [[ "$sorted_imports" != "$actual_imports" ]]; then
        has_unsorted=1
        unsorted_files+=("$file")
    fi
    
    if [[ $has_unsorted -eq 1 || $has_comments -eq 1 ]]; then
        return 1
    else
        return 0
    fi
}

# Find all Java files and check them
while IFS= read -r file; do
    TOTAL_FILES=$((TOTAL_FILES + 1))
    
    check_imports "$file"
    result=$?
    
    if [[ $result -eq 1 ]]; then
        # File has unsorted imports or comments
        if echo "${unsorted_files[@]}" | grep -q "$file"; then
            UNSORTED_FILES=$((UNSORTED_FILES + 1))
        fi
        if echo "${comment_files[@]}" | grep -q "$file"; then
            COMMENT_FILES=$((COMMENT_FILES + 1))
        fi
    elif [[ $result -eq 2 ]]; then
        # File has no imports but might need them
        MISSING_FILES=$((MISSING_FILES + 1))
    fi
done < <(find "$TARGET_DIR" -name "*.java" | grep -v "/target/" | grep -v "/node_modules/")

# Print statistics
echo -e "${BLUE}Import Analysis Results:${NC}"
echo -e "Total Java files analyzed: ${TOTAL_FILES}"
echo -e "Files with unsorted imports: ${RED}${UNSORTED_FILES}${NC}"
echo -e "Files with comments in imports: ${YELLOW}${COMMENT_FILES}${NC}"
echo -e "Files potentially missing imports: ${YELLOW}${MISSING_FILES}${NC}"
echo

# Print detailed issues
if [[ ${#unsorted_files[@]} -gt 0 ]]; then
    echo -e "${RED}Files with unsorted imports:${NC}"
    for file in "${unsorted_files[@]}"; do
        echo " - $file"
    done
    echo
fi

if [[ ${#comment_files[@]} -gt 0 ]]; then
    echo -e "${YELLOW}Files with comments in imports:${NC}"
    for file in "${comment_files[@]}"; do
        echo " - $file"
    done
    echo
fi

if [[ ${#missing_imports[@]} -gt 0 ]]; then
    echo -e "${YELLOW}Files potentially missing imports:${NC}"
    for file in "${missing_imports[@]}"; do
        echo " - $file"
    done
    echo
fi

# Add a fix tool to automatically sort imports
echo -e "${BLUE}Fix Tool:${NC}"
echo -e "To fix unsorted imports, run: ./fix-imports.sh [file-path]"
echo -e "Or to fix all issues at once: ./fix-imports.sh --all"
echo

exit 0