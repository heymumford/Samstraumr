#!/bin/bash
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
#
# This software was developed with analytical assistance from AI tools 
# including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
# which were used as paid services. All intellectual property rights 
# remain exclusively with the copyright holder listed above.
#
# Licensed under the Mozilla Public License 2.0

# This script creates the necessary symbolic links to resolve package structure issues
# between the component, component.core, component.composite, etc. packages and the domain packages.

set -e

# Base directory
BASE_DIR="/home/emumford/NativeLinuxProjects/Samstraumr"
SRC_DIR="$BASE_DIR/Samstraumr/samstraumr-core/src/main/java/org/s8r"

# Create component/core directory if it doesn't exist
mkdir -p "$SRC_DIR/component/core"
mkdir -p "$SRC_DIR/component/exception"
mkdir -p "$SRC_DIR/component/identity"
mkdir -p "$SRC_DIR/component/composite"
mkdir -p "$SRC_DIR/component/machine"
mkdir -p "$SRC_DIR/component/logging"

# Create symlinks for files that exist in the component root but need to be in component.core
echo "Creating component.core symlinks..."
if [ ! -f "$SRC_DIR/component/core/State.java" ]; then
  if [ -f "$SRC_DIR/component/State.java" ]; then
    ln -sf "$SRC_DIR/component/State.java" "$SRC_DIR/component/core/State.java"
    echo "Created symlink for State.java in component.core"
  fi
fi

if [ ! -f "$SRC_DIR/component/core/Component.java" ]; then
  if [ -f "$SRC_DIR/component/Component.java" ]; then
    ln -sf "$SRC_DIR/component/Component.java" "$SRC_DIR/component/core/Component.java"
    echo "Created symlink for Component.java in component.core"
  fi
fi

if [ ! -f "$SRC_DIR/component/core/Environment.java" ]; then
  if [ -f "$SRC_DIR/component/Environment.java" ]; then
    ln -sf "$SRC_DIR/component/Environment.java" "$SRC_DIR/component/core/Environment.java"
    echo "Created symlink for Environment.java in component.core"
  fi
fi

# Create symlinks for component.exception
echo "Creating component.exception symlinks..."
if [ ! -f "$SRC_DIR/component/exception/ComponentException.java" ]; then
  if [ -f "$SRC_DIR/component/ComponentException.java" ]; then
    ln -sf "$SRC_DIR/component/ComponentException.java" "$SRC_DIR/component/exception/ComponentException.java"
    echo "Created symlink for ComponentException.java in component.exception"
  fi
fi

if [ ! -f "$SRC_DIR/component/exception/InvalidStateTransitionException.java" ]; then
  if [ -f "$SRC_DIR/component/InvalidStateTransitionException.java" ]; then
    ln -sf "$SRC_DIR/component/InvalidStateTransitionException.java" "$SRC_DIR/component/exception/InvalidStateTransitionException.java"
    echo "Created symlink for InvalidStateTransitionException.java in component.exception"
  fi
fi

# Create symlinks for component.identity
echo "Creating component.identity symlinks..."
if [ ! -f "$SRC_DIR/component/identity/Identity.java" ]; then
  if [ -f "$SRC_DIR/component/Identity.java" ]; then
    ln -sf "$SRC_DIR/component/Identity.java" "$SRC_DIR/component/identity/Identity.java"
    echo "Created symlink for Identity.java in component.identity"
  fi
fi

# Create symlinks for component.composite
echo "Creating component.composite symlinks..."
if [ ! -f "$SRC_DIR/component/composite/Composite.java" ]; then
  if [ -f "$SRC_DIR/component/Composite.java" ]; then
    ln -sf "$SRC_DIR/component/Composite.java" "$SRC_DIR/component/composite/Composite.java"
    echo "Created symlink for Composite.java in component.composite"
  fi
fi

if [ ! -f "$SRC_DIR/component/composite/CompositeFactory.java" ]; then
  if [ -f "$SRC_DIR/component/CompositeFactory.java" ]; then
    ln -sf "$SRC_DIR/component/CompositeFactory.java" "$SRC_DIR/component/composite/CompositeFactory.java"
    echo "Created symlink for CompositeFactory.java in component.composite"
  fi
fi

if [ ! -f "$SRC_DIR/component/composite/CompositeException.java" ]; then
  if [ -f "$SRC_DIR/component/CompositeException.java" ]; then
    ln -sf "$SRC_DIR/component/CompositeException.java" "$SRC_DIR/component/composite/CompositeException.java"
    echo "Created symlink for CompositeException.java in component.composite"
  fi
fi

# Create symlinks for component.machine
echo "Creating component.machine symlinks..."
if [ ! -f "$SRC_DIR/component/machine/Machine.java" ]; then
  if [ -f "$SRC_DIR/component/Machine.java" ]; then
    ln -sf "$SRC_DIR/component/Machine.java" "$SRC_DIR/component/machine/Machine.java"
    echo "Created symlink for Machine.java in component.machine"
  fi
fi

if [ ! -f "$SRC_DIR/component/machine/MachineFactory.java" ]; then
  if [ -f "$SRC_DIR/component/MachineFactory.java" ]; then
    ln -sf "$SRC_DIR/component/MachineFactory.java" "$SRC_DIR/component/machine/MachineFactory.java"
    echo "Created symlink for MachineFactory.java in component.machine"
  fi
fi

if [ ! -f "$SRC_DIR/component/machine/MachineException.java" ]; then
  if [ -f "$SRC_DIR/component/MachineException.java" ]; then
    ln -sf "$SRC_DIR/component/MachineException.java" "$SRC_DIR/component/machine/MachineException.java"
    echo "Created symlink for MachineException.java in component.machine"
  fi
fi

# Create symlinks for component.logging
echo "Creating component.logging symlinks..."
if [ ! -f "$SRC_DIR/component/logging/Logger.java" ]; then
  if [ -f "$SRC_DIR/component/Logger.java" ]; then
    ln -sf "$SRC_DIR/component/Logger.java" "$SRC_DIR/component/logging/Logger.java"
    echo "Created symlink for Logger.java in component.logging"
  fi
fi

echo "All symlinks created successfully!"