#!/bin/bash
#==============================================================================
# map-atl-tests.sh: Map ATL tests across the codebase
# This script finds all tests marked with @ATL and generates a report
#==============================================================================

set -e

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../../.." && pwd)"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
RESET='\033[0m'

echo -e "${BLUE}Mapping ATL tests across the codebase${RESET}"

# Define the output file
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
OUTPUT_FILE="${PROJECT_ROOT}/target/atl-test-map-${TIMESTAMP}.txt"
mkdir -p "${PROJECT_ROOT}/target"

# Find all Java files with @ATL annotation
echo -e "${YELLOW}Finding all ATL annotated test classes...${RESET}"
find "${PROJECT_ROOT}" -type f -name "*.java" -exec grep -l "@ATL" {} \; > "${OUTPUT_FILE}.raw"

# Process the file to extract class names and package info
echo -e "${YELLOW}Processing test classes...${RESET}"

echo "# ATL Test Map - Generated $(date)" > "${OUTPUT_FILE}"
echo "This file contains a mapping of all ATL tests in the codebase." >> "${OUTPUT_FILE}"
echo "" >> "${OUTPUT_FILE}"
echo "## Test Classes by Package" >> "${OUTPUT_FILE}"
echo "" >> "${OUTPUT_FILE}"

# Count the total number of ATL classes
TOTAL_COUNT=$(wc -l < "${OUTPUT_FILE}.raw")
echo "Found ${TOTAL_COUNT} ATL test classes across the codebase."
echo "" >> "${OUTPUT_FILE}"
echo "Total ATL Test Classes: ${TOTAL_COUNT}" >> "${OUTPUT_FILE}"
echo "" >> "${OUTPUT_FILE}"

# Sort by package and extract info
current_package=""
while IFS= read -r file; do
    if [ -f "$file" ]; then
        package=$(grep -m 1 "package" "$file" | sed 's/package\s\+\([^;]\+\);/\1/')
        class=$(basename "$file" .java)
        
        # If the package changed, print the new package header
        if [ "$current_package" != "$package" ]; then
            echo "### Package: $package" >> "${OUTPUT_FILE}"
            current_package="$package"
        fi
        
        # Extract the class comment if available
        comment=$(grep -B 1 -m 1 "public class $class" "$file" | grep -m 1 "\*" | sed 's/\s*\*\s*//')
        if [ -z "$comment" ]; then
            comment="No description available"
        fi
        
        echo "- **$class**: $comment" >> "${OUTPUT_FILE}"
    fi
done < <(sort "${OUTPUT_FILE}.raw")

# Clean up temp file
rm "${OUTPUT_FILE}.raw"

echo -e "${GREEN}ATL test mapping complete!${RESET}"
echo "Report generated at: ${OUTPUT_FILE}"

# Count ATL tests in each primary package
echo -e "${YELLOW}ATL Test Distribution:${RESET}"
echo "-----------------------------"
find "${PROJECT_ROOT}" -type f -name "*.java" -exec grep -l "@ATL" {} \; | grep -v "stub" | sort | awk -F'/' '{
    for(i=1; i<=NF; i++) {
        if ($i == "java") {
            if ($(i+1) == "org" && $(i+2) == "s8r") {
                if ($(i+3) in counts) {
                    counts[$(i+3)]++
                } else {
                    counts[$(i+3)] = 1
                }
            }
        }
    }
} 
END {
    for (pkg in counts) {
        printf "%-20s: %d\n", pkg, counts[pkg]
    }
}'