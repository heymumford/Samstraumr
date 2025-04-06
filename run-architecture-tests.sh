#!/bin/bash

# Script to run architecture validation tests
# This validates that all our architectural decisions are implemented correctly

# Source common functions
source ./util/lib/common.sh

# Display header
echo "=================================="
echo " Running Architecture Tests"
echo "=================================="
echo "Validating implementation of architecture decisions in ADRs"
echo

# Ensure using the correct Java version
if command -v ./use-java21.sh &> /dev/null; then
    echo "Using Java 21 for tests..."
    export JAVA_TOOL_OPTIONS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED"
fi

# Parse command line arguments
RUN_MODE="full"
VERBOSE=false

while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        --verbose|-v)
            VERBOSE=true
            shift
            ;;
        quick)
            RUN_MODE="quick"
            shift
            ;;
        critical)
            RUN_MODE="critical"
            shift
            ;;
        *)
            # Unknown option
            shift
            ;;
    esac
done

# Define test execution strategy based on mode
if [ "$RUN_MODE" = "quick" ]; then
    echo "Running quick architecture validation (pre-commit mode)..."
    TEST_CLASSES="CleanArchitectureComplianceTest"
    
    # Also check for circular dependencies
    echo "Checking for circular dependencies..."
    if [ -x "$REPO_ROOT/util/scripts/check-circular-dependencies.sh" ]; then
        "$REPO_ROOT/util/scripts/check-circular-dependencies.sh"
        if [ $? -ne 0 ]; then
            exit 1
        fi
    fi
    
elif [ "$RUN_MODE" = "critical" ]; then
    echo "Running critical architecture tests only..."
    TEST_CLASSES="CleanArchitectureComplianceTest,ComponentBasedArchitectureTest,LifecycleStateManagementTest,EventDrivenCommunicationTest"
else
    echo "Running full architecture validation..."
    TEST_CLASSES="RunArchitectureTests"
fi

# Run the tests with Maven
if [ "$VERBOSE" = true ]; then
    echo "Running in verbose mode..."
    cd Samstraumr && mvn test -Dtest=$TEST_CLASSES
else
    echo "Running tests (use --verbose to see all output)..."
    cd Samstraumr && mvn test -Dtest=$TEST_CLASSES -q
fi

# Check the result
MVN_RESULT=$?

# Run circular dependency check in full and critical modes if not already run
if [ "$RUN_MODE" != "quick" ]; then
    echo "Running circular dependency check..."
    if [ -x "$REPO_ROOT/util/scripts/check-circular-dependencies.sh" ]; then
        "$REPO_ROOT/util/scripts/check-circular-dependencies.sh"
        DEP_RESULT=$?
    else
        DEP_RESULT=0
    fi
else
    DEP_RESULT=0
fi

# Final result combines both test results
if [ $MVN_RESULT -eq 0 ] && [ $DEP_RESULT -eq 0 ]; then
    echo -e "\n\033[32mAll architecture validation tests PASSED ✅\033[0m"
    echo -e "The codebase complies with all architectural decisions.\n"
    exit 0
else
    echo -e "\n\033[31mSome architecture validations FAILED ❌\033[0m"
    echo -e "Please ensure all architectural decisions are properly implemented.\n"
    exit 1
fi