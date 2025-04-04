#!/bin/bash
#==============================================================================
# Filename: generate-change-report.sh
# Description: Generate a change management report for a version
#==============================================================================

# Path variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"
CORE_MODULE="${PROJECT_ROOT}/Samstraumr/samstraumr-core"

# Show usage if insufficient arguments
if [ $# -lt 2 ]; then
  echo "Usage: $0 <from-version> <to-version> [output-dir]"
  echo
  echo "Arguments:"
  echo "  from-version  Starting version (e.g., 1.2.8)"
  echo "  to-version    Ending version (e.g., 1.2.9 or HEAD)"
  echo "  output-dir    Optional output directory (default: target/reports)"
  echo
  echo "Example: $0 1.2.8 1.2.9"
  exit 1
fi

FROM_VERSION="$1"
TO_VERSION="$2"
OUTPUT_DIR="${3:-target/reports}"

# Check for Docmosis license key
if [ -z "${DOCMOSIS_KEY}" ]; then
  echo "ERROR: DOCMOSIS_KEY environment variable is not set."
  echo "Set it with: export DOCMOSIS_KEY=your-license-key"
  exit 1
fi

# Ensure the JAR files are in place
echo "Checking if Docmosis is installed..."
if [ ! -d "${PROJECT_ROOT}/lib/com/docmosis" ]; then
  echo "Docmosis is not installed. Please run ./install-docmosis.sh first."
  exit 1
fi

# Build the project with the docmosis profile
echo "Building project with docmosis-report profile..."
cd "${PROJECT_ROOT}" || exit 1
mvn clean package -P docmosis-report -DskipTests

# Generate the report
echo "Generating change management report..."
cd "${CORE_MODULE}" || exit 1
java -cp target/samstraumr-core.jar org.samstraumr.tube.reporting.ChangeReportGenerator "$FROM_VERSION" "$TO_VERSION" "$OUTPUT_DIR"