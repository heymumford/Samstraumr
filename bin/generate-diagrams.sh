#!/usr/bin/env bash
#==============================================================================
# Filename: generate-diagrams.sh
# Description: Script for generating C4 diagrams for the Samstraumr project
#
# This script generates C4 model diagrams (Context, Containers, Components, Code)
# for the Samstraumr architecture. It supports synchronous and asynchronous
# generation, multiple output formats, and automatic documentation.
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
# This file is subject to the terms and conditions defined in
# the LICENSE file, which is part of this source code package.
#==============================================================================
# Usage: ./bin/generate-diagrams.sh [options]
#
# Options:
#   --async      Generate diagrams asynchronously (default: false)
#   --format     Output format for diagrams (default: svg)
#   --type       Type of diagrams to generate (default: all)
#   --output-dir Output directory for diagrams (default: docs/diagrams)
#   --clean      Clean existing diagrams before generating new ones
#   --help       Show this help message
#==============================================================================

# Determine script directory and load common library
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

# Source the unified common library
if [[ -f "${PROJECT_ROOT}/util/lib/unified-common.sh" ]]; then
  source "${PROJECT_ROOT}/util/lib/unified-common.sh"
else
  # Fallback to original common.sh if unified version not available
  source "${PROJECT_ROOT}/util/lib/common.sh"
fi

# Default options
ASYNC=false
FORMAT="svg"
TYPE="all"
OUTPUT_DIR="docs/diagrams"
CLEAN=false

# Parse command line arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    --async)
      ASYNC=true
      shift
      ;;
    --format)
      FORMAT="$2"
      shift 2
      ;;
    --type)
      TYPE="$2"
      shift 2
      ;;
    --output-dir)
      OUTPUT_DIR="$2"
      shift 2
      ;;
    --clean)
      CLEAN=true
      shift
      ;;
    --help)
      print_header "C4 Diagram Generator"
      echo "Generates C4 model diagrams for the Samstraumr architecture."
      echo ""
      print_section "Usage"
      echo "./bin/generate-diagrams.sh [--async] [--format png|svg|pdf] [--type context|container|component|code|clean|all] [--output-dir path] [--clean]"
      echo ""
      print_section "Options"
      echo "  --async      Generate diagrams asynchronously (default: false)"
      echo "  --format     Output format for diagrams (default: svg)"
      echo "  --type       Type of diagrams to generate:"
      echo "               - context:   System context diagram"
      echo "               - container: Container diagram showing major components"
      echo "               - component: Component diagram showing system structure"
      echo "               - code:      Code diagram showing key classes"
      echo "               - clean:     Clean Architecture diagram showing layers"
      echo "               - port:      Port interface diagrams showing Clean Architecture ports"
      echo "               - all:       Generate all diagram types (default)"
      echo "  --output-dir Output directory for diagrams (default: docs/diagrams)"
      echo "  --clean      Clean existing diagrams before generating new ones"
      echo "  --help       Show this help message"
      exit 0
      ;;
    *)
      print_error "Unknown option: $1"
      echo "Use --help to see available options"
      exit 1
      ;;
  esac
done

# Create output directory
ensure_directory_exists "$OUTPUT_DIR"

# Clean existing diagrams if requested
if [[ "$CLEAN" == "true" ]]; then
  print_info "Cleaning existing diagrams in $OUTPUT_DIR..."
  rm -f "$OUTPUT_DIR"/*.png "$OUTPUT_DIR"/*.svg "$OUTPUT_DIR"/*.pdf
fi

# Create a lock file to prevent concurrent generation
LOCK_FILE="${OUTPUT_DIR}/.diagram-generation.lock"

# Function to generate diagrams
function generate_diagrams() {
  # Check for Python environment
  if [[ -f "${PROJECT_ROOT}/activate-python.sh" ]]; then
    source "${PROJECT_ROOT}/activate-python.sh"
  elif [[ -f "${PROJECT_ROOT}/.venv/bin/activate" ]]; then
    source "${PROJECT_ROOT}/.venv/bin/activate"
  fi

  # Check for diagram generator script
  if [[ ! -f "${PROJECT_ROOT}/bin/c4_diagrams.py" ]]; then
    print_warning "C4 diagram generator script not found at ${PROJECT_ROOT}/bin/c4_diagrams.py"
    print_info "Creating a basic diagram generator script..."
    create_basic_generator_script
  fi

  # Create timestamp file to indicate generation in progress
  TIMESTAMP=$(date +%Y%m%d%H%M%S)
  touch "${OUTPUT_DIR}/.generating-${TIMESTAMP}"

  # Acquire lock
  if ! mkdir "$LOCK_FILE" 2>/dev/null; then
    print_warning "Another diagram generation process is already running. Skipping."
    return 0
  fi

  # Generate diagrams
  print_info "Generating $TYPE C4 diagrams in $FORMAT format..."
  python "${PROJECT_ROOT}/bin/c4_diagrams.py" --type "$TYPE" --output "$FORMAT" --dir "$OUTPUT_DIR"
  
  # Generate port interface diagrams
  if [[ "$TYPE" == "all" || "$TYPE" == "port" || "$TYPE" == "component" ]]; then
    print_info "Generating port interface diagrams in $FORMAT format..."
    python "${PROJECT_ROOT}/bin/port_interfaces_diagram.py" --output "$FORMAT" --dir "$OUTPUT_DIR"
  fi
  
  # Create markdown documentation that includes the diagrams
  generate_diagram_documentation
  
  # Release lock
  rmdir "$LOCK_FILE"
  
  # Remove timestamp file
  rm -f "${OUTPUT_DIR}/.generating-${TIMESTAMP}"
  
  # Create a marker file to indicate when diagrams were last generated
  date > "${OUTPUT_DIR}/.last-generated"
  
  print_success "Diagram generation completed successfully."
}

# Function to create a basic diagram generator script if the main one doesn't exist
function create_basic_generator_script() {
  cat > "${PROJECT_ROOT}/bin/c4_diagrams.py" << 'EOF'
#!/usr/bin/env python3
"""
Generate C4 model diagrams for the Samstraumr project

This script generates C4 model diagrams (Context, Container, Component, and Code)
for the Samstraumr architecture.

Example usage:
    ./bin/c4_diagrams.py --type context
    ./bin/c4_diagrams.py --type container --output svg
    ./bin/c4_diagrams.py --type all

Dependencies:
    - diagrams (pip install diagrams)
    - graphviz (system package)
"""

import os
import sys
import argparse
from pathlib import Path

# Import diagrams if available
try:
    from diagrams import Diagram, Cluster
    from diagrams.programming.language import Java
    from diagrams.onprem.database import PostgreSQL
    from diagrams.onprem.client import Users, User
    from diagrams.onprem.compute import Server
    from diagrams.generic.storage import Storage
    from diagrams.programming.framework import Spring
    diagrams_available = True
except ImportError:
    diagrams_available = False
    print("Warning: diagrams package not available. Please install it with:")
    print("  pip install diagrams")
    print("And make sure graphviz is installed:")
    print("  apt-get install graphviz  # Ubuntu/Debian")
    print("  brew install graphviz      # macOS")

class C4DiagramGenerator:
    """Generate C4 model diagrams for the Samstraumr project."""
    
    def __init__(self, output_dir: str = "docs/diagrams", output_format: str = "png"):
        """Initialize the diagram generator.
        
        Args:
            output_dir: Directory to save output files
            output_format: Output format (png, svg, pdf)
        """
        self.output_format = output_format.lower()
        if self.output_format not in ("png", "svg", "pdf"):
            print(f"Unsupported output format: {output_format}, defaulting to png")
            self.output_format = "png"
        
        # Get output directory
        self.output_dir = Path(output_dir)
        
        # Create output directory if it doesn't exist
        self.output_dir.mkdir(parents=True, exist_ok=True)
    
    def generate_context_diagram(self) -> str:
        """Generate a C4 context diagram.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate context diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_context_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr System Context",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
            ):
                users = Users("Development Teams")
                
                with Cluster("Samstraumr Framework"):
                    core = Java("Samstraumr Core")
                    api = Java("API")
                    db = PostgreSQL("Event Store")
                
                with Cluster("External Systems"):
                    vcs = Server("Version Control")
                    ci = Server("CI/CD System")
                    docs = Storage("Document Repository")
                
                users >> api >> core >> db
                users >> core
                core >> vcs
                core >> docs
                api >> ci
            
            print(f"Generated context diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate context diagram: {e}")
            return ""
    
    def generate_container_diagram(self) -> str:
        """Generate a C4 container diagram.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate container diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_container_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Container Diagram",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
            ):
                dev = User("Developer")
                
                with Cluster("Samstraumr Framework"):
                    with Cluster("Core Framework"):
                        tubes = Java("Tubes")
                        components = Java("Components")
                        identity = Java("Identity")
                        
                    with Cluster("Orchestration"):
                        machine = Java("Machine")
                        composite = Java("Composite")
                    
                    with Cluster("Infrastructure"):
                        events = Java("Event Dispatcher")
                        db = PostgreSQL("Event Store")
                
                with Cluster("External Systems"):
                    ci_cd = Server("CI/CD Pipeline")
                
                dev >> tubes >> events
                dev >> components >> machine >> composite
                tubes >> identity
                components >> identity
                composite >> identity
                events >> db
                machine >> ci_cd
            
            print(f"Generated container diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate container diagram: {e}")
            return ""
    
    def generate_component_diagram(self) -> str:
        """Generate a C4 component diagram.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate component diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_component_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Component Diagram",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
            ):
                with Cluster("Core Domain"):
                    tubes = Java("Tube")
                    components = Java("Component")
                    identifiers = Java("Identity")
                    lifecycle = Java("Lifecycle")
                
                with Cluster("Orchestration"):
                    machine = Java("Machine")
                    composite = Java("Composite")
                    flow = Java("DataFlow")
                
                with Cluster("Infrastructure"):
                    repo = Java("Repository")
                    events = Java("EventDispatcher")
                    logging = Java("Logger")
                
                # Connect components
                tubes >> identifiers
                components >> identifiers
                
                composite >> components
                machine >> components
                flow >> components
                
                composite >> repo
                machine >> repo
                
                events >> logging
                events >> repo
            
            print(f"Generated component diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate component diagram: {e}")
            return ""
    
    def generate_code_diagram(self) -> str:
        """Generate a C4 code diagram.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate code diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_code_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Code Diagram",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
                direction="TB",
            ):
                with Cluster("Domain Model"):
                    tube = Java("Tube")
                    component = Java("Component")
                    identity = Java("Identity")
                    state = Java("LifecycleState")
                
                with Cluster("Domain Services"):
                    tube_factory = Java("TubeFactory")
                    component_factory = Java("ComponentFactory")
                    machine_factory = Java("MachineFactory")
                    composite_factory = Java("CompositeFactory")
                
                with Cluster("Repositories"):
                    tube_repo = Java("TubeRepository")
                    component_repo = Java("ComponentRepository")
                    machine_repo = Java("MachineRepository")
                
                # Connect classes
                tube_factory >> tube >> tube_repo
                component_factory >> component >> component_repo
                machine_factory >> component >> machine_repo
                composite_factory >> component
                tube >> state
                component >> state
                tube >> identity
                component >> identity
            
            print(f"Generated code diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate code diagram: {e}")
            return ""
    
    def generate_clean_architecture_diagram(self) -> str:
        """Generate a diagram showing the Clean Architecture layers.
        
        Returns:
            Path to the generated diagram file
        """
        if not diagrams_available:
            print("Diagrams library not available. Cannot generate clean architecture diagram.")
            return ""
        
        output_file = self.output_dir / f"samstraumr_clean_architecture_diagram.{self.output_format}"
        
        try:
            with Diagram(
                "Samstraumr Clean Architecture",
                filename=str(output_file.with_suffix("")),
                outformat=self.output_format,
                show=False,
                direction="TB",
                curvestyle="ortho",
            ):
                with Cluster("Core Domain"):
                    entity = Java("Entities")
                
                with Cluster("Use Cases"):
                    usecases = Java("Use Cases")
                    
                with Cluster("Interface Adapters"):
                    with Cluster("Input Adapters"):
                        controllers = Java("Controllers")
                        presenters = Java("Presenters")
                    
                    with Cluster("Output Adapters"):
                        gateways = Java("Gateways")
                        repositories = Java("Repositories")
                
                with Cluster("Frameworks & Drivers"):
                    with Cluster("UI"):
                        web = Spring("Web UI")
                        cli = Java("CLI")
                    
                    with Cluster("External Interfaces"):
                        db = PostgreSQL("Database")
                        external_api = Java("External APIs")
                    
                # Dependency Rule: Arrows point inward
                web >> controllers
                cli >> controllers
                controllers >> usecases
                presenters >> usecases
                usecases >> entity
                repositories >> db
                gateways >> external_api
                usecases >> repositories
                usecases >> gateways
                presenters >> web
                
            print(f"Generated clean architecture diagram: {output_file}")
            return str(output_file)
        
        except Exception as e:
            print(f"Failed to generate clean architecture diagram: {e}")
            return ""
    
    def generate_all(self) -> list:
        """Generate all C4 diagrams.
        
        Returns:
            List of paths to generated diagrams
        """
        files = []
        
        # Context diagram
        context_file = self.generate_context_diagram()
        if context_file:
            files.append(context_file)
        
        # Container diagram
        container_file = self.generate_container_diagram()
        if container_file:
            files.append(container_file)
        
        # Component diagram
        component_file = self.generate_component_diagram()
        if component_file:
            files.append(component_file)
        
        # Code diagram
        code_file = self.generate_code_diagram()
        if code_file:
            files.append(code_file)
        
        # Clean architecture diagram
        clean_arch_file = self.generate_clean_architecture_diagram()
        if clean_arch_file:
            files.append(clean_arch_file)
        
        return files


def main():
    """Main entry point for the script."""
    parser = argparse.ArgumentParser(description="Generate C4 model diagrams for Samstraumr")
    parser.add_argument(
        "--type", 
        choices=["context", "container", "component", "code", "clean", "all"],
        default="all",
        help="Type of C4 diagram to generate (default: all)"
    )
    parser.add_argument(
        "--output", 
        choices=["png", "svg", "pdf"],
        default="png",
        help="Output format (default: png)"
    )
    parser.add_argument(
        "--dir",
        help="Output directory (default: docs/diagrams)"
    )
    
    args = parser.parse_args()
    
    # Initialize generator
    generator = C4DiagramGenerator(
        output_dir=args.dir or "docs/diagrams",
        output_format=args.output
    )
    
    # Generate diagrams based on type
    if args.type == "context":
        generator.generate_context_diagram()
    elif args.type == "container":
        generator.generate_container_diagram()
    elif args.type == "component":
        generator.generate_component_diagram()
    elif args.type == "code":
        generator.generate_code_diagram()
    elif args.type == "clean":
        generator.generate_clean_architecture_diagram()
    else:  # "all"
        generator.generate_all()


if __name__ == "__main__":
    main()
EOF
  chmod +x "${PROJECT_ROOT}/bin/c4_diagrams.py"
  print_success "Created basic diagram generator script at ${PROJECT_ROOT}/bin/c4_diagrams.py"
}

# Function to generate markdown documentation for diagrams
function generate_diagram_documentation() {
  DOC_FILE="${OUTPUT_DIR}/README.md"
  
  echo "# Samstraumr C4 Architecture Diagrams" > "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  echo "Last generated: $(date)" >> "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  echo "This directory contains automatically generated C4 model diagrams for the Samstraumr project architecture." >> "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  
  # Context diagram
  if [[ -f "${OUTPUT_DIR}/samstraumr_context_diagram.${FORMAT}" ]]; then
    echo "## Context Diagram" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "The context diagram shows the high-level system context and external dependencies:" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "![Context Diagram](./samstraumr_context_diagram.${FORMAT})" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
  fi
  
  # Container diagram
  if [[ -f "${OUTPUT_DIR}/samstraumr_container_diagram.${FORMAT}" ]]; then
    echo "## Container Diagram" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "The container diagram shows the major components and their relationships:" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "![Container Diagram](./samstraumr_container_diagram.${FORMAT})" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
  fi
  
  # Component diagram
  if [[ -f "${OUTPUT_DIR}/samstraumr_component_diagram.${FORMAT}" ]]; then
    echo "## Component Diagram" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "The component diagram shows the internal components of the system:" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "![Component Diagram](./samstraumr_component_diagram.${FORMAT})" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
  fi
  
  # Code diagram
  if [[ -f "${OUTPUT_DIR}/samstraumr_code_diagram.${FORMAT}" ]]; then
    echo "## Code Diagram" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "The code diagram shows the key classes and their relationships:" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "![Code Diagram](./samstraumr_code_diagram.${FORMAT})" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
  fi
  
  # Clean Architecture diagram
  if [[ -f "${OUTPUT_DIR}/samstraumr_clean_architecture_diagram.${FORMAT}" ]]; then
    echo "## Clean Architecture Diagram" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "The clean architecture diagram shows the architectural layers and their relationships:" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
    echo "![Clean Architecture Diagram](./samstraumr_clean_architecture_diagram.${FORMAT})" >> "$DOC_FILE"
    echo "" >> "$DOC_FILE"
  fi
  
  echo "## Diagram Generation" >> "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  echo "These diagrams are automatically generated during the build process. To manually regenerate them, run:" >> "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  echo '```bash' >> "$DOC_FILE"
  echo './bin/generate-diagrams.sh' >> "$DOC_FILE"
  echo '```' >> "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  echo "For more options, run:" >> "$DOC_FILE"
  echo "" >> "$DOC_FILE"
  echo '```bash' >> "$DOC_FILE"
  echo './bin/generate-diagrams.sh --help' >> "$DOC_FILE"
  echo '```' >> "$DOC_FILE"
  
  print_info "Generated diagram documentation: $DOC_FILE"
}

# Check if we should run asynchronously
if [[ "$ASYNC" == "true" ]]; then
  # Run the diagram generation in the background
  print_info "Starting asynchronous diagram generation..."
  (generate_diagrams > "${OUTPUT_DIR}/diagram-generation.log" 2>&1 &)
  print_success "Diagram generation running in background. Check ${OUTPUT_DIR}/diagram-generation.log for progress."
else
  # Run synchronously
  generate_diagrams
fi