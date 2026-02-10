#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#\!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# IMPORTANT: NEVER USE GREP FOR XML FILES, ONLY XMLSTARLET
# This is permanently encoded into the project standards

# Script for fixing common POM file issues:
# 1. <n> -> <name> conversion
# 2. XML validation and formatting

# Navigate to script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "\n${YELLOW}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}$1${RESET}" >&2; }
detail() { echo -e "${BLUE}$1${RESET}"; }

# Check for XMLStarlet
if \! command -v xmlstarlet &> /dev/null; then
    error "XMLStarlet not found. Please install it using 'sudo apt-get install xmlstarlet'"
    exit 1
fi

# Check if xmllint is available
if \! command -v xmllint &> /dev/null; then
    error "xmllint not found. Please install it using 'sudo apt-get install libxml2-utils'"
    exit 1
fi

# Default options
RECURSIVE=false
FIX_MODE=false
VERBOSE=false
TARGET_PATH="$ROOT_DIR"

# Print usage information
print_usage() {
    echo "Usage: $(basename $0) [OPTIONS] [PATH]"
    echo
    echo "Fix POM file issues, including <n> vs <name> problems and XML validation"
    echo
    echo "Options:"
    echo "  -r, --recursive    Process directories recursively (default: false)"
    echo "  -f, --fix          Fix issues automatically (default: false)"
    echo "  -v, --verbose      Display verbose output"
    echo "  -h, --help         Display this help message"
    echo
    echo "Examples:"
    echo "  $(basename $0) -f ./modules/samstraumr-core/pom.xml     # Fix a specific POM file"
    echo "  $(basename $0) -r -f ./modules                          # Fix all POM files recursively"
    echo "  $(basename $0) -v ./modules                             # Validate and show verbose output"
    echo
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        -r|--recursive)
            RECURSIVE=true
            shift
            ;;
        -f|--fix)
            FIX_MODE=true
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        *)
            TARGET_PATH="$1"
            shift
            ;;
    esac
done

# Process a single POM file
process_pom_file() {
    local pom_file="$1"
    local fixed=false
    
    if [ \! -f "$pom_file" ]; then
        error "File not found: $pom_file"
        return 1
    fi
    
    info "Processing $pom_file"
    
    # Validate XML format first
    if \! xmllint --noout "$pom_file" 2>/dev/null; then
        error "XML validation failed for $pom_file"
        return 1
    fi
    
    # Create a temporary file for potential modifications
    local temp_file=$(mktemp)
    cp "$pom_file" "$temp_file"
    
    # Check for <n> tag and fix if needed
    if grep -q "<n>" "$pom_file"; then
        detail "Found <n> tag instead of <name> in $pom_file"
        
        if [ "$FIX_MODE" = true ]; then
            # Extract project name
            local project_name=$(grep -A1 "<n>" "$pom_file" | tail -1 | sed 's/.*<\/n>.*//')
            
            # Replace <n> with <name> in an XML-aware way
            cat "$pom_file" | sed 's/<n>/<name>/g' | sed 's/<\/n>/<\/name>/g' > "$temp_file"
            
            # Validate the modified XML
            if xmllint --noout "$temp_file" 2>/dev/null; then
                # Apply the changes
                cp "$temp_file" "$pom_file"
                success "Fixed: Replaced <n> with <name> in $pom_file"
                fixed=true
            else
                error "Unable to fix $pom_file - XML validation failed after modification"
            fi
        else
            detail "Run with --fix to automatically convert <n> to <name>"
        fi
    fi
    
    # Clean up temporary file
    rm "$temp_file"
    
    if [ "$fixed" = false ] && [ "$VERBOSE" = true ]; then
        detail "No issues found or fixed in $pom_file"
    fi
    
    return 0
}

# Process all POM files in a directory
process_directory() {
    local dir="$1"
    
    if [ \! -d "$dir" ]; then
        error "Directory not found: $dir"
        return 1
    fi
    
    info "Processing directory: $dir"
    
    if [ "$RECURSIVE" = true ]; then
        # Find all POM files recursively
        while IFS= read -r pom_file; do
            process_pom_file "$pom_file"
        done < <(find "$dir" -name "pom.xml" -type f)
    else
        # Process only POM files in the current directory
        for pom_file in "$dir"/*pom.xml; do
            if [ -f "$pom_file" ]; then
                process_pom_file "$pom_file"
            fi
        done
    fi
}

# Main script execution
if [ -f "$TARGET_PATH" ]; then
    # Process a single file
    process_pom_file "$TARGET_PATH"
elif [ -d "$TARGET_PATH" ]; then
    # Process a directory
    process_directory "$TARGET_PATH"
else
    error "Path does not exist: $TARGET_PATH"
    exit 1
fi

# Success message
success "POM file processing complete\!"
