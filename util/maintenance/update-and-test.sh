#!/bin/bash
# Filename: update-and-test.sh
# Purpose: Bumps the version and runs tests in a single command
# Location: util/maintenance/
# Usage: ./update-and-test.sh [patch|minor|major] [--skip-tests] [--skip-quality]

set -e

# Define colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get script directory for relative path resolution
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." &> /dev/null && pwd 2> /dev/null || echo "$SCRIPT_DIR")"

# Change to project root directory
cd "$PROJECT_ROOT"

# Default settings
BUMP_TYPE="patch"
SKIP_TESTS=false
SKIP_QUALITY=false
PUSH_CHANGES=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case $1 in
    patch|minor|major)
      BUMP_TYPE="$1"
      shift
      ;;
    --skip-tests)
      SKIP_TESTS=true
      shift
      ;;
    --skip-quality)
      SKIP_QUALITY=true
      shift
      ;;
    --push)
      PUSH_CHANGES=true
      shift
      ;;
    --help|-h)
      echo "Usage: $0 [patch|minor|major] [--skip-tests] [--skip-quality] [--push]"
      echo ""
      echo "Options:"
      echo "  patch|minor|major    Type of version bump (default: patch)"
      echo "  --skip-tests         Skip running tests after version bump"
      echo "  --skip-quality       Skip quality checks"
      echo "  --push               Push commits and tags to remote after successful tests"
      echo "  --help, -h           Show this help message"
      exit 0
      ;;
    *)
      echo -e "${RED}Unknown option: $1${NC}"
      echo "Usage: $0 [patch|minor|major] [--skip-tests] [--skip-quality] [--push]"
      echo "Use --help for more information"
      exit 1
      ;;
  esac
done

# Function to print section headers
print_header() {
  echo -e "${BLUE}===== $1 =====${NC}"
  echo ""
}

# Step 1: Bump the version
print_header "Bumping Version ($BUMP_TYPE)"
"$PROJECT_ROOT/util/version" bump "$BUMP_TYPE" --no-commit

# Step 2: Run Spotless to ensure proper formatting
if [ "$SKIP_QUALITY" = false ]; then
  print_header "Running Code Formatting"
  mvn spotless:apply
fi

# Step 3: Run tests if not skipped
if [ "$SKIP_TESTS" = false ]; then
  print_header "Running ATL Tests"
  "$PROJECT_ROOT/util/test/run-atl-tests.sh"
fi

# Step 4: Commit the changes with version bump
print_header "Committing Version Changes"
OLD_VERSION=$(grep "samstraumr.version=" "Samstraumr/version.properties" | cut -d= -f2 | xargs)
NEW_VERSION=$(grep "samstraumr.version=" "Samstraumr/version.properties" | cut -d= -f2)

# Stage all changed files
git add Samstraumr/version.properties pom.xml Samstraumr/pom.xml Samstraumr/samstraumr-core/pom.xml README.md

# Create a commit message
COMMIT_MESSAGE="Bump $BUMP_TYPE version to $NEW_VERSION

* Update version from $OLD_VERSION to $NEW_VERSION
* Apply consistent versioning across all POM files
* Run code formatting and quality checks
* Ensure all tests pass with the new version

🤖 Generated with [Claude Code](https://claude.ai/code)

Co-Authored-By: Claude <noreply@anthropic.com>"

# Create commit
git commit -m "$COMMIT_MESSAGE"

# Step 5: Create a git tag for the version
print_header "Creating Git Tag"
echo "Creating tag v$NEW_VERSION..."
git tag -a "v$NEW_VERSION" -m "Version $NEW_VERSION

Release version $NEW_VERSION of Samstraumr.

Generated with Claude Code
"

print_header "Version Bump Complete: $OLD_VERSION → $NEW_VERSION"
echo -e "${GREEN}✓ All changes have been committed${NC}"
echo -e "${GREEN}✓ Git tag v$NEW_VERSION created${NC}"

# Push changes if requested
if [ "$PUSH_CHANGES" = true ]; then
  print_header "Pushing Changes to Remote"
  echo "Pushing commits and tags to remote repository..."
  
  # Push commits
  git push
  # Push tags
  git push origin "v$NEW_VERSION"
  
  echo -e "${GREEN}✓ All changes and tags have been pushed to remote${NC}"
else
  echo ""
  echo "To push the changes and tag to the remote repository, run:"
  echo -e "  ${YELLOW}git push && git push origin v$NEW_VERSION${NC}"
fi

# Make script executable
chmod +x "$0"