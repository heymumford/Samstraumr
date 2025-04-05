#!/bin/bash
#
# Import Statement Fixer
# Automatically fixes import statements in Java files by sorting them alphabetically
# and removing comments.
#
# Usage: ./fix-imports.sh [options] [file-path]
#   --all: Fix all Java files in the repository
#   file-path: Specific file to fix (if not using --all)
#

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}Java Import Statement Fixer${NC}"

# Function to fix imports in a file
fix_imports() {
    local file=$1
    echo -e "Processing: ${file}"
    
    # Create a backup
    cp "$file" "${file}.bak"
    
    # Fix the file by:
    # 1. Extract the package declaration and import section
    # 2. Remove comments from import section
    # 3. Sort the imports
    # 4. Reconstruct the file
    
    # Extract package declaration
    package_decl=$(grep -m 1 "^package " "$file")
    
    # Extract and sort imports
    imports=$(grep "^import " "$file" | sed 's/\/\/.*$//g' | sort)
    
    # Extract everything after imports
    body=$(awk '
        BEGIN {found_imports=0; past_imports=0}
        /^import / {found_imports=1; next}
        found_imports && /^[[:space:]]*$/ {next}
        found_imports && !past_imports && !/^import / {past_imports=1}
        past_imports {print}
    ' "$file")
    
    # Reconstruct the file
    {
        # Add license/copyright header if it exists
        if grep -q "^/\*" "$file"; then
            awk '
                BEGIN {in_header=0}
                /^\/\*/ {in_header=1}
                in_header {print}
                /^\*\// {in_header=0; exit}
            ' "$file"
            echo ""
        fi
        
        # Add package declaration
        echo "$package_decl"
        echo ""
        
        # Add sorted imports
        if [[ -n "$imports" ]]; then
            echo "$imports"
            echo ""
        fi
        
        # Add the rest of the file
        echo "$body"
    } > "$file.new"
    
    # Replace the original file with the fixed version
    mv "$file.new" "$file"
    echo -e "${GREEN}✓ Fixed imports in: ${file}${NC}"
}

# Check if running in --all mode
if [[ "$1" == "--all" ]]; then
    echo -e "Fixing all Java files in the repository..."
    
    # Find all Java files and fix them
    while IFS= read -r file; do
        fix_imports "$file"
    done < <(find . -name "*.java" | grep -v "/target/" | grep -v "/node_modules/")
    
    echo -e "${GREEN}✓ All Java files processed${NC}"
else
    # Check if a file was specified
    if [[ -z "$1" ]]; then
        echo -e "${RED}Error: Please specify a file to fix or use --all to fix all files${NC}"
        echo -e "Usage: ./fix-imports.sh [--all] [file-path]"
        exit 1
    fi
    
    # Check if the file exists
    if [[ ! -f "$1" ]]; then
        echo -e "${RED}Error: File not found: $1${NC}"
        exit 1
    fi
    
    # Check if it's a Java file
    if [[ "$1" != *.java ]]; then
        echo -e "${RED}Error: Not a Java file: $1${NC}"
        exit 1
    fi
    
    # Fix the specified file
    fix_imports "$1"
fi

echo -e "${BLUE}Import fixing complete!${NC}"
echo -e "Run ./check-imports.sh to verify the results."

exit 0