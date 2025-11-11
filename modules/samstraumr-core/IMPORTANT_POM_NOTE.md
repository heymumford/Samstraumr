# IMPORTANT: DO NOT CHANGE XML TAG NAMES IN POM FILES

We have had recurring issues with the `<name>` tag being changed to `<n>` in POM files.

This causes build problems and makes the project difficult to maintain.

## Guidelines:

1. Always preserve the original XML tag names in POM files:
   - `<name>` should remain `<name>`, never abbreviate it to `<n>`
   - All XML elements should maintain their standard Maven naming

2. When editing POM files:
   - Use proper XML editing tools
   - Verify the XML structure remains valid
   - Check for tag name preservation after edits

If you notice any issues with XML tags, please fix them immediately to prevent build problems.
