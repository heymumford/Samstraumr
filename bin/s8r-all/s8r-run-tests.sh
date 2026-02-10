#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# s8r-run-tests: Main test runner for Samstraumr CLI tests
# Executes tests at each layer of the testing pyramid
#==============================================================================
set -e

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the test framework
source "${SCRIPT_DIR}/s8r-test-framework.sh"

# Source test suites
source "${SCRIPT_DIR}/s8r-unit-tests.sh"
source "${SCRIPT_DIR}/s8r-component-tests.sh"
source "${SCRIPT_DIR}/s8r-composite-tests.sh"
source "${SCRIPT_DIR}/s8r-machine-tests.sh"
source "${SCRIPT_DIR}/s8r-system-tests.sh"
source "${SCRIPT_DIR}/s8r-acceptance-tests.sh"

# Parse command line arguments
LAYER=""
VERBOSE=false

# Process command-line options
while [ $# -gt 0 ]; do
    case "$1" in
        --layer=*)
            LAYER="${1#*=}"
            shift
            ;;
        --unit)
            LAYER="UNIT"
            shift
            ;;
        --component)
            LAYER="COMPONENT"
            shift
            ;;
        --composite)
            LAYER="COMPOSITE"
            shift
            ;;
        --machine)
            LAYER="MACHINE"
            shift
            ;;
        --system)
            LAYER="SYSTEM"
            shift
            ;;
        --acceptance)
            LAYER="ACCEPTANCE"
            shift
            ;;
        --all)
            LAYER="ALL"
            shift
            ;;
        --verbose)
            VERBOSE=true
            shift
            ;;
        -v)
            VERBOSE=true
            shift
            ;;
        --help|-h)
            echo "Usage: ./s8r-run-tests.sh [options]"
            echo ""
            echo "Options:"
            echo "  --layer=LAYER   Run tests for a specific layer (UNIT, COMPONENT, COMPOSITE, MACHINE, SYSTEM, ACCEPTANCE)"
            echo "  --unit          Run unit tests only"
            echo "  --component     Run component tests only"
            echo "  --composite     Run composite tests only"
            echo "  --machine       Run machine tests only"
            echo "  --system        Run system tests only"
            echo "  --acceptance    Run acceptance tests only (validates known issues)"
            echo "  --all           Run all tests (default)"
            echo "  --verbose, -v   Enable verbose output"
            echo "  --help, -h      Show this help message"
            exit 0
            ;;
        *)
            echo "Unknown option: $1"
            echo "Use --help to see available options"
            exit 1
            ;;
    esac
done

# Default to running all tests if no layer is specified
if [ -z "$LAYER" ]; then
    LAYER="ALL"
fi

# Set verbose flag for test framework
if [ "$VERBOSE" = true ]; then
    export TEST_VERBOSE=true
fi

# Run tests based on the specified layer
if [ "$LAYER" = "ALL" ] || [ "$LAYER" = "UNIT" ]; then
    run_unit_tests
fi

if [ "$LAYER" = "ALL" ] || [ "$LAYER" = "COMPONENT" ]; then
    run_component_tests
fi

if [ "$LAYER" = "ALL" ] || [ "$LAYER" = "COMPOSITE" ]; then
    run_composite_tests
fi

if [ "$LAYER" = "ALL" ] || [ "$LAYER" = "MACHINE" ]; then
    run_machine_tests
fi

if [ "$LAYER" = "ALL" ] || [ "$LAYER" = "SYSTEM" ]; then
    run_system_tests
fi

if [ "$LAYER" = "ALL" ] || [ "$LAYER" = "ACCEPTANCE" ]; then
    run_acceptance_tests
fi

# Print test summary
print_summary

# Exit with success if all tests passed
exit 0