<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Contrib Guide

Thank you for your interest in contributing to Samstraumr! This guide will help you understand the contribution process and expectations.

## Contribution Process

1. **Fork the Repository**: Create your own fork of the repository
2. **Create a Branch**: Create a branch for your contribution
3. **Make Changes**: Implement your changes following the coding standards
4. **Run Tests**: Ensure all tests pass with `./run-tests.sh all`
5. **Run Quality Checks**: Verify code quality with `./build-checks.sh`
6. **Submit a Pull Request**: Open a PR with a clear description of your changes

## Code Standards

All contributions should follow these standards:

- **Code Style**: Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with modifications
- **Documentation**: Add Javadoc for public APIs and inline comments for complex logic
- **Testing**: Include appropriate tests for new features and bug fixes
- **Quality**: Pass all quality checks without disabling them

## Testing Requirements

All code changes should include tests that:

- Verify the intended functionality
- Cover edge cases and error conditions
- Follow the dual terminology approach (industry-standard and Samstraumr-specific)
- Utilize the appropriate test type based on the testing pyramid

## Commit Guidelines

- Use clear, descriptive commit messages following the conventional commit format
- Reference issue numbers when applicable
- Keep commits focused on a single logical change
- Follow the [Git Commit Guidelines](contrib-git-commits.md) for detailed formatting instructions
- Use the provided git hooks to ensure consistent commit messages

## Pull Request Process

1. Ensure your PR describes the purpose and scope of the changes
2. Link to any related issues
3. Wait for CI checks to complete successfully
4. Address any review feedback promptly
5. Wait for approval from at least one maintainer before merging

## Getting Help

If you need assistance with your contribution, you can:

- Open an issue with the "question" label
- Reach out to the maintainers via email
- Check the [FAQ](../ref/ref-faq.md) for common questions
