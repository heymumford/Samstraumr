#!/bin/bash

# Script to use Java 21 for Samstraumr project

# Save current JAVA_HOME
OLD_JAVA_HOME=$JAVA_HOME

# Set JAVA_HOME to Java 21
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64

# Add JVM options for Java 21 compatibility
export JAVA_OPTS="$JAVA_OPTS --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED"

# Display current Java version
echo "Using Java version:"
$JAVA_HOME/bin/java -version

# Run the command with Java 21
if [ $# -eq 0 ]; then
  echo "Usage: $0 <command>"
  echo "Example: $0 ./s8r build"
  echo "         $0 mvn clean install"
  # Restore original JAVA_HOME
  export JAVA_HOME=$OLD_JAVA_HOME
  exit 1
fi

# Execute the command
"$@"

# Restore original JAVA_HOME
export JAVA_HOME=$OLD_JAVA_HOME