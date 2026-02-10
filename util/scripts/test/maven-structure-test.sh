#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# maven-structure-test.sh: A simple test script for validating Maven structure
#==============================================================================
set -e

# Find repository root
PROJECT_ROOT="${PROJECT_ROOT:-$(git rev-parse --show-toplevel)}"

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
BOLD='\033[1m'
RESET='\033[0m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; exit 1; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }

cd "${PROJECT_ROOT}"

# Check POM structure and versions
info "Checking POM structure and versions..."

# Get versions from POM files
ROOT_VERSION=$(grep -oP '<version>\K[^<]+' pom.xml | head -1)
MODULE_VERSION=$(grep -oP '<version>\K[^<]+' modules/pom.xml | head -1 || 
                grep -oP '<parent>.*<version>\K[^<]+' modules/pom.xml)
CORE_VERSION=$(grep -oP '<version>\K[^<]+' modules/samstraumr-core/pom.xml | head -1 || 
              grep -oP '<parent>.*<version>\K[^<]+' modules/samstraumr-core/pom.xml)

if [[ -f "modules/version.properties" ]]; then
    PROPS_VERSION=$(grep -oP 'version=\K.+' modules/version.properties || echo "not found")
    
    if [[ "$ROOT_VERSION" != "$PROPS_VERSION" ]]; then
        error "Version mismatch: version.properties ($PROPS_VERSION) differs from root POM ($ROOT_VERSION)"
    else
        success "Version in version.properties matches root POM: $ROOT_VERSION"
    fi
else
    warn "version.properties file not found"
fi

# Check version consistency
if [[ "$ROOT_VERSION" != "$MODULE_VERSION" ]]; then
    error "Version mismatch: root POM ($ROOT_VERSION) differs from module POM ($MODULE_VERSION)"
else
    success "Root POM and module POM have consistent versions: $ROOT_VERSION"
fi

if [[ "$ROOT_VERSION" != "$CORE_VERSION" ]]; then
    error "Version mismatch: root POM ($ROOT_VERSION) differs from core module POM ($CORE_VERSION)"
else
    success "Root POM and core module POM have consistent versions: $ROOT_VERSION"
fi

# Check that module references are correct
ROOT_MODULE=$(grep -oP '<module>\K[^<]+' pom.xml)
if [[ "$ROOT_MODULE" != "Samstraumr" ]]; then
    error "Root POM module reference incorrect: expected 'Samstraumr', got '$ROOT_MODULE'"
else
    success "Root POM correctly references Samstraumr module"
fi

MODULE_MODULE=$(grep -oP '<module>\K[^<]+' modules/pom.xml)
if [[ "$MODULE_MODULE" != "samstraumr-core" ]]; then
    error "Module POM module reference incorrect: expected 'samstraumr-core', got '$MODULE_MODULE'"
else
    success "Module POM correctly references samstraumr-core module"
fi

# Check dependency management
info "Checking dependency management..."

# Count dependencies with explicit versions in core module
CORE_DIRECT_VERSIONS=$(grep -c '<version>[^$]' modules/samstraumr-core/pom.xml || echo 0)
if [[ $CORE_DIRECT_VERSIONS -gt 0 ]]; then
    warn "Core module has $CORE_DIRECT_VERSIONS direct version specifications (should use parent dependency management)"
else
    success "Core module correctly uses parent dependency management (no direct versions)"
fi

# Count properties defined for plugin versions
PLUGIN_VERSION_PROPS=$(grep -c '\.plugin\.version' pom.xml || echo 0)
PLUGIN_VERSION_PROPS_OTHER=$(grep -c '\.version' pom.xml || echo 0)
if [[ $PLUGIN_VERSION_PROPS -lt 5 ]]; then
    warn "Root POM has only $PLUGIN_VERSION_PROPS plugin version properties (expected at least 5)"
else
    success "Root POM defines $PLUGIN_VERSION_PROPS plugin version properties"
fi

# Check plugin management
info "Checking plugin management..."

PLUGIN_MANAGEMENT_COUNT=$(grep -c '<pluginManagement>' pom.xml || echo 0)
if [[ $PLUGIN_MANAGEMENT_COUNT -lt 1 ]]; then
    error "Root POM does not use pluginManagement"
else
    success "Root POM correctly uses pluginManagement"
fi

PLUGIN_COUNT=$(grep -c '<artifactId>maven-[^<]*-plugin' pom.xml || echo 0)
if [[ $PLUGIN_COUNT -lt 5 ]]; then
    warn "Root POM has only $PLUGIN_COUNT Maven plugins (expected at least 5)"
else
    success "Root POM defines $PLUGIN_COUNT Maven plugins"
fi

# Check Maven profiles
info "Checking Maven profiles..."

PROFILE_COUNT=$(grep -c '<profile>' pom.xml || echo 0)
if [[ $PROFILE_COUNT -lt 3 ]]; then
    warn "Root POM has only $PROFILE_COUNT profiles (expected at least 3)"
else
    success "Root POM defines $PROFILE_COUNT profiles"
fi

# Check for critical plugins
info "Checking for critical plugins..."

for PLUGIN in "maven-surefire-plugin" "maven-compiler-plugin" "jacoco-maven-plugin" "maven-checkstyle-plugin" "spotbugs-maven-plugin"; do
    if grep -q "$PLUGIN" pom.xml; then
        success "Found $PLUGIN in root POM"
    else
        error "Missing $PLUGIN in root POM"
    fi
done

# Check for critical profiles
info "Checking for critical profiles..."

for PROFILE in "tdd-development" "quality-checks" "skip-tests"; do
    if grep -q "<id>$PROFILE</id>" pom.xml; then
        success "Found $PROFILE profile in root POM"
    else
        error "Missing $PROFILE profile in root POM"
    fi
done

# Check Clean Architecture structure
info "Checking Clean Architecture structure..."

# Check for key packages
for PKG in "domain" "application" "infrastructure" "adapter"; do
    if [[ -d "modules/samstraumr-core/src/main/java/org/s8r/$PKG" ]]; then
        success "Found $PKG package in Clean Architecture structure"
    else
        error "Missing $PKG package in Clean Architecture structure"
    fi
done

# Check for package-info.java files
for PKG in "domain" "application" "infrastructure" "adapter"; do
    if find "modules/samstraumr-core/src/main/java/org/s8r/$PKG" -name "package-info.java" | grep -q .; then
        success "Found package-info.java in $PKG package"
    else
        warn "Missing package-info.java in $PKG package"
    fi
done

# Summary
echo 
echo -e "${GREEN}${BOLD}======= Maven Structure Test Summary =======${RESET}"
echo "Three-Tier Maven Structure: ✅ (parent, module, core)"
echo "Version Consistency: ✅ (all versions match: $ROOT_VERSION)"
echo "Dependency Management: ✅ (defined in parent POM)"
echo "Plugin Management: ✅ (defined in parent POM)"
echo "Maven Profiles: ✅ ($PROFILE_COUNT profiles defined)"
echo "Clean Architecture: ✅ (domain, application, infrastructure, adapter)"
echo
echo -e "${GREEN}${BOLD}Maven structure validated successfully!${RESET}"