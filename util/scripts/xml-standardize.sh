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
#
# Script to standardize XML files in the repository, focusing on POM files

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

# Load common functions
if [ -f "$SCRIPT_DIR/../lib/common.sh" ]; then
    source "$SCRIPT_DIR/../lib/common.sh"
else
    echo -e "${RED}Error: common.sh not found${NC}"
    exit 1
fi

# Load XML library functions
if [ -f "$SCRIPT_DIR/../lib/xml-lib.sh" ]; then
    source "$SCRIPT_DIR/../lib/xml-lib.sh"
else
    echo -e "${YELLOW}Warning: xml-lib.sh not found, continuing without it${NC}"
fi

# Default options
DRY_RUN=0
VERBOSE=0
CHECK_ONLY=0
FILES_UPDATED=0
ERRORS_FOUND=0
SKIP_BACKUP=0
FORMAT_ONLY=0
SCAN_DIR="$PROJECT_ROOT"
XML_FILES=()
OUTPUT_FILE=""

# Function to show help
show_help() {
    echo "Usage: $0 [options]"
    echo "Standardizes XML files in the repository according to best practices"
    echo ""
    echo "Options:"
    echo "  -h, --help              Show this help message"
    echo "  -d, --dry-run           Show what would be changed without making changes"
    echo "  -v, --verbose           Show detailed output"
    echo "  -c, --check-only        Only check for issues without modifying files"
    echo "  -f, --format-only       Only format files without checking dependencies"
    echo "  -s, --skip-backup       Skip creating backups of modified files"
    echo "  -o, --output FILE       Output results to specified file (default: stdout)"
    echo "  -p, --path DIR          Path to scan (default: project root)"
    echo "  --pom-only              Only process POM files"
    echo "  --file FILE             Process a specific file"
    echo ""
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        -h|--help)
            show_help
            exit 0
            ;;
        -d|--dry-run)
            DRY_RUN=1
            shift
            ;;
        -v|--verbose)
            VERBOSE=1
            shift
            ;;
        -c|--check-only)
            CHECK_ONLY=1
            shift
            ;;
        -f|--format-only)
            FORMAT_ONLY=1
            shift
            ;;
        -s|--skip-backup)
            SKIP_BACKUP=1
            shift
            ;;
        -o|--output)
            OUTPUT_FILE="$2"
            shift
            shift
            ;;
        -p|--path)
            SCAN_DIR="$2"
            shift
            shift
            ;;
        --pom-only)
            POM_ONLY=1
            shift
            ;;
        --file)
            XML_FILES+=("$2")
            shift
            shift
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            show_help
            exit 1
            ;;
    esac
done

# Redirect output if output file is specified
if [ ! -z "$OUTPUT_FILE" ]; then
    exec > >(tee "$OUTPUT_FILE") 2>&1
fi

# Function to clean and standardize POM file
standardize_pom() {
    local pom_file="$1"
    local output_file="$(mktemp)"
    local errors=0
    
    if [ $VERBOSE -eq 1 ]; then
        echo -e "${BLUE}Processing${NC}: $pom_file"
    fi
    
    # Create backup if modifying and not skipping backup
    if [ $CHECK_ONLY -eq 0 ] && [ $DRY_RUN -eq 0 ] && [ $SKIP_BACKUP -eq 0 ]; then
        local backup_file="${pom_file}.bak"
        cp "$pom_file" "$backup_file"
        if [ $VERBOSE -eq 1 ]; then
            echo -e "${BLUE}Created backup${NC}: $backup_file"
        fi
    fi
    
    # Format XML for consistent indentation and structure
    xmlstarlet format --indent-spaces 4 "$pom_file" > "$output_file"
    
    # Perform specific POM standardizations if not format-only
    if [ $FORMAT_ONLY -eq 0 ]; then
        # Ensure project tag is properly formed
        if ! xmlstarlet sel -t -v "/project" "$output_file" > /dev/null 2>&1; then
            echo -e "${RED}Error${NC}: Invalid POM file - missing project tag: $pom_file"
            errors=$((errors + 1))
        fi
        
        # Check for required POM elements
        for element in "modelVersion" "groupId" "artifactId" "version"; do
            if ! xmlstarlet sel -t -v "/project/$element" "$output_file" > /dev/null 2>&1; then
                echo -e "${RED}Error${NC}: Missing required element <$element> in $pom_file"
                errors=$((errors + 1))
            fi
        fi
        
        # Check for consistent plugin versions
        check_plugin_versions "$output_file"
        
        # Check for consistent dependency versions
        check_dependency_versions "$output_file"
        
        # Check for proper parent declaration
        if xmlstarlet sel -t -v "/project/parent" "$output_file" > /dev/null 2>&1; then
            for element in "groupId" "artifactId" "version"; do
                if ! xmlstarlet sel -t -v "/project/parent/$element" "$output_file" > /dev/null 2>&1; then
                    echo -e "${RED}Error${NC}: Parent declaration missing <$element> in $pom_file"
                    errors=$((errors + 1))
                fi
            done
        fi
        
        # Check for proper relativePath in parent
        check_parent_relative_path "$output_file" "$pom_file"
    fi
    
    # Only update the file if not in check-only or dry-run mode and if there were no errors
    if [ $CHECK_ONLY -eq 0 ] && [ $DRY_RUN -eq 0 ] && [ $errors -eq 0 ]; then
        mv "$output_file" "$pom_file"
        if [ $VERBOSE -eq 1 ]; then
            echo -e "${GREEN}Updated${NC}: $pom_file"
        fi
        FILES_UPDATED=$((FILES_UPDATED + 1))
    else
        rm "$output_file"
        if [ $errors -gt 0 ]; then
            ERRORS_FOUND=$((ERRORS_FOUND + $errors))
        fi
    fi
    
    return $errors
}

# Function to check for consistent plugin versions
check_plugin_versions() {
    local pom_file="$1"
    local errors=0
    
    # Extract all plugin groupId:artifactId:version combinations
    local plugins=$(xmlstarlet sel -t -m "//plugin" -v "concat(groupId,':',artifactId,':',version)" -n "$pom_file" | sort | uniq)
    
    # Check for plugins without versions
    local plugins_without_version=$(xmlstarlet sel -t -m "//plugin[not(version)]" -v "concat(groupId,':',artifactId)" -n "$pom_file")
    if [ ! -z "$plugins_without_version" ]; then
        echo -e "${YELLOW}Warning${NC}: Plugins without version in $pom_file:"
        echo "$plugins_without_version" | sed 's/^/  /'
    fi
    
    # TODO: Compare versions with a central record for consistency
    
    return $errors
}

# Function to check for consistent dependency versions
check_dependency_versions() {
    local pom_file="$1"
    local errors=0
    
    # Extract all dependency groupId:artifactId:version combinations
    local dependencies=$(xmlstarlet sel -t -m "//dependency" -v "concat(groupId,':',artifactId,':',version)" -n "$pom_file" | sort | uniq)
    
    # Check for dependencies without versions
    local deps_without_version=$(xmlstarlet sel -t -m "//dependency[not(version)]" -v "concat(groupId,':',artifactId)" -n "$pom_file")
    if [ ! -z "$deps_without_version" ]; then
        # This might be intentional for managed dependencies, so just note it if verbose
        if [ $VERBOSE -eq 1 ]; then
            echo -e "${BLUE}Note${NC}: Dependencies without explicit version in $pom_file:"
            echo "$deps_without_version" | sed 's/^/  /'
        fi
    fi
    
    # TODO: Compare versions with a central record for consistency
    
    return $errors
}

# Function to check parent relativePath
check_parent_relative_path() {
    local pom_file="$1"
    local original_file="$2"
    local errors=0
    
    # Check if parent declaration exists
    if xmlstarlet sel -t -v "/project/parent" "$pom_file" > /dev/null 2>&1; then
        # Get relativePath or default to ../pom.xml
        local relative_path=$(xmlstarlet sel -t -v "/project/parent/relativePath" "$pom_file" 2>/dev/null || echo "../pom.xml")
        
        # Calculate absolute path
        local pom_dir=$(dirname "$original_file")
        local parent_path="$pom_dir/$relative_path"
        
        # Check if parent POM exists
        if [ ! -f "$parent_path" ]; then
            echo -e "${YELLOW}Warning${NC}: Parent POM referenced by relativePath does not exist: $parent_path"
        fi
    fi
    
    return $errors
}

# Function to standardize a general XML file
standardize_xml() {
    local xml_file="$1"
    local output_file="$(mktemp)"
    local errors=0
    
    if [ $VERBOSE -eq 1 ]; then
        echo -e "${BLUE}Processing${NC}: $xml_file"
    fi
    
    # Create backup if modifying and not skipping backup
    if [ $CHECK_ONLY -eq 0 ] && [ $DRY_RUN -eq 0 ] && [ $SKIP_BACKUP -eq 0 ]; then
        local backup_file="${xml_file}.bak"
        cp "$xml_file" "$backup_file"
        if [ $VERBOSE -eq 1 ]; then
            echo -e "${BLUE}Created backup${NC}: $backup_file"
        fi
    fi
    
    # Format XML for consistent indentation and structure
    xmlstarlet format --indent-spaces 2 "$xml_file" > "$output_file"
    
    # Only update the file if not in check-only or dry-run mode
    if [ $CHECK_ONLY -eq 0 ] && [ $DRY_RUN -eq 0 ]; then
        mv "$output_file" "$xml_file"
        if [ $VERBOSE -eq 1 ]; then
            echo -e "${GREEN}Updated${NC}: $xml_file"
        fi
        FILES_UPDATED=$((FILES_UPDATED + 1))
    else
        rm "$output_file"
    fi
    
    return $errors
}

# Main script logic
echo -e "${BLUE}XML Standardization Script${NC}"
echo -e "${BLUE}=========================${NC}"

if [ ${#XML_FILES[@]} -eq 0 ]; then
    # Find XML files if none were specified
    if [ "$POM_ONLY" == "1" ]; then
        echo -e "${BLUE}Scanning for POM files in${NC}: $SCAN_DIR"
        while IFS= read -r file; do
            XML_FILES+=("$file")
        done < <(find "$SCAN_DIR" -name "pom.xml" -type f | sort)
    else
        echo -e "${BLUE}Scanning for XML files in${NC}: $SCAN_DIR"
        while IFS= read -r file; do
            XML_FILES+=("$file")
        done < <(find "$SCAN_DIR" -name "*.xml" -type f | sort)
    fi
else
    echo -e "${BLUE}Processing specified files${NC}"
fi

# Count files
file_count=${#XML_FILES[@]}
echo -e "${BLUE}Found${NC} $file_count ${BLUE}XML files${NC}"

# Process each file
echo -e "${BLUE}Processing files...${NC}"
for file in "${XML_FILES[@]}"; do
    if [[ "$file" == *"pom.xml" ]]; then
        standardize_pom "$file"
    else
        standardize_xml "$file"
    fi
done

# Summary
echo -e "${BLUE}=========================${NC}"
echo -e "${BLUE}Standardization Summary${NC}"
echo -e "${BLUE}=========================${NC}"
echo -e "Files processed: $file_count"

if [ $DRY_RUN -eq 1 ]; then
    echo -e "Files that would be updated: $FILES_UPDATED"
else
    echo -e "Files updated: $FILES_UPDATED"
fi

echo -e "Errors/warnings found: $ERRORS_FOUND"

if [ $ERRORS_FOUND -gt 0 ]; then
    echo -e "${YELLOW}Please review the issues reported above${NC}"
    exit 1
else
    echo -e "${GREEN}XML standardization completed successfully${NC}"
    exit 0
fi