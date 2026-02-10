#!/bin/bash
# Copyright (c) 2026 Eric C. Mumford <ericmumford@outlook.com>
#
# This file is part of Samstraumr.
# Licensed under Mozilla Public License 2.0.
# See LICENSE file for details.

#==============================================================================
# generate-javadoc.sh - Generates JavaDoc API documentation with customizations
# This script configures and runs JavaDoc with custom templates and organization
#==============================================================================

set -e

# Find repository root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
cd "$PROJECT_ROOT"

# Source the doc-lib library that contains the shared documentation utilities
if [ -f "${PROJECT_ROOT}/util/lib/doc-lib.sh" ]; then
  source "${PROJECT_ROOT}/util/lib/doc-lib.sh"
  USING_LIB=true
else
  echo "Warning: Documentation library not found. Using fallback functions."
  USING_LIB=false
  
  # Terminal colors
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[0;33m'
  BLUE='\033[0;34m'
  NC='\033[0m' # No Color

  # Functions for prettier output
  info() { echo -e "${BLUE}$1${NC}"; }
  success() { echo -e "${GREEN}$1${NC}"; }
  error() { echo -e "${RED}Error: $1${NC}" >&2; }
  warning() { echo -e "${YELLOW}Warning: $1${NC}" >&2; }
fi

# Default output directory
OUTPUT_DIR="${PROJECT_ROOT}/target/site/apidocs"
TEMPLATE_DIR="${PROJECT_ROOT}/docs/assets/templates"

# Get the current project version using the library if available
if [[ "$USING_LIB" == true ]] && type get_maven_property &>/dev/null; then
  # Attempt to use the unified library function
  VERSION=""
  
  # First try the version.properties file
  if [ -f "${PROJECT_ROOT}/modules/version.properties" ]; then
    VERSION=$(grep "version=" "${PROJECT_ROOT}/modules/version.properties" | cut -d= -f2)
  fi
  
  # If not found, try to get from pom.xml using the library function
  if [ -z "$VERSION" ] && [ -f "${PROJECT_ROOT}/pom.xml" ]; then
    VERSION=$(get_maven_property "${PROJECT_ROOT}/pom.xml" "project.version")
  fi
else
  # Fall back to original implementation
  VERSION=$(grep "version=" "${PROJECT_ROOT}/modules/version.properties" 2>/dev/null | cut -d= -f2 || grep -m 1 "<version>" "${PROJECT_ROOT}/pom.xml" | sed 's/.*<version>\(.*\)<\/version>.*/\1/')
fi

# Also ensure REPORT_DIR is set if we're using the library
if [[ "$USING_LIB" == true ]] && [ -z "$REPORT_DIR" ]; then
  REPORT_DIR="${PROJECT_ROOT}/target/doc-reports"
  mkdir -p "$REPORT_DIR"
fi

# Parameter handling
show_help() {
  if [[ "$USING_LIB" == true ]] && type print_header &>/dev/null && type print_info &>/dev/null; then
    print_header "JavaDoc Generator Help"
    print_info "Generates JavaDoc API documentation with customizations"
    echo
    
    print_bold "Usage: $(basename "$0") [options]"
    echo
    
    print_bold "Options:"
    echo "  -o, --output DIR      Output directory (default: target/site/apidocs)"
    echo "  -t, --template DIR    Template directory (default: docs/assets/templates)"
    echo "  -p, --packages LIST   Comma-separated list of packages to document"
    echo "  -l, --links           Generate links to GitHub source"
    echo "  -m, --markdown        Include Markdown documentation"
    echo "  -h, --help            Show this help message"
    echo
    
    print_bold "Examples:"
    echo "  $(basename "$0") --packages org.s8r.component,org.s8r.domain"
    echo "  $(basename "$0") --output docs/api --links --markdown"
  else
    # Fallback to original implementation
    echo "Usage: $(basename "$0") [options]"
    echo
    echo "Options:"
    echo "  -o, --output DIR      Output directory (default: target/site/apidocs)"
    echo "  -t, --template DIR    Template directory (default: docs/assets/templates)"
    echo "  -p, --packages LIST   Comma-separated list of packages to document"
    echo "  -l, --links           Generate links to GitHub source"
    echo "  -m, --markdown        Include Markdown documentation"
    echo "  -h, --help            Show this help message"
    echo
    echo "Examples:"
    echo "  $(basename "$0") --packages org.s8r.component,org.s8r.domain"
    echo "  $(basename "$0") --output docs/api --links --markdown"
  fi
}

# Default settings
PACKAGES="org.s8r"
INCLUDE_LINKS=false
INCLUDE_MARKDOWN=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    -o|--output)
      OUTPUT_DIR="$2"
      shift 2
      ;;
    -t|--template)
      TEMPLATE_DIR="$2"
      shift 2
      ;;
    -p|--packages)
      PACKAGES="$2"
      shift 2
      ;;
    -l|--links)
      INCLUDE_LINKS=true
      shift
      ;;
    -m|--markdown)
      INCLUDE_MARKDOWN=true
      shift
      ;;
    -h|--help)
      show_help
      exit 0
      ;;
    *)
      error "Unknown option: $1"
      show_help
      exit 1
      ;;
  esac
done

info "Generating JavaDoc API documentation"
info "Version: ${VERSION}"
info "Output directory: ${OUTPUT_DIR}"

# Create output directory if it doesn't exist
mkdir -p "${OUTPUT_DIR}"

# Create custom stylesheet directory if it doesn't exist
STYLESHEET_DIR="${TEMPLATE_DIR}/javadoc-styles"
mkdir -p "${STYLESHEET_DIR}"

# Create custom stylesheet if it doesn't exist
if [ ! -f "${STYLESHEET_DIR}/custom-stylesheet.css" ]; then
  info "Creating custom stylesheet"
  cat > "${STYLESHEET_DIR}/custom-stylesheet.css" << EOF
/* Samstraumr custom JavaDoc stylesheet */
:root {
  --s8r-primary: #304878;
  --s8r-secondary: #6388b4;
  --s8r-accent: #8bb8e8;
  --s8r-light: #f8f9fa;
  --s8r-dark: #212529;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  line-height: 1.6;
  color: var(--s8r-dark);
  background-color: var(--s8r-light);
  margin: 0;
  padding: 0;
}

.topNav, .subNav {
  background-color: var(--s8r-primary);
  color: white;
}

.topNav a:link, .topNav a:visited, 
.subNav a:link, .subNav a:visited {
  color: white;
}

.topNav a:hover, .subNav a:hover {
  color: var(--s8r-accent);
}

h1, h2, h3, h4, h5, h6 {
  color: var(--s8r-primary);
}

.contentContainer .description dl dt, .contentContainer .details dl dt {
  font-weight: bold;
  color: var(--s8r-primary);
}

.memberSummary caption {
  background-color: var(--s8r-secondary);
  color: white;
}
EOF
  success "Created custom stylesheet"
fi

# Generate JavaDoc options
JAVADOC_OPTS=(
  "-d" "${OUTPUT_DIR}"
  "-quiet"
  "-source" "21"
  "-sourcepath" "${PROJECT_ROOT}/modules/samstraumr-core/src/main/java"
  "-classpath" "$(mvn -q dependency:build-classpath -DincludeScope=compile -Dmdep.outputFile=/dev/stdout)"
  "-subpackages" "${PACKAGES}"
  "-windowtitle" "Samstraumr API Documentation v${VERSION}"
  "-doctitle" "Samstraumr API<br>Documentation v${VERSION}"
  "-header" "<strong>Samstraumr API Documentation</strong>"
  "-bottom" "Copyright &copy; $(date +%Y) Eric C. Mumford. Licensed under MPL 2.0."
  "-overview" "${PROJECT_ROOT}/modules/samstraumr-core/src/main/java/overview.html"
  "-stylesheetfile" "${STYLESHEET_DIR}/custom-stylesheet.css"
  "-tag" "apiNote:a:API Note:"
  "-tag" "implNote:a:Implementation Note:"
  "-tag" "implSpec:a:Implementation Requirements:"
  "-Xdoclint:all,-missing"
)

# Create overview.html if it doesn't exist
if [ ! -f "${PROJECT_ROOT}/modules/samstraumr-core/src/main/java/overview.html" ]; then
  info "Creating overview.html"
  mkdir -p "$(dirname "${PROJECT_ROOT}/modules/samstraumr-core/src/main/java/overview.html")"
  cat > "${PROJECT_ROOT}/modules/samstraumr-core/src/main/java/overview.html" << EOF
<!DOCTYPE HTML>
<html>
<head>
    <title>Samstraumr API Overview</title>
</head>
<body>
    <h1>Samstraumr API Overview</h1>
    <p>Samstraumr is a component-based architecture framework for building scalable, 
       resilient, and maintainable systems. It provides a structured approach to 
       organizing application components and their interactions.</p>
    
    <h2>Key Packages</h2>
    <ul>
        <li><code>org.s8r.component</code> - Core component model and lifecycle management</li>
        <li><code>org.s8r.domain</code> - Domain model and business logic</li>
        <li><code>org.s8r.application</code> - Application services and use cases</li>
        <li><code>org.s8r.infrastructure</code> - Infrastructure and technical concerns</li>
        <li><code>org.s8r.adapter</code> - Integration adapters for external systems</li>
    </ul>
    
    <h2>Getting Started</h2>
    <p>Start by examining the <code>Component</code> interface and its implementations,
       which form the foundation of the Samstraumr architecture.</p>
    
    <h2>Version Information</h2>
    <p>This documentation applies to Samstraumr version ${VERSION}.</p>
</body>
</html>
EOF
  success "Created overview.html"
fi

# Add GitHub links if requested
if [ "$INCLUDE_LINKS" = true ]; then
  info "Adding GitHub source links"
  REPO_URL="https://github.com/emumford/Samstraumr"
  JAVADOC_OPTS+=(
    "-link" "https://docs.oracle.com/en/java/javase/21/docs/api/"
    "-linkoffline" "${REPO_URL}/blob/main/modules/samstraumr-core/src/main/java" "${REPO_URL}/blob/main/modules/samstraumr-core/src/main/java"
  )
fi

# Include markdown documentation if requested
if [ "$INCLUDE_MARKDOWN" = true ]; then
  info "Including Markdown documentation"
  
  # Create a temporary directory for converted Markdown files
  MARKDOWN_DIR="${OUTPUT_DIR}/markdown"
  mkdir -p "${MARKDOWN_DIR}"
  
  # Use documentation library functions if available
  if [[ "$USING_LIB" == true ]] && type find_readme_files &>/dev/null; then
    # Let the user know we're using library functions
    if type print_success &>/dev/null; then
      print_success "Using documentation library functions for Markdown processing"
    fi
    
    # Get a list of markdown files to process
    local md_files=()
    
    # Start with README files
    if type find_readme_files &>/dev/null; then
      for file in $(find_readme_files); do
        # Skip files from certain directories
        if [[ "$file" == *"/planning/"* || "$file" == *"/tools/"* ]]; then
          continue
        fi
        md_files+=("$file")
      done
    fi
    
    # Add other markdown files
    for file in $(find "${PROJECT_ROOT}/docs" -type f -name "*.md" -not -name "README.md" -not -path "*/\.*"); do
      # Skip files from certain directories
      if [[ "$file" == *"/planning/"* || "$file" == *"/tools/"* ]]; then
        continue
      fi
      md_files+=("$file")
    done
  else
    # Fall back to original implementation
    md_files=($(find "${PROJECT_ROOT}/docs" -name "*.md" -not -path "*/\.*"))
  fi
  
  # Convert Markdown to HTML
  for md_file in "${md_files[@]}"; do
    # Skip files from certain directories
    if [[ "$md_file" == *"/planning/"* || "$md_file" == *"/tools/"* ]]; then
      continue
    fi
    
    # Get relative path
    rel_path="${md_file#${PROJECT_ROOT}/docs/}"
    dir_path=$(dirname "${MARKDOWN_DIR}/${rel_path}")
    mkdir -p "${dir_path}"
    
    # Convert to HTML
    html_file="${MARKDOWN_DIR}/${rel_path%.md}.html"
    
    # Use library function to get the title if available
    if [[ "$USING_LIB" == true ]] && type get_level1_header &>/dev/null; then
      title=$(get_level1_header "$md_file")
      if [ -z "$title" ]; then
        title=$(basename "$md_file" .md)
      fi
    else
      title=$(head -n 1 "$md_file" | sed 's/^#\s*//')
    fi
    
    info "Converting ${rel_path} to HTML"
    
    {
      echo "<!DOCTYPE html>"
      echo "<html>"
      echo "<head>"
      echo "  <title>${title}</title>"
      echo "  <link rel='stylesheet' href='../stylesheet.css'>"
      echo "</head>"
      echo "<body class='markdown-content'>"
      
      # Use pandoc if available, otherwise fallback to simple conversion
      if command -v pandoc &> /dev/null; then
        pandoc -f markdown -t html "${md_file}"
      else
        # Simple conversion if pandoc is not available
        echo "<h1>${title}</h1>"
        sed '1d' "${md_file}" | sed 's/^#\(#*\) \(.*\)/<h\1\2<\/h\1>/' | sed 's/^- \(.*\)/<li>\1<\/li>/' | sed 's/^```.*/<pre><code>/' | sed 's/^```/<\/code><\/pre>/' | sed 's/\[\([^\]]*\)\](\([^)]*\))/<a href="\2">\1<\/a>/g'
      fi
      
      echo "</body>"
      echo "</html>"
    } > "${html_file}"
  done
  
  # Create an index for Markdown documentation
  {
    echo "<!DOCTYPE html>"
    echo "<html>"
    echo "<head>"
    echo "  <title>Samstraumr Documentation</title>"
    echo "  <link rel='stylesheet' href='stylesheet.css'>"
    echo "</head>"
    echo "<body>"
    echo "<h1>Samstraumr Documentation</h1>"
    echo "<p>This is the combined documentation for Samstraumr version ${VERSION}.</p>"
    echo "<ul>"
    echo "  <li><a href='index.html'>API Documentation</a></li>"
    echo "  <li><a href='markdown/index.html'>Markdown Documentation</a></li>"
    echo "</ul>"
    echo "</body>"
    echo "</html>"
  } > "${OUTPUT_DIR}/documentation.html"
  
  # Create an index for just the Markdown docs
  {
    echo "<!DOCTYPE html>"
    echo "<html>"
    echo "<head>"
    echo "  <title>Samstraumr Markdown Documentation</title>"
    echo "  <link rel='stylesheet' href='../stylesheet.css'>"
    echo "</head>"
    echo "<body>"
    echo "<h1>Samstraumr Markdown Documentation</h1>"
    echo "<p>This is the Markdown documentation for Samstraumr version ${VERSION}.</p>"
    echo "<h2>Documentation Categories</h2>"
    echo "<ul>"
    
    # List directories as categories
    for dir in $(find "${MARKDOWN_DIR}" -type d -not -path "${MARKDOWN_DIR}" | sort); do
      dir_name=$(basename "${dir}")
      echo "  <li><h3>${dir_name}</h3>"
      echo "    <ul>"
      
      # List files in each directory
      for file in $(find "${dir}" -type f -name "*.html" | sort); do
        file_name=$(basename "${file%.html}")
        file_title=$(grep -m 1 "<title>" "${file}" | sed 's/.*<title>\(.*\)<\/title>.*/\1/')
        rel_path="${file#${MARKDOWN_DIR}/}"
        echo "      <li><a href='${rel_path}'>${file_title}</a></li>"
      done
      
      echo "    </ul>"
      echo "  </li>"
    done
    
    echo "</ul>"
    echo "</body>"
    echo "</html>"
  } > "${MARKDOWN_DIR}/index.html"
fi

# Run JavaDoc
info "Running JavaDoc with these options:"
printf '  %s\n' "${JAVADOC_OPTS[@]}"
info "This may take a moment..."

# Create a file with compile errors
ERROR_LOG="${OUTPUT_DIR}/javadoc-errors.log"
touch "${ERROR_LOG}"

# Execute javadoc command
if javadoc "${JAVADOC_OPTS[@]}" 2> "${ERROR_LOG}"; then
  # Use library functions if available
  if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
    print_success "JavaDoc generated successfully in ${OUTPUT_DIR}"
  else
    success "JavaDoc generated successfully in ${OUTPUT_DIR}"
  fi
  
  # Check if there were any warnings
  if [ -s "${ERROR_LOG}" ]; then
    if [[ "$USING_LIB" == true ]] && type print_warning &>/dev/null; then
      print_warning "JavaDoc completed with warnings:"
      cat "${ERROR_LOG}"
    else
      warning "JavaDoc completed with warnings:"
      cat "${ERROR_LOG}"
    fi
  else
    rm "${ERROR_LOG}"
  fi
  
  # Create a simple index page that combines API and Markdown documentation
  if [ "$INCLUDE_MARKDOWN" = true ]; then
    cp "${OUTPUT_DIR}/documentation.html" "${OUTPUT_DIR}/index.html"
    mv "${OUTPUT_DIR}/index.html" "${OUTPUT_DIR}/api-index.html"
    
    if [[ "$USING_LIB" == true ]] && type print_success &>/dev/null; then
      print_success "Created combined documentation index"
    else
      success "Created combined documentation index"
    fi
    
    # Copy documentation to reports directory if using library
    if [[ "$USING_LIB" == true ]] && [ -d "$REPORT_DIR" ]; then
      local date_stamp=$(date +%Y%m%d)
      cp -r "${OUTPUT_DIR}" "${REPORT_DIR}/javadoc-${date_stamp}"
      print_info "Documentation backup created at ${REPORT_DIR}/javadoc-${date_stamp}"
    fi
  fi
  
  echo
  if [[ "$USING_LIB" == true ]] && type print_info &>/dev/null; then
    print_info "To view the documentation, open the following file in your browser:"
    print_bold "file://${OUTPUT_DIR}/index.html"
  else
    info "To view the documentation, open the following file in your browser:"
    echo "file://${OUTPUT_DIR}/index.html"
  fi
else
  if [[ "$USING_LIB" == true ]] && type print_error &>/dev/null; then
    print_error "JavaDoc generation failed with errors:"
    cat "${ERROR_LOG}"
  else
    error "JavaDoc generation failed with errors:"
    cat "${ERROR_LOG}"
  fi
  exit 1
fi

# Integration with the s8r CLI
if [ -f "${PROJECT_ROOT}/s8r" ]; then
  if [[ "$USING_LIB" == true ]] && type print_info &>/dev/null; then
    print_info "To regenerate the documentation in the future, you can use:"
    print_bold "  ./s8r docs api"
    print_bold "  ./s8r docs markdown"
    print_bold "  ./s8r docs all"
  else
    info "To regenerate the documentation in the future, you can use:"
    echo "  ./s8r docs api"
    echo "  ./s8r docs markdown"
    echo "  ./s8r docs all"
  fi
fi