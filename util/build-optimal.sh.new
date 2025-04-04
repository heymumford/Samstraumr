#!/bin/bash
# Filename: build-optimal.sh
# Purpose: Provides optimized Maven build configuration for Samstraumr with flexible build modes.
# Goals:
#   - Ensure that builds are executed with optimized memory and thread settings
#   - Ensure that different build modes (fast, compile, test) are easily accessible
#   - Ensure that project structure and Java version compatibility are properly handled

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "Configuration file not found: ${PROJECT_ROOT}/.samstraumr.config"
  exit 1
fi

set -e

# Source Java 17 compatibility script if exists
if [ -f "$SCRIPT_DIR/setup-java17-compat.sh" ]; then
  source "$SCRIPT_DIR/setup-java17-compat.sh"
elif [ -f "$PROJECT_ROOT/setup-java17-compat.sh" ]; then
  source "$PROJECT_ROOT/setup-java17-compat.sh"
fi

# Change to project root directory to ensure Maven runs correctly
cd "$PROJECT_ROOT"

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
      PROFILE="-P ${SAMSTRAUMR_FAST_PROFILE}"
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
  export MAVEN_OPTS="${SAMSTRAUMR_MEMORY_OPTS}"
fi

# Print build info
echo "🚀 Building Samstraumr with optimized settings"
echo "📦 Mode: $MODE"
echo "🧹 Clean: ${CLEAN:-No}"
echo "⚙️ Profile: ${PROFILE:-default}"
echo "💻 Parallel: ${SAMSTRAUMR_PARALLEL_FLAG}"
echo "🧠 Memory: $MAVEN_OPTS"
echo "➕ Additional args: $ADDITIONAL_ARGS"
echo ""

# Run Spotless first if not skipped and in a quality-related profile
if [[ "$PROFILE" != *"${SAMSTRAUMR_SKIP_QUALITY_PROFILE}"* ]] && [[ "$PROFILE" != *"${SAMSTRAUMR_FAST_PROFILE}"* ]]; then
  echo "🔍 Running code formatting with Spotless before build..."
  mvn -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" spotless:apply -q || {
    echo "⚠️ Warning: Spotless formatting failed, proceeding with build anyway"
  }
fi

# Execute the main build
echo "🚀 Executing the build..."
mvn -f "${SAMSTRAUMR_CORE_MODULE}/pom.xml" $CLEAN $MODE ${SAMSTRAUMR_PARALLEL_FLAG} $PROFILE $ADDITIONAL_ARGS

# Check if build was successful
if [ $? -eq 0 ]; then
  echo ""
  echo "✅ Build completed successfully"
  
  # Add helpful suggestions
  if [[ "$MODE" == "test" ]]; then
    echo ""
    echo "📊 Test reports available at:"
    echo "  - Cucumber: ${SAMSTRAUMR_CUCUMBER_REPORT}"
    echo "  - JaCoCo: ${SAMSTRAUMR_JACOCO_REPORT} (if enabled)"
  fi
else
  echo ""
  echo "❌ Build failed"
  exit 1
fi