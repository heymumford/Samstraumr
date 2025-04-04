#!/bin/bash

# Script to optimize build performance for any environment
# Detects system resources and adapts to them automatically

# Set colored output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
RESET='\033[0m'

# Detect system resources (works in Linux, macOS, and CI environments)
if [ -f /proc/cpuinfo ]; then
    # Linux method
    CPU_CORES=$(grep -c processor /proc/cpuinfo)
    if [ -f /proc/meminfo ]; then
        MEMORY_MB=$(grep MemTotal /proc/meminfo | awk '{print int($2/1024)}')
    else
        MEMORY_MB=4096 # Default if can't detect
    fi
elif [ "$(uname)" == "Darwin" ]; then
    # macOS method
    CPU_CORES=$(sysctl -n hw.ncpu)
    MEMORY_MB=$(sysctl -n hw.memsize | awk '{print int($1/1024/1024)}')
else
    # Default for other environments (including CI)
    CPU_CORES=4
    MEMORY_MB=4096
fi

# Detect if we're running in CI
if [ -n "$CI" ] || [ -n "$GITHUB_ACTIONS" ]; then
    IS_CI=true
    echo -e "${YELLOW}CI environment detected. Optimizing for CI.${RESET}"
    
    # GitHub Actions usually has 2 cores but might have more or less
    if [ -n "$GITHUB_ACTIONS" ]; then
        # Use lower memory in GitHub Actions as it has limits
        MEMORY_FACTOR=0.3
        PARALLELISM_FACTOR=0.75
    else
        # Other CI environments
        MEMORY_FACTOR=0.4
        PARALLELISM_FACTOR=0.8
    fi
else
    IS_CI=false
    echo -e "${GREEN}Local environment detected.${RESET}"
    
    # For local environment, we can use more resources
    MEMORY_FACTOR=0.5
    PARALLELISM_FACTOR=0.8
    
    # Check if running on WSL
    if grep -qi microsoft /proc/version 2>/dev/null; then
        echo -e "${YELLOW}WSL environment detected. Adjusting for filesystem performance.${RESET}"
        # WSL has slower filesystem, so adjust accordingly
        PARALLELISM_FACTOR=0.7
    fi
fi

# Calculate optimal settings based on available resources
# Check if bc is available
if command -v bc > /dev/null; then
    # Use bc for floating point arithmetic
    MEMORY_INT=$(echo "$MEMORY_MB * $MEMORY_FACTOR / 1" | bc)
    MAVEN_MEMORY=${MEMORY_INT:-2048}
    
    PARALLEL_INT=$(echo "$CPU_CORES * $PARALLELISM_FACTOR / 1" | bc)
    PARALLEL_COUNT=${PARALLEL_INT:-1}
else
    # Fallback if bc is not available (e.g., in some CI environments)
    echo -e "${YELLOW}bc not found, using integer calculations${RESET}"
    # Use integer division with percentages
    if [ "$IS_CI" = true ]; then
        # 30% of memory for CI
        MAVEN_MEMORY=$(($MEMORY_MB * 30 / 100))
    else
        # 50% of memory for local
        MAVEN_MEMORY=$(($MEMORY_MB * 50 / 100))
    fi
    
    # Use 75% of cores
    PARALLEL_COUNT=$(($CPU_CORES * 75 / 100))
fi

# Ensure memory is within bounds
if [ "$MAVEN_MEMORY" -gt 4096 ]; then
    MAVEN_MEMORY=4096
elif [ "$MAVEN_MEMORY" -lt 1024 ]; then
    MAVEN_MEMORY=1024
fi

# Ensure thread count is at least 1
if [ "$PARALLEL_COUNT" -lt 1 ]; then
    PARALLEL_COUNT=1
fi

THREADS=$((CPU_CORES - 1))
if [ $THREADS -lt 1 ]; then
    THREADS=1
fi

echo -e "${CYAN}====== System Information ======${RESET}"
echo -e "${YELLOW}CPU Cores: ${CPU_CORES}${RESET}"
echo -e "${YELLOW}Available Memory: ${MEMORY_MB}MB${RESET}"
echo -e "${YELLOW}Maven Memory: ${MAVEN_MEMORY}MB${RESET}"
echo -e "${YELLOW}Parallel Execution Factor: ${PARALLELISM_FACTOR}${RESET}"
echo -e "${YELLOW}Calculated Thread Count: ${THREADS}${RESET}"
echo -e "${CYAN}================================${RESET}"

# Sync filesystem to ensure everything is written to disk
echo -e "${GREEN}Syncing filesystem...${RESET}"
sync || echo -e "${RED}Failed to sync filesystem${RESET}"

# Environment variables for Maven
export MAVEN_OPTS="-Xmx${MAVEN_MEMORY}m -XX:+UseG1GC -XX:+TieredCompilation"

# Create the command based on args or use default
if [ $# -eq 0 ]; then
    echo -e "${GREEN}Running ATL tests with optimized settings${RESET}"
    CMD="mvn clean test -P atl-tests -P skip-quality-checks \
        -Dspotless.check.skip=true -Dpmd.skip=true -Dcheckstyle.skip=true \
        -Dspotbugs.skip=true -Djacoco.skip=true -Dmaven.test.skip=false \
        -T ${THREADS} -Dmaven.artifact.threads=${THREADS}"
else
    echo -e "${GREEN}Running custom command with optimized settings${RESET}"
    CMD="mvn $@ -T ${THREADS} -Dmaven.artifact.threads=${THREADS}"
fi

echo -e "${CYAN}Executing: ${CMD}${RESET}"
eval $CMD

# Print test report location
echo -e "${GREEN}Build completed.${RESET}"
echo -e "${YELLOW}Test reports available at:${RESET}"
echo -e "${YELLOW}- HTML Report: target/cucumber-reports/cucumber.html${RESET}"
echo -e "${YELLOW}- JSON Report: target/cucumber-reports/cucumber.json${RESET}"
echo -e "${YELLOW}- XML Report: target/cucumber-reports/cucumber.xml${RESET}"