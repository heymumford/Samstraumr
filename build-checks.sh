#!/bin/bash

# Samstraumr Build Quality Check Script
# This script runs all quality checks and tests

set -e

# Function to print colored output
print_header() {
  echo -e "\033[1;34m===== $1 =====\033[0m"
}

print_success() {
  echo -e "\033[1;32m✓ $1\033[0m"
}

print_error() {
  echo -e "\033[1;31m✗ $1\033[0m"
}

print_warning() {
  echo -e "\033[1;33m! $1\033[0m"
}

# Check if command exists
check_command() {
  if ! command -v $1 &> /dev/null; then
    print_error "$1 is required but not installed."
    exit 1
  fi
}

# Required commands
check_command "mvn"
check_command "java"

# Display Java version
print_header "Java Version"
java -version

# Display Maven version
print_header "Maven Version"
mvn --version | head -n 1

# Step 1: Code formatting check
print_header "Running Code Formatting Check (Spotless)"
mvn spotless:check || {
  print_error "Code formatting check failed"
  print_warning "Run 'mvn spotless:apply' to fix formatting issues"
  exit 1
}
print_success "Code formatting check passed"

# Step 2: Static code analysis (PMD)
print_header "Running Static Code Analysis (PMD)"
mvn pmd:check || {
  print_error "PMD code analysis failed"
  print_warning "Check target/pmd.xml for details"
  exit 1
}
print_success "PMD code analysis passed"

# Step 3: Coding standards check (Checkstyle)
print_header "Running Coding Standards Check (Checkstyle)"
mvn checkstyle:check || {
  print_error "Checkstyle check failed"
  print_warning "Check target/checkstyle-result.xml for details"
  exit 1
}
print_success "Checkstyle check passed"

# Step 4: Bug detection (SpotBugs)
print_header "Running Bug Detection (SpotBugs)"
mvn spotbugs:check || {
  print_error "SpotBugs bug detection failed"
  print_warning "Check target/spotbugsXml.xml for details"
  exit 1
}
print_success "SpotBugs bug detection passed"

# Step 5: Compile
print_header "Compiling Code"
mvn clean compile || {
  print_error "Compilation failed"
  exit 1
}
print_success "Compilation passed"

# Step 6: Run ATL tests first (must pass)
print_header "Running Above-The-Line (ATL) Critical Tests"
mvn test -P atl-tests || {
  print_error "ATL tests failed - critical issues found!"
  print_warning "Check target/cucumber-reports for details"
  exit 1
}
print_success "ATL tests passed"

# Step 7: Run BTL tests (additional quality checks)
print_header "Running Below-The-Line (BTL) Robustness Tests"
mvn test -P btl-tests || {
  print_warning "BTL tests failed - robustness issues found"
  print_warning "Check target/cucumber-reports for details"
  # Don't exit with error for BTL tests - they're not critical
}
print_success "BTL tests completed"

# Step 8: Code coverage check
print_header "Running Code Coverage Analysis (JaCoCo)"
mvn jacoco:report jacoco:check || {
  print_warning "Code coverage below threshold"
  print_warning "Check target/site/jacoco/index.html for details"
  # Don't exit with error for coverage - it's informative
}
print_success "Code coverage analysis completed"

print_header "All Quality Checks Completed"
echo "ATL Tests: Critical tests for core functionality"
echo "BTL Tests: Robustness tests for edge cases and additional quality"

echo ""
echo "Reports available in:"
echo " - Cucumber Report: target/cucumber-reports/cucumber.html"
echo " - JaCoCo Coverage: target/site/jacoco/index.html"
echo " - CheckStyle: target/checkstyle-result.xml"
echo " - PMD: target/pmd.xml"
echo " - SpotBugs: target/spotbugsXml.xml"

# Make script executable
chmod +x "$0"