#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Helper script to run the S8rInitializer directly for testing

# Determine project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Check for Maven and compile if needed
if ! [ -d "${PROJECT_ROOT}/modules/samstraumr-core/target/classes" ]; then
  echo "Building project first..."
  cd "${PROJECT_ROOT}"
  mvn clean compile
fi

# Set up classpath
CLASSPATH="${PROJECT_ROOT}/modules/samstraumr-core/target/classes"

# Add required libraries
for jar in "${PROJECT_ROOT}"/modules/samstraumr-core/target/dependency/*.jar; do
  CLASSPATH="${CLASSPATH}:${jar}"
done

# Create a test directory if it doesn't exist
TEST_DIR="${PROJECT_ROOT}/target/init-test"
mkdir -p "${TEST_DIR}"

# Initialize a git repo for testing
if ! [ -d "${TEST_DIR}/.git" ]; then
  echo "Initializing git repo for testing..."
  cd "${TEST_DIR}"
  git init
  echo "# Test README" > README.md
  git add README.md
  git config --local user.email "test@example.com"
  git config --local user.name "Test User"
  git commit -m "Initial commit"
fi

# Get absolute path to test directory
TEST_DIR_ABS="$(cd "${TEST_DIR}" && pwd)"

# Run the initializer
echo "Running S8rInitializer..."
java -cp "${CLASSPATH}" org.s8r.initialization.InitCommandRunner "${TEST_DIR_ABS}" "$@"

echo "Done. Check ${TEST_DIR} for results."