#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#
# Validates the ALZ001 test configuration by compiling and running the ConfigValidator
#

# Set current directory to the script location
cd "$(dirname "$0")"

echo "Compiling ConfigValidator..."
javac -d target/classes src/test/java/org/s8r/test/steps/alz001/util/ConfigValidator.java

if [ $? -ne 0 ]; then
  echo "ERROR: Failed to compile ConfigValidator" >&2
  exit 1
fi

echo -e "\nRunning ConfigValidator..."
java -cp target/classes org.s8r.test.steps.alz001.util.ConfigValidator

if [ $? -ne 0 ]; then
  echo "ERROR: Configuration validation failed" >&2
  exit 1
fi

echo -e "\nALZ001 configuration validated successfully!"
exit 0