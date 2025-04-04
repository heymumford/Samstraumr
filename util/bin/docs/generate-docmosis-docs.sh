#!/bin/bash
#==============================================================================
# Filename: generate-docmosis-docs.sh
# Description: Generate documentation using Docmosis
#==============================================================================

# Path variables
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="${SCRIPT_DIR}"
CORE_MODULE="${PROJECT_ROOT}/Samstraumr/samstraumr-core"

# Color definitions
COLOR_RED='\033[0;31m'
COLOR_GREEN='\033[0;32m'
COLOR_YELLOW='\033[0;33m'
COLOR_BLUE='\033[0;34m'
COLOR_RESET='\033[0m'

# Check for Docmosis configuration from environment variables
if [ -z "${DOCMOSIS_KEY}" ]; then
  # Try to read from user config
  USER_CONFIG_FILE="${HOME}/.s8r/config.json"
  if [ -f "${USER_CONFIG_FILE}" ]; then
    echo -e "${COLOR_BLUE}Reading Docmosis key from config file: ${USER_CONFIG_FILE}${COLOR_RESET}"
    DOCMOSIS_KEY=$(grep -o '"key": "[^"]*"' "${USER_CONFIG_FILE}" | cut -d'"' -f4)
    DOCMOSIS_SITE=$(grep -o '"site": "[^"]*"' "${USER_CONFIG_FILE}" | cut -d'"' -f4)
  fi
  
  # If still not set, show error
  if [ -z "${DOCMOSIS_KEY}" ]; then
    echo -e "${COLOR_RED}Error: Docmosis license key not found${COLOR_RESET}"
    echo -e "Please set it up in one of the following ways:"
    echo -e "1. Create config file at ~/.s8r/config.json with your license information"
    echo -e "2. Set the DOCMOSIS_KEY environment variable"
    exit 1
  fi
fi

# Show usage if called with -h or --help
if [[ "$1" == "-h" || "$1" == "--help" ]]; then
  echo "Usage: $0 [output-dir] [format]"
  echo
  echo "Arguments:"
  echo "  output-dir    Optional output directory (default: target/docs)"
  echo "  format        Output format: pdf, docx, html (default: pdf)"
  echo
  echo "Example: $0 ./my-docs docx"
  exit 0
fi

# Parse arguments
OUTPUT_DIR="${1:-target/docs}"
FORMAT="${2:-pdf}"

# Validate format
if [[ ! "$FORMAT" =~ ^(pdf|docx|html)$ ]]; then
  echo -e "${COLOR_RED}Invalid format: $FORMAT. Must be one of: pdf, docx, html${COLOR_RESET}"
  exit 1
fi

# Create output directory if it doesn't exist
mkdir -p "$OUTPUT_DIR"

# Ensure the JAR files are in place
echo -e "${COLOR_BLUE}Checking if Docmosis is installed...${COLOR_RESET}"
if [ ! -d "${PROJECT_ROOT}/lib/com/docmosis" ]; then
  echo -e "${COLOR_YELLOW}Docmosis is not installed. Installing now...${COLOR_RESET}"
  
  # Save Docmosis configuration
  echo "docmosis.key=${DOCMOSIS_KEY}" > "${PROJECT_ROOT}/docmosis.properties"
  echo "docmosis.site=${DOCMOSIS_SITE}" >> "${PROJECT_ROOT}/docmosis.properties"
  
  # Run installation script
  "${PROJECT_ROOT}/install-docmosis.sh"
  if [ $? -ne 0 ]; then
    echo -e "${COLOR_RED}Failed to install Docmosis JARs${COLOR_RESET}"
    exit 1
  fi
fi

# Build the project with the docmosis profile
echo -e "${COLOR_BLUE}Building project with docmosis-report profile...${COLOR_RESET}"
cd "${PROJECT_ROOT}" || exit 1
mvn clean package -P docmosis-report -DskipTests

echo -e "${COLOR_GREEN}Generating Docmosis documentation...${COLOR_RESET}"
cd "${CORE_MODULE}" || exit 1

# Use the class to generate docs with the provided key and site info
export DOCMOSIS_KEY="${DOCMOSIS_KEY}"
export DOCMOSIS_SITE="${DOCMOSIS_SITE}"
export DOCMOSIS_FORMAT="${FORMAT}"

# Run the documentation generator with proper arguments
java -cp target/samstraumr-core.jar \
     -Ddocmosis.key="${DOCMOSIS_KEY}" \
     -Ddocmosis.site="${DOCMOSIS_SITE}" \
     org.samstraumr.tube.reporting.DocumentGenerator "${OUTPUT_DIR}" "${FORMAT}"

# Check if generation was successful
if [ $? -eq 0 ]; then
  echo -e "${COLOR_GREEN}Documentation generated successfully in ${OUTPUT_DIR}${COLOR_RESET}"
  echo -e "${COLOR_BLUE}Generated files:${COLOR_RESET}"
  ls -la "${OUTPUT_DIR}"
else
  echo -e "${COLOR_RED}Documentation generation failed${COLOR_RESET}"
  exit 1
fi