#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

# Script to compile and run the isolated validator test directly without Maven

# Set colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Compiling and running isolated component name validator test${NC}"

# Clean up test-classes directory
CLASSES_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/target/test-classes"
mkdir -p $CLASSES_DIR

# Create isolated directory if needed
mkdir -p $CLASSES_DIR/isolated

# Get Maven classpath
MAVEN_CLASSPATH=$(mvn -q dependency:build-classpath)

# Compile validator classes first
SRC_DIR="/home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/main/java"
echo -e "${GREEN}Compiling validation classes...${NC}"
javac -d $CLASSES_DIR -cp "$CLASSES_DIR:$MAVEN_CLASSPATH" \
  $SRC_DIR/org/s8r/domain/exception/ComponentException.java \
  $SRC_DIR/org/s8r/domain/exception/InvalidComponentNameException.java \
  $SRC_DIR/org/s8r/domain/validation/ComponentNameValidator.java

# Add necessary JUnit dependencies 
echo -e "${GREEN}Compiling isolated test...${NC}"
javac -d $CLASSES_DIR -cp "$CLASSES_DIR:$MAVEN_CLASSPATH" \
  /home/emumford/NativeLinuxProjects/Samstraumr/modules/samstraumr-core/src/test/java/isolated/IsolatedComponentNameValidatorTest.java

if [ $? -ne 0 ]; then
  echo -e "${RED}Compilation failed!${NC}"
  exit 1
fi

echo -e "${GREEN}Running isolated test...${NC}"
java -cp "$CLASSES_DIR:$MAVEN_CLASSPATH" org.junit.platform.console.ConsoleLauncher --select-class=isolated.IsolatedComponentNameValidatorTest

echo -e "${YELLOW}Test complete.${NC}"