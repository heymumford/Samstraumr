#!/bin/bash
# This is a redirect script that points to the new location

# Skip warnings when called from s8r
if [[ "$SAMSTRAUMR_CLI" != "s8r" ]]; then
  echo -e "\033[1;33mWARNING: build-optimal.sh has been moved to util/bin/build/build-optimal.sh\033[0m"
  echo -e "Please use \033[1;32m./util/bin/build/build-optimal.sh\033[0m instead."
  echo ""
fi

# When called from s8r, perform build directly to avoid color variable conflicts
if [[ "$SAMSTRAUMR_CLI" == "s8r" ]]; then
  # Extract mode and options
  CLEAN=false
  VERBOSE=false
  PROFILE=""
  SKIP_QUALITY=false
  MODE="fast"
  
  # Parse arguments
  while [[ $# -gt 0 ]]; do
    case "$1" in
      -c|--clean)
        CLEAN=true
        shift
        ;;
      -v|--verbose)
        VERBOSE=true
        shift
        ;;
      -p|--profile)
        PROFILE="$2"
        shift 2
        ;;
      --skip-quality)
        SKIP_QUALITY=true
        shift
        ;;
      -h|--help)
        # Print help and exit
        echo "Usage: ./build-optimal.sh [options] [mode]"
        echo ""
        echo "Options:"
        echo "  -c, --clean               Clean before building"
        echo "  -v, --verbose             Enable verbose output"
        echo "  -p, --profile <profile>   Use specific Maven profile"
        echo "  --skip-quality            Skip quality checks"
        echo ""
        echo "Modes:"
        echo "  fast                      Fast build with quality checks skipped (default)"
        echo "  compile                   Compile only"
        echo "  test                      Compile and run tests"
        echo "  package                   Create JAR package"
        echo "  install                   Install to local repository"
        exit 0
        ;;
      fast|compile|test|package|install)
        MODE="$1"
        shift
        ;;
      *)
        echo "Unknown option: $1"
        exit 1
        ;;
    esac
  done
  
  # Build Maven command
  MVN_CMD="mvn"
  
  # Add clean if requested
  if $CLEAN; then
    MVN_CMD="$MVN_CMD clean"
  fi
  
  # Add appropriate goal based on mode
  case "$MODE" in
    fast)
      MVN_CMD="$MVN_CMD compile -Dmaven.test.skip=true"
      SKIP_QUALITY=true
      ;;
    compile)
      MVN_CMD="$MVN_CMD compile -Dmaven.test.skip=true"
      ;;
    test)
      MVN_CMD="$MVN_CMD test"
      ;;
    package)
      MVN_CMD="$MVN_CMD package"
      ;;
    install)
      MVN_CMD="$MVN_CMD install"
      ;;
  esac
  
  # Add profile if specified
  if [[ -n "$PROFILE" ]]; then
    MVN_CMD="$MVN_CMD -P $PROFILE"
  fi
  
  # Add skip quality if requested
  if $SKIP_QUALITY; then
    MVN_CMD="$MVN_CMD -P skip-quality-checks"
  fi
  
  # Add thread count
  MVN_CMD="$MVN_CMD -T 1C"
  
  # Execute the command
  echo "Executing: $MVN_CMD"
  cd /home/emumford/NativeLinuxProjects/Samstraumr && eval "$MVN_CMD"
else
  # Forward to new script for normal usage
  /home/emumford/NativeLinuxProjects/Samstraumr/util/bin/build/build-optimal.sh "$@"
fi
