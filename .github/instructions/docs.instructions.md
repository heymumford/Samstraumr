---
applyTo:
  - "docs/**"
  - "**/*.md"
  - "README.md"
excludeAgent: []
---

# Documentation Instructions

These instructions apply to all documentation in the Samstraumr repository.

## Documentation Philosophy

- Documentation is code - treat it with the same care
- Keep documentation close to the code it describes
- Update documentation when code changes
- Write for your audience: users, contributors, or maintainers

## Documentation Types

### Repository Documentation

- **README.md**: High-level project overview, getting started, key features
- **CONTRIBUTING.md**: How to contribute to the project
- **CHANGELOG.md**: Version history and notable changes
- **LICENSE**: Project license information

### Technical Documentation

- **Architecture Docs** (`docs/architecture/`): System design and patterns
- **Concept Docs** (`docs/concepts/`): Core concepts and theory
- **API Documentation** (`docs/api/`): Public API reference
- **Developer Guides** (`docs/dev/`): Development workflows and practices

### Reference Documentation

- **CLI Reference** (`docs/reference/`): Command-line tool documentation
- **Configuration Reference**: Configuration options and formats
- **FAQ**: Common questions and answers

## Markdown Standards

### Document Structure

Every markdown document should have:

1. **Copyright header** (for project files)
2. **Title** (H1, only one per document)
3. **Overview** (brief description)
4. **Table of Contents** (for long documents)
5. **Main Content** (organized with H2-H4 headers)
6. **Links and References** (at the end if extensive)

### Headings

- Use ATX-style headings (`#` symbols)
- Only one H1 (`#`) per document
- Maintain heading hierarchy (don't skip levels)
- Use sentence case for headings

```markdown
# Main Title

## Section Title

### Subsection Title

#### Detail Level
```

### Code Blocks

- Always specify the language for syntax highlighting
- Use meaningful code examples
- Include comments to explain non-obvious parts
- Test code examples to ensure they work

```java
// Good example with language specification and comments
/**
 * Example of creating and starting a component
 */
Component component = new ComponentBuilder()
    .withId("example-component")
    .withValidator(validator)
    .build();

component.start();
```

### Lists

- Use `-` for unordered lists
- Use `1.` for ordered lists (numbers will auto-increment)
- Indent nested lists with 2 spaces
- Add blank lines between list items with multiple paragraphs

### Links

- Use descriptive link text (avoid "click here")
- Use relative paths for internal links
- Verify links are not broken
- Use reference-style links for repeated URLs

```markdown
Good: See the [Architecture Overview](../architecture/README.md) for details.
Avoid: Click [here](../architecture/README.md) for architecture.
```

### Emphasis

- Use `**bold**` for important terms and UI elements
- Use `*italic*` for emphasis
- Use `code` for inline code, commands, and file names
- Use `> blockquotes` for important notes or warnings

### Tables

- Use tables for structured data
- Keep tables simple and readable
- Align columns for readability in source
- Add header row separator

```markdown
| Feature | Description | Status |
|---------|-------------|--------|
| Feature 1 | Description 1 | ✅ |
| Feature 2 | Description 2 | 🚧 |
```

## Writing Style

### Tone and Voice

- Use clear, concise language
- Write in present tense
- Use active voice
- Be direct and specific
- Avoid jargon unless necessary (define when used)

### Audience Awareness

- **For Users**: Focus on what and how
- **For Contributors**: Include why and architecture
- **For Maintainers**: Deep technical details and rationale

### Code Examples

- Provide complete, working examples
- Show common use cases first
- Include error handling where relevant
- Explain what the code does and why

## Documentation Organization

### File Naming

- Use lowercase with hyphens: `api-reference.md`
- Use descriptive names: `component-lifecycle.md` not `lifecycle.md`
- Include category in name when helpful: `test-bdd-cucumber.md`

### Directory Structure

```
docs/
├── architecture/      # System architecture and design
├── concepts/          # Conceptual overviews and theory
├── api/              # API documentation
├── dev/              # Development guides and workflows
├── reference/        # Reference documentation (CLI, config)
├── contribution/     # Contributing guidelines and standards
├── planning/         # Project planning and status
└── test-reports/     # Test assessment and reports
```

### README Files

Every major directory should have a README.md that:
- Explains the purpose of the directory
- Lists key documents with brief descriptions
- Provides navigation to related documentation

## API Documentation

### Javadoc Standards

- Document all public APIs
- Include `@param` for all parameters
- Include `@return` for non-void methods
- Include `@throws` for checked exceptions
- Use `{@link}` to reference other classes/methods
- Provide usage examples for complex APIs

```java
/**
 * Processes the input data through the component pipeline.
 * 
 * <p>This method validates the input, processes it through the
 * configured pipeline, and returns the result. If validation
 * fails, a {@link ValidationException} is thrown.</p>
 * 
 * <p>Example usage:
 * <pre>{@code
 * Data input = new Data("value");
 * Result result = component.process(input);
 * }</pre>
 * 
 * @param input the input data to process, must not be null
 * @return the processing result, never null
 * @throws ValidationException if input validation fails
 * @throws ComponentException if processing fails
 */
public Result process(Data input) throws ValidationException, ComponentException {
    // implementation
}
```

## Documentation Maintenance

### When to Update Documentation

- Always update docs when changing public APIs
- Update guides when changing workflows
- Add examples for new features
- Update changelog for notable changes
- Keep README current with major changes

### Documentation Review

- Verify all links work
- Test all code examples
- Check for typos and grammar
- Ensure consistency with current code
- Validate formatting renders correctly

## Special Sections

### Copyright Headers

All documentation files should include the standard copyright header:

```markdown
<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->
```

### Status Indicators

Use consistent indicators for feature status:
- ✅ Complete/Available
- 🚧 In Progress
- 📋 Planned
- ❌ Not Available/Deprecated

### Admonitions

Use consistent formatting for special notes:

```markdown
> **⚠️ WARNING**: Critical information that could cause issues

> **💡 TIP**: Helpful suggestion or best practice

> **📝 NOTE**: Additional information worth noting

> **🔒 SECURITY**: Security-related information
```

## Cross-References

- Link to related documentation
- Reference source code when helpful
- Link to issues/PRs for context
- Maintain bidirectional links where appropriate

## Documentation Testing

- Verify markdown renders correctly
- Test all code examples
- Check all links (internal and external)
- Review on GitHub to ensure proper rendering

## Common Documentation Anti-Patterns

- ❌ Outdated examples that don't match current code
- ❌ Broken internal or external links
- ❌ Jargon without definitions
- ❌ Missing code examples for complex features
- ❌ Walls of text without structure
- ❌ Inconsistent formatting and style
- ❌ Documentation that duplicates code comments
