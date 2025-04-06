#!/bin/bash
#==============================================================================
# javadoc.sh
# JavaDoc documentation check module for unified quality checker
#==============================================================================

# Check if the function is already defined
if declare -f "check_javadoc" > /dev/null; then
  return 0
fi

# Function to check JavaDoc documentation
check_javadoc() {
  local fix_issues="${1:-false}"
  
  header "Checking JavaDoc Documentation"
  
  # Load thresholds
  local javadoc_coverage="80"
  if [ -f "${QUALITY_DIR}/quality-thresholds.xml" ]; then
    # Extract JavaDoc coverage threshold from XML if possible
    if command -v xmllint > /dev/null; then
      local extracted_threshold=$(xmllint --xpath "string(/quality-thresholds/documentation/javadoc-coverage)" "${QUALITY_DIR}/quality-thresholds.xml" 2>/dev/null)
      if [ -n "$extracted_threshold" ]; then
        javadoc_coverage="$extracted_threshold"
      fi
    fi
  fi
  
  info "Using JavaDoc coverage threshold: $javadoc_coverage%"
  
  # Build the JavaDoc command
  local javadoc_cmd="mvn javadoc:javadoc -Djavadoc.minmembers=0"
  
  info "Generating JavaDoc documentation..."
  
  # Run the command
  if eval "$javadoc_cmd"; then
    success "JavaDoc generation passed"
    info "JavaDoc available at: target/site/apidocs/index.html"
    
    # Copy JavaDoc to unified report directory
    if [ -d "target/site/apidocs" ]; then
      mkdir -p "${REPORT_DIR}/javadoc"
      cp -r target/site/apidocs/* "${REPORT_DIR}/javadoc/"
      info "JavaDoc copied to: ${REPORT_DIR}/javadoc/"
    fi
    
    return 0
  else
    error "JavaDoc generation failed - documentation errors found"
    warn "Fix JavaDoc errors in source code"
    
    # Note: JavaDoc issues must be fixed manually
    if [ "$fix_issues" = "true" ]; then
      warn "JavaDoc issues must be fixed manually"
    fi
    
    return 1
  fi
}