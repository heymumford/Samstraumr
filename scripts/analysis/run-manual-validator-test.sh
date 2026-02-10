#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to compile and run the manual validator test without JUnit

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Compiling and running manual component name validator test${NC}"

# Clean up test-classes directory
CLASSES_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/target/test-classes"
mkdir -p $CLASSES_DIR

# Create isolated directory if needed
mkdir -p $CLASSES_DIR/isolated

# Compile validator classes first
SRC_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/main/java"
CLASSES_SRC="$SRC_DIR/org/s8r/domain/exception/ComponentException.java $SRC_DIR/org/s8r/domain/exception/InvalidComponentNameException.java $SRC_DIR/org/s8r/domain/validation/ComponentNameValidator.java"

echo -e "${GREEN}Compiling validation classes...${NC}"
javac -d $CLASSES_DIR $CLASSES_SRC

if [ $? -ne 0 ]; then
  echo -e "${RED}Compilation of validation classes failed!${NC}"
  exit 1
fi

# Compile and run the manual test
TEST_FILE="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/isolated/ManualValidatorTest.java"

echo -e "${GREEN}Compiling manual test...${NC}"
javac -d $CLASSES_DIR -cp $CLASSES_DIR $TEST_FILE

if [ $? -ne 0 ]; then
  echo -e "${RED}Compilation of manual test failed!${NC}"
  exit 1
fi

echo -e "${GREEN}Running manual test...${NC}"
java -cp $CLASSES_DIR isolated.ManualValidatorTest

echo -e "${YELLOW}Test complete.${NC}"