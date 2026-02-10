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
# Licensed under the Mozilla Public License 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.mozilla.org/en-US/MPL/2.0/
#

# Script to find potential test scenarios in markdown files

# Set project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# Functions for output formatting
print_info() {
    echo -e "\033[0;34m[INFO]\033[0m $1"
}

print_success() {
    echo -e "\033[0;32m[SUCCESS]\033[0m $1"
}

print_error() {
    echo -e "\033[0;31m[ERROR]\033[0m $1" >&2
}

print_banner() {
    local text="$1"
    local length=${#text}
    local line=$(printf '%*s' "$length" | tr ' ' '=')
    
    echo -e "\n\033[1;36m$line\033[0m"
    echo -e "\033[1;36m$text\033[0m"
    echo -e "\033[1;36m$line\033[0m\n"
}

# Function to find potential test scenarios in markdown files
find_test_scenarios() {
    local search_dir=$1
    local output_file=$2
    
    print_info "Searching for test scenarios in $search_dir..."
    
    # Initialize output file
    echo "# Potential Test Scenarios Found in Markdown Files" > "$output_file"
    echo "Generated on: $(date)" >> "$output_file"
    echo "" >> "$output_file"
    
    # Find markdown files
    local md_files=$(find "$search_dir" -name "*.md" -not -name "README.md" -not -path "*/target/*")
    
    # Count of files to process
    local file_count=$(echo "$md_files" | wc -l)
    print_info "Found $file_count markdown files to analyze"
    
    # Process each file
    local scenario_count=0
    local files_with_scenarios=0
    
    for file in $md_files; do
        local file_scenarios=0
        
        # Check for Gherkin-style scenarios
        if grep -q -E "^\s*(Scenario|Feature):" "$file"; then
            echo "## File: $file" >> "$output_file"
            echo "" >> "$output_file"
            
            # Extract Feature
            grep -A 1 -E "^\s*Feature:" "$file" | sed 's/^/  /' >> "$output_file"
            echo "" >> "$output_file"
            
            # Extract Scenarios
            echo "### Scenarios:" >> "$output_file"
            echo "" >> "$output_file"
            
            while read -r line; do
                echo "- $line" >> "$output_file"
                ((file_scenarios++))
                ((scenario_count++))
            done < <(grep -E "^\s*Scenario:" "$file" | sed 's/^\s*Scenario://')
            
            echo "" >> "$output_file"
            ((files_with_scenarios++))
        fi
        
        # Check for test descriptions that may not be in Gherkin format
        if grep -q -E -i "^\s*(test|verify|check|assert|should)" "$file" && [ $file_scenarios -eq 0 ]; then
            if grep -q -E "^\s*(Scenario|Feature):" "$file"; then
                echo "### Additional Test Descriptions:" >> "$output_file"
            else
                echo "## File: $file" >> "$output_file"
                echo "" >> "$output_file"
                echo "### Test Descriptions:" >> "$output_file"
            fi
            
            echo "" >> "$output_file"
            
            while read -r line; do
                echo "- $line" >> "$output_file"
                ((file_scenarios++))
                ((scenario_count++))
            done < <(grep -E -i "^\s*(test|verify|check|assert|should)" "$file" | head -10)
            
            if [ $file_scenarios -gt 10 ]; then
                echo "- ... (more tests in file)" >> "$output_file"
            fi
            
            echo "" >> "$output_file"
            
            if grep -q -E "^\s*(Scenario|Feature):" "$file"; then
                : # Do nothing, already counted
            else
                ((files_with_scenarios++))
            fi
        fi
    done
    
    # Add summary
    echo "## Summary" >> "$output_file"
    echo "" >> "$output_file"
    echo "- Total markdown files analyzed: $file_count" >> "$output_file"
    echo "- Files containing potential test scenarios: $files_with_scenarios" >> "$output_file"
    echo "- Total potential test scenarios found: $scenario_count" >> "$output_file"
    
    print_success "Analysis complete. Found $scenario_count potential test scenarios in $files_with_scenarios files."
    print_info "Results written to: $output_file"
}

# Main function
main() {
    print_banner "Samstraumr Test Scenario Finder"
    
    local search_dir="${1:-$PROJECT_ROOT}"
    local output_file="${2:-$PROJECT_ROOT/reports/test-scenarios-report.md}"
    
    # Create output directory if it doesn't exist
    mkdir -p "$(dirname "$output_file")"
    
    find_test_scenarios "$search_dir" "$output_file"
}

# Execute main function
main "$@"
