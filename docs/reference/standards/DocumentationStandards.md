# DocumentationStandards

This document defines the naming conventions and organization standards for documentation and tests in the Samstraumr project.

## Documentation File Naming

All documentation files should follow kebab-case naming convention:

```
section-name/topic-name.md
```

### Sections

Documentation is organized into the following sections:

1. **concepts/** - Core theoretical foundations and design principles
2. **guides/** - How-to guides and tutorials
3. **reference/** - API details and technical specifications
4. **testing/** - Testing strategies and methodologies
5. **contribution/** - Guidelines for contributors
6. **research/** - Research proposals and experimental features

### Naming examples

| Topic | Correct Filename |
|-------|-----------------|
| Core concepts | concepts/core-concepts.md |
| Getting started | guides/getting-started.md |
| FAQ | reference/faq.md |
| Testing strategy | testing/test-strategy.md |
| Contributing guide | contribution/contributing.md |
| LLM proposal | research/llm-context-proposal.md |

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

## Cross-References

When referring to other documentation files, use relative links with the `.md` extension:

```markdown
See [Getting Started](../guides/getting-started.md) for more information.
```

## Headers

Each documentation file should begin with a level 1 header that matches the title case of the file:

```markdown
# DocumentationStandards
```

Second-level headers should use title case:

```markdown
## State Management
```

Third-level and below headers should use sentence case:

```markdown
### Managing composite state
