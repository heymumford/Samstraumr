#!/bin/bash
# Samstraumr Build Report Generator
# Generates a comprehensive build report following Samstraumr's philosophy

# ANSI color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[0;33m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print styled messages
print_header() {
  echo -e "${BLUE}===== $1 =====${NC}"
}

print_success() {
  echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
  echo -e "${RED}✗ $1${NC}"
}

print_warning() {
  echo -e "${YELLOW}! $1${NC}"
}

print_info() {
  echo -e "${CYAN}→ $1${NC}"
}

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

print_header "Samstraumr Build Report Generator"
echo "Generating comprehensive build report for Samstraumr"

# Parse command line arguments
SKIP_TESTS=false
SKIP_QUALITY=false
OUTPUT_DIR="$PROJECT_ROOT/target/samstraumr-report"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --skip-tests)
      SKIP_TESTS=true
      shift
      ;;
    --skip-quality)
      SKIP_QUALITY=true
      shift
      ;;
    --output)
      OUTPUT_DIR="$2"
      shift 2
      ;;
    --help)
      echo "Usage: $0 [options]"
      echo "Options:"
      echo "  --skip-tests     Skip running tests (use existing results)"
      echo "  --skip-quality   Skip running quality checks (use existing results)"
      echo "  --output DIR     Specify output directory (default: target/samstraumr-report)"
      echo "  --help           Show this help message"
      exit 0
      ;;
    *)
      print_error "Unknown option: $1"
      echo "Use --help for available options"
      exit 1
      ;;
  esac
done

# Create output directory
mkdir -p "$OUTPUT_DIR"

# Get version information
VERSION=$(./util/version export)
print_info "Generating build report for Samstraumr version $VERSION"

# Run Maven site generation with build-report profile
print_header "Running Maven Site Generation"

if [ "$SKIP_TESTS" = true ] && [ "$SKIP_QUALITY" = true ]; then
  print_info "Skipping tests and quality checks"
  mvn site -P build-report -Dmaven.test.skip=true -P skip-quality-checks
elif [ "$SKIP_TESTS" = true ]; then
  print_info "Skipping tests"
  mvn site -P build-report -Dmaven.test.skip=true
elif [ "$SKIP_QUALITY" = true ]; then
  print_info "Skipping quality checks"
  mvn site -P build-report,skip-quality-checks
else
  print_info "Running complete report generation"
  mvn clean site -P build-report
fi

# Check if site generation was successful
if [ $? -eq 0 ]; then
  print_success "Maven site generation completed successfully"
else
  print_error "Maven site generation failed"
  exit 1
fi

# Post-process the generated site
print_header "Post-processing Report"

# Generate build status information
BUILD_DATE=$(date +"%Y-%m-%d %H:%M:%S")

# Determine if we're running as part of GitHub Actions
if [ -n "$GITHUB_ACTIONS" ]; then
  BUILD_TRIGGER="GitHub Actions"
  WORKFLOW_NAME=$GITHUB_WORKFLOW
else
  BUILD_TRIGGER="Local Build"
  WORKFLOW_NAME="Local Development"
fi

# Create a properties file for the dynamic content
PROPERTIES_FILE="$PROJECT_ROOT/target/site/filtered-resources.properties"
mkdir -p "$(dirname "$PROPERTIES_FILE")"

cat > "$PROPERTIES_FILE" <<EOF
# Build Information
build.date=$BUILD_DATE
build.trigger=$BUILD_TRIGGER
build.version=$VERSION
build.number=$(git rev-parse --short HEAD)
build.duration=$(date -u -d @$SECONDS +%H:%M:%S)
build.status=FLOWING

# Pipeline Stage Status
initialization.status=FLOWING
orchestration.status=FLOWING
processing.status=FLOWING
validation.status=FLOWING
analysis.status=FLOWING
reporting.status=FLOWING

# Test Metrics (placeholder values - would be replaced with actual data)
atl.tests.count=0
atl.tests.passing=0
atl.tests.failing=0
atl.tests.skipped=0
atl.tests.success.rate=0

btl.tests.count=0
btl.tests.passing=0
btl.tests.failing=0
btl.tests.skipped=0
btl.tests.success.rate=0

tests.count=0
tests.passing=0
tests.failing=0
tests.skipped=0
tests.success.rate=0

# Code Quality Metrics (placeholder values)
coverage.overall=0
quality.score=0
debt.ratio=0
EOF

print_success "Build report post-processing complete"

# Copy the report to the final destination if different from default
if [ "$OUTPUT_DIR" != "$PROJECT_ROOT/target/samstraumr-report" ]; then
  print_info "Copying report to $OUTPUT_DIR"
  mkdir -p "$OUTPUT_DIR"
  cp -r "$PROJECT_ROOT/target/site/"* "$OUTPUT_DIR/"
fi

print_header "Build Report Generation Complete"
echo "Report generated at: $OUTPUT_DIR"
echo "Open $OUTPUT_DIR/index.html in your browser to view the report"