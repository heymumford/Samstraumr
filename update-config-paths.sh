#!/bin/bash
# update-config-paths.sh - Updates the configuration file with new package paths
# Designed to be run after refactoring the directory structure

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}" && pwd)"

# Source the configuration file
CONFIG_FILE="${PROJECT_ROOT}/.samstraumr.config"
if [ -f "${CONFIG_FILE}" ]; then
  source "${CONFIG_FILE}"
else
  echo "Configuration file not found: ${CONFIG_FILE}"
  exit 1
fi

# Get new package paths
echo "Updating Samstraumr package paths"
echo "--------------------------------"
echo "Current paths:"
echo "  Core package: ${SAMSTRAUMR_CORE_PACKAGE}"
echo "  Test package: ${SAMSTRAUMR_TEST_PACKAGE}"
echo "  Composite package: ${SAMSTRAUMR_COMPOSITE_PACKAGE}"
echo "  Machine package: ${SAMSTRAUMR_MACHINE_PACKAGE}"
echo ""
echo "Please enter new package paths (or press Enter to keep current):"

echo -n "Core package [${SAMSTRAUMR_CORE_PACKAGE}]: "
read NEW_CORE_PACKAGE
NEW_CORE_PACKAGE=${NEW_CORE_PACKAGE:-$SAMSTRAUMR_CORE_PACKAGE}

echo -n "Test package [${SAMSTRAUMR_TEST_PACKAGE}]: "
read NEW_TEST_PACKAGE
NEW_TEST_PACKAGE=${NEW_TEST_PACKAGE:-$SAMSTRAUMR_TEST_PACKAGE}

echo -n "Composite package [${SAMSTRAUMR_COMPOSITE_PACKAGE}]: "
read NEW_COMPOSITE_PACKAGE
NEW_COMPOSITE_PACKAGE=${NEW_COMPOSITE_PACKAGE:-$SAMSTRAUMR_COMPOSITE_PACKAGE}

echo -n "Machine package [${SAMSTRAUMR_MACHINE_PACKAGE}]: "
read NEW_MACHINE_PACKAGE
NEW_MACHINE_PACKAGE=${NEW_MACHINE_PACKAGE:-$SAMSTRAUMR_MACHINE_PACKAGE}

# Update the configuration file
echo "Updating configuration file..."
sed -i "s|SAMSTRAUMR_CORE_PACKAGE=\"${SAMSTRAUMR_CORE_PACKAGE}\"|SAMSTRAUMR_CORE_PACKAGE=\"${NEW_CORE_PACKAGE}\"|g" "${CONFIG_FILE}"
sed -i "s|SAMSTRAUMR_TEST_PACKAGE=\"${SAMSTRAUMR_TEST_PACKAGE}\"|SAMSTRAUMR_TEST_PACKAGE=\"${NEW_TEST_PACKAGE}\"|g" "${CONFIG_FILE}"
sed -i "s|SAMSTRAUMR_COMPOSITE_PACKAGE=\"${SAMSTRAUMR_COMPOSITE_PACKAGE}\"|SAMSTRAUMR_COMPOSITE_PACKAGE=\"${NEW_COMPOSITE_PACKAGE}\"|g" "${CONFIG_FILE}"
sed -i "s|SAMSTRAUMR_MACHINE_PACKAGE=\"${SAMSTRAUMR_MACHINE_PACKAGE}\"|SAMSTRAUMR_MACHINE_PACKAGE=\"${NEW_MACHINE_PACKAGE}\"|g" "${CONFIG_FILE}"

# Add previous paths to legacy section
echo "# Legacy package structure (kept for reference during migration)" > /tmp/legacy_packages
echo "SAMSTRAUMR_LEGACY_CORE_PACKAGE=\"${SAMSTRAUMR_CORE_PACKAGE}\"" >> /tmp/legacy_packages
echo "SAMSTRAUMR_LEGACY_TEST_PACKAGE=\"${SAMSTRAUMR_TEST_PACKAGE}\"" >> /tmp/legacy_packages
echo "SAMSTRAUMR_LEGACY_COMPOSITE_PACKAGE=\"${SAMSTRAUMR_COMPOSITE_PACKAGE}\"" >> /tmp/legacy_packages
echo "SAMSTRAUMR_LEGACY_MACHINE_PACKAGE=\"${SAMSTRAUMR_MACHINE_PACKAGE}\"" >> /tmp/legacy_packages

# Replace the legacy section in the config file
sed -i '/^# Legacy package structure/,/^SAMSTRAUMR_LEGACY_.*PACKAGE=/{//!d}' "${CONFIG_FILE}"
sed -i "/^# Legacy package structure/r /tmp/legacy_packages" "${CONFIG_FILE}"
rm /tmp/legacy_packages

echo ""
echo "Configuration file updated successfully."
echo "New paths:"
echo "  Core package: ${NEW_CORE_PACKAGE}"
echo "  Test package: ${NEW_TEST_PACKAGE}"
echo "  Composite package: ${NEW_COMPOSITE_PACKAGE}"
echo "  Machine package: ${NEW_MACHINE_PACKAGE}"
echo ""
echo "Legacy paths (kept for reference) are also stored in the configuration file."
echo "Review ${CONFIG_FILE} to ensure everything is correct."