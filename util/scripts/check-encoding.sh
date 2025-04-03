#!/bin/bash

# Script to verify UTF-8 encoding and LF line endings in project files
# This helps ensure consistent encoding across git, quality checks, and the build process

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
RESET='\033[0m'

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

echo -e "${YELLOW}Checking file encodings...${RESET}"

# Array of extensions to check
EXTENSIONS=("java" "xml" "md" "feature" "properties" "yml" "yaml" "sh" "txt")

# Count variables
TOTAL_FILES=0
NON_UTF8_FILES=0
CRLF_FILES=0

# Function to check encoding and line endings
check_file() {
    local file="$1"
    local has_error=0
    
    # Check encoding
    encoding=$(file -i "$file" | grep -oP "charset=\K[^\s;]+")
    if [[ "$encoding" != "utf-8" && "$encoding" != "us-ascii" ]]; then
        echo -e "${RED}Non-UTF8 encoding ($encoding): $file${RESET}"
        NON_UTF8_FILES=$((NON_UTF8_FILES + 1))
        has_error=1
    fi
    
    # Skip binary files
    if file "$file" | grep -q "binary"; then
        echo -e "${YELLOW}Skipping binary file: $file${RESET}"
        return
    fi
    
    # Check line endings (except .bat and .cmd files)
    if [[ ! "$file" =~ \.(bat|cmd)$ ]]; then
        if grep -q $'\r' "$file"; then
            echo -e "${RED}CRLF line endings: $file${RESET}"
            CRLF_FILES=$((CRLF_FILES + 1))
            has_error=1
        fi
    fi
    
    TOTAL_FILES=$((TOTAL_FILES + 1))
    
    # Show success message only in verbose mode
    if [[ "$VERBOSE" == "1" && "$has_error" == "0" ]]; then
        echo -e "${GREEN}OK: $file${RESET}"
    fi
}

# Process command line arguments
VERBOSE=0
FIX_ISSUES=0

for arg in "$@"; do
    if [[ "$arg" == "-v" || "$arg" == "--verbose" ]]; then
        VERBOSE=1
    elif [[ "$arg" == "-f" || "$arg" == "--fix" ]]; then
        FIX_ISSUES=1
    fi
done

# Check all files with specified extensions
echo -e "${YELLOW}Scanning source files...${RESET}"
for ext in "${EXTENSIONS[@]}"; do
    if [[ "$VERBOSE" == "1" ]]; then
        echo -e "${YELLOW}Checking *.${ext} files...${RESET}"
    fi
    
    while IFS= read -r file; do
        check_file "$file"
    done < <(find . -name "*.${ext}" -not -path "*/target/*" -not -path "*/node_modules/*" -type f)
done

# Fix encoding and line ending issues if requested
if [[ "$FIX_ISSUES" == "1" && ("$NON_UTF8_FILES" -gt 0 || "$CRLF_FILES" -gt 0) ]]; then
    echo -e "${YELLOW}Fixing encoding and line ending issues...${RESET}"
    
    # Update git config to handle line endings correctly
    git config --local core.autocrlf false
    
    # Use dos2unix to fix line endings (if installed)
    if command -v dos2unix &> /dev/null; then
        echo -e "${YELLOW}Converting CRLF to LF using dos2unix...${RESET}"
        for ext in "${EXTENSIONS[@]}"; do
            # Skip .bat and .cmd files
            if [[ "$ext" != "bat" && "$ext" != "cmd" ]]; then
                find . -name "*.${ext}" -not -path "*/target/*" -not -path "*/node_modules/*" -type f | xargs -I{} dos2unix {} 2>/dev/null
            fi
        done
    else
        echo -e "${RED}dos2unix not found. Install it to fix line endings automatically.${RESET}"
    fi
    
    # Use iconv to fix encodings (if needed)
    if [[ "$NON_UTF8_FILES" -gt 0 ]]; then
        echo -e "${YELLOW}Cannot automatically fix non-UTF8 files. Please convert them manually.${RESET}"
    fi
    
    # Remind user to apply git attributes
    echo -e "${YELLOW}To ensure consistent line endings in git, run:${RESET}"
    echo -e "${GREEN}git add --renormalize .${RESET}"
fi

# Print summary
echo -e "${YELLOW}=== Encoding Check Summary ===${RESET}"
echo -e "Total files checked: ${TOTAL_FILES}"
echo -e "Files with non-UTF8 encoding: ${NON_UTF8_FILES}"
echo -e "Files with CRLF line endings: ${CRLF_FILES}"

if [[ "$NON_UTF8_FILES" -eq 0 && "$CRLF_FILES" -eq 0 ]]; then
    echo -e "${GREEN}All files have correct encoding and line endings.${RESET}"
    exit 0
else
    echo -e "${RED}Some files have incorrect encoding or line endings.${RESET}"
    echo -e "${YELLOW}To fix automatically, run:${RESET}"
    echo -e "${GREEN}./check-encoding.sh --fix${RESET}"
    exit 1
fi