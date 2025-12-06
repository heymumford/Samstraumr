#!/usr/bin/env bash
#
# fix-feature-file-comments.sh
# Purpose: Convert Java-style /* */ comments to Gherkin # comments in .feature files
# Usage: ./bin/fix-feature-file-comments.sh
#
# Copyright (c) 2025 Eric C. Mumford (@heymumford)
# Licensed under the Mozilla Public License 2.0

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo -e "${BLUE}Fixing Cucumber Feature File Comments${NC}"
echo -e "${BLUE}======================================${NC}"

# Find all .feature files with Java-style comments
FEATURE_DIR="$PROJECT_ROOT/modules/samstraumr-core/src/test/resources/features"
FILES_WITH_JAVA_COMMENTS=$(find "$FEATURE_DIR" -name "*.feature" -exec grep -l "^/\*" {} \;)

if [ -z "$FILES_WITH_JAVA_COMMENTS" ]; then
    echo -e "${GREEN}✓ No feature files with Java-style comments found${NC}"
    exit 0
fi

FILE_COUNT=$(echo "$FILES_WITH_JAVA_COMMENTS" | wc -l | tr -d ' ')
echo -e "${YELLOW}Found $FILE_COUNT feature files with Java-style comments${NC}"
echo

# Process each file
FIXED_COUNT=0
for file in $FILES_WITH_JAVA_COMMENTS; do
    echo -e "${BLUE}Processing: $(basename "$file")${NC}"

    # Create backup
    cp "$file" "$file.bak"

    # Convert /* */ block comments to # comments
    # This uses a Python script for multi-line comment handling
    python3 - "$file" <<'PYTHON'
import sys
import re

def convert_java_comments_to_gherkin(file_path):
    with open(file_path, 'r') as f:
        content = f.read()

    # Pattern to match Java block comments at start of file
    # Matches: /* ... */ (potentially multi-line)
    pattern = r'^/\*\n(.*?)\*/\n'

    def replace_comment(match):
        comment_block = match.group(1)
        # Convert each line to Gherkin comment
        lines = comment_block.split('\n')
        gherkin_lines = []
        for line in lines:
            # Remove leading/trailing whitespace
            stripped = line.strip()
            if stripped:
                gherkin_lines.append(f"# {stripped}")
            else:
                gherkin_lines.append("#")
        return '\n'.join(gherkin_lines) + '\n\n'

    # Apply replacement
    converted = re.sub(pattern, replace_comment, content, count=1, flags=re.DOTALL)

    # Write back
    with open(file_path, 'w') as f:
        f.write(converted)

    return converted != content

if __name__ == '__main__':
    file_path = sys.argv[1]
    changed = convert_java_comments_to_gherkin(file_path)
    sys.exit(0 if changed else 1)
PYTHON

    if [ $? -eq 0 ]; then
        echo -e "${GREEN}  ✓ Fixed${NC}"
        FIXED_COUNT=$((FIXED_COUNT + 1))
        # Remove backup if successful
        rm "$file.bak"
    else
        echo -e "${RED}  ✗ No changes made${NC}"
        # Restore from backup
        mv "$file.bak" "$file"
    fi
done

echo
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Fixed $FIXED_COUNT of $FILE_COUNT files${NC}"
echo
echo -e "${YELLOW}Next steps:${NC}"
echo -e "  1. Review changes: git diff modules/samstraumr-core/src/test/resources/features"
echo -e "  2. Run tests: ./s8r-test unit"
echo -e "  3. Commit: git add . && git commit -m 'fix: convert Java comments to Gherkin format in feature files'"
