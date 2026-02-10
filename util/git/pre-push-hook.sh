#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

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
# Git pre-push hook to check XML files and increment build number

set -e

# Repository root
REPO_ROOT="$(git rev-parse --show-toplevel)"

# Path to version properties
VERSION_FILE="${REPO_ROOT}/modules/version.properties"

# Track XML check execution
XML_CHECK_COUNTER_FILE="${REPO_ROOT}/.xml_check_counter"
XML_CHECK_FREQUENCY=10  # Run XML check every 10 builds

# Increment build number
if [ -f "$VERSION_FILE" ]; then
    # Get the current build number
    BUILD_NUMBER=$(grep "^build.number=" "$VERSION_FILE" | cut -d= -f2 || echo "0")
    # Increment it
    NEW_BUILD_NUMBER=$((BUILD_NUMBER + 1))
    # Update the file
    sed -i "s/^build.number=.*/build.number=$NEW_BUILD_NUMBER/" "$VERSION_FILE"
    echo "Build number incremented to $NEW_BUILD_NUMBER"
    
    # Determine if we should run the XML check
    if [ -f "$XML_CHECK_COUNTER_FILE" ]; then
        COUNT=$(cat "$XML_CHECK_COUNTER_FILE")
    else
        COUNT=0
    fi
    
    # Calculate whether to run XML check based on frequency
    if [ $((NEW_BUILD_NUMBER % XML_CHECK_FREQUENCY)) -eq 0 ] || [ $COUNT -eq 0 ]; then
        RUN_XML_CHECK=1
        echo "Build number $NEW_BUILD_NUMBER is a multiple of $XML_CHECK_FREQUENCY, running XML check"
    else
        RUN_XML_CHECK=0
        # Update counter
        echo $((COUNT + 1)) > "$XML_CHECK_COUNTER_FILE"
        echo "XML check skipped (will run every $XML_CHECK_FREQUENCY builds, current count: $((COUNT + 1)))"
    fi
    
    # Run the XML check if needed
    if [ $RUN_XML_CHECK -eq 1 ]; then
        # Reset counter
        echo "1" > "$XML_CHECK_COUNTER_FILE"
        
        echo "Running XML standardization check..."
        if [ -f "${REPO_ROOT}/s8r-xml-standardize" ]; then
            "${REPO_ROOT}/s8r-xml-standardize" --check --pom-only
            if [ $? -ne 0 ]; then
                echo "XML check failed. Please run ${REPO_ROOT}/s8r-xml-standardize to fix issues."
                exit 1
            fi
        else
            echo "XML standardization script not found, skipping check."
        fi
    fi
    
    # Add the updated version file to the current commit
    git add "$VERSION_FILE"
else
    echo "Version file not found at $VERSION_FILE"
    exit 1
fi

# Continue with push
exit 0