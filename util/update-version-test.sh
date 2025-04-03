#!/bin/bash
# DEPRECATED: Legacy version bump and test script
# This functionality has been consolidated into the main version utility

# Display deprecation warning
echo -e "\033[1;33mWARNING: util/update-version-test.sh is deprecated and will be removed in a future release.\033[0m"
echo -e "Please use \033[1;32m./util/version test\033[0m instead with appropriate options:"
echo -e "  \033[1;32m./util/version test patch\033[0m                       - Bump patch version and run tests"
echo -e "  \033[1;32m./util/version test minor --skip-tests\033[0m          - Bump minor version without tests"
echo -e "  \033[1;32m./util/version test major --skip-quality --push\033[0m - Bump major version, skip quality checks, push changes"
echo ""

# Parse command line arguments to map to the new utility
BUMP_TYPE="patch"  # Default
SKIP_TESTS="false"
SKIP_QUALITY="false"
PUSH_CHANGES="false"

# Map arguments to the new format
for arg in "$@"; do
  case $arg in
    patch|minor|major)
      BUMP_TYPE="$arg"
      ;;
    --skip-tests)
      SKIP_TESTS="--skip-tests"
      ;;
    --skip-quality)
      SKIP_QUALITY="--skip-quality"
      ;;
    --push)
      PUSH_CHANGES="--push"
      ;;
  esac
done

# Forward to new script with mapped arguments
CMD="./util/version test $BUMP_TYPE"
if [ "$SKIP_TESTS" != "false" ]; then
  CMD="$CMD $SKIP_TESTS"
fi
if [ "$SKIP_QUALITY" != "false" ]; then
  CMD="$CMD $SKIP_QUALITY"
fi
if [ "$PUSH_CHANGES" != "false" ]; then
  CMD="$CMD $PUSH_CHANGES"
fi

echo -e "Executing: \033[1;32m$CMD\033[0m"
$CMD