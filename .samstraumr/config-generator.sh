#!/bin/bash
#==============================================================================
# Filename: config-generator.sh
# Description: Generates config.sh from config.json
#==============================================================================
# Usage: ./config-generator.sh
#
# This script reads the JSON configuration and converts it to a Bash-compatible
# format for use in shell scripts.
#==============================================================================

set -e

# Determine script and project directories
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Define paths
CONFIG_JSON="${SCRIPT_DIR}/config.json"
CONFIG_SH="${SCRIPT_DIR}/config.sh"
USER_CONFIG="${SCRIPT_DIR}/user/config.json"

# Check if jq is installed
if ! command -v jq &> /dev/null; then
    echo "Error: jq is required but not installed. Please install jq to continue."
    echo "  Ubuntu/Debian: apt-get install jq"
    echo "  CentOS/RHEL: yum install jq"
    echo "  macOS: brew install jq"
    exit 1
fi

# Check if the config.json exists
if [ ! -f "${CONFIG_JSON}" ]; then
    echo "Error: ${CONFIG_JSON} not found."
    exit 1
fi

echo "Generating Bash configuration from JSON..."

# Create the header for config.sh
cat > "${CONFIG_SH}" << 'EOT'
#!/bin/bash
#==============================================================================
# Filename: config.sh
# Description: Samstraumr configuration for shell scripts
#==============================================================================
# AUTO-GENERATED FILE - DO NOT EDIT DIRECTLY
# Generated from config.json by config-generator.sh
#==============================================================================

# Determine project root automatically
SAMSTRAUMR_PROJECT_ROOT="${SAMSTRAUMR_PROJECT_ROOT:-$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)}"

EOT

# Extract and convert project section
echo "# Project Configuration" >> "${CONFIG_SH}"
jq -r '.project | to_entries[] | "SAMSTRAUMR_\(.key | ascii_upcase)=\"\(.value)\""' "${CONFIG_JSON}" >> "${CONFIG_SH}"
echo "" >> "${CONFIG_SH}"

# Extract and convert paths section
echo "# Path Configuration" >> "${CONFIG_SH}"
jq -r '.paths | to_entries[] | "SAMSTRAUMR_\(.key | ascii_upcase)=\"${SAMSTRAUMR_PROJECT_ROOT}/\(.value)\""' "${CONFIG_JSON}" >> "${CONFIG_SH}"
echo "" >> "${CONFIG_SH}"

# Extract and convert packages section
echo "# Package Configuration" >> "${CONFIG_SH}"
jq -r '.packages | to_entries[] | "SAMSTRAUMR_\(.key | ascii_upcase)_PACKAGE=\"\(.value)\""' "${CONFIG_JSON}" >> "${CONFIG_SH}"
echo "" >> "${CONFIG_SH}"

# Extract and convert test section
echo "# Test Configuration" >> "${CONFIG_SH}"
jq -r '.test.profiles | to_entries[] | "SAMSTRAUMR_\(.key | ascii_upcase)_PROFILE=\"\(.value)\""' "${CONFIG_JSON}" >> "${CONFIG_SH}"
jq -r '.test.parameters | to_entries[] | "SAMSTRAUMR_\(.key | ascii_upcase)=\"\(.value)\""' "${CONFIG_JSON}" >> "${CONFIG_SH}"
echo "" >> "${CONFIG_SH}"

# Extract and convert maven section
echo "# Maven Configuration" >> "${CONFIG_SH}"
jq -r '.maven | to_entries[] | if .value | type == "array" then empty else "SAMSTRAUMR_\(.key | ascii_upcase)=\"\(.value)\"" end' "${CONFIG_JSON}" >> "${CONFIG_SH}"
echo "" >> "${CONFIG_SH}"

# Add helper functions
cat >> "${CONFIG_SH}" << 'EOT'
# Helper for converting package paths
# Usage: path_for_package "org.test.steps"
path_for_package() {
  echo "${1//./\/}"
}

# Helper to get absolute path
# Usage: absolute_path "relative/path"
absolute_path() {
  echo "${SAMSTRAUMR_PROJECT_ROOT}/$1"
}
EOT

echo "Configuration successfully generated: ${CONFIG_SH}"

# Make the generated file executable
chmod +x "${CONFIG_SH}"

# Check if user config exists and note that it's not automatically applied
if [ -f "${USER_CONFIG}" ]; then
    echo "NOTE: User configuration detected at ${USER_CONFIG}."
    echo "User settings must be explicitly loaded in scripts that need them."
fi