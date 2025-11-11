<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and GitHub Copilot Pro,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

# GitHub Copilot Pro Setup for Samstraumr

This guide provides instructions for setting up and effectively using GitHub Copilot Pro with the Samstraumr codebase, focusing on IntelliJ IDEA Ultimate integration and complementing our use of Claude 3.7 Sonnet.

## Installation and Setup

1. **Install the GitHub Copilot Plugin in IntelliJ IDEA**
   - Go to Settings/Preferences → Plugins
   - Search for "GitHub Copilot" and install
   - Restart IntelliJ IDEA
   - Sign in to your GitHub account with Copilot Pro subscription

2. **Configure Copilot Settings**
   - Go to Settings/Preferences → Tools → GitHub Copilot
   - Enable "Auto-import completions"
   - Enable "Suggest multiple solutions at once"
   - Set completion confidence threshold to "Medium"
   - Enable "Show referenced source information"

3. **Java-Specific Settings**
   - Enable "Use typing patterns for code completion"
   - Set line completion threshold to 80 characters
   - Enable "Generate imports automatically"

## Recommended Usage Patterns

### When to Use Copilot vs. Claude

- **Use Copilot for**:
  - In-line code completions while actively writing code
  - Quick test method generation
  - Implementation of boilerplate adapters and port interfaces
  - Javadoc comment completion
  - Quick code navigation and explanation within IDE

- **Use Claude for**:
  - Complex architectural discussions and decisions
  - Multi-file refactoring operations
  - Detailed code reviews
  - Creating new architectural components
  - In-depth explanations of system behavior

### Effective Prompting for Port Interface Implementation

When implementing a new port or adapter, use these prompting patterns:

```
// Implement a port interface for [function] following Clean Architecture principles
// with methods for [core functionalities]

// Example: 
// Implement a port interface for cache management following Clean Architecture principles
// with methods for get, put, remove, and clear operations
```

### Test Generation Workflow

1. Identify untested components via coverage reports
2. Open the class file in IntelliJ
3. Create a corresponding test file
4. Use the Copilot prompt: 
   ```
   // Generate comprehensive tests for this class following our BDD pattern with Cucumber
   ```

## IntelliJ IDEA Live Templates

We've created the following live templates to work with Copilot:

- `port`: Creates a new port interface skeleton
- `adapt`: Creates a new adapter implementation
- `bddtest`: Creates a new Cucumber step definition class
- `svctest`: Creates a service test with mocked dependencies

## Code Review with Copilot

1. Open the GitHub Copilot Chat panel (Alt+C)
2. Select the files to review
3. Use the prompt:
   ```
   Review this code for:
   1. Clean Architecture violations
   2. Error handling completeness
   3. Thread safety issues
   4. Performance concerns
   5. Test coverage gaps
   ```

## Troubleshooting

- If Copilot is not providing useful suggestions:
  - Ensure you're in a file with the correct extension (.java, .feature, etc.)
  - Try writing more context or a detailed comment
  - Use explicit prompts in comments
  - Restart the Copilot plugin (Settings → GitHub Copilot → Restart)

- For port interface suggestions:
  - Ensure you have at least one similar implementation open in another tab
  - Follow naming conventions consistently (e.g., `PortName` for interfaces, `PortNameAdapter` for implementations)

## Best Practices

1. **Review All Generated Code**: Always review and understand code suggested by Copilot
2. **Maintain Architectural Boundaries**: Ensure Copilot suggestions respect our Clean Architecture patterns
3. **Test All Generated Code**: Automatically generated code must meet our test coverage requirements
4. **Document Generation Process**: Note when significant portions were AI-assisted in commit messages
5. **Prefer Small, Focused Generations**: Generate smaller, focused code segments rather than entire complex implementations