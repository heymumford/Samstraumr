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
# Library of common XML processing functions for Samstraumr scripts

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to check if a file is a valid XML file
is_valid_xml() {
    local file="$1"
    
    # Use xmlstarlet to validate the XML syntax
    if ! xmlstarlet val "$file" > /dev/null 2>&1; then
        echo -e "${RED}Error${NC}: Invalid XML syntax in $file"
        return 1
    fi
    
    return 0
}

# Function to check for redundant dependencies
check_redundant_dependencies() {
    local pom_file="$1"
    local errors=0
    
    # Get all dependency groupId:artifactId combinations
    local deps=$(xmlstarlet sel -t -m "//dependency" -v "concat(groupId,':',artifactId)" -n "$pom_file" | sort)
    
    # Count occurrences and find duplicates
    local duplicates=$(echo "$deps" | sort | uniq -c | sort -nr | awk '$1 > 1 {print $2}')
    
    if [ ! -z "$duplicates" ]; then
        echo -e "${YELLOW}Warning${NC}: Redundant dependencies found in $pom_file:"
        while IFS= read -r dep; do
            local group_id=$(echo "$dep" | cut -d':' -f1)
            local artifact_id=$(echo "$dep" | cut -d':' -f2)
            local versions=$(xmlstarlet sel -t -m "//dependency[groupId='$group_id' and artifactId='$artifact_id']" -v "version" -n "$pom_file" | sort | uniq)
            echo -e "  ${YELLOW}$group_id:$artifact_id${NC} with versions: ${versions//$'\n'/, }"
            errors=$((errors + 1))
        done <<< "$duplicates"
    fi
    
    return $errors
}

# Function to check for redundant plugins
check_redundant_plugins() {
    local pom_file="$1"
    local errors=0
    
    # Get all plugin groupId:artifactId combinations
    local plugins=$(xmlstarlet sel -t -m "//plugin" -v "concat(groupId,':',artifactId)" -n "$pom_file" | sort)
    
    # Count occurrences and find duplicates
    local duplicates=$(echo "$plugins" | sort | uniq -c | sort -nr | awk '$1 > 1 {print $2}')
    
    if [ ! -z "$duplicates" ]; then
        echo -e "${YELLOW}Warning${NC}: Redundant plugins found in $pom_file:"
        while IFS= read -r plugin; do
            local group_id=$(echo "$plugin" | cut -d':' -f1)
            local artifact_id=$(echo "$plugin" | cut -d':' -f2)
            local versions=$(xmlstarlet sel -t -m "//plugin[groupId='$group_id' and artifactId='$artifact_id']" -v "version" -n "$pom_file" | sort | uniq)
            echo -e "  ${YELLOW}$group_id:$artifact_id${NC} with versions: ${versions//$'\n'/, }"
            errors=$((errors + 1))
        done <<< "$duplicates"
    fi
    
    return $errors
}

# Function to check for property placeholders in versions
check_property_placeholders() {
    local pom_file="$1"
    local errors=0
    
    # Find all version elements that use properties
    local property_versions=$(xmlstarlet sel -t -m "//*[name()='version' and contains(text(), '${')]" -v "concat(../groupId, ':', ../artifactId, ':', .)" -n "$pom_file")
    
    if [ ! -z "$property_versions" ]; then
        # For each property reference, check if the property is defined
        while IFS= read -r line; do
            local property=$(echo "$line" | grep -o '\${[^}]*}')
            local property_name=${property:2:-1}
            
            # Check if property exists in the POM
            if ! xmlstarlet sel -t -v "//properties/$property_name" "$pom_file" > /dev/null 2>&1; then
                echo -e "${YELLOW}Warning${NC}: Property $property_name referenced but not defined in $pom_file"
                errors=$((errors + 1))
            fi
        done <<< "$property_versions"
    fi
    
    return $errors
}

# Function to pretty-print XML
prettify_xml() {
    local xml_file="$1"
    local indent="$2"
    
    # Default indent to 2 spaces if not specified
    [ -z "$indent" ] && indent=2
    
    # Create a temporary file
    local temp_file="$(mktemp)"
    
    # Format the XML
    xmlstarlet format --indent-spaces "$indent" "$xml_file" > "$temp_file"
    
    # Replace the original file with the formatted one
    mv "$temp_file" "$xml_file"
}