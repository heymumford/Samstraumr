#!/bin/bash
# Filename: verify-config.sh
# Purpose: Verify that the configuration file is properly set up and being used

# Determine script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "Verifying Samstraumr configuration system..."
echo "==============================================="

# Check for configuration file
if [ -f "${PROJECT_ROOT}/.samstraumr.config" ]; then
  echo "✅ Configuration file found at: ${PROJECT_ROOT}/.samstraumr.config"
  # Source the configuration file
  source "${PROJECT_ROOT}/.samstraumr.config"
else
  echo "❌ Configuration file NOT found at: ${PROJECT_ROOT}/.samstraumr.config"
  echo "Please ensure the configuration file exists and is readable."
  exit 1
fi

# Verify core paths and structure
echo ""
echo "Verifying core paths:"
echo "---------------------"

paths_to_check=(
  "${SAMSTRAUMR_PROJECT_ROOT}"
  "${SAMSTRAUMR_CORE_MODULE}"
  "${SAMSTRAUMR_SRC_MAIN}"
  "${SAMSTRAUMR_SRC_TEST}"
  "${SAMSTRAUMR_TARGET}"
  "${SAMSTRAUMR_JAVA_MAIN}"
  "${SAMSTRAUMR_JAVA_TEST}"
  "${SAMSTRAUMR_RESOURCES_TEST}"
)

path_errors=0
for path in "${paths_to_check[@]}"; do
  if [ -d "$path" ]; then
    echo "✅ Directory exists: $path"
  else
    echo "❌ Directory NOT found: $path"
    path_errors=$((path_errors + 1))
  fi
done

if [ $path_errors -gt 0 ]; then
  echo ""
  echo "❌ Found $path_errors path errors. Please update the configuration file."
else
  echo ""
  echo "✅ All configured paths are valid."
fi

# Check if path_for_package function works
echo ""
echo "Testing path_for_package function:"
echo "---------------------------------"
if [ "$(path_for_package 'org.test.steps')" = "org/test/steps" ]; then
  echo "✅ path_for_package function works correctly"
else
  echo "❌ path_for_package function is not working properly"
  echo "  Expected: 'org/test/steps'"
  echo "  Got: '$(path_for_package 'org.test.steps')'"
fi

# Test integrations with key scripts
echo ""
echo "Testing script integration:"
echo "--------------------------"

scripts_to_test=(
  "${SCRIPT_DIR}/build-optimal.sh"
  "${SCRIPT_DIR}/test-run.sh"
  "${SCRIPT_DIR}/test-run-atl.sh"
  "${SCRIPT_DIR}/test-map-type.sh"
  "${SCRIPT_DIR}/java-env-setup.sh"
  "${SCRIPT_DIR}/setup-java17-compat.sh"
  "${SCRIPT_DIR}/version"
)

script_errors=0
for script in "${scripts_to_test[@]}"; do
  if [ -f "$script" ]; then
    if grep -q "source.*samstraumr.config" "$script" || grep -q "\.samstraumr\.config" "$script"; then
      echo "✅ Script uses configuration: $(basename "$script")"
    else
      echo "❌ Script does NOT use configuration: $(basename "$script")"
      script_errors=$((script_errors + 1))
    fi
  else
    echo "⚠️ Script not found: $(basename "$script")"
  fi
done

if [ $script_errors -gt 0 ]; then
  echo ""
  echo "❌ Found $script_errors scripts not using configuration. Please update them."
else
  echo ""
  echo "✅ All checked scripts are using the configuration system."
fi

# Summary
echo ""
echo "==============================================="
echo "Configuration System Verification Summary:"
if [ $path_errors -eq 0 ] && [ $script_errors -eq 0 ]; then
  echo "✅ SUCCESS: The configuration system is properly set up and working."
  echo "All paths are valid and scripts are using the configuration."
else
  echo "❌ ATTENTION NEEDED: The configuration system has issues."
  echo "Please fix the errors listed above."
fi

exit $((path_errors + script_errors))