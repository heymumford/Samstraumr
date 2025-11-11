<!--
Copyright (c) 2025 Eric C. Mumford (@heymumford)

This software was developed with analytical assistance from AI tools 
including Claude 3.7 Sonnet, Claude Code, and Google Gemini Deep Research,
which were used as paid services. All intellectual property rights 
remain exclusively with the copyright holder listed above.

Licensed under the Mozilla Public License 2.0
-->


# Java Naming Standards

*Last update: April 3, 2025*

## Overview

This document defines the standard naming conventions for Java code in the Samstraumr project. Consistent naming practices improve code readability, maintainability, and collaboration.

## Classes and Interfaces

### Class names

Class names should follow **PascalCase** convention:

```java
public class TubeLogger { ... }
public class Environment { ... }
public class BundleFactory { ... }
```

### Interface names

Interface names should follow **PascalCase** convention, often with adjectives:

```java
public interface Connectable { ... }
public interface Processable { ... }
```

### Enum names

Enum names should follow **PascalCase** convention:

```java
public enum TubeStatus { ... }
public enum LogLevel { ... }
```

## Methods

Method names should follow **camelCase** convention and typically start with verbs:

```java
public void processData() { ... }
public String getUniqueId() { ... }
public boolean isActive() { ... }
private void initializeComponents() { ... }
```

## Variables

### Instance variables

Instance variable names should follow **camelCase** convention:

```java
private String uniqueId;
private Environment environment;
private final List<String> lineage;
```

### Constants

Constants (static final fields) should use **UPPER_SNAKE_CASE**:

```java
private static final String DIGEST_ALGORITHM = "SHA-256";
private static final int DEFAULT_TERMINATION_DELAY = 60;
public static final int MAX_RETRY_COUNT = 3;
```

### Parameter names

Parameter names should follow **camelCase** convention:

```java
public void setTerminationDelay(int seconds) { ... }
public Tube(String reason, Environment environment) { ... }
```

### Local variables

Local variable names should follow **camelCase** convention:

```java
String formattedMessage = formatMessage(message);
int userCount = getUserCount();
```

## Packages

Package names should be all lowercase with dot separation:

```java
package org.samstraumr.tube;
package org.samstraumr.tube.bundle;
```

## Annotations

Annotation names should follow **PascalCase** convention:

```java
@BundleTest
@AboveTheLine
@Deprecated
```

## Test Classes

### Test class names

Test classes should be named with a descriptive name followed by `Test` or `TestSuite`:

```java
public class TubeTestSuite { ... }
public class BundleConnectionTest { ... }
public class CompositeIntegrationTest { ... }
```

### Test method names

Test method names should be descriptive and follow **camelCase** convention or **snake_case** for readability:

```java
// camelCase style
@Test
public void shouldCreateTubeWithValidReason() { ... }

// snake_case style for BDD-like readability
@Test
public void tube_should_log_initialization_details() { ... }
```

### Cucumber test step methods

Cucumber test step methods should follow the Cucumber standard using **snake_case** with underscores for spaces:

```java
@Given("a monitor tube is initialized")
public void a_monitor_tube_is_initialized() { ... }

@When("the tube processes {int} messages")
public void the_tube_processes_messages(int count) { ... }
```

## Cucumber Feature Files

Cucumber feature files should use **kebab-case** with descriptive names ending in `-test.feature`:

```
tube-initialization-test.feature
machine-state-test.feature
observer-tube-test.feature
```

## Resource Files

Configuration and resource files should use **kebab-case** (for most files) or **snake_case** (for properties files):

```
log4j2.xml
version-template.md
application.properties
```

## Acronyms and Abbreviations

For most acronyms and abbreviations, only the first letter should be uppercase in camelCase and PascalCase contexts:

```java
// Good
public class HttpClient { ... }
private String xmlData;

// Avoid
public class HTTPClient { ... }
private String XMLData;
```

However, acronyms in constants should be all uppercase:

```java
private static final String XML_SCHEMA = "schema.xsd";
```

### Exceptions for specific acronyms

Certain acronyms are recognized as having special importance and may be fully capitalized even in class names, method names, and variables:

```java
// Exceptions for specific acronyms
public class FAQService { ... }  // Frequently Asked Questions
public class TBDValidator { ... } // Tube Based Development
private String TBDFormat;
```

This exception applies only to the following acronyms:
- ATL (Above The Line) - for critical tests
- BTL (Below The Line) - for robustness tests
- FAQ (Frequently Asked Questions)
- TBD (Tube Based Development)
- Other domain-specific acronyms documented in the project glossary

## Type Parameters

Type parameter names should be single uppercase letters, optionally followed by a number:

```java
public interface Mapper<T, U> { ... }
public class Cache<K, V> { ... }
```

Common type parameter names include:
- T for a type
- E for an element
- K for a key
- V for a value
- N for a number

## Exceptions

Exceptions should be suffixed with "Exception" or a more specific term like "Error":

```java
public class TubeInitializationException extends RuntimeException { ... }
public class ValidationException extends Exception { ... }
```

## Best Practices

1. **Be Descriptive**: Names should be meaningful and communicate intent
2. **Be Consistent**: Follow established patterns in the codebase
3. **Avoid Abbreviations**: Use full words unless the abbreviation is well-known
