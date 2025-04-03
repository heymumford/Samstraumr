#!/bin/bash
# Filename: run-tests.sh
# Purpose: Provides a unified interface for running different types of Samstraumr tests
# Goals:
#   - Ensure consistent test execution across development environments
#   - Provide clear command-line options for different test types
#   - Support the complete Samstraumr testing hierarchy
# Dependencies:
#   - Maven build system with appropriate profiles
#   - JUnit 5 for unit, integration, and property tests
#   - Cucumber for BDD and e2e tests
#   - TestContainers for system tests
# Assumptions:
#   - Script is run from the project root directory
#   - Java and Maven are properly installed and configured

set -e

# Get script directory for relative path resolution
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

# Default values
TEST_TYPE="all"
PARALLEL=false
SKIP_QUALITY=false
THREAD_COUNT="1C"
VERBOSE=false

# Function to display usage information
show_usage() {
    echo "Samstraumr Test Runner"
    echo "======================"
    echo "Runs different types of tests for the Samstraumr framework"
    echo ""
    echo "Usage: ./util/test/run-tests.sh [OPTIONS] [TEST_TYPE]"
    echo ""
    echo "TEST TYPES:"
    echo "  orchestration - Run Orchestration Tests (highest level of ATL)"
    echo "  tube          - Run Tube Tests (unit/JUnit)"
    echo "  flow          - Run Flow Tests (integration/JUnit)"
    echo "  bundle        - Run Bundle Tests (component/JUnit) - [DEPRECATED]"
    echo "  composite     - Run Composite Tests (component/JUnit)"
    echo "  stream        - Run Stream Tests (system/TestContainers)"
    echo "  adaptation    - Run Adaptation Tests (property/custom JUnit)"
    echo "  machine       - Run Machine Tests (e2e/Cucumber)"
    echo "  acceptance    - Run BDD Acceptance Tests (business/Cucumber)"
    echo "  all           - Run all tests (default)"
    echo "  atl           - Run Above The Line tests (critical, must-pass tests)"
    echo "  btl           - Run Below The Line tests (important but non-blocking tests)"
    echo "  critical      - Run only critical tests (alias for atl, fast for CI)"
    echo ""
    echo "OPTIONS:"
    echo "  -p, --parallel     Run tests in parallel"
    echo "  -t, --threads N    Set thread count (default: 1C = 1 per core)"
    echo "  -s, --skip-quality Skip quality checks during test runs"
    echo "  -v, --verbose      Enable verbose output"
    echo "  -h, --help         Show this help message"
    echo ""
    echo "EXAMPLES:"
    echo "  ./util/test/run-tests.sh tube           # Run tube tests"
    echo "  ./util/test/run-tests.sh -p bundle      # Run bundle tests in parallel"
    echo "  ./util/test/run-tests.sh -s acceptance  # Run acceptance tests without quality checks"
    echo "  ./util/test/run-tests.sh -p -t 4 all    # Run all tests in parallel with 4 threads"
    echo "  ./util/test/run-tests.sh atl            # Run Above The Line (critical) tests"
    echo "  ./util/test/run-tests.sh btl            # Run Below The Line (robustness) tests"
    echo ""
}

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -p|--parallel)
            PARALLEL=true
            shift
            ;;
        -t|--threads)
            THREAD_COUNT="$2"
            shift 2
            ;;
        -s|--skip-quality)
            SKIP_QUALITY=true
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        orchestration|tube|flow|bundle|composite|stream|adaptation|machine|acceptance|all|critical|atl|btl)
            TEST_TYPE="$1"
            shift
            ;;
        *)
            echo "Error: Unknown option '$1'"
            show_usage
            exit 1
            ;;
    esac
done

# Set Maven parallel execution if requested
PARALLEL_FLAG=""
if [ "$PARALLEL" = true ]; then
    PARALLEL_FLAG="-T $THREAD_COUNT"
fi

# Set quality skip flags if requested
QUALITY_FLAGS=""
if [ "$SKIP_QUALITY" = true ]; then
    QUALITY_FLAGS="-P skip-quality-checks -Dspotless.check.skip=true -Dcheckstyle.skip=true -Dspotbugs.skip=true -Djacoco.skip=true"
fi

# Set verbosity flags
VERBOSE_FLAG=""
if [ "$VERBOSE" = true ]; then
    VERBOSE_FLAG="-X"
fi

# Configure test specific settings
case $TEST_TYPE in
    orchestration)
        echo "🧪 Running Orchestration Tests (highest level of ATL)"
        echo "🔍 These tests verify the core building blocks and system wiring"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*Orchestration* -Dcucumber.filter.tags="@Orchestration"
        ;;
    tube)
        echo "🧪 Running Tube Tests (unit/JUnit)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*TubeTest
        ;;
    flow)
        echo "🧪 Running Flow Tests (integration/JUnit)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*FlowTest
        ;;
    bundle)
        echo "🧪 Running Bundle Tests (component/JUnit) - [DEPRECATED]"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*BundleTest
        ;;
    composite)
        echo "🧪 Running Composite Tests (component/JUnit)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*CompositeTest
        ;;
    stream)
        echo "🧪 Running Stream Tests (system/TestContainers)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*StreamTest
        ;;
    adaptation)
        echo "🧪 Running Adaptation Tests (property/custom JUnit)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dtest=*AdaptationTest
        ;;
    machine)
        echo "🧪 Running Machine Tests (e2e/Cucumber)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dcucumber.filter.tags="@L2_Machine"
        ;;
    acceptance)
        echo "🧪 Running BDD Acceptance Tests (business/Cucumber)"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -Dcucumber.filter.tags="@Acceptance"
        ;;
    atl|critical)
        echo "🧪 Running Above The Line (ATL) Tests - Critical Path"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -P atl-tests
        ;;
    btl)
        echo "🧪 Running Below The Line (BTL) Tests - Robustness Path"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test -P btl-tests
        ;;
    all)
        echo "🧪 Running All Tests"
        mvn $PARALLEL_FLAG $QUALITY_FLAGS $VERBOSE_FLAG test
        ;;
esac

echo "✅ Tests completed"