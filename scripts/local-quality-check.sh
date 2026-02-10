#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

###############################################################################
# Local Quality Check Script
# Runs the same quality gates as CI before you commit
#
# Usage:
#   ./scripts/local-quality-check.sh [--fast|--full|--fix]
#
# Options:
#   --fast    Run fast checks only (format, compile, unit tests)
#   --full    Run comprehensive checks (includes PMD, SpotBugs, OWASP)
#   --fix     Auto-fix issues where possible (formatting)
###############################################################################

set -euo pipefail  # Exit on error, undefined variables, pipe failures

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MODE="${1:---fast}"

print_header() {
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}→ $1${NC}"
}

cd "$PROJECT_ROOT"

print_header "Samstraumr Local Quality Check"
echo ""
print_info "Mode: $MODE"
print_info "Project: $PROJECT_ROOT"
echo ""

###############################################################################
# Stage 1: Code Formatting
###############################################################################
if [ "$MODE" == "--fix" ]; then
    print_header "Stage 1: Auto-fixing Code Format"
    print_info "Running Spotless apply..."
    if mvn spotless:apply -q; then
        print_success "Code formatting applied"
    else
        print_error "Failed to apply code formatting"
        exit 1
    fi
else
    print_header "Stage 1: Code Format Check"
    print_info "Running Spotless check..."
    if mvn spotless:check -q 2>&1 | grep -q "BUILD SUCCESS"; then
        print_success "Code formatting is correct"
    else
        print_error "Code formatting issues found"
        print_warning "Run './scripts/local-quality-check.sh --fix' to auto-fix"
        exit 1
    fi
fi

###############################################################################
# Stage 2: Checkstyle
###############################################################################
print_header "Stage 2: Checkstyle"
print_info "Running Checkstyle..."
if mvn checkstyle:check -q 2>&1 | grep -q "BUILD SUCCESS"; then
    print_success "Checkstyle passed"
else
    print_error "Checkstyle violations found"
    print_warning "Check target/checkstyle-result.xml for details"
    if [ "$MODE" != "--full" ]; then
        exit 1
    fi
fi

###############################################################################
# Stage 3: Compilation
###############################################################################
print_header "Stage 3: Compilation Check"
print_info "Compiling project..."
if mvn clean compile -DskipTests -q 2>&1 | tail -1 | grep -q "BUILD SUCCESS"; then
    print_success "Compilation successful"
else
    print_error "Compilation failed"
    exit 1
fi

###############################################################################
# Stage 4: Unit Tests
###############################################################################
print_header "Stage 4: Unit Tests"
print_info "Running unit tests..."
if mvn test -Dtest.level=unit -q 2>&1 | tail -5 | grep -q "BUILD SUCCESS"; then
    print_success "Unit tests passed"
else
    print_error "Unit tests failed"
    print_warning "Check target/surefire-reports/ for details"
    if [ "$MODE" != "--full" ]; then
        exit 1
    fi
fi

###############################################################################
# Fast mode stops here
###############################################################################
if [ "$MODE" == "--fast" ] || [ "$MODE" == "--fix" ]; then
    echo ""
    print_header "✓ Fast Quality Checks Complete"
    echo ""
    print_success "All fast checks passed!"
    print_info "Run with --full for comprehensive analysis"
    echo ""
    exit 0
fi

###############################################################################
# Stage 5: PMD Analysis (Full mode only)
###############################################################################
print_header "Stage 5: PMD Analysis"
print_info "Running PMD..."
if mvn pmd:check -q 2>&1 | grep -q "BUILD SUCCESS"; then
    print_success "PMD analysis passed"
else
    print_warning "PMD violations found (non-blocking)"
    print_info "Check target/pmd.xml for details"
fi

###############################################################################
# Stage 6: SpotBugs Analysis (Full mode only)
###############################################################################
print_header "Stage 6: SpotBugs Analysis"
print_info "Running SpotBugs..."
if mvn spotbugs:check -q 2>&1 | grep -q "BUILD SUCCESS"; then
    print_success "SpotBugs analysis passed"
else
    print_warning "SpotBugs issues found (non-blocking)"
    print_info "Check target/spotbugsXml.xml for details"
fi

###############################################################################
# Stage 7: Integration Tests (Full mode only)
###############################################################################
print_header "Stage 7: Integration Tests"
print_info "Running integration tests..."
if mvn verify -Dtest.level=integration -q 2>&1 | tail -5 | grep -q "BUILD SUCCESS"; then
    print_success "Integration tests passed"
else
    print_warning "Integration tests failed (non-blocking)"
fi

###############################################################################
# Stage 8: OWASP Dependency Check (Full mode only)
###############################################################################
print_header "Stage 8: OWASP Dependency Check"
print_info "Checking dependencies for vulnerabilities..."
print_warning "This may take 5-10 minutes on first run..."
if mvn org.owasp:dependency-check-maven:check -q 2>&1 | grep -q "BUILD SUCCESS"; then
    print_success "No critical vulnerabilities found"
else
    print_warning "Vulnerabilities detected (non-blocking)"
    print_info "Check target/dependency-check-report.html for details"
fi

###############################################################################
# Final Summary
###############################################################################
echo ""
print_header "✓ Comprehensive Quality Checks Complete"
echo ""
print_success "All checks completed!"
print_info "Review warnings above for non-blocking issues"
echo ""
print_info "Next steps:"
echo "  1. Fix any warnings if possible"
echo "  2. Commit your changes"
echo "  3. Push to trigger GitHub Actions CI"
echo ""
