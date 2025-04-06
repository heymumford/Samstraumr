#!/bin/bash
#==============================================================================
# s8r header update script: Calls the canonical header update script
#==============================================================================

# Get project root directory
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Call the canonical header update script
"$PROJECT_ROOT/update-standardized-headers.sh" "$@"

