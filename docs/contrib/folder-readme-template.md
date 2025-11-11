<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->

<\!-- 
Copyright (c) 2025 [Eric C. Mumford (@heymumford)](https://github.com/heymumford), Gemini Deep Research, Claude 3.7.
-->

# [Directory Name]

## Purpose
[One-sentence purpose statement of this directory. Be specific about its responsibility and role in the system architecture.]

## Key Responsibilities
- [Primary responsibility 1]
- [Primary responsibility 2]
- [Primary responsibility 3]

## Contents

| File/Directory | Description |
|----------------|-------------|
| `[filename]` | [Brief description of the file's purpose] |
| `[subdirectory]/` | [Brief description of the subdirectory's purpose] |

## Architectural Context

This directory is part of the [Layer Name] layer in the Clean Architecture structure. It [briefly describe how it relates to the overall architecture].

### Clean Architecture Layer: [Domain/Application/Interface/Infrastructure]

## Naming Conventions

Files in this directory follow these naming patterns:

### Java Classes
```
[Prefix][Domain][Type].java

Examples:
- ComponentRepository.java
- UserAuthenticationService.java
```

### Test Files (if applicable)
```
[Component][Scenario]Test.java

Examples:
- ComponentInitializationTest.java
- UserValidationTest.java
```

### Documentation Files (if applicable)
```
[topic]-[subtopic].md

Examples:
- component-lifecycle.md
- validation-rules.md
```

## Usage Examples

```java
// Brief example of how to use the components in this directory
ComponentFactory factory = new ComponentFactory();
Component component = factory.create("example");
```

## Do Not Include
- [Types of files that don't belong here]
- [Alternative locations for certain files]
- [Common mistakes to avoid]

## Related Directories
- `[path/to/related/directory]`: [Brief explanation of relationship]
- `[path/to/another/directory]`: [Brief explanation of relationship]

## Additional Notes
[Any additional information that developers should know about this directory]
