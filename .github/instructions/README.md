<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# Path-Specific Copilot Instructions

This directory contains path-specific instruction files that provide targeted guidance for GitHub Copilot when working on different parts of the Samstraumr codebase.

## Overview

Path-specific instructions allow for more focused and relevant guidance based on the area of code being worked on. Each instruction file includes frontmatter with an `applyTo` directive that specifies which paths it applies to.

## Instruction Files

### [core.instructions.md](core.instructions.md)
**Applies to:** Core framework implementation
- `modules/samstraumr-core/**`
- `modules/*/src/main/java/**`

Provides guidance on:
- Clean Architecture enforcement
- Component design patterns
- Event-driven communication
- Validation and error handling
- Threading and concurrency
- Performance and security considerations

### [tests.instructions.md](tests.instructions.md)
**Applies to:** Test code
- `modules/*/src/test/java/**`
- `test-module/**`
- `test-port-interfaces/**`
- `lifecycle-test/**`
- `**/src/test/**`

Provides guidance on:
- Test organization and types
- Test naming conventions
- Mocking and assertions
- Test data management
- Coverage requirements
- Testing best practices

### [docs.instructions.md](docs.instructions.md)
**Applies to:** Documentation
- `docs/**`
- `**/*.md`
- `README.md`

Provides guidance on:
- Documentation structure and organization
- Markdown standards
- Writing style and tone
- API documentation (Javadoc)
- Code examples
- Documentation maintenance

### [scripts.instructions.md](scripts.instructions.md)
**Applies to:** Scripts and utilities
- `bin/**`
- `util/**`
- `*.sh`
- `**/*.sh`

Provides guidance on:
- Shell script structure and standards
- Error handling and validation
- CLI tool best practices
- Build and test scripts
- Security considerations
- Script testing

## How It Works

When GitHub Copilot works on a file, it automatically loads:
1. The repository-wide instructions from `.github/copilot-instructions.md`
2. Any path-specific instructions that match the file's path

This ensures that Copilot has both general context about the project and specific guidance for the particular area of code being worked on.

## Customization

To add new path-specific instructions:

1. Create a new `.instructions.md` file in this directory
2. Add YAML frontmatter with `applyTo` paths:
   ```markdown
   ---
   applyTo:
     - "path/to/files/**"
   excludeAgent: []
   ---
   ```
3. Add your specific instructions below the frontmatter
4. Update this README to document the new file

## Best Practices

- Keep instructions focused and specific to their scope
- Avoid duplicating content from repository-wide instructions
- Use clear, actionable language
- Include examples where helpful
- Keep files concise - very large files may be partially ignored
- Reference repository-wide instructions when appropriate
- Update instructions when patterns or standards change

## References

- [GitHub Docs: Adding repository custom instructions](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions)
- [GitHub Blog: Unlocking the full power of Copilot code review](https://github.blog/ai-and-ml/unlocking-the-full-power-of-copilot-code-review-master-your-instructions-files/)
- [Repository-wide Copilot Instructions](../copilot-instructions.md)

---

For questions about these instructions or suggestions for improvements, please open an issue or discuss with the maintainers.
