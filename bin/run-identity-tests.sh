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

# Script to run the Atomic Tube Identity tests

# Set project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

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

# Main function
main() {
    print_banner "Samstraumr Atomic Tube Identity Tests"
    
    cd "${PROJECT_ROOT}/Samstraumr"
    
    print_info "Compiling test classes first..."
    mvn compiler:testCompile
    
    print_info "Running Atomic Tube Identity tests..."
    mvn test -Dtest=org.s8r.test.runner.AtomicTubeIdentityTests -DskipTests=false
    
    local exit_code=$?
    
    if [ $exit_code -eq 0 ]; then
        print_success "Tests completed successfully\!"
    else
        print_error "Tests failed with exit code: $exit_code"
    fi
    
    return $exit_code
}

# Execute main function
main "$@"
