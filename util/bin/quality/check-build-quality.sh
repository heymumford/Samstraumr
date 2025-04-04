#!/bin/bash

# Samstraumr Build Quality Check Script
# This script runs all quality checks and tests
#
# Usage:
#   ./util/scripts/check-build-quality.sh [OPTIONS]
#
# Options:
#   --skip-spotless      Skip code formatting check (Spotless)
#   --skip-checkstyle    Skip coding standards check (Checkstyle)
#   --skip-spotbugs      Skip bug detection (SpotBugs)
#   --skip-jacoco        Skip code coverage analysis (JaCoCo)
#   --skip-tests         Skip running tests
#   --only=TOOL1,TOOL2   Run only specified tools (spotless,checkstyle,spotbugs,jacoco,tests)
#   --help               Show this help message
#
# Example:
#   ./util/scripts/check-build-quality.sh --skip-spotbugs --skip-jacoco
#   ./util/scripts/check-build-quality.sh --only=spotless,checkstyle

set -e

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." &> /dev/null && pwd 2> /dev/null)"

# Parse command line arguments
SKIP_SPOTLESS=false
SKIP_CHECKSTYLE=false
SKIP_SPOTBUGS=false
SKIP_JACOCO=false
SKIP_TESTS=false
RUN_SPECIFIC=""

show_help() {
  echo "Samstraumr Build Quality Check Script"
  echo ""
  echo "Usage:"
  echo "  ./util/scripts/check-build-quality.sh [OPTIONS]"
  echo ""
  echo "Options:"
  echo "  --skip-spotless      Skip code formatting check (Spotless)"
  echo "  --skip-checkstyle    Skip coding standards check (Checkstyle)"
  echo "  --skip-spotbugs      Skip bug detection (SpotBugs)"
  echo "  --skip-jacoco        Skip code coverage analysis (JaCoCo)"
  echo "  --skip-tests         Skip running tests"
  echo "  --only=TOOL1,TOOL2   Run only specified tools (spotless,checkstyle,spotbugs,jacoco,tests)"
  echo "  --help               Show this help message"
  echo ""
  echo "Example:"
  echo "  ./util/scripts/check-build-quality.sh --skip-spotbugs --skip-jacoco"
  echo "  ./util/scripts/check-build-quality.sh --only=spotless,checkstyle"
  exit 0
}

for arg in "$@"; do
  case $arg in
    --skip-spotless)
      SKIP_SPOTLESS=true
      shift
      ;;
    --skip-checkstyle)
      SKIP_CHECKSTYLE=true
      shift
      ;;
    --skip-spotbugs)
      SKIP_SPOTBUGS=true
      shift
      ;;
    --skip-jacoco)
      SKIP_JACOCO=true
      shift
      ;;
    --skip-tests)
      SKIP_TESTS=true
      shift
      ;;
    --only=*)
      RUN_SPECIFIC="${arg#*=}"
      shift
      ;;
    --help)
      show_help
      ;;
  esac
done

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

# Step 1: File encoding and line ending check
print_header "Checking File Encoding and Line Endings"
if [ -f "$SCRIPT_DIR/check-encoding.sh" ]; then
  $SCRIPT_DIR/check-encoding.sh || {
    print_error "File encoding or line ending check failed"
    print_warning "Run '$SCRIPT_DIR/check-encoding.sh --fix' to fix encoding issues"
    exit 1
  }
  print_success "File encoding and line ending check passed"
else
  print_warning "check-encoding.sh not found - skipping encoding check"
fi

# Declare specific tool functions
run_spotless() {
  print_header "Running Code Formatting Check (Spotless)"
  
  if [ "$SKIP_SPOTLESS" = "true" ]; then
    print_warning "Spotless check skipped as requested"
    return 0
  fi
  
  # First check without applying changes
  if mvn spotless:check; then
    print_success "Spotless check passed - code is properly formatted"
  else
    print_error "Spotless check failed"
    print_warning "Run 'mvn spotless:apply' to fix formatting issues"
    
    # Ask user if they want to auto-format
    echo -n "Would you like to auto-format the code now? (y/n): "
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
      mvn spotless:apply
      print_success "Code formatting applied"
    else
      return 1
    fi
  fi
}

run_checkstyle() {
  print_header "Running Coding Standards Check (Checkstyle)"
  
  if [ "$SKIP_CHECKSTYLE" = "true" ]; then
    print_warning "Checkstyle check skipped as requested"
    return 0
  fi
  
  if mvn checkstyle:check; then
    print_success "Checkstyle check passed - code follows style guidelines"
  else
    print_error "Checkstyle check failed"
    print_warning "Check target/checkstyle-result.xml for details"
    return 1
  fi
}

run_spotbugs() {
  print_header "Running Bug Detection (SpotBugs)"
  
  if [ "$SKIP_SPOTBUGS" = "true" ]; then
    print_warning "SpotBugs check skipped as requested"
    return 0
  fi
  
  if mvn spotbugs:check; then
    print_success "SpotBugs check passed - no potential bugs detected"
  else
    print_error "SpotBugs check failed"
    print_warning "Check target/spotbugsXml.xml for details"
    return 1
  fi
}

run_jacoco() {
  print_header "Running Code Coverage Analysis (JaCoCo)"
  
  if [ "$SKIP_JACOCO" = "true" ]; then
    print_warning "JaCoCo analysis skipped as requested"
    return 0
  fi
  
  # Run JaCoCo report (don't fail on coverage issues)
  if mvn jacoco:report; then
    print_success "JaCoCo report generated successfully"
    
    # Try check but don't exit on failure
    mvn jacoco:check || {
      print_warning "Code coverage below threshold"
      print_warning "Check target/site/jacoco/index.html for details"
    }
  else
    print_error "JaCoCo analysis failed"
    return 1
  fi
  
  print_success "Code coverage analysis completed"
}

run_tests() {
  print_header "Compiling Code"
  mvn clean compile || {
    print_error "Compilation failed"
    exit 1
  }
  print_success "Compilation passed"
  
  if [ "$SKIP_TESTS" = "true" ]; then
    print_warning "Tests skipped as requested"
    return 0
  fi
  
  # Step 1: Run ATL tests first (must pass)
  print_header "Running Above-The-Line (ATL) Critical Tests"
  mvn test -P atl-tests || {
    print_error "ATL tests failed - critical issues found!"
    print_warning "Check target/cucumber-reports for details"
    return 1
  }
  print_success "ATL tests passed"
  
  # Step 2: Run BTL tests (additional quality checks)
  print_header "Running Below-The-Line (BTL) Robustness Tests"
  mvn test -P btl-tests || {
    print_warning "BTL tests failed - robustness issues found"
    print_warning "Check target/cucumber-reports for details"
    # Don't exit with error for BTL tests - they're not critical
  }
  print_success "BTL tests completed"
}

# Main execution
if [ -n "$RUN_SPECIFIC" ]; then
  # Run specific tool(s) only
  IFS=',' read -ra TOOLS <<< "$RUN_SPECIFIC"
  for tool in "${TOOLS[@]}"; do
    case $tool in
      spotless)
        run_spotless
        ;;
      checkstyle)
        run_checkstyle
        ;;
      spotbugs)
        run_spotbugs
        ;;
      jacoco)
        run_jacoco
        ;;
      tests)
        run_tests
        ;;
      *)
        print_error "Unknown tool: $tool"
        ;;
    esac
  done
else
  # Check encoding first if the script exists
  if [ -f "$SCRIPT_DIR/check-encoding.sh" ]; then
    print_header "Checking File Encoding and Line Endings"
    $SCRIPT_DIR/check-encoding.sh || {
      print_error "File encoding or line ending check failed"
      print_warning "Run '$SCRIPT_DIR/check-encoding.sh --fix' to fix encoding issues"
      exit 1
    }
    print_success "File encoding and line ending check passed"
  else
    print_warning "check-encoding.sh not found - skipping encoding check"
  fi
  
  # Run all tools sequentially with respect to skip flags
  run_spotless
  run_checkstyle
  run_spotbugs
  run_tests
  run_jacoco
fi

print_header "All Quality Checks Completed"
echo "ATL Tests: Critical tests for core functionality"
echo "BTL Tests: Robustness tests for edge cases and additional quality"

echo ""
echo "Reports available in:"
echo " - Cucumber Report: target/cucumber-reports/cucumber.html"
echo " - JaCoCo Coverage: target/site/jacoco/index.html"
echo " - CheckStyle: target/checkstyle-result.xml"
echo " - SpotBugs: target/spotbugsXml.xml"
echo " - SonarQube: Check the SonarQube dashboard for comprehensive analysis"
echo ""
echo "Encoding and line ending checks:"
echo " - Run '$SCRIPT_DIR/check-encoding.sh --verbose' for detailed encoding information"
echo " - Run '$SCRIPT_DIR/check-encoding.sh --fix' to automatically fix issues"

# Make script executable
chmod +x "$0"