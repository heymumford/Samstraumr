#!/bin/bash
#==============================================================================
# Filename: test-config.sh
# Description: Test and validate the unified configuration system
#==============================================================================

set -e

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration
source "${SCRIPT_DIR}/config.sh"

echo "==== Testing Samstraumr Unified Configuration ===="
echo "Project name: ${SAMSTRAUMR_NAME}"
echo "Project version: ${SAMSTRAUMR_VERSION}"
echo "Core module: ${SAMSTRAUMR_CORE_MODULE}"
echo ""

echo "Testing path resolution:"
echo "Java main directory exists: $([ -d "${SAMSTRAUMR_JAVA_MAIN}" ] && echo "YES" || echo "NO")"
echo "Java test directory exists: $([ -d "${SAMSTRAUMR_JAVA_TEST}" ] && echo "YES" || echo "NO")"
echo "Resources main directory exists: $([ -d "${SAMSTRAUMR_RESOURCES_MAIN}" ] && echo "YES" || echo "NO")"
echo "Resources test directory exists: $([ -d "${SAMSTRAUMR_RESOURCES_TEST}" ] && echo "YES" || echo "NO")"
echo ""

echo "Testing helper functions:"
echo "path_for_package org.s8r.core.tube: $(path_for_package "org.s8r.core.tube")"
echo "absolute_path util/bin: $(absolute_path "util/bin")"
echo ""

echo "==== Configuration Test Complete ===="