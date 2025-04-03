#!/bin/bash
# Optimized build script for Samstraumr
# Usage: ./build-optimal.sh [clean] [fast|compile|test] [additional maven flags]

set -e

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

# Set Maven options if not already set
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