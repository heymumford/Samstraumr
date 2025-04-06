<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Documentation Standards

This document defines the standardized approach to documentation in the Samstraumr project. Following these standards ensures consistency and improves readability across all project documentation.

## File Organization

Documentation is organized into specific sections based on content type:

|       Directory        |             Purpose             |                   Example Files                    |
|------------------------|---------------------------------|----------------------------------------------------|
| `/docs/concepts/`      | Core architectural concepts     | `core-concepts.md`, `systems-theory-foundation.md` |
| `/docs/guides/`        | How-to guides and tutorials     | `getting-started.md`, `migration-guide.md`         |
| `/docs/reference/`     | Technical reference material    | `api-reference.md`, `configuration-reference.md`   |
| `/docs/architecture/`  | Architectural documentation     | `clean/README.md`, `event/README.md`               |
| `/docs/testing/`       | Testing approach and strategies | `test-strategy.md`, `bdd-with-cucumber.md`         |
| `/docs/contribution/`  | Guidelines for contributors     | `contributing.md`, `code-standards.md`             |
| `/docs/research/`      | Research papers and proposals   | `llm-context-proposal.md`                          |
| `/docs/planning/`      | Development planning documents  | `documentation-standardization-plan.md`            |
| `/docs/compatibility/` | Compatibility information       | `compatibility-report.md`                          |

## File Naming Conventions

All documentation files should follow these naming conventions:

1. **Use kebab-case**: Files should use lowercase with hyphens separating words.
   - ✅ `getting-started.md`, `api-reference.md`, `code-standards.md`
   - ❌ `GettingStarted.md`, `API_Reference.md`, `codeStandards.md`
2. **Be descriptive but concise**: File names should clearly indicate the content.
   - ✅ `test-strategy.md`, `version-management.md`
   - ❌ `doc1.md`, `info.md`
3. **Follow section-based prefixes** (optional): For complex documentation structures, prefixes can help organize content.
   - `concept-tube-identity.md`
   - `guide-first-composite.md`
   - `ref-configuration-options.md`

## Header Conventions

Header styles should be consistent throughout all documentation:

1. **Level 1 Headers (#)**: Match the title case of the file name. There should only be one Level 1 header per document, at the very top.
   - File name: `core-concepts.md`
   - Level 1 header: `# Core Concepts`
2. **Level 2 Headers (##)**: Use title case (Each Major Word Capitalized).
   - `## Getting Started`
   - `## Implementation Details`
3. **Level 3+ Headers (###, ####, etc.)**: Use sentence case (Only first word capitalized).
   - `### Configuration options`
   - `### Running the tests`
4. **Header Structure**: Follow a logical hierarchy. Don't skip levels (e.g., don't follow a Level 2 header with a Level 4 header).

## Content Standards

### Cross-references

When referring to other documentation files:

1. **Use relative paths with .md extension**:
   - ✅ `[Core Concepts](../concepts/core-concepts.md)`
   - ❌ `[Core Concepts](/docs/concepts/core-concepts)` (absolute path)
   - ❌ `[Core Concepts](../concepts/core-concepts)` (missing extension)
2. **For section links, use lowercase anchor references**:
   - ✅ `[Configuration Options](./configuration.md#configuration-options)`
   - ❌ `[Configuration Options](./configuration.md#-configuration--options)`

### Code blocks

For code samples:

1. **Always specify the language for syntax highlighting**:

   ```java
   public class Example {
       public static void main(String[] args) {
           System.out.println("Hello, world!");
       }
   }
   ```
2. **Indent consistently** (4 spaces within code blocks)
3. **Keep code blocks focused and minimal**, showing only the relevant parts

### Images

For images in documentation:

1. **Store images in an `images/` directory** within the relevant documentation section
2. **Use descriptive file names**: `tube-lifecycle-diagram.png` instead of `diagram1.png`
3. **Always include alt text**:
   - ✅ `![Tube lifecycle diagram showing stages from creation to termination](./images/tube-lifecycle-diagram.png)`
   - ❌ `![](./images/diagram.png)`

### Lists

For better readability:

1. **Use numbered lists for sequential steps**
2. **Use bullet points for unordered items**
3. **Be consistent with punctuation** (either use periods at the end of each item or don't)

## Test File Naming

### Java test classes

Java test classes should follow these conventions:

1. **Unit tests:** `{ComponentName}Test.java`
2. **Integration tests:** `{ComponentName}IntegrationTest.java`
3. **Runner classes:** `Run{Category}Test.java`

### Cucumber feature files

Feature files should follow this naming pattern:

```
{functionality-area}-test.feature
```

For example:
- tube-initialization-test.feature
- bundle-connection-test.feature
- system-resilience-test.feature

## Documentation Types

### Readme files

Each major directory should have a README.md file that:

1. **Explains the purpose** of the directory or component
2. **Lists key files** with brief descriptions
3. **Provides usage examples** where applicable
4. **Links to more detailed documentation** when needed

### API documentation

For API documentation:

1. **Document all public interfaces** thoroughly
2. **Include code examples** for common use cases
3. **Document parameters, return values, and exceptions**
4. **Include version information** when APIs change

### Planning documents

For planning documents:

1. **Include current status** (In Progress, Completed, etc.)
2. **Specify timeframes** when applicable
3. **Reference related issues or tickets**
4. **Track progress** with checklists

## Maintenance

1. **Regular Review**: Documentation should be reviewed at least quarterly
2. **Update with Code Changes**: Update documentation when related code changes
3. **Archive Obsolete Docs**: Prefix obsolete docs with "archived-" rather than deleting
4. **Check Links**: Regularly verify cross-references remain valid

## Implementation

These documentation standards are implemented through:

1. **Templates**: Standard templates for different document types
2. **Pre-commit Hooks**: Automated checks for formatting and links
3. **Review Process**: Documentation-specific review criteria
4. **Standardization Scripts**: `docs/scripts/standardize-md-filenames.sh` helps enforce naming conventions

## Documentation Maintenance Plan

### 1. Copyright Headers

#### 1.1 Standardize Copyright Headers

- All files should have a consistent copyright header format
- Java files and markdown files should use different but consistent formats
- Remove any duplicate copyright headers

#### 1.2 Automation for Headers

```bash
#!/bin/bash
# Example script to check for duplicate copyright notices

find . -name "*.java" -type f -not -path "*/node_modules/*" -not -path "*/target/*" | while read -r file; do
    # Check for duplicate copyright notices
    if grep -q "Copyright (c)" "$file" | wc -l | grep -q "^[2-9]"; then
        echo "Found duplicate copyright header in $file"
    fi
done
```

### 2. CLI Command References

- Update all documentation to reference the current CLI commands
  - Use `./s8r test all` instead of `./run-tests.sh all`
  - Use `./s8r quality check` instead of `./build-checks.sh`
- Scan documentation regularly for outdated command references

### 3. Internal Links Validation

#### 3.1 Review Process

- Regularly check that all links in documentation accurately reference existing files
- Standardize casing in links to match file system
- Ensure all cross-references use relative paths with extensions

#### 3.2 Architecture Documentation

- Maintain accurate links between architecture documents
- Use consistent linking patterns for sections and subsections

### 4. Documentation Organization

#### 4.1 Avoiding Duplication

- Maintain a single source of truth for each piece of information
- Remove redundant files after consolidating content
- Document the correct location for various types of documentation

#### 4.2 Filename Conventions

- Regularly audit documentation to ensure kebab-case naming is followed
- Use standardization scripts to detect and fix naming issues

### 5. Implementation Process

1. Fix any copyright header duplication issues
2. Update CLI command references
3. Fix internal links and references
4. Clean up documentation organization

### 6. Validation Process

After implementing documentation updates:
1. Run documentation scans to verify no duplicate headers exist
2. Test internal links to ensure they correctly resolve
3. Review standards compliance across the documentation
4. Verify CLI commands work as documented

## References

- [Markdown Guide](https://www.markdownguide.org/basic-syntax/)
- [Google Developer Documentation Style Guide](https://developers.google.com/style)
- [Java Naming Standards](./java-naming-standards.md)
