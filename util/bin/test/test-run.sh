#!/bin/bash
# This is a redirect script that points to the new location

# Skip warnings when called from s8r
if [[ "$SAMSTRAUMR_CLI" != "s8r" ]]; then
  echo -e "\033[1;33mWARNING: test-run.sh has been moved to util/bin/test/run-tests.sh\033[0m"
  echo -e "Please use \033[1;32m./util/bin/test/run-tests.sh\033[0m instead."
  echo ""
fi

# When called from s8r, run tests directly to avoid color variable conflicts
if [[ "$SAMSTRAUMR_CLI" == "s8r" ]]; then
  # Parse arguments
  TEST_TYPE=""
  BOTH_FLAG=false
  PROFILE=""
  OUTPUT_FILE=""
  SKIP_QUALITY=false
  
  # Parse command line arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -b|--both)
        BOTH_FLAG=true
        shift
        ;;
      -p|--profile)
        PROFILE="$2"
        shift 2
        ;;
      -o|--output)
        OUTPUT_FILE="$2"
        shift 2
        ;;
      --skip-quality)
        SKIP_QUALITY=true
        shift
        ;;
      -v|--verbose)
        # Verbose flag (ignored for now)
        shift
        ;;
      -h|--help)
        # Print help and exit
        echo "Usage: ./run-tests.sh [options] <test-type>"
        echo ""
        echo "Options:"
        echo "  -h, --help                Display this help message"
        echo "  -v, --verbose             Enable verbose output"
        echo "  -b, --both                Include equivalent tests"
        echo "  -o, --output <file>       Write output to file"
        echo "  -p, --profile <profile>   Use specific Maven profile"
        echo "  --skip-quality            Skip quality checks"
        echo ""
        echo "Test types: unit, tube, component, composite, etc."
        exit 0
        ;;
      *)
        if [[ -z "$TEST_TYPE" ]]; then
          TEST_TYPE="$1"
        else
          echo "Unknown option: $1"
          exit 1
        fi
        shift
        ;;
    esac
  done
  
  # Check if test type is provided
  if [[ -z "$TEST_TYPE" ]]; then
    echo "Error: Test type is required."
    exit 1
  fi
  
  # Map test type to Maven profile
  case "$TEST_TYPE" in
    smoke)
      MAVEN_PROFILE="smoke-tests"
      EQUIVALENT="orchestration"
      ;;
    unit)
      MAVEN_PROFILE="unit-tests"
      EQUIVALENT="tube"
      ;;
    component)
      MAVEN_PROFILE="component-tests"
      EQUIVALENT="composite"
      ;;
    integration)
      MAVEN_PROFILE="integration-tests"
      EQUIVALENT="flow"
      ;;
    api)
      MAVEN_PROFILE="api-tests"
      EQUIVALENT="machine"
      ;;
    system)
      MAVEN_PROFILE="system-tests"
      EQUIVALENT="stream"
      ;;
    endtoend)
      MAVEN_PROFILE="endtoend-tests"
      EQUIVALENT="acceptance"
      ;;
    property)
      MAVEN_PROFILE="property-tests"
      EQUIVALENT="adaptation"
      ;;
    orchestration)
      MAVEN_PROFILE="orchestration-tests"
      EQUIVALENT="smoke"
      ;;
    tube)
      MAVEN_PROFILE="tube-tests"
      EQUIVALENT="unit"
      ;;
    composite|bundle)
      MAVEN_PROFILE="composite-tests"
      EQUIVALENT="component"
      ;;
    flow)
      MAVEN_PROFILE="flow-tests"
      EQUIVALENT="integration"
      ;;
    machine)
      MAVEN_PROFILE="machine-tests"
      EQUIVALENT="api"
      ;;
    stream)
      MAVEN_PROFILE="stream-tests"
      EQUIVALENT="system"
      ;;
    acceptance)
      MAVEN_PROFILE="acceptance-tests"
      EQUIVALENT="endtoend"
      ;;
    adaptation)
      MAVEN_PROFILE="adaptation-tests"
      EQUIVALENT="property"
      ;;
    all)
      MAVEN_PROFILE="all-tests"
      ;;
    *)
      echo "Error: Unknown test type: $TEST_TYPE"
      exit 1
      ;;
  esac
  
  # Override profile if specified
  if [[ -n "$PROFILE" ]]; then
    MAVEN_PROFILE="$PROFILE"
  fi
  
  # Build Maven command
  MVN_CMD="mvn test -P $MAVEN_PROFILE"
  
  # Add both flag if requested
  if $BOTH_FLAG && [[ -n "$EQUIVALENT" ]]; then
    MVN_CMD="$MVN_CMD,$EQUIVALENT-tests"
  fi
  
  # Add skip quality if requested
  if $SKIP_QUALITY; then
    MVN_CMD="$MVN_CMD -P skip-quality-checks"
  fi
  
  # Add thread count
  MVN_CMD="$MVN_CMD -T 1C"
  
  # Execute the command
  echo "Executing: $MVN_CMD"
  if [[ -n "$OUTPUT_FILE" ]]; then
    cd /home/emumford/NativeLinuxProjects/Samstraumr && eval "$MVN_CMD" | tee "$OUTPUT_FILE"
  else
    cd /home/emumford/NativeLinuxProjects/Samstraumr && eval "$MVN_CMD"
  fi
else
  # Forward to new script for normal usage
  /home/emumford/NativeLinuxProjects/Samstraumr/util/bin/test/run-tests.sh "$@"
fi
