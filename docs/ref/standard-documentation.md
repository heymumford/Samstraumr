<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Standard Documentation

This document defines the standardized approach to documentation in the Samstraumr project. Following these standards ensures consistency and improves readability across all project documentation.

## File Organization

Documentation is organized into specific sections based on content type:

|       Directory        |             Purpose             |                   Example Files                    |
|------------------------|---------------------------------|----------------------------------------------------|
| `/docs/concepts/`      | Core architectural concepts     | `core-concepts.md`, `systems-theory-foundation.md` |
| `/docs/guides/`        | How-to guides and tutorials     | `getting-started.md`, `migration-guide.md`         |
| `/docs/reference/`     | Technical reference material    | `api-reference.md`, `configuration-reference.md`   |
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
   - ❌ `[Core Concepts](../concepts/core-concepts.md)` (absolute path)
   - ❌ `[Core Concepts](../concepts/core-concepts.md)` (missing extension)
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
   - ✅ `![Tube lifecycle diagram showing stages from creation to termination](./images/tube-lifecycle-diagram.png.md)`
   - ❌ `![](./images/diagram.png)`

### Lists

For better readability:

1. **Use numbered lists for sequential steps**
2. **Use bullet points for unordered items**
3. **Be consistent with punctuation** (either use periods at the end of each item or don't)

## Documentation Types

### Readme files

Each major directory should have a README.md file that:

1. **Explains the purpose** of the directory or component
2. **Lists key files** with brief descriptions
3. **Provides usage examples** where applicable
4. **Links to more detailed documentation** when needed

### Api documentation

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

## References

- [Markdown Guide](https://www.markdownguide.org/basic-syntax/)
- [Google Developer Documentation Style Guide](https://developers.google.com/style)
- [Java Naming Standards](./java-naming-standards.md)
