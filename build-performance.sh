#!/bin/bash

# Script to optimize build performance on WSL for Windows
# Uses optimal settings for CI/CD and local development

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
RESET='\033[0m'

# Get CPU info
CPU_CORES=$(nproc)
MEMORY_MB=$(free -m | awk '/^Mem:/{print $2}')
MAVEN_MEMORY=$((MEMORY_MB / 3))

if [ $MAVEN_MEMORY -gt 4096 ]; then
    MAVEN_MEMORY=4096
fi

echo -e "${GREEN}Starting build with performance optimizations${RESET}"
echo -e "${YELLOW}CPU Cores: ${CPU_CORES}${RESET}"
echo -e "${YELLOW}Available Memory: ${MEMORY_MB}MB${RESET}"
echo -e "${YELLOW}Maven Memory: ${MAVEN_MEMORY}MB${RESET}"

# Sync filesystem to ensure everything is written to disk
echo -e "${GREEN}Syncing filesystem...${RESET}"
sync || echo -e "${RED}Failed to sync filesystem${RESET}"

# Environment variables for Maven
export MAVEN_OPTS="-Xmx${MAVEN_MEMORY}m -XX:+UseG1GC -XX:+TieredCompilation -XX:TieredStopAtLevel=1"

# Run build based on command line parameters or default to ATL tests
if [ $# -eq 0 ]; then
    echo -e "${GREEN}Running ATL tests with optimized settings${RESET}"
    mvn clean test -P atl-tests -P skip-quality-checks \
        -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true \
        -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false \
        -Dcucumber.execution.parallel.config.fixed.parallelism=$((CPU_CORES-2)) \
        -Dcucumber.execution.parallel.config.fixed.max-pool-size=$CPU_CORES \
        -T $CPU_CORES \
        -Dmaven.artifact.threads=$((CPU_CORES-2))
else
    echo -e "${GREEN}Running custom command with optimized settings${RESET}"
    mvn "$@" \
        -Dcucumber.execution.parallel.config.fixed.parallelism=$((CPU_CORES-2)) \
        -Dcucumber.execution.parallel.config.fixed.max-pool-size=$CPU_CORES \
        -T $CPU_CORES \
        -Dmaven.artifact.threads=$((CPU_CORES-2))
fi

# Print test report location
echo -e "${GREEN}Build completed.${RESET}"
echo -e "${YELLOW}Test reports available at:${RESET}"
echo -e "${YELLOW}- HTML Report: target/cucumber-reports/cucumber.html${RESET}"
echo -e "${YELLOW}- JSON Report: target/cucumber-reports/cucumber.json${RESET}"
echo -e "${YELLOW}- XML Report: target/cucumber-reports/cucumber.xml${RESET}"