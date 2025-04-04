#!/bin/bash
#==============================================================================
# Filename: install-docmosis.sh
# Description: Install Docmosis and Barcode4j JAR files into local Maven repository
#==============================================================================

# Path variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"
LIB_DIR="${PROJECT_ROOT}/lib"
DOCMOSIS_VERSION="4.8.0"
BARCODE4J_VERSION="2.1"

echo "=============================================================="
echo "Installing Docmosis JARs to local Maven repository"
echo "=============================================================="

# Ensure lib directory exists
mkdir -p "${LIB_DIR}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}"
mkdir -p "${LIB_DIR}/net/sf/barcode4j/barcode4j/${BARCODE4J_VERSION}"

# Check if JAR files exist in user's home directory
DOCMOSIS_SRC="${HOME}/java-${DOCMOSIS_VERSION}.jar"
DOCMOSIS_JAVADOC="${HOME}/java-${DOCMOSIS_VERSION}-javadoc.jar"
DOCMOSIS_DOWNLOAD_URL="https://www.docmosis.com/resources/java-${DOCMOSIS_VERSION}.zip"

# If JAR not found, attempt to download it
if [ ! -f "${DOCMOSIS_SRC}" ]; then
  echo "Docmosis JAR file not found at ${DOCMOSIS_SRC}"
  echo "Attempting to download Docmosis..."
  
  # Create a temporary directory for download
  TMP_DIR=$(mktemp -d)
  DOWNLOAD_ZIP="${TMP_DIR}/docmosis.zip"
  
  # Download the Docmosis package
  if command -v curl &> /dev/null; then
    curl -L -o "${DOWNLOAD_ZIP}" "${DOCMOSIS_DOWNLOAD_URL}"
  elif command -v wget &> /dev/null; then
    wget -O "${DOWNLOAD_ZIP}" "${DOCMOSIS_DOWNLOAD_URL}"
  else
    echo "Error: Neither curl nor wget are available to download Docmosis"
    echo "Please install Docmosis manually by downloading from ${DOCMOSIS_DOWNLOAD_URL}"
    echo "and place the jar file at ${DOCMOSIS_SRC}"
    rm -rf "${TMP_DIR}"
    exit 1
  fi
  
  # Unzip the downloaded file
  if [ -f "${DOWNLOAD_ZIP}" ]; then
    echo "Extracting Docmosis..."
    unzip -q "${DOWNLOAD_ZIP}" -d "${TMP_DIR}"
    
    # Find and copy the jar files
    if [ -f "${TMP_DIR}/java-${DOCMOSIS_VERSION}.jar" ]; then
      cp "${TMP_DIR}/java-${DOCMOSIS_VERSION}.jar" "${DOCMOSIS_SRC}"
      echo "Docmosis JAR downloaded and installed at ${DOCMOSIS_SRC}"
    fi
    
    if [ -f "${TMP_DIR}/java-${DOCMOSIS_VERSION}-javadoc.jar" ]; then
      cp "${TMP_DIR}/java-${DOCMOSIS_VERSION}-javadoc.jar" "${DOCMOSIS_JAVADOC}"
    fi
    
    # Clean up temp directory
    rm -rf "${TMP_DIR}"
    
    # Check if we have the JAR now
    if [ ! -f "${DOCMOSIS_SRC}" ]; then
      echo "Error: Could not find or download Docmosis JAR"
      exit 1
    fi
  else
    echo "Error: Failed to download Docmosis"
    rm -rf "${TMP_DIR}"
    exit 1
  fi
fi

# Copy the JAR files to the local repository with proper Maven naming
echo "Copying Docmosis JAR files to local repository structure..."
cp "${DOCMOSIS_SRC}" "${LIB_DIR}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.jar"

if [ -f "${DOCMOSIS_JAVADOC}" ]; then
  cp "${DOCMOSIS_JAVADOC}" "${LIB_DIR}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}-javadoc.jar"
fi

# Create a POM file for the local repository
cat > "${LIB_DIR}/com/docmosis/docmosis-java/${DOCMOSIS_VERSION}/docmosis-java-${DOCMOSIS_VERSION}.pom" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.docmosis</groupId>
  <artifactId>docmosis-java</artifactId>
  <version>${DOCMOSIS_VERSION}</version>
  <packaging>jar</packaging>
  <name>Docmosis Java</name>
  <description>Java library for document generation using templates</description>
  
  <dependencies>
    <dependency>
      <groupId>net.sf.barcode4j</groupId>
      <artifactId>barcode4j</artifactId>
      <version>${BARCODE4J_VERSION}</version>
      <optional>true</optional>
    </dependency>
  </dependencies>
</project>
EOF

echo "Docmosis JAR files installed successfully"
echo ""
echo "To use Docmosis in your project, activate the profile with:"
echo "mvn clean install -P docmosis-report"
echo ""
echo "=============================================================="