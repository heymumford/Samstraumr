#!/bin/bash
# Filename: build-optimal.sh
# Purpose: Provides optimized Maven build configuration for Samstraumr with flexible build modes.
# Goals:
#   - Ensure that builds are executed with optimized memory and thread settings
#   - Ensure that different build modes (fast, compile, test) are easily accessible
#   - Ensure that project structure and Java version compatibility are properly handled
# Dependencies:
#   - Maven build system with appropriate profiles
#   - setup-java17-compat.sh for version compatibility
#   - Project structure with standard Maven layout
# Assumptions:
#   - Script may be called from different locations relative to project root
#   - Maven is installed and properly configured in the environment
#   - The fast profile is defined in the project POM files
#
# Usage: ./build-optimal.sh [clean] [fast|compile|test] [additional maven flags]

set -e

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR" &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Source Java 17 compatibility script if exists
if [ -f "$SCRIPT_DIR/setup-java17-compat.sh" ]; then
  source "$SCRIPT_DIR/setup-java17-compat.sh"
elif [ -f "$PROJECT_ROOT/setup-java17-compat.sh" ]; then
  source "$PROJECT_ROOT/setup-java17-compat.sh"
fi

# Change to project root directory to ensure Maven runs correctly
cd "$PROJECT_ROOT"

PARALLEL_FLAG="-T 1C"
MEMORY_OPTS="-Xmx1g -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Parse command line arguments
MODE="compile"
CLEAN=""
ADDITIONAL_ARGS=""
PROFILE=""

for arg in "$@"; do
  case $arg in
    clean)
      CLEAN="clean"
      ;;
    fast)
      MODE="compile"
      PROFILE="-P fast"
      ;;
    compile)
      MODE="compile"
      ;;
    test)
      MODE="test"
      ;;
    *)
      ADDITIONAL_ARGS="$ADDITIONAL_ARGS $arg"
      ;;
  esac
done

# Set Maven options if not already set by setup-java17-compat.sh
if [ -z "$MAVEN_OPTS" ]; then
  export MAVEN_OPTS="$MEMORY_OPTS"
fi

# Print build info
echo "🚀 Building Samstraumr with optimized settings"
echo "📦 Mode: $MODE"
echo "🧹 Clean: ${CLEAN:-No}"
echo "⚙️ Profile: ${PROFILE:-default}"
echo "💻 Parallel: $PARALLEL_FLAG"
echo "🧠 Memory: $MAVEN_OPTS"
echo "➕ Additional args: $ADDITIONAL_ARGS"
echo ""

# Run Spotless first if not skipped and in a quality-related profile
if [[ "$PROFILE" != *"skip-quality-checks"* ]] && [[ "$PROFILE" != *"fast"* ]]; then
  echo "🔍 Running code formatting with Spotless before build..."
  mvn spotless:apply -q || {
    echo "⚠️ Warning: Spotless formatting failed, proceeding with build anyway"
  }
fi

# Execute the main build
echo "🚀 Executing the build..."
mvn $CLEAN $MODE $PARALLEL_FLAG $PROFILE $ADDITIONAL_ARGS

# Check if build was successful
if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Build completed successfully"
  
  # Add helpful suggestions
  if [[ "$MODE" == "test" ]]; then
    echo ""
    echo "📊 Test reports available at:"
    echo "  - Cucumber: target/cucumber-reports/cucumber.html"
    echo "  - JaCoCo: target/site/jacoco/index.html (if enabled)"
  fi
else
  echo ""
  echo "❌ Build failed"
  exit 1
fi