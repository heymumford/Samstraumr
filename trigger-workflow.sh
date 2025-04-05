#!/bin/bash

echo "Pushing changes and triggering workflow..."
echo

# Make a small change to force a push
CURRENT_DATE=$(date '+%Y-%m-%d %H:%M:%S')
echo "# Last workflow check: $CURRENT_DATE" >> .github/workflows/README.md

# Commit the change
git add .github/workflows/README.md
git commit -m "ci: trigger workflow check at $CURRENT_DATE"

# Push to GitHub
git push origin main

echo
echo "Changes pushed to GitHub. Check the Actions tab in your repository to see the workflow running:"
echo "https://github.com/heymumford/Samstraumr/actions"
echo
echo "This should trigger the samstraumr-pipeline.yml workflow."