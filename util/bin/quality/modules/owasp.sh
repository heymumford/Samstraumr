#!/bin/bash
#==============================================================================
# owasp.sh
# OWASP Dependency Check module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_owasp" > /dev/null; then
  return 0
fi

# Function to check for security vulnerabilities with OWASP Dependency Check
check_owasp() {
  local fix_issues="${1:-false}"
  
  header "Checking for Security Vulnerabilities with OWASP Dependency Check"
  
  # Load thresholds
  local cvss_threshold="7.0"
  if [ -f "${QUALITY_DIR}/quality-thresholds.xml" ]; then
    # Extract CVSS threshold from XML if possible
    if command -v xmllint > /dev/null; then
      local extracted_threshold=$(xmllint --xpath "string(/quality-thresholds/security/cvss)" "${QUALITY_DIR}/quality-thresholds.xml" 2>/dev/null)
      if [ -n "$extracted_threshold" ]; then
        cvss_threshold="$extracted_threshold"
      fi
    fi
  fi
  
  info "Using CVSS threshold: $cvss_threshold"
  
  # Build the OWASP command
  local owasp_cmd="mvn org.owasp:dependency-check-maven:check -DfailBuildOnCVSS=${cvss_threshold}"
  
  info "Scanning dependencies for vulnerabilities..."
  
  # Run the command
  if eval "$owasp_cmd"; then
    success "OWASP check passed - no critical vulnerabilities detected"
    return 0
  else
    error "OWASP check failed - critical vulnerabilities detected"
    warn "Check target/dependency-check-report.html for details"
    
    # Copy report to unified report directory
    if [ -f "target/dependency-check-report.html" ]; then
      cp "target/dependency-check-report.html" "${REPORT_DIR}/dependency-check-report.html"
      info "Report copied to: ${REPORT_DIR}/dependency-check-report.html"
    fi
    
    # Note: OWASP can't automatically fix issues
    if [ "$fix_issues" = "true" ]; then
      warn "Security vulnerabilities must be fixed manually by updating dependencies"
    fi
    
    return 1
  fi
}