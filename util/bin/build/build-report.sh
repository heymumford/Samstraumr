#!/bin/bash
# Filename: build-report.sh
# Purpose: Generate a comprehensive build report for Samstraumr

set -e

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
RESET='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"

# Change to project root directory to ensure Maven runs correctly
cd "$PROJECT_ROOT"

# Get current version
if [ -f "Samstraumr/version.properties" ]; then
  VERSION=$(grep "samstraumr.version=" Samstraumr/version.properties | cut -d= -f2)
else
  VERSION="unknown"
fi

echo -e "${CYAN}==== Samstraumr Build Report ====${RESET}"
echo -e "${YELLOW}Version: ${VERSION}${RESET}"
echo -e "${YELLOW}Generated: $(date)${RESET}"
echo -e "${YELLOW}System: $(uname -s) $(uname -r)${RESET}"
echo -e "${CYAN}================================${RESET}"

# Check Java version
echo -e "\n${CYAN}Java Version:${RESET}"
java -version 2>&1

# Check Maven version
echo -e "\n${CYAN}Maven Version:${RESET}"
mvn --version | head -1

# List recent commits
echo -e "\n${CYAN}Recent Commits:${RESET}"
git log -n 5 --pretty=format:"%h %ad %s" --date=short

# Check quality check status
echo -e "\n${CYAN}Quality Check Status:${RESET}"
echo -e "${YELLOW}Spotless:${RESET} $(if mvn spotless:check -q > /dev/null 2>&1; then echo -e "${GREEN}Passed${RESET}"; else echo -e "${RED}Failed${RESET}"; fi)"
echo -e "${YELLOW}Checkstyle:${RESET} $(if mvn checkstyle:check -q > /dev/null 2>&1; then echo -e "${GREEN}Passed${RESET}"; else echo -e "${RED}Failed${RESET}"; fi)"
echo -e "${YELLOW}SpotBugs:${RESET} $(if mvn spotbugs:check -q > /dev/null 2>&1; then echo -e "${GREEN}Passed${RESET}"; else echo -e "${RED}Failed${RESET}"; fi)"

# Verify POM files are consistent
echo -e "\n${CYAN}POM Version Consistency:${RESET}"
ROOT_VERSION=$(grep -o "<version>.*</version>" pom.xml | head -1 | sed 's/<version>\(.*\)<\/version>/\1/')
CORE_VERSION=$(grep -o "<version>.*</version>" Samstraumr/samstraumr-core/pom.xml | head -1 | sed 's/<version>\(.*\)<\/version>/\1/')

if [ "$ROOT_VERSION" = "$CORE_VERSION" ] && [ "$ROOT_VERSION" = "$VERSION" ]; then
  echo -e "${GREEN}All version numbers are consistent: $VERSION${RESET}"
else
  echo -e "${RED}Version inconsistency detected:${RESET}"
  echo -e "  Properties file: $VERSION"
  echo -e "  Root POM: $ROOT_VERSION"
  echo -e "  Core POM: $CORE_VERSION"
fi

# List available test suites
echo -e "\n${CYAN}Available Test Suites:${RESET}"
echo -e "${YELLOW}Industry Standard:${RESET} smoke, unit, component, integration, api, system, endtoend, property"
echo -e "${YELLOW}Samstraumr:${RESET} orchestration, tube, composite, flow, machine, stream, acceptance, adaptation"

echo -e "\n${CYAN}Test Commands:${RESET}"
echo -e " - All tests: ${YELLOW}./util/test-run.sh all${RESET}"
echo -e " - ATL tests: ${YELLOW}./util/test-run-atl.sh${RESET}"
echo -e " - Specific: ${YELLOW}./util/test-run.sh [test-type]${RESET}"

echo -e "\n${CYAN}Build Commands:${RESET}"
echo -e " - Optimized build: ${YELLOW}./util/build-optimal.sh${RESET}"
echo -e " - Performance build: ${YELLOW}./util/build-performance.sh${RESET}"
echo -e " - Fast dev mode: ${YELLOW}./util/build-optimal.sh fast${RESET}"

# Line ending check status
echo -e "\n${CYAN}Line Ending Status:${RESET}"
CRLF_FILES=$(find . -type f -name "*.java" -o -name "*.xml" -o -name "*.md" | xargs grep -l $'\r' 2>/dev/null | wc -l)
if [ "$CRLF_FILES" -eq 0 ]; then
  echo -e "${GREEN}All text files have LF line endings${RESET}"
else
  echo -e "${RED}$CRLF_FILES files with CRLF line endings detected${RESET}"
  echo -e "Run ${YELLOW}./util/scripts/fix-line-endings.sh${RESET} to fix them"
fi

echo -e "\n${CYAN}==== End of Report ====${RESET}"