#!/bin/bash
# Optimized build script for Samstraumr
# Usage: ./build-optimal.sh [clean] [fast|compile|test] [additional maven flags]

set -e

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# If the script is in util/build, adjust PROJECT_ROOT
if [[ "$SCRIPT_DIR" == */util/build ]]; then
  PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
fi

# Source Java 17 compatibility script if exists
if [ -f "$SCRIPT_DIR/java17-compat.sh" ]; then
  source "$SCRIPT_DIR/java17-compat.sh"
elif [ -f "$PROJECT_ROOT/util/build/java17-compat.sh" ]; then
  source "$PROJECT_ROOT/util/build/java17-compat.sh"
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

# Set Maven options if not already set by java17-compat.sh
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

# Execute the build
mvn $CLEAN $MODE $PARALLEL_FLAG $PROFILE $ADDITIONAL_ARGS

echo ""
echo "✅ Build completed"