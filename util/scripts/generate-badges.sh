#!/bin/bash
# Samstraumr Badge Generator
# Generate badge markdown for README and other documentation

set -e

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

# Get version information
VERSION=$(./util/version export)

# Generate badges
VERSION_BADGE="[![Version](https://img.shields.io/badge/version-$VERSION-blue)](https://github.com/heymumford/modules/releases)"
BUILD_BADGE="[![Build Status](https://github.com/heymumford/modules/actions/workflows/samstraumr-pipeline.yml/badge.svg)](https://github.com/heymumford/modules/actions/workflows/samstraumr-pipeline.yml)"
LICENSE_BADGE="[![License: MPL 2.0](https://img.shields.io/badge/License-MPL%202.0-brightgreen.svg)](https://opensource.org/licenses/MPL-2.0)"
JAVA_BADGE="[![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://openjdk.java.net/projects/jdk/17/)"

generate_version_badge() {
    echo "$VERSION_BADGE"
}

generate_build_badge() {
    echo "$BUILD_BADGE"
}

generate_license_badge() {
    echo "$LICENSE_BADGE"
}

generate_java_badge() {
    echo "$JAVA_BADGE"
}

# Combined badges
generate_all_badges() {
    echo "$(generate_version_badge) $(generate_build_badge) $(generate_license_badge) $(generate_java_badge)"
}

# Output badges
if [ "$1" == "all" ]; then
    generate_all_badges
elif [ "$1" == "version" ]; then
    generate_version_badge
elif [ "$1" == "build" ]; then
    generate_build_badge
elif [ "$1" == "license" ]; then
    generate_license_badge
elif [ "$1" == "java" ]; then
    generate_java_badge
else
    echo "Usage: $0 [all|version|build|license|java]"
    echo "Generate badges for Samstraumr documentation"
    echo ""
    echo "  all      Generate all badges"
    echo "  version  Generate version badge"
    echo "  build    Generate build badge"
    echo "  license  Generate license badge"
    echo "  java     Generate java version badge"
fi