#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# Script to check Maven test properties

cd /home/emumford/NativeLinuxProjects/Samstraumr

echo "Checking test properties..."
mvn -f modules/pom.xml help:system -Dorg.slf4j.simpleLogger.defaultLogLevel=info | grep -E "skipTests|maven.test.skip|testFailureIgnore"