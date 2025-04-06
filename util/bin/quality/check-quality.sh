#!/bin/bash
#==============================================================================
# check-quality.sh
# Unified quality check script for Samstraumr project
#==============================================================================

set -e

# Terminal colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
RESET='\033[0m' # No Color
BOLD='\033[1m'

# Functions for prettier output
info() { echo -e "${BLUE}$1${RESET}"; }
success() { echo -e "${GREEN}$1${RESET}"; }
error() { echo -e "${RED}Error: $1${RESET}" >&2; }
warn() { echo -e "${YELLOW}Warning: $1${RESET}" >&2; }
header() { echo -e "\n${BOLD}${YELLOW}$1${RESET}\n"; }

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
QUALITY_DIR="${PROJECT_ROOT}/quality-tools"
REPORT_DIR="${PROJECT_ROOT}/target/quality-reports"

# Create report directory
mkdir -p "$REPORT_DIR"

# Timestamp for reports
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Default settings
PROFILE="standard"
VERBOSE=false
FIX_ISSUES=false
REPORT_ONLY=false
TOOLS=()
SKIP_TOOLS=()

# Parse command-line arguments
show_help() {
  header "Unified Quality Checker"
  echo "Usage: $(basename "$0") [options]"
  echo ""
  echo "Options:"
  echo "  -p, --profile PROFILE  Use a specific quality profile (standard, strict, security)"
  echo "  -t, --tool TOOL        Run a specific tool (spotless, checkstyle, spotbugs, jacoco, pmd, owasp, javadoc, encoding)"
  echo "  -s, --skip TOOL        Skip a specific tool"
  echo "  -f, --fix              Fix issues where possible"
  echo "  -r, --report-only      Only generate reports, don't fail on issues"
  echo "  -v, --verbose          Show verbose output"
  echo "  -h, --help             Show this help message"
  echo ""
  echo "Examples:"
  echo "  $(basename "$0") --profile strict          # Run strict profile checks"
  echo "  $(basename "$0") --tool spotless --fix     # Run and fix spotless issues"
  echo "  $(basename "$0") --skip jacoco             # Run all tools except jacoco"
  echo ""
  exit 0
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -p|--profile)
      PROFILE="$2"
      shift 2
      ;;
    -t|--tool)
      TOOLS+=("$2")
      shift 2
      ;;
    -s|--skip)
      SKIP_TOOLS+=("$2")
      shift 2
      ;;
    -f|--fix)
      FIX_ISSUES=true
      shift
      ;;
    -r|--report-only)
      REPORT_ONLY=true
      shift
      ;;
    -v|--verbose)
      VERBOSE=true
      shift
      ;;
    -h|--help)
      show_help
      ;;
    *)
      error "Unknown option: $1"
      echo "Use --help for usage information."
      exit 1
      ;;
  esac
done

# Validate profile
if [[ ! "$PROFILE" =~ ^(standard|strict|security)$ ]]; then
  error "Invalid profile: $PROFILE. Valid profiles are: standard, strict, security"
  exit 1
fi

# Load tool modules
load_tool_module() {
  local tool="$1"
  local module_path="${PROJECT_ROOT}/util/quality/modules/${tool}.sh"
  
  if [ -f "$module_path" ]; then
    source "$module_path"
    return 0
  else
    # Check if the function is defined in case the module is already loaded
    if declare -f "check_${tool}" > /dev/null; then
      return 0
    else
      error "Tool module not found: $tool"
      exit 1
    fi
  fi
}

# Determine which tools to run based on profile and specific tools requested
determine_tools() {
  # If specific tools were provided, use only those
  if [ ${#TOOLS[@]} -gt 0 ]; then
    return 0
  fi
  
  # Otherwise, use tools based on the selected profile
  case "$PROFILE" in
    standard)
      TOOLS=(spotless checkstyle spotbugs)
      ;;
    strict)
      TOOLS=(spotless checkstyle spotbugs jacoco javadoc encoding)
      ;;
    security)
      TOOLS=(spotbugs owasp)
      ;;
  esac
  
  # Remove any tools that should be skipped
  for skip_tool in "${SKIP_TOOLS[@]}"; do
    TOOLS=("${TOOLS[@]/$skip_tool}")
  done
  
  # Clean up any empty elements
  TOOLS=("${TOOLS[@]}")
}

# Run a specific tool
run_tool() {
  local tool="$1"
  local fix="$2"
  
  # Load the tool module
  load_tool_module "$tool"
  
  # Run the tool check function
  header "Running $tool check"
  
  # Check if the tool-specific function exists
  if declare -f "check_${tool}" > /dev/null; then
    "check_${tool}" "$fix"
    return $?
  else
    error "Tool function not found: check_${tool}"
    return 1
  fi
}

# Generate a unified report
generate_report() {
  header "Generating Quality Report"
  
  local report_file="${REPORT_DIR}/quality-report-${TIMESTAMP}.html"
  
  # Create HTML report
  cat > "$report_file" << EOF
<!DOCTYPE html>
<html>
<head>
  <title>Samstraumr Quality Report</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 0; padding: 20px; }
    h1 { color: #333; }
    h2 { color: #555; margin-top: 30px; }
    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #f2f2f2; }
    tr:nth-child(even) { background-color: #f9f9f9; }
    .success { color: green; }
    .failure { color: red; }
    .warning { color: orange; }
  </style>
</head>
<body>
  <h1>Samstraumr Quality Report</h1>
  <p>Generated: $(date)</p>
  <p>Profile: $PROFILE</p>
  
  <h2>Summary</h2>
  <table>
    <tr>
      <th>Tool</th>
      <th>Status</th>
      <th>Details</th>
    </tr>
EOF
  
  # Add rows for each tool
  for tool in "${TOOLS[@]}"; do
    local status_file="${REPORT_DIR}/${tool}-status.txt"
    local details_file="${REPORT_DIR}/${tool}-details.txt"
    
    if [ -f "$status_file" ]; then
      local status=$(cat "$status_file")
      local details=""
      
      if [ -f "$details_file" ]; then
        details="See details below"
      fi
      
      if [ "$status" -eq 0 ]; then
        echo "    <tr><td>$tool</td><td class=\"success\">✅ Passed</td><td>$details</td></tr>" >> "$report_file"
      else
        echo "    <tr><td>$tool</td><td class=\"failure\">❌ Failed</td><td>$details</td></tr>" >> "$report_file"
      fi
    else
      echo "    <tr><td>$tool</td><td class=\"warning\">⚠️ Not Run</td><td></td></tr>" >> "$report_file"
    fi
  done
  
  # Close summary table and add details sections
  echo "  </table>" >> "$report_file"
  
  # Add detailed sections for each tool
  for tool in "${TOOLS[@]}"; do
    local details_file="${REPORT_DIR}/${tool}-details.txt"
    
    if [ -f "$details_file" ]; then
      echo "  <h2>$tool Details</h2>" >> "$report_file"
      echo "  <pre>" >> "$report_file"
      cat "$details_file" >> "$report_file"
      echo "  </pre>" >> "$report_file"
    fi
  done
  
  # Close HTML document
  cat >> "$report_file" << EOF
</body>
</html>
EOF
  
  info "Quality report generated: $report_file"
}

# Main execution
header "Unified Quality Checker"
info "Profile: $PROFILE"

# Create module directories
mkdir -p "${PROJECT_ROOT}/util/quality/modules"

# Load base modules
source "${PROJECT_ROOT}/util/lib/quality-lib.sh"

# Determine which tools to run
determine_tools

# Display tools to run
info "Tools to run: ${TOOLS[*]}"

# Run each tool
TOTAL_FAILURES=0

for tool in "${TOOLS[@]}"; do
  # Run the tool
  run_tool "$tool" "$FIX_ISSUES" > "${REPORT_DIR}/${tool}-details.txt" 2>&1
  RESULT=$?
  
  # Store the result
  echo "$RESULT" > "${REPORT_DIR}/${tool}-status.txt"
  
  # Update failure count
  if [ "$RESULT" -ne 0 ]; then
    TOTAL_FAILURES=$((TOTAL_FAILURES + 1))
    error "$tool check failed"
  else
    success "$tool check passed"
  fi
done

# Generate the unified report
generate_report

# Display final result
header "Quality Check Summary"

if [ "$TOTAL_FAILURES" -eq 0 ]; then
  success "All quality checks passed"
  exit 0
elif [ "$REPORT_ONLY" = "true" ]; then
  warn "$TOTAL_FAILURES quality checks failed, but continuing as report-only mode is enabled"
  exit 0
else
  error "$TOTAL_FAILURES quality checks failed"
  info "See detailed report at: ${REPORT_DIR}/quality-report-${TIMESTAMP}.html"
  exit 1
fi